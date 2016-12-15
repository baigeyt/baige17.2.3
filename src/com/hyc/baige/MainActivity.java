package com.hyc.baige;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.extend.CardMd5;
import com.extend.DataString;
import com.extend.DeleteFilePic;
import com.extend.InstallAPK;
import com.extend.PicDispose;
import com.hyc.bean.APKInfo;
import com.hyc.bean.Company;
import com.hyc.bean.ICCardTime;
import com.hyc.bean.ImgInfo;
import com.hyc.bean.MacEntity;
import com.hyc.bean.NameClass;
import com.hyc.bean.Stu;
import com.hyc.db.DBMacAddress;
import com.hyc.db.DBManagerAdvert;
import com.hyc.db.DBManagerCard;
import com.hyc.db.DBManagerCompany;
import com.hyc.db.DBManagerICCardTime;
import com.hyc.db.DBManagerSchPic;
import com.hyc.db.DBManagerStu;
import com.hyc.db.Db;
import com.hyc.network.GetDeviceID;
import com.hyc.network.IsNetWork;
import com.hyc.network.NetReceiver;
import com.hyc.rec.RecICCardTime;
import com.hyc.rec.RecOneCard;
import com.hyc.rec.RecSchoolInfo;
import com.hyc.rec.RecVerSionAPK;
import com.hyc.rec.RequestDataOSS;
import com.hyc.rec.ResInstallAPK;
import com.hyc.up.OSSSample;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends Activity {
	// TODO �����ǿ�����Ҫ�ĵĵط�
	// ------------------------------------------
	DBManagerStu dbManagerstu = new DBManagerStu();
	DBManagerCard dbManagercard = new DBManagerCard();
	DBManagerAdvert dBManageradvert = new DBManagerAdvert();
	DBManagerSchPic dBManagerSchPic = new DBManagerSchPic();
	private String name = null;
	private String classname = null;
	private Context context;
	private EchoThread echoThread = new EchoThread();
	private int client_num;
	private int allcount = 0;
	private int card_count = 0;
	public static int upallcount = 0;

	private int count = 0;

	private boolean noOss = false;
	public static String accesstoken;

	private Broad broad;

	private boolean isProducePic = true;
	private boolean school = true;
	private ServerS servers;
	private ServerSocket server;
	private String IP = null;
	private String serverAddress;
	private Db db;
	private DBMacAddress dbMac;
	private SQLiteDatabase dbWriter, dbReader;
	private List<String> paths;

	private int SchoolID = 0;
	public static Intent intent;
	private OSS oss;
	private List<String> list = new ArrayList<String>();
	public static final String action = "jason.broadcast.action";
	private ImgInfo mImginfo;
	private String parment;
	// ------------------------------------------
	private ImageView imageView1, imageView2, imageView3, imageView4,
			imageView5, imageView6, imageView7, imageView8, imageLOGO;
	private TextView textView1_1, textView1_2, textView2_1, textView2_2,
			textView3_1, textView3_2, textView4_1, textView4_2, textView5_1,
			textView5_2, textView6_1, textView6_2, textView7_1, textView7_2,
			textView8_1, textView8_2, textConnect, textIntroduce, textAllcount,
			textAAA;
	private Timer timercont;
	public static Timer timerAd;
	private Timer timer, timercardno,timerTwoUp;
	private TimerTask task, taskcont,taskTwoUp;
	// ------------------------------------------
	private int imgvCount = 0;
	int number = 0;
	private long tt = 4294967295L;
	// ------------------------------------------

	public static double latitude = 39.9;
	public static double longitude = 116.3;
	private IsNetWork work = new IsNetWork();
	public static int state = 0;
	public static TimerTask timerTask;


	// �洢ѧУ��Ϣ
	DBManagerCompany dbCompany = new DBManagerCompany();
	Company company = new Company();

	// ʱ�����������
	private static final int msgKey1 = 1;
	private TextView mTime;

	// ���̳�
	private ExecutorService mExecutorService = null;
	private ThreadPoolExecutor threadPoolSoc = null;
	
	//һ���ϴ����̳߳�
	private ExecutorService mUploadService = null;
	private ThreadPoolExecutor threadPool = null;

	// �����鿨����
	private ProgressDialog progressDialog;

	// �Զ���ˢ��ʱ��
	DBManagerICCardTime dbCardTime;
	List<ICCardTime> cardTimelist = null;
	int timeIdentify = 0;

	// uphandler��Ŀ
	private int upNum = 0;
	private int nameMum = 0;
	
	//����㲥
	private ConnectivityManager mConnectivityManager;    
	private NetworkInfo netInfo;  
	private boolean isNetWorkNormal = false;

	// ------------------------------------------
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ��TITLE
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ����ȫ����ʾ
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// ȥ�������(API������ڻ����14)
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

		setContentView(R.layout.activity_main);
		progressDialog = new ProgressDialog(this);
		registerUpdate();

		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(this));

		db = new Db(MainActivity.this);
		dbWriter = db.getWritableDatabase();
		dbReader = db.getReadableDatabase();
		db.insertTwo("0");
		dbWriter.delete("filepaths", null, null);
		dbWriter.delete("allpaths", null, null);
		dbManagercard.creatDB();
		dBManagerSchPic.creatDB();
		dbManagerstu.creatDB();
		paths = new ArrayList<String>();

		imageView1 = (ImageView) this.findViewById(R.id.imageView1);
		imageView2 = (ImageView) this.findViewById(R.id.imageView2);
		imageView3 = (ImageView) this.findViewById(R.id.imageView3);
		imageView4 = (ImageView) this.findViewById(R.id.imageView4);
		imageView5 = (ImageView) this.findViewById(R.id.imageView5);
		imageView6 = (ImageView) this.findViewById(R.id.imageView6);
		imageView7 = (ImageView) this.findViewById(R.id.imageView7);
		imageView8 = (ImageView) this.findViewById(R.id.imageView8);
		imageLOGO = (ImageView) this.findViewById(R.id.school_LOGO);

		textView1_1 = (TextView) this.findViewById(R.id.textView1_1);
		textView1_2 = (TextView) this.findViewById(R.id.textView1_2);
		textView2_1 = (TextView) this.findViewById(R.id.textView2_1);
		textView2_2 = (TextView) this.findViewById(R.id.textView2_2);
		textView3_1 = (TextView) this.findViewById(R.id.textView3_1);
		textView3_2 = (TextView) this.findViewById(R.id.textView3_2);
		textView4_1 = (TextView) this.findViewById(R.id.textView4_1);
		textView4_2 = (TextView) this.findViewById(R.id.textView4_2);
		textView5_1 = (TextView) this.findViewById(R.id.textView5_1);
		textView5_2 = (TextView) this.findViewById(R.id.textView5_2);
		textView6_1 = (TextView) this.findViewById(R.id.textView6_1);
		textView6_2 = (TextView) this.findViewById(R.id.textView6_2);
		textView7_1 = (TextView) this.findViewById(R.id.textView7_1);
		textView7_2 = (TextView) this.findViewById(R.id.textView7_2);
		textView8_1 = (TextView) this.findViewById(R.id.textView8_1);
		textView8_2 = (TextView) this.findViewById(R.id.textView8_2);

		textConnect = (TextView) this.findViewById(R.id.textConnect);
		textIntroduce = (TextView) this.findViewById(R.id.textIntroduce);
		textAllcount = (TextView) this.findViewById(R.id.allcount);
		textAAA = (TextView) this.findViewById(R.id.aaaa);

		// ��ʾʱ�䣬���ڣ�����
		mTime = (TextView) findViewById(R.id.mytime);
		mTime.setText(DataString.StringData());

		PicDispose.copyToSD(MainActivity.this);
		
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				noOss = true;
				dbCompany.creatDB();

				RecVerSionAPK recVerSionAPK = new RecVerSionAPK();
				APKInfo aInfo = recVerSionAPK.getAPKVersion(MainActivity.this);

				// ���ط����apk
				if (aInfo != null) {
					if (aInfo.getApkUrl() != null) {
						ResInstallAPK resInstallAPK = new ResInstallAPK();
						String path = aInfo.getApkUrl();
						resInstallAPK.getFileFromServer(path, schooleInfo);
					}
				}

				// ��ȡ�Զ���ˢ��ʱ��
				RecICCardTime recICCardTime = new RecICCardTime();
				recICCardTime.recCardTime(mHandler);

				// ��ȡѧУ���ֽ���
				RecSchoolInfo recSchoolInfo = new RecSchoolInfo();
				recSchoolInfo.receiveSchInfo();

				// ֪ͨ���߳�
				Message wthInfomsg = new Message();
				wthInfomsg.what = 1;
				MainActivity.this.weatherHandler.sendMessageDelayed(wthInfomsg,
						3000);

				try {
					Thread.sleep(5000);
					// ��ѧУ��Ϣ�������ݿ�
					company = dbCompany.query();
					if (company.getName() == null) {
						System.out.println("���ѧУ��Ϣ�����ݿ�");
						if (RecSchoolInfo.schoolName != null) {
							company.setSchoolid(RecSchoolInfo.logoId);
							company.setName(RecSchoolInfo.schoolName);
							company.setQq(RecSchoolInfo.qq);
							company.setMobile(RecSchoolInfo.mobile);
							company.setEmail(RecSchoolInfo.email);
							company.setProvince(RecSchoolInfo.province);
							company.setCity(RecSchoolInfo.city);
							company.setDistrict(RecSchoolInfo.district);
							company.setAddress(RecSchoolInfo.address);
							company.setContent(RecSchoolInfo.content);
							dbCompany.insert(company);
						}
					} else if (RecSchoolInfo.schoolName == null) {
						if (company.getName() != null) {
							Message msg = new Message();
							msg.what = 3;
							MainActivity.this.weatherHandler.sendMessage(msg);
						}
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		timer.schedule(task, 0, 1800000);

		initTimer();
		twoUpTimer();

		timercont = new Timer();
		taskcont = new TimerTask() {
			@Override
			public void run() {
				DeleteFilePic.deletelistFiles(new File(getDir()
						+ "/baige/picFile"));
				DeleteFilePic.deletelistFiles(new File(getDir()
						+ "/baige/twoFile"));
				servers = new ServerS();
				servers.start();
			}
		};
		timercont.schedule(taskcont, 3000);

		// ����ˢ�����ʱ�� ��բ/�п�60��
		timercardno = new Timer();
		TimerTask task_cardno = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (timeIdentify == 0) {
					dbManagercard.delete();
				}
				// ʵʱ���½����ϵ���ʾʱ��
				Message msg = new Message();
				msg.what = msgKey1;
				mHandler.sendMessage(msg);

				if (DataString.StringData2().equals("23:59")) {
					Message clear_allcount = new Message();
					clear_allcount.what = 456;
					mHandler.sendMessage(clear_allcount);
				}

				// �жϵ�ǰʱ���Ƿ����Զ���ˢ��ʱ����
				if (cardTimelist != null) {
					if(cardTimelist.size()>0){
						if (new DataString().isSwipingCard(cardTimelist)) {
							// ����Ϣ��socket�Ͽ����ӣ��ر�ˢ������
							Log.v("dd", "��ˢ��");
							mHandler.sendEmptyMessage(110);
						} else {
							// ����Ϣ��socket�����ӣ�����ˢ������
							Log.v("dd", "�ر�ˢ��");
							mHandler.sendEmptyMessage(111);

						}	
					}else{
						Log.v("dd", "��ˢ��aaabbb");
						timeIdentify = 0;
					}
				}else{
					Log.v("dd", "��ˢ��aaa");
					timeIdentify = 0;
				}

				if (oss == null && new IsNetWork().isNetWork()) {
					Message oss_init = new Message();
					oss_init.what = 4;
					weatherHandler.sendMessage(oss_init);
				}
			}
		};
		timercardno.schedule(task_cardno, 60000, 60000);

		IntentFilter filter = new IntentFilter(action);
		registerReceiver(broadcastReceiver, filter);

		// �Զ���ˢ��ʱ��
