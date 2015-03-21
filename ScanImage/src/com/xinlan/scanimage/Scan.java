package com.xinlan.scanimage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scan {
	private static Map<String,ImageRecord> imageMap = new HashMap<String,ImageRecord>();
	
	public static void main(String[] args) {
		System.out.println("任务开始...");
		File rootFile = new File(
				"/Users/panyi/git/Android-Fetcher/Android-Feather");
		File[] allFiles = rootFile.listFiles();
		File resFiles = null;
		// 找到res文件夹
		for (File file : allFiles) {
			if (file.getAbsolutePath().contains("res")) {
				resFiles = file;
				break;
			}
		}// end for each
		if (resFiles == null) {
			System.out.println("工程下未找到资源文件夹");
			return;
		}

		System.out.println("开始扫描资源文件夹---->" + resFiles.getAbsolutePath()
				+ " 读取图片数据");
		List<File> imageFiles = new ArrayList<File>();
		for(File file:resFiles.listFiles()){//选择图片文件夹
			String path = file.getAbsolutePath();
			if(path.contains("drawable")){
				imageFiles.add(file);
			}
			//System.out.println(path);
		}//end for each
		
		for(int i=0,len = imageFiles.size();i<len;i++){
			File file = imageFiles.get(i);
			scanImageFolder(file);
		}//end for i
	}
	
	public static void scanImageFolder(File file){
		System.out.println("扫描图片文件夹-->"+file.getAbsolutePath());
		
		for(File img:file.listFiles()){
			if(img.isFile()){
				System.out.println("文件名:"+img.getName());
			}//end if
		}//end for each
	}

	/**
	 * 读取文本文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readFile(String filePath) {
		File file = new File(filePath);
		StringBuffer sb = new StringBuffer();
		if (file.isFile() && file.exists()) {
			try {
				InputStreamReader reader = new InputStreamReader(
						new FileInputStream(file), "utf-8");
				BufferedReader bufferReader = new BufferedReader(reader);
				String lineTxt = null;
				while ((lineTxt = bufferReader.readLine()) != null) {
					// System.out.println(lineTxt);
					sb.append(lineTxt);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}// end if
		return sb.toString();
	}
}// end class
