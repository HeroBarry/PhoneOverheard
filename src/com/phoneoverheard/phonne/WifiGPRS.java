package com.phoneoverheard.phonne;

import java.lang.reflect.Method;
import java.util.List;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiGPRS{
	private final static String TAG = "WifiGPRS";
	private ConnectivityManager mCM;	
	private StringBuffer mStringBuffer = new StringBuffer();
	private List<ScanResult> listResult;
	private ScanResult mScanResult = null;
	// ����WifiManager����
	private WifiManager mWifiManager = null;
	// ����WifiInfo����
	private  WifiInfo mWifiInfo = null;
	// ���������б�
	private List<WifiConfiguration> mWifiConfiguration;
	// ����һ��WifiLock
	WifiLock mWifiLock;
	WriteLog mylog = new WriteLog();

	/**
	 * ���췽��
	 */
	public WifiGPRS(Context context) {
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
		mCM = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	/**
	 * ��Wifi����
	 */
	public void openNetCard() {
		if (!mWifiManager.isWifiEnabled()) {
			Log.i(TAG, "��ʼ��Wifi����");
			mylog.WrLog("i",TAG,"��ʼ��Wifi����");
			long a = System.currentTimeMillis();
			try {
				int i = 0;
				mWifiManager.setWifiEnabled(true);
				while(checkNetCardState()!=3 && i < 60){					
		            Thread.currentThread();
					Thread.sleep(1000);
					i++;
				}
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			long b = System.currentTimeMillis();
			Log.i(TAG, "�Ѿ���Wifi������"+" ��ʱ��"+(b-a)+"����");
			mylog.WrLog("i",TAG,"�Ѿ���Wifi������"+" ��ʱ��"+(b-a)+"����");
		}
	}

	/**
	 * �ر�Wifi����
	 */
	public void closeNetCard() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * ��鵱ǰWifi����״̬
	 */
	public int checkNetCardState() {
		if (mWifiManager.getWifiState() == 0) {
			Log.i(TAG, "�������ڹر�");
			mylog.WrLog("i",TAG,"�������ڹر�");
		} else if (mWifiManager.getWifiState() == 1) {
			Log.i(TAG, "�����Ѿ��ر�");
			mylog.WrLog("i",TAG,"�����Ѿ��ر�");
		} else if (mWifiManager.getWifiState() == 2) {
			Log.i(TAG, "�������ڴ�");
			mylog.WrLog("i",TAG,"�������ڴ�");
		} else if (mWifiManager.getWifiState() == 3) {
			Log.i(TAG, "�����Ѿ���");
			mylog.WrLog("i",TAG,"�����Ѿ���");
		} else {
			Log.i(TAG, "û�л�ȡ��״̬");
			mylog.WrLog("i",TAG,"û�л�ȡ��״̬");
		}
		return mWifiManager.getWifiState();
	}

	/**
	 * ɨ���ܱ�����
	 */
	public boolean scan() {
		Log.i(TAG, "��ʼɨ���ܱ�����");	
		mylog.WrLog("i",TAG,"��ʼɨ���ܱ�����");
		mWifiManager.startScan();
		long a = System.currentTimeMillis();
		try {
			int i = 0;
			while(i < 60 && mWifiManager.getScanResults() == null){	
				if(checkNetCardState() != 3){
					openNetCard();
				}
	            Thread.currentThread();
				Thread.sleep(1000);
				i++;
			}
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		long b = System.currentTimeMillis();
		Log.i(TAG, "ɨ���ܱ�������ɣ�"+" ��ʱ��"+(b-a)+"����");	
		mylog.WrLog("i",TAG, "ɨ���ܱ�������ɣ�"+" ��ʱ��"+(b-a)+"����");
		
		listResult = mWifiManager.getScanResults();
		if (listResult != null) {			
			Log.i(TAG, "��ǰ��������������磬��鿴ɨ����");
			mylog.WrLog("i",TAG, "��ǰ��������������磬��鿴ɨ����");
			return true;
		} else {
			Log.i(TAG, "��ǰ����û����������");
			mylog.WrLog("i",TAG, "��ǰ����û����������");
			return false;
		}
	}

	/**
	 * �õ�ɨ����
	 */
	public String getScanResult() {
		// ÿ�ε��ɨ��֮ǰ�����һ�ε�ɨ����
		if (mStringBuffer != null) {
			mStringBuffer = new StringBuffer();
		}
		// ��ʼɨ������
		scan();
		listResult = mWifiManager.getScanResults();
		if (listResult != null) {
			for (int i = 0; i < listResult.size(); i++) {
				mScanResult = listResult.get(i);
				mStringBuffer = mStringBuffer.append("NO.").append(i + 1)
					.append(" :").append(mScanResult.SSID).append("->")
					.append(mScanResult.BSSID).append("->")
					.append(mScanResult.capabilities).append("->")
					.append(mScanResult.frequency).append("->")
					.append(mScanResult.level).append("->")
					.append(mScanResult.describeContents()).append("\n\n");
			}
		}
		Log.i(TAG, mStringBuffer.toString());
		return mStringBuffer.toString();
	}

	/**
	 * ����ָ������
	 */
	public boolean connect() {
		mWifiInfo = mWifiManager.getConnectionInfo();
		Log.i(TAG, "��ʼ����ָ������");
		mylog.WrLog("i",TAG, "��ʼ����ָ������");
		long a = System.currentTimeMillis();
		int i = 0;
		try {			
			while(i < 10 && mWifiInfo.getSupplicantState().toString() != "COMPLETED"){
	            Thread.currentThread();
				Thread.sleep(1000);
				i++;
				mWifiInfo = mWifiManager.getConnectionInfo();
			}
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		long b = System.currentTimeMillis();
		Log.i(TAG, "����ָ��������ɣ�"+" ��ʱ��"+(b-a)+"����");	
		mylog.WrLog("i",TAG, "����ָ��������ɣ�"+" ��ʱ��"+(b-a)+"����");
		
		if(checkNetWorkState()){
			Log.i(TAG, "���ӳɹ���Wifi������������");
			mylog.WrLog("i",TAG, "���ӳɹ���Wifi������������");
			return true;
		}else if(i >= 10){
			Log.i(TAG, "����Wifi���糬ʱ");
			mylog.WrLog("i",TAG, "����Wifi���糬ʱ");
			return false;
		}else{
			Log.i(TAG, "����Wifi����ʧ��");
			mylog.WrLog("i",TAG, "����Wifi����ʧ��");
			return false;
		}
	}

	/**
	 * �Ͽ���ǰ���ӵ�����
	 */
	public void disconnectWifi() {
		int netId = getNetworkId();
		Log.i(TAG, "�Ͽ�Wifi��ǰ���ӵ����磺NetworkId="+netId);
		mylog.WrLog("i",TAG, "�Ͽ�Wifi��ǰ���ӵ����磺NetworkId="+netId);
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;
	}

	/**
	 * ��鵱ǰWifi����״̬
	 * 
	 * @return String
	 */
	public  boolean checkNetWorkState() {
		Log.i(TAG, "mWifiInfo=="+mWifiInfo);
		mylog.WrLog("i",TAG, "mWifiInfo=="+mWifiInfo);
		if (mWifiInfo != null) {
			if(mWifiInfo.getSupplicantState().toString() == "COMPLETED"){
				return true;
			}else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * �õ����ӵ�ID
	 */
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	/**
	 * �õ�IP��ַ
	 */
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// ����WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// ����WifiLock
	public void releaseWifiLock() {
		// �ж�ʱ������
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// ����һ��WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// ָ�����úõ������������
	public void connectConfiguration(int index) {
		// �����������úõ�������������
		if (index >= mWifiConfiguration.size()) {
			return;
		}
		// �������úõ�ָ��ID������
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}
	
	// �õ����úõ�����
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	// �õ�MAC��ַ
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// �õ�������BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// �õ�WifiInfo��������Ϣ��
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// ���һ�����粢����
	public int addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(mWifiConfiguration.get(3));
		mWifiManager.enableNetwork(wcgID, true);
		return wcgID;
	}
	
	
	/**
	 * ��鲢����GPRS��������
	 */
	public  boolean CheckAndConnectGPRS(){
		boolean ConnectState = false;
		String methodName = "setMobileDataEnabled";
		//�ر�Wifi����
		disconnectWifi();
		// ��GPRS����
		setGprsEnable(methodName, true);
		Log.i(TAG, "GPRS������������");	
		mylog.WrLog("i",TAG,"GPRS������������");
		ConnectState = true;
		return ConnectState;
	}
	
	
	/**
	 * ��鲢����Wifi��������
	 */
	public  boolean CheckAndConnectWifi(){
		boolean ConnectState = false;
		// ���Wifi��������״̬
		if(!checkNetWorkState()){
			Log.i(TAG, "Wifi����δ����");
			mylog.WrLog("i",TAG,"Wifi����δ����");
			// ���WIFI����״̬
			if(checkNetCardState()>1){
				Log.i(TAG, "�����Ѿ��򿪻����ڴ�");
				mylog.WrLog("i",TAG,"�����Ѿ��򿪻����ڴ�");
				// WIFI�����Ǵ�״̬����ʼɨ���ܱ�WIFI����
				if(scan()){
					Log.i(TAG, "��ǰ���������������");
					mylog.WrLog("i",TAG,"��ǰ���������������");
					// ��WIFI��������
					if(connect()){
						Log.i(TAG, "Wifi������������");	
						mylog.WrLog("i",TAG,"Wifi������������");
						ConnectState = true;
					}else{
						Log.i(TAG, "Wifi��������ʧ��");
						mylog.WrLog("i",TAG,"Wifi��������ʧ��");
						ConnectState = false;
					}
				}else{
					Log.i(TAG, "��ǰ����û����������");
					mylog.WrLog("i",TAG,"��ǰ����û����������");
					ConnectState = false;
				}
			}else{
				Log.i(TAG, "�����Ѿ��رջ������ڹر�");
				mylog.WrLog("i",TAG,"�����Ѿ��رջ������ڹر�");
				//WIFI�����ǹر�״̬����WIFI����
				openNetCard();
				CheckAndConnectWifi();  //���¼�鲢������������
			}			
		}else{			
			Log.i(TAG, "Wifi������������");	
			mylog.WrLog("i",TAG,"Wifi������������");
			ConnectState = true;
		}
		return ConnectState;
	}
	
	
	//���GPRS�Ƿ��
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean gprsIsOpenMethod(String methodName){
		Class cmClass 		= mCM.getClass();
		Class[] argClasses 	= null;
		Object[] argObject 	= null;
		Boolean isOpen = false;
		try{
			Method method = cmClass.getMethod(methodName, argClasses);
			isOpen = (Boolean) method.invoke(mCM, argObject);
		} catch (Exception e){
			e.printStackTrace();
		}
		return isOpen;
	}	
	
	/**
	 * ����/�ر�GPRS
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setGprsEnable(String methodName, boolean isEnable){
		Class cmClass 		= mCM.getClass();
		Class[] argClasses 	= new Class[1];
		argClasses[0] 		= boolean.class;
		try{
			Method method = cmClass.getMethod(methodName, argClasses);
			method.invoke(mCM, isEnable);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
