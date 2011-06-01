/*
 * Copyright 2007 the original author or authors.
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
package jdave.tools;

import static jdave.tools.Sentence.fromCamelCase;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Tommi Reiman
 */
public class SentenceTest {

    @Test
    public void camelCaseTestFormatsCorrectly() {
        assertEquals(fromCamelCase("camelCaseTest").toString(), "camel case test");
    }

    @Test
    public void initialUpperCaseLetterStaysUp() {
        assertEquals(fromCamelCase("KissMe").toString(), "Kiss me");
    }

    @Test
    public void numbersFormatCorrectly() {
        assertEquals(fromCamelCase("12camelsAreWorth100Euros").toString(),
                "12 camels are worth 100 euros");
    }
}