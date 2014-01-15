/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 17/06/2008
 *
 */	
package br.ufrn.sigaa.assistencia.restaurante.dominio;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoRefeicaoRU;

/**
 * Entidade responsável pelos dias de alimentação
 * permitidos para cada discente
 * 
 * @author agostinho
 *
 */

@Entity
@Table(name="dias_alimentacao", schema="sae")
public class DiasAlimentacao implements PersistDB {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id_dias_alimentacao")
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_bolsa_auxilio")
	private BolsaAuxilio bolsaAuxilio;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "id_tipo_refeicao_ru")
	private TipoRefeicaoRU tipoRefeicao;

	private Boolean segunda;
	private Boolean terca;
	private Boolean quarta;
	private Boolean quinta;
	private Boolean sexta;
	private Boolean sabado;
	private Boolean domingo;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public BolsaAuxilio getBolsaAuxilio() {
		return bolsaAuxilio;
	}
	public void setBolsaAuxilio(BolsaAuxilio bolsaAuxilio) {
		this.bolsaAuxilio = bolsaAuxilio;
	}
	public TipoRefeicaoRU getTipoRefeicao() {
		return tipoRefeicao;
	}
	public void setTipoRefeicao(TipoRefeicaoRU tipoRefeicao) {
		this.tipoRefeicao = tipoRefeicao;
	}
	public Boolean getSegunda() {
		return segunda;
	}
	public void setSegunda(Boolean segunda) {
		this.segunda = segunda;
	}
	public Boolean getTerca() {
		return terca;
	}
	public void setTerca(Boolean terca) {
		this.terca = terca;
	}
	public Boolean getQuarta() {
		return quarta;
	}
	public void setQuarta(Boolean quarta) {
		this.quarta = quarta;
	}
	public Boolean getQuinta() {
		return quinta;
	}
	public void setQuinta(Boolean quinta) {
		this.quinta = quinta;
	}
	public Boolean getSexta() {
		return sexta;
	}
	public void setSexta(Boolean sexta) {
		this.sexta = sexta;
	}
	public Boolean getSabado() {
		return sabado;
	}
	public void setSabado(Boolean sabado) {
		this.sabado = sabado;
	}
	public Boolean getDomingo() {
		return domingo;
	}
	public void setDomingo(Boolean domingo) {
		this.domingo = domingo;
	}
}
