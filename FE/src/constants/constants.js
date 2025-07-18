export const DEFAULT_COORDS = { latitude: 37.3595704, longitude: 127.105399 };

export const BASE_URL = import.meta.env.VITE_BASE_URL;

export const WEBSOCKET_URL = import.meta.env.VITE_WEBSOCKET_URL;

export const ACCESS_TOKEN = import.meta.env.VITE_ACCESS_TOKEN;

export const PARENT_TOKEN = import.meta.env.VITE_PARENT_TOKEN;

export const PICKUP_STATUS_MAP = {
  승인: 'proceeding',
  대기: 'pending',
  만료: 'expired',
  취소: 'canceled',
};
