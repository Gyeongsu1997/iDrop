import type { Day } from '../constants/day';

export interface Driver {
  driverId: number;
  name: string;
  birthDate: string; // 'YYYY-MM-DD'
  gender: '남성' | '여성';
  phoneNumber: string;
  imageUrl: string;
  career: string;
  introduction: string;
  schedule: Partial<Record<Day, { startTime: string; endTime: string }>>;
}
