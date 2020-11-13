import {DiscountType} from './discount-type-model';
    

export default class Discount{
    public id?: number
    public tipo?: DiscountType
    public dataInicio?: string | Date
    public dataFim?: string | Date
    public percentual?: number
    public ativo?:boolean
    public profissional?: {id:number} 

}