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
	public int oldX = 0; // 旧x坐标
	public int oldY = 0; // 旧y坐标
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

	//核心方法 -计算
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


	//特效效果 方法
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

			// 由于采取最邻近灰度插值，这里直接求整。如果采用双线性插值，必须保留小数，并计算与前后左右像素坐标的差值，作为计算新像素的权重
			// 将二维坐标转换为一维数组的下标。3个字节对应3个颜色分量
			int tempLocation1 = i * 3 + j * w * 3; // tempLocation1是新坐标的一维下标
			int tempLocation2 = i * 3 + 1 + j * w * 3;
			int tempLocation3 = i * 3 + 2 + j * w * 3;
			int srcLocation1 = oldX * 3 + oldY * w * 3;// srcLocation1是旧坐标的一维下标
			int srcLocation2 = oldX * 3 + 1 + oldY * w * 3;
			int srcLocation3 = oldX * 3 + 2 + oldY * w * 3;
			// 由于采用最邻近灰度插值，这里直接把旧坐标颜色赋给新坐标
			// 如果采用双线性插值，要用旧坐标的前后左右像素坐标的差值与旧坐标颜色的加权和
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
