package com.data.bean;

import in.srain.cube.views.list.ListPageInfo;
import in.srain.cube.views.list.PagedListDataModel;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hb.qx.HbApplication;


public class JournalismListDataMode extends PagedListDataModel<NewBean> {

	private Context mcontext;
	private HbApplication mApplication;

	private boolean mrefresh = false;

	public JournalismListDataMode(Context context) {
		mListPageInfo = new ListPageInfo<NewBean>(10);
		this.mcontext = context;
		mApplication = (HbApplication) HbApplication.getInstance();
	}

	@Override
	protected void doQueryData() {
		List<NewBean> list = getFxlistentity();

		setRequestResult(list, false);
	}

	public List<NewBean> getFxlistentity() {
		try {
			String text = Commonutil.getDataFile("new_text.txt", mcontext);
			Gson gson = new Gson();
			List<NewBean> list = gson.fromJson(text, new TypeToken<List<NewBean>>() {
			}.getType());
			if(this.mrefresh) {
				int count = mApplication.sp.getInt("new_count", 0);
				if (list.size() > 0) {
					int start = count * 10;
					int end = start + 10;
					if (end < list.size() && start < list.size()) {
						list = list.subList(start, end);
						count=count+1;
						mApplication.editor.putInt("new_count",count);
						mApplication.editor.commit();
					} else {
						list = list.subList(0, 10);
						mApplication.editor.putInt("new_count", 0);
						mApplication.editor.commit();
					}
				}
				this.mrefresh=false;
			} else{
				list = list.subList(0, 10);
			}
			return list;
		} catch (Exception e) {
		}
		return new ArrayList<NewBean>();
	}

	public void setRefresh(boolean refresh) {
		this.mrefresh = refresh;
	}

}
