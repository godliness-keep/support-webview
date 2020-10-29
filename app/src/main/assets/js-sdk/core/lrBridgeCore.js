var lr = (function() {

	const JAVASCRIPT_CALL_FINISHED = 'onJavaScriptCallFinished'
	const CALL_NATIVE_FROM_JAVASCRIPT = 'callNativeFromJavaScript'

	const RESULT_OK = 1

	const jsVersion = 1
	const jsCallbacks = {}
	var jsCallbackCurrentId = -1

	const loops = {};
	const Type = {
		ONCE: 0,
		ON: 1,
		OFF: 2
	}

	function sendNativeInternal(mapObject, message) {
		var hasParams = (message.params != null ||
			message.eventName != null ||
			message.success != null ||
			message.failed != null)

		var request

		if (hasParams) {
			request = packageRequest(message)
		}

		if (isAndroid()) {
			sendToAndroid(mapObject, message.methodName, request)
		} else if (isIos()) {
			sendToiOS(message.methodName, request)
		} else {
			alert('Unknown platform');
		}
	}

	function sendToAndroid(mapObject, methodName, request) {
		if (request != null) {
			mapObject[methodName](JSON.stringify(request))
		} else {
			mapObject[methodName]()
		}
	}

	function sendToiOS(methodName, request) {
		if (request != null) {
			webkit.messageHandlers[methodName].postMessage(JSON.stringify(request));
		} else {
			webkit.messageHandlers[methodName].postMessage(null);
		}
	}

	function callNativeInternal(mapObject, message) {
		message.loop = Type.ONCE;
		sendNativeInternal(mapObject, message);
	}

	function callEventInternal(mapObject, message) {
		message.methodName = CALL_NATIVE_FROM_JAVASCRIPT
		message.loop = Type.ONCE;
		sendNativeInternal(mapObject, message)
	}

	function notifyNativeInternal(mapObject, message) {
		var response = {
			version: jsVersion,
			id: message.id,
		}

		if (typeof message.result === 'object' && 'result' in message.result) {
			response.result = message.result
		} else {
			response.result = packageDefaultResult(message.result)
		}

		if (isAndroid()) {
			sendToAndroid(mapObject, JAVASCRIPT_CALL_FINISHED, response)
		} else if (isIos()) {
			sendToiOS(JAVASCRIPT_CALL_FINISHED, response)
		} else {
			alert('Unknown platform');
		}
	}

	function onNativeCallFinished(response) {
		if (chunkParse.onChunk(response)) {
			return;
		}
		onNativeCallInternalFinished(response);
	}

	function onNativeCallInternalFinished(response) {
		var responseJson
		if (typeof response === 'object' && response) {
			responseJson = response;
		} else {
			responseJson = JSON.parse(response);
		}
		var protocolId = responseJson.id;
		var protocolVersion = responseJson.version;

		var callbacks = jsCallbacks[protocolId];
		if (!callbacks || typeof callbacks !== 'object') {
			return;
		}
		var result = responseJson.result;

		try {
			if (result.state == RESULT_OK) {
				onSucceedFromNative(callbacks.successCallback, result);
			} else {
				onFailedFromNative(callbacks.failedCallback, result);
			}
			onCompleteFromNative(callbacks.completeCallback);
		} finally {
			var chain = callbacks.loop;
			if (chain.loop) {
				return
			}
			// 如果非持续性监听事件则直接移除
			delete jsCallbacks[protocolId];
			if (typeof chain.id === 'number' && chain.id >= 0) {
				// 此时为持续监听事件的取消
				delete jsCallbacks[chain.id]
			}
		}
	}

	function onSucceedFromNative(sck, result) {
		if (sck && typeof sck === 'function') {
			if (sck.length > 0) {
				sck(result);
			} else {
				sck();
			}
		}
	}

	function onFailedFromNative(fck, result) {
		if (fck && typeof fck === 'function') {
			if (fck.length > 0) {
				fck(result);
			} else {
				fck();
			}
		}
	}

	function onCompleteFromNative(cck) {
		if (cck && typeof cck === 'function') {
			cck()
		}
	}

	function packageRequest(message) {
		var request = {
			version: jsVersion,
			eventName: message.eventName,
			params: message.params
		}
		if (typeof message.success === 'function' ||
			typeof message.failed === 'function' ||
			typeof message.complete === 'function') {

			request.id = ++jsCallbackCurrentId
			var chain = {
				successCallback: message.success,
				failedCallback: message.failed,
				completeCallback: message.complete
			}
			jsCallbacks[request.id] = chain;

			if (message.loop === Type.ONCE) {
				chain.loop = {
					loop: false,
					id: -1
				}
			} else if (message.loop === Type.ON) {
				chain.loop = {
					loop: true
				}
				var name = message.methodName === CALL_NATIVE_FROM_JAVASCRIPT ? message.eventName : message.methodName;
				if (message.methodName === CALL_NATIVE_FROM_JAVASCRIPT) {
					message.eventName = 'on' + convertInitialsToUpperCase(name);
				} else {
					message.methodName = 'on' + convertInitialsToUpperCase(name);
				}
				loops[name] = request.id;
			} else if (message.loop === Type.OFF) {
				var name = message.methodName === CALL_NATIVE_FROM_JAVASCRIPT ? message.eventName : message.methodName;
				if (message.methodName == CALL_NATIVE_FROM_JAVASCRIPT) {
					message.eventName = 'off' + convertInitialsToUpperCase(name);
				} else {
					message.methodName = 'off' + convertInitialsToUpperCase(name);
				}
				chain.loop = {
					loop: false,
					id: loops[name]
				}
				// 删除这个链条
				delete loops[name]
			}
		}
		return request
	}

	function addMethod(object, name, fn) {
		var old = object[name];
		object[name] = function() {
			if (fn.length === arguments.length) {
				return fn.apply(this, arguments);
			} else if (typeof old === 'function') {
				return old.apply(this, arguments);
			}
		}
	}

	function packageDefaultResult(result) {
		return {
			state: result.state == null ? 0 : result.state,
			desc: result.desc == null ? '' : result.desc,
			result: result
		}
	}

	function sendLoopMessage(mapObject, message, to) {
		message.loop = Type.ON;
		to(mapObject, message);
	}

	function sendUnLoopMessage(mapObject, message, to) {
		message.loop = Type.OFF;
		to(mapObject, message);
	}

	function convertInitialsToUpperCase(str) {
		var firstChar = str.charAt(0);
		var regexp = firstChar;
		return str.replace(regexp, firstChar.toUpperCase());
	}

	function onNativeInternal(mapObject, message) {
		if (loops[message.methodName] >= 0) {
			// 首先判断是否已经存在该持续监听的方法
			throw 'There is already listening for this event : ' + message.methodName;
		}
		sendLoopMessage(mapObject, message, sendNativeInternal);
	}

	function offNativeInternal(mapObject, message) {
		sendUnLoopMessage(mapObject, message, sendNativeInternal);
	}

	function onEventInternal(mapObject, message) {
		if (loops[message.eventName] >= 0) {
			// 首先判断是否已经存在该持续监听的方法
			throw 'There is already listening for this event : ' + message.methodName;
		}
		sendLoopMessage(mapObject, message, sendNativeInternal);
	}

	function offEventInternal(mapObject, message) {
		sendUnLoopMessage(mapObject, message, sendNativeInternal);
	}

	return {

		ready: function() {
			/**
			 * @param {string} message.methodName - Native 方法名称，必填
			 * @param {object} message.params - Native 方法参数，选填（无参类型方法）
			 * @param {function} message.success - Native 回调（成功回调，取决于业务）选填
			 * @param {failed} message.failed - Native 回调（失败回调，取决于业务）选填
			 * */
			addMethod(lr, 'callNative', function(message) {
				if (typeof lrBridge === 'undefined') {
					lrBridge = null;
				}
				lr.callNative(lrBridge, message);
			})

			/**
			 * @param {object} mapObject 来自原生的映射对象（Android可能会需要，iOS在使用WkWebView时则不需要）
			 * @param {object} message 参照上面方法message
			 * */
			addMethod(lr, 'callNative', function(mapObject, message) {
				callNativeInternal(mapObject, message);
			})

			/**
			 * @param {string} message.eventName - Native 事件名称，必填
			 * @param {object} message.params - Native 事件方法参数，选填（无参数类型）
			 * @param {function} message.success - Native 回调（成功回调，取决于业务）选填
			 * @param {function} message.failed - Native 回调（失败回调，取决于业务）选填
			 * */
			addMethod(lr, 'callEvent', function(message) {
				if (typeof lrBridge === 'undefined') {
					lrBridge = null;
				}
				lr.callEvent(lrBridge, message);
			})

			/**
			 * @param {object} mapObject 来自原生的映射对象（Android可能会需要，iOS在使用WkWebView时则不需要）
			 * @param {object} message 参照上面方法message
			 * */
			addMethod(lr, 'callEvent', function(mapObject, message) {
				callEventInternal(mapObject, message);
			})

			/**
			 * @param {int} message.id - Native 回调id，必填
			 * @param {object} message.result - 返回值，选填
			 * 	{
			 * 		@param {int} message.result.state - 状态，必填
			 * 		@param {string} message.result.desc - 说明，必填
			 * 		@param {object} message.result.result - 返回参数，必填
			 * 	}
			 * */
			addMethod(lr, 'notifyNative', function(message) {
				if (typeof lrBridge === 'undefined') {
					lrBridge = null;
				}
				lr.notifyNative(lrBridge, message)
			})

			/**
			 * @param {object} mapObject 来自原生的映射对象（Android可能会需要，iOS在使用WkWebView时则不需要）
			 * @param {object} message 参照上面方法message
			 * */
			addMethod(lr, 'notifyNative', function(mapObject, message) {
				notifyNativeInternal(mapObject, message)
			})

			/**
			 * 区别与 call 类型，可以持续监听方法*/
			addMethod(lr, 'onNative', function(message) {
				if (typeof lrBridge === 'undefined') {
					lrBridge = null;
				}
				lr.onNative(lrBridge, message);
			})

			/**
			 * 区别与 call 类型，可以持续监听方法*/
			addMethod(lr, 'onNative', function(mapObject, message) {
				onNativeInternal(mapObject, message);
			})

			/**
			 * 与 onNative 对应，移除持续监听的方法*/
			addMethod(lr, 'offNative', function(message) {
				if (typeof lrBridge === 'undefined') {
					lrBridge = null;
				}
				lr.offNative(lrBridge, message);
			})

			/**
			 * 与 onNative 对应，移除持续监听的方法*/
			addMethod(lr, 'offNative', function(mapObject, message) {
				offNativeInternal(mapObject, message);
			})

			/**
			 * 区别于 call 类型，可以持续监听事件*/
			addMethod(lr, 'onEvent', function(message) {
				if (typeof lrBridge === 'undefined') {
					lrBridge = null;
				}
				lr.onEvent(lrBridge, message);
			})

			/**
			 * 区别于 call 类型，可以持续监听事件*/
			addMethod(lr, 'onEvent', function(mapObject, message) {
				onEventInternal(mapObject, message);
			})

			/**
			 * 与 onEvent 对应，移除持续监听的事件*/
			addMethod(lr, 'offEvent', function(message) {
				if (typeof lrBridge === 'undefined') {
					lrBridge = null;
				}
				lr.offEvent(lrBridge, message);
			})

			/**
			 * 与 onEvent 对应，移除持续监听的事件*/
			addMethod(lr, 'offEvent', function(mapObject, message) {
				offEventInternal(mapObject, message);
			})
		},

		config: function(message) {
			wx.config(lr, message);
		},

		dispatchCallNativeCallback: function(response) {
			onNativeCallFinished(response)
		}
	}
})()

/**
 * 接收来自Native的通知, Native返回Response
 * */
function onNativeCallFinished(response) {
	lr.dispatchCallNativeCallback(response)
}

/**
 * 接收来自Native的事件调用
 * */
function callJavaScriptFromNative(request) {
	receiverManager.dispatchReceiver(JSON.parse(request))
}
