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
	private static List<File> xmlList = new ArrayList<File>();

	private static List<String> usePictures = new ArrayList<String>();

	public static void main(String[] args) {
		System.out.println("任务开始...");
//		 File rootFile = new File(
//		 "/Users/panyi/git/Android-Fetcher/Android-Feather");
		//File rootFile = new File("/Users/panyi/app/work/DiskCacheDemo");
		 File rootFile = new File("/Users/panyi/git/ImageZoomLibrary");
		// File rootFile = new File("/Users/panyi/git/BabeShow/Babeshow");
		File[] allFiles = rootFile.listFiles();
		File resFiles = null;
		File srcFiles = null;
		File mainfestFile = null;// 布局文件夹
		// 找到res文件夹
		for (File file : allFiles) {
			if (file.getAbsolutePath().contains("res")) {
				resFiles = file;
			}

			if (file.getAbsolutePath().contains("src")) {
				srcFiles = file;
			}
			
			if (file.getAbsolutePath().contains("AndroidManifest.xml")) {
				mainfestFile = file;
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
			System.out.println("分析代码文件--->" + srcFile.getName());
			analysisSrcFile(srcFile);
		}// end for each

		searchXmlFile(resFiles);// 搜寻资源目录下的xml文件

		for (File xmlFile : xmlList) {
			System.out.println("分析" + xmlFile.getName() + "文件 ");
			analysisResXmlLayoutFile(xmlFile);
		}// end for each
		
		//读取分析manifest文件
		if(mainfestFile!=null){
			analysisResXmlLayoutFile(mainfestFile);
		}else{
			System.out.println("未发现AndroidManifest文件");
		}
		
		// 统计图片
		for (String imageName : usePictures) {
			ImageRecord record = imageMap.get(imageName);
			if (record != null) {
				System.out.println("使用图片--->" + record.name);
				record.useCount++;
			}
		}// end for each

		List<ImageRecord> canDeleteImageList = new ArrayList<ImageRecord>();
		for (String key : imageMap.keySet()) {
			ImageRecord record = imageMap.get(key);
			if (record.useCount < 1) {
				System.out.println("未被使用图片:" + record.getFilesName());
				canDeleteImageList.add(record);
			}// end
		}// end for each

		System.out.println("共有" + canDeleteImageList.size() + "张图片未被使用");
		for(ImageRecord record:canDeleteImageList){
			record.deleteFiles();
		}
	}

	/**
	 * 搜寻所有xml文件
	 * 
	 * @param rootFile
	 */
	public static void searchXmlFile(File rootFile) {
		File[] files = rootFile.listFiles();
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".xml")) {
				xmlList.add(file);
			} else if (file.isDirectory()) {
				searchXmlFile(file);
			}
		}// end for each
	}

	/**
	 * 分析布局文件 读取文件内容
	 * 
	 * @param file
	 */
	public static void analysisResXmlLayoutFile(File file) {
		String content = readFile(file);// 读取文件内容
		// System.out.println("xml文件内容--->" + content);
		//
		List<String> findImages = new ArrayList<String>();
		analysisImage(content, "@drawable/", findImages);
		for (String imageName : findImages) {
			System.out.println("扫描到的图 片名称--->" + imageName);
		}// end for each
		usePictures.addAll(findImages);
	}

	private static void analysisImage(String content, String reg,
			List<String> list) {
		int index = -1;
		int begin = 0;
		do {
			index = content.indexOf(reg, begin);
			// System.out.println("index="+index);
			if (index > 0) {
				int startIndex = index + reg.length();
				int endIndex = startIndex + 1;
				while (isDrawableChar(content.charAt(endIndex))) {
					endIndex++;
				}// end while
				String imageName = content.substring(startIndex, endIndex);
				list.add(imageName);
				begin = endIndex;
			}// end if
		} while (index > 0);
	}

	/**
	 * Invalid file name: must contain only [a-z0-9_.] 分析源码
	 * 
	 * @param file
	 */
	public static void analysisSrcFile(File file) {
		String content = readFile(file);// 读取文件内容
		List<String> findImages = new ArrayList<String>();
		// System.out.println(content);
		analysisImage(content, "R.drawable.", findImages);
		for (String imageName : findImages) {
			System.out.println("扫描到的图片名称--->" + imageName);
		}// end for each
		usePictures.addAll(findImages);
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
