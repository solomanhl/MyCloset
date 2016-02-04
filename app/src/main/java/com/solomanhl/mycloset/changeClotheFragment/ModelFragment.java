package com.solomanhl.mycloset.changeClotheFragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.solomanhl.file.FileUtils;
import com.solomanhl.mycloset.App;
import com.solomanhl.mycloset.R;
import com.solomanhl.mycloset.SelectPicPopupWindow;
import com.solomanhl.mycloset.fittingRoom.FittingRoomFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link ModelFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link ModelFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class ModelFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //自定义的弹出框类
    SelectPicPopupWindow menuWindow;
    // TODO: Rename and change types of parameters
    private String mParam1;

//    private OnFragmentInteractionListener mListener;
    private String mParam2;
    private App app;
    private LinearLayout bot;
    private GridView gv_model;
    private List<String> tempArray01 = new ArrayList<String>();
    private ImageView del, back;
    private boolean delMode;
    private boolean[] delId;//需要删除的
    private int delNum;
    private TextView cancel, delete;
    private FittingRoomFragment frf;

    private CameraAddMaskFragment cam;
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    takePhoto();
                    break;
                case R.id.btn_pick_photo:
                    pickPhoto();
                    break;
                default:
                    break;
            }
        }

    };

    private void takePhoto() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, cam).addToBackStack("camera").commit();
    }

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_OK = -1;
    private void pickPhoto() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            copyFile(picturePath);
            reFresh();
        }

    }

    private int widthDrawable = 640;//预览和保存的宽度
    private int heightDrawable = 360;
    private void copyFile(String picturePath) {
        Bitmap b = BitmapFactory.decodeFile(picturePath);
        float scaleWidth = (float) widthDrawable/b.getWidth();
        float scaleHeight = (float) heightDrawable/b.getHeight();//宽高比
        Bitmap resizeBmp;
        Matrix matrix = new Matrix();
        float scale;
        if(scaleWidth < scaleHeight) {
            scale = scaleHeight;//取大的
        } else {
            scale = scaleWidth;
        }
        matrix.postScale(scale, scale);//缩放比例
        resizeBmp = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),  matrix, true);

        // 生成文件
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

        // 格式化时间
        String sdate = format.format(date);
        String filename = "model" + sdate + ".png";
        String savePath = app.SDpath + app.AppPath + "model/";
        File fileFolder = new File(savePath);
