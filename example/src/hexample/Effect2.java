package hexample;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Controls;
import javax.media.Effect;
import javax.media.Format;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

/**
 * �����ڰ���Ч
 */
public class Effect2 implements Effect {

	private static String EffectName = "Effect2";
	protected RGBFormat inputFormat;//�����ɫ�ʸ�ʽ
	protected RGBFormat outputFormat;//�����ɫ�ʸ�ʽ
	protected Format[] supportedInputFormats;//��֧�ֵ������ʽ
	protected Format[] supportedOutputFormats;//��֧�ֵ������ʽ
	private int BUFFER_PROCESSED_FAILED=0;

	//���췽��   ʵ��������֧�ֵ��������ɫ��ΪRGB��ʽ
	public Effect2() {
		supportedInputFormats = new Format[] { new RGBFormat() };
		supportedOutputFormats = new Format[] { new RGBFormat() };
	}

	//ʵ��Effect�ӿڼ̳���Codec�ķ�������������֧�ֵ������ʽ
	public Format[] getSupportedInputFormats() {
		return supportedInputFormats;
	}

	//ʵ��Effect�ӿڼ̳���Codec�ķ�������������֧�ֵ������ʽ
	public Format[] getSupportedOutputFormats(Format parm1) {

		if (parm1 == null)
			return supportedOutputFormats;
		if (!(parm1 instanceof RGBFormat))
			return new Format[0];
		System.out.println("����LGS");
		//����� ����һ�����������
		RGBFormat orf = (RGBFormat) parm1.clone();
		return new Format[] {orf};
	}

	//ʵ��Effect�ӿڼ̳���Codec�ķ��� ���ش�������ݸ�ʽ
	public Format setInputFormat(Format parm1) {
		System.out.println("setInputFormat[input=" + parm1 + "]");
		inputFormat = (RGBFormat) parm1;
		return (Format) inputFormat;
	}

	//ʵ��Effect�ӿڼ̳���Codec�ķ��� ���ش�������ݸ�ʽ
	public Format setOutputFormat(Format parm1) {
		System.out.println("setOutputFormat[output=" + parm1 + "]");
		outputFormat = (RGBFormat) parm1;
		return (Format) outputFormat;

	}

	/**
	 * �÷�����ɱ������Ч������ý������
	 * @param buffer �����ý������
	 * @param buffer1 ������������ý������
	 * @return
	 */
	@Override
	public int process(Buffer buffer, Buffer buffer1) {
		//��ȡ�����ʽ����Ƶ�ֱ���
		Dimension size = ((VideoFormat) buffer.getFormat()).getSize();
		int inWidth = size.width;
		int inHeight = size.height;
		Object srcData = buffer.getData();//��ȡ��������ݶ��������
		if (!(srcData instanceof byte[]))
			return this.BUFFER_PROCESSED_FAILED;
		Object outputData = null;
		if (outputData != null){
			if (!(srcData instanceof Byte[]))
				return this.BUFFER_PROCESSED_FAILED;
		}
		//�����Լ�д�ķ�������� ���ص�������
		outputData = this.hMirror((byte[]) srcData, inWidth, inHeight);
		buffer1.setData(outputData);//��������
		int inLength = buffer.getLength();//�������ݵĳ���
		int inOffset = buffer.getOffset();//ƫ����
		//�������ý�����ݵĸ�ʽ
		buffer1.setFormat(buffer.getFormat());
		//�������ý�����ݵĳ��Ⱥ�ƫ��
		buffer1.setLength(inLength);
		buffer1.setOffset(inOffset);
		//�������ݴ���ɹ������Ϣ
		return this.BUFFER_PROCESSED_OK;
	}

	public String getName() {
		System.out.println("getName");
		return EffectName;
	}

	// Effect�ӿڵĸ����ϲ���
	public void open() throws javax.media.ResourceUnavailableException {
		System.out.println("open");
	}

	public void close() {
		System.out.println("close");

	}

	public void reset() {
		System.out.println("reset");
	}

	public Object[] getControls() {
		System.out.println("getControls");
		return new Controls[0];
	}

	public Object getControl(String parm1) {
		System.out.println("getControl[controlType=" + parm1 + "]");
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
			return null;
		}
	}

	public byte[] hMirror(byte[] srcData, int w, int h) {
		int cx=w/2;
		int cy=h/2;
		int radius=0;
		double theta=0;
		int newx=0;
		int newy=0;
		int newr=0;
		int tx;
		int ty;

		byte[] tempData = (byte[]) srcData.clone();
		int length = srcData.length;
		for (int j = 0; j < h; j++)
		{
	     for (int i = 0; i < w; i++) {
				tx = i-cx;
				ty= j-cy ;
				theta=Math.atan2((double)ty,(double)tx);
				radius=(int)Math.sqrt((double)(tx*tx)+(double)(ty*ty));
				newr=(int)(Math.sqrt((double)radius)*12);
				newx=cx+(int)(newr*Math.cos(theta));
				newy=cy+(int)(newr*Math.sin(theta));
				
				int tempLocation1 = i * 3 + j * w * 3;
				int tempLocation2 = i * 3 + 1 + j * w * 3;
				int tempLocation3 = i * 3 + 2 + j * w * 3;

				int srcLocation1 = newx*3 + newy*w*3;
				int srcLocation2 = newx*3 + 1 + newy*w*3;
				int srcLocation3 = newx*3+ 2 + newy*w*3;

				if ((tempLocation1 <= length) && (tempLocation2 <= length)
						&& (tempLocation3 <= length)
						&& (srcLocation1 <= length) && (srcLocation2 <= length)
						&& (srcLocation3 <= length)) {
					tempData[tempLocation1] = srcData[srcLocation1];
					tempData[tempLocation2] = srcData[srcLocation2];
					tempData[tempLocation3] = srcData[srcLocation3];
				}

			}
		}
		return tempData;
	}

}
