package mx.udg.helpdesk.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "capturists")
@NamedQueries(
        {
            @NamedQuery(name = "Capturist.findAll", query = "SELECT c FROM Capturist c"),
            @NamedQuery(name = "Capturist.findByUserID", query = "SELECT c FROM Capturist c WHERE c.userID = :userID"),
            @NamedQuery(name = "Capturist.findByUsername", query = "SELECT c FROM Capturist c WHERE c.username = :username"),
            @NamedQuery(name = "Capturist.findByPassword", query = "SELECT c FROM Capturist c WHERE c.password = :password")
        })
public class Capturist implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "userID")
    private String userID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "password")
    private String password;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "capturisID")
    private List<Report> reportList;
    @JoinColumn(name = "userID", referencedColumnName = "userID", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private User user;

    public Capturist() {
    }

    public Capturist(String userID) {
        this.userID = userID;
    }

    public Capturist(String userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        if (!(object instanceof Capturist)) {
            return false;
        }
        Capturist other = (Capturist) object;
        if ((this.userID == null && other.userID != null) || (this.userID != null && !this.userID.equals(other.userID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.udg.helpdesk.entities.Capturist[ userID=" + userID + " ]";
    }

}
