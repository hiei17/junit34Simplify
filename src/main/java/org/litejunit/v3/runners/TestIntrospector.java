package org.litejunit.v3.runners;


import org.litejunit.v3.Before;
import org.litejunit.v3.BeforeClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//工具 找出类的给定注解方法
public class TestIntrospector {
	private final Class< ?> testClass;
	
	public TestIntrospector(Class<?> testClass) {
		this.testClass= testClass;
	}

	/**
	 *
	 * @param annotationClass 注解类
	 * @return testClass里面有此注解类的方法list
	 */
	public List<Method> getTestMethods(Class<? extends Annotation> annotationClass) {
		List<Method> results= new ArrayList<Method>();

		Method[] methods= testClass.getDeclaredMethods();
		for (Method method : methods) {
			Annotation annotation= method.getAnnotation(annotationClass);
			if (annotation != null && ! isShadowed(method, results)) {
				results.add(method);
			}
		}

		if (runsTopToBottom(annotationClass)) {
			Collections.reverse(results);
		}
		return results;
	}


	//是不是前置注解
	private boolean runsTopToBottom(Class< ? extends Annotation> annotation) {
		return annotation.equals(Before.class) || annotation.equals(BeforeClass.class);
	}

	//是不是已经有了
	private boolean isShadowed(Method method, List<Method> results) {
		for (Method m : results) {
			if (m.getName().equals(method.getName())) {
				return true;
			}
		}
		return false;
	}





}

