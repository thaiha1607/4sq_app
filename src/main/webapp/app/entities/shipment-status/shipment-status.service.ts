import axios from 'axios';

import { type IShipmentStatus } from '@/shared/model/shipment-status.model';

const baseApiUrl = 'api/shipment-statuses';
const baseSearchApiUrl = 'api/shipment-statuses/_search?query=';

export default class ShipmentStatusService {
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

  public find(statusCode: number): Promise<IShipmentStatus> {
    return new Promise<IShipmentStatus>((resolve, reject) => {
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

  public create(entity: IShipmentStatus): Promise<IShipmentStatus> {
    return new Promise<IShipmentStatus>((resolve, reject) => {
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

  public update(entity: IShipmentStatus): Promise<IShipmentStatus> {
    return new Promise<IShipmentStatus>((resolve, reject) => {
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

  public partialUpdate(entity: IShipmentStatus): Promise<IShipmentStatus> {
    return new Promise<IShipmentStatus>((resolve, reject) => {
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
