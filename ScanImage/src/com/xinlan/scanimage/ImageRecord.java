package com.xinlan.scanimage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片使用情况记录
 * 
 * @author panyi
 * 
 */
public class ImageRecord {
	public static final int NO_USE = 0;// 未被使用
	public static final int CODE_USE = 1;// 代码中使用
	public static final int LAYOUT_USE = 2;// 资源布局中使用
	public static final int CODE_AND_LAYOUT_USE = 3;

	public String name;
	public int useType;
	public int userCount;//被引用次数
	private List<File> fileList = new ArrayList<File>();

	public List<File> getFileList() {
		return fileList;
	}

	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}

	public ImageRecord(String path) {
		this.name = getNameByPath(path);
		useType = NO_USE;
		userCount = 0;//
	}

	public ImageRecord(File file) {
		this(file.getAbsolutePath());
		addFile(file);
	}

	/**
	 * 增加file对象
	 * @param newFile
	 */
	public void addFile(File newFile) {
		boolean hasExist = false;
		for (File itemFile : fileList) {
			if (itemFile.getAbsoluteFile().equals(newFile)) {
				hasExist = true;
			}
		}// end for each
		if (!hasExist) {// 不存在 加入列表中
			fileList.add(newFile);
		}
	}

	public static String getNameByPath(String path) {
		int index = -1;
		if (path.contains(".9.png")) {// 含有.9图
			index = path.lastIndexOf(".9.png");
		} else {// 普通图片
			index = path.lastIndexOf(".");
		}
		int divIndex = path.lastIndexOf("/") + 1;
		return path.substring(divIndex, index).trim();
	}
}// end class
