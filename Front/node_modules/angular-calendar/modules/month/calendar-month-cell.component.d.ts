import { EventEmitter, TemplateRef } from '@angular/core';
import { MonthViewDay, CalendarEvent } from 'calendar-utils';
import { isWithinThreshold } from '../common/util';
import { PlacementArray } from 'positioning';
import * as ɵngcc0 from '@angular/core';
export declare class CalendarMonthCellComponent {
    day: MonthViewDay;
    openDay: MonthViewDay;
    locale: string;
    tooltipPlacement: PlacementArray;
    tooltipAppendToBody: boolean;
    customTemplate: TemplateRef<any>;
    tooltipTemplate: TemplateRef<any>;
    tooltipDelay: number | null;
    highlightDay: EventEmitter<any>;
    unhighlightDay: EventEmitter<any>;
    eventClicked: EventEmitter<{
        event: CalendarEvent<any>;
        sourceEvent: MouseEvent;
    }>;
    trackByEventId: (index: number, event: CalendarEvent<any>) => string | number | CalendarEvent<any>;
    validateDrag: typeof isWithinThreshold;
    static ɵfac: ɵngcc0.ɵɵFactoryDef<CalendarMonthCellComponent, never>;
    static ɵcmp: ɵngcc0.ɵɵComponentDefWithMeta<CalendarMonthCellComponent, "mwl-calendar-month-cell", never, { "day": "day"; "openDay": "openDay"; "locale": "locale"; "tooltipPlacement": "tooltipPlacement"; "tooltipAppendToBody": "tooltipAppendToBody"; "customTemplate": "customTemplate"; "tooltipTemplate": "tooltipTemplate"; "tooltipDelay": "tooltipDelay"; }, { "highlightDay": "highlightDay"; "unhighlightDay": "unhighlightDay"; "eventClicked": "eventClicked"; }, never, never>;
}

//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY2FsZW5kYXItbW9udGgtY2VsbC5jb21wb25lbnQuZC50cyIsInNvdXJjZXMiOlsiY2FsZW5kYXItbW9udGgtY2VsbC5jb21wb25lbnQuZC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQUNBO0FBQ0E7QUFDQTs7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOzs7QUFDQSIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IEV2ZW50RW1pdHRlciwgVGVtcGxhdGVSZWYgfSBmcm9tICdAYW5ndWxhci9jb3JlJztcbmltcG9ydCB7IE1vbnRoVmlld0RheSwgQ2FsZW5kYXJFdmVudCB9IGZyb20gJ2NhbGVuZGFyLXV0aWxzJztcbmltcG9ydCB7IGlzV2l0aGluVGhyZXNob2xkIH0gZnJvbSAnLi4vY29tbW9uL3V0aWwnO1xuaW1wb3J0IHsgUGxhY2VtZW50QXJyYXkgfSBmcm9tICdwb3NpdGlvbmluZyc7XG5leHBvcnQgZGVjbGFyZSBjbGFzcyBDYWxlbmRhck1vbnRoQ2VsbENvbXBvbmVudCB7XG4gICAgZGF5OiBNb250aFZpZXdEYXk7XG4gICAgb3BlbkRheTogTW9udGhWaWV3RGF5O1xuICAgIGxvY2FsZTogc3RyaW5nO1xuICAgIHRvb2x0aXBQbGFjZW1lbnQ6IFBsYWNlbWVudEFycmF5O1xuICAgIHRvb2x0aXBBcHBlbmRUb0JvZHk6IGJvb2xlYW47XG4gICAgY3VzdG9tVGVtcGxhdGU6IFRlbXBsYXRlUmVmPGFueT47XG4gICAgdG9vbHRpcFRlbXBsYXRlOiBUZW1wbGF0ZVJlZjxhbnk+O1xuICAgIHRvb2x0aXBEZWxheTogbnVtYmVyIHwgbnVsbDtcbiAgICBoaWdobGlnaHREYXk6IEV2ZW50RW1pdHRlcjxhbnk+O1xuICAgIHVuaGlnaGxpZ2h0RGF5OiBFdmVudEVtaXR0ZXI8YW55PjtcbiAgICBldmVudENsaWNrZWQ6IEV2ZW50RW1pdHRlcjx7XG4gICAgICAgIGV2ZW50OiBDYWxlbmRhckV2ZW50PGFueT47XG4gICAgICAgIHNvdXJjZUV2ZW50OiBNb3VzZUV2ZW50O1xuICAgIH0+O1xuICAgIHRyYWNrQnlFdmVudElkOiAoaW5kZXg6IG51bWJlciwgZXZlbnQ6IENhbGVuZGFyRXZlbnQ8YW55PikgPT4gc3RyaW5nIHwgbnVtYmVyIHwgQ2FsZW5kYXJFdmVudDxhbnk+O1xuICAgIHZhbGlkYXRlRHJhZzogdHlwZW9mIGlzV2l0aGluVGhyZXNob2xkO1xufVxuIl19