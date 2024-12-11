/**
 * 计算时间差
 * @param lastTime 比较的时间-之前
 * @param nowTime 比较的时间-最新
 * @param type 类型 [以什么为单位返回 ] 默认*天
 * @returns 天数||分钟数。。。
 */
export const countTimeDiff = (lastTime: string, nowTime?: string, type?: number): number => {
  type = type || 3600 * 24;
  nowTime = nowTime || new Date().toString();
  return Math.ceil((Date.parse(nowTime) - Date.parse(lastTime)) / (1000 * type));
};

/**
 * 全局时间展示格式
 * @param time 日期[yyyy-MM-dd HH:mm:ss]
 * @param showAll 是否展示更全的时间
 * @returns
 */
export const reckonTime = (time: string, showAll?: boolean): string => {
  if (!time) return '-';
  time = time.toString().replace(/-/g, '/'); //转成部分苹果支持的格式
  let tsTemp = new Date().valueOf() - new Date(time).valueOf();
  if (tsTemp < 0) return '刚刚';
  let second = 1000 * 60; //1分钟内
  let min = 1000 * 60 * 60; //1小时内
  let hour = 1000 * 60 * 60 * 24; //一天内
  let day = 1000 * 60 * 60 * 24 * 7; //七天内
  let result = '';
  showAll = showAll ?? false;
  switch (true) {
    case new Date().getFullYear() != new Date(time).getFullYear():
      showAll ? (result = time) : (result = time?.split(' ')[0]);
      break;
    case tsTemp < second:
      result = '刚刚';
      break;
    case tsTemp < min:
      result = Math.ceil(tsTemp / (1000 * 60)) + '分钟前';
      break;
    case tsTemp < hour:
      result = Math.ceil(tsTemp / (1000 * 60 * 60)) + '小时前';
      break;
    case tsTemp < day:
      result = Math.ceil(tsTemp / (1000 * 60 * 60 * 24)) + '天前';
      break;
    default:
      let splitMon = time.split('/')[1] + '-' + time.split('/')[2];
      result = showAll ? splitMon : splitMon.split(' ')[0];
  }
  return result;
};

/**
 * 私信聊天时间推算
 * @param time 时间[yyyy-MM-dd HH:mm:ss]
 * @param isShowInfo 是否显示提示(如'上午') 默认显示
 * @returns 时间信息
 */
export const cutChatTime = (time: string, isShowInfo?: boolean): string => {
  time = time.replace(/-/g, '/'); //转成部分苹果支持的格式
  isShowInfo = isShowInfo ?? true;
  let tsTemp = new Date().valueOf() - new Date(time).valueOf();
  if (tsTemp < 0) return '刚刚';
  let collect;
  let nowHours = new Date(time).getHours();
  let dataDiff = new Date().getDate() - new Date(time).getDate();
  let yearDiff = new Date().getFullYear() - new Date(time).getFullYear();
  let infoArr = isShowInfo ? ['凌晨', '上午', '中午', '下午', '傍晚'] : ['', '', '', '', ''];
  let result = '',
    timeFirst = '',
    timeLast = '';
  if (dataDiff < 2 && yearDiff == 0) {
    collect = time.split(' ');
    timeFirst = dataDiff == 1 && yearDiff == 0 ? '昨天 ' : '';
    timeLast = collect[1].split(':')[0] + ':' + collect[1].split(':')[1];
  } else {
    collect = time.split('/');
    let splitCollect = collect[2].split(' ');
    let isShowYear = yearDiff == 0 ? '' : collect[0] + '-';
    timeFirst = (isShowYear + collect[1] + '-' + collect[2]).split(' ')[0] + ' ';
    timeLast = splitCollect[1].split(':')[0] + ':' + splitCollect[1].split(':')[1];
  }
  // 一天外
  switch (true) {
    case nowHours >= 0 && nowHours < 6:
      result = timeFirst + infoArr[0] + ' ' + timeLast;
      break;
    case nowHours >= 6 && nowHours < 12:
      result = timeFirst + infoArr[1] + ' ' + timeLast;
      break;
    case nowHours >= 12 && nowHours < 14:
      result = timeFirst + infoArr[2] + ' ' + timeLast;
      break;
    case nowHours >= 14 && nowHours < 18:
      result = timeFirst + infoArr[3] + ' ' + timeLast;
      break;
    case nowHours >= 18 && nowHours < 24:
      result = timeFirst + infoArr[4] + ' ' + timeLast;
      break;
  }
  return result;
};

/**
 * 时间格式化
 * @param date 时间，默认当前时间
 * @param fmt 事件格式类型 ，默认yyyy-MM-dd HH:mm:ss
 * @returns
 */
export const getTimeFormat = (date?: Date, fmt?: string): string => {
  fmt = fmt || 'yyyy-MM-dd HH:mm:ss';
  date = date || new Date();
  let o = {
    'M+': date.getMonth() + 1, //月份
    'd+': date.getDate(), //日
    'H+': date.getHours(), //小时
    'm+': date.getMinutes(), //分
    's+': date.getSeconds(), //秒
    'q+': Math.floor((date.getMonth() + 3) / 3), //季度
    S: date.getMilliseconds() //毫秒
  } as any;
  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  for (var k in o)
    if (new RegExp('(' + k + ')').test(fmt)) fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
  return fmt;
};

/**
 *  某期限内时间数据转换 如某七天内的时间数据
 * @param time 时间
 * @returns 转换后时间
 */
export const logTime = (time: string): string => {
  let tstamp = new Date().valueOf() - new Date(time).valueOf();
  let hour = 1000 * 60 * 60 * 24;
  let dayTime = time.split(' ')[1];
  let date = dayTime.split(':')[0] + ':' + dayTime.split(':')[1];
  if (tstamp < hour) return date;
  return Math.ceil(tstamp / hour) + '天前 ' + date;
};

/**
 * 获取有多少年份[默认截至1949]
 * @param actYear 截至到多少年份
 * @returns 年份数组
 */
export const getYear = (actYear?: number): Array<number> => {
  actYear = actYear || 1949;
  const date = new Date();
  if (actYear >= date.getFullYear()) return [1949];
  let yearArr = [];
  for (let i = actYear; i <= date.getFullYear(); i++) yearArr.push(i);
  return yearArr;
};

/**
 * 获取当前年份有多少月份
 * @param year 年份
 * @returns 月份数组
 */
export const getMonthToYear = (year: number): Array<number> => {
  const date = new Date();
  const nowYear = date.getFullYear();
  if (year > nowYear) return [1];
  let monthArr = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
  if (year == nowYear) {
    monthArr = [];
    for (let i = 1; i <= date.getMonth() + 1; i++) monthArr.push(i);
  }
  return monthArr;
};

/**
 * 获取当前年的月份有多少天
 * @param year 年份
 * @param month 月份
 * @returns 天数数组
 */
export const getDayToMoYe = (year: number, month: number): Array<number> => {
  const date = new Date();
  const nowYear = date.getFullYear();
  const nowMonth = date.getMonth() + 1;
  if (year > nowYear) return [1];
  let monthArr = getMonthToYear(year);
  if (month > monthArr.length) return [1];
  let dayArr = [];
  if (year == nowYear && month == nowMonth) {
    for (let i = 1; i <= date.getDate(); i++) dayArr.push(i);
    return dayArr;
  }
  for (let i = 1; i <= new Date(year, month, 0).getDate(); i++) dayArr.push(i);
  return dayArr;
};
