import { type IShipmentStatus } from '@/shared/model/shipment-status.model';
import { type IOrder } from '@/shared/model/order.model';
import { type IInvoice } from '@/shared/model/invoice.model';

import { type ShipmentType } from '@/shared/model/enumerations/shipment-type.model';
export interface IShipment {
  id?: string;
  type?: keyof typeof ShipmentType;
  shipmentDate?: Date;
  note?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  status?: IShipmentStatus;
  order?: IOrder;
  invoice?: IInvoice;
}

export class Shipment implements IShipment {
  constructor(
    public id?: string,
    public type?: keyof typeof ShipmentType,
    public shipmentDate?: Date,
    public note?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public status?: IShipmentStatus,
    public order?: IOrder,
    public invoice?: IInvoice,
  ) {}
}
