/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.concesiones.consultasweb.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alex
 */
@Entity
@Table(name = "alta_usuarios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AltaUsuarios.findAll", query = "SELECT a FROM AltaUsuarios a"),
    @NamedQuery(name = "AltaUsuarios.findByUsuario", query = "SELECT a FROM AltaUsuarios a WHERE a.usuario = :usuario"),
    @NamedQuery(name = "AltaUsuarios.findByClave", query = "SELECT a FROM AltaUsuarios a WHERE a.clave = :clave"),
    @NamedQuery(name = "AltaUsuarios.findById", query = "SELECT a FROM AltaUsuarios a WHERE a.id = :id"),
    @NamedQuery(name = "AltaUsuarios.findByNombre", query = "SELECT a FROM AltaUsuarios a WHERE a.nombre = :nombre"),
    @NamedQuery(name = "AltaUsuarios.findByEmail", query = "SELECT a FROM AltaUsuarios a WHERE a.email = :email"),
    @NamedQuery(name = "AltaUsuarios.findByTelefono", query = "SELECT a FROM AltaUsuarios a WHERE a.telefono = :telefono"),
    @NamedQuery(name = "AltaUsuarios.findByAlta", query = "SELECT a FROM AltaUsuarios a WHERE a.alta = :alta"),
    @NamedQuery(name = "AltaUsuarios.findByFechacreacion", query = "SELECT a FROM AltaUsuarios a WHERE a.fechacreacion = :fechacreacion"),
    @NamedQuery(name = "AltaUsuarios.findByFechamodificacion", query = "SELECT a FROM AltaUsuarios a WHERE a.fechamodificacion = :fechamodificacion"),
    @NamedQuery(name = "AltaUsuarios.findByClinumcl", query = "SELECT a FROM AltaUsuarios a WHERE a.clinumcl = :clinumcl"),
    @NamedQuery(name = "AltaUsuarios.findByUltimoacceso", query = "SELECT a FROM AltaUsuarios a WHERE a.ultimoacceso = :ultimoacceso"),
    @NamedQuery(name = "AltaUsuarios.findByEsAdmin", query = "SELECT a FROM AltaUsuarios a WHERE a.esAdmin = :esAdmin")})
public class AltaUsuarios implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "usuario")
    private String usuario;
    @Size(max = 45)
    @Column(name = "clave")
    private String clave;
    @Size(max = 13)
    @Column(name = "id")
    private String id;
    @Size(max = 200)
    @Column(name = "nombre")
    private String nombre;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 45)
    @Column(name = "email")
    private String email;
    @Size(max = 45)
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "alta")
    private Boolean alta;
    @Column(name = "fechacreacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechacreacion;
    @Column(name = "fechamodificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechamodificacion;
    @Column(name = "clinumcl")
    private Integer clinumcl;
    @Column(name = "ultimoacceso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ultimoacceso;
    @Column(name = "es_admin")
    private Boolean esAdmin;

    public AltaUsuarios() {
    }

    public AltaUsuarios(String usuario) {
        this.usuario = usuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getAlta() {
        return alta;
    }

    public void setAlta(Boolean alta) {
        this.alta = alta;
    }

    public Date getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(Date fechacreacion) {
        this.fechacreacion = fechacreacion;
    }

    public Date getFechamodificacion() {
        return fechamodificacion;
    }

    public void setFechamodificacion(Date fechamodificacion) {
        this.fechamodificacion = fechamodificacion;
    }

    public Integer getClinumcl() {
        return clinumcl;
    }

    public void setClinumcl(Integer clinumcl) {
        this.clinumcl = clinumcl;
    }

    public Date getUltimoacceso() {
        return ultimoacceso;
    }

    public void setUltimoacceso(Date ultimoacceso) {
        this.ultimoacceso = ultimoacceso;
    }

    public Boolean getEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(Boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (usuario != null ? usuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AltaUsuarios)) {
            return false;
        }
        AltaUsuarios other = (AltaUsuarios) object;
        if ((this.usuario == null && other.usuario != null) || (this.usuario != null && !this.usuario.equals(other.usuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iveloper.concesiones.consultasweb.entities.AltaUsuarios[ usuario=" + usuario + " ]";
    }
    
}
