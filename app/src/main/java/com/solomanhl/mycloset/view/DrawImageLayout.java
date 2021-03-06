package com.solomanhl.mycloset.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.solomanhl.mycloset.App;
import com.solomanhl.mycloset.utils.LogUtil;
import com.solomanhl.mycloset.utils.ScalingUtilities;

interface ImageLoadListener {
    void loadSuccess(ImageInfo[] infos);

    void loadFalied();
}

public class DrawImageLayout extends FrameLayout implements ImageLoadListener {
    private final int MESSAGE_TYPE_LOADIMAGE_SUCCESS = 1;
    private final int MESSAGE_TYPE_LOADIMAGE_FAILED = MESSAGE_TYPE_LOADIMAGE_SUCCESS + 1;
    private ImageInfo[] pats = null;
    /**
     * 手指头的x坐标
     */
    private float X = 0f;
    /**
     * 手指头的y坐标
     */
    private float Y = 0f;
    /**
     * 按下时手指头的x坐标与图片的x坐标的距离
     **/
    private float CX = 0f;
    /**
     * 按下时手指头的y坐标与图片的y坐标的距离
     **/
    private float CY = 0f;
    private String tag = this.getClass().getSimpleName();
    private MyImageView topImageInfo = null;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_TYPE_LOADIMAGE_SUCCESS:
                    if (pats != null) {
                        loadImage(pats);
                    }
                    break;
            }
        }

    };
    private float[] rotalP = null;
    private float[] rotalP_2 = null;
    private float[] rotalC = null;
    private float preLength = 480.0f;
    private float length = 480.0f;
    private float preCos = 0f;
    private float cos = 0f;
    private boolean bool = true;
    private boolean Begin = true;
    private float[] p1 = new float[2];
    private float[] p2 = new float[2];

    private App app;
    public DrawImageLayout(Context context) {
        super(context);
        app = (App) context.getApplicationContext(); // 获得全局变量
//		setWillNotDraw(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtil.i(tag, "onAttachedToWindow()");
        ImageLoadThread thread = new ImageLoadThread();
        thread.setPath(pats);
        thread.setImageLoadListener(this);
        thread.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setImages(ImageInfo[] paths) {
        if (paths == null)
            return;
        pats = paths;
    }

    private void loadImage(ImageInfo[] bitmap) {
        LogUtil.i(tag, "loadImage()");
        if (bitmap == null || bitmap.length <= 0)
            return;
        for (int i = 0; i < bitmap.length; i++) {
            ImageInfo info = bitmap[i];
            MyImageView iv = new MyImageView(super.getContext());
            iv.setPiority(i);
            // iv.setPadding(3, 3, 3, 3);;
            LogUtil.i(tag, "loadImage() -- bit:" + info.getBit());
            iv.setImageBitmap(info.getBit());
            //soloman设置yImageView包含的衣服类型
            iv.setYifu_type(bitmap[i].getYifu_type());
//			iv.setImageResource(R.drawable.ic_launcher);
            FrameLayout.LayoutParams pa = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            topImageInfo = iv;
            float preX = info.getX();
            float preY = info.getY();
            iv.setPreX(preX);
            iv.setPreY(preY);
            iv.setmWidth(info.getWidth());
            iv.setmHeight(info.getHeight());
            iv.setLayoutParams(pa);
            if (i == bitmap.length - 1) {
                iv.setDrawBorder(true);
            }
            LogUtil.i(tag, "loadImage() -- preX:" + preX + ";preY:" + preY);
//			iv.getImageMatrix().translate(preX-info.getWidth()/2, preY-info.getHeight()/2);
            iv.getmMatrix().postTranslate(preX - info.getWidth() / 2, preY - info.getHeight() / 2);
            iv.getImageMatrix().set(iv.getmMatrix());
            addView(iv, pa);

            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.d(tag, "onTouchEvent() -- 第一根手指点下...");
                actionDown(event);
                break;
            //副点按下
            case MotionEvent.ACTION_POINTER_DOWN:
                topImageInfo.getSavedMatrix().set(topImageInfo.getmMatrix());
                p2[0] = event.getX(1);
                p2[1] = event.getY(1);
                topImageInfo.setMood(MyImageView.MOOD_ACTION_POINTERDOWN);
                LogUtil.d(tag, "onTouchEvent() -- 副手指点下... p2[0]:" + p2[0] + "; p2[1]:" + p2[1]);
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.d(tag, "onTouchEvent() -- 手指头抬起..");
                CX = 0f;
                CY = 0f;
                topImageInfo.setMood(MyImageView.MOOD_ACTION_UP);
                Begin = false;
                bool = true;
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                topImageInfo.setMood(MyImageView.MOOD_ACTION_POINTERUP);
                LogUtil.d(tag, "onTouchEvent() -- 副手指头抬起..");
                Begin = false;
                bool = true;
                return true;
            case MotionEvent.ACTION_MOVE:
                LogUtil.d(tag, "onTouchEvent() -- ACTION_MOVE..");
                boolean b = actionMove(event);
                if (b)
                    return b;
                break;
        }
        LogUtil.i(tag, "onTouchEvent() -- 开始刷新..");
        topImageInfo.setImageMatrix(topImageInfo.getmMatrix());
        invalidate();
        return true;
    }

    private boolean actionMove(MotionEvent event) {
        if (Begin && topImageInfo.getMood() == MyImageView.MOOD_ACTION_DOWN) {
//			topImageInfo.setMood(MyImageView.MOOD_ACTION_MOVE);
            if (spacingSingel(event.getX(0), event.getY(0), p1[0], p1[1]) < 5)
                return true;
            p1[0] = event.getX(0);
            p1[1] = event.getY(0);
//			LogUtil.d(tag, "actionMove() -- move.. preX:"+topImageInfo.getPreX()+"; preY:"+topImageInfo.getPreY());
            // 1根手指头移动
            this.X = event.getX();
            this.Y = event.getY();
            topImageInfo.getmMatrix().set(topImageInfo.getSavedMatrix());
            rotalP = rotalPoint(new float[]{this.X, this.Y}, topImageInfo.getPreX(),
                    topImageInfo.getPreY(), topImageInfo.getmMatrix());
            rotalC = getT(topImageInfo.getmWidth() / 2f, topImageInfo.getmHeight() / 2f, X + CX,
                    Y + CY, topImageInfo.getmMatrix());
//			LogUtil.i(tag, "actionMove() -- x:" + rotalC[0] + ";y:" + rotalC[1]);
            float oldPreX = topImageInfo.getPreX();
            float oldPreY = topImageInfo.getPreY();
            topImageInfo.setPreX(X + CX);
            topImageInfo.setPreY(Y + CY);


            //soloman获取MyImageView包含的衣服类型
//            String s = topImageInfo.getYifu_type();
            switch (topImageInfo.getYifu_type()){
                case "shangyi":
                    app.shangyi_info[2] = topImageInfo.getPreX();
                    app.shangyi_info[3] = topImageInfo.getPreY();
                    break;
                case "kuzi":
                    app.kuzi_info[2] = topImageInfo.getPreX();
                    app.kuzi_info[3] = topImageInfo.getPreY();
                    break;
                case "qunzi":
                    app.qunzi_info[2] = topImageInfo.getPreX();
                    app.qunzi_info[3] = topImageInfo.getPreY();
                    break;
                default:
                    break;
            }
//			topImageInfo.getmMatrix().postTranslate(topImageInfo.getPreX() - oldPreX, topImageInfo.getPreY() - oldPreY);
//			LogUtil.i(tag, "actionMove() -- topImageInfo.getPreX() - oldPreX:" +(topImageInfo.getPreX() - oldPreX)
//					+ ";  topImageInfo.getPreY() - oldPreY:" + (topImageInfo.getPreY() - oldPreY));
            topImageInfo.transFrame(topImageInfo.getPreX() - oldPreX, topImageInfo.getPreY() - oldPreY);
            LogUtil.e(tag, "actionMove() -- top width:" + topImageInfo.getmWidth() + ";  top height:" + topImageInfo.getmHeight());
        }

        // 两指移动
        if (topImageInfo.getMood() == MyImageView.MOOD_ACTION_POINTERDOWN) {
            float p1J = spacingSingel(event.getX(0), event.getY(0), p1[0], p1[1]);
            float p2J = spacingSingel(event.getX(1), event.getY(1), p2[0], p2[1]);
//			LogUtil.d(tag, "onTouchEvent() -- 副手指  p2[0]:"+p2[0]+"; p2[1]:"+p2[1]+"; 最新的x:"+event.getX(1)+"; y:"+event.getY(1));
//			LogUtil.d(tag, "onTouchEvent() -- 两个点move.. 手指1移动的距离 :"+p1J+";  手指2移动的距离 :"+p2J);
            // 防抖功能
            // 如果两个手指头移动的距离同时都小于5
            if (p1J < 5 && p2J < 5) {
                return true;
            }
//			LogUtil.d(tag, "actionMove() -- MOOD_ACTION_POINTERDOWN.. preX:"+topImageInfo.getPreX()+"; preY:"+topImageInfo.getPreY());
            p1[0] = event.getX(0);
            p1[1] = event.getY(0);
            p2[0] = event.getX(1);
            p2[1] = event.getY(1);
//			topImageInfo.getmMatrix().set(topImageInfo.getSavedMatrix());
            rotalP = rotalPoint(new float[]{event.getX(0), event.getY(0)},
                    topImageInfo.getPreX(), topImageInfo.getPreY(), topImageInfo.getmMatrix());
            rotalP_2 = rotalPoint(new float[]{event.getX(1), event.getY(1)},
                    topImageInfo.getPreX(), topImageInfo.getPreY(), topImageInfo.getmMatrix());
            if ((Math.abs(rotalP[0] - topImageInfo.getPreX()) < topImageInfo.getmWidth() / 2f)
                    && (Math.abs(rotalP[1]
                    - topImageInfo.getPreY()) < topImageInfo.getmHeight() / 2f)
                    && (Math.abs(rotalP_2[0]
                    - topImageInfo.getPreX()) < topImageInfo.getmWidth() / 2f)
                    && (Math.abs(rotalP_2[1]
                    - topImageInfo.getPreY()) < topImageInfo.getmHeight() / 2f) || true) {
                if (bool) {
                    // 第一次两指头点来，记录下角度和长度
                    preLength = spacing(event);
                    preCos = cos(event);
                    bool = false;
                }
                // 获取最新角度和长度
                length = spacing(event);
                //soloman获取MyImageView包含的衣服类型
//                switch (topImageInfo.getYifu_type()){
//                    case "shangyi":
//                        cos = cos(event) + app.shangyi_info[4];
//                        break;
//                    case "kuzi":
//                        cos = cos(event) + app.kuzi_info[4];
//                        break;
//                    case "qunzi":
//                        cos = cos(event) + app.qunzi_info[4];
//                        break;
//                    default:
//                        break;
//                }
                LogUtil.i(tag, "actionMove() -- 旋转角度:" + cos);
                float width = topImageInfo.getmWidth();
                float height = topImageInfo.getmHeight();
                LogUtil.i(tag, "actionMove() -- width:" + width + "; height:" + height);
                // 放大和缩小
                if (length - preLength != 0) {

                    float scW = (1.0f + (length - preLength) / length);
                    topImageInfo.getmMatrix().postScale(scW, scW, topImageInfo.getPreX(), topImageInfo.getPreY());
//					scale(width/2, height/2, topImageInfo.getPreX(), topImageInfo.getPreY(), topImageInfo.getmMatrix());
                    topImageInfo.scalFrame(scW);

                    //soloman获取MyImageView包含的衣服类型
                    switch (topImageInfo.getYifu_type()){
                        case "shangyi":
                            app.shangyi_info[0] *= scW;
                            app.shangyi_info[1] *= scW;
                            break;
                        case "kuzi":
                            app.kuzi_info[0] *= scW;
                            app.kuzi_info[1] *= scW;
                            break;
                        case "qunzi":
                            app.qunzi_info[0] *= scW;
                            app.qunzi_info[1] *= scW;
                            break;
                        default:
                            break;
                    }
                }

                // 旋转
                if (Math.abs(cos) > 5 && Math.abs(cos) < 177
                        && Math.abs(cos - preCos) < 15) {
                    topImageInfo.getmMatrix().postRotate(cos - preCos);
                    this.getT(width / 2f, height / 2f,
                            topImageInfo.getPreX(), topImageInfo.getPreY(), topImageInfo.getmMatrix());
                    topImageInfo.rotateFrame(width, height);
                }
                preCos = cos;
                preLength = length;

                //soloman获取MyImageView包含的衣服类型
//                switch (topImageInfo.getYifu_type()){
//                    case "shangyi":
//                        app.shangyi_info[4] = cos;
//                        break;
//                    case "kuzi":
//                        app.kuzi_info[4] = cos;
//                        break;
//                    case "qunzi":
//                        app.qunzi_info[4] = cos;
//                        break;
//                    default:
//                        break;
//                }

            }
        }
        return false;
    }

    private boolean actionDown(MotionEvent event) {
        LogUtil.d(tag, "actionDown() -- down..");
        LogUtil.i(tag, "actionDown() -- topImageInfo.getPreX() :" + topImageInfo.getPreX() + ";topImageInfo.getPreY():"
                + topImageInfo.getPreY());
        order(event);
        // 设置最顶上的imageview
        topImageInfo = findTopImage();
        LogUtil.i(tag, "actionDown() -- top width:" + topImageInfo.getmWidth() + ";  top height:" + topImageInfo.getmHeight());
        this.X = event.getX();
        this.Y = event.getY();
        CX = topImageInfo.getPreX() - event.getX();
        CY = topImageInfo.getPreY() - event.getY();
        LogUtil.i(tag, "actionDown() -- this.X :" + this.X + ";this.Y:"
                + this.Y + ";CX:" + CX + ";CY:" + CY);
        topImageInfo.getSavedMatrix().set(topImageInfo.getmMatrix());
        Begin = true;
        p1[0] = event.getX();
        p1[1] = event.getY();
        topImageInfo.setMood(MyImageView.MOOD_ACTION_DOWN);
        return true;
    }

    /**
     * 找到优先级最高的view
     *
     * @return
     */
    private MyImageView findTopImage() {
        int pre = 0;
        int index = 0;
        for (int i = 0; i < getChildCount(); i++) {
            MyImageView my = (MyImageView) getChildAt(i);
            if (my.getPiority() > pre) {
                pre = my.getPiority();
                index = i;
            }
        }
        return (MyImageView) getChildAt(index);
    }

    @Override
    public void loadSuccess(ImageInfo[] bitmap) {
        LogUtil.i(tag, "loadSuccess() -- bitmap:" + bitmap);
        if (bitmap != null)
            LogUtil.i(tag, "loadSuccess() -- bitmap.length is:" + bitmap.length);
        else
            LogUtil.i(tag, "loadSuccess() -- bitmap.length is:" + 0);
        Message msg = handler.obtainMessage();
        msg.what = MESSAGE_TYPE_LOADIMAGE_SUCCESS;
        this.pats = bitmap;
        handler.sendMessage(msg);
    }

    @Override
    public void loadFalied() {
        LogUtil.i(tag, "loadFalied()");
    }

    /**
     * @param preX 图片中心点x
     * @param preY 图片中心点y
     * @param x    手指头x坐标加上移动的x轴距离
     * @param y    手指头y坐标加上移动的y轴距离
     * @param iv
     * @return
     */
    public float[] getT(float preX, float preY, float x, float y, Matrix iv) {
        float[] re = new float[2];
        float[] matrixArray = new float[9];
        iv.getValues(matrixArray);
        float a = x - preX * matrixArray[0] - preY * matrixArray[1];
        float b = y - preX * matrixArray[3] - preY * matrixArray[4];
        matrixArray[2] = a;
        matrixArray[5] = b;
        iv.setValues(matrixArray);
        re[0] = a;
        re[1] = b;
        return re;
    }

    /**
     * 得到旋转点
     *
     * @param p      当前手指头的x,y坐标
     * @param X      图片之前的x坐标
     * @param Y      图片之前的y坐标
     * @param matrix
     * @return
     */
    public float[] rotalPoint(float[] p, float X, float Y, Matrix matrix) {
        float re[] = new float[2];
        float matrixArray[] = new float[9];
        matrix.getValues(matrixArray);
        LogUtil.i(tag, "rotalPoint() -- matrixArray[0]: " + matrixArray[0] + "; matrixArray[1] :" + matrixArray[1] + "; matrixArray[2] :" + matrixArray[1]);
        LogUtil.i(tag, "rotalPoint() -- matrixArray[3]: " + matrixArray[3] + "; matrixArray[4] :" + matrixArray[4] + "; matrixArray[5] :" + matrixArray[5]);
        // 计算出x,y的差值
        float a = p[0] - X;
        float b = p[1] - Y;
        // 矩阵公式
        // x' = a*x+b*y+c
        re[0] = a * matrixArray[0] - b * matrixArray[1] + X;
        re[1] = -a * matrixArray[3] + b * matrixArray[4] + Y;
//		re[0] = a * matrixArray[0] + b * matrixArray[1] + X;
//		re[1] = a * matrixArray[3] + b * matrixArray[4] + Y;
        LogUtil.i(tag, "rotalPoint() -- re[0]: " + re[0] + "; re[1] :" + re[1] + "; a :" + a + "; b:" + b + ";X:" + X + ";Y:" + Y);
        return re;
    }

    /**
     * 计算长度
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float spacingSingel(float newX, float newY, float oldX, float oldY) {
        float x = newX - oldX;
        float y = newY - oldY;
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算余弦
     *
     * @param event
     * @return
     */
    private float cos(MotionEvent event) {
        // LogUtil.i("XY", String.valueOf(event.getX(0))
        // + " " + String.valueOf(event.getY(0))
        // + " " + String.valueOf(event.getX(1))
        // + " " + String.valueOf(event.getY(1)));
        if ((event.getX(0) - event.getX(1)) * (event.getY(0) - event.getY(1)) > 0) {
            return (float) ((float) Math.acos(Math.abs(event.getX(0)
                    - event.getX(1))
                    / spacing(event))
                    / Math.PI * 180f);
        }
        if ((event.getX(0) - event.getX(1)) * (event.getY(0) - event.getY(1)) < 0) {
            return (float) ((float) Math.acos(-Math.abs(event.getX(0)
                    - event.getX(1))
                    / spacing(event))
                    / Math.PI * 180f);
        }
        if (event.getX(0) - event.getX(1) == 0) {
            return (float) 90f;
        }
        if (event.getY(0) - event.getY(1) == 0) {
            return 0f;
        }
        return 45f;
    }

    public float[] scale(float preX, float preY, float x, float y, Matrix matrix) {
        float[] matrixArray = new float[9];
        matrix.getValues(matrixArray);
        float a = x - preX;
        float b = y - preY;
        matrixArray[2] = a;
        matrixArray[5] = b;
        matrix.setValues(matrixArray);
        float[] scale = {a, b};
        return scale;
    }

    public void setToO(Matrix matrix) {
        float[] matrixArray = new float[9];
        matrix.getValues(matrixArray);
        float a = 0f;
        float b = 0f;
        matrixArray[2] = a;
        matrixArray[5] = b;
        matrix.setValues(matrixArray);
    }

    public void order(MotionEvent event) {
        MyImageView temp = null;
        LogUtil.i(tag, "order() -- event.x:" + event.getX() + ";event.y:" + event.getY());
        for (int i = (getChildCount() - 1); i > -1; i--) {
            temp = (MyImageView) getChildAt(i);
            LogUtil.i(tag, "order() -- i:" + i + ";  width:" + temp.getmWidth() + "; height:" + temp.getmHeight());
//			rotalP = rotalPoint(new float[] { event.getX(), event.getY() },
//					temp.getPreX(),
//					temp.getPreY(),
//					temp.getImageMatrix());
            // 获取触控点
            float tx = event.getX();
            float ty = event.getY();
            // 存放新坐标的数组
            float[] dst = new float[2];
            // 触控点坐标的数组
            float[] src = {tx, ty};
            Matrix matrix = new Matrix();
            // 获取绘制图片的Matrix，并转换mantrix
            // set inverse to be the inverse of this matrix.
            if (temp.getImageMatrix().invert(matrix)) {
                // 触控坐标根据matrix转换成新的坐标，并存放于dst
                matrix.mapPoints(dst, src);
            }
            boolean isSelect = false;
            float[] ma = new float[9];
            temp.getImageMatrix().getValues(ma);

            LogUtil.i(tag, "order() -- dst[0]:" + dst[0] + " dst[1]:" + dst[1] + "; tx:" + tx + "; ty:" + ty);
            /**
             * 判断是否击中bitmap
             */
            if (dst[0] >= 0 && dst[0] <= temp.getmWidth() && dst[1] >= 0
                    && dst[1] <= temp.getmHeight()) {
                isSelect = true;
            }
            LogUtil.i(tag, "order() -- isSelect:" + isSelect);
            LogUtil.i(tag, "order() -- temp.getPreX():" + temp.getPreX() + "; temp.getPreY():" + temp.getPreY());
            LogUtil.d(tag, "order() -- width*scale:" + temp.getmWidth() * ma[0] + "; height*scale:" + temp.getmHeight() * ma[0] + "; scale:" + ma[0]);
//			if(temp.getPreX()>screenWidth/2||temp.getPreY()>screenHeight/2)
//				count = 1;
//			if ((Math.abs(temp.getPreX() - rotalP[0]) < temp.getmWidth()*ma[0]/count )
//					&& (Math.abs(temp.getPreY()
//							- rotalP[1]) < temp.getmHeight()*ma[0]/count )) {
            if (isSelect) {
                for (int j = (getChildCount() - 1); j > -1; j--) {
                    MyImageView child = (MyImageView) getChildAt(j);
                    if (child.getPiority() > temp.getPiority()) {
                        child.setPiority(child.getPiority() - 1);
                    }
                    child.setDrawBorder(false);
                    child.invalidate();
                }
                temp.setPiority(getChildCount() - 1);
                temp.setDrawBorder(true);

                return;
            }
//			boolean b = pointIsOnView(temp,event);
//			if(b){
//				topImageInfo = temp;
//				return;
//			}
        }
    }

    private boolean pointIsOnView(MyImageView temp, MotionEvent event) {
        rotalP = rotalPoint(new float[]{event.getX(), event.getY()},
                temp.getPreX(),
                temp.getPreY(),
                temp.getmMatrix());
        if ((Math.abs(temp.getPreX() - rotalP[0]) < temp.getmWidth() / 2)
                && (Math.abs(temp.getPreY()
                - rotalP[1]) < temp.getmHeight() / 2)) {
            for (int j = (getChildCount() - 1); j > -1; j--) {
                LogUtil.i(tag, "pointIsOnView() -- j:" + j);
                MyImageView child = (MyImageView) getChildAt(j);
                if (child.getPiority() > temp.getPiority()) {
                    child.setPiority(child.getPiority() - 1);
                }
                child.setDrawBorder(false);
                child.invalidate();
            }
            temp.setPiority(getChildCount() - 1);
            temp.setDrawBorder(true);

            return true;
        }
        return false;
    }

    /**
     * 异步加载图片
     *
     * @author Administrator
     */
    class ImageLoadThread extends Thread {
        private ImageInfo[] paths = null;
        private ImageLoadListener listener = null;
        private String tag = this.getClass().getSimpleName();

        public void setPath(ImageInfo[] paths) {
            this.paths = paths;
        }

        public void setImageLoadListener(ImageLoadListener lis) {
            this.listener = lis;
        }

        public void run() {
            LogUtil.i(tag, "run()");
            if (paths == null || paths.length <= 0) {
                if (listener != null)
                    listener.loadFalied();
                return;
            }
            for (int i = 0; i < paths.length; i++) {
//				ImageInfo info = paths[i];
                LogUtil.i(tag, "run() -- widht:" + paths[i].getWidth()
                        + "; height:" + paths[i].getHeight());
                Bitmap bit = ScalingUtilities.createCenterScropBitmap(
                        paths[i].getPath(), paths[i].getWidth(),
                        paths[i].getHeight());
                paths[i].setBit(bit);
//				info.setPriority(i);
            }
            listener.loadSuccess(paths);
        }
    }
}