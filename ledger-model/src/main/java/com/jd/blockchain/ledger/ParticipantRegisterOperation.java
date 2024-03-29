package com.jd.blockchain.ledger;

import com.jd.binaryproto.DataContract;
import com.jd.binaryproto.DataField;
import com.jd.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code= DataCodes.TX_OP_PARTICIPANT_REG)
public interface ParticipantRegisterOperation extends Operation {
	
	ParticipantNodeState DEFAULT_STATE = ParticipantNodeState.READY;

    @DataField(order = 0, primitiveType=PrimitiveType.TEXT)
    String getParticipantName();

    @DataField(order = 1, refContract = true)
    BlockchainIdentity getParticipantID();

    @DataField(order = 2, primitiveType = PrimitiveType.TEXT)
    String getCertificate();
}
