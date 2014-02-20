
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

@Entity
@Table(name = "modules")
@NamedQueries(
{
    @NamedQuery(name = "Module.findAll", query = "SELECT m FROM Module m"),
    @NamedQuery(name = "Module.findByModuleID", query = "SELECT m FROM Module m WHERE m.moduleID = :moduleID"),
    @NamedQuery(name = "Module.findByName", query = "SELECT m FROM Module m WHERE m.name = :name")
})
public class Module implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "moduleID")
    private Integer moduleID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "module")
    private List<DepartmentByModule> departmentByModuleList;

    public Module()
    {
    }

    public Module(Integer moduleID)
    {
        this.moduleID = moduleID;
    }

    public Module(Integer moduleID, String name)
    {
        this.moduleID = moduleID;
        this.name = name;
    }

    public Integer getModuleID()
    {
        return moduleID;
    }

    public void setModuleID(Integer moduleID)
    {
        this.moduleID = moduleID;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<DepartmentByModule> getDepartmentByModuleList()
    {
        return departmentByModuleList;
    }

    public void setDepartmentByModuleList(List<DepartmentByModule> departmentByModuleList)
    {
        this.departmentByModuleList = departmentByModuleList;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (moduleID != null ? moduleID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Module))
        {
            return false;
        }
        Module other = (Module) object;
        if ((this.moduleID == null && other.moduleID != null) || (this.moduleID != null && !this.moduleID.equals(other.moduleID)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mx.udg.helpdesk.entities.Module[ moduleID=" + moduleID + " ]";
    }
    
}
