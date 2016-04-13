package io.github.lijunguan.album.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.github.lijunguan.album.R;
import io.github.lijunguan.album.entity.AlbumFloder;
import io.github.lijunguan.album.view.AlbumView;

/**
 * Created by lijunguan on 2016/4/13
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderViewHolder> {

    private List<AlbumFloder> mData;

    private AlbumView mAlbumView;

    private int mSelectedIndex = 0;

    private Context mContext;

    public FolderListAdapter(AlbumView albumView, List<AlbumFloder> mData) {
        this.mData = mData;
        this.mAlbumView = albumView;
        if (albumView instanceof Context) {
            mContext = (Context) mAlbumView;
        }

    }

    @Override
    public FolderListAdapter.FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_folder_list, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FolderListAdapter.FolderViewHolder holder, final int position) {

        final AlbumFloder floder = mData.get(position);

        Glide.with(mContext)
                .load(floder.getCover().getPath())
                .into(holder.mCoverView);
        holder.mFolderName.setText(floder.getFloderName());
        holder.mFolderSize.setText(mContext.getResources()
                .getString(R.string.folder_size, floder.getImgInfos().size()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlbumView.switchAlbumFolder(floder);
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.primary_light));
                mSelectedIndex = position;
                notifyDataSetChanged();
            }
        });

        if (mSelectedIndex == position) {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.primary_light));
        } else {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.material_grey_50));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView mCoverView;
        TextView mFolderName;
        TextView mFolderSize;

        public FolderViewHolder(View itemView) {
            super(itemView);
            mCoverView = (ImageView) itemView.findViewById(R.id.iv_cover);
            mFolderName = (TextView) itemView.findViewById(R.id.tv_floder_name);
            mFolderSize = (TextView) itemView.findViewById(R.id.tv_folder_size);
        }
    }

}
