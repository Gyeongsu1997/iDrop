import type { Driver } from '../../../types/driver';
import styles from './DriverListItem.module.scss';

interface DriverListItemProps {
  driver: Driver;
  onClick: (driver: Driver) => void;
}

export function DriverListItem({ driver, onClick }: DriverListItemProps) {
  const { name, gender, imageUrl, introduction } = driver;
  return (
    <article onClick={() => onClick(driver)} className={styles.item}>
      <div className={styles.info}>
        <div className={styles.profile}>
          <h4 className={styles.name}>
            {name}({gender})
          </h4>
        </div>
        <img src={imageUrl} alt='avatar' className={styles.img} />
      </div>
      <p className={styles.introduce}>{introduction}</p>
    </article>
  );
}
