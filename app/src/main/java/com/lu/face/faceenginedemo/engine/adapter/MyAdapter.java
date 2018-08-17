package com.lu.face.faceenginedemo.engine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lu.face.faceenginedemo.R;

import java.util.HashMap;
import java.util.List;

public class MyAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<HashMap<String, Object>> testData;

	public MyAdapter(Context context, List<HashMap<String, Object>> showData) {
		this.inflater = LayoutInflater.from(context);
		testData = showData;
	}

	@Override
	public int getCount() {
		return testData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.testData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = inflater.inflate(R.layout.vlist, null);
			holder.image = (ImageView) convertView.findViewById(R.id.img);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.info = (TextView) convertView.findViewById(R.id.info);
			convertView.setTag(holder);
			holder.info.setVisibility(View.GONE);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.image.setBackgroundResource((Integer) (testData.get(position).get("img")));// ((Drawable)testData.get(position).get("img"));
		holder.title.setText((String) testData.get(position).get("title"));

		return convertView;
	}

	static class ViewHolder {
		TextView title;
		TextView info;
		ImageView image;
	}

}
