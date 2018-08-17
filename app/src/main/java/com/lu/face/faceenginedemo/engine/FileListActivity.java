package com.lu.face.faceenginedemo.engine;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.THID.FaceSDK.ToolUtils;
import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.adapter.MyAdapter;
import com.lu.face.faceenginedemo.engine.data.ConstantValues;
import com.lu.face.faceenginedemo.engine.utils.LogUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.FileUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.StringUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.ValueUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FileListActivity extends ListActivity implements Callback {
    private final String TAG = "FileListActivity";
    private final String PATHKEY = "selectedPath"; // 通过该字符串得到返回的文件路径
    private List<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();// 存储应用程序信息
    private List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    private String currPath = Environment.getExternalStorageDirectory().getPath();
    private MyAdapter adap = null;
    private String currClickPath = null;
    private Handler mHandler;
    private ProgressDialog mProgressDialog;
    private Resources mResources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResources = getResources();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(mResources.getString(R.string.msg_progress_dialog));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mHandler = new Handler(this);
        getListView().addHeaderView(buildHeader());
        this.getListView().setBackgroundColor(Color.WHITE);
        this.getListView().setCacheColorHint(0);
        /* 添加长按事件*/
        this.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                LogUtil.getInstance().i(TAG, "positon: " + position);
                currClickPath = (String) items.get(position - 1).get("info");
                alertConfirm();
                return true;
            }
        });
        adap = new MyAdapter(this, items);
        setListAdapter(adap);
        getPathContent();
    }

    /* 添加点击事件的处理*/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String absPath = (String) items.get(position - 1).get("info");
        Log.e(TAG, "adsPath: " + absPath);
        File file = new File(absPath);
        if (file.isDirectory()) {
            currPath = absPath;
            getPathContent();
        }
    }

    /* 获得指定路径下文件、文件夹信息*/
    private void getPathContent() {
        new Thread() {
            @Override
            public void run() {
                mHandler.obtainMessage(ConstantValues.SCAN_START).sendToTarget();
                File file = new File(currPath);
                if (file.exists()) {
                    File[] files = file.listFiles();
                    if (null != files && 0 < files.length) {
                        list.clear();
                        for (File file2 : files) {
                            if (!file2.getName().startsWith(".")) {
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("title", file2.getName());
                                map.put("info", file2.getAbsolutePath());
                                if (file2.isDirectory()) {
                                    map.put("img", R.drawable.isfolder);
                                } else {
                                    map.put("img", R.drawable.isfile);
                                }
                                list.add(map);
                            }
                        }
                    } else {
                        mHandler.obtainMessage(ConstantValues.ID_HINT, mResources.getString(R.string.no_files)).sendToTarget();
                    }
                }
                mHandler.obtainMessage(ConstantValues.SCAN_END).sendToTarget();
            }
        }.start();
    }

    @Override
    public boolean handleMessage(Message message) {
        // TODO Auto-generated method stub
        switch (message.what) {
            case ConstantValues.SCAN_START:
                mProgressDialog.show();
                break;
            case ConstantValues.SCAN_END:
                mProgressDialog.dismiss();
                items.clear();
                items.addAll(list);
                adap.notifyDataSetChanged();
                break;
            case ConstantValues.ID_HINT:
                Toast.makeText(FileListActivity.this, (String) message.obj, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (StringUtil.isEqual(currPath, Environment.getExternalStorageDirectory().getPath())) {
            FileListActivity.this.finish();
            return;
        }
        currPath = currPath.substring(0, currPath.lastIndexOf(File.separator));
        getPathContent();
    }

    private void alertConfirm() {
        new AlertDialog.Builder(this).setTitle(mResources.getString(R.string.filelist_attention)).setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(mResources.getString(R.string.filelist_msg)).setPositiveButton(mResources.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                /* 向上层Activity返回选择的文件夹路径*/
                Intent intent = new Intent();
                intent.putExtra("selectedPath", currClickPath);
                setResult(RESULT_OK, intent);
                FileListActivity.this.finish();
            }
        }).setNegativeButton(mResources.getString(R.string.btn_cancel), null).show();
    }

    public interface MyListViewInterface {
        public void getSelectedPath(String path);
    }

    private View buildHeader() {
        Button btn = new Button(this);
        btn.setTextSize(4 * ValueUtil.getInstance().getTextSize());
        btn.setText(mResources.getString(R.string.take_photo));
        btn.setTextColor(Color.RED);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                } else {
                    takePhotoByCamera();
                }
//				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				startActivityForResult(intent, 1);
            }
        });
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        LayoutParams lp = new LayoutParams((int) (0.5F * mDisplayMetrics.widthPixels), LayoutParams.WRAP_CONTENT);
        btn.setLayoutParams(lp);
        return (btn);
    }

    /**
     * 检查拍照权限
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 进入这儿表示没有权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // 提示已经禁止

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            }
        } else {
            takePhotoByCamera();
        }
    }

    private void takePhotoByCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && null != data) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                LogUtil.getInstance().i(TAG, "SD card is not avaiable/writeable right now.");
                return;
            }
//			String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            currClickPath = ValueUtil.getInstance().getFACE_PATH() + File.separator + new DateFormat().format("yyyyMMddHHmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
            if (null != bitmap) {
                Bitmap bitmapTmp = ToolUtils.resizeImage(bitmap);
                if (null != bitmapTmp) {
                    FileUtil.getInstance().bitmapToJpeg(bitmapTmp, currClickPath);
                }
            }
            Intent intent = new Intent();
            intent.putExtra("selectedPath", currClickPath);
            setResult(RESULT_OK, intent);
            FileListActivity.this.finish();
        }
    }

}
