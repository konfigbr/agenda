import { Action } from '@ngrx/store';
import EquipmentType from 'src/models/equipment/equipment-type.model';
import Equipment from 'src/models/equipment/equipment.model';

export enum EquipmentActionTypes {
    SET_EQUIPMENT_TYPE = '[SET_EQUIPMENT_TYPE]: Set equipment type',
    SET_EQUIPMENT = '[SET_EQUIPMENT]: Set equipment',
}

export class SetEquipmentType implements Action {
    readonly type = EquipmentActionTypes.SET_EQUIPMENT_TYPE;
    constructor(public payload: { equipmentType: EquipmentType }) { }
}

export class SetEquipment implements Action {
    readonly type = EquipmentActionTypes.SET_EQUIPMENT;
    constructor(public payload: { equipment: Equipment }) { }
}

export type EquipmentActions = SetEquipmentType | SetEquipment;
