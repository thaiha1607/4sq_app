import { type IProduct } from '@/shared/model/product.model';

export interface IProductImage {
  id?: string;
  imageUri?: string;
  altText?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  product?: IProduct;
}

export class ProductImage implements IProductImage {
  constructor(
    public id?: string,
    public imageUri?: string,
    public altText?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public product?: IProduct,
  ) {}
}
