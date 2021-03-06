package com.generallycloud.nio.codec.protobuf.future;

import java.io.IOException;
import java.nio.charset.Charset;

import com.generallycloud.nio.buffer.ByteBuf;
import com.generallycloud.nio.codec.base.future.BaseReadFutureImpl;
import com.generallycloud.nio.component.SocketSession;
import com.generallycloud.nio.component.SocketChannelContext;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

public class ProtobufReadFutureImpl extends BaseReadFutureImpl implements ProtobufReadFuture {

	private MessageLite		message;

	private boolean		writed;

	// for ping & pong
	public ProtobufReadFutureImpl(SocketChannelContext context) {
		super(context);
	}
	
	public ProtobufReadFutureImpl(SocketChannelContext context,String futureName) {
		super(context, futureName);
	}

	public ProtobufReadFutureImpl(SocketChannelContext context,Integer futureID, String futureName) {
		super(context, futureID, futureName);
	}
	
	public ProtobufReadFutureImpl(SocketSession session, ByteBuf buf) throws IOException {
		super(session, buf);
	}

	public MessageLite getMessage() throws InvalidProtocolBufferException {

		if (message == null) {

			ProtobufIOEventHandle handle = (ProtobufIOEventHandle) context.getIoEventHandleAdaptor();

			Parser<? extends MessageLite> parser = handle.getParser(getParserName());

			message = parser.parseFrom(getBinary());
		}

		return message;
	}
	
	public void writeProtobuf(MessageLite messageLite) throws InvalidProtocolBufferException {
		writeProtobuf(messageLite.getClass().getName(), messageLite);
	}

	public void writeProtobuf(String parserName, MessageLite messageLite) throws InvalidProtocolBufferException {

		if (writed) {
			throw new InvalidProtocolBufferException("writed");
		}

		super.write(parserName);

		//FIXME 判断array是否过大
		byte [] array = messageLite.toByteArray();
		
		super.writeBinary(array,0,array.length);
	}

	public void write(byte[] bytes, int offset, int length) {
		throw new UnsupportedOperationException("use writeProtobuf instead");
	}

	public void write(String content, Charset encoding) {
		throw new UnsupportedOperationException("use writeProtobuf instead");
	}

	public String getParserName() {
		return getText();
	}
	
}
