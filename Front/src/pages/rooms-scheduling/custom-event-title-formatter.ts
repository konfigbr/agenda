import {Injectable} from '@angular/core';
import { CalendarEvent, CalendarEventTitleFormatter } from 'angular-calendar';

@Injectable()
export class CustomEventTitleFormatter extends CalendarEventTitleFormatter {
  weekTooltip(event: CalendarEvent, title: string): string {
    if (!event.meta || !event.meta.tmpEvent) {
      return super.weekTooltip(event, title);
    }
  }

  dayTooltip(event: CalendarEvent, title: string): string {
    if (!event.meta || !event.meta.tmpEvent) {
      return super.dayTooltip(event, title);
    }
  }
}
