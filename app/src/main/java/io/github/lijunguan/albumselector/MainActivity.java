package io.github.lijunguan.albumselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.lijunguan.imgselector.ImageSelector;
import io.github.lijunguan.imgselector.utils.KLog;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_IMAGE_REQUEST = 100;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.radio_group_select_model)
    RadioGroup mRadioGroupSelectModel;
    @Bind(R.id.switch_carmera)
    SwitchCompat mSwitchCarmera;
    @Bind(R.id.et_max_count)
    EditText mEtMaxCount;
    @Bind(R.id.et_span_count)
    EditText mEtSpanCount;


    private SelectedImgAdapter mAdapter;

    private List<String> mImagePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new SelectedImgAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.fab)
    public void testImageSelector(View view) {
        ImageSelector imageSelector = ImageSelector.getInstance();
        loadConfig(imageSelector).startSelect(this);
    }


    private ImageSelector loadConfig(ImageSelector imageSelector) {
        switch (mRadioGroupSelectModel.getCheckedRadioButtonId()) {
            case R.id.radio_multi:
                imageSelector.setSelectModel(ImageSelector.MULTI_MODE);
            case R.id.radio_avator:
                imageSelector.setSelectModel(ImageSelector.AVATOR_MODE);
        }

        imageSelector.setShowCamera(mSwitchCarmera.isChecked());

        String maxCountStr = mEtMaxCount.getText().toString();
        String spanCount = mEtSpanCount.getText().toString();
        if (!TextUtils.isEmpty(maxCountStr)) {
            imageSelector.setMaxCount(Integer.parseInt(maxCountStr));
        }
        if (!TextUtils.isEmpty(spanCount)) {
            imageSelector.setGridColumns(Integer.parseInt(spanCount));
        }
        return imageSelector;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.REQUEST_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                mImagePaths = data.getStringArrayListExtra(ImageSelector.SELECTED_RESULT);
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
        public ImgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridLayoutManager.LayoutParams(parent.getWidth() / 3, parent.getWidth() / 3));
            return new ImgViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(ImgViewHolder holder, int position) {
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
