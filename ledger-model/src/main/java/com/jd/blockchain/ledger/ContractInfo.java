package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code= DataCodes.CONTRACT_ACCOUNT_HEADER)
public interface ContractInfo extends BlockchainIdentity, AccountSnapshot {

    @DataField(order=4, primitiveType= PrimitiveType.BYTES)
    byte[] getChainCode();

    @DataField(order=5, primitiveType= PrimitiveType.INT64)
    long getChainCodeVersion();
}
