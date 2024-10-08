// 对axios进行二次封装
import axios from 'axios';
import { createDiscreteApi } from 'naive-ui';
const { notification } = createDiscreteApi(['notification']);
const { loadingBar } = createDiscreteApi(['loadingBar']);
//1. 创建axios对象
const requests = axios.create({
  baseURL: 'http://localhost:8087',
  //   baseURL: 'https://www.notes-something.fun/notes/',
  timeout: 20000
});

const errorTips = [
  ['访问过于频繁，请稍候再试', '别搞别搞别搞😟'],
  ['网络服务出错啦', '开发被外星人抓走了👾']
];
//2. 请求拦截器
requests.interceptors.request.use(
  (config) => {
    const store = sessionStorage.getItem('nh');
    if (store && JSON.parse(store).token) {
      config.headers.Authorization = JSON.parse(store).token;
    } else {
      config.headers.Authorization = 'null';
    }
    loadingBar.start();
    return config;
  },
  (error) => {
    notification['error']({
      content: errorTips[1][0],
      meta: errorTips[1][1],
      duration: 2500,
      keepAliveOnHover: true
    });
    loadingBar.error();
    setTimeout(() => loadingBar.finish());
    return Promise.reject(new Error(errorTips[1][0]));
  }
);

//3. 响应拦截器
requests.interceptors.response.use(
  (response) => {
    loadingBar.finish();
    return response.data;
  },
  (error) => {
    loadingBar.error();
    setTimeout(() => loadingBar.finish());
    const errorMessage = error.request.status === 500 ? errorTips[0] : errorTips[1];
    notification['error']({
      content: errorMessage[0],
      meta: errorMessage[1],
      duration: 2500,
      keepAliveOnHover: true
    });
    return error.request;
  }
);

export default requests;
