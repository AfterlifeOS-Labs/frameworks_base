/*
 * Copyright (C) 2022 The Android Open Source Project
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

package android.libcore.regression;

import android.perftests.utils.BenchmarkState;
import android.perftests.utils.PerfStatusReporter;

import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BigIntegerPerfTest {
    @Rule public PerfStatusReporter mPerfStatusReporter = new PerfStatusReporter();

    @Test
    public void timeRandomDivision() throws Exception {
        Random r = new Random();
        BigInteger x = new BigInteger(1024, r);
        BigInteger y = new BigInteger(1024, r);
        BenchmarkState state = mPerfStatusReporter.getBenchmarkState();
        while (state.keepRunning()) {
            x.divide(y);
        }
    }

    @Test
    public void timeRandomGcd() throws Exception {
        Random r = new Random();
        BigInteger x = new BigInteger(1024, r);
        BigInteger y = new BigInteger(1024, r);
        BenchmarkState state = mPerfStatusReporter.getBenchmarkState();
        while (state.keepRunning()) {
            x.gcd(y);
        }
    }

    @Test
    public void timeRandomMultiplication() throws Exception {
        Random r = new Random();
        BigInteger x = new BigInteger(1024, r);
        BigInteger y = new BigInteger(1024, r);
        BenchmarkState state = mPerfStatusReporter.getBenchmarkState();
        while (state.keepRunning()) {
            x.multiply(y);
        }
    }
}
