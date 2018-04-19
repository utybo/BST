/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.utils;

import org.pushingpixels.trident.ease.TimelineEase;

/**
 * This class is adapted from the bezier-easing javascript library
 * 
 * <p>https://github.com/gre/bezier-easing
 *
 */
public class BezierEase implements TimelineEase
{
    private static int NEWTON_ITERATIONS = 40;
    private static float NEWTON_MIN_SLOPE = 0.001F;
    private static int SUBDIVISION_MAX_ITERATIONS = 50;
    private static float SUBDIVISION_PRECISION = 0.0000001F;

    private int kSplineTableSize = 11;
    private float kSampleStepSize = (float)(1.0 / (kSplineTableSize - 1.0));
    private float mX1, mY1, mX2, mY2;
    private float[] sampleValues;

    public BezierEase(float mX1, float mY1, float mX2, float mY2)
    {
        if(!(0 <= mX1 && mX1 <= 1 && 0 <= mX2 && mX2 <= 1))
        {
            throw new IllegalArgumentException("bezier x values must be in [0, 1] range");
        }

        this.mX1 = mX1;
        this.mX2 = mX2;
        this.mY1 = mY1;
        this.mY2 = mY2;
        
        // Precompute samples table
        sampleValues = new float[kSplineTableSize];
        if(mX1 != mY1 || mX2 != mY2)
        {
            for(int i = 0; i < kSplineTableSize; ++i)
            {
                sampleValues[i] = calcBezier(i * kSampleStepSize, mX1, mX2);
            }
        }
    }

    public float easing(float x)
    {
        if(mX1 == mY1 && mX2 == mY2)
        {
            return x; // linear
        }
        // Because JavaScript number are imprecise, we should guarantee the extremes are right.
        if(x == 0)
        {
            return 0;
        }
        if(x == 1)
        {
            return 1;
        }
        return calcBezier(getTForX(x), mY1, mY2);
    }

    public float getTForX(float aX)
    {
        float intervalStart = 0.0F;
        int currentSample = 1;
        float lastSample = kSplineTableSize - 1;

        for(; currentSample != lastSample && sampleValues[currentSample] <= aX; ++currentSample)
        {
            intervalStart += kSampleStepSize;
        }
        --currentSample;

        // Interpolate to provide an initial guess for t
        float dist = (aX - sampleValues[currentSample])
                / (sampleValues[currentSample + 1] - sampleValues[currentSample]);
        float guessForT = intervalStart + dist * kSampleStepSize;

        float initialSlope = getSlope(guessForT, mX1, mX2);
        if(initialSlope >= NEWTON_MIN_SLOPE)
        {
            return newtonRaphsonIterate(aX, guessForT, mX1, mX2);
        }
        else if(initialSlope == 0.0)
        {
            return guessForT;
        }
        else
        {
            return binarySubdivide(aX, intervalStart, intervalStart + kSampleStepSize, mX1, mX2);
        }
    }

    @Override
    public float map(float x)
    {
        return easing(x);
    }

    private float A(float aA1, float aA2)
    {
        return (float)(1.0 - 3.0 * aA2 + 3.0 * aA1);
    }

    private float B(float aA1, float aA2)
    {
        return (float)(3.0 * aA2 - 6.0 * aA1);
    }

    private float C(float aA1)
    {
        return (float)(3.0 * aA1);
    }

    private float binarySubdivide(float aX, float aA, float aB, float mX1, float mX2)
    {
        float currentX, currentT;
        int i = 0;
        do
        {
            currentT = (float)(aA + (aB - aA) / 2.0);
            currentX = calcBezier(currentT, mX1, mX2) - aX;
            if(currentX > 0.0)
            {
                aB = currentT;
            }
            else
            {
                aA = currentT;
            }
        }
        while(Math.abs(currentX) > SUBDIVISION_PRECISION && ++i < SUBDIVISION_MAX_ITERATIONS);
        return currentT;
    }

    private float calcBezier(float aT, float aA1, float aA2)
    {
        return ((A(aA1, aA2) * aT + B(aA1, aA2)) * aT + C(aA1)) * aT;
    }

    private float getSlope(float aT, float aA1, float aA2)
    {
        return (float)(3.0 * A(aA1, aA2) * aT * aT + 2.0 * B(aA1, aA2) * aT + C(aA1));
    };

    private float newtonRaphsonIterate(float aX, float aGuessT, float mX1, float mX2)
    {
        for(int i = 0; i < NEWTON_ITERATIONS; ++i)
        {
            float currentSlope = getSlope(aGuessT, mX1, mX2);
            if(currentSlope == 0.0)
            {
                return aGuessT;
            }
            float currentX = calcBezier(aGuessT, mX1, mX2) - aX;
            aGuessT -= currentX / currentSlope;
        }
        return aGuessT;
    }

}
