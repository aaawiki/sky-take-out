package com.sky.aspect;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Args;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类，实现公共字段自动填充
 */
@Aspect
@Component
@Slf4j
public class AutofillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){
    }
    /**
     * 前置通知
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        MethodSignature signature=(MethodSignature) joinPoint.getSignature();
        OperationType operationType= signature.getMethod().getAnnotation(AutoFill.class).value();
        Object[] args=joinPoint.getArgs();
        if(args==null||args.length==0)
            return;
        Object entity=args[0];
        LocalDateTime localDateTime=LocalDateTime.now();
        Long id= BaseContext.getCurrentId();
        /**
         * 根据不同对象通过反射来赋值
         */
            if (operationType == OperationType.INSERT) {
                try {
                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setCreateUser =entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser =entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

                setCreateTime.invoke(entity,localDateTime);
                setCreateUser.invoke(entity,id);
                setUpdateTime.invoke(entity,localDateTime);
                setUpdateUser.invoke(entity,id);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (operationType==OperationType.UPDATE) {
                try {
                    Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                    Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

                    setUpdateTime.invoke(entity, localDateTime);
                    setUpdateUser.invoke(entity, id);
                }catch (Exception e){
                    e.printStackTrace();;
                }
            }

    }
}
