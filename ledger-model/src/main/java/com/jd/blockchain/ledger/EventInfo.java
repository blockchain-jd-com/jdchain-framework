package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.utils.Bytes;

public class EventInfo implements Event {

    static {
        DataContractRegistry.register(Event.class);
    }

    private String name;
    private long sequence;
    private BytesValue content;
    private HashDigest transactionSource;
    private String contractSource;
    private long blockHeight;
    private Bytes eventAccount;

    public EventInfo(Event event) {
        this.name = event.getName();
        this.sequence = event.getSequence();
        this.content = TypedValue.wrap(event.getContent());
        this.transactionSource = event.getTransactionSource();
        this.contractSource = event.getContractSource();
        this.blockHeight = event.getBlockHeight();
        this.eventAccount = event.getEventAccount();
    }


    public EventInfo(Bytes eventAccount, String name, long sequence, BytesValue content, HashDigest transactionSource, long blockHeight) {
        this.name = name;
        this.sequence = sequence;
        this.content = content;
        this.transactionSource = transactionSource;
        this.blockHeight = blockHeight;
        this.eventAccount = eventAccount;
    }

    public EventInfo(String name, long sequence, BytesValue content, HashDigest transactionSource, long blockHeight) {
        this.name = name;
        this.sequence = sequence;
        this.content = content;
        this.transactionSource = transactionSource;
        this.blockHeight = blockHeight;
    }

    public EventInfo(Bytes eventAccount, String name, long sequence, BytesValue content, String contractSource, long blockHeight) {
        this.name = name;
        this.sequence = sequence;
        this.content = content;
        this.contractSource = contractSource;
        this.blockHeight = blockHeight;
        this.eventAccount = eventAccount;
    }

    public EventInfo(String name, long sequence, BytesValue content, String contractSource, long blockHeight) {
        this.name = name;
        this.sequence = sequence;
        this.content = content;
        this.contractSource = contractSource;
        this.blockHeight = blockHeight;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSequence() {
        return sequence;
    }

    @Override
    public BytesValue getContent() {
        return content;
    }

    @Override
    public HashDigest getTransactionSource() {
        return transactionSource;
    }

    @Override
    public String getContractSource() {
        return contractSource;
    }

    @Override
    public long getBlockHeight() {
        return blockHeight;
    }

    @Override
    public Bytes getEventAccount() {
        return eventAccount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public void setContent(BytesValue content) {
        this.content = TypedValue.wrap(content);
    }

    public void setTransactionSource(HashDigest transactionSource) {
        this.transactionSource = transactionSource;
    }

    public void setContractSource(String contractSource) {
        this.contractSource = contractSource;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public void setEventAccount(Bytes eventAccount) {
        this.eventAccount = eventAccount;
    }
}
