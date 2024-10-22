# 下载

前往[GitHub](https://github.com/sniHao/nh-chat)或[Gitee](https://gitee.com/snihao/nh-chat)将代码clone到你的本地，然后按照以下步骤操作。

## 安装依赖

该项目的node.js版本为**v18.18.0**，npm版本为**9.8.1**，建议使用差距不那么大的版本，否则可能会出现一些问题。

```bash
npm i
```

## 启动项目

```bash
npm run dev
```

需要注意的是，项目中存在后端接口，需要你结合自己项目的后端去做修改，详细的目录结构在下节介绍。若未启动后端接口，会有服务连接失败等等弹窗，可以自行调整这些操作。未启动后端服务，可以进行体验但不会进行数据保存。详细的文档请参考**项目结构**。
## 打包项目

```bash
npm run build
```
详情参考官网：[https://nuxt.com.cn/docs/getting-started/deployment](https://nuxt.com.cn/docs/getting-started/deployment)
