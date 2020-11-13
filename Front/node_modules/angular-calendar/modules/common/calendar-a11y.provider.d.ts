import { I18nPluralPipe } from '@angular/common';
import { A11yParams } from './calendar-a11y.interface';
/**
 * This class is responsible for adding accessibility to the calendar.
 * You may override any of its methods via angulars DI to suit your requirements.
 * For example:
 *
 * ```typescript
 * import { A11yParams, CalendarA11y } from 'angular-calendar';
 * import { formatDate, I18nPluralPipe } from '@angular/common';
 * import { Injectable } from '@angular/core';
 *
 * // adding your own a11y params
 * export interface CustomA11yParams extends A11yParams {
 *   isDrSuess?: boolean;
 * }
 *
 * @Injectable()
 * export class CustomCalendarA11y extends CalendarA11y {
 *   constructor(protected i18nPlural: I18nPluralPipe) {
 *     super(i18nPlural);
 *   }
 *
 *   // overriding a function
 *   public openDayEventsLandmark({ date, locale, isDrSuess }: CustomA11yParams): string {
 *     if (isDrSuess) {
 *       return `
 *         ${formatDate(date, 'EEEE MMMM d', locale)}
 *          Today you are you! That is truer than true! There is no one alive
 *          who is you-er than you!
 *       `;
 *     }
 *   }
 * }
 *
 * // in your component that uses the calendar
 * providers: [{
 *  provide: CalendarA11y,
 *  useClass: CustomCalendarA11y
 * }]
 * ```
 */
import * as ɵngcc0 from '@angular/core';
export declare class CalendarA11y {
    protected i18nPlural: I18nPluralPipe;
    constructor(i18nPlural: I18nPluralPipe);
    /**
     * Aria label for the badges/date of a cell
     * @example: `Saturday October 19 1 event click to expand`
     */
    monthCell({ day, locale }: A11yParams): string;
    /**
     * Aria label for the open day events start landmark
     * @example: `Saturday October 19 expanded view`
     */
    openDayEventsLandmark({ date, locale }: A11yParams): string;
    /**
     * Aria label for alert that a day in the month view was expanded
     * @example: `Saturday October 19 expanded`
     */
    openDayEventsAlert({ date, locale }: A11yParams): string;
    /**
     * Descriptive aria label for an event
     * @example: `Saturday October 19th, Scott's Pizza Party, from 11:00am to 5:00pm`
     */
    eventDescription({ event, locale }: A11yParams): string;
    /**
     * Descriptive aria label for an all day event
     * @example:
     * `Scott's Party, event spans multiple days: start time October 19 5:00pm, no stop time`
     */
    allDayEventDescription({ event, locale }: A11yParams): string;
    /**
     * Aria label for the calendar event actions icons
     * @returns 'Edit' for fa-pencil icons, and 'Delete' for fa-times icons
     */
    actionButtonLabel({ action }: A11yParams): string;
    /**
     * @returns {number} Tab index to be given to month cells
     */
    monthCellTabIndex(): number;
    /**
     * @returns true if the events inside the month cell should be aria-hidden
     */
    hideMonthCellEvents(): boolean;
    /**
     * @returns true if event titles should be aria-hidden (global)
     */
    hideEventTitle(): boolean;
    /**
     * @returns true if hour segments in the week view should be aria-hidden
     */
    hideWeekHourSegment(): boolean;
    /**
     * @returns true if hour segments in the day view should be aria-hidden
     */
    hideDayHourSegment(): boolean;
    static ɵfac: ɵngcc0.ɵɵFactoryDef<CalendarA11y, never>;
    static ɵprov: ɵngcc0.ɵɵInjectableDef<CalendarA11y>;
}

