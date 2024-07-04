import { type IProduct } from '@/shared/model/product.model';

export interface ITag {
  id?: string;
  name?: string;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  products?: IProduct[] | null;
}

export class Tag implements ITag {
  constructor(
    public id?: string,
    public name?: string,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public products?: IProduct[] | null,
  ) {}
}
