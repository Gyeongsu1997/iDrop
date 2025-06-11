import { Modal } from '@/components/Modal/Modal';
import { AddressFinderMap } from './AddressFinderMap/AddressFinderMap';
import { Footer } from '@/components/Footer/Footer';
import styles from './MapModal.module.scss';
import { MapAddressForm } from './MapAddressForm/MapAddressForm';
import type { Location } from '../../../types/location';

interface MapModalProps {
  isVisible: boolean;
  location: Location;
  changeLocation: (newLoc: Partial<Location>) => void;
  closeModal: () => void;
}

export function MapModal({
  isVisible,
  location,
  changeLocation,
  closeModal,
}: MapModalProps) {
  return (
    <Modal
      isVisible={isVisible}
      onClose={closeModal}
      width='100%'
      height='100dvh'
      animationType='slideDown'
    >
      <div className={styles.modalContainer}>
        <AddressFinderMap changeLocation={changeLocation} />
        <MapAddressForm location={location} changeLocation={changeLocation} />
        <Footer onClick={closeModal} text='완료' />
      </div>
    </Modal>
  );
}
