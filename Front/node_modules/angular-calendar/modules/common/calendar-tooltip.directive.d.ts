import { OnDestroy, Injector, ComponentFactoryResolver, ViewContainerRef, ElementRef, Renderer2, TemplateRef, OnChanges, SimpleChanges } from '@angular/core';
import { PlacementArray } from 'positioning';
import { CalendarEvent } from 'calendar-utils';
import * as ɵngcc0 from '@angular/core';
export declare class CalendarTooltipWindowComponent {
    contents: string;
    placement: string;
    event: CalendarEvent;
    customTemplate: TemplateRef<any>;
    static ɵfac: ɵngcc0.ɵɵFactoryDef<CalendarTooltipWindowComponent, never>;
    static ɵcmp: ɵngcc0.ɵɵComponentDefWithMeta<CalendarTooltipWindowComponent, "mwl-calendar-tooltip-window", never, { "contents": "contents"; "placement": "placement"; "event": "event"; "customTemplate": "customTemplate"; }, {}, never, never>;
}
export declare class CalendarTooltipDirective implements OnDestroy, OnChanges {
    private elementRef;
    private injector;
    private renderer;
    private viewContainerRef;
    private document;
    contents: string;
    placement: PlacementArray;
    customTemplate: TemplateRef<any>;
    event: CalendarEvent;
    appendToBody: boolean;
    delay: number | null;
    private tooltipFactory;
    private tooltipRef;
    private cancelTooltipDelay$;
    constructor(elementRef: ElementRef, injector: Injector, renderer: Renderer2, componentFactoryResolver: ComponentFactoryResolver, viewContainerRef: ViewContainerRef, document: any);
    ngOnChanges(changes: SimpleChanges): void;
    ngOnDestroy(): void;
    onMouseOver(): void;
    onMouseOut(): void;
    private show;
    private hide;
    private positionTooltip;
    static ɵfac: ɵngcc0.ɵɵFactoryDef<CalendarTooltipDirective, never>;
    static ɵdir: ɵngcc0.ɵɵDirectiveDefWithMeta<CalendarTooltipDirective, "[mwlCalendarTooltip]", never, { "placement": "tooltipPlacement"; "delay": "tooltipDelay"; "contents": "mwlCalendarTooltip"; "customTemplate": "tooltipTemplate"; "event": "tooltipEvent"; "appendToBody": "tooltipAppendToBody"; }, {}, never>;
}

//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY2FsZW5kYXItdG9vbHRpcC5kaXJlY3RpdmUuZC50cyIsInNvdXJjZXMiOlsiY2FsZW5kYXItdG9vbHRpcC5kaXJlY3RpdmUuZC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQUNBO0FBQ0E7O0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7O0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOzs7QUFDQSIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IE9uRGVzdHJveSwgSW5qZWN0b3IsIENvbXBvbmVudEZhY3RvcnlSZXNvbHZlciwgVmlld0NvbnRhaW5lclJlZiwgRWxlbWVudFJlZiwgUmVuZGVyZXIyLCBUZW1wbGF0ZVJlZiwgT25DaGFuZ2VzLCBTaW1wbGVDaGFuZ2VzIH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XG5pbXBvcnQgeyBQbGFjZW1lbnRBcnJheSB9IGZyb20gJ3Bvc2l0aW9uaW5nJztcbmltcG9ydCB7IENhbGVuZGFyRXZlbnQgfSBmcm9tICdjYWxlbmRhci11dGlscyc7XG5leHBvcnQgZGVjbGFyZSBjbGFzcyBDYWxlbmRhclRvb2x0aXBXaW5kb3dDb21wb25lbnQge1xuICAgIGNvbnRlbnRzOiBzdHJpbmc7XG4gICAgcGxhY2VtZW50OiBzdHJpbmc7XG4gICAgZXZlbnQ6IENhbGVuZGFyRXZlbnQ7XG4gICAgY3VzdG9tVGVtcGxhdGU6IFRlbXBsYXRlUmVmPGFueT47XG59XG5leHBvcnQgZGVjbGFyZSBjbGFzcyBDYWxlbmRhclRvb2x0aXBEaXJlY3RpdmUgaW1wbGVtZW50cyBPbkRlc3Ryb3ksIE9uQ2hhbmdlcyB7XG4gICAgcHJpdmF0ZSBlbGVtZW50UmVmO1xuICAgIHByaXZhdGUgaW5qZWN0b3I7XG4gICAgcHJpdmF0ZSByZW5kZXJlcjtcbiAgICBwcml2YXRlIHZpZXdDb250YWluZXJSZWY7XG4gICAgcHJpdmF0ZSBkb2N1bWVudDtcbiAgICBjb250ZW50czogc3RyaW5nO1xuICAgIHBsYWNlbWVudDogUGxhY2VtZW50QXJyYXk7XG4gICAgY3VzdG9tVGVtcGxhdGU6IFRlbXBsYXRlUmVmPGFueT47XG4gICAgZXZlbnQ6IENhbGVuZGFyRXZlbnQ7XG4gICAgYXBwZW5kVG9Cb2R5OiBib29sZWFuO1xuICAgIGRlbGF5OiBudW1iZXIgfCBudWxsO1xuICAgIHByaXZhdGUgdG9vbHRpcEZhY3Rvcnk7XG4gICAgcHJpdmF0ZSB0b29sdGlwUmVmO1xuICAgIHByaXZhdGUgY2FuY2VsVG9vbHRpcERlbGF5JDtcbiAgICBjb25zdHJ1Y3RvcihlbGVtZW50UmVmOiBFbGVtZW50UmVmLCBpbmplY3RvcjogSW5qZWN0b3IsIHJlbmRlcmVyOiBSZW5kZXJlcjIsIGNvbXBvbmVudEZhY3RvcnlSZXNvbHZlcjogQ29tcG9uZW50RmFjdG9yeVJlc29sdmVyLCB2aWV3Q29udGFpbmVyUmVmOiBWaWV3Q29udGFpbmVyUmVmLCBkb2N1bWVudDogYW55KTtcbiAgICBuZ09uQ2hhbmdlcyhjaGFuZ2VzOiBTaW1wbGVDaGFuZ2VzKTogdm9pZDtcbiAgICBuZ09uRGVzdHJveSgpOiB2b2lkO1xuICAgIG9uTW91c2VPdmVyKCk6IHZvaWQ7XG4gICAgb25Nb3VzZU91dCgpOiB2b2lkO1xuICAgIHByaXZhdGUgc2hvdztcbiAgICBwcml2YXRlIGhpZGU7XG4gICAgcHJpdmF0ZSBwb3NpdGlvblRvb2x0aXA7XG59XG4iXX0=