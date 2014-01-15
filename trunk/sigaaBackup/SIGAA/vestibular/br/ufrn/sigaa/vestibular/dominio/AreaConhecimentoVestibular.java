/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Modela as áreas de conhecimento do Vestibular. Os cursos ofertados são
 * agrupados em áreas de conhecimentos (Tecnológica, Humanas, Biomédica, etc.),
 * onde cada uma possui um conjunto de provas específicas (biomédica, por
 * exemplo, tem provas de Biologia, Química, Física e Redação).
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "area_conhecimento_vestibular", schema = "vestibular", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AreaConhecimentoVestibular implements PersistDB, Validatable {
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_area_conhecimento_vestibular", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Descrição da área. */
	private String descricao;
	
	/** Indica se o uso desta área em cadastros é ativo. */
	private boolean ativo = true;

	
	/** Construtor padrão. */
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

	/** Indica se o uso desta área em cadastros é ativo. 
	 * @return True, se é permitido o uso. False, caso contrário.
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se o uso desta área em cadastros é ativo. 
	 * @param ativo True, se é permitido o uso. False, caso contrário.
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna a descrição da área. 
	 * @return Descrição da área. 
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descrição da área. 
	 * @param descricao Descrição da área. 
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

	/** Valida os dados: a descrição da área não pode ser nula.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDescricao(),
				"Descrição da Área de Conhecimento", lista);
		return lista;
	}
	
	/** Retorna uma string representando a área de conhecimento do vestibular no formato:
	 * ID, seguido de vírgula, seguido da descrição, seguido de vírgula, seguido de ativo.
	 */
	@Override
	public String toString() {
		return getId() + ", " + getDescricao() + ", "
				+ (isAtivo() ? "ATIVO" : "INATIVO");
	}
}
