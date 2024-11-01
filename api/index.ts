import request from './request';

export const userInfo = () => request({ url: 'user/userInfo', method: 'get' });

export const eqUserBasics = (data: any) => request({ url: 'user/eqUserBasics', method: 'get', data });

export const eqUserMail = (mail: string) => request({ url: 'user/eqUserMail/' + mail, method: 'get' });

export const login = (data: any) => request({ url: 'user/login', method: 'post', data });

export const welcome = (data: any) => request({ url: 'user/welcome', method: 'post', data });

export const welcomeCode = (mail: string) => request({ url: 'user/welcomeCode/' + mail, method: 'get' });

export const logout = () => request({ url: 'user/logout', method: 'get' });

export const upBasic = (name: string) => request({ url: 'user/upBasic/' + name, method: 'put' });

export const adPassWord = (pwd: string) => request({ url: 'user/adPassWord/' + pwd, method: 'post' });

export const hasPwd = () => request({ url: 'user/hasPwd', method: 'get' });

export const eqUserCount = () => request({ url: 'user/eqUserCount', method: 'get' });

export const notes = () => request({ url: 'sys/notes', method: 'get' });

export const notesCounts = () => request({ url: 'sys/notesCounts', method: 'get' });
