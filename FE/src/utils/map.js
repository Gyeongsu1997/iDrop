const initMapOptions = {
  mapDataControl: false,
  scaleControl: true,
  scaleControlOptions: {
    position: naver.maps.Position.TOP_RIGHT,
  },
  logoControl: true,
  logoControlOptions: {
    position: naver.maps.Position.TOP_LEFT,
  },
};

export function generateMarker(map, position) {
  return new naver.maps.Marker({
    position: position,
    map: map,
  });
}

export function getLatLng(x, y) {
  return new naver.maps.LatLng(x, y);
}
