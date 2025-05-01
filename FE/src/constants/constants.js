export const userType = {
    parent: 0,
    driver: 1
};

export const BASE_URL = import.meta.env.VITE_BASE_URL;

export const DEFAULT_COORDS = { latitude: 0, longitude: 0 };

export const WEBSOCKET_URL = import.meta.env.VITE_WEBSOCKET_URL;

export const ACCESS_TOKEN = import.meta.env.VITE_ACCESS_TOKEN;

export const PARENT_TOKEN = import.meta.env.VITE_PARENT_TOKEN;

export const SEARCH_PAGE = (function () {
    const WEEK = ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"];
    const WEEK_MAP = {
        SUN: "일요일",
        MON: "월요일",
        TUE: "화요일",
        WED: "수요일",
        THU: "목요일",
        FRI: "금요일",
        SAT: "토요일"
    };

    return {
        WEEK,
        WEEK_MAP
    };
})();

export const PICKUP_STATUS_MAP = {
    승인: "proceeding",
    대기: "pending",
    만료: "expired",
    취소: "canceled"
};
