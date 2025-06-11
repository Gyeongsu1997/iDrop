import { useState } from 'react';
import type { Location } from '../../types/location';

export const useLocation = () => {
  const [location, setLocation] = useState({
    address: '',
    detailedAddress: '',
    latitude: 0,
    longitude: 0,
  });

  const changeLocation = (newLoc: Partial<Location>) => {
    setLocation((prevLoc) => ({
      ...prevLoc,
      ...newLoc,
    }));
  };

  return {
    location,
    changeLocation,
  };
};
