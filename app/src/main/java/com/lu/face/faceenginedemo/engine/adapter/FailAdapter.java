package com.lu.face.faceenginedemo.engine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.enginesdk.utils.ValueUtil;

import java.util.ArrayList;
import java.util.List;

public class FailAdapter extends BaseAdapter {
	private final String TAG = "FailAdapter";
	private Context mContext;
	private ViewHolder mViewHolder;
	private List<String> list = new ArrayList<String>();

	public FailAdapter(Context context, List<String> list) {
		mContext = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public String getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (null == arg1) {
			arg1 = LayoutInflater.from(mContext).inflate(R.layout.item_fail, null);
			mViewHolder = new ViewHolder();
//			DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
//			LayoutParams faceIvPas = (LayoutParams) mViewHolder.faceIv.getLayoutParams();
//			faceIvPas.width = (int) (0.04F * dm.widthPixels);
//			faceIvPas.height = (int) (1.333F * faceIvPas.width);
//			mViewHolder.faceIv.setLayoutParams(faceIvPas);
			mViewHolder.nameTv = (TextView) arg1.findViewById(R.id.tv_name);
			mViewHolder.nameTv.setTextSize(ValueUtil.getInstance().getTextSize());
			arg1.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) arg1.getTag();
		}
		mViewHolder.nameTv.setText(list.get(position));
		return arg1;
	}

	class ViewHolder {
		TextView nameTv;
	}

}
