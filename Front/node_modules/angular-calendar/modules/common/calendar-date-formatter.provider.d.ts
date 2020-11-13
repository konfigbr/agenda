import { CalendarAngularDateFormatter } from './calendar-angular-date-formatter.provider';
/**
 * This class is responsible for all formatting of dates. There are 3 implementations available, the `CalendarAngularDateFormatter` (default) which uses the angular date pipe to format dates, the `CalendarNativeDateFormatter` which will use the <a href="https://developer.mozilla.org/en/docs/Web/JavaScript/Reference/Global_Objects/Intl" target="_blank">Intl</a> API to format dates, or there is the `CalendarMomentDateFormatter` which uses <a href="http://momentjs.com/" target="_blank">moment</a>.
 *
 * If you wish, you may override any of the defaults via angulars DI. For example:
 *
 * ```typescript
 * import { CalendarDateFormatter, DateFormatterParams } from 'angular-calendar';
 * import { formatDate } from '@angular/common';
 * import { Injectable } from '@angular/core';
 *
 * @Injectable()
 * class CustomDateFormatter extends CalendarDateFormatter {
 *
 *   public monthViewColumnHeader({date, locale}: DateFormatterParams): string {
 *     return formatDate(date, 'EEE', locale); // use short week days
 *   }
 *
 * }
 *
 * // in your component that uses the calendar
 * providers: [{
 *   provide: CalendarDateFormatter,
 *   useClass: CustomDateFormatter
 * }]
 * ```
 */
import * as ɵngcc0 from '@angular/core';
export declare class CalendarDateFormatter extends CalendarAngularDateFormatter {
    static ɵfac: ɵngcc0.ɵɵFactoryDef<CalendarDateFormatter, never>;
    static ɵprov: ɵngcc0.ɵɵInjectableDef<CalendarDateFormatter>;
}

//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY2FsZW5kYXItZGF0ZS1mb3JtYXR0ZXIucHJvdmlkZXIuZC50cyIsInNvdXJjZXMiOlsiY2FsZW5kYXItZGF0ZS1mb3JtYXR0ZXIucHJvdmlkZXIuZC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FBQ0E7OztBQUNBIiwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHsgQ2FsZW5kYXJBbmd1bGFyRGF0ZUZvcm1hdHRlciB9IGZyb20gJy4vY2FsZW5kYXItYW5ndWxhci1kYXRlLWZvcm1hdHRlci5wcm92aWRlcic7XG4vKipcbiAqIFRoaXMgY2xhc3MgaXMgcmVzcG9uc2libGUgZm9yIGFsbCBmb3JtYXR0aW5nIG9mIGRhdGVzLiBUaGVyZSBhcmUgMyBpbXBsZW1lbnRhdGlvbnMgYXZhaWxhYmxlLCB0aGUgYENhbGVuZGFyQW5ndWxhckRhdGVGb3JtYXR0ZXJgIChkZWZhdWx0KSB3aGljaCB1c2VzIHRoZSBhbmd1bGFyIGRhdGUgcGlwZSB0byBmb3JtYXQgZGF0ZXMsIHRoZSBgQ2FsZW5kYXJOYXRpdmVEYXRlRm9ybWF0dGVyYCB3aGljaCB3aWxsIHVzZSB0aGUgPGEgaHJlZj1cImh0dHBzOi8vZGV2ZWxvcGVyLm1vemlsbGEub3JnL2VuL2RvY3MvV2ViL0phdmFTY3JpcHQvUmVmZXJlbmNlL0dsb2JhbF9PYmplY3RzL0ludGxcIiB0YXJnZXQ9XCJfYmxhbmtcIj5JbnRsPC9hPiBBUEkgdG8gZm9ybWF0IGRhdGVzLCBvciB0aGVyZSBpcyB0aGUgYENhbGVuZGFyTW9tZW50RGF0ZUZvcm1hdHRlcmAgd2hpY2ggdXNlcyA8YSBocmVmPVwiaHR0cDovL21vbWVudGpzLmNvbS9cIiB0YXJnZXQ9XCJfYmxhbmtcIj5tb21lbnQ8L2E+LlxuICpcbiAqIElmIHlvdSB3aXNoLCB5b3UgbWF5IG92ZXJyaWRlIGFueSBvZiB0aGUgZGVmYXVsdHMgdmlhIGFuZ3VsYXJzIERJLiBGb3IgZXhhbXBsZTpcbiAqXG4gKiBgYGB0eXBlc2NyaXB0XG4gKiBpbXBvcnQgeyBDYWxlbmRhckRhdGVGb3JtYXR0ZXIsIERhdGVGb3JtYXR0ZXJQYXJhbXMgfSBmcm9tICdhbmd1bGFyLWNhbGVuZGFyJztcbiAqIGltcG9ydCB7IGZvcm1hdERhdGUgfSBmcm9tICdAYW5ndWxhci9jb21tb24nO1xuICogaW1wb3J0IHsgSW5qZWN0YWJsZSB9IGZyb20gJ0Bhbmd1bGFyL2NvcmUnO1xuICpcbiAqIEBJbmplY3RhYmxlKClcbiAqIGNsYXNzIEN1c3RvbURhdGVGb3JtYXR0ZXIgZXh0ZW5kcyBDYWxlbmRhckRhdGVGb3JtYXR0ZXIge1xuICpcbiAqICAgcHVibGljIG1vbnRoVmlld0NvbHVtbkhlYWRlcih7ZGF0ZSwgbG9jYWxlfTogRGF0ZUZvcm1hdHRlclBhcmFtcyk6IHN0cmluZyB7XG4gKiAgICAgcmV0dXJuIGZvcm1hdERhdGUoZGF0ZSwgJ0VFRScsIGxvY2FsZSk7IC8vIHVzZSBzaG9ydCB3ZWVrIGRheXNcbiAqICAgfVxuICpcbiAqIH1cbiAqXG4gKiAvLyBpbiB5b3VyIGNvbXBvbmVudCB0aGF0IHVzZXMgdGhlIGNhbGVuZGFyXG4gKiBwcm92aWRlcnM6IFt7XG4gKiAgIHByb3ZpZGU6IENhbGVuZGFyRGF0ZUZvcm1hdHRlcixcbiAqICAgdXNlQ2xhc3M6IEN1c3RvbURhdGVGb3JtYXR0ZXJcbiAqIH1dXG4gKiBgYGBcbiAqL1xuZXhwb3J0IGRlY2xhcmUgY2xhc3MgQ2FsZW5kYXJEYXRlRm9ybWF0dGVyIGV4dGVuZHMgQ2FsZW5kYXJBbmd1bGFyRGF0ZUZvcm1hdHRlciB7XG59XG4iXX0=