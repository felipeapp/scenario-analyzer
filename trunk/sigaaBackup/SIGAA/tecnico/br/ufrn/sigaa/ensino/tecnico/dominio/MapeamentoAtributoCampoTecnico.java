/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

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
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Mantem um histórico que relaciona um campo de um arquivo com dados para
 * importação de discentes aprovados em outros concusos e um atributo nas
 * classes que serão criadas na importação. <br/>
 * Desta forma, o trabalho na importação é simplificado uma vez que reaproveita
 * mapeamentos anteriores.
 * 
 * @author Édipo Elder F. de Melo
 * @author Fred_Castro
 * 
 */
@Entity
@Table(name = "mapeamento_atributo_campo_tecnico", schema = "tecnico")
public class MapeamentoAtributoCampoTecnico implements PersistDB {
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_mapeamento_atributo_campo_tecnico", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Nome do campo a ser mapeado. */
	@Column(unique = true)
	private String campo;
	
	/** Atributo em que o campo será mapeado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_atributo_mapeavel", nullable = false, insertable = true, updatable = true)
	private AtributoClasseMapeavelTecnico atributoMapeavel;

	/** Leiaute ao qual este mapeamento pertence. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_leiaute_arquivo_importacao_tecnico", nullable = false, insertable = true, updatable = true)
	LeiauteArquivoImportacaoTecnico leiauteArquivoImportacao;
	
	/** Construtor padrão. */
	public MapeamentoAtributoCampoTecnico() {
		campo = "";
		atributoMapeavel = new AtributoClasseMapeavelTecnico();
	}
	
	/** Construtor parametrizado
	 * @param campo
	 * @param atributoMapeavel
	 */
	public MapeamentoAtributoCampoTecnico(String campo, AtributoClasseMapeavelTecnico atributoMapeavel) {
		this();
		this.campo = campo;
		this.atributoMapeavel = atributoMapeavel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

	public AtributoClasseMapeavelTecnico getAtributoMapeavel() {
		return atributoMapeavel;
	}

	public void setAtributoMapeavel(AtributoClasseMapeavelTecnico atributoMapeavel) {
		this.atributoMapeavel = atributoMapeavel;
	}
	
	/** Retorna uma descrição textual deste histórico de mapeamento no formato: campo -> atributo.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return campo + " -> " + atributoMapeavel.getDescricao();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof MapeamentoAtributoCampoTecnico) {
			return this.campo != null && this.campo.equals(((MapeamentoAtributoCampoTecnico)other).getCampo());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(campo);
	}

	public LeiauteArquivoImportacaoTecnico getLeiauteArquivoImportacao() {
		return leiauteArquivoImportacao;
	}

	public void setLeiauteArquivoImportacao(
			LeiauteArquivoImportacaoTecnico leiauteArquivoImportacao) {
		this.leiauteArquivoImportacao = leiauteArquivoImportacao;
	}
}
