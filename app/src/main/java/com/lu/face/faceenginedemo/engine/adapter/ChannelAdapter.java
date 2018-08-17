package com.lu.face.faceenginedemo.engine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.models.ChannelParam;

import java.util.ArrayList;
import java.util.List;

public class ChannelAdapter extends BaseAdapter {
	private Context mContext;
	private ViewHolder mViewHolder;
	private int selectedPos;
	private List<ChannelParam> mList = new ArrayList<ChannelParam>();

	public ChannelAdapter(Context context, List<ChannelParam> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public ChannelParam getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (null == arg1) {
			arg1 = LayoutInflater.from(mContext).inflate(R.layout.item_system, null);
			mViewHolder = new ViewHolder();
			mViewHolder.channelLl = (LinearLayout) arg1.findViewById(R.id.ll_channel);
			mViewHolder.idTv = (TextView) arg1.findViewById(R.id.tv_id);
			mViewHolder.cameraTv = (TextView) arg1.findViewById(R.id.tv_camera);
			mViewHolder.bottomTv = (TextView) arg1.findViewById(R.id.tv_bottom);
			mViewHolder.topTv = (TextView) arg1.findViewById(R.id.tv_top);
			arg1.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) arg1.getTag();
		}
		if (arg0 == selectedPos) {
			mViewHolder.channelLl.setSelected(true);
		} else {
			mViewHolder.channelLl.setSelected(false);
		}
		ChannelParam param = getItem(arg0);
		if (null != param) {
			mViewHolder.idTv.setText(String.valueOf(param.getId()));
			mViewHolder.cameraTv.setText(param.getCamera());
			mViewHolder.bottomTv.setText(String.valueOf(param.getBottom()));
			mViewHolder.topTv.setText(String.valueOf(param.getTop()));
		}
		return arg1;
	}

	public void setSelection(int position) {
		selectedPos = position;
		notifyDataSetChanged();
	}

	class ViewHolder {
		private LinearLayout channelLl;
		private TextView idTv;
		private TextView cameraTv;
		private TextView bottomTv;
		private TextView topTv;
	}
}
