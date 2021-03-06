/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.converters.BinarySerializeResponseConverter
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/9/5 下午5:22
 * Description:
 */
package com.jd.blockchain.sdk.converters;

import com.jd.binaryproto.BinaryProtocol;
import com.jd.blockchain.transaction.TxResponseMessage;
import com.jd.httpservice.HttpServiceContext;
import com.jd.httpservice.ResponseConverter;
import com.jd.httpservice.agent.ServiceRequest;

import utils.io.BytesUtils;

import java.io.InputStream;

/**
 *
 * @author shaozhuguang
 * @create 2018/9/5
 * @since 1.0.0
 */

public class BinarySerializeResponseConverter implements ResponseConverter {

    @Override
    public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext)
            throws Exception {
        byte[] serializeBytes = BytesUtils.readBytes(responseStream);
        Object resolvedObj = BinaryProtocol.decode(serializeBytes);
        return resolvedObj;
    }

}