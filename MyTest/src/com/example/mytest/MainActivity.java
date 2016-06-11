package com.example.mytest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends ActionBarActivity {
	 private Context context;
	 private MyChart myChart ;
	 
		public  int width;
		public  int height;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		width = this.getResources().getDisplayMetrics().widthPixels;// 屏幕的宽度
		height = this.getResources().getDisplayMetrics().heightPixels;// 屏幕的高度
		Button btn = (Button) findViewById(R.id.btn_toast);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {}
		});
		findViewById(R.id.btn_toast2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {}
		});
		MyChart myChart = (MyChart) findViewById(R.id.my_chart);
		myChart.setLayoutParams(new LinearLayout.LayoutParams(width, height / 3- height / 60));
		String []  Xscale = new String[]{"6-1","6-2","6-3","6-4","6-5","6-6"};
		float [] Y  = new float[]{3.0156f,3.1526f,3.563f,3.589f};
		myChart.initData(Xscale, Y);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
