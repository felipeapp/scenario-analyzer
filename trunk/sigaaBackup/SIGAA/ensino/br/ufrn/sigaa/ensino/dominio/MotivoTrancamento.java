/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Jul 3, 2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Motivos de trancamento pr�-definidos para utiliza��o na solicita��o 
 * de trancamento de matr�cula em componente curricular 
 *
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "motivo_trancamento", schema = "ensino", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class MotivoTrancamento implements PersistDB {

	/**
	 * esta constante deve estar sincronizada com o id da op��o OUTROS da tabela.
	 */
	public static final int OUTROS = 6;
	public static final int TRANCOU_COREQUISITO = 7;
	public static final int TRANCOU_BLOCO = 9;

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_motivo_trancamento", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	/**
	 * informa se esta op��o deve ser exibida no SELECT nas JSPs
	 */
	private boolean exibir;


	public MotivoTrancamento() {
		super();
	}

	public MotivoTrancamento(int id) {
		this.id = id;
	}

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

	public boolean isExibir() {
		return exibir;
	}

	public void setExibir(boolean exibir) {
		this.exibir = exibir;
	}

	@Override
	public String toString() {
		return descricao;
	}
}
