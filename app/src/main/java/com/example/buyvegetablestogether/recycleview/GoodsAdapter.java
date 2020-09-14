package com.example.buyvegetablestogether.recycleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buyvegetablestogether.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import news.jaywei.com.compresslib.CompressTools;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private Context mContext;
    private static final String TAG = "GoodsAdapter";
    private List<Goods> mGoodsList;


    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewGoods;
        private TextView textViewNameGoods;
        private TextView textViewPrice;
        private TextView textViewNameShop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewGoods = itemView.findViewById(R.id.image_view_goods);
            textViewNameGoods = itemView.findViewById(R.id.text_view_name_goods);
            textViewPrice = itemView.findViewById(R.id.text_view_price);
            textViewNameShop = itemView.findViewById(R.id.text_view_name_shop);
        }
    }

    public GoodsAdapter(List<Goods> goodsList) {
        mGoodsList = goodsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (null == mContext) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.goods_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goods goods = mGoodsList.get(position);
        holder.textViewNameGoods.setText(goods.getNameGoods());
        holder.textViewNameShop.setText(goods.getNameShop());
        holder.textViewPrice.setText(String.valueOf(goods.getPrice()));

        // TODO: 读取读取图片并且显示到holder.imageViewGoods上
        // 如果路径为空, 则显示LOGO
        if (goods.judgeHasImage()) {
            Bitmap bitmap = goods.getImage();
            holder.imageViewGoods.setImageBitmap(bitmap);
        } else {
            Glide.with(mContext).load(R.mipmap.ic_launcher).into(holder.imageViewGoods);
        }
    }

    @Override
    public int getItemCount() {
        return mGoodsList.size();
    }

}