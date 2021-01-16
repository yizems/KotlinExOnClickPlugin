

# 该库过时

如果是旧项目,并且使用 `kotlin-android-extension` ,可以继续使用,如果 是新项目,建议使用`ViewBinding` 替代 `kotlin-android-extension`;


使用说明
--------

- 在BaseActivity/BaseFragment中定义好方法

```kotlin
//实现View.OnClickListener接口
abstract class BaseActivity : PermissionActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
		setContentView(R.layout.xxx)
		
        initViewClickListeners()
    }
    //改方法一定要确保被调用,否则无法生效,可以看后面生成的代码,其实特别容易理解,当然这个方法也可以不写在base里, 其实该插件只是帮你生成了代码而已,并没有做任何其他的事情
    open fun initViewClickListeners() {}

}


```

在fragment中 一定要在onStart()方法之后调用initViewClickListeners,不了解的可以百度一下




![EX](https://i.loli.net/2017/12/24/5a3f92ca222d7.gif)

更新日志
-------
- 1.3 支持 include 的布局自动导入

- 1.2 加入自动导包功能

- 1.1 更新代码示例

```kotlin

abstract class BaseActivity : PermissionActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        initViewClickListeners()
    }
    
    open fun initViewClickListeners() {}

}

```

生成的代码

```kotlin
    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            AAA -> {
            }
        }
    }

    override fun initViewClickListeners() {
        AAA.setOnClickListener(this)
    }

```




参考
----

- https://github.com/dongjunkun/KotterKnife-Plugin



License
-------
    Copyright 2017 Yi Zeliang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
