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

[Apk_Demp DownLoad](https://raw.githubusercontent.com/lijunguan/AlbumSelector/master/screenshot/sample-imagselector.apk)

<div class='row'>
    <img src='https://raw.githubusercontent.com/lijunguan/AlbumSelector/master/screenshot/ScrennShot1.gif' width="300px" style='border: #f1f1f1 solid 1px'/>
    <img src='https://raw.githubusercontent.com/lijunguan/AlbumSelector/master/screenshot/ScrennShot2.gif' width="300px" style='border: #f1f1f1 solid 1px'/>
</div>

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
