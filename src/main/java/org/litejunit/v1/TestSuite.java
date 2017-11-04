package org.litejunit.v1;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;




public class TestSuite extends Assert implements Test {	
	private List<Test> tests= new ArrayList<>(10);
	private String name;
	public TestSuite(){
		
	}

	//把测试类变成多个测试类实例
	public TestSuite(final Class<?> theClass) {
		this.name= theClass.getName();
		//得入参是string的构造方法
		Constructor<?> constructor= null;
		try {	
			constructor= getConstructor(theClass);
		} catch (NoSuchMethodException e) {
			addTest(warning("Class "+theClass.getName()+" has no public constructor TestCase(String name)"));
			return;
		}

		//类要是 不是public 抛出异常
		if (!Modifier.isPublic(theClass.getModifiers())) {
			addTest(warning("Class "+theClass.getName()+" is not public"));
			return;
		}

		//保存测试方法名
		Vector<String> names= new Vector<>();
		Method[] methods= theClass.getDeclaredMethods();
		for (Method method : methods) {
			addTestMethod(method, names, constructor);
		}		
		
		if (tests.size() == 0)
			addTest(warning("No tests found in "+theClass.getName()));
	}

	//得theClass入仓是String的的构造方法
	private Constructor<?> getConstructor(Class<?> theClass) throws NoSuchMethodException {
		Class<?>[] args= { String.class };
		return theClass.getConstructor(args);
	}
	private void addTestMethod(Method m, Vector<String> names, Constructor<?> constructor) {
		String name= m.getName();
		if (!isPublicTestMethod(m)) {
			return;
		}
		//保存方法名
		names.addElement(name);
		Object[] args= new Object[]{name};
		try {
			//把测试实例new出来加进去
			addTest((Test)constructor.newInstance(args));
		} catch (InstantiationException e) {
			addTest(warning("Cannot instantiate test case: "+name+" ("+exceptionToString(e)+")"));
		} catch (InvocationTargetException e) {
			addTest(warning("Exception in constructor: "+name+" ("+exceptionToString(e.getTargetException())+")"));
		} catch (IllegalAccessException e) {
			addTest(warning("Cannot access test case: "+name+" ("+exceptionToString(e)+")"));
		}

	}
	private boolean isPublicTestMethod(Method m) {
		return isTestMethod(m) && Modifier.isPublic(m.getModifiers());
	}
	//没有入参 void  test开头
	private boolean isTestMethod(Method m) {
		String name= m.getName();
		Class<?>[] parameters= m.getParameterTypes();
		Class<?> returnType= m.getReturnType();
		return parameters.length == 0 && name.startsWith("test") && returnType.equals(Void.TYPE);
	}

	public void addTest(Test test) {
		tests.add(test);
	}
	
	private Test warning(final String message) {
		return new TestCase("warning") {
			public void doRun() {
				fail(message);
			}
		};		
	}
	private String exceptionToString(Throwable t) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		return stringWriter.toString();
		
	}
	
	

	//遍历运行每个测试实例
	@Override
	public void run(TestResult result) {
		for (Test test:tests) {
	  		if (result.shouldStop() ){
	  			break;
	  		}
	  		//执行一个测试用例
			test.run(result);//TestCase.run
		}

	}
	
	@Override
	public int countTestCases() {
		int count= 0;
		
		for (Test test:tests) {
			count= count + test.countTestCases();
		}
		return count;
	}

}
