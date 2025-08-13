package com.example.springboot_ch_1.util;

import net.coobird.thumbnailator.Thumbnails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;

/**
 * 栾俊豪 2020012422
 *
 * @author 栾俊豪
 * @Date 2024/2/22 14:59
 */

public class ImageFc {
    public static BufferedImage compressImage(byte[] imageBytes, String extension,double size,String type) {
        try {
            System.out.println("数据为: "+imageBytes);
            System.out.println("格式为: "+extension);
            System.out.println("大小为: "+size);
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            double imageSizeInMB = (double) imageBytes.length / (1024 * 1024);
            double maxSizeInMB = 100;
            System.out.println("图片传送来大小为: "+imageSizeInMB+"MB,类型是: "+type);
            if (type.equals("avatar")){
                maxSizeInMB = 0.7;
            }else if(type.equals("content")){
                maxSizeInMB = 1.5;
            }
            // 检查图片大小是否大于0.7/1.5MB,如果大于就压缩,不大于就算了
            if (imageSizeInMB >= maxSizeInMB) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Thumbnails.of(originalImage)
                        .outputFormat(extension)
                        .size((int) (originalImage.getWidth() * size), (int) (originalImage.getHeight() * size))
                        .outputQuality(1)
                        .toOutputStream(outputStream);
                return ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));
            } else {
                // 如果图片大小小于等于0.7/1.5MB，返回原始图片
                return originalImage;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // 如果发生异常，返回 null
    }
    public static String getExtensionFromBase64Data(String base64Data) {
        return base64Data.startsWith("/9j/") ? "jpeg" : "png";
    }
    public static void saveImage(String base64Data,String imagePath,String extension){
        // 将 base64 数据解码为二进制数据
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

        // 压缩图片大小
        BufferedImage compressedImage = compressImage(imageBytes, extension, 0.5,"content");
        System.out.println("压缩后的图片为: "+compressedImage);
        // 将压缩后的二进制数据保存为图片文件
        try (FileOutputStream fos = new FileOutputStream(imagePath)) {
            ImageIO.write(compressedImage, extension, fos);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
