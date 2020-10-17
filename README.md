# Terminal

## 导入Terminal作为依赖
```maven
1.将存储库添加到你的Maven构建中
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
</repositories>

2.添加依赖
<dependency>
	    <groupId>com.github.MayBlock</groupId>
	    <artifactId>Terminal</artifactId>
	    <version>Tag</version>
</dependency>
```
```grade
1.将其添加到存储库末尾的build.gradle中
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}

2.添加依赖
dependencies {
	        implementation 'com.github.MayBlock:Terminal:Tag'
}
```

### 配置主类
```main
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
