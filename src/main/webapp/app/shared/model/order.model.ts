import { type IUser } from '@/shared/model/user.model';
import { type IOrderStatus } from '@/shared/model/order-status.model';
import { type IAddress } from '@/shared/model/address.model';

import { type OrderType } from '@/shared/model/enumerations/order-type.model';
export interface IOrder {
  id?: string;
  type?: keyof typeof OrderType;
  priority?: number | null;
  note?: string | null;
  otherInfo?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  customer?: IUser;
  status?: IOrderStatus;
  address?: IAddress | null;
  rootOrder?: IOrder | null;
}

export class Order implements IOrder {
  constructor(
    public id?: string,
    public type?: keyof typeof OrderType,
    public priority?: number | null,
    public note?: string | null,
    public otherInfo?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public customer?: IUser,
    public status?: IOrderStatus,
    public address?: IAddress | null,
    public rootOrder?: IOrder | null,
  ) {}
}
