package com.solomanhl.mycloset.fittingRoom;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.solomanhl.mycloset.App;
import com.solomanhl.mycloset.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FittingRoomFragment extends Fragment {

    private App app;
    private ImageView model;
    private FrameLayout room_bg;

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

    private void init() {
        if (!"".equals(app.model)){
            Bitmap bm = BitmapFactory.decodeFile(app.model);
            model.setImageBitmap(bm);
            room_bg.setBackgroundResource(R.mipmap.room_bg1);
        }else{
            room_bg.setBackgroundResource(R.mipmap.room_bg2);
        }
    }

    private void findView(View view) {
        room_bg = (FrameLayout) view.findViewById(R.id.room_bg);
        model = (ImageView) view.findViewById(R.id.model);
    }

}
