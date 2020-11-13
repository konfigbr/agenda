import {CalendarDateFormatter, DateFormatterParams} from 'angular-calendar';
import {DatePipe} from '@angular/common';
import {addHours} from 'date-fns';

export class CustomDateFormatter extends CalendarDateFormatter {

  public weekViewHour({ date, locale }: DateFormatterParams): string {
    const dayAfter = addHours(date, 1)
    return new DatePipe(locale).transform(date, 'HH:mm', locale) + ' - ' + new DatePipe(locale).transform(dayAfter, 'HH:mm', locale);
  }

}
