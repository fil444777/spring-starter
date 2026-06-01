package spring.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import spring.dto.UserCreateEditDto;

@Slf4j
@Aspect
@Component
public class CreateAndUpdateAspect {

    @Pointcut("execution(* spring.service.UserService.create(..))")
    public void userServiceCreateMethod() {
    }

    @Pointcut("execution(* spring.service.UserService.update(..))")
    public void userServiceUpdateMethod() {
    }

    @Before("userServiceCreateMethod() && args(userDto)")
    public void addLoggingBeforeCreate(JoinPoint joinPoint, UserCreateEditDto userDto) {
        log.info("Before invoked create method in class {}, with dto {}",
                joinPoint.getTarget().getClass().getSimpleName(), userDto);
    }

    @Before("userServiceUpdateMethod() && args(id, userDto)")
    public void addLoggingBeforeUpdate(JoinPoint joinPoint, Long id, UserCreateEditDto userDto) {
        log.info("Before invoked update method in class {}, with id {}, dto {}",
                joinPoint.getTarget().getClass().getSimpleName(), id, userDto);
    }

    @AfterReturning(value = "userServiceCreateMethod()", returning = "result")
    public void addLoggingAfterReturningCreate(Object result) {
        log.info("AfterReturning invoked create method, with result {}", result);
    }

    @AfterReturning(value = "userServiceUpdateMethod()", returning = "result")
    public void addLoggingAfterReturningUpdate(Object result) {
        log.info("AfterReturning invoked update method, with result {}", result);
    }

    @AfterThrowing(value = "userServiceCreateMethod()", throwing = "ex")
    public void addLoggingAfterThrowingCreate(Throwable ex) {
        log.info("Throwing invoked create method, with throwing {}", ex.toString());
    }

    @AfterThrowing(value = "userServiceUpdateMethod()", throwing = "ex")
    public void addLoggingAfterThrowingUpdate(Throwable ex) {
        log.info("Throwing invoked update method, with throwing {}", ex.toString());
    }

    @After("userServiceCreateMethod()")
    public void addLoggingAfterCreate() {
        log.info("After invoked create method");
    }

    @After("userServiceUpdateMethod()")
    public void addLoggingAfterUpdate() {
        log.info("After invoked update method");
    }
}
