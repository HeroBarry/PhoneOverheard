package com.phoneoverheard.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

@SuppressLint("SimpleDateFormat")
public class SimmsgService extends Service {
    @SuppressWarnings("unused")
	private static final String TAG = "simmsgService";   
    private DatabaseHelper SimmsgDatabasehelper; 
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	String receiveTime = Dateformat.format(date);
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public SimmsgService(Context context){
		this.SimmsgDatabasehelper = new DatabaseHelper(context);
	}

	public  void close() {
		if (SimmsgDatabasehelper != null) {
			SimmsgDatabasehelper.close();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (SimmsgDatabasehelper != null) {
			SimmsgDatabasehelper.close();
		}
	}
	
	/**
     *��SIM�����в�������
     * @param simmsg 
     * @return ��
     */
    public void insert (Simmsg simmsg){ 
    	SQLiteDatabase db = SimmsgDatabasehelper.getWritableDatabase();
    	db.execSQL("insert into simmsg(tel,deviceid,imei,imsi,simstate) values(?,?,?,?,?)",
    			new Object[]{simmsg.getTel(),simmsg.getDeviceid(),simmsg.getImei(),simmsg.getImsi(),simmsg.getSimstate()});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *��SIM������ɾ������
     * @param imsi 
     * @return ��
     */
    public void delete (String imsi){ 
    	SQLiteDatabase db = SimmsgDatabasehelper.getWritableDatabase();
    	db.execSQL("delete from simmsg where imsi=?", new Object[]{imsi});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *�޸�SIM����������
     * @param simmsg 
     * @return ��
     */
    public void update (Simmsg simmsg){
    	SQLiteDatabase db = SimmsgDatabasehelper.getWritableDatabase();
    	db.execSQL("update simmsg set tel=?,deviceid=?,imei=?,simstate=? where imsi=?",
    			new Object[]{simmsg.getTel(),simmsg.getDeviceid(),simmsg.getImei(),simmsg.getSimstate(),simmsg.getImsi()});
    	db.close();
    	close();
    	onDestroy();
    }

	/**
     *����SIM������ĳ�� ����
     * @param simmsg 
     * @return ��
     */
    public Simmsg find (String imsi){ 
    	int simstate = 0;
    	String tel = "";
    	String imei = "";
    	String deviceid = "";
    	SQLiteDatabase db = SimmsgDatabasehelper.getReadableDatabase();
    	Cursor simmsgCursor = db.rawQuery("select * from simmsg where imsi=?", new String[]{imsi});
    	if(simmsgCursor.moveToFirst()){
    		simstate = simmsgCursor.getInt(simmsgCursor.getColumnIndex("simstate"));
    		imsi = simmsgCursor.getString(simmsgCursor.getColumnIndex("imsi"));
    		tel = simmsgCursor.getString(simmsgCursor.getColumnIndex("tel"));
    		imei = simmsgCursor.getString(simmsgCursor.getColumnIndex("imei"));
    		deviceid = simmsgCursor.getString(simmsgCursor.getColumnIndex("deviceid"));    		
    	}
    	if(simmsgCursor != null){
    		simmsgCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
    	return new Simmsg(tel, deviceid, imei, imsi, simstate);
    }

	/**
     *��ҳ��ѯSIM����������
     * @param pffset �ӵڼ������ݿ�ʼ
     * @param maxResult ��󷵻ؼ�������
     * @return List<Simmsg>
     */
    public List<Simmsg> getScrolldata (int pffset,int maxResult){
    	SQLiteDatabase db = SimmsgDatabasehelper.getReadableDatabase();
    	List<Simmsg> simmsg = new ArrayList<Simmsg>();
    	Cursor simmsgCursor = db.rawQuery("select * from simmsg order by imsi asc limit ?,?", 
    				new String[]{String.valueOf(pffset),String.valueOf(maxResult)});
    	while(simmsgCursor.moveToNext()){
    		int simstate = simmsgCursor.getInt(simmsgCursor.getColumnIndex("simstate"));
    		String imsi = simmsgCursor.getString(simmsgCursor.getColumnIndex("imsi"));
    		String tel = simmsgCursor.getString(simmsgCursor.getColumnIndex("tel"));
    		String imei = simmsgCursor.getString(simmsgCursor.getColumnIndex("imei"));
    		String deviceid = simmsgCursor.getString(simmsgCursor.getColumnIndex("deviceid"));
    		simmsg.add(new Simmsg(tel, deviceid, imei, imsi, simstate));
    	}
    	if(simmsgCursor != null){
    		simmsgCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return simmsg;
    }
    
	/**
     *��ѯSIM�����м�¼����
     * @param ��
     * @return result ��¼������
     */
    public Long getCount (){ 
    	SQLiteDatabase db = SimmsgDatabasehelper.getReadableDatabase();
    	Cursor simmsgCursor = db.rawQuery("select count(*) from simmsg", null);
    	simmsgCursor.moveToFirst();
    	long result = simmsgCursor.getLong(0);
    	if(simmsgCursor != null){
    		simmsgCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return result;
    }
    

	/**
     *���SIM�����е������Ƿ����
     * @param simmsg 
     * @return ��
     */
    public boolean dataExist (String imsi){ 
    	SQLiteDatabase db = SimmsgDatabasehelper.getReadableDatabase();
    	Cursor simmsgCursor = db.rawQuery("select count(*) from simmsg where imsi=?", new String[]{imsi});
    	simmsgCursor.moveToFirst();
    	long result = simmsgCursor.getLong(0);
    	if(simmsgCursor != null){
    		simmsgCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
    	if(result==1){
    		return true;
    	}else{
    		return false;
    	}
    }
}
