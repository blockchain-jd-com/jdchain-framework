package com.jd.blockchain.ledger;

import java.util.Set;

import utils.Bytes;

/**
 * 表示赋予角色的特权码；
 *
 * @author zhaoguangwei
 *
 */
public interface UserPrivilegeSet {
	Bytes getUserAddress();
	Set<String> getUserRole();
	LedgerPrivilege getLedgerPrivilegesBitset();
	TransactionPrivilege getTransactionPrivilegesBitset();
}
