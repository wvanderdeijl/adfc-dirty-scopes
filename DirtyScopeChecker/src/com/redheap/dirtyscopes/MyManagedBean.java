package com.redheap.dirtyscopes;

import java.io.Serializable;

import oracle.adf.controller.ControllerContext;
import oracle.adf.share.ADFContext;
import oracle.adf.share.logging.ADFLogger;

public class MyManagedBean implements Serializable {

    private int number = 0;
    private static final ADFLogger logger = ADFLogger.createADFLogger(MyManagedBean.class);

    public String emptyAction() {
        logger.info("performing empty action that doesn't change any state");
        return null;
    }

    public String changeState() {
        logger.info("changing internal state of viewScope bean without marking scope as dirty");
        number++;
        return null;
    }

    public String changeStateAndMark() {
        logger.info("changing internal state of viewScope bean");
        number++;
        logger.info("marking viewScope dirty");
        ControllerContext.getInstance().markScopeDirty(ADFContext.getCurrent().getViewScope());
        return null;
    }

    public int getState() {
        return number;
    }

}
