package trueffect.truconnect.api.commons.model.importexport;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by richard.jaldin on 7/7/2016.
 */
public class PackageMapId {
    private String id;
    private String name;

    public PackageMapId(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackageMapId)) return false;

        PackageMapId that = (PackageMapId) o;

        if(id != null && that.getId() != null) return id.equals(that.getId());
        if(name != null && that.getName() != null) return name.equals(that.getName());
        return false;
    }

    @Override
    public int hashCode() {
        int result =  0;
        if(id != null) {
            result = id.hashCode();
        } else if(name != null) {
            result = 31 * result + name.hashCode();
        }
        return result;
    }
}
