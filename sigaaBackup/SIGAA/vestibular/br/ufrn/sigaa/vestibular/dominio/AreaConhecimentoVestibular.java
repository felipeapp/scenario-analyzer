/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/07/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Modela as �reas de conhecimento do Vestibular. Os cursos ofertados s�o
 * agrupados em �reas de conhecimentos (Tecnol�gica, Humanas, Biom�dica, etc.),
 * onde cada uma possui um conjunto de provas espec�ficas (biom�dica, por
 * exemplo, tem provas de Biologia, Qu�mica, F�sica e Reda��o).
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "area_conhecimento_vestibular", schema = "vestibular", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AreaConhecimentoVestibular implements PersistDB, Validatable {
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_area_conhecimento_vestibular", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Descri��o da �rea. */
	private String descricao;
	
	/** Indica se o uso desta �rea em cadastros � ativo. */
	private boolean ativo = true;

	
	/** Construtor padr�o. */
	public AreaConhecimentoVestibular() {
		super();
	}

	/** Construtor parametrizado.
	 * @param id
	 */
	public AreaConhecimentoVestibular(int id) {
		super();
		this.id = id;
	}

	/** Indica se o uso desta �rea em cadastros � ativo. 
	 * @return True, se � permitido o uso. False, caso contr�rio.
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se o uso desta �rea em cadastros � ativo. 
	 * @param ativo True, se � permitido o uso. False, caso contr�rio.
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna a descri��o da �rea. 
	 * @return Descri��o da �rea. 
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descri��o da �rea. 
	 * @param descricao Descri��o da �rea. 
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Valida os dados: a descri��o da �rea n�o pode ser nula.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(),
				"Descri��o da �rea de Conhecimento", lista);
		return lista;
	}
	
	/** Retorna uma string representando a �rea de conhecimento do vestibular no formato:
	 * ID, seguido de v�rgula, seguido da descri��o, seguido de v�rgula, seguido de ativo.
	 */
	@Override
	public String toString() {
		return getId() + ", " + getDescricao() + ", "
				+ (isAtivo() ? "ATIVO" : "INATIVO");
	}
}
