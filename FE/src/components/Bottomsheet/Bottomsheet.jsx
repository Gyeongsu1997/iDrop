import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import styles from './BottomSheet.module.scss';
import { DriverContents, ParentContents } from './KidInfoBox/KidInfoBox';
import { Footer } from '../Footer/Footer';

export function ParentBottomSheet({ childrenData }) {
  const { headerMsg, isHaveData } = checkData(childrenData);

  return (
    <BottomSheet headerMsg={headerMsg}>
      {isHaveData && <ParentContents childrenData={childrenData} />}
    </BottomSheet>
  );
}

export function DriverBottomSheet({ childrenData }) {
  const { pathname } = useLocation();
  const { headerMsg, isHaveData } = checkData(childrenData);

  const navigate = useNavigate();
  const movePage = () => {
    navigate('/pickup', {
      state: { flag: true, childrenData: childrenData },
    });
  };
  return (
    <BottomSheet headerMsg={headerMsg}>
      {isHaveData && (
        <DriverContents childrenData={childrenData}></DriverContents>
      )}
      {pathname === '/map' ? (
        <Footer text={'운행종료'} onClick={movePage} />
      ) : (
        ''
      )}
    </BottomSheet>
  );
}

export function BottomSheet({ children, headerMsg }) {
  const [isExpanded, setIsExpanded] = useState(false);

  const toggleBottomSheet = () => {
    setIsExpanded(!isExpanded);
  };

  return (
    <div className={`${styles.wrapper} ${isExpanded ? styles.expanded : ''}`}>
      <header className={styles.headLine} onClick={toggleBottomSheet}>
        <div className={styles.handle}></div>
        <span className={styles.headMessage}>{headerMsg}</span>
      </header>
      <div className={styles.container}>{children}</div>
    </div>
  );
}

function checkData(childrenData) {
  const isHaveData = childrenData.length > 0 ? true : false;
  const headerMsg = isHaveData ? '오늘의 픽업' : '오늘은 픽업이 없습니다';
  return { headerMsg, isHaveData };
}
