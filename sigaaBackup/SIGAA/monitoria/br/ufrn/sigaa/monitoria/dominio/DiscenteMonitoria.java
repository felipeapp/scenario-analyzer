/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 08/06/2009
 * 
 */
package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Banco;

/*******************************************************************************
 * <p>
 * Representa um discente no projeto de monitoria. O discente ingressa no projeto
 * através de um processo seletivo. Um discente de monitoria pode ser bolsista
 * (remunerado) ou voluntário (não remunerado)
 * </p>
 * 
 * @author Ilueny Santos
 * @author Victor Hugo
 * 
 ******************************************************************************/
@Entity
@Table(name = "discente_monitoria", schema = "monitoria")
public class DiscenteMonitoria implements Validatable, Comparable<DiscenteMonitoria> {


	/** Identificador do discente monitoria  */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_discente_monitoria", unique = true, nullable = false)
	private int id;

	/** Discente do nível de graduação */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private DiscenteGraduacao discente = new DiscenteGraduacao();

	/** Representar a classificação do discente monitoria dentro de um processo seletivo */
	@Column(name = "classificacao")
	private int classificacao;

	/** Representa nota do discente monitoria */
	@Column(name = "nota")
	private Double nota = 0.0;

	/** Representa nota da prova do discente monitoria */
	@Column(name = "nota_prova")
	private Double notaProva = 0.0;

	/** Indica se discente monitoria é ativo */
	@Column(name = "ativo")
	@CampoAtivo
	private boolean ativo;

	/**
	 * Representa o tipo de vínculo do aluno com o projeto de monitoria. valores
	 * possíveis: 1 - NAO_REMUNERADO, 2 - BOLSISTA, 3 - NAO_CLASSIFICADO
	 
	@Column(name = "tipo_monitoria")
	private Integer tipoMonitoria;
	*/	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_vinculo_discente")
	private TipoVinculoDiscenteMonitoria tipoVinculo = new TipoVinculoDiscenteMonitoria();
	
    /** Observação do discente monitoria */
	@Column(name = "observacao")
	private String observacao;

	/** Representa a situação do discente monitoria 
	 *  Exemplos: AGUARDANDO_CONVOCACAO = 2, ASSUMIU_MONITORIA = 5
	 * */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_discente_monitoria")
	private SituacaoDiscenteMonitoria situacaoDiscenteMonitoria = new SituacaoDiscenteMonitoria();

	/** Projeto de ensino ao qual o discente monitoria fica vinculado  */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto_monitoria")
	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();

	/** Representa a data de cadastro do discente monitoria */
	@CriadoEm
	@Temporal(TemporalType.DATE)
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	/** Representa data de início do discente monitoria  */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Representa data final do discente monitoria  */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Representa a data de validação da PROGRAD */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_validacao_prograd")
	private Date dataValidacaoPrograd;

	/** Coleção que representa as orientações que o discente possui */
	@OneToMany(mappedBy = "discenteMonitoria", cascade = { CascadeType.ALL })
	private Collection<Orientacao> orientacoes = new HashSet<Orientacao>();

	/** Conjunto de relatórios que discente monitoria possui */
	@OneToMany(mappedBy = "discenteMonitoria")
	@OrderBy(value = "dataCadastro")
	private Set<RelatorioMonitor> relatoriosMonitor = new HashSet<RelatorioMonitor>();

	/** Conjunto de atividades que discente de monitoria poossui */
	@OneToMany(mappedBy = "discenteMonitoria")
	@OrderBy(value = "ano, mes")
	private Set<AtividadeMonitor> atividadesMonitor = new HashSet<AtividadeMonitor>();

	/** Representa a prova de seleção ao qual o discente faz parte */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_prova_selecao")
	private ProvaSelecao provaSelecao = new ProvaSelecao();

	/** Dados bancários dos alunos. São informados depois da validação da
	 * 	seleção pela prograd; Banco */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_banco")
	private Banco banco = null;

	/** Representa número da agência */
	@Column(name = "num_agencia")
	private String agencia;

	/** Representa número da conta */
	@Column(name = "num_conta")
	private String conta;
	
	/** Número da operação da conta bancária do discente de monitoria. */
	@Column(name = "num_operacao")
	private String operacao;

	/** Representa observação da PROGRAD */
	@Column(name = "observacao_prograd")
	private String observacaoPrograd;
	
	/** Indica convocação do discente monitoria */
	@Column(name = "convocado")
	private boolean discenteConvocado;

