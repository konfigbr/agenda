import EquipmentType from './equipment-type.model';
import Equipment from './equipment.model';

export default class EquipmentState {
    public equipmentType: EquipmentType = new EquipmentType();
    public equipment: Equipment = new Equipment();
}