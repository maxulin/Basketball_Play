package com.basketball.play.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

import com.basketball.play.R;
import com.basketball.play.bean.Group;
import com.basketball.play.bean.Site;
import com.basketball.play.bean.UserBean;
import com.basketball.play.util.ImageUtils;
import com.basketball.play.util.PhotoPickUtil;
import com.basketball.play.util.PhotoPickUtil.OnPhotoPickedlistener;
import com.basketball.play.view.CircularImage;
import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GroupActivity extends BaseActivity {
	private EditText group_name;
	private EditText group_content;
	private CircularImage group_img;
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/group_avator.jpg";
	private Uri imageUri;
	private String group_img_sd;
	private Button group_commit;
	private String upload_group_img;
	private Site site = new Site();
	private LinearLayout layout ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_add_activity);
		initView();
		
		BmobQuery<Site> query = new BmobQuery<Site>();
		query.getObject(getApplicationContext(), getIntent().getStringExtra("obj_id"), new GetListener<Site>() {
			
			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast(arg1);
				
			}
			
			@Override
			public void onSuccess(Site arg0) {
				site = arg0;
				
			}
		});
	}
	
	private void initView(){
		group_name = (EditText) findViewById(R.id.group_name);
		group_content = (EditText) findViewById(R.id.group_content);
		group_img = (CircularImage) findViewById(R.id.group_img);
		group_img.setOnClickListener(ocl);
		group_commit = (Button) findViewById(R.id.group_commit);
		group_commit.setOnClickListener(ocl);
		layout = (LinearLayout) findViewById(R.id.add_group_img);
		layout.setOnClickListener(ocl);
		imageUri = Uri.parse(IMAGE_FILE_LOCATION);
	}
	
	
	ProgressDialog dialog =null;
	OnClickListener ocl = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(arg0.getId()==R.id.group_img){
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, 0);
			}if(arg0.getId()==R.id.add_group_img){
				showImagePickDialog();
			}
			else{
				if(group_name.getText().toString()==null || group_name.getText().toString().equals("")){
					ShowToast("请输入群组名称");
					return;
				}
				if(group_content.getText().toString()==null || group_content.getText().toString().equals("")){
					ShowToast("请输入群组介绍");
					return;
				}
				if(group_img_sd==null || group_img_sd.equals("")){
					ShowToast("请选择群组头像");
					return;
				}
				dialog = new ProgressDialog(GroupActivity.this);
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);                 
				dialog.setMessage("正在上传数据");
				dialog.setIndeterminate(false);               
				dialog.setCancelable(true);       
				dialog.setCanceledOnTouchOutside(false);  
				dialog.show();
				BTPFileResponse response = BmobProFile.getInstance(getApplicationContext()).upload(group_img_sd, new UploadListener() {
					
					@Override
					public void onError(int arg0, String arg1) {
						dialog.dismiss();
						ShowToast("上传出错："+arg1);
					}
					
					@Override
					public void onSuccess(String arg0, String arg1, BmobFile arg2) {
						Log.i("smile", "新版文件服务的fileName = "+arg0+",新版文件服务的url ="+arg1);
						upload_group_img =BmobProFile.getInstance(getApplicationContext()).signURL(arg0,arg1,"ffa7150668e1dc6b25eae12607d1cecb",0,null);
						if(arg2!=null){
							Log.i("smile", "兼容旧版文件服务的源文件名 = "+arg2.getFilename()+",文件地址url = "+arg2.getUrl());
						}
						addGroup(upload_group_img);
					}
					
					@Override
					public void onProgress(int arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		}
	};
	
	private void addGroup(String upload_group_img){
		Group group = new Group();
		BmobRelation relation = new BmobRelation();
		relation.add(userManager.getCurrentUser(UserBean.class));
		group.setGroup_img_address(upload_group_img);
		group.setGroup_name(group_name.getText().toString());
		group.setGroup_content(group_content.getText().toString());
		group.setGroup_owner(userManager.getCurrentUser(UserBean.class));
		group.setGroup_member(relation);
		group.setGroup_peoplecount(1);
		group.setSite(site);
		group.save(getApplicationContext(), new SaveListener() {
			
			@Override
			public void onSuccess() {
				ShowToast("添加成功");
				dialog.dismiss();
				GroupActivity.this.setResult(0,null);
				GroupActivity.this.finish();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {

		if (arg1 == RESULT_CANCELED) {
			return;
		}
		
		switch (arg0) {
		// 拍照获取图片
		case ImageUtils.GET_IMAGE_BY_CAMERA:
			// uri传入与否影响图片获取方式,以下二选一
			// 方式一,自定义Uri(ImageUtils.imageUriFromCamera),用于保存拍照后图片地址
			if(ImageUtils.imageUriFromCamera != null) {
				// 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
//				iv.setImageURI(ImageUtils.imageUriFromCamera);
				
				// 对图片进行裁剪
				ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera);
				break;
			}
			
			break;
		// 手机相册获取图片
		case ImageUtils.GET_IMAGE_FROM_PHONE:
			if(arg2 != null && arg2.getData() != null) {
				// 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
				// iv.setImageURI(data.getData());
				
				// 对图片进行裁剪
				ImageUtils.cropImage(this, arg2.getData());
			}
			break;
		// 裁剪图片后结果
		case ImageUtils.CROP_IMAGE:
			if(ImageUtils.cropImageUri != null) {
				// 可以直接显示图片,或者进行其他处理(如压缩等)
				group_img.setImageURI(ImageUtils.cropImageUri);
				Bitmap bitmap = decodeUriAsBitmap(ImageUtils.cropImageUri);
				saveMyBitmap(bitmap, "group_avator");
			}
			break;
		default:
			break;
		}
	
	};
	
	public void showImagePickDialog() {
		String title = "获取图片方式";
		String[] choices = new String[]{"拍照", "从手机中选择"};
		
		new AlertDialog.Builder(this)
			.setTitle(title)
			.setItems(choices, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					switch (which) {
					case 0:
						ImageUtils.openCameraImage(GroupActivity.this);
						break;
					case 1:
						ImageUtils.openLocalImage(GroupActivity.this);
						break;
					}
				}
			})
			.setNegativeButton("返回", null)
			.show();
	}
	
	/**
	 * 裁剪图片方法
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
		Intent intent = new Intent("Intent.ACTION_PICK");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}
	
	private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	/**
	 * 图片保存到sd卡
	 * @param mBitmap
	 * @param bitName
	 */
	public void saveMyBitmap(Bitmap mBitmap,String bitName)  {
		String img_file = "/sdcard/"+bitName + ".png";
        File f = new File(img_file);
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
                group_img_sd = img_file;
        } catch (IOException e) {
                e.printStackTrace();
        }
}
}
