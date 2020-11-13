import PriceRange from './price-range.model';
export default class Patient {
    public id?: number | string;
    public nome?: string;    
    public username?: string; 
    public password?: string; 
    public confirmPassword?: string = "";
    public telefone?: string; 
    public rg?: string; 
    public cep?: string; 
    public endereco?: string; 
    public cpf?: string; 
    public idProfissional: number | string;
}
