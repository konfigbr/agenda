import { ActionReducerMap } from '@ngrx/store';
import { AppState } from 'src/models/app-state.model';
import * as authReducer from './auth/reducer';
import * as equipmentReducer from './equipment/reducer';
import * as roomReducer from './room/reducer';

export const appReducers: ActionReducerMap<AppState> = {
    auth: authReducer.reducer,
    equipment: equipmentReducer.reducer,
    room: roomReducer.reducer
};

export default appReducers;
