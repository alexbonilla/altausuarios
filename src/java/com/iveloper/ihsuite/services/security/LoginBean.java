/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.security;


import com.iveloper.concesiones.controllers.AltaUsuariosJpaController;
import com.iveloper.concesiones.controllers.exceptions.RollbackFailureException;
import com.iveloper.concesiones.consultasweb.entities.AltaUsuarios;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;

@ManagedBean(name = "loginBean")
@SessionScoped

/**
 *
 * @author alexbonilla
 */
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String password;
    private String message, uname;
    private AltaUsuarios sessionAccount = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public AltaUsuarios getSessionAccount() {
        return sessionAccount;
    }

    public void setSessionAccount(AltaUsuarios sessionAccount) {
        this.sessionAccount = sessionAccount;
    }

    public boolean verifyAccount() {
        boolean result = false;
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("altausuariosPU");
            Context c = new InitialContext();
            UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");

            AltaUsuariosJpaController accountController = new AltaUsuariosJpaController(utx, emf);
            result = accountController.validateCredentials(uname, password);
            
            if (result) {
                sessionAccount = accountController.findAltaUsuarios(uname);
            }
        } catch (NamingException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public String login() {
        String destinationPage = "error";
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("altausuariosPU");
            Context c = new InitialContext();
            UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");

            AltaUsuariosJpaController accountController = new AltaUsuariosJpaController(utx, emf);
            boolean result = accountController.validateCredentials(uname, password);
            if (result) {

                HttpSession session = LoginBeanUtils.getSession();
                session.setAttribute("username", uname);

                AltaUsuarios thisAccount = accountController.findAltaUsuarios(uname);
                //ask if this user is admin
                if (thisAccount.getEsAdmin()) {
                    thisAccount.setUltimoacceso(new Date());
                    accountController.edit(thisAccount);

                    session.setAttribute("usuario", thisAccount.getUsuario());
                    session.setAttribute("customerid", thisAccount.getId());
                    session.setAttribute("clinumcl", thisAccount.getClinumcl());

                    this.sessionAccount = thisAccount;
                    destinationPage = "altaUsuarios/List";
                } else {
                    //not the correct role
                    FacesContext.getCurrentInstance().addMessage(
                            null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN,
                                    "Invalid Role!",
                                    "Please Try Again!"));
                    destinationPage = "login";
                }

            } else {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Invalid Login!",
                                "Please Try Again!"));

                // invalidate session, and redirect to other pages
                //message = "Invalid Login. Please Try Again!";
                destinationPage = "login";
            }
        } catch (NamingException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);

        } catch (RollbackFailureException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return destinationPage;
    }

    public String logout() {
        HttpSession session = LoginBeanUtils.getSession();
        session.invalidate();
        try {
//            EntityManagerFactory emf = Persistence.createEntityManagerFactory("consultaswebPU");
//            Context c = new InitialContext();
//            UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");

//            AltaUsuariosJpaController accountController = new AltaUsuariosJpaController(utx, emf);
//            AltaUsuarios thisAccount = accountController.findAltaUsuarios(uname);
//            thisAccount.setOnline(Short.valueOf("0"));
//            accountController.edit(thisAccount);
            this.sessionAccount = null;

//        } catch (NamingException ex) {
//            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "/login";
    }
}
