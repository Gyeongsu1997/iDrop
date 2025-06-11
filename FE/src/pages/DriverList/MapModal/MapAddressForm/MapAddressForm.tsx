import type { Location } from '../../../../types/location';
import styles from './MapAddressForm.module.scss';

interface MapAddressFormProps {
  location: Location;
  changeLocation: (newLoc: Partial<Location>) => void;
}

export function MapAddressForm({
  location,
  changeLocation,
}: MapAddressFormProps) {
  return (
    <div className={styles.addressWrapper}>
      <label htmlFor='address'>주소</label>
      <input
        name='address'
        className={styles.address}
        type='text'
        value={location.address}
        placeholder='지도를 이동해 주세요'
        readOnly
      />
      <label htmlFor='detailedAddress'>상세주소</label>
      <input
        name='detailedAddress'
        className={styles.address}
        type='text'
        value={location.detailedAddress}
        onChange={({ target: { value } }) =>
          changeLocation({ detailedAddress: value })
        }
        placeholder='상세 주소가 있다면 적어주세요'
      />
    </div>
  );
}
