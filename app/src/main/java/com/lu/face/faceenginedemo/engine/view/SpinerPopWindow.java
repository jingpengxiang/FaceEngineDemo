package com.lu.face.faceenginedemo.engine.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;

import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.adapter.AbstractSpinerAdapter;
import com.lu.face.faceenginedemo.engine.adapter.NormalSpinerAdapter;

import java.util.Arrays;

public class SpinerPopWindow extends PopupWindow implements OnItemClickListener {

	private Context mContext;
	private ListView mListView;
	private NormalSpinerAdapter mAdapter;
	private AbstractSpinerAdapter.IOnItemSelectListener mItemSelectListener;

	public SpinerPopWindow(Context context) {
		super(context);

		mContext = context;
		init();
	}

	public void setItemListener(AbstractSpinerAdapter.IOnItemSelectListener listener) {
		mItemSelectListener = listener;
	}

	private void init() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_spiner, null);
		setContentView(view);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);

		setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00);
		setBackgroundDrawable(dw);

		mListView = (ListView) view.findViewById(R.id.listview);

		mAdapter = new NormalSpinerAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	public void refreshData(String[] datas, int selIndex) {
		if (datas != null && selIndex != -1) {
			mAdapter.refreshData(Arrays.asList(datas), selIndex);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		dismiss();
		if (mItemSelectListener != null) {
			mItemSelectListener.onItemClick(pos);
		}
	}

}
