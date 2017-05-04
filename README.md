# code.parser

### 安装
将附件中的jar包放到eclipse根目录的plugins目录中，重启eclipse。

### 使用
在工程中选中java文件，可以看到右键菜单Generate Java Data，点击菜单执行成功后会在工程根目录下生成"code.parser.result.json"文件。
该文件中保存了java文件的接口信息。

在工程中选中java文件，可以看到右键菜单Generate Java Xls，点击菜单执行成功后默认会在工程根目录下生成"code.parser.result.xls"文件。
该文件中保存了java文件的接口信息。

### 配置
修改jar包中的config.properties文件的javadoc.tags的值可以收集不同tag的信息。多个tag名称以“,”分割，不能有空格。



