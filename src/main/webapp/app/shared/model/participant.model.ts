import { type IUser } from '@/shared/model/user.model';
import { type IConversation } from '@/shared/model/conversation.model';
import { type IMessage } from '@/shared/model/message.model';

export interface IParticipant {
  id?: string;
  isAdmin?: boolean | null;
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  user?: IUser;
  conversation?: IConversation;
  seenMessages?: IMessage[] | null;
}

export class Participant implements IParticipant {
  constructor(
    public id?: string,
    public isAdmin?: boolean | null,
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public user?: IUser,
    public conversation?: IConversation,
    public seenMessages?: IMessage[] | null,
  ) {
    this.isAdmin = this.isAdmin ?? false;
  }
}
