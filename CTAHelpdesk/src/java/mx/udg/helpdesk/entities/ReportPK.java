package mx.udg.helpdesk.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Embeddable
public class ReportPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "reportID")
    private int reportID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "reportDate")
    @Temporal(TemporalType.DATE)
    private Date reportDate;

    public ReportPK() {
    }

    public ReportPK(int reportID, Date reportDate) {
        this.reportID = reportID;
        this.reportDate = reportDate;
    }

    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) reportID;
        hash += (reportDate != null ? reportDate.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReportPK)) {
            return false;
        }
        ReportPK other = (ReportPK) object;
        if (this.reportID != other.reportID) {
            return false;
        }
        if ((this.reportDate == null && other.reportDate != null) || (this.reportDate != null && !this.reportDate.equals(other.reportDate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.udg.helpdesk.entities.ReportPK[ reportID=" + reportID + ", reportDate=" + reportDate + " ]";
    }

}
