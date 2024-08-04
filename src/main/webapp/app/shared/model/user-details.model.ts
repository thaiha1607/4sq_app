import { type IUser } from '@/shared/model/user.model';

export interface IUserDetails {
  id?: number;
  phone?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  user?: IUser;
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
  ) {}
}
