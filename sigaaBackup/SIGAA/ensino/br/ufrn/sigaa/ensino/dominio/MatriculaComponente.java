/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
* Created on 17/06/2008
*/
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

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
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ava.dominio.PerfilUsuarioAva;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;
import br.ufrn.sigaa.ensino.medio.dominio.NotaDisciplina;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.stricto.dominio.ConceitoNota;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que congrega a matr�cula de todos os n�veis de ensino e dos v�rios
 * componentes curriculares e atividades.
 * 
 * Gleydson Lima
 * 
 * aten��o: N�O USE CONSULTAS NESSA ENTIDADE COM HQL OU CRIT�RIA. SEMPRE FA�A
 * PROJE��O.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "matricula_componente", schema = "ensino")
public class MatriculaComponente implements PersistDB, Comparable<MatriculaComponente> {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="SEQ_MATRICULA")
	@GenericGenerator(name="SEQ_MATRICULA", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="ensino.matricula_componente_seq")})
	@Column(name = "id_matricula_componente", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
	 * Estrat�gia utilizada para implementar a consolida��o de turmas. Cont�m
	 * as regras de consolida��o de acordo com o n�vel de ensino. Implementa��o
	 * do padr�o de projeto Strategy.
	 */
	@Transient
	private EstrategiaConsolidacao estrategia;
	
	/**
	 * Situa��o atual da matr�cula.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_situacao_matricula", unique = false, nullable = false, insertable = true, updatable = true)	
	private SituacaoMatricula situacaoMatricula;

	/** Discente matriculado. */
	@ManyToOne(fetch=FetchType.EAGER, targetEntity=Discente.class)
	@JoinColumn(name = "id_discente")
	private DiscenteAdapter discente;

	/**
	 * Dado preenchido caso a matr�cula no componente seja atrav�s de uma turma.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_turma", unique = false, nullable = true, insertable = true, updatable = true)
	private Turma turma;

	/**
	 * Dado prenchido caso a matr�cula no componente seja atrav�s de um
	 * aproveitamento
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_atividade", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroAtividade registroAtividade;

	/** M�dia final do Discente no Componente Curricular */
	@Column(name = "media_final", unique = false, nullable = true, insertable = true, updatable = true, precision = 4)
	private Double mediaFinal;

	/** N�mero de faltas do Discente no Componente Curricular */
	@Column(name = "numero_faltas", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer numeroFaltas;

	/** Indica se o discente foi apto ou inapto no Componente Curricular */
	private Boolean apto;

	/** Registro de entrada, para fins de log. */
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;

	/** Data de cadastro da matr�cula */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	private Date dataCadastro;

	/** Foi alterada depois de consolidada */
	@Transient
	private Boolean foiRetificada;

	/** Atributo transiente usado em chekboxes de data tables */
	@Transient
	private boolean selected;

	/** Atributo transiente usado na jsp de trancamento */
	@Transient
	private boolean corequisito;

	/** Notas do Discente no Componente Curricular */
	@OneToMany(mappedBy = "matricula")
	private Collection<NotaUnidade> notas = new HashSet<NotaUnidade>();
	
	/** Nota que o aluno tirou na recupera��o */
	private Double recuperacao;

	/** Ano letivo da matr�cula */
	private Short ano;

	/** Per�odo letivo da matr�cula */
	private Byte periodo;

	/** Tipo da integraliza��o do Componente Curricular. 
	 * @see TipoIntegralizacao
	 */
	@Column(name = "tipo_integralizacao")
	private String tipoIntegralizacao;

	/** Componente Curricular em que o discente est� matriculado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_componente_curricular")
	private ComponenteCurricular componente = new ComponenteCurricular();

	/** Detalhes do Componente Curricular, quando da matr�cula. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_componente_detalhes")
	private ComponenteDetalhes detalhesComponente = new ComponenteDetalhes();

	/** identifica quais restri��es foram usadas numa matr�cula COMPULS�RIA */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_restricoes")
	private RestricoesMatricula restricoes;

	/** Atributo transiente usado para solicita��o de trancamento de matr�cula. */
	@Transient
	private SolicitacaoTrancamentoMatricula solicitacaoTrancamento;

	/** M�todo de avalia��o utilizado nessa matr�cula. */
	@Transient
	private Integer metodoAvaliacao;

	/** Metodologia de avalia��o utilizada em EAD */
	@Transient
	private MetodologiaAvaliacao metodologiaAvaliacao;	
	
	/** Quantidades de notas de uma matr�cula, em EAD, a metodologia pode mudar */
	@Transient
	private int qtdNotas;	
	
	/**
	 * Usado na consolida��o de ensino a dist�ncia, para mostrar a nota da
	 * avalia��o do tutor
	 */
	@Transient
	private Double notaTutor;

	/**
	 * Usado na consolida��o de ensino a dist�ncia, para mostrar a nota da
	 * avalia��o do segundo tutor
	 */
	@Transient
	private Double notaTutor2;

	/** N�mero de faltas calculadas da lista de presen�a da turma virtual */
	@Transient
	private Integer faltasCalculadas;
	
	/** Peso dado a m�dia no calculo da m�dia final, usado quando o discente encontra-se em recupera��o */
	@Transient
	private Integer pesoMedia;
	
	/** Peso dado a Recupera��o no calculo da m�dia final */
	@Transient
	private Integer pesoRecuperacao;

	/** Ano de in�cio dessa matr�cula (usado pra p�s) */
	@Column(name = "ano_inicio")
	private Integer anoInicio;
	
	/** M�s de in�cio dessa matr�cula (usado pra p�s) */
	private Integer mes;

	/** M�s de consolida��o dessa matr�cula (usado pra p�s) */
	@Column(name = "mes_fim")
	private Integer mesFim;

	/** Ano de consolida��o dessa matr�cula (usado pra p�s) */
	@Column(name = "ano_fim")
	private Integer anoFim;

	/** Usu�rio vinculado � matr�cula no componente curricular. */
	@Transient
	private Usuario usuario;

	/** Usu�rio que realizou a consolida��o da matr�cula. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_usuario_consolidacao")
	private Usuario usuarioConsolidacao;
	
	/** Data de consolida��o. */
	@Column(name="data_consolidacao")
	private Date dataConsolidacao;
	
	/** Indica se � uma rematr�cula. */
	private Boolean rematricula;
	
	/** Sexo do discente. */
	@Transient
	private Character sexo;
	
	/** Nome do pai do discente. */
	@Transient
	private String nomePai;
	
	/** Nome da m�e do discente. */
	@Transient
	private String nomeMae;
	
	/** Nacionalidade do discente. */
	@Transient
	private String nacionalidade;
	
	/** Naturalidade do discente. */
	@Transient
	private String naturalidade;
	
	/** Informa��es do usu�rio para cada turma. */
	@Transient
	private PerfilUsuarioAva perfilDiscente;
	
	/** Nova situa��o de matr�cula para altera��o da situa��o atual do discente. */
	@Transient
	private SituacaoMatricula novaSituacaoMatricula;
	
	/** Define se os dados do aluno devem aparecer ou n�o na Turma Virtual. */
	@Transient
	private Boolean esconder; 
	
	/** Define se as notas do aluno devem ser publicadas ou n�o para ele. */
	@Transient
	private Boolean ocultarNotas; 
	
	/**
	 * Este campo informa at� quando vai ser poss�vel trancar esta matricula componente.
	 */
	@Transient
	private Date dataLimiteTrancamento;
	
	/**
	 * C�digo da subturma onde se encontra o discente.
	 */
	@Transient
	private String codigoSubturma;
	
	/**
	 * Frequencia Implantada no hist�rico do discente.
	 */
	@Transient
	private Integer frequenciaImplantadaHistorico = null;
	
	/** Dados da notas das disciplinas do n�vel m�dio */
	@Transient 
	private NotaDisciplina notaDisciplina;
	
	/** Informa se o a matr�cula do discente se refere a uma depend�ncia. */
	@Transient
	private boolean dependencia;
	
	/** S�rie referente ao discente matriculado do n�vel m�dio */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_serie")
	private Serie serie;	
	
	
	public MatriculaComponente() {
		
	}

	public MatriculaComponente(int id) {
		this.id = id;
	}

	/** Construtor. 
	 * @param idMatriculaDisciplina
	 * @param discente
	 * @param turma
	 */
	public MatriculaComponente(int idMatriculaDisciplina, Discente discente,
			Turma turma) {
		this.id = idMatriculaDisciplina;
		this.discente = discente;
		this.turma = turma;
	}

	/** Construtor. */
	public MatriculaComponente(int idMatriculaDisciplina, Discente discente,
			Turma turma, Double mediaFinal, Integer numeroFaltas) {
		this.id = idMatriculaDisciplina;
		this.discente = discente;
		this.turma = turma;
		this.mediaFinal = mediaFinal;
		this.numeroFaltas = numeroFaltas;
	}

	/** Construtor.
	 * @param id
	 * @param codigoComponente
	 * @param nomeComponente
	 * @param idTipoComponente
	 * @param idSituacao
	 * @param situacao
	 * @param registro
	 */
	public MatriculaComponente(int id, String codigoComponente,
			String nomeComponente, int idTipoComponente, int idSituacao,
			String situacao, RegistroAtividade registro) {
		this.id = id;
		this.componente.setCodigo(codigoComponente);
		this.componente.setNome(nomeComponente);
		this.componente.setTipoComponente(new TipoComponenteCurricular(
				idTipoComponente));
		this.situacaoMatricula = new SituacaoMatricula(idSituacao, situacao);
		this.registroAtividade = registro;
	}

	/** Construtor.
	 * @param id
	 * @param codigoComponente
	 * @param nomeComponente
	 * @param idTipoComponente
	 * @param idSituacao
	 * @param situacao
	 * @param registro
	 * @param ano
	 * @param periodo
	 */
	public MatriculaComponente(int id, String codigoComponente,
			String nomeComponente, int idTipoComponente, int idSituacao,
			String situacao, RegistroAtividade registro, short ano, byte periodo) {
		this.id = id;
		this.componente.setCodigo(codigoComponente);
		this.componente.setNome(nomeComponente);
		this.componente.setTipoComponente(new TipoComponenteCurricular(
				idTipoComponente));
		this.situacaoMatricula = new SituacaoMatricula(idSituacao, situacao);
		this.registroAtividade = registro;
		this.ano = ano;
		this.periodo = periodo;
	}

	/** Construtor. 
	 * @param id
	 * @param nomeComponente
	 * @param codigo
	 * @param idSituacao
	 * @param situacao
	 */
	public MatriculaComponente(int id, String nomeComponente, String codigo,
			int idSituacao, String situacao) {
		this.id = id;
		this.componente.setNome(nomeComponente);
		this.turma.setCodigo(codigo);
		this.situacaoMatricula = new SituacaoMatricula(idSituacao, situacao);
	}

	/** Construtor.
	 * @param turma
	 * @param discente
	 * @param situacao
	 */
	public MatriculaComponente(Turma turma, Discente discente,
			SituacaoMatricula situacao) {
		this.turma = turma;
		this.discente = discente;
		this.situacaoMatricula = situacao;
	}

	/**
	 * Calcula a m�dia do aluno de acordo com as avalia��es cadastradas
	 * para esta disciplina.
	 * 
	 * @return
	 */
	public Double calculaMedia() {
		return estrategia != null ? estrategia.calculaMediaSemRecuperacao(this) : null;
	}
	
	/**
	 * Calcula a m�dia final do aluno, isto �, considerando todas as notas
	 * mais as poss�veis recupera��es. 
	 * @return
	 */
	public Double calculaMediaFinal() {
		return estrategia != null ? estrategia.calculaMediaFinal(this) : null;
	}
	
	/**
	 * Implementa��o do compareTo de acordo com o nome do discente 
	 * da {@link MatriculaComponente} e o passado como par�metro.
	 */
	public int compareTo(MatriculaComponente outro) {
		String nome = StringUtils.toAscii(getDiscente().getNome());
		return nome.compareToIgnoreCase(StringUtils.toAscii(outro.getDiscente().getNome()));
	}

	/**
	 * Consolida a turma ap�s a finaliza��o da inser��oo de notas e n�mero de
	 * faltas. O aluno � aprovado se tiver m�dia maior ou igual a 5.0 e n�mero
	 * de faltas menor que 25% da carga hor�ria. Se possuir m�dia menor que 5.0
	 * � reprovado por nota e se possuir frequencia menor que 75% da carga
	 * hor�ria � reprovado por faltas.
	 */
	public void consolidar() {
		estrategia.consolidar(this);
	}
	
	/**
	 * Implementa��o do m�todo equals comparando os ids da 
	 * {@link MatriculaComponente} atual com a passada como par�metro.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatriculaComponente other = (MatriculaComponente) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Short getAno() {
		return ano;
	}

	public Integer getAnoFim() {
		return anoFim;
	}

	public Integer getAnoInicio() {
		return anoInicio;
	}

	/** 
	 * Retorna o ano/per�odo da matr�cula, no formato: ano.periodo.
	 * @return
	 */
	public String getAnoPeriodo() {
		String anoPeriodo = "";
		
		if (ano != null)
			anoPeriodo = String.valueOf(ano);
		
		if ( ano != null && periodo != null && periodo > 0)
			anoPeriodo += "." + periodo;
		
		return anoPeriodo;
	}

	public Boolean getApto() {
		return apto;
	}
	
	/** Retorna a descri��o da compet�ncia
	 * @return Compet�ncia [Apto,Inapto]
	 */
	public String getCompetenciaDescricao() {
		return apto ? "Apto" : "Inapto";
	}

	public ComponenteCurricular getComponente() {
		return componente;
	}

	public int getComponenteCHTotal() {
		if (isEmpty(getDetalhesComponente()))
			return componente.getChTotal();
		return detalhesComponente.getChTotal();
	}
	
	/** 
	 * Retorna o c�digo do componente curricular.
	 * @return
	 */
	public String getComponenteCodigo() {
		if (isEmpty(detalhesComponente))
			return componente.getCodigo();
		return detalhesComponente.getCodigo();
	}

	/** 
	 * Retorna um string no formato codigo - nome.
	 * @see ComponenteCurricular#getCodigoNome()
	 * @return
	 */
	public String getComponenteCodigoNome() {
		return componente.getCodigoNome();
	}

	/** 
	 * Retorna os co-requisitos do componente curricular.
	 * @return
	 */
	public String getComponenteCoRequisito() {
		if (isEmpty(detalhesComponente))
			return componente.getCoRequisito();
		return detalhesComponente.getCoRequisito();
	}

	/** 
	 * Retorna o n�mero de cr�ditos totais do componente curricular.
	 * @return
	 * @throws DAOException 
	 */
	public int getComponenteCrTotal() throws DAOException {
		if ( isEmpty(detalhesComponente) ) {
			return componente.getCrTotal();
		}		
		else if ( (componente.getNivel() == NivelEnsino.STRICTO)
				&& detalhesComponente.getCrTotal() == 0 
				&& detalhesComponente.getCrAula() + detalhesComponente.getCrLaboratorio() + detalhesComponente.getCrEstagio() == 0) {
			ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(componente);
			return detalhesComponente.getChTotal() / parametros.getHorasCreditosAula();
		}
		return detalhesComponente.getCrTotal();		
	}

	/** 
	 * Retorna uma descri��o mais detalhada do componente curricular.
	 * @return
	 */
	public String getComponenteDescricao() {
		if (isEmpty(detalhesComponente))
			return componente.getDescricao();
		return detalhesComponente.getCodigo() + " - " + detalhesComponente.getNome() + " - " + detalhesComponente.getChTotal() + "h";
	}

	/** 
	 * Retorna uma descri��o resumida do componente curricular.
	 * @return
	 */
	public String getComponenteDescricaoResumida() {
		if (isEmpty(detalhesComponente))
			return componente.getDescricaoResumida();
		return detalhesComponente.getCodigo() + " - " + detalhesComponente.getNome();
	}

	/** 
	 * Retorna a ementa do componente curricular.
	 * @return
	 */
	public String getComponenteEmenta() {
		if (isEmpty(detalhesComponente))
			return componente.getEmenta();
		return detalhesComponente.getEmenta();
	}

	/** 
	 * Retorna os componentes curriculares equivalentes.
	 * @return
	 */
	public String getComponenteEquivalencia() {
		if (isEmpty(detalhesComponente))
			return componente.getEquivalencia();
		return detalhesComponente.getEquivalencia();
	}

	/** 
	 * Retorna o nome do componente curricular.
	 * @return
	 */
	public String getComponenteNome() {
		if (detalhesComponente == null || detalhesComponente.getId() == 0)
			return componente.getNome();
		return detalhesComponente.getNome();
	}

	/** 
	 * Retorna uma descri��o abreviada do nome e observa��o do componente curricular.
	 * @return
	 */
	public String getComponenteNomeObservacao() {
		String nome = getComponenteNome();
		if (nome != null && getTurma() != null && !isEmpty(getTurma().getObservacao())) {
			String observacao = getTurma().getObservacao();
			nome += " - " + StringUtils.limitTxt(observacao, 80).toUpperCase();
		}
		
		return nome;
	}
	
	/** 
	 * Retorna os pr�-requisitos do componente curricular.
	 * @return
	 */
	public String getComponentePreRequisito() {
		if (isEmpty(detalhesComponente))
			return componente.getPreRequisito();
		return detalhesComponente.getPreRequisito();
	}

	/** 
	 * Retorna o conceito final do discente no componente curricular.
	 * @return
	 */
	public Double getConceito() {
		return mediaFinal;
	}

	/** Converte a m�dia final em conceito.
	 * @return Conceito [A,E]
	 */
	public String getConceitoChar() {
		if (mediaFinal == null)
			return "-";

		return ConceitoNota.getDescricao(mediaFinal);
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public Date getDataConsolidacao() {
		return dataConsolidacao;
	}
	
	public ComponenteDetalhes getDetalhesComponente() {
		return detalhesComponente;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public EstrategiaConsolidacao getEstrategia() {
		return estrategia;
	}

	public Integer getFaltasCalculadas() {
		return faltasCalculadas;
	}

	public Boolean getFoiRetificada() {
		return foiRetificada;
	}

	/** Retorna o percentual de frequ�ncia do discente no componente curricular.
	 * @return
	 * @throws DAOException 
	 */
	public Double getFrequencia() throws DAOException {
		if ( (getTurma() == null || getTurma().getId() == 0) 
				&& ( getDetalhesComponente().getChTotal() == 0 ) )
			return null;

		ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(getComponente());
		
		if (parametros == null)
			return null;
		
		int totalAulas =  (int) (getDetalhesComponente().getChTotal() * 60 / parametros.getMinutosAulaRegular());
		int faltas = numeroFaltas != null ? numeroFaltas : 0;
		
		int totalAssistido = totalAulas - faltas;
		
		return (double) ((totalAssistido * 100) / totalAulas);
	}

	public int getId() {
		return id;
	}

	/** 
	 * Retorna o n�mero m�ximo de faltas no componente curricular. 
	 * @param frequenciaMinima
	 * @return
	 * @throws DAOException 
	 */
	public int getMaximoFaltas(double frequenciaMinima, double minutosAulaRegular){
			
		int chTotal = getComponenteCHTotal();
		int totalAulas =  (int) (chTotal * 60 / minutosAulaRegular);
		return (int) (totalAulas - (totalAulas * frequenciaMinima/100.0));
	}

	public Double getMediaFinal() {
		return mediaFinal;
	}

	/** 
	 * Seta a descri��o da m�dia final do discente de acordo com o m�todo de avalia��o.
	 * @return M�dia, ou conceito, do discente.
	 */
	public String getMediaFinalDesc() {
		
		if( estrategia!= null) {
			return estrategia.getMediaFinalDesc(this);
		}
		else{
			if (getMetodoAvaliacao() != null && getMetodoAvaliacao() == MetodoAvaliacao.CONCEITO) {
				return String.valueOf(getConceitoChar());
			} else {
				//Caso getMediaFinal() seja nulo vai ser APROVEITAMENTO DE CREDITOS
				//Apenas evitando que mostre nulo no campo referente a nota.
				return getMediaFinal() != null ? String.valueOf(getMediaFinal()) : "---";
			}			
		}	
	}

	public Double getMedia() {
		return estrategia != null ? estrategia.calculaMediaFinal(this) : null;
	}
	
	/** 
	 * Retorna a m�dia do discente se todas suas notas tiverem sido preenchidas.
	 */
	public Double getMediaPreenchida() {
		
		if ( !isConsolidada() ) {
			if ( notas == null || notas.size() == 0 )
				return null;
			
			for ( NotaUnidade nota : notas ){
				if ( nota.getNotaPreenchida() == null && !nota.isRecuperacao() )
					return null;
			}
		}	
		return getMedia();
	}
	
	/** 
	 * Retorna a m�dia parcial(atual) do discente.
	 */
	public Double getMediaParcial() {
		return calculaMedia();
	}
	
	public Integer getMes() {
		return mes;
	}

	/** 
	 * Retorna o m�s/ano de fim.
	 */
	public String getMesAnoFim() {
		if (anoFim == null || mesFim == null)
			return null;
		else
			return mesFim + "/" + anoFim;
	}

	/** 
	 * Retorna o m�s/ano de in�cio.
	 */
	public String getMesAnoInicio() {
		if (componente != null && componente.isAtividade())
			return mes + "/" + ano;
		else if (turma != null)
			return turma.getMesAnoInicio();
		else
			return null;
	}

	public Integer getMesFim() {
		return mesFim;
	}

	public Integer getMetodoAvaliacao() {
		return metodoAvaliacao;
	}

	public String getNacionalidade() {
		return nacionalidade;
	}

	public String getNaturalidade() {
		return naturalidade;
	}
	
	public String getNomeMae() {
		return nomeMae;
	}

	public String getNomePai() {
		return nomePai;
	}

	/** 
	 * Retorna a nota do discente pelo �ndice da unidade.
	 * @param indice
	 * @return Nota[i] do discente
	 */
	public NotaUnidade getNotaByIndice(int indice) {
		if (notas != null) {
			for (NotaUnidade nota : notas) {
				if (nota.getUnidade() == indice)
					return nota;
			}
		}
		return null;
	}

	/** 
	 * Retorna as notas de recupera��o.
	 * @return
	 */
	public Collection<NotaUnidade> getRecuperacoes() {
		Collection<NotaUnidade> recuperacoes = new ArrayList<NotaUnidade>();
		if (notas != null) {
			for (NotaUnidade nota : notas) {
				if (nota.isRecuperacao())
					recuperacoes.add(nota);
			}
		}
		return recuperacoes;
	}

	/** Retorna a note de uma unidade */
	public NotaUnidade getNotaByUnidade(int unidade) {
		for (NotaUnidade nu : notas) {
			if (nu.getUnidade() == unidade)
				return nu;
		}
		return null;
	}
	
	public Collection<NotaUnidade> getNotas() {
		return notas;
	}

	public Double getNotaTutor() {
		return notaTutor;
	}

	public Double getNotaTutor2() {
		return notaTutor2;
	}

	/** Retorna nota de tutor(primeira ou segunda nota) */
	public Double getNotaTutorByUnidade(int numero) {
		if (numero == 1)
			return notaTutor;
		else if (numero == 2)
			return notaTutor2;
		else
			return null;
	}
	
	public Integer getNumeroFaltas() {
		return this.numeroFaltas;
	}

	public Byte getPeriodo() {
		return periodo;
	}

	public RegistroAtividade getRegistroAtividade() {
		return registroAtividade;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public Boolean getRematricula() {
		return rematricula;
	}

	public RestricoesMatricula getRestricoes() {
		return restricoes;
	}

	public Character getSexo() {
		return sexo;
	}

	/** 
	 * Retorna uma string abreviada da situa��o da matr�cula. Ex.: REPF, TRAN, APR, REC REP.
	 * @return
	 */
	public String getSituacaoAbrev() {
		return estrategia != null ? estrategia.getDescricaoSituacao(this) : "--";
	}

	/** 
	 * Retorna uma string abreviada da situa��o da matr�cula apenas quando todas as notas estiverem preenchidas. Ex.: REPF, TRAN, APR, REC REP.
	 * @return
	 */
	public String getSituacaoPreenchida() {
		
		if ( metodoAvaliacao != null && metodoAvaliacao == 1 && (notas == null || notas.size() == 0) )
			return "--";
		
		if(!ValidatorUtil.isEmpty(notas)) {
			for ( NotaUnidade nota : notas ){
				if ( nota.getNotaPreenchida() == null && !nota.isRecuperacao() )
					return "--";
			}
		}
		return getSituacaoAbrev();
	}
	
	/** 
	 * Retorna uma descri��o textual da situa��o da matr�cula no coponente curricular. Ex.: "Reprovado por falta",
	 * "Reprovado por notas", "Em recupera��o", "Aprovado".
	 * @return
	 */
	public String getSituacaoCompleta() {
		if ("REPF".equals(getSituacaoAbrev()))
			return "Reprovado por falta";
		else if ("REP".equals(getSituacaoAbrev()))
			return "Reprovado por notas";
		else if ("REC".equals(getSituacaoAbrev()))
			return "Em recupera��o";
		else if ("APR".equals(getSituacaoAbrev()))
			return "Aprovado";
		else if ("REMF".equals(getSituacaoAbrev()))
			return "Reprovado por notas e faltas";
		else
			return "--";
	}
	
	/** 
	 * Retorna uma descri��o textual da situa��o do discente da matr�cula. Ex.: "Aluno Reprovado", 
	 *  Aluno em Recupera��o", "Aluno Aprovado".
	 * @return
	 */
	public String getSituacaoAluno() {
		if ("REPF".equals(getSituacaoAbrev()) || "REP".equals(getSituacaoAbrev()) || "REMF".equals(getSituacaoAbrev()) )
			return "Aluno Reprovado";
		else if ("REC".equals(getSituacaoAbrev()))
			return "Aluno em Recupera��o";
		else if ("APR".equals(getSituacaoAbrev()))
			return "Aluno Aprovado";
		else
			return null;
	}

	public SituacaoMatricula getSituacaoMatricula() {
		return situacaoMatricula;
	}

	public SolicitacaoTrancamentoMatricula getSolicitacaoTrancamento() {
		return solicitacaoTrancamento;
	}

	public String getTipoIntegralizacao() {
		return tipoIntegralizacao;
	}

	/** 
	 * Retorna uma descri��o textual do tipo de integraliza��o do componente curricular.
	 * @return
	 */
	public String getTipoIntegralizacaoDescricao() {
		return TipoIntegralizacao.getDescricao(tipoIntegralizacao);
	}

	/** 
	 * Retorna um caractere indicando o tipo de integraliza��o do componente curricular.
	 * <ul>
	 *   <li>"@", caso DISCIPLINA OBRIGATORIA</li>
	 *   <li>"$", caso ATIVIDADE OBRIGATORIA</li>
	 *   <li>"�", caso ATIVIDADE E (OPTATIVA DA GRADE OU OPTATIVA DO CURSO/CIDADE)</li>
	 *   <li>"*", caso DISCIPLINA E (OPTATIVA DA GRADE OU OPTATIVA DO CURSO/CIDADE)</li>
	 *   <li>"#", caso EXTRA CURRICULAR</li>
	 *   <li>"e", caso EQUIVALENTE OBRIGAT�RIA</li>
	 *   <li>"&", caso EQUIVALENTE OPTATIVA DA GRADE</li>
	 * </ul>
	 * @return
	 */
	public String getTipoIntegralizacaoLegenda() {
		return TipoIntegralizacao.getLegenda(tipoIntegralizacao, componente.isAtividade() || componente.isAtividadeColetiva());
	}

	public Turma getTurma() {
		return turma;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Usuario getUsuarioConsolidacao() {
		return usuarioConsolidacao;
	}

	/** 
	 * Indica se o componente tem alguma avalia��o.
	 * @return
	 */
	public boolean hasAvaliacao() {
		if (!isEmpty(notas)) {
			for (NotaUnidade nota : notas) {
				if (!isEmpty(nota.getAvaliacoes()))
					return true;
			}
		}
		return false;
	}

	/**
	 * Defini��o do hashcode da {@link MatriculaComponente}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	
	/** Indica se o discente foi aprovado ou n�o no componente curricular.
	 * @return true, caso o status seja APROVADO. False, caso contr�rio.
	 */
	public boolean isAprovado() {
		return SituacaoMatricula.APROVADO.equals(situacaoMatricula);
	}
	
	/** Indica se o discente foi reprovado ou n�o no componente curricular.
	 * @return true, caso o status seja REPROVADO ou REPROVADO_FALTA ou REPROVADO_MEDIA_FALTA. False, caso contr�rio.
	 */
	public boolean isReprovado() {
		return this.situacaoMatricula.equals(SituacaoMatricula.REPROVADO)
				|| this.situacaoMatricula.equals(SituacaoMatricula.REPROVADO_FALTA)
				|| this.situacaoMatricula.equals(SituacaoMatricula.REPROVADO_MEDIA_FALTA);
	}	
	
	/** Indica se a matr�cula no componente curricular foi um aproveitamento.
	 * @return true, caso o status seja CUMPRIU ou DISPENSADO ou TRANSFERIDO. False, caso contr�rio.
	 */
	public boolean isAproveitadoDispensado() {
		return SituacaoMatricula.APROVEITADO_CUMPRIU.equals(situacaoMatricula)
				|| SituacaoMatricula.APROVEITADO_DISPENSADO.equals(situacaoMatricula)
				|| SituacaoMatricula.APROVEITADO_TRANSFERIDO.equals(situacaoMatricula);
	}

	/** Indica se a matr�cula no componente curricular foi um aproveitamento.
	 * @return true, caso o status seja CUMPRIU ou TRANSFERIDO. False, caso contr�rio.
	 */
	public boolean isAproveitado() {
		return SituacaoMatricula.APROVEITADO_CUMPRIU.equals(situacaoMatricula)
				|| SituacaoMatricula.APROVEITADO_TRANSFERIDO.equals(situacaoMatricula);
	}	
	
	/** Indica se o discente teve a matr�cula cancelada ou n�o no componente curricular.
	 * @return true, caso o status seja CANCELADO. False, caso contr�rio.
	 */
	public boolean isCancelada() {
		return SituacaoMatricula.CANCELADO.equals(situacaoMatricula);
	}

	/** 
	 * Indica se a matriculaComponente est� consolidade. Ela estar� consolidade quando a situa��o for APROVADO ou
	 * REPROVADO ou REPROVADO_FALTA ou APROVEITADO_CUMPRIU.
	 * @return
	 */
	public boolean isConsolidada() {
		return this.situacaoMatricula.equals(SituacaoMatricula.APROVADO)
				|| this.situacaoMatricula.equals(SituacaoMatricula.REPROVADO)
				|| this.situacaoMatricula.equals(SituacaoMatricula.REPROVADO_FALTA)
				|| this.situacaoMatricula.equals(SituacaoMatricula.REPROVADO_MEDIA_FALTA)
				|| this.situacaoMatricula.equals(SituacaoMatricula.APROVEITADO_CUMPRIU);
	}

	public boolean isCorequisito() {
		return corequisito;
	}

	/** 
	 * Indica se cumpriu o componente em outro curso dentro da UFRN.
	 * @return
	 */
	public boolean isCumpriu() {
		return SituacaoMatricula.APROVEITADO_CUMPRIU.equals(situacaoMatricula);
	}

	/** 
	 * Indica se aproveitou o componente e foi dispensando. 
	 * @return
	 */
	public boolean isDispensa() {
		return SituacaoMatricula.APROVEITADO_DISPENSADO.equals(situacaoMatricula);
	}

	/** 
	 * Indica se o componente � de Ensino a Dist�ncia.
	 * @return
	 */
	public boolean isEad() {
		return turma.getPolo() != null;
	}

	/** Indica se o discente est� em recupera��o ou n�o.
	 * @return true, se o discente est� em recupera��o. False, caso contr�rio.
	 */
	public boolean isEmRecuperacao() {
		return estrategia != null ? estrategia.isEmRecuperacao(this) : null;
	}

	/** Indica se o discente teve a matr�cula exclu�da ou n�o no componente curricular.
	 * @return true, caso o status seja EXCLU�DO. False, caso contr�rio.
	 */
	public boolean isExcluida() {
		return SituacaoMatricula.EXCLUIDA.equals(situacaoMatricula);
	}

	/** Indica se o discente est� MATRICULADO ou n�o no componente curricular.
	 * @return true, caso o status esteja MATRICULADO. False, caso contr�rio.
	 */
	public boolean isMatriculado() {
		return SituacaoMatricula.MATRICULADO.equals(situacaoMatricula);
	}

	/** 
	 * Indica se o m�todo de avalia��o � COMPETENCIA.
	 * @return
	 */
	public boolean isMetodoAptidao() {
		return metodoAvaliacao != null && metodoAvaliacao == MetodoAvaliacao.COMPETENCIA;
	}

	/** 
	 * Indica se o m�todo de avalia��o � CONCEITO.
	 * @return
	 */
	public boolean isMetodoConceito() {
		return metodoAvaliacao != null && metodoAvaliacao == MetodoAvaliacao.CONCEITO;
	}

	/** 
	 * Indica se o m�todo de avalia��o � NOTA.
	 * @return
	 */
	public boolean isMetodoNota() {
		return metodoAvaliacao != null && metodoAvaliacao == MetodoAvaliacao.NOTA;
	}

	/** Indica se o discente est� reprovado por falta.
	 * @param frequenciaMinima
	 * @return true, se o discente est� reprovado por falta. False, caso contr�rio.
	 * @throws DAOException 
	 */
	public boolean isReprovadoFalta(double frequenciaMinima, double minutosAulaRegular) {
		if (getNumeroFaltas() == null)
			return false;
		return getNumeroFaltas() > getMaximoFaltas(frequenciaMinima,minutosAulaRegular);
	}

	public boolean isSelected() {
		return selected;
	}

	/** Indica se o discente trancou ou n�o no componente curricular.
	 * @return true, caso o status seja TRANCADO. False, caso contr�rio.
	 */
	public boolean isTrancado() {
		return SituacaoMatricula.TRANCADO.equals(situacaoMatricula);
	}

	/** Indica se o discente est� dentro do prazo de trancamento
	 * M�todo chamado pela seguinte JSP: /ensino/trancamento_matricula/solicitacao.jsp
	 */
	@Transient
	public boolean isDentroPrazoLimiteTrancamento() {
		
		if (dataLimiteTrancamento == null)
			return true;
		
		Date hoje = CalendarUtils.descartarHoras(new Date());
		
		if (hoje.after(dataLimiteTrancamento))
			return false;
		
		return true;
	}	
	
	public void setAno(Short ano) {
		this.ano = ano;
	}

	public void setAnoFim(Integer anoFim) {
		this.anoFim = anoFim;
	}

	public void setAnoInicio(Integer anoInicio) {
		this.anoInicio = anoInicio;
	}

	public void setApto(Boolean apto) {
		this.apto = apto;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

	/** 
	 * Seta o conceito final do discente no componente curricular.
	 * @param conceito
	 */
	public void setConceito (Double conceito) {
		if (conceito != null)
			this.mediaFinal = conceito;
		else
			this.mediaFinal = null;
	}

	public void setCorequisito(boolean corequisito) {
		this.corequisito = corequisito;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public void setDataConsolidacao(Date dataConsolidacao) {
		this.dataConsolidacao = dataConsolidacao;
	}

	public void setDetalhesComponente(ComponenteDetalhes detalhesComponente) {
		this.detalhesComponente = detalhesComponente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public void setEstrategia(EstrategiaConsolidacao estrategia) {
		this.estrategia = estrategia;
	}

	public void setFaltasCalculadas(Integer faltasCalculadas) {
		this.faltasCalculadas = faltasCalculadas;
	}

	public void setFoiRetificada(Boolean foiRetificada) {
		this.foiRetificada = foiRetificada;
	}

	public void setId(int idMatriculaDisciplina) {
		this.id = idMatriculaDisciplina;
	}

	public void setMediaFinal(Double mediaFinal) {
		this.mediaFinal = mediaFinal;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public void setMesFim(Integer mesFim) {
		this.mesFim = mesFim;
	}

	public void setMetodoAvaliacao(Integer metodoAvaliacao) {
		this.metodoAvaliacao = metodoAvaliacao;
	}

	public void setNacionalidade(String nacionalidade) {
		this.nacionalidade = nacionalidade;
	}

	public void setNaturalidade(String naturalidade) {
		this.naturalidade = naturalidade;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	public void setNotas(Collection<NotaUnidade> notas) {
		this.notas = notas;
	}

	public void setNotaTutor(Double notaTutor) {
		this.notaTutor = notaTutor;
	}

	public void setNotaTutor2(Double notaTutor2) {
		this.notaTutor2 = notaTutor2;
	}

	public void setNumeroFaltas(Integer numeroFaltas) {
		this.numeroFaltas = numeroFaltas;
	}

	/** Seta o n�mero de faltas por frequ�ncia no componente curricular.
	 * @param frequencia
	 * @throws DAOException 
	 */
	public void setNumeroFaltasPorFrequencia(double frequencia, double minutosAulaRegular) throws DAOException {
		numeroFaltas = getMaximoFaltas(frequencia,minutosAulaRegular);
	}

	/**
	 *  Seta a frequ�ncia atrav�s do n�mero de falta fornecido
	 * @param numeroFaltas
	 */
	public void setFrequenciaPorNumeroFaltas(int numeroFaltas) {
		try{
			int chTotal = getComponenteCHTotal();
			frequenciaImplantadaHistorico = (chTotal - numeroFaltas) * 100 / chTotal;;
		}catch (ArithmeticException e) {
		}
	}
	
	public void setPeriodo(Byte periodo) {
		this.periodo = periodo;
	}

	public void setRegistroAtividade(RegistroAtividade registroAtividade) {
		this.registroAtividade = registroAtividade;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public void setRematricula(Boolean rematricula) {
		this.rematricula = rematricula;
	}

	public void setRestricoes(RestricoesMatricula restricoes) {
		this.restricoes = restricoes;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setSexo(Character sexo) {
		this.sexo = sexo;
	}

	public void setSituacaoMatricula(SituacaoMatricula situacaoMatricula) {
		this.situacaoMatricula = situacaoMatricula;
	}

	public void setSolicitacaoTrancamento(SolicitacaoTrancamentoMatricula soliictacaoTrancamento) {
		this.solicitacaoTrancamento = soliictacaoTrancamento;
	}

	public void setTipoIntegralizacao(String tipoIntegralizacao) {
		this.tipoIntegralizacao = tipoIntegralizacao;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setUsuarioConsolidacao(Usuario usuarioConsolidacao) {
		this.usuarioConsolidacao = usuarioConsolidacao;
	}

	/** Seta os valores a serem retificados na matr�cula do componente. Os valores a retificar s�o mediaFinal, numeroFaltas,
	 * apto, id, situacaoMatricula, componente, detalhesComponente, ano, e periodo. 
	 * @param mat valores a retificar
	 */
	public void setValoresRetificacao(MatriculaComponente mat) {
		mediaFinal = mat.getMediaFinal();
		numeroFaltas = mat.getNumeroFaltas();
		apto = mat.getApto();
		id = mat.getId();
		situacaoMatricula = mat.getSituacaoMatricula();
		componente = mat.getComponente();
		detalhesComponente = mat.getDetalhesComponente();
		ano = mat.getAno();
		periodo = mat.getPeriodo();
	}

	/**
	 * Override do m�todo toString definido como: <br />
	 * <code>{id} {ano}.{periodo} {c�digo componente} - {nome componente}</code>
	 */
	@Override
	public String toString() {
		return getId() + " " + getAnoPeriodo() + " " + getComponenteCodigoNome();
	}

	/**
	 * Retorna a note tirada pelo aluno na recupera��o.
	 * @return
	 */
	public Double getRecuperacao() {	
		return recuperacao;
	}
	
	/** Retorna a s�rie, ano e turma.
	 * @return
	 */
	@Transient
	public String getSerieAnoTurma() {
		return ano + " - " + serie.getDescricaoCompleta() + " - " +
		"Turma " + getTurma().getCodigo()+ " - " + getTurma().getDisciplina().getNome();
	}
	
	/**
	 * Retorna a note tirada pelo aluno na recupera��o caso todas as unidades estiverem preenchidas.
	 * @return
	 */
	public Double getRecuperacaoPreenchida() {	
		if (!isConsolidada()) {
			for ( NotaUnidade nota : notas ){
				if ( nota.getNotaPreenchida() == null && !nota.isRecuperacao() )
					return null;
			}
		}	
		return recuperacao;
	}

	public void setRecuperacao(Double recuperacao) {
		this.recuperacao = recuperacao;
	}

	public PerfilUsuarioAva getPerfilDiscente() {
		return perfilDiscente;
	}

	public void setPerfilDiscente(PerfilUsuarioAva perfilDiscente) {
		this.perfilDiscente = perfilDiscente;
	}

	public SituacaoMatricula getNovaSituacaoMatricula() {
		return novaSituacaoMatricula;
	}

	public void setNovaSituacaoMatricula(SituacaoMatricula novaSituacaoMatricula) {
		this.novaSituacaoMatricula = novaSituacaoMatricula;
	}

	public Boolean getEsconder() {
		return esconder;
	}

	public void setEsconder(Boolean esconder) {
		this.esconder = esconder;
	}

	public Integer getPesoMedia() {
		return pesoMedia;
	}

	public void setPesoMedia(Integer pesoMedia) {
		this.pesoMedia = pesoMedia;
	}

	public Integer getPesoRecuperacao() {
		return pesoRecuperacao;
	}

	public void setPesoRecuperacao(Integer pesoRecuperacao) {
		this.pesoRecuperacao = pesoRecuperacao;
	}

	public Date getDataLimiteTrancamento() {
		return dataLimiteTrancamento;
	}

	public void setDataLimiteTrancamento(Date dataLimiteTrancamento) {
		this.dataLimiteTrancamento = dataLimiteTrancamento;
	}

	public void setOcultarNota(Boolean ocultarNotas) {
		this.ocultarNotas = ocultarNotas;
	}

	/**
	 * Oculta as notas se n�o tiver consolidada
	 * @return
	 */
	public Boolean getOcultarNotas() {	
		if ( isConsolidada() )
			return false;
			
		return ocultarNotas;
	}
	
	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	public String getCodigoSubturma() {
		return codigoSubturma;
	}

	public void setCodigoSubturma(String codigoSubturma) {
		this.codigoSubturma = codigoSubturma;
	}

	public void setOcultarNotas(Boolean ocultarNotas) {
		this.ocultarNotas = ocultarNotas;
	}

	public Integer getFrequenciaImplantadaHistorico() {
		return frequenciaImplantadaHistorico;
	}

	public void setFrequenciaImplantadaHistorico(
			Integer frequenciaImplantadaHistorico) {
		this.frequenciaImplantadaHistorico = frequenciaImplantadaHistorico;
	}

	public NotaDisciplina getNotaDisciplina() {
		return notaDisciplina;
	}

	public void setNotaDisciplina(NotaDisciplina notaDisciplina) {
		this.notaDisciplina = notaDisciplina;
	}

	public boolean isDependencia() {
		return dependencia;
	}

	public void setDependencia(boolean dependencia) {
		this.dependencia = dependencia;
	}

	public MetodologiaAvaliacao getMetodologiaAvaliacao() {
		return metodologiaAvaliacao;
	}

	public void setMetodologiaAvaliacao(MetodologiaAvaliacao metodologiaAvaliacao) {
		this.metodologiaAvaliacao = metodologiaAvaliacao;
	}

	public void setQtdNotas(int qtdNotas) {
		this.qtdNotas = qtdNotas;
	}

	public int getQtdNotas() {
		return qtdNotas;
	}
	
	public void setPorcentagemFrequencia(Double valor) {
		
	}
}