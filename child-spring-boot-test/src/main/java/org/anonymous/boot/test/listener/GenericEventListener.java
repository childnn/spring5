package org.anonymous.boot.test.listener;

import org.anonymous.boot.test.event.EntityCreatedEvent;
import org.anonymous.boot.test.model.Person;
import org.springframework.context.event.EventListener;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 */
public class GenericEventListener {

	// Generic Events
	// You can also use generics to further define the structure of your event.
	// Consider using an EntityCreatedEvent<T> where T is the type of the actual entity that got created.
	// For example, you can create the following listener definition to receive only EntityCreatedEvent for a Person:

	@EventListener
	public void onPersonCreated(EntityCreatedEvent<Person> event) {
		// ...
	}

}
