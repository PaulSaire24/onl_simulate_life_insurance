package com.bbva.rbvd.lib.r302.business;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;

public interface IInsrDynamicLifeBusiness {

    PayloadStore doDynamicLife( PayloadConfig payloadConfig);

}
