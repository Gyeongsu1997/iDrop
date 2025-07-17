import { useLoaderData } from 'react-router-dom';
import { Header } from '@/components/Header/Header';
import { getKidInfo } from '@/services/childrenAPI';
import styles from './SelectChild.module.scss';
import { TodayPickUpList } from './TodayPickUpList';
import { parseData } from '../../../utils/parseData';

export default function SelectChild() {
  const childrenData = useLoaderData();
  const isHaveChildData = childrenData.length > 0;

  if (!isHaveChildData) {
    return <div className={styles.headMessage}>현재 가능한 픽업이 없어요</div>;
  }

  return (
    <>
      <Header title='픽업 선택' />
      <div className={styles.container}>
        <TodayPickUpList childrenData={childrenData} />
      </div>
    </>
  );
}

export async function fetchPickUpList() {
  const pickUpList = await getKidInfo('driver/pickup/today/remaining');
  return pickUpList.data.length === 0
    ? pickUpList.data
    : parseData(pickUpList.data);
}
