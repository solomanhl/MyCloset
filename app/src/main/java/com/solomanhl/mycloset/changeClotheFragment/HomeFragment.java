package com.solomanhl.mycloset.changeClotheFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.solomanhl.file.FileUtils;
import com.solomanhl.mycloset.App;
import com.solomanhl.mycloset.R;
import com.solomanhl.mycloset.clothes.ClothesFragment;
import com.solomanhl.mycloset.fittingRoom.FittingRoomFragment;

import java.io.IOException;

//import android.app.Fragment;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link HomeFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link HomeFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    private App app;
    private ModelFragment modelFragment;
    private FittingRoomFragment roomFragment;
    private ClothesFragment clothesFragment;
    private ImageView model, room, closet;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        findView(view);
        setOnclickListener();
        findFragment();
        createFolders();

        return view;
    }

    private void createFolders() {

        try {
            FileUtils f = new FileUtils();
            f.createSDDir("BeautifulCloset/model");
            f.createSDFile("BeautifulCloset/model/.nomedia");
            f.createSDDir("BeautifulCloset/shangyi");
            f.createSDFile("BeautifulCloset/shangyi/.nomedia");
            f.createSDDir("BeautifulCloset/qunzi");
            f.createSDFile("BeautifulCloset/qunzi/.nomedia");

//            f.createSDDir("BeautifulCloset/waitao");
//            f.createSDFile("BeautifulCloset/waitao/.nomedia");
//            f.createSDDir("BeautifulCloset/lianyiqun");
//            f.createSDFile("BeautifulCloset/lianyiqun/.nomedia");
//            f.createSDDir("BeautifulCloset/banshenqun");
//            f.createSDFile("BeautifulCloset/banshenqun/.nomedia");
            f.createSDDir("BeautifulCloset/kuzi");
            f.createSDFile("BeautifulCloset/kuzi/.nomedia");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void findFragment() {
        modelFragment = new ModelFragment();
        roomFragment = new FittingRoomFragment();
        clothesFragment = new ClothesFragment();
    }

    private void setOnclickListener() {
        model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("info","model Onclicked");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, modelFragment).addToBackStack("model").commit();
//                getFragmentManager().beginTransaction().replace(R.id.container, modelFragment).addToBackStack( "model").commit();
            }
        });
        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("info","room Onclicked");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, roomFragment).addToBackStack("room").commit();
//                getFragmentManager().beginTransaction().replace(R.id.container, modelFragment).addToBackStack( "model").commit();
            }
        });
        closet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("info","closet Onclicked");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, clothesFragment).addToBackStack("clothe").commit();
//                getFragmentManager().beginTransaction().replace(R.id.container, modelFragment).addToBackStack( "model").commit();
            }
        });
    }

    private void findView(View view) {
        model = (ImageView) view.findViewById(R.id.model);
        room = (ImageView) view.findViewById(R.id.room);
        closet = (ImageView) view.findViewById(R.id.closet);
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
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
