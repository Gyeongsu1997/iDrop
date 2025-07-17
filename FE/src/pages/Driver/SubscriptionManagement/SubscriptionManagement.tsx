import { useEffect, useState } from 'react';
import styles from './SubscriptionManagement.module.scss';
import iDrop from '@/assets/iDropGreen.svg';
import { Header } from '@/components/Header/Header';
import { redirect } from 'react-router-dom';
import { ScheduleList } from '../../../components/Schedule/ScheduleList';
import { postSubscribeRequest } from '../../../services/driverAPI';
import { getDriverSubscriptions } from '../../../services/subscription';
import { useFetch } from '../../../hooks/useFetchRefactor';
import { Loader } from '../../../components/Loader/Loader';

export default function SubscriptionManagement() {
  const [subscriptions, setSubscriptions] = useState([]);

  const [state] = useFetch(() => getDriverSubscriptions(1), []); // client가 driverId를 알고 있어야함.
  const { loading, data, error } = state;

  useEffect(() => {
    if (data) {
      setSubscriptions(data.data);
    }
  }, [data]);

  if (error) {
    return <div className={styles.headMessage}>에러가 발생했습니다</div>;
  }
  if (loading) {
    return <Loader />;
  }
  if (subscriptions.length === 0) {
    return <div className={styles.headMessage}>현재 요청된 구독이 없어요</div>;
  }

  return (
    <div>
      <Header title='구독 요청' />
      {subscriptions.map((subscription, index) => (
        <KidInformationBox key={index} subscription={subscription} />
      ))}
    </div>
  );
}

interface KidInformationBoxProps {
  subscription: any;
}

function KidInformationBox({ subscription }: KidInformationBoxProps) {
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
      <ScheduleList schedule={transformedSchedule} status={status} />
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

  const makeScheduleObj = (day) => {
    const [hour, min] = schedule[day].split(':');
    return {
      day,
      hour,
      min,
    };
  };

  const translateDay = (schedule) => ({
    ...schedule,
    day: daysInfo[schedule.day].translation,
  });

  const sortByDay = (a, b) => daysInfo[a.day].order - daysInfo[b.day].order;

  const scheduleArray = Object.keys(schedule).map(makeScheduleObj);
  scheduleArray.sort(sortByDay);
  const translatedScheduleArray = scheduleArray.map(translateDay);

  return translatedScheduleArray;
}
