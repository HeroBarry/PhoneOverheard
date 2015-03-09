package com.phoneoverheard.phonne;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

@SuppressLint({ "WorldWriteableFiles", "WorldReadableFiles" })
public class WriteLog {	
	/**
	 * д��־�ļ�
	 * @param Info ��־��Ϣ
	 * @param filedir ��־�ļ�����Ŀ¼
	 * @param TAG ��־��Դ����־��
	 * @param infoType ��־����
	 */
	@SuppressLint("SimpleDateFormat")
	public void WrLog(String infoType,String TAG,String Info){ 
		String Msg = "";
		String filename = "";	
		String filedir = "/phonne/log/";
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat Timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMdd");
		final String receiveDate = Dateformat.format(date);
		final String receiveTime = Timeformat.format(date);
		//������д��SD���ļ���
	    FileService FileService = new FileService();
	    FileService.getSDPath(filedir);
	    //filedir = Environment.getExternalStorageDirectory()+filedir;//��ȡSDCardĿ¼
	    Log.i(TAG, "��־�ļ�����Ŀ¼:"+filedir);
	    try {
		    boolean sdCardExist = Environment.getExternalStorageState()
					.equals(android.os.Environment.MEDIA_MOUNTED); //�ж�sd���Ƿ����  	
    		if (sdCardExist){ 
    			new FileService().getSDPath(filedir);
    		}
    		if(infoType.equals("i")){
	    		filename = "info"+receiveDate+".log";
	    		Msg = "["+receiveTime+"]["+TAG+"]:"+Info;
	    		Log.i(TAG, Msg);
	    		FileService.saveToSDCard(filedir,filename,Msg);
    		}else if(infoType.equals("w")){
	    		filename = "warn"+receiveDate+".log";
	    		Msg = "["+receiveTime+"]["+TAG+"]:"+Info;
	    		Log.w(TAG, Msg);
	    		FileService.saveToSDCard(filedir,filename,Msg);	    			
    		}else{
	    		filename = "error"+receiveDate+".log";
	    		Msg = "["+receiveTime+"]["+TAG+"]:"+Info;
	    		Log.e(TAG, Msg);
	    		FileService.saveToSDCard(filedir,filename,Msg);	    			
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 	
   

   
}
