import * as equipmentActions from './actions';
import EquipmentState from 'src/models/equipment/equipment-state.model';

export function reducer(state = new EquipmentState(), action: equipmentActions.EquipmentActions): EquipmentState {
    switch (action.type) {
        case equipmentActions.EquipmentActionTypes.SET_EQUIPMENT_TYPE:
            return { ...state, equipmentType: { ...state.equipmentType, ...action.payload.equipmentType } };
        case equipmentActions.EquipmentActionTypes.SET_EQUIPMENT:
            return { ...state, equipment: { ...state.equipment, ...action.payload.equipment } };
        default:
            return state;
    }
}
