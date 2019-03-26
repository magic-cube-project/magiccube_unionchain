package com.magiccube.blockchain.socket.distruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.magiccube.blockchain.socket.distruptor.base.BaseEvent;
import com.magiccube.blockchain.socket.distruptor.base.MessageProducer;

/**
 * 所有客户端、server端发来的消息，都进入这里，然后publish出去，供消费者消费
 */
public class DisruptorProducer implements MessageProducer {
    private Disruptor<BaseEvent> disruptor;

    public DisruptorProducer(Disruptor<BaseEvent> disruptor) {
        this.disruptor = disruptor;
    }

    @Override
    public void publish(BaseEvent baseEvent) {
        RingBuffer<BaseEvent> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            // Get the entry in the Disruptor
            BaseEvent event = ringBuffer.get(sequence);
            // for the sequence   // Fill with data
            event.setBlockPacket(baseEvent.getBlockPacket());
            event.setChannelContext(baseEvent.getChannelContext());
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}