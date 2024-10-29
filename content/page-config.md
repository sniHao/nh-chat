# 前端配置

下面是一个全面的示例，当然你可能不会使用所有的配置项。为了方便让你理解，这里做了详细的 demo。

```vue
<NhChat inputTips="输入Ta的账号-发起聊天"
 :searchUserResult="searchUserResult"
  baseUrl="http://localhost:34560" 
  @searchUser="searchUser"></NhChat>
```

**传递**

| 属性             | 类型                               | 默认值                    | 说明             |
| ---------------- | ---------------------------------- | ------------------------- | ---------------- |
| inputTips        | `string`                           | `'输入Ta的账号-发起聊天'` | 搜索框的提示文字 |
| searchUserResult | `Array[{uid:xx name:xx photo:xx}]` | `[]`                      | 搜索用户的结果集 |
| baseUrl          | `string`                           | `http://localhost:8087`   | 接口的地址       |

**回调**

| 属性       | 类型               | 说明                                                                                     |
| ---------- | ------------------ | ---------------------------------------------------------------------------------------- |
| searchUser | `(val:string)=>{}` | 搜索用户的值条件，你需要在回调中调用你自己查询用户的接口，将结果赋值给`searchUserResult` |
