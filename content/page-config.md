# 前端配置

下面是一个全面的示例，当然你可能不会使用所有的配置项。为了方便让你理解，这里做了详细的 demo。

引入组件

```vue
import { NhChat } from 'nh-chat'; 
import 'nh-chat/dist/assets/nh-style.css';
```

```vue
<NhChat
  inputTips="输入Ta的账号-发起聊天"
  :searchUserResult="searchUserResult"
  baseUrl="http://localhost:34560"
  :userInfo="userInfo"
  eqUserInfo=""
  socketUrl="ws://localhost:8087/socket.chat/"
  token=""
  chatRoute="/"
  :messageFlicker="true"
  :experienceMode="true"
  @searchUser="searchUser"></NhChat>
```

## 传递

| 属性             | 类型                               | 默认值                                 | 说明                                                                       |
| ---------------- | ---------------------------------- | -------------------------------------- | -------------------------------------------------------------------------- |
| inputTips        | `string`                           | `'输入Ta的账号-发起聊天'`              | 搜索框的提示文字                                                           |
| searchUserResult | `Array[{uid:xx name:xx photo:xx}]` | `[]`                                   | 搜索用户的结果集                                                           |
| baseUrl          | `string`                           | `'http://localhost:8087'`              | 接口的地址                                                                 |
| userInfo         | `{uid:xx name:xx photo:xx}`        | `{uid:-1, name: "默认", photo: "默" }` | 当前用户的基本信息(用户 uid、昵称、头像)                                   |
| eqUserInfo       | `string`                           | `''`                                   | 接口地址，获取用户集的昵称和头像。**详细看后端配置**                       |
| socketUrl        | `string`                           | `'ws://localhost:8087/socket.chat/'`   | socket 连接地址，只需传入接口，后面会默认带上 token 值                     |
| token            | `string`                           | `''`                                   | 用户的 Token 值，后面会将 token 放于请求的头部携带 **Authorization:token** |
| chatRoute        | `string`                           | `'/'`                                  | 当前消息组件所在路由 (用于查看消息的跳转)                                  |
| messageFlicker   | `boolean`                          | `true`                                 | 新消息是否开启边框闪烁提醒                                                 |
| experienceMode   | `boolean`                          | `true`                                 | 是否开启体验模式(接口请求失败时)                                           |

## 回调

| 属性       | 类型               | 说明                                                                                     |
| ---------- | ------------------ | ---------------------------------------------------------------------------------------- |
| searchUser | `(val:string)=>{}` | 搜索用户的值条件，你需要在回调中调用你自己查询用户的接口，将结果赋值给`searchUserResult` |
