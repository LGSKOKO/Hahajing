package hexample;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.media.*;
import javax.media.control.FrameGrabbingControl;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.SourceCloneable;
import javax.media.util.BufferToImage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import jmapps.util.StateHelper;
import com.sun.media.format.AviVideoFormat;



public class MainWindow extends JFrame{
	

	JPanel jp_select,jp_original,jp_transform,jp_video1,jp_video2;
//	static JPanel jp_video2;
	JLabel jLabel,jl_SDirectory,jl_VDirectory,jl_SFormat,jl_VFormat;
	JButton btn_capture,btn_pause,btn_screenshot,btn_record,btn_set,btn_SDirectory,btn_VDirectory,btn_sure,btn_cutstop,btn_cutstart;
	JComboBox jBox;
	static JComboBox jb_SFormat;
	static JComboBox jb_VFormat;
	JList jList;
	JFrame setFrame,cutFrame;
	static JTextField jf_SDirectory;
	static JTextField jf_VDirectory;//,"鬼影特效"
	Object []show = {"纵向拉长","中心内凹特效","中轴外凸特效","复合特效","鬼影特效","中轴外凸特效并发"};
	static Object []sFormat = {"bmp","png","jpg","gif"};
	static Object []vFormat = {"avi","rm","rmvb","mp4","mov"};
	private static String SDirectory =   "D:\\";//默认图片存放地址
	private static String VDirectory = "D:\\";//默认视频存放地址
	private static String SFormat = "bmp";//默认图片存放格式
	private static String VFormat = "avi";//默认截屏存放格式
	private static int VFormatIndex;
	
	private static Processor originalProcessor=null;//摄像头的原始处理器
//	private static DataSource dataSource;//数据源
	private static DataSource OutputData=null;//originalprocessor的输出流
	private static DataSource cloneData=null;//初始的数据源
	private static Processor videoProcessor=null;//转换处理器
	private static javax.media.Processor captureProcessor;//捕获视频处理器
	private static DataSink dataSink=null;//保存转换视频文件的
	private static StateHelper sh=null;//处理器控制状态
	private static Format suitableFormat = new Format(VideoFormat.RGB);//哈哈镜颜色格式

	private static boolean isStop = false; 
//	private static String sufix=".avi";
	private static AviVideoFormat aviFormat=new AviVideoFormat(VideoFormat.YUV);
	private static String fileTypeDescriptor=FileTypeDescriptor.MSVIDEO;
	//private static CaptureDeviceInfo devInfo=null;// 设备信息
	
	
	//startCapture()   开始捕获
	public  Player player = null;//视频播放器
	public static Player transPlayer = null;
	private CaptureDeviceInfo deviceInfo = null;   //定义管理器的截取设备注册信息
	private MediaLocator mediaLocator = null;//摄像头视频地址
	private static Component component = null;
	private static Component component2 = null;
	//private Component transformComponent = null;
	String str = "vfw:Microsoft WDM Image Capture (Win32):0";    //获取本地摄像头的字符串
	
	//private static DataSink dataSink=null; 
//	public static SetMessage setMessage;
	
	//screenshot()  截屏
	private Buffer buffer;
	private BufferToImage bufferToImage;
	private Image image;
	private FrameGrabbingControl fgc;
	private static int selectEffect = -1;

	//构造函数
	public MainWindow(){
		this.setBounds(150,150,1200,605);//(x,y,width,height)
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(null);

//		jLabel = new JLabel("请选择摄像头设备：");
//		jLabel.setBounds(20, 20, 120, 30);
//		this.add(jLabel);
//
//		jBox = new JComboBox();
//		jBox.setBounds(145, 20, 100, 30);
//		for (int i = 0; i < 3; i++) {
//			jBox.addItem("摄像头设备"+i);
//		}
//		jBox.addItemListener(new ItemListener() {
//
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				// TODO Auto-generated method stub
//			}
//		});
//		this.add(jBox);

        //捕获视频 界面
		btn_capture = new JButton("捕获视频");
		btn_capture.setBounds(290, 20, 90, 30);
		btn_capture.addActionListener(new ActionListener() { //监听事件
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (null != player)
					player.start();
				else
					startCapture();
			}
		});
		this.add(btn_capture);

