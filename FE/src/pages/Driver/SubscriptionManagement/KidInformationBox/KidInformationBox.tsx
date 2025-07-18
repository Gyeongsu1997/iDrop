import iDrop from '@/assets/iDropGreen.svg';
import { ScheduleList } from '../../../../components/Schedule/ScheduleList';
import { postSubscribeRequest } from '../../../../services/driverAPI';
import styles from './KidInformationBox.module.scss';

interface KidInformationBoxProps {
  subscription: any;
}

export function KidInformationBox({ subscription }: KidInformationBoxProps) {
  const {
    pickUpInfoId,
    childImage,
    childName,
    childBirth,
    childGender,
    startDate,
    endDate,
    startAddress,
    endAddress,
    parentName,
    parentPhoneNumber,
    status,
    schedule,
  } = subscription;
  const transformedSchedule = transformSchedule(schedule);

  const handleSubscription = async (pickUpId, status) => {
    const postData = {
      pickUpInfoId: pickUpId,
      statusCode: status,
    };
    await postSubscribeRequest(postData);
    // TODO 요청 처리 후 데이터 업데이트 고려하기
  };

  return (
    <div className={styles.content}>
      <div className={styles.kidInfo}>
        <img className={styles.kidImg} src={childImage || iDrop}></img>
        <div className={styles.infoBox}>
          <div className={styles.profiles}>
            <span className={styles.name}>{childName}</span>
            <div
              className={`${styles.status} ${status === '대기' ? styles.waiting : styles.accept}`}
            >
              {status}
            </div>
          </div>
          <span className={styles.birthDay}>
            {childGender}, {childBirth}
          </span>

          <span>
            {startDate} ~ {endDate}
          </span>
          <span>
            {parentName} / {parentPhoneNumber}
          </span>
        </div>
      </div>
      <div>
        <span>
          {startAddress} <br /> {'→'} {endAddress}
        </span>
      </div>
      <ScheduleList schedule={transformedSchedule} />
      <div className={styles.btnBox}>
        {status !== '승인' && (
          <>
            <button
              className={styles.denyButton}
              onClick={() => handleSubscription(pickUpInfoId, 0)}
            >
              거절
            </button>
            <button
              className={styles.acceptButton}
              onClick={() => handleSubscription(pickUpInfoId, 1)}
            >
              수락
            </button>
          </>
        )}
      </div>
    </div>
  );
}

function transformSchedule(schedule) {
  const daysInfo = {
    MON: { order: 1, translation: '월' },
    TUE: { order: 2, translation: '화' },
    WED: { order: 3, translation: '수' },
    THU: { order: 4, translation: '목' },
    FRI: { order: 5, translation: '금' },
    SAT: { order: 6, translation: '토' },
    SUN: { order: 7, translation: '일' },
  };

  const translateDay = (schedule) => ({
    ...schedule,
    day: daysInfo[schedule.day].translation,
  });

  const sortByDay = (a, b) => daysInfo[a.day].order - daysInfo[b.day].order;

  const scheduleArray = Object.keys(schedule).map((day) => {
    return {
      day,
      time: schedule[day],
    };
  });
  scheduleArray.sort(sortByDay);
  const translatedScheduleArray = scheduleArray.map(translateDay);

  return translatedScheduleArray;
}
