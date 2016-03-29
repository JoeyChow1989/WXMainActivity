package com.hb.qx;

import in.srain.cube.request.JsonData;
import in.srain.cube.request.RequestJsonHandler;
import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.list.PagedListViewDataAdapter;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.data.bean.JournalismListDataMode;
import com.data.bean.JournalismViewHolder;
import com.data.bean.NewBean;
import com.hb.tool.NetUtil;
import com.hb.ui.SlideBar;
import com.hb.ui.SlideBar.OnTriggerListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;



public class LockMainActvity   extends Activity implements OnTriggerListener {
	private static final int LOACTION_OK = 0;
	private static final int ON_NEW_INTENT = 1;
	private static final int UPDATE_EXISTS_CITY = 2;
	private static final int GET_WEATHER_RESULT = 3;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle toggle;
	private View right_sliding;
	private ImageView show_slide;
	private GridViewWithHeaderAndFooter gridView;
	private PagedListViewDataAdapter<NewBean> mAdapter;
	private JournalismListDataMode dataMode;
	private LocationClient mLocationClient;
	private HbApplication mApplication;

	private TextView munuber_text;
	private TextView tianqi_view;
	private TextView tianqing_img;
	private TextView district;
	private TextView update_ivew;
	
	private EditText baidu_edit;
	private ImageView search_img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getWindow().setFormat(1);
		getWindow().addFlags(4096);
		getWindow().addFlags(524288);
		getWindow().addFlags(4194304);
		setContentView(R.layout.lock_activity_main);
		SlideBar slideToUnLock = (SlideBar) findViewById(R.id.slideBar);
		slideToUnLock.setOnTriggerListener(this);
		show_slide = (ImageView) findViewById(R.id.show_slide);
		show_slide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleRightSliding();
			}
		});
		munuber_text = (TextView) findViewById(R.id.munuber_text);
		tianqi_view = (TextView) findViewById(R.id.tianqi_view);
		tianqing_img = (TextView) findViewById(R.id.tianqing_img);
		district = (TextView) findViewById(R.id.district_text);
		update_ivew = (TextView) findViewById(R.id.update_ivew);
		update_ivew.setOnClickListener(update_click);
		
		baidu_edit=(EditText)findViewById(R.id.baidu_text);
		search_img=(ImageView)findViewById(R.id.search_img);
		search_img.setOnClickListener(search_onclick);
		initDrawerLayout();
		intGrid();
		loclCity();
		startLocate();
	}

	public OnClickListener update_click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dataMode.setRefresh(true);
			dataMode.queryFirstPage();
			mAdapter.notifyDataSetChanged();
		}
	};
	
	public OnClickListener search_onclick=new OnClickListener() {
		@Override
		public void onClick(View v) {
			String text=baidu_edit.getText().toString();
			if(text!=null&&!text.equals("")){
				Intent intent=new Intent(LockMainActvity.this,WebViewActivity.class);
				StringBuffer url=new StringBuffer();
				url.append("http://m.baidu.com/s?word=");
				url.append(text);
				intent.putExtra("url",url.toString());
				startActivity(intent);
			}else{
				Toast.makeText(getApplicationContext(), "搜索内容不能为空", 0).show();
			}
			
		}
	};

	public void intGrid() {
		dataMode = new JournalismListDataMode(this);
		mAdapter = new PagedListViewDataAdapter<NewBean>();
		mAdapter.setViewHolderClass(this, JournalismViewHolder.class, "index");
		mAdapter.setListPageInfo(dataMode.getListPageInfo());
		gridView = (GridViewWithHeaderAndFooter) findViewById(R.id.gridView);
		gridView.setAdapter(mAdapter);
		gridView.setOnItemClickListener(gridClickListener);
		dataMode.queryFirstPage();
		mAdapter.notifyDataSetChanged();
	}

	public void loclCity() {
		mApplication = HbApplication.getInstance();
		mLocationClient = mApplication.getLocationClient();
		mLocationClient.registerLocationListener(mLocationListener);
	}

	public void startLocate() {
		if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
			mLocationClient.start();
			mLocationClient.requestLocation();
		} else {
			setTQ();
		}

	}

	BDLocationListener mLocationListener = new BDLocationListener() {
		@Override
		public void onReceivePoi(BDLocation arg0) {

		}
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || TextUtils.isEmpty(location.getCity())) {
				return;
			}
			String cityName = location.getCity();
			mApplication.editor.putString("district", location.getDistrict());
			mApplication.editor.commit();
			mLocationClient.stop();
			Message msg = mHandler.obtainMessage();
			msg.what = LOACTION_OK;
			msg.obj = cityName;
			mHandler.sendMessage(msg);
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOACTION_OK:
				try {
					cityCode(msg.obj.toString());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			case ON_NEW_INTENT:

				break;
			case UPDATE_EXISTS_CITY:

				break;
			case GET_WEATHER_RESULT:

				break;
			default:
				break;
			}
		}
	};

	// 访问天气
	public void cityCode(final String city) throws UnsupportedEncodingException {

		if (isLocation(city)) {
			System.out.println("##setTQ###");
			StringBuffer url = new StringBuffer();
			url.append(URL.TQ_URL);
			url.append("?city=");
			url.append(java.net.URLEncoder.encode(city.substring(0, city.length() - 1), "utf-8"));
			RequestUtil.reverseget(url.toString(), new RequestJsonHandler() {
				@Override
				public void onRequestFinish(JsonData data) {
					if (data != null) {
						JsonData json_data = data.optJson("HeWeather data service 3.0");
						JSONArray jsonArray = json_data.optArrayOrNew();
						try {
							Object object = jsonArray.get(0);
							JsonData resultData = JsonData.create(object);
							JsonData aqi_data = resultData.optJson("aqi");
							JsonData now_data = resultData.optJson("now");
							String wr = aqi_data.optJson("city").optString("qlty");
							String h_wd = now_data.optString("tmp");
							String txt = now_data.optJson("cond").optString("txt");
							munuber_text.setText(h_wd);
							tianqi_view.setText(txt);
							tianqing_img.setText(wr);
							// 保存城市
							mApplication.editor.putString("loca_city", city);
							// 保存天气时间
							mApplication.editor.putString("tq_day", mApplication.getDate());
							// 保存天气情况
							mApplication.editor.putString("tq_wd", h_wd);
							mApplication.editor.putString("tq_txt", txt);
							mApplication.editor.putString("tq_wr", wr);
							mApplication.editor.commit();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			});
		} else {
			setTQ();
		}
	}

	// 判断定位与时间
	public boolean isLocation(String city) {
		boolean reuslt = false;
		String file_city = mApplication.sp.getString("loca_city", "");
		// 城市为空
		if (file_city.equals("")) {
			reuslt = true;
			// 城市无变化
		} else if (file_city.equals(city)) {
			String file_data = mApplication.sp.getString("tq_day", "");
			String now_data = mApplication.getDate();
			// 日期为空
			if (file_data.equals("")) {
				reuslt = true;
				// 日期无变化
			} else if (!file_data.equals(now_data)) {
				reuslt = true;
			}
		} else {
			reuslt = true;
		}
		return reuslt;
	}

	public void setTQ() {
		String h_wd = mApplication.sp.getString("tq_wd", "");
		String txt = mApplication.sp.getString("tq_txt", "");
		String wr = mApplication.sp.getString("tq_wr", "");
		String district_text = mApplication.sp.getString("district", "");
		district.setText(district_text);
		munuber_text.setText(h_wd);
		tianqi_view.setText(txt);
		tianqing_img.setText(wr);
	}

	private OnItemClickListener gridClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Object obj = view.getTag();
			if(null!=obj&&obj instanceof JournalismViewHolder){
				 JournalismViewHolder holder=(JournalismViewHolder)obj;
				 Intent intent=new Intent(LockMainActvity.this,WebViewActivity.class);
		            intent.putExtra("url",holder.mNewBean.getUrl());
		            startActivity(intent);
			}
		}
	};

	@Override
	public void onTrigger() {
		finish();
	}

	private void initDrawerLayout() {
		drawerLayout = (DrawerLayout) super.findViewById(R.id.drawer_layout);
		drawerLayout.setScrimColor(Color.TRANSPARENT);
		right_sliding = super.findViewById(R.id.right_sliding);
		toggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.back_move_details_normal, R.string.drawer_open, R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				show_slide.setVisibility(View.VISIBLE);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				show_slide.setVisibility(View.GONE);
			}

		};
		drawerLayout.setDrawerListener(toggle);
	}

	private void toggleRightSliding() {
		if (drawerLayout.isDrawerOpen(Gravity.END)) {
			drawerLayout.closeDrawer(Gravity.END);
		} else {
			drawerLayout.openDrawer(Gravity.END);
		}
	}

}
