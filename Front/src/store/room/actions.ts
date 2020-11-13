import { Action } from '@ngrx/store';
import Room from 'src/models/room/room.model';

export enum RoomActionTypes {
    SET_ROOM = '[SET_ROOM]: Set room',
}

export class SetRoom implements Action {
    readonly type = RoomActionTypes.SET_ROOM;
    constructor(public payload: { room: Room }) { }
}

export type RoomActions = SetRoom;
