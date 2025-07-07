import { authRequest } from './authenticationAPI';
import { BASE_URL } from '@/constants/constants';

export const getChildren = async () => {
  try {
    const response = await authRequest(`${BASE_URL}/api/children`);
    if (response.ok) {
      return await response.json();
    } else {
      console.error('Failed to GET children');
      return { data: [] };
    }
  } catch (error) {
    console.error(error);
    throw new Error('Faild to GET request');
  }
};
