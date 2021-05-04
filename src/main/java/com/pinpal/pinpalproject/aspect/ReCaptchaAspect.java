//package com.pinpal.pinpalproject.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//
//@Aspect
//public class ReCaptchaAspect {
//    @Around("@annotation(com.pinpal.pinpalproject.ReCaptchaAnnotation)")
//    public Boolean checkRecaptchaScore(ProceedingJoinPoint proceedingJoinPoint) {
//        String[] methodParameters = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames();
//
//        return true;
//    }
//}
