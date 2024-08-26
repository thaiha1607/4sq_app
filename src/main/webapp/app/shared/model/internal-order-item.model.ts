import { type IOrderItem } from '@/shared/model/order-item.model';

export interface IInternalOrderItem {
  id?: string;
  qty?: number;
  note?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  orderItem?: IOrderItem;
}

export class InternalOrderItem implements IInternalOrderItem {
  constructor(
    public id?: string,
    public qty?: number,
    public note?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public orderItem?: IOrderItem,
  ) {}
}
