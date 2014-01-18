package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Data Transfer Object utilizado no cadastro de solicitação de boletim
 * informativo no SIPAC quando da homologação de solicitações de afastamento no
 * SIGPRH
 * 
 * @author Cezar Miranda
 * 
 */
public class SolicitacaoInformativoDTO implements Serializable {

	/**
	 * Necessário para a serialização/desserialização do objeto.
	 * Caso algum dos atributos também seja um objeto, a classe precisa
	 * implementar Serializable e declarar o serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	/** Assunto do informativo */
	private String assunto;
	/** Informativo */
	private String informativo;
	/** Tipo do informativo. Por ex.: afastamento, edital, portaria, diárias */
	/** Usuário que cadastrou o informativo */
	private int idUsuario;
	
	private int idUnidade;
	
	private int anoPortaria;
	
	private int numeroPortaria;
	
	private Integer numeroBoletim;
	
	private Integer anoBoletim;
	
	private Date dataPublicacaoBoletim;
	
	private Map<String, Object> mapaBoletim;
	
	public SolicitacaoInformativoDTO() {
		super();
	}
	
	public SolicitacaoInformativoDTO(Map<String, Object> mapaBoletim) {
		super();
		this.mapaBoletim = mapaBoletim;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getInformativo() {
		return informativo;
	}

	public void setInformativo(String informativo) {
		this.informativo = informativo;
	}
	
	public int getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public int getAnoPortaria() {
		return anoPortaria;
	}

	public void setAnoPortaria(int anoPortaria) {
		this.anoPortaria = anoPortaria;
	}

	public int getNumeroPortaria() {
		return numeroPortaria;
	}

	public void setNumeroPortaria(int numeroPortaria) {
		this.numeroPortaria = numeroPortaria;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNumeroBoletim() {
		return numeroBoletim;
	}

	public void setNumeroBoletim(Integer numeroBoletim) {
		this.numeroBoletim = numeroBoletim;
	}

	public Integer getAnoBoletim() {
		return anoBoletim;
	}

	public void setAnoBoletim(Integer anoBoletim) {
		this.anoBoletim = anoBoletim;
	}

	public Date getDataPublicacaoBoletim() {
		return dataPublicacaoBoletim;
	}

	public void setDataPublicacaoBoletim(Date dataPublicacaoBoletim) {
		this.dataPublicacaoBoletim = dataPublicacaoBoletim;
	}

	public Map<String, Object> getMapaBoletim() {
		return mapaBoletim;
	}

	public void setMapaBoletim(Map<String, Object> mapaBoletim) {
		this.mapaBoletim = mapaBoletim;
	}
}