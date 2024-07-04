import { type IParticipant } from '@/shared/model/participant.model';

import { type MessageType } from '@/shared/model/enumerations/message-type.model';
export interface IMessage {
  id?: string;
  type?: keyof typeof MessageType;
  content?: string | null;
  isSeen?: boolean | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  participant?: IParticipant;
}

export class Message implements IMessage {
  constructor(
    public id?: string,
    public type?: keyof typeof MessageType,
    public content?: string | null,
    public isSeen?: boolean | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public participant?: IParticipant,
  ) {
    this.isSeen = this.isSeen ?? false;
  }
}
