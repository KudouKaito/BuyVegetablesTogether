package com.example.buyvegetablestogether.nav;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.buyvegetablestogether.AllGoodsActivity;
import com.example.buyvegetablestogether.LoginActivity;
import com.example.buyvegetablestogether.MainActivity;
import com.example.buyvegetablestogether.R;
import com.example.buyvegetablestogether.db.GoodsDatabaseHelper;
import com.example.buyvegetablestogether.recycleview.Goods;
import com.example.buyvegetablestogether.recycleview.GoodsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static news.jaywei.com.compresslib.FileUtil.runOnUiThread;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartPage extends Fragment {
    private List<Goods> cartList = new ArrayList<Goods>();
    private static final String TAG = "CartPage";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment CartPage.
     */
    // TODO: Rename and change types and number of parameters
//    public static CartPage newInstance(String param1, String param2) {
    public static CartPage newInstance() {
        CartPage fragment = new CartPage();
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
        return inflater.inflate(R.layout.fragment_cart_page, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                LoginActivity.currentUserVerify(requireContext());
                if (LoginActivity.currentUserName.equals("")) {  // 验证没通过
                    BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav);
                    MenuItem menuItemHome = bottomNavigationView.getMenu().findItem(R.id.home);
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    NavigationUI.onNavDestinationSelected(menuItemHome, navController);
                } else {
                    displayCart();
                }
                break;
            default:
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginActivity.currentUserVerify(requireContext());  // 进行先行登录帐号有效性验证
        if (LoginActivity.currentUserName.equals("")) {  // 验证没通过
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.putExtra("intent_source",2);
            startActivityForResult(intent,2);
        } else {
            displayCart();
        }

    }

    // TODO: DisplayCart
    private void displayCart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cartList.clear();
                // 向列表中添加数据
                GoodsDatabaseHelper dbHelperGoods = new GoodsDatabaseHelper(requireContext(), "GoodsDatabase.db", null, 1);
                SQLiteDatabase dbGoods = dbHelperGoods.getWritableDatabase();

                Cursor cursor = dbGoods.query("goods", new String[]{"id","goods_name", "price", "shop_name", "has_image"}, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        cartList.add(new Goods(
                                requireContext(),
                                cursor.getInt(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("goods_name")),
                                cursor.getString(cursor.getColumnIndex("shop_name")),
                                1 == cursor.getInt(cursor.getColumnIndex("has_image")),
                                cursor.getDouble(cursor.getColumnIndex("price"))
                        ));

                    } while (cursor.moveToNext());
                }
                cursor.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 摆放到recyclerView上
                        RecyclerView recyclerView = requireActivity().findViewById(R.id.recyclerview_cart);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
                        recyclerView.setLayoutManager(layoutManager);
                        GoodsAdapter adapter = new GoodsAdapter(cartList);
                        recyclerView.setAdapter(adapter);

                    }
                });
            }
        }).start();
    }
}