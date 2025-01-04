import {getTimeFormat} from './TimeUtil';

// ===================================静态数据【用于体验时】===================================//
//未登录静态数据
export const eqChatDataStatic = () => {
    let data = [];
    const initMessage = ['欢迎体验nh-chat👋。需要提醒的是，当前为体验模式，数据不会被保存', '你可以尝试向我发送消息哟，体验不同的功能。'];
    for (let i = 0; i < 2; i++) {
        data.push(addStaticDataCom(i, new Date(new Date().getTime() - 1000 * 60 * 50 + 1000 * 60 * 11 * i), initMessage[i], 0));
    }
    return data;
};

// 添加静态消息公共
export const addStaticDataCom = (id: number, date: Date, message: string, type: number) => {
    return {
        date: getTimeFormat(date),
        id: id,
        message: message,
        receiveState: 1,
        receiveUid: -1,
        sendState: 1,
        sendUid: undefined,
        type: type,
        tab: false,
        state: 1,
        check: false,
        action: 0,
        quoteId: 0,
        quoteType: 0,
        quoteMessage: ''
    };
};

import {createDiscreteApi} from 'naive-ui';

const {dialog} = createDiscreteApi(['dialog']);
// 体验模式提示
export const welcome = () => {
    let tips = localStorage.getItem('first_tips');
    if (!tips || tips == 'true') {
        dialog.warning({
            title: 'Hi~👋',
            content: '您当前为体验模式，数据不会保留。建议您登录后与好友开启实时聊天哦🎉',
            positiveText: '别提醒我了',
            onPositiveClick: () => {
                localStorage.setItem('first_tips', 'false');
            },
            negativeText: '知道啦'
        });
    }
};

// 静态用户通讯录
export const staticUserData = (): Relation[] => {
    let data = [];
    const photo = ['体', 'https://p26-passport.byteacctimg.com/img/user-avatar/8399303d871443a1971d579c7a8875d5~40x40.awebp'];
    for (let i = 0; i < 2; i++) {
        data.push({
            id: i,
            uid: -1,
            relationUid: 2,
            lastMessage: '你可以尝试向我发送消息哟，体验不同的功能。',
            notRead: 1,
            top: i,
            lastMessageDate: getTimeFormat(new Date(new Date().getTime() - 1000 * 60 * i)),
            name: '体验官H-' + (i + 1),
            photo: photo[i],
            wsState: i
        });
    }
    return data;
};


// 过滤静态数据
export const filterExperienceData = (data: any, record: { search: string, type: number, date: number }) => {
    let newData = [];
    for (let i = 0; i < data.length; i++) {
        if (record.search && !data[i].message.includes(record.search)) continue;
        if (record.type === 1 && data[i].type !== 0) continue;
        if (record.type === 2 && data[i].type !== 1) continue;
        if (record.date && !isSameDay(record.date, data[i].date)) continue;
        newData.push(data[i]);
    }
    return newData;
};

// 是否当天
function isSameDay(timestamp: number, dateString: string) {
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const formattedDate = `${year}-${month}-${day}`;
    return formattedDate === dateString.split(" ")[0];
}
