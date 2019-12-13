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
	static JTextField jf_VDirectory;//,"��Ӱ��Ч"
	Object []show = {"��������","�����ڰ���Ч","������͹��Ч","������Ч","��Ӱ��Ч","������͹��Ч����"};
	static Object []sFormat = {"bmp","png","jpg","gif"};
	static Object []vFormat = {"avi","rm","rmvb","mp4","mov"};
	private static String SDirectory =   "D:\\";//Ĭ��ͼƬ��ŵ�ַ
	private static String VDirectory = "D:\\";//Ĭ����Ƶ��ŵ�ַ
	private static String SFormat = "bmp";//Ĭ��ͼƬ��Ÿ�ʽ
	private static String VFormat = "avi";//Ĭ�Ͻ�����Ÿ�ʽ
	private static int VFormatIndex;
	
	private static Processor originalProcessor=null;//����ͷ��ԭʼ������
//	private static DataSource dataSource;//����Դ
	private static DataSource OutputData=null;//originalprocessor�������
	private static DataSource cloneData=null;//��ʼ������Դ
	private static Processor videoProcessor=null;//ת��������
	private static javax.media.Processor captureProcessor;//������Ƶ������
	private static DataSink dataSink=null;//����ת����Ƶ�ļ���
	private static StateHelper sh=null;//����������״̬
	private static Format suitableFormat = new Format(VideoFormat.RGB);//��������ɫ��ʽ

	private static boolean isStop = false; 
//	private static String sufix=".avi";
	private static AviVideoFormat aviFormat=new AviVideoFormat(VideoFormat.YUV);
	private static String fileTypeDescriptor=FileTypeDescriptor.MSVIDEO;
	//private static CaptureDeviceInfo devInfo=null;// �豸��Ϣ
	
	
	//startCapture()   ��ʼ����
	public  Player player = null;//��Ƶ������
	public static Player transPlayer = null;
	private CaptureDeviceInfo deviceInfo = null;   //����������Ľ�ȡ�豸ע����Ϣ
	private MediaLocator mediaLocator = null;//����ͷ��Ƶ��ַ
	private static Component component = null;
	private static Component component2 = null;
	//private Component transformComponent = null;
	String str = "vfw:Microsoft WDM Image Capture (Win32):0";    //��ȡ��������ͷ���ַ���
	
	//private static DataSink dataSink=null; 
