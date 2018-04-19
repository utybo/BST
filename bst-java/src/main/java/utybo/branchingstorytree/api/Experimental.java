/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Denotes an experimental class, method, parameter, constructor, variable,
 * annotation, package, type parameter or type use. Experimental elements must
 * not be used in production code, or, if normally called via external
 * resources, must never be used in a production environment.
 * <p>
 * If, under any circumstance, experimental code is called, the final user must
 * be notified of such usage. Experimental features may be removed at any time.
 * <p>
 * Experimental methods must never have to be implemented. This means that they
 * must never be abstract. If abstraction is needed in your code, implement the
 * method, but simply raise an {@link UnsupportedExperimentalException}. Here is
 * an example of this with an interface :
 * 
 * <pre>
 * <code>
 * public interface SomeInterface {
 *    public void someAbstractMethod();
 *    
 *    {@literal @}Experimental
 *    public default void someExperimentalAbstractMethod()
 *    {
 *      throw new UnsupportedExperimentalException();
 *    }
 * }
 * </code>
 * </pre>   
 * <p>
 * In general, any method, variable, constructor, or any element contained
 * within an experimental element, such as a class or method, should be
 * considered experimental as well. However, classes containing experimental
 * methods are not necessarily experimental themselves.
 * 
 * @author utybo
 *
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE,
        TYPE_PARAMETER, TYPE_USE})
public @interface Experimental
{}
