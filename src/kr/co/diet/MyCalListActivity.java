package kr.co.diet;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.TextView;

/**
 *	운동 방법 엑티비티
 */
public class MyCalListActivity extends BaseActivity {
	private WebView mWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tip_web_view_layout);
		
		initLayout();
	}
	
	/*
	 * Layout
	 */
	private void initLayout() {
		// 웹뷰 설정
		mWebView = (WebView) findViewById(R.id.webview);
		// 웹뷰 설정 가져오기
		WebSettings ws = mWebView.getSettings();
		// 웹뷰에서 자바스크립트실행가능
		ws.setJavaScriptEnabled(true);
		ws.setSupportZoom(false);
		ws.setBuiltInZoomControls(true);			// zoom 컨트롤
		ws.setPluginsEnabled(true);
		mWebView.loadUrl("file:///android_asset/" + CAL_URL);


	}		

	
}
