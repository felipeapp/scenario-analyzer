/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.jsf;

import java.util.HashMap;

/**
 * Encapsula informa��es sobre o bean que deseja realizar a busca
 * 
 * @author Henrique Andr�
 * 
 */
public class DadosRequisitor {

	public static final int RESERVAR_ESPACO = 1;
	public static final int ALOCAR_TURMA_ESPACO = 2;
	public static final int GERENCIAR_ESPACO_FISICO = 3;
	
	private static HashMap<Integer, DadosRequisitor> mapa;
	
	static {
		mapa = new HashMap<Integer, DadosRequisitor>();
		mapa.put(RESERVAR_ESPACO, new DadosRequisitor("reservaEspacoFisico", "Espa�o fisico para reserva"));
		mapa.put(ALOCAR_TURMA_ESPACO, new DadosRequisitor("alocarTurmaBean", "Alocar turma em espa�o f�sico"));
		mapa.put(GERENCIAR_ESPACO_FISICO, new DadosRequisitor("espacoFisicoBean", "Buscar Espa�o F�sico", "/infra_fisica/busca_geral/operacao.jsp"));
	}
	
	public DadosRequisitor(String mBean, String descricao) {
		this.mBean = mBean;
		this.descricao = descricao;
		this.url = "/infra_fisica/busca_geral/default.jsp";
	}
	
	public DadosRequisitor(String mBean, String descricao, String url) {
		this.mBean = mBean;
		this.descricao = descricao;
		this.url = url;
	}	
	
	/**
	 * MBean que requisitou
	 */
	private String mBean;
	
	/**
	 * Descri��o da opera��o
	 */
	private String descricao;
	
	/**
	 * URL contendo as opera��es que ser�o inclu�das dinamicamente na p�gina de resultado da busca
	 */
	private String url;

	public String getMBean() {
		return mBean;
	}

	public void setMBean(String bean) {
		mBean = bean;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * Exibe os dados de quem requisitou a opera��o
	 * 
	 * @param operacao
	 * @return
	 */
	public static DadosRequisitor info(Integer operacao){
		return mapa.get(operacao);
	}

	/**
	 * Lista de papeis que poder�o realizar a busca
	 * 
	 * @return
	 */
	public int[] getRoles() {
		return null;
	}

	
	
}
