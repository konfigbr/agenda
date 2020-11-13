import { EventEmitter, TemplateRef } from '@angular/core';
import { CalendarEvent, WeekDay } from 'calendar-utils';
import * as ɵngcc0 from '@angular/core';
export declare class CalendarWeekViewHeaderComponent {
    days: WeekDay[];
    locale: string;
    customTemplate: TemplateRef<any>;
    dayHeaderClicked: EventEmitter<{
        day: WeekDay;
        sourceEvent: MouseEvent;
    }>;
    eventDropped: EventEmitter<{
        event: CalendarEvent<any>;
        newStart: Date;
    }>;
    dragEnter: EventEmitter<{
        date: Date;
    }>;
    trackByWeekDayHeaderDate: (index: number, day: WeekDay) => string;
    static ɵfac: ɵngcc0.ɵɵFactoryDef<CalendarWeekViewHeaderComponent, never>;
    static ɵcmp: ɵngcc0.ɵɵComponentDefWithMeta<CalendarWeekViewHeaderComponent, "mwl-calendar-week-view-header", never, { "days": "days"; "locale": "locale"; "customTemplate": "customTemplate"; }, { "dayHeaderClicked": "dayHeaderClicked"; "eventDropped": "eventDropped"; "dragEnter": "dragEnter"; }, never, never>;
}

//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY2FsZW5kYXItd2Vlay12aWV3LWhlYWRlci5jb21wb25lbnQuZC50cyIsInNvdXJjZXMiOlsiY2FsZW5kYXItd2Vlay12aWV3LWhlYWRlci5jb21wb25lbnQuZC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQUNBOztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOzs7QUFDQSIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IEV2ZW50RW1pdHRlciwgVGVtcGxhdGVSZWYgfSBmcm9tICdAYW5ndWxhci9jb3JlJztcbmltcG9ydCB7IENhbGVuZGFyRXZlbnQsIFdlZWtEYXkgfSBmcm9tICdjYWxlbmRhci11dGlscyc7XG5leHBvcnQgZGVjbGFyZSBjbGFzcyBDYWxlbmRhcldlZWtWaWV3SGVhZGVyQ29tcG9uZW50IHtcbiAgICBkYXlzOiBXZWVrRGF5W107XG4gICAgbG9jYWxlOiBzdHJpbmc7XG4gICAgY3VzdG9tVGVtcGxhdGU6IFRlbXBsYXRlUmVmPGFueT47XG4gICAgZGF5SGVhZGVyQ2xpY2tlZDogRXZlbnRFbWl0dGVyPHtcbiAgICAgICAgZGF5OiBXZWVrRGF5O1xuICAgICAgICBzb3VyY2VFdmVudDogTW91c2VFdmVudDtcbiAgICB9PjtcbiAgICBldmVudERyb3BwZWQ6IEV2ZW50RW1pdHRlcjx7XG4gICAgICAgIGV2ZW50OiBDYWxlbmRhckV2ZW50PGFueT47XG4gICAgICAgIG5ld1N0YXJ0OiBEYXRlO1xuICAgIH0+O1xuICAgIGRyYWdFbnRlcjogRXZlbnRFbWl0dGVyPHtcbiAgICAgICAgZGF0ZTogRGF0ZTtcbiAgICB9PjtcbiAgICB0cmFja0J5V2Vla0RheUhlYWRlckRhdGU6IChpbmRleDogbnVtYmVyLCBkYXk6IFdlZWtEYXkpID0+IHN0cmluZztcbn1cbiJdfQ==