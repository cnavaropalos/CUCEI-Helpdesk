
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "areas")
@NamedQueries(
{
    @NamedQuery(name = "Area.findAll", query = "SELECT a FROM Area a"),
    @NamedQuery(name = "Area.findByAreaID", query = "SELECT a FROM Area a WHERE a.areaID = :areaID"),
    @NamedQuery(name = "Area.findByName", query = "SELECT a FROM Area a WHERE a.name = :name")
})
public class Area implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "areaID")
    private Integer areaID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "areaID")
    private List<ProblemCategory> problemCategoryList;
    @JoinColumn(name = "areaManagerID", referencedColumnName = "userID")
    @ManyToOne(optional = false)
    private AreaManager areaManagerID;

    public Area()
    {
    }

    public Area(Integer areaID)
    {
        this.areaID = areaID;
    }

    public Area(Integer areaID, String name)
    {
        this.areaID = areaID;
        this.name = name;
    }

    public Integer getAreaID()
    {
        return areaID;
    }

    public void setAreaID(Integer areaID)
    {
        this.areaID = areaID;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<ProblemCategory> getProblemCategoryList()
    {
        return problemCategoryList;
    }

    public void setProblemCategoryList(List<ProblemCategory> problemCategoryList)
    {
        this.problemCategoryList = problemCategoryList;
    }

    public AreaManager getAreaManagerID()
    {
        return areaManagerID;
    }

    public void setAreaManagerID(AreaManager areaManagerID)
    {
        this.areaManagerID = areaManagerID;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (areaID != null ? areaID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Area))
        {
            return false;
        }
        Area other = (Area) object;
        if ((this.areaID == null && other.areaID != null) || (this.areaID != null && !this.areaID.equals(other.areaID)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mx.udg.helpdesk.entities.Area[ areaID=" + areaID + " ]";
    }
    
}
