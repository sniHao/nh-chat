// 单文字背景色
export const tranColor = (name: string) => {
  let res = parseInt(name.charCodeAt(0) + "", 10).toString(16);
  if (res.length === 2) res = res.repeat(2);
  return "#" + res.slice(1, 4);
};

// 通知弹窗
import { createDiscreteApi } from "naive-ui";
const { notification } = createDiscreteApi(["notification"]);

type NotificationType = keyof typeof notification;

export const tips = (type: NotificationType, msg: string) => {
  notification[type]({
    content: msg,
    duration: 2500,
    keepAliveOnHover: true,
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
  if (str.length > 30) return str.substring(0, 30) + "...";
  return str;
};

// 12位随机数
export const randomNumber = () => {
  const randomNumberString = Math.floor(Math.random() * 100000000000)
    .toString()
    .padEnd(12, "0");
  return randomNumberString;
};

// 复制文本
export const copyText = (text: string): boolean => {
  const textArea = document.createElement("textarea");
  textArea.value = text;
  document.body.appendChild(textArea);
  textArea.select();
  textArea.setSelectionRange(0, 99999);
  let success = false;
  try {
    success = document.execCommand("copy");
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
    img.crossOrigin = "Anonymous";
    img.src = imageUrl;
    img.onload = async () => {
      const canvas = document.createElement("canvas");
      const ctx = canvas.getContext("2d") as CanvasRenderingContext2D;
      canvas.width = img.width;
      canvas.height = img.height;
      ctx.drawImage(img, 0, 0);
      canvas.toBlob(async (blob) => {
        if (blob) {
          const item = new ClipboardItem({ "image/png": blob });
          try {
            await navigator.clipboard.write([item]);
            reactive(true);
          } catch (err) {
            reactive(false);
          }
        }
      }, "image/png");
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
