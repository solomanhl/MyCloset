package com.solomanhl.mycloset.fittingRoom;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.solomanhl.file.FileUtils;
import com.solomanhl.mycloset.App;
import com.solomanhl.mycloset.R;
import com.solomanhl.mycloset.utils.FileUtil;
import com.solomanhl.mycloset.view.DrawImageLayout;
import com.solomanhl.mycloset.view.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FittingRoomFragment extends Fragment {

    private App app;
    private FrameLayout model;
    private FrameLayout room_bg;
    private ListView lv_shangyi,lv_kuzi,lv_qunzi;
    private String[] yifu = new String[3];
    private boolean hasYifu = false;
    private boolean hasKuzi = false;
    private boolean hasQunzi = false;

    public FittingRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        app = (App) getActivity().getApplicationContext(); // 获得全局变量
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fitting_room, container, false);

        findView(view);
        init();

        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init() {
        //不空且存在
        if (!"".equals(app.model) && app.model!=null && FileUtil.fielExist(app.model)){
//            Bitmap bm = BitmapFactory.decodeFile(app.model);
//            model.setImageBitmap(bm);
            Drawable d = Drawable.createFromPath(app.model);
            model.setBackground(d);
            room_bg.setBackgroundResource(R.mipmap.room_bg1);
        }else{
            room_bg.setBackgroundResource(R.mipmap.room_bg2);
        }

        updateListView_shangyi();
        updateListView_kuzi();
        updateListView_qunzi();

        //测试数据
//        yifu[0] = (String) list_shangyi_data.get(0).get("img");
//        yifu[1] = (String) list_kuzi_data.get(0).get("img");
//        yifu[2] = (String) list_qunzi_data.get(0).get("img");
        create_DrawImageLayout();
    }

    private void findView(View view) {
        room_bg = (FrameLayout) view.findViewById(R.id.room_bg);
        model = (FrameLayout) view.findViewById(R.id.model);
        lv_shangyi = (ListView) view.findViewById(R.id.lv_shangyi);
        lv_kuzi = (ListView) view.findViewById(R.id.lv_kuzi);
        lv_qunzi = (ListView) view.findViewById(R.id.lv_qunzi);
    }


    //上衣LIst--------------------------------------------------------------------------------------
    private void updateListView_shangyi() {
        // TODO Auto-generated method stub
        getData_shangyi();

        /*为ListView设置Adapter来绑定数据*/
        MyAdapter_shangyi adapter = new MyAdapter_shangyi(getContext());

        lv_shangyi.setAdapter(adapter);

        //添加消息处理
        lv_shangyi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //点击后在标题上显示点击了第几行                    setTitle("你点击了第"+arg2+"行");
                yifu[0] = (String) list_shangyi_data.get(arg2).get("img");
                create_DrawImageLayout();
            }
        });
    }

    private List<Map<String, Object>> list_shangyi_data = new ArrayList<Map<String, Object>>();
    private void getData_shangyi() {
        Map<String, Object> map = new HashMap<String, Object>();
        String modelPath = app.SDpath + app.AppPath + "shangyi/";
        FileUtils f = new FileUtils();
        File[] files = f.getFiles(modelPath);

        list_shangyi_data.clear();
//        tempArray01 = new ArrayList<String>();
        if (files == null) {

        } else {
            int num = files.length;
            for (int i = 0; i < num; i++) {
                if (!".nomedia".equals(files[i].getName())) {
                    map = new HashMap<String, Object>();
                    map.put("img", modelPath + files[i].getName());
                    list_shangyi_data.add(map);
                }
            }
        }

//        Map<String, Object> map = new HashMap<String, Object>();
//        map = new HashMap<String, Object>();
//        map.put("img", R.mipmap.i_yifu_1);
//        list_shangyi_data.add(map);
//        map = new HashMap<String, Object>();
//        map.put("img", R.mipmap.i_kuzi_1);
//        list_shangyi_data.add(map);
    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        /**
//         * listview中点击按键弹出对话框
//         */
//    }

    public final class ViewHolder{
        public ImageView img;
    }
    public class MyAdapter_shangyi extends BaseAdapter {
        private LayoutInflater mInflater;
        Drawable d;
        public MyAdapter_shangyi(Context context){
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list_shangyi_data.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_yifu, null);
                holder.img = (ImageView)convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
//            holder.img.setBackgroundResource((Integer)list_shangyi_data.get(position).get("img"));

//            d = Drawable.createFromPath((String) list_shangyi_data.get(position).get("img"));
//            holder.img.setBackground(d);

            Bitmap b;
            b = BitmapFactory.decodeFile((String) list_shangyi_data.get(position).get("img"));
            holder.img.setImageBitmap(b);

            return convertView;
        }
    }
