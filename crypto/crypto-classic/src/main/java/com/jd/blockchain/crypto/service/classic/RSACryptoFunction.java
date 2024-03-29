package com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.AsymmetricEncryptionFunction;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.CASignatureFunction;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoBytes;
import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.CryptoKeyType;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.crypto.base.AlgorithmUtils;
import com.jd.blockchain.crypto.base.DefaultCryptoEncoding;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import utils.crypto.classic.RSAUtils;
import utils.crypto.classic.SHA256SecureRandom;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;

/**
 * @author zhanglin33
 * @title: RSACryptoFunction
 * @description: Interfaces for RSA crypto functions, including key generation,
 * encryption, signature, and so on
 * @date 2019-03-25, 17:28
 */
public class RSACryptoFunction implements AsymmetricEncryptionFunction, SignatureFunction, CASignatureFunction {

    private static final CryptoAlgorithm ALGORITHM = ClassicAlgorithm.RSA;

    // modulus.length = 256, publicExponent.length = 3
    private static final int PUBKEY_SIZE = 259;
    // modulus.length = 256, publicExponent.length = 3, privateExponent.length =
    // 256, p.length = 128, q.length =128,
    // dP.length = 128, dQ.length = 128, qInv.length = 128
    private static final int PRIVKEY_SIZE = 1155;

    private static final int SIGNATUREDIGEST_SIZE = 256;
    private static final int CIPHERTEXTBLOCK_SIZE = 256;

    private static final int PUBKEY_LENGTH = CryptoAlgorithm.CODE_SIZE + CryptoKeyType.TYPE_CODE_SIZE + PUBKEY_SIZE;
    private static final int PRIVKEY_LENGTH = CryptoAlgorithm.CODE_SIZE + CryptoKeyType.TYPE_CODE_SIZE + PRIVKEY_SIZE;
    private static final int SIGNATUREDIGEST_LENGTH = CryptoAlgorithm.CODE_SIZE + SIGNATUREDIGEST_SIZE;

    @Override
    public byte[] encrypt(PubKey pubKey, byte[] data) {

        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();

        // 验证原始公钥长度为257字节
        if (rawPubKeyBytes.length != PUBKEY_SIZE) {
            throw new CryptoException("This key has wrong format!");
        }

        // 验证密钥数据的算法标识对应RSA算法
        if (pubKey.getAlgorithm() != ALGORITHM.code()) {
            throw new CryptoException("The is not RSA public key!");
        }

        // 调用RSA加密算法计算密文
        byte[] cipherbytes = RSAUtils.encrypt(data, rawPubKeyBytes);
        return cipherbytes;
    }

    @Override
    public byte[] decrypt(PrivKey privKey, byte[] cipherBytes) {
        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();

        // 验证原始私钥长度为1153字节
        if (rawPrivKeyBytes.length != PRIVKEY_SIZE) {
            throw new CryptoException("This key has wrong format!");
        }

        // 验证密钥数据的算法标识对应RSA算法
        if (privKey.getAlgorithm() != ALGORITHM.code()) {
            throw new CryptoException("This key is not RSA private key!");
        }

        // 验证密文数据的算法标识对应RSA算法，并且密文是分组长度的整数倍
        if (cipherBytes.length % CIPHERTEXTBLOCK_SIZE != 0) {
            throw new CryptoException("This is not RSA ciphertext!");
        }

        // 调用RSA解密算法得到明文结果
        return RSAUtils.decrypt(cipherBytes, rawPrivKeyBytes);
    }

    @Override
    public PubKey retrievePubKey(PrivKey privKey) {
        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
        byte[] rawPubKeyBytes = RSAUtils.retrievePublicKey(rawPrivKeyBytes);
        return DefaultCryptoEncoding.encodePubKey(ALGORITHM, rawPubKeyBytes);
    }

    @Override
    public SignatureDigest sign(PrivKey privKey, byte[] data) {

        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();

        // 验证原始私钥长度为1153字节
        if (rawPrivKeyBytes.length != PRIVKEY_SIZE) {
            throw new CryptoException("This key has wrong format!");
        }

        // 验证密钥数据的算法标识对应RSA签名算法
        if (privKey.getAlgorithm() != ALGORITHM.code()) {
            throw new CryptoException("This key is not RSA private key!");
        }

        // 调用RSA签名算法计算签名结果
        byte[] signatureBytes = RSAUtils.sign(data, rawPrivKeyBytes);
        return DefaultCryptoEncoding.encodeSignatureDigest(ALGORITHM, signatureBytes);

    }

