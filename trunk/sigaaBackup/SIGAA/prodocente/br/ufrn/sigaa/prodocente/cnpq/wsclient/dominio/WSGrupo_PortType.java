/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 22/02/2013 
 */
/**
 * WSGrupo_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.ufrn.sigaa.prodocente.cnpq.wsclient.dominio;

public interface WSGrupo_PortType extends java.rmi.Remote {
    public GrupoDePesquisa[] getIdsGrupos(java.lang.String uf, java.lang.String sigla, java.lang.String anoCenso, java.lang.String senha) throws java.rmi.RemoteException;
    public GrupoDePesquisa[] getIdsGruposPorDataAtualizacao(java.lang.String dataInicio, java.lang.String dataFim, java.lang.String uf, java.lang.String anoCenso, java.lang.String sigla, java.lang.String senha) throws java.rmi.RemoteException;
    public GrupoDePesquisa getUpdate(java.lang.String idGrupo, java.lang.String anoCenso, java.lang.String senha) throws java.rmi.RemoteException;
    public GrupoDePesquisa getXMLGrupo(java.lang.String idGrupo, java.lang.String anoCenso, java.lang.String senha) throws java.rmi.RemoteException;
}
