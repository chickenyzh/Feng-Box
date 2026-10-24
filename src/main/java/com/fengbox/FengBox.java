/*
 * 
 */
package com.fengbox;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FengBox
implements ModInitializer {
    public static final String MOD_ID = "feng_box";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"feng_box");

    public void onInitialize() {
        LOGGER.info("Feng Box mod loaded!");
    }
}

