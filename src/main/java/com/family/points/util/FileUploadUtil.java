package com.family.points.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

/**
 * 文件上传工具类
 */
public class FileUploadUtil {

    private static final String UPLOAD_DIR = "upload/";

    /**
     * 上传文件
     */
    public static String upload(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 创建上传目录
        String uploadPath = UPLOAD_DIR + subDir + "/";
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        String fileName = IdUtil.simpleUUID() + "." + suffix;

        // 保存文件
        File destFile = new File(uploadPath + fileName);
        file.transferTo(destFile);

        // 返回访问路径
        return "/" + subDir + "/" + fileName;
    }

    /**
     * 删除文件
     */
    public static void delete(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(UPLOAD_DIR + filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
