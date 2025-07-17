export function transformSchedule(schedule) {
    const daysInfo = {
        MON: { order: 1, translation: "월" },
        TUE: { order: 2, translation: "화" },
        WED: { order: 3, translation: "수" },
        THU: { order: 4, translation: "목" },
        FRI: { order: 5, translation: "금" },
        SAT: { order: 6, translation: "토" },
        SUN: { order: 7, translation: "일" }
    };

    const makeScheduleObject = (day) => ({
        day,
        min: schedule[day].min,
        hour: schedule[day].hour
    });

    const translateDay = (schedule) => ({
        ...schedule,
        day: daysInfo[schedule.day].translation
    });

    const sortByDay = (a, b) => daysInfo[a.day].order - daysInfo[b.day].order;

    const scheduleArray = Object.keys(schedule).map(makeScheduleObject);
    scheduleArray.sort(sortByDay);
    const translatedScheduleArray = scheduleArray.map(translateDay);

    return translatedScheduleArray;
}
