import styles from './ScheduleList.module.scss';

export function ScheduleList({ schedule }) {
  const getScheduleElement = (scheduleData) => (
    <ScheduleItem key={scheduleData.day} scheduleData={scheduleData} />
  );

  return (
    <ul className={styles.scheduleList}>{schedule.map(getScheduleElement)}</ul>
  );
}

function ScheduleItem({ scheduleData }) {
  return (
    <li
      className={`${styles.scheduleItem}`}
    >{`${scheduleData.day} ${scheduleData.time}`}</li>
  );
}