	/** Representa histórico de situações */
	@OneToMany(mappedBy = "discenteMonitoria")
	private Collection<HistoricoSituacaoDiscenteMonitoria> historicoSituacaoDiscenteMonitoria = new HashSet<HistoricoSituacaoDiscenteMonitoria>();

	/**
	 * Campo utilizado no form para validação.
	 */
	@Transient
	private boolean classificado = false;

	/**
	 * Utilizado apenas na visão para o usuário definir se o discente é
	 * bolsista ou não
	 */
	@Transient
	private boolean bolsista = false;

	/**
	 * Utilizado apenas na visão para o usuário definir se o discente poderá
	 * assumir a monitoria ou não
	 */
	@Transient
	private boolean inativo = false;

	/**
	 * Média ponderada das médias finais das disciplinas cursadas pelo aluno que
	 * fazem parte de um determinado projeto utilizado para desempate no cálculo
	 * da classificação do discente no projeto de monitoria em questão.
	 */
	@Transient
	private float mediaComponentesProjeto;

	/** coleção de docentes */
	@Transient
	private Collection<EquipeDocente> docentes = new ArrayList<EquipeDocente>();

	/** indica se discente foi selecionado no processo seletivo. */
	@Transient
	private boolean selecionado = false;

	/** justificar reprovação na seleção.... */
	@Transient
	private String justificativa;

	/** Utilizado na view no cadastro do resultado da seleção*/
	@Transient
	private Boolean prioritario = false;
	
	/** Utilizado na view ao adicionar novas orientações para o discente*/
	@Transient
	private Date dataInicioOrientacao;
	
	/** Utilizado na view ao adicionar novas orientações para o discente*/
	@Transient
	private Date dataFimOrientacao;

	
	// Constructors

	/** default constructor */
	public DiscenteMonitoria() {
	}

