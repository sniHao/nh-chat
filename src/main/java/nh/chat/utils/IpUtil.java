package nh.chat.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Description:
 * @Author: xph
 * @Date: 2024-09-26 10:34
 */
public class IpUtil {
    private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);
    protected static Searcher searcher;

    static {
        InputStream resourceAsStream = IpUtil.class.getClassLoader().getResourceAsStream("ip2region.xdb");
        if (resourceAsStream != null) {
            byte[] cBuff;
            try (resourceAsStream) {
                cBuff = IOUtils.toByteArray(resourceAsStream);
                searcher = Searcher.newWithBuffer(cBuff);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过request解析请求的Ip
     *
     * @param request
     * @return: java.lang.String
     * @Author:Xphao
     * @Date: 2023/3/11 10:52
     */
    public static String getIp(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet;
                    try {
                        inet = InetAddress.getLocalHost();
                        ipAddress = inet.getHostAddress();
                    } catch (UnknownHostException e) {
                        logger.error("通过request解析请求的Ip异常" + e);
                    }
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }
}
