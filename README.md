# personal-blog
- This is my personal blog website source code

## 本地测试环境部署指南
- mysql数据库配置文件在jdbc.properties
- redis端口配置在server.properties
- mysql中的数据需要与redis数据保持一致，如果不一致，运行ConvertDB2Redis.java
- 启动前，开启mysql和redis
- 如果需要使用搜索功能，注意更改lucene的索引目录，相关配置在文件server.properties

## 使用war包替换线上版本注意
- 需要把就版本的图片等静态资源备份，之后替换。静态资源位于
```
项目目录/umeditor/jsp/upload
```
- 将此目录备份，待新版本war包解压后替换即可

## 关于编码问题
- 如果出现中文编码问题，可以看看BlogController.java中的393行附近