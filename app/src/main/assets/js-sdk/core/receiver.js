var receiverManager = (function() {

	var events = {}
	var lifecycles = {}

	function addEventInternal(eventName) {
		events[eventName] = document;
	}

	function addLifecycleInternal(eventName, event) {
		if (lifecycles[document] == null) {
			lifecycles[document] = {}
		}
		var eventMap = lifecycles[document]
		eventMap[eventName] = event
	}

	function dispatchReceiverInternal(request) {
		var host = events[request.eventName];
		var eventMap = lifecycles[host];
		var targetMethod = eventMap[request.eventName];
		var targetResult;
		if (targetMethod.length > 0) {
			targetResult = targetMethod(request.params);
		} else {
			targetResult = targetMethod();
		}
		if (targetResult != null) {
			var message = {
				id: request.id,
				result: targetResult
			}
			lr.notifyNative(message);
		}
	}

	function removeLifecycleInternal(host) {
		var eventMap = lifecycles[host]
		if (eventMap != null) {
			for (key in eventMap) {
				delete events[key]
			}
			delete lifecycles[host]
		}
	}

	return {

		/**
		 * @param {string} eventName 事件名称
		 * */
		addEvent: function(eventName) {
			addEventInternal(eventName)
		},

		/**
		 * @param {string} eventName 事件名称
		 * @param {function} event 事件
		 * */
		addLifecycle: function(eventName, event) {
			addLifecycleInternal(eventName, event)
		},

		/**
		 * @param {object} request 来自Native的请求体
		 * */
		dispatchReceiver: function(request) {
			dispatchReceiverInternal(request)
		},

		/**
		 * @param {object} host 事件宿主
		 * */
		removeLifecycle: function(host) {
			removeLifecycleInternal(host)
		}
	}
})()

//var receiver = new Proxy(receiverManager, {
//
//	set: function(target, key, value) {
//		if (typeof value === 'function') {
//			target.addEvent(key);
//			target.addLifecycle(key, value);
//		} else {
//			throw 'The property \'' + key + '\' must function type'
//		}
//	}
//});

