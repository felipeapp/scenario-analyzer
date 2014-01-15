/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 28/05/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade que armazena o tipo de movimentação (ações realizadas sobre a matrícula) que o aluno realiza.
 * Exemplos: Conclusão de curso, entrada em curso, jubilamento.
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_movimentacao_aluno", schema = "ensino", uniqueConstraints = {})
public class TipoMovimentacaoAluno implements Validatable, ConstantesTipoMovimentacaoAluno {

	/**
	 * chave primária
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_tipo_movimentacao_aluno", nullable = false)
	private int id;

	/** decrição do tipo de movimentação */
	private String descricao;

	/** Grupo que o tipo de movimentação pertence */
	private String grupo;

	/** Indica se está ativo ou não */
	private boolean ativo;

	/** Status do discente que está vinculado ao tipo */
	private Integer statusDiscente;

	/** Indica que será visível para todos os níveis */
	private boolean todos;

	/** Indica que será visível apenas para graduação */
	private boolean graduacao;

	/** Indica que será visível apenas para stricto */
	private boolean stricto;

	/** Indica que será visível apenas para técnico */
	private boolean tecnico;
	
	/** Indica que será visível apenas para médio */
	private boolean medio;
	
	/** Indica que este tipo de movimentação tem como ano-período de referência o mesmo ano-período do calendário atual. */
	@Column(name="mesmo_ano_periodo")
	private boolean mesmoAnoPeriodo;

	// Constructors

	/** default constructor */
	public TipoMovimentacaoAluno() { }

	/** default minimal constructor */
	public TipoMovimentacaoAluno(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public TipoMovimentacaoAluno(int idTipoAfastamentoAluno, String descricao) {
		this.id = idTipoAfastamentoAluno;
		this.descricao = descricao;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int idTipoAfastamentoAluno) {
		this.id = idTipoAfastamentoAluno;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getStatusDiscente() {
		return statusDiscente;
	}

	public void setStatusDiscente(Integer statusDiscente) {
		this.statusDiscente = statusDiscente;
	}

	/**
	 *  Indica se este objeto é igual ao passado por parâmetro, comparando se possuem a mesma chave primária. 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/**
	 * Retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/**
	 * Valida os atributos do objeto.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		return null;
	}

	/**
	 * Se o discente possui movimentação do tipo Permanente
	 * @return
	 */
	@Transient
	public boolean isTemporario() {
		return grupo.equals(GrupoMovimentacaoAluno.AFASTAMENTO_TEMPORARIO);
	}
	
	/**
	 * Se o discente possui movimentação do tipo Permanente
	 * @return
	 */
	@Transient
	public boolean isPermanente() {
		return grupo.equals(GrupoMovimentacaoAluno.AFASTAMENTO_PERMANENTE);
	}

	/**
	 * Se o discente possui movimentação do tipo Permanante ou Temporária
	 * @return
	 */
	@Transient
	public boolean isAfastamento() {
		return grupo.equals(GrupoMovimentacaoAluno.AFASTAMENTO_PERMANENTE) || grupo.equals(GrupoMovimentacaoAluno.AFASTAMENTO_TEMPORARIO);
	}
	
	@Transient
	public boolean isAbandono() {
		return id == ABANDONO || id == ABANDONO_NENHUMA_MATRICULA || id == ABANDONO_NENHUMA_INTEGRALIZACAO;
	}
	
	public boolean isTodos() {
		return todos;
	}

	public void setTodos(boolean todos) {
		this.todos = todos;
	}

	public boolean isGraduacao() {
		return graduacao;
	}

	public void setGraduacao(boolean graduacao) {
		this.graduacao = graduacao;
	}

	public boolean isStricto() {
		return stricto;
	}

	public void setStricto(boolean stricto) {
		this.stricto = stricto;
	}

	public boolean isTecnico() {
		return tecnico;
	}

	public void setTecnico(boolean tecnico) {
		this.tecnico = tecnico;
	}

	public boolean isMedio() {
		return medio;
	}

	public void setMedio(boolean medio) {
		this.medio = medio;
	}

	public boolean isMesmoAnoPeriodo() {
		return mesmoAnoPeriodo;
	}

	public void setMesmoAnoPeriodo(boolean mesmoAnoPeriodo) {
		this.mesmoAnoPeriodo = mesmoAnoPeriodo;
	}
}
