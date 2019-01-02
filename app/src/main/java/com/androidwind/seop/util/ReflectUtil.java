package com.androidwind.seop.util;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author	ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class ReflectUtil {
	private static final String TAG = "GaramutHelperReflect";
	public static final String LOG_FAIL_REASON = "fail_reason:";

	public static Class<?> getClass(String className) {
		try {
			Class<?> cls = Class.forName(className);
			Log.d(TAG, "getClass->cls:" + cls);

			return cls;
		} catch (ClassNotFoundException e) {
			Log.d(TAG, "getClass->className:" + className
					+ "," + LOG_FAIL_REASON + e);
			e.printStackTrace();
			return null;
		}

	}

	public static Class<?> getClass(Class<?> cls, String className) {
		Class<?>[] classes = cls.getClasses();
		for (Class<?> clas : classes) {
			Log.d(TAG, "getClass->getClasses " + clas.toString());
			if (clas.getName().equals(className)) {
				return clas;
			}
		}
		classes = cls.getDeclaredClasses();
		for (Class<?> clas : classes) {
			Log.d(TAG, "getClass->getDeclaredClasses " + clas.toString());
			if (clas.getName().equals(className)) {
				return clas;
			}
		}
		return null;
	}

	public static Method getMethod(Class<?> cls, String funcName) {
		Method[] methods = cls.getDeclaredMethods();
		Log.d(TAG, "getMethod->getMethodsDeclared " + methods.toString());
		for (Method method : methods) {
			// GaramutHelperLog.log(TAG, "getMethod", "getMethodDeclared " +
			// method.toString());
			if (method.getName().equals(funcName)) {
				return method;
			}
		}

		methods = cls.getMethods();
		Log.d(TAG, "getMethod->getMethodsBase " + methods.toString());
		for (Method method : methods) {
			// GaramutHelperLog.log(TAG, "getMethod", "getMethodBase " +
			// method.toString());
			if (method.getName().equals(funcName)) {
				return method;
			}
		}

		Log.d(TAG, "getMethod->fail");
		return null;
	}

	public static Method getMethod(Class<?> owner, String funcName,
                                   Class<?>... paramTypes) {
		try {
			return owner.getDeclaredMethod(funcName, paramTypes);
		} catch (NoSuchMethodException e) {
			Log.d(TAG, "getMethod->fail " + LOG_FAIL_REASON + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getObject(String className) {

		return getObject(getClass(className));
	}

	public static Object getObject(Class<?> cls) {
		try {
			return cls.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			Log.d(TAG, "getObject->fail " + LOG_FAIL_REASON + e.toString());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Log.d(TAG, "getObject->fail " + LOG_FAIL_REASON + e.toString());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			Log.d(TAG, "getObject->fail " + LOG_FAIL_REASON + e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "getObject->fail " + LOG_FAIL_REASON + e.toString());
		}
		return null;
	}

	public static Object invoke(Object owner, Method method, Object... params) {
		try {
			return method.invoke(owner, params);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}