import { type ITag } from '@/shared/model/tag.model';

export interface IProduct {
  id?: string;
  name?: string;
  description?: string | null;
  expectedPrice?: number;
  provider?: string | null;
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
    public expectedPrice?: number,
    public provider?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public tags?: ITag[] | null,
  ) {}
}
