package com.winterfarmer.virgo.restapi.doc;

import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangtianhang on 15/4/26.
 */
public class ErrorPageGenerator extends DocGenerator {
    @Override
    public Map<String, String> prepare() throws Throwable {
        Map<String, String> rstMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        for (RestExceptionFactor ref : RestExceptionFactor.values()) {
            sb.append(ref.getHttpErrorCode()).append("|").append(ref.getInnerErrorCode()).append("|").append(ref.getReason()).append(NEWLINE);
        }

        rstMap.put("{errorCodeTab}", sb.toString());
        return rstMap;
    }

    @Override
    public String getTemplatePath() {
        return templateBase + SEP + "ErrorDesc.markdown";
    }

    @Override
    public String getDocPath() {
        return defaultDocBase + SEP + "base" + SEP + "error_code_info.md";
    }

    @Override
    public void postActions() {

    }
}
