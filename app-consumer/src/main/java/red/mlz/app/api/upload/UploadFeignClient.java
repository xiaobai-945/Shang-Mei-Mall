package red.mlz.app.api.upload;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import red.mlz.common.utils.Response;

@FeignClient(name="app-provider")
public interface UploadFeignClient {

    // 上传图片接口
    @RequestMapping("/upload")
    String uploadImage(@RequestParam("file") MultipartFile file) ;


    @RequestMapping("/uploadImage")
    Response uploadImages(@RequestParam("file") MultipartFile file);

    @RequestMapping("/uploadVideo")
    Response uploadVideo(@RequestParam("file") MultipartFile file);

    @RequestMapping("/uploadFile")
    Response uploadFile(@RequestParam("file") MultipartFile file);
}
