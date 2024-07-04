export interface IAddress {
  id?: string;
  line1?: string;
  line2?: string | null;
  city?: string;
  state?: string;
  country?: string;
  zipOrPostalCode?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
}

export class Address implements IAddress {
  constructor(
    public id?: string,
    public line1?: string,
    public line2?: string | null,
    public city?: string,
    public state?: string,
    public country?: string,
    public zipOrPostalCode?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
  ) {}
}
