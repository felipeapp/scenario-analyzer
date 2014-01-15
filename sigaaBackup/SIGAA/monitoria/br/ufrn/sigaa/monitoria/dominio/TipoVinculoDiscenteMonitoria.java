/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/12/2009
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * Representa o tipo de vínculo do discente com um projeto de monitoria. Compõe o
 * DiscenteMonitoria, dependendo do tipo de vínculo, direitos e deveres
 * diferentes são dados ao discente de monitoria.
 * 
 * @author Igor Linnik
 * 
 ******************************************************************************/
@Entity
@Table(name = "tipo_vinculo_discente", schema = "monitoria", uniqueConstraints = {})
public class TipoVinculoDiscenteMonitoria implements Validatable {
	
	public static final int SEM_VINCULO = -1;
	
	public static final int NAO_REMUNERADO = 1;
	
	public static final int BOLSISTA = 2;
	
	public static final int NAO_CLASSIFICADO = 3;
	
	public static final int EM_ESPERA = 4;

	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_vinculo_discente", unique = true, nullable = false)
	private int id;

	@Column(name = "descricao", length = 50)
	private String descricao;

	/** Indica se o Tipo de Bolsa Pesquisa está em uso */
	private boolean ativo = true;
	
	/** Armazena o tipo de bolsa que está associado no SIPAC */
	@Column(name = "id_tipo_bolsa_sipac")
	private Integer tipoBolsaSipac;
	
	/** default constructor */
	public TipoVinculoDiscenteMonitoria() {
	}

	/** minimal constructor */
	public TipoVinculoDiscenteMonitoria(int id) {
		this.id = id;
	}

	/** full constructor */
	public TipoVinculoDiscenteMonitoria(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

	public Integer getTipoBolsaSipac() {
	    return tipoBolsaSipac;
	}

	public void setTipoBolsaSipac(Integer tipoBolsaSipac) {
	    this.tipoBolsaSipac = tipoBolsaSipac;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj == null)
			return false;

		if (obj instanceof TipoVinculoDiscenteMonitoria) {
			TipoVinculoDiscenteMonitoria o = (TipoVinculoDiscenteMonitoria) obj;
			if (o.getId() > 0 && this.getId() > 0) {
			    result = this.getId() == o.getId();
			} else {
			    result = this.getDescricao().equalsIgnoreCase(o.getDescricao());
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		int result = 17;
		if (this.getId() > 0) {
		    result = 37 * result + new Integer(this.getId()).hashCode();
		} else {
		    result = 37 * result + this.getDescricao().hashCode();
		}
		return result;
	}

}
