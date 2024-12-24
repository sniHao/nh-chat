import request from './request';

export const eqRelation = () => request({url: 'chat/eqRelation', method: 'get'});

export const goChat = (receiveUid: number) => request({url: 'chat/goChat/' + receiveUid, method: 'get'});

export const eqChat = (receiveUid: number, page: number) => request({
    url: 'chat/eqChat/' + receiveUid + '/' + page,
    method: 'get'
});

export const sendMessage = (data: {
    receiveUid: number;
    message: string;
    type: number,
    action: number,
    quoteId: number
}) => request({url: 'chat/sendMessage', method: 'post', data});

export const sendMessageImage = (data: FormData) => request({
    url: 'chat/sendMessageImage',
    method: 'post',
    data
});

export const topChat = (relationId: number, state: number) => request({
    url: 'chat/topChat/' + relationId + '/' + state,
    method: 'put'
});

export const delChat = (relationId: number) => request({url: 'chat/delChat/' + relationId, method: 'put'});

export const revocationMessage = (mid: number) => request({url: 'chat/revocationMessage/' + mid, method: 'put'});

export const delMessage = (data: number[]) => request({url: 'chat/delMessage', method: 'post', data});

export const clearNotRead = (receiveUid: number) => request({url: 'chat/clearNotRead/' + receiveUid, method: 'put'});

