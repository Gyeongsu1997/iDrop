import type { Day } from '../constants/day';

export interface Subscription {
  subscriptionId: number;
  requestDate: string;
  responseDate: string;
  startDate: string;
  status: string; // '승인' | '만료' | '요청' | ...
  startAddress: string;
  goalAddress: string;
  schedule: Partial<Record<Day, string>>;
}
