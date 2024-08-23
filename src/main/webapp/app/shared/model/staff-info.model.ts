import { type IUser } from '@/shared/model/user.model';
import { type IWorkingUnit } from '@/shared/model/working-unit.model';

import { type StaffStatus } from '@/shared/model/enumerations/staff-status.model';
import { type StaffRole } from '@/shared/model/enumerations/staff-role.model';
export interface IStaffInfo {
  id?: number;
  status?: keyof typeof StaffStatus;
  role?: keyof typeof StaffRole;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  user?: IUser;
  workingUnit?: IWorkingUnit | null;
}

export class StaffInfo implements IStaffInfo {
  constructor(
    public id?: number,
    public status?: keyof typeof StaffStatus,
    public role?: keyof typeof StaffRole,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public user?: IUser,
    public workingUnit?: IWorkingUnit | null,
  ) {}
}
