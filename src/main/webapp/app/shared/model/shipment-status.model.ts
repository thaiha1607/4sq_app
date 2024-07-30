export interface IShipmentStatus {
  id?: number;
  statusCode?: string;
  description?: string | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
}

export class ShipmentStatus implements IShipmentStatus {
  constructor(
    public id?: number,
    public statusCode?: string,
    public description?: string | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
  ) {}
}
