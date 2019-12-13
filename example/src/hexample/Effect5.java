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
 * ��Ӱ��Ч
 */
public class Effect5 implements Effect {

	private static String EffectName = "Effect5"; // ����
	protected RGBFormat inputFormat; // �����ɫ�ʸ�ʽ
	protected RGBFormat outputFormat; // �����ɫ�ʸ�ʽ
	protected Format[] supportedInputFormats; // ����֧�ֵ������ʽ
	protected Format[] supportedOutputFormats; // ����֧�ֵ������ʽ

	// ���췽����ʵ��������֧�ֵ��������ɫ��ΪRGBɫ�ʸ�ʽ
	public Effect5() {
		supportedInputFormats = new Format[] { new RGBFormat() };
		supportedOutputFormats = new Format[] { new RGBFormat() };
	}

	// ʵ��Effect�ӿڼ̳���Codec�ķ�������������֧�ֵ������ʽ
	@Override
	public Format[] getSupportedInputFormats() {
		// TODO Auto-generated method stub
		return supportedInputFormats;
	}

	// ʵ��Effect�ӿڼ̳���Codec�ķ�������������֧�ֵ������ʽ
	@Override
	public Format[] getSupportedOutputFormats(Format parm1) {
		// TODO Auto-generated method stub
		if (parm1 == null) {
			return supportedOutputFormats;
		}
		if (!(parm1 instanceof RGBFormat)) {
			return new Format[0];
		}
		// ����ǣ����ݸø�ʽ����һ����������
		RGBFormat orf = (RGBFormat) parm1.clone();
		return new Format[] { orf };
	}

	// ʵ��Effect�ӿڼ̳���Codec�ķ��������������ý������parm1���õ���������������ý������parm2
	// �÷�����ɱ����Ч�ã�����ý�����ݣ��Ǳ���ĺ��ķ���
	@Override
	public int process(Buffer parm1, Buffer parm2) {
		// TODO Auto-generated method stub
		// ��ȡ�����ʽ����Ƶ�ֱ���
		Dimension size = ((VideoFormat) parm1.getFormat()).getSize();
		int inWidth = size.width;
		int inHeight = size.height;
		Object srcData = parm1.getData(); // ��ȡ��������ݶ��������
		// ������������ý�����ݷǺϷ���short[]int[]��ʽ�����س�����Ϣ
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

		int inLength = parm1.getLength(); // ��������ݵĳ���
		int inOffset = parm1.getOffset(); // ƫ����
		// �������ý�����ݵĸ�ʽ
		parm2.setFormat(parm1.getFormat());
		// �������ý�����ݵĳ��Ⱥ�ƫ����
		parm2.setLength(inLength);
		parm2.setOffset(inOffset);
		// �������ݴ���ɹ������Ϣ
		return this.BUFFER_PROCESSED_OK;

	}

	// ʵ��Effect�ӿڼ̳���Codec�ķ��������ش�������ݸ�ʽ
	@Override
	public Format setInputFormat(Format parm1) {
		// TODO Auto-generated method stub
		System.out.println("setInputFormat[input=" + parm1 + "]");
		inputFormat = (RGBFormat) parm1;
		return (Format) inputFormat;
	}

	// ʵ��Effect�ӿڼ̳���Codec�ķ��������ش��������ݸ�ʽ
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

	// ��������
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
		// i�������ص�x���꣬j�������ص�y����
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {

				// ���ڲ�ȡ���ڽ��ҶȲ�ֵ������ֱ���������������˫���Բ�ֵ�����뱣��С������������ǰ��������������Ĳ�ֵ����Ϊ���������ص�Ȩ��
				// ����ά����ת��Ϊһά������±ꡣ3���ֽڶ�Ӧ3����ɫ����
				int tempLocation1 = i * 3 + j * w * 3; // tempLocation���������һά�±�
				int tempLocation2 = i * 3 + 1 + j * w * 3;
				int tempLocation3 = i * 3 + 2 + j * w * 3;
				int srcLocation1 = i * 3 + j * w * 3 + 3; // tempLocation���������һά�±�
				int srcLocation2 = i * 3 + 1 + j * w * 3 + 3;
				int srcLocation3 = i * 3 + 2 + j * w * 3 + 3;
				// ���ڲ������ڽ��ҶȲ�ֵ������ֱ�ӰѾ�������ɫ����������
				// �������˫���Բ�ֵ��Ҫ�þ������ǰ��������������Ĳ�ֵ���������ɫ�ļ�Ȩ��
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
