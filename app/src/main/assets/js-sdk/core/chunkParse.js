var chunkParse = (function() {

	var stream = {}

	function parseInternal(chunk) {
		var chunkParse = getChunkParse(chunk);
		if (chunkParse != null) {
			if (stream[chunkParse.sid] == null) {
				stream[chunkParse.sid] = {
					data: '',
					index: 0
				}
			}
			stream[chunkParse.sid].data += chunkParse.data;
			stream[chunkParse.sid].index++;
			if (chunkParse.count == stream[chunkParse.sid].index) {
				// 比较摘要，相等则分发，否则删除（数据损坏）
				// if (hashCode(stream[chunkParse.sid].data) == chunkParse.sid) {
				lr.dispatchCallNativeCallback(stream[chunkParse.sid].data)
				// }
				delete stream[chunkParse.sid]
			}
		}
	}

	function getChunkParse(chunk) {
		var length = chunk.length;
		var ssi = chunk.indexOf(':');
		if (length > ssi + 2 &&
			chunk.charAt(ssi + 1) == '/' &&
			chunk.charAt(ssi + 2) == '/') {

			var chunkParse = {};
			var end = ssi + 3;
			outer: while (end < length) {
				var char = chunk.charAt(end);
				switch (char) {
					case '/':
					case '\\':
					case '?':
					case '#':
						break outer
				}
				// if (char == '/' ||
				// 	char == '\\' ||
				// 	char == '?' ||
				// 	char == '#') {
				// 	break outer
				// }
				end++;
			}
			var breakUp = chunk.substring(ssi + 3, end).split(':');
			chunkParse.sid = breakUp[0];
			chunkParse.count = breakUp[1];
			chunkParse.data = chunk.substring(end + 1, length);
			return chunkParse;
		}
		return null;
	}

	function hashCode(src) {
		var hash = 0;
		var len = src.length;
		if (len > 0) {
			for (var i = 0; i < len; i++) {
				hash = 31 * hash + src.charAt(i);
			}
			return hash;
		}
		return hash;
	}

	return {

		onChunk: function(response) {
			if (response.startsWith('chunk')) {
				parseInternal(response);
				return true;
			}
			return false;
		}
	}
})()
