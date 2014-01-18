/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 08/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Metas e a��es de forma��o de Recursos Humanos para o pr�ximo per�odo sugeridos na
 * Auto Avalia��o do Programa.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Entity
@Table(schema = "stricto_sensu", name = "meta_acao_formacao_rh")
public class MetaAcaoFormacaoRH implements PersistDB {

	/** Chave primaria. */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "hibernate_sequence") })
	@Column(name = "id_meta_acao_formacao_rh", nullable = false)
	private int id;
	
	/** Meta para o pr�ximo per�odo. */
	private String meta;
	
	/** A��o para o pr�ximo per�odo. */
	private String acao;
	
	/** Indicador utilizado para atingir a meta para o pr�ximo per�odo. */
	private String indicador;
	
	/** Auto Avalia��o ao qual pertence estas metas. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_respostas_auto_avaliacao",nullable=false)
	private RespostasAutoAvaliacao respostasAutoAvaliacao;

	/**
	 * Construtor Padr�o.
	 */
	public MetaAcaoFormacaoRH() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getIndicador() {
		return indicador;
	}

	public void setIndicador(String indicador) {
		this.indicador = indicador;
	}

	public RespostasAutoAvaliacao getRespostasAutoAvaliacao() {
		return respostasAutoAvaliacao;
	}

	public void setRespostasAutoAvaliacao(
			RespostasAutoAvaliacao respostasAutoAvaliacao) {
		this.respostasAutoAvaliacao = respostasAutoAvaliacao;
	}
	
	/** Retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(meta, acao, indicador);
	}
	
	/** Compara se este objeto � igual a outro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MetaAcaoFormacaoRH) {
			MetaAcaoFormacaoRH other = (MetaAcaoFormacaoRH) obj;
			return this.id != 0 && this.id == other.id || EqualsUtil.testEquals(this, other, "meta", "acao", "indicador"); 
		} else
			return false;
	}
	
	/** Encapsulamento para retornar o c�digo hash do objeto usando JSF.
	 * @return
	 */
	public int getHash() {
		return hashCode();
	}
}
