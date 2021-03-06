package com.jd.blockchain.sdk.converters;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.httpservice.HttpServiceContext;
import com.jd.httpservice.ResponseConverter;
import com.jd.httpservice.agent.ServiceRequest;
import com.jd.httpservice.converters.JsonResponseConverter;
import com.jd.httpservice.utils.agent.WebServiceException;
import com.jd.httpservice.utils.web.WebResponse;

import utils.codec.Base58Utils;

/**
 * Created by zhangshuang3 on 2018/10/17.
 */
public class HashDigestsResponseConverter implements ResponseConverter {

    private JsonResponseConverter jsonConverter = new JsonResponseConverter(WebResponse.class);

    @Override
    public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) throws Exception {
        WebResponse response = (WebResponse) jsonConverter.getResponse(request, responseStream, null);
        if (response == null) {
            return null;
        }
        if (response.getError() != null) {
            throw new WebServiceException(response.getError().getErrorCode(), response.getError().getErrorMessage());
        }
        if (response.getData() == null) {
            return null;
        }


//        byte[] serializeBytes = BytesUtils.readBytes(responseStream);
//        String jsonChar = new String(serializeBytes, "UTF-8");
//        JSONArray jsonArray = JSON.parseArray(jsonChar);
//        List<HashDigest> hashDigests = new ArrayList<>();
//        for (Object obj : jsonArray) {
//            if (obj instanceof JSONObject) {
//                String base58Str = ((JSONObject)obj).getString("value");
//                hashDigests.add(new HashDigest(Base58Utils.decode(base58Str)));
//            }
//        }
        return deserialize(response.getData());
    }

    private Object deserialize(Object object) {
        List<HashDigest> hashDigests = new ArrayList<>();
        if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray)object;
            for (Object obj : jsonArray) {
                if (obj instanceof Map) {
                    Map<String, String> objMap = (Map)obj;
                    String base58Str = objMap.get("value");
                    hashDigests.add(Crypto.resolveAsHashDigest(Base58Utils.decode(base58Str)));
                } else {
                    hashDigests.add(Crypto.resolveAsHashDigest(Base58Utils.decode(obj.toString())));

                }
            }
        }
        return hashDigests.toArray(new HashDigest[hashDigests.size()]);
    }
}
