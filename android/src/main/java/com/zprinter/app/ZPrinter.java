package com.zprinter.app;

import com.getcapacitor.Logger;

public class ZPrinter {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
