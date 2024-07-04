export interface IInvoiceStatus {
  id?: number;
  description?: string;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
}

export class InvoiceStatus implements IInvoiceStatus {
  constructor(
    public id?: number,
    public description?: string,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
  ) {}
}
