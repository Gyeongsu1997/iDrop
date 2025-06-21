import type { Driver } from '../../../types/driver';
import styles from './DriverListItem.module.scss';

interface DriverListItemProps {
  driver: Driver;
  handleClick: (id: number) => void;
}

export function DriverListItem({
  driver: { driverId, name, gender, imageUrl, introduction },
  handleClick,
}: DriverListItemProps) {
  return (
    <article onClick={() => handleClick(driverId)} className={styles.item}>
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
