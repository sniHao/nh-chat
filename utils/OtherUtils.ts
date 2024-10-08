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

// 通知弹窗
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

// 邮箱校验
export const isEmail = (email: string) => {
  const reg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  return reg.test(email);
};

// 字符长度截取
export const truncate = (str: string) => {
  if (str == null) return null;
  if (str.length > 30) return str.substring(0, 30) + '...';
  return str;
};
