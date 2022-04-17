/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.annotation;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationTypeMapping.MirrorSets.MirrorSet;
import org.springframework.core.annotation.MergedAnnotation.Adapt;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.lang.Nullable;
import org.springframework.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * General utility methods for working with annotations, handling meta-annotations,
 * bridge methods (which the compiler generates for generic declarations) as well
 * as super methods (for optional <em>annotation inheritance</em>).
 *
 * <p>Note that most of the features of this class are not provided by the
 * JDK's introspection facilities themselves.
 *
 * <p>As a general rule for runtime-retained application annotations (e.g. for
 * transaction control, authorization, or service exposure), always use the
 * lookup methods on this class (e.g. {@link #findAnnotation(java.lang.reflect.Method, Class)} or
 * {@link #getAnnotation(java.lang.reflect.Method, Class)}) instead of the plain annotation lookup
 * methods in the JDK. You can still explicitly choose between a <em>get</em>
 * lookup on the given class level only ({@link #getAnnotation(java.lang.reflect.Method, Class)})
 * and a <em>find</em> lookup in the entire inheritance hierarchy of the given
 * method ({@link #findAnnotation(java.lang.reflect.Method, Class)}).
 *
 * <h3>Terminology</h3>
 * The terms <em>directly present</em>, <em>indirectly present</em>, and
 * <em>present</em> have the same meanings as defined in the class-level
 * javadoc for {@link java.lang.reflect.AnnotatedElement} (in Java 8).
 *
 * <p>An annotation is <em>meta-present</em> on an element if the annotation
 * is declared as a meta-annotation on some other annotation which is
 * <em>present</em> on the element. Annotation {@code A} is <em>meta-present</em>
 * on another annotation if {@code A} is either <em>directly present</em> or
 * <em>meta-present</em> on the other annotation.
 *
 * <h3>Meta-annotation Support</h3>
 * <p>Most {@code find*()} methods and some {@code get*()} methods in this class
 * provide support for finding annotations used as meta-annotations. Consult the
 * javadoc for each method in this class for details. For fine-grained support for
 * meta-annotations with <em>attribute overrides</em> in <em>composed annotations</em>,
 * consider using {@link AnnotatedElementUtils}'s more specific methods instead.
 *
 * <h3>Attribute Aliases</h3>
 * <p>All public methods in this class that return annotations, arrays of
 * annotations, or {@link AnnotationAttributes} transparently support attribute
 * aliases configured via {@link AliasFor @AliasFor}. Consult the various
 * {@code synthesizeAnnotation*(..)} methods for details.
 *
 * <h3>Search Scope</h3>
 * <p>The search algorithms used by methods in this class stop searching for
 * an annotation once the first annotation of the specified type has been
 * found. As a consequence, additional annotations of the specified type will
 * be silently ignored.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Mark Fisher
 * @author Chris Beams
 * @author Phillip Webb
 * @author Oleg Zhurakousky
 * @since 2.0
 * @see AliasFor
 * @see AnnotationAttributes
 * @see AnnotatedElementUtils
 * @see org.springframework.core.BridgeMethodResolver
 * @see java.lang.reflect.AnnotatedElement#getAnnotations()
 * @see java.lang.reflect.AnnotatedElement#getAnnotation(Class)
 * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotations()
 */
public abstract class AnnotationUtils {

	/**
	 * The attribute name for annotations with a single element.
	 */
	public static final String VALUE = MergedAnnotation.VALUE;

	private static final AnnotationFilter JAVA_LANG_ANNOTATION_FILTER =
			AnnotationFilter.packages("java.lang.annotation");

	private static final Map<Class<? extends Annotation>, Map<String, DefaultValueHolder>> defaultValuesCache =
			new ConcurrentReferenceHashMap<>();
	private static final Map<Class<?>, Set<Method>> annotatedBaseTypeCache =
			new ConcurrentReferenceHashMap<>(256);

	@SuppressWarnings("unused")
	@Deprecated  // just here for older tool versions trying to reflectively clear the cache
	private static final Map<Class<?>, ?> annotatedInterfaceCache = annotatedBaseTypeCache;

	private static final Map<Class<? extends Annotation>, Map<String, List<String>>> attributeAliasesCache =
			new ConcurrentReferenceHashMap<>(256);

	private static final Map<Class<? extends Annotation>, List<Method>> attributeMethodsCache =
			new ConcurrentReferenceHashMap<>(256);

	private static final Map<Method, AliasDescriptor> aliasDescriptorCache =
			new ConcurrentReferenceHashMap<>(256);

	private static final Map<Class<? extends Annotation>, Boolean> synthesizableCache =
			new ConcurrentReferenceHashMap<>(256);

	/**
	 * Internal holder used to wrap default values.
	 */
	private static class DefaultValueHolder {

		final Object defaultValue;

		public DefaultValueHolder(Object defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		public String toString() {
			return "*" + this.defaultValue;
		}
	}

	/**
	 * {@code AliasDescriptor} encapsulates the declaration of {@code @AliasFor}
	 * on a given annotation attribute and includes support for validating
	 * the configuration of aliases (both explicit and implicit).
	 *
	 * @see #from
	 * @see #getAttributeAliasNames
	 * @see #getAttributeOverrideName
	 * @since 4.2.1
	 */
	private static final class AliasDescriptor {

		private final Method sourceAttribute;

		private final Class<? extends Annotation> sourceAnnotationType;

		private final String sourceAttributeName;

		private final Method aliasedAttribute;

		private final Class<? extends Annotation> aliasedAnnotationType;

		private final String aliasedAttributeName;

		private final boolean isAliasPair;

		@SuppressWarnings("unchecked")
		private AliasDescriptor(Method sourceAttribute, AliasFor aliasFor) {
			Class<?> declaringClass = sourceAttribute.getDeclaringClass();

			this.sourceAttribute = sourceAttribute;
			this.sourceAnnotationType = (Class<? extends Annotation>) declaringClass;
			this.sourceAttributeName = sourceAttribute.getName();

			this.aliasedAnnotationType = (Annotation.class == aliasFor.annotation() ?
					this.sourceAnnotationType : aliasFor.annotation());
			this.aliasedAttributeName = getAliasedAttributeName(aliasFor, sourceAttribute);
			if (this.aliasedAnnotationType == this.sourceAnnotationType &&
					this.aliasedAttributeName.equals(this.sourceAttributeName)) {
				String msg = String.format("@AliasFor declaration on attribute '%s' in annotation [%s] points to " +
								"itself. Specify 'annotation' to point to a same-named attribute on a meta-annotation.",
						sourceAttribute.getName(), declaringClass.getName());
				throw new AnnotationConfigurationException(msg);
			}
			try {
				this.aliasedAttribute = this.aliasedAnnotationType.getDeclaredMethod(this.aliasedAttributeName);
			} catch (NoSuchMethodException ex) {
				String msg = String.format(
						"Attribute '%s' in annotation [%s] is declared as an @AliasFor nonexistent attribute '%s' in annotation [%s].",
						this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName,
						this.aliasedAnnotationType.getName());
				throw new AnnotationConfigurationException(msg, ex);
			}

			this.isAliasPair = (this.sourceAnnotationType == this.aliasedAnnotationType);
		}

		/**
		 * Create an {@code AliasDescriptor} <em>from</em> the declaration
		 * of {@code @AliasFor} on the supplied annotation attribute and
		 * validate the configuration of {@code @AliasFor}.
		 *
		 * @param attribute the annotation attribute that is annotated with
		 *                  {@code @AliasFor}
		 * @return an alias descriptor, or {@code null} if the attribute
		 * is not annotated with {@code @AliasFor}
		 * @see #validateAgainst
		 */
		@Nullable
		public static AliasDescriptor from(Method attribute) {
			AliasDescriptor descriptor = aliasDescriptorCache.get(attribute);
			if (descriptor != null) {
				return descriptor;
			}

			AliasFor aliasFor = attribute.getAnnotation(AliasFor.class);
			if (aliasFor == null) {
				return null;
			}

			descriptor = new AliasDescriptor(attribute, aliasFor);
			descriptor.validate();
			aliasDescriptorCache.put(attribute, descriptor);
			return descriptor;
		}

		private void validate() {
			// Target annotation is not meta-present?
			if (!this.isAliasPair && !isAnnotationMetaPresent(this.sourceAnnotationType, this.aliasedAnnotationType)) {
				String msg = String.format("@AliasFor declaration on attribute '%s' in annotation [%s] declares " +
								"an alias for attribute '%s' in meta-annotation [%s] which is not meta-present.",
						this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName,
						this.aliasedAnnotationType.getName());
				throw new AnnotationConfigurationException(msg);
			}

			if (this.isAliasPair) {
				AliasFor mirrorAliasFor = this.aliasedAttribute.getAnnotation(AliasFor.class);
				if (mirrorAliasFor == null) {
					String msg = String.format(
							"Attribute '%s' in annotation [%s] must be declared as an @AliasFor [%s].",
							this.aliasedAttributeName, this.sourceAnnotationType.getName(), this.sourceAttributeName);
					throw new AnnotationConfigurationException(msg);
				}

				String mirrorAliasedAttributeName = getAliasedAttributeName(mirrorAliasFor, this.aliasedAttribute);
				if (!this.sourceAttributeName.equals(mirrorAliasedAttributeName)) {
					String msg = String.format(
							"Attribute '%s' in annotation [%s] must be declared as an @AliasFor [%s], not [%s].",
							this.aliasedAttributeName, this.sourceAnnotationType.getName(), this.sourceAttributeName,
							mirrorAliasedAttributeName);
					throw new AnnotationConfigurationException(msg);
				}
			}

			Class<?> returnType = this.sourceAttribute.getReturnType();
			Class<?> aliasedReturnType = this.aliasedAttribute.getReturnType();
			if (returnType != aliasedReturnType &&
					(!aliasedReturnType.isArray() || returnType != aliasedReturnType.getComponentType())) {
				String msg = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] " +
								"and attribute '%s' in annotation [%s] must declare the same return type.",
						this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName,
						this.aliasedAnnotationType.getName());
				throw new AnnotationConfigurationException(msg);
			}

			if (this.isAliasPair) {
				validateDefaultValueConfiguration(this.aliasedAttribute);
			}
		}

		private void validateDefaultValueConfiguration(Method aliasedAttribute) {
			Object defaultValue = this.sourceAttribute.getDefaultValue();
			Object aliasedDefaultValue = aliasedAttribute.getDefaultValue();

			if (defaultValue == null || aliasedDefaultValue == null) {
				String msg = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] " +
								"and attribute '%s' in annotation [%s] must declare default values.",
						this.sourceAttributeName, this.sourceAnnotationType.getName(), aliasedAttribute.getName(),
						aliasedAttribute.getDeclaringClass().getName());
				throw new AnnotationConfigurationException(msg);
			}

			if (!ObjectUtils.nullSafeEquals(defaultValue, aliasedDefaultValue)) {
				String msg = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] " +
								"and attribute '%s' in annotation [%s] must declare the same default value.",
						this.sourceAttributeName, this.sourceAnnotationType.getName(), aliasedAttribute.getName(),
						aliasedAttribute.getDeclaringClass().getName());
				throw new AnnotationConfigurationException(msg);
			}
		}

		/**
		 * Validate this descriptor against the supplied descriptor.
		 * <p>This method only validates the configuration of default values
		 * for the two descriptors, since other aspects of the descriptors
		 * are validated when they are created.
		 */
		private void validateAgainst(AliasDescriptor otherDescriptor) {
			validateDefaultValueConfiguration(otherDescriptor.sourceAttribute);
		}

		/**
		 * Determine if this descriptor represents an explicit override for
		 * an attribute in the supplied {@code metaAnnotationType}.
		 *
		 * @see #isAliasFor
		 */
		private boolean isOverrideFor(Class<? extends Annotation> metaAnnotationType) {
			return (this.aliasedAnnotationType == metaAnnotationType);
		}

		/**
		 * Determine if this descriptor and the supplied descriptor both
		 * effectively represent aliases for the same attribute in the same
		 * target annotation, either explicitly or implicitly.
		 * <p>This method searches the attribute override hierarchy, beginning
		 * with this descriptor, in order to detect implicit and transitively
		 * implicit aliases.
		 *
		 * @return {@code true} if this descriptor and the supplied descriptor
		 * effectively alias the same annotation attribute
		 * @see #isOverrideFor
		 */
		private boolean isAliasFor(AliasDescriptor otherDescriptor) {
			for (AliasDescriptor lhs = this; lhs != null; lhs = lhs.getAttributeOverrideDescriptor()) {
				for (AliasDescriptor rhs = otherDescriptor; rhs != null; rhs = rhs.getAttributeOverrideDescriptor()) {
					if (lhs.aliasedAttribute.equals(rhs.aliasedAttribute)) {
						return true;
					}
				}
			}
			return false;
		}

		public List<String> getAttributeAliasNames() {
			// Explicit alias pair?
			if (this.isAliasPair) {
				return Collections.singletonList(this.aliasedAttributeName);
			}

			// Else: search for implicit aliases
			List<String> aliases = new ArrayList<>();
			for (AliasDescriptor otherDescriptor : getOtherDescriptors()) {
				if (this.isAliasFor(otherDescriptor)) {
					this.validateAgainst(otherDescriptor);
					aliases.add(otherDescriptor.sourceAttributeName);
				}
			}
			return aliases;
		}

		private List<AliasDescriptor> getOtherDescriptors() {
			List<AliasDescriptor> otherDescriptors = new ArrayList<>();
			for (Method currentAttribute : getAttributeMethods(this.sourceAnnotationType)) {
				if (!this.sourceAttribute.equals(currentAttribute)) {
					AliasDescriptor otherDescriptor = AliasDescriptor.from(currentAttribute);
					if (otherDescriptor != null) {
						otherDescriptors.add(otherDescriptor);
					}
				}
			}
			return otherDescriptors;
		}

		@Nullable
		public String getAttributeOverrideName(Class<? extends Annotation> metaAnnotationType) {
			// Search the attribute override hierarchy, starting with the current attribute
			for (AliasDescriptor desc = this; desc != null; desc = desc.getAttributeOverrideDescriptor()) {
				if (desc.isOverrideFor(metaAnnotationType)) {
					return desc.aliasedAttributeName;
				}
			}

			// Else: explicit attribute override for a different meta-annotation
			return null;
		}

		@Nullable
		private AliasDescriptor getAttributeOverrideDescriptor() {
			if (this.isAliasPair) {
				return null;
			}
			return AliasDescriptor.from(this.aliasedAttribute);
		}

		/**
		 * Get the name of the aliased attribute configured via the supplied
		 * {@link AliasFor @AliasFor} annotation on the supplied {@code attribute},
		 * or the original attribute if no aliased one specified (indicating that
		 * the reference goes to a same-named attribute on a meta-annotation).
		 * <p>This method returns the value of either the {@code attribute}
		 * or {@code value} attribute of {@code @AliasFor}, ensuring that only
		 * one of the attributes has been declared while simultaneously ensuring
		 * that at least one of the attributes has been declared.
		 *
		 * @param aliasFor  the {@code @AliasFor} annotation from which to retrieve
		 *                  the aliased attribute name
		 * @param attribute the attribute that is annotated with {@code @AliasFor}
		 * @return the name of the aliased attribute (never {@code null} or empty)
		 * @throws AnnotationConfigurationException if invalid configuration of
		 *                                          {@code @AliasFor} is detected
		 */
		private String getAliasedAttributeName(AliasFor aliasFor, Method attribute) {
			String attributeName = aliasFor.attribute();
			String value = aliasFor.value();
			boolean attributeDeclared = StringUtils.hasText(attributeName);
			boolean valueDeclared = StringUtils.hasText(value);

			// Ensure user did not declare both 'value' and 'attribute' in @AliasFor
			if (attributeDeclared && valueDeclared) {
				String msg = String.format(
						"In @AliasFor declared on attribute '%s' in annotation [%s], attribute 'attribute' " +
								"and its alias 'value' are present with values of [%s] and [%s], but only one is permitted.",
						attribute.getName(), attribute.getDeclaringClass().getName(), attributeName, value);
				throw new AnnotationConfigurationException(msg);
			}

			// Either explicit attribute name or pointing to same-named attribute by default
			attributeName = (attributeDeclared ? attributeName : value);
			return (StringUtils.hasText(attributeName) ? attributeName.trim() : attribute.getName());
		}

		@Override
		public String toString() {
			return String.format("%s: @%s(%s) is an alias for @%s(%s)", getClass().getSimpleName(),
					this.sourceAnnotationType.getSimpleName(), this.sourceAttributeName,
					this.aliasedAnnotationType.getSimpleName(), this.aliasedAttributeName);
		}
	}

	/**
	 * Determine whether the given class is a candidate for carrying one of the specified
	 * annotations (at type, method or field level).
	 *
	 * @param clazz           the class to introspect
	 * @param annotationTypes the searchable annotation types
	 * @return {@code false} if the class is known to have no such annotations at any level;
	 * {@code true} otherwise. Callers will usually perform full method/field introspection
	 * if {@code true} is being returned here.
	 * @see #isCandidateClass(Class, Class)
	 * @see #isCandidateClass(Class, String)
	 * @since 5.2
	 */
	public static boolean isCandidateClass(Class<?> clazz, Collection<Class<? extends Annotation>> annotationTypes) {
		for (Class<? extends Annotation> annotationType : annotationTypes) {
			if (isCandidateClass(clazz, annotationType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <em>Synthesize</em> an array of annotations from the supplied array
	 * of {@code maps} of annotation attributes by creating a new array of
	 * {@code annotationType} with the same size and populating it with
	 * {@linkplain #synthesizeAnnotation(Map, Class, AnnotatedElement)
	 * synthesized} versions of the maps from the input array.
	 *
	 * @param maps           the array of maps of annotation attributes to synthesize
	 * @param annotationType the type of annotations to synthesize
	 *                       (never {@code null})
	 * @return a new array of synthesized annotations, or {@code null} if
	 * the supplied array is {@code null}
	 * @throws AnnotationConfigurationException if invalid configuration of
	 *                                          {@code @AliasFor} is detected
	 * @see #synthesizeAnnotation(Map, Class, AnnotatedElement)
	 * @see #synthesizeAnnotationArray(Annotation[], Object)
	 * @since 4.2.1
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	static <A extends Annotation> A[] synthesizeAnnotationArray(
			@Nullable Map<String, Object>[] maps, Class<A> annotationType) {

		if (maps == null) {
			return null;
		}

		A[] synthesized = (A[]) Array.newInstance(annotationType, maps.length);
		for (int i = 0; i < maps.length; i++) {
			synthesized[i] = synthesizeAnnotation(maps[i], annotationType, null);
		}
		return synthesized;
	}

	/**
	 * Determine whether the given class is a candidate for carrying the specified annotation
	 * (at type, method or field level).
	 *
	 * @param clazz          the class to introspect
	 * @param annotationType the searchable annotation type
	 * @return {@code false} if the class is known to have no such annotations at any level;
	 * {@code true} otherwise. Callers will usually perform full method/field introspection
	 * if {@code true} is being returned here.
	 * @see #isCandidateClass(Class, String)
	 * @since 5.2
	 */
	public static boolean isCandidateClass(Class<?> clazz, Class<? extends Annotation> annotationType) {
		return isCandidateClass(clazz, annotationType.getName());
	}

	/**
	 * Determine whether the given class is a candidate for carrying the specified annotation
	 * (at type, method or field level).
	 *
	 * @param clazz          the class to introspect
	 * @param annotationName the fully-qualified name of the searchable annotation type
	 * @return {@code false} if the class is known to have no such annotations at any level;
	 * {@code true} otherwise. Callers will usually perform full method/field introspection
	 * if {@code true} is being returned here.
	 * @see #isCandidateClass(Class, Class)
	 * @since 5.2
	 */
	public static boolean isCandidateClass(Class<?> clazz, String annotationName) {
		if (annotationName.startsWith("java.")) {
			return true;
		}
		return !AnnotationsScanner.hasPlainJavaAnnotationsOnly(clazz);
	}

	/**
	 * Determine if the supplied method is an "annotationType" method.
	 *
	 * @return {@code true} if the method is an "annotationType" method
	 * @see Annotation#annotationType()
	 * @since 4.2
	 */
	static boolean isAnnotationTypeMethod(@Nullable Method method) {
		return (method != null && method.getName().equals("annotationType") && method.getParameterCount() == 0);
	}

	/**
	 * Get the annotation with the supplied {@code annotationName} on the
	 * supplied {@code element}.
	 *
	 * @param element        the element to search on
	 * @param annotationName the fully qualified class name of the annotation
	 *                       type to find
	 * @return the annotation if found; {@code null} otherwise
	 * @since 4.2
	 */
	@Nullable
	static Annotation getAnnotation(AnnotatedElement element, String annotationName) {
		for (Annotation annotation : element.getAnnotations()) {
			if (annotation.annotationType().getName().equals(annotationName)) {
				return annotation;
			}
		}
		return null;
	}

	/**
	 * Get a single {@link java.lang.annotation.Annotation} of {@code annotationType} from the supplied
	 * annotation: either the given annotation itself or a direct meta-annotation
	 * thereof.
	 * <p>Note that this method supports only a single level of meta-annotations.
	 * For support for arbitrary levels of meta-annotations, use one of the
	 * {@code find*()} methods instead.
	 *
	 * @param annotation     the Annotation to check
	 * @param annotationType the annotation type to look for, both locally and as a meta-annotation
	 * @return the first matching annotation, or {@code null} if not found
	 * @since 4.0
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static <A extends Annotation> A getAnnotation(Annotation annotation, Class<A> annotationType) {
		// Shortcut: directly present on the element, with no merging needed?
		if (annotationType.isInstance(annotation)) {
			return synthesizeAnnotation((A) annotation, annotationType);
		}
		// Shortcut: no searchable annotations to be found on plain Java classes and core Spring types...
		if (AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotation)) {
			return null;
		}
		// Exhaustive retrieval of merged annotations...
		return MergedAnnotations.from(annotation, new Annotation[]{annotation}, RepeatableContainers.none())
				.get(annotationType).withNonMergedAttributes()
				.synthesize(AnnotationUtils::isSingleLevelPresent).orElse(null);
	}

	/**
	 * Get a single {@link java.lang.annotation.Annotation} of {@code annotationType} from the supplied
	 * {@link java.lang.reflect.AnnotatedElement}, where the annotation is either <em>present</em> or
	 * <em>meta-present</em> on the {@code AnnotatedElement}.
	 * <p>Note that this method supports only a single level of meta-annotations.
	 * For support for arbitrary levels of meta-annotations, use
	 * {@link #findAnnotation(java.lang.reflect.AnnotatedElement, Class)} instead.
	 *
	 * @param annotatedElement the {@code AnnotatedElement} from which to get the annotation
	 * @param annotationType   the annotation type to look for, both locally and as a meta-annotation
	 * @return the first matching annotation, or {@code null} if not found
	 * @since 3.1
	 */
	@Nullable
	public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
		// Shortcut: directly present on the element, with no merging needed?
		if (AnnotationFilter.PLAIN.matches(annotationType) ||
				AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
			return annotatedElement.getAnnotation(annotationType);
		}
		// Exhaustive retrieval of merged annotations...
		return MergedAnnotations.from(annotatedElement, SearchStrategy.INHERITED_ANNOTATIONS,
						RepeatableContainers.none())
				.get(annotationType).withNonMergedAttributes()
				.synthesize(AnnotationUtils::isSingleLevelPresent).orElse(null);
	}

	private static <A extends Annotation> boolean isSingleLevelPresent(MergedAnnotation<A> mergedAnnotation) {
		int distance = mergedAnnotation.getDistance();
		return (distance == 0 || distance == 1);
	}

	/**
	 * Get a single {@link java.lang.annotation.Annotation} of {@code annotationType} from the
	 * supplied {@link java.lang.reflect.Method}, where the annotation is either <em>present</em>
	 * or <em>meta-present</em> on the method.
	 * <p>Correctly handles bridge {@link java.lang.reflect.Method Methods} generated by the compiler.
	 * <p>Note that this method supports only a single level of meta-annotations.
	 * For support for arbitrary levels of meta-annotations, use
	 * {@link #findAnnotation(java.lang.reflect.Method, Class)} instead.
	 * @param method the method to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @return the first matching annotation, or {@code null} if not found
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod(java.lang.reflect.Method)
	 * @see #getAnnotation(java.lang.reflect.AnnotatedElement, Class)
	 */
	@Nullable
	public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
		Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
		return getAnnotation((AnnotatedElement) resolvedMethod, annotationType);
	}

	/**
	 * Get all {@link java.lang.annotation.Annotation Annotations} that are <em>present</em> on the
	 * supplied {@link java.lang.reflect.AnnotatedElement}.
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * @param annotatedElement the Method, Constructor or Field to retrieve annotations from
	 * @return the annotations found, an empty array, or {@code null} if not
	 * resolvable (e.g. because nested Class values in annotation attributes
	 * failed to resolve at runtime)
	 * @since 4.0.8
	 * @see java.lang.reflect.AnnotatedElement#getAnnotations()
	 * @deprecated as of 5.2 since it is superseded by the {@link MergedAnnotations} API
	 */
	@Deprecated
	@Nullable
	public static Annotation[] getAnnotations(AnnotatedElement annotatedElement) {
		try {
			return synthesizeAnnotationArray(annotatedElement.getAnnotations(), annotatedElement);
		} catch (Throwable ex) {
			handleIntrospectionFailure(annotatedElement, ex);
			return null;
		}
	}

	/**
	 * Get all {@link java.lang.annotation.Annotation Annotations} that are <em>present</em> on the
	 * supplied {@link java.lang.reflect.Method}.
	 * <p>Correctly handles bridge {@link java.lang.reflect.Method Methods} generated by the compiler.
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * @param method the Method to retrieve annotations from
	 * @return the annotations found, an empty array, or {@code null} if not
	 * resolvable (e.g. because nested Class values in annotation attributes
	 * failed to resolve at runtime)
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod(java.lang.reflect.Method)
	 * @see java.lang.reflect.AnnotatedElement#getAnnotations()
	 * @deprecated as of 5.2 since it is superseded by the {@link MergedAnnotations} API
	 */
	@Deprecated
	@Nullable
	public static Annotation[] getAnnotations(Method method) {
		try {
			return synthesizeAnnotationArray(BridgeMethodResolver.findBridgedMethod(method).getAnnotations(), method);
		} catch (Throwable ex) {
			handleIntrospectionFailure(method, ex);
			return null;
		}
	}

	/**
	 * Get the <em>repeatable</em> {@linkplain java.lang.annotation.Annotation annotations} of
	 * {@code annotationType} from the supplied {@link java.lang.reflect.AnnotatedElement}, where
	 * such annotations are either <em>present</em>, <em>indirectly present</em>,
	 * or <em>meta-present</em> on the element.
	 * <p>This method mimics the functionality of Java 8's
	 * {@link java.lang.reflect.AnnotatedElement#getAnnotationsByType(Class)}
	 * with support for automatic detection of a <em>container annotation</em>
	 * declared via @{@link java.lang.annotation.Repeatable} (when running on
	 * Java 8 or higher) and with additional support for meta-annotations.
	 * <p>Handles both single annotations and annotations nested within a
	 * <em>container annotation</em>.
	 * <p>Correctly handles <em>bridge methods</em> generated by the
	 * compiler if the supplied element is a {@link java.lang.reflect.Method}.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>present</em> on the supplied element.
	 * @param annotatedElement the element to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @return the annotations found or an empty set (never {@code null})
	 * @since 4.2
	 * @see #getRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class, Class)
	 * @see #getDeclaredRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class, Class)
	 * @see AnnotatedElementUtils#getMergedRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class)
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod
	 * @see java.lang.annotation.Repeatable
	 * @see java.lang.reflect.AnnotatedElement#getAnnotationsByType
	 * @deprecated as of 5.2 since it is superseded by the {@link MergedAnnotations} API
	 */
	@Deprecated
	public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement,
																		 Class<A> annotationType) {

		return getRepeatableAnnotations(annotatedElement, annotationType, null);
	}

	/**
	 * Get the <em>repeatable</em> {@linkplain java.lang.annotation.Annotation annotations} of
	 * {@code annotationType} from the supplied {@link java.lang.reflect.AnnotatedElement}, where
	 * such annotations are either <em>present</em>, <em>indirectly present</em>,
	 * or <em>meta-present</em> on the element.
	 * <p>This method mimics the functionality of Java 8's
	 * {@link java.lang.reflect.AnnotatedElement#getAnnotationsByType(Class)}
	 * with additional support for meta-annotations.
	 * <p>Handles both single annotations and annotations nested within a
	 * <em>container annotation</em>.
	 * <p>Correctly handles <em>bridge methods</em> generated by the
	 * compiler if the supplied element is a {@link java.lang.reflect.Method}.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>present</em> on the supplied element.
	 *
	 * @param annotatedElement        the element to look for annotations on
	 * @param annotationType          the annotation type to look for
	 * @param containerAnnotationType the type of the container that holds
	 *                                the annotations; may be {@code null} if a container is not supported
	 *                                or if it should be looked up via @{@link java.lang.annotation.Repeatable}
	 *                                when running on Java 8 or higher
	 * @return the annotations found or an empty set (never {@code null})
	 * @see #getRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class)
	 * @see #getDeclaredRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class)
	 * @see #getDeclaredRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class, Class)
	 * @see AnnotatedElementUtils#getMergedRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class, Class)
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod
	 * @see java.lang.annotation.Repeatable
	 * @see java.lang.reflect.AnnotatedElement#getAnnotationsByType
	 * @since 4.2
	 * @deprecated as of 5.2 since it is superseded by the {@link MergedAnnotations} API
	 */
	@Deprecated
	public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement,
																		 Class<A> annotationType,
																		 @Nullable Class<? extends Annotation> containerAnnotationType) {

		RepeatableContainers repeatableContainers = (containerAnnotationType != null ?
				RepeatableContainers.of(annotationType, containerAnnotationType) :
				RepeatableContainers.standardRepeatables());

		return MergedAnnotations.from(annotatedElement, SearchStrategy.SUPERCLASS, repeatableContainers)
				.stream(annotationType)
				.filter(MergedAnnotationPredicates.firstRunOf(MergedAnnotation::getAggregateIndex))
				.map(MergedAnnotation::withNonMergedAttributes)
				.collect(MergedAnnotationCollectors.toAnnotationSet());
	}

	/**
	 * Get the declared <em>repeatable</em> {@linkplain java.lang.annotation.Annotation annotations}
	 * of {@code annotationType} from the supplied {@link java.lang.reflect.AnnotatedElement},
	 * where such annotations are either <em>directly present</em>,
	 * <em>indirectly present</em>, or <em>meta-present</em> on the element.
	 * <p>This method mimics the functionality of Java 8's
	 * {@link java.lang.reflect.AnnotatedElement#getDeclaredAnnotationsByType(Class)}
	 * with support for automatic detection of a <em>container annotation</em>
	 * declared via @{@link java.lang.annotation.Repeatable} (when running on
	 * Java 8 or higher) and with additional support for meta-annotations.
	 * <p>Handles both single annotations and annotations nested within a
	 * <em>container annotation</em>.
	 * <p>Correctly handles <em>bridge methods</em> generated by the
	 * compiler if the supplied element is a {@link java.lang.reflect.Method}.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>present</em> on the supplied element.
	 * @param annotatedElement the element to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @return the annotations found or an empty set (never {@code null})
	 * @since 4.2
	 * @see #getRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class)
	 * @see #getRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class, Class)
	 * @see #getDeclaredRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class, Class)
	 * @see AnnotatedElementUtils#getMergedRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class)
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod
	 * @see java.lang.annotation.Repeatable
	 * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotationsByType
	 * @deprecated as of 5.2 since it is superseded by the {@link MergedAnnotations} API
	 */
	@Deprecated
	public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement,
																				 Class<A> annotationType) {

		return getDeclaredRepeatableAnnotations(annotatedElement, annotationType, null);
	}

	/**
	 * Get the declared <em>repeatable</em> {@linkplain java.lang.annotation.Annotation annotations}
	 * of {@code annotationType} from the supplied {@link java.lang.reflect.AnnotatedElement},
	 * where such annotations are either <em>directly present</em>,
	 * <em>indirectly present</em>, or <em>meta-present</em> on the element.
	 * <p>This method mimics the functionality of Java 8's
	 * {@link java.lang.reflect.AnnotatedElement#getDeclaredAnnotationsByType(Class)}
	 * with additional support for meta-annotations.
	 * <p>Handles both single annotations and annotations nested within a
	 * <em>container annotation</em>.
	 * <p>Correctly handles <em>bridge methods</em> generated by the
	 * compiler if the supplied element is a {@link java.lang.reflect.Method}.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>present</em> on the supplied element.
	 * @param annotatedElement the element to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @param containerAnnotationType the type of the container that holds
	 * the annotations; may be {@code null} if a container is not supported
	 * or if it should be looked up via @{@link java.lang.annotation.Repeatable}
	 * when running on Java 8 or higher
	 * @return the annotations found or an empty set (never {@code null})
	 * @since 4.2
	 * @see #getRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class)
	 * @see #getRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class, Class)
	 * @see #getDeclaredRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class)
	 * @see AnnotatedElementUtils#getMergedRepeatableAnnotations(java.lang.reflect.AnnotatedElement, Class, Class)
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod
	 * @see java.lang.annotation.Repeatable
	 * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotationsByType
	 * @deprecated as of 5.2 since it is superseded by the {@link MergedAnnotations} API
	 */
	@Deprecated
	public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement,
																				 Class<A> annotationType,
																				 @Nullable Class<? extends Annotation> containerAnnotationType) {

		RepeatableContainers repeatableContainers = containerAnnotationType != null ?
				RepeatableContainers.of(annotationType, containerAnnotationType) :
				RepeatableContainers.standardRepeatables();

		return MergedAnnotations.from(annotatedElement, SearchStrategy.DIRECT, repeatableContainers)
				.stream(annotationType)
				.map(MergedAnnotation::withNonMergedAttributes)
				.collect(MergedAnnotationCollectors.toAnnotationSet());
	}

	/**
	 * Find a single {@link java.lang.annotation.Annotation} of {@code annotationType} on the
	 * supplied {@link java.lang.reflect.AnnotatedElement}.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>directly present</em> on the supplied element.
	 * <p><strong>Warning</strong>: this method operates generically on
	 * annotated elements. In other words, this method does not execute
	 * specialized search algorithms for classes or methods. If you require
	 * the more specific semantics of {@link #findAnnotation(Class, Class)}
	 * or {@link #findAnnotation(java.lang.reflect.Method, Class)}, invoke one of those methods
	 * instead.
	 * @param annotatedElement the {@code AnnotatedElement} on which to find the annotation
	 * @param annotationType the annotation type to look for, both locally and as a meta-annotation
	 * @return the first matching annotation, or {@code null} if not found
	 * @since 4.2
	 */
	@Nullable
	public static <A extends Annotation> A findAnnotation(
			AnnotatedElement annotatedElement, @Nullable Class<A> annotationType) {

		if (annotationType == null) {
			return null;
		}

		// Shortcut: directly present on the element, with no merging needed?
		if (AnnotationFilter.PLAIN.matches(annotationType) ||
				AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
			return annotatedElement.getDeclaredAnnotation(annotationType);
		}

		// Exhaustive retrieval of merged annotations...
		return MergedAnnotations.from(annotatedElement, SearchStrategy.INHERITED_ANNOTATIONS,
						RepeatableContainers.none())
				.get(annotationType).withNonMergedAttributes()
				.synthesize(MergedAnnotation::isPresent).orElse(null);
	}

	/**
	 * Find a single {@link java.lang.annotation.Annotation} of {@code annotationType} on the supplied
	 * {@link java.lang.reflect.Method}, traversing its super methods (i.e. from superclasses and
	 * interfaces) if the annotation is not <em>directly present</em> on the given
	 * method itself.
	 * <p>Correctly handles bridge {@link java.lang.reflect.Method Methods} generated by the compiler.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>directly present</em> on the method.
	 * <p>Annotations on methods are not inherited by default, so we need to handle
	 * this explicitly.
	 * @param method the method to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @return the first matching annotation, or {@code null} if not found
	 * @see #getAnnotation(java.lang.reflect.Method, Class)
	 */
	@Nullable
	public static <A extends Annotation> A findAnnotation(Method method, @Nullable Class<A> annotationType) {
		if (annotationType == null) {
			return null;
		}

		// Shortcut: directly present on the element, with no merging needed?
		if (AnnotationFilter.PLAIN.matches(annotationType) ||
				AnnotationsScanner.hasPlainJavaAnnotationsOnly(method)) {
			return method.getDeclaredAnnotation(annotationType);
		}

		// Exhaustive retrieval of merged annotations...
		return MergedAnnotations.from(method, SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none())
				.get(annotationType).withNonMergedAttributes()
				.synthesize(MergedAnnotation::isPresent).orElse(null);
	}

	/**
	 * Find a single {@link java.lang.annotation.Annotation} of {@code annotationType} on the
	 * supplied {@link Class}, traversing its interfaces, annotations, and
	 * superclasses if the annotation is not <em>directly present</em> on
	 * the given class itself.
	 * <p>This method explicitly handles class-level annotations which are not
	 * declared as {@link java.lang.annotation.Inherited inherited} <em>as well
	 * as meta-annotations and annotations on interfaces</em>.
	 * <p>The algorithm operates as follows:
	 * <ol>
	 * <li>Search for the annotation on the given class and return it if found.
	 * <li>Recursively search through all annotations that the given class declares.
	 * <li>Recursively search through all interfaces that the given class declares.
	 * <li>Recursively search through the superclass hierarchy of the given class.
	 * </ol>
	 * <p>Note: in this context, the term <em>recursively</em> means that the search
	 * process continues by returning to step #1 with the current interface,
	 * annotation, or superclass as the class to look for annotations on.
	 * @param clazz the class to look for annotations on
	 * @param annotationType the type of annotation to look for
	 * @return the first matching annotation, or {@code null} if not found
	 */
	@Nullable
	public static <A extends Annotation> A findAnnotation(Class<?> clazz, @Nullable Class<A> annotationType) {
		if (annotationType == null) {
			return null;
		}

		// Shortcut: directly present on the element, with no merging needed?
		if (AnnotationFilter.PLAIN.matches(annotationType) ||
				AnnotationsScanner.hasPlainJavaAnnotationsOnly(clazz)) {
			A annotation = clazz.getDeclaredAnnotation(annotationType);
			if (annotation != null) {
				return annotation;
			}
			// For backwards compatibility, perform a superclass search with plain annotations
			// even if not marked as @Inherited: e.g. a findAnnotation search for @Deprecated
			Class<?> superclass = clazz.getSuperclass();
			if (superclass == null || superclass == Object.class) {
				return null;
			}
			return findAnnotation(superclass, annotationType);
		}

		// Exhaustive retrieval of merged annotations...
		return MergedAnnotations.from(clazz, SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none())
				.get(annotationType).withNonMergedAttributes()
				.synthesize(MergedAnnotation::isPresent).orElse(null);
	}

	/**
	 * Find the first {@link Class} in the inheritance hierarchy of the
	 * specified {@code clazz} (including the specified {@code clazz} itself)
	 * on which an annotation of the specified {@code annotationType} is
	 * <em>directly present</em>.
	 * <p>If the supplied {@code clazz} is an interface, only the interface
	 * itself will be checked; the inheritance hierarchy for interfaces will
	 * not be traversed.
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * <p>The standard {@link Class} API does not provide a mechanism for
	 * determining which class in an inheritance hierarchy actually declares
	 * an {@link java.lang.annotation.Annotation}, so we need to handle this explicitly.
	 * @param annotationType the annotation type to look for
	 * @param clazz the class to check for the annotation on (may be {@code null})
	 * @return the first {@link Class} in the inheritance hierarchy that
	 * declares an annotation of the specified {@code annotationType},
	 * or {@code null} if not found
	 * @see Class#isAnnotationPresent(Class)
	 * @see Class#getDeclaredAnnotations()
	 * @deprecated as of 5.2 since it is superseded by the {@link MergedAnnotations} API
	 */
	@Deprecated
	@Nullable
	public static Class<?> findAnnotationDeclaringClass(
			Class<? extends Annotation> annotationType, @Nullable Class<?> clazz) {

		if (clazz == null) {
			return null;
		}

		return (Class<?>) MergedAnnotations.from(clazz, SearchStrategy.SUPERCLASS)
				.get(annotationType, MergedAnnotation::isDirectlyPresent)
				.getSource();
	}

	/**
	 * Find the first {@link Class} in the inheritance hierarchy of the
	 * specified {@code clazz} (including the specified {@code clazz} itself)
	 * on which at least one of the specified {@code annotationTypes} is
	 * <em>directly present</em>.
	 * <p>If the supplied {@code clazz} is an interface, only the interface
	 * itself will be checked; the inheritance hierarchy for interfaces will
	 * not be traversed.
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * <p>The standard {@link Class} API does not provide a mechanism for
	 * determining which class in an inheritance hierarchy actually declares
	 * one of several candidate {@linkplain java.lang.annotation.Annotation annotations}, so we
	 * need to handle this explicitly.
	 * @param annotationTypes the annotation types to look for
	 * @param clazz the class to check for the annotation on (may be {@code null})
	 * @return the first {@link Class} in the inheritance hierarchy that
	 * declares an annotation of at least one of the specified
	 * {@code annotationTypes}, or {@code null} if not found
	 * @since 3.2.2
	 * @see Class#isAnnotationPresent(Class)
	 * @see Class#getDeclaredAnnotations()
	 * @deprecated as of 5.2 since it is superseded by the {@link MergedAnnotations} API
	 */
	@Deprecated
	@Nullable
	public static Class<?> findAnnotationDeclaringClassForTypes(
			List<Class<? extends Annotation>> annotationTypes, @Nullable Class<?> clazz) {

		if (clazz == null) {
			return null;
		}

		return (Class<?>) MergedAnnotations.from(clazz, SearchStrategy.SUPERCLASS)
				.stream()
				.filter(MergedAnnotationPredicates.typeIn(annotationTypes).and(MergedAnnotation::isDirectlyPresent))
				.map(MergedAnnotation::getSource)
				.findFirst().orElse(null);
	}

	/**
	 * Determine whether an annotation of the specified {@code annotationType}
	 * is declared locally (i.e. <em>directly present</em>) on the supplied
	 * {@code clazz}.
	 * <p>The supplied {@link Class} may represent any type.
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * <p>Note: This method does <strong>not</strong> determine if the annotation
	 * is {@linkplain java.lang.annotation.Inherited inherited}.
	 * @param annotationType the annotation type to look for
	 * @param clazz the class to check for the annotation on
	 * @return {@code true} if an annotation of the specified {@code annotationType}
	 * is <em>directly present</em>
	 * @see Class#getDeclaredAnnotations()
	 * @see Class#getDeclaredAnnotation(Class)
	 */
	public static boolean isAnnotationDeclaredLocally(Class<? extends Annotation> annotationType, Class<?> clazz) {
		return MergedAnnotations.from(clazz).get(annotationType).isDirectlyPresent();
	}

	/**
	 * Determine whether an annotation of the specified {@code annotationType}
	 * is <em>present</em> on the supplied {@code clazz} and is
	 * {@linkplain java.lang.annotation.Inherited inherited}
	 * (i.e. not <em>directly present</em>).
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * <p>If the supplied {@code clazz} is an interface, only the interface
	 * itself will be checked. In accordance with standard meta-annotation
	 * semantics in Java, the inheritance hierarchy for interfaces will not
	 * be traversed. See the {@linkplain java.lang.annotation.Inherited javadoc}
	 * for the {@code @Inherited} meta-annotation for further details regarding
	 * annotation inheritance.
	 * @param annotationType the annotation type to look for
	 * @param clazz the class to check for the annotation on
	 * @return {@code true} if an annotation of the specified {@code annotationType}
	 * is <em>present</em> and <em>inherited</em>
	 * @see Class#isAnnotationPresent(Class)
	 * @see #isAnnotationDeclaredLocally(Class, Class)
	 * @deprecated as of 5.2 since it is superseded by the {@link MergedAnnotations} API
	 */
	@Deprecated
	public static boolean isAnnotationInherited(Class<? extends Annotation> annotationType, Class<?> clazz) {
		return MergedAnnotations.from(clazz, SearchStrategy.INHERITED_ANNOTATIONS)
				.stream(annotationType)
				.filter(MergedAnnotation::isDirectlyPresent)
				.findFirst().orElseGet(MergedAnnotation::missing)
				.getAggregateIndex() > 0;
	}

	/**
	 * Determine if an annotation of type {@code metaAnnotationType} is
	 * <em>meta-present</em> on the supplied {@code annotationType}.
	 * @param annotationType the annotation type to search on
	 * @param metaAnnotationType the type of meta-annotation to search for
	 * @return {@code true} if such an annotation is meta-present
	 * @since 4.2.1
	 * @deprecated as of 5.2 since it is superseded by the {@link MergedAnnotations} API
	 */
	@Deprecated
	public static boolean isAnnotationMetaPresent(Class<? extends Annotation> annotationType,
												  @Nullable Class<? extends Annotation> metaAnnotationType) {

		if (metaAnnotationType == null) {
			return false;
		}
		// Shortcut: directly present on the element, with no merging needed?
		if (AnnotationFilter.PLAIN.matches(metaAnnotationType) ||
				AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotationType)) {
			return annotationType.isAnnotationPresent(metaAnnotationType);
		}
		// Exhaustive retrieval of merged annotations...
		return MergedAnnotations.from(annotationType, SearchStrategy.INHERITED_ANNOTATIONS,
				RepeatableContainers.none()).isPresent(metaAnnotationType);
	}

	/**
	 * Determine if the supplied {@link java.lang.annotation.Annotation} is defined in the core JDK
	 * {@code java.lang.annotation} package.
	 * @param annotation the annotation to check
	 * @return {@code true} if the annotation is in the {@code java.lang.annotation} package
	 */
	public static boolean isInJavaLangAnnotationPackage(@Nullable Annotation annotation) {
		return (annotation != null && JAVA_LANG_ANNOTATION_FILTER.matches(annotation));
	}

	/**
	 * Determine if the {@link java.lang.annotation.Annotation} with the supplied name is defined
	 * in the core JDK {@code java.lang.annotation} package.
	 *
	 * @param annotationType the name of the annotation type to check
	 * @return {@code true} if the annotation is in the {@code java.lang.annotation} package
	 * @since 4.2
	 */
	public static boolean isInJavaLangAnnotationPackage(@Nullable String annotationType) {
		return (annotationType != null && JAVA_LANG_ANNOTATION_FILTER.matches(annotationType));
	}

	/**
	 * Check the declared attributes of the given annotation, in particular covering
	 * Google App Engine's late arrival of {@code TypeNotPresentExceptionProxy} for
	 * {@code Class} values (instead of early {@code Class.getAnnotations() failure}.
	 * <p>This method not failing indicates that {@link #getAnnotationAttributes(java.lang.annotation.Annotation)}
	 * won't failure either (when attempted later on).
	 *
	 * @param annotation the annotation to validate
	 * @throws IllegalStateException if a declared {@code Class} attribute could not be read
	 * @see Class#getAnnotations()
	 * @see #getAnnotationAttributes(java.lang.annotation.Annotation)
	 * @since 4.3.15
	 */
	public static void validateAnnotation(Annotation annotation) {
		AttributeMethods.forAnnotationType(annotation.annotationType()).validate(annotation);
	}

	/**
	 * Retrieve the given annotation's attributes as an {@link AnnotationAttributes} map.
	 * <p>This method provides fully recursive annotation reading capabilities on par with
	 * the reflection-based {@link org.springframework.core.type.StandardAnnotationMetadata}.
	 *
	 * @param annotation             the annotation to retrieve the attributes for
	 * @param classValuesAsString    whether to convert Class references into Strings (for
	 *                               compatibility with {@link org.springframework.core.type.AnnotationMetadata})
	 *                               or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested annotations into
	 *                               {@link AnnotationAttributes} maps (for compatibility with
	 *                               {@link org.springframework.core.type.AnnotationMetadata}) or to preserve them as
	 *                               {@code Annotation} instances
	 * @return the annotation attributes (a specialized Map) with attribute names as keys
	 * and corresponding attribute values as values (never {@code null})
	 * @since 3.1.1
	 */
	public static AnnotationAttributes getAnnotationAttributes(
			Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		return getAnnotationAttributes(null, annotation, classValuesAsString, nestedAnnotationsAsMap);
	}

	/**
	 * Retrieve the given annotation's attributes as a {@link java.util.Map}, preserving all
	 * attribute types.
	 * <p>Equivalent to calling {@link #getAnnotationAttributes(java.lang.annotation.Annotation, boolean, boolean)}
	 * with the {@code classValuesAsString} and {@code nestedAnnotationsAsMap} parameters
	 * set to {@code false}.
	 * <p>Note: This method actually returns an {@link AnnotationAttributes} instance.
	 * However, the {@code Map} signature has been preserved for binary compatibility.
	 * @param annotation the annotation to retrieve the attributes for
	 * @return the Map of annotation attributes, with attribute names as keys and
	 * corresponding attribute values as values (never {@code null})
	 * @see #getAnnotationAttributes(java.lang.reflect.AnnotatedElement, java.lang.annotation.Annotation)
	 * @see #getAnnotationAttributes(java.lang.annotation.Annotation, boolean, boolean)
	 * @see #getAnnotationAttributes(java.lang.reflect.AnnotatedElement, java.lang.annotation.Annotation, boolean, boolean)
	 */
	public static Map<String, Object> getAnnotationAttributes(Annotation annotation) {
		return getAnnotationAttributes(null, annotation);
	}

	/**
	 * Retrieve the given annotation's attributes as a {@link java.util.Map}.
	 * <p>Equivalent to calling {@link #getAnnotationAttributes(java.lang.annotation.Annotation, boolean, boolean)}
	 * with the {@code nestedAnnotationsAsMap} parameter set to {@code false}.
	 * <p>Note: This method actually returns an {@link AnnotationAttributes} instance.
	 * However, the {@code Map} signature has been preserved for binary compatibility.
	 * @param annotation the annotation to retrieve the attributes for
	 * @param classValuesAsString whether to convert Class references into Strings (for
	 * compatibility with {@link org.springframework.core.type.AnnotationMetadata})
	 * or to preserve them as Class references
	 * @return the Map of annotation attributes, with attribute names as keys and
	 * corresponding attribute values as values (never {@code null})
	 * @see #getAnnotationAttributes(java.lang.annotation.Annotation, boolean, boolean)
	 */
	public static Map<String, Object> getAnnotationAttributes(
			Annotation annotation, boolean classValuesAsString) {

		return getAnnotationAttributes(annotation, classValuesAsString, false);
	}

	/**
	 * Retrieve the given annotation's attributes as an {@link AnnotationAttributes} map.
	 * <p>Equivalent to calling {@link #getAnnotationAttributes(java.lang.reflect.AnnotatedElement, java.lang.annotation.Annotation, boolean, boolean)}
	 * with the {@code classValuesAsString} and {@code nestedAnnotationsAsMap} parameters
	 * set to {@code false}.
	 * @param annotatedElement the element that is annotated with the supplied annotation;
	 * may be {@code null} if unknown
	 * @param annotation the annotation to retrieve the attributes for
	 * @return the annotation attributes (a specialized Map) with attribute names as keys
	 * and corresponding attribute values as values (never {@code null})
	 * @since 4.2
	 * @see #getAnnotationAttributes(java.lang.reflect.AnnotatedElement, java.lang.annotation.Annotation, boolean, boolean)
	 */
	public static AnnotationAttributes getAnnotationAttributes(
			@Nullable AnnotatedElement annotatedElement, Annotation annotation) {

		return getAnnotationAttributes(annotatedElement, annotation, false, false);
	}

	/**
	 * Retrieve the given annotation's attributes as an {@link AnnotationAttributes} map.
	 * <p>This method provides fully recursive annotation reading capabilities on par with
	 * the reflection-based {@link org.springframework.core.type.StandardAnnotationMetadata}.
	 *
	 * @param annotatedElement       the element that is annotated with the supplied annotation;
	 *                               may be {@code null} if unknown
	 * @param annotation             the annotation to retrieve the attributes for
	 * @param classValuesAsString    whether to convert Class references into Strings (for
	 *                               compatibility with {@link org.springframework.core.type.AnnotationMetadata})
	 *                               or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested annotations into
	 *                               {@link AnnotationAttributes} maps (for compatibility with
	 *                               {@link org.springframework.core.type.AnnotationMetadata}) or to preserve them as
	 *                               {@code Annotation} instances
	 * @return the annotation attributes (a specialized Map) with attribute names as keys
	 * and corresponding attribute values as values (never {@code null})
	 * @since 4.2
	 */
	public static AnnotationAttributes getAnnotationAttributes(
			@Nullable AnnotatedElement annotatedElement, Annotation annotation,
			boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		Adapt[] adaptations = Adapt.values(classValuesAsString, nestedAnnotationsAsMap);
		return MergedAnnotation.from(annotatedElement, annotation)
				.withNonMergedAttributes()
				.asMap(mergedAnnotation ->
						new AnnotationAttributes(mergedAnnotation.getType(), true), adaptations);
	}

	/**
	 * Register the annotation-declared default values for the given attributes,
	 * if available.
	 *
	 * @param attributes the annotation attributes to process
	 * @since 4.3.2
	 */
	public static void registerDefaultValues(AnnotationAttributes attributes) {
		Class<? extends Annotation> annotationType = attributes.annotationType();
		if (annotationType != null && Modifier.isPublic(annotationType.getModifiers()) &&
				!AnnotationFilter.PLAIN.matches(annotationType)) {
			Map<String, DefaultValueHolder> defaultValues = getDefaultValues(annotationType);
			defaultValues.forEach(attributes::putIfAbsent);
		}
	}

	private static Map<String, DefaultValueHolder> getDefaultValues(
			Class<? extends Annotation> annotationType) {

		return defaultValuesCache.computeIfAbsent(annotationType,
				AnnotationUtils::computeDefaultValues);
	}

	private static Map<String, DefaultValueHolder> computeDefaultValues(
			Class<? extends Annotation> annotationType) {

		AttributeMethods methods = AttributeMethods.forAnnotationType(annotationType);
		if (!methods.hasDefaultValueMethod()) {
			return Collections.emptyMap();
		}
		Map<String, DefaultValueHolder> result = CollectionUtils.newLinkedHashMap(methods.size());
		if (!methods.hasNestedAnnotation()) {
			// Use simpler method if there are no nested annotations
			for (int i = 0; i < methods.size(); i++) {
				Method method = methods.get(i);
				Object defaultValue = method.getDefaultValue();
				if (defaultValue != null) {
					result.put(method.getName(), new DefaultValueHolder(defaultValue));
				}
			}
		} else {
			// If we have nested annotations, we need them as nested maps
			AnnotationAttributes attributes = MergedAnnotation.of(annotationType)
					.asMap(annotation ->
							new AnnotationAttributes(annotation.getType(), true), Adapt.ANNOTATION_TO_MAP);
			for (Map.Entry<String, Object> element : attributes.entrySet()) {
				result.put(element.getKey(), new DefaultValueHolder(element.getValue()));
			}
		}
		return result;
	}

	/**
	 * Post-process the supplied {@link AnnotationAttributes}, preserving nested
	 * annotations as {@code Annotation} instances.
	 * <p>Specifically, this method enforces <em>attribute alias</em> semantics
	 * for annotation attributes that are annotated with {@link AliasFor @AliasFor}
	 * and replaces default value placeholders with their original default values.
	 *
	 * @param annotatedElement    the element that is annotated with an annotation or
	 *                            annotation hierarchy from which the supplied attributes were created;
	 *                            may be {@code null} if unknown
	 * @param attributes          the annotation attributes to post-process
	 * @param classValuesAsString whether to convert Class references into Strings (for
	 *                            compatibility with {@link org.springframework.core.type.AnnotationMetadata})
	 *                            or to preserve them as Class references
	 * @see #getDefaultValue(Class, String)
	 * @since 4.3.2
	 */
	public static void postProcessAnnotationAttributes(@Nullable Object annotatedElement,
													   @Nullable AnnotationAttributes attributes,
													   boolean classValuesAsString) {

		if (attributes == null) {
			return;
		}
		if (!attributes.validated) {
			Class<? extends Annotation> annotationType = attributes.annotationType();
			if (annotationType == null) {
				return;
			}
			AnnotationTypeMapping mapping = AnnotationTypeMappings.forAnnotationType(annotationType).get(0);
			for (int i = 0; i < mapping.getMirrorSets().size(); i++) {
				MirrorSet mirrorSet = mapping.getMirrorSets().get(i);
				int resolved = mirrorSet.resolve(attributes.displayName, attributes,
						AnnotationUtils::getAttributeValueForMirrorResolution);
				if (resolved != -1) {
					Method attribute = mapping.getAttributes().get(resolved);
					Object value = attributes.get(attribute.getName());
					for (int j = 0; j < mirrorSet.size(); j++) {
						Method mirror = mirrorSet.get(j);
						if (mirror != attribute) {
							attributes.put(mirror.getName(),
									adaptValue(annotatedElement, value, classValuesAsString));
						}
					}
				}
			}
		}
		for (Map.Entry<String, Object> attributeEntry : attributes.entrySet()) {
			String attributeName = attributeEntry.getKey();
			Object value = attributeEntry.getValue();
			if (value instanceof DefaultValueHolder) {
				value = ((DefaultValueHolder) value).defaultValue;
				attributes.put(attributeName,
						adaptValue(annotatedElement, value, classValuesAsString));
			}
		}
	}

	private static Object getAttributeValueForMirrorResolution(Method attribute, Object attributes) {
		Object result = ((AnnotationAttributes) attributes).get(attribute.getName());
		return (result instanceof DefaultValueHolder ? ((DefaultValueHolder) result).defaultValue : result);
	}

	@Nullable
	private static Object adaptValue(
			@Nullable Object annotatedElement, @Nullable Object value, boolean classValuesAsString) {

		if (classValuesAsString) {
			if (value instanceof Class) {
				return ((Class<?>) value).getName();
			}
			if (value instanceof Class[]) {
				Class<?>[] classes = (Class<?>[]) value;
				String[] names = new String[classes.length];
				for (int i = 0; i < classes.length; i++) {
					names[i] = classes[i].getName();
				}
				return names;
			}
		}
		if (value instanceof Annotation) {
			Annotation annotation = (Annotation) value;
			return MergedAnnotation.from(annotatedElement, annotation).synthesize();
		}
		if (value instanceof Annotation[]) {
			Annotation[] annotations = (Annotation[]) value;
			Annotation[] synthesized = (Annotation[]) Array.newInstance(
					annotations.getClass().getComponentType(), annotations.length);
			for (int i = 0; i < annotations.length; i++) {
				synthesized[i] = MergedAnnotation.from(annotatedElement, annotations[i]).synthesize();
			}
			return synthesized;
		}
		return value;
	}

	/**
	 * Retrieve the <em>value</em> of the {@code value} attribute of a
	 * single-element Annotation, given an annotation instance.
	 * @param annotation the annotation instance from which to retrieve the value
	 * @return the attribute value, or {@code null} if not found unless the attribute
	 * value cannot be retrieved due to an {@link AnnotationConfigurationException},
	 * in which case such an exception will be rethrown
	 * @see #getValue(java.lang.annotation.Annotation, String)
	 */
	@Nullable
	public static Object getValue(Annotation annotation) {
		return getValue(annotation, VALUE);
	}

	/**
	 * Retrieve the <em>value</em> of a named attribute, given an annotation instance.
	 * @param annotation the annotation instance from which to retrieve the value
	 * @param attributeName the name of the attribute value to retrieve
	 * @return the attribute value, or {@code null} if not found unless the attribute
	 * value cannot be retrieved due to an {@link AnnotationConfigurationException},
	 * in which case such an exception will be rethrown
	 * @see #getValue(java.lang.annotation.Annotation)
	 */
	@Nullable
	public static Object getValue(@Nullable Annotation annotation, @Nullable String attributeName) {
		if (annotation == null || !StringUtils.hasText(attributeName)) {
			return null;
		}
		try {
			Method method = annotation.annotationType().getDeclaredMethod(attributeName);
			ReflectionUtils.makeAccessible(method);
			return method.invoke(annotation);
		} catch (NoSuchMethodException ex) {
			return null;
		} catch (InvocationTargetException ex) {
			rethrowAnnotationConfigurationException(ex.getTargetException());
			throw new IllegalStateException("Could not obtain value for annotation attribute '" +
					attributeName + "' in " + annotation, ex);
		} catch (Throwable ex) {
			handleIntrospectionFailure(annotation.getClass(), ex);
			return null;
		}
	}

	/**
	 * If the supplied throwable is an {@link AnnotationConfigurationException},
	 * it will be cast to an {@code AnnotationConfigurationException} and thrown,
	 * allowing it to propagate to the caller.
	 * <p>Otherwise, this method does nothing.
	 *
	 * @param ex the throwable to inspect
	 */
	static void rethrowAnnotationConfigurationException(Throwable ex) {
		if (ex instanceof AnnotationConfigurationException) {
			throw (AnnotationConfigurationException) ex;
		}
	}

	/**
	 * Retrieve the <em>default value</em> of a named attribute, given an annotation instance.
	 *
	 * @param annotation    the annotation instance from which to retrieve the default value
	 * @param attributeName the name of the attribute value to retrieve
	 * @return the default value of the named attribute, or {@code null} if not found
	 * @see #getDefaultValue(Class, String)
	 */
	@Nullable
	public static Object getDefaultValue(@Nullable Annotation annotation, @Nullable String attributeName) {
		return (annotation != null ? getDefaultValue(annotation.annotationType(), attributeName) : null);
	}

	/**
	 * Retrieve the <em>default value</em> of the {@code value} attribute
	 * of a single-element Annotation, given the {@link Class annotation type}.
	 *
	 * @param annotationType the <em>annotation type</em> for which the default value should be retrieved
	 * @return the default value, or {@code null} if not found
	 * @see #getDefaultValue(Class, String)
	 */
	@Nullable
	public static Object getDefaultValue(Class<? extends Annotation> annotationType) {
		return getDefaultValue(annotationType, VALUE);
	}

	/**
	 * Handle the supplied annotation introspection exception.
	 * <p>If the supplied exception is an {@link AnnotationConfigurationException},
	 * it will simply be thrown, allowing it to propagate to the caller, and
	 * nothing will be logged.
	 * <p>Otherwise, this method logs an introspection failure (in particular for
	 * a {@link TypeNotPresentException}) before moving on, assuming nested
	 * {@code Class} values were not resolvable within annotation attributes and
	 * thereby effectively pretending there were no annotations on the specified
	 * element.
	 *
	 * @param element the element that we tried to introspect annotations on
	 * @param ex      the exception that we encountered
	 * @see #rethrowAnnotationConfigurationException
	 * @see IntrospectionFailureLogger
	 */
	static void handleIntrospectionFailure(@Nullable AnnotatedElement element, Throwable ex) {
		rethrowAnnotationConfigurationException(ex);
		IntrospectionFailureLogger logger = IntrospectionFailureLogger.INFO;
		boolean meta = false;
		if (element instanceof Class && Annotation.class.isAssignableFrom((Class<?>) element)) {
			// Meta-annotation or (default) value lookup on an annotation type
			logger = IntrospectionFailureLogger.DEBUG;
			meta = true;
		}
		if (logger.isEnabled()) {
			String message = meta ?
					"Failed to meta-introspect annotation " :
					"Failed to introspect annotations on ";
			logger.log(message + element + ": " + ex);
		}
	}

	/**
	 * <em>Synthesize</em> an annotation from the supplied {@code annotation}
	 * by wrapping it in a dynamic proxy that transparently enforces
	 * <em>attribute alias</em> semantics for annotation attributes that are
	 * annotated with {@link AliasFor @AliasFor}.
	 * @param annotation the annotation to synthesize
	 * @param annotatedElement the element that is annotated with the supplied
	 * annotation; may be {@code null} if unknown
	 * @return the synthesized annotation if the supplied annotation is
	 * <em>synthesizable</em>; {@code null} if the supplied annotation is
	 * {@code null}; otherwise the supplied annotation unmodified
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 * @see #synthesizeAnnotation(java.util.Map, Class, java.lang.reflect.AnnotatedElement)
	 * @see #synthesizeAnnotation(Class)
	 */
	// public static <A extends Annotation> A synthesizeAnnotation(
	// 		A annotation, @Nullable AnnotatedElement annotatedElement) {
	//
	// 	if (annotation instanceof SynthesizedAnnotation || AnnotationFilter.PLAIN.matches(annotation)) {
	// 		return annotation;
	// 	}
	// 	return MergedAnnotation.from(annotatedElement, annotation).synthesize();
	// }

	/**
	 * <em>Synthesize</em> an annotation from the supplied {@code annotation}
	 * by wrapping it in a dynamic proxy that transparently enforces
	 * <em>attribute alias</em> semantics for annotation attributes that are
	 * annotated with {@link AliasFor @AliasFor}.
	 *
	 * @param annotation the annotation to synthesize
	 * @return the synthesized annotation, if the supplied annotation is
	 * <em>synthesizable</em>; {@code null} if the supplied annotation is
	 * {@code null}; otherwise, the supplied annotation unmodified
	 * @throws AnnotationConfigurationException if invalid configuration of
	 *                                          {@code @AliasFor} is detected
	 * @see #synthesizeAnnotation(Annotation, AnnotatedElement)
	 * @since 4.2
	 */
	static <A extends Annotation> A synthesizeAnnotation(A annotation) {
		return synthesizeAnnotation(annotation, null);
	}

	/**
	 * <em>Synthesize</em> an annotation from the supplied {@code annotation}
	 * by wrapping it in a dynamic proxy that transparently enforces
	 * <em>attribute alias</em> semantics for annotation attributes that are
	 * annotated with {@link AliasFor @AliasFor}.
	 *
	 * @param annotation       the annotation to synthesize
	 * @param annotatedElement the element that is annotated with the supplied
	 *                         annotation; may be {@code null} if unknown
	 * @return the synthesized annotation if the supplied annotation is
	 * <em>synthesizable</em>; {@code null} if the supplied annotation is
	 * {@code null}; otherwise the supplied annotation unmodified
	 * @throws AnnotationConfigurationException if invalid configuration of
	 *                                          {@code @AliasFor} is detected
	 * @see #synthesizeAnnotation(Map, Class, AnnotatedElement)
	 * @see #synthesizeAnnotation(Class)
	 * @since 4.2
	 */
	public static <A extends Annotation> A synthesizeAnnotation(
			A annotation, @Nullable AnnotatedElement annotatedElement) {

		return synthesizeAnnotation(annotation, (Object) annotatedElement);
	}

	@SuppressWarnings("unchecked")
	static <A extends Annotation> A synthesizeAnnotation(A annotation, @Nullable Object annotatedElement) {
		if (annotation instanceof SynthesizedAnnotation || hasPlainJavaAnnotationsOnly(annotatedElement)) {
			return annotation;
		}

		Class<? extends Annotation> annotationType = annotation.annotationType();
		if (!isSynthesizable(annotationType)) {
			return annotation;
		}

		DefaultAnnotationAttributeExtractor attributeExtractor =
				new DefaultAnnotationAttributeExtractor(annotation, annotatedElement);
		InvocationHandler handler = new SynthesizedAnnotationInvocationHandler(attributeExtractor);

		// Can always expose Spring's SynthesizedAnnotation marker since we explicitly check for a
		// synthesizable annotation before (which needs to declare @AliasFor from the same package)
		Class<?>[] exposedInterfaces = new Class<?>[]{annotationType, SynthesizedAnnotation.class};
		return (A) Proxy.newProxyInstance(annotation.getClass().getClassLoader(), exposedInterfaces, handler);
	}

	/**
	 * Retrieve the <em>default value</em> of the {@code value} attribute
	 * of a single-element Annotation, given an annotation instance.
	 *
	 * @param annotation the annotation instance from which to retrieve the default value
	 * @return the default value, or {@code null} if not found
	 * @see #getDefaultValue(java.lang.annotation.Annotation, String)
	 */
	@Nullable
	public static Object getDefaultValue(Annotation annotation) {
		return getDefaultValue(annotation, VALUE);
	}

	/**
	 * Retrieve the <em>default value</em> of a named attribute, given the
	 * {@link Class annotation type}.
	 *
	 * @param annotationType the <em>annotation type</em> for which the default value should be retrieved
	 * @param attributeName  the name of the attribute value to retrieve.
	 * @return the default value of the named attribute, or {@code null} if not found
	 * @see #getDefaultValue(java.lang.annotation.Annotation, String)
	 */
	@Nullable
	public static Object getDefaultValue(
			@Nullable Class<? extends Annotation> annotationType, @Nullable String attributeName) {

		if (annotationType == null || !StringUtils.hasText(attributeName)) {
			return null;
		}
		return MergedAnnotation.of(annotationType).getDefaultValue(attributeName).orElse(null);
	}

	/**
	 * <em>Synthesize</em> an annotation from its default attributes values.
	 * <p>This method simply delegates to
	 * {@link #synthesizeAnnotation(java.util.Map, Class, java.lang.reflect.AnnotatedElement)},
	 * supplying an empty map for the source attribute values and {@code null}
	 * for the {@link java.lang.reflect.AnnotatedElement}.
	 *
	 * @param annotationType the type of annotation to synthesize
	 * @return the synthesized annotation
	 * @throws IllegalArgumentException         if a required attribute is missing
	 * @throws AnnotationConfigurationException if invalid configuration of
	 *                                          {@code @AliasFor} is detected
	 * @see #synthesizeAnnotation(java.util.Map, Class, java.lang.reflect.AnnotatedElement)
	 * @see #synthesizeAnnotation(java.lang.annotation.Annotation, java.lang.reflect.AnnotatedElement)
	 * @since 4.2
	 */
	public static <A extends Annotation> A synthesizeAnnotation(Class<A> annotationType) {
		return synthesizeAnnotation(Collections.emptyMap(), annotationType, null);
	}

	/**
	 * Determine if annotations of the supplied {@code annotationType} are
	 * <em>synthesizable</em> (i.e. in need of being wrapped in a dynamic
	 * proxy that provides functionality above that of a standard JDK
	 * annotation).
	 * <p>Specifically, an annotation is <em>synthesizable</em> if it declares
	 * any attributes that are configured as <em>aliased pairs</em> via
	 * {@link AliasFor @AliasFor} or if any nested annotations used by the
	 * annotation declare such <em>aliased pairs</em>.
	 *
	 * @see SynthesizedAnnotation
	 * @see SynthesizedAnnotationInvocationHandler
	 * @since 4.2
	 */
	@SuppressWarnings("unchecked")
	private static boolean isSynthesizable(Class<? extends Annotation> annotationType) {
		if (hasPlainJavaAnnotationsOnly(annotationType)) {
			return false;
		}

		Boolean synthesizable = synthesizableCache.get(annotationType);
		if (synthesizable != null) {
			return synthesizable;
		}

		synthesizable = Boolean.FALSE;
		for (Method attribute : getAttributeMethods(annotationType)) {
			if (!getAttributeAliasNames(attribute).isEmpty()) {
				synthesizable = Boolean.TRUE;
				break;
			}
			Class<?> returnType = attribute.getReturnType();
			if (Annotation[].class.isAssignableFrom(returnType)) {
				Class<? extends Annotation> nestedAnnotationType =
						(Class<? extends Annotation>) returnType.getComponentType();
				if (isSynthesizable(nestedAnnotationType)) {
					synthesizable = Boolean.TRUE;
					break;
				}
			} else if (Annotation.class.isAssignableFrom(returnType)) {
				Class<? extends Annotation> nestedAnnotationType = (Class<? extends Annotation>) returnType;
				if (isSynthesizable(nestedAnnotationType)) {
					synthesizable = Boolean.TRUE;
					break;
				}
			}
		}

		synthesizableCache.put(annotationType, synthesizable);
		return synthesizable;
	}

	/**
	 * <em>Synthesize</em> an array of annotations from the supplied array
	 * of {@code annotations} by creating a new array of the same size and
	 * type and populating it with {@linkplain #synthesizeAnnotation(Annotation)
	 * synthesized} versions of the annotations from the input array.
	 *
	 * @param annotations      the array of annotations to synthesize
	 * @param annotatedElement the element that is annotated with the supplied
	 *                         array of annotations; may be {@code null} if unknown
	 * @return a new array of synthesized annotations, or {@code null} if
	 * the supplied array is {@code null}
	 * @throws AnnotationConfigurationException if invalid configuration of
	 *                                          {@code @AliasFor} is detected
	 * @see #synthesizeAnnotation(Annotation, AnnotatedElement)
	 * @see #synthesizeAnnotation(Map, Class, AnnotatedElement)
	 * @since 4.2
	 */
	static Annotation[] synthesizeAnnotationArray(Annotation[] annotations, @Nullable Object annotatedElement) {
		if (hasPlainJavaAnnotationsOnly(annotatedElement)) {
			return annotations;
		}

		Annotation[] synthesized = (Annotation[]) Array.newInstance(
				annotations.getClass().getComponentType(), annotations.length);
		for (int i = 0; i < annotations.length; i++) {
			synthesized[i] = synthesizeAnnotation(annotations[i], annotatedElement);
		}
		return synthesized;
	}

	/**
	 * <em>Synthesize</em> an annotation from the supplied map of annotation
	 * attributes by wrapping the map in a dynamic proxy that implements an
	 * annotation of the specified {@code annotationType} and transparently
	 * enforces <em>attribute alias</em> semantics for annotation attributes
	 * that are annotated with {@link AliasFor @AliasFor}.
	 * <p>The supplied map must contain a key-value pair for every attribute
	 * defined in the supplied {@code annotationType} that is not aliased or
	 * does not have a default value. Nested maps and nested arrays of maps
	 * will be recursively synthesized into nested annotations or nested
	 * arrays of annotations, respectively.
	 * <p>Note that {@link AnnotationAttributes} is a specialized type of
	 * {@link java.util.Map} that is an ideal candidate for this method's
	 * {@code attributes} argument.
	 * @param attributes the map of annotation attributes to synthesize
	 * @param annotationType the type of annotation to synthesize
	 * @param annotatedElement the element that is annotated with the annotation
	 * corresponding to the supplied attributes; may be {@code null} if unknown
	 * @return the synthesized annotation
	 * @throws IllegalArgumentException if a required attribute is missing or if an
	 * attribute is not of the correct type
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 * @see #synthesizeAnnotation(java.lang.annotation.Annotation, java.lang.reflect.AnnotatedElement)
	 * @see #synthesizeAnnotation(Class)
	 * @see #getAnnotationAttributes(java.lang.reflect.AnnotatedElement, java.lang.annotation.Annotation)
	 * @see #getAnnotationAttributes(java.lang.reflect.AnnotatedElement, java.lang.annotation.Annotation, boolean, boolean)
	 */
	public static <A extends Annotation> A synthesizeAnnotation(Map<String, Object> attributes,
																Class<A> annotationType,
																@Nullable AnnotatedElement annotatedElement) {

		try {
			return MergedAnnotation.of(annotatedElement, annotationType, attributes).synthesize();
		} catch (NoSuchElementException | IllegalStateException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * <em>Synthesize</em> an array of annotations from the supplied array
	 * of {@code annotations} by creating a new array of the same size and
	 * type and populating it with {@linkplain #synthesizeAnnotation(java.lang.annotation.Annotation,
	 * java.lang.reflect.AnnotatedElement) synthesized} versions of the annotations from the input
	 * array.
	 * @param annotations the array of annotations to synthesize
	 * @param annotatedElement the element that is annotated with the supplied
	 * array of annotations; may be {@code null} if unknown
	 * @return a new array of synthesized annotations, or {@code null} if
	 * the supplied array is {@code null}
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 * @see #synthesizeAnnotation(java.lang.annotation.Annotation, java.lang.reflect.AnnotatedElement)
	 * @see #synthesizeAnnotation(java.util.Map, Class, java.lang.reflect.AnnotatedElement)
	 */
	static Annotation[] synthesizeAnnotationArray(Annotation[] annotations, AnnotatedElement annotatedElement) {
		if (AnnotationsScanner.hasPlainJavaAnnotationsOnly(annotatedElement)) {
			return annotations;
		}
		Annotation[] synthesized = (Annotation[]) Array.newInstance(
				annotations.getClass().getComponentType(), annotations.length);
		for (int i = 0; i < annotations.length; i++) {
			synthesized[i] = synthesizeAnnotation(annotations[i], annotatedElement);
		}
		return synthesized;
	}

	/**
	 * Get a map of all attribute aliases declared via {@code @AliasFor}
	 * in the supplied annotation type.
	 * <p>The map is keyed by attribute name with each value representing
	 * a list of names of aliased attributes.
	 * <p>For <em>explicit</em> alias pairs such as x and y (i.e. where x
	 * is an {@code @AliasFor("y")} and y is an {@code @AliasFor("x")}, there
	 * will be two entries in the map: {@code x -> (y)} and {@code y -> (x)}.
	 * <p>For <em>implicit</em> aliases (i.e. attributes that are declared
	 * as attribute overrides for the same attribute in the same meta-annotation),
	 * there will be n entries in the map. For example, if x, y, and z are
	 * implicit aliases, the map will contain the following entries:
	 * {@code x -> (y, z)}, {@code y -> (x, z)}, {@code z -> (x, y)}.
	 * <p>An empty return value implies that the annotation does not declare
	 * any attribute aliases.
	 * @param annotationType the annotation type to find attribute aliases in
	 * @return a map containing attribute aliases (never {@code null})
	 * @since 4.2
	 */
	static Map<String, List<String>> getAttributeAliasMap(@Nullable Class<? extends Annotation> annotationType) {
		if (annotationType == null) {
			return Collections.emptyMap();
		}

		Map<String, List<String>> map = attributeAliasesCache.get(annotationType);
		if (map != null) {
			return map;
		}

		map = new LinkedHashMap<>();
		for (Method attribute : getAttributeMethods(annotationType)) {
			List<String> aliasNames = getAttributeAliasNames(attribute);
			if (!aliasNames.isEmpty()) {
				map.put(attribute.getName(), aliasNames);
			}
		}

		attributeAliasesCache.put(annotationType, map);
		return map;
	}

	/**
	 * Get the names of the aliased attributes configured via
	 * {@link AliasFor @AliasFor} for the supplied annotation {@code attribute}.
	 * @param attribute the attribute to find aliases for
	 * @return the names of the aliased attributes (never {@code null}, though
	 * potentially <em>empty</em>)
	 * @throws IllegalArgumentException if the supplied attribute method is
	 * {@code null} or not from an annotation
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 * @see #getAttributeOverrideName(Method, Class)
	 */
	static List<String> getAttributeAliasNames(Method attribute) {
		AliasDescriptor descriptor = AliasDescriptor.from(attribute);
		return (descriptor != null ? descriptor.getAttributeAliasNames() : Collections.emptyList());
	}

	/**
	 * Get the name of the overridden attribute configured via
	 * {@link AliasFor @AliasFor} for the supplied annotation {@code attribute}.
	 * @param attribute the attribute from which to retrieve the override
	 * (never {@code null})
	 * @param metaAnnotationType the type of meta-annotation in which the
	 * overridden attribute is allowed to be declared
	 * @return the name of the overridden attribute, or {@code null} if not
	 * found or not applicable for the specified meta-annotation type
	 * @throws IllegalArgumentException if the supplied attribute method is
	 * {@code null} or not from an annotation, or if the supplied meta-annotation
	 * type is {@code null} or {@link Annotation}
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 */
	@Nullable
	static String getAttributeOverrideName(Method attribute, @Nullable Class<? extends Annotation> metaAnnotationType) {
		AliasDescriptor descriptor = AliasDescriptor.from(attribute);
		return (descriptor != null && metaAnnotationType != null ?
				descriptor.getAttributeOverrideName(metaAnnotationType) : null);
	}

	/**
	 * Get all methods declared in the supplied {@code annotationType} that
	 * match Java's requirements for annotation <em>attributes</em>.
	 * <p>All methods in the returned list will be
	 * {@linkplain ReflectionUtils#makeAccessible(Method) made accessible}.
	 * @param annotationType the type in which to search for attribute methods
	 * (never {@code null})
	 * @return all annotation attribute methods in the specified annotation
	 * type (never {@code null}, though potentially <em>empty</em>)
	 * @since 4.2
	 */
	static List<Method> getAttributeMethods(Class<? extends Annotation> annotationType) {
		List<Method> methods = attributeMethodsCache.get(annotationType);
		if (methods != null) {
			return methods;
		}

		methods = new ArrayList<>();
		for (Method method : annotationType.getDeclaredMethods()) {
			if (isAttributeMethod(method)) {
				ReflectionUtils.makeAccessible(method);
				methods.add(method);
			}
		}

		attributeMethodsCache.put(annotationType, methods);
		return methods;
	}

	/**
	 * Determine if the supplied {@code method} is an annotation attribute method.
	 * @param method the method to check
	 * @return {@code true} if the method is an attribute method
	 * @since 4.2
	 */
	static boolean isAttributeMethod(@Nullable Method method) {
		return (method != null && method.getParameterCount() == 0 && method.getReturnType() != void.class);
	}

	/**
	 * Determine if the given annotated element is defined in a
	 * {@code java} or in the {@code org.springframework.lang} package.
	 * @param annotatedElement the annotated element to check
	 * @return {@code true} if the given element is in a {@code java}
	 * package or in the {@code org.springframework.lang} package
	 * @since 5.1
	 */
	static boolean hasPlainJavaAnnotationsOnly(@Nullable Object annotatedElement) {
		Class<?> clazz;
		if (annotatedElement instanceof Class) {
			clazz = (Class<?>) annotatedElement;
		}
		else if (annotatedElement instanceof Member) {
			clazz = ((Member) annotatedElement).getDeclaringClass();
		}
		else {
			return false;
		}
		String name = clazz.getName();
		return (name.startsWith("java.") || name.startsWith("org.springframework.lang."));
	}

	/**
	 * Clear the internal annotation metadata cache.
	 * @since 4.3.15
	 */
	public static void clearCache() {
		AnnotationTypeMappings.clearCache();
		AnnotationsScanner.clearCache();
	}



}