//		cardTimelist = new ArrayList<ICCardTime>();
		dbCardTime = new DBManagerICCardTime();
		dbCardTime.creatDB();
//		cardTimelist = dbCardTime.query();

	}// onCreate �����Y��

	@SuppressLint("HandlerLeak")
	Handler weatherHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// setControls();
				textIntroduce.setText(RecSchoolInfo.schoolName + "\r\n��ϵ��ʽ��"
						+ "QQ:" + RecSchoolInfo.qq + "\t�绰��"
						+ RecSchoolInfo.mobile + "\tEmail��"
						+ RecSchoolInfo.email + "\r\nѧУ��ַ��"
						+ RecSchoolInfo.province + RecSchoolInfo.city
						+ RecSchoolInfo.district + RecSchoolInfo.address);

				// ��ʾѧУLOGO
				File file = new File(Environment.getExternalStorageDirectory()
						+ "/baige/LOGOFile/0.jpg");
				if (file.exists()) {
					imageLOGO.setImageURI(Uri.fromFile(file));
				}

				break;
			case 3:
				Log.v("dddd", "û���� ȥ������ѧУ��Ϣ");
				company = dbCompany.query();
				textIntroduce.setText(company.getName() + "\r\n��ϵ��ʽ��" + "QQ:"
						+ company.getQq() + "\t�绰��" + company.getMobile()
						+ "\tEmail��" + company.getEmail() + "\r\nѧУ��ַ��"
						+ company.getProvince() + company.getCity()
						+ company.getDistrict() + company.getAddress());
				break;
			case 4:
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						RecSchoolInfo recSchoolInfo = new RecSchoolInfo();
						recSchoolInfo.receiveSchInfo();
						return null;
					}

					protected void onPostExecute(Void result) {
						SchoolID = RecSchoolInfo.Id;
						if (SchoolID != 0 && String.valueOf(SchoolID) != null) {

							getOSSCertificate();
						} else {

							// ��һ������apkû���� �ӱ������ݿ��ȡѧУID
							if (dbCompany.query() != null) {
								company = dbCompany.query();
								int number = 0;

								number = company.getSchoolid();
								if (number != 0) {
									SchoolID = company.getSchoolid();
									getOSSCertificate();
								}
							}

						}
					};
				}.execute();
				break;
			}
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case msgKey1:
				// ����������ʱ��
				mTime.setText(DataString.StringData());
				break;
			case 456:
				// ��ʱ���ˢ�������ϴ���
				upallcount = 0;
				allcount = 0;
				textAllcount.setText("ˢ���� : " + allcount);
				textAAA.setText("�ϴ��� : " + upallcount);
				break;
			case 120:
				textConnect.setText("����״̬��" + "\n" + "������");
				isNetWorkNormal = true;
				break;
			case 121:
				textConnect.setText("����״̬��" + "\n" + "δ����");
				isNetWorkNormal = false;
				break;
			case 222:
				// �Զ�������ˢ��ʱ��
				cardTimelist = dbCardTime.query();
				break;
			case 111:
				timeIdentify = 1;
				break;
			case 110:
				timeIdentify = 0;
				break;
			case 77:
				Toast.makeText(MainActivity.this, "����λ������ȷ", Toast.LENGTH_SHORT)
						.show();
				break;
			case 88:
				Toast.makeText(MainActivity.this, "�쳣",
						Toast.LENGTH_SHORT).show();
				break;
			case 99:
				Toast.makeText(MainActivity.this, "���ӳ�ʱ���쳣��",
						Toast.LENGTH_SHORT).show();
				break;
			default:

				break;
			}
		}
	};

	public void getOSSCertificate() {

		// ��ʼ��OSS
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				list = new RequestDataOSS().uploadFileOss(MainActivity.this,
						String.valueOf(SchoolID));
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				new Thread() {
					public void run() {
						if (work.isNetWork() && list.size() == 3) {

							OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
									list.get(0), list.get(1), list.get(2));
							
							ClientConfiguration ccf = new ClientConfiguration();
							ccf.setConnectionTimeout(3000);
							ccf.setSocketTimeout(3000);
							oss = new OSSClient(MainActivity.this,
									"http://oss-bj.360baige.cn",
									credentialProvider,ccf);
							try {
								String url = oss.presignConstrainedObjectURL(
										"sdk-baige", "logo.jpg", 3600);
								System.out
										.println(url
												+ "oooooooooooooooooooooookkkkkkkkkkkkk");
							} catch (ClientException e) {
								// TODO Auto-generated catch
								// block
								e.printStackTrace();
							}
						}
					};
				}.start();
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}

		}.execute();

	}

	private void initTimer() {
		timerAd = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				Log.e("haha", "��������������");
				if (work.isNetWork()==true) {
					mHandler.sendEmptyMessage(120);
					if (noOss) {
						weatherHandler.sendEmptyMessage(4);
						noOss = false;
					}
					Log.e("haha", "��������");

					if (school) {
						RecSchoolInfo recSchoolInfo = new RecSchoolInfo();
						recSchoolInfo.receiveSchoolInfoMain(schooleInfo,
								getMac(), MainActivity.this);
						school = false;
					}
				} else {
					mHandler.sendEmptyMessage(121);
				}
			}
		};

		timerAd.schedule(timerTask, 0, 3000);
	}
	
	// ��ʱ���������ϴ�
	private void twoUpTimer() {
		timerTwoUp = new Timer();
		taskTwoUp = new TimerTask() {
			@Override
			public void run() {
				if (oss!=null&&isNetWorkNormal==true) {
					if (db.queryTwo() != null) {
						if (db.queryTwo().equals("0")) {
							two_compressPic();
						}
					}
				}
			}
		};

		timerTwoUp.schedule(taskTwoUp, 0, 3000);
	}

	@SuppressWarnings({ "resource" })
	private void compressPic(ImgInfo imginfo) {
		Log.e("haha", "һ���ϴ�����");
		// allcount++;
		try {
			// TODO Auto-generated method stub
			try {
				if (oss != null&&isNetWorkNormal==true) {
					System.out.println("22222222222222");
					System.out.println("imginfo" + imginfo.getCardno());
					String testObject = imginfo.getFile().substring(
							imginfo.getFile().lastIndexOf("/") + 1,
							imginfo.getFile().lastIndexOf("."));

					Date nowTime = new Date(System.currentTimeMillis());
					SimpleDateFormat sdFormatter = new SimpleDateFormat(
							"yyyyMMdd");
					String retStrFormatNowDate = sdFormatter.format(nowTime);

					final String uploadFilePath = imginfo.getFile();

					parment = "baige2"
							+ "/"
							+ RecSchoolInfo.Id
							+ "/"
							+ retStrFormatNowDate
							+ "/"
							+ new CardMd5().GetMD5Code(testObject
									+ System.currentTimeMillis()) + ".jpg";
					System.out.println(parment);
					new OSSSample(MainActivity.this, uploadFilePath, oss,
							 imginfo, parment, dbWriter, mHandler)
							.upload();

				} else {
					ContentValues values = new ContentValues();
					values.put("type", imginfo.getType());
					values.put("cardno", imginfo.getCardno());
					values.put("alluploadpaths", imginfo.getFile());
					values.put("timecode", System.currentTimeMillis() / 1000);
					dbWriter.insert("allpaths", null, values);

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v("tt", "http://" + "sdk-baige."
					+ "oss-cn-beijing.aliyuncs.com/" + serverAddress);

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	// http://sdk-baige.oss-cn-beijing.aliyuncs.com/2813/2016-04-13/c25e34b4f1ef506928f1d6bf52b6270f
	private void two_compressPic() {

		ArrayList<String> reup = GetFileName();
		System.out.println("��������" + reup.size());
		db.insertTwo("1");
		try {
			context = MainActivity.this;
			int num = reup.size();
			for (int j = 0; j < num; j++) {
				ArrayList<String> listCode = queryRecode(reup.get(j));
				String content = reup.get(j);
				int type = Integer.parseInt(listCode.get(1));
				long cardno = Long.parseLong(listCode.get(0));
				long time = Long.parseLong(listCode.get(2));

				ImgInfo info = new ImgInfo();
				info.setType(type);
				info.setCardno(cardno);

				String testObject = content.substring(
						content.lastIndexOf("/") + 1, content.lastIndexOf("."));

				Date nowTime = new Date(System.currentTimeMillis());
				SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyyMMdd");
				String retStrFormatNowDate = sdFormatter.format(nowTime);

				String filePath = content;

				parment = "baige2"
						+ "/"
						+ RecSchoolInfo.Id
						+ "/"
						+ retStrFormatNowDate
						+ "/"
						+ new CardMd5().GetMD5Code(testObject
								+ System.currentTimeMillis()) + ".jpg";

				if (oss != null) {
					new OSSSample(MainActivity.this, filePath, oss,
							info, parment, dbWriter, mHandler).sycupload(time);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.insertTwo("0");
		}
		// reup.clear();
		// paths.clear();
	}

	public void transImage1(String fromFile, String toFile, int width,
			int height, int quality) {
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
			int bitmapWidth = bitmap.getWidth();
			int bitmapHeight = bitmap.getHeight();
			// ����ͼƬ�ĳߴ�
			float scaleWidth = (float) width / bitmapWidth;
			float scaleHeight = (float) height / bitmapHeight;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// �������ź��Bitmap����
			Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmapWidth, bitmapHeight, matrix, false);
			// save file
			File myCaptureFile = new File(toFile);
			FileOutputStream out = new FileOutputStream(myCaptureFile);
			if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
				out.flush();
				out.close();
			}
			if (!bitmap.isRecycled()) {
				bitmap.recycle();// �ǵ��ͷ���Դ��������ڴ����
			}
			if (!resizeBitmap.isRecycled()) {
				resizeBitmap.recycle();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak")
	Handler upHandler = new Handler() {
		public void handleMessage(Message msg) {
			// System.out.println(imgvCount);
			if (msg.what == 1) {
				File file1 = new File(msg.getData().getString("File"));
				// // new
				// //
				// ComprssBitmap().getSmallBitmap(msg.getData().getString("File"));
				Uri uri = Uri.fromFile(file1);
				ImgInfo imginfo = new ImgInfo();
				imginfo.setCardno(msg.getData().getLong("cardno"));
				imginfo.setType(msg.getData().getInt("type"));
				imginfo.setFile(msg.getData().getString("File"));
				mImginfo = imginfo;
				switch (imgvCount) {
				case 0:
					// Log.e("uri", uri.toString());
					// displayFromSDCard(uri.toString(), imageView1);
					imageView1.setImageURI(uri);
					// imageView1.setImageBitmap(bitmap);
					// System.out.println("imageView1");
					imgvCount++;
					break;
				case 1:
					imageView2.setImageURI(uri);
					// imageView2.setImageBitmap(bitmap);
					// displayFromSDCard(uri.toString(), imageView2);
					imgvCount++;
					break;
				case 2:
					imageView3.setImageURI(uri);
					// imageView3.setImageBitmap(bitmap);
					// displayFromSDCard(uri.toString(), imageView3);
					imgvCount++;
					break;
				case 3:
					imageView4.setImageURI(uri);
					// imageView4.setImageBitmap(bitmap);
					// displayFromSDCard(uri.toString(), imageView4);
					imgvCount++;
					break;
				case 4:
					imageView5.setImageURI(uri);
					// imageView5.setImageBitmap(bitmap);
					// displayFromSDCard(uri.toString(), imageView5);
					imgvCount++;
					break;
				case 5:
					imageView6.setImageURI(uri);
					// imageView6.setImageBitmap(bitmap);
					// displayFromSDCard(uri.toString(), imageView6);
					imgvCount++;
					break;
				case 6:
					imageView7.setImageURI(uri);
					// imageView7.setImageBitmap(bitmap);
					// displayFromSDCard(uri.toString(), imageView7);
					imgvCount++;
					break;
				case 7:
					imageView8.setImageURI(uri);
					// imageView8.setImageBitmap(bitmap);
					// displayFromSDCard(uri.toString(), imageView8);
					imgvCount = 0;
					break;
				default:
					break;
				}
				compressPic(imginfo);
//				threadPool.execute(new UploadThread(imginfo));
//				mUploadService.execute(new UploadThread(imginfo));

			}
		}
	};

	public static byte[] fromHex(String hexString) throws NumberFormatException {
		hexString = hexString.trim();
		String s[] = hexString.split(" ");
		byte ret[] = new byte[s.length];
		for (int i = 0; i < s.length; i++) {
			ret[i] = (byte) Integer.parseInt(s[i], 16);
		}
		return ret;
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				String mClassname = "�༶��";
				int mType = 8;
				mType = msg.getData().getInt("type");

				if (mType != 8) {
					mClassname = "ְλ��";
				}
				name = msg.getData().getString("name");
				classname = msg.getData().getString("classname");
				switch (imgvCount) {
				case 0:
					textView1_1.setText("������" + name);
					textView1_2.setText(mClassname + classname);
					textAllcount.setText("ˢ���� : " + allcount);
					// textAAA.setText("�ϴ��� : " + upallcount);
					name = null;
					classname = null;
					// nameMum++;
					break;
				case 1:
					textView2_1.setText("������" + name);
					textView2_2.setText(mClassname + classname);
					textAllcount.setText("ˢ���� : " + allcount);
					// textAAA.setText("�ϴ��� : " + upallcount);
					name = null;
					classname = null;
					// nameMum++;
					break;
				case 2:
					textView3_1.setText("������" + name);
					textView3_2.setText(mClassname + classname);
					textAllcount.setText("ˢ���� : " + allcount);
					// textAAA.setText("�ϴ��� : " + upallcount);
					name = null;
					classname = null;
					// nameMum++;
					break;
				case 3:
					textView4_1.setText("������" + name);
					textView4_2.setText(mClassname + classname);
					textAllcount.setText("ˢ���� : " + allcount);
					// textAAA.setText("�ϴ��� : " + upallcount);
					name = null;
					classname = null;
					// nameMum++;
					break;
				case 4:
					textView5_1.setText("������" + name);
					textView5_2.setText(mClassname + classname);
					textAllcount.setText("ˢ���� : " + allcount);
					// textAAA.setText("�ϴ��� : " + upallcount);
					name = null;
					classname = null;
					// nameMum++;
					break;
				case 5:
					textView6_1.setText("������" + name);
					textView6_2.setText(mClassname + classname);
					textAllcount.setText("ˢ���� : " + allcount);
					// textAAA.setText("�ϴ��� : " + upallcount);
					name = null;
					classname = null;
					// nameMum++;
					break;
				case 6:
					textView7_1.setText("������" + name);
					textView7_2.setText(mClassname + classname);
					textAllcount.setText("ˢ���� : " + allcount);
					// textAAA.setText("�ϴ��� : " + upallcount);
					name = null;
					classname = null;
					// nameMum++;
					break;
				case 7:
					textView8_1.setText("������" + name);
					textView8_2.setText(mClassname + classname);
					textAllcount.setText("ˢ���� : " + allcount);
					// textAAA.setText("�ϴ��� : " + upallcount);
					name = null;
					classname = null;
					// nameMum=0;
					break;

				default:
					break;
				}
			}

		}
	};

	Handler schooleInfo = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:
				dbManagerstu.delete();
				Log.e("��������", "��������");
				Log.e("comecome", "�������������0.6");
				new Thread() {
					public void run() {
						Login l = new Login();
						l.myFun(MainActivity.this);
					};
				}.start();
				break;
			case 3:
				MainActivity.intent = new Intent(MainActivity.this,
						MyService.class);
				startService(MainActivity.intent);
				break;
			case 5:
				// û��ѧУ���ճ���������
				Log.e("comecome", "�������������5.0");
				dbManagerstu.delete();
				new Thread() {
					public void run() {
						Login l = new Login();
						l.myFun(MainActivity.this);
					};
				}.start();
				break;
			case 6:
				// ��Ĭ��װapk
				InstallAPK installAPK = new InstallAPK();
				installAPK.onClick_install();
				break;

			}

		};
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
		}
		stopService(intent);

		upallcount = 0;

		Login.accesstoken = null;

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timerAd != null) {
			timerAd.cancel();
			timerAd = null;
		}
		if (timercont != null) {
			timercont.cancel();
			timercont = null;
		}

		if (broad != null) {
			unregisterReceiver(broad);
		}
		dbManagercard.closeDB();
		dbManagerstu.closeDB();
		// dBManageradvert.closeDB();
		dBManagerSchPic.closeDB();

		if (dbMac != null) {
			dbMac.closeDBMac();
		}

		try {

			if (server != null)
				server.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			echoThread.server_socket.close();
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("�ر�------------------");
		// super.onDestroy();
	}

	// �������ݹ㲥
	private void registerUpdate() {
		broad = new Broad();
		IntentFilter filter = new IntentFilter();// ����IntentFilter����
		filter.addAction("com.baige.ui.service");
		registerReceiver(broad, filter);// ע��Broadcast Receive
	}

	private class Broad extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getStringExtra("miss") != null) {
				if (intent.getStringExtra("miss").equals("2")) {
					timeIdentify = 0;
				}
			}

			if (intent.getStringExtra("reflush") != null) {
				if (intent.getStringExtra("reflush").equals("1")) {
					timeIdentify = 1;
					Toast.makeText(MainActivity.this, "���ڸ������ݣ����Ժ�",
							Toast.LENGTH_LONG).show();
				}
			}
		}

	}

	private class ServerS extends Thread {
		@Override
		public void run() {
			super.run();

			try {
				server = new ServerSocket(3333);
//				mExecutorService = Executors.newCachedThreadPool();// ����һ������pool
				threadPoolSoc = new ThreadPoolExecutor(3, 50, 0,TimeUnit.SECONDS,    
		                //�������Ϊ3   
		                new ArrayBlockingQueue<Runnable>(3),   
		                //�����ɵ�����   
		                new ThreadPoolExecutor.DiscardOldestPolicy());    

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			while (true) {
				try {
					if (server != null) {
						Socket server_socket = server.accept();
						threadPoolSoc.execute(new EchoThread(server_socket));
//						mExecutorService.execute(new EchoThread(server_socket));// ��������ľ���
						Log.e("�߳�", "...�߳��߳�...");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	class EchoThread extends Thread {


		private Socket server_socket;
		private BufferedOutputStream outStream = null;
		private String socket_class, socket_name;
		private int type;
		private File pictureFile = null;
		private String picFilename, picFilepath = null;
		private Long cardno;
		private NameClass nameClass;

		private boolean out = true;
		private boolean cardok = true;
		private boolean carbad = true;
		private boolean picok = true;
		private boolean picbad = true;

		private String result = null;
		private int l = 0;
		private int j = 0;

		private int resetTime = 0;

		public EchoThread(Socket server_socket) {
			this.server_socket = server_socket;
		}

		public EchoThread() {

		}

		@Override
		public void run() {
			BufferedInputStream br = null;
			OutputStream server_out = null;
			try {
				server_socket.setSoLinger(true, 0);
				server_socket.setReuseAddress(true);
				server_socket.setSoTimeout(10000);
				br = new BufferedInputStream(server_socket.getInputStream());
				server_out = server_socket.getOutputStream();

				boolean temp = true;
				while (temp) {
					final byte[] buff = new byte[1024];
					int len = -1;

					while ((len = br.read(buff)) != -1) {
						result = new String(buff, 0, len);

						if (result.indexOf(result.valueOf("end")) != -1) {
							// System.out.println(result);
							if (result.indexOf(result.valueOf("cardok")) != -1
									&& cardok) {
								String[] card_string = result.split(":");

								for (int i = 0; i < card_string.length; i++) {

									if (i == 1) {
										byte[] card_data = new byte[len];
										for (int j = 0; j < len; j++) {
											card_data[j] = buff[j];
										}

										nameClass = card_client(server_out,
												card_data);
										long carNun = 0;
										if (nameClass != null) {

											carNun = nameClass.getCardno();
										}

										Date nowTime = new Date(
												System.currentTimeMillis());
										SimpleDateFormat sdFormatter = new SimpleDateFormat(
												"yyyy-MM-dd");

										String cardTime1 = sdFormatter
												.format(nowTime);
										CardMd5 cardMd5 = new CardMd5();

										if (carNun != 0) {
											serverAddress = SchoolID
													+ "/"
													+ cardTime1
													+ "/"
													+ cardMd5
															.GetMD5Code(carNun
																	+ nowTime
																			.toString());
											System.out.println(serverAddress
													+ "0000010");
										}

										if (nameClass != null) {
											Log.e("�жϿ���", "en");
											temp = nameClass.getTemp();
											if (temp) {
												Log.e("������ȷ", "en");
												allcount++;
												nameClass.setStuNo(String.valueOf(allcount));
											}
										}
									}
								}
								cardok = false;
							} else if (result.indexOf(result
									.valueOf("cardokbad")) != -1 && carbad) {
								String[] cardbad_string = result.split(":");
								for (int i = 0; i < cardbad_string.length; i++) {
									if (i == 1) {
										// System.out.println(cardbad_string[i]);
									}
								}
								// outStream.write(buff, 0, len);
								// outStream.flush();
								temp = false;
								carbad = false;
							} else if (result.indexOf(result
									.valueOf("picturebad")) != -1 && picbad) {

								String[] picturebad_string = result.split(":");
								for (int i = 0; i < picturebad_string.length; i++) {
									if (i == 1) {
										System.out.println("picturebad...."
												+ picturebad_string[i]);
									}
								}
								// outStream.write(buff, 0, len);
								// outStream.flush();
								temp = false;
								picbad = false;
							} else {

								outStream.write(buff, 0, len);
								outStream.flush();
								l++;
						if (nameClass.getPath() != null) {
								File f = new File(nameClass.getPath());
								if (f.exists()) {
									String message2 = "recv:ok:end";
									server_out.write(message2
											.getBytes("UTF-8"));
									// System.out.println(message);
									server_out.flush();
								}
							}
							}
						} else {

							if (result.indexOf(result.valueOf("pictureok")) != -1
									&& picok) {
								Log.e("ͼƬ������", "������");
								String[] picture_string = result.split(":");

								int lenght = 0;
								for (int i = 0; i < 4; i++) {
									if (i == 1)
										lenght = picture_string[i].length();

									if ((i == 3) && (nameClass != null)) {

										picFilename = CardMd5.GetMD5Code(String.valueOf(System.currentTimeMillis())) + nameClass.getStuNo();
										// picFilename
										// =Long.toString(physicsno);

										pictureFile = new File(
												Environment
														.getExternalStorageDirectory()
														+ "/baige/picFile/"
														+ picFilename + ".jpg");
										FileOutputStream outputStream = new FileOutputStream(
												pictureFile);
										outStream = new BufferedOutputStream(
												outputStream);
										outStream.write(buff, (18 + lenght),
												len - (18 + lenght));
										outStream.flush();
										
										nameClass.setPath(Environment
												.getExternalStorageDirectory()
												+ "/baige/picFile/"
												+ picFilename + ".jpg");  
									}
								}
								picok = false;
								j++;
							} else {
								outStream.write(buff, 0, len);
								outStream.flush();

							}
						}
					
					}
					Log.e("������", 555 + "");
					if (nameClass != null) {
						if (nameClass.getTemp() && nameClass.getPath() == null) { 
							mHandler.sendEmptyMessage(88);
							String path = Environment
									.getExternalStorageDirectory()
									+ "/baige/picFile/"
									+ CardMd5.GetMD5Code(String.valueOf(System.currentTimeMillis())) + nameClass.getStuNo() + ".jpg";
							if (new File(getDir() + "/baige/LOGOFile/1.jpg")
									.exists()) {
								new OSSSample().copyFile(getDir()
										+ "/baige/LOGOFile/1.jpg", path);
								nameClass.setPath(path);
								
                                while(!new File(nameClass.getPath()).exists()){
							    	
							    }
								Message message1 = new Message();
								Bundle bundle = new Bundle();
								bundle.putString("name", nameClass.getName());
								bundle.putString("classname",
										nameClass.getClasses());
								bundle.putInt("type", nameClass.getType());
								System.out.println("ˢ��������");
								System.out.println(nameClass.getType());

								message1.setData(bundle);
								message1.what = 1;
								MainActivity.this.handler.sendMessage(message1);

								Message message = new Message();
								message.what = 1;
								Bundle bundle1 = new Bundle();
								bundle1.putInt("type", nameClass.getType());
								bundle1.putLong("cardno", nameClass.getCardno());
								bundle1.putString("File", nameClass.getPath());
								message.setData(bundle1);
								MainActivity.this.upHandler
										.sendMessage(message);
								if(nameClass.getType()==8){
									dbManagercard.deletePre(Long.toString(nameClass
											.getCardno()));	
								}
							}
							temp = false;

						} else {
							temp = false;
						}
					} else {
						temp = false;
					}

					if ((len == -1 && l>0) || (len == -1 && j > 0)) {
						if (nameClass != null) {
							if (nameClass.getPath() != null) {
							    if(new File(nameClass.getPath()).exists()){
							    	if (isNoImage(nameClass.getPath())) {
										new OSSSample().copyFile(getDir()
												+ "/baige/LOGOFile/1.jpg",
												nameClass.getPath());
										 while(!new File(nameClass.getPath()).exists()){
										    	
										    }
										if (nameClass.getTemp()) {
											Message message1 = new Message();
											Bundle bundle = new Bundle();
											bundle.putString("name",
													nameClass.getName());
											bundle.putString("classname",
													nameClass.getClasses());
											message1.setData(bundle);
											message1.what = 1;
											MainActivity.this.handler
													.sendMessage(message1);
											Message message = new Message();
											message.what = 1;
											Bundle bundle1 = new Bundle();
											bundle1.putInt("type",
													nameClass.getType());
											bundle1.putLong("cardno",
													nameClass.getCardno());
											bundle1.putString("File",
													nameClass.getPath());
											message.setData(bundle1);
											MainActivity.this.upHandler
													.sendMessage(message);

										}

										temp = false;
									} else {
										if (nameClass.getTemp()) {
											Message message1 = new Message();
											Bundle bundle = new Bundle();
											bundle.putString("name",
													nameClass.getName());
											bundle.putString("classname",
													nameClass.getClasses());
											bundle.putInt("type",
													nameClass.getType());
											System.out.println("ˢ��������");
											System.out.println(nameClass.getType());

											message1.setData(bundle);
											message1.what = 1;

											MainActivity.this.handler
													.sendMessage(message1);
											Message message = new Message();
											message.what = 1;
											Bundle bundle1 = new Bundle();
											bundle1.putInt("type",
													nameClass.getType());
											bundle1.putLong("cardno",
													nameClass.getCardno());
											bundle1.putString("File",
													nameClass.getPath());
											message.setData(bundle1);
											MainActivity.this.upHandler
													.sendMessage(message);

										}

										temp = false;
									}
							    }else{
							    	new OSSSample().copyFile(getDir()
											+ "/baige/LOGOFile/1.jpg",
											nameClass.getPath());
							    	while(!new File(nameClass.getPath()).exists()){
								    	
								    }
							    	if (nameClass.getTemp()) {
										Message message1 = new Message();
										Bundle bundle = new Bundle();
										bundle.putString("name",
												nameClass.getName());
										bundle.putString("classname",
												nameClass.getClasses());
										message1.setData(bundle);
										message1.what = 1;
										MainActivity.this.handler
												.sendMessage(message1);
										Message message = new Message();
										message.what = 1;
										Bundle bundle1 = new Bundle();
										bundle1.putInt("type",
												nameClass.getType());
										bundle1.putLong("cardno",
												nameClass.getCardno());
										bundle1.putString("File",
												nameClass.getPath());
										message.setData(bundle1);
										MainActivity.this.upHandler
												.sendMessage(message);

									}
							    }
							}
						}
					}
				}
				result = null;
				if (br != null) {
					br.close();
					br = null;
				}
				if (outStream != null) {
					outStream.close();
					outStream = null;
				}
				if (server_out != null) {
					server_out.close();
					server_out = null;   
				}
				if (server_socket != null) {
					server_socket.close();
					server_socket = null;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (out) {
					result = null;
					try {
						if (br != null) {
							br.close();
							br = null;
						}
						if (outStream != null) {
							outStream.close();
							outStream = null;
						}
						if (server_out != null) {
							server_out.close();
							server_out = null;
						}
						if (server_socket != null) {
							server_socket.close();
							server_socket = null;
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					if (nameClass != null) {
						if (nameClass.getTemp()) {
							mHandler.sendEmptyMessage(99);
							if (nameClass.getPath() != null) {
							    if(new File(nameClass.getPath()).exists()){
							    	if (isNoImage(nameClass.getPath())) {
										new OSSSample().copyFile(getDir()
												+ "/baige/LOGOFile/1.jpg",
												nameClass.getPath());
										 while(!new File(nameClass.getPath()).exists()){
										    	
										    }
											Message message1 = new Message();
											Bundle bundle = new Bundle();
											bundle.putString("name",
													nameClass.getName());
											bundle.putString("classname",
													nameClass.getClasses());
											message1.setData(bundle);
											message1.what = 1;
											MainActivity.this.handler
													.sendMessage(message1);
											Message message = new Message();
											message.what = 1;
											Bundle bundle1 = new Bundle();
											bundle1.putInt("type",
													nameClass.getType());
											bundle1.putLong("cardno",
													nameClass.getCardno());
											bundle1.putString("File",
													nameClass.getPath());
											message.setData(bundle1);
											MainActivity.this.upHandler
													.sendMessage(message);
									} else {
											Message message1 = new Message();
											Bundle bundle = new Bundle();
											bundle.putString("name",
													nameClass.getName());
											bundle.putString("classname",
													nameClass.getClasses());
											bundle.putInt("type",
													nameClass.getType());
											System.out.println("ˢ��������");
											System.out.println(nameClass.getType());

											message1.setData(bundle);
											message1.what = 1;

											MainActivity.this.handler
													.sendMessage(message1);
											Message message = new Message();
											message.what = 1;
											Bundle bundle1 = new Bundle();
											bundle1.putInt("type",
													nameClass.getType());
											bundle1.putLong("cardno",
													nameClass.getCardno());
											bundle1.putString("File",
													nameClass.getPath());
											message.setData(bundle1);
											MainActivity.this.upHandler
													.sendMessage(message);

									}
							    }else{
							    	new OSSSample().copyFile(getDir()
											+ "/baige/LOGOFile/1.jpg",
											nameClass.getPath());
							    	while(!new File(nameClass.getPath()).exists()){
								    	
								    }
							    	if (nameClass.getTemp()) {
										Message message1 = new Message();
										Bundle bundle = new Bundle();
										bundle.putString("name",
												nameClass.getName());
										bundle.putString("classname",
												nameClass.getClasses());
										message1.setData(bundle);
										message1.what = 1;
										MainActivity.this.handler
												.sendMessage(message1);
										Message message = new Message();
										message.what = 1;
										Bundle bundle1 = new Bundle();
										bundle1.putInt("type",
												nameClass.getType());
										bundle1.putLong("cardno",
												nameClass.getCardno());
										bundle1.putString("File",
												nameClass.getPath());
										message.setData(bundle1);
										MainActivity.this.upHandler
												.sendMessage(message);

									}
							    }
							} else {
								String path = Environment
										.getExternalStorageDirectory()
										+ "/baige/picFile/"+
										CardMd5.GetMD5Code(String.valueOf(System.currentTimeMillis())) + nameClass.getStuNo()+ ".jpg";
								if (new File(getDir() + "/baige/LOGOFile/1.jpg")
										.exists()) {
									new OSSSample().copyFile(getDir()
											+ "/baige/LOGOFile/1.jpg", path);
									nameClass.setPath(path);
									while(!new File(nameClass.getPath()).exists()){
								    	
								    }
									Message message1 = new Message();
									Bundle bundle = new Bundle();
									bundle.putString("name",
											nameClass.getName());
									bundle.putString("classname",
											nameClass.getClasses());
									message1.setData(bundle);
									message1.what = 1;
									MainActivity.this.handler
											.sendMessage(message1);

									Message message = new Message();
									message.what = 1;
									Bundle bundle1 = new Bundle();
									bundle1.putInt("type", nameClass.getType());
									bundle1.putLong("cardno",
											nameClass.getCardno());
									bundle1.putString("File",
											nameClass.getPath());
									message.setData(bundle1);
									MainActivity.this.upHandler
											.sendMessage(message);
									if(nameClass.getType()==8){
										dbManagercard.deletePre(Long.toString(nameClass
												.getCardno()));	
									}
								}
							}

						}
					}
					out = false;
				}
			}

			// TODO Auto-generated method stub
		}

	
	}
	
//	class UploadThread extends Thread{
//		private ImgInfo imfo;
//		public UploadThread(ImgInfo imfo) {
//			// TODO Auto-generated constructor stub
//			this.imfo = imfo;
//		}
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			super.run();
//			compressPic(imfo);
//		}
//	}

	private NameClass card_client(OutputStream server_out, byte card_data[]) {
		NameClass nameClass = null;
		StringBuilder sMsg3 = new StringBuilder();
		boolean temp = true;

		try {
			byte[] bRec = null;
			bRec = new byte[card_data.length];
			for (int i = 0; i < card_data.length; i++) {
				bRec[i] = card_data[i];
			}
			sMsg3.append(MyFunc.ByteArrToHex(bRec));
			Log.e("--sMsg3--", sMsg3.toString());
			System.out.println(sMsg3.toString());
			// String frcardno_datd[] = sMsg3.toString().split("3A");
			String frcardno_datd = sMsg3.substring(sMsg3.indexOf("3A") + 2,
					sMsg3.lastIndexOf("3A"));

			/*
			 * for (int i = 0; i < frcardno_datd.length; i++) { if (i == 1) {
			 */

			System.out.println("frcardno_datd" + frcardno_datd);
			System.out.println("frcardno_datd.length()"
					+ frcardno_datd.length());
			if (frcardno_datd.length() == 31) {
				String frcardno = MainActivity.fromCardno(frcardno_datd
						.substring(12, frcardno_datd.length() - 7));
				System.out.println(frcardno);
				nameClass = server_read(frcardno);
			} else if (frcardno_datd.length() > 31) {
				String str = frcardno_datd.substring(0, 31);
				String frcardno = MainActivity.fromCardno(str.substring(12,
						str.length() - 7));
				nameClass = server_read(frcardno);
			} else {
				Message msg = new Message();
				msg.what = 77;
				Bundle b = new Bundle();
				b.putString("car", frcardno_datd);
				msg.setData(b);
				mHandler.sendEmptyMessage(77);
			}
			sMsg3 = new StringBuilder();
			String message = null;
			if (card_count == 0) {
				if (nameClass != null) {
					if (nameClass.getName() != null) {
						// �ж���û���Զ����ˢ��ʱ��
						if (timeIdentify == 0) {
							message = "cardno:" + nameClass.getCardno()
									+ ":contentok:" + nameClass.getClasses()
									+ nameClass.getName() + ":end";
							temp = true;
							nameClass.setTemp(temp);
						} else {
							message = "cardno:" + nameClass.getCardno()
									+ ":contentagain:end";
							temp = false;
							nameClass.setTemp(temp);
						}
					} else {
						message = "cardno:" + nameClass.getCardno()
								+ ":contentbad:end";
						temp = false;
						nameClass.setTemp(temp);
					}
				}
			} else {
				message = "cardno:" + nameClass.getCardno()
						+ ":contentagain:end";
				temp = false;
				nameClass.setTemp(temp);
			}
			if (message != null) {
				server_out.write(message.getBytes("UTF-8"));
				// System.out.println(message);
			}

			server_out.flush();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nameClass;
	}

	private NameClass server_read(String read_string) {
		long physicsno = 0;

		System.out.println(read_string + "oooooooooo" + read_string.length()
				+ "pppppp");

		NameClass nameClass = new NameClass();

		// physicsno = (16777215 - (Long.parseLong(read_string, 16) %
		// 16777216)); // ����

		physicsno = Long.parseLong(read_string, 16);

		nameClass.setCardno(tt - physicsno);

		System.out.println("000aaa" + Long.toString(tt - physicsno));
		System.out.println("000bbb"
				+ dbManagercard.query(Long.toString(tt - physicsno)));
		if (!(Long.toString(tt - physicsno).equals(dbManagercard.query(Long
				.toString(tt - physicsno))))) {

			Log.e("MainActivity", physicsno + "");
			card_count = 0;
			Stu stu = dbManagerstu.query(nameClass.getCardno() + "");
			nameClass.setType(stu.getType());
			nameClass.setName(stu.getName());
			nameClass.setClasses(stu.getClassname());
			// System.out.println(server_class);
			if (nameClass.getName() != null) {
				if (nameClass.getType() == 8) {
					dbManagercard.insert(Long.toString(nameClass.getCardno()));

					System.out.println("server_name = " + nameClass.getName()
							+ " server_class = " + nameClass.getClasses());
				}
			} else if (nameClass.getName() == null) {
				RecOneCard recOneCard = new RecOneCard();
				stu = recOneCard.receiveDate(physicsno);
				nameClass.setType(stu.getType());
				nameClass.setName(stu.getName());
				nameClass.setClasses(stu.getClassname());
				if (nameClass.getName() != null) {
					// type=8��ѧ�� �������ݿ������ظ�ˢ�� �������ѧ�����Ͳ�����
					if (nameClass.getType() == 8) {
						dbManagercard.insert(Long.toString(nameClass
								.getCardno()));

						System.out.println("server_name = "
								+ nameClass.getName() + " server_class = "
								+ nameClass.getClasses());
					}
				} else if (nameClass.getName() == null) {
					nameClass.setCardno(0);
					nameClass.setName(null);
					nameClass.setClasses(null);
				}
			}

		} else {
			card_count = 1;
		}
		physicsno = 0;
		return nameClass;
	}

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + 1;
	}

	/**
	 * ͨ����ȡ�ļ�����ȡ��width��height�ķ�ʽ�����ж��жϵ�ǰ�ļ��Ƿ�ͼƬ������һ�ַǳ��򵥵ķ�ʽ��
	 * 
	 * @param imageFile
	 * @return
	 */
	public boolean isNoImage(String imagepath) {
		//
		Bitmap bitmap = BitmapFactory.decodeFile(imagepath);

		try {
			if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
				return true;
			}
			BufferedOutputStream stream;
			stream = new BufferedOutputStream(new FileOutputStream(new File(
					imagepath)));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
			return false;
		} catch (Exception e) {
			return false;
		} finally {
			if (bitmap != null) {
				bitmap.recycle();
			}
			bitmap = null;
		}
	}

	public void displayFromSDCard(String uri, ImageView imageView) {
		// String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
		ImageLoader.getInstance().displayImage(uri, imageView);
	}

	public boolean fileIsExists(String file) {
		try {
			File f = new File(file);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@SuppressLint("DefaultLocale")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList GetFileName() {
		ArrayList<String> vector = new ArrayList<String>();
//		Cursor c = dbReader.query("allpaths", null, null, null, null, null,
//				null);
		Cursor c  = dbReader.rawQuery("select alluploadpaths from allpaths limit 0,10", null);
		while (c.moveToNext()) {   
			vector.add(c.getString(c.getColumnIndex("alluploadpaths")));  
		}
		c.close();
		return vector;
	}

	@SuppressLint("DefaultLocale")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Vector GetFileName1(String fileAbsolutePath, String form) {
		Vector vecFile = new Vector();
		File file = new File(fileAbsolutePath);
		if (file.exists() && file.isDirectory()) {
			if (file.listFiles().length > 0) {
				File[] subFile = file.listFiles();
				for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
					// �ж��Ƿ�Ϊ�ļ���
					if (!subFile[iFileLength].isDirectory()) {
						String filename = subFile[iFileLength].getName();
						// �ж��Ƿ�ΪMP4��β
						if (filename.trim().toLowerCase().endsWith(form)) {
							vecFile.add(filename);
						}
					}
				}
			}
		}
		return vecFile;
	}

	// ----------------------------------------------------��ȡ�ļ���
	public File getDir() {
		// �õ�SD����Ŀ¼
		File dir = Environment.getExternalStorageDirectory();
		if (dir.exists()) {
			return dir;
		} else {
			dir.mkdirs();
			return dir;
		}
	}

	public static String fromCardno(String hexString) {
		hexString = hexString.trim();
		String s[] = hexString.split(" ");
		StringBuilder builder = new StringBuilder();
		for (int i = s.length - 1; i >= 0; i--) {
			builder.append(s[i]);
		}
		return builder.toString();
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			 if (upallcount > allcount){
				 upallcount = allcount;
			 }
			 if (intent.getAction().equals(action)) {
					textAAA.setText("�ϴ��� : " + upallcount);
				} 
		}
	};

	private static Bitmap getImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // ��Ϊ false
		// �������ű�
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	private ArrayList<String> queryRecode(String val) {
		ArrayList<String> list = new ArrayList<String>();
		System.out.println(val);
		String cardq = "c";
		String type = "b";
		String time = "d";
		String[] columns = { "type", "cardno", "timecode" };
		String[] selectionArgs = { val };
		Cursor c = dbReader.query("allpaths", columns, "alluploadpaths=?",
				selectionArgs, null, null, null);
		while (c.moveToNext()) {
			cardq = c.getString(c.getColumnIndex("cardno"));
			type = c.getString(c.getColumnIndex("type"));
			time = c.getString(c.getColumnIndex("timecode"));
		}
		c.close();
		list.add(String.valueOf(cardq));
		list.add(String.valueOf(type));
		list.add(String.valueOf(time));
		return list;
	}

	private String getMac() {

		dbMac = new DBMacAddress();
		dbMac.creatDB();
		dbMac.creatDB_ID();
		MacEntity macEntity = new MacEntity();
		macEntity = dbMac.query();
		if (macEntity.getMac() != null) {
			System.out.println("mac:  " + macEntity.getMac());
			accesstoken = macEntity.getMac();
			System.out.println("�����ݿ��ȡMAC");
		}
		if (accesstoken == null) {
			GetDeviceID getDeviceID = new GetDeviceID();
			accesstoken = getDeviceID.getMacAddress();
			System.out.println("accesstoken  " + accesstoken);
			if (accesstoken != null) {
				dbMac.insert(accesstoken);
			}
		}
		return accesstoken;
	}
}
