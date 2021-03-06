package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.ParticipantNodeState;
import com.jd.blockchain.ledger.ParticipantStateUpdateInfo;
import com.jd.blockchain.ledger.ParticipantStateUpdateOperation;

import utils.net.NetworkAddress;

public class ParticipantStateUpdateOperationBuilderImpl implements ParticipantStateUpdateOperationBuilder {

    @Override
    public ParticipantStateUpdateOperation update(BlockchainIdentity blockchainIdentity, ParticipantNodeState participantNodeState) {
        return new ParticipantStateUpdateOpTemplate(blockchainIdentity, participantNodeState);
    }
}
