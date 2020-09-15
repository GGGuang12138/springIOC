package com.gg.framework;

import com.gg.framework.annotation.Autowired;
import com.gg.framework.annotation.Component;
import com.gg.framework.annotation.ComponentScan;
import com.gg.framework.annotation.Scope;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creat by GG
 * Date on 2020/9/2  8:09 下午
 */
public class CustomApplicationContext {

    private Class configClass;

    private Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
    private Map<String,Object> singletonPool = new ConcurrentHashMap<String, Object>();


    public CustomApplicationContext(Class configClass) {

        this.configClass = configClass;

        //扫描(判断类上是否存在Component)(class文件) --> 形成beanDefinition
        List<Class> classList = scan(configClass);

        for (Class clazz: classList){
                Component component = (Component) clazz.getAnnotation(Component.class);
                String beanName = component.value();

                //为什么BeanDefinition
                //扫描得到的beanName、Clsss路径、bean的属性进行保存进行保存 getBean时不用重新扫描

                BeanDefinition beanDefinition = new BeanDefinition();
                if (clazz.isAnnotationPresent(Scope.class)){
                    Scope scope = (Scope) clazz.getAnnotation(Scope.class);
                    beanDefinition.setScope(scope.value());
                }else{
                    beanDefinition.setScope("singleton");
                }
                beanDefinition.setBeanClass(clazz);
                beanDefinitionMap.put(beanName,beanDefinition);
        }

        for (String beanName : beanDefinitionMap.keySet()){
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")){
                //生产bean
                Object bean = createBean(beanName,beanDefinition);
                singletonPool.put(beanName,bean);
            }
        }

    }

    private Object createBean(String beanName,BeanDefinition beanDefinition) {
        //实例化、填充属性、Aware、初始化
        Class beanClass = beanDefinition.getBeanClass();
        try {
            // 从beanDefinition中获取类型，并实例化
            Object bean = beanClass.getDeclaredConstructor().newInstance();
            //属性填充
            Field[] fields = beanClass.getDeclaredFields(); //DeclaredFields 所有属性
            for(Field field: fields){
                if (field.isAnnotationPresent(Autowired.class)){
                    Object annotationField = getBean(field.getName());
                    field.setAccessible(true); //反射产生对象要打开权限
                    field.set(bean,annotationField);
                }
            }
            // Aware
            if (bean instanceof BeanNameAware){
                ((BeanNameAware)bean).setBeanName(beanName); //实现该端口就调用此方法
            }
            // 初始化
            if (bean instanceof InitializingBean){
                ((InitializingBean)bean).afterPropertiesSet(); //实现该端口就调用此方法
            }
            return bean;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getBean(String beanName){
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition.getScope().equals("prototype")){
            return createBean(beanName,beanDefinition);
        }else{
            Object bean = singletonPool.get(beanName);
            if (bean == null){
                Object newBean = createBean(beanName,beanDefinition);
                singletonPool.put(beanName,newBean);
                return newBean;
            }
            return bean;
        }
    }

    private List<Class> scan(Class configClass) {
        List<Class> list = new ArrayList<Class>();
        if (configClass.isAnnotationPresent(ComponentScan.class)){
            ComponentScan componentScan = (ComponentScan)configClass.getAnnotation(ComponentScan.class);
            String path = componentScan.value();
            path = path.replace(".","/"); //找到classPath（target/classes）

            //扫描path下到类
            ClassLoader classLoader = CustomApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());

            if (file.isDirectory()) {
                for (File f: file.listFiles()){
                    System.out.println(f);

                    String absolutePath = f.getAbsolutePath();
                    absolutePath = absolutePath.substring(absolutePath.indexOf("com"),absolutePath.indexOf(".class"));
                    absolutePath = absolutePath.replace("/",".");

                    Class clazz = null;
                    try {
                        clazz = classLoader.loadClass(absolutePath);
                        if (clazz.isAnnotationPresent(Component.class)) {
                            list.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }
}
