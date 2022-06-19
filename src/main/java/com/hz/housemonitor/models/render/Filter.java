package com.hz.housemonitor.models.render;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Filter implements Comparable {
	private String name;
	private String icon;

	public Filter(String name) {
		this.name = name;
		this.icon = "fas " + mapIcon(name);
	}

	private String mapIcon(String text) {
		if (text.equalsIgnoreCase("temperature")) {
			return "fa-thermometer-full";
		} else if (text.equalsIgnoreCase("humidity")) {
			return "fa-water";
		} else if (text.equalsIgnoreCase("battery")) {
			return "fa-battery-full";
		} else if (text.equalsIgnoreCase("voltage")) {
			return "fa-bolt";
		} else if (text.equalsIgnoreCase("presence")) {
				return "fa-toggle-on";
		} else if (text.equalsIgnoreCase("pressure")) {
			return "fa-cloud";
		} else if (text.equalsIgnoreCase("winddirection")) {
			return "fa-compass";
		} else if (text.equalsIgnoreCase("windspeed")) {
			return "fa-tachometer-alt";
		} else if (text.equalsIgnoreCase("weather")) {
			return "fa-cloud-sun-rain";
		} else {
			return "fa-question-circle";
		}
	}

	@Override
	public boolean equals(Object obj) {
		return ((Filter) obj).getName().equals(getName());
	}

	@Override
	public int compareTo(Object o) {
		Filter e = (Filter) o;
		return getName().compareTo(e.getName());
	}
}
