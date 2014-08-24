/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.validationengine.metadata;

/**
 * Holds static time translations to ms
 * 
 * @author Roger Parkinson
 *
 */
public class TimeModifier {
	private String m_type;
	public String getType() {
		return m_type;
	}
	public void setType(String m_type) {
		this.m_type = m_type;
	}
	private int m_delta;
	public int getDelta() {
		return m_delta;
	}
	public void setDelta(int m_delta) {
		this.m_delta = m_delta;
	}
	public TimeModifier(String type, int delta) {
		m_type = type;
		m_delta = delta;
	}
	private static TimeModifier[] s_timeModifiers = new TimeModifier[]{
		new TimeModifier("SECOND",1000),
		new TimeModifier("SECONDS",1000),
		new TimeModifier("MINUTE",60*1000),
		new TimeModifier("MINUTES",60*1000),
		new TimeModifier("HOUR",60*60*1000),
		new TimeModifier("HOURS",60*60*1000),
		new TimeModifier("DAY",60*60*1000*24),
		new TimeModifier("DAYS",60*60*1000*24),
		new TimeModifier("WEEK",60*60*1000*24*7),
		new TimeModifier("WEEKS",60*60*1000*24*7)
		};
	public static TimeModifier[] getTimeModifier() {
		return s_timeModifiers;
	}
}