//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY2FsZW5kYXItYTExeS5wcm92aWRlci5kLnRzIiwic291cmNlcyI6WyJjYWxlbmRhci1hMTF5LnByb3ZpZGVyLmQudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7O0FBQ0EiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBJMThuUGx1cmFsUGlwZSB9IGZyb20gJ0Bhbmd1bGFyL2NvbW1vbic7XG5pbXBvcnQgeyBBMTF5UGFyYW1zIH0gZnJvbSAnLi9jYWxlbmRhci1hMTF5LmludGVyZmFjZSc7XG4vKipcbiAqIFRoaXMgY2xhc3MgaXMgcmVzcG9uc2libGUgZm9yIGFkZGluZyBhY2Nlc3NpYmlsaXR5IHRvIHRoZSBjYWxlbmRhci5cbiAqIFlvdSBtYXkgb3ZlcnJpZGUgYW55IG9mIGl0cyBtZXRob2RzIHZpYSBhbmd1bGFycyBESSB0byBzdWl0IHlvdXIgcmVxdWlyZW1lbnRzLlxuICogRm9yIGV4YW1wbGU6XG4gKlxuICogYGBgdHlwZXNjcmlwdFxuICogaW1wb3J0IHsgQTExeVBhcmFtcywgQ2FsZW5kYXJBMTF5IH0gZnJvbSAnYW5ndWxhci1jYWxlbmRhcic7XG4gKiBpbXBvcnQgeyBmb3JtYXREYXRlLCBJMThuUGx1cmFsUGlwZSB9IGZyb20gJ0Bhbmd1bGFyL2NvbW1vbic7XG4gKiBpbXBvcnQgeyBJbmplY3RhYmxlIH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XG4gKlxuICogLy8gYWRkaW5nIHlvdXIgb3duIGExMXkgcGFyYW1zXG4gKiBleHBvcnQgaW50ZXJmYWNlIEN1c3RvbUExMXlQYXJhbXMgZXh0ZW5kcyBBMTF5UGFyYW1zIHtcbiAqICAgaXNEclN1ZXNzPzogYm9vbGVhbjtcbiAqIH1cbiAqXG4gKiBASW5qZWN0YWJsZSgpXG4gKiBleHBvcnQgY2xhc3MgQ3VzdG9tQ2FsZW5kYXJBMTF5IGV4dGVuZHMgQ2FsZW5kYXJBMTF5IHtcbiAqICAgY29uc3RydWN0b3IocHJvdGVjdGVkIGkxOG5QbHVyYWw6IEkxOG5QbHVyYWxQaXBlKSB7XG4gKiAgICAgc3VwZXIoaTE4blBsdXJhbCk7XG4gKiAgIH1cbiAqXG4gKiAgIC8vIG92ZXJyaWRpbmcgYSBmdW5jdGlvblxuICogICBwdWJsaWMgb3BlbkRheUV2ZW50c0xhbmRtYXJrKHsgZGF0ZSwgbG9jYWxlLCBpc0RyU3Vlc3MgfTogQ3VzdG9tQTExeVBhcmFtcyk6IHN0cmluZyB7XG4gKiAgICAgaWYgKGlzRHJTdWVzcykge1xuICogICAgICAgcmV0dXJuIGBcbiAqICAgICAgICAgJHtmb3JtYXREYXRlKGRhdGUsICdFRUVFIE1NTU0gZCcsIGxvY2FsZSl9XG4gKiAgICAgICAgICBUb2RheSB5b3UgYXJlIHlvdSEgVGhhdCBpcyB0cnVlciB0aGFuIHRydWUhIFRoZXJlIGlzIG5vIG9uZSBhbGl2ZVxuICogICAgICAgICAgd2hvIGlzIHlvdS1lciB0aGFuIHlvdSFcbiAqICAgICAgIGA7XG4gKiAgICAgfVxuICogICB9XG4gKiB9XG4gKlxuICogLy8gaW4geW91ciBjb21wb25lbnQgdGhhdCB1c2VzIHRoZSBjYWxlbmRhclxuICogcHJvdmlkZXJzOiBbe1xuICogIHByb3ZpZGU6IENhbGVuZGFyQTExeSxcbiAqICB1c2VDbGFzczogQ3VzdG9tQ2FsZW5kYXJBMTF5XG4gKiB9XVxuICogYGBgXG4gKi9cbmV4cG9ydCBkZWNsYXJlIGNsYXNzIENhbGVuZGFyQTExeSB7XG4gICAgcHJvdGVjdGVkIGkxOG5QbHVyYWw6IEkxOG5QbHVyYWxQaXBlO1xuICAgIGNvbnN0cnVjdG9yKGkxOG5QbHVyYWw6IEkxOG5QbHVyYWxQaXBlKTtcbiAgICAvKipcbiAgICAgKiBBcmlhIGxhYmVsIGZvciB0aGUgYmFkZ2VzL2RhdGUgb2YgYSBjZWxsXG4gICAgICogQGV4YW1wbGU6IGBTYXR1cmRheSBPY3RvYmVyIDE5IDEgZXZlbnQgY2xpY2sgdG8gZXhwYW5kYFxuICAgICAqL1xuICAgIG1vbnRoQ2VsbCh7IGRheSwgbG9jYWxlIH06IEExMXlQYXJhbXMpOiBzdHJpbmc7XG4gICAgLyoqXG4gICAgICogQXJpYSBsYWJlbCBmb3IgdGhlIG9wZW4gZGF5IGV2ZW50cyBzdGFydCBsYW5kbWFya1xuICAgICAqIEBleGFtcGxlOiBgU2F0dXJkYXkgT2N0b2JlciAxOSBleHBhbmRlZCB2aWV3YFxuICAgICAqL1xuICAgIG9wZW5EYXlFdmVudHNMYW5kbWFyayh7IGRhdGUsIGxvY2FsZSB9OiBBMTF5UGFyYW1zKTogc3RyaW5nO1xuICAgIC8qKlxuICAgICAqIEFyaWEgbGFiZWwgZm9yIGFsZXJ0IHRoYXQgYSBkYXkgaW4gdGhlIG1vbnRoIHZpZXcgd2FzIGV4cGFuZGVkXG4gICAgICogQGV4YW1wbGU6IGBTYXR1cmRheSBPY3RvYmVyIDE5IGV4cGFuZGVkYFxuICAgICAqL1xuICAgIG9wZW5EYXlFdmVudHNBbGVydCh7IGRhdGUsIGxvY2FsZSB9OiBBMTF5UGFyYW1zKTogc3RyaW5nO1xuICAgIC8qKlxuICAgICAqIERlc2NyaXB0aXZlIGFyaWEgbGFiZWwgZm9yIGFuIGV2ZW50XG4gICAgICogQGV4YW1wbGU6IGBTYXR1cmRheSBPY3RvYmVyIDE5dGgsIFNjb3R0J3MgUGl6emEgUGFydHksIGZyb20gMTE6MDBhbSB0byA1OjAwcG1gXG4gICAgICovXG4gICAgZXZlbnREZXNjcmlwdGlvbih7IGV2ZW50LCBsb2NhbGUgfTogQTExeVBhcmFtcyk6IHN0cmluZztcbiAgICAvKipcbiAgICAgKiBEZXNjcmlwdGl2ZSBhcmlhIGxhYmVsIGZvciBhbiBhbGwgZGF5IGV2ZW50XG4gICAgICogQGV4YW1wbGU6XG4gICAgICogYFNjb3R0J3MgUGFydHksIGV2ZW50IHNwYW5zIG11bHRpcGxlIGRheXM6IHN0YXJ0IHRpbWUgT2N0b2JlciAxOSA1OjAwcG0sIG5vIHN0b3AgdGltZWBcbiAgICAgKi9cbiAgICBhbGxEYXlFdmVudERlc2NyaXB0aW9uKHsgZXZlbnQsIGxvY2FsZSB9OiBBMTF5UGFyYW1zKTogc3RyaW5nO1xuICAgIC8qKlxuICAgICAqIEFyaWEgbGFiZWwgZm9yIHRoZSBjYWxlbmRhciBldmVudCBhY3Rpb25zIGljb25zXG4gICAgICogQHJldHVybnMgJ0VkaXQnIGZvciBmYS1wZW5jaWwgaWNvbnMsIGFuZCAnRGVsZXRlJyBmb3IgZmEtdGltZXMgaWNvbnNcbiAgICAgKi9cbiAgICBhY3Rpb25CdXR0b25MYWJlbCh7IGFjdGlvbiB9OiBBMTF5UGFyYW1zKTogc3RyaW5nO1xuICAgIC8qKlxuICAgICAqIEByZXR1cm5zIHtudW1iZXJ9IFRhYiBpbmRleCB0byBiZSBnaXZlbiB0byBtb250aCBjZWxsc1xuICAgICAqL1xuICAgIG1vbnRoQ2VsbFRhYkluZGV4KCk6IG51bWJlcjtcbiAgICAvKipcbiAgICAgKiBAcmV0dXJucyB0cnVlIGlmIHRoZSBldmVudHMgaW5zaWRlIHRoZSBtb250aCBjZWxsIHNob3VsZCBiZSBhcmlhLWhpZGRlblxuICAgICAqL1xuICAgIGhpZGVNb250aENlbGxFdmVudHMoKTogYm9vbGVhbjtcbiAgICAvKipcbiAgICAgKiBAcmV0dXJucyB0cnVlIGlmIGV2ZW50IHRpdGxlcyBzaG91bGQgYmUgYXJpYS1oaWRkZW4gKGdsb2JhbClcbiAgICAgKi9cbiAgICBoaWRlRXZlbnRUaXRsZSgpOiBib29sZWFuO1xuICAgIC8qKlxuICAgICAqIEByZXR1cm5zIHRydWUgaWYgaG91ciBzZWdtZW50cyBpbiB0aGUgd2VlayB2aWV3IHNob3VsZCBiZSBhcmlhLWhpZGRlblxuICAgICAqL1xuICAgIGhpZGVXZWVrSG91clNlZ21lbnQoKTogYm9vbGVhbjtcbiAgICAvKipcbiAgICAgKiBAcmV0dXJucyB0cnVlIGlmIGhvdXIgc2VnbWVudHMgaW4gdGhlIGRheSB2aWV3IHNob3VsZCBiZSBhcmlhLWhpZGRlblxuICAgICAqL1xuICAgIGhpZGVEYXlIb3VyU2VnbWVudCgpOiBib29sZWFuO1xufVxuIl19