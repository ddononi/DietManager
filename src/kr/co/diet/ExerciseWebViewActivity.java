package kr.co.diet;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 *	� ��� ��Ƽ��Ƽ
 */
public class ExerciseWebViewActivity extends BaseActivity {
	private WebView mWebView;
	private ProgressDialog dialog = null;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_web_view_layout);
		
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
		mWebView.setWebViewClient(new WebViewClientClass());		
		mWebView.loadUrl("file:///android_asset/" + EXERCISE_LIST_URL);
	}		
	
	
	
	private class WebViewClientClass extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(final WebView view,
				final String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(final WebView view, final String url) {
			super.onPageFinished(view, url);

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

		@Override
		public void onPageStarted(final WebView view, final String url,
				final Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			dialog = ProgressDialog.show(ExerciseWebViewActivity.this, "�ε���",
					"������ �ҷ��������Դϴ�.");
		}

	}	

	
}
