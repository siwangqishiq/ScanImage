package com.xinlan.scanimage;

/**
 * 图片使用情况记录
 * @author panyi
 *
 */
public class ImageRecord {
	public static final int NO_USE=0;//未被使用
	public static final int CODE_USE=1;//代码中使用
	public static final int LAYOUT_USE=2;//资源布局中使用
	public static final int CODE_AND_LAYOUT_USE=3;
	
	public String path;//完整路径
	public String name;
	public int useType;
	
	public ImageRecord(String path){
		this.path = path;
		this.name = getNameByPath(path);
		useType = NO_USE;
	}
	
	public static String getNameByPath(String path){
		int index = path.lastIndexOf(".");
		int divIndex = path.lastIndexOf("/")+1;
		return path.substring(divIndex, index).trim();
	}
}//end class
