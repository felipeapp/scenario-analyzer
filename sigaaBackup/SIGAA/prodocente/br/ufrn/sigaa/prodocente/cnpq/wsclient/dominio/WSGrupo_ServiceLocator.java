/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 22/02/2013 
 */
/**
 * WSGrupo_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.ufrn.sigaa.prodocente.cnpq.wsclient.dominio;

public class WSGrupo_ServiceLocator extends org.apache.axis.client.Service implements WSGrupo_Service {

    public WSGrupo_ServiceLocator() {
    }


    public WSGrupo_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WSGrupo_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WSGrupoPort
    private java.lang.String WSGrupoPort_address = "http://servicosweb.cnpq.br/wsgrupos/WSGrupoSoapHttpPort";

    public java.lang.String getWSGrupoPortAddress() {
        return WSGrupoPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WSGrupoPortWSDDServiceName = "WSGrupoPort";

    public java.lang.String getWSGrupoPortWSDDServiceName() {
        return WSGrupoPortWSDDServiceName;
    }

    public void setWSGrupoPortWSDDServiceName(java.lang.String name) {
        WSGrupoPortWSDDServiceName = name;
    }

    public WSGrupo_PortType getWSGrupoPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WSGrupoPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWSGrupoPort(endpoint);
    }

    public WSGrupo_PortType getWSGrupoPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            WSGrupoBindingStub _stub = new WSGrupoBindingStub(portAddress, this);
            _stub.setPortName(getWSGrupoPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWSGrupoPortEndpointAddress(java.lang.String address) {
        WSGrupoPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (WSGrupo_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                WSGrupoBindingStub _stub = new WSGrupoBindingStub(new java.net.URL(WSGrupoPort_address), this);
                _stub.setPortName(getWSGrupoPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("WSGrupoPort".equals(inputPortName)) {
            return getWSGrupoPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.servico.repositorio.cnpq.br/", "WSGrupo");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.servico.repositorio.cnpq.br/", "WSGrupoPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WSGrupoPort".equals(portName)) {
            setWSGrupoPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
