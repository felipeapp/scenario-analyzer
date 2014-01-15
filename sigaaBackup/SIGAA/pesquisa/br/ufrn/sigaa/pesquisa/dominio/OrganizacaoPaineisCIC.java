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
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Registra a organização da apresentação dos trabalhos do Congresso de Iniciação
 * Científica em painéis, definida pelo gestor de pesquisa
 * @author Leonardo
 *
 */
@Entity
@Table(name="organizacao_paineis_cic", schema="pesquisa", uniqueConstraints={})
public class OrganizacaoPaineisCIC implements Validatable {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name="id_organizacao_paineis_cic")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_congresso")
	private CongressoIniciacaoCientifica congresso;
	
	/** Quantidade de painéis disponíveis no espaço físico da apresentação dos trabalhos. */
	@Column(name="numero_paineis")
	private int numeroPaineis; 
	
	@IndexColumn(name = "dia", base = 1)
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "organizacao")
	private List<DiaApresentacaoCentro> diasApresentacao = new ArrayList<DiaApresentacaoCentro>(0);
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	public OrganizacaoPaineisCIC() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CongressoIniciacaoCientifica getCongresso() {
		return congresso;
	}

	public void setCongresso(CongressoIniciacaoCientifica congresso) {
		this.congresso = congresso;
	}

	public int getNumeroPaineis() {
		return numeroPaineis;
	}

	public void setNumeroPaineis(int numeroPaineis) {
		this.numeroPaineis = numeroPaineis;
	}

	public List<DiaApresentacaoCentro> getDiasApresentacao() {
		return diasApresentacao;
	}

	public void setDiasApresentacao(List<DiaApresentacaoCentro> diasApresentacao) {
		this.diasApresentacao = diasApresentacao;
	}

	public ListaMensagens validate() {
		return null;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
	/**
	 * Retorna uma lista com os identificadores das unidades cujas apresentações de
	 * trabalhos ocorrerão no mesmo dia da unidade informada.
	 * @param unidade
	 * @return
	 */
	public List<Integer> getIdsUnidadesMesmoDia(Unidade unidade){
		int dia = 0;
		for(DiaApresentacaoCentro d: diasApresentacao){
			if(d.getCentro().getId() == unidade.getId()){
				dia = d.getDia();
			}
		}
		if(dia == 0)
			return null;
		List<Integer> ids = new ArrayList<Integer>();
		for(DiaApresentacaoCentro d: diasApresentacao){
			if(d.getDia() == dia)
				ids.add(d.getCentro().getId());
		}
		return ids;
	}
}
