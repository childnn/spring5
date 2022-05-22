package org.anonymous.boot.test.event;

import org.anonymous.boot.test.model.Person;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.lang.reflect.Type;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
public class EntityCreatedEvent1<T> extends ApplicationEvent implements ResolvableTypeProvider {
	private static final long serialVersionUID = -7003852180062949335L;

	// Due to type erasure, this works only if the event that is fired resolves the generic parameters
	// on which the event listener filters (that is, something like
	// class PersonCreatedEvent extends EntityCreatedEvent<Person> { … }).
	//
	// In certain circumstances, this may become quite tedious if all events follow the same
	// structure (as should be the case for the event in the preceding example).
	// In such a case, you can implement ResolvableTypeProvider to guide the framework
	// beyond what the runtime environment provides. The following event shows how to do so:
	// 	This works not only for ApplicationEvent but any arbitrary object that you send as an event.

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param entity the object on which the event initially occurred (never {@code null})
	 */
	public EntityCreatedEvent1(T entity) {
		super(entity);
	}

	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getSource()));
	}

	public static void main(String[] args) {
		EntityCreatedEvent1<Person> ece = new EntityCreatedEvent1<>(new Person());
		ResolvableType resolvableType = ece.getResolvableType();
		// Type type = resolvableType.getType();
		Type type = resolvableType.getGeneric(0).getType();
		System.out.println("type = " + type);
	}

}
