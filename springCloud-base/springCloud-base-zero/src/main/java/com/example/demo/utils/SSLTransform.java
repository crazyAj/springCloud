package com.example.demo.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;

/**
 * JKS 与 PFX 互转
 */
public class SSLTransform {

    public static void main(String[] args) {
        String jksFilePath = "d:/ssl.jks";
        String jksPwd = "123456";
        String pfxFilePath = "d:/ssl";
        String pfxPwd = "123456";
        convertJKSToPFX(jksFilePath,jksPwd,pfxFilePath,pfxPwd);
    }

    /**
     * 从JKS格式转换为PKCS12格式
     *    jksFilePath    JKS格式证书库路径
     *    jksPwd      JKS格式证书库密码
     *    pfxFilePath    PKCS12格式证书库保存文件夹
     *    pfxPwd      PKCS12格式证书库密码
     */
    public static void convertJKSToPFX(String jksFilePath, String jksPwd, String pfxFolderPath, String pfxPwd) {
        FileInputStream fis = null;
        try {
            System.out.println("输入JKS文件：" + jksFilePath);
            KeyStore inputKeyStore = KeyStore.getInstance("JKS");
            fis = new FileInputStream(jksFilePath);
            char[] srcPwd = jksPwd == null ? null : jksPwd.toCharArray();
            char[] destPwd = pfxPwd == null ? null : pfxPwd.toCharArray();
            inputKeyStore.load(fis, srcPwd);
            KeyStore outputKeyStore = KeyStore.getInstance("PKCS12");
            Enumeration enums = inputKeyStore.aliases();
            while (enums.hasMoreElements()) {
                String keyAlias = (String) enums.nextElement();
//                System.out.println("alias=[" + keyAlias + "]");
                outputKeyStore.load(null, destPwd);
                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, srcPwd);
                    java.security.cert.Certificate[] certChain = inputKeyStore.getCertificateChain(keyAlias);
                    outputKeyStore.setKeyEntry(keyAlias, key, destPwd, certChain);
                }
                String fName = pfxFolderPath + "_" + keyAlias + ".pfx";
                FileOutputStream out = new FileOutputStream(fName);
                outputKeyStore.store(out, destPwd);
                out.close();
                outputKeyStore.deleteEntry(keyAlias);
                System.out.println("输出PFX文件：" + fName);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从PFX格式转换为JKS格式
     *    pfxFilePath    PFX格式证书库路径
     *    keystorePwd    PFX格式证书库密码
     *    jksFilePath    JKS格式证书库保存文件夹
     */
    public static void converPFXToJKS(String pfxFilePath, String keystorePwd, String jksFilePath) {
        FileInputStream fis = null;
        try {
            System.out.println("输入PFX文件：" + pfxFilePath);
            KeyStore inputKeyStore = KeyStore.getInstance("PKCS12");
            fis = new FileInputStream(pfxFilePath);
            char[] nPassword = keystorePwd == null ? null : keystorePwd.toCharArray();
            inputKeyStore.load(fis, nPassword);
            KeyStore outputKeyStore = KeyStore.getInstance("JKS");
            outputKeyStore.load(null, keystorePwd.toCharArray());
            Enumeration enums = inputKeyStore.aliases();
            while (enums.hasMoreElements()) {
                String keyAlias = (String) enums.nextElement();
//                System.out.println("alias=[" + keyAlias + "]");
                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, nPassword);
                    Certificate[] certChain = inputKeyStore.getCertificateChain(keyAlias);
                    outputKeyStore.setKeyEntry(keyAlias, key, keystorePwd.toCharArray(), certChain);
                }
            }
            FileOutputStream out = new FileOutputStream(jksFilePath);
            outputKeyStore.store(out, nPassword);
            out.close();
            System.out.println("输出JKS文件：" + jksFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
