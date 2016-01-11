package com.gifisan.mtp.component;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.gifisan.mtp.Encoding;
import com.gifisan.mtp.server.OutputStream;
import com.gifisan.mtp.server.Response;
import com.gifisan.mtp.server.ServerEndPoint;

public class MTPServletResponse implements Response {

	public final byte			RESPONSE_STREAM	= 1;
	public final byte			RESPONSE_TEXT		= 0;
	private byte				emptyByte			= ' ';
	private int				dataLength		= 0;
	private ServerEndPoint		endPoint			= null;
	private boolean			flushed			= false;
	private byte				type				= RESPONSE_TEXT;
	private boolean			typed			= false;
	private BufferedOutputStream	bufferWriter		= new BufferedOutputStream();
	private OutputStream		writer			= null;

	public void flush() throws IOException {
		if (type < RESPONSE_STREAM) {
			this.flushText();
		}
	}

	public void flushEmpty() throws IOException {
		this.endPoint.write(emptyByte);
		this.flushText();
		
	}

	private void flushText() throws IOException {
		if (flushed) {
			throw new FlushedException("flushed already");
		}

		if (bufferWriter.size() == 0) {
			throw new EOFException("empty byte");
		}

		if (!endPoint.isOpened()) {
			throw new MTPChannelException("channel closed");
		}
		
		this.flushed = true;

		ByteBuffer buffer = getByteBufferTEXT();

		this.bufferWriter.reset();
		this.endPoint.write(buffer);
		
	}

	private ByteBuffer getByteBufferStream() {
		byte[] header = new byte[5];
		int _dataLength = dataLength;

		header[0] = type;
		header[1] = (byte) (_dataLength & 0xff);
		header[2] = (byte) ((_dataLength >> 8) & 0xff);
		header[3] = (byte) ((_dataLength >> 16) & 0xff);
		header[4] = (byte) (_dataLength >>> 24);

		return ByteBuffer.wrap(header);
	}

	private ByteBuffer getByteBufferTEXT() {
		int length = bufferWriter.size();
		byte[] header = new byte[5];

		header[0] = type;
		header[1] = (byte) (length & 0xff);
		header[2] = (byte) ((length >> 8) & 0xff);
		header[3] = (byte) ((length >> 16) & 0xff);
		header[4] = (byte) (length >>> 24);

		ByteBuffer buffer = ByteBuffer.allocate(length + 5);

		buffer.put(header);
		buffer.put(bufferWriter.toByteArray());
		buffer.flip();

		return buffer;
	}

	public void setStreamResponse(int length) throws IOException {
		if (length < 1) {
			throw new EOFException("invalidate length");
		}

		if (typed) {
			throw new IOException("response typed");
		}

		this.type = RESPONSE_STREAM;
		this.dataLength = length;

		ByteBuffer buffer = getByteBufferStream();

		this.typed = true;
		this.endPoint.write(buffer);
		this.writer = this.endPoint;
	}

	public void write(byte b) throws IOException {
		this.writer.write(b);

	}

	public void write(byte[] bytes) throws IOException {
		this.writer.write(bytes);
	}

	public void write(byte[] bytes, int offset, int length) throws IOException {
		this.writer.write(bytes, offset, length);

	}

	public void write(String content) {
		try {
			byte[] bytes = content.getBytes(Encoding.DEFAULT);
			writer.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Response update(ServerEndPoint endPoint){
		this.endPoint = endPoint;
		this.type = RESPONSE_TEXT;
		this.writer = this.bufferWriter;
		this.flushed = false;
		this.typed = false;
		return this;
	}

	public void write(String content, Charset encoding) {
		try {
			byte[] bytes = content.getBytes(encoding);
			writer.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
