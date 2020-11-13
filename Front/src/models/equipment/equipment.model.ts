import EquipmentType from './equipment-type.model';

export default class Equipment {
    public id?: string | number;
    public valorResidual!: number;
    public custo!: number | string;
    public dataEntrada: string | Date = new Date();
    public nome: string;
    public cobravel: boolean = true;
    public depreciacao!: number;
    public tipoEquipamento!: EquipmentType;
}
