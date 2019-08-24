package net.bmahe.genetics4j.samples;

import java.util.List;
import java.util.Map;

public class TSPLIBProblem {

	public final Map<String, String> attributes;
	public final List<Position> cities;

	public TSPLIBProblem(final Map<String, String> _attributes, final List<Position> _cities) {
		this.attributes = _attributes;
		this.cities = _cities;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public List<Position> getCities() {
		return cities;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((cities == null) ? 0 : cities.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TSPLIBProblem other = (TSPLIBProblem) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (cities == null) {
			if (other.cities != null)
				return false;
		} else if (!cities.equals(other.cities))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TSPLIBProblem [attributes=" + attributes + "]";
	}
}