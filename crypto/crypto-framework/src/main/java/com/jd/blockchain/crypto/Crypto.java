package com.jd.blockchain.crypto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import utils.provider.Provider;
import utils.provider.ProviderManager;

/**
 * 密码服务提供者的管理器；
 * 
 * @author huanghaiquan
 *
 */
public final class Crypto {

	private static Map<Short, CryptoFunction> functions = new ConcurrentHashMap<>();

	private static Map<Short, CryptoAlgorithm> algorithms = new ConcurrentHashMap<>();

	private static Map<String, Short> names = new ConcurrentHashMap<>();

	private static CompositeCryptoEncoding encoding = new CompositeCryptoEncoding();

	private static ProviderManager pm = new ProviderManager();

	static {
		loadDefaultProviders();
	}

	private static void loadDefaultProviders() {
		ClassLoader cl = Crypto.class.getClassLoader();
		pm.installAllProviders(CryptoService.class, cl);

		Iterable<Provider<CryptoService>> providers = pm.getAllProviders(CryptoService.class);
		for (Provider<CryptoService> provider : providers) {
			register(provider);
		}
	}

	private static void register(Provider<CryptoService> provider) {
		CryptoEncoding providedEncoding = provider.getService().getEncoding();
		Set<CryptoAlgorithm> providedAlgorithms = new HashSet<>();
		for (CryptoFunction cryptoFunction : provider.getService().getFunctions()) {
			CryptoAlgorithm algorithm = cryptoFunction.getAlgorithm();
			if (providedEncoding.isRandomAlgorithm(algorithm) && !(cryptoFunction instanceof RandomFunction)) {
				continue;
			}
			if (providedEncoding.isAsymmetricEncryptionAlgorithm(algorithm)
					&& !(cryptoFunction instanceof AsymmetricEncryptionFunction)) {
				continue;
			}
			if (providedEncoding.isSignatureAlgorithm(algorithm) && !(cryptoFunction instanceof SignatureFunction)) {
				continue;
			}
			if (providedEncoding.isSymmetricEncryptionAlgorithm(algorithm)
					&& !(cryptoFunction instanceof SymmetricEncryptionFunction)) {
				continue;
			}
			if (providedEncoding.isHashAlgorithm(algorithm) && !(cryptoFunction instanceof HashFunction)) {
				continue;
			}
			if (providedEncoding.isExtAlgorithm(algorithm) && (cryptoFunction instanceof RandomFunction
					|| cryptoFunction instanceof AsymmetricEncryptionFunction
					|| cryptoFunction instanceof SignatureFunction
					|| cryptoFunction instanceof SymmetricEncryptionFunction
					|| cryptoFunction instanceof HashFunction)) {
				continue;
			}

			if (functions.containsKey(algorithm.code()) || names.containsKey(algorithm.name())) {
				continue;
			}

			if ((!providedEncoding.isRandomAlgorithm(algorithm))//
					&& (!providedEncoding.isHashAlgorithm(algorithm))//
					&& (!providedEncoding.isAsymmetricEncryptionAlgorithm(algorithm))//
					&& (!providedEncoding.isSignatureAlgorithm(algorithm))//
					&& (!providedEncoding.isSymmetricEncryptionAlgorithm(algorithm))//
					&& (!providedEncoding.isExtAlgorithm(algorithm))) {
				// 算法类型不属于任何预设的分类，算法定义无效；
				continue;
			}

			functions.put(algorithm.code(), cryptoFunction);
			algorithms.put(algorithm.code(), algorithm);
			String nameIndex = algorithm.name().toUpperCase();
			names.put(nameIndex, algorithm.code());

			providedAlgorithms.add(algorithm);
		}
		if (providedAlgorithms.size() > 0) {
			encoding.register(providedAlgorithms, providedEncoding);
		}
	}

	private Crypto() {
	}

	public static CryptoProvider[] getProviders() {
		Collection<Provider<CryptoService>> providers = pm.getAllProviders(CryptoService.class);
		CryptoProvider[] infos = new CryptoProvider[providers.size()];

		int i = 0;
		for (Provider<CryptoService> pd : providers) {
			CryptoProviderInfo info = getProviderInfo(pd);
			infos[i] = info;
			i++;
		}

		return infos;
	}

