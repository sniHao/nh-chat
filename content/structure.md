# 项目结构

我将尽可能详细的介绍一下项目的结构，方便您更快的入手。

## 总结构
这是项目的完整结构图，大体的作用在图中已做标识。当然你并不需要全部的文件，我会在下面主要介绍你所需要的文件。在此之前，可以了解一下项目的整体结构。

![my image](/structure.png)

## 文件详细介绍
我相信你看完结构会觉得非常简单，是的没错，这就是Nuxt.js项目的结构，如果你有使用过它构建的项目，那么就太好了，因为你会非常轻松的对项目进行修改调整。当然，如果你没用使用过，那么也没关系，我会尽量详细的介绍。🥳

**准备好了吗，开始咯！**

- **api**

这个文件夹顾名思义是存放后端接口的，它的结构如下：

```
├─ api
│  ├─ index.ts    ！-这里面会统一管理所有的接口，我个人认为很漂亮和方便。
│  └─ request.ts  -主要用于封装和配置axios，你可以不那么需要。
```

- **assets**

这个文件夹存放css样式，它的结构如下：
```
├─ assets\css
│  ├─ constant.scss  -主要是样式和字体大小。
│  ├─ index.scss     -全局公共的一些样式。
│  └─ nh.scss        -作者个人的样式库[我习惯用自己的这个]。
```
- **components**

这个文件夹存放的是所有的组件模块，它的结构如下：

```
├─ components
│  ├─ chat               ！-聊天组件文件，里面存放着两个模块，对你来说很重要。
│     ├─ Frame.vue       -右侧聊天框，主要进行聊天消息的展示和发送。
│     └─ Main.vue        -左侧通讯录，主要发现聊天和好友搜索。
│  ├─ index          
│     └─ Nav.vue         -网站的Nav，对你并不重要。
│  ├─ login          
│     ├─ Go.vue          -登录注册的事件。
│     └─ Main.vue        -大体样式框。
│  ├─ of                 -封装的公共组件
│     ├─ EmoJi.vue       -表情包。
│     ├─ Loader.vue      -加载动画。
│     ├─ RightButton.vue -右键操作封装。
│     └─ Svg.vue         -单色图标封装。
│  ├─ svg          
│     └─ NullChat.vue    -多色的图标。
```

- **composables**

这个文件夹目前主要作用字体管理，它的结构如下：

```
├─ composables
│  └─ store.ts  -状态持久化管理。
```

- **content**

这个文件夹目前主要存放markdown文档，对你来说并不重要，这里就不做介绍。

- **pages**

这个文件夹目前主要存放页面组件，用于页面路由跳转，它的结构如下：
```
├─ pages
│  ├─ docs           -md不同文档路由跳转，并不重要。
│     ├─ xxx.vue     
│     └─ xxx.vue       
│  ├─ docs.vue       -md文档主入口，并不重要。
│  ├─ index.vue      -首页入口。
│  └─ try.vue        -进入聊天的页面路由。
```

- **plugins**
这个文件夹为配置文件夹，目前只针对pinia进行了配置，它的结构如下：

```
├─ plugins
│  └─ pinia.ts  -pinia的配置。
```

- **public**

这个文件夹存放静态资源，如图片和logo等等，这里就不做介绍。

- **utils**

这个文件夹放了我个人封装的一些方法，对你可能不重要，但是我推荐你可以看看TimeUtil.ts和WebSocketService.ts。

```
├─ utils
│  ├─ xxx.ts 
│  ├─ TimeUtil.ts           -我对时间的封装，聊天会用到它。      
│  └─ WebSocketService.ts   ！-对WebSocket的封装，很重要。
```  

## 最后唠叨

上面你所需要的就是注意 **"!"** 的文件或者文件夹，你可以将它们直接复制到你的项目中.如果可以，我建议你将样式和工具都复制，因为它们缺失，可能会让项目变得很怪异。

## 需要帮助
如果你在操作过程中遇到问题，可以随时联系我，我会尽我所能提供帮助。
若发现bug也可以联系我，我会尽快修复。
- 邮箱：<snihao6@qq.com>
- GitHub：[GitHub](https://github.com/sniHao/nh-chat/issues)
- Gitee：[Gitee](https://gitee.com/snihao/nh-chat/issues)