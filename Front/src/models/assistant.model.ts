export default class Assistant {
    public id?: number | string;
    public nome!: string;
    public rg!: string;
    public cpf!: string;
    public endereco!: string;
    public cep!: string;
    public telefone!: string;
    public nomeDoCurso!: string;
    public anoConclusao!: string;
    public faculdade!: string;
    public username!: string;
    public password?: string;
    public confirmPassword?: string;
    public imagerg: string | null = null;
    public imagecpf: string | null = null;
}