	private static CryptoProviderInfo getProviderInfo(Provider<CryptoService> pd) {
		Collection<CryptoFunction> functions = pd.getService().getFunctions();
		CryptoAlgorithm[] algorithms = new CryptoAlgorithm[functions.size()];
		int i = 0;
		for (CryptoFunction function : functions) {
			algorithms[i] = function.getAlgorithm();
			i++;
		}
		return new CryptoProviderInfo(pd.getFullName(), algorithms);
	}

	/**
	 * 返回指定名称的密码服务提供者；如果不存在，则返回 null ；
	 * 
	 * @param providerFullName
	 * @return
	 */
	public static CryptoProvider getProvider(String providerFullName) {
		Provider<CryptoService> pd = pm.getProvider(CryptoService.class, providerFullName);
		if (pd == null) {
			throw new CryptoException("Crypto service provider named [" + providerFullName + "] does not exist!");
		}
		return getProviderInfo(pd);
	}

	public static Collection<CryptoAlgorithm> getAllAlgorithms() {
		return algorithms.values();
	}

	/**
	 * 返回指定编码的算法； <br>
	 * 如果不存在，则返回 null；
	 * 
	 * @param code
	 * @return
	 */
	public static CryptoAlgorithm getAlgorithm(short code) {
		return algorithms.get(code);
	}

	/**
	 * Return the CryptoAlogrithm object of the specified name ,or null if none;
	 * 
	 * @param name
	 * @return
	 */
	public static CryptoAlgorithm getAlgorithm(String name) {
		Short code = names.get(name.toUpperCase());
		return code == null ? null : algorithms.get(code);
	}

	public static RandomFunction getRandomFunction(short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getRandomFunction(algorithm);
	}

