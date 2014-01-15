/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/06/2007
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.ArquivoProjeto;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.FotoProjeto;
import br.ufrn.sigaa.projetos.dominio.HistoricoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/*******************************************************************************
 * <p>
 * Projeto de Ensino de Monitoria ou de Programas de Apoio à Melhoria da
 * Qualidade do Ensino de Graduação da UFRN (PAMQEG).<br/>
 * 
 * Os projetos de ensino podem envolver um ou mais componentes de estruturas
 * curriculares de Cursos de Graduação.<br/>
 * 
 * Para os efeitos da resolução Nº 013/2006 - CONSEPE, de 14 de março de 2006,
 * considera-se componente curricular:
 * disciplinas, atividades de formação ou qualquer outra unidade de estruturação
 * didático-pedagógica prevista legalmente. Em cada período letivo regular, pelo
 * menos um dos componentes curriculares integrantes do projeto de ensino deverá
 * ser oferecido.<br/>
 * 
 * O projeto de ensino deverá ter um ou mais professores orientadores, do quadro
 * permanente da UFRN, envolvidos com o (os)componente(s) curricular(es) em
 * referência.<br/>
 * 
 * Um dos professores orientadores assumirá a função de coordenador do projeto
 * de ensino.<br/>
 * 
 * Cada professor somente poderá participar de, no máximo, dois projetos de
 * ensino.<br/>

 * Cada professor orientador poderá ser responsável por, no máximo, dois
 * monitores.<br/>
 * 
 * A seleção dos projetos de ensino será disciplinada por edital da PROGRAD.
 * <br/>
 * 
 * Cada projeto de ensino terá vigência de um ano, renovável uma única vez pelo
 * mesmo período, após aprovação dos relatórios.
 * <br/>
 * 
 * O projeto de ensino, após aprovação pelo Plenário de cada Departamento
 * envolvido ou Órgão Colegiado da Unidade Acadêmica Especializada, deve ser
 * encaminhado, para seleção, à Coordenação do Programa de Monitoria dentro do
 * prazo estabelecido no edital referido anteriormente.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "projeto_monitoria", schema = "monitoria")
public class ProjetoEnsino implements PersistDB, Validatable {

	// Fields
	
	/** Atributo utilizado para representar o ID */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
			@Column(name = "id_projeto_monitoria")
			private int id;

	/** Atributo utilizado para representar o projeto de monitoria */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto")
	private Projeto projeto = new Projeto(new TipoProjeto(TipoProjeto.ENSINO));
	/** Atributo utilizado para representar as quantidade de bolsas solicitadas */
	@Column(name = "bolsas_solicitadas")
	private Integer bolsasSolicitadas = 0;
	/** Atributo utilizado para representar a quantidade de bolsas concedidas */
	@Column(name = "bolsas_concedidas")
	private Integer bolsasConcedidas = 0;
	/** Atributo utilizado para representar a quantidade de bolsas  */
	@Column(name = "bolsas_canceladas")
	private Integer bolsasCanceladas = 0;
	/** Atributo utilizado para representar o edital do projeto */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_edital")
	private EditalMonitoria editalMonitoria = new EditalMonitoria();
	/** Atributo utilizado para representar o componente curricular do projeto */
	@OneToMany(mappedBy = "projetoEnsino")
	@OrderBy(value = "disciplina")
	private Set<ComponenteCurricularMonitoria> componentesCurriculares = new HashSet<ComponenteCurricularMonitoria>();
	/** Atributo utilizado para representar os relatórios do projeto de monitoria */
	@OneToMany(mappedBy = "projetoEnsino")
	@OrderBy(value = "tipoRelatorio")
	private Set<RelatorioProjetoMonitoria> relatoriosProjetoMonitoria = new HashSet<RelatorioProjetoMonitoria>();
	/** Atributo utilizado para representar as avaliações do projeto */
	@OneToMany(mappedBy = "projetoEnsino")
	@OrderBy(value = "avaliador")
	private List<AvaliacaoMonitoria> avaliacoes = new ArrayList<AvaliacaoMonitoria>();
	/** Atributo utilizado para representar o resumo das atividades do projeto */
	@OneToMany(mappedBy = "projetoEnsino")
	private Set<ResumoSid> resumosSid = new HashSet<ResumoSid>();
	/** Atributo utilizado para representar a Equipe de Docentes do projeto */
	@OneToMany(mappedBy = "projetoEnsino")
	@OrderBy(value = "servidor")
	private Collection<EquipeDocente> equipeDocentes = new HashSet<EquipeDocente>();
	/** Atributo utilzado para representar os discentes do projeto de monitoria */
	@OneToMany(mappedBy = "projetoEnsino")
	@OrderBy(value = "provaSelecao, classificacao, discente")
	private Collection<DiscenteMonitoria> discentesMonitoria = new ArrayList<DiscenteMonitoria>();
	/** Atributo utilizado para representar as autorizações dadas pelos chefe do departamento que participa do projeto através dos componentes curriculares */
	@OneToMany(mappedBy = "projetoEnsino")
	private Set<AutorizacaoProjetoMonitoria> autorizacoesProjeto = new HashSet<AutorizacaoProjetoMonitoria>();
	/** Atributo utilizado para representar as autorizações dadas pelos membros da PROGRAD para re-analisação da proposta do projeto */
	@OneToMany(mappedBy = "projetoEnsino")
	private Set<AutorizacaoReconsideracao> autorizacoesReconsideracao = new HashSet<AutorizacaoReconsideracao>();
	/** Atributo utilizado para representar informações sobre a seleção de monitoria */
	@OneToMany(mappedBy = "projetoEnsino")
	@OrderBy(value = "dataProva, dataLimiteIncricao")
	private List<ProvaSelecao> provasSelecao = new ArrayList<ProvaSelecao>();
	/** Atributo utilizado para reprensentar a data de envio do projeto */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio")
	private Date dataEnvio;
	/** Atributo utilizado para representar a média de análise do projeto */
	@Column(name = "media_analise")
	private Double mediaAnalise;
	/** Atributo utilizado para representar a nota da primeira avaliação dada ao projeto */
	@Column(name = "nota_primeira_avaliacao")
	private Double notaPrimeiraAvaliacao;
	/** Atributo utilizado para representar a nota da segunda avaliação dada ao projeto */
	@Column(name = "nota_segunda_avaliacao")
	private Double notaSegundaAvaliacao;

	/**
	 * O projeto só recebe esta nota quando há uma discrepância na avaliação
	 * dos membros da comissão de monitoria. Ela define a média de análise do projeto
	 */
	@Column(name = "nota_avaliacao_final")
	private Double notaAvaliacaoFinal;

	/**
	 * Utilizado na avaliação do 3º avaliador do projeto (pró-reitoria de graduação) para projetos
	 * de monitoria que tiveram notas discrepantes pelos avaliadores da comissão
	 * de monitoria.
	 * 
	 */
	@Column(name = "parecer_avaliacao_final")
	private String parecerAvaliacaoFinal;
	/** Atributo utilizado para representar os tipos de projeto de ensino */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_projeto_ensino")
	private TipoProjetoEnsino tipoProjetoEnsino = new TipoProjetoEnsino();
	/** Atributo utilizado para representar o valor do financiamento do projeto */
	@Column(name = "valor_financiamento")
	private Double valorFinanciamento;
	/** Atributo utilizado para representar a avaliação dada ao projeto */
	private String avaliacao;
	/** Atributo utilizado para representar o produto de um projeto */
	private String produto;
	/** Atributo utilizado para descrever como será o processo seletivo de um monitor */
	@Column(name = "processo_seletivo")
	private String processoSeletivo;
	
	/** Situação na qual o projeto se encontra (Ex.: Concluído, Cancelado, Aguardando Avaliação). */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_situacao_projeto")
	private TipoSituacaoProjeto situacaoProjeto = new TipoSituacaoProjeto();

	/** Informa se o Projeto está ou não em uso. */
	@Column(name = "ativo")
	@CampoAtivo
	private boolean ativo;

	/** Campo utilizado para o cálculo de classificação dos projetos. */
	@Transient
	private int numDepartamentos;

	/** Campo utilizado para o cálculo de classificação dos projetos. */
	@Transient
	private int numProfessores;

	/** Campo utilizado para o cálculo de classificação dos projetos. */
	@Transient
	private int numComponentesCurriculares;

	/** Campo utilizados para o cálculo de classificação dos projetos. */
	@Transient
	private double rt;
	/** Atributo utilizado para representar a situação do relatório do projeto */
	@Transient
	private String situacaoRelatorio;

	/** utilizado apenas na visão para o usuário visualizar a situação do relatório parcial do projeto */
	@Transient
	private boolean enviouRelatorioParcial = false;

	/** utilizado apenas na visão para o usuário visualizar a situação do relatório final do projeto */
	@Transient
	private boolean enviouRelatorioFinal = false;

	/** Utilizado no relatório que exibe a quantidade de monitores por projeto. */
	@Transient
	private long totalMonitores;

	/** Informa o total de bolsas remuneradas utilizadas no momento. Para uso no cadastro de nova prova seletiva*/
	@Transient
	private int totalBolsasRemuneradasAtivas;

	/** Informa o total de bolsas não remuneradas utilizadas no momento. Para uso no cadastro de nova prova seletiva*/
	@Transient
	private int totalBolsasNaoRemuneradasAtivas;

	/** Verifica se é pra validar os novos campos obrigatórios de projetos de monitoria antigos*/
	@Transient
	private Boolean validaNovosCampos = true;

	
	/** default constructor */
	public ProjetoEnsino() {
	}

	/** minimal constructor */
	public ProjetoEnsino(int idProjetoMonitoria) {
		id = idProjetoMonitoria;
	}

	/**
	 * Retorna a quantidade de bolsas não remuneradas do projeto
	 * 
	 * @return
	 */
	public Integer getBolsasNaoRemuneradas() {
		return getBolsasSolicitadas() - getBolsasConcedidas();
	}


	/**
	 * Coleção de Componentes Curriculares (Disciplinas) que fazem parte do
	 * projeto.
	 * 
	 * @return
	 */
	public Set<ComponenteCurricularMonitoria> getComponentesCurriculares() {
		if( componentesCurriculares != null && !componentesCurriculares.isEmpty() ){
			for(Iterator<ComponenteCurricularMonitoria> it = componentesCurriculares.iterator(); it.hasNext();){
				ComponenteCurricularMonitoria comp = it.next();
				if(!comp.isAtivo()){
					it.remove();
				}
			}
		}
		return componentesCurriculares;
	}

	public void setComponentesCurriculares(
			Set<ComponenteCurricularMonitoria> componentesCurriculares) {
		this.componentesCurriculares = componentesCurriculares;
	}

	/**
	 * Coleção de Resumos dos Seminários de Iniciação a docência (SID) do
	 * projeto submetidos a PROGRAD
	 * 
	 * @return
	 */

	public Set<ResumoSid> getResumosSid() {
		return resumosSid;
	}

	public void setResumosSid(Set<ResumoSid> resumosSid) {
		this.resumosSid = resumosSid;
	}

	/**
	 * Coleção de Avaliações do projeto feitas pela comissão de monitoria ou
	 * científica. Com base nestas avaliações um projeto pode ser Recomendado ou
	 * NÃO recomendado pela comissão para entrar em execução.
	 * 
	 * @return
	 */
	public List<AvaliacaoMonitoria> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<AvaliacaoMonitoria> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	/**
	 * Data do envio do projeto para avaliação pela PROGRAD.
	 * 
	 * @return
	 */
	public Date getDataEnvio() {
		return this.dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	/**
	 * Adiciona um Componente Curricular ao projeto.
	 * 
	 * @param componenteCurricular
	 * @return true - se adicionado com sucesso.
	 */
	public boolean addComponenteCurricular(
			ComponenteCurricularMonitoria componenteCurricular) {
		componenteCurricular.setProjetoEnsino(this);
		componenteCurricular.setAtivo(true);
		return this.getComponentesCurriculares().add(componenteCurricular);
	}

	/**
	 * Remove um componente curricular do projeto.
	 * 
	 * @param componenteCurricular
	 * @return true - se removido com sucesso
	 */
	public boolean removeComponenteCurricular(
			ComponenteCurricularMonitoria componenteCurricular) {
		componenteCurricular.setProjetoEnsino(null);
		return this.getComponentesCurriculares().remove(componenteCurricular);
	}

	/**
	 * Adiciona um Docente ao projeto.
	 * 
	 * @param equipeDocente
	 * @return true - se adicionado com sucesso.
	 */
	public boolean addEquipeDocente(EquipeDocente equipeDocente) {
		equipeDocente.setProjetoEnsino(this);
		return this.getEquipeDocentes().add(equipeDocente);
	}

	/**
	 * Remove um equipeDocente do projeto.
	 * 
	 * @param equipeDocente
	 * @return true - se removido com sucesso
	 */
	public boolean removeEquipeDocente(EquipeDocente equipeDocente) {
		equipeDocente.setProjetoEnsino(null);
		return this.getEquipeDocentes().remove(equipeDocente);
	}

	/**
	 * Adiciona uma avaliação ao projeto.
	 * 
	 * @param obj
	 * @return true se adicionado com sucesso.
	 */
	public boolean addAvaliacaoMonitoria(AvaliacaoMonitoria obj) {
		obj.setProjetoEnsino(this);
		return avaliacoes.add(obj);
	}

	/**
	 * Remove uma avaliação do projeto
	 * 
	 * @param obj
	 * @return true se removido com sucesso.
	 */
	public boolean removeAvaliacaoMonitoria(AvaliacaoMonitoria obj) {
		obj.setProjetoEnsino(null);
		return avaliacoes.remove(obj);
	}

	/**
	 * Método utilizado para retornar os discentes ativos do projeto
	 * 
	 * @return
	 */
	public Collection<DiscenteMonitoria> getDiscentesMonitoria() {

		// removendo os excluídos da lista..
		if ((discentesMonitoria != null) && (!discentesMonitoria.isEmpty())) {
			for (Iterator<DiscenteMonitoria> it = discentesMonitoria.iterator(); it.hasNext();) {
				DiscenteMonitoria dm = it.next();
				if (!dm.isAtivo()) {
					it.remove();
				}
			}
		}

		return discentesMonitoria;
	}

	/**
	 * Lista somente os discentes de monitoria que
	 * estão executando o projeto. Descata todos que estão em
	 * espera.
	 * 
	 */
	public Collection<DiscenteMonitoria> getDiscentesExecutandoProjeto() {

		// removendo os excluídos da lista..
		if ((discentesMonitoria != null) && (!discentesMonitoria.isEmpty())) {
			for (Iterator<DiscenteMonitoria> it = discentesMonitoria.iterator(); it.hasNext();) {
				DiscenteMonitoria dm = it.next();
				if ( (!dm.isVigente())) {
					it.remove();
				}
			}
		}

		return discentesMonitoria;
	}


	/**
	 * Lista somente os discentes de monitoria que
	 * foram efetivados durante a execução do projeto.
	 * 
	 */
	public Collection<DiscenteMonitoria> getDiscentesConsolidados() {

		// removendo os excluídos da lista..
		if ((discentesMonitoria != null) && (!discentesMonitoria.isEmpty())) {
			for (Iterator<DiscenteMonitoria> it = discentesMonitoria.iterator(); it.hasNext();) {
				DiscenteMonitoria dm = it.next();
				if ( (!dm.isVinculoBolsista() && !dm.isVinculoNaoRemunerado())) {
					it.remove();
				}
			}
		}

		return discentesMonitoria;
	}



	public void setDiscentesMonitoria(
			Collection<DiscenteMonitoria> discentesMonitoria) {
		this.discentesMonitoria = discentesMonitoria;
	}

	/**
	 * Retorna todos os Docentes participantes do projeto de monitoria
	 * 
	 * @return
	 */
	public Collection<EquipeDocente> getEquipeDocentes() {

		// removendo os excluídos da lista..
		if ((equipeDocentes != null) && (!equipeDocentes.isEmpty())) {
			for (Iterator<EquipeDocente> it = equipeDocentes.iterator(); it.hasNext();) {
				EquipeDocente eq = it.next();
				if (eq.isExcluido()) {
					it.remove();
				}
			}
		}

		return equipeDocentes;
	}

	public void setEquipeDocentes(Collection<EquipeDocente> equipeDocentes) {
		this.equipeDocentes = equipeDocentes;
	}

	public Double getMediaAnalise() {
		return mediaAnalise;
	}

	public void setMediaAnalise(Double mediaAnalise) {
		this.mediaAnalise = mediaAnalise;
	}

	@Transient
	public int getNumComponentesCurriculares() {
		return numComponentesCurriculares;
	}

	public void setNumComponentesCurriculares(int numComponentesCurriculares) {
		this.numComponentesCurriculares = numComponentesCurriculares;
	}

	@Transient
	public int getNumDepartamentos() {
		return numDepartamentos;
	}

	public void setNumDepartamentos(int numDepartamentos) {
		this.numDepartamentos = numDepartamentos;
	}

	@Transient
	public int getNumProfessores() {
		return numProfessores;
	}

	public void setNumProfessores(int numProfessores) {
		this.numProfessores = numProfessores;
	}

	@Transient
	public double getRt() {
		return rt;
	}

	public void setRt(double rt) {
		this.rt = rt;
	}

	public String getSituacaoRelatorio() {
		return situacaoRelatorio;
	}

	public void setSituacaoRelatorio(String situacaoRelatorioFinal) {
		this.situacaoRelatorio = situacaoRelatorioFinal;
	}

	/**
	 * Usado na classificação do projeto de ensino
	 * 
	 * @return
	 */
	@Transient
	public Double getMediaFinal() {
		double departamentos = getNumDepartamentos()
		* getEditalMonitoria().getPesoNumDepartamentos();
		double componentes = getNumComponentesCurriculares()
		* getEditalMonitoria().getPesoCompCurriculares();
		double professores = getNumProfessores()
		* getEditalMonitoria().getPesoNumProfessores();
		double mediaAnalise = getMediaAnalise()
		* getEditalMonitoria().getPesoMediaAnalise();
		double rt = getRt() * getEditalMonitoria().getPesoRT();

		double mediaFinal = (departamentos + componentes + professores
				+ mediaAnalise + rt)
				/ (getEditalMonitoria().getPesoNumDepartamentos()
						+ getEditalMonitoria().getPesoCompCurriculares()
						+ getEditalMonitoria().getPesoNumProfessores()
						+ getEditalMonitoria().getPesoMediaAnalise() + getEditalMonitoria()
						.getPesoRT());

		return mediaFinal;

	}

	@Transient
	public double getIcb() {
		return (getEditalMonitoria().getNumeroBolsas() / this.bolsasSolicitadas);
	}

	/**
	 * Método utilizado para calcular a quantidade de bolsas concedidas ao projeto
	 */
	public void calculaBolsas() {
		double bolsas = getIcb() * this.bolsasSolicitadas;
		if (bolsas < 1) {
			this.bolsasConcedidas = 1;
		} else {
			this.bolsasConcedidas = (int) Math.floor(bolsas);
		}
	}
	
	/**
	 * Método utilizado para conceder tantas bolsas quanto foram solicitadas
	 * @return
	 */
	public int concederTodasBolsas() {
		return this.bolsasConcedidas = this.bolsasSolicitadas;
	}

	/**
	 * Usado na classificação do projeto
	 * 
	 * @param other
	 * @return
	 */
	public int compareTo(Object other) {
		ProjetoEnsino proj = (ProjetoEnsino) other;
		return proj.getMediaFinal().compareTo(this.getMediaFinal());
	}

	/**
	 * Método utilizado para conceder a quantidade de bolsas proporcional ao icb e não ultrapassando a quantidade total de bolsas para o projeto
	 * @param icb
	 * @param totalBolsas
	 * @return
	 */
	public int concederBolsaProporcionalIcb(double icb, Integer totalBolsas) {
		double valor = this.bolsasSolicitadas * icb;

		if (valor < 1) {
			this.bolsasConcedidas = 1;
		} else {
			this.bolsasConcedidas = (int) Math.floor(valor);
			if (this.bolsasConcedidas > totalBolsas) {
				this.bolsasConcedidas = totalBolsas;
			}
		}

		return this.bolsasConcedidas;
	}

	public ListaMensagens validate() {
		return null;
	}

	/**
	 * Método utilizado para adicionar uma quantidade X de bolsas, onde X é o valor passado por parâmentro
	 * @param qtd
	 */
	public void adicionarBolsa(int qtd) {
		this.bolsasConcedidas += qtd;
	}

	/**
	 * Todas as autorizações dadas pelos chefes de departamentos envolvidos pelo
	 * projeto
	 * 
	 * @return
	 */
	public Set<AutorizacaoProjetoMonitoria> getAutorizacoesProjeto() {
		Set<AutorizacaoProjetoMonitoria> autorizacoes = new HashSet<AutorizacaoProjetoMonitoria>();
		if ((autorizacoesProjeto != null) && (!autorizacoesProjeto.isEmpty())) {
			for (AutorizacaoProjetoMonitoria auto : autorizacoesProjeto) {
				if (auto.isAtivo()) {
					autorizacoes.add(auto);
				}
			}
		}
		return autorizacoes;
	}

	public void setAutorizacoesProjeto(
			Set<AutorizacaoProjetoMonitoria> autorizacoesProjeto) {
		this.autorizacoesProjeto = autorizacoesProjeto;
	}
	
	public void setAutorizacoesProjetoColecao(
			Collection<AutorizacaoProjetoMonitoria> autorizacoesProjeto) {
		this.autorizacoesProjeto.addAll(autorizacoesProjeto);
	}

	/**
	 * Nota do primeiro avaliador
	 * 
	 * @return the notaPrimeiraAvaliacao
	 */
	public Double getNotaPrimeiraAvaliacao() {
		return notaPrimeiraAvaliacao;
	}

	public void setNotaPrimeiraAvaliacao(Double notaPrimeiraAvaliacao) {
		this.notaPrimeiraAvaliacao = notaPrimeiraAvaliacao;
	}

	/**
	 * Nota do segundo avaliador
	 * 
	 * @return the notaSegundaAvaliacao
	 */
	public Double getNotaSegundaAvaliacao() {
		return notaSegundaAvaliacao;
	}

	public void setNotaSegundaAvaliacao(Double notaSegundaAvaliacao) {
		this.notaSegundaAvaliacao = notaSegundaAvaliacao;
	}

	/**
	 * Calcula a média de análise do projeto.<br>
	 * Em caso de notas discrepantes entre os avaliadores da comissão, um membro
	 * da PROGRAD, define essa média analisando o projeto como se fosse um
	 * terceiro avaliador.<br>
	 * 
	 * Essa nota representa a média final do projeto e determina se o projeto
	 * será RECOMENDADO, ou NÃO RECOMENDADO pela comissão de monitoria
	 * 
	 */
	public void calculaMedia() {
		if (notaAvaliacaoFinal != null) {
			mediaAnalise = notaAvaliacaoFinal; //avaliado por discrepância;
		}else {
			mediaAnalise = (notaPrimeiraAvaliacao + notaSegundaAvaliacao) / 2;
		}
	}

	/**
	 * Verifica se o projeto foi reconsiderado por requisitos formais
	 * 
	 * @return TRUE se pelo menos uma das reconsiderações foi autorizada
	 * 
	 */
	public boolean isReconsideradoPorRequisitosFormais() {

		for (AutorizacaoReconsideracao reconsideracao : getAutorizacoesReconsideracao()) {
			if (reconsideracao.isAutorizado()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	public Set<AutorizacaoReconsideracao> getAutorizacoesReconsideracao() {
		return autorizacoesReconsideracao;
	}

	public void setAutorizacoesReconsideracao(
			Set<AutorizacaoReconsideracao> autorizacoesReconsideracao) {
		this.autorizacoesReconsideracao = autorizacoesReconsideracao;
	}

	/**
	 * Método utilizado para informar o coordenador do projeto
	 * @return
	 */
	@Transient
	public Servidor getCoordenacao() {
		if (getProjeto().getCoordenador() != null) {
			return getProjeto().getCoordenador().getServidor();
		}
		for (EquipeDocente ed : equipeDocentes) {
			if (ed.isCoordenador()) {
				return ed.getServidor();
			}
		}
		for (ComponenteCurricularMonitoria ccm : getComponentesCurriculares()) {
			for (EquipeDocenteComponente edc : ccm.getDocentesComponentes()) {
				if (edc.getEquipeDocente().isCoordenador()) {
					return edc.getEquipeDocente().getServidor();
				}
			}
		}

		return null;
	}


	/**
	 * Retorna o Docente da equipe que coordena o projeto.
	 * Utilizado na finalização do coordenador do projeto.
	 * 
	 * @return
	 */
	public EquipeDocente getCoordenacaoEquipeDocente() {
		for (EquipeDocente ed : equipeDocentes) {
			if (ed.isCoordenador()) {
				return ed;
			}
		}
		for (ComponenteCurricularMonitoria ccm : getComponentesCurriculares()) {
			for (EquipeDocenteComponente edc : ccm.getDocentesComponentes()) {
				if (edc.getEquipeDocente().isCoordenador()) {
					return edc.getEquipeDocente();
				}
			}
		}
		return null;
	}

	public long getTotalMonitores() {
		return totalMonitores;
	}

	public void setTotalMonitores(long totalMonitores) {
		this.totalMonitores = totalMonitores;
	}

	/**
	 * utilizado em: see
	 * br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao.findQuantitativoMonitores(Integer
	 * idCentro)
	 * 
	 * @param sigla
	 */
	public void setSiglaUnidade(String sigla) {
		getUnidade().setSigla(sigla);
	}

	public String getParecerAvaliacaoFinal() {
		return parecerAvaliacaoFinal;
	}

	public void setParecerAvaliacaoFinal(String parecerAvaliacaoFinal) {
		this.parecerAvaliacaoFinal = parecerAvaliacaoFinal;
	}

	public List<ProvaSelecao> getProvasSelecao() {
		return provasSelecao;
	}

	public void setProvasSelecao(List<ProvaSelecao> provasSelecao) {
		this.provasSelecao = provasSelecao;
	}

	/**
	 * Coleção de relatórios do projeto (parcial ou final) enviados à prograd.
	 * 
	 * @return
	 */
	public Set<RelatorioProjetoMonitoria> getRelatoriosProjetoMonitoria() {
		return relatoriosProjetoMonitoria;
	}

	public void setRelatoriosProjetoMonitoria(
			Set<RelatorioProjetoMonitoria> relatoriosProjetoMonitoria) {
		this.relatoriosProjetoMonitoria = relatoriosProjetoMonitoria;
	}

	public boolean isEnviouRelatorioFinal() {
		return enviouRelatorioFinal;
	}

	public void setEnviouRelatorioFinal(boolean enviouRelatorioFinal) {
		this.enviouRelatorioFinal = enviouRelatorioFinal;
	}

	public boolean isEnviouRelatorioParcial() {
		return enviouRelatorioParcial;
	}

	public void setEnviouRelatorioParcial(boolean enviouRelatorioParcial) {
		this.enviouRelatorioParcial = enviouRelatorioParcial;
	}

	public TipoProjetoEnsino getTipoProjetoEnsino() {
		return tipoProjetoEnsino;
	}

	public void setTipoProjetoEnsino(TipoProjetoEnsino tipoProjetoEnsino) {
		this.tipoProjetoEnsino = tipoProjetoEnsino;
	}

	/**
	 * Valor solicita para financiamento do projeto usado por projetos PAMQEG
	 * 
	 * @return
	 */
	public Double getValorFinanciamento() {
		return valorFinanciamento;
	}

	public void setValorFinanciamento(Double valorFinanciamento) {
		this.valorFinanciamento = valorFinanciamento;
	}

	public String getTitulo() {
		return getProjeto().getTitulo();
	}

	/** Título do projeto de monitoria. */
	public void setTitulo(String titulo) {
		getProjeto().setTitulo(titulo);
	}

	/**
	 * As bolsas do projeto podem ser canceladas, basicamente, por 2 motivos: 1 -
	 * quando o monitor não envia o relatório de atividades por 2 meses
	 * consecutivos. 2 - quando o coordenador do projeto desliga um monitor e
	 * não o substitui por 2 meses consecutivos;
	 * 
	 * Este campo retira bolsas remuneradas e não remuneradas do projeto,
	 * diferente da movimentação de cotas que apenas transforma uma bolsa não
	 * remunerada em remunerada e virse-versa
	 * 
	 */
	public Integer getBolsasCanceladas() {
		return bolsasCanceladas;
	}

	public void setBolsasCanceladas(Integer bolsasCanceladas) {
		this.bolsasCanceladas = bolsasCanceladas;
	}

	/**
	 * Total de bolsas solicitadas pelo projeto.
	 * 
	 * @return int com a quantidade de bolsas
	 */
	public Integer getBolsasSolicitadas() {
		return bolsasSolicitadas;
	}

	public void setBolsasSolicitadas(Integer bolsasSolicitadas) {
		this.bolsasSolicitadas = bolsasSolicitadas;
	}

	public void setBolsasConcedidas(Integer bolsasConcedidas) {
		this.bolsasConcedidas = bolsasConcedidas;
	}

	public Integer getBolsasConcedidas() {
		return bolsasConcedidas;
	}

	/**
	 * Utilizado na verificação para permissão de continuação do cadastro após o
	 * prazo final de emissão de propostas
	 * 
	 * @return
	 */
	public boolean isCadastroNoPrazoRegular() {
		if ((getEditalMonitoria() != null) && (getDataCadastro() != null)) {
			return (DateUtils
					.truncate(getDataCadastro(), Calendar.DAY_OF_MONTH)
					.compareTo(getEditalMonitoria().getFimSubmissao()) <= 0);
		} else {
			return false;
		}
	}


	/**
	 * Utilizado na verificação para permissão de continuação do cadastro após o
	 * prazo final de emissão de propostas
	 * 
	 * @return
	 */
	public boolean isPermitidoSeguirCadastroAposPrazoRegular() {
		if (getEditalMonitoria() != null) {
			return isCadastroNoPrazoRegular();
		} else {
			return false;
		}
	}

	/**
	 * Retorna true se o projeto puder ser distribuído para avaliação da
	 * comissão de monitoria
	 * 
	 */
	public boolean isPermitidoDistribuirComissaoMonitoria() {
		return ((getSituacaoProjeto().getId() == TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO)
				|| (getSituacaoProjeto().getId() == TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_PROJETO));
	}


	/**
	 * Verifica se o projeto está autorizado para envio de relatórios de
	 * atividades dos monitores (frequência) baseado na regra de envio de
	 * frequência passada por parâmetro.
	 * 
	 * @return <code>true</code> caso o projeto possa enviar frequência de seus monitores
	 */
	public boolean isPermitidoCadastrarRelatorioAtividadesMonitores(EnvioFrequencia regraEnvioFrequencia) {
		return ((getAno() >= regraEnvioFrequencia.getAnoInicioProjetosPermitidos())
				&& (getAno() <= regraEnvioFrequencia.getAnoFimProjetosPermitidos()));
	}



	/**
	 * Retorna total de membros da comissão avaliando a projeto (com avaliações
	 * ativas)
	 * 
	 * @return
	 */
	public int getTotalAvaliadoresProjetoAtivos() {
		int result = 0;
		for (AvaliacaoMonitoria a : avaliacoes) {
			if ((a.getTipoAvaliacao().getId() == TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO)
					&& (a.getStatusAvaliacao().getId() != StatusAvaliacao.AVALIACAO_CANCELADA) && a.isAtivo()) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Retorna total de membros da comissão avaliando a relatórios parciais e
	 * finais (com avaliações ativas)
	 * 
	 * @return
	 */
	public int getTotalAvaliadoresRelatorioAtivos() {
		int result = 0;
		for (AvaliacaoMonitoria a : avaliacoes) {
			if ((a.getTipoAvaliacao().getId() == TipoAvaliacaoMonitoria.AVALIACAO_RELATORIO)
					&& (a.getStatusAvaliacao().getId() != StatusAvaliacao.AVALIACAO_CANCELADA) && a.isAtivo()) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Retorna total de membros da comissão avaliando a resumos sid (com
	 * avaliações ativas)
	 * 
	 * @return
	 */
	public int getTotalAvaliadoresResumoSidAtivos() {
		int result = 0;
		for (AvaliacaoMonitoria a : avaliacoes) {
			if ((a.getTipoAvaliacao().getId() == TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID)
					&& (a.getStatusAvaliacao().getId() != StatusAvaliacao.AVALIACAO_CANCELADA)) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Retorna todos os relatórios parciais do projeto
	 * 
	 * @return
	 */
	public Set<RelatorioProjetoMonitoria> getRelatoriosParciais() {
		Set<RelatorioProjetoMonitoria> result = new HashSet<RelatorioProjetoMonitoria>();

		for (RelatorioProjetoMonitoria rp : getRelatoriosProjetoMonitoria()) {
			if ((rp.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_PARCIAL)
					&& (rp.isAtivo())
					&& (rp.getStatus().getId() != StatusRelatorio.REMOVIDO)) {
				result.add(rp);
			}
		}
		return result;
	}

	/**
	 * Retorna todos os relatórios finais do projeto
	 * 
	 * @return
	 */
	public Set<RelatorioProjetoMonitoria> getRelatoriosFinais() {
		Set<RelatorioProjetoMonitoria> result = new HashSet<RelatorioProjetoMonitoria>();

		for (RelatorioProjetoMonitoria rp : getRelatoriosProjetoMonitoria()) {
			if (rp.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_FINAL) {
				result.add(rp);
			}
		}
		return result;
	}

	public int getTotalBolsasRemuneradasAtivas() {
		return totalBolsasRemuneradasAtivas;
	}

	public void setTotalBolsasRemuneradasAtivas(int totalBolsasRemuneradasAtivas) {
		this.totalBolsasRemuneradasAtivas = totalBolsasRemuneradasAtivas;
	}

	public int getTotalBolsasNaoRemuneradasAtivas() {
		return totalBolsasNaoRemuneradasAtivas;
	}

	public void setTotalBolsasNaoRemuneradasAtivas(
			int totalBolsasNaoRemuneradasAtivas) {
		this.totalBolsasNaoRemuneradasAtivas = totalBolsasNaoRemuneradasAtivas;
	}

	/**
	 * Retorna o total de bolsas remuneradas disponíveis no projeto.
	 * @return
	 */
	public int getTotalBolsasRemuneradasDisponiveis() {
		return getBolsasConcedidas() - getTotalBolsasRemuneradasAtivas();
	}

	/**
	 * Retorna o total de bolsas não remuneradas disponíveis no projeto.
	 * @return
	 */
	public int getTotalBolsasNaoRemuneradasDisponiveis() {
		return getBolsasNaoRemuneradas() - getTotalBolsasNaoRemuneradasAtivas();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public TipoSituacaoProjeto getSituacaoProjeto() {
		return situacaoProjeto;
	}

	public void setSituacaoProjeto(TipoSituacaoProjeto s) {
		this.situacaoProjeto = s;
	}

	/** Unidade proponente do projeto de monitoria. */
	public void setUnidade(Unidade unidade) {
		getProjeto().setUnidade(unidade);
	}

	public Unidade getUnidade() {
		return getProjeto().getUnidade();
	}

	public String getEmail() {
		return getProjeto().getEmail();
	}

	/** Email utilizado para contato com o coordenador do projeto de monitoria. */
	public void setEmail(String email) {
		getProjeto().setEmail(email);
	}

	public String getCentroAnoTitulo() {
		return getUnidade().getSigla() + " - " + getAno() + " - " + getTitulo();
	}

	/** Ano de referência do projeto. */
	public void setAno(Integer ano) {
		getProjeto().setAno(ano);
	}

	public Integer getAno() {
		return getProjeto().getAno();
	}

	public Collection<FotoProjeto> getFotos() {
		return getProjeto().getFotos();
	}

	/** Lista de fotos vinculadas ao projeto. */
	public void setFotos(Collection<FotoProjeto> fotos) {
		getProjeto().setFotos(fotos);
	}

	public Integer getIdArquivo() {
		return getProjeto().getIdArquivo();
	}

	/** Arquivo vinculado ao projeto. */
	public void setIdArquivo(Integer idArq) {
		getProjeto().setIdArquivo(idArq);
	}

	public Collection<HistoricoSituacaoProjeto> getHistoricoSituacao() {
		return getProjeto().getHistoricoSituacao();
	}

	/** Data de finalização do projeto. */
	public void setDataFim(Date date) {
		getProjeto().setDataFim(date);
	}
		
	/** Data de início do projeto. */
	public void setDataInicio(Date inicio) {
		getProjeto().setDataInicio(inicio);
	}

	/** Registro de entrada do cadastro do projeto. */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		getProjeto().setRegistroEntrada(registroEntrada);
	}

	/** Número institucional do projeto. */
	public void setNumeroInstitucional(Integer numeroInstitucional) {
		getProjeto().setNumeroInstitucional(numeroInstitucional);
	}

	public String getAnoTitulo() {
		return getProjeto().getAnoTitulo();
	}

	public String getResumo() {
		return getProjeto().getResumo();
	}

	/** Resumo do projeto. */
	public void setResumo(String resumo) {
		getProjeto().setResumo(resumo);
	}

	public String getJustificativa() {
		return getProjeto().getJustificativa();
	}

	/** Justificativa do projeto. */
	public void setJustificativa(String justificativa) {
		getProjeto().setJustificativa(justificativa);
	}

	public String getObjetivos() {
		return getProjeto().getObjetivos();
	}

	/** Objetivos do projeto. */
	public void setObjetivos(String objetivos) {
		getProjeto().setObjetivos(objetivos);
	}

	public String getReferencias() {
		return getProjeto().getReferencias();
	}

	/** Referências do projeto. */
	public void setReferencias(String referencias) {
		getProjeto().setReferencias(referencias);
	}

	public String getResultados() {
		return getProjeto().getResultados();
	}

	/** Resultados do projeto. */
	public void setResultados(String resultados) {
		getProjeto().setResultados(resultados);
	}

	public String getMetodologia() {
		return getProjeto().getMetodologia();
	}

	/** Metodologia do projeto. */
	public void setMetodologia(String metodologia) {
		getProjeto().setMetodologia(metodologia);
	}

	public Date getDataCadastro() {
		return getProjeto().getDataCadastro();
	}

	/** Data de cadastro do projeto. */
	public void setDataCadastro(Date date) {
		getProjeto().setDataCadastro(date);
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean a) {
		this.ativo = a;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Informa se este projeto faz parte de uma
	 * Ação Acadêmica Associada.
	 * 
	 * @return
	 */
	public boolean isProjetoAssociado() {
		return getProjeto().isProjetoAssociado();
	}

	/**
	 * Informa se é um projeto isolado.
	 * 
	 * @return
	 */
	public boolean isProjetoIsolado() {
		return getProjeto().isProjetoIsolado();
	}


	/**
	 * Informa se é um projeto de monitoria.
	 * 
	 * @return
	 */
	public boolean isProjetoMonitoria() {
		return (this.getTipoProjetoEnsino().getId() == TipoProjetoEnsino.PROJETO_DE_MONITORIA);
	}

	/**
	 * Informa se é um projeto de Melhoria da Qualidade do Ensino de Graduação
	 * @return
	 */
	public boolean isProjetoPAMQEG() {
		return (this.getTipoProjetoEnsino().getId() == TipoProjetoEnsino.PROJETO_PAMQEG);
	}

	/**
	 * Informa se é um projeto de Melhoria da Qualidade do Ensino de Graduação
	 * @return
	 */
	public boolean isAmbosProjetos() {
		return (this.getTipoProjetoEnsino().getId() == TipoProjetoEnsino.AMBOS_MONITORIA_PAMQEG);
	}
	
	/**
	 * Retorna o tipo de edital que deve reger este projeto de ensino.
	 * 
	 * @return
	 */
	public Character getTipoEdital() {
		if (isProjetoMonitoria()) {
			return Edital.MONITORIA;
		}
		if (isProjetoPAMQEG()) {
			return Edital.INOVACAO;
		}
		if (isAmbosProjetos()){
			return Edital.AMBOS_MONITORIA_E_INOVACAO;
		}	
		return null;
	}

	public String getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(String avaliacao) {
		this.avaliacao = avaliacao;
	}

	/**
	 * Método utilizado para informar se o projeto de monitoria está em execução
	 * @return
	 */
	public boolean isMonitoriaEmExecucao() {
		if (situacaoProjeto.getId() == TipoSituacaoProjeto.MON_EM_EXECUCAO
				|| situacaoProjeto.getId() == TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Verifica se o edital deste projeto ainda está em aberto.
	 * Utilizado para habilitar o campo de preenchimento de edital no formulário
	 * de cadastro do projeto.
	 * 
	 * @param editaisAbertosAtualmente Lista de editais abertos atualmente.
	 * @return
	 */
	public boolean isPermitidoSelecionarEditaisEmAberto(Collection<Edital> editaisAbertosAtualmente) {
		if ((getEditalMonitoria() != null) && (getEditalMonitoria().getId() > 0)) {
			for (Edital ee : editaisAbertosAtualmente) {
				if (getEditalMonitoria().getEdital().getId() == ee.getId()) {
					return true;
				}
			}
			return false; //edital deste projeto não está mais aberto.
		}
		return true; //projeto ainda não selecionou um edital.
	}

	public Double getNotaAvaliacaoFinal() {
		return notaAvaliacaoFinal;
	}

	public void setNotaAvaliacaoFinal(Double notaAvaliacaoFinal) {
		this.notaAvaliacaoFinal = notaAvaliacaoFinal;
	}

	public EditalMonitoria getEditalMonitoria() {
		return editalMonitoria;
	}

	public void setEditalMonitoria(EditalMonitoria editalMonitoria) {
		this.editalMonitoria = editalMonitoria;
	}

	public void setProduto(String produto) {
		this.produto = produto;
	}

	public String getProduto() {
		return produto;
	}

	public void setProcessoSeletivo(String processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	public String getProcessoSeletivo() {
		return processoSeletivo;
	}

    public Collection<OrcamentoDetalhado> getOrcamentosDetalhados() {
    	return getProjeto().getOrcamento();
    }

    /**
     * Método utilizado para setar os orçamentos detalhados
     * @param orcamentosDetalhados
     */
    public void setOrcamentosDetalhados(Collection<OrcamentoDetalhado> orcamentosDetalhados) {
    	this.getProjeto().setOrcamento(orcamentosDetalhados);
    }
    
    /**
     * Adiciona um OrcamentoDetalhado a lista de orçamentos do projeto de monitoria
     * 
     * @param orcamento
     * @return
     */
    public boolean addOrcamentoDetalhado(OrcamentoDetalhado orcamentoDetalhado) {
		orcamentoDetalhado.setProjeto(getProjeto());
		return getProjeto().getOrcamento().add(orcamentoDetalhado);
    }
	   
    public Collection<ArquivoProjeto> getArquivos() {
    	return getProjeto().getArquivos();
        }
    
    /**
     * Método utilizado para setar o Collection de arquivos do projeto de monitoria
     * @param arquivos
     */
    public void setArquivos(Collection<ArquivoProjeto> arquivos) {
	getProjeto().setArquivos(arquivos);
    }
    
    /**
     * Método utilizado para adicionar um arquivo ao projeto de monitoria
     * @param arquivo
     */
    public void addArquivo(ArquivoProjeto arquivo) {
	getProjeto().addArquivo(arquivo);	    
    }

	public void setValidaNovosCampos(Boolean validaNovosCampos) {
		this.validaNovosCampos = validaNovosCampos;
	}

	public Boolean getValidaNovosCampos() {
		return validaNovosCampos;
	}

}