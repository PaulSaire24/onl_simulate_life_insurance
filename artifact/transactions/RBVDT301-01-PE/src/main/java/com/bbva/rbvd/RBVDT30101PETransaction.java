package com.bbva.rbvd;

import com.bbva.rbvd.lib.r302.RBVDR302;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trx done for life simulation
 *
 */
public class RBVDT30101PETransaction extends AbstractRBVDT30101PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDT30101PETransaction.class);

	/**
	 * The execute method...
	 */
	@Override
	public void execute() {
		RBVDR302 rbvdR302 = this.getServiceLibrary(RBVDR302.class);
		// TODO - Implementation of business logic
		LOGGER.info("***** RBVDT30101PETransaction START *****");
	}

}
