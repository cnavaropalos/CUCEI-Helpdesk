package mx.udg.helpdesk.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@NamedQueries(
        {
            @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
            @NamedQuery(name = "User.findByUserID", query = "SELECT u FROM User u WHERE u.userID = :userID"),
            @NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE u.name = :name"),
            @NamedQuery(name = "User.findByLastName", query = "SELECT u FROM User u WHERE u.lastName = :lastName")
        })
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "userID")
    private String userID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "lastName")
    private String lastName;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Capturist capturist;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private ReportManager reportManager;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private AreaManager areaManager;

    public User() {
    }

    public User(String userID) {
        this.userID = userID;
    }

    public User(String userID, String name, String lastName) {
        this.userID = userID;
        this.name = name;
        this.lastName = lastName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Capturist getCapturist() {
        return capturist;
    }

    public void setCapturist(Capturist capturist) {
        this.capturist = capturist;
    }

    public ReportManager getReportManager() {
        return reportManager;
    }

    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public AreaManager getAreaManager() {
        return areaManager;
    }

    public void setAreaManager(AreaManager areaManager) {
        this.areaManager = areaManager;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userID != null ? userID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.userID == null && other.userID != null) || (this.userID != null && !this.userID.equals(other.userID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.udg.helpdesk.entities.User[ userID=" + userID + " ]";
    }

}
