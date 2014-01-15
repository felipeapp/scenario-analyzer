/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.biblioteca.ConsultasEmprestimoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.parametros.dominio.ParametrosFormaIngresso;

/**
 * Entidade que representa as formas de ingresso de um discente na instituição, 
 * definido por nível de ensino.
 */
@Entity
@Table(name = "forma_ingresso", schema = "ensino", uniqueConstraints = {})
@Cache ( usage = CacheConcurrencyStrategy.READ_WRITE )
public class FormaIngresso implements ParametrosFormaIngresso, Validatable {


	/** Constante que define a forma de ingresso REINGRESSO. */
	public static final List<Integer> REINGRESSO = Arrays.asList(34131, 34132);

	/** Constante que define a forma de ingresso ALUNO_ESPECIAL. */
	public static final FormaIngresso ALUNO_ESPECIAL = new FormaIngresso(34116);

	/** Constante que define a forma de ingresso VESTIBULAR. */
	public static final FormaIngresso VESTIBULAR = new FormaIngresso(34110);

	/** Constante que define a forma de ingresso SELECAO_POS_GRADUACAO. */
	public static final FormaIngresso SELECAO_POS_GRADUACAO = new FormaIngresso(994409);

	/** Constante que define a forma de ingresso MOBILIDADE_ESTUDANTIL. */
	public static final FormaIngresso MOBILIDADE_ESTUDANTIL = new FormaIngresso(34130);
	
	/** Constante que define a forma de ingresso REINGRESSO AUTOMÁTICO */
	public static final FormaIngresso REINGRESSO_AUTOMATICO = new FormaIngresso(34131);
	
	/** Constante que define a forma de ingresso Transferência Voluntária. */
	public static final FormaIngresso TRANSFERENCIA_VOLUNTARIA = new FormaIngresso(34109);
	
	/** Constante que define a forma de ingresso Processo Seletivo Técnico, Lato-Sensu. */
	public static final FormaIngresso PROCESSO_SELETIVO = new FormaIngresso(37350);
	
	/** Constante que define a forma de ingresso ESTUDOS COMPLEMENTARES. */
	public static final FormaIngresso ESTUDOS_COMPLEMENTARES = new FormaIngresso(1697747);
	
	/** Constante que define a  forma de ingresso PORTADOR DE DIPLOMA. */
	public static final FormaIngresso PORTADOR_DIPLOMA  = new FormaIngresso(1697747);
	
	/** Constante que define a forma de ingresso ALUNO_ESPECIAL DE PÓS GRADUAÇÃO. */
	public static final FormaIngresso ALUNO_ESPECIAL_POS = new FormaIngresso(994410);

	/** Constante que define a forma de ingresso "CONVENIO PEC-G". */
	public static final FormaIngresso ALUNO_PEC_G = new FormaIngresso(34117);
	
	public static final FormaIngresso SISU = new FormaIngresso(51252808);
	
	
	/** Constante que define o Tipo da forma ingresso " ALUNO ESPECIAL" */
	public static final Character TIPO_ALUNO_ESPECIAL = 'E';
	
	/** Constante que define o Tipo da forma ingresso " ALUNO REGULAR" */
	public static final Character TIPO_ALUNO_REGULAR = 'R';
	
	// Fields

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_forma_ingresso", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Descrição da forma de ingresso. */
	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	private String descricao;

	/** Nível de ensino específico da forma de ingresso. */
	@Column(name = "nivel")
	private Character nivel;

	/** Tipo da forma de ingresso (Regular, Especial). */
	@Column(name = "tipo_forma")
	private Character tipo;

	/** Categoria de discente especial relacionado com a forma de ingresso */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_categoria_discente_especial")
	private CategoriaDiscenteEspecial categoriaDiscenteEspecial;
	
	/** Indica se a forma de ingresso é ativa. */
	@Column(name = "ativo")
	private Boolean ativo;
	
	/** Indica se o modelo de diploma impresso do aluno será o de convênio. */
	@Column(name = "diploma_convenio")
	private Boolean diplomaConvenio;
	
	/** Indica se a forma de ingresso se dá por Processo Seletivo. */
	@Column(name = "realiza_processo_seletivo")
	private Boolean realizaProcessoSeletivo;
	
	/** Indica a forma de ingresso se foi Mobilidade Estudantil. */
	@Column(name="mobilidade_estudantil")
	private Boolean tipoMobilidadeEstudantil;
	
	/** Indica a forma de ingresso considera para a taxa de conclusão. */
	@Column(name="contagem_taxa_conclusao")
	private Boolean contagemTaxaConclusao;	

	/**
	 * Para alunos não regulares, diz se a forma de ingresso permite que o aluno faça empréstimos na biblioteca. 
	 * 
	 * @see ConsultasEmprestimoDao#findDiscentesBibliotecaByPessoa(int)
	 */
	@Column(name="permite_emprestimo_biblioteca", nullable=false)
	private boolean permiteEmprestimoBiblioteca;
	
	
	// Constructors

	/** Construtor padrão. */
	public FormaIngresso() {
	}

	/** Construtor parametrizado.
	 * @param id
	 */
	public FormaIngresso(int id) {
		this.id = id;
	}

	/** Construtor parametrizado. 
	 * @param idFormaIngresso
	 * @param descricao
	 */
	public FormaIngresso(int idFormaIngresso, String descricao) {
		this.id = idFormaIngresso;
		this.descricao = descricao;
	}

	/** Construtor parametrizado. 
	 * 
	 * @param idFormaIngresso
	 * @param descricao
	 * @param discenteTecnicos
	 * @param processoSeletivos
	 */
	public FormaIngresso(int idFormaIngresso, String descricao,
			Set<DiscenteTecnico> discenteTecnicos,
			Set<ProcessoSeletivo> processoSeletivos) {
		this.id = idFormaIngresso;
		this.descricao = descricao;
	}

