package org.anonymous.boot.test;

import org.anonymous.boot.test.model.Inventor;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import java.util.*;

/**
 * ~~ Talk is cheap. Show me the code. ~~ :-)
 *
 * @author MiaoOne
 * @since 2022/05/04
 */
public class SpELTests {

	// For example, consider the following utility method that reverses a string:
	public static abstract class StringUtils {

		public static String reverseString(String input) {
			StringBuilder backwards = new StringBuilder(input.length());
			for (int i = 0; i < input.length(); i++) {
				backwards.append(input.charAt(input.length() - 1 - i));
			}
			return backwards.toString();
		}
	}

	@Test
	public void string() {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("'Hello World'");
		String message = (String) exp.getValue();
		System.out.println("message = " + message);
	}

	@Test
	public void stringM1() {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("'Hello World'.concat('!')");
		String message = (String) exp.getValue();
		System.out.println("message = " + message);
	}

	@Test
	public void stringM2() {
		ExpressionParser parser = new SpelExpressionParser();

		// invokes 'getBytes()'
		Expression exp = parser.parseExpression("'Hello World'.bytes");
		byte[] bytes = (byte[]) exp.getValue();
		System.out.println(Arrays.toString(bytes));
	}

	@Test
	public void stringM3() {
		ExpressionParser parser = new SpelExpressionParser();

		// invokes 'getBytes().length'
		Expression exp = parser.parseExpression("'Hello World'.bytes.length");
		int length = (Integer) exp.getValue();
		System.out.println("length = " + length);
	}

	@Test
	public void stringM4() {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("new String('hello world').toUpperCase()");
		String message = exp.getValue(String.class);
		System.out.println("message = " + message);
	}

	/**
	 * @see #Properties_Arrays_Lists_Maps_Indexers()
	 */
	@Test
	public void testA() {
		// Create and set a calendar
		GregorianCalendar c = new GregorianCalendar();
		c.set(1856, Calendar.AUGUST, 9);

		// The constructor arguments are name, birthday, and nationality.
		Inventor tesla = new Inventor("Nikola Tesla", c.getTime(), "Serbian");

		ExpressionParser parser = new SpelExpressionParser();

		Expression exp = parser.parseExpression("name"); // Parse name as an expression
		String name = (String) exp.getValue(tesla);
		// name == "Nikola Tesla"
		System.out.println("name = " + name);

		exp = parser.parseExpression("name == 'Nikola Tesla'");
		boolean result = exp.getValue(tesla, Boolean.class);
		// result == true
		System.out.println("result = " + result);
	}

	@Test
	public void typeConverter() {
		/*
		Type Conversion
		By default, SpEL uses the conversion service available in Spring core (org.springframework.core.convert.ConversionService).
		This conversion service comes with many built-in converters for common conversions but is also fully extensible
		so that you can add custom conversions between types. Additionally, it is generics-aware.
		This means that, when you work with generic types in expressions,
		SpEL attempts conversions to maintain type correctness for any objects it encounters.

		What does this mean in practice? Suppose assignment, using setValue(), is being used to set a List property.
		The type of the property is actually List<Boolean>. SpEL recognizes that the elements of the list need
		to be converted to Boolean before being placed in it. The following example shows how to do so:
		 */
		class Simple {
			public List<Boolean> booleanList = new ArrayList<>();
		}

		Simple simple = new Simple();
		simple.booleanList.add(true);

		EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

		ExpressionParser parser = new SpelExpressionParser();

		// "false" is passed in here as a String. SpEL and the conversion service
		// will recognize that it needs to be a Boolean and convert it accordingly.
		parser.parseExpression("booleanList[0]").setValue(context, simple, "false");

		// b is false
		Boolean b = simple.booleanList.get(0);
		System.out.println("b = " + b);
	}

