
    项目地址：https://code.aliyun.com/longrise_mobile/js-sdk.git

>**JS-SDK 框架（JavaScript 部分）可以帮助快速构建如下两种交互场景：**

- 接收来自 Native 的调用 / 并 return 回 Native 调用者
- 调用 Native 方法 / 并接收来自 Native 方法的 return 

---


#### 集成步骤

1. 导入 js 在您的 HTML 文件

    

```
    <!-- 必须导入 -->
    <script type="text/javascript" src="js-sdk/utils/platform.js"></script>
	<script type="text/javascript" src="js-sdk/core/lrBridgeCore.js"></script>
    <!-- 以上两个文件必须导入 -->
    
    <!-- 负责大数据场景下传递 (默认超过 1M)-->
	<script type="text/javascript" src="js-sdk/core/chunkParse.js"></script>
    <!-- 兼容微信 js-sdk -->
    <script type="text/javascript" src="js-sdk/core/wxBridge.js"></script>
	<!-- JavaScript 事件类型，由于客户端对 ES6 的支持有限，不推荐适用 -->
	<script type="text/javascript" src="js-sdk/core/receiver.js"></script>
	
```

2. 初始化通信框架：

```
    // 在使用之前需要先初始化通信框架
    // 初始化之后，便可以正常使用内部功能
    lr.ready()
```

---

### 如何使用


JS-SDK 框架（JavaScript 部分）对于 JavaScript 调用 Native 提供了两种不同的实现思路：

- **直接调用 Native 方法**
- **通过事件调用到 Native**

--- 

#### 调用 Native 方法 / 并接收来自 Native 方法的 return

