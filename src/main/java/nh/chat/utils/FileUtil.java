package nh.chat.utils;

import cn.hutool.core.util.RandomUtil;
import nh.chat.exception.ChatException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 上传图片到服务器
     *
     * @param file 图片
     * @return
     * @throws ChatException
     */
    public static String uploadImg(MultipartFile file) throws ChatException {
        if (file.isEmpty()) throw new ChatException("图片空异常");
        String fileName = file.getOriginalFilename();
        if (fileName == null || StringUtils.isEmpty(fileName.trim())) throw new ChatException("图片空昵称异常");
        String[] splitFile = fileName.trim().split("\\.");
        String fileSuffix = splitFile[splitFile.length - 1];
        if (!"png".equals(fileSuffix) && !"jpg".equals(fileSuffix) && !"jpeg".equals(fileSuffix))
            throw new ChatException("只支持jpg、jpeg和png格式图片");
        String newFileName = RandomUtil.randomString(14) + "." + fileSuffix;
        try {
            return FileUtil.comUpImg(file, newFileName);
        } catch (Exception e) {
            throw new ChatException("服务器异常,发送失败");
        }
    }

    /**
     * 上传头像
     *
     * @param file     文件
     * @param fileName 文件名
     * @return 服务器图片的位置
     * @throws ChatException c
     */
    public static String comUpImg(MultipartFile file, String fileName) throws ChatException {
        try {
            File newFile = new File("/home/nh-chat/chat/permit/images");
            if (!newFile.exists()) {
                boolean resMkdirs = newFile.mkdirs();
                if (!resMkdirs) throw new ChatException("服务器异常");
            }
            String fileRoute = newFile + File.separator + fileName;
            File lastFile = new File(fileRoute);
            if (!lastFile.exists()) {
                boolean resNewFile = lastFile.createNewFile();
                if (!resNewFile) throw new ChatException("服务器异常");
            }
            file.transferTo(lastFile);
            return "https://www.notes-something.fun/im/images/" + fileName;
        } catch (Exception e) {
            throw new ChatException("服务器异常");
        }
    }
}
