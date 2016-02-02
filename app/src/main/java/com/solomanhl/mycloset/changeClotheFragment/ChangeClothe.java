package com.solomanhl.mycloset.changeClotheFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.solomanhl.file.FileUtils;
import com.solomanhl.mycloset.App;
import com.solomanhl.mycloset.R;
import com.solomanhl.mycloset.utils.FileUtil;
import com.solomanhl.mycloset.view.DrawImageLayout;
import com.solomanhl.mycloset.view.ImageInfo;

import java.io.InputStream;

/**
 * Created by solomanhl on 2015/12/24.
 */
public class ChangeClothe extends Fragment {

    private App app;
    private FrameLayout drawImageLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        app = (App) getActivity().getApplicationContext(); // 获得全局变量
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.content_main, container, false);

        drawImageLayout = (FrameLayout) view.findViewById(R.id.drawImageLayout);

        WriteAssetsFileToSd("model.png");
        WriteAssetsFileToSd("yifu.png");
        WriteAssetsFileToSd("qunzi.png");
        setDrawImageView();

        return view;
    }

    public void WriteAssetsFileToSd(String fileName) {

        try {
            InputStream in = getResources().getAssets().open(fileName);
            // 写到本地
            FileUtils file = new FileUtils();
            file.writeFromInput(app.SDpath + "MyCloset/", fileName, in);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDrawImageView() {
        DrawImageLayout layout = new DrawImageLayout(getActivity());
        FrameLayout.LayoutParams params =  new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        layout.setImages(getInfos());
//        getActivity().setContentView(layout);
        drawImageLayout.addView(layout);
    }

    private ImageInfo[] getInfos(){
        ImageInfo[] infos = new ImageInfo [3];
        ImageInfo info = new ImageInfo();
        info.setWidth(390);
        info.setHeight(1050);
        info.setX(390/2);
        info.setY(1050/2);
        info.setPath(FileUtil.getImagePath("model", "png"));
//        info.setPath(FileUtil.getImagePath("2014-03big/1393830348113"));
        infos[0]=info;

        info = new ImageInfo();
        info.setWidth(360);
        info.setHeight(640);
        info.setX(360/2);
        info.setY(640/2);
        info.setPath(FileUtil.getImagePath("qunzi", "png"));
        infos[1]=info;

        info = new ImageInfo();
        info.setWidth(360);
        info.setHeight(640);
        info.setX(360/2);
        info.setY(640/2);
        info.setPath(FileUtil.getImagePath("yifu", "png"));
        infos[2]=info;
        return infos;
    }
}