//	public static SetMessage setMessage;
	
	//screenshot()  ����
	private Buffer buffer;
	private BufferToImage bufferToImage;
	private Image image;
	private FrameGrabbingControl fgc;
	private static int selectEffect = -1;

	//���캯��
	public MainWindow(){
		this.setBounds(150,150,1200,605);//(x,y,width,height)
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(null);

//		jLabel = new JLabel("��ѡ������ͷ�豸��");
//		jLabel.setBounds(20, 20, 120, 30);
//		this.add(jLabel);
//
//		jBox = new JComboBox();
//		jBox.setBounds(145, 20, 100, 30);
//		for (int i = 0; i < 3; i++) {
//			jBox.addItem("����ͷ�豸"+i);
//		}
//		jBox.addItemListener(new ItemListener() {
//
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				// TODO Auto-generated method stub
//			}
//		});
//		this.add(jBox);

        //������Ƶ ����
		btn_capture = new JButton("������Ƶ");
		btn_capture.setBounds(290, 20, 90, 30);
		btn_capture.addActionListener(new ActionListener() { //�����¼�
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

		//��ͣ���� ����
		btn_pause = new JButton("��ͣ����");
		btn_pause.setBounds(420, 20, 90, 30);
		btn_pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.stop();
				//jp_video1.removeAll();
			}
		});
		this.add(btn_pause);

		//��ͼ ����
		btn_screenshot = new JButton("��ͼ");
		btn_screenshot.setBounds(550, 20, 90, 30);
		btn_screenshot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				screenShot();
			}
		});
		this.add(btn_screenshot);

		//¼����Ƶ ����
		btn_record = new JButton("¼����Ƶ");
		btn_record.setBounds(680, 20, 90, 30);
		btn_record.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Capvideoview();
			}
		});
		this.add(btn_record);

		//���� ����
		btn_set = new JButton("����");
		btn_set.setBounds(810, 20, 90, 30);
		btn_set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSetFrame();
			}
		});
		this.add(btn_set);

		//ѡ����Ч ����
	    jp_select = new JPanel();
	    jp_select.setLayout(null);
	    jp_select.setBounds(20, 80, 200, 460);
	    jp_select.setBorder(BorderFactory.createTitledBorder("��ѡ����Ч��"));
	    this.add(jp_select);

		//JList  ѡ���б�
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
		jp_original.setBorder(BorderFactory.createTitledBorder("ԭʼ��Ƶ��"));
	    this.add(jp_original);

	    jp_transform = new JPanel();
	    jp_transform.setLayout(null);
	    jp_transform.setBounds(690, 80, 470, 460);
	    jp_transform.setBorder(BorderFactory.createTitledBorder("�任��Ƶ��"));
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
	 * ��ʼ���� ����
	 */
	public void startCapture(){
		deviceInfo = CaptureDeviceManager.getDevice(str);  //�����ַ�����ȡ�ɼ��豸������ͷ��������
		mediaLocator = deviceInfo.getLocator(); //��CaptureDevicesInfo�����л�ȡMediaLocator����Ȼ������������һ��DataSource����Դ����

		Format[] FORMATS = new Format[]{suitableFormat};

		ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(ContentDescriptor.RAW);
		//��ȡ�ɼ��豸�Ķ�λ�������ã���Ҫ���ݴ�������������Ƶ������
		 
		try {
//			dataSource =  Manager.createDataSource(mediaLocator);
			originalProcessor = Manager.createRealizedProcessor(new ProcessorModel(mediaLocator,FORMATS,CONTENT_DESCRIPTOR));
			OutputData = originalProcessor.getDataOutput();//����ͷ����γ�������Դ
			OutputData =  Manager.createCloneableDataSource(OutputData);
			cloneData = ((SourceCloneable)OutputData).createClone();
			System.out.println(cloneData+"����clonedata");
//			videoProcessor = Manager.createProcessor(cloneData);
			player = Manager.createRealizedPlayer(OutputData);// ��������Դ ��ȡһ��player ������һ��player����
			videoProcessor = Manager.createProcessor(cloneData);
			originalProcessor.start();
			component = player.getVisualComponent(); //player �����ͼ�񲿼����ڴ˲����Ͽ��Բ��Ŷ�ý��ͼ��
			component.setBounds(0, 0, 450, 420);
			if (component != null) {
				/* 
		  	���ô����һЩ����
				 */
				jp_video1.add(component);
		
				player.start();  //���Ų�������������
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * ���ô��� ����
	 */
	public void showSetFrame(){
		//�����µĴ���				
		setFrame = new JFrame("���ô���");				
		//��������Ļ��λ��				
		setFrame.setLocation(750,320);		
		//�����С			
		setFrame.setSize(530,360);
		
		setFrame.setLayout(null);
		
		jl_SDirectory = new JLabel("�洢��ͼĿ¼��");
		jl_SDirectory.setBounds(80, 5, 100, 30);
		setFrame.add(jl_SDirectory);
		
		jf_SDirectory = new JTextField();
		jf_SDirectory.setBounds(80, 40, 240, 30);
		jf_SDirectory.setText(SDirectory);
		jf_SDirectory.setFocusable(false);
		setFrame.add(jf_SDirectory);
		
		btn_SDirectory = new JButton("ѡ��Ŀ¼");
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
			        String filePath= fileChooser.getSelectedFile().getAbsolutePath();//�������ѡ����ļ��е�·��
			        jf_SDirectory.setText(filePath);
			        
			    }
			}
		});
		
		jl_VDirectory = new JLabel("�洢��ƵĿ¼��");
		jl_VDirectory.setBounds(80, 75, 100, 30);
		setFrame.add(jl_VDirectory);
		
		jf_VDirectory = new JTextField();
		jf_VDirectory.setBounds(80, 110, 240, 30);
		jf_VDirectory.setText(VDirectory);
		jf_VDirectory.setFocusable(false);
		setFrame.add(jf_VDirectory);
		
		btn_VDirectory = new JButton("ѡ��Ŀ¼");
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
			        String filePath= fileChooser.getSelectedFile().getAbsolutePath();//�������ѡ����ļ��е�·��
			        jf_VDirectory.setText(filePath);
			    }
			}
		});
		
		jl_SFormat = new JLabel("��ͼ�����ʽ��");
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
		
		jl_VFormat = new JLabel("��Ƶ�����ʽ��");
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
		
		btn_sure = new JButton("ȷ��");
		btn_sure.setBounds(220, 260, 80, 30);
		btn_sure.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SDirectory = jf_SDirectory.getText();//ͼƬ����·��
				SFormat = (String)jb_SFormat.getSelectedItem();//ͼƬ�����ʽ
				VDirectory = jf_VDirectory.getText();//��Ƶ����·��
				VFormat = (String)jb_VFormat.getSelectedItem();//��Ƶ�����ʽ
				
				try {
					writeObject();//��ѡ�񱣴����ļ���
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
			//��ʾ����			
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

	//���� ����
	public void screenShot(){
		if (null != player) {
			//�ؼ�����
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
				
				System.out.println("��ͼ�ɹ�!");
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}	
			
	}

	/**
	 * ¼����Ƶ ����
	 */
   public void Capvideoview() {
    	        //�����µĴ���				
    			cutFrame = new JFrame("¼�ƴ���");							
    			cutFrame.setLocation(500,320);		
    			cutFrame.setSize(530,360);		
    			cutFrame.setLayout(null);
    			
   			    btn_cutstart=new JButton("��ʼ¼��");
    			btn_cutstart.setBounds(200, 120, 150, 50);
    			btn_cutstart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String DateFormatString="yyyy-MM-dd HH.mm.ss";
    				SimpleDateFormat sdf=new SimpleDateFormat(DateFormatString);
    				String dataString =sdf.format(new Date());
    				String fileName= VDirectory + "\\" + dataString + "." + VFormat;
					//����������Ƶ�����ַ
    				MediaLocator dest=new MediaLocator(new java.lang.String("file:///"+fileName));
    				if(player==null){
    					JOptionPane.showMessageDialog(null,"���ȴ�ת����Ƶ��");
    					return ;
    				}
    			
    				cloneData = ((SourceCloneable)OutputData).createClone();
    				
    				try{
    					captureProcessor=Manager.createProcessor(cloneData);
    					sh=new StateHelper(captureProcessor);//�¼����ƻ���ʵ���ж�״̬
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
    					//jmf�ṩ�� ����������Ƶ���ļ�
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
    			
    			
    		   btn_cutstop=new JButton("����¼��");
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
	 * ��Ч ����
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
							// ����ý�������Ϊ��ͬ��Ч��
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
								// ָ��һ������ÿ��ý���ټ���ʹ�õı����������
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
		JOptionPane.showMessageDialog(null, "������Ƶ����ͣ,���ȿ�ʼ������Ƶ!");
	}

} else {
	JOptionPane.showMessageDialog(null, "���ȿ�ʼ������Ƶ!");
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
 