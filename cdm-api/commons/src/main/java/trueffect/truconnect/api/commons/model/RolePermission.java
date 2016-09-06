package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.annotations.TableFieldMapping;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO that includes the Role ID and Permission ID
 * Created by marcelo.heredia on 6/27/2015.
 * @author Marcelo Heredia
 */
@XmlRootElement(name = "RolePermission")
public class RolePermission {
    
    @TableFieldMapping(table = "ROLE_PERMISSION", field = "ROLE_ID")
    private String roleId;
    @TableFieldMapping(table = "ROLE_PERMISSION", field = "PERMISSION_ID")
    private Long permissionId;
    @TableFieldMapping(table = "PERMISSION", field = "PERMISSION_KEY")
    private String permissionKey;

    public RolePermission() {
    }

    public RolePermission(String roleId, Long permissionId, String permissionKey) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.permissionKey = permissionKey;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    public void setPermissionKey(String permissionKey) {
        this.permissionKey = permissionKey;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
