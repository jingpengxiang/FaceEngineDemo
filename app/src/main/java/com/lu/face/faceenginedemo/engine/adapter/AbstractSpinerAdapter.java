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

public abstract class AbstractSpinerAdapter<T> extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<T> mObjects = new ArrayList();
	private int mSelectItem = 0;

	public AbstractSpinerAdapter(Context paramContext) {
		init(paramContext);
	}

	private void init(Context paramContext) {
		this.mContext = paramContext;
		this.mInflater = ((LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
	}

	public int getCount() {
		return this.mObjects.size();
	}

	public Object getItem(int paramInt) {
		return this.mObjects.get(paramInt).toString();
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		ViewHolder localViewHolder;
		if (paramView == null) {
			paramView = this.mInflater.inflate(R.layout.item_spiner, null);
			localViewHolder = new ViewHolder();
			localViewHolder.mTextView = ((TextView) paramView.findViewById(R.id.tv_item));
			localViewHolder.mTextView.setTextSize(ValueUtil.getInstance().getTextSize());
			paramView.setTag(localViewHolder);
		} else {
			localViewHolder = (ViewHolder) paramView.getTag();
		}
		String str = (String) getItem(paramInt);
		localViewHolder.mTextView.setText(str);
		return paramView;
	}

	public void refreshData(List<T> paramList, int paramInt) {
		this.mObjects = paramList;
		if (paramInt < 0)
			paramInt = 0;
		if (paramInt >= this.mObjects.size())
			paramInt = -1 + this.mObjects.size();
		this.mSelectItem = paramInt;
	}

	public static abstract interface IOnItemSelectListener {
		public abstract void onItemClick(int paramInt);
	}

	public static class ViewHolder {
		public TextView mTextView;
	}
}
