package com.generallycloud.nio.codec.http11.future;

import com.generallycloud.nio.common.BASE64Util;
import com.generallycloud.nio.common.UUIDGenerator;
import com.generallycloud.nio.component.SocketChannelContext;

public class WebSocketUpgradeRequestFuture extends ClientHttpReadFuture {

	public WebSocketUpgradeRequestFuture(SocketChannelContext context,String url) {
		
		super(context,url, "GET");

		this.setResponseHeaders();
	}

	private void setResponseHeaders() {
		setResponseHeader("Connection", "Upgrade");
		setResponseHeader("Upgrade", "websocket");
		setResponseHeader("Sec-WebSocket-Version", "13");
		setResponseHeader("Sec-WebSocket-Key", BASE64Util.byteArrayToBase64(UUIDGenerator.random().substring(8,24).getBytes()));
		setResponseHeader("Sec-WebSocket-Extensions", "permessage-deflate; client_max_window_bits");
	}
}
