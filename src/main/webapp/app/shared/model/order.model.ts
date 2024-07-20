import { type IUser } from '@/shared/model/user.model';
import { type IOrderStatus } from '@/shared/model/order-status.model';
import { type IAddress } from '@/shared/model/address.model';

import { type OrderType } from '@/shared/model/enumerations/order-type.model';
export interface IOrder {
  id?: string;
  type?: keyof typeof OrderType;
  priority?: number | null;
  isInternal?: boolean | null;
  note?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  customer?: IUser;
  status?: IOrderStatus;
  address?: IAddress | null;
  parentOrder?: IOrder | null;
}

export class Order implements IOrder {
  constructor(
    public id?: string,
    public type?: keyof typeof OrderType,
    public priority?: number | null,
    public isInternal?: boolean | null,
    public note?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public customer?: IUser,
    public status?: IOrderStatus,
    public address?: IAddress | null,
    public parentOrder?: IOrder | null,
  ) {
    this.isInternal = this.isInternal ?? false;
  }
}
