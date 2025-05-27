package red.mlz.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class AliOssUtility {
    private static final String endpoint = "oss-cn-beijing.aliyuncs.com";
    private static final String accessKeyId = "LTAI5t7gv18YBqEW2QV791Ax";
    private static final String accessKeySecret = "ZaNr2v8TqbjIKA4IAF1LEeISDY5uGy";
    private static final String bucketName = "goodsimage01";

    public String uploadImage(MultipartFile file) {
        try {
            // 创建OSSClient实例
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 获取图片宽度和高度
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            // 获取当前日期
            String date = new SimpleDateFormat("yyMMdd").format(new Date());
            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 生成唯一文件名
            String uniqueId = UUID.randomUUID().toString().replace("-", "");
            String fileName = String.format("image/%s/%s/%s_%dx%d%s", date, date, uniqueId, width, height, fileExtension);

            // 上传文件
            ossClient.putObject(bucketName, fileName, file.getInputStream());
            ossClient.shutdown();

            // 返回文件访问URL
            return "https://" + bucketName + "." + endpoint + "/" + fileName;
        } catch (
                IOException e) {
            e.printStackTrace();
            return "上传失败：" + e.getMessage();
        }
    }

    public  String uploadVideo(MultipartFile file) {
        try {
            // 创建OSSClient实例
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 获取当前日期
            String date = new SimpleDateFormat("yyMMdd").format(new Date());

            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 生成唯一文件名
            String uniqueId = UUID.randomUUID().toString().replace("-", "");
            String fileName = String.format("video/%s/%s%s", date, uniqueId, fileExtension);

            // 上传文件
            ossClient.putObject(bucketName, fileName, file.getInputStream());
            ossClient.shutdown();

            // 返回文件访问URL
            return "https://" + bucketName + "." + endpoint + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败：" + e.getMessage();
        }
    }

    public  String uploadFile(MultipartFile file) {
        try {
            // 创建OSSClient实例
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 获取当前日期
            String date = new SimpleDateFormat("yyMMdd").format(new Date());

            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 生成唯一文件名
            String uniqueId = UUID.randomUUID().toString().replace("-", "");
            String fileName = String.format("file/%s/%s%s", date, uniqueId, fileExtension);

            // 上传文件
            ossClient.putObject(bucketName, fileName, file.getInputStream());
            ossClient.shutdown();

            // 返回文件访问URL
            return "https://" + bucketName + "." + endpoint + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败：" + e.getMessage();
        }
    }

}