    @Override
    public boolean verify(SignatureDigest digest, PubKey pubKey, byte[] data) {

        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
        byte[] rawDigestBytes = digest.getRawDigest();

        // 验证原始公钥长度为257字节
        if (rawPubKeyBytes.length != PUBKEY_SIZE) {
            throw new CryptoException("This key has wrong format!");
        }

        // 验证密钥数据的算法标识对应RSA签名算法
        if (pubKey.getAlgorithm() != ALGORITHM.code()) {
            throw new CryptoException("This key is not RSA public key!");
        }

        // 验证签名数据的算法标识对应RSA签名算法，并且原始签名长度为256字节
        if (digest.getAlgorithm() != ALGORITHM.code() || rawDigestBytes.length != SIGNATUREDIGEST_SIZE) {
            throw new CryptoException("This is not RSA signature digest!");
        }

        // 调用RSA验签算法验证签名结果
        return RSAUtils.verify(data, rawPubKeyBytes, rawDigestBytes);
    }

    @Override
    public boolean supportPrivKey(byte[] privKeyBytes) {
        // 验证输入字节数组长度=算法标识长度+密钥类型长度+密钥长度，密钥数据的算法标识对应RSA算法，并且密钥类型是私钥
        return privKeyBytes.length == PRIVKEY_LENGTH && AlgorithmUtils.match(ALGORITHM, privKeyBytes)
                && privKeyBytes[CryptoAlgorithm.CODE_SIZE] == PRIVATE.CODE;
    }

    @Override
    public PrivKey resolvePrivKey(byte[] privKeyBytes) {
        if (supportPrivKey(privKeyBytes)) {
            return DefaultCryptoEncoding.createPrivKey(ALGORITHM.code(), privKeyBytes);
        } else {
            throw new CryptoException("privKeyBytes are invalid!");
        }
    }

    @Override
    public boolean supportPubKey(byte[] pubKeyBytes) {
        // 验证输入字节数组长度=算法标识长度+密钥类型长度+椭圆曲线点长度，密钥数据的算法标识对应RSA算法，并且密钥类型是公钥
        return pubKeyBytes.length == PUBKEY_LENGTH && AlgorithmUtils.match(ALGORITHM, pubKeyBytes)
                && pubKeyBytes[CryptoAlgorithm.CODE_SIZE] == PUBLIC.CODE;
    }

    @Override
    public PubKey resolvePubKey(byte[] pubKeyBytes) {
        if (supportPubKey(pubKeyBytes)) {
            return DefaultCryptoEncoding.createPubKey(ALGORITHM.code(), pubKeyBytes);
        } else {
            throw new CryptoException("pubKeyBytes are invalid!");
        }
    }

    @Override
    public boolean supportDigest(byte[] digestBytes) {
        // 验证输入字节数组长度=算法标识长度+签名长度，字节数组的算法标识对应RSA算法
        return digestBytes.length == SIGNATUREDIGEST_LENGTH && AlgorithmUtils.match(ALGORITHM, digestBytes);
    }

    @Override
    public SignatureDigest resolveDigest(byte[] digestBytes) {
        if (supportDigest(digestBytes)) {
            return DefaultCryptoEncoding.createSignatureDigest(ALGORITHM.code(), digestBytes);
        } else {
            throw new CryptoException("digestBytes is invalid!");
        }
    }

