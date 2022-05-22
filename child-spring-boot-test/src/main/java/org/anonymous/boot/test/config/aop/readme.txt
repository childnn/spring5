how to use LockMixin/LockMixinAdvisor/Lockable ?

	It is questionable whether it is advisable (no pun intended) to modify advice on a business object in production,
	although there are, no doubt, legitimate usage cases.
	However, it can be very useful in development (for example, in tests).
	We have sometimes found it very useful to be able to add test code in the form of an interceptor or other advice,
	getting inside a method invocation that we want to test.
	(For example, the advice can get inside a transaction created for that method,
	perhaps to run SQL to check that a database was correctly updated, before marking the transaction for roll back.)

	Depending on how you created the proxy, you can usually set a frozen flag.
	In that case, the Advised isFrozen() method returns true,
	and any attempts to modify advice through addition or removal results in an AopConfigException.
	The ability to freeze the state of an advised object is useful in some cases
	(for example, to prevent calling code removing a security interceptor).

