package com.jd.blockchain.ledger;

import com.jd.binaryproto.EnumContract;
import com.jd.binaryproto.EnumField;
import com.jd.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

import utils.Int8Code;

/**
 * TxPermission 交易权限表示一个用户可以发起的交易类型；
 * 
 * @author huanghaiquan
 *
 */
@EnumContract(code = DataCodes.ENUM_TX_PERMISSION)
public enum TransactionPermission implements Int8Code {

	/**
	 * 交易中包含指令操作；
	 */
	DIRECT_OPERATION((byte) 0x01),

	/**
	 * 交易中包含合约操作；
	 */
	CONTRACT_OPERATION((byte) 0x02);

	@EnumField(type = PrimitiveType.INT8)
	public final byte CODE;

	private TransactionPermission(byte code) {
		this.CODE = code;
	}

	@Override
	public byte getCode() {
		return CODE;
	}

}
