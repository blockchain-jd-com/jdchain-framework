package com.jd.blockchain.ledger;

import com.jd.blockchain.ca.CertificateRole;
import com.jd.blockchain.ca.CertificateUtils;
import com.jd.blockchain.consts.Global;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PubKey;
import utils.Bytes;
import utils.PropertiesUtils;
import utils.StringUtils;
import utils.codec.HexUtils;
import utils.io.FileUtils;
import utils.net.NetworkAddress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LedgerInitProperties implements Serializable {

	private static final long serialVersionUID = 6261483113521649870L;

	// 账本种子；
	public static final String LEDGER_SEED = "ledger.seed";

	// 证书模式，默认 false
	public static final String IDENTITY_MODE = "identity-mode";

	// 根证书路径，CA_MODE 为 true 时，此选项不能为空，支持多个，半角逗号相隔
	public static final String CA_PATH = "root-ca-path";

	// 账本名称
	public static final String LEDGER_NAME = "ledger.name";

	// 声明的账本建立时间；
	public static final String CREATED_TIME = "created-time";
	// 创建时间的格式；
	public static final String CREATED_TIME_FORMAT = Global.DEFAULT_TIME_FORMAT;

	// 合约运行超时配置
	public static final String CONTRACT_TIMEOUT = "contract.timeout";
	// 合约运行超时默认值：1分钟
	public static final int DEFAULT_CONTRACT_TIMEOUT = 60000;
	// 合约运行最大相互调用栈深
	public static final String MAX_STACK_DEPTH = "contract.max-stack-depth";
	public static final int DEFAULT_MAX_STACK_DEPTH = 100;

	// 角色清单；
	public static final String ROLES = "security.roles";
	// 角色的账本权限；用角色名称替代占位符；
	public static final String ROLE_LEDGER_PRIVILEGES_PATTERN = "security.role.%s.ledger-privileges";
	// 角色的交易权限；用角色名称替代占位符；
	public static final String ROLE_TX_PRIVILEGES_PATTERN = "security.role.%s.tx-privileges";

	// 共识参与方的个数，后续以 part.id 分别标识每一个参与方的配置；
	public static final String PART_COUNT = "cons_parti.count";
	// 共识参与方的名称的模式；
	public static final String PART_ID_PATTERN = "cons_parti.%s";
	// 参与方的名称；
	public static final String PART_NAME = "name";
	// 参与方的公钥文件路径；
	public static final String PART_PUBKEY_PATH = "pubkey-path";
	// 参与方的公钥文件路径；
	public static final String PART_PUBKEY = "pubkey";
	// 参与方证书文件路径
	public static final String PART_CA_PATH = "ca-path";
	// 参与方的角色清单；
	public static final String PART_ROLES = "roles";
	// 参与方的角色权限策略；
	public static final String PART_ROLES_POLICY = "roles-policy";

	// 共识参与方的账本初始服务的主机；
	public static final String PART_INITIALIZER_HOST = "initializer.host";
	// 共识参与方的账本初始服务的端口；
	public static final String PART_INITIALIZER_PORT = "initializer.port";
	// 共识参与方的账本初始服务是否开启安全连接；
	public static final String PART_INITIALIZER_SECURE = "initializer.secure";
	// 共识参与方的状态；
	public static final String PART_INITIALIZER_STATE = "initializer.state";

	// 共识服务的参数配置；必须；
	public static final String CONSENSUS_CONFIG = "consensus.conf";

	// 共识服务提供者；必须；
	public static final String CONSENSUS_SERVICE_PROVIDER = "consensus.service-provider";

	// 密码服务提供者列表，以英文逗点“,”分隔；必须；
	public static final String CRYPTO_SERVICE_PROVIDERS = "crypto.service-providers";
	// 从存储中加载账本数据时，是否校验哈希；可选；
	public static final String CRYPTO_VRIFY_HASH = "crypto.verify-hash";
	// 哈希算法；
	public static final String CRYPTO_HASH_ALGORITHM = "crypto.hash-algorithm";

	public static final String LEDGER_DATA_STRUCTURE = "ledger.data.structure";

	public static final String LEDGER_DATA_WRITE_MYSQL = "ledger.data.write.mysql";

	public static final String CRYPTO_SERVICE_PROVIDERS_SPLITTER = ",";

	private byte[] ledgerSeed;

	private IdentityMode identityMode;

	private String[] ledgerCertificates;

	private String ledgerName;

	private RoleInitData[] roles;

	private List<ParticipantProperties> consensusParticipants = new ArrayList<>();

	private GenesisUser[] genesisUsers;

	private String consensusProvider;

	private Properties consensusConfig;

	private CryptoProperties cryptoProperties = new CryptoProperties();

	private long createdTime;

	private LedgerDataStructure ledgerDataStructure;

	private boolean ledgerDataWriteMysql;

	private long contractTimeout;

	private int maxStackDepth;

	public byte[] getLedgerSeed() {
		return ledgerSeed.clone();
	}

	public IdentityMode getIdentityMode() {
		return identityMode;
	}

	public String[] getLedgerCertificates() {
		return ledgerCertificates;
	}

	public GenesisUser[] getGenesisUsers() {
		return genesisUsers;
	}

	public void setGenesisUsers(GenesisUser[] genesisUsers) {
		this.genesisUsers = genesisUsers;
	}

	public String getLedgerName() {
		return ledgerName;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public LedgerDataStructure getLedgerDataStructure() {
		return ledgerDataStructure;
	}

	public boolean isLedgerDataWriteMysql() {
		return ledgerDataWriteMysql;
	}

	public long getContractTimeout() {
		return contractTimeout;
	}

	public int getMaxStackDepth() {
		return maxStackDepth;
	}

	public Properties getConsensusConfig() {
		return consensusConfig;
	}

	public String getConsensusProvider() {
		return consensusProvider;
	}

	public int getConsensusParticipantCount() {
		return consensusParticipants.size();
	}

	public List<ParticipantProperties> getConsensusParticipants() {
		return consensusParticipants;
	}

	public ParticipantNode[] getConsensusParticipantNodes() {
		if (consensusParticipants.isEmpty()) {
			return null;
		}
		ParticipantNode[] participantNodes = new ParticipantNode[consensusParticipants.size()];
		return consensusParticipants.toArray(participantNodes);
	}

	public CryptoProperties getCryptoProperties() {
		return cryptoProperties;
	}

	public void setCryptoProperties(CryptoProperties cryptoProperties) {
		if (cryptoProperties == null) {
			cryptoProperties = new CryptoProperties();
		}
		this.cryptoProperties = cryptoProperties;
	}

	/**
	 * 返回参与者；
	 *
	 * @param id 从 1 开始； 小于等于 {@link #getConsensusParticipantCount()};
	 * @return
	 */
	public ParticipantProperties getConsensusParticipant(int id) {
		for (ParticipantProperties p : consensusParticipants) {
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}

	/**
	 * 私有的构造器；
	 *
	 * @param ledgerSeed
	 */
	private LedgerInitProperties(byte[] ledgerSeed) {
		this.ledgerSeed = ledgerSeed;
	}

	public void addConsensusParticipant(ParticipantProperties participant) {
		consensusParticipants.add(participant);
	}

	private static String getKeyOfParticipant(int partId, String partPropKey) {
		String partAddrStr = String.format(PART_ID_PATTERN, partId);
		return String.format("%s.%s", partAddrStr, partPropKey);
	}

	public static LedgerInitProperties createDefault(byte[] ledgerSeed, String ledgerName, Date createdTime, LedgerDataStructure ledgerDataStructure,
													 Properties consensusConfig, CryptoProperties cryptoProperties) {
		LedgerInitProperties initProps = new LedgerInitProperties(ledgerSeed);
		initProps.ledgerName = ledgerName;
		initProps.createdTime = createdTime.getTime();
		initProps.ledgerDataStructure = ledgerDataStructure;
		initProps.consensusProvider = "com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider";
		initProps.consensusConfig = consensusConfig;
		initProps.cryptoProperties = cryptoProperties.clone();
		return initProps;
	}

	public static LedgerInitProperties resolve(String initSettingFile) {
		Properties props = FileUtils.readProperties(initSettingFile, "UTF-8");
		File realFile = new File(initSettingFile);
		return resolve(realFile.getParentFile().getPath(), props);
	}

	public static LedgerInitProperties resolve(InputStream in) {
		Properties props = FileUtils.readProperties(in, "UTF-8");
		return resolve(props);
	}

	public static LedgerInitProperties resolve(Properties props) {
		return resolve(null, props);
	}

	/**
	 * 从属性表解析账本初始化参数；
	 *
	 * @param baseDirectory 基础路径；属性中涉及文件位置的相对路径以此参数指定的目录为父目录；
	 * @param props         要解析的属性表；
	 * @return
	 */
	public static LedgerInitProperties resolve(String baseDirectory, Properties props) {
		String hexLedgerSeed = PropertiesUtils.getRequiredProperty(props, LEDGER_SEED).replace("-", "");
		byte[] ledgerSeed = HexUtils.decode(hexLedgerSeed);
		LedgerInitProperties initProps = new LedgerInitProperties(ledgerSeed);

		// 证书配置
		String identityMode = PropertiesUtils.getOptionalProperty(props, IDENTITY_MODE, IdentityMode.KEYPAIR.name());
		initProps.identityMode = IdentityMode.valueOf(identityMode);
		X509Certificate[] ledgerCerts = null;
		if (initProps.identityMode == IdentityMode.CA) {
			// 根证书
			String[] ledgerCAPaths = PropertiesUtils.getRequiredProperty(props, CA_PATH).split(",");
			if (ledgerCAPaths.length == 0) {
				throw new LedgerInitException("root-ca-path is empty");
			}
			ledgerCerts = new X509Certificate[ledgerCAPaths.length];
			String[] ledgersCAs = new String[ledgerCAPaths.length];
			for (int i = 0; i < ledgerCAPaths.length; i++) {
				ledgersCAs[i] = FileUtils.readText(ledgerCAPaths[i]);
				ledgerCerts[i] = CertificateUtils.parseCertificate(ledgersCAs[i]);
				// 时间有效性校验
				CertificateUtils.checkValidity(ledgerCerts[i]);
				// 证书类型校验
				CertificateUtils.checkCACertificate(ledgerCerts[i]);
			}
			initProps.ledgerCertificates = ledgersCAs;
		}

		// 解析账本信息；
		// 账本名称
		String ledgerName = PropertiesUtils.getRequiredProperty(props, LEDGER_NAME);
		initProps.ledgerName = ledgerName;

		// 创建时间；
		String strCreatedTime = PropertiesUtils.getRequiredProperty(props, CREATED_TIME);
		try {
			initProps.createdTime = new SimpleDateFormat(CREATED_TIME_FORMAT).parse(strCreatedTime).getTime();
		} catch (ParseException ex) {
			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
		// 合约运行时参数
		initProps.contractTimeout = PropertiesUtils.getIntOptional(props, CONTRACT_TIMEOUT, DEFAULT_CONTRACT_TIMEOUT);
		initProps.maxStackDepth = PropertiesUtils.getIntOptional(props, MAX_STACK_DEPTH, DEFAULT_MAX_STACK_DEPTH);

		String dataStructure = PropertiesUtils.getOptionalProperty(props, LEDGER_DATA_STRUCTURE, LedgerDataStructure.MERKLE_TREE.name());
		initProps.ledgerDataStructure = LedgerDataStructure.valueOf(dataStructure);

		boolean writeMysql = PropertiesUtils.getBooleanOptional(props, LEDGER_DATA_WRITE_MYSQL, false);
		initProps.ledgerDataWriteMysql = writeMysql;

		// 解析角色清单；
		String strRoleNames = PropertiesUtils.getOptionalProperty(props, ROLES);
		String[] roles = StringUtils.splitToArray(strRoleNames, ",");

		Map<String, RoleInitData> rolesInitSettingMap = new TreeMap<String, RoleInitData>();
		// 解析角色权限表；
		for (String role : roles) {
			String ledgerPrivilegeKey = getKeyOfRoleLedgerPrivilege(role);
			String strLedgerPermissions = PropertiesUtils.getOptionalProperty(props, ledgerPrivilegeKey);
			LedgerPermission[] ledgerPermissions = resolveLedgerPermissions(strLedgerPermissions);

			String txPrivilegeKey = getKeyOfRoleTxPrivilege(role);
			String strTxPermissions = PropertiesUtils.getOptionalProperty(props, txPrivilegeKey);
			TransactionPermission[] txPermissions = resolveTransactionPermissions(strTxPermissions);

			if (ledgerPermissions.length > 0 || txPermissions.length > 0) {
				RoleInitData rolesSettings = new RoleInitData(role, ledgerPermissions, txPermissions);
				rolesInitSettingMap.put(role, rolesSettings);
			}
		}
		RoleInitData[] rolesInitDatas = rolesInitSettingMap.values()
				.toArray(new RoleInitData[rolesInitSettingMap.size()]);
		initProps.setRoles(rolesInitDatas);

		// 解析共识相关的属性；
		initProps.consensusProvider = PropertiesUtils.getRequiredProperty(props, CONSENSUS_SERVICE_PROVIDER);
		ConsensusTypeEnum consensusType = ConsensusTypeEnum.of(initProps.consensusProvider);
		if (consensusType.equals(ConsensusTypeEnum.UNKNOWN)) {
			throw new IllegalArgumentException(String.format("Property[%s] is unsupported!", CONSENSUS_SERVICE_PROVIDER));
		}
		String consensusConfigFilePath = PropertiesUtils.getRequiredProperty(props, CONSENSUS_CONFIG);
		try {
			initProps.consensusConfig = FileUtils.readPropertiesResouce(consensusConfigFilePath, baseDirectory);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(
					String.format("Consensus config file[%s] doesn't exist! ", consensusConfigFilePath), e);
		}

		// 解析密码提供者列表；
		String cryptoProviderNames = PropertiesUtils.getProperty(props, CRYPTO_SERVICE_PROVIDERS, true);
		String[] cryptoProviders = cryptoProviderNames.split(CRYPTO_SERVICE_PROVIDERS_SPLITTER);
		for (int i = 0; i < cryptoProviders.length; i++) {
			cryptoProviders[i] = cryptoProviders[i].trim();
		}
		initProps.cryptoProperties.setProviders(cryptoProviders);
		// 哈希校验选项；
		boolean verifyHash = PropertiesUtils.getBooleanOptional(props, CRYPTO_VRIFY_HASH, false);
		initProps.cryptoProperties.setVerifyHash(verifyHash);
		// 哈希算法；
		String hashAlgorithm = PropertiesUtils.getOptionalProperty(props, CRYPTO_HASH_ALGORITHM);
		initProps.cryptoProperties.setHashAlgorithm(hashAlgorithm);

		// 解析参与方节点列表；
		int partCount = getInt(PropertiesUtils.getRequiredProperty(props, PART_COUNT));
		if (partCount < 0) {
			throw new IllegalArgumentException(String.format("Property[%s] is negative!", PART_COUNT));
		}
		GenesisUser[] genesisUsers = new GenesisUserConfig[partCount];
		int consensusNodeCount = 0;
		for (int i = 0; i < partCount; i++) {
			ParticipantProperties parti = new ParticipantProperties();

			parti.setId(i);

			String nameKey = getKeyOfParticipant(i, PART_NAME);
			parti.setName(PropertiesUtils.getRequiredProperty(props, nameKey));

			String pubkeyPathKey = getKeyOfParticipant(i, PART_PUBKEY_PATH);
			String pubkeyKey = getKeyOfParticipant(i, PART_PUBKEY);
			String partCAPathKey = getKeyOfParticipant(i, PART_CA_PATH);
			PubKey pubKey;
			String ca = null;
			boolean isGw = false;
			if (initProps.identityMode == IdentityMode.CA) {
				ca = FileUtils.readText(PropertiesUtils.getRequiredProperty(props, partCAPathKey));
				X509Certificate cert = CertificateUtils.parseCertificate(ca);
				CertificateUtils.checkValidity(cert);
				// CA模式下，初始化的节点证书必须是 PEER 和 GW 角色类型
				CertificateUtils.checkCertificateRolesAny(cert, CertificateRole.PEER, CertificateRole.GW);
				isGw = CertificateUtils.checkCertificateRolesAnyNoException(cert, CertificateRole.GW);
				CertificateUtils.verifyAny(cert, ledgerCerts);
				pubKey = CertificateUtils.resolvePubKey(cert);
			} else {
				String base58PubKey = PropertiesUtils.getProperty(props, pubkeyKey, false);
				String pubkeyPath = PropertiesUtils.getProperty(props, pubkeyPathKey, false);
				String pCA = PropertiesUtils.getProperty(props, partCAPathKey, false);
				if (base58PubKey != null) {
					pubKey = KeyGenUtils.decodePubKey(base58PubKey);
				} else if (pubkeyPath != null) {
					pubKey = KeyGenUtils.readPubKey(pubkeyPath);
				} else if (pCA != null) {
					pubKey = CertificateUtils.resolvePubKey(CertificateUtils.parseCertificate(FileUtils.readText(pCA)));
				} else {
					throw new IllegalArgumentException(
							String.format("Property[%s] and property[%s] are all empty!", pubkeyKey, pubkeyPathKey));
				}
			}
			parti.setPubKey(pubKey);

			// 解析参与方的角色权限配置；
			String partiRolesKey = getKeyOfParticipant(i, PART_ROLES);
			String strPartiRoles = PropertiesUtils.getOptionalProperty(props, partiRolesKey);
			String[] partiRoles = StringUtils.splitToArray(strPartiRoles, ",");

			String partiRolePolicyKey = getKeyOfParticipant(i, PART_ROLES_POLICY);
			String strPartiPolicy = PropertiesUtils.getOptionalProperty(props, partiRolePolicyKey);
			RolesPolicy policy = strPartiPolicy == null ? RolesPolicy.UNION
					: RolesPolicy.valueOf(strPartiPolicy.trim());
			policy = policy == null ? RolesPolicy.UNION : policy;

			if (!isGw) {
				consensusNodeCount++;
				// 解析参与方的网络配置参数；
				String initializerHostKey = getKeyOfParticipant(i, PART_INITIALIZER_HOST);
				String initializerHost = PropertiesUtils.getRequiredProperty(props, initializerHostKey);

				String initializerPortKey = getKeyOfParticipant(i, PART_INITIALIZER_PORT);
				int initializerPort = getInt(PropertiesUtils.getRequiredProperty(props, initializerPortKey));

				String initializerSecureKey = getKeyOfParticipant(i, PART_INITIALIZER_SECURE);
				boolean initializerSecure = Boolean
						.parseBoolean(PropertiesUtils.getRequiredProperty(props, initializerSecureKey));
				NetworkAddress initializerAddress = new NetworkAddress(initializerHost, initializerPort, initializerSecure);
				parti.setInitializerAddress(initializerAddress);
			} else {
				parti.setInitializerAddress(new NetworkAddress("127.0.0.1", 8080));
			}
			parti.setParticipantNodeState(isGw ? ParticipantNodeState.READY : ParticipantNodeState.CONSENSUS);
			initProps.addConsensusParticipant(parti);
			genesisUsers[i] = new GenesisUserConfig(pubKey, ca, partiRoles, policy);
		}
		if (partCount < consensusType.getMinimalNodeSize()) {
			throw new IllegalArgumentException(String.format("Consensus peer nodes size [%s] is less than [%s]!", consensusNodeCount, consensusType.getMinimalNodeSize()));
		}
		initProps.setGenesisUsers(genesisUsers);

		return initProps;
	}

	private static TransactionPermission[] resolveTransactionPermissions(String strTxPermissions) {
		String[] strPermissions = StringUtils.splitToArray(strTxPermissions, ",");
		List<TransactionPermission> permissions = new ArrayList<TransactionPermission>();
		if (strPermissions != null) {
			for (String pm : strPermissions) {
				TransactionPermission permission = TransactionPermission.valueOf(pm);
				if (permission != null) {
					permissions.add(permission);
				}
			}
		}
		return permissions.toArray(new TransactionPermission[permissions.size()]);
	}

	private static LedgerPermission[] resolveLedgerPermissions(String strLedgerPermissions) {
		String[] strPermissions = StringUtils.splitToArray(strLedgerPermissions, ",");
		List<LedgerPermission> permissions = new ArrayList<LedgerPermission>();
		if (strPermissions != null) {
			for (String pm : strPermissions) {
				LedgerPermission permission = LedgerPermission.valueOf(pm);
				if (permission != null) {
					permissions.add(permission);
				}
			}
		}
		return permissions.toArray(new LedgerPermission[permissions.size()]);
	}

	private static String getKeyOfRoleLedgerPrivilege(String role) {
		return String.format(ROLE_LEDGER_PRIVILEGES_PATTERN, role);
	}

	private static String getKeyOfRoleTxPrivilege(String role) {
		return String.format(ROLE_TX_PRIVILEGES_PATTERN, role);
	}

	private static int getInt(String strInt) {
		return Integer.parseInt(strInt.trim());
	}

	public RoleInitData[] getRoles() {
		return roles;
	}

	public void setRoles(RoleInitData[] roles) {
		this.roles = roles;
	}

	public static class CryptoProperties implements Serializable {

		private static final long serialVersionUID = -2464539697473908124L;

		private String[] providers;

		private boolean verifyHash;

		private String hashAlgorithm;

		public String[] getProviders() {
			return providers;
		}

		public void setProviders(String[] providers) {
			this.providers = providers;
		}

		public boolean isVerifyHash() {
			return verifyHash;
		}

		public void setVerifyHash(boolean verifyHash) {
			this.verifyHash = verifyHash;
		}

		public String getHashAlgorithm() {
			return hashAlgorithm;
		}

		public void setHashAlgorithm(String hashAlgorithm) {
			this.hashAlgorithm = hashAlgorithm;
		}

		@Override
		protected CryptoProperties clone() {
			CryptoProperties cryptoProperties = new CryptoProperties();
			cryptoProperties.hashAlgorithm = hashAlgorithm;
			cryptoProperties.providers = providers.clone();
			cryptoProperties.verifyHash = verifyHash;
			return cryptoProperties;
		}
	}

	/**
	 * 参与方配置信息；
	 *
	 * @author huanghaiquan
	 */
	public static class ParticipantProperties implements ParticipantNode, Serializable {

		private static final long serialVersionUID = 7038013516766733725L;

		private int id;

		private Bytes address;

		private String name;

		private PubKey pubKey;

		private ParticipantNodeState participantNodeState;

		private NetworkAddress initializerAddress;

		@Override
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Override
		public Bytes getAddress() {
			return address;
		}

		@Override
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public ParticipantNodeState getParticipantNodeState() {
			return participantNodeState;
		}

		public void setParticipantNodeState(ParticipantNodeState participantNodeState) {
			this.participantNodeState = participantNodeState;
		}

		public NetworkAddress getInitializerAddress() {
			return initializerAddress;
		}

		public void setInitializerAddress(NetworkAddress initializerAddress) {
			this.initializerAddress = initializerAddress;
		}

		@Override
		public PubKey getPubKey() {
			return pubKey;
		}

		public void setPubKey(PubKey pubKey) {
			this.pubKey = pubKey;
			this.address = AddressEncoding.generateAddress(pubKey);
		}
	}

}
