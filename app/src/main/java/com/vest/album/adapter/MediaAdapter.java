package com.vest.album.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vest.album.MediaModel;
import com.vest.album.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/8/9.
 */
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.mediaHolder> {

    @Override
    public int getItemCount() {
        return mGalleryModelList.size();
    }

    @Override
    public MediaAdapter.mediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new mediaHolder(view);
    }

    @Override
    public void onBindViewHolder(final MediaAdapter.mediaHolder holder, int position) {
        if (Type == AUDIO) {
            holder.imageView.setImageResource(R.mipmap.ic_item_audio);
        } else {
            showImage(holder.imageView, position, Type);
        }
        holder.nameTxt.setText(mGalleryModelList.get(position).name);
        notifyView(holder.checkedView, mGalleryModelList.get(position).status);
    }

    private List<MediaModel> mGalleryModelList = new ArrayList<MediaModel>();
    private int mWidth;
    private static final int AUDIO = 0, VIDEO = 1, IMAGE = 2;
    private int Type;

    public MediaAdapter(Context context, List<MediaModel> categories, int type) {
        mGalleryModelList = categories;
        this.Type = type;
        mWidth = context.getResources().getDisplayMetrics().widthPixels / 2;
    }

    public void setData(List<MediaModel> categories) {
        mGalleryModelList.clear();
        mGalleryModelList.addAll(categories);
        notifyDataSetChanged();
    }


    public void addLatestEntry(MediaModel mediaModel) {
        if (mediaModel != null) {
            if (!mGalleryModelList.contains(mediaModel)) {
                mGalleryModelList.add(0, mediaModel);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void showImage(final ImageView img, final int position, final int type) {
        final String loadUrl = "file://" + Uri.decode(mGalleryModelList.get(position).url);
        img.setTag(loadUrl);
        ImageSize imageSize = new ImageSize(mWidth, mWidth);
        ImageLoader.getInstance().loadImage(loadUrl, imageSize, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                img.setImageResource(R.drawable.ic_loading);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (type == VIDEO) {
                    img.setImageResource(R.mipmap.ic_item_video);
                } else {
                    img.setImageResource(R.mipmap.ic_empty);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage == null) {
                    if (type == VIDEO) {
                        img.setImageResource(R.mipmap.ic_item_video);
                    } else {
                        img.setImageResource(R.mipmap.ic_empty);
                    }
                    return;
                }
                if (img == null)
                    return;
                String uri = (String) img.getTag();
                if (uri.equals(imageUri)) {
                    img.setImageBitmap(loadedImage);
                    img.setTag("");
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (type == VIDEO) {
                    img.setImageResource(R.mipmap.ic_item_video);
                } else {
                    img.setImageResource(R.mipmap.ic_empty);
                }
            }
        });
    }

    private void notifyView(FrameLayout layout, boolean isShow) {
        if (isShow) {
            layout.bringToFront();
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    class mediaHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        FrameLayout checkedView;
        View bg;
        TextView nameTxt;

        public mediaHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_media_image);
            checkedView = (FrameLayout) itemView.findViewById(R.id.item_media_check_bg);
            bg = itemView.findViewById(R.id.item_media_bg);
            nameTxt = (TextView) itemView.findViewById(R.id.item_media_name_txt);
            bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaModel galleryModel = mGalleryModelList.get(getAdapterPosition());
                    galleryModel.status = !galleryModel.status;
                    notifyView(checkedView, galleryModel.status);
                    mGalleryModelList.set(getAdapterPosition(), galleryModel);
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(galleryModel);
                    }
                }
            });
            bg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MediaModel galleryModel = mGalleryModelList.get(getAdapterPosition());
                    if (onItemClickListener != null) {
                        onItemClickListener.onLongClick(galleryModel);
                    }
                    return false;
                }
            });
        }
    }

    public interface onItemClickListener {
        void onClick(MediaModel model);

        void onLongClick(MediaModel model);
    }

    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
