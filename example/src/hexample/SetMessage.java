package hexample;

import java.io.Serializable;

public class SetMessage implements Serializable{

	public String ScreenshotDirectory,VideoDirectory,ScreenshotFormat,VideoFormat;
	
	public SetMessage(String ScreenshotDirectory,String VideoDirectory,
			String ScreenshotFormat,String VideoFormat){
		this.ScreenshotDirectory = ScreenshotDirectory;
		this.VideoDirectory = VideoDirectory;
		this.ScreenshotFormat = ScreenshotFormat;
		this.VideoFormat = VideoFormat;
	}
	
	public void setScreenshotDirectory(String ScreenshotDirectory){
		this.ScreenshotDirectory = ScreenshotDirectory;
	}
	
	public String getScreenshotDirectory(){
		return ScreenshotDirectory;
	}
	
	public void setVideoDirectory(String VideoDirectory){
		this.VideoDirectory = VideoDirectory;
	}
	
	public String getVideoDirectory(){
		return VideoDirectory;
	}
	
	public void setScreenshotFormat(String ScreenshotFormat){
		this.ScreenshotFormat = ScreenshotFormat;
	}
	
	public String getScreenshotFormat(){
		return ScreenshotFormat;
	}
	
	public void setVideoFormat(String VideoFormat){
		this.VideoFormat = VideoFormat;
	}
	
	public String getVideoFormat(){
		return VideoFormat;
	}
	
	public  void display() {
		System.out.println("存储截图目录：" + ScreenshotDirectory + "\n"
				+ "存储视频目录：" + VideoDirectory + "\n"
				+"截图保存格式：" + ScreenshotFormat + "\n"
				+"视频保存格式：" + VideoFormat);
	}
}
