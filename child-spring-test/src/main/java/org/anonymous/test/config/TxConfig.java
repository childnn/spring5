package org.anonymous.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @see org.springframework.transaction.support.TransactionSynchronizationManager#isActualTransactionActive()
 * @since 2022/04/03
 */
@Configuration
public class TxConfig {

	@Bean
	PlatformTransactionManager transactionManager() {
		return new AbstractPlatformTransactionManager() {

			@Override
			protected Object doGetTransaction() throws TransactionException {
				return null;
			}

			@Override
			protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {

			}

			@Override
			protected void doCommit(DefaultTransactionStatus status) throws TransactionException {

			}

			@Override
			protected void doRollback(DefaultTransactionStatus status) throws TransactionException {

			}
		};
	}


}
