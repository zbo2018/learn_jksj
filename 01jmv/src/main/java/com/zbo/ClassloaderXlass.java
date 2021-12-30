package com.zbo;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Properties;

public class ClassloaderXlass {

    public static void main(String[] args) throws Exception {
        String filePath = Properties.class.getResource("/Hello.xlass").getPath();
        byte[] content = read(filePath);
        //解密
        for(int i=0;i<content.length;i++){
            content[i] = (byte)(255-content[i]);
        }

        Class<?> aClass = new MyClassLoader(content).findClass("Hello");
        Object o = aClass.newInstance();
        Method hello = aClass.getMethod("hello");
        hello.invoke(o);
    }

    private static class MyClassLoader extends ClassLoader{
        private byte[] content;

        public MyClassLoader(byte[] content){
            this.content = content;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return defineClass(name, content, 0, content.length);
        }
    }

    public static byte[] read(String filePath){
        ByteArrayOutputStream out = null;
        BufferedInputStream in = null;
        try {
            File file = new File(filePath);
            if(file.exists()){
                FileInputStream fileInputStream = new FileInputStream(file);
                in = new BufferedInputStream(fileInputStream);
                out = new ByteArrayOutputStream(1024);
                //当前字节输入流中的字节数
                byte[] temp = new byte[1024];
                int size = 0;
                while ((size = in.read(temp)) != -1) {
                    out.write(temp, 0, size);
                }

                byte[] content = out.toByteArray();
                return content;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