	public DiscenteMonitoria(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isInativo() {
		return inativo;
	}

	public void setInativo(boolean inativo) {
		this.inativo = inativo;
	}

	public boolean isBolsista() {
		return bolsista;	    
	}

	public void setBolsista(boolean bolsista) {
		this.bolsista = bolsista;
	}
	
	/** Formata a classificação do discente para exibição.*/
	public String getClassificacaoView() {
		return classificacao == 0 ? "-" : String.valueOf(classificacao) + "º"  ;
	}

	public int getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(int classificacao) {
		this.classificacao = classificacao;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public Double getNota() {
		return nota;
	}

	public void setNota(Double nota) {
		this.nota = nota;
	}

	public Double getNotaProva() {
		return notaProva;
	}

	public void setNotaProva(Double notaProva) {
		this.notaProva = notaProva;
	}

	/**
	 * Referente a alteração da ordem na classificação ou descreve o motivo da
	 * não aceitação da monitoria pelo discente
	 * 
	 * @return
	 */
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	@Override
	public boolean equals(Object obj) {

		boolean result = false;

		if (obj == null)
			return false;

		if (obj instanceof DiscenteMonitoria) {
			DiscenteMonitoria o = (DiscenteMonitoria) obj;
			if (o.getId() > 0 && this.getId() > 0) {
			    result = this.getId() == o.getId();
			} else {
			    result = this.getDiscente().getId() == o.getDiscente().getId();
			}
		}

		return result;
	}

	@Override
	public int hashCode() {

		int result = 17;
		if (this.getId() > 0) {
		    result = 37 * result + new Integer(this.getId()).hashCode();
		} else {
		    result = 37 * result + new Integer(this.discente.getId()).hashCode();
		}
		return result;
	}

	
	/**
	 * Valida os campos obrigatórios para um discente de monitoria existir.
	 * 
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(discente, "Discente", lista);
		ValidatorUtil.validateRequired(nota, "Nota do discente", lista);
		ValidatorUtil.validateRequired(notaProva, "Nota do discente", lista);
		ValidatorUtil.validateRequired(classificacao, "Classificação do discente", lista);
		ValidatorUtil.validateRequired(situacaoDiscenteMonitoria, "Situacao", lista);
		ValidatorUtil.validateRequired(tipoVinculo, "Tipo de Vínculo", lista);
		if ((nota != null) && ((nota > 10) || (nota < 0))) {
		    lista.addErro("Nota: O valor deve ser maior ou igual a 0 (zero) e menor ou igual a 10 (dez).");
		}
		if ((notaProva != null) && ((notaProva > 10) || (notaProva < 0))) {
			lista.addErro("Nota: O valor deve ser maior ou igual a 0 (zero) e menor ou igual a 10 (dez).");
		}
		if ((dataFim != null) && (dataInicio != null)) {
		    
			//Início da monitoria menor que o fim.
			ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Período da monitoria",lista);
		    
			Date inicioProjeto = getProjetoEnsino().getProjeto().getDataInicio();
			Date fimProjeto = getProjetoEnsino().getProjeto().getDataFim();
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if (!CalendarUtils.isDentroPeriodo(inicioProjeto, fimProjeto, dataInicio)) {
			    lista.addErro("Data de início da monitoria está fora do período do projeto (" + sdf.format(inicioProjeto) + " até " + sdf.format(fimProjeto) + ").");
			}
			
			if (!CalendarUtils.isDentroPeriodo(inicioProjeto, fimProjeto, dataFim)) {
			    lista.addErro("Data de fim da monitoria está fora do período do projeto (" + sdf.format(inicioProjeto) + " até " + sdf.format(fimProjeto) + ").");
			}
		}
		
		//Selecionou assumiu monitoria e um tipo de vínculo incompatível
		if ( !ValidatorUtil.isEmpty(discente) && isAssumiuMonitoria() && !isVinculoBolsista() && !isVinculoNaoRemunerado() ){			
			lista.addErro("Para que " + discente.getNome() + " possa ASSUMIR MONITORIA selecione o vínculos BOLSISTA ou NÃO REMUNERADO.");
		}
		
		return lista;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Collection<EquipeDocente> getDocentes() {
		return docentes;
	}

	public void setDocentes(Collection<EquipeDocente> docentes) {
		this.docentes = docentes;
	}

	public Collection<Orientacao> getOrientacoes() {
		return orientacoes;
	}

	/**
	 * Não exibe orientações marcadas como excluídas no banco
	 * 
	 * @return
	 */
	@Transient
	public Collection<Orientacao> getOrientacoesValidas() {

		// removendo os excluídos da lista..
		if (orientacoes != null) {
			for (Iterator<Orientacao> it = orientacoes.iterator(); it.hasNext();) {
				if (!it.next().isAtivo()) {
					it.remove();
				}
			}
		}
		return orientacoes;
	}

	public void setOrientacoes(Collection<Orientacao> orientacoes) {
		this.orientacoes = orientacoes;
	}

	public SituacaoDiscenteMonitoria getSituacaoDiscenteMonitoria() {
		return situacaoDiscenteMonitoria;
	}

	public void setSituacaoDiscenteMonitoria(
			SituacaoDiscenteMonitoria situacaoDiscenteMonitoria) {
		this.situacaoDiscenteMonitoria = situacaoDiscenteMonitoria;
	}
	

	public TipoVinculoDiscenteMonitoria getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscenteMonitoria tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Na view de cadastro de resultado da seleção este campo é
	 * exibido com 'situação'.
	 * 
	 * @return
	 */
	public boolean isClassificado() {
		return classificado;
	}

	public void setClassificado(boolean clas) {
		this.classificado = clas;
	}

	/**
	 * Retorna true caso o tipoVinculo do DiscenteMonitoria esteja definido
	 * entre os vínculos que permitem ao discente assumir a 
	 * monitoria como voluntário ou bolsista.
	 * 
	 * @return
	 */
	public boolean isVinculoValido() {
		return (tipoVinculo.getId() == TipoVinculoDiscenteMonitoria.BOLSISTA) 
			|| (tipoVinculo.getId() == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO)
			|| (tipoVinculo.getId() == TipoVinculoDiscenteMonitoria.EM_ESPERA);
	}

	/**
	 * Método para clonar a coleção de docentes
	 * 
	 * @param colEquipe
	 */
	public void clonaDocentes(Collection<EquipeDocente> colEquipe) {
		docentes = new ArrayList<EquipeDocente>();
		for (EquipeDocente equipe : colEquipe) {

			EquipeDocente novaEquipe = new EquipeDocente(equipe);
			docentes.add(novaEquipe);

		}
	}

	/**
	 * Retorna a lista de Atividades do monitor.
	 * 
	 * @return
	 */
	public Set<AtividadeMonitor> getAtividadesMonitor() {		
		// removendo os excluídos da lista..
		if ((atividadesMonitor != null) && (!atividadesMonitor.isEmpty())) {
			for (Iterator<AtividadeMonitor> it = atividadesMonitor.iterator(); it.hasNext();) {
			    	AtividadeMonitor r = it.next();
				if (!r.isAtivo())
					it.remove();
			}
		}

		return atividadesMonitor;
	}

	public void setAtividadesMonitor(Set<AtividadeMonitor> atividadesMonitor) {
		this.atividadesMonitor = atividadesMonitor;
	}

	/** Recupera relatórios do discente monitoria */
	public Set<RelatorioMonitor> getRelatoriosMonitor() {
		// removendo os excluídos da lista..
		if ((relatoriosMonitor != null) && (!relatoriosMonitor.isEmpty())) {
			for (Iterator<RelatorioMonitor> it = relatoriosMonitor.iterator(); it.hasNext();) {
			    	RelatorioMonitor r = it.next();
				if (!r.isAtivo())
					it.remove();
			}
		}

		return relatoriosMonitor;
	}

	public void setRelatoriosMonitor(Set<RelatorioMonitor> relatoriosMonitor) {
		this.relatoriosMonitor = relatoriosMonitor;
	}

	public float getMediaComponentesProjeto() {
		return mediaComponentesProjeto;
	}

	public void setMediaComponentesProjeto(float mediaComponentesProjeto) {
		this.mediaComponentesProjeto = mediaComponentesProjeto;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	/**
	 * Este método ordena os discentes participantes do projeto de monitoria de
	 * acordo com os critérios estabelecidos sendo o primeiro critério a nota
	 * final do processo de seleção. Em caso de empate o desempate obedecerá os
	 * seguintes critérios na seguinte ordem: 1 - Maior nota na prova escrita; 2
	 * - Maior nota no(s) componente(s) curricular(es) de formação objeto da
	 * seleção; (media ponderada dos componentes curriculares do projeto
	 * cursadas pelo aluno) 3 - Maior índice de rendimento acadêmico (IRA).
	 * 
	 * @return 1, 0 ou -1 dependendo do resultado da comparação.
	 * @param other discente que será comparado com o discente atual (this).
	 * 
	 * 
	 */
	public int compareTo(DiscenteMonitoria other) {
		// Primeiro critério: Nota final da avaliação
		int result = other.getNota().compareTo(this.getNota());

		if (result == 0) {
			// Segundo critério: Nota da prova escrita
			result = other.getNotaProva().compareTo(this.getNotaProva());
			if (result == 0) {
				// Terceiro critério: Média ponderada dos componentes do
				// projetos cursadas pelo discente
				result = Float.valueOf(other.getMediaComponentesProjeto()).compareTo(this.getMediaComponentesProjeto());
				if (result == 0) {
					// Quarto critério: IRA(índice 2) do discente
					if (other.getDiscente() != null && other.getDiscente().getDiscente() != null && other.getIndiceAcademicoSelecao() != null
							&& this.getDiscente() != null && this.getDiscente().getDiscente() != null && this.getIndiceAcademicoSelecao() != null) {
						
						Double v1 = other.getIndiceAcademicoSelecao().getValor();
						Double v2 = this.getIndiceAcademicoSelecao().getValor();
						result = v1.compareTo(v2);
					}
				}
			}
		}

		return result;
	}
	
	/** 
	 * Informa o índice acadêmico considerado na seleção dos monitores
	 */
	public IndiceAcademicoDiscente getIndiceAcademicoSelecao() {
		if (this.getDiscente().getDiscente().getIndices() == null) {
			return null;
		}
		//TODO: Remover número mágico 2  = IRA(índice 2) do discente.
		return this.getDiscente().getDiscente().getIndice(2);
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getObservacaoPrograd() {
		return observacaoPrograd;
	}

	public void setObservacaoPrograd(String observacaoPrograd) {
		this.observacaoPrograd = observacaoPrograd;
	}

	public ProvaSelecao getProvaSelecao() {
		return provaSelecao;
	}

	public void setProvaSelecao(ProvaSelecao provaSelecao) {
		this.provaSelecao = provaSelecao;
	}

	public Collection<HistoricoSituacaoDiscenteMonitoria> getHistoricoSituacaoDiscenteMonitoria() {
		return historicoSituacaoDiscenteMonitoria;
	}

	public void setHistoricoSituacaoDiscenteMonitoria(
			Collection<HistoricoSituacaoDiscenteMonitoria> historicoSituacaoDiscenteMonitoria) {
		this.historicoSituacaoDiscenteMonitoria = historicoSituacaoDiscenteMonitoria;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	/**
	 * Utilizado em certificados (Certificado do SID, e participação em
	 * projetos)...
	 * 
	 * @return String de orientadores do discente separadas por vírgula
	 */
	public String getListaOrientadores() {

		StringBuffer orientadores = new StringBuffer();
		orientadores.append("");

		for (Orientacao orientacao : getOrientacoes()) {
			EquipeDocente orientador = orientacao.getEquipeDocente();
			if ((!orientador.isExcluido())) {
				orientadores.append(", " + orientador.getServidor().getNome());
			}
		}

		if (orientadores.toString().trim().equals(""))
			return "";

		String result = orientadores.toString().replaceFirst("[, ]", "");
		int pos = result.lastIndexOf(',');

		if (pos > 0){
			return result.substring(0, pos) + " e" + result.substring(pos + 1);
		}else {
			return result == null ? "" : result;
		}
	}

	/**
	 * Informa se o discente já foi validado
	 * 
	 * Utilizado para controlar exibição de botões na tela de validação da prova
	 * seletiva.
	 * 
	 * Só podem ser validados/não-validados os monitores que ainda não enviaram
	 * freqüências
	 * 
	 * @return
	 */
	public boolean isPassivelValidacao() {
		return (this.situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.AGUARDANDO_VALIDACAO_PROGRAD) 
			|| (this.situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.DESVALIDADO_PELA_PROGRAD)
			|| (this.situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA)
			|| (this.situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO);
	}

	/**
	 * Informa a data em que a prograd realizou a validação da prova seletiva do
	 * discente
	 * 
	 * @return
	 */
	public Date getDataValidacaoPrograd() {
		return dataValidacaoPrograd;
	}

	public void setDataValidacaoPrograd(Date dataValidacaoPrograd) {
		this.dataValidacaoPrograd = dataValidacaoPrograd;
	}

	/**
	 * Informa se a prograd já realizou a validação deste discente
	 * 
	 * @return
	 */
	public boolean isValidadoPrograd() {
		return dataValidacaoPrograd == null;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	/**
	 * Retorna todos os relatórios parciais do monitor
	 * 
	 * @return
	 */
	public Set<RelatorioMonitor> getRelatoriosParciais() {
		Set<RelatorioMonitor> result = new HashSet<RelatorioMonitor>();

		for (RelatorioMonitor rm : getRelatoriosMonitor()) {
			if ((rm.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_PARCIAL) && rm.isAtivo())
				result.add(rm);
		}
		return result;
	}

	/** Verifica se foi enviado relatório parcial */
	public boolean isEnviouRelatoriosParciais() {
		if (getRelatoriosParciais().isEmpty())
			return false;
		else
			for (RelatorioMonitor rm : getRelatoriosParciais())
				if (rm.isEnviado())
					return true;

		return false;
	}

	/**
	 * Retorna todos os relatórios finais do monitor
	 * 
	 * @return
	 */
	public Set<RelatorioMonitor> getRelatoriosFinais() {
		Set<RelatorioMonitor> result = new HashSet<RelatorioMonitor>();

		for (RelatorioMonitor rm : getRelatoriosMonitor()) {
			if (rm.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_FINAL)
				result.add(rm);
		}
		return result;
	}

	/** Verifica se foi enviado relatório final */
	public boolean isEnviouRelatoriosFinais() {
		if (getRelatoriosFinais().isEmpty())
			return false;
		else
			for (RelatorioMonitor rm : getRelatoriosFinais())
				if (rm.isEnviado())
					return true;

		return false;
	}

	/**
	 * Retorna todos os relatórios de desligamento do monitor
	 * 
	 * @return
	 */
	public Set<RelatorioMonitor> getRelatoriosDesligamento() {
		Set<RelatorioMonitor> result = new HashSet<RelatorioMonitor>();

		for (RelatorioMonitor rm : getRelatoriosMonitor()) {
			if (rm.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR)
				result.add(rm);
		}
		return result;
	}

	/** Verifica se foi enviado relatório de desligamento */
	public boolean isEnviouRelatoriosDesligamento() {
		if (getRelatoriosDesligamento().isEmpty())
			return false;
		else
			for (RelatorioMonitor rm : getRelatoriosDesligamento())
				if (rm.isEnviado())
					return true;

		return false;
	}

	/**
	 * Informa se o discente está finalizado no projeto e se enviou os
	 * relatórios de desligamento ou final
	 * 
	 * Usado por: monitoria/ConsultarMonitor/lista.jsp
	 * Usado por: monitoria/DocumentosAutenticados/lista_discentes.jsp
	 * 
	 * @return
	 */
	public boolean isRecebeCertificado() {
		return ( isFinalizado()
				&& (situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA));
	}
	
	/**
	 * Informa se o discente está finalizado no projeto e se enviou os
	 * relatórios de desligamento ou final
	 * 
	 * @return
	 */
	public boolean isCadastrarRelatorios() {
		return (getDataInicio() != null)
				&& ((situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) 
						|| (situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA));
	}

	
	
	/**
	 * Verifica se o discente pode cadastrar relatório de atividades (frequência) segundo a regra de envio de frequência
	 * passada por parâmetro.
	 * 
	 * @param atv {@link AtividadeMonitor} relatório de atividades do monitor.
	 * @return <code>true</code>
	 */
	public boolean isPermitidoCadastrarRelatorioAtividade(EnvioFrequencia regraEnvioFrequencia) {
		
		if(!isVigente() || !isAssumiuMonitoria()) {
			return false;
		}		
		
		return (
			(regraEnvioFrequencia.isEnvioLiberado()) 
			&& (regraEnvioFrequencia.getDataInicioEntradaMonitorPermitido().compareTo(getDataInicio()) <= 0)
			&& (regraEnvioFrequencia.getDataFimEntradaMonitorPermitido().compareTo(getDataInicio()) >= 0)
			); 
	}
	

	/**
	 * Informa se o Discente tem algum tipo de prioridade na seleção de bolsas.
	 * Relativo ao Cadastro Único de Bolsistas
	 * 
	 * @return
	 */
	public Boolean getPrioritario() {
		return prioritario;
	}

	public void setPrioritario(Boolean prioritario) {
		this.prioritario = prioritario;
	}

	public Date getDataInicioOrientacao() {
	    return dataInicioOrientacao;
	}

	public void setDataInicioOrientacao(Date dataInicioOrientacao) {
	    this.dataInicioOrientacao = dataInicioOrientacao;
	}

	public Date getDataFimOrientacao() {
	    return dataFimOrientacao;
	}

	public void setDataFimOrientacao(Date dataFimOrientacao) {
	    this.dataFimOrientacao = dataFimOrientacao;
	}
	
	public boolean isVinculoBolsista() {
	    return getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.BOLSISTA;
	}

	public boolean isVinculoNaoRemunerado() {
	    return getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.NAO_REMUNERADO;
	}
	
	public boolean isVinculoEmEspera() {
	    return getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.EM_ESPERA;
	}
	
	public boolean isVinculoNaoClassificado() {
	    return getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.NAO_CLASSIFICADO;
	}
	
	/**
	 * Verifica, com base na data início e data fim, se a monitoria está ocorrendo.
	 * @return
	 */
	public boolean isVigente() {
	    return isAtivo() && isValido() && CalendarUtils.isDentroPeriodo(getDataInicio(), getDataFim());
	}

	/**
	 * Verifica, com base na data fim, se a orientação já foi finalizada.
	 * @return
	 */
	public boolean isFinalizado() {
	    return isAtivo() && isValido() && getDataFim().before(CalendarUtils.descartarHoras(new Date()));
	}
	
	/**
	 * Indica, com base na data início e data fim, se o discente já iniciou a monitoria.
	 * @return
	 */
	public boolean isValido() {
	    return (getDataInicio() != null) && (getDataFim() != null);
	}

	/**
	 * Retorna o status do discente com base nas datas de início e fim.
	 * 
	 * @return
	 */
	public String getStatus() {
	    return isFinalizado() ? "Finalizado" : isVigente() ? "Ativo" : "Inativo" ;
	}

	
	public boolean isAssumiuMonitoria() {
	    return (situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA);
	}
	
	public boolean isValidacaoCancelada() {
	    return situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.DESVALIDADO_PELA_PROGRAD;
	}
	
	public boolean isConvocado() {
	    return situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA;
	}
	
	public boolean isAguardandoConvocacao() {
	    return situacaoDiscenteMonitoria.getId() == SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO;
	}

	public boolean isAguardandoValidacao() {
		return getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.AGUARDANDO_VALIDACAO_PROGRAD;
	}

	public boolean isDiscenteConvocado() {
		return discenteConvocado;
	}

	public void setDiscenteConvocado(boolean discenteConvocado) {
		this.discenteConvocado = discenteConvocado;
	}
	
	
}