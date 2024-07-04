import { type IColour } from '@/shared/model/colour.model';
import { type IProduct } from '@/shared/model/product.model';

export interface IProductCategory {
  id?: string;
  name?: string | null;
  price?: number;
  description?: string | null;
  imageUri?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  colour?: IColour;
  product?: IProduct;
}

export class ProductCategory implements IProductCategory {
  constructor(
    public id?: string,
    public name?: string | null,
    public price?: number,
    public description?: string | null,
    public imageUri?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public colour?: IColour,
    public product?: IProduct,
  ) {}
}