>**两种方式有各自的适用场景，详情请参照《[JS-SDK 框架（Native 部分）](http://note.youdao.com/noteshare?id=267a30a383d878aa0a6b020ef1219dbe)》**

直接调用 Native 方法（callNative）和 通过事件调用到 Native（callEvent） 同样存在如下 4 种场景：

- **无参无返回值**
- **有参无返回值** （此时通过 callNative 的方式无法获取 Native 的返回值的，callEvent 则允许）
- **无参有返回值**
- **有参有返回值**

##### 1、直接调用 Native 方法


    let params = {
		link: '中国特色社会主义道路',
		age: '人民日益增长的物质文化需求同落后的生产关系之间的矛盾'
	}
	let message = {
	    // 原生方法名称
		methodName: 'fromJavaScriptWithParamsWithReturn',
		params: params,
		success: function(res) {
			alert('来自 Native 的返回：' + JSON.stringify(res))
	    }
    }
    // 通 callNative 调用Native方法
	lr.callNative(message)
	
也可以添加失败类型的回调，**具体取决于您的业务场景。**

    
    let message = {
        methodName: 'fromJavaScriptWithParamsWithReturn',
        params: params,
        success: function(res) {
            alert('来自 Native 的返回：' + JSON.stringify(res))
		},
		failed:function(res){
			alert('来自 Native 的返回：' + JSON.stringify(res))
		}
    }
    
或者不需要 Native 返回信息：

    let message = {
        methodName: 'fromJavaScriptWithParamsWithReturn',
        params:params
    }
    
或者无参类型：

    let message = {
        methodName: 'fromJavaScriptWithParamsWithReturn',
    }

> **注意无参数类型，此时添加 success 或 failed 无意义，因为该场景无法得到 Native 的 return，后面将要介绍的通过事件调用到 Native 则允许。**
    
---

##### 2、通过事件调用到 Native

对于上述 4 种场景，事件类型则都可以满足。关于 Native 自定义事件规则请参照《[JS-SDK 框架（Native 部分）](http://note.youdao.com/noteshare?id=267a30a383d878aa0a6b020ef1219dbe)》


    let message = {
        eventName: 'onVoiceRecordEnd',
        params: params,
        success: function(res) {
            alert('来自 Native 的返回：' + JSON.stringify(res))
        },
        failed: function(res) {
            alert('来自 Native 的返回：' + JSON.stringify(res))
        }
	}
	// 通过 callEvent 调用 Native 中自定义事件类型
	lr.callEvent(message)
	
> 通过事件类型最大的区别是要通过 callEvent 发起调用，并且不会受限于 callNative 当无参数类型时无法获取 Native 的 return 的限制。

此时无参数类型，依然可以接收来自 Native 方法的 return:

    let message = {
        eventName: 'onVoiceRecordEnd',
        success: function(res) {
            alert('来自 Native 的返回：' + JSON.stringify(res))
        },
	}
	// 通过 callEvent 调用 Native 中自定义事件类型
	lr.callEvent(message)
    

---

#### 接收来自 Native 的调用 / 并 return 回 Native 调用者

JS-SDK框架（JavaScript 部分）对于 Native 调用 JavaScript 提供了两种不同的实现思路：

- **自定义 JavaScript 方法**
- **自定义事件（JS-SDK框架（JavaScript 部分）暂不支持）**

>**两种方式的具体使用场景，请参照《[JS-SDK 框架（Native 部分）](http://note.youdao.com/noteshare?id=267a30a383d878aa0a6b020ef1219dbe)》。总体来讲自定义 JavaScript 方法存在与 Native 扩展方法类似问题。解决方案是自定义事件类型**

##### 1、自定义 JavaScript 方法

关于自定义方法，大家都非常熟悉，不过多叙述，看示例：

（1）无参无返回值

    /**
     * 无参无返回值
     */
    function callFromNativeNoParamsNoReturn() {
        alert('来自 Native 的调用');
    }
    
（2）含参无返回值

    /**
     * 无参无返回值
     */
    function callFromNativeWithParamsNoReturn(request) {
        var jsonRequest = JSON.parse(request);
        alert('来自 Native 的参数：' + JSON.stringify(jsonRequest.params));
    }
    
（3）无参有返回值（存在与 Native 类似问题，无法响应回 Native）

    function callFromNativeNoParamsWithReturn() {
        alert('来自 Native 的调用'');
        
        // 通过 notifyNative 无法 return 回 Native 调用者
    }
    
> 与原生方法类似，无参数类型的 JavaScript 无法 return 会 Naitve 调用者，后面介绍的自定义 JavaScript 事件类型则允许。
    
（4）含参含返回值

    function callFromNativeWithParamsWithReturn(request) {
        let jsonRequest = JSON.parse(request);
        alert('来自 Native 的参数：' + JSON.stringify(jsonRequest.params));
    
        // 以下 return 回 Native 调用者
        let params = {
            from: 'JavaScript',
        }
        
        let message = {
            // 注意该 id 的获取
            id: jsonRequest.id,
            result: params
            // 注意这里 result 的标准响应格式
            // 含有状态及说明，否则采用如上
            result: {
                state: 1,
                desc: '不说也可以',
                result: params
            }
        }
        // 通过 notifyNative return 回 Native 调用者
        lr.notifyNative(message)
    }
    
> 注意 result 为标准响应格式，如下：

    上述代码中 result: params 默认会被转化为下方：

    result: {
        state: 1,
        desc: '状态说明，默认无',
        result: params
    }

state 状态值，desc 描述状态，result 具体返回内容。

---

##### 2、自定义事件类型

> 定义 JavaScript 事件类型允许无参场景下 return，相比直接定义 JavaScript 方法更加灵活；每个 JavaScript 事件生命周期将会自动管理，事件将会根据当前页面的生命周期自动销毁。

**定义JavaScript事件类型的接收者**

> 注意 login/share 为该事件的名称，Native 将会根据该事件名称匹配到目标方法。！目前事件类型暂不支持 callback 操作 ！。

    // 无参数
    receiver.share = function() {
			
		// 该return将直接回到Native调用者：Android/iOS	
		
		let message = {
		    name: 'godliness'，
		    age: 100,
		}
		return message;
	}

    // 含有参数
    receiver.login = function(params) {
    
		alert('来自Native的参数: ' + JSON.stringify(params))
			
		// 该return将直接回到Native调用者：Android/iOS	
		
		let message = {
		    name: 'godliness'，
		    age: 100,
		}
		return message;
	}
	
> 注意 return 为标准响应格式，如下：

    上述代码中 return message 会自动被转化为下方：

    return {
        state: 1,
        desc: '状态说明，默认无',
        result: message
    }

state 状态值，desc 描述状态，result 具体返回内容。



	


