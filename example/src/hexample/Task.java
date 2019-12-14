package hexample;

import java.util.concurrent.RecursiveAction;

public class Task extends RecursiveAction {

	private static final Integer MAX = 20;

	final byte[] srcData;
	final byte[] tempData;
	final int w;
	final int h;

	public int start = 0;
	public int end = 0;
	public int cenX;
	public int cenY;
	public int radius;
	public int tX = 0;
	public int tY = 0;
	public int oldX = 0; // ��x����
	public int oldY = 0; // ��y����
	public double distance = 0;
	public int length;

	public Task(byte[] srcData, byte[] tempData, int w, int h, int start,
			int end) {
		// TODO Auto-generated constructor stub
		this.srcData = srcData;
		this.tempData = tempData;
		this.w = w;
		this.h = h;
		this.start = start;
		this.end = end;

		cenX = w / 2;
		cenY = h / 2;
		radius = Math.max(cenX, cenY);
		length = srcData.length;
	}

	//���ķ��� -����
	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		if (end - start < MAX) {
			for (int index = this.start; index <= this.end; index++) {
				transform(0, index);
			}
		} else {
			invokeAll(new Task(srcData, tempData, w, h, start,
					(start + end) / 2), new Task(srcData, tempData, w, h,
					(start + end) / 2 + 1, end));
		}

	}


	//��ЧЧ�� ����
	private void transform(int i, int j) {
		for (i = 0; i < w; i++) {
			tX = i - cenX;
			tY = j - cenY;
			distance = Math.sqrt(Math.pow(tX, 2) + Math.pow(tY, 2));

			if (distance < radius) {
				oldX = cenX + (int) ((i - cenX) * distance / radius);
				// oldY = cenY + (int) ((j - cenY)*distance/radius);
				oldY = j;
			} else {
				oldX = i;
				oldY = j;
			}

			// ���ڲ�ȡ���ڽ��ҶȲ�ֵ������ֱ���������������˫���Բ�ֵ�����뱣��С������������ǰ��������������Ĳ�ֵ����Ϊ���������ص�Ȩ��
			// ����ά����ת��Ϊһά������±ꡣ3���ֽڶ�Ӧ3����ɫ����
			int tempLocation1 = i * 3 + j * w * 3; // tempLocation1���������һά�±�
			int tempLocation2 = i * 3 + 1 + j * w * 3;
			int tempLocation3 = i * 3 + 2 + j * w * 3;
			int srcLocation1 = oldX * 3 + oldY * w * 3;// srcLocation1�Ǿ������һά�±�
			int srcLocation2 = oldX * 3 + 1 + oldY * w * 3;
			int srcLocation3 = oldX * 3 + 2 + oldY * w * 3;
			// ���ڲ������ڽ��ҶȲ�ֵ������ֱ�ӰѾ�������ɫ����������
			// �������˫���Բ�ֵ��Ҫ�þ������ǰ��������������Ĳ�ֵ���������ɫ�ļ�Ȩ��
			if ((tempLocation1 <= length) && (tempLocation2 <= length)
					&& (tempLocation3 <= length) && (srcLocation1 <= length)
					&& (srcLocation2 <= length) && (srcLocation3 <= length)) {

				tempData[tempLocation1] = srcData[srcLocation1];
				tempData[tempLocation2] = srcData[srcLocation2];
				tempData[tempLocation3] = srcData[srcLocation3];
			}
		}

	}

}
