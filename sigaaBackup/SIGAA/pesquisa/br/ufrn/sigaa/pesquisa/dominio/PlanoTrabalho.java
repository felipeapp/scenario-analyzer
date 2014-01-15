/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;

/**
 * Classe que representa os planos de trabalho de alunos de iniciação científica, os planos de trabalho são 
 * como pequenos projetos dentro de um projeto maior (projeto de pesquisa). Eles possuem título, objetivos 
 * e metodologia próprios, além de um cronograma diferenciado. 
 * 
 * @author ricardo
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "plano_trabalho", schema = "pesquisa", uniqueConstraints = {})
public class PlanoTrabalho extends AbstractMovimento implements PersistDB {

	
	/** Chave primária */
	private int id;

	/** Tipo de bolsa atribuída ao plano de trabalho */
	private TipoBolsaPesquisa tipoBolsa;

	/** Projeto de pesquisa do Plano de trabalho */
	private ProjetoPesquisa projetoPesquisa;

	/** Discente membro do projeto */
	private MembroProjetoDiscente membroProjetoDiscente;
	
	/** Orientador do Plano de Trabalho */
	private Servidor orientador;

	/** Título do plano de trabalho */
	private String titulo;
	
	/** Introdução e justificativa do plano de trabalho */
	private String introducaoJustificativa;

	/** Objetivos do plano de trabalho */
	private String objetivos;

	/** Metodologia do plano de trabalho */
	private String metodologia;

	/** Cronograma do plano de trabalho */
	private List<CronogramaProjeto> cronogramas = new ArrayList<CronogramaProjeto>(0);

	/** Cota do plano de trabalho */
	private CotaBolsas cota;

	/** Consultor do plano de trabalho */
	private Consultor consultor;

	/** Parecer do Consultor do plano de trabalho */
	private String parecerConsultor;

	/** Data da avaliação do plano de trabalho */
	private Date dataAvaliacao;

	/** Status referente ao plano de trabalho */
	private int status;

	/** Os discentes membro do plano de trabalho */
	private Set<MembroProjetoDiscente> membrosDiscentes = new HashSet<MembroProjetoDiscente>(0);

	/** Relatório final de bolsa do plano de trabalho */
	private Set<RelatorioBolsaFinal> relatoriosBolsaFinal;

	/** Relatório parcial de bolsa do plano de trabalho */
	private Set<RelatorioBolsaParcial> relatoriosBolsaParcial;

	/** Data de cadastro do plano de trabalho */
	private Date dataCadastro;

	/** Registro de entrada do plano de trabalho */
	private RegistroEntrada registroEntrada;

	/** Histórico do plano de trabalho */
	private Collection<HistoricoPlanoTrabalho> historico =  new ArrayList<HistoricoPlanoTrabalho>();
	
	/** Data de início do plano de trabalho */
	private Date dataInicio;

	/** Data final do plano de trabalho */
	private Date dataFim;
	
	/** Armazena o edital de pesquisa que o plano de trabalho faz parte */
	private EditalPesquisa edital;
	
	/** Armazena se o plano de trabalho tem continuação */
	private Boolean continuacao;
	
	/** Armazena um booleano que indica se o plano de trabalho foi selecionado */
	private boolean selecionado; 

	/** Armazena as referências do plano de trabalho */
	private String referencias;
	
	/** Orientador externo do Plano de Trabalho */
	private DocenteExterno externo;
	
	/** Informa ql a bolsa que o docente deseja concorrer no edital em questão. */
	private Integer bolsaDesejada = 0; 
	
	// Constructors

	/** default constructor */
	public PlanoTrabalho() {
		setCodMovimento(SigaaListaComando.CADASTRAR_PLANO_TRABALHO);
	}

	/** minimal constructor */
	public PlanoTrabalho(int idPlanoTrabalho) {
		this.id = idPlanoTrabalho;
	}

	/** full constructor */
	public PlanoTrabalho(int idPlanoTrabalho,
			TipoBolsaPesquisa tipoBolsa,
			ProjetoPesquisa projetoPesquisa,
			MembroProjetoDiscente membroProjetoDiscente,
			Servidor orientador,
			String objetivos,
			String metodologia, DocenteExterno externo) {
		this.id = idPlanoTrabalho;
		this.tipoBolsa = tipoBolsa;
		this.projetoPesquisa = projetoPesquisa;
		this.membroProjetoDiscente = membroProjetoDiscente;
		this.orientador = orientador;
		this.objetivos = objetivos;
		this.metodologia = metodologia;
		this.externo = externo;
	}

	/** Responsável por retorna o status */
	@Transient
	public String getStatusString() {
		if(status == TipoStatusPlanoTrabalho.CADASTRADO)
			if(tipoBolsa != null && (tipoBolsa.getId() == TipoBolsaPesquisa.VOLUNTARIO || tipoBolsa.getId() == TipoBolsaPesquisa.VOLUNTARIO_IT))
				return "CADASTRADO";
		return TipoStatusPlanoTrabalho.getDescricao(getStatus());
	}

	/** Responsável por retornar o tipo de bolsa no formato de String */
	@Transient
	public String getTipoBolsaString() {
		return tipoBolsa.getDescricaoResumida();
	}

	/** Responsável por retornar o tipo de bolsa no formato de String */
	@Transient
	public String getTipoBolsaSolicitada() {
		if ( !isEmpty(bolsaDesejada)  )
			return TipoBolsaPesquisa.getDescricao(bolsaDesejada);
		else
			return "Não selecionada.";
	}

	/** Geração da chave primária e retorno da chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_plano_trabalho", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	/** Responsável por setar a chave primária */
	public void setId(int idPlanoTrabalho) {
		this.id = idPlanoTrabalho;
	}

	/** Responsável por retornar o tipo bolsa */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "tipo_bolsa", unique = false, nullable = false, insertable = true, updatable = true)
	public TipoBolsaPesquisa getTipoBolsa() {
		return tipoBolsa;
	}

	/** Responsável por setar o tipo bolsa */
	public void setTipoBolsa(TipoBolsaPesquisa tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	/** Responsável por retornar o projeto Pesquisa */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	public ProjetoPesquisa getProjetoPesquisa() {
		return this.projetoPesquisa;
	}

	/** Responsável por setar o projeto Pesquisa */
	public void setProjetoPesquisa(ProjetoPesquisa projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}
	
	/** Responsável por retornar o membro Projeto Discente */
	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_membro_projeto_discente", unique = false, nullable = true, insertable = true, updatable = true)
	public MembroProjetoDiscente getMembroProjetoDiscente() {
		return this.membroProjetoDiscente;
	}

	/** Responsável por setar o membro Projeto Discente */
	public void setMembroProjetoDiscente(
			MembroProjetoDiscente membroProjetoDiscente) {
		this.membroProjetoDiscente = membroProjetoDiscente;
	}

	/** Responsável por retornar o orientador */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_orientador", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getOrientador() {
		return this.orientador;
	}

	/** Responsável por setar o orientador */
	public void setOrientador(Servidor servidor) {
		this.orientador = servidor;
	}

	/** Responsável por retornar os objetivos */
	@Column(name = "objetivos", unique = false, nullable = true, insertable = true, updatable = true)
	public String getObjetivos() {
		return this.objetivos;
	}

	/** Responsável por setar os objetivos */
	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}

	/** Responsável por retornar a metodologia */
	@Column(name = "metodologia", unique = false, nullable = true, insertable = true, updatable = true)
	public String getMetodologia() {
		return this.metodologia;
	}

	/** Responsável por setar a metodologia */
	public void setMetodologia(String metodologia) {
		this.metodologia = metodologia;
	}

	/** Responsável por retornar o cronograma */
	@IndexColumn(name = "ordem", base=1)
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "planoTrabalho")
	public List<CronogramaProjeto> getCronogramas() {
		return cronogramas;
	}

	/** Responsável por setar o cronograma */
	public void setCronogramas(List<CronogramaProjeto> cronogramas) {
		this.cronogramas = cronogramas;
	}

	/**
	 * Retornar o consultor do plano de trabalho
	 * 
	 * @return the consultor
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_consultor", unique = false, nullable = true, insertable = true, updatable = true)
	public Consultor getConsultor() {
		return consultor;
	}

	/**
	 * Retorna a data de avaliação do plano de trabalho
	 * 
	 * @return the dataAvaliacao
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_avaliacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataAvaliacao() {
		return dataAvaliacao;
	}

	/**
	 * Retorna o parecer do consultor do plano de trabalho
	 * 
	 * @return the parecerConsultor
	 */
	@Column(name = "parecer_consultor", unique = false, nullable =  true, insertable =  true, updatable =  true)
	public String getParecerConsultor() {
		return parecerConsultor;
	}

	/**
	 * Retorna o status do plano de trabalho
	 * 
	 * @return the status
	 */
	@Column(name = "status", unique = false, nullable =  false, insertable =  true, updatable =  true)
	public int getStatus() {
		return status;
	}


	/**
	 * Retornar os discentes Membros do plano de trabalho. Somente retorna os ativos.
	 * 
	 * @return the membrosDiscentes
	 */
	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "planoTrabalho")
	public Set<MembroProjetoDiscente> getMembrosDiscentes() {
		Set<MembroProjetoDiscente> membrosAtivos = new HashSet<MembroProjetoDiscente>();
		for(MembroProjetoDiscente m: membrosDiscentes)
			if(!m.isInativo())
				membrosAtivos.add(m);
		return membrosAtivos;
	}

	/** Responsável por retornar o relatório Bolsa Final */
	@OneToMany(mappedBy = "planoTrabalho", fetch = FetchType.LAZY)
	public Set<RelatorioBolsaFinal> getRelatoriosBolsaFinal() {
		return relatoriosBolsaFinal;
	}

	/** Responsável por setar o relatório Bolsa Final */
	public void setRelatoriosBolsaFinal(Set<RelatorioBolsaFinal> rel) {
		relatoriosBolsaFinal = rel;
	}

	/** Responsável por retornar o relatório Bolsa Parcial */
	@OneToMany(mappedBy = "planoTrabalho", fetch = FetchType.LAZY)
	public Set<RelatorioBolsaParcial> getRelatoriosBolsaParcial() {
		return relatoriosBolsaParcial;
	}

	/** Responsável por setar o relatório Bolsa Parcial */
	public void setRelatoriosBolsaParcial(Set<RelatorioBolsaParcial> rel) {
		relatoriosBolsaParcial = rel;
	}

	/** Responsável por retornar o histórico */
	@OneToMany(mappedBy = "planoTrabalho", fetch = FetchType.LAZY)
	public Collection<HistoricoPlanoTrabalho> getHistorico() {
		return historico;
	}

	/** Responsável por setar o histórico */
	public void setHistorico(Collection<HistoricoPlanoTrabalho> historico) {
		this.historico = historico;
	}

	/** Responsável por retornar o relatório Bolsa Final */
	@Transient
	public RelatorioBolsaFinal getRelatorioBolsaFinal() {

		getRelatoriosBolsaFinal();
		if ( relatoriosBolsaFinal != null && relatoriosBolsaFinal.size() > 0 ) {
			RelatorioBolsaFinal rel = relatoriosBolsaFinal.iterator().next();
			return rel.isAtivo() ? rel : null;
		} else {
			return null;
		}

	}

	/** Responsável por retornar o relatório Bolsa Parcial */
	@Transient
	public RelatorioBolsaParcial getRelatorioBolsaParcial() {
		getRelatoriosBolsaParcial();
		if ( relatoriosBolsaParcial != null && relatoriosBolsaParcial.size() > 0 ) {
			RelatorioBolsaParcial rel = relatoriosBolsaParcial.iterator().next();
			return rel.isAtivo() ? rel : null;
		} else {
			return null;
		}
	}

	/** Responsável por retornar a cota */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cota_bolsas", unique = false, nullable = true, insertable = true, updatable = true)
	public CotaBolsas getCota() {
		return cota;
	}

	/** Responsável por setar a cota */
	public void setCota(CotaBolsas cota) {
		this.cota = cota;
	}

	/** Responsável por retornar a Data de Cadastro */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Responsável por retornar o registro de entrada */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Responsável por setar a data de cadastro */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Responsável por setar o registro de entrada */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/** Responsável por setar o status do plano de trabalho */
	public void setStatus(int status) {
		this.status = status;
	}

	/** Responsável por setar o consultor do plano de trabalho */
	public void setConsultor(Consultor consultor) {
		this.consultor = consultor;
	}

	/** Responsável por setar a data da avaliação do plano de trabalho */
	public void setDataAvaliacao(Date dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}

	/** Responsável por setar o parecer do consultor */
	public void setParecerConsultor(String parecerConsultor) {
		this.parecerConsultor = parecerConsultor;
	}

	/** Responsável por retornar o título */
	@Column(name = "titulo", unique = false, nullable = true, insertable = true, updatable = true)
	public String getTitulo() {
		return titulo;
	}
	
	/** Responsável por setar o título */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/** Responsável por setar os discentes membros do plano de trabalho */
	public void setMembrosDiscentes(Set<MembroProjetoDiscente> membrosDiscentes) {
		this.membrosDiscentes = membrosDiscentes;
	}

	/** Responsável por retornar um boleano informando se o plano de trabalho está cadastrado */
	@Transient
	public boolean isCadastrado() {
		return status == TipoStatusPlanoTrabalho.CADASTRADO;
	}
	
	/** Responsável por retornar um boleano informando se o plano de trabalho foi corrigido */
	@Transient
	public boolean isCorrigido() {
		return status == TipoStatusPlanoTrabalho.CORRIGIDO;
	}
	
	/** Responsável por retornar um boleano informando se o plano de trabalho foi corrigido */
	@Transient
	public boolean isNotExcluido() {
		return status != TipoStatusPlanoTrabalho.EXCLUIDO;
	}
	
	/** Responsável por retornar um boleano informando se o plano de trabalho está aguardando Resubmissão */
	@Transient
	public boolean isAguardandoResubmissao() {
		return status == TipoStatusPlanoTrabalho.AGUARDANDO_RESUBMISSAO 
		    || status == TipoStatusPlanoTrabalho.APROVADO_COM_RESTRICOES
		    || status ==  TipoStatusPlanoTrabalho.NAO_APROVADO;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "membroProjetoDiscente");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, membroProjetoDiscente);
	}

	/** Responsável por retornar um boleano informando se o plano de trabalho está pendente de Indicação */
	@Transient
	public boolean isPendenteIndicacao() {
		return getTipoBolsa().getId() == TipoBolsaPesquisa.A_DEFINIR;
	}
	
	/** Responsável por retornar um boleano informando se o plano de trabalho está Passivel de musar o tipo da bolsa */
	@Transient
	public boolean isPassivelMudarTipoBolsa() {
		return status == TipoStatusPlanoTrabalho.EM_ANDAMENTO && getTipoBolsa().getId() != TipoBolsaPesquisa.A_DEFINIR;
	}

	/** Responsável por retornar um boleano informando se o plano de trabalho está Passivel do Envio do Relatório Parcial */
	public boolean isPassivelEnvioRelatorioParcial(CalendarioPesquisa calendario) {
		return TipoBolsaPesquisa.isExterna(tipoBolsa.getId()) || calendario.isPeriodoEnvioRelatorioParcialBolsa();
	}

	/** Responsável por retornar um boleano informando se o plano de trabalho está Passivel do Envio do Relatório Final */
	public boolean isPassivelEnvioRelatorioFinal(CalendarioPesquisa calendario) {
		return TipoBolsaPesquisa.isExterna(tipoBolsa.getId()) || calendario.isPeriodoEnvioRelatorioFinalBolsa();
	}
	
	/** Responsável por retornar um booleano informando se é possível finalizaro plano de trabalho sem vínculo com alguma cota. */
	@Transient
	public boolean isFinalizarPlanoSemCota(){
		return !tipoBolsa.isVinculadoCota() && new Date().after(getDataFim()) && status == TipoStatusPlanoTrabalho.EM_ANDAMENTO;
	}

	/** Responsável por retornar a Data Inicial */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataInicio() {
		return dataInicio;
	}

	/** Responsável por setar a Data Inicial */
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	/** Responsável por retornar a Data Final */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataFim() {
		return dataFim;
	}
	
	/** Responsável por setar a Data Final */
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	/** Responsável por retornar o Período */
	@Transient
	public String getPeriodo(){
		Formatador f = Formatador.getInstance();
		return f.formatarData(dataInicio) + f.formatarData(dataFim); 
	}

	/** Responsável por retornar o edital */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_edital", unique = false, nullable = true, insertable = true, updatable = true)
	public EditalPesquisa getEdital() {
		return edital;
	}

	/** Responsável por setar o edital */
	public void setEdital(EditalPesquisa edital) {
		this.edital = edital;
	}

	/** Responsável por retornar a Introdução e Justificativa */
	@Column(name = "introducao_justificativa", unique = false, nullable = true, insertable = true, updatable = true)
	public String getIntroducaoJustificativa() {
		return introducaoJustificativa;
	}

	/** Responsável por setar a Introdução e Justificativa */
	public void setIntroducaoJustificativa(String introducaoJustificativa) {
		this.introducaoJustificativa = introducaoJustificativa;
	}
	
	/** Responsável por retornar a continuação */
	@Column(name = "continuacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Boolean getContinuacao() {
		return continuacao;
	}

	/** Responsável por setar a Continuação */
	public void setContinuacao(Boolean continuacao) {
		this.continuacao = continuacao;
	}

	/** Responsável por retornar um boleano informando se foi selecionado ou não */
	@Transient
	public boolean isSelecionado() {
		return selecionado;
	}

	/** Responsável por seta o campo selecionado */
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	/** Responsável por retornar as referências */
	public String getReferencias() {
		return referencias;
	}
	
	/** Responsável por setar as referências */
	public void setReferencias(String referencias) {
		this.referencias = referencias;
	}

	/** Responsável por retornar o orientador Externo */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente_externo", unique = false, nullable = true, insertable = true, updatable = true)
	public DocenteExterno getExterno() {
		return externo;
	}
	
	/** Responsável por setar o orientador Externo */
	public void setExterno(DocenteExterno externo) {
		this.externo = externo;
	}

	@Column(name = "id_bolsa_desejada", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getBolsaDesejada() {
		return bolsaDesejada;
	}

	public void setBolsaDesejada(Integer bolsaDesejada) {
		this.bolsaDesejada = bolsaDesejada;
	}

	/** 
	 * Verifica se o plano de trabalho está em situação que ainda precisa ser avaliado. 
	 */
	@Transient
	public boolean isPendenteAvaliacao(){
		if ( getStatus() == TipoStatusPlanoTrabalho.CADASTRADO  || getStatus() == TipoStatusPlanoTrabalho.CORRIGIDO )
			return true;
		return false;
	}

}