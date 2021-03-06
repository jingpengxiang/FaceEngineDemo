package com.lu.face.faceenginedemo.engine.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.models.FingerPrint;
import com.lu.face.faceenginedemo.engine.utils.FpFeatureUtil;
import com.lu.face.faceenginedemo.engine.utils.LogUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.FileUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.StringUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.ValueUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FpAdapter extends BaseAdapter {
	private final String TAG = "FingerPrintAdapter";
	private Context mContext;
	private ViewHolder mViewHolder;

	private boolean isCheckBoxShowing;

	public boolean isCheckBoxShowing() {
		return isCheckBoxShowing;
	}

	public void setCheckBoxShowing(boolean isCheckBoxShowing) {
		this.isCheckBoxShowing = isCheckBoxShowing;
	}

	private FpFeatureUtil mFpFeatureUtil;
	private boolean isQuerying;
	private FingerPrintAdapterCallback mFingerPrintAdapterCallback;
	private List<FingerPrint> mList = new ArrayList<FingerPrint>();

	public FpAdapter(Context context, FpFeatureUtil mFeatureUtil, FingerPrintAdapterCallback callback) {
		mContext = context;
		this.mFpFeatureUtil = mFeatureUtil;
		mFingerPrintAdapterCallback = callback;
		resetData();
	}

	public void resetData() {
		mList.clear();
		mList.addAll(new ArrayList(ValueUtil.getInstance().getFpMap().values()));
		notifyDataSetChanged();
	}

	public void clearData() {
		mList.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public FingerPrint getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (null == arg1) {
			arg1 = LayoutInflater.from(mContext).inflate(R.layout.item_face_lib, null);
			mViewHolder = new ViewHolder();
			mViewHolder.faceLibLl = (LinearLayout) arg1.findViewById(R.id.ll_face_lib);
			mViewHolder.userIdTv = (TextView) arg1.findViewById(R.id.tv_user_id);
			mViewHolder.idTv = (TextView) arg1.findViewById(R.id.tv_id);
			mViewHolder.faceIv = (ImageView) arg1.findViewById(R.id.iv_face);
			DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
			LayoutParams faceIvPas = (LayoutParams) mViewHolder.faceIv.getLayoutParams();
			faceIvPas.width = (int) (0.04F * dm.widthPixels);
			faceIvPas.height = (int) (1.333F * faceIvPas.width);
			mViewHolder.faceIv.setLayoutParams(faceIvPas);
			mViewHolder.faceTv = (TextView) arg1.findViewById(R.id.tv_face_title);
			mViewHolder.nameTv = (TextView) arg1.findViewById(R.id.tv_name);
			mViewHolder.delBtn = (Button) arg1.findViewById(R.id.btn_del);

			mViewHolder.cbRl = (RelativeLayout) arg1.findViewById(R.id.rl_cb);
			mViewHolder.faceCb = (CheckBox) arg1.findViewById(R.id.cb_face);
			mViewHolder.faceCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					// TODO Auto-generated method stub
//					checkedList.remove(position);
				}
			});
			mViewHolder.userIdTv.setTextSize(ValueUtil.getInstance().getTextSize());
			mViewHolder.idTv.setTextSize(ValueUtil.getInstance().getTextSize());
			mViewHolder.faceTv.setTextSize(ValueUtil.getInstance().getTextSize());
			mViewHolder.nameTv.setTextSize(ValueUtil.getInstance().getTextSize());
			mViewHolder.delBtn.setTextSize(ValueUtil.getInstance().getTextSize());
//			mViewHolder.allDelBtn.setTextSize(ValueUtil.getInstance().getTextSize());
			arg1.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) arg1.getTag();
		}
		mViewHolder.faceIv.setImageDrawable(null);
