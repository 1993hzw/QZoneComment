[《Android仿手机QQ空间动态评论,自动定位到输入框》](http://blog.csdn.net/u012964944/article/details/51153686)

手机QQ空间浏览好友动态时，可以直接对动态评论，点击某条评论，动态列表自动滚动，使输入框刚好在该评论下面，而不会覆盖住评论内容。如下图所示，

![img](https://raw.githubusercontent.com/1993hzw/common/master/QZoneComment/01.png)

首先要实现输入框刚好在输入面板上面，且动态列表不会被挤上去。可以使用对话框的形式，这样输入框不会影响原有的布局，弹出的对话框布局如下所示，点击EditText时，红色块的内容将位于输入法上面。在这里我把ScrollerView的背景设为透明。其实QQ空间的输入框也是以对话框的形式弹出，因为弹出对话框时，原本全屏的布局突然多了一条状态栏。

接着就是要让点击的评论刚好在评论输入框上面，可以使用ListView.smoothScrollBy(distance, duration)让列表滚动到相应位置，但是难点是如何计算出滚动的距离distance。
如下图所示，我们主要计算弹出输入面板后的输入框和评论之间的距离（绿色线条的长度）。通过View.getLocationOnScreen()方法可以计算出view在屏幕上的坐标（x,y）,那么列表滑动的距离distance = listview的y坐标 - 输入框的y坐标。

![img](https://raw.githubusercontent.com/1993hzw/common/master/QZoneComment/02.png)

[CommonFun](https://github.com/1993hzw/QZoneComment/blob/master/app/src/com/example/QzoneComment/CommentFun.java)

最后要弄的就是评论中，评论者、接收者和评论内容用不同的颜色显示，且点击时有点击效果。这里可以通过下面的代码实现，
```java
TextView.setText(Html.fromHtml(content, imageGettor, tagHandler))
```
自定义标签，通过自定义的标签解析类Html.TagHandler来响应不同标签的操作。这里我自定义了commentator、receiver、content标签，列入一条评论的字符串形式为“<commentator>用户１</commentator> 回复 <receiver>用户２</receiver>:<content>评论内容</content>”，点击content标签时对该评论的评论者进行回复。
[CustomTagHandler](https://github.com/1993hzw/QZoneComment/blob/master/app/src/com/example/QzoneComment/CustomTagHandler.java)

在ListView中，因为Item里面的子View使用了ClickableSpan,导致ListView的OnItemClickListener失效，解决的方法可以在getView中加入下列代码，阻止ListView里面的子View拦截焦点。
```java
public View getView(int position, View convertView, ViewGroup parent) {  
    if (convertView != null) {  
            //防止ListView的OnItemClick与item里面子view的点击发生冲突  
    ((ViewGroup) convertView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);  
    }  
}
```

设置点击的文字背景色：

```xml
<TextView xmlns:android="http://schemas.android.com/apk/res/android"  
          ...  
          android:textColorLink="@color/selector_comment_name"  
          android:textColorHighlight="#44000000"  
        />  
<!-- 上面两个属性(textColorLink、textColorHighlight)要同时设置，而且textColorLink必须设置为ColorList！！！！！！！！！ 
-->  
```

难点都已经解决了，最后实现的效果如下：

![img](https://raw.githubusercontent.com/1993hzw/common/master/QZoneComment/03.gif)

