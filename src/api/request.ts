// 对axios进行二次封装
import axios from "axios";
import { createDiscreteApi } from "naive-ui";
const { notification } = createDiscreteApi(["notification"]);
const { loadingBar } = createDiscreteApi(["loadingBar"]);
import { sleep } from "@/utils/OtherUtils";

const request = async (objet: any) => {
  await sleep(1);
  objet.timeout = 20000;
  objet.baseURL = sessionStorage.getItem("baseUrl");
  const of = axios.create(objet);
  const errorTips = [
    ["访问过于频繁，请稍候再试", "别搞别搞别搞😟"],
    ["网络服务出错啦", "开发被外星人抓走了👾"],
  ];
  // 请求拦截器
  of.interceptors.request.use(
    (config) => {
      const store = localStorage.getItem("nh");
      if (store && JSON.parse(store).token) {
        config.headers.Authorization = JSON.parse(store).token;
      } else {
        config.headers.Authorization = "null";
      }
      loadingBar.start();
      return config;
    },
    (error) => {
      notification["error"]({
        content: errorTips[1][0],
        meta: errorTips[1][1],
        duration: 2500,
        keepAliveOnHover: true,
      });
      loadingBar.error();
      setTimeout(() => loadingBar.finish());
      return Promise.reject(new Error(errorTips[1][0]));
    }
  );
  // 响应拦截器
  of.interceptors.response.use(
    (response) => {
      loadingBar.finish();
      return response.data;
    },
    (error) => {
      loadingBar.error();
      setTimeout(() => loadingBar.finish());
      const errorMessage =
        error.response && error.response.status === 500
          ? errorTips[0]
          : errorTips[1];
      notification["error"]({
        content: errorMessage[0],
        meta: errorMessage[1],
        duration: 2500,
        keepAliveOnHover: true,
      });
      return Promise.reject(error);
    }
  );
  return of;
};

const requests = async (config: any) => {
  const axiosInstance = await request(config);
  return axiosInstance(config);
};

export default requests;
