package com.example.buyvegetablestogether.nav;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.buyvegetablestogether.LoginActivity;
import com.example.buyvegetablestogether.R;
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
        LoginActivity.currentUserVerify(requireContext());  // 进行先行登录帐号有效性验证
        if (LoginActivity.currentUserName.equals("")) {  // 验证没通过
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivityForResult(intent,3);
        } else {
            displayMe();
        }
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
            default:
        }

    }
// TODO: DisplayMe
    private void displayMe() {
        Log.d(TAG, "displayMe: 333333333333333333");
    }
}