export interface Cep {
  erro?: boolean,
  cep: string,
  logradouro: string,
  complemento?: string,
  bairro: string,
  localidade: string,
  uf: string,
  unidade?: string,
  ibge: string | number,
  gia: string | number,
}