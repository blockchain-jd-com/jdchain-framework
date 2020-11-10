package com.jd.blockchain.crypto.service.classic;

import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_CODE_SIZE;

import java.util.Arrays;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoBytes;
import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.crypto.base.DefaultCryptoEncoding;
import com.jd.blockchain.crypto.utils.classic.SHA256Utils;

public class SHA256HashFunction implements HashFunction {

	private static final CryptoAlgorithm ALGORITHM = ClassicAlgorithm.SHA256;

	public static final int DIGEST_BYTES = 256 / 8;

	private static final int DIGEST_LENGTH = ALGORYTHM_CODE_SIZE + SHA256HashFunction.DIGEST_BYTES;

	SHA256HashFunction() {
	}

	@Override
	public CryptoAlgorithm getAlgorithm() {
		return ALGORITHM;
	}

	@Override
	public HashDigest hash(byte[] data) {
		if (data == null) {
			throw new CryptoException("data is null!");
		}

		byte[] digestBytes = SHA256Utils.hash(data);
		return DefaultCryptoEncoding.encodeHashDigest(ALGORITHM, digestBytes);
	}

	@Override
	public HashDigest hash(byte[] data, int offset, int len) {
		if (data == null) {
			throw new CryptoException("data is null!");
		}

		byte[] digestBytes = SHA256Utils.hash(data, offset, len);
		return DefaultCryptoEncoding.encodeHashDigest(ALGORITHM, digestBytes);
	}

	@Override
	public byte[] rawHash(byte[] data) {
		if (data == null) {
			throw new CryptoException("data is null!");
		}

		byte[] digestBytes = SHA256Utils.hash(data);
		return digestBytes;
	}

	@Override
	public byte[] rawHash(byte[] data, int offset, int len) {
		if (data == null) {
			throw new CryptoException("data is null!");
		}

		byte[] digestBytes = SHA256Utils.hash(data, offset, len);
		return digestBytes;
	}

	@Override
	public boolean verify(HashDigest digest, byte[] data) {
		HashDigest hashDigest = hash(data);
		return Arrays.equals(hashDigest.toBytes(), digest.toBytes());
	}

	@Override
	public boolean supportHashDigest(byte[] digestBytes) {
		// 验证输入字节数组长度=算法标识长度+摘要长度，以及算法标识；
		return DIGEST_LENGTH == digestBytes.length && CryptoAlgorithm.match(ALGORITHM, digestBytes);
	}

	@Override
	public HashDigest resolveHashDigest(byte[] digestBytes) {
		if (supportHashDigest(digestBytes)) {
			return DefaultCryptoEncoding.createHashDigest(ALGORITHM.code(), digestBytes);
		} else {
			throw new CryptoException("digestBytes is invalid!");
		}
	}

	@Override
	public <T extends CryptoBytes> boolean support(Class<T> cryptoDataType, byte[] encodedCryptoBytes) {
		return HashDigest.class == cryptoDataType && supportHashDigest(encodedCryptoBytes);
	}

}
