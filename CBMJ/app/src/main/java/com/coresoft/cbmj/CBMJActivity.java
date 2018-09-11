package com.coresoft.cbmj;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
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
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CBMJActivity extends AppCompatActivity {

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
	public SurfaceView ma_sarface_view;
//	public SurfaceView cam_sv;    		//カメラモニター

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

	/**
	 * この時点では_peer.on.OPENに至っていない
	 */
	@Override
	protected void onResume() {
		super.onResume();
		final String TAG = "onResume[CBMIA}";
		String dbMsg = "";
		try {
			dbMsg += ",mBackgroundThread=" + mBackgroundThread;
			if ( mBackgroundThread == null ) {
				//org
			} else {
				dbMsg += ",mBackgroundThread=" + mBackgroundThread.isAlive();
			}			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	@Override
	protected void onPause() {
		final String TAG = "onPause[CBMIA}";
		String dbMsg = "";
		try {
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		final String TAG = "onStop[CBMIA}";
		String dbMsg = "";
		try {
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		//
	}

	public void callQuit() {
		final String TAG = "callQuit[MA]";
		String dbMsg = "";
		try {
//			sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);            //	getActivity().getBaseContext()
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
//					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
//					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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