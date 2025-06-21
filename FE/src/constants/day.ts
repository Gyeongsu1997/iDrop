export type Day = 'MON' | 'TUE' | 'WED' | 'THU' | 'FRI' | 'SAT' | 'SUN';

export const DAY = {
  MON: '월요일',
  TUE: '화요일',
  WED: '수요일',
  THU: '목요일',
  FRI: '금요일',
  SAT: '토요일',
  SUN: '일요일',
} as const;
