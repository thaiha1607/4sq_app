import { type IOrderStatus } from '@/shared/model/order-status.model';
import { type IOrder } from '@/shared/model/order.model';

export interface IOrderHistory {
  id?: string;
  comments?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  status?: IOrderStatus;
  order?: IOrder;
}

export class OrderHistory implements IOrderHistory {
  constructor(
    public id?: string,
    public comments?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public status?: IOrderStatus,
    public order?: IOrder,
  ) {}
}
