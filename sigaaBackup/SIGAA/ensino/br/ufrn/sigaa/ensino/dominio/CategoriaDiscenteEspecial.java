package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que representa uma categoria de discente especial. <br>
 * Exemplos: <br>
 * <li> Discentes especiais ordinários
 * <li> Discentes em mobilidade estudantil
 * <li> Discentes em complementação de estudos
 * 
 * 
 * @author Wendell
 *
 */
@Entity
@Table(name = "categoria_discente_especial", schema = "ensino")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CategoriaDiscenteEspecial implements Validatable {

	public CategoriaDiscenteEspecial() {
		super();
	}
	/** serialVersionUID */
	private static final long serialVersionUID = -7317981913916475760L;
	/** Constante que define a categoria de discente especial ORDINARIO. */
	public static final int ORDINARIO = 1;
	/** Constante que define a categoria de discente especial EM_MOBILIDADE. */
	public static final int EM_MOBILIDADE = 2;
	/** Constante que define a categoria de discente especial EM_COMPLEMENTACAO_ESTUDOS . */
	public static final int EM_COMPLEMENTACAO_ESTUDOS = 3;
	
	/** Chave primária. */
	@Id
	@Column(name = "id_categoria_discente_especial")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	/**Descrição da categoria discente especial*/
	private String descricao;
	/**Número Máximo de Disciplinas*/
	@Column(name = "max_disciplinas")
	private Integer maxDisciplinas;
	
	/**Número Máximo de Períodos*/
	@Column(name = "max_periodos")
	private Integer maxPeriodos;
	
	/** Atributo utilizado para informar se a categoria poderá realizar solicitação de re-matrícula. */
	@Column(name = "permite_rematricula", nullable = false, insertable = true, updatable = true)
	private boolean permiteRematricula = false;
	/** Atributo utilizado para informar se a categoria participa no processamento de matrícula. */
	@Column(name = "processa_matricula", nullable = false, insertable = true, updatable = true)
	private boolean processaMatricula = false;
	/** Atributo utilizado para informar se a categoria participa no processamento de re-matrícula. */
	@Column(name = "processa_rematricula", nullable = false, insertable = true, updatable = true)
	private boolean processaRematricula = false;
	/** Atributo que indica se o chefe de departamento poderá analisar as solicitações de matrícula dessa categoria de discente especial. */
	@Column(name = "chefe_analisa_solicitacao")
	private boolean chefeAnalisaSolicitacao = false;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

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
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista);
		ValidatorUtil.validateRequired(getMaxDisciplinas(), "Número Máximo de Disciplinas", lista);
		ValidatorUtil.validateRequired(getMaxPeriodos(), "Número Máximo de Períodos", lista);
		return lista;
	}

	public Integer getMaxDisciplinas() {
		return maxDisciplinas;
	}

	public void setMaxDisciplinas(Integer maxDisciplinas) {
		this.maxDisciplinas = maxDisciplinas;
	}

	public Integer getMaxPeriodos() {
		return maxPeriodos;
	}

	public void setMaxPeriodos(Integer maxPeriodos) {
		this.maxPeriodos = maxPeriodos;
	}

	public boolean isPermiteRematricula() {
		return permiteRematricula;
	}

	public void setPermiteRematricula(boolean permiteRematricula) {
		this.permiteRematricula = permiteRematricula;
	}

	public boolean isProcessaMatricula() {
		return processaMatricula;
	}

	public void setProcessaMatricula(boolean processaMatricula) {
		this.processaMatricula = processaMatricula;
	}

	public boolean isProcessaRematricula() {
		return processaRematricula;
	}

	public void setProcessaRematricula(boolean processaRematricula) {
		this.processaRematricula = processaRematricula;
	}

	public boolean isChefeAnalisaSolicitacao() {
		return chefeAnalisaSolicitacao;
	}

	public void setChefeAnalisaSolicitacao(boolean chefeAnalisaSolicitacao) {
		this.chefeAnalisaSolicitacao = chefeAnalisaSolicitacao;
	}

}
