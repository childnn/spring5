package org.anonymous.boot.test.ioc;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/29
 */
public class IoCTest {

	public static void main(String[] args) {

		//
		A a1 = new A();
		A a2 = new A();



		A bean1 = IoC.getBean(A.class.getName());
		A bean2 = IoC.getBean(A.class.getName());
		System.out.println("bean2 = " + bean2);
		System.out.println(bean1 == bean2);

		// BeanFactory

		// A a = new A();
		// System.out.println("a = " + a);
		//
		// Object o = Proxy.newProxyInstance(a.getClass().getClassLoader(), new Class[]{InF.class},
		// 		new InvocationHandler() {
		// 			@Override
		// 			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//
		// 				System.out.println("proxy = " + proxy.getClass());
		//
		// 				Object result = method.invoke(a, args);
		//
		// 				System.out.println("method = " + method);
		//
		// 				return result;
		// 			}
		// 		});
		//
		// ((InF) o).f();

		// System.out.println("o = " + o);
	}

}


class A implements InF {

	@Override
	public void f() {
		System.out.println("true = " + true);
	}

}

interface InF {
	void f();
}