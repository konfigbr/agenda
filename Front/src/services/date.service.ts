import { Injectable } from '@angular/core';
import * as moment from 'moment-timezone';

@Injectable({
  providedIn: 'root'
})
export class DateService {

  static TIMEZONE_SERVER = 'America/Sao_Paulo'

  constructor() { }

  previousCurrentServerDate(data: Date): boolean {
    return moment(data).tz(DateService.TIMEZONE_SERVER, true).isBefore()
  }

  formatToAPITimezone(date: Date): string {
    return moment(date).tz(DateService.TIMEZONE_SERVER, true).format()
  }

}
