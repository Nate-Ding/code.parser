# code.parser

### 安装
jar包从附件或者http://git.oschina.net/dingnate/code-parser/releases 获取。
将jar包放到eclipse根目录的plugins目录中，重启eclipse。

注：Mini包 请下载V1.0.0版本（ 345 KB），支持Java文件转Json。

### 使用
在工程中选中java文件，可以看到右键菜单Generate Java Json，点击菜单执行成功后默认会在工程根目录下生成"interfaceName.json"文件。
该文件中保存了java文件的接口信息。

在工程中选中java文件，可以看到右键菜单Generate Java Excel，点击菜单执行成功后默认会在工程根目录下生成"code.parser.result.xls"文件。
该文件中保存了java文件的接口信息。

在工程中选中生成的Json文件，可以看到右键菜单Generate Java Code，点击菜单执行成功后默认会在工程src目录的对应的包下生成"interfaceName.java"文件。
该文件中保存了java文件的源码信息。


### 配置
修改jar包中的config.properties文件的javadoc.tags的值可以收集不同tag的信息。多个tag名称以“,”分割，不能有空格。