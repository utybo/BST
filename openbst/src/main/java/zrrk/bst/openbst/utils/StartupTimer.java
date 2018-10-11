/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StartupTimer
{
    public static enum StartupTimerState
    {
        UNKNOWN, ERROR, OK, OK_WARNING, ABORTED;
    }

    private class StartupTimerStep
    {
        private String stepName;
        private long timeTaken = -1;
        private StartupTimerState state = StartupTimerState.UNKNOWN;
        @SuppressWarnings("unused")
        private Object additionalObject;
    }

    private List<StartupTimerStep> previousSteps = new ArrayList<>();

    private long time;
    private StartupTimerStep currentStep;

    public void step(String stepName)
    {
        step(stepName, StartupTimerState.OK);
    }

    public void step(String stepName, StartupTimerState state)
    {
        step(stepName, state, null);
    }

    public void endStep()
    {
        endStep(StartupTimerState.OK);
    }

    public void endStep(StartupTimerState state)
    {
        endStep(state, null);
    }

    public void endStep(StartupTimerState state, Object additionalObject)
    {
        if(currentStep != null)
        {
            currentStep.timeTaken = System.currentTimeMillis() - time;
            currentStep.additionalObject = additionalObject;
            currentStep.state = state;
            previousSteps.add(currentStep);
            currentStep = null;
        }
        else
        {
            throw new NullPointerException("No steps started.");
        }
    }

    public void step(String stepName, StartupTimerState state, Object additionalObject)
    {
        if(currentStep != null)
        {
            currentStep.timeTaken = System.currentTimeMillis() - time;
            currentStep.additionalObject = additionalObject;
            currentStep.state = state;
            previousSteps.add(currentStep);
        }
        currentStep = new StartupTimerStep();
        currentStep.stepName = stepName;
        time = System.currentTimeMillis();
    }

    public void forceEnd()
    {
        if(currentStep != null)
            endStep(StartupTimerState.ABORTED);
    }

    public String getReport()
    {
        forceEnd();
        StringBuilder sb = new StringBuilder();
        sb.append("Startup Times (ms)\n");
        int max = Collections.max(previousSteps.stream().map((a) -> a.stepName.length())
                .collect(Collectors.toList()));
        for(StartupTimerStep step : previousSteps)
            sb.append(String.format("%" + max + "s", step.stepName) + " : " + step.timeTaken
                    + " ms [" + step.state.toString() + "]\n");
        return sb.toString();
    }
}
