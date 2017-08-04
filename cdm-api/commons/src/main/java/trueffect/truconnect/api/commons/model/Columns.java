package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "Columns")
public class Columns {

    private java.util.List<Column> columns;
    
    public Columns() {	}
    
	public Columns(java.util.List<Column> columns) {
		this.columns = columns;
	}

	public java.util.List<Column> getColumns() {
		return columns;
	}

	public void setColumns(java.util.List<Column> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "Columns [columns=" + columns + "]";
	}
}
