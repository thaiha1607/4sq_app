import axios from 'axios';

import { type IInvoiceStatus } from '@/shared/model/invoice-status.model';

const baseApiUrl = 'api/invoice-statuses';
const baseSearchApiUrl = 'api/invoice-statuses/_search?query=';

export default class InvoiceStatusService {
  public search(query): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(`${baseSearchApiUrl}${query}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public find(statusCode: number): Promise<IInvoiceStatus> {
    return new Promise<IInvoiceStatus>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/${statusCode}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public retrieve(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(baseApiUrl)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public delete(statusCode: number): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .delete(`${baseApiUrl}/${statusCode}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public create(entity: IInvoiceStatus): Promise<IInvoiceStatus> {
    return new Promise<IInvoiceStatus>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public update(entity: IInvoiceStatus): Promise<IInvoiceStatus> {
    return new Promise<IInvoiceStatus>((resolve, reject) => {
      axios
        .put(`${baseApiUrl}/${entity.statusCode}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public partialUpdate(entity: IInvoiceStatus): Promise<IInvoiceStatus> {
    return new Promise<IInvoiceStatus>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${entity.statusCode}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
