import { type IUser } from '@/shared/model/user.model';
import { type IProduct } from '@/shared/model/product.model';

export interface IComment {
  id?: number;
  rating?: number | null;
  content?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  user?: IUser;
  product?: IProduct;
}

export class Comment implements IComment {
  constructor(
    public id?: number,
    public rating?: number | null,
    public content?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public user?: IUser,
    public product?: IProduct,
  ) {}
}
