/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/10/2009
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Registra o dia da apresentação dos trabalhos de um determinado centro ou unidade acadêmica
 * especializada dentro da organização dos painéis definida pelo gestor de pesquisa
 *  
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="dia_apresentacao_centro", schema="pesquisa", uniqueConstraints={})
public class DiaApresentacaoCentro implements Validatable, Comparable<DiaApresentacaoCentro>{

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name="id_dia_apresentacao_centro")
	private int id;
	
	/** Agrupador indicando o dia em que os trabalhos do centro serão apresentados */
	private int dia;
	/** Centro ou unidade acadêmica especializada cujos trabalhos serão apresentados no dia definido */
	@ManyToOne
	@JoinColumn(name="id_unidade")
	private Unidade centro = new Unidade();
	/** organização de painéis do CIC definida pelo gestor de pesquisa */
	@ManyToOne
	@JoinColumn(name="id_organizacao")
	private OrganizacaoPaineisCIC organizacao = new OrganizacaoPaineisCIC();
	/** Quantidade de trabalhos do centro apresentados no dia */
	@Column(name="numero_trabalhos")
	private int numeroTrabalhos;
	
	@Transient
	private List<Integer> listaTrabalhos =  new ArrayList<Integer>();
	
	public DiaApresentacaoCentro() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public OrganizacaoPaineisCIC getOrganizacao() {
		return organizacao;
	}

	public void setOrganizacao(OrganizacaoPaineisCIC organizacao) {
		this.organizacao = organizacao;
	}

	public String getDescricaoDia(){
		return dia+"º DIA";
	}

	public int getNumeroTrabalhos() {
		return numeroTrabalhos;
	}

	public void setNumeroTrabalhos(int numeroTrabalhos) {
		this.numeroTrabalhos = numeroTrabalhos;
	}

	public int compareTo(DiaApresentacaoCentro o) {
		return new Integer(dia).compareTo(o.getDia()) == 0 ? centro.compareTo(o.getCentro()) : new Integer(dia).compareTo(o.getDia());
	}

	public ListaMensagens validate() {
		return null;
	}

	public List<Integer> getListaTrabalhos() {
		return listaTrabalhos;
	}

	public void setListaTrabalhos(List<Integer> listaTrabalhos) {
		this.listaTrabalhos = listaTrabalhos;
	}
}
