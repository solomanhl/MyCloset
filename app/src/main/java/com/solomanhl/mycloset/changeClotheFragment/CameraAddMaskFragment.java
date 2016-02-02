package com.solomanhl.mycloset.changeClotheFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.solomanhl.mycloset.App;
import com.solomanhl.mycloset.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraAddMaskFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private App app;
    private SurfaceView surfaceView;
    private Camera camera;
    private Camera.Parameters parameters = null;
    private WindowManager mWindowManager;
    private int windowWidth;// 获取手机屏幕宽度
    private int windowHight;// 获取手机屏幕高度
    private float density;//屏幕密度
    private int photoWidth = 1280;//预览和保存的宽度
    private int photoHeight = 720;
    private String savePath;
    private Bundle bundle = null;// 声明一个Bundle对象，用来存储数据
    private int IS_TOOK = 0;// 是否已经拍照 ,0为否

    private ImageView take_photo;

    public CameraAddMaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraAddMaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraAddMaskFragment newInstance(String param1, String param2) {
        CameraAddMaskFragment fragment = new CameraAddMaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 检验是否有SD卡
     *
     * @true or false
     */
    public static boolean isHaveSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        app = (App) getActivity().getApplicationContext(); // 获得全局变量
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cameraaddmask, container, false);
        savePath = app.SDpath + app.AppPath + "model/";
        init(view);
        return view;
    }

    private void init(View view) {
        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = mWindowManager.getDefaultDisplay().getWidth();
        windowHight = mWindowManager.getDefaultDisplay().getHeight();

        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）

        // 按钮
        take_photo = (ImageView) view.findViewById(R.id.take_photo);

        take_photo.setOnClickListener(this);

        // 照相机预览的空间
        surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setFixedSize(windowWidth, windowWidth); // 设置Surface分辨率
        surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮
        surfaceView.getHolder().addCallback(new SurfaceCallback());// 为SurfaceView的句柄添加一个回调函数
    }

    /**
     * 三个按钮点击事件
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.take_photo:
                // 拍照
                if (camera != null) {
                    camera.takePicture(null, null, new MyPictureCallback());
                }
                break;
        }
    }

    private void save() {
        if (bundle == null) {
            Toast.makeText(getContext().getApplicationContext(), "bundle null",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (isHaveSDCard())
                saveToSDCard(bundle.getByteArray("bytes"));
//                else
//                    saveToRoot(bundle.getByteArray("bytes"));
        }
    }

    private Bitmap rotate(Bitmap b, int deg) {
        Matrix m = new Matrix();
        m.setRotate(deg, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
        float targetX, targetY;
        if (deg == 90) {
            targetX = b.getHeight();
            targetY = 0;
        } else {
            targetX = b.getHeight();
            targetY = b.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(b.getHeight(), b.getWidth(),
                Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(b, m, paint);
        return bm1;
    }

    /**
     * 将拍下来的照片存放在SD卡中
     *
     * @param data
     * @throws IOException
     */
    public void saveToSDCard(byte[] data) {
        // 剪切为四边形
        try {
            Bitmap b = byteToBitmap(data);
            //逆时针旋转90度
            b = rotate(b, 90);
            //这里得到的b是预设拍照分辨率的一半
//			Bitmap bitmap = Bitmap.createBitmap(b, 0, 0, windowWidth, windowWidth);
            int startY = 0;
            startY = take_photo.getHeight() * b.getHeight() / windowHight;
//            Bitmap bitmap = Bitmap.createBitmap(b, 0, startY, b.getWidth(), b.getHeight() - startY);//左下为原点
            Bitmap bitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight() - startY);//左上为原点
            // 生成文件
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

            // 格式化时间
            String sdate = format.format(date);
            String filename = "model" + sdate + ".png";
            File fileFolder = new File(savePath);
//			if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
//				fileFolder.mkdir();
//			}
            File pngFile = new File(fileFolder, filename);
            if (pngFile.exists()) {
                pngFile.delete();
            }
            FileOutputStream outputStream = new FileOutputStream(pngFile); // 文件输出流
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();

            // out.close();
            // outputStream.write(data); // 写入sd卡中
            outputStream.close(); // 关闭输出流
            Intent intent = new Intent();
            intent.putExtra("path", savePath + filename);
            getActivity().setResult(1, intent);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 把图片byte流编程bitmap
     *
     * @param data
     * @return
     */
    private Bitmap byteToBitmap(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int i = 0;
        while (true) {
            if ((options.outWidth >> i <= 1000)
                    && (options.outHeight >> i <= 1000)) {
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                b = BitmapFactory
                        .decodeByteArray(data, 0, data.length, options);
                break;
            }
            i += 1;
        }
        return b;

    }

    /**
     * 重构照相类
     *
     * @author
     */
    private final class MyPictureCallback implements PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                bundle = new Bundle();
                bundle.putByteArray("bytes", data); // 将图片字节数据保存在bundle当中，实现数据交换

                // saveToSDCard(data); // 保存图片到sd卡中
                Toast.makeText(getActivity().getApplicationContext(), "success",
                        Toast.LENGTH_SHORT).show();
                // camera.startPreview(); // 拍完照后，重新开始预览
                IS_TOOK = 1;
                save();
                getActivity().getSupportFragmentManager().popBackStack();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 重构相机照相回调类
     *
     * @author pc
     */
    private final class SurfaceCallback implements Callback {

        @SuppressWarnings("deprecation")
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub
            try {
                //参考SDK中的API，获取相机的参数：
                Camera.Parameters parameters = camera.getParameters();
                //获取预览的各种分辨率
                List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                //获取摄像头支持的各种分辨率
                List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();

                parameters.setPictureFormat(PixelFormat.JPEG);  // 设置图片格式
                parameters.setPreviewSize(photoWidth, photoHeight); // 设置预览大小
//				parameters.setPreviewFrameRate(5); // 设置每秒显示4帧
                parameters.setPictureSize(photoWidth, photoHeight); // 设置保存的图片尺寸
//				parameters.setJpegQuality(80); // 设置照片质量
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            try {
                camera = Camera.open(); // 打开摄像头
                camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象
                camera.setDisplayOrientation(getPreviewDegree((Activity) getActivity()));
                camera.startPreview(); // 开始预览
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            if (camera != null) {
                camera.release(); // 释放照相机
                camera = null;
            }
        }


        // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
        public int getPreviewDegree(Activity activity) {
            // 获得手机的方向
            int rotation = activity.getWindowManager().getDefaultDisplay()
                    .getRotation();
            int degree = 0;
            // 根据手机的方向计算相机预览画面应该选择的角度
            switch (rotation) {
                case Surface.ROTATION_0:
                    degree = 90;
                    break;
                case Surface.ROTATION_90:
                    degree = 0;
                    break;
                case Surface.ROTATION_180:
                    degree = 270;
                    break;
                case Surface.ROTATION_270:
                    degree = 180;
                    break;
            }
            return degree;
        }

        /**
         * 通过文件地址获取文件的bitmap
         *
         * @param path
         * @return
         * @throws IOException
         */

        public Bitmap getBitmapByPath(String path) throws IOException {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                    new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int i = 0;
            Bitmap bitmap = null;
            while (true) {
                if ((options.outWidth >> i <= 1000)
                        && (options.outHeight >> i <= 1000)) {
                    in = new BufferedInputStream(
                            new FileInputStream(new File(path)));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
            return bitmap;
        }

    }

}
