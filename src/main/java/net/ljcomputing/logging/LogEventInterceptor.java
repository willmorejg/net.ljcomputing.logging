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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ljcomputing.gson.converter.GsonConverterService;
import net.ljcomputing.logging.annotation.LogEvent;

/**
 * Log event interceptor.
 * 
 * @author James G. Willmore
 *
 */
@Aspect
@Component
public class LogEventInterceptor {

  /** The Gson converter service. */
  @Autowired
  private transient GsonConverterService gsonConverterService;

  /**
   * Log the event.
   *
   * @param jointPoint the jointPoint
   * @param bean the bean
   * @param event the event
   */
  @Before(value = "net.ljcomputing.logging.LogEventManager.logEvent()" + "&& target(bean) "
      + "&& @annotation(net.ljcomputing.logging.annotation.LogEvent)"
      + "&& @annotation(event)", argNames = "bean,event")
  public void log(final JoinPoint jointPoint, final Object bean, final LogEvent event) {
    final LogEvent.Level level = event.level();
    final String message = event.message();
    final String method = jointPoint.getSignature().getName();
    final Logger logger = LoggerFactory.getLogger(bean.getClass());

    logEvent(logger, level, "Method: " + method + " - " + message);

    if (event.showArgs()) {
      logEvent(logger, level, "     arguments passed:");

      final Object[] signatureArgs = jointPoint.getArgs();

      for (final Object arg : signatureArgs) {
        logEvent(logger, level, "      arg : " + arg);
      }
    }

  }

  /**
   * Log response.
   *
   * @param joinPoint the jointPoint
   * @param bean the bean
   */
  @AfterReturning(value = "net.ljcomputing.logging.LogEventManager.logResponse()"
      + "&& @annotation(net.ljcomputing.logging.annotation.LogResponse)", returning = "bean")
  public void logResponse(final JoinPoint joinPoint, final Object bean) {
    final String method = joinPoint.getSignature().getName();
    final Logger logger = LoggerFactory.getLogger(bean.getClass());
    final String result = gsonConverterService.toJson(bean);

    if (logger.isDebugEnabled()) {
      logger.debug("Method: " + method + "==> " + result + " <==");
    }
  }

  /**
   * Log event based upon the bean's class, given log event level, and
   * message.
   *
   * @param logger the logger
   * @param level the level
   * @param message the message
   */
  private static void logEvent(final Logger logger, final LogEvent.Level level,
      final String message) {
    switch (level) {
      case TRACE:
        logger.trace(message);
        break;
      case DEBUG:
        logger.debug(message);
        break;
      case INFO:
        logger.info(message);
        break;
      case WARN:
        logger.warn(message);
        break;
      case ERROR:
        logger.error(message);
        break;
      default:
        break;
    }
  }

}