		//暂停捕获 界面
		btn_pause = new JButton("暂停捕获");
		btn_pause.setBounds(420, 20, 90, 30);
		btn_pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.stop();
				//jp_video1.removeAll();
			}
		});
		this.add(btn_pause);

		//截图 界面
		btn_screenshot = new JButton("截图");
		btn_screenshot.setBounds(550, 20, 90, 30);
		btn_screenshot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				screenShot();
			}
		});
		this.add(btn_screenshot);

		//录制视频 界面
		btn_record = new JButton("录制视频");
		btn_record.setBounds(680, 20, 90, 30);
		btn_record.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Capvideoview();
			}
		});
		this.add(btn_record);

		//设置 界面
		btn_set = new JButton("设置");
		btn_set.setBounds(810, 20, 90, 30);
		btn_set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSetFrame();
			}
		});
		this.add(btn_set);

		//选择特效 界面
	    jp_select = new JPanel();
	    jp_select.setLayout(null);
	    jp_select.setBounds(20, 80, 200, 460);
	    jp_select.setBorder(BorderFactory.createTitledBorder("请选择特效："));
	    this.add(jp_select);

		//JList  选择列表
		jList = new JList(show);
		jList.setBounds(5, 25, 190, 420);
		jList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				selectEffect = jList.getSelectedIndex() + 1;
				transform();
			}
		});
		jp_select.add(jList);

		jp_original = new JPanel();
		jp_original.setLayout(null);
		jp_original.setBounds(220, 80, 470, 460);
		jp_original.setBorder(BorderFactory.createTitledBorder("原始视频："));
	    this.add(jp_original);

	    jp_transform = new JPanel();
	    jp_transform.setLayout(null);
	    jp_transform.setBounds(690, 80, 470, 460);
	    jp_transform.setBorder(BorderFactory.createTitledBorder("变换视频："));
	    this.add(jp_transform);

	    jp_video1 = new JPanel();
	    jp_video1.setLayout(null);
	    jp_video1.setBounds(10, 25, 450, 420);
	    jp_video1.setBorder(BorderFactory.createLineBorder(Color.black, 1));
	    jp_original.add(jp_video1);

	    jp_video2 = new JPanel();
	    jp_video2.setLayout(null);
	    jp_video2.setBounds(10, 25, 450, 420);
	    jp_video2.setBorder(BorderFactory.createLineBorder(Color.black, 1));
	    jp_transform.add(jp_video2);

		this.setVisible(true);
	}


	/**
	 * 开始捕获 方法
	 */
	public void startCapture(){
		deviceInfo = CaptureDeviceManager.getDevice(str);  //根据字符串获取采集设备（摄像头）的引用
		mediaLocator = deviceInfo.getLocator(); //从CaptureDevicesInfo对象中获取MediaLocator对象，然后用它来创建一个DataSource数据源对象；

		Format[] FORMATS = new Format[]{suitableFormat};

		ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(ContentDescriptor.RAW);
		//获取采集设备的定位器的引用，需要根据此引用来创建视频播放器
		 
		try {
//			dataSource =  Manager.createDataSource(mediaLocator);
			originalProcessor = Manager.createRealizedProcessor(new ProcessorModel(mediaLocator,FORMATS,CONTENT_DESCRIPTOR));
			OutputData = originalProcessor.getDataOutput();//摄像头输出形成新数据源
			OutputData =  Manager.createCloneableDataSource(OutputData);
			cloneData = ((SourceCloneable)OutputData).createClone();
			System.out.println(cloneData+"我是clonedata");
//			videoProcessor = Manager.createProcessor(cloneData);
			player = Manager.createRealizedPlayer(OutputData);// 利用数据源 获取一个player ，创建一个player对象
			videoProcessor = Manager.createProcessor(cloneData);
			originalProcessor.start();
			component = player.getVisualComponent(); //player 对象的图像部件，在此部件上可以播放多媒体图像
			component.setBounds(0, 0, 450, 420);
			if (component != null) {
				/* 
		  	设置窗体的一些属性
				 */
				jp_video1.add(component);
		
				player.start();  //播放捕获来的数据流
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 设置窗口 方法
	 */
	public void showSetFrame(){
		//创建新的窗口				
		setFrame = new JFrame("设置窗口");				
		//设置在屏幕的位置				
		setFrame.setLocation(750,320);		
		//窗体大小			
		setFrame.setSize(530,360);
		
		setFrame.setLayout(null);
		
		jl_SDirectory = new JLabel("存储截图目录：");
		jl_SDirectory.setBounds(80, 5, 100, 30);
		setFrame.add(jl_SDirectory);
		
		jf_SDirectory = new JTextField();
		jf_SDirectory.setBounds(80, 40, 240, 30);
		jf_SDirectory.setText(SDirectory);
		jf_SDirectory.setFocusable(false);
		setFrame.add(jf_SDirectory);
		
		btn_SDirectory = new JButton("选择目录");
		btn_SDirectory.setBounds(340, 40, 90, 30);
		setFrame.add(btn_SDirectory);
		btn_SDirectory.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser fileChooser = new JFileChooser("D:\\");
			    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    int returnVal = fileChooser.showOpenDialog(fileChooser);
			    if(returnVal == JFileChooser.APPROVE_OPTION){       
			        String filePath= fileChooser.getSelectedFile().getAbsolutePath();//这个就是选择的文件夹的路径
			        jf_SDirectory.setText(filePath);
			        
			    }
			}
		});
		
		jl_VDirectory = new JLabel("存储视频目录：");
		jl_VDirectory.setBounds(80, 75, 100, 30);
		setFrame.add(jl_VDirectory);
		
		jf_VDirectory = new JTextField();
		jf_VDirectory.setBounds(80, 110, 240, 30);
		jf_VDirectory.setText(VDirectory);
		jf_VDirectory.setFocusable(false);
		setFrame.add(jf_VDirectory);
		
		btn_VDirectory = new JButton("选择目录");
		btn_VDirectory.setBounds(340, 110, 90, 30);
		setFrame.add(btn_VDirectory);
		btn_VDirectory.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser fileChooser = new JFileChooser("D:\\");
			    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    int returnVal = fileChooser.showOpenDialog(fileChooser);
			    if(returnVal == JFileChooser.APPROVE_OPTION){       
			        String filePath= fileChooser.getSelectedFile().getAbsolutePath();//这个就是选择的文件夹的路径
			        jf_VDirectory.setText(filePath);
			    }
			}
		});
		
		jl_SFormat = new JLabel("截图保存格式：");
		jl_SFormat.setBounds(80, 155, 100, 30);
		setFrame.add(jl_SFormat);
		
		jb_SFormat = new JComboBox();
		jb_SFormat.setBounds(190, 155, 100, 30);
		for (int i = 0; i < 4; i++) {
			jb_SFormat.addItem(sFormat[i]);
		}
		jb_SFormat.addItemListener(new ItemListener() {
					
			@Override
			public void itemStateChanged(ItemEvent e) {
				// 
			}
		});
		setFrame.add(jb_SFormat);
		
		jl_VFormat = new JLabel("视频保存格式：");
		jl_VFormat.setBounds(80, 200, 100, 30);
		setFrame.add(jl_VFormat);
		
		jb_VFormat = new JComboBox();
		jb_VFormat.setBounds(190, 200, 100, 30);
		jb_VFormat.setBorder(BorderFactory.createRaisedBevelBorder());
		for (int i = 0; i < 5; i++) {
			jb_VFormat.addItem(vFormat[i]);
			if (vFormat[i].equals(VFormat)) {
				VFormatIndex = i;
			}
		}
		jb_VFormat.setSelectedIndex(VFormatIndex);
		
		jb_VFormat.addItemListener(new ItemListener() {
					
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
			}
		});
		setFrame.add(jb_VFormat);
		
		btn_sure = new JButton("确定");
		btn_sure.setBounds(220, 260, 80, 30);
		btn_sure.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SDirectory = jf_SDirectory.getText();//图片保存路径
				SFormat = (String)jb_SFormat.getSelectedItem();//图片保存格式
				VDirectory = jf_VDirectory.getText();//视频保存路径
				VFormat = (String)jb_VFormat.getSelectedItem();//视频保存格式
				
				try {
					writeObject();//将选择保存在文件中
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					readObject();
				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				setFrame.dispose();
			}
		});
		setFrame.add(btn_sure);

		if (setFrame == null || setFrame.isVisible()==false) {
			//显示窗体			
			setFrame.setVisible(true);
		}
	}


	public static void writeObject()throws IOException{
		 FileOutputStream fos = new FileOutputStream("D:\\data.txt");
         ObjectOutputStream oos = new ObjectOutputStream(fos);
         oos.writeObject(new SetMessage(SDirectory, VDirectory, SFormat, VFormat));
         //oos.writeObject("");
         oos.close();
         
	}
	
	public static void readObject()throws IOException,ClassNotFoundException{
		FileInputStream fis = new FileInputStream("D:\\data.txt");
		if (fis.equals(null)) {
			writeObject();
		}else{
			ObjectInputStream ois = new ObjectInputStream(fis);
	         SetMessage e1 = (SetMessage)ois.readObject();
	         
	         e1.display();
	         ois.close();
		}
         
	}

	//截屏 方法
	public void screenShot(){
		if (null != player) {
			//关键代码
			fgc = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");	
			this.buffer = fgc.grabFrame();
			bufferToImage = new BufferToImage((VideoFormat) buffer.getFormat());	
			image = bufferToImage.createImage(this.buffer);
			
			try {
				String DateFormatString = "yyyy-MM-dd HH.mm.ss";
				SimpleDateFormat sdf = new SimpleDateFormat(DateFormatString);
				String dateString = sdf.format(new Date());
				String savename = dateString + "." + SFormat;
				ImageIO.write((RenderedImage)image, SFormat, new File(SDirectory+"\\"+savename));
				
				System.out.println("截图成功!");
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}	
			
	}

	/**
	 * 录制视频 方法
	 */
   public void Capvideoview() {
    	        //创建新的窗口				
    			cutFrame = new JFrame("录制窗口");							
    			cutFrame.setLocation(500,320);		
    			cutFrame.setSize(530,360);		
    			cutFrame.setLayout(null);
    			
   			    btn_cutstart=new JButton("开始录制");
    			btn_cutstart.setBounds(200, 120, 150, 50);
    			btn_cutstart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String DateFormatString="yyyy-MM-dd HH.mm.ss";
    				SimpleDateFormat sdf=new SimpleDateFormat(DateFormatString);
    				String dataString =sdf.format(new Date());
    				String fileName= VDirectory + "\\" + dataString + "." + VFormat;
					//用来定义视频保存地址
    				MediaLocator dest=new MediaLocator(new java.lang.String("file:///"+fileName));
    				if(player==null){
    					JOptionPane.showMessageDialog(null,"请先打开转换视频！");
    					return ;
    				}
    			
    				cloneData = ((SourceCloneable)OutputData).createClone();
    				
    				try{
    					captureProcessor=Manager.createProcessor(cloneData);
    					sh=new StateHelper(captureProcessor);//事件机制机制实现判断状态
    				}catch(NoProcessorException e1){
    					e1.printStackTrace();
    					return ;
    					
    				}catch(IOException e2){
    					e2.printStackTrace();
    					return ;
    				}
    				if(!sh.configure(1000)){
    					System.out.print("configure wrong!");
    					return;
    				}
    				captureProcessor.setContentDescriptor(new ContentDescriptor(fileTypeDescriptor));
    				TrackControl trackControls[]=captureProcessor.getTrackControls();
    				javax.media.Format formats[]=trackControls[0].getSupportedFormats();
    				if(formats==null||formats.length<=0){
    					throw new UnsupportedOperationException("no uotput format available");
    				}
    				String encoding=aviFormat.getEncoding();
    				javax.media.Format selecteFormat=null;
    				for(javax.media.Format f:formats){
    					if(f.getEncoding().equals(encoding)){
    						selecteFormat=f;
    						break;
    					}
    				}
    		
    				
    		
    				if(selecteFormat==null){
    					throw new UnsupportedOperationException("No output format aelected.");
    				}
    				trackControls[0].setFormat(selecteFormat);
    				if(!sh.realize(1000)){
    					System.out.println("realize wrong!");
    				}
    				try{
    					DataSource capoutsDataSource=captureProcessor.getDataOutput();
    					//jmf提供的 用来保存视频到文件
    					dataSink=Manager.createDataSink(capoutsDataSource, dest);
    					dataSink.open();
    				}catch(NoDataSinkException ez1){
    					ez1.printStackTrace();
    				}catch(IOException ez2){
    					ez2.printStackTrace();
    				}catch(SecurityException ez3){
    					ez3.printStackTrace();
    				}try{
    			  dataSink.start();
    			  captureProcessor.start();
    		   
    				}catch(IOException ez4){
    					ez4.printStackTrace();
    					
    				}

				}		
				
    			
    			});
    			
    		cutFrame.add(btn_cutstart);
    			
    			
    		   btn_cutstop=new JButton("结束录制");
    		   btn_cutstop.setBounds(200,180,150,50);
    		   btn_cutstop.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					captureProcessor.close();
					dataSink.close();
 				    captureProcessor=null;	
				}
    			   
    		   });
    		   cutFrame.add(btn_cutstop);
    		   	
    		   cutFrame.setVisible(true);
    	      	
    	
    }


	/**
	 * 特效 方法
	 */
   public  void transform(){
	   
	   transPlayer = null;
	   component2 = null;
	   try {
		videoProcessor = Manager.createProcessor(cloneData);
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
	   
	   ContentDescriptor CONTENT_DESCRIPOR=new ContentDescriptor(ContentDescriptor.RAW);
	   TrackControl[] tc=null;
	   if(videoProcessor!=null){
			if (!isStop) {
		   component = null;
		  try{
			  //videoProcessor.configure();
			  videoProcessor.configure();
			  while(videoProcessor.getState()!=videoProcessor.Configured&&videoProcessor.getState()!=600){
				  Thread.sleep(1000);
				  System.out.println("Configuring is"+videoProcessor.getState());
			  }
			  System.out.println("Configured is"+videoProcessor.getState());
				  if(videoProcessor.getState()==videoProcessor.Configured){
					  videoProcessor.setContentDescriptor(CONTENT_DESCRIPOR);
					  tc=videoProcessor.getTrackControls();

						if (tc.length > 0) {
							Codec[] cd = new Codec[1];
							// 设置媒体编码器为不同的效果
							if (selectEffect == 1) {
								System.out.println(1);
								cd[0] = new Effect1();
							}
							if (selectEffect == 2) {
								System.out.println(2);
								cd[0] = new Effect2();
							}
							if (selectEffect == 3) {
								cd[0] = new Effect3();
							}
							if (selectEffect == 4) {
								cd[0] = new Effect4();
							}
						   if(selectEffect==5){
							   cd[0]=new Effect5();
						   }
							if(selectEffect==6){
								cd[0]=new Effect6();
							}
							try {
								// 指定一条将在每条媒体踪迹中使用的编码器插件链
								tc[0].setCodecChain(cd);
							} catch (UnsupportedPlugInException err) {
								// TODO: handle exception
								err.toString();
							}
						}

					}
				  videoProcessor.realize();
				  System.out.println("realizing is"+videoProcessor.getState());
				  while(videoProcessor.getState()!=videoProcessor.Realized){
					  Thread.sleep(1000);
				  }
				  System.out.println("Realized is"+videoProcessor.getState());
				  transPlayer = Manager.createRealizedPlayer(videoProcessor.getDataOutput());
				  videoProcessor.start();
				  transPlayer.start();
				  component2 = transPlayer.getVisualComponent();
				  component2.setBounds(0, 0, 450, 420);
					if (component2 != null) {
						jp_video2.removeAll();
						jp_video2.add(component2);
					}

			} catch (Exception e) {
				e.printStackTrace();
			}
	} else {
		JOptionPane.showMessageDialog(null, "捕获视频已暂停,请先开始捕获视频!");
	}

} else {
	JOptionPane.showMessageDialog(null, "请先开始捕获视频!");
      }

}
   
   
   
   public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					new MainWindow();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});

	}
   

}
 