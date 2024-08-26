import { type IOrderStatus } from '@/shared/model/order-status.model';
import { type IInternalOrder } from '@/shared/model/internal-order.model';

export interface IInternalOrderHistory {
  id?: string;
  note?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  status?: IOrderStatus;
  order?: IInternalOrder;
}

export class InternalOrderHistory implements IInternalOrderHistory {
  constructor(
    public id?: string,
    public note?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public status?: IOrderStatus,
    public order?: IInternalOrder,
  ) {}
}
