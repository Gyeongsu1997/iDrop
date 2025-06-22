import { useLocation, useNavigate } from 'react-router-dom';
import { Footer } from '@/components/Footer/Footer';
import { Header } from '@/components/Header/Header';
import { postSubscribe } from '@/services/parentsAPI';
import styles from './DriverDetail.module.scss';

export default function DriverDetailRefactor() {
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

  const handleSubscriptionRequest = async () => {
    try {
      await postSubscribe({
        driverId,
        ...startLocation,
        ...goalLocation,
      });
      navigate('/subscription/confirmation');
    } catch (error) {
      console.error(error);
      alert('구독 요청 처리 중 오류가 발생했습니다.');
      navigate('/subscription/search');
    }
  };

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
      <Footer text='구독 신청' onClick={handleSubscriptionRequest} />
    </div>
  );
}
