package mx.udg.helpdesk.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class DepartmentByModulePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "moduleID")
    private int moduleID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "departmentID")
    private int departmentID;

    public DepartmentByModulePK() {
    }

    public DepartmentByModulePK(int moduleID, int departmentID) {
        this.moduleID = moduleID;
        this.departmentID = departmentID;
    }

    public int getModuleID() {
        return moduleID;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) moduleID;
        hash += (int) departmentID;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DepartmentByModulePK)) {
            return false;
        }
        DepartmentByModulePK other = (DepartmentByModulePK) object;
        if (this.moduleID != other.moduleID) {
            return false;
        }
        if (this.departmentID != other.departmentID) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.udg.helpdesk.entities.DepartmentByModulePK[ moduleID=" + moduleID + ", departmentID=" + departmentID + " ]";
    }

}
