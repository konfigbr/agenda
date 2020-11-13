import { createSelector } from '@ngrx/store';
import { AppState } from 'src/models/app-state.model';
import EquipmentState from 'src/models/equipment/equipment-state.model';

export const equipmentState = (state: AppState) => state.equipment;

export const selectEquipmentType = createSelector(
    equipmentState,
    (state: EquipmentState) => state.equipmentType
);

export const selectEquipment = createSelector(
    equipmentState,
    (state: EquipmentState) => state.equipment
);
