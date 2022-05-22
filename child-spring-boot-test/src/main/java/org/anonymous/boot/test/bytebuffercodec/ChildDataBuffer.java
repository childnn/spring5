package org.anonymous.boot.test.bytebuffercodec;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.core.io.buffer.DataBuffer
 * @see org.springframework.core.io.buffer.DataBufferFactory
 * @see org.springframework.core.io.buffer.DataBufferUtils
 * @see org.springframework.core.codec.Decoder
 * @see org.springframework.core.codec.Encoder
 * @since 2022/05/21
 */
public class ChildDataBuffer {

	// Java NIO provides ByteBuffer but many libraries build their own byte buffer API on top,
	// especially for network operations where reusing buffers and/or using direct buffers is beneficial for performance.
	// For example Netty has the ByteBuf hierarchy, Undertow uses XNIO, Jetty uses pooled byte buffers with a callback
	// to be released, and so on. The spring-core module provides a set of abstractions to work with various byte buffer APIs as follows:
	//
	// - DataBufferFactory abstracts the creation of a data buffer.
	//
	// - DataBuffer represents a byte buffer, which may be pooled.
	//
	// - DataBufferUtils offers utility methods for data buffers.
	//
	// - Codecs decode or encode data buffer streams into higher level objects.


	//=============================================
	// DataBufferFactory
	//=============================================
	// DataBufferFactory is used to create data buffers in one of two ways:
	//
	// - Allocate a new data buffer, optionally specifying capacity upfront, if known,
	//   which is more efficient even though implementations of DataBuffer can grow and shrink on demand.
	//
	// - Wrap an existing byte[] or java.nio.ByteBuffer, which decorates the given data with a DataBuffer implementation and that does not involve allocation.
	//
	// Note that WebFlux applications do not create a DataBufferFactory directly but instead access it
	// through the ServerHttpResponse or the ClientHttpRequest on the client side.
	// The type of factory depends on the underlying client or server, e.g. NettyDataBufferFactory for Reactor Netty, DefaultDataBufferFactory for others.


	//=============================================
	// DataBuffer
	//=============================================

	// The DataBuffer interface offers similar operations as java.nio.ByteBuffer but also brings a few additional
	// benefits some of which are inspired by the Netty ByteBuf.
	// Below is a partial list of benefits:
	//
	// - Read and write with independent positions, i.e. not requiring a call to flip() to alternate between read and write.
	// - Capacity expanded on demand as with java.lang.StringBuilder.
	// - Pooled buffers and reference counting via PooledDataBuffer.
	// - View a buffer as java.nio.ByteBuffer, InputStream, or OutputStream.
	// - Determine the index, or the last index, for a given byte.


	//=============================================
	// PooledDataBuffer
	//=============================================

	// As explained in the Javadoc for ByteBuffer, byte buffers can be direct or non-direct.
	// Direct buffers may reside outside the Java heap which eliminates the need for copying for native I/O operations.
	// That makes direct buffers particularly useful for receiving and sending data over a socket,
	// but they’re also more expensive to create and release, which leads to the idea of pooling buffers.
	//
	// PooledDataBuffer is an extension of DataBuffer that helps with reference counting which is essential for byte buffer pooling.
	// How does it work?
	// When a PooledDataBuffer is allocated the reference count is at 1.
	// Calls to retain() increment the count, while calls to release() decrement it.
	// As long as the count is above 0, the buffer is guaranteed not to be released.
	// When the count is decreased to 0, the pooled buffer can be released,
	// which in practice could mean the reserved memory for the buffer is returned to the memory pool.
	//
	// Note that instead of operating on PooledDataBuffer directly, in most cases it’s better to use the convenience methods in
	// DataBufferUtils that apply release or retain to a DataBuffer only if it is an instance of PooledDataBuffer.


	//=============================================
	// DataBufferUtils
	//=============================================
	//
	// DataBufferUtils offers a number of utility methods to operate on data buffers:
	//
	// - Join a stream of data buffers into a single buffer possibly with zero copy, e.g. via composite buffers, if that’s supported by the underlying byte buffer API.
	// - Turn InputStream or NIO Channel into Flux<DataBuffer>, and vice versa a Publisher<DataBuffer> into OutputStream or NIO Channel.
	// - Methods to release or retain a DataBuffer if the buffer is an instance of PooledDataBuffer.
	// - Skip or take from a stream of bytes until a specific byte count.


	//=============================================
	// Codecs
	//=============================================
	//
	// The org.springframework.core.codec package provides the following strategy interfaces:
	//
	// Encoder to encode Publisher<T> into a stream of data buffers.
	//
	// Decoder to decode Publisher<DataBuffer> into a stream of higher level objects.
	//
	// The spring-core module provides byte[], ByteBuffer, DataBuffer, Resource, and String encoder and decoder implementations.
	// The spring-web module adds Jackson JSON, Jackson Smile, JAXB2, Protocol Buffers and other encoders and decoders. See Codecs in the WebFlux section.


}
