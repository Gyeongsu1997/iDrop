import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { Header } from '@/components/Header/Header';
import { AddressForm } from './AddressForm/AddressForm';
import { DriverListItem } from './DriverListItem/DriverListItem';
import styles from './DriverList.module.scss';
import { useModal } from '@/hooks/useModal';
import { getDrivers } from '@/services/parentsAPI';
import { MapModal } from './MapModal/MapModal';
import { useLocation } from './useLocation';
import type { Driver } from '../../types/driver';

export default function DriverList() {
  const navigate = useNavigate();
  const [drivers, setDrivers] = useState<Driver[]>([]);
  const [isStart, setIsStart] = useState(true);
  const { location: startLocation, changeLocation: changeStartLocation } =
    useLocation();
  const { location: goalLocation, changeLocation: changeGoalLocation } =
    useLocation();
  const { isVisible, open: openModal, close: closeModal } = useModal();

  useEffect(() => {
    if (
      isVisible.animate ||
      startLocation.address === '' ||
      goalLocation.address === ''
    ) {
      return;
    }
    (async () => {
      const { data } = await getDrivers(startLocation, goalLocation);
      setDrivers(data);
    })();
  }, [isVisible, startLocation, goalLocation]);

  const openStartMap = () => {
    setIsStart(true);
    openModal();
  };

  const openEndMap = () => {
    setIsStart(false);
    openModal();
  };

  const handleItemClick = (id: number) => {
    navigate(`/subscription/driver/${id}`, {
      state: {
        startLocation,
        goalLocation,
      },
    });
  };

  return (
    <main className={styles.container}>
      <Header title='기사님 목록' />
      <AddressForm
        startLocation={startLocation}
        endLocation={goalLocation}
        openStartMap={openStartMap}
        openEndMap={openEndMap}
      />
      <section className={styles.list}>
        {drivers?.map((driver) => (
          <DriverListItem
            key={driver.driverId}
            driver={driver}
            handleClick={handleItemClick}
          />
        ))}
      </section>
      <MapModal
        isVisible={isVisible}
        location={isStart === true ? startLocation : goalLocation}
        changeLocation={
          isStart === true ? changeStartLocation : changeGoalLocation
        }
        closeModal={closeModal}
      />
    </main>
  );
}
