export interface IColour {
  id?: string;
  name?: string;
  hexCode?: string;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
}

export class Colour implements IColour {
  constructor(
    public id?: string,
    public name?: string,
    public hexCode?: string,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
  ) {}
}
