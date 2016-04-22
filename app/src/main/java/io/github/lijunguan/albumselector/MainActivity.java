package io.github.lijunguan.albumselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.lijunguan.album.ImgSelector;
import io.github.lijunguan.album.utils.KLog;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_IMAGE_REQUEST = 100;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private SelectedImgAdapter mAdapter;
    private List<String> mImagePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(mToolbar);
        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new SelectedImgAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.fab)
    public void testAlbum(View view) {
        ImgSelector.getInstance()
                .setMaxCount(6)
                .setShowCamera(true)
                .setSelectModel(ImgSelector.MULTI_MODEL)
                .startSelect(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImgSelector.REQUEST_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                mImagePaths = data.getStringArrayListExtra(ImgSelector.SELECTED_RESULT);
                if (mImagePaths != null) {
                    mAdapter.notifyDataSetChanged();
                    for (String mImagePath : mImagePaths) {
                        KLog.d(mImagePath);
                    }
                }
            }
        }
    }

    class SelectedImgAdapter extends RecyclerView.Adapter<SelectedImgAdapter.ImgViewHolder> {
        @Override
        public SelectedImgAdapter.ImgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridLayoutManager.LayoutParams(parent.getWidth() / 3, parent.getWidth() / 3));
            return new ImgViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(SelectedImgAdapter.ImgViewHolder holder, int position) {
            Glide.with(MainActivity.this).load(mImagePaths.get(position)).into((ImageView) holder.itemView);
        }

        @Override
        public int getItemCount() {
            return mImagePaths.size();
        }

        class ImgViewHolder extends RecyclerView.ViewHolder {

            public ImgViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

}
