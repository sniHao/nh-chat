# 后端配置

首先请确认你项目中已经引入了 SDK 包，如果没有，请先引入。

**请根据以下步骤进行操作，如果对你不是那么需要的我会进行提醒，你可以跳过。请注意后面的'\*' 则表示必须这样做**

## 启动类上添加包扫描\*

```java
@ComponentScans({
        @ComponentScan(basePackages = "nh.chat"),
        ...
})

'...'是需要把你自己的包也进行配置扫描
```

## 配置文件 application.yml

在配置文件中添加以下配置。

**memory-address：** 聊天中图片的存储地址，默认是`D:/nh-chat/images`

**website-address：** 聊天中图片的显示地址，默认是`D:/nh-chat/images/`

```yaml
nh-chat:
  memory-address: xxxx
  website-address: xxxx
```

## 过滤器

接口中存在权限问题，如果你不进行配置，则表示使用的接口都没有权限认证，建议进行配置。

在项目中新建 filter 文件夹，里面有 3 个文件`ApiInterceptor`、`NhConfig`、`SocketInterceptor`。
**ApiInterceptor：** 接口过滤器
**SocketInterceptor：** Socket 握手过滤器
**NhConfig：** 过滤器的配置类

**注：配置完后，请注意让包被扫描到，否则会无效。**

具体的内容可以参考如下，具体需要根据你的权限校验规则进行调整。

- **ApiInterceptor：**

```java
@Component
public class ApiInterceptor implements HandlerInterceptor {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 进行token校验，通过则将用户ID设置到请求属性中，key:uid
        String token = request.getHeader("Authorization");
        Long uid = TokenUtil.userTokenNotException(token, redisUtil);
        if (Objects.isNull(uid)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write("{\"msg\": \"请先登录\",\"code\": 403}");
            writer.flush();
            return false;
        }
        request.setAttribute("uid", uid);
        return true;
    }
}
```

- **SocketInterceptor：**

```java
public class SocketInterceptor implements HandshakeInterceptor {

    private String splitUrl;

    SocketInterceptor(String url) {
        this.splitUrl = url;
    }

    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> map) throws Exception {
        // 若放行连接，请在request的头部添加"Authorization"字段，值为用户的id;
        HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
        String token = req.getRequestURI().replace(splitUrl, "");
        Long uid = TokenUtil.userTokenNotException(token, redisUtil);
        if (Objects.isNull(uid)) return false;
        List<String> strings = request.getHeaders().get("Authorization");
        if (strings != null) strings.clear();
        request.getHeaders().add("Authorization", String.valueOf(uid));
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Exception e) {}
}
```

- **NhConfig：**

```java
@Configuration
@EnableWebSocket
public class NhConfig implements WebSocketConfigurer, WebMvcConfigurer {
    //==================WebSocket过滤=============//
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(new ChatSocket(), "/socket.chat/**")
                .addInterceptors(new SocketInterceptor("/socket.chat/"))
                .setAllowedOrigins("*");
    }


    //==================接口过滤=============//
    @Resource
    private ApiInterceptor apiInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiInterceptor)
                .addPathPatterns("/chat/**");
    }
}
```

## 最后需要你编写一个接口

因为项目存在用户头像和昵称问题，所以需要额外的接口来获取用户的头像和昵称。
但这并不是必须的，如果未提供，用户的头像和昵称都将显示默认数据。

**类型：POST**

**参数：@RequestBody Long[] uIds**

**返回值：结果请自定义一个 VO 进行封装，需要有以下三个属性。将以下结果统称为 Result**

```java
{
    data:[{
            uid:用户ID,
            name:用户昵称,
            photo:用户头像
            },
            ...
        ]
}

```
示例：
```java
    @Operation(summary = "获取用户基础信息可多个")
    @PostMapping("eqUserBasics")
    public Result<?> eqUserBasics(@RequestBody Long[] uIds) throws ChatException {
        return Result.success("ok", BeanCopy.BEANCOPY.userBasicsVo(userService.eqUser(uIds)));
    }
```
- **注意将数据结果用 data 赋值返回，前端只会读取 data 的值。**

**`注意在前端进行配置该接口`**
