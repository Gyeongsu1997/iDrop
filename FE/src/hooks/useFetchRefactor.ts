import { useEffect, useReducer, type Reducer } from 'react';

type State = {
  loading: boolean;
  data: any;
  error: any;
};

type Action =
  | { type: 'LOADING' }
  | { type: 'SUCCESS'; data: any }
  | { type: 'ERROR'; error: any };

const reducer = (_: State, action: Action): State => {
  switch (action.type) {
    case 'LOADING':
      return {
        loading: true,
        data: null,
        error: null,
      };
    case 'SUCCESS':
      return {
        loading: false,
        data: action.data,
        error: null,
      };
    case 'ERROR':
      return {
        loading: false,
        data: null,
        error: action.error,
      };
    default:
      throw new Error(`Unhandled action type`);
  }
};

export const useFetch = (callback: () => any, deps = [], skip = false) => {
  const [state, dispatch] = useReducer<Reducer<State, Action>>(reducer, {
    loading: false,
    data: null,
    error: null,
  });

  const fetchData = async () => {
    dispatch({ type: 'LOADING' });
    try {
      const data = await callback();
      dispatch({ type: 'SUCCESS', data });
    } catch (error) {
      dispatch({ type: 'ERROR', error });
    }
  };

  useEffect(() => {
    if (skip) {
      return;
    }
    fetchData();
  }, deps);

  return [state, fetchData];
};
