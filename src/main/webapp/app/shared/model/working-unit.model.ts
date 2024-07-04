import { type IAddress } from '@/shared/model/address.model';

import { type WorkingUnitType } from '@/shared/model/enumerations/working-unit-type.model';
export interface IWorkingUnit {
  id?: string;
  name?: string;
  type?: keyof typeof WorkingUnitType;
  imageUri?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  address?: IAddress | null;
}

export class WorkingUnit implements IWorkingUnit {
  constructor(
    public id?: string,
    public name?: string,
    public type?: keyof typeof WorkingUnitType,
    public imageUri?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public address?: IAddress | null,
  ) {}
}
