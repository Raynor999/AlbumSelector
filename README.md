## ImageSelector 

一个采用MVP架构的图片选择器，可以选择头像，多张图片选择，在很多App上都需要使用这样的功能。
良好的设计，使用起来非常简单，可根据自己实际需求进行配置。

我的博客[追求卓越--成功就会在不经意间追上你](https://lijunguan.github.io)

## 特色

    - 根据Google官方的MVP架构最佳实践 设计
    - 采用RecyclerView + Toolbar + FloatActionButton 状态栏颜色等Material Design
    - 可配置，最大选择数量，Grid列数，是否显示相机，Toolbar颜色等
    - 支持Android6.0 运行时权限检查

## ScreenShot

[Apk_Demp DownLoad](https://raw.githubusercontent.com/lijunguan/AlbumSelector/master/screenshot/sample-imageselector.apk)


<img src='https://raw.githubusercontent.com/lijunguan/AlbumSelector/master/screenshot/ScrennShot1.gif' width="300px" style='border: #f1f1f1 solid 1px'/>

### Android6.0 运行时权限

<img src='https://raw.githubusercontent.com/lijunguan/AlbumSelector/master/screenshot/ScrennShot2.gif' width="300px" style='border: #f1f1f1 solid 1px'/>


## Gradle Dependency Or Maven

支持`API >= 11`。

```groovy
    dependencies {
            compile "com.lijunguan:imageseletor:1.0.1"
    }

```

```groovy
    <dependency>
        <groupId>com.lijunguan</groupId>
        <artifactId>imageseletor</artifactId>
        <version>1.0.1</version>
        <type>pom</type>
    </dependency>
```

## 使用

### 使用默认配置

```java
    public void selectButtonClick(){
     ImageSelector.getInstance()
                .startSelect(MainActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.REQUEST_SELECT_IMAGE 
            && resultCode == RESULT_OK) {
                ArrayList<String> imagesPath = data.getStringArrayListExtra(ImageSelector.SELECTED_RESULT);
                if(imagesPath != null){
                    //TODO  do something...
                }
        }
    }
```

### 配置

```java
     ImageSelector.getInstance()
                .setSelectModel(ImageSelector.MULTI_MODE)
                .setMaxCount(6)
                .setGridColumns(3)
                .setShowCamera(true)
                .setToolbarColor(getResources().getColor(R.color.colorPrimary))
                .startSelect(this);
```

#### 配置简介

- 最大可选数量
    默认：9张 通过setMaxCount(int count)配置
- 图片展示列数
    默认：3列 通过setGridColumns(int columns)配置
- 显示相机Item
    默认：true setShowCamera(boolean shown)配置
- 图片选择模式
    默认：多选模式  可选AvatorModel(头像选择模式) 同 SingleModel（单选模式已废弃） 通过setSelectModel(ImageSelector.AVATOR_MODE)配置
- Toolbar和状态栏颜色
    默认： 蓝色#3F51B5  状态栏颜色需API>19 , 4.4 渐变色，5.0以上为纯色填充

## 项目MVP简介 

如果觉得这个项目还可以，请给个start支持一下，谢谢啦，希望和各位同道互相学习交流，下面简单介绍一下项目。 后期计划写一篇博客详细总结一下项目，会给出自己详尽的设计构想和代码组织逻辑和UML图，渴望得到大家的指点，不断改进。

[Google官方MVP架构](https://github.com/googlesamples/android-architecture)官方称该项目为Android架构蓝图，与很多我见过的MVP架构不尽相同，不过看了官方的架构，自我感觉Google的架构方式合理很多，更清晰明了， 用XXXContract来统一管理Presenter和View的所有接口，一个Contract可以对应一个业务逻辑，清晰明了易于维护。

### 项目中相册界面的Contract

```java
public interface AlbumContract {

    interface View extends BaseView<Presenter> {

        void showEmptyView(@Nullable CharSequence message);

        void showImages(@NonNull List<ImageInfo> imageInfos);

        void showSystemCamera();

        void showFolderList();

        void hideFolderList();

        void initFolderList(@NonNull List<AlbumFolder> folders);

        void showImageDetailUi(int currentPosition); // 打开ImagedetailFragment

        void showImageCropUi(@NonNull String imagePath);// 启动裁剪图片的Activity

        void showOutOfRange(int position); //提示用户图片选择数量已经达到上限

        void showSelectedCount(int count);

        /**
         * 图片选择完成，返回选择数据给等待结果的Activity，
         * 根据refreshMedia状态判断是否将相机拍摄或裁剪的图片加入媒体库
         *
         * @param imagePaths   选择的图片路径集合
         * @param refreshMedia 是否刷新系统媒体库 true 将通过相机拍摄的照片加入Media.Store
         */
        void selectComplete(@NonNull List<String> imagePaths, boolean refreshMedia);

        /**
         * 同步  ImageDetailFragment 界面Checkbox选中状态
         * @param position
         */
        void syncCheckboxStatus(int position);

    }

    interface Presenter extends ImageContract.Presenter {
        /**
         * 切换相册目录，刷新Grid显示
         *
         * @param folder 选择的相册目录实体
         */
        void swtichFloder(@NonNull AlbumFolder folder);

        void previewImage(int position);

        void cropImage(ImageInfo imageInfo);

        /**
         * 将用户选择的图片结果返回
         */
        void returnResult();

        void openCamera();

        /**
         * 系统相机Activity 返回结果    * {@link Activity#onActivityResult(int, int, Intent)}.
         * @param mTmpFile 保存相机拍摄图片的零时文件
         */
        void result(int requestCode, int resultCode, Intent data, File mTmpFile);

        void clearCache();
    }

}
```

```java
public interface ImageContract {

    interface View extends BaseView<Presenter> {

        void updateIndicator();

        void showOutOfRange(int position);

        void showSelectedCount(int count);
    }

    interface Presenter extends BasePresenter {

        void selectImage(@NonNull ImageInfo imageInfo, int maxCount, int position);

        void unSelectImage(@NonNull ImageInfo imageInfo, int position);
    }

}
```

由于架构的引入，代码肯定是额外增加的了，不够从上面两个XXXContact类，我们就能看出优势所在，由于界限非常清晰，后期的扩展维护都非常简单，从分体现了单一职责的设计思想。最终要的是可测试性，UI层和业务层可以分别进行单元测试。计划在下一个版本学习官方案例，加入测试案例。

## License

    Copyright 2016 lijunguan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.>