//			if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
//				fileFolder.mkdir();
//			}
        File pngFile = new File(fileFolder, filename);
        if (pngFile.exists()) {
            pngFile.delete();
        }
        FileOutputStream outputStream = null; // 文件输出流
        try {
            outputStream = new FileOutputStream(pngFile);
            resizeBmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            // out.close();
            // outputStream.write(data); // 写入sd卡中
            outputStream.close(); // 关闭输出流
            app.model = savePath + filename;
        } catch (Exception e) {
            e.printStackTrace();
        }
        b.recycle();
        resizeBmp.recycle();
    }

    public ModelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ModelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ModelFragment newInstance(String param1, String param2) {
        ModelFragment fragment = new ModelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_model, container, false);

        findView(view);
        setOnclickListener();
        findFragment();

        delMode = false;
        bot.setVisibility(View.INVISIBLE);
        reFresh();

        return view;
    }

    private void addModel() {
        String modelPath = app.SDpath + app.AppPath + "model/";
        FileUtils f = new FileUtils();
        File[] files = f.getFiles(modelPath);

        tempArray01.clear();
//        tempArray01 = new ArrayList<String>();
        if (files == null) {

        } else {
            int num = files.length;
            for (int i = 0; i < num; i++) {
                if (!".nomedia".equals(files[i].getName())) {
                    tempArray01.add(modelPath + files[i].getName());
                }
            }
        }
        if (!delMode) {
            tempArray01.add("");//最后加一个加号图片
        }
    }

    private void findFragment() {
        cam = new CameraAddMaskFragment();
        frf = new FittingRoomFragment();
    }

    private void setOnclickListener() {
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("info", "del Onclicked");
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, modelFragment).addToBackStack("model").commit();
                delMode = true;
                reFresh();
                del.setVisibility(View.INVISIBLE);
                bot.setVisibility(View.VISIBLE);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("info", "back Onclicked");
                delNum = 0;
                if (delMode) {//删除模式返回到正常模式
                    delMode = false;
                    reFresh();
                    del.setVisibility(View.VISIBLE);
                    bot.setVisibility(View.INVISIBLE);
                } else {//正常模式返回到上个frag
                    getActivity().getSupportFragmentManager().popBackStack();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("info", "cancel Onclicked");
                delNum = 0;
                if (delMode) {//删除模式返回到正常模式
                    delMode = false;
                    reFresh();
                    del.setVisibility(View.VISIBLE);
                    bot.setVisibility(View.INVISIBLE);
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("info", "delete Onclicked");
                delete();
            }
        });
    }

    private void delete() {
        for (int i = 0; i < tempArray01.size(); i++) {
            if (delId[i]) {//第I个需要删除
                FileUtils f = new FileUtils();
                f.deleteFile(tempArray01.get(i).toString());
                delNum--;
            }
        }
        showDelNum();
        reFresh();
    }

    private void reFresh() {
        delNum = 0;
        addModel();
        updateGridView(tempArray01);
    }

    private void findView(View view) {
        bot = (LinearLayout) view.findViewById(R.id.bot);
        gv_model = (GridView) view.findViewById(R.id.gv_model);
        del = (ImageView) view.findViewById(R.id.del);
        back = (ImageView) view.findViewById(R.id.back);
        cancel = (TextView) view.findViewById(R.id.cancel);
        delete = (TextView) view.findViewById(R.id.delete);
    }

    private void updateGridView(List<String> dataArray) {
        // TODO Auto-generated method stub
        //生成动态数组，并且转入数据
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        delId = new boolean[dataArray.size()];
        for (int i = 0; i < dataArray.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            if ("".equals(dataArray.get(i))) {
                map.put("ItemImage", R.mipmap.add);// 添加图像资源的ID
            } else {
                map.put("ItemImage", dataArray.get(i).toString());
                delId[i] = false;//初始都是不删除的
            }
            lstImageItem.add(map);
        }
        //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
        SimpleAdapter saImageItems = new SimpleAdapter(getActivity(), //没什么解释
                lstImageItem,//数据来源
                R.layout.grid_model,//night_item的XML实现

                //动态数组与ImageItem对应的子项
                new String[]{"ItemImage"},

                //ImageItem的XML文件里面的ImageView ID
                new int[]{R.id.iv_gridmodel});
        //添加并且显示
        gv_model.setAdapter(saImageItems);
        //添加消息处理
        gv_model.setOnItemClickListener(new ItemClickListener());
    }

    //从相册选择或者拍照
    private void openDialog() {
//        iv.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
        //gridView里面已经在监听器里面了，直接弹窗
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(getActivity(), itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(getActivity().findViewById(R.id.rl_model), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
//            }
//        });

    }

    private void showDelNum() {
        if (delNum == 0) {
            delete.setText(R.string.delete);
            delete.setTextColor(Color.GRAY);
        } else {
            delete.setText(getString(R.string.delete) + "(" + String.valueOf(delNum) + ")");
            delete.setTextColor(Color.RED);
        }
    }

    // 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
    class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
                                // click happened
                                View arg1,// The view within the AdapterView that was clicked
                                int arg2,// The position of the view in the adapter
                                long arg3// The row id of the item that was clicked
        ) {
            // 在本例中arg2=arg3
            HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
            // 显示所选Item的ItemText

//            Log.w("info","arg0:" + String.valueOf(arg0) + ";arg1:" + String.valueOf(arg1) + ";arg2:" + String.valueOf(arg2) + ";arg3:" + String.valueOf(arg3));
//            Log.w("info", "Click position " + String.valueOf(arg2) + ".The model is " + app.model);

            ImageView iv = (ImageView) arg1.findViewById(R.id.iv_gridmodel);
            if (delMode) {
                if (delId[arg2]) {//如果标记删除，转成不删除
                    delId[arg2] = false;
                    //去掉水印
                    iv.setBackgroundResource(0);
                    iv.setAlpha(1.0f);
                    delNum--;
                } else {//如果标记不删除，转成删除
                    delId[arg2] = true;
                    //加上选中的水印
                    iv.setBackgroundResource(R.mipmap.selected);
                    iv.setAlpha(0.5f);
                    delNum++;
                }
                showDelNum();
            } else {
                if ( arg2 == tempArray01.size() - 1 ) {//点的是加号图片
                    app.model = "";
                    openDialog();
                }else{
                    app.model = item.get("ItemImage").toString();
                    //正常模式转到试衣间
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, frf).addToBackStack("FittinRoom").commit();
                }
            }


        }

    }
    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
