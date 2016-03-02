package com.solomanhl.mycloset.view;

import android.graphics.Bitmap;

public class ImageInfo {
	private String path;
	/** 图片宽 **/
	private int width;
	/** 图片高 **/
	private int height;
	/** 左上角（中心点）的x初始坐标 **/
	private int x;
	/** 左上角（中心点）的y初始坐标 **/
	private int y;
	private Bitmap bit = null;

	private String yifu_type;//衣服的种类 shangyi kuzi qunzi

	public ImageInfo(){

	}

	public String getYifu_type() {
		return yifu_type;
	}

	public void setYifu_type(String yifu_type) {
		this.yifu_type = yifu_type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}


	public Bitmap getBit() {
		return bit;
	}

	public void setBit(Bitmap bit) {
		this.bit = bit;
	}
}
