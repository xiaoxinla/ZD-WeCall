package com.wecall.contacts.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 图片处理工具类
 * 
 * @author xiaoxin
 */
public class ImageUtil {

	/**
	 * 将文本转化为二维码
	 * 
	 * @param content
	 *            传入的字符串
	 * @return 生成的二维码
	 * @throws WriterException
	 */
	public static Bitmap CreateQRCode(String content,int size) throws WriterException {
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(content,
				BarcodeFormat.QR_CODE, size, size);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				}
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	/**
	 * 获取本地的图片
	 * @param path 图片所在路径
	 * @param filename 图片名
	 * @return 图片的bitmap对象
	 */
	public static Bitmap getLocalBitmap(String path,String filename){
		Bitmap bitmap = null;  
	    try  
	    {  
	        File file = new File(path+filename);  
	        if(file.exists())  
	        {  
	            bitmap = BitmapFactory.decodeFile(path+filename);  
	        }  
	    } catch (Exception e)  
	    {  
	        e.printStackTrace();
	    }  
	    
	    return bitmap;  
	}

	/**
	 * 保存图片
	 * @param bitmap 要保存的bitmap图片
	 * @param path 路径
	 * @param fileName 文件名
	 * @throws IOException
	 */
	public static void saveImage(Bitmap bitmap, String path, String fileName)
			throws IOException {
		File dirFile = new File(path);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File myCaptureFile = new File(path + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
	}

	/**
	 * 保存图片
	 * @param bitmap 要保存的bitmap图片
	 * @param path 路径
	 * @param fileName 文件名
	 * @param ratio 图片压缩比率
	 * @throws IOException
	 */
	public static void saveImage(Bitmap bitmap, String path, String fileName,
			int ratio) throws IOException {
		File dirFile = new File(path);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File myCaptureFile = new File(path + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bitmap.compress(Bitmap.CompressFormat.JPEG, ratio, bos);
		bos.flush();
		bos.close();
	}
	
	/**
	 * 删除图片
	 * @param path 所在路径
	 * @param fileName 图片名
	 */
	public static void deleteImage(String path,String fileName){
		File file = new File(path+fileName);
		if(file.exists()){
			if(file.isFile()){
				file.delete();
			}
		}
	}
	
	/**
	 * 重命名图片
	 * @param oldpath 就路径
	 * @param newpath 新路径
	 */
	public static void renameImage(String oldpath,String newpath){
		File file = new File(oldpath);
		File newFile = new File(newpath);
		if(file.exists()){  
            file.renameTo(newFile);  
        } 
	}
	
}
