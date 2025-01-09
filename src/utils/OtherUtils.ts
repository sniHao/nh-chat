// 单文字背景色
export const tranColor = (name: string) => {
  if (!name) return '#FF6700';
  let res = parseInt((name as string).charCodeAt(0) + '', 10).toString(16);
  if (res.length === 2) res = res.repeat(2);
  return '#' + res.slice(1, 4);
};

// 通知弹窗
import { createDiscreteApi } from 'naive-ui';
import axios from 'axios';

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

// 12位随机数
export const randomNumber = () => {
  const randomNumberString = Math.floor(Math.random() * 100000000000)
    .toString()
    .padEnd(12, '0');
  return randomNumberString;
};

// 复制文本
export const copyText = (text: string): boolean => {
  const textArea = document.createElement('textarea');
  textArea.value = text;
  document.body.appendChild(textArea);
  textArea.select();
  textArea.setSelectionRange(0, 99999);
  let success = false;
  try {
    success = document.execCommand('copy');
  } catch (err) {
    success = false;
  }
  document.body.removeChild(textArea);
  return success;
};

// 复制图片
export const copyImage = (imageUrl: string): Promise<Boolean> => {
  return new Promise((reactive) => {
    const img = new Image();
    img.crossOrigin = 'Anonymous';
    img.src = imageUrl;
    img.onload = async () => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d') as CanvasRenderingContext2D;
      canvas.width = img.width;
      canvas.height = img.height;
      ctx.drawImage(img, 0, 0);
      canvas.toBlob(async (blob) => {
        if (blob) {
          const item = new ClipboardItem({ 'image/png': blob });
          try {
            await navigator.clipboard.write([item]);
            reactive(true);
          } catch (err) {
            reactive(false);
          }
        }
      }, 'image/png');
    };
    img.onerror = () => {
      reactive(false);
    };
  });
};

// 休眠
export const sleep = (time: number) => {
  return new Promise((resolve) => setTimeout(resolve, time));
};

// 为用户添加昵称和头像属性
export const setUser = (data: Array<Relation>, url: string) => {
  return new Promise((resolve) => {
    if (!url) resolve(defaultUser(data));
    axios
      .post(
        url,
        data.map((item) => item.relationUid),
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      )
      .then((response) => {
        if (!response.data.data || response.data.data.length == 0) return resolve(defaultUser(data));
        data.forEach((item) => {
          let user = response.data.data.filter((ite: userInfo) => ite.uid === item.relationUid)[0];
          if (user) {
            item['name'] = user.name;
            item['photo'] = user.photo;
          }
        });
        resolve(data);
      })
      .catch((error) => {
        tips('error', '服务器异常，获取头像失败');
      });
  });
};

// 默认头像昵称
const defaultUser = (data: Array<Relation>) => {
  data.forEach((item) => {
    item['name'] = '默认昵称';
    item['photo'] = '默';
  });
  return data;
};

// 头像设置
export const computePhoto = (photo: string) => {
  if (!photo) return '默';
  if (photo.startsWith('http')) return `<img src='${photo}' />`;
  return photo;
};

// 将dom颜色透明化
export const setFilterColor = (color: string, newOpacity: number = 0.8): string => {
  let dom = document.createElement('div');
  dom.style.backgroundColor = color;
  document.body.appendChild(dom);
  const computedStyle = window.getComputedStyle(dom);
  const bgColor = computedStyle.backgroundColor;
  const rgbValues = bgColor.match(/\d+/g);
  if (!rgbValues) return bgColor;
  let r = parseInt(rgbValues[0]);
  let g = parseInt(rgbValues[1]);
  let b = parseInt(rgbValues[2]);
  document.body.removeChild(dom);
  return `rgba(${r}, ${g}, ${b}, ${newOpacity})`;
};

// 获取引用视角
export const getQuoteView = (message: string): Array<string> => {
  if (!message) return ['', ''];
  let index = message.indexOf('：');
  if (index !== -1) {
    let firstPart = message.slice(0, index + 1);
    let secondPart = message.slice(index + 1);
    return [firstPart, secondPart];
  }
  return ['', message];
};