package red.mlz.app.controller.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import red.mlz.common.utils.AliOssUtility;
import red.mlz.common.utils.Response;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class UploadController {
    // 直接在代码中指定文件上传路径
    private static final String UPLOAD_DIR = "/Users/liuzefeng/Documents/JavaWeb/good/app/image";  // 保存文件的目录

    // 上传图片接口
    @RequestMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();

        // 生成唯一文件名，避免文件重名
        String newFileName = UUID.randomUUID().toString() + "_" + originalFilename;

        // 定义文件保存的路径
        File dest = new File(UPLOAD_DIR + newFileName);

        // 如果文件夹不存在，创建文件夹
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        // 保存文件到本地
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 返回文件的访问路径
        return "文件上传成功，文件访问路径为:" + newFileName;
    }

    @Autowired
    private AliOssUtility aliOssUtility;

    @RequestMapping("/uploadImage")
    public Response uploadImages(@RequestParam("file") MultipartFile file) {

        return new Response<>(1001, "上传成功,图片地址：" + aliOssUtility.uploadImage(file));
    }

    @RequestMapping("/uploadVideo")
    public Response uploadVideo(@RequestParam("file") MultipartFile file) {

        return new Response<>(1001, "上传成功，视频地址:" + aliOssUtility.uploadVideo(file));
    }

    @RequestMapping("/uploadFile")
    public Response uploadFile(@RequestParam("file") MultipartFile file) {

        return new Response(1001, "上传成功，文件地址:" + aliOssUtility.uploadFile(file));
    }
}
