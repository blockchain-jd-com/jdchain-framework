package com.jd.blockchain.sdk.service.event;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.Event;
import com.jd.blockchain.sdk.EventContext;
import com.jd.blockchain.sdk.EventListenerHandle;
import com.jd.blockchain.sdk.EventPoint;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象事件处理线程
 *
 * @author shaozhuguang
 *
 * @param <E>
 */
public abstract class AbstractEventRunnable<E extends EventPoint> implements Runnable {

    private static final int MAX_COUNT = 100;

    private final Map<String, Long> eventSequences = new ConcurrentHashMap<>();

    /**
     * 账本Hash
     */
    private HashDigest ledgerHash;

    /**
     * 事件集合
     */
    private Set<E> eventPointSet;

    /**
     * 事件监听处理器
     */
    private EventListenerHandle<E> handle;

    public AbstractEventRunnable(HashDigest ledgerHash, Set<E> eventPointSet, EventListenerHandle<E> handle) {
        this.ledgerHash = ledgerHash;
        this.eventPointSet = eventPointSet;
        this.handle = handle;
        initEventSequences();
    }

    @Override
    public void run() {
        try {
            loadEventsByHttpProtocol();
        } catch (Throwable t) {
        }
    }

    public HashDigest getLedgerHash() {
        return ledgerHash;
    }

    public EventListenerHandle<E> getHandle() {
        return handle;
    }

    /**
     * 通过http协议加载所有的事件
     */
    private void loadEventsByHttpProtocol() {
        // 发送一个请求至网关
        for(E eventPoint : eventPointSet) {
            Event[] events = loadEvent(eventPoint, eventSequence(eventPoint), MAX_COUNT);
            onEvent(eventPoint, events);
        }
    }

    /**
     * 处理接收到的事件
     *
     * @param eventPoint
     * @param events
     */
    private void onEvent(E eventPoint, Event[] events) {
        if (events != null && events.length > 0) {
            long fromSequence = -1L;
            sortEventsBySequence(events);
            for (Event event : events) {
                fromSequence = Math.max(fromSequence, event.getSequence());
            }
            onEvent(events);
            updateEventSequence(eventPoint, fromSequence + 1);
        }
    }

    /**
     * 根据sequence序号进行排序
     *
     * @param events
     */
    private void sortEventsBySequence(Event[] events) {
        Arrays.sort(events, (e1, e2) -> (int)(e1.getSequence() - e2.getSequence()));
    }

    /**
     * 更新事件对应的from序号
     *
     * @param eventPoint
     *             事件
     * @param sequence
     *             事件对应的fromSequence
     */
    private synchronized void updateEventSequence(E eventPoint, long sequence) {
        eventSequences.put(eventPointKey(eventPoint), sequence);
    }

    /**
     * 获取事件对应的sequence
     *
     * @param eventPoint
     *             事件
     *
     * @return
     */
    private synchronized long eventSequence(E eventPoint) {
        Long fromSequence = eventSequences.get(eventPointKey(eventPoint));
        if (fromSequence == null) {
            return -1L;
        } else {
            return fromSequence;
        }
    }

    /**
     * 从远端加载对应的事件
     *
     * @param eventPoint
     * @param fromSequence
     * @param maxCount
     * @return
     */
    abstract Event[] loadEvent(E eventPoint, long fromSequence, int maxCount);

    /**
     * 事件处理
     *
     * @param events
     */
    abstract void onEvent(Event[] events);

    /**
     * 将事件转换为事件上下文
     *
     * @param event
     * @return
     */
    abstract EventContext<E> eventContext(Event event);

    /**
     * eventSequences中保存的EventPoint唯一键
     *
     * @param eventPoint
     * @return
     */
    abstract String eventPointKey(E eventPoint);

    /**
     * 初始化事件对应的Sequence
     *
     */
    private void initEventSequences() {
        for (E eventPoint : eventPointSet) {
            eventSequences.put(eventPointKey(eventPoint), eventPoint.getSequence());
        }
    }
}
