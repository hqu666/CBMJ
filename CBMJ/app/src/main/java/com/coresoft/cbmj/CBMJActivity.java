package com.coresoft.cbmj;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class CBMJActivity extends AppCompatActivity {
	public boolean isNotSet = true;
	static final int REQUEST_PREF = 100;                          //Prefarensからの戻り
	public TextView bin_type_tv;
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
		final String TAG = "readPref[RBS]";
		String dbMsg = "許諾済み";//////////////////
		try {
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {                //(初回起動で)全パーミッションの許諾を取る
				dbMsg = "許諾確認";
				String[] PERMISSIONS = { Manifest.permission.CAMERA};
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
		readPref();

		setContentView(R.layout.activity_cbmj);

		Toolbar toolbar = ( Toolbar ) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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

	}

	/**
	 * 全リソースの読み込みが終わってフォーカスが当てられた時
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		final String TAG = "onStart[RBS]";
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
		final String TAG = "onStart[RBS]";
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
		final String TAG = "onResume[RBS]";
		String dbMsg = "";
		try {
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	@Override
	protected void onPause() {
		final String TAG = "onPause[RBS]";
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
		final String TAG = "onStop[RBS]";
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_cbmj , menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		            switch ( id ) {
						case R.id.actio_5nbreathe5vomit:
//							bin_type_tv.setText(R.string.mm5nbreathe5vomit);
							return true;
						case R.id.actio_3nbreathe1stop6vomit:
//							bin_type_tv.setText(R.string.mm3nbreathe1stop6vomit);
							return true;
					}
		return super.onOptionsItemSelected(item);
	}

	///////////////////////////////////
	/**
	 * onCreateに有ったイベントなどの処理パート
	 * onCreateは終了処理後のonDestroyの後でも再度、呼び出されるので実データの割り付けなどを分離する
	 */
	public void laterCreate() {
		final String TAG = "laterCreate[RBS]";
		String dbMsg = "";
		try {


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
			/**
			 * A placeholder fragment containing a simple view.
			 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		public PlaceholderFragment() {
		}

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER , sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_cbmj , container , false);
			TextView textView = ( TextView ) rootView.findViewById(R.id.section_label);
			textView.setText(getString(R.string.section_format , getArguments().getInt(ARG_SECTION_NUMBER)));
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
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
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
*
* */