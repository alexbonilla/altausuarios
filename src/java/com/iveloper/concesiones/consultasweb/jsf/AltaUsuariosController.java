package com.iveloper.concesiones.consultasweb.jsf;

import com.iveloper.concesiones.consultasweb.entities.AltaUsuarios;
import com.iveloper.concesiones.consultasweb.jsf.util.JsfUtil;
import com.iveloper.concesiones.consultasweb.jsf.util.JsfUtil.PersistAction;
import com.iveloper.concesiones.consultasweb.beans.AltaUsuariosFacade;
import com.iveloper.concesiones.controllers.AltaUsuariosJpaController;
import com.iveloper.ihsuite.services.security.LoginBean;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

@ManagedBean(name = "altaUsuariosController")
@SessionScoped
public class AltaUsuariosController implements Serializable {

    @EJB
    private com.iveloper.concesiones.consultasweb.beans.AltaUsuariosFacade ejbFacade;
    private List<AltaUsuarios> items = null;
    private AltaUsuarios selected;
    private Date now;

    public AltaUsuariosController() {
    }

    public AltaUsuarios getSelected() {
        return selected;
    }

    public void setSelected(AltaUsuarios selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private AltaUsuariosFacade getFacade() {
        return ejbFacade;
    }

    public AltaUsuarios prepareCreate() {
        selected = new AltaUsuarios();
        selected.setAlta(Boolean.FALSE);
        selected.setFechacreacion(new Date());
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {

        AltaUsuarios usuario = getFacade().find(selected.getUsuario());
        if (usuario != null) {
            Logger.getLogger(AltaUsuariosController.class.getName()).log(Level.INFO, "Usuario ya existe");
        } else {
            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AltaUsuariosCreated"));
            //avisar por correo a administrador de nueva solicitud de alta
            usuario = getFacade().find(selected.getUsuario());
            if (usuario != null) {
                try {
                    MandrillApi mandrillApi = new MandrillApi("Wb30-AONNEEZioNKxX9BXQ");
                    //Se notifica a Admin
                    List<AltaUsuarios> administrators = null;
                    try {
                        EntityManagerFactory emf = Persistence.createEntityManagerFactory("altausuariosPU");
                        Context c = new InitialContext();
                        UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");

                        AltaUsuariosJpaController accountController = new AltaUsuariosJpaController(utx, emf);
                        administrators = accountController.findAdmins();

                    } catch (NamingException ex) {
                        Logger.getLogger(AltaUsuariosController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(AltaUsuariosController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (administrators != null) {
                        // create your message
                        MandrillMessage message = new MandrillMessage();
                        message.setSubject("Consultas Web Concesiones");
                        message.setHtml("<p>Se ha solicitado el alta para el usuario " + usuario.getUsuario() + ". Por favor proceder con la validaci&oacute;n de datos.</p>");
                        message.setAutoText(true);
                        message.setFromEmail("alex@iveloper.com");
                        message.setFromName("Consultas Web Concesiones");
                        // add recipients                                        
                        ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
                        MandrillMessage.Recipient recipient = null;
                        Iterator<AltaUsuarios> administratorsItr = administrators.iterator();
                        while (administratorsItr.hasNext()) {
                            recipient =  new MandrillMessage.Recipient();
                            AltaUsuarios thisAdmin = administratorsItr.next();
                            recipient.setEmail(thisAdmin.getEmail());
                            recipient.setName(thisAdmin.getNombre());
                            recipients.add(recipient);
                        }

                        message.setTo(recipients);
                        message.setPreserveRecipients(true);
                        ArrayList<String> tags = new ArrayList<String>();
                        tags.add("notificacion");
                        tags.add("admin");
                        message.setTags(tags);

                        // send
                        MandrillMessageStatus[] messageStatusReports = mandrillApi.messages().send(message, false);
                    }

                    //Se notifica a Usuario
                    MandrillMessage messageUser = new MandrillMessage();
                    messageUser.setSubject("Consultas Web Concesiones");
                    messageUser.setHtml("<p>Estimado " + usuario.getNombre() + ", su solicitud de alta ha sido recibida. Usted recibir&aacute; una respuesta de aprobaci&oacute;n en un lapso de 24 horas, luego de lo cual podr&aacute; acceder al sistema de Consultas Web.</p><p>Muchas gracias por utilizar nuestro servicios.</p>");
                    messageUser.setAutoText(true);
                    messageUser.setFromEmail("alex@iveloper.com");
                    messageUser.setFromName("ConsultasWeb Suite");
                    // add recipients
                    ArrayList<MandrillMessage.Recipient> recipientsUser = new ArrayList<MandrillMessage.Recipient>();
                    MandrillMessage.Recipient recipientUser = new MandrillMessage.Recipient();
                    recipientUser.setEmail(usuario.getEmail());
                    recipientUser.setName(usuario.getNombre());
                    recipientsUser.add(recipientUser);

                    messageUser.setTo(recipientsUser);
                    messageUser.setPreserveRecipients(true);
                    ArrayList<String> tagsUser = new ArrayList<String>();
                    tagsUser.add("notificacion");
                    tagsUser.add("user");
                    messageUser.setTags(tagsUser);

                    // send
                    MandrillMessageStatus[] messageStatusReportsUser = mandrillApi.messages().send(messageUser, false);
                } catch (MandrillApiError ex) {
                    Logger.getLogger(AltaUsuariosController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AltaUsuariosController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("AltaUsuariosUpdated"));
        //Se notifica a Usuario si fue dado de alta
        if (selected.getAlta()) {
            try {
                MandrillApi mandrillApi = new MandrillApi("Wb30-AONNEEZioNKxX9BXQ");

                // create your message
                MandrillMessage message = new MandrillMessage();

                //Se notifica a Usuario
                MandrillMessage messageUser = new MandrillMessage();
                messageUser.setSubject("Consultas Web Concesiones");
                messageUser.setHtml("<p>Estimado " + selected.getNombre() + ", su solicitud de alta ha sido aprobada. Usted puede acceder a nuestro portal en <a href='http://concesiones.rastreototal.com:20004/consultasweb/' target='_blank'>http://concesiones.rastreototal.com:20004/consultasweb/</a> usando las siguientes credenciales:</p><p>usuario: " + selected.getUsuario() + "</p><p>clave: " + selected.getClave() + "</p>");
                messageUser.setAutoText(true);
                messageUser.setFromEmail("alex@iveloper.com");
                messageUser.setFromName("ConsultasWeb Suite");
                // add recipients
                ArrayList<MandrillMessage.Recipient> recipientsUser = new ArrayList<MandrillMessage.Recipient>();
                MandrillMessage.Recipient recipientUser = new MandrillMessage.Recipient();
                recipientUser.setEmail(selected.getEmail());
                recipientUser.setName(selected.getNombre());
                recipientsUser.add(recipientUser);

                messageUser.setTo(recipientsUser);
                messageUser.setPreserveRecipients(true);
                ArrayList<String> tagsUser = new ArrayList<String>();
                tagsUser.add("alta");
                tagsUser.add("user");
                messageUser.setTags(tagsUser);

                // send
                MandrillMessageStatus[] messageStatusReportsUser = mandrillApi.messages().send(messageUser, false);
            } catch (MandrillApiError ex) {
                Logger.getLogger(AltaUsuariosController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AltaUsuariosController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("AltaUsuariosDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<AltaUsuarios> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {

        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    if (persistAction == PersistAction.UPDATE) {
                        selected.setFechamodificacion(new Date());
                    }
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
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

    public List<AltaUsuarios> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<AltaUsuarios> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = AltaUsuarios.class)
    public static class AltaUsuariosControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AltaUsuariosController controller = (AltaUsuariosController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "altaUsuariosController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof AltaUsuarios) {
                AltaUsuarios o = (AltaUsuarios) object;
                return getStringKey(o.getUsuario());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), AltaUsuarios.class.getName()});
                return null;
            }
        }

    }

}