//end 上衣-----------------------------------------------------------------------------------------
//裤子LIst--------------------------------------------------------------------------------------
private void updateListView_kuzi() {
    // TODO Auto-generated method stub
    getData_kuzi();
        /*为ListView设置Adapter来绑定数据*/
    MyAdapter_kuzi adapter = new MyAdapter_kuzi(getContext());

    lv_kuzi.setAdapter(adapter);

    //添加消息处理
    lv_kuzi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            //点击后在标题上显示点击了第几行                    setTitle("你点击了第"+arg2+"行");
            yifu[1] = (String) list_kuzi_data.get(arg2).get("img");
            create_DrawImageLayout();
        }
    });
}

    private List<Map<String, Object>> list_kuzi_data = new ArrayList<Map<String, Object>>();
    private void getData_kuzi() {
        Map<String, Object> map = new HashMap<String, Object>();
        String modelPath = app.SDpath + app.AppPath + "kuzi/";
        FileUtils f = new FileUtils();
        File[] files = f.getFiles(modelPath);

        list_kuzi_data.clear();
//        tempArray01 = new ArrayList<String>();
        if (files == null) {

        } else {
            int num = files.length;
            for (int i = 0; i < num; i++) {
                if (!".nomedia".equals(files[i].getName())) {
                    map = new HashMap<String, Object>();
                    map.put("img", modelPath + files[i].getName());
                    list_kuzi_data.add(map);
                }
            }
        }

//        Map<String, Object> map = new HashMap<String, Object>();
//        map = new HashMap<String, Object>();
//        map.put("img", R.mipmap.i_yifu_1);
//        list_shangyi_data.add(map);
//        map = new HashMap<String, Object>();
//        map.put("img", R.mipmap.i_kuzi_1);
//        list_shangyi_data.add(map);
    }

    public class MyAdapter_kuzi extends BaseAdapter {
        private LayoutInflater mInflater;
        Drawable d;
        public MyAdapter_kuzi(Context context){
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list_kuzi_data.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_yifu, null);
                holder.img = (ImageView)convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
//            holder.img.setBackgroundResource((Integer)list_shangyi_data.get(position).get("img"));
            d = Drawable.createFromPath((String) list_kuzi_data.get(position).get("img"));
            holder.img.setBackground(d);

            return convertView;
        }
    }
