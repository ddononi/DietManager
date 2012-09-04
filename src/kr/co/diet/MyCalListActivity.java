package kr.co.diet;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.TextView;

/**
 *	� ��� ��Ƽ��Ƽ
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
		// ���� ����
		mWebView = (WebView) findViewById(R.id.webview);
		// ���� ���� ��������
		WebSettings ws = mWebView.getSettings();
		// ���信�� �ڹٽ�ũ��Ʈ���డ��
		ws.setJavaScriptEnabled(true);
		ws.setSupportZoom(false);
		ws.setBuiltInZoomControls(true);			// zoom ��Ʈ��
		ws.setPluginsEnabled(true);
		mWebView.loadUrl("file:///android_asset/" + CAL_URL);


	}		

	
}
