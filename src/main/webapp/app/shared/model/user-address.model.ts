import { type IUser } from '@/shared/model/user.model';
import { type IAddress } from '@/shared/model/address.model';

import { type AddressType } from '@/shared/model/enumerations/address-type.model';
export interface IUserAddress {
  id?: string;
  type?: keyof typeof AddressType;
  friendlyName?: string | null;
  isDefault?: boolean | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  user?: IUser;
  address?: IAddress;
}

export class UserAddress implements IUserAddress {
  constructor(
    public id?: string,
    public type?: keyof typeof AddressType,
    public friendlyName?: string | null,
    public isDefault?: boolean | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public user?: IUser,
    public address?: IAddress,
  ) {
    this.isDefault = this.isDefault ?? false;
  }
}
