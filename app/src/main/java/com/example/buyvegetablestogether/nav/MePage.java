package com.example.buyvegetablestogether.nav;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.buyvegetablestogether.LoginActivity;
import com.example.buyvegetablestogether.R;
import com.example.buyvegetablestogether.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MePage extends Fragment {
    private static final String TAG = "MePage";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int REQUEST_CODE_ME_TO_SETTINGS =6;

//     TOD: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public MePage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment MePage.
     */
    // TOD: Rename and change types and number of parameters
//    public static MePage newInstance(String param1, String param2) {
    public static MePage newInstance() {
        MePage fragment = new MePage();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me_page, container, false);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 3:
                LoginActivity.currentUserVerify(requireContext());
                if (LoginActivity.currentUserName.equals("")) {  // 验证没通过
                    BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav);
                    MenuItem menuItemHome = bottomNavigationView.getMenu().findItem(R.id.home);
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    NavigationUI.onNavDestinationSelected(menuItemHome, navController);
                } else {
                    displayMe();
                }
                break;
            case REQUEST_CODE_ME_TO_SETTINGS:
                if (Activity.RESULT_OK==resultCode) {
                    initMe();
                }
                break;
            default:
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMe();
    }

    private void initMe() {
        LoginActivity.currentUserVerify(requireContext());  // 进行先行登录帐号有效性验证
        if (LoginActivity.currentUserName.equals("")) {  // 验证没通过
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivityForResult(intent,3);
        } else {
            displayMe();
        }

    }

    // TODO: DisplayMe
    private void displayMe() {

        // 设置按钮
        Button buttonSettings = requireActivity().findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  设置界面
                Intent intent = new Intent(requireContext(),SettingsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ME_TO_SETTINGS);
            }
        });
        // 查看订单按钮
        Button buttonDeal = requireActivity().findViewById(R.id.button_deal);
        buttonDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 打开订单Activity
            }
        });
        // 显示用户名
        TextView textViewCurrentUserName = requireActivity().findViewById(R.id.text_view_current_user_name);
        textViewCurrentUserName.setText(LoginActivity.currentUserName);
    }
}