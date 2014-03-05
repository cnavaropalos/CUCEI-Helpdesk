package mx.udg.helpdesk.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Carlos Navapa
 */
@Entity
@Table(name = "floors")
@NamedQueries({
    @NamedQuery(name = "Floor.findAll", query = "SELECT f FROM Floor f")})
public class Floor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "floorID")
    private Integer floorID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "floorID")
    private List<DepartmentsByModule> departmentsByModuleList;

    public Floor() {
    }

    public Floor(Integer floorID) {
        this.floorID = floorID;
    }

    public Floor(Integer floorID, String name) {
        this.floorID = floorID;
        this.name = name;
    }

    public Integer getFloorID() {
        return floorID;
    }

    public void setFloorID(Integer floorID) {
        this.floorID = floorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DepartmentsByModule> getDepartmentsByModuleList() {
        return departmentsByModuleList;
    }

    public void setDepartmentsByModuleList(List<DepartmentsByModule> departmentsByModuleList) {
        this.departmentsByModuleList = departmentsByModuleList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (floorID != null ? floorID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Floor)) {
            return false;
        }
        Floor other = (Floor) object;
        if ((this.floorID == null && other.floorID != null) || (this.floorID != null && !this.floorID.equals(other.floorID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.udg.helpdesk.entities.Floor[ floorID=" + floorID + " ]";
    }

}
