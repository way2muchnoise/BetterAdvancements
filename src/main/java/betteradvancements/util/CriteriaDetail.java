package betteradvancements.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum CriteriaDetail {
	OFF("Off", "Vanilla default"),
	DEFAULT("Default", "List which criteria you have already obtained"),
	SPOILER("Spoiler", "Also reveal unobtained criteria");

	private final String name;
	private final String comment;

	CriteriaDetail(String description, String comment) {
		this.name = description;
		this.comment = comment;
	}

	public static CriteriaDetail fromName(String value) {
		return Arrays.stream(values())
	        .filter(x -> x.name.equals(value))
	        .findFirst()
	        .orElse(DEFAULT);
	}
	
	public String getName() {
		return name;
	}

    public static String[] names() {
    	return Arrays.stream(values())
    	    .map(CriteriaDetail::getName)
    	    .toArray(String[]::new);
    }
    
    public static String comments() {
    	return "Lists the criteria for partially completed advancements, e.g. the biomes required for 'Adventuring Time'" +
    		Arrays.stream(values())
    	    .map(x -> "\n    " + x.getName() + ": " + x.comment)
    	    .collect(Collectors.joining());
    }
}
