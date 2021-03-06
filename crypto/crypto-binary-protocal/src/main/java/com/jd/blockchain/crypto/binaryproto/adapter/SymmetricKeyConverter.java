package com.jd.blockchain.crypto.binaryproto.adapter;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.SymmetricKey;

public class SymmetricKeyConverter extends CryptoBytesConverter<SymmetricKey> {

	@Override
	public SymmetricKey instanceFrom(byte[] bytes) {
		return Crypto.resolveAsSymmetricKey(bytes);
	}

}