	@Test
	public void parserConfig() {
		/*
		It is possible to configure the SpEL expression parser by using a parser configuration object
		(org.springframework.expression.spel.SpelParserConfiguration).
		The configuration object controls the behavior of some of the expression components.
		For example, if you index into an array or collection and the element at the specified index is null,
		SpEL can automatically create the element. This is useful when using expressions made up of a chain of property references.
		If you index into an array or list and specify an index that is beyond the end of the current size of the array or list,
		SpEL can automatically grow the array or list to accommodate that index. In order to add an element at the specified index,
		SpEL will try to create the element using the element type’s default constructor before setting the specified value.
		If the element type does not have a default constructor, null will be added to the array or list.
		If there is no built-in or custom converter that knows how to set the value,
		null will remain in the array or list at the specified index.
		The following example demonstrates how to automatically grow the list:
		 */

		class Demo {
			public List<String> list;
		}

		// Turn on:
		// - auto null reference initialization
		// - auto collection growing
		SpelParserConfiguration config = new SpelParserConfiguration(true, true);

		ExpressionParser parser = new SpelExpressionParser(config);

		Expression expression = parser.parseExpression("list[3]");

		Demo demo = new Demo();

		Object o = expression.getValue(demo);

		// demo.list will now be a real collection of 4 entries
		// Each entry is a new empty String
		System.out.println("o = " + o);
		System.out.println("o = " + o.getClass());
	}

	@Test
	public void literalExpressions() {
		/*
		The types of literal expressions supported are strings, numeric values (int, real, hex),
		boolean, and null. Strings are delimited by single quotation marks.
		To put a single quotation mark itself in a string, use two single quotation mark characters.

		The following listing shows simple usage of literals. Typically,
		they are not used in isolation like this but, rather, as part of a more complex expression--
		for example, using a literal on one side of a logical comparison operator.
		 */
		ExpressionParser parser = new SpelExpressionParser();

		// evals to "Hello World"
		String helloWorld = (String) parser.parseExpression("'Hello World'").getValue();

		// Numbers support the use of the negative sign, exponential notation, and decimal points.
		// By default, real numbers are parsed by using Double.parseDouble().
		double avogadrosNumber = (Double) parser.parseExpression("6.0221415E+23").getValue();

		// evals to 2147483647
		int maxValue = (Integer) parser.parseExpression("0x7FFFFFFF").getValue();

		boolean trueValue = (Boolean) parser.parseExpression("true").getValue();

		Object nullValue = parser.parseExpression("null").getValue();
	}

	/**
	 * @see #testA()
	 */
	@Test
	public void Properties_Arrays_Lists_Maps_Indexers() {
		/*
		Navigating with property references is easy. To do so, use a period to indicate a nested property value.
		The instances of the Inventor class, pupin and tesla, were populated with data listed in the Classes
		used in the examples section.
		To navigate "down" the object graph and get Tesla’s year of birth and Pupin’s city of birth,
		we use the following expressions:
		 */

		/*
		Case insensitivity is allowed for the first letter of property names.
		Thus, the expressions in the above example may be written as Birthday.Year + 1900 and Nationality.City, respectively.
		In addition, properties may optionally be accessed via method invocations
		--For example, getNationality().getCity() instead of nationality.city.
		 */

		// Create and set a calendar
		GregorianCalendar c = new GregorianCalendar();
		c.set(1856, Calendar.AUGUST, 9);

		// The constructor arguments are name, birthday, and nationality.
		Inventor root = new Inventor("Nikola Tesla", c.getTime(), "Serbian");

		ExpressionParser parser = new SpelExpressionParser();

		// EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

		// evals to 1856
		int year = (Integer) parser.parseExpression("birthday.year + 1900").getValue(root);
		System.out.println("year = " + year);

		String city = (String) parser.parseExpression("nationality.city").getValue(root);
		System.out.println("city = " + city);

		// 多余的空格会自动去除
		city = (String) parser.parseExpression("getNationality()  .getCity() ").getValue(root);
		System.out.println("city = " + city);

	}

