var wx = (function() {

	function addMethod(core, methodName) {
		core[methodName] = eval(methodName)
	}

	/**
	 * 关闭窗口
	 * */
	function closeWindow() {
		lr.callNative({
			methodName: 'closeWindow'
		});
	}

	/**
	 * 分享弹窗
	 * @param {string} message.title - 分享标题.
	 * @param {string} message.desc - 分享描述.
	 * @param {string} message.link - 分享地址.
	 * @param {string} message.imgUrl - 分享图标.
	 * @param {function} message.success - 成功回调.
	 * @param {function} message.cancel - 取消回调.
	 * @param {function} message.complete - 无论分享结果都会回调.
	 */
	function showShareUI(message) {
		var params = {
			title: message.title,
			desc: message.desc,
			link: message.link,
			imgUrl: message.imgUrl
		}

		lr.callNative({
			methodName: 'showShareUI',
			params: params,
			success: message.success,
			failed: message.cancel,
			complete: message.complete
		});
	}

	/**
	 * 分享到微信
	 * @param {string} message.title - 分享标题.
	 * @param {string} message.desc - 分享描述.
	 * @param {string} message.link - 分享地址.
	 * @param {string} message.imgUrl - 分享图标.
	 * @param {string} message.type - 分享类型.
	 * @param {string} message.dataUrl - 如果 type 是 music 或 video 则需要提供数据链接.
	 * @param {function} message.success - 成功回调.
	 * @param {function} message.cancel - 取消回调.
	 * @param {function} message.complete - 无论分享结果都会回调.
	 */
	function shareToWechat(message) {
		var params = {
			title: message.title,
			desc: message.desc,
			link: message.link,
			imgUrl: message.imgUrl,
			type: message.type,
			dataUrl: message.dataUrl
		}
		lr.callNative({
			methodName: 'shareToWechat',
			params: params,
			success: message.success,
			failed: message.cancel,
			complete: message.complete
		});
	}

	/**
	 * 分享到微信朋友圈
	 * @param {string} message.title - 分享标题.
	 * @param {string} message.link - 分享地址.
	 * @param {string} message.imgUrl - 分享图标.
	 * @param {function} message.success - 成功回调.
	 * @param {function} message.cancel - 取消回调.
	 * @param {function} message.complete - 无论分享结果都会回调.
	 */
	function shareToWechatMoment(message) {
		var params = {
			title: message.title,
			link: message.link,
			imgUrl: message.imgUrl
		}
		lr.callNative({
			methodName: 'shareToWechatMoment',
			params: params,
			success: message.success,
			failed: message.cancel,
			complete: message.complete
		});
	}

	/**
	 * 分享到QQ
	 * @param {string} message.title - 分享标题.
	 * @param {string} message.desc - 分享描述.
	 * @param {string} message.link - 分享地址.
	 * @param {string} message.imgUrl - 分享图标.
	 * @param {function} message.success - 成功回调.
	 * @param {function} message.cancel - 取消回调.
	 * @param {function} message.complete - 无论分享结果都会回调.
	 */
	function shareToQQ(message) {
		var params = {
			title: message.title,
			desc: message.desc,
			link: message.link,
			imgUrl: message.imgUrl
		}
		lr.callNative({
			methodName: 'shareToQQ',
			params: params,
			success: message.success,
			failed: message.cancel,
			complete: message.complete
		});
	}

	/**
	 * 分享到QQ空间
	 * @param {string} message.title - 分享标题.
	 * @param {string} message.desc - 分享描述.
	 * @param {string} message.link - 分享地址.
	 * @param {string} message.imgUrl - 分享图标.
	 * @param {function} message.success - 成功回调.
	 * @param {function} message.cancel - 取消回调.
	 * @param {function} message.complete - 无论分享结果都会回调.
	 */
	function shareToQZone(message) {
		var params = {
			title: message.title,
			desc: message.desc,
			link: message.link,
			imgUrl: message.imgUrl
		}
		lr.callNative({
			methodName: 'shareToQZone',
			params: params,
			success: message.success,
			failed: message.cancel,
			complete: message.complete
		});
	}

	//-------------------------------多媒体--------------------------------------------

	/**
	 * 拍照
	 * @param {boolean} message.crop - 是否需要裁减，默认 false.
	 * @param {number} message.width - 裁减宽度，默认 468px.
	 * @param {number} message.height - 裁减高度，默认高度 624px.
	 * @param {number} message.x - 宽高比例，默认（x）3:4.
	 * @param {number} message.y - 宽高比例，默认 3：4（y）.
	 * @param {string} message.tips - 裁剪框说明.
	 * @param {function} message.success - 成功的回调.
	 * @param {function} message.failed - 失败的回调.
	 * @param {function} message.complete - 无论成功失败都会回调.
	 */
	function takePicture(message) {
		var params = {
			crop: message.crop,
			width: message.width,
			height: message.height,
			x: message.x,
			y: message.y,
			tips: message.tips
		}

		lr.callNative({
			methodName: 'takePicture',
			params: params,
			success: function(res) {
				message.success({
					localId: res.result
				});
			},
			failed: message.failed,
			complete: message.complete
		});
	}

	/**
	 * 相册
	 * @param {boolean} message.crop - 是否需要裁减，默认 false.
	 * @param {number} message.width - 裁减宽度，默认 468px.
	 * @param {number} message.height - 裁减高度，默认高度 624px.
	 * @param {number} message.x - 宽高比例，默认（x）3:4.
	 * @param {number} message.y - 宽高比例，默认 3：4（y）.
	 * @param {string} message.tips - 裁剪框说明.
	 * @param {function} message.success - 成功的回调.
	 * @param {function} message.failed - 失败的回调.
	 * @param {function} message.complete - 无论成功失败都会回调.
	 */
	function startGallery(message) {
		var params = {
			crop: message.crop,
			width: message.width,
			height: message.height,
			x: message.x,
			y: message.y,
			tips: message.tips
		}

		lr.callNative({
			methodName: 'startGallery',
			params: params,
			success: function(res) {
				message.success({
					localId: res.result
				});
			},
			failed: message.failed,
			complete: message.complete
		});
	}

	/**
	 * 图片裁减
	 * @param {string} message.src - 图片地址（本地）必须是 Uri 类型.
	 * @param {number} message.width - 裁减宽度，默认 468px.
	 * @param {number} message.height - 裁减高度，默认高度 624px.
	 * @param {string} message.tips - 裁剪框说明.
	 * @param {function} message.success - 成功的回调.
	 * @param {function} message.failed - 失败的回调.
	 * @param {function} message.complete - 无论成功失败都会回调.
	 */
	function startGallery(message) {
		var params = {
			crop: message.crop,
			width: message.width,
			height: message.height,
			tips: message.tips
		}

		lr.callNative({
			methodName: 'startGallery',
			params: params,
			success: function(res) {
				message.success({
					localId: res.result
				});
			},
			failed: message.failed,
			complete: message.complete
		});
	}

	/**
	 * 图片预览
	 * @param {string} message.current - 图片地址（本地或远程）.
	 * @param {array} message.urls - 需要预览图片的地址列表.
	 * @param {function} message.success - 成功的回调.
	 * @param {function} message.failed - 失败的回调.
	 * @param {function} message.complete - 无论成功失败都会回调.
	 */
	function previewImage(message) {
		var params = {
			current: message.current,
			urls: message.urls
		}

		lr.callNative({
			methodName: 'previewImage',
			params: params,
			success: message.success,
			failed: message.failed,
			complete: message.complete
		});
	}

	/**
	 * 多选图片
	 * @param {number} message.count - 图片数量，最大为 9.
	 * @param {function} message.success - 成功的回调.
	 * @param {function} message.failed - 失败的回调.
	 * @param {function} message.complete - 无论成功失败都会回调.
	 */
	function chooseImage(message) {
		var params = {
			count: message.count
		}

		lr.callNative({
			methodName: 'chooseImage',
			params: params,
			success: function(res){
			    message.success({
			        localIds: res.result
			    });
			},
			failed: message.failed,
			complete: message.complete
		});
	}

	/**
	 * 图片转换为 base64，适配 iOS WKWebView 不支持图片路径问题.
	 * @param {string} message.localId - 图片的 localId.
	 * @param {function} message.success - 成功的回调.
	 * @param {function} message.failed - 失败的回调.
	 * @param {function} message.complete - 无论成功失败都会回调 */
	function getLocalImgData(message) {
		var params = {
			localId: message.localId
		}
		lr.callNative({
			methodName: 'getLocalImgData',
			params: params,
			success: message.success,
			failed: message.failed,
			complete: message.complete
		});
	}

	//---------------------------------------网络状态--------------------------------------------------------------------

	/**
	 * 获取网络状态
	 * @param {function} message.complete - 成功获取的回调.
	 * @param {function} message.failed - 获取失败的回调.
	 * @param {function} message.complete - 无论成功/失败都会回调. */
	function getNetworkType(message) {
		var request = {
			methodName: 'getNetworkType',
			success: function(res) {
				message.success({
					networkType: res.result
				});
			},
			failed: message.failed,
			complete: message.complete
		}
		lr.callNative(request);
	}

	//----------------------------------地图-------------------------------------------------------------------------

	/**
	 * 获取地理位置接口
	 * @param {string} message.type - 默认为gcj102的gps坐标.
	 * @param {boolean} message.altitude - 是否需要高度信息.
	 * @param {boolean} message.isHighAccuracy - 是否开启高精度定位.
	 * @param {number} message.highAccuracyExpireTime - 高精度定位超时时间(ms)，指定时间内返回最高精度，该值在 1000ms 以上高精度定位才有效
	 * @param {boolean} message.isAddress - 是否需要地址描述信息.
	 * @param {function} message.success - 接口调用成功的回调函数.
	 * @param {function} message.failed - 接口调用失败的回调函数.
	 * @param {function} message.complete - 接口调用结束回调函数.
	 */
	function getLocation(message) {
		var params = {
			type: message.type,
			altitude: message.altitude,
			isHighAccuracy: message.isHighAccuracy,
			highAccuracyExpireTime: message.highAccuracyExpireTime,
			isAddress: message.isAddress,
		}
		lr.callNative({
			methodName: 'getLocation',
			params: params,
			success: function(res) {
				message.success(res.result);
			},
			failed: message.failed,
			complete: message.complete
		});
	}

	//----------------------------------------扫码---------------------------------------------------------------

	/**
	 * 扫码
	 * @param {string} message.tip - 扫描框提示语，默认：请讲二维码放入扫描框内.
	 * @param {boolean} message.barCode - 是否为条形码，默认 false.
	 * @param {number} message.animTime - 扫描光标的动画时常，默认 1000ms.
	 * @param {number} message.width - 扫描框宽度，默认屏幕宽度的3/4.
	 * @param {function} message.success - 扫描成功的回调.
	 * @param {function} message.failed - 扫描失败后的回调.
	 * @param {function} message.complete - 无论扫描结果都会回调. */
	function scanQRCode(message) {
		var params = {
			tip: message.tip,
			barCode: message.barCode,
			animTime: message.animTime,
			width: message.width
		}
		var request = {
			methodName: 'scanQRCode',
			params: params,
			success: function(res) {
				message.success({
					qrValue: res.result
				});
			},
			failed: message.failed,
			complete: message.complete
		}
		lr.callNative(request);
	}

	return {

		config: function(host, message) {
			if (message.debug !== 'undefined') {
				var params = {
					debug: message.debug
				}
				var request = {
					methodName: 'config',
					params: params
				}
				lr.callNative(request)
			}

			var list = message.methodList;
			for (var i = 0; i < list.length; i++) {
				addMethod(host, list[i]);
			}
		}
	}
})()