    @Override
    public PubKey resolvePubKey(X509Certificate certificate) {
        try {
            PublicKey publicKey = certificate.getPublicKey();
            AsymmetricKeyParameter pubkeyParam = PublicKeyFactory.createKey(publicKey.getEncoded());
            byte[] encoded = RSAUtils.pubKey2Bytes_RawKey(((RSAKeyParameters) pubkeyParam));
            return DefaultCryptoEncoding.encodePubKey(ALGORITHM, encoded);
        } catch (IOException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Override
    public PrivKey parsePrivKey(String privateKey) {
        try (PEMParser pemParser = new PEMParser(new StringReader(privateKey))) {
            Object object = pemParser.readObject();
            PrivateKeyInfo pemKeyPair;
            if (object instanceof PrivateKeyInfo) {
                pemKeyPair = (PrivateKeyInfo) object;
            } else {
                pemKeyPair = ((PEMKeyPair) object).getPrivateKeyInfo();
            }
            AsymmetricKeyParameter privkeyParam = PrivateKeyFactory.createKey(pemKeyPair);
            byte[] encoded = RSAUtils.privKey2Bytes_RawKey((RSAPrivateCrtKeyParameters) privkeyParam);
            return DefaultCryptoEncoding.encodePrivKey(ALGORITHM, encoded);
        } catch (Exception e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Override
    public PrivKey parsePrivKey(String privateKey, char[] password) {
        try (PEMParser pemParser = new PEMParser(new StringReader(privateKey))) {
            Object object = pemParser.readObject();
            PEMEncryptedKeyPair ckp = (PEMEncryptedKeyPair) object;
            PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password);
            PrivateKeyInfo pemKeyPair = ckp.decryptKeyPair(decProv).getPrivateKeyInfo();
            AsymmetricKeyParameter privkeyParam = PrivateKeyFactory.createKey(pemKeyPair);
            byte[] encoded = RSAUtils.privKey2Bytes_RawKey((RSAPrivateCrtKeyParameters) privkeyParam);
            return DefaultCryptoEncoding.encodePrivKey(ALGORITHM, encoded);
        } catch (Exception e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Override
    public PubKey resolvePubKey(PKCS10CertificationRequest csr) {
        try {
            SubjectPublicKeyInfo pkInfo = csr.getSubjectPublicKeyInfo();
            AsymmetricKeyParameter pubkeyParam = PublicKeyFactory.createKey(pkInfo);
            byte[] encoded = RSAUtils.pubKey2Bytes_RawKey(((RSAKeyParameters) pubkeyParam));
            return DefaultCryptoEncoding.encodePubKey(ALGORITHM, encoded);
        } catch (IOException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Override
    public PrivateKey retrievePrivateKey(PrivKey privKey) {
        try {
            RSAPrivateCrtKeyParameters pk = RSAUtils.bytes2PrivKey_RawKey(privKey.getRawKeyBytes());
            return KeyFactory.getInstance("RSA").generatePrivate(
                    new RSAPrivateCrtKeySpec(pk.getModulus(), pk.getPublicExponent(),
                            pk.getExponent(), pk.getP(), pk.getQ(),
                            pk.getDP(), pk.getDQ(), pk.getQInv()));
        } catch (Exception e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Override
    public PrivateKey retrievePrivateKey(PrivKey privKey, PubKey pubKey) {
        try {
            RSAPrivateCrtKeyParameters pk = RSAUtils.bytes2PrivKey_RawKey(privKey.getRawKeyBytes());
            return KeyFactory.getInstance("RSA").generatePrivate(
                    new RSAPrivateCrtKeySpec(pk.getModulus(), pk.getPublicExponent(),
                            pk.getExponent(), pk.getP(), pk.getQ(),
                            pk.getDP(), pk.getDQ(), pk.getQInv()));
        } catch (Exception e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Override
    public PublicKey retrievePublicKey(PubKey pubKey) {
        try {
            RSAKeyParameters pk = RSAUtils.bytes2PubKey_RawKey(pubKey.getRawKeyBytes());
            return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(pk.getModulus(), pk.getExponent()));
        } catch (Exception e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Override
    public CryptoAlgorithm getAlgorithm() {
        return ALGORITHM;
    }

    @Override
    public AsymmetricKeypair generateKeypair() {
        return generateKeypair(new SecureRandom());
    }

    @Override
    public AsymmetricKeypair generateKeypair(byte[] seed) {
        return generateKeypair(new SHA256SecureRandom(seed));
    }

    public AsymmetricKeypair generateKeypair(SecureRandom random) {
        AsymmetricCipherKeyPair keyPair = RSAUtils.generateKeyPair(random);
        RSAKeyParameters pubKeyParams = (RSAKeyParameters) keyPair.getPublic();
        RSAPrivateCrtKeyParameters privKeyParams = (RSAPrivateCrtKeyParameters) keyPair.getPrivate();

        byte[] pubKeyBytes = RSAUtils.pubKey2Bytes_RawKey(pubKeyParams);
        byte[] privKeyBytes = RSAUtils.privKey2Bytes_RawKey(privKeyParams);

        PrivKey privKey = DefaultCryptoEncoding.encodePrivKey(ALGORITHM, privKeyBytes);
        PubKey pubKey = DefaultCryptoEncoding.encodePubKey(ALGORITHM, pubKeyBytes);

        return new AsymmetricKeypair(pubKey, privKey);
    }

    @Override
    public <T extends CryptoBytes> boolean support(Class<T> cryptoDataType, byte[] encodedCryptoBytes) {
        return (SignatureDigest.class == cryptoDataType && supportDigest(encodedCryptoBytes))
                || (PubKey.class == cryptoDataType && supportPubKey(encodedCryptoBytes))
                || (PrivKey.class == cryptoDataType && supportPrivKey(encodedCryptoBytes));
    }


}
