/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 15/01/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.StringUtils;

/**
 * Esta entidade serve para mudar os detalhes da disciplina e n�o perder a
 * informa��o para fins de hist�rico. Uma disciplina quando � alterada, um novo
 * detalhe � criado, mas, o anterior permanece para sair no hist�rico as
 * disciplinas com os detalhes de quando o aluno a cursou.
 *
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "componente_curricular_detalhes", schema = "ensino", uniqueConstraints = { @UniqueConstraint(columnNames = { "codigo" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ComponenteDetalhes implements PersistDB, Cloneable {
   
	/** Id dos detalhes do componente */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_componente_detalhes", nullable = false)
	private int id;
	
	/** Componente curricular ao qual os detalhes se referem */
	@Column(name = "id_componente")
	private int componente;
	
	/** Data de cria��o */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	private Date data;
	
	/** Quem criou o registro */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;
	
    /** Nome do componente curricular */
	private String nome;
	
	/** Nome no formato ASCII */
	private String nome_ascii;
	
	/** Carga hor�ria de aulas para o componente curricular */
	@Column(name = "ch_aula")
	private int chAula; // ou CH te�rica
	
	/** Carga hor�ria de aulas para o componente curricular */
	@Column(name = "ch_nao_aula")
	private int chNaoAula; // ou CH te�rica
	
    /** Carga hor�rio de laborat�rio para o componente curricular */
	@Column(name = "ch_laboratorio")
	private int chLaboratorio; // ou CH pr�tica

	/**
	 * Carga hor�ria de est�gio para o componente curricular
	 * N�o existe carga hor�ria de est�gio. Na view esse campo � exibido caso tenha vindo
	 * migrado PontoA
	 */
	@Column(name = "ch_estagio")
	private int chEstagio;

	/** Carga hor�ria de aula ministrada � dist�ncia. Geralmente, corresponde no m�ximo a 20% da ch total. */
	@Column(name = "ch_ead")
	private int chEad;
	
	/** Total de cr�ditos de aula */
	@Column(name = "cr_aula")
	private int crAula;
	
    /** Total de cr�ditos de laborat�rio */
	@Column(name = "cr_laboratorio")
	private int crLaboratorio;
	
    /** Total de cr�ditos de est�gio */
	@Column(name = "cr_estagio")
	private int crEstagio;
	
	/** Total de cr�ditos de aula � dist�ncia */
	@Column(name = "cr_ead")
	private int crEad;
	
	/** Atributo transiente contendo a soma total dos cr�ditos do componente. */
	@Transient
	private int crTotal;
    
	/** Carga hor�ria total do componente curricular (somando aulas, laborat�rio e est�gio) */
	@Column(name = "ch_total")
	private int chTotal;

	/** Carga hor�ria dedicada do docente na atividade, utilizada em est�gio 
	 * pois o acompanhamento do orientador das atividades n�o � feito no tempo integral da atividade. */
	@Column(name = "ch_dedicada_docente")
	private int chDedicadaDocente;
	
	/** C�digo de identifica��o do componente curricular */
	private String codigo;

	/** Para disciplina � ementa, para atividade � descri��o */
	private String ementa;
	
	/** Pr�-requisito para cursar a disciplina */
	@Column(name = "pre_requisito")
	private String preRequisito;
	
    /** Co-requisito para cursar a disciplina */
	@Column(name = "co_requisito")
	private String coRequisito;
	
    /** Descri��o com disciplinas que s�o equivalentes */
	private String equivalencia;
	
    /** Tipo do componente */
	@Column(name = "id_tipo_componente")
	private int tipoComponente;
	
	/** Define se aceita cadastrar subturmas */
	@Column(name = "aceita_subturma")
	//@Transient
	private boolean aceitaSubturma = false;
	
	/** Atributo que permite ativar/inativar a equival�ncia de um DetalheComponente para que ele n�o seja considerado 
	 *  quando calculamos a equival�ncia no hist�rico do aluno, que agora considera n�o s� o 
	 *  atual mas todos os que existiram durante o v�nculo do aluno. */
	@Column(name="desconsiderar_equivalencia")
	private boolean desconsiderarEquivalencia = false;
	
	/** Data de inativa��o da equival�ncia */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="equivalencia_valida_ate")
	private Date equivalenciaValidaAte;
	
	/** Este atributo diz se o componente pode ser aproveitado ou n�o.*/
	@Column(name="proibe_aproveitamento")
	private boolean proibeAproveitamento = false;	
	
	/** Este atributo diz se � poss�vel criar uma turma para a atividade*/
	@Column(name="atividade_aceita_turma")
	private boolean atividadeAceitaTurma = false;
	
	public ComponenteDetalhes() {
	}

	public ComponenteDetalhes(int id) {
		this.id = id;
	}
	
	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nome
	 * @param equivalencia
	 * @param coRequisito
	 * @param preRequisito
	 */
	public ComponenteDetalhes(int id, String codigo, String nome, String equivalencia, String coRequisito, String preRequisito) {
		setId(id);
		setNome(nome);
		setCodigo(codigo);
		setEquivalencia(equivalencia);
		setCoRequisito(coRequisito);
		setPreRequisito(preRequisito);
	}

	public String getCoRequisito() {
		return coRequisito;
	}

	public void setCoRequisito(String coRequisito) {
		this.coRequisito = coRequisito;
	}

	public String getEquivalencia() {
		return equivalencia;
	}

	public void setEquivalencia(String equivalencia) {
		this.equivalencia = equivalencia;
	}

	public String getPreRequisito() {
		return preRequisito;
	}

	public void setPreRequisito(String preRequisito) {
		this.preRequisito = preRequisito;
	}

	/** M�todo respons�vel por retornar o valor total de cr�ditos, sendo a soma dos cr�ditos de Aula, 
	 * Laborat�rio e Est�gio do componente curricular.*/
	@Transient
	public int getCrTotal() {
		if (crTotal == 0)
			return crAula + crLaboratorio + crEstagio + crEad;
		else
			return crTotal;
	}

	public int getCrAula() {
		return crAula;
	}

	public void setCrAula(int crAula) {
		this.crAula = crAula;
	}

	public int getCrEstagio() {
		return crEstagio;
	}

	public void setCrEstagio(int crEstagio) {
		this.crEstagio = crEstagio;
	}

	public int getCrLaboratorio() {
		return crLaboratorio;
	}

	public void setCrLaboratorio(int crLaboratorio) {
		this.crLaboratorio = crLaboratorio;
	}

	public void calcularCHTotal() {
		chTotal = chAula + chLaboratorio + chEstagio + chEad  + chNaoAula;
	}

	public int getChTotal() {
		if (chTotal == 0)
			chTotal = chAula + chLaboratorio + chEstagio + chEad + chNaoAula;
		return chTotal;
	}
	
	public int getChTotalAula() {
		return chAula + chLaboratorio + chEad;
	}

	public void setChTotal(int chTotal) {
		this.chTotal = chTotal;
	}

	public int getChAula() {
		return chAula;
	}

	public void setChAula(int chAula) {
		this.chAula = chAula;
	}

	public int getChEstagio() {
		return chEstagio;
	}

	public void setChEstagio(int chEstagio) {
		this.chEstagio = chEstagio;
	}

	public int getChLaboratorio() {
		return chLaboratorio;
	}

	public void setChLaboratorio(int chLaboratorio) {
		this.chLaboratorio = chLaboratorio;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getEmenta() {
		return ementa;
	}

	public void setEmenta(String ementa) {
		this.ementa = ementa;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		if (nome != null)
			nome_ascii = StringUtils.toAscii(nome);
		this.nome = nome;
	}

	public int getComponente() {
		return componente;
	}

	public void setComponente(int componente) {
		this.componente = componente;
	}

	@Override
	public ComponenteDetalhes clone() throws CloneNotSupportedException {
		return (ComponenteDetalhes) super.clone();
	}

	/**
	 * Verifica se algum desses atributos foram modificados. Usado para quando for
	 * atualizar um obj.
	 *
	 * @param obj
	 * @return
	 */
	public boolean equalsToUpdate(ComponenteDetalhes obj) {
		return EqualsUtil.testEquals(this, obj, "id", "nome", "codigo",
				"ementa", "chAula", "chLaboratorio", "chEstagio", "chEad", "chNaoAula",
				"preRequisito", "coRequisito", "equivalencia", "tipoComponente", "aceitaSubturma", "permiteChCompartilhada");
	}

	/**
	 * Verifica se algum desses atributos foram modificados. Usado para quando for
	 * atualizar um Componente para verificar se os c�lculos do curr�culo devem ser refeitos
	 * @param obj
	 * @return
	 */
	public boolean equalsToCalculoCurriculo( ComponenteDetalhes obj ){
		return EqualsUtil.testEquals(this, obj, "chAula", "chLaboratorio", "chEstagio", "chTotal",
				"preRequisito", "coRequisito", "equivalencia", "tipoComponente");
	}
	
	/**
	 * S� para atividades
	 *
	 * @return
	 */
	@Transient
	public String getDescricaoAtividade() {
		return ementa;
	}

	public void setCrTotal(int crTotal) {
		this.crTotal = crTotal;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public String getNome_ascii() {
		return nome_ascii;
	}

	public void setNome_ascii(String nome_ascii) {
		this.nome_ascii = nome_ascii;
	}

	public int getTipoComponente() {
		return tipoComponente;
	}

	public void setTipoComponente(int tipoComponente) {
		this.tipoComponente = tipoComponente;
	}

	public boolean isAceitaSubturma() {
		return aceitaSubturma;
	}

	public void setAceitaSubturma(boolean aceitaSubturma) {
		this.aceitaSubturma = aceitaSubturma;
	}

	public int getChEad() {
		return chEad;
	}

	public void setChEad(int chEad) {
		this.chEad = chEad;
	}

	public int getCrEad() {
		return crEad;
	}

	public void setCrEad(int crEad) {
		this.crEad = crEad;
	}

	public boolean isDesconsiderarEquivalencia() {
		return desconsiderarEquivalencia;
	}

	public void setDesconsiderarEquivalencia(boolean desconsiderarEquivalencia) {
		this.desconsiderarEquivalencia = desconsiderarEquivalencia;
	}

	public int getChDedicadaDocente() {
		return chDedicadaDocente;
	}

	public void setChDedicadaDocente(int chDedicadaDocente) {
		this.chDedicadaDocente = chDedicadaDocente;
	}

	public boolean isPermiteChCompartilhada() {
		return false;
	}

	public boolean isProibeAproveitamento() {
		return proibeAproveitamento;
	}

	public void setProibeAproveitamento(boolean proibeAproveitamento) {
		this.proibeAproveitamento = proibeAproveitamento;
	}

	public boolean isAtividadeAceitaTurma() {
		return atividadeAceitaTurma;
	}

	public void setAtividadeAceitaTurma(boolean atividadeAceitaTurma) {
		this.atividadeAceitaTurma = atividadeAceitaTurma;
	}

	public Date getEquivalenciaValidaAte() {
		return equivalenciaValidaAte;
	}

	public void setEquivalenciaValidaAte(Date equivalenciaValidaAte) {
		this.equivalenciaValidaAte = equivalenciaValidaAte;
	}

	public int getChNaoAula() {
		return chNaoAula;
	}

	public void setChNaoAula(int chNaoAula) {
		this.chNaoAula = chNaoAula;
	}
	
	
	
}