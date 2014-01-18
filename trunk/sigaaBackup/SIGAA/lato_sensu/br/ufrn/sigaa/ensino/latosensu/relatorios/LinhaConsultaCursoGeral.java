package br.ufrn.sigaa.ensino.latosensu.relatorios;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;

/**
 * Classe auxiliar usada para exibir as informações de uma proposta de curso Lato.
 * */
public class LinhaConsultaCursoGeral {

	/**Identificador do o curso da proposta.*/
	private Integer idCurso;
	
	/**Nome do o curso da proposta.*/
	private String nomeCurso;
	
	/**Situação da proposta de Curso Lato.*/
	private SituacaoProposta situacao;
	
	/**Data de inicio o curso da proposta.*/
	private Date dataInicio;
	
	/**Servidor que coordena o curso da proposta.*/
	private String coordenador;
	
	/**Nome da área de conhecimento CNPq.*/
	private String areaConhecimento;

	/** Prefixo utilizao para gerar o código */
	private String prefixoCodigo;
	
	/** Número que se refere ao código do curso Lato */
	private Integer numeroCodigo;
	
	/** Referente ao código do ano do código do curso Lato*/
	private Short ano;
		

	/**Verifica se uma proposta de curso foi aprovada.*/
	public boolean isPropostaAprovada(){
		return situacao.getId() == SituacaoProposta.ACEITA;
	}
	
	/** Representação o código sequencial para os curso de lato */
	public String getCodigoLatoCompleto() {
		NumberFormat format = new DecimalFormat("000");
		return prefixoCodigo != null ?  this.prefixoCodigo + format.format(numeroCodigo) + "-" + getAno() : null;
	}
	
	public Integer getIdCurso() {
		return idCurso;
	}
	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

	public String getNomeCurso() {
		return nomeCurso;
	}

	public void setNomeCurso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public String getAreaConhecimento() {
		return areaConhecimento;
	}

	public void setAreaConhecimento(String areaConhecimento) {
		this.areaConhecimento = areaConhecimento;
	}

	public String getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(String coordenador) {
		this.coordenador = coordenador;
	}

	public String getPrefixoCodigo() {
		return prefixoCodigo;
	}
	public void setPrefixoCodigo(String prefixoCodigo) {
		this.prefixoCodigo = prefixoCodigo;
	}
	public Integer getNumeroCodigo() {
		return numeroCodigo;
	}
	public void setNumeroCodigo(Integer numeroCodigo) {
		this.numeroCodigo = numeroCodigo;
	}
	public Short getAno() {
		return ano;
	}
	public void setAno(Short ano) {
		this.ano = ano;
	}
	public SituacaoProposta getSituacao() {
		return situacao;
	}
	
	public void setSituacao(SituacaoProposta situacao) {
		this.situacao = situacao;
	}
	
}