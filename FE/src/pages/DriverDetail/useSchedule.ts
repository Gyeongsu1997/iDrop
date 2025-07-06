import { useReducer, type Reducer } from 'react';
import type { Day } from '../../constants/day';

const DEFAULT_TIME = { hour: '08', min: '10' };

export type State = Partial<Record<Day, { hour: string; min: string }>>;

type Action =
  | { type: 'ADD_DAY'; payload: { day: Day } }
  | { type: 'DELETE_DAY'; payload: { day: Day } }
  | {
      type: 'CHANGE_HOUR';
      payload: { day: Day; newHour: string };
    }
  | {
      type: 'CHANGE_MINUTE';
      payload: { day: Day; newMin: string };
    };

const reducer = (state: State, action: Action): State => {
  switch (action.type) {
    case 'ADD_DAY':
      return {
        ...state,
        [action.payload.day]: DEFAULT_TIME,
      };
    case 'DELETE_DAY':
      const newState = { ...state };
      delete newState[action.payload.day];
      return newState;
    case 'CHANGE_HOUR':
      return {
        ...state,
        [action.payload.day]: {
          ...state[action.payload.day],
          hour: action.payload.newHour,
        },
      };
    case 'CHANGE_MINUTE':
      return {
        ...state,
        [action.payload.day]: {
          ...state[action.payload.day],
          min: action.payload.newMin,
        },
      };
    default:
      return state;
  }
};

export const useSchedule = () => {
  const [state, dispatch] = useReducer<Reducer<State, Action>>(reducer, {});

  const toggleDay = (day: Day) => {
    if (state[day]) {
      return dispatch({
        type: 'DELETE_DAY',
        payload: { day },
      });
    }
    dispatch({
      type: 'ADD_DAY',
      payload: { day },
    });
  };

  const changeHour = (day: Day) => (newHour: string) => {
    dispatch({
      type: 'CHANGE_HOUR',
      payload: { day, newHour },
    });
  };

  const changeMinute = (day: Day) => (newMin: string) => {
    dispatch({
      type: 'CHANGE_MINUTE',
      payload: { day, newMin },
    });
  };

  return {
    schedule: state,
    toggleDay,
    changeHour,
    changeMinute,
  };
};
