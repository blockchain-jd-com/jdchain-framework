package com.jd.blockchain.consensus;

import java.util.Properties;

public interface ConsensusSettingsBuilder {

	/**
	 * 从属性表中解析生成共识网络的参数配置；
	 * 
	 * @param props    属性表；
	 * @param replicas 参与方列表；<br>
	 * @return
	 */
	ConsensusViewSettings createSettings(Properties props, Replica[] replicas);

	/**
	 * 创建共识配置属性的模板；
	 * 
	 * @return
	 */
	Properties createPropertiesTemplate();

	/**
	 * 从共识网络的环境配置解析成属性列表
	 *
	 * @param settings 共识环境；<br>
	 * @param props    属性表；
	 * @return
	 */
	Properties convertToProperties(ConsensusViewSettings settings);

	/**
	 * 更新配置加入新的参与方；
	 * 
	 * @param viewSettings 现有的配置；
	 * @param replica  要加入的新参与方；
	 * @return
	 */
	ConsensusViewSettings addReplicaSetting(ConsensusViewSettings viewSettings, Replica replica);

	/**
	 * 根据属性信息对旧的共识环境进行更新 如果oldConsensusSettings是代理对象，需要在方法内部建立新的对象返回；
	 *
	 * @param oldConsensusSettings 旧的共识环境；<br>
	 * @param props                新添加的属性表；
	 * @return
	 */
	ConsensusViewSettings updateSettings(ConsensusViewSettings oldConsensusSettings, Properties newProps);
}
