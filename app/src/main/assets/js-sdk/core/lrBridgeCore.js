var lr = (function() {

	function parseSdkPath() {
		const scripts = document.getElementsByTagName("script");
		for (var script of scripts) {
			const src = script.getAttribute("src");
			if (src.lastIndexOf("lrBridgeCore.js") > 0) {
				const lastIndexOf = src.lastIndexOf("/");
				const temp = src.substr(0, lastIndexOf);
				const secLastIndexOf = temp.lastIndexOf("/");
				return temp.substr(0, secLastIndexOf + 1);
			}
		}
	}

	var lazyLoader = (function(sdkPointer) {

		var moduleList = {};
		var sdkPath = null;

		function LoaderHelper(moduleName, callback) {
			this.moduleName = moduleName;
			this.callback = callback;

			if (!sdkPath) {
				sdkPath = sdkPointer();
			}
		}

		LoaderHelper.prototype = {

			load: function() {
				this.moduleName = sdkPath + this.moduleName;

				if (this.isLoad()) {
					this.callback();
				} else {
					var script = document.createElement('script');
					script.src = this.moduleName;
					script.type = 'text/javascript';
					document.getElementsByTagName('head')[0].appendChild(script)

					var that = this;
					script.onload = function() {
						moduleList[that.moduleName] = that.moduleName
						that.callback();
					}
				}
			},

			isLoad: function() {
				var key = this.moduleName;
				return typeof moduleList[key] !== 'undefined' && moduleList[key] === key
			}
		}

		return {
			load: function(moduleName, callback) {
				var loaderHelper = new LoaderHelper(moduleName, callback);
				loaderHelper.load();
			}
		}

	})(parseSdkPath)

	var JAVASCRIPT_CALL_FINISHED = 'onJavaScriptCallFinished'
	var CALL_NATIVE_FROM_JAVASCRIPT = 'callNativeFromJavaScript'
	var RESULT_OK = 1

	var jsVersion = 1
	var jsCallbacks = {}
	var jsCallbackCurrentId = -1
	var isReady = false

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

		lazyLoader.load('utils/platform.js', () => {
			if (isAndroid()) {
				sendToAndroid(mapObject, message.methodName, request)
			} else if (isIos()) {
				sendToiOS(message.methodName, request)
			} else {
				alert('Unknown platform');
			}
		});
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

		lazyLoader.load('utils/platform.js', () => {
			if (isAndroid()) {
				sendToAndroid(mapObject, JAVASCRIPT_CALL_FINISHED, response);
			} else if (isIos()) {
				sendToiOS(JAVASCRIPT_CALL_FINISHED, response);
			} else {
				alert('Unknown platform');
			}
		});
	}

	function onBridgeConfigInternal(message) {
		if (!isReady) {
			ready();
		}
		lazyLoader.load('core/wxBridge.js', () => {
			wx.config(lr, message)
		});
	}

	function dispatchReceiverInternal(request) {
		lazyLoader.load('core/receiver.js', () => {
			receiverManager.dispatchReceiver(request)
		});
	}

	function onNativeCallFinishedInternal(response) {
		lazyLoader.load('core/chunkParse.js', () => {
			if (chunkParse.onChunk(response)) {
				return
			}
			onNativeCallInternalFinished(response);
		});
	}

	function onNativeCallInternalFinished(response) {
		var responseJson
		if (response && typeof response === 'object') {
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
			// ?????????????????????????????????????????????
			delete jsCallbacks[protocolId];
			if (typeof chain.id === 'number' && chain.id >= 0) {
				// ????????????????????????????????????
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
			} else {
				var isEvent = message.methodName === CALL_NATIVE_FROM_JAVASCRIPT;
				var realName = isEvent ? message.eventName : message.methodName;
				var typeName = isEvent ? 'eventName' : 'methodName';

				if (message.loop === Type.ON) {
					chain.loop = {
						loop: true
					}
					message[typeName] = 'on' + convertInitialsToUpperCase(realName);
					loops[realName] = request.id;
				} else if (message.loop === Type.OFF) {
					chain.loop = {
						loop: false,
						id: loops[realName]
					}
					message[typeName] = 'off' + convertInitialsToUpperCase(realName);
					// ??????????????????
					delete loops[realName]
				}
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
			// ??????????????????????????????????????????????????????
			throw 'There is already listening for this event : ' + message.methodName;
		}
		sendLoopMessage(mapObject, message, sendNativeInternal);
	}

	function offNativeInternal(mapObject, message) {
		sendUnLoopMessage(mapObject, message, sendNativeInternal);
	}

	function onEventInternal(mapObject, message) {
		if (loops[message.eventName] >= 0) {
			// ??????????????????????????????????????????????????????
			throw 'There is already listening for this event : ' + message.methodName;
		}
		sendLoopMessage(mapObject, message, sendNativeInternal);
	}

	function offEventInternal(mapObject, message) {
		sendUnLoopMessage(mapObject, message, sendNativeInternal);
	}

	function ready() {
		/**
		 * @param {string} message.methodName - Native ?????????????????????
		 * @param {object} message.params - Native ?????????????????????????????????????????????
		 * @param {function} message.success - Native ????????????????????????????????????????????????
		 * @param {failed} message.failed - Native ????????????????????????????????????????????????
		 * */
		addMethod(lr, 'callNative', function(message) {
			if (typeof lrBridge === 'undefined') {
				lrBridge = null;
			}
			lr.callNative(lrBridge, message);
		})

		/**
		 * @param {object} mapObject ??????????????????????????????Android??????????????????iOS?????????WkWebView??????????????????
		 * @param {object} message ??????????????????message
		 * */
		addMethod(lr, 'callNative', function(mapObject, message) {
			callNativeInternal(mapObject, message);
		})

		/**
		 * @param {string} message.eventName - Native ?????????????????????
		 * @param {object} message.params - Native ????????????????????????????????????????????????
		 * @param {function} message.success - Native ????????????????????????????????????????????????
		 * @param {function} message.failed - Native ????????????????????????????????????????????????
		 * */
		addMethod(lr, 'callEvent', function(message) {
			if (typeof lrBridge === 'undefined') {
				lrBridge = null;
			}
			lr.callEvent(lrBridge, message);
		})

		/**
		 * @param {object} mapObject ??????????????????????????????Android??????????????????iOS?????????WkWebView??????????????????
		 * @param {object} message ??????????????????message
		 * */
		addMethod(lr, 'callEvent', function(mapObject, message) {
			callEventInternal(mapObject, message);
		})

		/**
		 * @param {int} message.id - Native ??????id?????????
		 * @param {object} message.result - ??????????????????
		 * 	{
		 * 		@param {int} message.result.state - ???????????????
		 * 		@param {string} message.result.desc - ???????????????
		 * 		@param {object} message.result.result - ?????????????????????
		 * 	}
		 * */
		addMethod(lr, 'notifyNative', function(message) {
			if (typeof lrBridge === 'undefined') {
				lrBridge = null;
			}
			lr.notifyNative(lrBridge, message)
		})

		/**
		 * @param {object} mapObject ??????????????????????????????Android??????????????????iOS?????????WkWebView??????????????????
		 * @param {object} message ??????????????????message
		 * */
		addMethod(lr, 'notifyNative', function(mapObject, message) {
			notifyNativeInternal(mapObject, message)
		})

		/**
		 * ????????? call ?????????????????????????????????*/
		addMethod(lr, 'onNative', function(message) {
			if (typeof lrBridge === 'undefined') {
				lrBridge = null;
			}
			lr.onNative(lrBridge, message);
		})

		/**
		 * ????????? call ?????????????????????????????????*/
		addMethod(lr, 'onNative', function(mapObject, message) {
			onNativeInternal(mapObject, message);
		})

		/**
		 * ??? onNative ????????????????????????????????????*/
		addMethod(lr, 'offNative', function(message) {
			if (typeof lrBridge === 'undefined') {
				lrBridge = null;
			}
			lr.offNative(lrBridge, message);
		})

		/**
		 * ??? onNative ????????????????????????????????????*/
		addMethod(lr, 'offNative', function(mapObject, message) {
			offNativeInternal(mapObject, message);
		})

		/**
		 * ????????? call ?????????????????????????????????*/
		addMethod(lr, 'onEvent', function(message) {
			if (typeof lrBridge === 'undefined') {
				lrBridge = null;
			}
			lr.onEvent(lrBridge, message);
		})

		/**
		 * ????????? call ?????????????????????????????????*/
		addMethod(lr, 'onEvent', function(mapObject, message) {
			onEventInternal(mapObject, message);
		})

		/**
		 * ??? onEvent ????????????????????????????????????*/
		addMethod(lr, 'offEvent', function(message) {
			if (typeof lrBridge === 'undefined') {
				lrBridge = null;
			}
			lr.offEvent(lrBridge, message);
		})

		/**
		 * ??? onEvent ????????????????????????????????????*/
		addMethod(lr, 'offEvent', function(mapObject, message) {
			offEventInternal(mapObject, message);
		})
	}

	return {

		ready: function() {
			// todo ???????????????????????????
		},

		config: function(message) {
			onBridgeConfigInternal(message)
		},

		dispatchCallNativeCallback: function(response) {
			onNativeCallFinishedInternal(response)
		},

		dispatchReceiver: function(request) {
			dispatchReceiverInternal(request);
		}
	}
})();

/**
 * ????????????Native?????????, Native??????Response
 * */
function onNativeCallFinished(response) {
	lr.dispatchCallNativeCallback(response)
}

/**
 * ????????????Native???????????????
 * */
function callJavaScriptFromNative(request) {
	lr.dispatchReceiver(JSON.parse(request))
}
