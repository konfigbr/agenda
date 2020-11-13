import Price from './price.model';
export default class Specialty {
    public id?: string | number ;
    public nome!: string;
    public tipoAceite!: string;
    public tabelaPreco?: Price
    public exibirCadastroProfissional: boolean = true;
}
