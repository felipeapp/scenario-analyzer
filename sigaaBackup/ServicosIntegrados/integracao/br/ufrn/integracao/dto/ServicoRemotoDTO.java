/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * DTO para armazenar informações de serviços remotos. Utilizado
 * pelo BuscaServicosRemotosRemoteService.
 * 
 * @author David Pereira
 *
 */
public class ServicoRemotoDTO implements Serializable {

	private static final long serialVersionUID = -3515935324010179231L;
	
	private String nomeBean;
	
	private String nomeInterface;
	
	private String nomeImpl;
	
	private String url;

	public String getNomeBean() {
		return nomeBean;
	}

	public void setNomeBean(String nomeBean) {
		this.nomeBean = nomeBean;
	}

	public String getNomeInterface() {
		return nomeInterface;
	}

	public void setNomeInterface(String nomeInterface) {
		this.nomeInterface = nomeInterface;
	}

	public String getNomeImpl() {
		return nomeImpl;
	}

	public void setNomeImpl(String nomeImpl) {
		this.nomeImpl = nomeImpl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
