package org.anonymous.test.tx;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/03/09
 */
public interface TestService {

	void update(String name, String id);

	void update2(String name, String id);

	void updateIn(String name);

	void updateInTx(String name);

	void saveEx(String name);
}
