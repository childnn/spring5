package org.anonymous.boot.test.config;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.propertyeditors.CustomDateEditor;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/03
 * CustomEditorConfigurer is a bean-factory-post-processor, 可通过多种方式加入到 app-ctx
 */
public class ChildPropertyEditorConfig extends CustomEditorConfigurer {

	public ChildPropertyEditorConfig() {
		System.out.println(getClass().getName() + "----init....");
	}

	public static class ChildDateEditor extends CustomDateEditor {

		public ChildDateEditor() {
			super(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true);
			System.out.println(getClass().getName() + "----init....");
		}
	}

	// 1. 名为 ExoticTypeEditor 可识别: 目标类名(FQN) + Editor 后缀
	// 没有查找 2. 唯一参数的 string-构造
	static class ExoticTypeEditor extends PropertyEditorSupport {

		public void setAsText(String text) {
			ExoticType value = new ExoticType();
			value.name = text.toUpperCase();
			setValue(value);
		}
	}

	static class ExoticType {

		private String name;

		// 2. string-构造
		// public ExoticType(String name) {
		// 	this.name = name;
		// }

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	static class DependsOnExoticType {

		private ExoticType type;

		public ExoticType getType() {
			return type;
		}

		public void setType(ExoticType type) {
			this.type = type;
		}
	}

	// 方式一: 直接 setCustomEditors
	@Override
	public void setCustomEditors(Map<Class<?>, Class<? extends PropertyEditor>> customEditors) {
		super.setCustomEditors(new HashMap<Class<?>, Class<? extends PropertyEditor>>() {{
			put(Date.class, ChildDateEditor.class);
			// put(ExoticType.class, ExoticTypeEditor.class);
		}});
	}

	// 方式二: 间接方式--通过 PropertyEditorRegistrar
	@Override
	public void setPropertyEditorRegistrars(PropertyEditorRegistrar[] propertyEditorRegistrars) {
		super.setPropertyEditorRegistrars(new PropertyEditorRegistrar[]{new CustomPropertyEditorRegistrar()});
	}

	/**
	 * @see org.springframework.beans.TypeConverterDelegate#findDefaultEditor(Class)
	 * @see org.springframework.beans.BeanUtils#findEditorByConvention(Class) convention over configuration! unbelievable!
	 * 如果
	 */
	public static void main(String[] args) {
		BeanWrapper doe = new BeanWrapperImpl(new DependsOnExoticType());
		// 根据 约定优于配置原则, 甚至不需要手动注册 ExoticTypeEditor,
		// 会自动查找 名为 ExoticType + Editor 的类 -- 详见: org.springframework.beans.BeanUtils#findEditorByConvention
		// 如果没有 名为 ExoticType + Editor 的类, 则会查找 ExoticType(String) 构造方法
		// 如果以上两种都没有则抛出异常
		// Caused by: java.lang.IllegalStateException: Cannot convert value of type 'java.lang.String' to
		// required type 'org.anonymous.boot.test.config.ChildPropertyEditorConfig$ExoticType' for property 'type':
		// no matching editors or conversion strategy found
		// org.springframework.beans.TypeConverterDelegate.convertIfNecessary
		// doe.registerCustomEditor(ExoticType.class, new ExoticTypeEditor());
		doe.setPropertyValue("type", "aaa");

		// MutablePropertyValues propertyValues = new MutablePropertyValues(
		// 		Collections.singletonList(new PropertyValue("type", "aaa")));
		// doe.setPropertyValues(propertyValues, false, false);

		// org.springframework.beans.NotReadablePropertyException:
		// Invalid property 'type' of bean class [org.anonymous.boot.test.config.ChildPropertyEditorConfig$DependsOnExoticType]:
		// Bean property 'type' is not readable or has an invalid getter method:
		// Does the return type of the getter match the parameter type of the setter?
		// 属性必须要 getter 方法, 才可以调用 #getPropertyValue 否则会抛异常
		ExoticType type = (ExoticType) doe.getPropertyValue("type");
		System.out.println("type = " + type);
		if (type != null) {
			System.out.println("type.name = " + type.name);
		}


	}

}
