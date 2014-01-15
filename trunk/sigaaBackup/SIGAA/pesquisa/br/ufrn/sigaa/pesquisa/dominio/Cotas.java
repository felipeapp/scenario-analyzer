/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/03/2009
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que associa uma quantidade de cotas de um determinado tipo de bolsa
 * a um edital de pesquisa ou a uma cota distribuída a um docente.
 * 
 * @author Leonardo
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name="cotas", schema="pesquisa", uniqueConstraints={})
public class Cotas implements PersistDB {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name="id_cotas")
	private int id;
	
	/** Edital de pesquisa que distribuirá as cotas */
	@ManyToOne
	@JoinColumn(name="id_edital_pesquisa")
	private EditalPesquisa editalPesquisa;
	
	/** Cota distribuída a um docente */
	@ManyToOne
	@JoinColumn(name="id_cota_docente")
	private CotaDocente cotaDocente;
	/** Quantidade de bolsas distribuídas */
	private int quantidade;
	
	@ManyToOne
	@JoinColumn(name="id_tipo_bolsa_pesquisa")
	private TipoBolsaPesquisa tipoBolsa;
	
	public Cotas(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EditalPesquisa getEditalPesquisa() {
		return editalPesquisa;
	}

	public void setEditalPesquisa(EditalPesquisa editalPesquisa) {
		this.editalPesquisa = editalPesquisa;
	}

	public CotaDocente getCotaDocente() {
		return cotaDocente;
	}

	public void setCotaDocente(CotaDocente cotaDocente) {
		this.cotaDocente = cotaDocente;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public TipoBolsaPesquisa getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoBolsaPesquisa tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}
	
	
}
