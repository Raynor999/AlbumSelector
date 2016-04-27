package io.github.lijunguan.imgselector.album.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.github.lijunguan.imgselector.R;
import io.github.lijunguan.imgselector.model.entity.AlbumFolder;
import io.github.lijunguan.imgselector.album.AlbumFragment;

import static io.github.lijunguan.imgselector.utils.CommonUtils.checkNotNull;

/**
 * Created by lijunguan on 2016/4/13
 * email: lijunguan199210@gmail.com
 * blog : https://lijunguan.github.io
 */
public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderViewHolder> {

    private List<AlbumFolder> mData;

    private int mSelectedIndex = 0;

    private Context mContext;

    private AlbumFragment.FolderItemListener mListener;

    public FolderListAdapter(Context context, AlbumFragment.FolderItemListener listener) {
        mContext = checkNotNull(context);
        mListener = listener;
        mData = new ArrayList<>();
    }


    @Override
    public FolderListAdapter.FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_folder_list, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FolderListAdapter.FolderViewHolder holder, int position) {

        final AlbumFolder floder = mData.get(position);

        Glide.with(mContext)
                .load(floder.getCover().getPath())
                .into(holder.mCoverView);
        holder.mFolderName.setText(floder.getFloderName());
        holder.mFolderSize.setText(mContext.getResources()
                .getString(R.string.folder_size, floder.getImgInfos().size()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFloderItemClick(floder);
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.primary_light));
                mSelectedIndex = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });

        if (mSelectedIndex == position) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.primary_light));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.background));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<AlbumFolder> data) {
        mData = checkNotNull(data);
        notifyDataSetChanged();
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
