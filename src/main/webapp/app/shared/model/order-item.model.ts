import { type IProductCategory } from '@/shared/model/product-category.model';
import { type IOrder } from '@/shared/model/order.model';

export interface IOrderItem {
  id?: string;
  orderedQty?: number;
  receivedQty?: number;
  unitPrice?: number;
  note?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  productCategory?: IProductCategory;
  order?: IOrder;
}

export class OrderItem implements IOrderItem {
  constructor(
    public id?: string,
    public orderedQty?: number,
    public receivedQty?: number,
    public unitPrice?: number,
    public note?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public productCategory?: IProductCategory,
    public order?: IOrder,
  ) {}
}
