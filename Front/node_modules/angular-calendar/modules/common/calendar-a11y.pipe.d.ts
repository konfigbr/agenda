import { PipeTransform } from '@angular/core';
import { CalendarA11y } from './calendar-a11y.provider';
import { A11yParams } from './calendar-a11y.interface';
/**
 * This pipe is primarily for rendering aria labels. Example usage:
 * ```typescript
 * // where `myEvent` is a `CalendarEvent` and myLocale is a locale identifier
 * {{ { event: myEvent, locale: myLocale } | calendarA11y: 'eventDescription' }}
 * ```
 */
import * as ɵngcc0 from '@angular/core';
export declare class CalendarA11yPipe implements PipeTransform {
    private calendarA11y;
    private locale;
    constructor(calendarA11y: CalendarA11y, locale: string);
    transform(a11yParams: A11yParams, method: string): string;
    static ɵfac: ɵngcc0.ɵɵFactoryDef<CalendarA11yPipe, never>;
    static ɵpipe: ɵngcc0.ɵɵPipeDefWithMeta<CalendarA11yPipe, "calendarA11y">;
}

//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY2FsZW5kYXItYTExeS5waXBlLmQudHMiLCJzb3VyY2VzIjpbImNhbGVuZGFyLWExMXkucGlwZS5kLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7OztBQUNBIiwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHsgUGlwZVRyYW5zZm9ybSB9IGZyb20gJ0Bhbmd1bGFyL2NvcmUnO1xuaW1wb3J0IHsgQ2FsZW5kYXJBMTF5IH0gZnJvbSAnLi9jYWxlbmRhci1hMTF5LnByb3ZpZGVyJztcbmltcG9ydCB7IEExMXlQYXJhbXMgfSBmcm9tICcuL2NhbGVuZGFyLWExMXkuaW50ZXJmYWNlJztcbi8qKlxuICogVGhpcyBwaXBlIGlzIHByaW1hcmlseSBmb3IgcmVuZGVyaW5nIGFyaWEgbGFiZWxzLiBFeGFtcGxlIHVzYWdlOlxuICogYGBgdHlwZXNjcmlwdFxuICogLy8gd2hlcmUgYG15RXZlbnRgIGlzIGEgYENhbGVuZGFyRXZlbnRgIGFuZCBteUxvY2FsZSBpcyBhIGxvY2FsZSBpZGVudGlmaWVyXG4gKiB7eyB7IGV2ZW50OiBteUV2ZW50LCBsb2NhbGU6IG15TG9jYWxlIH0gfCBjYWxlbmRhckExMXk6ICdldmVudERlc2NyaXB0aW9uJyB9fVxuICogYGBgXG4gKi9cbmV4cG9ydCBkZWNsYXJlIGNsYXNzIENhbGVuZGFyQTExeVBpcGUgaW1wbGVtZW50cyBQaXBlVHJhbnNmb3JtIHtcbiAgICBwcml2YXRlIGNhbGVuZGFyQTExeTtcbiAgICBwcml2YXRlIGxvY2FsZTtcbiAgICBjb25zdHJ1Y3RvcihjYWxlbmRhckExMXk6IENhbGVuZGFyQTExeSwgbG9jYWxlOiBzdHJpbmcpO1xuICAgIHRyYW5zZm9ybShhMTF5UGFyYW1zOiBBMTF5UGFyYW1zLCBtZXRob2Q6IHN0cmluZyk6IHN0cmluZztcbn1cbiJdfQ==