package com.spring.common;

import com.spring.common.annotation.autowired.UDAutowired;
import com.spring.common.annotation.component.UDComponent;
import com.spring.common.annotation.component.UDComponentScan;
import com.spring.common.annotation.scope.UDScope;
import com.spring.common.bean.UDBeanDefinition;
import com.spring.common.bean.UDInitializingBean;
import com.spring.common.invocation.UDCglibCallback;
import com.spring.common.processor.UDBeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UDApplicationContext {

    private Class configClass;

    public Map<String, UDBeanDefinition> beanDefinition = new HashMap<>();
    public Map<String, Object> ioc = new HashMap<>();

    public List<UDBeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public UDApplicationContext(Class configClass) throws Exception {
        this.configClass = configClass;

        //获取需要依赖注入的类
        scan(configClass);

        udPreInstantiateSingletons();
    }

    public Object getBean(String beanName) throws Exception {
        if(beanDefinition.containsKey(beanName)) {
            UDBeanDefinition ubd = beanDefinition.get(beanName);
            if("singleton".equals(ubd.getScope())){
                if(ioc.containsKey(beanName)){
                    return ioc.get(beanName);
                }else {
                    return createBean(beanName);
                }
            }else {
                Object object = createBean(beanName);
                object = populateProperties(beanName, ubd, object);
                return object;
            }
        }else {
            throw new ClassNotFoundException();
        }
    }

    public Object createBean(String beanName) throws Exception {
        UDBeanDefinition beanDefinition = this.beanDefinition.get(beanName);
        Class clazz = beanDefinition.getClazz();
        Object instance = null;
        instance = getProxyObject(beanDefinition, beanName);

//        //依赖注入
//
//        for (Field field : clazz.getDeclaredFields()) {
//            if(field.isAnnotationPresent(UDAutowired.class)){
//                String name = field.getName();
//                Object o = getBean(name);
//                field.setAccessible(true);
//                field.set(instance, o);
//            }
//        }
//
//        for (UDBeanPostProcessor udBeanPostProcessor : beanPostProcessorList) {
//            udBeanPostProcessor.postProcessBeforeInitialization(instance, beanName);
//        }
//
//
//        if(instance instanceof UDInitializingBean){
//            ((UDInitializingBean) instance).afterPropertiesSet();
//        }
//
//
//
//        for (UDBeanPostProcessor udBeanPostProcessor : beanPostProcessorList) {
//            udBeanPostProcessor.postProcessAfterInitialization(instance, beanName);
//        }
        return instance;
    }

    public void scan(Class configClass){
        //获取依赖注入的类的目录
        UDComponentScan annotation = (UDComponentScan) configClass.getAnnotation(UDComponentScan.class);
        String scanPath = null;
        if(annotation !=null){
            scanPath = annotation.value();
        }
        URL resource = this.getClass().getClassLoader().getResource(scanPath.replace(".", "/"));
        File file = new File(resource.getPath());
        File[] files = file.listFiles();
        //对每个需要依赖注入的类 添加到beanDefinition中
        for(File clazz : files){
            System.out.println(clazz.getName());
            if(clazz.getAbsolutePath().endsWith(".class")) {
                try {
                    String clazzName = scanPath.concat("/").concat(clazz.getName().replace(".class", ""));
                    clazzName = clazzName.replace("/", ".");

                    Class<?> aClass = this.getClass().getClassLoader().loadClass(clazzName);

                    System.out.println(aClass);
                    if (aClass.isAnnotationPresent(UDComponent.class)) {

                        if(UDBeanPostProcessor.class.isAssignableFrom(aClass)){
                            UDBeanPostProcessor ob = (UDBeanPostProcessor)aClass.newInstance();
                            beanPostProcessorList.add(ob);
                        }

                        UDBeanDefinition bd = new UDBeanDefinition();
                        bd.setClazz(aClass);
                        if(aClass.isAnnotationPresent(UDScope.class)){
                            bd.setScope((aClass.getAnnotation(UDScope.class)).value());
                        }else {
                            bd.setScope("singleton");
                        }
                        String beanName = aClass.getAnnotation(UDComponent.class).value();
                        beanDefinition.put(beanName, bd);
                    }

                }catch (ClassNotFoundException exception){
                    exception.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void udPreInstantiateSingletons() throws Exception {
        for(Map.Entry<String, UDBeanDefinition> entry : beanDefinition.entrySet()){
            String beanName = entry.getKey();
            UDBeanDefinition ubd = entry.getValue();
            //对于单例类，添加cglib的对象到bean 实例的map中
            if("singleton".equals(ubd.getScope())){
               // Object bean = getBean(beanName);
                Object target = createBean(beanName);
                ioc.put(beanName, target);
                populateProperties(beanName, ubd, target);
            }
        }
    }

    public Object getProxyObject(UDBeanDefinition ubd, String beanName) throws Exception {
        Enhancer enhancer = new Enhancer();
        Class clazz = ubd.getClazz();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new UDCglibCallback());
        Object target = enhancer.create();

        return target;
    }

    public Object setProxyAttributes(Object target, Class clazz) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            if(field.isAnnotationPresent(UDAutowired.class)){
                UDAutowired annotation = field.getAnnotation(UDAutowired.class);

                Object bean = ioc.get(field.getName());
                if(bean == null && annotation.required()){
                    if(beanDefinition.containsKey(field.getName())){
                        bean = createBean(field.getName());
                    }else {
                        throw new ClassNotFoundException();
                    }
                }
                field.setAccessible(true);
                field.set(target, bean);
            }
        }
        return target;
    }

    public Object runAfterPropertiesSet(Object target, Class clazz) throws InvocationTargetException, IllegalAccessException {
        if(UDInitializingBean.class.isAssignableFrom(clazz)){
            Method[] methods = target.getClass().getMethods();
            for (Method method : methods) {
                if("afterPropertiesSet".equals(method.getName())){
                    method.invoke(target);
                }
            }
        }
        return target;
    }

    public Object populateProperties(String beanName, UDBeanDefinition ubd, Object target) throws Exception {
        Class clazz = ubd.getClazz();
        for (UDBeanPostProcessor udBeanPostProcessor : beanPostProcessorList) {
            udBeanPostProcessor.postProcessBeforeInitialization(target, beanName);
        }
        target = setProxyAttributes(target, clazz);
        target = runAfterPropertiesSet(target, clazz);

        for (UDBeanPostProcessor udBeanPostProcessor : beanPostProcessorList) {
            udBeanPostProcessor.postProcessAfterInitialization(target, beanName);
        }
        return target;
    }
}
