import { type IUser } from '@/shared/model/user.model';
import { type IShipment } from '@/shared/model/shipment.model';

import { type AssignmentStatus } from '@/shared/model/enumerations/assignment-status.model';
export interface IShipmentAssignment {
  id?: string;
  status?: keyof typeof AssignmentStatus;
  note?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  user?: IUser;
  shipment?: IShipment;
}

export class ShipmentAssignment implements IShipmentAssignment {
  constructor(
    public id?: string,
    public status?: keyof typeof AssignmentStatus,
    public note?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public user?: IUser,
    public shipment?: IShipment,
  ) {}
}
