import { TemplateRef } from '@angular/core';
import { CalendarEvent, EventAction } from 'calendar-utils';
import * as ɵngcc0 from '@angular/core';
export declare class CalendarEventActionsComponent {
    event: CalendarEvent;
    customTemplate: TemplateRef<any>;
    trackByActionId: (index: number, action: EventAction) => string | number | EventAction;
    static ɵfac: ɵngcc0.ɵɵFactoryDef<CalendarEventActionsComponent, never>;
    static ɵcmp: ɵngcc0.ɵɵComponentDefWithMeta<CalendarEventActionsComponent, "mwl-calendar-event-actions", never, { "event": "event"; "customTemplate": "customTemplate"; }, {}, never, never>;
}

//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY2FsZW5kYXItZXZlbnQtYWN0aW9ucy5jb21wb25lbnQuZC50cyIsInNvdXJjZXMiOlsiY2FsZW5kYXItZXZlbnQtYWN0aW9ucy5jb21wb25lbnQuZC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQUNBOztBQUNBO0FBQ0E7QUFDQTtBQUNBOzs7QUFDQSIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IFRlbXBsYXRlUmVmIH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XG5pbXBvcnQgeyBDYWxlbmRhckV2ZW50LCBFdmVudEFjdGlvbiB9IGZyb20gJ2NhbGVuZGFyLXV0aWxzJztcbmV4cG9ydCBkZWNsYXJlIGNsYXNzIENhbGVuZGFyRXZlbnRBY3Rpb25zQ29tcG9uZW50IHtcbiAgICBldmVudDogQ2FsZW5kYXJFdmVudDtcbiAgICBjdXN0b21UZW1wbGF0ZTogVGVtcGxhdGVSZWY8YW55PjtcbiAgICB0cmFja0J5QWN0aW9uSWQ6IChpbmRleDogbnVtYmVyLCBhY3Rpb246IEV2ZW50QWN0aW9uKSA9PiBzdHJpbmcgfCBudW1iZXIgfCBFdmVudEFjdGlvbjtcbn1cbiJdfQ==