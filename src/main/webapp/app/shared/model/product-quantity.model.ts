import { type IWorkingUnit } from '@/shared/model/working-unit.model';
import { type IProductCategory } from '@/shared/model/product-category.model';

export interface IProductQuantity {
  id?: string;
  qty?: number;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  workingUnit?: IWorkingUnit;
  productCategory?: IProductCategory;
}

export class ProductQuantity implements IProductQuantity {
  constructor(
    public id?: string,
    public qty?: number,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public workingUnit?: IWorkingUnit,
    public productCategory?: IProductCategory,
  ) {}
}
