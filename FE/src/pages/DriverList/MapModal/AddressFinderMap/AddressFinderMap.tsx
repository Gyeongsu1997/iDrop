import { useRef } from 'react';
import styles from './AddressFinderMap.module.scss';
import { Loader } from '@/components/Loader/Loader';
import { useMap } from '@/hooks/useMap';
import { getLatLng } from '@/utils/map';
import { useMarker } from '@/hooks/useMarker';
import { useCoords } from '@/hooks/useCoords';
import type { Location } from '../../../../types/location';

const searchCoordinateToAddress = (
  coords: naver.maps.Coord,
  changeLocation: (newLoc: Partial<Location>) => void,
) => {
  naver.maps.Service.reverseGeocode(
    {
      coords,
      orders: [
        naver.maps.Service.OrderType.ADDR,
        naver.maps.Service.OrderType.ROAD_ADDR,
      ].join(','),
    },
    (
      status: naver.maps.Service.Status,
      response: naver.maps.Service.ReverseGeocodeResponse,
    ) => {
      if (status === naver.maps.Service.Status.ERROR) {
        console.log(response);
        return alert('Something went wrong!');
      }

      const { roadAddress, jibunAddress } = response.v2.address;
      const address = roadAddress ?? jibunAddress;
      const { y: latitude, x: longitude } = coords;
      const location = {
        address,
        latitude,
        longitude,
      };
      changeLocation(location);
    },
  );
};

const addDragEventListener = ({
  map,
  marker,
  changeLocation,
}: {
  map: naver.maps.Map;
  marker: any;
  changeLocation: (newLoc: Partial<Location>) => void;
}) => {
  if (!map || !marker) {
    return;
  }
  naver.maps.Event.addListener(map, 'drag', () => {
    marker.setPosition(map.getCenter());
  });

  naver.maps.Event.addListener(map, 'dragend', () => {
    const currentCoords: naver.maps.Coord = map.getCenter();
    searchCoordinateToAddress(currentCoords, changeLocation);
  });
};

interface AddressFinderMapProps {
  changeLocation: (newLoc: Partial<Location>) => void;
}

export function AddressFinderMap({ changeLocation }: AddressFinderMapProps) {
  const mapRef = useRef<HTMLDivElement>(null);
  const {
    location: { latitude, longitude },
    isLoading: locationLoading,
  } = useCoords();

  const center: naver.maps.LatLng =
    !locationLoading && getLatLng(latitude, longitude);
  const map: naver.maps.Map = useMap(mapRef, { center }, locationLoading);
  const marker = useMarker(map, map?.getCenter());
  addDragEventListener({ map, marker, changeLocation });

  return (
    <div className={styles.container}>
      {!map && <Loader />}
      <div ref={mapRef} className={styles.map} />
    </div>
  );
}
