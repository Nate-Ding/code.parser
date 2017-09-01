# code.parser

### 安装
jar包从附件或者http://git.oschina.net/dingnate/code-parser/releases 获取。
将jar包放到eclipse根目录的plugins目录中，重启eclipse。

注：Mini包 请下载code.parser.code_xx.jar和code.parser.ui_xx.jar，支持Java接口文件和Json的转换。

### 使用
在工程中选中java文件，可以看到右键菜单Generate Java Json，点击菜单执行成功后默认会在工程根目录下生成"interfaceName.json"文件。
该文件中保存了java文件的接口信息。

在工程中选中java文件，可以看到右键菜单Generate Java Excel，点击菜单执行成功后默认会在工程根目录下生成"code.parser.result.xls"文件。
该文件中保存了java文件的接口信息。

在工程中选中生成的Json文件，可以看到右键菜单Generate Java Code，点击菜单执行成功后默认会在工程src目录的对应的包下生成"interfaceName.java"文件。
该文件中保存了java文件的源码信息。


### 配置
修改code.parser.core_xx.jar包中的config.properties文件的javadoc.tags的值可以收集不同tag的信息。多个tag名称以“,”分割，不能有空格。


### 用途
这个项目是Eclipse Plug-in 插件。不了解的话可以百度一下或查看 http://www.eclipse.org/ 
    
 **使用方法：** 
直接把Jar包放到Eclipse的根目录下的plugins目录中，重启Eclipse即可使用。 

 **使用场景：**  主要是使用Eclipse进行Java后端服务开发时  
1.给前端提供服务接口的JSON格式数据（包含接口声明信息，出入参报文等 javadoc标签自定义）。该工具可以把Java接口一键转成JSON数据，直接提供给前端开发。 

2.给二次开发的人员提供接口说明的Excel文档。该工具可以把Java接口一键转成Excel文档。

3.在使用RPC或微服务框架时，可以更具导出的接口JSON数据，批量更新注册接口服务。 

4.在使用RPC或微服务框架时，可以根据注册的信息生成的JSON数据反向生成接口代码，节省编码时间。 


### 解决的问题
1.Generate java json write Failed.  
java.lang.NullPointerException  
org.apache.velocity.exception.VelocityException: The specified class for 
ResourceManager
(org.apache.velocity.runtime.resource.ResourceManagerImpl) 
does not implement org.apache.velocity.runtime.resource.ResourceManager; 
Velocity is not initialized correctly.
