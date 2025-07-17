import { authRequest } from './authenticationAPI';
import { BASE_URL } from '@/constants/constants';

export const postSubscription = async (subscribeOption) => {
  try {
    const response = await authRequest(`${BASE_URL}/api/subscriptions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(subscribeOption),
    });

    if (response.ok) {
      console.log('Review submitted successfully.');
    } else {
      console.error('구독 요청 실패.');
    }
  } catch (error) {
    throw error;
  }
};

export const getDriverSubscriptions = async (driverId: number) => {
  const response = await authRequest(
    `${BASE_URL}/api/subscriptions/drivers/${driverId}`,
  );
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  return await response.json();
};
