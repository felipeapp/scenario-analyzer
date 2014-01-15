/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object para informações do projeto
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class ProjetoDTO implements Serializable {
	
	/* Dados gerais: */
	
	private int idStatusProjeto;
	
	private String denominacaoStatus;
	
	/** Representa o número formatado do projeto.*/
	private String numeroFormatado;

	/** Representa o título do projeto.*/
	private String titulo;

	/** Representa o nome do coordenador do projeto.*/
	private String nomeCoordenador;

	/** Representa o siape do coordenador do projeto.*/
	private Integer siapeCoordenador;

	/** Representa o valor do projeto.*/
	private Double valorProjeto;

	/** Representa o valor do projeto.*/
	private Date dataAssinatura;
	
	/* Dados Complementares: */

	/** Representa o identificador do tipo do projeto.*/
	private Integer idTipoProjeto;

	/** Representa o tipo do projeto.*/
	private String tipoProjeto;

	/** Representa o identificador do sub-tipo do projeto.*/
	private Integer idSubTipoProjeto;

	/** Representa o sub-tipo do projeto.*/
	private String subTipoProjeto;

	/** Representa o identificador da área do projeto.*/
	private Integer idArea;

	/** Representa a área do projeto.*/
	private String area;

	/** Representa o identificador da sub-área do projeto.*/
	private Integer idSubArea;

	/** Representa a sub-área do projeto.*/
	private String subArea;
	
	/* Dados contratuais: */

	/** Representa o identificador do tipo de acordo do projeto.*/
	private Integer idClassificacao;

	/** Representa o identificador do tipo do documento.*/
	private Integer idDocumento;
	
	/** Representa o identificador do tipo de captação de recurso do projeto. */
	private Integer idTipoCaptacaoRecurso;

	/** Representa o tipo de captação de recurso do projeto. */
	private String tipoCaptacaoRecurso;

	/** Representa o tipo de acordo do projeto.*/
	private String classificacao;

	/** Representa o nome do proponente do projeto.*/
	private String nomeProponente;

	/** Representa o CPFCNPJ do proponente do projeto.*/
	private Long cpfCNPJProponente;
	
	/*Dados da vigência: */

	/** Representa o início da vigência do projeto.*/
	private Date inicioVigenciaExecucao;

	/** Representa o término da vigência do projeto.*/
	private Date terminoVigenciaExecucao;
	
	/** Objetivos/Justificativas: */

	/** Representa o objetivo do projeto.*/
	private String objetivosGerais;

	/** Representa a justificativa do projeto.*/
	private String justificativa;
	
	/* Cronograma de Execução: */

	/** Representa as metas do projeto.*/
	private List<MetaDTO> metas;
	
	/* Dados Financerios: */

	/** Representa os orgãos financiadores do projeto.*/
	private List<ProjetoOrigemRecursoDTO> orgaosFinanciadores;
	
	/* Dados da equipe de trabalho: */

	/** Representa os participantes do projeto.*/
	private List<ParticipanteDTO> participantes;
	
	/* Dados do orçamento: */

	/** Representa os planos de aplicação do projeto.*/
	private List<PlanoAplicacaoDTO> planosAplicacao;

	/** Representa os cronogramas de desembolso do projeto.*/
	private List<CronogramaDesembolsoDTO> cronogramasDesembolsoDTO;

	/** Representa os partécipes do projeto.*/
	private List<ParticipeDTO> participesDTO;

	/** Representa as unidades interessadas pelo projeto.*/
	private List<UnidadeInteressadaDTO> unidadesInteressadasDTO;
	
	public void setNumeroFormatado(String numeroFormatado) {
		this.numeroFormatado = numeroFormatado;
	}

	public String getNumeroFormatado() {
		return numeroFormatado;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setNomeCoordenador(String nomeCoordenador) {
		this.nomeCoordenador = nomeCoordenador;
	}

	public String getNomeCoordenador() {
		return nomeCoordenador;
	}

	public void setValorProjeto(Double valorProjeto) {
		this.valorProjeto = valorProjeto;
	}

	public Double getValorProjeto() {
		return valorProjeto;
	}

	public void setDataAssinatura(Date dataAssinatura) {
		this.dataAssinatura = dataAssinatura;
	}

	public Date getDataAssinatura() {
		return dataAssinatura;
	}

	public void setTipoProjeto(String tipoProjeto) {
		this.tipoProjeto = tipoProjeto;
	}

	public String getTipoProjeto() {
		return tipoProjeto;
	}

	public void setSubTipoProjeto(String subTipoProjeto) {
		this.subTipoProjeto = subTipoProjeto;
	}

	public String getSubTipoProjeto() {
		return subTipoProjeto;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getArea() {
		return area;
	}

	public void setSubArea(String subArea) {
		this.subArea = subArea;
	}

	public String getSubArea() {
		return subArea;
	}

	public void setInicioVigenciaExecucao(Date inicioVigenciaExecucao) {
		this.inicioVigenciaExecucao = inicioVigenciaExecucao;
	}

	public Date getInicioVigenciaExecucao() {
		return inicioVigenciaExecucao;
	}

	public void setObjetivosGerais(String objetivosGerais) {
		this.objetivosGerais = objetivosGerais;
	}

	public String getObjetivosGerais() {
		return objetivosGerais;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setSiapeCoordenador(Integer siapeCoordenador) {
		this.siapeCoordenador = siapeCoordenador;
	}

	public Integer getSiapeCoordenador() {
		return siapeCoordenador;
	}

	public void setNomeProponente(String nomeProponente) {
		this.nomeProponente = nomeProponente;
	}

	public String getNomeProponente() {
		return nomeProponente;
	}

	public void setCpfCNPJProponente(Long cpfCNPJProponente) {
		this.cpfCNPJProponente = cpfCNPJProponente;
	}

	public Long getCpfCNPJProponente() {
		return cpfCNPJProponente;
	}

	public void setTerminoVigenciaExecucao(Date terminoVigenciaExecucao) {
		this.terminoVigenciaExecucao = terminoVigenciaExecucao;
	}

	public Date getTerminoVigenciaExecucao() {
		return terminoVigenciaExecucao;
	}

	public void setMetas(List<MetaDTO> metas) {
		this.metas = metas;
	}

	public List<MetaDTO> getMetas() {
		return metas;
	}

	public void setOrgaosFinanciadores(List<ProjetoOrigemRecursoDTO> orgaosFinanciadores) {
		this.orgaosFinanciadores = orgaosFinanciadores;
	}

	public List<ProjetoOrigemRecursoDTO> getOrgaosFinanciadores() {
		return orgaosFinanciadores;
	}

	public void setParticipantes(List<ParticipanteDTO> participantes) {
		this.participantes = participantes;
	}

	public List<ParticipanteDTO> getParticipantes() {
		return participantes;
	}

	public void setPlanosAplicacao(List<PlanoAplicacaoDTO> planosAplicacao) {
		this.planosAplicacao = planosAplicacao;
	}

	public List<PlanoAplicacaoDTO> getPlanosAplicacao() {
		return planosAplicacao;
	}

	public void setCronogramasDesembolsoDTO(List<CronogramaDesembolsoDTO> cronogramasDesembolsoDTO) {
		this.cronogramasDesembolsoDTO = cronogramasDesembolsoDTO;
	}

	public List<CronogramaDesembolsoDTO> getCronogramasDesembolsoDTO() {
		return cronogramasDesembolsoDTO;
	}

	public void setIdTipoProjeto(Integer idTipoProjeto) {
		this.idTipoProjeto = idTipoProjeto;
	}

	public Integer getIdTipoProjeto() {
		return idTipoProjeto;
	}

	public void setIdSubTipoProjeto(Integer idSubTipoProjeto) {
		this.idSubTipoProjeto = idSubTipoProjeto;
	}

	public Integer getIdSubTipoProjeto() {
		return idSubTipoProjeto;
	}

	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	public Integer getIdArea() {
		return idArea;
	}

	public void setIdSubArea(Integer idSubArea) {
		this.idSubArea = idSubArea;
	}

	public Integer getIdSubArea() {
		return idSubArea;
	}

	public void setParticipesDTO(List<ParticipeDTO> participesDTO) {
		this.participesDTO = participesDTO;
	}

	public List<ParticipeDTO> getParticipesDTO() {
		return participesDTO;
	}

	public void setUnidadesInteressadasDTO(List<UnidadeInteressadaDTO> unidadesInteressadasDTO) {
		this.unidadesInteressadasDTO = unidadesInteressadasDTO;
	}

	public List<UnidadeInteressadaDTO> getUnidadesInteressadasDTO() {
		return unidadesInteressadasDTO;
	}

	public void setIdTipoCaptacaoRecurso(Integer idTipoCaptacaoRecurso) {
		this.idTipoCaptacaoRecurso = idTipoCaptacaoRecurso;
	}

	public Integer getIdTipoCaptacaoRecurso() {
		return idTipoCaptacaoRecurso;
	}

	public void setTipoCaptacaoRecurso(String tipoCaptacaoRecurso) {
		this.tipoCaptacaoRecurso = tipoCaptacaoRecurso;
	}

	public String getTipoCaptacaoRecurso() {
		return tipoCaptacaoRecurso;
	}

	public void setIdClassificacao(Integer idClassificacao) {
		this.idClassificacao = idClassificacao;
	}

	public Integer getIdClassificacao() {
		return idClassificacao;
	}

	public void setIdDocumento(Integer idDocumento) {
		this.idDocumento = idDocumento;
	}

	public Integer getIdDocumento() {
		return idDocumento;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	public String getClassificacao() {
		return classificacao;
	}

	public void setIdStatusProjeto(int idStatusProjeto) {
		this.idStatusProjeto = idStatusProjeto;
	}

	public int getIdStatusProjeto() {
		return idStatusProjeto;
	}

	public void setDenominacaoStatus(String denominacaoStatus) {
		this.denominacaoStatus = denominacaoStatus;
	}

	public String getDenominacaoStatus() {
		return denominacaoStatus;
	}

}
