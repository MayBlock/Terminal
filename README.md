# Terminal

## 导入Terminal作为依赖

### Maven
```xml
<!-- 1.添加依赖 -->
<dependency>
    <groupId>cn.newcraft.terminal</groupId>
    <artifactId>Terminal</artifactId>
    <version>1.3.0.201114_release</version>
</dependency>
```
### Grade
```editor-config
<!-- 1.将Maven仓库添加到build.gradle中 -->
allprojects {
	repositories {
		maven { url 'http://repo1.maven.org/maven2' }
	}
}

<!-- 2.添加依赖 -->
dependencies {
	implementation 'cn.newcraft.terminal:Terminal:1.3.0.201114_release'
}
```

## 配置主类
```java
public class ExamplePlugin extends MainPlugin {

    public ExamplePlugin() {
        super("ExamplePlugin"); // PluginName
    }

    @Override
    public void onLoad() {
        // override onLoad()
    }

    @Override
    public void onEnable() {
        // override onEnable()
    }

    @Override
    public void onDisable() {
        // override onDisable()
    }
}
```

## 添加引导
在项目根目录创建名为“plugin.yml”的文件并如下进行配置

```yaml
name: ExamplePlugin #项目名（必填）
main: com.google.exampleplugin.Main #项目主类路径（必填）
version: 1.0.0 #项目版本（必填）
author: YourName #项目作者（选填）
prefix: ExamplePlugin #项目前缀（选填）
api-version: 4 #支持Terminal的API版本（选填）
```
