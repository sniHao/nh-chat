import request from './request';

export const userInfo = () => request({ url: 'user/userInfo', method: 'get' });

export const eqUserMail = (mail: string) => request({ url: 'user/eqUserMail/' + mail, method: 'get' });

export const login = (data: any) => request({ url: 'user/login', method: 'post', data });

export const welcome = (data: any) => request({ url: 'user/welcome', method: 'post', data });

export const welcomeCode = (mail: string) => request({ url: 'user/welcomeCode/' + mail, method: 'get' });

export const eqUserCount = () => request({ url: 'user/eqUserCount', method: 'get' });

export const eqRelation = () => request({ url: 'chat/eqRelation', method: 'get' });

export const goChat = (receiveUid: number) => request({ url: 'chat/goChat/' + receiveUid, method: 'get' });
