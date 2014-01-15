/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2007
 */
package br.ufrn.arq.seguranca.log;

import java.util.Date;
import java.util.StringTokenizer;

import br.ufrn.arq.util.UFRNUtils;

/**
 * Classe de domínio do log de operações do usuário
 * 
 * @author Gleydson Lima
 * 
 */
public class LogOperacao implements LogUFRN {

	private int idRequest;

	// Data e Hora da operação
	private Date hora;

	// URL acionada
	private String action;

	// Parametros enviados
	private String parameters;

	// Registro de entrada do usuário
	private Integer idRegistroEntrada;

	// Registro de entrada do usuário na parte pública
	private Integer idRegistroAcessoPublico;

	// Tempo que durou a operação
	private long tempo;

	// Se uma exceção foi disparada
	private Boolean erro;

	// Caso a exceção foi disparada, o tracing da mesma
	private String tracing;

	// Id do log operação
	private int id;

	// sistema que originou a operação
	private int sistema;
	
	// mensagens exibidas para o usuário durante a requisição
	private String mensagens;

	public int getSistema() {
		return sistema;
	}

	public void setSistema(int sistema) {
		this.sistema = sistema;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTracing() {
		return tracing;
	}

	public void setTracing(String tracing) {
		this.tracing = tracing;
	}

	public Boolean getErro() {
		return erro;
	}

	public void setErro(Boolean erro) {
		this.erro = erro;
	}

	public long getTempo() {
		return tempo;
	}

	public double getTempoSegundos() {
		return UFRNUtils.truncateDouble((double) tempo / 1000, 2);
	}

	public void setTempo(long tempo) {
		this.tempo = tempo;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public Integer getIdRegistroEntrada() {
		return idRegistroEntrada;
	}

	public void setIdRegistroEntrada(Integer idRegistroEntrada) {
		this.idRegistroEntrada = idRegistroEntrada;
	}
	
	public Integer getIdRegistroAcessoPublico() {
		return idRegistroAcessoPublico;
	}

	public void setIdRegistroAcessoPublico(Integer idRegistroAcessoPublico) {
		this.idRegistroAcessoPublico = idRegistroAcessoPublico;
	}

	public int getIdRequest() {
		return idRequest;
	}

	public void setIdRequest(int idRequest) {
		this.idRequest = idRequest;
	}

	public String getParameters() {

		StringTokenizer st = new StringTokenizer(parameters, "\n");
		StringBuffer newEspec = new StringBuffer(parameters.length() + 10);

		while (st.hasMoreTokens()) {
			String linha = st.nextToken();
			if (linha.length() > 50) {
				linha = linha.substring(0, 50) + "<br>"
						+ linha.substring(50, linha.length());
			}
			newEspec.append(linha + "<br>");
		}

		return newEspec.toString();
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getMensagens() {
		return mensagens;
	}

	public void setMensagens(String mensagens) {
		this.mensagens = mensagens;
	}

}
