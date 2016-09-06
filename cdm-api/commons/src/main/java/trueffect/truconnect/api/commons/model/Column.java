package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "Column")
public class Column {

    private String column;
    private Object value;
    
    public Column() {	}
    
	public Column(String column, Object value) {
		this.column = column;
		this.value = value;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Column [column=" + column + ", value=" + value + "]";
	}
}
