import axios from 'axios';

import { type IOrderStatus } from '@/shared/model/order-status.model';

const baseApiUrl = 'api/order-statuses';
const baseSearchApiUrl = 'api/order-statuses/_search?query=';

export default class OrderStatusService {
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

  public find(statusCode: number): Promise<IOrderStatus> {
    return new Promise<IOrderStatus>((resolve, reject) => {
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

  public create(entity: IOrderStatus): Promise<IOrderStatus> {
    return new Promise<IOrderStatus>((resolve, reject) => {
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

  public update(entity: IOrderStatus): Promise<IOrderStatus> {
    return new Promise<IOrderStatus>((resolve, reject) => {
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

  public partialUpdate(entity: IOrderStatus): Promise<IOrderStatus> {
    return new Promise<IOrderStatus>((resolve, reject) => {
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
