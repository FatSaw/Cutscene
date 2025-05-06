package io.netty.channel;

public interface ChannelHandler {
	public void channelRead(ChannelHandlerContext context, Object packet) throws Exception;
	public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception;
}
