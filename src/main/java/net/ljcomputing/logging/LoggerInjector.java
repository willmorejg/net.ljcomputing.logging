/**
           Copyright 2015, James G. Willmore

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.ljcomputing.logging;

import net.ljcomputing.logging.annotation.InjectLogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import java.lang.reflect.Field;

/**
 * A SLF4J Logger injector. Injects the Logger into the member of a class with
 * the {@link InjectLogging}  annotation.
 * 
 * @author James G. Willmore
 *
 */
@Component
public class LoggerInjector implements BeanPostProcessor {

  /**
   * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
   */
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  /**
   * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
   */
  public Object postProcessBeforeInitialization(final Object bean,
      String beanName) throws BeansException {
    ReflectionUtils.doWithFields(bean.getClass(),
        new LoggerFieldCallback(bean));
    return bean;
  }

}

/**
 * Logger field callback.
 */
class LoggerFieldCallback implements FieldCallback {
  /** The bean. */
  private final Object bean;

  /**
   * Instantiates a new logger field callback.
   *
   * @param bean the bean
   */
  LoggerFieldCallback(final Object bean) {
    this.bean = bean;
  }

  /**
   * @see org.springframework.util.ReflectionUtils.FieldCallback#doWith(java.lang.reflect.Field)
   */
  public void doWith(Field field)
      throws IllegalArgumentException, IllegalAccessException {
    ReflectionUtils.makeAccessible(field);
    if (field.getAnnotation(InjectLogging.class) != null) {
      Logger logger = LoggerFactory.getLogger(bean.getClass());

      field.set(bean, logger);
    }
  }
}