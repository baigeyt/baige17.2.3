package com.hyc.up;

import android.content.Context;

import com.hyc.bean.ImgInfo;

public class UploadFile {



public static void uploadFile(ImgInfo imginfo, Context context,String object,Long time){
		
		UploadRecord uploadRecord = new UploadRecord();
		uploadRecord.upLoadRecord(imginfo, context, "0",object,time);

	}
}
