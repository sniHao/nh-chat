import request from './request';

export const eqUserMail = (mail: string) => request({ url: 'user/eqUserMail/' + mail, method: 'get' });

export const eqRelation = () => request({ url: 'chat/eqRelation', method: 'get' });
