import PriceRange from './price-range.model';
export default class Price {
    public id?: number | string;
    public nome?: string; 
    public faixaPrecos?: PriceRange[] = []
}
