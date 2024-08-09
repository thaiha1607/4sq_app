import { type IOrderItem } from '@/shared/model/order-item.model';
import { type IShipment } from '@/shared/model/shipment.model';

export interface IShipmentItem {
  id?: string;
  qty?: number;
  total?: number;
  rollQty?: number;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  orderItem?: IOrderItem;
  shipment?: IShipment;
}

export class ShipmentItem implements IShipmentItem {
  constructor(
    public id?: string,
    public qty?: number,
    public total?: number,
    public rollQty?: number,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public orderItem?: IOrderItem,
    public shipment?: IShipment,
  ) {}
}
