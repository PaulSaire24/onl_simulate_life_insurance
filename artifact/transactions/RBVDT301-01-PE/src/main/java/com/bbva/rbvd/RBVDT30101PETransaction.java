package com.bbva.rbvd;

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
		// TODO - Implementation of business logic
		LOGGER.info("***** RBVDT30101PETransaction START *****");
	}

}
