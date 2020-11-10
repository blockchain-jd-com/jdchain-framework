package com.jd.blockchain.crypto;

import java.util.Arrays;

import com.jd.blockchain.utils.io.BytesSlice;
import com.jd.blockchain.utils.io.BytesUtils;

public final class CryptoBytesEncoding {

	public static byte[] encodeBytes(short algorithm, byte[] rawCryptoBytes) {
		return BytesUtils.concat(BytesUtils.toBytes(algorithm), rawCryptoBytes);
	}

	public static byte[] encodeBytes(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		return BytesUtils.concat(CryptoAlgorithm.getCodeBytes(algorithm), rawCryptoBytes);
	}

	public static short decodeAlgorithm(byte[] cryptoBytes) {
		return CryptoAlgorithm.resolveCode(cryptoBytes);
	}

	static byte[] encodeKeyBytes(byte[] rawKeyBytes, CryptoKeyType keyType) {
		return BytesUtils.concat(new byte[] { keyType.CODE }, rawKeyBytes);
	}

	static CryptoKeyType decodeKeyType(BytesSlice cryptoBytes) {
		return CryptoKeyType.valueOf(cryptoBytes.getByte());
	}
	
	public static byte[] decodeRawBytes(byte[] cryptoBytes) {
		return Arrays.copyOfRange(cryptoBytes, CryptoAlgorithm.CODE_SIZE, cryptoBytes.length);
	}
	
	public static BytesSlice decodeRawBytesSlice(byte[] cryptoBytes) {
		return new BytesSlice(cryptoBytes, CryptoAlgorithm.CODE_SIZE);
	}
}
