
# 使用方法

1. 新建Java Module ,添加 lint-api 依赖

 ```
  dependencies {
      compileOnly 'com.android.tools.lint:lint-api:26.2.0'
      compileOnly 'com.android.tools.lint:lint-checks:26.2.0'
  }
```

2. 实现detector和Registry,参考lintrules代码

3. AS3.0以后不需要通过包装aar的形式引入lint,直接在项目的gradle里增加

 ```
  dependencies {
    lintChecks project(':lintrules')
  }
  ```
  
# 已经实现的Detector

**LayoutNameDetector:** layout命名检测activity_XX  fragment_XX  

**MessageObtainDetector:**  通过handler.obtainMessage or Message.Obtain()获取缓存的message来替代newMessage  

**NewThreadDetector:** 通过线程池来代替newThread()造成的开销  

**SelfLogDetector:** 使用封装的log代替原生log来控制log的输出环境,是否要上传等需求  

**SerializableDetector:** 实现了序列化的外部类,内部类也要实现序列化的提示  

**ViewIdCorrectnessDetector:** xml中view的id命名规范,推荐使用tv_XX, btn_XX, 可自行定义  


