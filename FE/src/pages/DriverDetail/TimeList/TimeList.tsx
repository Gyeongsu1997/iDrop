import styles from './TimeList.module.scss';
import { LabelledList } from '@/components/Layout/LabelledList';
import { NumericSelector } from '@/components/Input/NumericSelector';
import { Day, DAY } from '@/constants/day';
import { type State as Schedule } from './useSchedule';

interface TimeItemProps {
  day: Day;
  onHourChange: (day: Day) => (newHour: string) => void;
  onMinuteChange: (day: Day) => (newMin: string) => void;
}

const TimeItem = ({ day, onHourChange, onMinuteChange }: TimeItemProps) => {
  const handleHourSelect = onHourChange(day);
  const handleMinuteSelect = onMinuteChange(day);
  return (
    <li className={styles.timeItem}>
      <h6 className={styles.timeDay}>{DAY[day]}</h6>
      <form className={styles.timeForm}>
        <div className={styles.timeWrapper}>
          <NumericSelector
            start={0}
            end={23}
            step={1}
            targetLength={2}
            padChar='0'
            unit='시'
            defaultValue='08'
            onSelect={handleHourSelect}
          />
          <NumericSelector
            start={0}
            end={50}
            step={10}
            targetLength={2}
            padChar='0'
            defaultValue='10'
            unit='분'
            onSelect={handleMinuteSelect}
          />
        </div>
      </form>
    </li>
  );
};

interface TimeListProps {
  schedule: Schedule;
  onHourChange: (day: Day) => (newHour: string) => void;
  onMinuteChange: (day: Day) => (newMin: string) => void;
}

export function TimeList({
  schedule,
  onHourChange,
  onMinuteChange,
}: TimeListProps) {
  const filtered = Object.keys(DAY).filter((day) => day in schedule);

  const timeListElement =
    filtered.length > 0 ? (
      filtered.map((day, index) => (
        <TimeItem
          key={`day-${index}`}
          day={day}
          onHourChange={onHourChange}
          onMinuteChange={onMinuteChange}
        />
      ))
    ) : (
      <li className={styles.timeEmpty}>픽업 요일을 선택해주세요</li>
    );

  return (
    <LabelledList articleStyle='time' label='픽업 시간'>
      {timeListElement}
    </LabelledList>
  );
}
