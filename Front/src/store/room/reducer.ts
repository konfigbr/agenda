import RoomState from 'src/models/room/room-state.model';
import * as roomActions from './actions';

export function reducer(state = new RoomState(), action: roomActions.RoomActions): RoomState {
    switch (action.type) {
        case roomActions.RoomActionTypes.SET_ROOM:
            return { ...state, room: { ...state.room, ...action.payload.room } };
        default:
            return state;
    }
}
