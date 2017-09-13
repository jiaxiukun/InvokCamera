package baway.com.invokcamera;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import baway.com.invokcamera.utils.Tools;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn;
    private String[] items = new String[]{"选择本地图片", "拍照"};
    private static final String IMAGE_FILE_NAME = "faceImage.jpg";
    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    private ImageView image_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        image_login = (ImageView) findViewById(R.id.image_login);
        btn.setOnClickListener(this);
        image_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                Toast.makeText(this, "没啥作用", Toast.LENGTH_SHORT).show();
                break;
            case R.id.image_login:
                showDialog();
                break;
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("设置头像")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType("image/*");//设置文件类型
                                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                                break;
                            case 1:
                                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                //判断储存卡是否可用，可用进行存储
                                if (Tools.hasSdcard()) {
                                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                                }
                                startActivityForResult(intentFromCapture,
                                        CAMERA_REQUEST_CODE);
                                break;
                        }

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {

                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (Tools.hasSdcard()) {
                        File tempFile = new File(Environment.getExternalStorageDirectory() + IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(this, "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;

            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            image_login.setImageDrawable(drawable);
        }
    }

}
