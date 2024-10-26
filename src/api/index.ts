import request from './request';

export const userInfo = () => request({ url: 'user/userInfo', method: 'get' });

export const eqUserBasics = (uid: number) => request({ url: 'user/eqUserBasics/' + uid, method: 'get' });

export const eqUserMail = (mail: string) => request({ url: 'user/eqUserMail/' + mail, method: 'get' });

export const login = (data: any) => request({ url: 'user/login', method: 'post', data });

export const welcome = (data: any) => request({ url: 'user/welcome', method: 'post', data });

export const welcomeCode = (mail: string) => request({ url: 'user/welcomeCode/' + mail, method: 'get' });

export const logout = () => request({ url: 'user/logout', method: 'get' });

export const upBasic = (name: string) => request({ url: 'user/upBasic/' + name, method: 'put' });

export const adPassWord = (pwd: string) => request({ url: 'user/adPassWord/' + pwd, method: 'post' });

export const eqUserCount = () => request({ url: 'user/eqUserCount', method: 'get' });

export const eqRelation = () => request({ url: 'chat/eqRelation', method: 'get' });

export const goChat = (receiveUid: number) => request({ url: 'chat/goChat/' + receiveUid, method: 'get' });

export const eqChat = (receiveUid: number, page: number) => request({ url: 'chat/eqChat/' + receiveUid + '/' + page, method: 'get' });

export const sendMessage = (data: { receiveUid: number; message: string; type: number }) => request({ url: 'chat/sendMessage', method: 'post', data });

export const sendMessageImage = (data: FormData, receiveUid: number) => request({ url: 'chat/sendMessageImage/' + receiveUid, method: 'post', data });

export const topChat = (relationId: number, state: number) => request({ url: 'chat/topChat/' + relationId + '/' + state, method: 'put' });

export const delChat = (relationId: number) => request({ url: 'chat/delChat/' + relationId, method: 'put' });

export const revocationMessage = (mid: number) => request({ url: 'chat/revocationMessage/' + mid, method: 'put' });

export const delMessage = (data: number[]) => request({ url: 'chat/delMessage', method: 'post', data });

export const notes = () => request({ url: 'sys/notes', method: 'get' });

export const notesCounts = () => request({ url: 'sys/notesCounts', method: 'get' });
