package com.jd.blockchain.ledger;

import com.jd.binaryproto.EnumContract;
import com.jd.binaryproto.EnumField;
import com.jd.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

/**
 * 交易（事务）执行状态；
 *
 * @author huanghaiquan
 *
 */
@EnumContract(code = DataCodes.TRANSACTION_EXEC_STATE)
public enum TransactionState {

	/**
	 * 成功；
	 */
	SUCCESS((byte) 0),

	/**
	 * 账本的未知错误；
	 */
	LEDGER_ERROR((byte) 0x01),

	/**
	 * 数据账户不存在；
	 */
	DATA_ACCOUNT_DOES_NOT_EXIST((byte) 0x02),

	/**x
	 * 用户不存在；
	 */
	USER_DOES_NOT_EXIST((byte) 0x03),

	/**
	 * 合约不存在；
	 */
	CONTRACT_DOES_NOT_EXIST((byte) 0x04),

	/**
	 * 数据写入时版本冲突；
	 */
	DATA_VERSION_CONFLICT((byte) 0x05),

	/**
	 * 参与方不存在；
	 */
	PARTICIPANT_DOES_NOT_EXIST((byte) 0x06),

//	/**
//	 * 激活新参与方时的视图更新错误
//	 */
//	VIEW_UPDATE_ERROR((byte) 0x07),
//
//	/**
//	 * 激活新的参与方时的节点启动失败
//	 */
//	START_SERVER_ERROR((byte) 0x08),
//
//	/**
//	 * 参与方重复激活错误
//	 */
//	PARTICIPANT_REPEATLY_ACTIVATE_ERROR((byte) 0x09),

	/**
	 * 合约执行错误；
	 */
	CONTRACT_EXECUTE_ERROR((byte) 0x10),

	/**
	 * 合约部署时版本冲突；
	 */
	CONTRACT_VERSION_CONFLICT((byte) 0x08),

	/**
	 * 被安全策略拒绝；
	 */
	REJECTED_BY_SECURITY_POLICY((byte) 0x11),

	/**
	 * 账户注册冲突；
	 */
	ACCOUNT_REGISTER_CONFLICT((byte) 0x12),

	/**
	 * 由于在错误的账本上执行交易而被忽略；
	 */
	IGNORED_BY_WRONG_LEDGER((byte) 0x40),

	/**
	 * 由于交易内容的验签失败而忽略；
	 */
	IGNORED_BY_ILLEGAL_CONTENT_SIGNATURE((byte) 0x41),
	
	/**
	 * 由于交易内容哈希不一致而忽略；
	 */
	IGNORED_BY_INCONSISTENT_CONTENT_HASH((byte) 0x42),

//	/**
//	 * 由于交易内容的验签失败而丢弃；
//	 */
//	IGNORED_BY_CONFLICTING_STATE((byte) 0x42),

	/**
	 * 由于交易的整体回滚而丢弃；
	 * <p>
	 *
	 * 注： “整体回滚”是指把交易引入的数据更改以及交易记录本身全部都回滚；<br>
	 * “部分回滚”是指把交易引入的数据更改回滚了，但是交易记录本身以及相应的“交易结果({@link TransactionState})”都会提交；<br>
	 */
	IGNORED_BY_TX_FULL_ROLLBACK((byte) 0x43),

	/**
	 * 由于区块的整体回滚而丢弃；
	 * <p>
	 *
	 * 注： “整体回滚”是指把交易引入的数据更改以及交易记录本身全部都回滚；<br>
	 *
	 * “部分回滚”是指把交易引入的数据更改回滚了，但是交易记录本身以及相应的“交易结果({@link TransactionState})”都会提交；<br>
	 */
	IGNORED_BY_BLOCK_FULL_ROLLBACK((byte) 0x44),

//	/**
//	 *
//	 * 共识阶段加入新区块哈希预计算功能, 如果来自其他Peer的新区块哈希值不一致，本批次整体回滚
//	 *
//	 */
//	IGNORED_BY_CONSENSUS_PHASE_PRECOMPUTE_ROLLBACK((byte) 0x45),

	/**
	 * 系统错误；
	 */
	SYSTEM_ERROR((byte) 0x80),

	/**
	 * 超时；
	 */
	TIMEOUT((byte) 0x81),

	/**
	 * 共识错误；
	 */
	CONSENSUS_ERROR((byte) 0x82),

	/**
	 * 未收到共识网络响应的错误
	 */
	CONSENSUS_NO_REPLY_ERROR((byte) 0x83),

	/**
	 * 创建共识的代理客户端错误
	 */
	CONSENSUS_PROXY_CLIENT_ERROR((byte) 0x84),

	/**
	 * 空区块错误；
	 */
	EMPTY_BLOCK_ERROR((byte) 0x85),

	/**
	 * 共识时间戳错误
	 *
	 */
	CONSENSUS_TIMESTAMP_ERROR((byte) 0x86);


	@EnumField(type = PrimitiveType.INT8)
	public final byte CODE;

	private TransactionState(byte code) {
		this.CODE = code;
	}

	public static TransactionState valueOf(byte code) {
		for (TransactionState tr : values()) {
			if (tr.CODE == code) {
				return tr;
			}
		}
		throw new IllegalArgumentException("Unsupported transaction result code!");
	}

}
