/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/
package org.yajul.scheduler;

import java.util.Date;

/**
 * TODO: Add class javadoc
 * User: josh
 * Date: Nov 25, 2003
 * Time: 8:48:11 AM
 */
public interface ScheduleEntryAttributes
{
    /**
     * Repeat interval value that indicates the job should only execute once.
     */
    int NON_REPEATING = -1;

    /**
     * Returns the name of the schedule entry.
     * @return
     */
    String getName();

    /**
     * Returns the start time of the schedule entry.
     * @return
     */
    Date getStartTime();

    /**
     * Returns the last time the schedule entry was executed.
     * @return
     */
    Date getLastTime();

    /**
     * Returns the next time the schedule entry will be executed.
     * @return
     */
    Date getNextTime();

    /**
     * Returns true if overlapping threads for the same task are allowed.
     * @return
     */
    boolean allowsOverlap();

    void setOverlap(boolean overlap);

    void enable(boolean flag);

    boolean isEnabled();

    void setRescheduling(int unit, int interval);

    int getRescheduleCount();

    int getRunCount();

    boolean isRepeating();

    void incrementRunCount(int i);

    void setLastTime(Date last);

    void incrementRescheduleCount(int i);

    void setNextTime(Date date);

    int getRescheduleUnit();

    int getReschedule();

    void setStartTime(Date start);

    void setName(String name);

    void setRescheduleUnit(int unit);

    void setIntervalUnit(int unit);

    void setReschedule(int reschedule);

    void setInterval(int interval);

    int getIntervalUnit();

    int getInterval();


}
