import { useReducer } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { DayList } from './DayList/DayList';
import { TimeList } from './TimeList/TimeList';
import { Footer } from '@/components/Footer/Footer';
import { Header } from '@/components/Header/Header';
import { BottomSheet } from '@/components/BottomSheet/BottomSheet';
import { postSubscribe } from '@/services/parentsAPI';
import styles from './DriverDetail.module.scss';
import type { Location } from '../../types/location';

const formatSchedule = (schedule) => {
  return Object.fromEntries(
    Object.entries(schedule).map(([day, { hour, min }]) => [
      day,
      `${hour}:${min}`,
    ]),
  );
};

const getToday = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = (today.getMonth() + 1).toString().padStart(2, '0');
  const day = today.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
};

export default function DriverDetail() {
  const navigate = useNavigate();
  const {
    state: { startLocation, goalLocation, driver },
  } = useLocation();
  const {
    driverId,
    name,
    birthDate,
    gender,
    phoneNumber,
    imageUrl,
    career,
    introduction,
  } = driver;
  const [schedule, dispatchSchedule] = useReducer(
    scheduleReducer,
    INITIAL_SCHEDULE_STATE,
  );

  const handleSubmit = async (isButtonActive: boolean) => {
    if (!isButtonActive) {
      return;
    }

    const {
      address: startAddress,
      detailedAddress: startDetailedAddress,
      latitude: startLatitude,
      longitude: startLongitude,
    } = startLocation;

    const {
      address: goalAddress,
      detailedAddress: goalDetailedAddress,
      latitude: goalLatitude,
      longitude: goalLongitude,
    } = goalLocation;

    const payload = {
      driverId,
      startDate: getToday(),
      startAddress,
      startDetailedAddress,
      startLatitude,
      startLongitude,
      goalAddress,
      goalDetailedAddress,
      goalLatitude,
      goalLongitude,
      schedule: formatSchedule(schedule),
    };

    try {
      await postSubscribe(payload);
      navigate('/subscription/confirmation');
    } catch (error) {
      console.error(error);
      alert('구독 요청 처리 중 오류가 발생했습니다.');
      navigate('/subscription/search');
    }
  };

  const handleWeekClick = (day) => {
    if (schedule[day]) {
      dispatchSchedule({
        type: SCHEDULE_ACTION_TYPE.DELETE_DAY,
        payload: { day },
      });
    } else {
      dispatchSchedule({
        type: SCHEDULE_ACTION_TYPE.ADD_DAY,
        payload: { day, time: DEFAULT_TIME },
      });
    }
  };

  const handleTimeChange = (day, unit) => (value) => {
    dispatchSchedule({
      type: SCHEDULE_ACTION_TYPE.CHANGE_TIME,
      payload: { day, unit, value },
    });
  };

  const isButtonActive =
    startLocation.address &&
    goalLocation.address &&
    Object.keys(schedule).length > 0;

  return (
    <div className={styles.container}>
      <Header title='기사님 정보' />
      <main className={styles.main}>
        <section className={styles.profile}>
          <img src={imageUrl} className={styles.profileImg} alt='프로필' />
          <article className={styles.profileTextWrapper}>
            <h3 className={styles.name}>{name}</h3>
            <h4 className={styles.age}>{`${birthDate} (${gender})`}</h4>
          </article>
        </section>
        <section className={styles.infoList}>
          <article className={styles.info}>
            <span className={styles.infoTitle}>자기소개</span>
            <p className={styles.infoContent}>{introduction}</p>
          </article>
          <article className={styles.info}>
            <span className={styles.infoTitle}>경력</span>
            <p className={styles.infoContent}>{career}</p>
          </article>
          <article className={styles.info}>
            <span className={styles.infoTitle}>연락처</span>
            <p className={styles.infoContent}>{phoneNumber}</p>
          </article>
        </section>
      </main>
      <BottomSheet headerMsg='구독 신청'>
        <DayList schedule={schedule} handleWeekClick={handleWeekClick} />
        <TimeList schedule={schedule} handleTimeChange={handleTimeChange} />
        <Footer
          text='확인'
          onClick={() => handleSubmit(isButtonActive)}
          isButtonDisabled={!isButtonActive}
        />
      </BottomSheet>
      {/* <Footer text='구독 신청' onClick={handleSubscriptionRequest} /> */}
    </div>
  );
}

const INITIAL_SCHEDULE_STATE = {};

const SCHEDULE_ACTION_TYPE = {
  ADD_DAY: 'ADD_DAY',
  DELETE_DAY: 'DELETE_DAY',
  CHANGE_TIME: 'CHANGE_TIME',
};

const DEFAULT_TIME = { hour: '08', min: '10' };

const scheduleReducer = (state, action) => {
  switch (action.type) {
    case SCHEDULE_ACTION_TYPE.ADD_DAY:
      return {
        ...state,
        [action.payload.day]: action.payload.time,
      };
    case SCHEDULE_ACTION_TYPE.DELETE_DAY:
      const newState = { ...state };
      delete newState[action.payload.day];
      return newState;
    case SCHEDULE_ACTION_TYPE.CHANGE_TIME:
      return {
        ...state,
        [action.payload.day]: {
          ...state[action.payload.day],
          [action.payload.unit]: action.payload.value,
        },
      };
    default:
      return state;
  }
};
