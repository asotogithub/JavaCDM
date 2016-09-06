package trueffect.truconnect.api.commons.model;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * This class is a container for a set of Query Parameters that collaborates getting filtered
 * information for a given HTTP request.
 * <p>
 * The main objective of this is to be able to provide a flexible tool that allows request to create
 * complex filtering in a given API GET path.
 * </p>
 */
@ApiModel(value="SearchCriteria")
@XmlRootElement(name = "SearchCriteria")
public class SearchCriteria {
	
    public static final Integer SEARCH_CRITERIA_START_INDEX = 0;
    public static final Integer SEARCH_CRITERIA_PAGE_SIZE = 1000;
    
	/**
	 * Specify the start index for the records your are looking for. Start index should start with 0.
	 * 
	 * This property is used in conjunction with the pageSize to get small subsets of records allowing both the API client and the server to scale for large record sets.
	 */
	@DefaultValue("0")
	@QueryParam("startIndex")
	protected Integer startIndex = SEARCH_CRITERIA_START_INDEX;
	
	/**
	 * Specifies the limit on the number of records per page. The maximum page size allowed is 1000. If page size is not specified, then the default page size would be set to maximum limit of 1000.
	 * 
	 * This property is used in conjunction with the startIndex to get small subsets of records allowing both the API client and the server to scale for large record sets. 
	 */
	@DefaultValue("1000")
	@QueryParam("pageSize")
	protected Integer pageSize = SEARCH_CRITERIA_PAGE_SIZE;
	
	/**
	 * Specifies arbitrary queries based on fields from Commons' classes, also manages the ordering .
	 */
	@ApiModelProperty(required=true)
	@DefaultValue("")
	@QueryParam("query")
	protected String query = "";

	public Integer getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}

        @Override
	public String toString() {
		return "SearchCriteria [startIndex=" + startIndex + ", pageSize="
				+ pageSize + ", query=" + query + "]";
	}
}
