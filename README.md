**暂时没有完善README 先给张图,实在没时间**




![EX](https://i.loli.net/2017/12/24/5a3f92ca222d7.gif)

更新日志
-------

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
