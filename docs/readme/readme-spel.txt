The expression language supports the following functionality:
Literal expressions
Boolean and relational operators
Regular expressions
Class expressions
Accessing properties, arrays, lists, and maps
Method invocation
Relational operators
Assignment
Calling constructors
Bean references
Array construction
Inline lists
Inline maps
Ternary operator
Variables
User-defined functions
Collection projection
Collection selection
Templated expressions
-------
Understanding EvaluationContext
The EvaluationContext interface is used when evaluating an expression to resolve properties, methods, or fields and to help perform type conversion. Spring provides two implementations.

SimpleEvaluationContext: Exposes a subset of essential SpEL language features and configuration options, for categories of expressions that do not require the full extent of the SpEL language syntax and should be meaningfully restricted. Examples include but are not limited to data binding expressions and property-based filters.

StandardEvaluationContext: Exposes the full set of SpEL language features and configuration options. You can use it to specify a default root object and to configure every available evaluation-related strategy.

SimpleEvaluationContext is designed to support only a subset of the SpEL language syntax. It excludes Java type references, constructors, and bean references. It also requires you to explicitly choose the level of support for properties and methods in expressions. By default, the create() static factory method enables only read access to properties. You can also obtain a builder to configure the exact level of support needed, targeting one or some combination of the following:
- Custom PropertyAccessor only (no reflection)
- Data binding properties for read-only access
- Data binding properties for read and write