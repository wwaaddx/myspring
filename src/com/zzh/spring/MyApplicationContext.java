package com.zzh.spring;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyApplicationContext {
    private Class clazz;

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private Map<String, Object> singletonMap = new HashMap<>();

    public MyApplicationContext(Class clazz) {
        this.clazz = clazz;

        //获取扫描路径
        ComponentScan annotation = (ComponentScan) clazz.getAnnotation(ComponentScan.class);
        String path = annotation.value();
        path = path.replace(".", "/");

        ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(path);
        File file = new File(resource.getFile());

        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(File f: files) {
                String absolutePath = f.getAbsolutePath();
                if(absolutePath.endsWith(".class")) {
                    absolutePath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class")).replace("\\", ".");

                    try {
                        Class<?> aClass = classLoader.loadClass(absolutePath);
                        if(aClass.isAnnotationPresent(Component.class)) {
                            BeanDefinition beanDefinition = new BeanDefinition();

                            Component component = aClass.getAnnotation(Component.class);
                            String beanName = component.value();
                            beanDefinition.setClazz(aClass);

                            if(aClass.isAnnotationPresent(Scope.class)) {
                                Scope scope = aClass.getAnnotation(Scope.class);
                                String value = scope.value();
                                beanDefinition.setScope(value);
                            } else {
                                beanDefinition.setScope("singleton");
                            }

                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Set<String> keySet = beanDefinitionMap.keySet();
        for (String beanName : keySet) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

            if(beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanDefinition);
                singletonMap.put(beanName, bean);
            }
        }

    }

    private Object createBean(BeanDefinition beanDefinition) {

        Class clazz = beanDefinition.getClazz();
        try {
            Constructor clazzConstructor = clazz.getConstructor();
            Object o = clazzConstructor.newInstance();
            return o;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        String scope = beanDefinition.getScope();
        if(scope.equals("singleton")) {
            return singletonMap.get(beanName);
        } else {
            return createBean(beanDefinition);
        }
    }
}
