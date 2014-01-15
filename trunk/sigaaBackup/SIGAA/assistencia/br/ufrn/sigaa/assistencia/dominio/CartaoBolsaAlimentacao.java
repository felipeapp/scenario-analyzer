/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 25/02/2011
 * 
 */
package br.ufrn.sigaa.assistencia.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
/**
 * 
 * Classe que representa cart�es de bolsas alimenta��o.
 * @author geyson
 *
 */
@Entity
@Table(name = "cartao_bolsa_alimentacao", schema = "sae")
public class CartaoBolsaAlimentacao implements PersistDB {

	/** Identificador */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_cartao_bolsa_alimentacao", unique = true, nullable = false)
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="sae.cartao_bolsa_alimentacao_seq") })
	private int id;
	
	/** C�digo do cart�o */
	@Column( name = "codigo" )
	private Integer codigo;
	
	/** C�digo de barras do cart�o */
	@Column( name = "cod_barras" )
	private String codBarras;
	
	/** Verifica bloqueio do cart�o */
	@Column( name = "bloqueado" )
	private boolean bloqueado;
	
	/** Motivo do cart�o bloqueado */
	@Column( name = "motivo_bloqueio" )
	private String motivoBloqueio;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getCodBarras() {
		return codBarras;
	}
	public void setCodBarras(String codBarras) {
		this.codBarras = codBarras;
	}
	public boolean isBloqueado() {
		return bloqueado;
	}
	public void setBloqueado(boolean bloqueado) {
		this.bloqueado = bloqueado;
	}
	public String getMotivoBloqueio() {
		return motivoBloqueio;
	}
	public void setMotivoBloqueio(String motivoBloqueio) {
		this.motivoBloqueio = motivoBloqueio;
	}
	
}
