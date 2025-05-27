package red.mlz.app.controller.excel;

import com.alibaba.excel.EasyExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import red.mlz.app.module.goods.service.CategoryService;
import red.mlz.common.module.goods.entity.Category;
import red.mlz.common.utils.Response;

import java.util.List;


/**
 * 导出类目表
 */
@RestController
public class ExcelController {
    @Autowired
    private CategoryService service;

    @RequestMapping("/export/file")
    public String export() {
        List<Category> categories = service.getAll();
        String fileName = "categories.xlsx";
        EasyExcel.write(fileName, Category.class).sheet("类目列表").doWrite(categories);
        return "导出成功" + fileName;

    }

    /**
     * 导入
     */
    @RequestMapping("/import/file")
    public Response importFile(@RequestParam(name = "file") MultipartFile file) {

        if (file.isEmpty()) {
            return new Response<>(4006);
        }

        List<String> excleList;
        // 读取 Excel 文件
        try {
            excleList = EasyExcel.read(file.getInputStream())
                    .sheet()
                    .doReadSync();
            return new Response<>(1001, excleList);

        } catch (Exception e) {
            return new Response(4004);
        }
    }

}
