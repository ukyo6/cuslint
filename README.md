# cuslint

# 使用方法


- 新建Java Module ,添加 lint-api 依赖

 ```
  dependencies {
      compileOnly 'com.android.tools.lint:lint-api:26.2.0'
      compileOnly 'com.android.tools.lint:lint-checks:26.2.0'
  }
```

- 实现detector和Registry,参考lintrules代码

- AS3.0以后不需要通过包装aar的形式引入lint,直接在项目的gradle里增加

 ```
  dependencies {
    lintChecks project(':lintrules')
  }
  ```