	// Property accessors
	/** Retorna a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return this.id;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int idFormaIngresso) {
		this.id = idFormaIngresso;
	}

	/** Retorna a descrição da forma de ingresso. 
	 * @return
	 */
	public String getDescricao() {
		return this.descricao;
	}

	/** Seta a descrição da forma de ingresso.
	 * @param descricao
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Valida os dados: descrição.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(), "Descricao", lista);
		ValidatorUtil.validateRequiredId(getNivel(), "Nível", lista);
		ValidatorUtil.validateRequiredId(getTipo(), "Tipo", lista);
		if(getTipo().equals(TIPO_ALUNO_ESPECIAL))
			ValidatorUtil.validateRequiredId(getCategoriaDiscenteEspecial().getId(), "Categoria de Discente Especial", lista);
		return lista;
	}

	/** Indica se a forma de ingresso é ativa. 
	 * @return
	 */
	public Boolean getAtivo() {
		return ativo;
	}

	/** Seta se a forma de ingresso é ativa. 
	 * @param ativo
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna o nível de ensino específico da forma de ingresso. 
	 * @return
	 */
	public Character getNivel() {
		return nivel;
	}

	/** Indica o nível de ensino específico da forma de ingresso.
	 * @param nivel
	 */
	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	/** Retorna o tipo da forma de ingresso (Regular, Especial). 
	 * @return
	 */
	public Character getTipo() {
		return tipo;
	}

	/** Seta o tipo da forma de ingresso (Regular, Especial).
	 * @param tipo
	 */
	public void setTipo(Character tipo) {
		this.tipo = tipo;
	}

	/** Indica se a forma de ingresso é do tipo Reingresso.
	 * @return
	 */
	@Transient
	public boolean isReIngresso() {
		return REINGRESSO.contains(id);
	}
	
	/** Indica se o tipo de ingresso é Aluno Especial.
	 * @return
	 */
	@Transient
	public boolean isAlunoEspecial() {
		return ALUNO_ESPECIAL.getId() == id || ALUNO_ESPECIAL_POS.getId() == id;
	}
	
	/** Indica se o tipo de ingresso é Estudos Complementares.
	 * @return
	 */
	@Transient
	public boolean isEstudosComplementares() {
		return ESTUDOS_COMPLEMENTARES.getId() == id;
	}
	
	/** Indica se a forma de ingresso é do tipo Mobilidade Estudantil.
	 * @return
	 */
	@Transient
	public boolean isMobilidadeEstudantil() {
		return getTipoMobilidadeEstudantil() != null && getTipoMobilidadeEstudantil();
	}

	/** Indica se o modelo de diploma impresso do aluno será o de convênio. 
	 * @return
	 */
	public Boolean getDiplomaConvenio() {
		return diplomaConvenio;
	}

	/** Seta se o modelo de diploma impresso do aluno será o de convênio. 
	 * @param diplomaConvenio
	 */
	public void setDiplomaConvenio(Boolean diplomaConvenio) {
		this.diplomaConvenio = diplomaConvenio;
	}

	/** Indica se a forma de ingresso se dá por Processo Seletivo. 
	 * @return
	 */
	public Boolean getRealizaProcessoSeletivo() {
		return realizaProcessoSeletivo;
	}

	/** Seta se a forma de ingresso se dá por Processo Seletivo. 
	 * @param realizaProcessoSeletivo
	 */
	public void setRealizaProcessoSeletivo(Boolean realizaProcessoSeletivo) {
		this.realizaProcessoSeletivo = realizaProcessoSeletivo;
	}

	/** Retorna uma representação textual desta forma de ingresso.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricao();
	}

	/** 
	 *  Retorna o tipo de Mobilidade Estudantil
	 */
	public Boolean getTipoMobilidadeEstudantil() {
		return tipoMobilidadeEstudantil;
	}

	/**
	 * Seta o tipo de Mobilidade Estudantil.
	 * @param tipoMobilidadeEstudantil
	 */
	public void setTipoMobilidadeEstudantil(Boolean tipoMobilidadeEstudantil) {
		this.tipoMobilidadeEstudantil = tipoMobilidadeEstudantil;
	}

	public Boolean getContagemTaxaConclusao() {
		return contagemTaxaConclusao;
	}

	public void setContagemTaxaConclusao(Boolean contagemTaxaConclusao) {
		this.contagemTaxaConclusao = contagemTaxaConclusao;
	}

	public boolean isPermiteEmprestimoBiblioteca() {
		return permiteEmprestimoBiblioteca;
	}

	public void setPermiteEmprestimoBiblioteca(boolean permiteEmprestimoBiblioteca) {
		this.permiteEmprestimoBiblioteca = permiteEmprestimoBiblioteca;
	}

	public CategoriaDiscenteEspecial getCategoriaDiscenteEspecial() {
		return categoriaDiscenteEspecial;
	}

	public void setCategoriaDiscenteEspecial(CategoriaDiscenteEspecial categoriaDiscenteEspecial) {
		this.categoriaDiscenteEspecial = categoriaDiscenteEspecial;
	}
	
	/** Retorna uma coleção de forma de ingresso que indicam os alunos ingressantes.
	 * @return
	 */
	public static Collection<Integer> getFormaIngressoAlunoIngressante() {
		Collection<Integer> formaIngresso = new ArrayList<Integer>(0);
		formaIngresso.add(VESTIBULAR.getId());
		formaIngresso.add(SISU.getId());
		return formaIngresso;
	}
	
}