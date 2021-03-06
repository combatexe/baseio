package com.generallycloud.nio.buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.generallycloud.nio.AbstractLifeCycle;
import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;

public abstract class AbstractByteBufAllocator extends AbstractLifeCycle implements ByteBufAllocator {

	protected ByteBufUnit[]			units;

	protected int					capacity;

	protected int					mask;

	protected boolean				isDirect;

	protected int					unitMemorySize;

	protected ByteBufFactory		bufFactory;

	protected ReentrantLock			lock		;

	protected List<ByteBufUnit>		busyUnit	= new ArrayList<ByteBufUnit>();

	protected Logger				logger	= LoggerFactory.getLogger(AbstractByteBufAllocator.class);

	public AbstractByteBufAllocator(int capacity, int unitMemorySize, boolean isDirect) {
		this.isDirect = isDirect;
		this.capacity = capacity;
		this.unitMemorySize = unitMemorySize;
	}

	public boolean isDirect() {
		return isDirect;
	}

	public ByteBuf allocate(int capacity) {
		
		int size = (capacity + unitMemorySize - 1) / unitMemorySize;

		ReentrantLock lock = this.lock;

		lock.lock();
		
		try {

			int mask = this.mask;
			
			ByteBuf buf = allocate(capacity, mask, this.capacity, size);

			if (buf == null) {

				buf = allocate(capacity, 0, mask, size);
			}

			return buf;

		} finally {
			lock.unlock();
		}
	}

	public void freeMemory() {
		bufFactory.freeMemory();
	}

	protected abstract ByteBuf allocate(int capacity, int start, int end, int size);

	protected void doStart() throws Exception {
		
		lock		= new ReentrantLock();

		bufFactory = createBufFactory();

		int capacity = this.capacity;

		initializeMemory(capacity * unitMemorySize);

		ByteBufUnit[] bufs = new ByteBufUnit[capacity];

		for (int i = 0; i < capacity; i++) {
			ByteBufUnit buf = new ByteBufUnit();
			buf.index = i;
			bufs[i] = buf;
		}

		this.units = bufs;
	}

	private ByteBufFactory createBufFactory() {
		if (isDirect) {
			return new DirectByteBufFactory();
		}
		return new HeapByteBufFactory();
	}

	protected void doStop() throws Exception {
		this.freeMemory();
	}

	public int getCapacity() {
		return capacity;
	}

	public int getUnitMemorySize() {
		return unitMemorySize;
	}

	protected void initializeMemory(int capacity) {
		bufFactory.initializeMemory(capacity);
	}

	public void release(ByteBuf buf) {

		ReentrantLock lock = this.lock;

		lock.lock();

		try {

			doRelease(units[((PooledByteBuf) buf).getBeginUnit()]);

		} finally {
			lock.unlock();
		}
	}

	protected abstract void doRelease(ByteBufUnit beginUnit);

	public String toString() {

		busyUnit.clear();

		ByteBufUnit[] memoryUnits = this.units;

		int free = 0;

		for (ByteBufUnit b : memoryUnits) {

			if (b.free) {
				free++;
			} else {
				busyUnit.add(b);
			}
		}

		StringBuilder b = new StringBuilder();
		b.append(this.getClass().getSimpleName());
		b.append("[free=");
		b.append(free);
		b.append(",memory=");
		b.append(capacity);
		b.append("]");

		return b.toString();
	}
}
