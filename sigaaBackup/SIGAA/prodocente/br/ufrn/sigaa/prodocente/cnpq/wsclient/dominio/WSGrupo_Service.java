/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 22/02/2013 
 */
/**
 * WSGrupo_Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.ufrn.sigaa.prodocente.cnpq.wsclient.dominio;

public interface WSGrupo_Service extends javax.xml.rpc.Service {
    public java.lang.String getWSGrupoPortAddress();

    public WSGrupo_PortType getWSGrupoPort() throws javax.xml.rpc.ServiceException;

    public WSGrupo_PortType getWSGrupoPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
