package betteradvancements.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CriteriaDetail {
	OFF("Off", "Vanilla default", false, false),
	DEFAULT("Default", "List which criteria you have already obtained", true, false),
	SPOILER("Spoiler", "Only reveal unobtained criteria", false, true),
	ALL("All", "Show both obtained and unobtained criteria", true, true);

	private final String name, comment;
	private final boolean obtained, unobtained;

	CriteriaDetail(String description, String comment, boolean obtained, boolean unobtained) {
		this.name = description;
		this.comment = comment;
		this.obtained = obtained;
		this.unobtained = unobtained;
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

	public boolean showObtained() {
		return obtained;
	}

	public boolean showUnobtained() {
		return unobtained;
	}

	public static List<String> names() {
    	return Arrays.stream(values())
    	    .map(CriteriaDetail::getName)
    	    .collect(Collectors.toList());
    }
    
    public static String comments() {
    	return "Lists the criteria for partially completed advancements, e.g. the biomes required for 'Adventuring Time'" +
    		Arrays.stream(values())
    	    .map(x -> "\n    " + x.getName() + ": " + x.comment)
    	    .collect(Collectors.joining());
    }

	public static List<CriteriaDetail> valuesAsList() {
		return Arrays.asList(CriteriaDetail.values());
	}
}
