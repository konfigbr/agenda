import AuthState from './auth/auth-state.model';
import EquipmentState from './equipment/equipment-state.model';
import RoomState from './room/room-state.model';

export interface AppState {
    auth: AuthState;
    equipment: EquipmentState;
    room: RoomState;
}
