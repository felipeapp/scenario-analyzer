/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/04/2007
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.EmissaoRelatorio;

/**
 * Classe de domínio que mantém a distribuição de cotas de cada edital de pesquisa
 *
 * @author ricardo
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cota_docente", schema = "pesquisa", uniqueConstraints = {})
public class CotaDocente implements PersistDB, Comparable<CotaDocente> {

	private int id;
	/** Edital de pesquisa ao qual se refere a distribuição da cota */
	private EditalPesquisa edital;
	/** Registro de emissão do relatório de produtividade */
	EmissaoRelatorio emissaoRelatorio;

	private Servidor docente;
	/** Fator de Produtividade utilizada na distribuição */
	private double fppi;
	/** Nota média dos projetos submetidos pelo docente */
	private double mediaProjetos;
	/** Data quando as cotas foram concedidas */ 
	private Date dataConcessao;
	/** registro de entrada do usuário que realizou a operaÃ§Ã£o de distribuição de cotas */
	private RegistroEntrada registroEntrada;
	
	private Collection<Cotas> cotas = new HashSet<Cotas>();

	public CotaDocente() {
		docente = new Servidor();
	}

	public CotaDocente(int id) {
		this.id = id;
	}

	/**
	 * Retorna o IFC do docente de acordo com o FPPI e a media das notas dos projetos
	 *
	 * @return
	 */
	@Transient
	public double getIfc() {
		return ( (fppi * 8) + (mediaProjetos * 2) )/10;
	}

	@Transient
	public int getQtdCotas() {
		int total = 0;
		for(Cotas c: cotas)
			total += c.getQuantidade();
		return total;
	}

	public int compareTo(CotaDocente o) {
		int comparacao = new Double(o.getIfc()).compareTo(this.getIfc());
		
		if(comparacao == 0) {
			comparacao = new Double(o.getMediaProjetos()).compareTo(this.getMediaProjetos());
		}
		if(comparacao == 0) {
			comparacao = this.getDocente().getNome().compareTo(o.getDocente().getNome());
		}

		return comparacao ;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "docente", "edital");
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getDocente() {
		return docente;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_edital_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	public EditalPesquisa getEdital() {
		return edital;
	}

	@Column(name = "fppi", unique = true, nullable = false, insertable = true, updatable = true)
	public double getFppi() {
		return fppi;
	}

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	@Column(name = "media_projetos", unique = true, nullable = false, insertable = true, updatable = true)
	public double getMediaProjetos() {
		return mediaProjetos;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_concessao", unique = false, insertable = true, updatable = true)
	public Date getDataConcessao() {
		return dataConcessao;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId(), getDocente(), getEdital());
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_concessao", unique = false, nullable = true, insertable = true, updatable = true)
	public void setDataConcessao(Date dataConcessao) {
		this.dataConcessao = dataConcessao;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = false, insertable = true, updatable = true)
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public void setEdital(EditalPesquisa edital) {
		this.edital = edital;
	}

	public void setFppi(double fppi) {
		this.fppi = fppi;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMediaProjetos(double mediaProjetos) {
		this.mediaProjetos = mediaProjetos;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_emissao_relatorio", unique = false, nullable = true, insertable = true, updatable = true)
	public EmissaoRelatorio getEmissaoRelatorio() {
		return this.emissaoRelatorio;
	}

	public void setEmissaoRelatorio(EmissaoRelatorio emissaoRelatorio) {
		this.emissaoRelatorio = emissaoRelatorio;
	}

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "cotaDocente")
	@OrderBy("tipoBolsa")
	public Collection<Cotas> getCotas() {
		return cotas;
	}

	public void setCotas(Collection<Cotas> cotas) {
		this.cotas = cotas;
	}

	public void addCotas(Cotas cotas){
		cotas.setCotaDocente(this);
		this.cotas.add(cotas);
	}
	
	public void clearCotas() {
		for(Cotas c: cotas){
			if(c.getId() > 0)
				c.setId(0);
			if(c.getEditalPesquisa() != null)
				c.setEditalPesquisa(null);
			c.setCotaDocente(this);
			c.setQuantidade(0);
		}
	}
}
