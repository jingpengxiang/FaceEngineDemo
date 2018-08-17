package com.lu.face.faceenginedemo.engine.models;

public class ChannelParam {
	private int id;
	private String camera;
	private int bottom;
	private int top;

	public ChannelParam(int id, String camera, int bottom, int top) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.camera = camera;
		this.bottom = bottom;
		this.top = top;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCamera() {
		return camera;
	}

	public void setCamera(String camera) {
		this.camera = camera;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}
}