	@Test
	public void arrays() {
		// The contents of arrays and lists are obtained by using square bracket notation, as the following example shows:

		// Create and set a calendar
		GregorianCalendar c = new GregorianCalendar();
		c.set(1856, Calendar.AUGUST, 9);

		// The constructor arguments are name, birthday, and nationality.
		Inventor tesla = new Inventor("Nikola Tesla", c.getTime(), "Serbian");

		ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));
		EvaluationContext context = SimpleEvaluationContext.forReadWriteDataBinding().build();

		// Inventions Array

		// evaluates to "Induction motor"
		String invention = parser.parseExpression("inventions[3]").getValue(
				context, tesla, String.class);
		System.out.println("invention = " + invention);

		// Members List

		// evaluates to "Nikola Tesla"
		// String name = parser.parseExpression("members[0].name").getValue(
		// 		context, ieee, String.class);

		// List and Array navigation
		// evaluates to "Wireless communication"
		// String invention = parser.parseExpression("members[0].inventions[6]").getValue(
		// 		context, ieee, String.class);
	}

	@Test
	public void map() {
		// The contents of maps are obtained by specifying the literal key value within the brackets.
		// In the following example, because keys for the officers map are strings, we can specify string literals:
		// Officer's Dictionary

		ExpressionParser parser = new SpelExpressionParser();

		// Inventor pupin = parser.parseExpression("officers['president']").getValue(
		// 		societyContext, Inventor.class);

		// evaluates to "Idvor"
		// String city = parser.parseExpression("officers['president'].placeOfBirth.city").getValue(
		// 		societyContext, String.class);

		// setting values
		// parser.parseExpression("officers['advisors'][0].placeOfBirth.country").setValue(
		// 		societyContext, "Croatia");
	}

	@Test
	public void inlineLists() {
		// You can directly express lists in an expression by using {} notation.
		// evaluates to a Java list containing the four numbers
		// List numbers = (List) parser.parseExpression("{1,2,3,4}").getValue(context);

		// List listOfLists = (List) parser.parseExpression("{{'a','b'},{'x','y'}}").getValue(context);

		// {} by itself means an empty list. For performance reasons, if the list is itself entirely composed of fixed literals,
		// a constant list is created to represent the expression (rather than building a new list on each evaluation).
	}

	@Test
	public void inlineMaps() {
		// You can also directly express maps in an expression by using {key:value} notation.
		// The following example shows how to do so:
		// evaluates to a Java map containing the two entries
		// Map inventorInfo = (Map) parser.parseExpression("{name:'Nikola',dob:'10-July-1856'}").getValue(context);

		// Map mapOfMaps = (Map) parser.parseExpression("{name:{first:'Nikola',last:'Tesla'},dob:{day:10,month:'July',year:1856}}").getValue(context);

		// {:} by itself means an empty map. For performance reasons,
		// if the map is itself composed of fixed literals or other nested constant structures (lists or maps),
		// a constant map is created to represent the expression (rather than building a new map on each evaluation).
		// Quoting of the map keys is optional (unless the key contains a period (.)). The examples above do not use quoted keys.
	}

	@Test
	public void arrayConstruction() {
		// You can build arrays by using the familiar Java syntax,
		// optionally supplying an initializer to have the array populated at construction time.
		// The following example shows how to do so:

		// int[] numbers1 = (int[]) parser.parseExpression("new int[4]").getValue(context);

		// Array with initializer
		// int[] numbers2 = (int[]) parser.parseExpression("new int[]{1,2,3}").getValue(context);

		// Multi dimensional array
		// int[][] numbers3 = (int[][]) parser.parseExpression("new int[4][5]").getValue(context);

		// You cannot currently supply an initializer when you construct a multi-dimensional array.
	}

	@Test
	public void Methods() {
		// You can invoke methods by using typical Java programming syntax.
		// You can also invoke methods on literals.
		// Variable arguments are also supported.
		// The following examples show how to invoke methods:
		// string literal, evaluates to "bc"
		// String bc = parser.parseExpression("'abc'.substring(1, 3)").getValue(String.class);

		// evaluates to true
		// boolean isMember = parser.parseExpression("isMember('Mihajlo Pupin')").getValue(
		// 		societyContext, Boolean.class);
	}

	// Operators: 关系/逻辑/算数/赋值
	@Test
	public void RelationalOp() {
		// The relational operators (equal, not equal, less than, less than or equal, greater than, and greater than or equal)
		// are supported by using standard operator notation. The following listing shows a few examples of operators:
		// evaluates to true

		// boolean trueValue = parser.parseExpression("2 == 2").getValue(Boolean.class);

		// evaluates to false
		// boolean falseValue = parser.parseExpression("2 < -5.0").getValue(Boolean.class);

		// evaluates to true
		// boolean trueValue = parser.parseExpression("'black' < 'block'").getValue(Boolean.class);

		/*
			Greater-than and less-than comparisons against null follow a simple rule:
			null is treated as nothing (that is NOT as zero).
			As a consequence, any other value is always greater than null (X > null is always true)
			and no other value is ever less than nothing (X < null is always false).

			If you prefer numeric comparisons instead, avoid number-based null comparisons
			in favor of comparisons against zero (for example, X > 0 or X < 0).
		 */

		// In addition to the standard relational operators,
		// SpEL supports the instanceof and regular expression-based matches operator.
		// The following listing shows examples of both:

		/*
			Be careful with primitive types, as they are immediately boxed up to their wrapper types.
			For example, 1 instanceof T(int) evaluates to false, while 1 instanceof T(Integer) evaluates to true, as expected.
		 */

		// evaluates to false
		// boolean falseValue = parser.parseExpression(
		// 		"'xyz' instanceof T(Integer)").getValue(Boolean.class);

		// evaluates to true
		// boolean trueValue = parser.parseExpression(
		// 		"'5.00' matches '^-?\\d+(\\.\\d{2})?$'").getValue(Boolean.class);

		// evaluates to false
		// boolean falseValue = parser.parseExpression(
		// 		"'5.0067' matches '^-?\\d+(\\.\\d{2})?$'").getValue(Boolean.class);

		/*
		Each symbolic operator can also be specified as a purely alphabetic equivalent.
		This avoids problems where the symbols used have special meaning for the document type
		in which the expression is embedded (such as in an XML document). The textual equivalents are:
				lt (<)
				gt (>)
				le (<=)
				ge (>=)
				eq (==)
				ne (!=)
				div (/)
				mod (%)
				not (!).
				All of the textual operators are case-insensitive.
		 */
	}

	@Test
	public void LogicalOp() {
		/*
		SpEL supports the following logical operators:
		and (&&)
		or (||)
		not (!)
		The following example shows how to use the logical operators:
		 */

		// -- AND --

		// evaluates to false
		// boolean falseValue = parser.parseExpression("true and false").getValue(Boolean.class);

		// evaluates to true
		// String expression = "isMember('Nikola Tesla') and isMember('Mihajlo Pupin')";
		// boolean trueValue = parser.parseExpression(expression).getValue(societyContext, Boolean.class);

		// -- OR --

		// evaluates to true
		// boolean trueValue = parser.parseExpression("true or false").getValue(Boolean.class);

		// evaluates to true
		// String expression = "isMember('Nikola Tesla') or isMember('Albert Einstein')";
		// boolean trueValue = parser.parseExpression(expression).getValue(societyContext, Boolean.class);

		// -- NOT --

		// evaluates to false
		// boolean falseValue = parser.parseExpression("!true").getValue(Boolean.class);

		// -- AND and NOT --
		// String expression = "isMember('Nikola Tesla') and !isMember('Mihajlo Pupin')";
		// boolean falseValue = parser.parseExpression(expression).getValue(societyContext, Boolean.class);
	}

	@Test
	public void MathematicalOp() {
		// You can use the addition operator (+) on both numbers and strings.
		// You can use the subtraction (-), multiplication (*), and division (/) operators only on numbers.
		// You can also use the modulus (%) and exponential power (^) operators on numbers.
		// Standard operator precedence is enforced.
		// The following example shows the mathematical operators in use:

		// Addition
		// int two = parser.parseExpression("1 + 1").getValue(Integer.class);  // 2

		// String testString = parser.parseExpression(
		// 		"'test' + ' ' + 'string'").getValue(String.class);  // 'test string'

		// Subtraction
		// int four = parser.parseExpression("1 - -3").getValue(Integer.class);  // 4

		// double d = parser.parseExpression("1000.00 - 1e4").getValue(Double.class);  // -9000

		// Multiplication
		// int six = parser.parseExpression("-2 * -3").getValue(Integer.class);  // 6

		// double twentyFour = parser.parseExpression("2.0 * 3e0 * 4").getValue(Double.class);  // 24.0

		// Division
		// int minusTwo = parser.parseExpression("6 / -3").getValue(Integer.class);  // -2

		// double one = parser.parseExpression("8.0 / 4e0 / 2").getValue(Double.class);  // 1.0

		// Modulus
		// int three = parser.parseExpression("7 % 4").getValue(Integer.class);  // 3

		// int one = parser.parseExpression("8 / 5 % 2").getValue(Integer.class);  // 1

		// Operator precedence
		// int minusTwentyOne = parser.parseExpression("1+2-3*8").getValue(Integer.class);  // -21
	}

	@Test
	public void AssignmentOp() {
		// To set a property, use the assignment operator (=). This is typically done within a call to
		// setValue but can also be done inside a call to getValue.
		// The following listing shows both ways to use the assignment operator:

		// Inventor inventor = new Inventor();
		// EvaluationContext context = SimpleEvaluationContext.forReadWriteDataBinding().build();

		// parser.parseExpression("name").setValue(context, inventor, "Aleksandar Seovic");

		// alternatively
		// String aleks = parser.parseExpression(
		// 		"name = 'Aleksandar Seovic'").getValue(context, inventor, String.class);
	}

	@Test
	public void TypesTest() {
		// You can use the special T operator to specify an instance of java.lang.Class (the type).
		// Static methods are invoked by using this operator as well.
		// The StandardEvaluationContext uses a TypeLocator to find types,
		// and the StandardTypeLocator (which can be replaced) is built with
		// an understanding of the java.lang package.
		// This means that T() references to types within the java.lang package do not need
		// to be fully qualified, but all other type references must be.
		// The following example shows how to use the T operator:

		// Class dateClass = parser.parseExpression("T(java.util.Date)").getValue(Class.class);

		// Class stringClass = parser.parseExpression("T(String)").getValue(Class.class);

		// boolean trueValue = parser.parseExpression(
		// 				"T(java.math.RoundingMode).CEILING < T(java.math.RoundingMode).FLOOR")
		// 		.getValue(Boolean.class);
	}

	@Test
	public void ConstructorsTest() {
		// You can invoke constructors by using the new operator.
		// You should use the fully qualified class name for all types except those
		// located in the java.lang package (Integer, Float, String, and so on).
		// The following example shows how to use the new operator to invoke constructors:

		// Inventor einstein = p.parseExpression(
		// 				"new org.spring.samples.spel.inventor.Inventor('Albert Einstein', 'German')")
		// 		.getValue(Inventor.class);

		// create new Inventor instance within the add() method of List
		// p.parseExpression(
		// 		"Members.add(new org.spring.samples.spel.inventor.Inventor(
		// 'Albert Einstein', 'German'))").getValue(societyContext);
	}

	@Test
	public void VariablesTest() {
		// You can reference variables in the expression by using the #variableName syntax.
		// Variables are set by using the setVariable method on EvaluationContext implementations.
		// Valid variable names must be composed of one or more of the following supported characters.
		// letters: A to Z and a to z
		// digits: 0 to 9
		// underscore: _
		// dollar sign: $

		// Inventor tesla = new Inventor("Nikola Tesla", "Serbian");

		// EvaluationContext context = SimpleEvaluationContext.forReadWriteDataBinding().build();
		// context.setVariable("newName", "Mike Tesla");

		// parser.parseExpression("name = #newName").getValue(context, tesla);
		// System.out.println(tesla.getName())  // "Mike Tesla"

		//-------------------------------

		// The #this and #root Variables
		// The #this variable is always defined and refers to the current evaluation object
		// (against which unqualified references are resolved).
		// The #root variable is always defined and refers to the root context object.
		// Although #this may vary as components of an expression are evaluated,
		// #root always refers to the root.
		// The following examples show how to use the #this and #root variables:

		// create an array of integers
		// List<Integer> primes = new ArrayList<Integer>();
		// primes.addAll(Arrays.asList(2,3,5,7,11,13,17));

		// create parser and set variable 'primes' as the array of integers
		// ExpressionParser parser = new SpelExpressionParser();
		// EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataAccess();
		// context.setVariable("primes", primes);

		// all prime numbers > 10 from the list (using selection ?{...})
		// evaluates to [11, 13, 17]
		// List<Integer> primesGreaterThanTen = (List<Integer>) parser.parseExpression(
		//         "#primes.?[#this>10]").getValue(context);
	}

	@Test
	public void FunctionsTest() {
		// You can extend SpEL by registering user-defined functions
		// that can be called within the expression string.
		// The function is registered through the EvaluationContext.
		// The following example shows how to register a user-defined function:

		// Method method = ...;

		// EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
		// context.setVariable("myFunction", method);
	}

	// You can then register and use the preceding method, as the following example shows:
	void funs() {
		// ExpressionParser parser = new SpelExpressionParser();

		// EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
		// context.setVariable("reverseString",
		// 		StringUtils.class.getDeclaredMethod("reverseString", String.class));

		// String helloWorldReversed = parser.parseExpression(
		// 		"#reverseString('hello')").getValue(context, String.class);
	}

	@Test
	public void BeanReferencesTest() {
		// If the evaluation context has been configured with a bean resolver,
		// you can look up beans from an expression by using the @ symbol.
		// The following example shows how to do so:

		// ExpressionParser parser = new SpelExpressionParser();
		// StandardEvaluationContext context = new StandardEvaluationContext();
		// context.setBeanResolver(new MyBeanResolver());

		// This will end up calling resolve(context, "something") on MyBeanResolver during evaluation
		// Object bean = parser.parseExpression("@something").getValue(context);


		// To access a factory bean itself,
		// you should instead prefix the bean name with an & symbol.
		// The following example shows how to do so:
		// ExpressionParser parser = new SpelExpressionParser();
		// StandardEvaluationContext context = new StandardEvaluationContext();
		// context.setBeanResolver(new MyBeanResolver());

		// This will end up calling resolve(context, "&foo") on MyBeanResolver during evaluation
		// Object bean = parser.parseExpression("&foo").getValue(context);
	}

	// 三元运算符
	@Test
	public void TernaryOperator() {
		// Ternary Operator (If-Then-Else)
		// You can use the ternary operator for performing if-then-else conditional logic
		// inside the expression.
		// The following listing shows a minimal example:
		// String falseString = parser.parseExpression(
		// 		"false ? 'trueExp' : 'falseExp'").getValue(String.class);

		// In this case, the boolean false results in returning
		// the string value 'falseExp'.
		// A more realistic example follows:

		// parser.parseExpression("name").setValue(societyContext, "IEEE");
		// societyContext.setVariable("queryName", "Nikola Tesla");

		// expression = "isMember(#queryName)? #queryName + ' is a member of the ' " +
		// 		"+ Name + ' Society' : #queryName + ' is not a member of the ' + Name + ' Society'";

		// String queryResultString = parser.parseExpression(expression)
		// 		.getValue(societyContext, String.class);
		// queryResultString = "Nikola Tesla is a member of the IEEE Society"
	}

	@Test
	public void TheElvisOperator() {
		// The Elvis Operator
		// The Elvis operator is a shortening of the ternary operator syntax and is used in the Groovy language.
		// With the ternary operator syntax, you usually have to repeat a variable twice,
		// as the following example shows:
		String name = "Elvis Presley";
		String displayName = (name != null ? name : "Unknown");

		// Instead, you can use the Elvis operator (named for the resemblance to Elvis' hair style).
		// The following example shows how to use the Elvis operator:
		// ExpressionParser parser = new SpelExpressionParser();

		// String name = parser.parseExpression("name?:'Unknown'").getValue(new Inventor(), String.class);
		// System.out.println(name);  // 'Unknown'

		// The following listing shows a more complex example:
		// ExpressionParser parser = new SpelExpressionParser();
		// EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

		// Inventor tesla = new Inventor("Nikola Tesla", "Serbian");
		// String name = parser.parseExpression("name?:'Elvis Presley'").getValue(context, tesla, String.class);
		// System.out.println(name);  // Nikola Tesla

		// tesla.setName(null);
		// name = parser.parseExpression("name?:'Elvis Presley'").getValue(context, tesla, String.class);
		// System.out.println(name);  // Elvis Presley

		// You can use the Elvis operator to apply default values in expressions.
		// The following example shows how to use the Elvis operator in a @Value expression:
		// @Value("#{systemProperties['pop3.port'] ?: 25}")
		// This will inject a system property pop3.port if it is defined or 25 if not.
	}

	@Test
	public void SafeNavigationOperator() {
		// The safe navigation operator is used to avoid a NullPointerException and comes from the Groovy language.
		// Typically, when you have a reference to an object, you might need to verify
		// that it is not null before accessing methods or properties of the object.
		// To avoid this, the safe navigation operator returns null instead of throwing an exception.
		// The following example shows how to use the safe navigation operator:

		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

		// Inventor tesla = new Inventor("Nikola Tesla", "Serbian");
		// tesla.setPlaceOfBirth(new PlaceOfBirth("Smiljan"));

		// String city = parser.parseExpression("placeOfBirth?.city").getValue(context, tesla, String.class);
		// System.out.println(city);  // Smiljan

		// tesla.setPlaceOfBirth(null);
		// city = parser.parseExpression("placeOfBirth?.city").getValue(context, tesla, String.class);
		// System.out.println(city);  // null - does not throw NullPointerException!!!
	}

	@Test
	public void CollectionSelection() {
		// election is a powerful expression language feature that lets you transform
		// a source collection into another collection by selecting from its entries.

		// Selection uses a syntax of .?[selectionExpression].
		// It filters the collection and returns a new collection that
		// contains a subset of the original elements.
		// For example, selection lets us easily get a list of Serbian inventors, as the following example shows:

		// List<Inventor> list = (List<Inventor>) parser.parseExpression(
		//         "members.?[nationality == 'Serbian']").getValue(societyContext);

		// Selection is supported for arrays and anything that implements java.lang.Iterable or java.util.Map.
		// For a list or array, the selection criteria is evaluated against each individual element.
		// Against a map, the selection criteria is evaluated against each map entry (objects of the Java type Map.Entry).
		// Each map entry has its key and value accessible as properties for use in the selection.

		// The following expression returns a new map that consists of
		// those elements of the original map where the entry’s value is less than 27:
		// Map newMap = parser.parseExpression("map.?[value<27]").getValue();

		// In addition to returning all the selected elements, you can retrieve only the first or the last element.
		// To obtain the first element matching the selection, the syntax is .^[selectionExpression].
		// To obtain the last matching selection, the syntax is .$[selectionExpression].
	}

	@Test
	public void CollectionProjection() {
		// Projection lets a collection drive the evaluation of a sub-expression, and the result is a new collection.
		// The syntax for projection is .![projectionExpression].
		// For example, suppose we have a list of inventors but want the list of cities where they were born.
		// Effectively, we want to evaluate 'placeOfBirth.city' for every entry in the inventor list.
		// The following example uses projection to do so:
		// returns ['Smiljan', 'Idvor' ]
		// List placesOfBirth = (List)parser.parseExpression("members.![placeOfBirth.city]");

		// Projection is supported for arrays and anything that implements java.lang.Iterable or java.util.Map.
		// When using a map to drive projection, the projection expression is evaluated against each entry in the map (represented as a Java Map.Entry).
		// The result of a projection across a map is a list that consists of the evaluation of the projection expression against each map entry.
	}

	/**
	 * @see org.springframework.expression.common.TemplateParserContext
	 */
	@Test
	public void ExpressionTemplating() {
		// Expression templates allow mixing literal text with one or more evaluation blocks.
		// Each evaluation block is delimited with prefix and suffix characters that you can define.
		// A common choice is to use #{ } as the delimiters, as the following example shows:

		// String randomPhrase = parser.parseExpression(
		// 		"random number is #{T(java.lang.Math).random()}",
		// 		new TemplateParserContext()).getValue(String.class);

		// evaluates to "random number is 0.7038186818312008"

		// The string is evaluated by concatenating the literal text 'random number is ' with the result of
		// evaluating the expression inside the #{ } delimiter (in this case, the result of calling that random() method).
		// The second argument to the parseExpression() method is of the type ParserContext.
		// The ParserContext interface is used to influence how the expression is parsed in order to support the expression templating functionality.
		// The definition of TemplateParserContext follows:
	}

}
