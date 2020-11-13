import { createSelector } from '@ngrx/store';
import { AppState } from 'src/models/app-state.model';
import RoomState from 'src/models/room/room-state.model';

export const roomState = (state: AppState) => state.room;

export const selectRoom = createSelector(
    roomState,
    (state: RoomState) => state.room
);
