/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Tipo de atividade acadêmica complementar
 */
@Entity
@Table(name = "tipo_atividade", schema = "ensino", uniqueConstraints = {})
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class TipoAtividade implements Validatable {

	public static final int ESTAGIO = 1;
	public static final int COMPLEMENTAR = 2;
	public static final int TRABALHO_CONCLUSAO_CURSO = 3;
	public static final int TESE = 4;
	public static final int QUALIFICACAO = 5;
	public static final int PROFICIENCIA = 6;
	public static final int COMPLEMENTAR_STRICTO = 7;
	public static final int ATIVIDADE_HOSPITALAR = 8;
	public static final int ESTAGIO_TECNICO = 10;
	public static final int COMPLEMENTAR_TECNICO = 11;
	public static final int TRABALHO_CONCLUSAO_CURSO_TECNICO = 12;
	public static final int ATIVIDADE_FORMACAO_COMPLEMENTAR = 16;


	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_atividade", nullable = false)
	private int id;

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 40)
	private String descricao;

	@Column(name="nivel")
	private Character nivelEnsino;

	// Constructors
	/** default constructor */
	public TipoAtividade() {
	}

	/** minimal constructor */
	public TipoAtividade(int idTipoAtividade) {
		this.id = idTipoAtividade;
	}

	/** full constructor */
	public TipoAtividade(int idTipoAtividade, String descricao) {
		this.id = idTipoAtividade;
		this.descricao = descricao;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int idTipoAtividade) {
		this.id = idTipoAtividade;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Character getNivelEnsino() {
		return nivelEnsino;
	}
	
	public void setNivelEnsino(Character nivelEnsino) {
		this.nivelEnsino = nivelEnsino;
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
	public String toString() {
		return descricao;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista);
		return lista;
	}


	@Transient
	public boolean isGraduacao() {
		return nivelEnsino == NivelEnsino.GRADUACAO;
	}
	
	@Transient
	public boolean isTecnico() {
		return nivelEnsino == NivelEnsino.TECNICO;
	}

	@Transient
	public boolean isStricto() {
		return NivelEnsino.isAlgumNivelStricto(getNivelEnsino());
	}
	
	@Transient
	public boolean isOrientacaoIndividual() {
		// do ProcessadorComponenteCurricular:
//		cc.getTipoComponente() != null && cc.getTipoComponente().getId() == TipoComponenteCurricular.ATIVIDADE
//				&& cc.getTipoAtividade() != null 
//				&& (cc.getTipoAtividade().getId() == TipoAtividade.ESTAGIO || cc.getTipoAtividade().getId() == TipoAtividade.TRABALHO_CONCLUSAO_CURSO)
		switch (id) {
			case ESTAGIO:
			case TRABALHO_CONCLUSAO_CURSO:
			case TESE:
			case QUALIFICACAO:
			case ESTAGIO_TECNICO:
			case TRABALHO_CONCLUSAO_CURSO_TECNICO:
			case ATIVIDADE_FORMACAO_COMPLEMENTAR:
			return true;
		default:
			return false;
		}
	}

}