package io.github.lijunguan.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.lijunguan.imgselector.ImageSelector;
import io.github.lijunguan.imgselector.utils.KLog;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.radio_group_select_model)
    RadioGroup mRadioGroupSelectModel;
    @Bind(R.id.radio_group_toolbar_color)
    RadioGroup mRadioGroupToolbarColor;
    @Bind(R.id.switch_carmera)
    SwitchCompat mSwitchCarmera;
    @Bind(R.id.et_max_count)
    EditText mEtMaxCount;
    @Bind(R.id.et_span_count)
    EditText mEtSpanCount;

    @Bind(R.id.rl_avator)
    View mRlAvator;
    @Bind(R.id.iv_avator)
    ImageView mIvAvator;


    boolean isAvatorModel;

    private SelectedImgAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.d("===========MainActivity:onCreate=============");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new GridDividerDecorator(this));
        mAdapter = new SelectedImgAdapter(Glide.with(this));
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
                isAvatorModel = false;
                break;
            case R.id.radio_avator:
                imageSelector.setSelectModel(ImageSelector.AVATOR_MODE);
                isAvatorModel = true;
                break;
            default:
                imageSelector.setSelectModel(ImageSelector.MULTI_MODE);
        }

        switch (mRadioGroupToolbarColor.getCheckedRadioButtonId()) {
            case R.id.radio_red:
                imageSelector.setToolbarColor(ContextCompat.getColor(this, R.color.red));
                break;
            case R.id.radio_green:
                imageSelector.setToolbarColor(ContextCompat.getColor(this, R.color.green));
                break;
            case R.id.radio_blue:
                imageSelector.setToolbarColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case R.id.radio_orange:
                imageSelector.setToolbarColor(ContextCompat.getColor(this, R.color.orange));
                break;

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

    /**
     * 得到选择的图片路径集合
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.REQUEST_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {

                ArrayList<String> imagesPath = data.getStringArrayListExtra(ImageSelector.SELECTED_RESULT);
                KLog.d("imagesPath ----------------" + imagesPath.get(0));
                if (isAvatorModel && imagesPath != null) {
                    mRlAvator.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(imagesPath.get(0))
                            .asBitmap()
                            .into(new BitmapImageViewTarget(mIvAvator) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    mIvAvator.setImageDrawable(circularBitmapDrawable);
                                }
                            });

                } else if (imagesPath != null) {
                    mRlAvator.setVisibility(View.GONE);
                    mAdapter.refreshData(imagesPath);
                }

            }
        }
    }

    static class SelectedImgAdapter extends RecyclerView.Adapter<SelectedImgAdapter.ImgViewHolder> {

        List<String> mData = new ArrayList<>();

        private RequestManager mRequestManager;

        public SelectedImgAdapter(RequestManager requestManager) {
            this.mRequestManager = requestManager;
        }

        @SuppressWarnings("ReturnOfInnerClass")
        @Override
        public ImgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridLayoutManager.LayoutParams(parent.getWidth() / 3, parent.getWidth() / 3));
            return new ImgViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(ImgViewHolder holder, int position) {
            mRequestManager
                    .load(mData.get(position))
                    .into((ImageView) holder.itemView);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        static class ImgViewHolder extends RecyclerView.ViewHolder {

            public ImgViewHolder(View itemView) {
                super(itemView);
            }
        }

        public void refreshData(List<String> data) {
            mData = data;
            notifyDataSetChanged();
        }
    }

}
