import { type IUser } from '@/shared/model/user.model';
import { type IWorkingUnit } from '@/shared/model/working-unit.model';

export interface IUserDetails {
  id?: number;
  phone?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  user?: IUser;
  workingUnit?: IWorkingUnit | null;
}

export class UserDetails implements IUserDetails {
  constructor(
    public id?: number,
    public phone?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public user?: IUser,
    public workingUnit?: IWorkingUnit | null,
  ) {}
}
