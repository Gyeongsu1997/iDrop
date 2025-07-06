import { useState } from 'react';
import { formatNumberDigit } from '@/utils/number';
import styles from './NumericSelector.module.scss';

interface NumericSelectorProps {
  start: number;
  end: number;
  step: number;
  targetLength: number;
  padChar: string;
  unit: string;
  defaultValue: string;
  onSelect: (value: string) => void;
}

export function NumericSelector({
  start,
  end,
  step,
  targetLength,
  padChar,
  unit,
  defaultValue = '00',
  onSelect,
}: NumericSelectorProps) {
  const [value, setValue] = useState(defaultValue);
  const handleChange = ({ target: { value } }) => {
    setValue(value);
    onSelect?.(value);
  };

  const optionLength = (end - start) / step + 1;
  const options = Array.from({ length: optionLength }, (_, index) => {
    const optionValue = start + index * step;
    const value = formatNumberDigit(optionValue, targetLength, padChar);
    return (
      <option key={value} value={value}>
        {value}
      </option>
    );
  });

  return (
    <>
      <select className={styles.select} value={value} onChange={handleChange}>
        {options}
      </select>
      <label className={styles.suffix}>{unit}</label>
    </>
  );
}
