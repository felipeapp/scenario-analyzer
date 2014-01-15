/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 10/01/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe que representa o tipo de componente curricular : disciplina, atividade, estagio
 *
 * @author Andr�
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "tipo_componente_curricular", schema = "ensino")
@Cache ( usage = CacheConcurrencyStrategy.READ_ONLY )
public class TipoComponenteCurricular implements PersistDB {

	/** Indica um componente do tipo Atividade. */
	public static final int ATIVIDADE = 1;

	/** Indica um componente do tipo Disciplina. */
	public static final int DISCIPLINA = 2;

	/** Indica que o componente � do tipo M�dulo. */
	public static final int MODULO = 3;

	/** Indica que o componente � ofertado em bloco. */
	public static final int BLOCO = 4;
	/**
	 * Chave prim�ria do {@link TipoComponenteCurricular}.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_disciplina", nullable = false)
	private int id;

	/** Descri��o do {@link TipoComponenteCurricular}. */
	private String descricao;
	
	/**
	 * Indica qual a porcentagem m�xima de carda hor�ria de aulas � dist�ncia o componente pode ter,
	 * em rela��o � carga hor�ria total.
	 */
	@Column (name = "cr_ead_max"/*"porcentagem_max_ch_ead"*/, nullable = false)
	private int chEadMax;
	
	/** Indica se o componente � ativo ou n�o */
	@CampoAtivo(true)
	private boolean ativo = true;

	public TipoComponenteCurricular() {
	}
	public TipoComponenteCurricular(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = (id == null)?0:id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getChEadMax() {
		return chEadMax;
	}
	public void setChEadMax(int chEadMax) {
		this.chEadMax = chEadMax;
	}

	/**
	 * Override do m�todo toString. mostra a descri��o do tipo, caso haja, ou uma String vazia, caso contr�rio.
	 */
	@Override
	public String toString() {
		return descricao == null ? "" : descricao;
	}

	/**
	 * Retorna os tipos que n�o s�o atividades.
	 * @return
	 */
	public static Collection<TipoComponenteCurricular> getNaoAtividades() {
		List<TipoComponenteCurricular> tipos = new ArrayList<TipoComponenteCurricular>();
		tipos.add( new TipoComponenteCurricular( TipoComponenteCurricular.DISCIPLINA) );
		tipos.add( new TipoComponenteCurricular( TipoComponenteCurricular.BLOCO) );
		tipos.add( new TipoComponenteCurricular( TipoComponenteCurricular.MODULO) );
		return tipos;
	}
	
	/**
	 * Retorna os tipos referentes a atividades.
	 * @return
	 */
	public static Collection<TipoComponenteCurricular> getAtividades() {
		List<TipoComponenteCurricular> tipos = new ArrayList<TipoComponenteCurricular>();
		tipos.add( new TipoComponenteCurricular( TipoComponenteCurricular.ATIVIDADE) );		
		return tipos;
	}

	/**
	 * Retorna todos os tipos cadastrados.
	 * @return
	 */
	public static Collection<TipoComponenteCurricular> getAll() {
		
		List<TipoComponenteCurricular> resultado = new ArrayList<TipoComponenteCurricular>();
		
		resultado.addAll(getAtividades());
		resultado.addAll(getNaoAtividades());
		
		return resultado;
	}
	
	/**
	 * Override do hashCode.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	
	/**
	 * implementa��o do equals levando em considera��o o id dos objetos.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoComponenteCurricular other = (TipoComponenteCurricular) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public boolean isAtivo() {
		return ativo;
	}
	
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}
