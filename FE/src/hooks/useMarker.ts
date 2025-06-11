import { useEffect, useState } from 'react';

export function useMarker(
  map: naver.maps.Map,
  position: naver.maps.Coord,
  options = {},
) {
  const [marker, setMarker] = useState(null);
  useEffect(() => {
    if (!map || !position) {
      return;
    }
    const newMarker = new naver.maps.Marker({
      map,
      position,
      ...options,
    });
    setMarker(newMarker);
  }, [map]);

  return marker;
}