//end 裤子-----------------------------------------------------------------------------------------
//裙子LIst--------------------------------------------------------------------------------------
private void updateListView_qunzi() {
    // TODO Auto-generated method stub
    getData_qunzi();
        /*为ListView设置Adapter来绑定数据*/
    MyAdapter_qunzi adapter = new MyAdapter_qunzi(getContext());

    lv_qunzi.setAdapter(adapter);

    //添加消息处理
    lv_qunzi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            //点击后在标题上显示点击了第几行                    setTitle("你点击了第"+arg2+"行");
            yifu[2] = (String) list_qunzi_data.get(arg2).get("img");
            create_DrawImageLayout();
        }
    });
}

    private List<Map<String, Object>> list_qunzi_data = new ArrayList<Map<String, Object>>();
    private void getData_qunzi() {
        Map<String, Object> map = new HashMap<String, Object>();
        String modelPath = app.SDpath + app.AppPath + "qunzi/";
        FileUtils f = new FileUtils();
        File[] files = f.getFiles(modelPath);

        list_qunzi_data.clear();
//        tempArray01 = new ArrayList<String>();
        if (files == null) {

        } else {
            int num = files.length;
            for (int i = 0; i < num; i++) {
                if (!".nomedia".equals(files[i].getName())) {
                    map = new HashMap<String, Object>();
                    map.put("img", modelPath + files[i].getName());
                    list_qunzi_data.add(map);
                }
            }
        }

//        Map<String, Object> map = new HashMap<String, Object>();
//        map = new HashMap<String, Object>();
//        map.put("img", R.mipmap.i_yifu_1);
//        list_shangyi_data.add(map);
//        map = new HashMap<String, Object>();
//        map.put("img", R.mipmap.i_kuzi_1);
//        list_shangyi_data.add(map);
    }

    public class MyAdapter_qunzi extends BaseAdapter {
        private LayoutInflater mInflater;
        Drawable d;
        public MyAdapter_qunzi(Context context){
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list_qunzi_data.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_yifu, null);
                holder.img = (ImageView)convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
//            holder.img.setBackgroundResource((Integer)list_shangyi_data.get(position).get("img"));
            d = Drawable.createFromPath((String) list_qunzi_data.get(position).get("img"));
            holder.img.setBackground(d);

            return convertView;
        }
    }
//end 裙子-----------------------------------------------------------------------------------------

    private DrawImageLayout layout = null;
    private FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
//   private int x_yifu, y_yifu, w_yifu, h_yifu;
    private void  create_DrawImageLayout(){
        if (layout !=null){
            model.removeView(layout);
//            x_yifu = info_yifu.getX();
//            y_yifu  = info_yifu.getY();
//            w_yifu = info_yifu.getWidth();
//            h_yifu  = info_yifu.getHeight();
        }else {

        }
        layout = new DrawImageLayout(getContext());
        layout.setLayoutParams(params);
        layout.setImages(getInfos(yifu));//加入图片
        model.addView(layout);
//        setContentView(layout);
        model.invalidate();
    }

    private ImageInfo [] infos = new ImageInfo [3];

    private ImageInfo info_yifu = new ImageInfo();
    private ImageInfo info_kuzi = new ImageInfo();
    private ImageInfo info_qunzi = new ImageInfo();
    private ImageInfo[] getInfos(String[] path){
            info_yifu.setWidth((int) app.shangyi_info[0]);
            info_yifu.setHeight((int) app.shangyi_info[1]);
            info_yifu.setX((int) app.shangyi_info[2]);
            info_yifu.setY((int) app.shangyi_info[3]);
        info_yifu.setYifu_type("shangyi");
//        info.setPath(FileUtil.getImagePath("2014-03big/1393830348113"));
        info_yifu.setPath(path[0]);
        infos[0]=info_yifu;

            info_kuzi.setWidth((int) app.kuzi_info[0]);
            info_kuzi.setHeight((int) app.kuzi_info[1]);
            info_kuzi.setX((int) app.kuzi_info[2]);
            info_kuzi.setY((int) app.kuzi_info[3]);
        info_kuzi.setYifu_type("kuzi");
        info_kuzi.setPath(path[1]);
        infos[1]=info_kuzi;

            info_qunzi.setWidth((int) app.qunzi_info[0]);
            info_qunzi.setHeight((int) app.qunzi_info[1]);
            info_qunzi.setX((int) app.qunzi_info[2]);
            info_qunzi.setY((int) app.qunzi_info[3]);
        info_qunzi.setYifu_type("qunzi");
        info_qunzi.setPath(path[2]);
        infos[2]=info_qunzi;

        return infos;
    }

}
