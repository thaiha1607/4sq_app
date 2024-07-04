import { type IInvoiceStatus } from '@/shared/model/invoice-status.model';
import { type IOrder } from '@/shared/model/order.model';

import { type InvoiceType } from '@/shared/model/enumerations/invoice-type.model';
import { type PaymentMethod } from '@/shared/model/enumerations/payment-method.model';
export interface IInvoice {
  id?: string;
  totalAmount?: number;
  type?: keyof typeof InvoiceType;
  paymentMethod?: keyof typeof PaymentMethod;
  note?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  status?: IInvoiceStatus;
  order?: IOrder;
}

export class Invoice implements IInvoice {
  constructor(
    public id?: string,
    public totalAmount?: number,
    public type?: keyof typeof InvoiceType,
    public paymentMethod?: keyof typeof PaymentMethod,
    public note?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public status?: IInvoiceStatus,
    public order?: IOrder,
  ) {}
}
