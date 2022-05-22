package org.anonymous.boot.test.model;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
public class Company {
	private String name;
	private Employee managingDirector;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Employee getManagingDirector() {
		return this.managingDirector;
	}

	public void setManagingDirector(Employee managingDirector) {
		this.managingDirector = managingDirector;
	}

	@Override
	public String toString() {
		return "Company{" +
				"name='" + name + '\'' +
				", managingDirector=" + managingDirector +
				'}';
	}
}
