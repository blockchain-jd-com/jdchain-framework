package com.jd.blockchain.ledger;

import com.jd.binaryproto.DataContract;
import com.jd.binaryproto.DataField;
import com.jd.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

import utils.Bytes;

@DataContract(code = DataCodes.SECURITY_USER_AUTH_INIT_SETTING)
public interface UserAuthInitSettings {

	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	Bytes getUserAddress();

	@DataField(order = 2, primitiveType = PrimitiveType.TEXT, list = true)
	String[] getRoles();

	@DataField(order = 3, refEnum =  true)
	RolesPolicy getPolicy();

}
