# CourseAdapter
适配平台是什么？简单来说，它是一组接口以及封装好的一些组件，使用它可以快速解析各个高校的课程，提供的功能有以下几点：

- 根据学校名称或教务类型获取教务url、课程解析函数（Js）等相关信息
- 获取页面源码
- 通过Js解析出课程集合
- 申请适配（上传源码）

## 注意事项

**欢迎广大开发者参与学校的适配中**

[怪兽课表-简洁的课表](https://www.coolapk.com/apk/com.zhuangfei.hputimetable)

## 目录

- [申请成为适配者](#申请成为适配者)

- [适配流程](#适配流程)
    - [登录](#适配流程)
    - [个人中心](#个人中心)
    - [编码控制台](#编码控制台)

- [开发文档](https://github.com/zfman/CourseAdapter/wiki)
    
- [授权列表](#授权列表)

## 效果展示

可以直接搜索学校获得支持的学校列表，然后进入相应的URL，登录个人教务账号后点击解析按钮，直接可以解析出课程集合，案例参考以下软件

[怪兽课表](https://www.coolapk.com/apk/com.zhuangfei.hputimetable)

该平台也可以和课表控件相结合，具体的看该控件的使用文档

[课表控件](https://github.com/zfman/TimetableView)


## 申请成为适配者

> 随着用户提交的源码增多，以我一人之力肯定不能适配这么多的学校，所以邀请开发者参与适配。适配用到的语言是Js，但是逻辑都很简单，就是正则匹配到结果后返回，不会的话也没问题，我相信你可以通过我的文档以及各种各样的的案例学会它
> 你可以申请参与学校的适配，如果申请后长时间不适配则可能被取消适配资格!

注册功能目前全面开放，在[全国大学生课程适配平台-开发者注册](http://www.liuzhuangfei.com/apis/area/public/register.html)注册即可，然后登录[全国大学生课程适配平台-官网](http://www.liuzhuangfei.com/)适配

## 适配流程

### 1.登录[全国大学生课程适配平台-官网](http://www.liuzhuangfei.com/)

![Alt](resource/img/adapter_img1.png)

### 2.个人中心

登录成功后会跳转到个人中心页面，个人中心页面分为以下几个部分：

- 我适配的学校 ： 用户与学校建立绑定关系后会显示在这个区域，可以暂停发布、解除绑定关系、前往编码控制台
- 无人认领的学校 ： 经管理员筛选后且未与用户建立绑定关系的学校会显示在这个区域，点击去认领即建立绑定关系
- 所有适配列表 ： 所有与用户建立绑定关系的学校会显示在这个区域

![Alt](resource/img/adapter_img2.png)

### 3.编码控制台

左侧编码区，右侧调试区，选择一个测试用例，在【源码参见】处可以看到这个测试用例的具体信息，通过对该用例页面的分析编写解析函数，点击调试程序，会模拟显示出程序的执行结果，多个用例调试均无误后，可以在功能菜单中选择发布程序按钮，此时所有用户都可以搜索到该校并使用该Js对课程解析

![Alt](resource/img/adapter_img3.png)

## 开发文档

[https://github.com/zfman/CourseAdapter/wiki](https://github.com/zfman/CourseAdapter/wiki)

请加QQ群：958342740

## 接入文档

### 搜索页面

搜索页面是课程适配的入口，只要前往搜索页，然后在本页面接收返回的数据即可

```java
    public static final int REQUEST_CODE=1;
```

```java
    Intent intent=new Intent(this, SearchSchoolActivity.class);
    startActivityForResult(intent,REQUEST_CODE);
```

**接收解析的结果**

- `ParseManager`是解析管理类，可以判断是否解析成功以及取出解析结果
- `ParseResult`是本平台提供的课程实体类

接收结果示例如下：

```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==SearchSchoolActivity.RESULT_CODE){
            if(ParseManager.isSuccess()&&ParseManager.getData()!=null){
                List<ParseResult> result=ParseManager.getData();
                String str="";
                for(ParseResult item:result){
                    str+=item.getName()+"\n";
                }
                Toast.makeText(MainActivity.this,str, Toast.LENGTH_SHORT).show();
            }
        }
    }
```

## 申请授权流程

> 如果你想将本功能接入到自己的项目中，需要向开发者提出申请!

以邮件的形式向作者邮箱`119360556@qq.com`提出申请

邮件主题:申请课程适配授权-xxx

邮件内容请注明：

- 项目名称（有链接的话提供链接）
- 申请者姓名
- 选择的授权方式

如果你的申请通过的话，我会将你的项目加入到授权列表中

## 授权列表

> 在此列出的为作者授权使用“全国大学生适配系统”的开发者列表，未列出的名单禁止借助任何手段使用本平台提供的服务

| 项目名称 | 申请者 | 授权日期 | 完成日期 |授权状态
| ------ | ------ | ------ | ------ |------ |
| [怪兽课表](https://www.coolapk.com/apk/com.zhuangfei.hputimetable) | [刘壮飞](https://github.com/zfman) | 2018/10/18 | --- |已授权|

任何问题可以联系开发者邮箱`1193600556@qq.com`
