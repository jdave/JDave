/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jdave.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import jdave.Group;
import jdave.runner.Groups;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Lasse Koskela
 */
public class ReflectionTest {
    public static class ClassBeingExamined {
        public static int publicStaticVoidCalls;
        public static int publicStaticIntAmbiguousCalls;
        public static int publicIntAmbiguousStringCalls;

        public static void publicStaticVoid() {
            publicStaticVoidCalls++;
        }

        public static int ambiguous() {
            publicStaticIntAmbiguousCalls++;
            return 0;
        }

        public int ambiguous(String arg) {
            publicIntAmbiguousStringCalls++;
            return 1;
        }

        protected static void notPublic() {
        }

        public void notStatic() {
        }
    }

    @Before
    public void resetClassBeingExamined() {
        ClassBeingExamined.publicIntAmbiguousStringCalls = 0;
        ClassBeingExamined.publicStaticIntAmbiguousCalls = 0;
        ClassBeingExamined.publicStaticVoidCalls = 0;
    }

    @Test
    public void determinesVoidCorrectly() throws Exception {
        org.junit.Assert.assertTrue(Reflection.isVoid(Reflection.getMethod(
                ClassBeingExamined.class, "publicStaticVoid")));
        org.junit.Assert.assertFalse(Reflection.isVoid(Reflection.getMethod(
                ClassBeingExamined.class, "ambiguous", null, null)));
    }

    @Test
    public void determinesVisibilityCorrectly() throws Exception {
        org.junit.Assert.assertTrue(Reflection.isPublic(Reflection.getMethod(
                ClassBeingExamined.class, "publicStaticVoid")));
        org.junit.Assert.assertFalse(Reflection.isPublic(Reflection.getMethod(
                ClassBeingExamined.class, "notPublic", null, null)));
    }

    @Test
    public void determinesScopeCorrectly() throws Exception {
        org.junit.Assert.assertTrue(Reflection.isStatic(Reflection.getMethod(
                ClassBeingExamined.class, "publicStaticVoid")));
        org.junit.Assert.assertFalse(Reflection.isStatic(Reflection.getMethod(
                ClassBeingExamined.class, "notStatic", null, null)));
    }

    @Test
    public void findsMethodsWithTheirModifiers() throws Exception {
        try {
            Method method = Reflection.getMethod(ClassBeingExamined.class, "publicStaticVoid",
                    Modifier.PUBLIC, Modifier.STATIC);
            method.invoke(null);
            org.junit.Assert.assertEquals(1, ClassBeingExamined.publicStaticVoidCalls);
        } catch (NoSuchMethodException e) {
            org.junit.Assert.fail("should've found method");
        }
    }

    @Test
    public void findsMethodsWithTheirParameterTypes() throws Exception {
        try {
            Method method = Reflection.getMethod(ClassBeingExamined.class, "ambiguous",
                    new Class[] { String.class }, Modifier.PUBLIC);
            method.invoke(new ClassBeingExamined(), "lol");
            org.junit.Assert.assertEquals(1, ClassBeingExamined.publicIntAmbiguousStringCalls);
        } catch (NoSuchMethodException e) {
            org.junit.Assert.fail("should've found method");
        }
    }

    @Test
    public void reportsFailureToFindMethod() throws Exception {
        try {
            Reflection.getMethod(ClassBeingExamined.class, "noSuchMethod", Modifier.PROTECTED,
                    Modifier.STATIC);
            org.junit.Assert.fail("should not have found a method");
        } catch (NoSuchMethodException expected) {
            org.junit.Assert.assertEquals("protected static method 'noSuchMethod' not found",
                    expected.getMessage());
        }
    }

    @Ignore
    interface IgnoredInterface { }

    @Group("test")
    static class GroupImplementsIgnoredInterface implements IgnoredInterface { }

    static class ExtendsGroupImplementsIgnoredInterface extends GroupImplementsIgnoredInterface { }

    @Test
    public void getAnnotation() {
        Assert.assertNotNull(Reflection.getAnnotation(IgnoredInterface.class, Ignore.class));
        Assert.assertNotNull(Reflection.getAnnotation(GroupImplementsIgnoredInterface.class, Ignore.class));
        Assert.assertNotNull(Reflection.getAnnotation(ExtendsGroupImplementsIgnoredInterface.class, Ignore.class));
        Assert.assertNotNull(Reflection.getAnnotation(ExtendsGroupImplementsIgnoredInterface.class, Group.class));
        Assert.assertNull(Reflection.getAnnotation(ExtendsGroupImplementsIgnoredInterface.class, Groups.class));
    }

    public static class WithPrivateField {
        private boolean field;
        public boolean getField() {
            return field;
        }
    }
    WithPrivateField obj = new WithPrivateField();

    @Test
    public void setPrivateField() {
        Reflection.setPrivateField(obj, "field", Boolean.TRUE);
        Assert.assertTrue(obj.getField());
    }
    @Test(expected=RuntimeException.class)
    public void setPrivateFieldWithWrongObjectTypeThrowsException() {
        Reflection.setPrivateField(obj, "field", new Object());
    }
    @Test(expected=RuntimeException.class)
    public void setNonExistantFieldThrowsException() {
        Reflection.setPrivateField(obj, "noField", Boolean.TRUE);
    }
}
