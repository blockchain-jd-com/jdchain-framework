package com.jd.blockchain.ledger.binaryproto;

import com.jd.binaryproto.DataContractAutoRegistrar;
import com.jd.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.ledger.AccountSnapshot;
import com.jd.blockchain.ledger.BlockBody;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.BytesValueList;
import com.jd.blockchain.ledger.ConsensusSettingsUpdateOperation;
import com.jd.blockchain.ledger.ContractCodeDeployOperation;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.ledger.ContractInfo;
import com.jd.blockchain.ledger.ContractStateUpdateOperation;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.DataAccountInfo;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.DigitalSignatureBody;
import com.jd.blockchain.ledger.Event;
import com.jd.blockchain.ledger.EventAccountInfo;
import com.jd.blockchain.ledger.EventAccountRegisterOperation;
import com.jd.blockchain.ledger.EventPublishOperation;
import com.jd.blockchain.ledger.GenesisUser;
import com.jd.blockchain.ledger.HashObject;
import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerDataSnapshot;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerMetadata;
import com.jd.blockchain.ledger.LedgerMetadata_V2;
import com.jd.blockchain.ledger.LedgerSettings;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.LedgerTransactions;
import com.jd.blockchain.ledger.MerkleSnapshot;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.ParticipantRegisterOperation;
import com.jd.blockchain.ledger.ParticipantStateUpdateInfo;
import com.jd.blockchain.ledger.ParticipantStateUpdateOperation;
import com.jd.blockchain.ledger.PrivilegeSet;
import com.jd.blockchain.ledger.RoleInitSettings;
import com.jd.blockchain.ledger.RoleSet;
import com.jd.blockchain.ledger.RolesConfigureOperation;
import com.jd.blockchain.ledger.RootCAUpdateOperation;
import com.jd.blockchain.ledger.SecurityInitSettings;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionResult;
import com.jd.blockchain.ledger.UserAccountHeader;
import com.jd.blockchain.ledger.UserAuthInitSettings;
import com.jd.blockchain.ledger.UserAuthorizeOperation;
import com.jd.blockchain.ledger.UserCAUpdateOperation;
import com.jd.blockchain.ledger.UserInfo;
import com.jd.blockchain.ledger.UserInfoSetOperation;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.UserStateUpdateOperation;

public class LedgerModelDataContractAutoRegistrar implements DataContractAutoRegistrar{

	@Override
	public void initContext(DataContractRegistry registry) {
		DataContractRegistry.register(MerkleSnapshot.class);
		DataContractRegistry.register(BlockchainIdentity.class);
		DataContractRegistry.register(AccountSnapshot.class);
		
		DataContractRegistry.register(DataAccountInfo.class);
		DataContractRegistry.register(ContractInfo.class);
		DataContractRegistry.register(EventAccountInfo.class);
		
		DataContractRegistry.register(BytesValue.class);
		DataContractRegistry.register(BytesValueList.class);
		DataContractRegistry.register(BlockchainIdentity.class);
		DataContractRegistry.register(LedgerBlock.class);
		DataContractRegistry.register(BlockBody.class);
		DataContractRegistry.register(LedgerDataSnapshot.class);
		DataContractRegistry.register(LedgerAdminInfo.class);
		DataContractRegistry.register(TransactionContent.class);
		DataContractRegistry.register(TransactionRequest.class);
		DataContractRegistry.register(TransactionResult.class);
		DataContractRegistry.register(LedgerTransaction.class);
		DataContractRegistry.register(Operation.class);
		DataContractRegistry.register(LedgerInitOperation.class);
		DataContractRegistry.register(UserRegisterOperation.class);
		DataContractRegistry.register(UserInfoSetOperation.class);
		DataContractRegistry.register(UserInfoSetOperation.KVEntry.class);
		DataContractRegistry.register(DataAccountRegisterOperation.class);
		DataContractRegistry.register(DataAccountKVSetOperation.class);
		DataContractRegistry.register(DataAccountKVSetOperation.KVWriteEntry.class);
		DataContractRegistry.register(ContractCodeDeployOperation.class);
		DataContractRegistry.register(ContractEventSendOperation.class);
		DataContractRegistry.register(ParticipantRegisterOperation.class);
		DataContractRegistry.register(ParticipantStateUpdateOperation.class);
		DataContractRegistry.register(TransactionResponse.class);
		DataContractRegistry.register(OperationResult.class);
		DataContractRegistry.register(RolesConfigureOperation.class);
		DataContractRegistry.register(RolesConfigureOperation.RolePrivilegeEntry.class);
		DataContractRegistry.register(UserAuthorizeOperation.class);
		DataContractRegistry.register(UserAuthorizeOperation.UserRolesEntry.class);
		DataContractRegistry.register(EventAccountRegisterOperation.class);
		DataContractRegistry.register(EventPublishOperation.class);
		DataContractRegistry.register(EventPublishOperation.EventEntry.class);
		DataContractRegistry.register(ConsensusSettingsUpdateOperation.class);
		DataContractRegistry.register(PrivilegeSet.class);
		DataContractRegistry.register(RoleSet.class);
		DataContractRegistry.register(SecurityInitSettings.class);
		DataContractRegistry.register(RoleInitSettings.class);
		DataContractRegistry.register(UserAuthInitSettings.class);
		DataContractRegistry.register(Event.class);
		DataContractRegistry.register(LedgerMetadata.class);
		DataContractRegistry.register(LedgerMetadata_V2.class);
		DataContractRegistry.register(LedgerInitSetting.class);
		DataContractRegistry.register(LedgerSettings.class);
		DataContractRegistry.register(ParticipantNode.class);
		DataContractRegistry.register(ParticipantStateUpdateInfo.class);
		DataContractRegistry.register(CryptoSetting.class);
		DataContractRegistry.register(CryptoProvider.class);
		DataContractRegistry.register(UserAccountHeader.class);
		DataContractRegistry.register(UserInfo.class);
		DataContractRegistry.register(HashObject.class);
		DataContractRegistry.register(CryptoAlgorithm.class);
		DataContractRegistry.register(DigitalSignature.class);
		DataContractRegistry.register(DigitalSignatureBody.class);
		DataContractRegistry.register(LedgerTransactions.class);
		DataContractRegistry.register(UserCAUpdateOperation.class);
		DataContractRegistry.register(RootCAUpdateOperation.class);
		DataContractRegistry.register(UserStateUpdateOperation.class);
		DataContractRegistry.register(GenesisUser.class);
		DataContractRegistry.register(ContractStateUpdateOperation.class);
	}
}
