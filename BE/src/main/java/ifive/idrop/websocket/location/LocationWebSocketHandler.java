package ifive.idrop.websocket.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import ifive.idrop.domain.driver.entity.Driver;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.domain.auth.entity.AuthenticateUser;
import ifive.idrop.domain.auth.filter.VerifyUserFilter;
import ifive.idrop.domain.auth.utils.JwtProvider;
import ifive.idrop.domain.parent.entity.Parent;
import ifive.idrop.domain.pickup.entity.PickUpHistory;
import ifive.idrop.domain.pickup.entity.PickUpLocation;
import ifive.idrop.domain.pickup.repository.PickUpRepository;
import ifive.idrop.domain.user.repository.UserRepository;
import ifive.idrop.domain.user.entity.User;
import ifive.idrop.websocket.CustomObjectMapper;
import ifive.idrop.websocket.location.dto.ChildGeoLocation;
import ifive.idrop.websocket.location.dto.CurrentPickUp;
import ifive.idrop.websocket.direction.dto.Direction;
import ifive.idrop.websocket.direction.NaverDirectionFinder;
import ifive.idrop.websocket.location.dto.DriverGeoLocation;
import ifive.idrop.websocket.location.dto.Location;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class LocationWebSocketHandler extends TextWebSocketHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PickUpRepository pickUpRepository;
    private final NaverDirectionFinder directionFinder;

    private static final Map<String, WebSocketSession> sessions; //세션아이디, 세션
    private static final Map<String, Long> drivers;  //기사 세션아이디, 기사 id
    private static final Map<Long, String> parents; //부모 id, 부모 세션아이디

    private static final Map<Long, CurrentPickUp> currentPickUps; //기사 id, 현재픽업(child id, parent id, reserved time)
    private static final Map<Long, Long> parentDriverSets; //부모 id, 기사 id
    private static final Map<String, Location> lastLocations; //기사 세션아이디, 직전 위치

    static {
        sessions = new ConcurrentHashMap<>();
        drivers = new ConcurrentHashMap<>();
        parents = new ConcurrentHashMap<>();
        currentPickUps = new ConcurrentHashMap<>();
        parentDriverSets = new ConcurrentHashMap<>();
        lastLocations = new ConcurrentHashMap<>();
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);

        User user = getUserBySession(session);

        if (user instanceof Driver) {
            Long driverId = ((Driver) user).getId();
            drivers.put(sessionId, driverId);
            try {
                CurrentPickUp currentPickUp = setCurrentPickUps(driverId);
                Direction direction = directionFinder.getDirection(currentPickUp.getStartLocation(), currentPickUp.getEndLocation());
                session.sendMessage(new TextMessage(CustomObjectMapper.getString(direction)));
            } catch (BusinessException e) {
                sendErrorMessage(session, e.getMessage());
                session.close(CloseStatus.NORMAL); // 정상 종료 상태로 소켓 연결 종료
                return; // 메소드 종료
            }

            log.info("webSocket/location - DRIVER connected (session ID={}, driver ID={})", sessionId, driverId);

        } else if (user instanceof Parent) {
            Long parentId = ((Parent) user).getId();
            if (!parentDriverSets.containsKey(parentId)) {
                session.sendMessage(new TextMessage("기사가 접속 중이 아닙니다."));
                session.close(CloseStatus.NORMAL);
            }
            parents.put(parentId, sessionId);

            log.info("webSocket/location - PARENT connected (session ID={}, parent ID={})", sessionId, parentId);
        }

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String sessionId = session.getId();
        Long driverId = drivers.get(sessionId);

        DriverGeoLocation driverLocation = CustomObjectMapper.getObject(textMessage.getPayload(), DriverGeoLocation.class);
        log.info("webSocket/location - DRIVER LOCATION {} {} ", driverLocation.getLatitude(), driverLocation.getLongitude());
        CurrentPickUp currentPickUp = currentPickUps.get(driverId);

        Location lastLocation = lastLocations.get(sessionId);
        if (lastLocation == null) { //전 위치 기록이 없음 첫 위치
            lastLocations.put(sessionId, driverLocation.getLocation());
        }
        else {
            if (!driverLocation.isSameLocation(lastLocation)) { //전에 왔던 위치와 다른 위치
                Direction direction = directionFinder.getDirection(driverLocation.getLocation(), currentPickUp.getEndLocation());
                session.sendMessage(new TextMessage(CustomObjectMapper.getString(direction)));
                lastLocation.update(driverLocation);
                lastLocations.replace(sessionId, lastLocation);

                if (parents.containsKey(currentPickUp.getParentId())) {
                    String parentSessionId = parents.get(currentPickUp.getParentId());
                    WebSocketSession parentSession = sessions.get(parentSessionId);
                    if(parentSession.isOpen())
                        parentSession.sendMessage(new TextMessage(CustomObjectMapper.getString(direction)));
                }
            }
        }
        sendChildLocationToParent(currentPickUp, driverLocation);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        if (drivers.containsKey(sessionId)) {
            removeSessionData(sessionId);
        }
    }

    private void sendErrorMessage(WebSocketSession session, String message) throws IOException {
        // 사용자 정의 오류 메시지 전송
        session.sendMessage(new TextMessage(message));
    }

    //엑세스 토큰으로 userId, role 구하기
    private AuthenticateUser getAuthenticateUser(String token) throws JsonProcessingException {
        Claims claims = jwtProvider.getClaims(token);
        String authenticateUserJson = claims.get(VerifyUserFilter.AUTHENTICATE_USER, String.class);
        return CustomObjectMapper.getMapper().readValue(authenticateUserJson, AuthenticateUser.class);
    }

    //웹소켓 세션에서 User 구하기
    private User getUserBySession(WebSocketSession session) throws JsonProcessingException {
        // HTTP 헤더에서 엑세스 토큰을 꺼낸다.

        String accessToken = String.valueOf(session.getHandshakeHeaders().get("Sec-Websocket-Protocol"));
        accessToken = accessToken.substring(1, accessToken.length() - 1);
        AuthenticateUser authenticateUser = getAuthenticateUser(accessToken);
        return userRepository.findByLoginId(authenticateUser.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    //웹소켓 driver가 연결 시 currentPickUp 만들어서 세팅
    private CurrentPickUp setCurrentPickUps(Long driverId) {
        PickUpHistory pickup = pickUpRepository.findPickUpByDriverIdWithCurrentTimeInReservedWindow(driverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PICKUP_NOT_FOUND));
        PickUpLocation pickUpLocation = pickUpRepository.findPickUpLocationById(pickup.getId().getSubscriptionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_EXIST)); // todo: ErrorCode 변경

//        Object[] childIdAndParentId = pickUpInfoRepository.findChildAndParentIdByPickUp(pickup.getId());
        Object[] childIdAndParentId = pickUpRepository.findChildAndParentIdByPickUp(1L);
        CurrentPickUp currentPickUp = CurrentPickUp.builder()
                .childId((Long) childIdAndParentId[0])
                .parentId((Long) childIdAndParentId[1])
                .reservedTime(pickup.getReservedTime())
                .startLocation(new Location(pickUpLocation.getStartLongitude(), pickUpLocation.getStartLatitude()))
                .endLocation(new Location(pickUpLocation.getEndLongitude(), pickUpLocation.getEndLatitude()))
                .build();
        currentPickUps.put(driverId, currentPickUp);
        parentDriverSets.put((Long) childIdAndParentId[1], driverId);
        return currentPickUp;
    }

    //웹소켓 연결 종료 시, 관련 데이터 각종 hashmap에서 삭제
    private void removeSessionData(String sessionId) {
        Long driverId = drivers.get(sessionId);
        CurrentPickUp currentPickUp = currentPickUps.get(driverId);
        Long parentId = currentPickUp.getParentId();
        drivers.remove(sessionId);
        currentPickUps.remove(driverId);
        parents.remove(parentId);
        lastLocations.remove(sessionId);
    }

    private void sendChildLocationToParent(CurrentPickUp currentPickUp, DriverGeoLocation driverLocation) throws Exception {
        Long parentId = currentPickUp.getParentId();
        try {
            WebSocketSession receiver = sessions.get(parents.get(parentId));
            if (receiver != null && receiver.isOpen()) {
                ChildGeoLocation childLocation = new ChildGeoLocation(driverLocation, currentPickUp.getChildId());
                receiver.sendMessage(new TextMessage(CustomObjectMapper.getString(childLocation)));
            }
        } catch (Exception e) { //driver는 정보를 보내는데 부모가 접속중이 아닌경우
            ;
        }

    }
}
