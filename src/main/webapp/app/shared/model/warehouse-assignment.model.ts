import { type IUser } from '@/shared/model/user.model';
import { type IWorkingUnit } from '@/shared/model/working-unit.model';
import { type IInternalOrder } from '@/shared/model/internal-order.model';

import { type AssignmentStatus } from '@/shared/model/enumerations/assignment-status.model';
export interface IWarehouseAssignment {
  id?: string;
  status?: keyof typeof AssignmentStatus;
  note?: string | null;
  otherInfo?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  user?: IUser | null;
  sourceWorkingUnit?: IWorkingUnit;
  targetWorkingUnit?: IWorkingUnit | null;
  internalOrder?: IInternalOrder;
}

export class WarehouseAssignment implements IWarehouseAssignment {
  constructor(
    public id?: string,
    public status?: keyof typeof AssignmentStatus,
    public note?: string | null,
    public otherInfo?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public user?: IUser | null,
    public sourceWorkingUnit?: IWorkingUnit,
    public targetWorkingUnit?: IWorkingUnit | null,
    public internalOrder?: IInternalOrder,
  ) {}
}
