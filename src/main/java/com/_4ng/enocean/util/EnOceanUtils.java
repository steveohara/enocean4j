/*
 * Copyright $DateInfo.year enocean4j development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com._4ng.enocean.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * A class offering static methods for easily handling operations on bytes and
 * byte arrays
 *
 * @author bonino
 */
public class EnOceanUtils {

    /**
     * Given a byte array provides back the corresponding string representation
     * as 0xHHHH. the byte order is assumed to be little endian, i.e., LSB
     * located at higher positions in the array.
     *
     * @param byteArray
     * @return The {@link String} representation of the given byte array.
     */
    public static String toHexString(byte byteArray[]) {
        if (byteArray == null || byteArray.length == 0) {
            return "<empty>";
        }

        // prepare the string buffer for holding the final byte representation
        StringBuilder hexBytes = new StringBuilder();

        // append the hexadecimal notation identifier
        hexBytes.append("0x");

        // convert each byte
        for (byte aByteArray : byteArray) {
            hexBytes.append(String.format("%02X", aByteArray));
        }
        // render the buffer as a string
        return hexBytes.toString();
    }

    /**
     * Given a byte provides back the corresponding string representation as
     * 0xHH.
     *
     * @param theByte to convert.
     * @return The {@link String} representation of the given byte.
     */
    public static String toHexString(byte theByte) {
        return String.format("0x%02X", theByte);
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     *
     * @throws ClassNotFoundException
     * @throws java.io.IOException
     */
    @SuppressWarnings("unchecked")
    public static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String fileName = resource.getFile();
            String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
            dirs.add(new File(fileNameDecoded));
        }
        List<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     *
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (file.isDirectory()) {
                    assert !fileName.contains(".");
                    classes.addAll(findClasses(file, packageName + '.' + fileName));
                }
                else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                    Class _class;
                    try {
                        _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                    }
                    catch (ExceptionInInitializerError e) {
                        // happen, for example, in classes, which depend on
                        // Spring to inject some beans, and which fail,
                        // if dependency is not fulfilled
                        _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false, Thread.currentThread().getContextClassLoader());
                    }
                    classes.add(_class);
                }
            }
        }
        return classes;
    }

}
