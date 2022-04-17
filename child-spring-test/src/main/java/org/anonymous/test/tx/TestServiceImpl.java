package org.anonymous.test.tx;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/03/09
 */
@Service
public class TestServiceImpl implements TestService, InitializingBean {

	/*
		PROPAGATION_REQUIRED ： 支持当前事务，如果当前没有事务，就新建一个事务，这也是最常见的
		PROPAGATION_SUPPORTS ： 支持当前事务，如果当前没有事务，就以非事务的方式执行
		PROPAGATION_MANDATORY： 支持当前事务，如果当前没有事务，就抛异常
		PROPAGATION_REQUIRES_NEW：新建事务，如果当前事务存在，就把当前事务挂起
		PROPAGATION_NOT_SUPPORTED：以非事务的方式执行，如果存在当前事务，就把当前事务挂起
		PROPAGATION_NEVER： 以非事务的方式执行，如果当前存在事务，就抛异常
		PROPAGATION_NESTED：如果存在当前事务，则在嵌套事务内执行，如果当前没有事务，则新建一个事务

		在 @Transactional的方法里面捕获了异常，必须要手动回滚，
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

		AbstractFallbackTransactionAttributeSource#computeTransactionAttribute
		protected TransactionAttribute computeTransactionAttribute(Method method, Class<?> targetClass) {
		   // Don't allow no-public methods as required.
		   //非public 方法，返回@Transactional信息一律是null
		   if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
			  return null;
		   }
		   //.......
		 }

	 */

	// private final ZjJiuZhenXxRepository jiuZhenXxRepository;
	// private final JPAQueryFactory queryFactory;
	//
	// private final TestService testService;
	//
	// public TestServiceImpl(ZjJiuZhenXxRepository jiuZhenXxRepository,
	//                        JPAQueryFactory queryFactory, TestService testService) {
	//     this.jiuZhenXxRepository = jiuZhenXxRepository;
	//     this.queryFactory = queryFactory;
	//     this.testService = testService;
	// }

	// @Autowired
	// private ZjJiuZhenXxRepository jiuZhenXxRepository;
	// @Autowired
	// private JPAQueryFactory queryFactory;
	@Autowired
	private TestService testService;
	// @Autowired
	// private YuJianFzController yuJianFzController;
	// @Autowired
	// private ApplicationService applicationService;

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("this = " + this);
		System.out.println("testService = " + testService);
		// System.out.println(this == testService);
		System.out.println("this == testService = " + (this == testService));
		// Object o = AopContext.currentProxy();

	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void update(String name, String id) {
		try {
			System.out.println("name = " + name);
			// ZJ_JiuZhenXx jzxx = jiuZhenXxRepository.getJiuZhenXxByJiuZhenId(id);
			// jzxx.setXingMing(name);
			// // jiuZhenXxRepository.update(jzxx);
			// QZJ_JiuZhenXx jz = QZJ_JiuZhenXx.zJ_JiuZhenXx;
			//
			// Object o = AopContext.currentProxy();
			//
			// queryFactory.update(jz)
			//         .set(jz.xingMing, name)
			//         .where(jz.jiuZhenId.eq(id))
			//         .execute();

			// this 调用, #updateIn 自己的事务无法生效
			/*testService.*/
			updateIn(name);
			throw new RuntimeException("自定义异常");
		} catch (RuntimeException e) {
			// TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void update2(String name, String id) {
		System.out.println("name = " + name);
		// update in 外部事务
		// ZJ_JiuZhenXx jzxx = jiuZhenXxRepository.getJiuZhenXxByJiuZhenId(id);
		// jzxx.setXingMing(name);
		// // jiuZhenXxRepository.update(jzxx);
		// QZJ_JiuZhenXx jz = QZJ_JiuZhenXx.zJ_JiuZhenXx;
		//
		// // Object o = AopContext.currentProxy();
		//
		// queryFactory.update(jz)
		//         .set(jz.xingMing, name)
		//         .where(jz.jiuZhenId.eq(id))
		//         .execute();

		// update 子事务
		// REQUIRES_NEW
		// 子事务的异常抛出则会影响外部事物
		testService.updateInTx(name);

		// 外部事务的后续逻辑, 如果有异常
		// 子事务不会会滚 -- REQUIRES_NEW
	}

	@Transactional(rollbackFor = Throwable.class, propagation = REQUIRES_NEW)
	@Override
	public void updateIn(String name) {
		// QZJ_JiuZhenXx jz = QZJ_JiuZhenXx.zJ_JiuZhenXx;
		//
		// Object o = AopContext.currentProxy();
		//
		// queryFactory.update(jz)
		//         .set(jz.xingMing, name)
		//         .where(jz.jiuZhenId.eq("55077"))
		//         .execute();

	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void updateInTx(String name) {
		try {
			// QZJ_JiuZhenXx jz = QZJ_JiuZhenXx.zJ_JiuZhenXx;
			//
			// queryFactory.update(jz)
			//         .set(jz.xingMing, name)
			//         .where(jz.jiuZhenId.eq("55077"))
			//         .execute();

			throw new RuntimeException("自定义异常");
		} catch (RuntimeException e) {
			// 捕获异常后, 手动回滚
			// 类似
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

			// 再次嵌套子事务: 比如记录异常信息到 数据库
			testService.saveEx(name);
		}

	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void saveEx(String name) {
		// QZJ_JiuZhenXx jz = QZJ_JiuZhenXx.zJ_JiuZhenXx;
		//
		// queryFactory.update(jz)
		//         .set(jz.xingMing, name)
		//         .where(jz.jiuZhenId.eq("54887"))
		//         .execute();

		// 执行业务逻辑 --
	}

}
