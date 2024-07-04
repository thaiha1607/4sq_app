import { type ITag } from '@/shared/model/tag.model';

export interface IProduct {
  id?: string;
  name?: string;
  description?: string | null;
  provider?: string | null;
  otherInfo?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  tags?: ITag[] | null;
}

export class Product implements IProduct {
  constructor(
    public id?: string,
    public name?: string,
    public description?: string | null,
    public provider?: string | null,
    public otherInfo?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public tags?: ITag[] | null,
  ) {}
}
