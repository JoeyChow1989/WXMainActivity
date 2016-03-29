package com.data.bean;





import com.hb.qx.R;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import in.srain.cube.views.list.ViewHolderBase;



public class JournalismViewHolder extends ViewHolderBase<NewBean> {

	private String mtag;
	private TextView textView;
	private View view;
	private TextView author_name;
	public NewBean mNewBean;
	private JournalismViewHolder() {
		throw new RuntimeException("Call TopLevelViewHolder(ImageLoader imageLoader) instead.");
	}

	public JournalismViewHolder(String tag) {
		this.mtag = tag;
	}
	
	@Override
	public View createView(LayoutInflater layoutInflater) {
		View v = layoutInflater.inflate(R.layout.journalism_item, null);
		textView=(TextView)v.findViewById(R.id.context_view);
		view=v.findViewById(R.id.br_view);
		author_name=(TextView)v.findViewById(R.id.author_name);
		
		return v;
	}

	@Override
	public void showData(int position, NewBean itemData) {
		  this.mNewBean=itemData;
		  if(position%2==0){
			  view.setVisibility(View.VISIBLE);
		  }
		  textView.setText(itemData.getTitle());
		  author_name.setText(itemData.getAuthor_name());
		  
	}

}
