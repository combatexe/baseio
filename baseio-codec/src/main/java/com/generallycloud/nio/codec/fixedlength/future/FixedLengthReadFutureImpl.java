package com.generallycloud.nio.codec.fixedlength.future;

import java.io.IOException;

import com.generallycloud.nio.buffer.ByteBuf;
import com.generallycloud.nio.codec.fixedlength.FixedLengthProtocolDecoder;
import com.generallycloud.nio.common.ReleaseUtil;
import com.generallycloud.nio.component.SocketChannelContext;
import com.generallycloud.nio.component.Session;
import com.generallycloud.nio.component.SocketSession;
import com.generallycloud.nio.protocol.AbstractTextReadFuture;
import com.generallycloud.nio.protocol.ProtocolException;

public class FixedLengthReadFutureImpl extends AbstractTextReadFuture implements FixedLengthReadFuture {

	private ByteBuf	buf;

	private int		length;

	private boolean	header_complete;

	private boolean	body_complete;

	private byte[]	byteArray;

	private int		limit;

	public FixedLengthReadFutureImpl(SocketSession session, ByteBuf buf) {
		this(session, buf, 1024 * 1024);
	}
	
	public FixedLengthReadFutureImpl(SocketSession session, ByteBuf buf,int limit) {
		super(session.getContext());
		this.buf = buf;
		this.limit = limit;
	}

	public FixedLengthReadFutureImpl(SocketChannelContext context) {
		super(context);
	}

	private boolean isHeaderReadComplete(ByteBuf buf) {
		return !buf.hasRemaining();
	}

	private void doHeaderComplete(Session session, ByteBuf buf) {

		header_complete = true;

		int length = buf.getInt(0);

		this.length = length;

		if (length < 1) {

			if (length == FixedLengthProtocolDecoder.PROTOCOL_PING) {

				setPING();

				body_complete = true;

				return;
			} else if (length == FixedLengthProtocolDecoder.PROTOCOL_PONG) {

				setPONG();

				body_complete = true;

				return;
			}

			throw new ProtocolException("illegal length:" + length);

		} else if (length > limit) {

			throw new ProtocolException("max "+limit+" ,length:" + length);

		} else if (length > buf.capacity()) {

			ReleaseUtil.release(buf);

			this.buf = allocate(session,length);

		} else {

			buf.limit(length);
		}
	}

	public boolean read(SocketSession session, ByteBuf buffer) throws IOException {

		ByteBuf buf = this.buf;

		if (!header_complete) {

			buf.read(buffer);

			if (!isHeaderReadComplete(buf)) {
				return false;
			}

			doHeaderComplete(session, buf);
		}

		if (!body_complete) {

			buf.read(buffer);

			if (buf.hasRemaining()) {
				return false;
			}

			doBodyComplete(buf);
		}

		return true;
	}

	//FIXME decode
	private void doBodyComplete(ByteBuf buf) {

		body_complete = true;

		byteArray = new byte[buf.limit()];

		buf.flip();

		buf.get(byteArray);
		
		readText = new String(byteArray, context.getEncoding());
	}

	public String getFutureName() {
		return null;
	}

	public int getLength() {
		return length;
	}

	public byte[] getByteArray() {
		return byteArray;
	}

	public void release() {
		ReleaseUtil.release(buf);
	}

}
