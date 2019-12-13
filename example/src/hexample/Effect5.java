package hexample;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Controls;
import javax.media.Effect;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

/**
 * 鬼影特效
 */
public class Effect5 implements Effect {

	private static String EffectName = "Effect5"; // 类名
	protected RGBFormat inputFormat; // 输入的色彩格式
	protected RGBFormat outputFormat; // 输出的色彩格式
	protected Format[] supportedInputFormats; // 所有支持的输入格式
	protected Format[] supportedOutputFormats; // 所有支持的输出格式

	// 构造方法，实例化所有支持的输入输出色彩为RGB色彩格式
	public Effect5() {
		supportedInputFormats = new Format[] { new RGBFormat() };
		supportedOutputFormats = new Format[] { new RGBFormat() };
	}

	// 实现Effect接口继承自Codec的方法，返回所有支持的输入格式
	@Override
	public Format[] getSupportedInputFormats() {
		// TODO Auto-generated method stub
		return supportedInputFormats;
	}

	// 实现Effect接口继承自Codec的方法，返回所有支持的输出格式
	@Override
	public Format[] getSupportedOutputFormats(Format parm1) {
		// TODO Auto-generated method stub
		if (parm1 == null) {
			return supportedOutputFormats;
		}
		if (!(parm1 instanceof RGBFormat)) {
			return new Format[0];
		}
		// 如果是，根据该格式返回一个对象引用
		RGBFormat orf = (RGBFormat) parm1.clone();
		return new Format[] { orf };
	}

	// 实现Effect接口继承自Codec的方法，处理输入的媒体数据parm1，得到经过处理的输出的媒体数据parm2
	// 该方法完成本类的效用，处理媒体数据，是本类的核心方法
	@Override
	public int process(Buffer parm1, Buffer parm2) {
		// TODO Auto-generated method stub
		// 获取输入格式的视频分辨率
		Dimension size = ((VideoFormat) parm1.getFormat()).getSize();
		int inWidth = size.width;
		int inHeight = size.height;
		Object srcData = parm1.getData(); // 获取输入的数据对象的引用
		// 如果输入输出的媒体数据非合法的short[]int[]形式，返回出错信息
		if (!(srcData instanceof byte[])) {
			return this.BUFFER_PROCESSED_FAILED;
		}
		Object outputData = null; // parm2.getData();
		if (outputData != null) {
			if (!(srcData instanceof byte[])) {
				return this.BUFFER_PROCESSED_FAILED;
			}
		}
		outputData = this.hMirror((byte[]) srcData, inWidth, inHeight);

		parm2.setData(outputData);

		int inLength = parm1.getLength(); // 输入的数据的长度
		int inOffset = parm1.getOffset(); // 偏移量
		// 设置输出媒体数据的格式
		parm2.setFormat(parm1.getFormat());
		// 设置输出媒体数据的长度和偏移量
		parm2.setLength(inLength);
		parm2.setOffset(inOffset);
		// 返回数据处理成功完成信息
		return this.BUFFER_PROCESSED_OK;

	}

	// 实现Effect接口继承自Codec的方法，返回传入的数据格式
	@Override
	public Format setInputFormat(Format parm1) {
		// TODO Auto-generated method stub
		System.out.println("setInputFormat[input=" + parm1 + "]");
		inputFormat = (RGBFormat) parm1;
		return (Format) inputFormat;
	}

	// 实现Effect接口继承自Codec的方法，返回传出的数据格式
	@Override
	public Format setOutputFormat(Format parm1) {
		// TODO Auto-generated method stub
		System.out.println("setOutputFormat[output=" + parm1 + "]");
		outputFormat = (RGBFormat) parm1;
		return (Format) outputFormat;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		System.out.println("close");
	}

	// 返回类名
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		System.out.println("getName");
		return EffectName;
	}

	@Override
	public void open() throws ResourceUnavailableException {
		// TODO Auto-generated method stub
		System.out.println("open");
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		System.out.println("reset");
	}

	@Override
	public Object getControl(String parm1) {
		// TODO Auto-generated method stub
		System.out.println("getControl[controlType = " + parm1 + "]");
		try {
			Class cls = Class.forName(parm1);
			Object[] cs = this.getControls();
			for (int i = 0; i < cs.length; i++) {
				if (cls.isInstance(cs[i])) {
					return cs[i];
				}
			}
			return null;
		} catch (Exception err) {
			// TODO: handle exception
			return null;
		}

	}

	@Override
	public Object[] getControls() {
		// TODO Auto-generated method stub
		System.out.println("getControls");
		return new Controls[0];
	}

	public byte[] hMirror(byte[] srcData, int w, int h) {

		int b = 0, g = 0, r = 0;

		byte[] tempData = (byte[]) srcData.clone();
		int length = srcData.length;
		// i是新像素的x坐标，j是新像素的y坐标
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {

				// 由于采取最邻近灰度插值，这里直接求整。如果采用双线性插值，必须保留小数，并计算与前后左右像素坐标的差值，作为计算新像素的权重
				// 将二维坐标转换为一维数组的下标。3个字节对应3个颜色分量
				int tempLocation1 = i * 3 + j * w * 3; // tempLocation是新坐标的一维下标
				int tempLocation2 = i * 3 + 1 + j * w * 3;
				int tempLocation3 = i * 3 + 2 + j * w * 3;
				int srcLocation1 = i * 3 + j * w * 3 + 3; // tempLocation是新坐标的一维下标
				int srcLocation2 = i * 3 + 1 + j * w * 3 + 3;
				int srcLocation3 = i * 3 + 2 + j * w * 3 + 3;
				// 由于采用最邻近灰度插值，这里直接把旧坐标颜色赋给新坐标
				// 如果采用双线性插值，要用旧坐标的前后左右像素坐标的差值与旧坐标颜色的加权和
				if ((tempLocation1 <= length) && (tempLocation2 <= length)
						&& (tempLocation3 <= length)
						&& (srcLocation1 <= length)
						&& (srcLocation2 <= length)
						&& (srcLocation3 <= length)) {

					tempData[tempLocation1] = (byte)~srcData[srcLocation1];

					tempData[tempLocation2] = (byte)~srcData[srcLocation2];

					tempData[tempLocation3] = (byte)~srcData[srcLocation3];

				}

				

			}
		}

		return tempData;
	}

}
