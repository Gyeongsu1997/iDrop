import styles from './AddressForm.module.scss';
import type { Location } from '../../../types/location';

interface AddressFormProps {
  startLocation: Location;
  endLocation: Location;
  openStartMap: () => void;
  openEndMap: () => void;
}

export function AddressForm({
  startLocation,
  endLocation,
  openStartMap,
  openEndMap,
}: AddressFormProps) {
  return (
    <form className={styles.addressForm}>
      <label className={styles.addressLabel}>출발지/도착지</label>
      <input
        required
        type='button'
        className={styles.addressInput}
        name='start'
        value={
          startLocation.address
            ? startLocation.address + ' ' + startLocation.detailedAddress
            : '출발지 입력'
        }
        onClick={openStartMap}
      />
      <input
        required
        type='button'
        className={styles.addressInput}
        name='end'
        value={
          endLocation.address
            ? endLocation.address + ' ' + endLocation.detailedAddress
            : '도착지 입력'
        }
        onClick={openEndMap}
      />
    </form>
  );
}