//		mViewHolder.nameTv.setOnClickListener(new NameListener(position));
		mViewHolder.delBtn.setOnClickListener(new BtnListener(position));
		if (isCheckBoxShowing) {
			mViewHolder.delBtn.setVisibility(View.GONE);
			mViewHolder.cbRl.setVisibility(View.VISIBLE);
		} else {
			mViewHolder.delBtn.setVisibility(View.VISIBLE);
			mViewHolder.cbRl.setVisibility(View.GONE);
		}
		FingerPrint pf = getItem(position);
		if (null != pf) {
			mViewHolder.userIdTv.setText(String.valueOf(pf.getId()));
//			mViewHolder.idTv.setText(String.valueOf(pf.getFeatureCommon().getFeatureId()));
			mViewHolder.nameTv.setText(pf.getName());

			showIv(pf.getPath(), mViewHolder.faceIv, 4);
			mViewHolder.faceCb.setChecked(pf.isChecked());
		}
		return arg1;
	}

	public void setSelection(int position) {
		LogUtil.getInstance().i(TAG, "238 setSelection position = " + position);
		mList.get(position).setChecked(!mList.get(position).isChecked());
		notifyDataSetChanged();
	}

	public boolean isAllNotSelected() {
		boolean ret = true;
		if (0 < mList.size()) {
			for (int i = 0; i < mList.size(); i++) {
				if (mList.get(i).isChecked()) {
					ret = false;
					break;
				}
			}
		}
		return ret;
	}

	public boolean isPartSelected() {
		int num = 0;
		if (0 < mList.size()) {
			for (int i = 0; i < mList.size(); i++) {
				if (mList.get(i).isChecked()) {
					num++;
					break;
				}
			}
		}
		return 0 < num;
	}

	public boolean isAllSelected() {
		boolean ret = true;
		if (0 < mList.size()) {
			for (int i = 0; i < mList.size(); i++) {
				if (!mList.get(i).isChecked()) {
					ret = false;
					break;
				}
			}
		}
		return ret;
	}

	class ViewHolder {
		private LinearLayout faceLibLl;
		private TextView userIdTv;
		private TextView idTv;
		private TextView nameTv;
		private TextView faceTv;
		private ImageView faceIv;
		private Button delBtn;
		private CheckBox faceCb;
		private RelativeLayout cbRl;
	}

	public void setAllChecked() {
		int size = mList.size();
		for (int i = 0; i < size; i++) {
			mList.get(i).setChecked(true);
		}
	}

	public void setAllNotChecked() {
		int size = mList.size();
		for (int i = 0; i < size; i++) {
			mList.get(i).setChecked(false);
		}
	}

	public void delChecked() {
		Iterator<FingerPrint> it = mList.iterator();
		while (it.hasNext()) {
			FingerPrint fp = (FingerPrint) it.next();
			if (fp.isChecked()) {
				delFeature(fp);
				it.remove();
			}
		}
		mFingerPrintAdapterCallback.onListUpdate();
	}

	public void delAll() {
		mList.clear();
		notifyDataSetChanged();
		ValueUtil.getInstance().getFpMap().clear();
		FileUtil.getInstance().delFile(ValueUtil.getInstance().getFP_PATH());
	}

	private void showIv(String path, ImageView iv, int size) {
		// TODO Auto-generated method stub
		if (!StringUtil.isNotNull(path)) {
			return;
		}
		try {
//			FileInputStream fis = new FileInputStream(path);
//			byte[] bt=getBytes(new FileInputStream(path)); // 注释部分换用另外一种方式解码
			// bm=BitmapFactory.decodeByteArray(bt,0,bt.length);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = size;
			options.inJustDecodeBounds = false;
			WeakReference<Bitmap> bmWr = new WeakReference<Bitmap>(BitmapFactory.decodeStream(new FileInputStream(path), null, options));
//			Bitmap bm = // BitmapFactory.decodeByteArray(bt, 0, bt.length);
//					BitmapFactory.decodeStream(new FileInputStream(path), null, options);
			if (null != bmWr) {
				iv.setImageBitmap(bmWr.get());
			}
//			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private byte[] getBytes(InputStream is) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;

		while ((len = is.read(b, 0, 1024)) != -1) {
			baos.write(b, 0, len);
			baos.flush();
		}
		byte[] bytes = baos.toByteArray();
		return bytes;
	}

	public ViewHolder getViewHolder(View v) {
		if (v.getTag() == null) {
			return getViewHolder((View) v.getParent());
		}
		return (ViewHolder) v.getTag();
	}

	class NameListener implements OnClickListener {
		private int position;

		public NameListener(int pos) {
			position = pos;
		}

		@Override
		public void onClick(View v) {
			mFingerPrintAdapterCallback.onNameClick(position);
		}
	}

	class BtnListener implements OnClickListener {
		private int position;

		public BtnListener(int pos) {
			position = pos;
		}

		@Override
		public void onClick(View v) {
			delFeature(getItem(position));
			mList.remove(position);
			mFingerPrintAdapterCallback.onListUpdate();
			notifyDataSetChanged();
		}
	}

	private void delFeature(FingerPrint fp) {
		mFingerPrintAdapterCallback.onListChanged(fp);
		ValueUtil.getInstance().getFpMap().remove(fp.getId());
		FileUtil.getInstance().delFile(fp.getPath());
		FileUtil.getInstance().delFile(fp.getPath() + ValueUtil.getInstance().getFeaturePostfix());
	}

	public boolean isQuerying() {
		return isQuerying;
	}

	public void setQuerying(boolean isQuerying) {
		this.isQuerying = isQuerying;
	}

	public interface FingerPrintAdapterCallback {
		public void onListChanged(FingerPrint fp);

		public void onListUpdate();

		public void onNameClick(int position);
	}
}
