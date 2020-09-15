package com.example.buyvegetablestogether.recycleview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buyvegetablestogether.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;

import news.jaywei.com.compresslib.CompressTools;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private Context mContext;
    private static final String TAG = "GoodsAdapter";
    private List<Goods> mGoodsList;
    private ArrayList<ImageLoadTask> pendingImageLoading = new ArrayList<ImageLoadTask>();

    private int mImageLoading = 0;



    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewGoods;
        private TextView textViewNameGoods;
        private TextView textViewPrice;
        private TextView textViewNameShop;
        private Goods good;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewGoods = itemView.findViewById(R.id.image_view_goods);
            textViewNameGoods = itemView.findViewById(R.id.text_view_name_goods);
            textViewPrice = itemView.findViewById(R.id.text_view_price);
            textViewNameShop = itemView.findViewById(R.id.text_view_name_shop);
        }
    }

    class ImageLoadTask implements Runnable {
        private Goods goods;
        private ViewHolder holder;

        public ImageLoadTask(Goods good, ViewHolder holder){
            this.goods = good;
            this.holder = holder;
        }

        @Override
        public void run() {
            Bitmap bitmap = goods.getImage(goods.getId());
            ((AppCompatActivity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageLoading--;
                    if (holder.good.getId() == goods.getId()) {
                        holder.imageViewGoods.setImageBitmap(bitmap);
                    }
                    TryRunImageLoadTask();
                }
            });
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
        holder.good = goods;
        holder.textViewNameGoods.setText(goods.getNameGoods());
        holder.textViewNameShop.setText(goods.getNameShop());
        holder.textViewPrice.setText(String.valueOf(goods.getPrice()));
        // 读取读取图片并且显示到holder.imageViewGoods上
        // 如果路径为空, 则显示LOGO
//        ImageView imageView = holder.imageViewGoods;
        if (goods.judgeHasImage()) {
//            imageView.setImageResource(R.drawable.default_avatar);
            PushImageLoadTask(new ImageLoadTask(goods, holder));
        } else {
                Glide.with(mContext).load(R.drawable.default_avatar).into(holder.imageViewGoods);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.imageViewGoods.setImageResource(R.drawable.default_avatar);
        for (ImageLoadTask task : pendingImageLoading) {
            if (task.holder == holder) {
                pendingImageLoading.remove(task);
                break;
            }
        }
    }

    void PushImageLoadTask(ImageLoadTask task) {
        pendingImageLoading.add(task);
        TryRunImageLoadTask();
    }

    void TryRunImageLoadTask() {
        while (pendingImageLoading.size() > 0 && mImageLoading < 4) {
            mImageLoading++;
            new Thread(pendingImageLoading.remove(0)).start();
        }
    }

    @Override
    public int getItemCount() {
        return mGoodsList.size();
    }

}