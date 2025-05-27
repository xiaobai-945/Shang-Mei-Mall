package red.mlz.common.utils;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtils {
    private static String defaultMaleAvatar = "/photo/avatar.png";
    private static String defaultFeMaleAvatar = "/photo/avatar.png";

    public static String getDefaultMaleAvatar(){
        return defaultMaleAvatar;
    }

    public static String getDefaultFeMaleAvatar(){
        return defaultFeMaleAvatar;
    }

    public static int[] getImageWidthAndHeight(String imageUrl){
        int[] wh = new int[2];
        String[] imageStr = imageUrl.split("_");
        if(imageStr.length >= 2){
            String[] imageStrEnd = imageStr[imageStr.length -1].split("\\.");
            String[] imageParam = imageStrEnd[0].split("x");
            if(imageParam.length == 2 && BaseUtils.isNumeric(imageParam[0]) && BaseUtils.isNumeric(imageParam[1])){
                wh[0] = new Integer(imageParam[0]);
                wh[1] = new Integer(imageParam[1]);
                return wh;
            }
        }

        try {
            URL url = new URL(imageUrl);
            BufferedImage sourceImg = ImageIO.read(new BufferedInputStream(url.openStream()));
            wh[0] = sourceImg.getWidth();
            wh[1] = sourceImg.getHeight();
        } catch (Exception e) {
            wh[0] = 0;
            wh[1] = 0;
        }
        return wh;
    }
    // ar的获取
    public static ImageInfo getImageInfo(String imageUrl) {
        // 正则匹配宽和高
        Pattern pattern = Pattern.compile("_(\\d+)x(\\d+)");  // 假设宽高格式是 _宽x高
        Matcher matcher = pattern.matcher(imageUrl);

        if (matcher.find()) {
            int width = Integer.parseInt(matcher.group(1));  // 宽度
            int height = Integer.parseInt(matcher.group(2));  // 高度
            double ar = (double) width / height;  // 计算AR值


            // 返回 ImageInfo 对象
            return new ImageInfo(imageUrl, ar);
        }

        // 如果没有匹配到宽高，返回默认值
        return new ImageInfo(imageUrl, 1.0);  // 默认值
    }


}
