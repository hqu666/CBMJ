package com.coresoft.cbmj;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaActionSound;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CBMJActivity extends AppCompatActivity {
	public CS_Util UTIL;
	public static boolean debugNow = false;
	private HandlerThread mBackgroundThread;
	private Handler mBackgroundHandler;        // A {@link Handler} for running tasks in the background.

	public boolean isNotSet = true;
	static final int REQUEST_PREF = 100;                          //Prefarensからの戻り
	public TextView bin_type_tv;

	public Toolbar toolbar;
	public LinearLayout hart_beat_ll;      		//心拍数履歴
	public LinearLayout coherencei_ll;    		// コヒーレンス達成度合い
	public LinearLayout breathing_ll;    		//呼吸目安
	public AlertDialog myDlog;
	public FrameLayout ma_preview_fl;
	public SurfaceView ma_sarface_view;        //  プレビュー用サーフェス
	public SurfaceHolder ma_sarfaceeHolder;
//	public SurfaceView cam_sv;    		//カメラモニター
	public long haarcascadesLastModified = 0;
	public boolean isReWriteNow = true;                        //リソース書き換え中
	public boolean isPrevieSending = false;     //プレビュー画面処理中

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	/**
	 * このアプリケーションの設定ファイル読出し
	 **/
	public void readPref() {
		final String TAG = "readPref[CBMIA}";
		String dbMsg = "許諾済み";//////////////////
		try {
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {                //(初回起動で)全パーミッションの許諾を取る
				dbMsg = "許諾確認";
				String[] PERMISSIONS = {Manifest.permission.CAMERA};
//		Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.INTERNET ,		Manifest.permission.ACCESS_NETWORK_STATE , Manifest.permission.ACCESS_WIFI_STATE ,
// , Manifest.permission.MODIFY_AUDIO_SETTINGS , Manifest.permission.RECORD_AUDIO ,  Manifest.permission.MODIFY_AUDIO_SETTINGS,
				boolean isNeedParmissionReqest = false;
				for ( String permissionName : PERMISSIONS ) {
					dbMsg += "," + permissionName;
					int checkResalt = checkSelfPermission(permissionName);
					dbMsg += "=" + checkResalt;
					if ( checkResalt != PackageManager.PERMISSION_GRANTED ) {
						isNeedParmissionReqest = true;
					}
				}
				if ( isNeedParmissionReqest ) {
					dbMsg += "許諾処理へ";
					requestPermissions(PERMISSIONS , REQUEST_PREF);
					return;
				}
			}
//			dbMsg += ",isReadPref=" + isReadPref;
//			MyPreferenceFragment prefs = new MyPreferenceFragment();
//			prefs.readPref(this);
//			rootUrlStr = prefs.rootUrlStr;
//			dbMsg += ",rootUrlStr=" + rootUrlStr;
//			readFileName = prefs.readFileName;
//			dbMsg += ",readFileName=" + readFileName;
//			savePatht = prefs.savePatht;
//			dbMsg += ",作成したファイルの保存場所=" + savePatht;
//			isStartLast = prefs.isStartLast;
//			dbMsg += ",次回は最後に使った元画像からスタート=" + isStartLast;
//			is_v_Mirror = prefs.is_v_Mirror;
//			dbMsg += ",左右鏡面動作=" + is_v_Mirror;
//			is_h_Mirror = prefs.is_h_Mirror;
//			dbMsg += ",上下鏡面動作=" + is_h_Mirror;
//			isAautoJudge = prefs.isAautoJudge;
//			dbMsg += ",トレース後に自動判定=" + isAautoJudge;
//			traceLineWidth = prefs.traceLineWidth;
//			dbMsg += ",トレース線の太さ=" + traceLineWidth;
//			isPadLeft = prefs.isPadLeft;
//			dbMsg += ",左側にPad=" + isPadLeft;
//			isLotetCanselt = prefs.isLotetCanselt;
//			dbMsg += ",自動回転阻止=" + isLotetCanselt;
//			sharedPref = PreferenceManager.getDefaultSharedPreferences(this);            //	getActivity().getBaseContext()
//			myEditor = sharedPref.edit();
//			stereoTypeRady( "");

			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	 * Cameraパーミッションが通った時点でstartLocalStream
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode , String permissions[] , int[] grantResults) {
		final String TAG = "onRequestPermissionsResult[MA]";
		String dbMsg = "";
		try {
			dbMsg = "requestCode=" + requestCode;
			switch ( requestCode ) {
				case REQUEST_PREF:
					readPref();        //ループする？
					break;
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String TAG = "onCreate[CBMIA}";
		String dbMsg = "";
		try {
			readPref();

			setContentView(R.layout.activity_cbmj);

			toolbar = ( Toolbar ) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);

			hart_beat_ll = ( LinearLayout ) findViewById(R.id.hart_beat_ll);      		//心拍数履歴
			coherencei_ll = ( LinearLayout ) findViewById(R.id.coherencei_ll);    		// コヒーレンス達成度合い
			breathing_ll = ( LinearLayout ) findViewById(R.id.breathing_ll);    		//呼吸目安
//			cam_sv = ( SurfaceView ) findViewById(R.id.cam_sv);    		//カメラモニター
			ma_preview_fl = ( FrameLayout ) findViewById(R.id.ma_preview_fl);        //pereviewVの呼び込み枠       ViewGroup

			// Set up the ViewPager with the sections adapter.
			mViewPager = ( ViewPager ) findViewById(R.id.container);
			mViewPager.setAdapter(mSectionsPagerAdapter);

			TextView bin_type_tv = ( TextView ) findViewById(R.id.bin_type_tv);

//		FloatingActionButton fab = ( FloatingActionButton ) findViewById(R.id.fab);
//		fab.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Snackbar.make(view , "Replace with your own action" , Snackbar.LENGTH_LONG).setAction("Action" , null).show();
//			}
//		});
			//カメラモニター
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
//			layoutParams.weight = 1.0f;
			layoutParams.gravity = Gravity.CENTER;           //17;効いてない？
			ma_sarface_view = new SurfaceView(this);       //  プレビュー用サーフェス
			ma_sarface_view.setLayoutParams(layoutParams);
			Display display = getWindowManager().getDefaultDisplay();                // 画面サイズ;HardwareSize;を取得する
			Point p = new Point();
			display.getSize(p);
			int hsWidth = p.x;
			int hsHeight = p.y;
			dbMsg += ",this[" + hsWidth + "×" + hsHeight + "]";
			ViewGroup.LayoutParams svlp = ma_sarface_view.getLayoutParams();
//				dbMsg += ",LayoutParams[" + svlp.width + "×" + svlp.height + "]";
			svlp.width = hsWidth;    //ma_sarface_view.getWidth();
			svlp.height = hsHeight;        // ma_sarface_view.getWidth() * PREVIEW_HEIGHT / PREVIEW_WIDTH;
			if ( hsHeight < hsWidth ) {
				hsWidth = hsHeight * 4 / 3;
				svlp.width = hsWidth;
			} else {
				hsHeight = hsWidth * 4 / 3;
				svlp.height = hsHeight;
			}
			dbMsg += ">>[" + hsWidth + "×" + hsHeight + "]";
			dbMsg += ">LayoutParams>[" + svlp.width + "×" + svlp.height + "]";
			ma_sarface_view.setLayoutParams(svlp);                //ここではviewにサイズを与えるだけ。   Holderはカメラセッション開始以降で設定
			svlp = ma_sarface_view.getLayoutParams();
			ma_preview_fl.addView(ma_sarface_view);
			ma_sarface_view.setId(( int ) (9999));            //生成時のみ付与する必要有り

			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	 * 全リソースの読み込みが終わってフォーカスが当てられた時
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		final String TAG = "onStart[CBMIA}";
		String dbMsg = "hasFocus=" + hasFocus;
		try {
			if ( hasFocus ) {
				if ( isNotSet ) {
					laterCreate();
				}
			}
			//			if(isFarst){
//			if ( splashDlog != null ) {
//				if ( splashDlog.isShowing() ) {
//					splashDlog.dismiss();
//				}
//			}
////				isFarst = false;       //初回起動
////			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		final String TAG = "onStart[CBMIA}";
		String dbMsg = "";
		try {

			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		final String TAG = "onResume[MA]";
		String dbMsg = "";
		try {
			isReWriteNow = false;                        //書き換え終了
			dbMsg += ",mBackgroundThread=" + mBackgroundThread;
			if ( mBackgroundThread == null ) {
				startBackgroundThread();        //org
			} else {
				dbMsg += ",mBackgroundThread=" + mBackgroundThread.isAlive();
			}

//			//ここに来るときは未稼働で、登録したリスナーからカメラを起動する
 			if ( ma_sarface_view != null ) {
				dbMsg += ",ma_sarface_view.isActivated=" + ma_sarface_view.isActivated();
				if ( ma_sarface_view.isActivated() ) {
					int TVWIdht = ma_sarface_view.getWidth();
					int TVHight = ma_sarface_view.getHeight();
					dbMsg += "[" + TVWIdht + "×" + TVHight + "]";
					openCamera(TVWIdht , TVHight);                  //org このタイミングで起動出来ず onSurfaceTextureAvailable　へ
				} else {
					ma_sarfaceeHolder = ma_sarface_view.getHolder();                    //SurfaceHolder(SVの制御に使うInterface）
					ma_sarfaceeHolder.addCallback(sarfacCallback);                        //コールバックを設定
				}
			} else {
				dbMsg += "Camera View== null";
			}
			// When the screen is turned off and turned back on, the SurfaceTexture is already available,
			//  and "onSurfaceTextureAvailable" will not be called. In that case, we can open a camera and start preview from here
			// (otherwise, we wait until the surface is ready in the SurfaceTextureListener).

//			if ( isFaceRecognition ) {
//				ma_detecter_bt.setImageResource(android.R.drawable.star_on);
//			} else {
//				ma_detecter_bt.setImageResource(android.R.drawable.star_off);
//			}
//			writeFolder += File.separator + "phot";
//			dbMsg += "writeFolder=" + writeFolder;
			if ( UTIL == null ) {
				UTIL = new CS_Util();
			}
//			saveFileName = UTIL.getSaveFiles(writeFolder);
//			dbMsg += ",saveFileName=" + saveFileName;
//			File dFile = new File(saveFileName);
//			if ( dFile.exists() ) {
//				if ( dFile.isFile() ) {
//					setLastThumbnail(saveFileName);
//				} else {
//					dbMsg += ";ファイルでは無い";
//				}
//			} else {
//				dbMsg += ";無い";
//			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	@Override
	protected void onPause() {
		final String TAG = "onPause[MA]";
		String dbMsg = "";
		try {
			dbMsg += "isReWriteNow=" + isReWriteNow;
			if ( !isReWriteNow ) {
				laterDestroy();
				dbMsg += ">>" + isReWriteNow;
			}

//			 closeCamera();                   //orgではここで破棄
//			stopBackgroundThread();            //prg
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		final String TAG = "onStop[MA}";
		String dbMsg = "";
		try {
			dbMsg += "isReWriteNow=" + isReWriteNow;
			if ( !isReWriteNow ) {
				laterDestroy();
				dbMsg += ">>" + isReWriteNow;
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

		@Override
	protected void onDestroy() {
		super.onDestroy();
		final String TAG = "onDestroy[MA}";
		String dbMsg = "";
		try {
			dbMsg += "isReWriteNow=" + isReWriteNow;
			if ( !isReWriteNow ) {
				laterDestroy();
				dbMsg += ">>" + isReWriteNow;
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}


	//inter Face //////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final String TAG = "onCreateOptionsMenu[CBMIA}";
		String dbMsg = "";
		try {
			getMenuInflater().inflate(R.menu.menu_cbmj , menu);
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return true;
	}

	/**
	 * Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button,
	 * so long as you specify a parent activity in AndroidManifest.xml.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final String TAG = "onOptionsItemSelected[CBMIA}";
		String dbMsg = "";
		try {
			int id = item.getItemId();
			switch ( id ) {
				case R.id.actio_5nbreathe5vomit:
//							bin_type_tv.setText(R.string.mm5nbreathe5vomit);
					return true;
				case R.id.actio_3nbreathe1stop6vomit:
//							bin_type_tv.setText(R.string.mm3nbreathe1stop6vomit);
					return true;
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return super.onOptionsItemSelected(item);
	}

		@Override
	public boolean onKeyDown(int keyCode , KeyEvent event) {
		final String TAG = "onKeyDown";
		String dbMsg = "開始";
		try {
			dbMsg = "keyCode=" + keyCode;//+",getDisplayLabel="+String.valueOf(MyEvent.getDisplayLabel())+",getAction="+MyEvent.getAction();////////////////////////////////
			myLog(TAG , dbMsg);
			switch ( keyCode ) {    //キーにデフォルト以外の動作を与えるもののみを記述★KEYCODE_MENUをここに書くとメニュー表示されない
				case KeyEvent.KEYCODE_HOME:            //3
				case KeyEvent.KEYCODE_BACK:            //4KEYCODE_BACK :keyCode；09SH: keyCode；4,MyEvent=KeyEvent{action=0 code=4 repeat=0 meta=0 scancode=158 mFlags=72}
					callQuit();
					return true;
				default:
					return false;
			}
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			return false;
		}
	}
	///"////////////////////////////////

	/**
	 * onCreateに有ったイベントなどの処理パート
	 * onCreateは終了処理後のonDestroyの後でも再度、呼び出されるので実データの割り付けなどを分離する
	 */
	public void laterCreate() {
		final String TAG = "laterCreate[CBMIA}";
		String dbMsg = "";
		try {
			toolbar.setTitle("達成度　41 点");
			
			hart_beat_ll.setOnClickListener(new View.OnClickListener() {       		//心拍数履歴
				@Override
				public void onClick(View v) {
					showhartBeat();
				}
			});

			coherencei_ll.setOnClickListener(new View.OnClickListener() {       		// コヒーレンス達成度合い
				@Override
				public void onClick(View v) {
					showSpectrum();
				}
			});

			breathing_ll.setOnClickListener(new View.OnClickListener() {       		//呼吸目安
				@Override
				public void onClick(View v) {
					showBbeathing();
				}
			});

////カメラモニター
//			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
////			layoutParams.weight = 1.0f;
//			layoutParams.gravity = Gravity.CENTER;           //17;効いてない？
//			ma_sarface_view = new SurfaceView(this);       //  プレビュー用サーフェス
//			ma_sarface_view.setLayoutParams(layoutParams);
//			Display display = getWindowManager().getDefaultDisplay();                // 画面サイズ;HardwareSize;を取得する
//			Point p = new Point();
//			display.getSize(p);
//			int hsWidth = p.x;
//			int hsHeight = p.y;
//			dbMsg += ",this[" + hsWidth + "×" + hsHeight + "]";
//			ViewGroup.LayoutParams svlp = ma_sarface_view.getLayoutParams();
////				dbMsg += ",LayoutParams[" + svlp.width + "×" + svlp.height + "]";
//			svlp.width = hsWidth;    //ma_sarface_view.getWidth();
//			svlp.height = hsHeight;        // ma_sarface_view.getWidth() * PREVIEW_HEIGHT / PREVIEW_WIDTH;
//			if ( hsHeight < hsWidth ) {
//				hsWidth = hsHeight * 4 / 3;
//				svlp.width = hsWidth;
//			} else {
//				hsHeight = hsWidth * 4 / 3;
//				svlp.height = hsHeight;
//			}
//			dbMsg += ">>[" + hsWidth + "×" + hsHeight + "]";
//			dbMsg += ">LayoutParams>[" + svlp.width + "×" + svlp.height + "]";
//			ma_sarface_view.setLayoutParams(svlp);                //ここではviewにサイズを与えるだけ。   Holderはカメラセッション開始以降で設定
//			svlp = ma_sarface_view.getLayoutParams();
//			ma_preview_fl.addView(ma_sarface_view);
//			ma_sarface_view.setId(( int ) (9999));            //生成時のみ付与する必要有り

			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		//
	}


	/**
	 * 終了/再描画前の破棄処理
	 */
	public void laterDestroy() {
		final String TAG = "laterDestroy[MA}";
		String dbMsg = "";
		try {
//			if ( OCVFRV != null ) {
//				dbMsg += ",ma_effect_flに" + ma_effect_fl.getChildCount() + "件";
//				OCVFRV.canvasRecycle();
//				ma_effect_fl.removeView(OCVFRV);
//				dbMsg += ">>" + ma_effect_fl.getChildCount();
//			}
			dbMsg += ",mCameraDevice=" + mCameraDevice;
			if ( mCameraDevice != null ) {
				closeCamera();
				stopBackgroundThread();
				dbMsg += ">>" + mCameraDevice;
			}
			isReWriteNow = true;                        //書き換え発生
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	public void callQuit() {
		final String TAG = "callQuit[MA]";
		String dbMsg = "";
		try {
//			sharedPref = PreferenceManager.getDefaultSharedPreferences(CBMJActivity.this);            //	getActivity().getBaseContext()
//			myEditor = sharedPref.edit();
////			myEditor.putString("peer_id_key" , "");      //使用した
////			boolean kakikomi = myEditor.commit();
			this.finish();
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
				finishAndRemoveTask();                      //アプリケーションのタスクを消去する事でデバッガーも停止する。
			} else {
				moveTaskToBack(true);                       //ホームボタン相当でアプリケーション全体が中断状態
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	public void reStart() {
		final String TAG = "reStart[MA}";
		String dbMsg = "";
		try {
			Intent intent = new Intent();
			intent.setClass(this , this.getClass());
			this.startActivity(intent);
			this.finish();
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	public void showhartBeat() {
		final String TAG = "showhartBeat[RBS]";
		String dbMsg = "";
		try {

			// カスタムビューを設定    http://androidguide.nomaki.jp/html/dlg/custom/customMain.html
			LayoutInflater inflater = ( LayoutInflater ) this.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.dlog_hart_beat , ( ViewGroup ) findViewById(R.id.spectrum_root));
			// アラーとダイアログ を生成
			AlertDialog.Builder builder = new AlertDialog.Builder(this );          //, R.style.MyAlertDialogStyle
			final View titolLayout = inflater.inflate(R.layout.dlog_titol , null);
			builder.setCustomTitle(titolLayout);
			TextView dlog_title_tv = ( TextView ) titolLayout.findViewById(R.id.dlog_title_tv);
			dlog_title_tv.setText("心拍数測定結果 ");  //			builder.setTitle( R.string.thumbnail_list_titol);
			ImageButton dlog_left_bt = ( ImageButton ) titolLayout.findViewById(R.id.dlog_left_bt);
//			dlog_left_bt.setImageResource(R.drawable.edit);
			ImageButton dlog_close_bt = ( ImageButton ) titolLayout.findViewById(R.id.dlog_close_bt);
			dlog_close_bt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					myDlog.dismiss();
				}
			});
			builder.setView(layout);
//			builder.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//					// タップしたアイテムの取得
//					ListView listView = (ListView)parent;
//					SampleListItem item = (SampleListItem)listView.getItemAtPosition(position);  // SampleListItemにキャスト
//
//					AlertDialog.Builder builder = new AlertDialog.Builder(CBMJActivity.this);
//					builder.setTitle("Tap No. " + String.valueOf(position));
//					builder.setMessage(item.getTitle());
//					builder.show();
//				}
//			};
//			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					// Cancel ボタンクリック処理
//				}
//			});
			builder.setPositiveButton("OK", null);
			myDlog = builder.create();                // 表示
			myDlog.show();                // 表示
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	public void showSpectrum() {
		final String TAG = "showSpectrum[RBS]";
		String dbMsg = "";
		try {

			// カスタムビューを設定    http://androidguide.nomaki.jp/html/dlg/custom/customMain.html
			LayoutInflater inflater = ( LayoutInflater ) this.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.spectrum , ( ViewGroup ) findViewById(R.id.spectrum_root));
//			GridView gridview = layout.findViewById(R.id.gridview);                // GridViewのインスタンスを生成
//			GridAdapter adapter = new GridAdapter(RecoveryBrainActivity.this , R.layout.grid_items , iconList);            // BaseAdapter を継承したGridAdapterのインスタンスを生成
//			gridview.setAdapter(adapter);                // gridViewにadapterをセット
//			gridview.setOnItemClickListener(this);            // item clickのListnerをセット
			// アラーとダイアログ を生成
			AlertDialog.Builder builder = new AlertDialog.Builder(this );          //, R.style.MyAlertDialogStyle
			final View titolLayout = inflater.inflate(R.layout.dlog_titol , null);
			builder.setCustomTitle(titolLayout);
			TextView dlog_title_tv = ( TextView ) titolLayout.findViewById(R.id.dlog_title_tv);
			dlog_title_tv.setText("コヒーレンス　スペクトラム ");  //			builder.setTitle( R.string.thumbnail_list_titol);
			ImageButton dlog_left_bt = ( ImageButton ) titolLayout.findViewById(R.id.dlog_left_bt);
//			dlog_left_bt.setImageResource(R.drawable.edit);
			ImageButton dlog_close_bt = ( ImageButton ) titolLayout.findViewById(R.id.dlog_close_bt);
			dlog_close_bt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					myDlog.dismiss();
				}
			});
			builder.setView(layout);
//			builder.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//					// タップしたアイテムの取得
//					ListView listView = (ListView)parent;
//					SampleListItem item = (SampleListItem)listView.getItemAtPosition(position);  // SampleListItemにキャスト
//
//					AlertDialog.Builder builder = new AlertDialog.Builder(CBMJActivity.this);
//					builder.setTitle("Tap No. " + String.valueOf(position));
//					builder.setMessage(item.getTitle());
//					builder.show();
//				}
//			};
//			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					// Cancel ボタンクリック処理
//				}
//			});
			builder.setPositiveButton("OK", null);
			myDlog = builder.create();                // 表示
			myDlog.show();                // 表示
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	public void showBbeathing() {
		final String TAG = "showBbeathing[RBS]";
		String dbMsg = "";
		try {
			final String[] items = {"5秒吸って、5秒吐く", "3秒吸って、1秒止めて、6秒吐く"};
			int defaultItem = 0; // デフォルトでチェックされているアイテム
			final List<Integer> checkedItems = new ArrayList<>();
			checkedItems.add(defaultItem);
			AlertDialog.Builder builder = new AlertDialog.Builder(this );          //, R.style.MyAlertDialogStyle
			// カスタムビューを設定    http://androidguide.nomaki.jp/html/dlg/custom/customMain.html
			LayoutInflater inflater = ( LayoutInflater ) this.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View titolLayout = inflater.inflate(R.layout.dlog_titol , null);
			builder.setCustomTitle(titolLayout);
			TextView dlog_title_tv = ( TextView ) titolLayout.findViewById(R.id.dlog_title_tv);
			dlog_title_tv.setText("呼吸タイミング ");  //			builder.setTitle( R.string.thumbnail_list_titol);
			ImageButton dlog_left_bt = ( ImageButton ) titolLayout.findViewById(R.id.dlog_left_bt);
//			dlog_left_bt.setImageResource(R.drawable.edit);
			ImageButton dlog_close_bt = ( ImageButton ) titolLayout.findViewById(R.id.dlog_close_bt);
			dlog_close_bt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					myDlog.dismiss();
				}
			});
			builder.setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkedItems.clear();
							checkedItems.add(which);
						}
					});
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							final String TAG = "showBbeathing[RBS]";
							String dbMsg = "";
							if (!checkedItems.isEmpty()) {
								dbMsg = "" + checkedItems.get(0);
								myLog(TAG , dbMsg);

							}
						}
					}) ;
			builder.show();
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this fragment.
		 */

		private static final String ARG_SECTION_NUMBER = "section_number";

		public PlaceholderFragment() {
		}

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			final String TAG = "PlaceholderFragment[CBMIA}";
			String dbMsg = "";
			PlaceholderFragment fragment = null;
			try {
				fragment = new PlaceholderFragment();
				Bundle args = new Bundle();
				args.putInt(ARG_SECTION_NUMBER , sectionNumber);
				fragment.setArguments(args);
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState) {
			final String TAG = "onCreateView[CBMIA}";
			String dbMsg = "";
			View rootView = null;
			try {
				rootView = inflater.inflate(R.layout.fragment_cbmj , container , false);
				TextView textView = ( TextView ) rootView.findViewById(R.id.section_label);
				textView.setText(getString(R.string.section_format , getArguments().getInt(ARG_SECTION_NUMBER)));
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
			return rootView;
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			final String TAG = "SectionsPagerAdapter[CBMIA}";
			String dbMsg = "";
			try {
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}

		/**
		 * getItem is called to instantiate the fragment for the given page.
		 * Return a PlaceholderFragment (defined as a static inner class below).
		 */
		@Override
		public Fragment getItem(int position) {
			final String TAG = "getItem[CBMIA}";
			String dbMsg = "";
			Fragment retFragment = null;
			try {
				retFragment = PlaceholderFragment.newInstance(position + 1);
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
			return retFragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			final String TAG = "getCount[CBMIA}";
			String dbMsg = "";
			int retInt = 3;
			try {
				dbMsg = "retInt= " + retInt;
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
			return retInt;
		}
	}

	//プレビュー////////////////////////////////////////////////////////////////////////////////////エフェクト//
//	/**
//	 * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
//	 * {@link TextureView}.
//	 */
//	private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
//
//		@Override
//		public void onSurfaceTextureAvailable(SurfaceTexture texture , int width , int height) {
//			final String TAG = "onSurfaceTextureAvailable[MA]";
//			String dbMsg = "";
//			try {
//				dbMsg = "[" + width + "×" + height + "]";                   // [1920×1080]
//				openCamera(width , height);
//				myLog(TAG , dbMsg);
//			} catch (Exception er) {
//				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//			}
//		}
//
//		@Override
//		public void onSurfaceTextureSizeChanged(SurfaceTexture texture , int width , int height) {
//			final String TAG = "onSurfaceTextureSizeChanged[MA]";
//			String dbMsg = "";
//			try {
//				dbMsg = "[" + width + "×" + height + "]DISP_DEGREES=" + DISP_DEGREES;    // [810×1080]DISP_DEGREES=0
//				if ( OCVFRV != null ) {
//					dbMsg += ",camera=" + mSensorOrientation + "dig";
//					setEffectViewSize();
//				}
//				myLog(TAG , dbMsg);
//			} catch (Exception er) {
//				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//			}
//		}
//
//		@Override
//		public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
//			final String TAG = "onSurfaceTextureDestroyed[MA]";
//			String dbMsg = "発生";
//			try {
//				myLog(TAG , dbMsg);
//			} catch (Exception er) {
//				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//			}
//			return true;
//		}
//
//		@Override
//		public void onSurfaceTextureUpdated(SurfaceTexture texture) {
//			final String TAG = "onSurfaceTextureUpdated[MA]";
//			String dbMsg = "";
//			try {
//// Surface surface = new Surface(texture);  //までは出来る　がBitmap取得の方法不明
//////				synchronized (mCaptureSync) {                //http://serenegiant.com/blog/?p=2074&page=3
//// sendPreviewBitMap(mTextureView.getId());		//ここで送るとプレビューに干渉
//				myLog(TAG , dbMsg);
//			} catch (Exception er) {
//				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//			}
//		}
//
//	};
	//プレビュー ; Surfac////////////////////////////////////////////////////////////////////////////////////
	//		https://qiita.com/zaburo/items/d9d07eb4d87d21308124
	//		http://tech.pjin.jp/blog/2014/02/20/androidtips-surfaceview%E3%82%92%E4%BD%BF%E3%81%A3%E3%81%A6%E3%81%BF%E3%81%BE%E3%81%97%E3%81%9F/
//		https://qiita.com/fslasht/items/be41e84cfbc4bbb91af7
	private SurfaceHolder.Callback sarfacCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceCreated(SurfaceHolder surfaceHolder) {
			final String TAG = "surfaceCreated[MA}";
			String dbMsg = "";
			try {
				dbMsg += "surfaceHolder=" + surfaceHolder;
				ma_sarfaceeHolder = surfaceHolder;
				int surfaceWidth = surfaceHolder.getSurfaceFrame().width();
				int surfaceHeight = surfaceHolder.getSurfaceFrame().height();
				dbMsg += "[" + surfaceWidth + "×" + surfaceHeight + "]";
				openCamera(surfaceWidth , surfaceHeight);

				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder surfaceHolder , int format , int surfaceWidth , int surfaceHeight) {
			final String TAG = "surfaceChanged[MA}";
			String dbMsg = "";
			try {
				dbMsg += "surfaceHolder=" + surfaceHolder;
				dbMsg += ",format=" + format;
				dbMsg += "[" + surfaceWidth + "×" + surfaceHeight + "]DISP_DEGREES=" + DISP_DEGREES;    // [810×1080]DISP_DEGREES=0
				if ( surfaceHeight < surfaceWidth ) {
					dbMsg += ">横画面";
				} else {
					dbMsg += ">縦画面";
				}
				configureTransform(surfaceWidth , surfaceHeight);                  //org

//				if ( OCVFRV != null ) {
//					dbMsg += ",camera=" + mSensorOrientation + "dig";
//					setEffectViewSize();
//				}
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
			final String TAG = "surfaceDestroyed[MA}";
			String dbMsg = "";
			try {
				dbMsg += "surfaceHolder=" + surfaceHolder;
				laterDestroy();
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}
	};

	///カメラ/////////////////////////////////////////////////////////////////////////////////プレビュー//////
	/**
	 * A {@link CameraCaptureSession } for camera preview.
	 * createCameraPreviewSessionのonConfiguredで取得
	 */
	private CameraCaptureSession mCaptureSession;
	private CameraDevice mCameraDevice;            // A reference to the opened {@link CameraDevice}.
	private String mCameraId;        //ID of the current {@link CameraDevice}.
	/**
	 * createCameraPreviewSession で取得
	 * startAutoFocus でcaptureBuilder.addTarget
	 */
	private Surface surface;
	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
	private MediaActionSound mSound;    //撮影音のためのMediaActionSound

	private CaptureRequest.Builder mPreviewRequestBuilder;        // {@link CaptureRequest.Builder} for the camera preview
	private CaptureRequest mPreviewRequest;    //{@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}

	private Semaphore mCameraOpenCloseLock = new Semaphore(1);        // A {@link Semaphore} to prevent the app from exiting before closing the camera.

	/**
	 * An {@link ImageReader} that handles still image capture.
	 */
	private ImageReader mImageReader;
	private int mMaxImages = 2;                                    //読込み枚数
	private ImageReader mPreviewReader;

	/**
	 * This is the output file for our picture.
	 */
	private File mFile;

	static {
		ORIENTATIONS.append(Surface.ROTATION_0 , 90);
		ORIENTATIONS.append(Surface.ROTATION_90 , 0);
		ORIENTATIONS.append(Surface.ROTATION_180 , 270);
		ORIENTATIONS.append(Surface.ROTATION_270 , 180);
	}

	private static final int REQUEST_CAMERA_PERMISSION = 1;
	private static final int STATE_PREVIEW = 0;    //Camera state: Showing camera preview.
	private static final int STATE_WAITING_LOCK = 1;        // Camera state: Waiting for the focus to be locked.
	/**
	 * Camera state: Waiting for the exposure to be precapture state.
	 */
	private static final int STATE_WAITING_PRECAPTURE = 2;

	/**
	 * Camera state: Waiting for the exposure state to be something other than precapture.
	 */
	private static final int STATE_WAITING_NON_PRECAPTURE = 3;

	/**
	 * Camera state: Picture was taken.
	 */
	private static final int STATE_PICTURE_TAKEN = 4;
	/**
	 * 使用しているカメラの現状
	 * The current state of camera state for taking pictures.
	 * @see #
	 */
	private int mState = STATE_PREVIEW;
	/**
	 * Max preview width that is guaranteed by Camera2 API
	 */
	private static final int MAX_PREVIEW_WIDTH = 1920;
	/**
	 * Max preview height that is guaranteed by Camera2 API
	 */
	private static final int MAX_PREVIEW_HEIGHT = 1080;
	private static float MAX_PREVIEW_ASPECT;
	/**
	 * 実際に配置できたプレビュー幅
	 */
	private static int PREVIEW_WIDTH = MAX_PREVIEW_WIDTH;
	/**
	 * 実際に配置できたプレビュー高さ
	 */
	private static int PREVIEW_HEIGHT = MAX_PREVIEW_HEIGHT;
	/**
	 * 現在の端末の向き
	 */
	private static int DISP_DEGREES;
	/**
	 * 保存処理中
	 */
	public boolean isPhotography = false;

	/**
	 * The {@link android.util.Size} of camera preview.
	 * setUpCameraOutputs で取得したカメラ出力のプレビューサイズ
	 * createCameraPreviewSession , configureTransform , startAutoFocus　で変数に移して使用
	 */
	private Size mPreviewSize;
	public int mSensorOrientation;

	/**
	 * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
	 * manager.openCamera() メソッドで指定
	 */
	private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

		@Override
		public void onOpened(CameraDevice cameraDevice) {
			final String TAG = "onOpened[MA]";
			String dbMsg = "";
			try {
				// This method is called when the camera is opened.  We start camera preview here.
				mCameraOpenCloseLock.release();
				mCameraDevice = cameraDevice;
				dbMsg += ",mCameraDevice = " + mCameraDevice.getId();
				createCameraPreviewSession();
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}

		@Override
		public void onDisconnected(CameraDevice cameraDevice) {
			final String TAG = "mStateCallback.onDisconnected[MA]";
			String dbMsg = "";
			try {
				mCameraOpenCloseLock.release();
				cameraDevice.close();
				mCameraDevice = null;
				dbMsg += ",mCameraDevice = " + mCameraDevice;
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}

		@Override
		public void onError(CameraDevice cameraDevice , int error) {
			final String TAG = "mStateCallback.onError[MA]";
			String dbMsg = "";
			mCameraOpenCloseLock.release();
			cameraDevice.close();
			mCameraDevice = null;
			Activity activity = CBMJActivity.this;    // getActivity();
			if ( null != activity ) {
				String titolStr = "再起動してください";
				String mggStr = "エラーが発生しました\n" + error;
				messageShow(titolStr , mggStr);
				activity.finish();
			}
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + error);
		}
	};

	/**
	 * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a still image is ready to be saved.
	 * setUpCameraOutputsで設定
	 */
	private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
		@Override
		public void onImageAvailable(ImageReader reader) {
			final String TAG = "mOnImageAvailableListener[MA]";
			String dbMsg = "";
			try {
				dbMsg = "mState=" + mState;   //撮影時も STATE_PREVIEWになっている
				Image rImage = reader.acquireLatestImage();     //キューから 最新のものを取得し、古いものを削除します
				// ;2枚保持させて acquireNextImage ?  キューから次のImageを取得
				if ( rImage != null ) {
					dbMsg += ",rImage;Timestamp=" + rImage.getTimestamp();
					dbMsg += ",isPhotography=" + isPhotography;   //撮影時も falseになっている
					if ( !isPhotography ) {     //撮影中で無ければ
						dbMsg += "プレビュー取得";
					} else {
						dbMsg += ",静止画撮影処理";			//；writeFolder=" + writeFolder;
						long timestamp = System.currentTimeMillis();
						dbMsg += ",timestamp=" + timestamp;
//						if ( UTIL == null ) {
//							UTIL = new CS_Util();
//						}
//						UTIL.maikOrgPass( writeFolder);
//						File saveFolder = new File(writeFolder);
//						java.text.DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ssZ" , Locale.JAPAN);
//						String dtStr = df.format(timestamp);
//						dbMsg += ",dtStr=" + dtStr;
//						String[] dtStrs = dtStr.split("/");
//						int lCount = 0;
//						String pFolderName = saveFolder.getPath();
//						for ( String rStr : dtStrs ) {
//							dbMsg += "(" + lCount + ")" + rStr;
//							if ( lCount < 3 ) {
//								File pFolder = new File(pFolderName , rStr);
//								pFolderName = pFolder.toString();
//								UTIL.maikOrgPass( pFolderName);
//								dbMsg += ",dtStr=" + pFolderName;
//							}
//							lCount++;
//						}
//						dtStr = dtStr.replaceAll("/" , "");
//						dtStr = dtStr.replaceAll(":" , "");
//						dtStr = dtStr.substring(0 , dtStr.length() - 5) + ".jpg";
//						mFile = new File(pFolderName , dtStr);                 //getActivity().getExternalFilesDir(null)
//						dbMsg += ",mFile=" + mFile.toString();
						//maxImages (2) has already been acquired, call #close before acquiring more.
//						ImageSaver IS = new ImageSaver(rImage , pFolderName , ma_iv , dtStr);//acquireNextImage ?	第二引数以降は追加     /
//						mBackgroundHandler.post(IS);
					}
				}
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}
	};

	///エフェクト更新処理//////////////////////////////////////////////////////////////
	public int fpsCount = 0;
	public int fpsLimi = 30;

	/**
	 * 受け取ったIDのViewからBitmapを抽出しエフェクトビューへ送る。
	 * エフェクトビューが無ければ作成して、動作指定が無くなった時点でViewを破棄する
	 */
	public void sendPreviewBitMap(int targetViewID) {
		final String TAG = "sendPreviewBitMap[MA]";
		String dbMsg = "";
		try {
//			dbMsg += ",顔検出実行中=" + isFaceRecognition;
			dbMsg += ",targetViewID=" + targetViewID;
			if ( -1 < targetViewID ) {               // ;                 //
//				if ( isFaceRecognition ) {               // ;                 //
//					if ( OCVFRV != null ) {
						fpsCount++;
						dbMsg += "(" + fpsCount + "/" + fpsLimi + "フレーム)";          //実測 8回で送信
//						dbMsg += ",前回処理終了=" + OCVFRV.getCompletion();
//						if ( OCVFRV.getCompletion() ) {    //onDrawが終了するまでfalseが返る     && fpsLimi < fpsCount
							fpsCount = 0;
							shotBitmap = null;
//							if ( targetViewID == mTextureViewID ) {
//								dbMsg += ",TextureView";
//								shotBitmap = (( TextureView ) findViewById(targetViewID)).getBitmap();
//							} else {
								dbMsg += ",sarfacee";
								if ( ma_sarfaceeHolder != null ) {
									int surfaceWidth = ma_sarfaceeHolder.getSurfaceFrame().width();
									int surfaceHeight = ma_sarfaceeHolder.getSurfaceFrame().height();
									dbMsg += "[" + surfaceWidth + "×" + surfaceHeight + "]";
									ma_sarface_view.setDrawingCacheEnabled(true);      // キャッシュを取得する設定にする
									ma_sarface_view.destroyDrawingCache();             // 既存のキャッシュをクリアする☆通常はこちらが先
									shotBitmap = ma_sarface_view.getDrawingCache();    // キャッシュを作成して取得する       Bitmap bmpOrig
								}
//							}
							if ( shotBitmap != null ) {
								dbMsg += ",bitmap[" + shotBitmap.getWidth() + "×" + shotBitmap.getHeight() + "]";
								int byteCount = shotBitmap.getByteCount();
								dbMsg += "" + byteCount + "バイト";
								dbMsg += ",disp=" + DISP_DEGREES + "dig";
								mSensorOrientation = getOrientation(DISP_DEGREES);
								dbMsg += ",camera=" + mSensorOrientation + "dig";
//								List< Rect > retArray = OCVFRV.readFrameRGB(shotBitmap , mSensorOrientation);
//								if ( retArray != null ) {
//									dbMsg += ">結果>" + retArray.size() + "箇所検出";
//								}
							} else {
								dbMsg += ",shotBitmap = null";
							}
//						} else {
//							dbMsg = "";    //余計なコメントを出さない
//						}
//					} else {
//						dbMsg += ",OCVFRV = null>>view追加";
//						setEffectView();
//					}
//				} else {                            //顔検出中で無ければ
//					removetEffectView();            //viewを破棄
//				}
			}
			if ( !dbMsg.equals("") ) {
				myLog(TAG , dbMsg);
			}
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

//	Canvas svCanvas;

	/**
	 * Bitmapの汎用保存
	 */
	public void savePreviewBitMap(int targetViewID) {
		final String TAG = "savePreviewBitMap[MA]";
		String dbMsg = "";
		try {
//			dbMsg += ",顔検出実行中=" + isFaceRecognition;
			dbMsg += ",targetViewID=" + targetViewID;
			if ( -1 < targetViewID ) {               // ;                 //
//				if ( isFaceRecognition ) {               // ;                 //
////				dbMsg += "isReWriteNow=" + isReWriteNow;
////				if ( !isReWriteNow ) {                                    // //書き換え終了(onResume～onPause)
//					if ( OCVFRV != null ) {
//						fpsCount++;
//						dbMsg += "(" + fpsCount + "/" + fpsLimi + "フレーム)";          //実測 8回で送信
//						dbMsg += ",前回処理終了=" + OCVFRV.getCompletion();
//						if ( OCVFRV.getCompletion() ) {    //onDrawが終了するまでfalseが返る     && fpsLimi < fpsCount
//							fpsCount = 0;
				shotBitmap = null;
//				if ( targetViewID == mTextureViewID ) {
//					dbMsg += ",TextureView";
//					shotBitmap = (( TextureView ) findViewById(targetViewID)).getBitmap();
//				} else {
					dbMsg += ",sarfacee";
					if ( ma_sarfaceeHolder != null ) {
						Canvas svCanvas = ma_sarfaceeHolder.lockCanvas();              //unlockCanvasはAPIL17で廃止
						int surfaceWidth = ma_sarfaceeHolder.getSurfaceFrame().width();
						int surfaceHeight = ma_sarfaceeHolder.getSurfaceFrame().height();
						dbMsg += "[" + surfaceWidth + "×" + surfaceHeight + "]";
						shotBitmap = Bitmap.createBitmap(surfaceWidth , surfaceHeight , Bitmap.Config.ARGB_8888);  //別途BitmapとCanvasを用意する；この時点は真っ黒
//						if (svCanvas == null) {
//										svCanvas = new Canvas(shotBitmap);
//									}
//// Viewの描画キャッシュを使用する方法 ;0バイトになる  			 http://blog.lciel.jp/blog/2013/12/16/android-capture-view-image/
//						ma_sarface_view.setDrawingCacheEnabled(true);      // キャッシュを取得する設定にする
//						ma_sarface_view.destroyDrawingCache();             // 既存のキャッシュをクリアする☆通常はこちらが先
//						shotBitmap = ma_sarface_view.getDrawingCache();    // キャッシュを作成して取得する       Bitmap bmpOrig
////									Matrix matrix = new Matrix();
////									matrix.postRotate(270);
////									shotBitmap= Bitmap.createBitmap(bmpOrig, 0, 0, surfaceWidth, surfaceHeight, matrix, true);  // 回転したビットマップを作成
					}
//				}
				if ( shotBitmap != null ) {
					dbMsg += ",shotBitmap[" + shotBitmap.getWidth() + "×" + shotBitmap.getHeight() + "]";
					int byteCount = shotBitmap.getByteCount();
					dbMsg += "" + byteCount + "バイト";
					if ( 0 < byteCount ) {
//						dbMsg += ",isPhotography=" + isPhotography;
//						if ( !isPhotography ) {
//							isPhotography = true;
//						saveFileName = writeFolder + File.separator + "pre.jpg";
//						dbMsg += ",saveFileName=" + saveFileName;
//						BmpSaver BS = new BmpSaver(this , this , shotBitmap , saveFileName , ma_iv);

//						dbMsg += ",disp=" + DISP_DEGREES + "dig";
//						mSensorOrientation = getOrientation(DISP_DEGREES);
//						dbMsg += ",camera=" + mSensorOrientation + "dig";

//								BmpSaver BS = new BmpSaver(this,this,shotBitmap , writeFolder , ma_iv , saveFileName);
//						mBackgroundHandler.post(BS);
//						shotBitmap.recycle();
//						}
					}
				} else {
					dbMsg += ",shotBitmap = null";
				}
//						} else {
//							dbMsg = "";    //余計なコメントを出さない
//						}
//					} else {
//						dbMsg += ",OCVFRV = null>>view追加";
//						setEffectView();
//					}
//				} else {                            //顔検出中で無ければ
//					removetEffectView();            //viewを破棄
//				}
			}
//			if ( !dbMsg.equals("") ) {
			isPhotography = false;
			myLog(TAG , dbMsg);
//			}
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}


	private class EffectSendData {
		Bitmap sendBitmap;
		int sensorOrientation;
	}

	private class EffectSendTask extends AsyncTask< EffectSendData, Void, EffectSendData > {
		/**
		 * The system calls this to perform work in a worker thread and
		 * delivers it the parameters given to AsyncTask.execute()
		 */
		protected EffectSendData doInBackground(EffectSendData... pram) {
			final String TAG = "EffectSendTask.DIB[MA]";
			String dbMsg = "";
			try {
				Bitmap shotBitmap = pram[0].sendBitmap;
				dbMsg += ",bitmap[" + shotBitmap.getWidth() + "×" + shotBitmap.getHeight() + "]" + shotBitmap.getByteCount();
				int mSensorOrientation = pram[0].sensorOrientation;
				dbMsg += ",camera=" + mSensorOrientation + "dig";
//				OCVFRV.readFrameRGB(shotBitmap , mSensorOrientation);
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
			return pram[0];
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(EffectSendData result) {       //doInBackgroundの  returnを受け取る
			final String TAG = "EffectAddTask.ope[MA]";
			String dbMsg = "";
			try {

				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}
	}

//	/**
//	 * assetsのdirフォルダに置かれたDetegerデータの内容を /data/data/.../files/ にコピーします。
//	 * ☆assetsのフルパス名は拾えないのでアプリケーションがリ利用可能なエリアに作成
//	 */
//	private void copyAssets(String dir) throws IOException {
//		final String TAG = "copyAssets[MA}";      // , long haarcascadesLastModified
//		String dbMsg = "";
//		try {
//			dbMsg = "dir=" + dir;
////			MainActivity MA = new MainActivity();
//			//			dbMsg += ",認証ファイル最終更新日=" + haarcascadesLastModified;
//			byte[] buf = new byte[8192];
//			int size;
//			boolean isCopy = false;    //初回使用時なと、強制的にコピーする
//			File dst = new File(getApplicationContext().getFilesDir() , dir);
//			if ( !dst.exists() ) {                   //作成されていない場合；インストール直後のみ
//				dst.mkdirs();
//				dst.setReadable(true , false);
//				dst.setWritable(true , false);
//				dst.setExecutable(true , false);
//				dbMsg += ">>作成";
//				isCopy = true;
////			}
//				int readedCount = dst.list().length;
//				dbMsg += ",読込み済み=" + readedCount + "件";
//				if ( readedCount < 10 ) {
//					isCopy = true;
//				}
//				for ( String filename : getApplicationContext().getAssets().list(dir) ) {
//					File file = new File(dst , filename);
//					Long lastModified = file.lastModified();
////				if ( isCopy || haarcascadesLastModified < lastModified ) {    //無ければ
//					dbMsg += "," + filename + ";" + lastModified;
////					haarcascadesLastModified = lastModified;
//					OutputStream out = new FileOutputStream(file);
//					InputStream in = getApplicationContext().getAssets().open(dir + "/" + filename);
//					while ( (size = in.read(buf)) >= 0 ) {
//						if ( size > 0 ) {
//							out.write(buf , 0 , size);
//						}
//					}
//					in.close();
//					out.close();
//					file.setReadable(true , false);
//					file.setWritable(true , false);
//					file.setExecutable(true , false);
//					dbMsg += ">>コピー";
////				}
//				}
//			}
//			myLog(TAG , dbMsg);
//		} catch (Exception er) {
//			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//		}
//	}
	//プレビュー////////////////////////////////////////////////////////////////////////////////////エフェクト//
//	/**
//	 * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
//	 * {@link TextureView}.
//	 */
//	private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
//
//		@Override
//		public void onSurfaceTextureAvailable(SurfaceTexture texture , int width , int height) {
//			final String TAG = "onSurfaceTextureAvailable[MA]";
//			String dbMsg = "";
//			try {
//				dbMsg = "[" + width + "×" + height + "]";                   // [1920×1080]
//				openCamera(width , height);
//				myLog(TAG , dbMsg);
//			} catch (Exception er) {
//				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//			}
//		}
//
//		@Override
//		public void onSurfaceTextureSizeChanged(SurfaceTexture texture , int width , int height) {
//			final String TAG = "onSurfaceTextureSizeChanged[MA]";
//			String dbMsg = "";
//			try {
////				PREVIEW_WIDTH = width;                    //mTextureView.getWidth();
////				PREVIEW_HEIGHT = height;                //mTextureView.getHeight();
//				dbMsg = "[" + width + "×" + height + "]DISP_DEGREES=" + DISP_DEGREES;    // [810×1080]DISP_DEGREES=0
////				configureTransform(width , height);
//				if ( OCVFRV != null ) {
//					dbMsg += ",camera=" + mSensorOrientation + "dig";
////					OCVFRV.setCondition();
//					setEffectViewSize();
//				}
//				myLog(TAG , dbMsg);
//			} catch (Exception er) {
//				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//			}
//		}
//
//		@Override
//		public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
//			final String TAG = "onSurfaceTextureDestroyed[MA]";
//			String dbMsg = "発生";
//			try {
//				myLog(TAG , dbMsg);
//			} catch (Exception er) {
//				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//			}
//			return true;
//		}
//
//		@Override
//		public void onSurfaceTextureUpdated(SurfaceTexture texture) {
//			final String TAG = "onSurfaceTextureUpdated[MA]";
//			String dbMsg = "";
//			try {
//// Surface surface = new Surface(texture);  //までは出来る　がBitmap取得の方法不明
//////				synchronized (mCaptureSync) {                //http://serenegiant.com/blog/?p=2074&page=3
//// sendPreviewBitMap(mTextureView.getId());		//ここで送るとプレビューに干渉
//				myLog(TAG , dbMsg);
//			} catch (Exception er) {
//				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//			}
//		}
//
//	};

	//プレビューの画像取得 ///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	private final ImageReader.OnImageAvailableListener mOnPreviwListener = new ImageReader.OnImageAvailableListener() {
//		@Override
//		public void onImageAvailable(ImageReader reader) {
//			final String TAG = "mOnPreviwListener[MA]";
//			String dbMsg = "";
//			try {
//				if ( OCVFRV != null ) {
//					dbMsg += ",completion=" + OCVFRV.getCompletion() ;
//					if ( OCVFRV.getCompletion() ) {
////			if( camera.mImageReader != null) {
//						Image image = reader.acquireLatestImage();
//						if ( image != null ) {
//							int width = image.getWidth();
//							int height = image.getHeight();
//							long timestamp = image.getTimestamp();
//							dbMsg += ",image[" + width + "×" + height + "]Format=" + image.getFormat();
//							dbMsg += ",=" + timestamp + "," + image.getPlanes().length + "枚";
//							ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
//							final byte[] imageBytes = new byte[imageBuf.remaining()];        //直接渡すと.ArrayIndexOutOfBoundsException: length=250,095; index=15,925,248
//							dbMsg += ",imageBytes=" + imageBytes.length;
//							imageBuf.get(imageBytes);
//
//							final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes , 0 , imageBytes.length);
//							ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//							bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , byteArrayOutputStream);
//							dbMsg += ",bitmap[" + bitmap.getWidth() + "×" + bitmap.getHeight() + "]";
//							int byteCount = bitmap.getByteCount();
//							dbMsg += "" + byteCount + "バイト";
//
////					degrees = camera.getCameraRotation();
////					dbMsg += "," + degrees + "dig";
////						sendPreview(bitmap);
//							byteArrayOutputStream.close();
////						if ( bitmap != null ) {
////							bitmap.recycle();
////							byteCount = bitmap.getByteCount();
////							dbMsg += ">>" + byteCount + "バイト";
////						}
//						} else {
//							dbMsg += ",image = null ";
//						}
//						image.close();
////			}
//					} else {
//						dbMsg += ",getCompletion = false ";
//					}
//				} else {
//					dbMsg += ",OCVFRV = null ";
//				}
//				myLog(TAG , dbMsg);
//			} catch (Exception er) {
//				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//			}
//		}
//	};


//	private final ImageReader.OnImageAvailableListener mOnPreviwListener = new ImageReader.OnImageAvailableListener() {
//
//		@Override
//		public void onImageAvailable(ImageReader reader) {
//			final String TAG = "mOnPreviwListener[MA]";
//			String dbMsg = "";
//			try {
//					sendPreview(reader);
//				myLog(TAG , dbMsg);
//			} catch (Exception er) {
//				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//			}
//		}
//	};

//	public ImageReader prevReader;
//	public void sendPreview(ImageReader _prevReader) {
//		prevReader = _prevReader;
////		shotBitmap = _shotBitmap;
//		// 別スレッドを実行
//		CBMJActivity.this.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				final String TAG = "sendPreview[MA]";
//				String dbMsg = "";
//				try {
////					final Bitmap shotBitmap = null;
////					if ( OCVFRV != null ) {
////			if( camera.mImageReader != null) {
//					Image image = prevReader.acquireLatestImage();
//					if ( image != null ) {
//						int width = image.getWidth();
//						int height = image.getHeight();
//						long timestamp = image.getTimestamp();
//						dbMsg += ",image[" + width + "×" + height + "]Format=" + image.getFormat();
//						dbMsg += ",=" + timestamp + "," + image.getPlanes().length + "枚";
//						ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
//						final byte[] imageBytes = new byte[imageBuf.remaining()];        //直接渡すと.ArrayIndexOutOfBoundsException: length=250,095; index=15,925,248
//						dbMsg += ",imageBytes=" + imageBytes.length;
//						imageBuf.get(imageBytes);
//
//						final Bitmap shotBitmap = BitmapFactory.decodeByteArray(imageBytes , 0 , imageBytes.length);
//						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//						shotBitmap.compress(Bitmap.CompressFormat.JPEG , 100 , byteArrayOutputStream);
//						dbMsg += ",bitmap[" + shotBitmap.getWidth() + "×" + shotBitmap.getHeight() + "]";
//						int byteCount = shotBitmap.getByteCount();
//						dbMsg += "" + byteCount + "バイト";
//						//			}
//						OCVFRV.readFrameRGB(shotBitmap);
//
//						if ( shotBitmap != null ) {
//							shotBitmap.recycle();
//							byteCount = shotBitmap.getByteCount();
//							dbMsg += ">>" + byteCount + "バイト";
//						}
//						image.close();
//					} else {
//						dbMsg += ",image = null ";
//					}
////					} else {
////						dbMsg += ",OCVFRV = null ";
////					}
//					myLog(TAG , dbMsg);
//				} catch (Exception er) {
//					myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//				}
//			}
//		});
//	}


	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////プレビューの画像取得 ///

	/**
	 * フラッシュが使えるか否か
	 * Whether the current camera device supports Flash or not.
	 */
	private boolean mFlashSupported;

	/**
	 * カメラの角度
	 * Orientation of the camera sensor
	 */

	/**
	 * JPEG捕獲に関連したそのハンドル・イベント。
	 * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
	 * <p>
	 * createCameraPreviewSessionのonConfiguredで	CONTROL_AF_MODE_CONTINUOUS_PICTURE
	 * lockFocus()で 								CameraMetadata.CONTROL_AF_TRIGGER_START
	 * unlockFocus()で  							CameraMetadata.CONTROL_AF_TRIGGER_CANCEL				mState = STATE_PREVIEW;
	 * mCaptureSession.setRepeatingRequest(mPreviewRequest , mCaptureCallback , mBackgroundHandler); 	 // プレビューに戻る
	 * runPrecaptureSequenceで						CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START		mState = STATE_WAITING_PRECAPTURE;
	 * 追加
	 * copyPreview()で CameraDevice.TEMPLATE_STILL_CAPTURE
	 * SendPreview.runで mCaptureSession.setRepeatingRequest(mPreviewRequest , mCaptureCallback , mBackgroundHandler);    //プレビュ再開
	 */
	private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
		private void process(CaptureResult result) {
			final String TAG = "process[MA]";
			String dbMsg = "";
			try {
				///6/18	この時点で破棄動作に入っていないか

//				dbMsg += "result=" + result;
				dbMsg += "mState=" + mState;
				switch ( mState ) {
					case STATE_PREVIEW: {                //0 ＜＜初期値とunlockFocus() 、We have nothing to do when the camera preview is working normally.
						dbMsg = "";    //余計なコメントを出さない
//						if ( mTextureView != null ) {
//							sendPreviewBitMap(mTextureView.getId());     //ここから送ると回転動作にストレス発生？ ？
//						} else if ( ma_sarface_view != null ) {
							sendPreviewBitMap(ma_sarface_view.getId());
//						}
						break;
					}
					case STATE_WAITING_LOCK: {        //1<<lockFocus()から
						Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
						dbMsg += ",afState=" + afState;
						if ( afState == null ) {
							captureStillPicture();
						} else if ( CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState || CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState ) {
							// CONTROL_AE_STATE can be null on some devices
							Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
							if ( aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED ) {
								mState = STATE_PICTURE_TAKEN;  //4;
								captureStillPicture();
							} else {
								runPrecaptureSequence();
							}
						}
						isPhotography = true;
						break;
					}
					case STATE_WAITING_PRECAPTURE: {    //2	<runPrecaptureSequence// CONTROL_AE_STATE can be null on some devices
						Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
						dbMsg += ",aeState=" + aeState;
						if ( aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE || aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED ) {
							mState = STATE_WAITING_NON_PRECAPTURE;     //3
						}
						break;
					}
					case STATE_WAITING_NON_PRECAPTURE: {    //3<STATE_WAITING_PRECAPTURE	// CONTROL_AE_STATE can be null on some devices
						Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
						dbMsg += ",aeState=" + aeState;
						if ( aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE ) {
							mState = STATE_PICTURE_TAKEN;
							captureStillPicture();
						}
						break;
					}
				}
				if ( !dbMsg.equals("") ) {
					dbMsg += ">>mState=" + mState;
					myLog(TAG , dbMsg);
				}
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}

		@Override
		public void onCaptureProgressed(CameraCaptureSession session , CaptureRequest request , CaptureResult partialResult) {
			final String TAG = "onCaptureProgressed[MA]";
			String dbMsg = "";
			try {
				process(partialResult);
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}

		/**
		 * 撮影完了
		 * */
		@Override
		public void onCaptureCompleted(CameraCaptureSession session , CaptureRequest request , TotalCaptureResult result) {
			final String TAG = "MCC.onCaptureCompleted[MA]";
			String dbMsg = "";
			try {
				dbMsg += ",CaptureRequest=" + request.getKeys().size() + "件";    //CaptureRequest=android.hardware.camera2.CaptureRequest@ed0bb9ae,TotalCaptureResult=android.hardware.camera2
				dbMsg += ",TotalCaptureResult=" + result.getRequest().getKeys().size() + "件";   // TotalCaptureResult@17e91b3
				process(result);

//				Bitmap shotBitmap = mTextureView.getBitmap();
//				dbMsg += ",bitmap[" + shotBitmap.getWidth() + "×" + shotBitmap.getHeight() + "]";
//				int byteCount = shotBitmap.getByteCount();
//				dbMsg += "" + byteCount + "バイト";
//				sendPreviewbitMap( shotBitmap);
//
//				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}
	};
	//機器の状況取得////////////////////////////////////////////////////////////////////// /

	/**
	 * スクリーン回転の角度からカメラの角度を返す
	 * Retrieves the JPEG orientation from the specified screen rotation.
	 * @param rotation The screen rotation.
	 * @return The JPEG orientation (one of 0, 90, 270, and 360)
	 */
	private int getOrientation(int rotation) {
		final String TAG = "getOrientation[MA]";
		String dbMsg = "";
//		int retInt = 0;
		try {
			dbMsg = "Disp=" + rotation;
			dbMsg += "、camera=" + mSensorOrientation;
			mSensorOrientation = ORIENTATIONS.get(rotation) % 360;   //			retInt = (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
			dbMsg += ">>=" + mSensorOrientation;

			// Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
			// We have to take that into account and rotate JPEG properly.
			// For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
			// For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return mSensorOrientation;
	}

	/**
	 * Compares two {@code Size}s based on their areas.
	 */
	static class CompareSizesByArea implements Comparator< Size > {

		@Override
		public int compare(Size lhs , Size rhs) {
			final String TAG = "compare[MA]";
			String dbMsg = "";
			try {
				dbMsg += "lhs[" + lhs.getWidth() + "×" + lhs.getHeight() + "]";
				dbMsg += ",rhs[" + rhs.getWidth() + "×" + rhs.getHeight() + "]";
				// We cast here to ensure the multiplications won't overflow
				if ( debugNow ) {
					Log.i(TAG , dbMsg + "");
				}
			} catch (Exception er) {
				Log.e(TAG , dbMsg + "");
			}
			return Long.signum(( long ) lhs.getWidth() * lhs.getHeight() - ( long ) rhs.getWidth() * rhs.getHeight());
		}
	}
//
//	/**
//	 * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
//	 * is at least as large as the respective texture view size, and that is at most as large as the
//	 * respective max size, and whose aspect ratio matches with the specified value. If such size
//	 * doesn't exist, choose the largest one that is at most as large as the respective max size,
//	 * and whose aspect ratio matches with the specified value.
//	 * @param choices           The list of sizes that the camera supports for the intended output
//	 *                          class
//	 * @param textureViewWidth  The width of the texture view relative to sensor coordinate
//	 * @param textureViewHeight The height of the texture view relative to sensor coordinate
//	 * @param maxWidth          The maximum width that can be chosen
//	 * @param maxHeight         The maximum height that can be chosen
//	 * @param aspectRatio       The aspect ratio
//	 * @return The optimal {@code Size}, or an arbitrary one if none were big enough
//	 */
//	private static Size chooseOptimalSize(Size[] choices , int textureViewWidth , int textureViewHeight , int maxWidth , int maxHeight , Size aspectRatio) {
//		final String TAG = "chooseOptimalSize[MA]";
//		String dbMsg = "";
//		Size retSize = null;
//		try {
//			// Collect the supported resolutions that are at least as big as the preview Surface
//			List< Size > bigEnough = new ArrayList<>();
//			// Collect the supported resolutions that are smaller than the preview Surface
//			List< Size > notBigEnough = new ArrayList<>();
//			int w = aspectRatio.getWidth();
//			int h = aspectRatio.getHeight();
//			for ( Size option : choices ) {
//				if ( option.getWidth() <= maxWidth && option.getHeight() <= maxHeight && option.getHeight() == option.getWidth() * h / w ) {
//					if ( option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight ) {
//						bigEnough.add(option);
//					} else {
//						notBigEnough.add(option);
//					}
//				}
//			}
//
//			// Pick the smallest of those big enough. If there is no one big enough, pick the
//			// largest of those not big enough.
//			if ( bigEnough.size() > 0 ) {
//				retSize = Collections.min(bigEnough , new CompareSizesByArea());
//			} else if ( notBigEnough.size() > 0 ) {
//				retSize = Collections.max(notBigEnough , new CompareSizesByArea());
//			} else {
//				dbMsg = "Couldn't find any suitable preview size";
//				retSize = choices[0];
//			}
//			dbMsg += ",retSize=" + retSize;
//			if ( debugNow ) {
//				Log.i(TAG , dbMsg + "");
//			}
//		} catch (Exception er) {
//			Log.e(TAG , dbMsg + "");
//		}
//		return retSize;
//	}

	/**
	 * 回転方向によって変化する利用可能な撮影サイズとプレビューサイズを取得する
	 * Sets up member variables related to camera.
	 * @param width  The width of available size for camera preview
	 * @param height The height of available size for camera preview
	 *               openCamera　から呼ばれる
	 */
	@SuppressWarnings ( "SuspiciousNameCombination" )
	private void setUpCameraOutputs(int width , int height) {
		final String TAG = "setUpCameraOutputs[MA]";
		String dbMsg = "";
		try {
			Activity activity = CBMJActivity.this;                //getActivity();
			CameraManager manager = ( CameraManager ) activity.getSystemService(Context.CAMERA_SERVICE);
//			try {
			for ( String cameraId : manager.getCameraIdList() ) {
				dbMsg += ",cameraId=" + cameraId;
				CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

				Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);                        // We don't use a front facing camera in this sample.
				dbMsg += ",facing=" + facing + "(0;FRONT)";
//				if ( facing != null ) {
//					if ( (!isSubCamera && facing == CameraCharacteristics.LENS_FACING_FRONT) ||                //0
//								 (isSubCamera && facing == CameraCharacteristics.LENS_FACING_BACK) ) {    //1
//						continue;
//					}
//				}

				StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
				if ( map == null ) {
					continue;
				}

				// For still image captures, we use the largest available size.
				Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)) , ( Comparator< ? super Size > ) new CompareSizesByArea());
				dbMsg += "m,largest[" + largest.getWidth() + "×" + largest.getHeight() + "]";
				mImageReader = ImageReader.newInstance(largest.getWidth() , largest.getHeight() , ImageFormat.JPEG , mMaxImages);
				//目的のサイズ（利用可能な最大撮影サイズ）とフォーマットの画像用の新しいリーダーを作成
				mImageReader.setOnImageAvailableListener(mOnImageAvailableListener , mBackgroundHandler);
				//ImageReaderから新しいイメージが利用可能になったときに呼び出されるリスナーを登録

				// Find out if we need to swap dimension to get the preview size relative to sensor coordinate.
				int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
				dbMsg += ",displayRotation=" + displayRotation;
				//noinspection ConstantConditions
				mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
				dbMsg += ",mSensorOrientation=" + mSensorOrientation;
				boolean swappedDimensions = false;
				switch ( displayRotation ) {
					case Surface.ROTATION_0:
					case Surface.ROTATION_180:
						if ( mSensorOrientation == 90 || mSensorOrientation == 270 ) {
							swappedDimensions = true;
						}
						break;
					case Surface.ROTATION_90:
					case Surface.ROTATION_270:
						if ( mSensorOrientation == 0 || mSensorOrientation == 180 ) {
							swappedDimensions = true;
						}
						break;
					default:
						dbMsg += "Display rotation is invalid: " + displayRotation;
				}

				Point displaySize = new Point();
				activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
				int rotatedPreviewWidth = width;
				int rotatedPreviewHeight = height;
				int maxPreviewWidth = displaySize.x;
				int maxPreviewHeight = displaySize.y;

				if ( swappedDimensions ) {
					rotatedPreviewWidth = height;
					rotatedPreviewHeight = width;
					maxPreviewWidth = displaySize.y;
					maxPreviewHeight = displaySize.x;
				}

				if ( maxPreviewWidth > MAX_PREVIEW_WIDTH ) {
					maxPreviewWidth = MAX_PREVIEW_WIDTH;    //定数；1920
				}

				if ( maxPreviewHeight > MAX_PREVIEW_HEIGHT ) {
					maxPreviewHeight = MAX_PREVIEW_HEIGHT;        //定数；1080
				}
				mCameraId = cameraId;

				dbMsg += ",rotatedPreview[" + rotatedPreviewWidth + "×" + rotatedPreviewHeight + "]";
				dbMsg += ",maxPreview[" + maxPreviewWidth + "×" + maxPreviewHeight + "]";
				MAX_PREVIEW_ASPECT = 1.0f * maxPreviewWidth / maxPreviewHeight;
				dbMsg += ",MAX_PREVIEW_ASPECT=" + MAX_PREVIEW_ASPECT;
				// Danger, W.R.! Attempting to use too large a preview size could  exceed the camera bus' bandwidth limitation, resulting in gorgeous previews but the storage of garbage capture data.
//				mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class) , rotatedPreviewWidth , rotatedPreviewHeight , maxPreviewWidth , maxPreviewHeight , largest);
				// We fit the aspect ratio of TextureView to the size of preview we picked.
				mPreviewSize = new Size(38 , 22);
				int setWidth = mPreviewSize.getWidth();
				int setHeight = mPreviewSize.getHeight();
				dbMsg += ",最大プレビューサイズ[" + setWidth + "×" + setHeight + "]";
				int orientation = getResources().getConfiguration().orientation;
				dbMsg += ",orientation=" + orientation;
				if ( orientation == Configuration.ORIENTATION_LANDSCAPE ) {
					dbMsg += ";横;";
				} else {
					dbMsg += ";縦;";
					int retention = setWidth;
					setWidth = setHeight;
					setHeight = retention;
					mPreviewSize = new Size(setWidth , setHeight);
				}

				dbMsg += ">>[" + setWidth + "×" + setHeight + "]";
//				if ( mTextureView != null ) {
//					mTextureView.setAspectRatio(setWidth , setHeight);  //生成時のみ？
//				} else
					if ( ma_sarface_view != null ) {
					ma_sarfaceeHolder.setFixedSize(setWidth , setHeight);
//					mPreviewReader  = ImageReader.newInstance(setWidth , setHeight , ImageFormat.JPEG , 1);
//					mImageReader.setOnImageAvailableListener(mOnPreviwListener , mBackgroundHandler);            //プレビューの画像取得
				}
				Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);                // Check if the flash is supported.
				mFlashSupported = available == null ? false : available;
				dbMsg += ",mFlashSupported=" + mFlashSupported;

				mCameraId = cameraId;
				PREVIEW_WIDTH = setWidth;                        //effectなどに渡す
				PREVIEW_HEIGHT = setHeight;

//				myLog(TAG , dbMsg);
//				return;
			}
			//縦;displayRotation=0,mSensorOrientation=90,rotatedPreview[1440×1080],maxPreview[1776×1080],orientation=1,mFlashSupported=true
			//横;displayRotation=1,mSensorOrientation=90,rotatedPreview[1440×1080],maxPreview[1776×1080],orientation=2,mFlashSupported=true
			//どちらも   largest[4608×3456],  最大プレビューサイズ[1440×1080],
			myLog(TAG , dbMsg);
		} catch (CameraAccessException er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		} catch (NullPointerException er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			// Currently an NPE is thrown when the Camera2API is used but not supported on the
			// device this code runs.
//				ErrorDialog.newInstance(getString(R.string.camera_error)).show(getChildFragmentManager() , FRAGMENT_DIALOG);
//			}
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	// Opens the camera specified by {@link Camera2BasicFragment#mCameraId}.

	/**
	 * 指定されたサイズと設定されているCameraIdで指定されたカメラの動作を開始させる
	 * onSurfaceTextureAvailable、onResumeから呼ばれる
	 * 受け取るサイズは意味込み初期値
	 */
	private void openCamera(int width , int height) {
		final String TAG = "openCamera[MA]";
		String dbMsg = "";
		try {
			dbMsg = "DISP[" + width + "×" + height + "]" + DISP_DEGREES;
			setUpCameraOutputs(width , height);
			configureTransform(width , height);                 //org
			Activity activity = CBMJActivity.this;            //getActivity();
			CameraManager manager = ( CameraManager ) activity.getSystemService(Context.CAMERA_SERVICE);
			try {
				if ( !mCameraOpenCloseLock.tryAcquire(2500 , TimeUnit.MILLISECONDS) ) {
					throw new RuntimeException("Time out waiting to lock camera opening.");
				}
				dbMsg += ",mCameraId=" + mCameraId;
				dbMsg += ",mStateCallback=" + mStateCallback;
				dbMsg += ",mBackgroundHandler=" + mBackgroundHandler;
				if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
					if ( checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
						dbMsg += ",permission.CAMERA取得できず";
						return;
					}
				}
				manager.openCamera(mCameraId , mStateCallback , mBackgroundHandler);    //SecurityException;validateClientPermissionsLocked

			} catch (CameraAccessException er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			} catch (InterruptedException er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}

	}

	/**
	 * カメラの終了動作
	 * Session、Session ,ImageReaderと使用したリソースの破棄
	 * Closes the current {@link CameraDevice}.
	 */
	private void closeCamera() {
		final String TAG = "closeCamera[MA]";
		String dbMsg = "";
		try {
			try {
				dbMsg += "mCaptureSession=" + mCaptureSession;
				mCameraOpenCloseLock.acquire();
				if ( null != mCaptureSession ) {         //org①
					mCaptureSession.stopRepeating();    //追加
					mCaptureSession.abortCaptures();    //追加
					mCaptureSession.close();
					mCaptureSession = null;
					dbMsg += ",mCaptureSession 破棄";
				}
				if ( null != mCameraDevice ) {          //org②
					mCameraDevice.close();
					mCameraDevice = null;
					dbMsg += ",mCameraSession 破棄";
				}
				if ( null != mImageReader ) {                  //org
					mImageReader.close();         // ImageReaderに関連するすべてのリソースを解放
					mImageReader = null;
					dbMsg += ",mImageReader 破棄";
				}
//				if ( surface != null ) {
//					surface.release();
//					surface = null;
//					dbMsg += ",surface 破棄";
//				}
				if ( mSound != null ) {
					mSound.release();
					mSound = null;
					dbMsg += ",mSound 破棄";
				}
				myLog(TAG , dbMsg);
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted while trying to lock camera closing." , e);
			} finally {
				mCameraOpenCloseLock.release();
			}
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}

	}

	/**
	 * Starts a background thread and its {@link Handler}.
	 * onResumeでスタート
	 */
	private void startBackgroundThread() {
		final String TAG = "startBackgroundThread[MA]";
		String dbMsg = "";
		try {
			dbMsg = "mBackgroundThread=" + mBackgroundThread;
			if ( mBackgroundThread == null ) {
				mBackgroundThread = new HandlerThread("CameraBackground");
				mBackgroundThread.start();
				dbMsg += ">>=" + mBackgroundThread;
			}
			dbMsg += " , mBackgroundHandler=" + mBackgroundHandler;
			if ( mBackgroundHandler == null ) {
				mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
				dbMsg += ">>=" + mBackgroundHandler;
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}

	}

	/**
	 * Stops the background thread and its {@link Handler}.
	 * <p>
	 * Quit処理 , onDestroy
	 */
	private void stopBackgroundThread() {
		final String TAG = "stopBackgroundThread[MA]";
		String dbMsg = "";
		try {
			dbMsg = "mBackgroundThread=" + mBackgroundThread;
			if ( mBackgroundThread != null ) {
				mBackgroundThread.quitSafely();
				mBackgroundThread.join();
				mBackgroundThread = null;
				dbMsg += ">>=" + mBackgroundThread;
			}
			dbMsg += " , mBackgroundHandler=" + mBackgroundHandler;
			if ( mBackgroundHandler != null ) {
				mBackgroundHandler = null;
				dbMsg += ">>=" + mBackgroundHandler;
			}
			myLog(TAG , dbMsg);
		} catch (InterruptedException er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}

	}

	/**
	 * プレビューの生成
	 * Creates a new {@link CameraCaptureSession} for camera preview.
	 * 　onOpene	から呼ばれる
	 * 各ViewはonCreateで追加する
	 */
	private void createCameraPreviewSession() {
		final String TAG = "createCameraPreviewSession[MA]";
		String dbMsg = "";
		try {
			int tWidth = mPreviewSize.getWidth();
			int tHight = mPreviewSize.getHeight();
			dbMsg = "PreviewSize[" + tWidth + "×" + tHight + "]mSensorOrientation=" + mSensorOrientation;
//			dbMsg += ",isTexturView=" + isTexturView;                 //高速プレビュー
//			if ( mTextureView != null ) {
//				SurfaceTexture texture = mTextureView.getSurfaceTexture();      //org;ここから
//				assert texture != null;
//				texture.setDefaultBufferSize(tWidth , tHight);     // バッファサイズを、プレビューサイズに合わせる
//				surface = new Surface(texture);   // プレビューが描画されるSurface	This is the output Surface we need to start preview.
//			} else
			if ( ma_sarface_view != null ) {
				ArrayList< Surface > surfaceList = new ArrayList();
				surfaceList.add(ma_sarface_view.getHolder().getSurface());                // プレビュー用のSurfaceViewをリストに登録
				surface = ma_sarface_view.getHolder().getSurface();
			}
			mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);    //(5)CaptureRequest作成	 カメラのプレビューウィンドウに適した;We set up a CaptureRequest.Builder with the output Surface.
			mPreviewRequestBuilder.addTarget(surface);
			dbMsg += ",surface=" + surface.toString();
			// (6)プレビュー用のセッション生成を要求する// Here, we create a CameraCaptureSession for camera preview.
			mCameraDevice.createCaptureSession(Arrays.asList(surface , mImageReader.getSurface()) , new CameraCaptureSession.StateCallback() {
				@Override
				public void onConfigured(CameraCaptureSession cameraCaptureSession) {
					final String TAG = "CCPS.onConfigured[MA]";
					String dbMsg = "";
					try {
						if ( null != mCameraDevice ) {  // カメラが閉じていなければ	// The camera is already closed
							mCaptureSession = cameraCaptureSession;        // When the session is ready, we start displaying the preview.
							dbMsg += ",getId=" + mCaptureSession.getDevice().getId();
							try {
								PointF[] focusPoints = {new PointF(mPreviewSize.getWidth() / 2 , mPreviewSize.getHeight() / 2)};
								dbMsg += ",focusPoints(" + focusPoints[0].x + "," + focusPoints[0].y + ")";
								startAutoFocus(focusPoints , CBMJActivity.this);
								mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE , CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
								// オートフォーカスを設定する// Auto focus should be continuous for camera preview.
								setAutoFlash(mPreviewRequestBuilder);        // Flash is automatically enabled when necessary.
								mPreviewRequest = mPreviewRequestBuilder.build();        // リクエスト作成// Finally, we start displaying the camera preview.
								mCaptureSession.setRepeatingRequest(mPreviewRequest , mCaptureCallback , mBackgroundHandler);
								//(7)RepeatSession作成 カメラプレビューを表示する	//APIL21;このキャプチャセッションで、イメージのキャプチャを無限に繰り返すように要求:ここの他は unlockFocus()
							} catch (CameraAccessException er) {
								myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
							}
						} else {
							dbMsg += "mCameraDevice = null";
						}
						myLog(TAG , dbMsg);
					} catch (Exception er) {
						myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
					}
				}

				@Override
				public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
					final String TAG = "CCPS.onConfigureFailed[MA]";
					String dbMsg = "";
					dbMsg += ",getId=" + cameraCaptureSession.getDevice().getId();
					showToast("Failed");
					myErrorLog(TAG , dbMsg + "発生；");
				}
			} , null);
			dbMsg += ",mPreviewRequestBuilder=" + mPreviewRequestBuilder.toString();
			myLog(TAG , dbMsg);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	 * 『mTextureView』への変化に応じて必要なものを構成します。
	 * カメラ・プレビュー・サイズが中で測定されたあと、このmethodが呼ばれなければなりません
	 * setUpCameraOutputs、更には『mTextureView』のサイズは、固定されます。
	 * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
	 * This method should be called after the camera preview size is determined in
	 * setUpCameraOutputs and also the size of `mTextureView` is fixed.
	 * @param viewWidth  The width of `mTextureView`
	 * @param viewHeight The height of `mTextureView`
	 *                   onSurfaceTextureSizeChanged、openCameraから呼ばれる
	 */
	private void configureTransform(int viewWidth , int viewHeight) {
		final String TAG = "configureTransform[MA]";
		String dbMsg = "";
		try {
			dbMsg += ",view[" + viewWidth + "×" + viewHeight + "]";   //正しい値が与えられていない
			int targetViewId;
			View targetView;        //pereviewVの呼び込み枠       ViewGroup
//			if ( isTexturView ) {
//				dbMsg += ",TextureView";
//				targetViewId = mTextureView.getId();
//				targetView = ( AutoFitTextureView ) findViewById(targetViewId);        //pereviewVの呼び込み枠       ViewGroup
//			} else {
				dbMsg += ",sarfaceView";
				targetViewId = ma_sarface_view.getId();
				targetView = ( SurfaceView ) findViewById(targetViewId);        //pereviewVの呼び込み枠       ViewGroup
//			}
			int vgWIDTH = ma_preview_fl.getWidth();
			int vgHEIGHT = ma_preview_fl.getHeight();
			dbMsg += ",読込みViewGroup[" + vgWIDTH + "×" + vgHEIGHT + "]";
			Activity activity = CBMJActivity.this;                //getActivity();
			if ( null != targetView && null != mPreviewSize && null != activity ) {
//				dbMsg += ";Id=" + targetViewId;
				ViewGroup.LayoutParams svlp = targetView.getLayoutParams();
				//			dbMsg += ",変更前LayoutParams[" + svlp.width + "×" + svlp.height + "]";
				int targetViewLeft = targetView.getLeft();
				int targetViewTop = targetView.getTop();
				int targetViewWidth = targetView.getWidth();
				int targetViewHeight = targetView.getHeight();
				dbMsg += ",targetVie(" + targetViewLeft + "×" + targetViewTop + ")[" + targetViewWidth + "×" + targetViewHeight + "]";
				int pvWidth = mPreviewSize.getWidth();
				int pvHeight = mPreviewSize.getHeight();
				dbMsg += ",最大プレビューサイズ[" + pvWidth + "×" + pvHeight + "]";
				int orientation = getResources().getConfiguration().orientation;
				dbMsg += ",orientation=" + orientation;

//				if ( mTextureView != null ) {
//					Matrix matrix = new Matrix();            //org
//					RectF viewRect = new RectF(0 , 0 , viewWidth , viewHeight);        //org viewWidth , viewHeight        vgWIDTH , vgHEIGHT
//					RectF bufferRect = new RectF(0 , 0 , pvHeight , pvWidth);
//					//org	 mPreviewSize.getHeight(), mPreviewSize.getWidth()
//// pvWidth, pvHeightだと横向きで右によって左が余る
//					float centerX = viewRect.centerX();
//					float centerY = viewRect.centerY();
//					dbMsg += ",center;ViewGrupe(" + centerX + "," + centerY + ")とpreview(" + bufferRect.centerX() + "," + bufferRect.centerY() + ")";
//					int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//					dbMsg += ",rotation=" + rotation;
//					if ( Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation ) {   //1||3
//						dbMsg += ";横";
//						float dx = centerX - bufferRect.centerX();
//						float dy = centerY - bufferRect.centerY();
//						dbMsg += ",shift(" + dx + "," + dy + ")";
//						bufferRect.offset(dx , dy);
//						matrix.setRectToRect(viewRect , bufferRect , Matrix.ScaleToFit.FILL);       //org;	FILL		START   ,    CENTER,    END
//						float scale = Math.max(( float ) viewHeight / pvHeight , ( float ) viewWidth / pvWidth);        //	org
//						dbMsg += ",scale=" + scale;                     //MAX_PREVIEW_ASPECT;			//
//						matrix.postScale(MAX_PREVIEW_ASPECT , scale , centerX , centerY);
//						matrix.postRotate(90 * (rotation - 2) , centerX , centerY);                    //  270 || 90
//					} else if ( Surface.ROTATION_0 == rotation || Surface.ROTATION_180 == rotation ) {            //    0 || 2                                               //org
//						dbMsg += ";縦";
//						matrix.postRotate(180 * (rotation - 2) , centerX , centerY);                    // -180 || 0
//					}
//					mTextureView.setTransform(matrix);
//				} else
				if ( ma_sarfaceeHolder != null ) {      //ma_sarfaceeHolder	    ma_sarface_view
					if ( orientation == Configuration.ORIENTATION_LANDSCAPE ) {
						dbMsg += ";横";
						int retention = vgWIDTH;
						vgWIDTH = vgHEIGHT;
						vgHEIGHT = retention;

					} else {
						dbMsg += ";縦";
						int retention = pvWidth;
						pvWidth = pvHeight;
						pvHeight = retention;
					}
					dbMsg += ",読込みViewGroup[" + vgWIDTH + "×" + vgHEIGHT + "]";
					dbMsg += ",>>プレビューサイズ[" + pvWidth + "×" + pvHeight + "]";

					ma_sarfaceeHolder.setFixedSize(pvWidth , pvHeight);
					dbMsg += ",Scale[" + ma_sarface_view.getScaleX() + "×" + ma_sarface_view.getScaleY() + "]";

				}
				targetViewLeft = targetView.getLeft();
				targetViewTop = targetView.getTop();
				targetViewWidth = targetView.getWidth();
				targetViewHeight = targetView.getHeight();
				dbMsg += ">変更結果>(" + targetViewLeft + "×" + targetViewTop + ")[" + targetViewWidth + "×" + targetViewHeight + "]";
				FrameLayout.LayoutParams sParams = ( FrameLayout.LayoutParams ) targetView.getLayoutParams();
				dbMsg += "=(" + sParams.leftMargin + "×" + sParams.topMargin + ")[" + sParams.width + "×" + sParams.height + "]";
				dbMsg += ",gravity=" + sParams.gravity;
				dbMsg += "=(" + targetView.getLeft() + "×" + targetView.getTop() + ")[" + targetView.getWidth() + "×" + targetView.getHeight() + "]";
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	///フォーカス設定////////////////////////////////////////////////////////////////////
	//https://moewe-net.com/android/2016/camera2-af
	private static final int STATE_INIT = -1;
	private static final int STATE_WAITING_LOCK_FUCUS = 0;                    //STATE_WAITING_LOCK
	private static final int STATE_WAITING_PRE_CAPTURE = 11;            //1
	private static final int STATE_WAITING_NON_PRE_CAPTURE = 12;        //2
	private static final int AF_SAME_STATE_REPEAT_MAX = 20;

	private int fState;
	private int mSameAFStateCount;
	private int mPreAFState;
	/**
	 * オートフォーカスの動作リスナー
	 */
	CameraCaptureSession.CaptureCallback mAFListener = new CameraCaptureSession.CaptureCallback() {
		@Override
		public void onCaptureCompleted(CameraCaptureSession session , CaptureRequest request , TotalCaptureResult result) {
			super.onCaptureCompleted(session , request , result);
			final String TAG = "FL.onCaptureCompleted[MA]";
			String dbMsg = "";
			try {
				dbMsg += "fState=" + fState;
				if ( fState == STATE_WAITING_LOCK_FUCUS ) {
					Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
					dbMsg += ",afState=" + afState;
					if ( afState == null ) {
						dbMsg += "onCaptureCompleted AF STATE is null";
						fState = STATE_INIT;
						autoFocusEnd(false);
						return;
					}

					if ( afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED ) {
						Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
						dbMsg += "onCaptureCompleted AF STATE = " + afState + ", AE STATE = " + aeState;
						if ( (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) ) {        //mCancel ||
							fState = STATE_INIT;
							autoFocusEnd(false);
							return;
						}
					}

					if ( afState != CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN && afState == mPreAFState ) {
						mSameAFStateCount++;
						// 同一状態上限
						dbMsg += ",mSameAFStateCount=" + mSameAFStateCount;
						if ( mSameAFStateCount >= AF_SAME_STATE_REPEAT_MAX ) {
							fState = STATE_INIT;
							autoFocusEnd(false);
							return;
						}
					} else {
						mSameAFStateCount = 0;
					}
					mPreAFState = afState;
					return;
				}

				if ( fState == STATE_WAITING_PRE_CAPTURE ) {
					Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
					dbMsg += "WAITING_PRE_CAPTURE AE STATE = " + aeState;
					if ( aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE || aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED ) {
						fState = STATE_WAITING_NON_PRE_CAPTURE;
					} else if ( aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED ) {
						fState = STATE_INIT;
						autoFocusEnd(true);
					}
					return;
				}

				if ( fState == STATE_WAITING_NON_PRE_CAPTURE ) {
					Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
					dbMsg += "WAITING_NON_PRE_CAPTURE AE STATE = " + aeState;
					if ( aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE ) {
						fState = STATE_INIT;
						autoFocusEnd(true);
					}
				}
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}

		private void autoFocusEnd(boolean isSuccess) {
			final String TAG = "FL.autoFocusEnd[MA]";
			String dbMsg = "";
			try {
				dbMsg = "isSuccess=" + isSuccess;
				// フォーカス完了/失敗時の処理
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}
	};

	/**
	 * プレビュー画面上で指定したfocusPointsにオートフォーカスする
	 **/
	public void startAutoFocus(PointF[] focusPoints , Context context) {
		final String TAG = "startAutoFocus[MA]";
		String dbMsg = "";
		try {
			int maxRegionsAF = 0;
			Rect activeArraySize = null;
			CameraManager cameraManager = ( CameraManager ) context.getSystemService(Context.CAMERA_SERVICE);
			try {
				CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(mCameraId);
				maxRegionsAF = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
				activeArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);  //カメラ最大出力サイズ
			} catch (CameraAccessException er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
			if ( activeArraySize == null ) {
				activeArraySize = new Rect();
				//追加；カメラ最大出力サイズ
			}
			dbMsg += ",activeArraySize[" + activeArraySize.width() + "×" + activeArraySize.height() + "]"; //,[4672×3504
			dbMsg += ",maxRegionsAF=" + maxRegionsAF;                                                        // 1
			if ( maxRegionsAF <= 0 ) {
				return;
			}
			if ( focusPoints == null ) {
				return;
			}
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			int r = ( int ) (4 * metrics.density);
			dbMsg += ",r=" + r + "分割？";
			dbMsg += ",focusPoints=" + focusPoints.length + "件";
			Double scaleX = 1.0 * mPreviewSize.getWidth() / activeArraySize.width();
			Double scaleY = 1.0 * mPreviewSize.getHeight() / activeArraySize.height();
			dbMsg += ",scaleY=" + scaleX + ":" + scaleY;
			Double scaleXY = scaleX;
			if ( scaleX < scaleY ) {
				scaleXY = scaleY;
			}
			dbMsg += ">>scale=" + scaleXY;

			int ariaW = ( int ) (activeArraySize.width() * scaleXY) / 3;                //追加
			int ariaH = ( int ) (activeArraySize.height() * scaleXY) / 3;                //追加
			MeteringRectangle[] afRegions = new MeteringRectangle[focusPoints.length];
			for ( int i = 0 ; i < focusPoints.length ; i++ ) {
				dbMsg += "(" + i + ")[" + focusPoints[i].x + "×" + focusPoints[i].y + "]";
				int centerX = ( int ) (focusPoints[i].x / scaleXY);            //( int ) (activeArraySize.width() * focusPoints[i].x);
				int centerY = ( int ) (focusPoints[i].y / scaleXY);        // ( int ) (activeArraySize.height() * focusPoints[i].y);
				dbMsg += ",>center(" + centerX + "," + centerY + ")";
				int rectLeft = centerX - ariaW;                        //Math.max(activeArraySize.bottom , centerX - r);
				int rectTop = centerY - ariaH;                        //Math.max(activeArraySize.top , centerY - r);
				int rectRight = centerX + ariaW;                        //Math.min(centerX + r , activeArraySize.right);
				int rectBottom = centerY + ariaH;                        //Math.min(centerY + r , activeArraySize.bottom);
				dbMsg += ",rect(" + rectLeft + "," + rectTop + ")～(" + rectRight + "×" + rectBottom + ")";
				Rect p = new Rect(rectTop , rectLeft , rectRight , rectBottom);
				afRegions[i] = new MeteringRectangle(p , MeteringRectangle.METERING_WEIGHT_MAX);

			}
			dbMsg += ",afRegions=" + afRegions.length + "件";

			// 状態初期化
			fState = STATE_WAITING_LOCK_FUCUS;
			mSameAFStateCount = 0;
			mPreAFState = -1;
			try {
				CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
				if ( null != surface ) {                                     //   if (null != mPreviewSurface) {
					captureBuilder.addTarget(surface);
					dbMsg += ",addTarget";
				}
				captureBuilder.set(CaptureRequest.CONTROL_AF_MODE , CaptureRequest.CONTROL_AF_MODE_AUTO);
				if ( 0 < afRegions.length ) {
					captureBuilder.set(CaptureRequest.CONTROL_AF_REGIONS , afRegions);
					dbMsg += ",CONTROL_AF_REGIONS";
				}
				captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER , CameraMetadata.CONTROL_AF_TRIGGER_START);  //lockFocus()はここからスタート
				mCaptureSession.setRepeatingRequest(captureBuilder.build() , mAFListener , mBackgroundHandler);//mBackgroundHandler   /
			} catch (CameraAccessException er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}
	///撮影操作////////////////////////////////////////////////////////////////フォーカス設定////

	/**
	 * レリースボタンクリックで呼ばれる
	 * Initiate a still image capture.
	 */
	private void takePicture() {
		final String TAG = "takePicture[MA]";
		String dbMsg = "開始";
		try {
			isPhotography = true;
			lockFocus();
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	 * 静止画像捕獲のための第一歩として、焦点をロックしてください。
	 * Lock the focus as the first step for a still image capture.
	 */
	private void lockFocus() {
		final String TAG = "lockFocus[MA]";
		String dbMsg = "開始";
		try {
			if ( mPreviewRequestBuilder != null ) {
				mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER , CameraMetadata.CONTROL_AF_TRIGGER_START);            // This is how to tell the camera to lock focus.
				mState = STATE_WAITING_LOCK;            // Tell #mCaptureCallback to wait for the lock.
				mCaptureSession.capture(mPreviewRequestBuilder.build() , mCaptureCallback , mBackgroundHandler);
			} else {
				dbMsg = "mPreviewRequestBuilder== null";
			}
			dbMsg += ",mState= " + mState;
			myLog(TAG , dbMsg);
		} catch (CameraAccessException er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	 * 静止画像を捕えるために、プレ捕獲シーケンスを走らせてください。
	 * 我々が反応を中で｛@link #mCaptureCallback｝得る｛@link #lockFocus（）｝とき、この方法は呼ばれなければなりません。
	 * processで STATE_WAITING_LOCK の時に呼ばれる  <<  lockFocus()で　mState = STATE_WAITING_LOCK
	 * Run the precapture sequence for capturing a still image.
	 * This method should be called when we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
	 */
	private void runPrecaptureSequence() {
		final String TAG = "runPrecaptureSequence[MA]";
		String dbMsg = "";
		try {
			// This is how to tell the camera to trigger.
			mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER , CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
			// Tell #mCaptureCallback to wait for the precapture sequence to be set.
			mState = STATE_WAITING_PRECAPTURE;
			mCaptureSession.capture(mPreviewRequestBuilder.build() , mCaptureCallback , mBackgroundHandler);
			myLog(TAG , dbMsg);
		} catch (CameraAccessException er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	 * スチール写真を捕えてください。 我々が反応を入れるとき、この方法は呼ばれなければなりません
	 * Capture a still picture. This method should be called when we get a response in
	 * {@link #mCaptureCallback} from both {@link #lockFocus()}.
	 */
	private void captureStillPicture() {
		final String TAG = "captureStillPicture[MA]";
		String dbMsg = "";
		try {
			final Activity activity = CBMJActivity.this;                //getActivity();
			if ( null == activity || null == mCameraDevice ) {
				return;
			}
			// 撮影用のCaptureRequestを設定する	// This is the CaptureRequest.Builder that we use to take a picture.
			final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);    //静止画像キャプチャに適した
			captureBuilder.addTarget(mImageReader.getSurface());  // キャプチャ結果をImageReaderに渡す

			// オートフォーカス// Use the same AE and AF modes as the preview.
			captureBuilder.set(CaptureRequest.CONTROL_AF_MODE , CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
			setAutoFlash(captureBuilder);
			int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
			dbMsg += ",端末の回転角=" + rotation;
			mSensorOrientation = getOrientation(rotation);//int camLotation         /
			dbMsg += ",カメラセンサーの方向=" + mSensorOrientation;
			captureBuilder.set(CaptureRequest.JPEG_ORIENTATION , mSensorOrientation);        // JPEG画像の方向を設定する。
			CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {
				/**
				 * 撮影が終わったら、フォーカスのロックを外すためのコールバック
				 * */
				@Override
				public void onCaptureCompleted(CameraCaptureSession session , CaptureRequest request , TotalCaptureResult result) {
					final String TAG = "CSP.onCaptureCompleted[MA]";
					String dbMsg = "";
					try {
						dbMsg += "Saved: " + mFile.getPath();
						Thread.sleep(2000);            //暫定；このタイミングではmOnImageAvailableListenerに到達していないので待たせる
						unlockFocus();
						dbMsg += "保存終了";
						myLog(TAG , dbMsg);
					} catch (Exception er) {
						myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
					}
				}
			};

			mCaptureSession.stopRepeating();          //現在のプレビューを停止；setRepeatingRequest または いずれかで進行中の繰り返しキャプチャをキャンセルします 。
			mCaptureSession.abortCaptures();            //Repeating requestsも止まる。
			mCaptureSession.capture(captureBuilder.build() , CaptureCallback , null);  // 撮影する。終了コールバックはメソッド内
			myLog(TAG , dbMsg);
		} catch (CameraAccessException er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	 * lockFocusを解除する。
	 * 静止画撮影が終わる時、このmethodが呼ばれなければなりません。
	 * Unlock the focus. This method should be called when still image capture sequence is finished.
	 */
	private void unlockFocus() {
		final String TAG = "unlockFocus[MA]";
		String dbMsg = "";
		try {
			mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER , CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
			// オートフォーカストリガーを外す// Reset the auto-focus trigger
			setAutoFlash(mPreviewRequestBuilder);
			mCaptureSession.capture(mPreviewRequestBuilder.build() , mCaptureCallback , mBackgroundHandler);
			mState = STATE_PREVIEW;
			mCaptureSession.setRepeatingRequest(mPreviewRequest , mCaptureCallback , mBackgroundHandler);
			// プレビューに戻る// After this, the camera will go back to the normal state of preview.
			//APIL21;このキャプチャセッションで、イメージのキャプチャを無限に繰り返すように要求:ここの他は onConfigured
//			isPhotography = false;
			myLog(TAG , dbMsg);
		} catch (CameraAccessException er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
		final String TAG = "setAutoFlash[MA]";
		String dbMsg = "";
		try {
			if ( mFlashSupported ) {
				requestBuilder.set(CaptureRequest.CONTROL_AE_MODE , CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	 * 指定された名称でJPEGファイルを保存する
	 * Saves a JPEG {@link Image} into the specified {@link File}.
	 */
	private class ImageSaver implements Runnable {        //static ?必要？
		/**
		 * The JPEG image
		 */
		private Image mImage;
		/**
		 * The file we save the image into.
		 */
		private File mFile;
		private File saveFolder;
		private String saveFolderName;
		private ImageView ma_iv;
		private String saveFileName;

		ImageSaver(Image image , String _saveFolderName , ImageView _ma_iv , String _saveFileName) {                //static
			final String TAG = "ImageSaver[MA]";
			String dbMsg = "";
			try {
				mImage = image;
				saveFolderName = _saveFolderName;
				ma_iv = _ma_iv;
				saveFileName = _saveFileName;
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
				isPhotography = false;
			}
		}

		@Override
		public void run() {
			final String TAG = "ImageSaver.run[MA]";
			String dbMsg = "";
			try {
				FileOutputStream output = null;
				try {
					dbMsg += ",saveFolder=" + saveFolderName;
					dbMsg += ",saveFileName=" + saveFileName;
					mFile = new File(saveFolderName , saveFileName);                 //getActivity().getExternalFilesDir(null)
					dbMsg += ",mFile=" + mFile.toString();
					output = new FileOutputStream(mFile);
					int width = mImage.getWidth();
					int height = mImage.getHeight();
					dbMsg += ",image[" + width + "×" + height + "]Format=" + mImage.getFormat() + "," + mImage.getPlanes().length + "枚";
					ByteBuffer imageBuf = mImage.getPlanes()[0].getBuffer();
					byte[] bytes = new byte[imageBuf.remaining()];
					dbMsg += ",bytes=" + bytes.length + "バイト";
					imageBuf.get(bytes);
					output.write(bytes);                    //書込み

					Bitmap shotBitmap = BitmapFactory.decodeByteArray(bytes , 0 , bytes.length);
					shotBitmap.compress(Bitmap.CompressFormat.JPEG , 100 , output);       //output
					Double bmWidth = shotBitmap.getWidth() * 1.0;
					Double bmHeigh = shotBitmap.getHeight() * 1.0;
					dbMsg += ",bitmap[" + bmWidth + "×" + bmHeigh + "]";
					int byteCount = shotBitmap.getByteCount();
					dbMsg += "" + byteCount + "バイト";
//					setLastThumbnail(  mFile.toString()) ;
				} catch (IOException er) {
					myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
					isPhotography = false;
				} finally {
					mImage.close();
					if ( null != output ) {
						try {
							output.close();
						} catch (IOException er) {
							myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
						}
					}
				}
				isPhotography = false;
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}
	}


	private static Bitmap shotBitmap;

	///プレビューデータ操作//////////////////////////////////////////////////////////////////撮影操作//
	private void copyPreview() {
		final String TAG = "copyPreview[MA]";
		String dbMsg = "";
		try {
			if ( !isReWriteNow ) {                                    // //onResume～onPause以外
				if ( mCameraDevice != null ) {
					if ( mCaptureSession != null ) {
						mCaptureSession.stopRepeating();          //プレビューの更新を止める
						mCaptureSession.abortCaptures();            //Repeating requestsも止まる。☆これを加えると300>>200フレームに間隔短縮
						dbMsg += "stopRepeating";
						CaptureRequest.Builder mCopyPreviewRequestBuilder = null;                    // 静止画を送ってもらうためのリクエストのビルダーですよ
						try {
							dbMsg += ",createCaptureRequest;mCameraDevice=" + mCameraDevice;
							mCopyPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);   //静止画像キャプチャに適した要求を作成
						} catch (CameraAccessException er) {
							myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
						}
						mCopyPreviewRequestBuilder.addTarget(mImageReader.getSurface());                        // 送り先はImageReaderにしてね
						CaptureRequest copyPreviewRequest = mCopyPreviewRequestBuilder.build();
						try {
							dbMsg += ",capture";
							int retInt = mCaptureSession.capture(copyPreviewRequest , mCaptureCallback , mBackgroundHandler);
							// (プレビュー時にセッションは開いたままで、)追加で静止画送ってくれリクエストを送る
//				List<CaptureRequest> requestList = new ArrayList<CaptureRequest>();
//				// キャプチャーの指示一覧を作成
//				requestList.add(mCopyPreviewRequestBuilder.build());	//	requestList.add(captureBuilder.build());
//				int retInt = mCaptureSession.captureBurst(requestList, mCaptureCallback, mBackgroundHandler); 				// 登録した指示通りに連写で撮影
							/**
							 * キャプチャ方法４通り
							 * • CameraCaptureSession#captureBurst() 　　　　　　・・・撮影条件を変えながら複数枚撮影する
							 * • CameraCaptureSession#setRepeatingRequest() 　　　　　　・・・同一条件で連続撮影する (Preview 用 )
							 * */
							dbMsg += ",retInt=" + retInt;    //unique capture sequence IDが戻される
						} catch (CameraAccessException er) {
							myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
						}
					} else {
						dbMsg += "mCaptureSession = null ";
					}
				} else {
					dbMsg += "mCameraDevice = null ";
				}
			} else {
				dbMsg += "書き換え中 ";
			}

			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	private class SendPreview implements Runnable {        //static ?必要？
		/**
		 * The JPEG image
		 */
		private Image mImage;

		/**
		 * The file we save the image into.
		 */

		SendPreview(Image image) {                //static                , String _saveFolderName , ImageView _ma_iv , String _saveFileName
			final String TAG = "SendPreview[MA]";
			String dbMsg = "";
			try {
				mImage = image;
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}

		@Override
		public void run() {
			final String TAG = "SendPreview.run[MA]";
			String dbMsg = "";
			try {
				if ( isReWriteNow ) {                                    // //onResume～onPause以外
					dbMsg += "書き換え中 ";
					return;                  //Fragmentなら  isDetached  とgetActivity
				}
//				if ( OCVFRV == null ) {                    ///6/18	この時点でViewがまだ存在しているか
//					dbMsg += ",OCVFRV=null";
//					return;
//				}

				if ( mCaptureSession != null ) {  //回転時クラッシュ；CAMERA_DISCONNECTED (2): checkPidStatus:1493: The camera device has been disconnected
					dbMsg += ",mPreviewRequest=" + mPreviewRequest;
					dbMsg += ",mCaptureCallback=" + mCaptureCallback;
					dbMsg += ",mBackgroundHandler=" + mBackgroundHandler;
					int retInt = mCaptureSession.setRepeatingRequest(mPreviewRequest , mCaptureCallback , mBackgroundHandler);    //プレビュ再開
					dbMsg += ",プレビュ再開=" + retInt;
					isPrevieSending = false;
				} else {
					dbMsg += ",mCaptureSession = null ";
//					createCameraPreviewSession();
					return;
				}
				int width = mImage.getWidth();
				int height = mImage.getHeight();
				long timestamp = mImage.getTimestamp();
				dbMsg += ",image[" + width + "×" + height + "]Format=" + mImage.getFormat();
				dbMsg += ",=" + timestamp + "," + mImage.getPlanes().length + "枚";
				ByteBuffer imageBuf = mImage.getPlanes()[0].getBuffer();
				final byte[] imageBytes = new byte[imageBuf.remaining()];        //直接渡すと.ArrayIndexOutOfBoundsException: length=250,095; index=15,925,248
				dbMsg += ",imageBytes=" + imageBytes.length;
				imageBuf.get(imageBytes);

				final Bitmap shotBitmap = BitmapFactory.decodeByteArray(imageBytes , 0 , imageBytes.length);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				shotBitmap.compress(Bitmap.CompressFormat.JPEG , 100 , byteArrayOutputStream);
				dbMsg += ",bitmap[" + shotBitmap.getWidth() + "×" + shotBitmap.getHeight() + "]";
				int byteCount = shotBitmap.getByteCount();
				dbMsg += "" + byteCount + "バイト";

				mSensorOrientation = getOrientation(DISP_DEGREES);
				dbMsg += ",camera=" + mSensorOrientation + "dig";
//				OCVFRV.readFrameRGB(shotBitmap , mSensorOrientation);

				if ( shotBitmap != null ) {
					shotBitmap.recycle();
					byteCount = shotBitmap.getByteCount();
					dbMsg += ">>" + byteCount + "バイト";
				}
				imageBuf.clear();
				mImage.close();
				myLog(TAG , dbMsg);
			} catch (Exception er) {
				myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
			}
		}
	}

	/////////////////////////////////////////////////////////////////////プレビューデータ操作//
	private void showToast(final String text) {
		final String TAG = "showToast[MA]";
		String dbMsg = "";
		try {
			final Activity activity = this;    //getActivity();
			if ( activity != null ) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(activity , text , Toast.LENGTH_SHORT).show();
					}
				});
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}





//	/**
//	 * Starts a background thread and its {@link Handler}.
//	 * onResumeでスタート
//	 */
//	private void startBackgroundThread() {
//		final String TAG = "startBackgroundThread[MA]";
//		String dbMsg = "";
//		try {
//			dbMsg = "mBackgroundThread=" + mBackgroundThread;
//			if ( mBackgroundThread == null ) {
//				mBackgroundThread = new HandlerThread("CameraBackground");
//				mBackgroundThread.start();
//				dbMsg += ">>=" + mBackgroundThread;
//			}
//			dbMsg += " , mBackgroundHandler=" + mBackgroundHandler;
//			if ( mBackgroundHandler == null ) {
//				mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
//				dbMsg += ">>=" + mBackgroundHandler;
//			}
//			myLog(TAG , dbMsg);
//		} catch (Exception er) {
//			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
//		}
//
//	}
	///////////////////////////////////////////////////////////////////////////////////
	public void pendeingMessege() {
		String titolStr = "制作中です";
		String mggStr = "最終リリースをお待ちください";
		messageShow(titolStr , mggStr);
	}


	public void messageShow(String titolStr , String mggStr) {
		CS_Util UTIL = new CS_Util();
		UTIL.messageShow(titolStr , mggStr , CBMJActivity.this);
	}

	public static void myLog(String TAG , String dbMsg) {
		CS_Util UTIL = new CS_Util();
		UTIL.myLog(TAG , dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		CS_Util UTIL = new CS_Util();
		UTIL.myErrorLog(TAG , dbMsg);
	}
}


/*
CBMJ
Coherence breathing method judgment
コヒーレンス呼吸法判定

バイオフィードバック

達成率
iOS			平均値
Android		最大値

欲しいのは　達成度　×　時間[s]/60
            10点 *10min = 100点
            １００点*1分　＝１００点
Chirence Spectram

投資された方のみ無償配布する方法



*
* */