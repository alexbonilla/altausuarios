/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.concesiones.consultasweb.beans;

import com.iveloper.concesiones.consultasweb.entities.AltaUsuarios;
import com.iveloper.concesiones.consultasweb.jsf.util.JsfUtil;
import com.iveloper.concesiones.consultasweb.jsf.util.JsfUtil.PersistAction;
import com.iveloper.concesiones.utils.SendFileEmail;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author alexbonilla
 */
@ManagedBean(name = "newUserBean")
@SessionScoped
public class NewUserBean implements Serializable {

    @EJB
    private com.iveloper.concesiones.consultasweb.beans.AltaUsuariosFacade ejbFacade;
    private String usuario;
    private String clave;
    private String id;
    private String nombre;
    private String email;
    private String telefono;
    private AltaUsuarios newUser;

    public AltaUsuarios getNewUser() {
        return newUser;
    }

    public void setNewUser(AltaUsuarios newUser) {
        this.newUser = newUser;
    }

    private AltaUsuariosFacade getFacade() {
        return ejbFacade;
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

    public AltaUsuarios prepareCreate() {
        newUser = new AltaUsuarios();
        newUser.setUsuario(usuario);
        newUser.setClave(clave);
        newUser.setId(id);
        newUser.setNombre(nombre);
        newUser.setEmail(email);
        newUser.setTelefono(telefono);
        newUser.setAlta(Boolean.FALSE);
        newUser.setFechacreacion(new Date());
        return newUser;
    }

    public String create() {
        String result = "failure";
        AltaUsuarios nuevousuario = getFacade().find(prepareCreate().getUsuario());
        if (nuevousuario != null) {
            Logger.getLogger(NewUserBean.class.getName()).log(Level.INFO, "Usuario {0} ya existe", usuario);
            JsfUtil.addSuccessMessage("Usuario " + usuario + " ya existe");
        } else {
            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AltaUsuariosCreated"));
            //avisar por correo a administrador de nueva solicitud de alta
            nuevousuario = getFacade().find(newUser.getUsuario());
            if (nuevousuario != null) {
            //Se notifica a Admin
            SendFileEmail sfe = new SendFileEmail();
            sfe.setUser("invoicehub@iveloper.com");
            sfe.setPwd("bgNILL1982");
            sfe.setHost("gator4095.hostgator.com");
            sfe.setPort("465");
            sfe.setSsl("true");
            sfe.setAuth("false");
            sfe.setFrom("invoicehub@iveloper.com");
            sfe.setTo("info@concegua.com");
            sfe.setMessageSubject("ConsultasWeb Suite");
            sfe.setMessageBody("<p>Se ha solicitado el alta para el usuario " + nuevousuario.getUsuario() + ". Por favor proceder con la validaci&oacute;n de datos.</p>");
            Thread sendMailThread = new Thread(sfe);
            sendMailThread.start();

                //Se notifica a Usuario
            SendFileEmail sfeUser = new SendFileEmail();
            sfeUser.setUser("invoicehub@iveloper.com");
            sfeUser.setPwd("bgNILL1982");
            sfeUser.setHost("gator4095.hostgator.com");
            sfeUser.setPort("465");
            sfeUser.setSsl("true");
            sfeUser.setAuth("false");
            sfeUser.setFrom("invoicehub@iveloper.com");
            sfeUser.setTo(nuevousuario.getEmail());
            sfeUser.setMessageSubject("ConsultasWeb Suite");
            sfeUser.setMessageBody("<p>Su solicitud de alta ha sido recibida. Usted recibir&aacute; una respuesta de aprobaci&oacute;n en un lapso de 24 horas, luego de lo cual podr&aacute; acceder al sistema de Consultas Web.</p><p>Muchas gracias por utilizar nuestro servicios.</p>");
            Thread sendMailUserThread = new Thread(sfeUser);
            sendMailUserThread.start();
                //ivalidate newUser                                
                result = "success";
            }
        }
        usuario = "";
        clave = "";
        id = "";
        nombre = "";
        email = "";
        telefono = "";        
        return result;
    }

    private void persist(PersistAction persistAction, String successMessage) {

        if (newUser != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(newUser);
                } else {
                    getFacade().remove(newUser);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

}
