

package red.mlz.common.config.dynamic.datasource.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import red.mlz.common.config.ReadOnly;
import red.mlz.common.config.dynamic.datasource.config.DynamicContextHolder;

import java.lang.reflect.Method;

import static java.util.Objects.requireNonNull;

/**
 * 多数据源，切面处理类
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@Component
@Aspect
public class DataSourceAspect {


    @Pointcut("@annotation(red.mlz.common.config.ReadOnly)")
    public void dataSourcePointCut() {
        //拦截所有标有 @ReadOnly 注解的方法
    }


    /**
     * 获取最接近方法的DS注解值
     *
     * @param clazz  类对象
     * @param method 方法对象
     * @return 数据源名称
     */
    private String getNearestDataSource(Class<?> clazz, Method method) {
        ReadOnly methodDataSource = method.getAnnotation(ReadOnly.class);
        ReadOnly classDataSource = clazz.getAnnotation(ReadOnly.class);

        return requireNonNullElse(methodDataSource, classDataSource).value();
    }

    public static <T> T requireNonNullElse(T obj, T defaultObj) {
        return (obj != null) ? obj : requireNonNull(defaultObj, "defaultObj");
    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = point.getTarget().getClass();

        // 根据目标类和方法上的 @ReadOnly 注解，确定要切换的数据源
        String dataSourceValue = getNearestDataSource(targetClass, method);
        if (dataSourceValue != null) {
            // 将当前数据源推入 ThreadLocal 中
            DynamicContextHolder.push(dataSourceValue);
            log.info("切换数据源: {}", dataSourceValue);
            try {
                return point.proceed();
            } finally {
                //清除当前线程的数据源上下文
                DynamicContextHolder.poll();
                log.info("Cleared datasource context");
            }
        } else {
            log.warn("No DS annotation found on class {} or method {}", targetClass.getName(), method.getName());
            return point.proceed();
        }
    }


}