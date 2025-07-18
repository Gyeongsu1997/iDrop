import { useEffect, useState } from 'react';
import styles from './SubscriptionManagement.module.scss';
import { Header } from '@/components/Header/Header';
import { KidInformationBox } from './KidInformationBox/KidInformationBox';
import { getDriverSubscriptions } from '../../../services/subscription';
import { useFetch } from '../../../hooks/useFetchRefactor';
import { Loader } from '../../../components/Loader/Loader';
import type { Subscription } from '../../../types/subscription.ts';

export default function SubscriptionManagement() {
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);

  const [state] = useFetch(() => getDriverSubscriptions(1), []); // client가 자신의 driverId를 알고 있어야함.
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
      {subscriptions.map((subscription) => (
        <KidInformationBox
          key={subscription.subscriptionId}
          subscription={subscription}
        />
      ))}
    </div>
  );
}
