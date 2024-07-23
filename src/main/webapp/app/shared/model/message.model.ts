import { type IParticipant } from '@/shared/model/participant.model';

import { type MessageType } from '@/shared/model/enumerations/message-type.model';
export interface IMessage {
  id?: string;
  type?: keyof typeof MessageType;
  content?: string;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  participant?: IParticipant | null;
  seenParticipants?: IParticipant[] | null;
}

export class Message implements IMessage {
  constructor(
    public id?: string,
    public type?: keyof typeof MessageType,
    public content?: string,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public participant?: IParticipant | null,
    public seenParticipants?: IParticipant[] | null,
  ) {}
}
