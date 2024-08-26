import { type IOrderStatus } from '@/shared/model/order-status.model';
import { type IOrder } from '@/shared/model/order.model';

import { type OrderType } from '@/shared/model/enumerations/order-type.model';
export interface IInternalOrder {
  id?: string;
  type?: keyof typeof OrderType;
  note?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  status?: IOrderStatus;
  rootOrder?: IOrder;
}

export class InternalOrder implements IInternalOrder {
  constructor(
    public id?: string,
    public type?: keyof typeof OrderType,
    public note?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public status?: IOrderStatus,
    public rootOrder?: IOrder,
  ) {}
}
