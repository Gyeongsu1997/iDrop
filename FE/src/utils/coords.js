const DEFAULT_COORDS = { latitude: 0, longitude: 0 };

export function getCurrentLocation() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('Geolocation is not supported!'));
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        resolve({ latitude, longitude });
      },
      (error) => {
        reject(error);
      },
    );
  });
}

export const getCurrentLocation = async () => {
  if (!navigator.geolocation) {
    throw new Error('Geolocation is not supported!');
  }
  navigator.geolocation.getCurrentPosition();
};

export async function getCurrentCoords(defaultCoords = DEFAULT_COORDS) {
  try {
    const response = await getCurrentLocation();
    return response;
  } catch (error) {
    console.error(error.message);
    return defaultCoords;
  }
}
