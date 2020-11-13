export default class UserRegister {
    public nome: string = "";
    public username: string = "";
    public password: string = "";
    public confirmPassword?: string = "";
    public especialidade: any = {};
    public tipoDocumento: any = {};
    public documento: string = "";
    public logradouro!: string;
    public municipio!: string;
    public crm!: string;
    public numero!: string;
    public bairro!: string;
    public cidade!: string;
    public uf!: string;
    public endereco!: string;
    public orgao!: string;
    public cep!: string;
}