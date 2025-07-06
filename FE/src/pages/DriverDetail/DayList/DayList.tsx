import styles from './DayList.module.scss';
import { LabelledList } from '@/components/Layout/LabelledList';
import { DAY } from '@/constants/day';
import { type State as Schedule } from './useSchedule';

interface DayListProps {
  schedule: Schedule;
  handleWeekClick: any;
}

export function DayList({ schedule, handleWeekClick }: DayListProps) {
  const dayListElement = Object.entries(DAY).map(([key, value]) => {
    return (
      <li
        key={key}
        className={`${styles.dayItem} ${schedule[key] && styles.active}`}
        onClick={() => handleWeekClick(key)}
      >
        <p>{value[0]}</p>
      </li>
    );
  });

  return (
    <LabelledList articleStyle='dayBox' label='픽업 요일'>
      {dayListElement}
    </LabelledList>
  );
}
