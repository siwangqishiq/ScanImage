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

/**
 * 扫描图片
 * 
 * @author panyi
 * 
 */
public class Scan {
	private static Map<String, ImageRecord> imageMap = new HashMap<String, ImageRecord>();
	private static List<File> srcFileList = new ArrayList<File>();

	public static void main(String[] args) {
		System.out.println("任务开始...");
		// File rootFile = new File(
		// "/Users/panyi/git/Android-Fetcher/Android-Feather");

		File rootFile = new File("/Users/panyi/git/ImageZoomLibrary");
		File[] allFiles = rootFile.listFiles();
		File resFiles = null;
		File srcFiles = null;
		// 找到res文件夹
		for (File file : allFiles) {
			if (file.getAbsolutePath().contains("res")) {
				resFiles = file;
			}

			if (file.getAbsolutePath().contains("src")) {
				srcFiles = file;
			}
		}// end for each
		if (resFiles == null) {
			System.out.println("工程下未找到资源文件夹");
			return;
		}

		System.out.println("开始扫描资源文件夹---->" + resFiles.getAbsolutePath()
				+ " 读取图片数据");
		List<File> imageFiles = new ArrayList<File>();
		for (File file : resFiles.listFiles()) {// 选择图片文件夹
			String path = file.getAbsolutePath();
			if (path.contains("drawable")) {
				imageFiles.add(file);
			}
			// System.out.println(path);
		}// end for each

		// 图片导入操作
		for (int i = 0, len = imageFiles.size(); i < len; i++) {
			File file = imageFiles.get(i);
			scanImageFolder(file);
		}// end for i

		if (srcFiles != null) {
			findSrcFolder(srcFiles);
		} else {
			System.out.println("未找到源代码文件夹");
		}// end if

		for (File srcFile : srcFileList) {
			 System.out.println("分析代码文件--->" + srcFile.getAbsolutePath());
			analysisSrcFile(srcFile);
		}// end for each

	}

	/**
	 * Invalid file name: must contain only [a-z0-9_.] 分析源码
	 * 
	 * @param file
	 */
	public static void analysisSrcFile(File file) {
		String content = readFile(file);// 读取文件内容
		List<String> findImages = new ArrayList<String>();
		System.out.println(content);

		int index = -1;
		int begin = 0;
		do {
			index = content.indexOf("R.drawable.", begin);
			//System.out.println("index="+index);
			if (index > 0) {
				int startIndex = index+"R.drawable.".length();
				int endIndex = startIndex+1;
				while(isDrawableChar(content.charAt(endIndex))){
					endIndex++;
				}//end while
				String imageName = content.substring(startIndex, endIndex);
				findImages.add(imageName);
				begin = endIndex;
			}// end if
		} while (index > 0);

		for (String imageName : findImages) {
			System.out.println("扫描到的图片名称--->" + imageName);
		}// end for each
	}

	/**
	 * Invalid file name: must contain only [a-z0-9_.] 判断是否还是图片名称
	 * 
	 * @param ch
	 * @return
	 */
	private static boolean isDrawableChar(char ch) {
		if (isLowUpperChar(ch) || ch == '_' || isNumber(ch)) {
			return true;
		}

		return false;
	}

	private static boolean isNumber(char c) {
		return c >= '0' && c <= '9';
	}

	private static boolean isLowUpperChar(char c) {
		return c >= 'a' && c <= 'z';
	}

	/**
	 * 寻找源代码文件夹
	 * 
	 * @param baseFile
	 */
	public static void findSrcFolder(File baseFile) {
		File[] files = baseFile.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				findSrcFolder(file);
			} else if (file.isFile()) {
				srcFileList.add(file);
			}// end if
		}// end for each
	}

	public static void println(String msg) {
		System.out.println(msg);
	}

	/**
	 * 
	 * @param file
	 */
	public static void scanImageFolder(File file) {
		System.out.println("扫描图片文件夹-->" + file.getAbsolutePath());

		for (File img : file.listFiles()) {
			if (img.isFile()) {
				String key = ImageRecord.getNameByPath(img.getAbsolutePath());
				System.out.println("文件名:" + key);

				ImageRecord record = imageMap.get(key);
				if (record == null) {//
					record = new ImageRecord(img);
					imageMap.put(key, record);
				} else {// 已存在
					record.addFile(img);
				}// end if
			}// end if
		}// end for each
	}

	/**
	 * 读取文本文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readFile(File file) {
		StringBuffer sb = new StringBuffer();
		if (file.isFile() && file.exists()) {
			try {
				InputStreamReader reader = new InputStreamReader(
						new FileInputStream(file), "utf-8");
				BufferedReader bufferReader = new BufferedReader(reader);
				String lineTxt = null;
				while ((lineTxt = bufferReader.readLine()) != null) {
					// System.out.println(lineTxt);
					sb.append(lineTxt.trim());
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}// end if
		return sb.toString();
	}

	/**
	 * 读取文本文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readFile(String filePath) {
		File file = new File(filePath);
		return readFile(file);
	}
}// end class
