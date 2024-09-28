// 单文字背景色
export const tranColor = (name: string) => {
  let res = parseInt(name.charCodeAt(0) + '', 10).toString(16);
  if (res.length === 2) {
    const randomNumberString = Math.floor(Math.random() * 100)
      .toString()
      .padStart(2, '0');
    res += randomNumberString;
  }
  return '#' + res.slice(1, 4);
};

import { createDiscreteApi } from 'naive-ui';
const { notification } = createDiscreteApi(['notification']);

type NotificationType = keyof typeof notification;

export const tips = (type: NotificationType, msg: string) => {
  notification[type]({
    content: msg,
    duration: 2500,
    keepAliveOnHover: true
  });
};