	public static RandomFunction getRandomFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getRandomFunction(algorithm);
	}

	public static RandomFunction getRandomFunction(CryptoAlgorithm algorithm) {
		if (!encoding.isRandomAlgorithm(algorithm)) {
			throw new CryptoException("The specified algorithm " + algorithm.name() + "[" + algorithm.code()
					+ "] is not a random function!");
		}
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return (RandomFunction) func;
	}

	public static HashFunction getHashFunction(short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getHashFunction(algorithm);
	}

	public static HashFunction getHashFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getHashFunction(algorithm);
	}

	public static HashFunction getHashFunction(CryptoAlgorithm algorithm) {
		if (!encoding.isHashAlgorithm(algorithm)) {
			throw new CryptoException("The specified algorithm " + algorithm.name() + "[" + algorithm.code()
					+ "] is not a hash function!");
		}
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return (HashFunction) func;
	}

	public static AsymmetricEncryptionFunction getAsymmetricEncryptionFunction(short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getAsymmetricEncryptionFunction(algorithm);
	}

	public static AsymmetricEncryptionFunction getAsymmetricEncryptionFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getAsymmetricEncryptionFunction(algorithm);
	}

	public static AsymmetricEncryptionFunction getAsymmetricEncryptionFunction(CryptoAlgorithm algorithm) {
		if (!encoding.isAsymmetricEncryptionAlgorithm(algorithm)) {
			throw new CryptoException("The specified algorithm " + algorithm.name() + "[" + algorithm.code()
					+ "] is not a asymmetric encryption function!");
		}
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return (AsymmetricEncryptionFunction) func;
	}

	public static SignatureFunction getSignatureFunction(Short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getSignatureFunction(algorithm);
	}

	public static CASignatureFunction getCASignatureFunction(Short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getCASignatureFunction(algorithm);
	}

	public static CASignatureFunction getCASignatureFunction(CryptoAlgorithm algorithm) {
		if (!encoding.isSignatureAlgorithm(algorithm)) {
			throw new CryptoException("The specified algorithm " + algorithm.name() + "[" + algorithm.code()
					+ "] is not a signature function!");
		}
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return (CASignatureFunction) func;
	}

	public static SignatureFunction getSignatureFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getSignatureFunction(algorithm);
	}

	public static SignatureFunction getSignatureFunction(CryptoAlgorithm algorithm) {
		if (!encoding.isSignatureAlgorithm(algorithm)) {
			throw new CryptoException("The specified algorithm " + algorithm.name() + "[" + algorithm.code()
					+ "] is not a signature function!");
		}
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return (SignatureFunction) func;
	}

	public static SymmetricEncryptionFunction getSymmetricEncryptionFunction(short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getSymmetricEncryptionFunction(algorithm);
	}

	public static SymmetricEncryptionFunction getSymmetricEncryptionFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getSymmetricEncryptionFunction(algorithm);
	}

	public static SymmetricEncryptionFunction getSymmetricEncryptionFunction(CryptoAlgorithm algorithm) {
		if (!encoding.isSymmetricEncryptionAlgorithm(algorithm)) {
			throw new CryptoException("The specified algorithm " + algorithm.name() + "[" + algorithm.code()
					+ "] is not a symmetric encryption function!");
		}
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return (SymmetricEncryptionFunction) func;
	}

	public static CryptoFunction getCryptoFunction(short algorithmCode) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("Algorithm [code:" + algorithmCode + "] has no service provider!");
		}
		return getCryptoFunction(algorithm);
	}

	public static CryptoFunction getCryptoFunction(String algorithmName) {
		CryptoAlgorithm algorithm = getAlgorithm(algorithmName);
		if (algorithm == null) {
			throw new CryptoException("Algorithm " + algorithmName + " has no service provider!");
		}
		return getCryptoFunction(algorithm);
	}

	public static CryptoFunction getCryptoFunction(CryptoAlgorithm algorithm) {
		CryptoFunction func = functions.get(algorithm.code());
		if (func == null) {
			throw new CryptoException(
					"Algorithm " + algorithm.name() + "[" + algorithm.code() + "] has no service provider!");
		}

		return func;
	}

	public static HashDigest resolveAsHashDigest(byte[] encodedCryptoBytes) {
		return encoding.tryDecodeHashDigest(encodedCryptoBytes);
	}

	public static SignatureDigest resolveAsSignatureDigest(byte[] encodedCryptoBytes) {
		return encoding.decodeSignatureDigest(encodedCryptoBytes);
	}

	public static PrivKey resolveAsPrivKey(byte[] encodedCryptoBytes) {
		return encoding.decodePrivKey(encodedCryptoBytes);
	}

	public static PubKey resolveAsPubKey(byte[] encodedCryptoBytes) {
		return encoding.decodePubKey(encodedCryptoBytes);
	}

	public static SymmetricKey resolveAsSymmetricKey(byte[] encodedCryptoBytes) {
		return encoding.decodeSymmetricKey(encodedCryptoBytes);
	}

	private static class CompositeCryptoEncoding implements CryptoEncoding {

		private ArrayList<CryptoEncoding> encodings = new ArrayList<>();

		private Map<Short, CryptoEncoding> algorithmEncoding = new HashMap<>();

		public void register(Set<CryptoAlgorithm> algorithms, CryptoEncoding encoding) {
			for (CryptoAlgorithm algorithm : algorithms) {
				algorithmEncoding.put(algorithm.code(), encoding);
			}
			encodings.add(encoding);
		}


		@Override
		public SymmetricKey decodeSymmetricKey(byte[] encodedCryptoBytes) {
			SymmetricKey cryptoBytes = null;
			for (CryptoEncoding encoding : encodings) {
				cryptoBytes = encoding.decodeSymmetricKey(encodedCryptoBytes);
				if (cryptoBytes != null) {
					return cryptoBytes;
				}
			}
			throw new CryptoException("Unsupport the specified encoded symmetric key bytes!");
		}

		@Override
		public PubKey decodePubKey(byte[] encodedCryptoBytes) {
			PubKey cryptoBytes = null;
			for (CryptoEncoding encoding : encodings) {
				cryptoBytes = encoding.decodePubKey(encodedCryptoBytes);
				if (cryptoBytes != null) {
					return cryptoBytes;
				}
			}
			throw new CryptoException("Unsupport the specified encoded public key bytes!");
		}

		@Override
		public PrivKey decodePrivKey(byte[] encodedCryptoBytes) {
			PrivKey cryptoBytes = null;
			for (CryptoEncoding encoding : encodings) {
				cryptoBytes = encoding.decodePrivKey(encodedCryptoBytes);
				if (cryptoBytes != null) {
					return cryptoBytes;
				}
			}
			throw new CryptoException("Unsupport the specified encoded private key bytes!");
		}

		@Override
		public HashDigest tryDecodeHashDigest(byte[] encodedBytes) {
			HashDigest cryptoBytes = null;
			for (CryptoEncoding encoding : encodings) {
				cryptoBytes = encoding.tryDecodeHashDigest(encodedBytes);
				if (cryptoBytes != null) {
					return cryptoBytes;
				}
			}
			throw new CryptoException("Unsupport the specified encoded hash digest bytes!");
		}

		@Override
		public SignatureDigest decodeSignatureDigest(byte[] encodedBytes) {
			SignatureDigest cryptoBytes = null;
			for (CryptoEncoding encoding : encodings) {
				cryptoBytes = encoding.decodeSignatureDigest(encodedBytes);
				if (cryptoBytes != null) {
					return cryptoBytes;
				}
			}
			throw new CryptoException("Unsupport the specified encoded signature digest bytes!");
		}

		@Override
		public boolean isRandomAlgorithm(CryptoAlgorithm algorithm) {
			CryptoEncoding codec = algorithmEncoding.get(algorithm.code());
			if (codec != null) {
				return codec.isRandomAlgorithm(algorithm);
			}
			return false;
		}

		@Override
		public boolean isHashAlgorithm(CryptoAlgorithm algorithm) {
			CryptoEncoding codec = algorithmEncoding.get(algorithm.code());
			if (codec != null) {
				return codec.isHashAlgorithm(algorithm);
			}
			return false;
		}

		@Override
		public boolean isHashAlgorithm(short algorithmCode) {
			CryptoEncoding codec = algorithmEncoding.get(algorithmCode);
			if (codec != null) {
				return codec.isHashAlgorithm(algorithmCode);
			}
			return false;
		}

		@Override
		public boolean isSignatureAlgorithm(short algorithmCode) {
			CryptoEncoding codec = algorithmEncoding.get(algorithmCode);
			if (codec != null) {
				return codec.isSignatureAlgorithm(algorithmCode);
			}
			return false;
		}

		@Override
		public boolean isSignatureAlgorithm(CryptoAlgorithm algorithm) {
			CryptoEncoding codec = algorithmEncoding.get(algorithm.code());
			if (codec != null) {
				return codec.isSignatureAlgorithm(algorithm);
			}
			return false;
		}

		@Override
		public boolean isEncryptionAlgorithm(short algorithmCode) {
			CryptoEncoding codec = algorithmEncoding.get(algorithmCode);
			if (codec != null) {
				return codec.isEncryptionAlgorithm(algorithmCode);
			}
			return false;
		}

		@Override
		public boolean isEncryptionAlgorithm(CryptoAlgorithm algorithm) {
			CryptoEncoding codec = algorithmEncoding.get(algorithm.code());
			if (codec != null) {
				return codec.isEncryptionAlgorithm(algorithm);
			}
			return false;
		}

		@Override
		public boolean isExtAlgorithm(CryptoAlgorithm algorithm) {
			CryptoEncoding codec = algorithmEncoding.get(algorithm.code());
			if (codec != null) {
				return codec.isExtAlgorithm(algorithm);
			}
			return false;
		}

		@Override
		public boolean hasAsymmetricKey(CryptoAlgorithm algorithm) {
			CryptoEncoding codec = algorithmEncoding.get(algorithm.code());
			if (codec != null) {
				return codec.hasAsymmetricKey(algorithm);
			}
			return false;
		}

		@Override
		public boolean hasSymmetricKey(CryptoAlgorithm algorithm) {
			CryptoEncoding codec = algorithmEncoding.get(algorithm.code());
			if (codec != null) {
				return codec.hasSymmetricKey(algorithm);
			}
			return false;
		}

		@Override
		public boolean isSymmetricEncryptionAlgorithm(CryptoAlgorithm algorithm) {
			CryptoEncoding codec = algorithmEncoding.get(algorithm.code());
			if (codec != null) {
				return codec.isSymmetricEncryptionAlgorithm(algorithm);
			}
			return false;
		}

		@Override
		public boolean isAsymmetricEncryptionAlgorithm(CryptoAlgorithm algorithm) {
			CryptoEncoding codec = algorithmEncoding.get(algorithm.code());
			if (codec != null) {
				return codec.isAsymmetricEncryptionAlgorithm(algorithm);
			}
			return false;
		}

	}

}
