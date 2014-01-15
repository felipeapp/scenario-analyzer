/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/05/2008
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;

/**
 * Classe que representa um inventor, ou seja, um participante autor de uma invenção
 * 
 * @author leonardo
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name="inventor", schema="pesquisa", uniqueConstraints = {})
public class Inventor implements Validatable {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name="id_inventor", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Invenção a qual o inventor será relacionado. */
	@ManyToOne
	@JoinColumn(name = "id_invencao")
	private Invencao invencao;
	
	/** Discente autor da invenção (se for o caso) */
	@ManyToOne
	@JoinColumn(name = "id_discente")
	private Discente discente;
	
	/** Docente autor da invenção (se for o caso) */
	@ManyToOne
	@JoinColumn(name = "id_docente")
	private Servidor docente;
	
	/** Servidor autor da invenção (se for o caso) */
	@ManyToOne
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;
	
	/** Docente Externo autor da invenção (se for o caso) */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_docente_externo")
	private DocenteExterno docenteExterno;
	
	@Transient
	private boolean selecionado = false;
	
	/** Indica se o autor é bolsista */
	private Boolean bolsista = false;
	
	/** Descrição da bolsa do autor */
	private String bolsa;
	
	/** Indica se há co-titularidade entre a instituição do autor externo e a Instituição */
	private Boolean cotitular;
	
	/**
	 * Usado somente para autores externos. Indica o percentual de participação da instituição do autor
	 * em relação ao total.
	 */
	@Column(name = "percentual_cotitularidade", unique = false, nullable = true, insertable = true, updatable = true, precision = 6)
	private Double percentualCotitularidade;
	
	/**
	 * Usado por todos os autores. Indica o percentual de participação no invento dentro do total da instituição.
	 */
	@Column(name = "participacao", unique = false, nullable = true, insertable = true, updatable = true, precision = 6)
	private Double participacao;
	
	/**
	 * DOCENTE = 1; DISCENTE = 2; SERVIDOR = 3; EXTERNO = 4;
	 *
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_categoria_membro")
	private CategoriaMembro categoriaMembro;
	
	// Deve ser definido para todos
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;
	
	public Inventor() {
	}

	public Inventor(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Invencao getInvencao() {
		return invencao;
	}

	public void setInvencao(Invencao invencao) {
		this.invencao = invencao;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public ListaMensagens validate() {
		return null;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public CategoriaMembro getCategoriaMembro() {
		return categoriaMembro;
	}

	public void setCategoriaMembro(CategoriaMembro categoriaMembro) {
		this.categoriaMembro = categoriaMembro;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Boolean getBolsista() {
		return bolsista;
	}

	public void setBolsista(Boolean bolsista) {
		this.bolsista = bolsista;
	}

	public String getBolsa() {
		return bolsa;
	}

	public void setBolsa(String bolsa) {
		this.bolsa = bolsa;
	}

	public Boolean getCotitular() {
		return cotitular;
	}

	public void setCotitular(Boolean cotitular) {
		this.cotitular = cotitular;
	}
	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Inventor other = (Inventor) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Transient
	public boolean isUfrn(){
		return categoriaMembro.getId() != CategoriaMembro.EXTERNO;
	}

	public Double getPercentualCotitularidade() {
		return percentualCotitularidade;
	}

	public void setPercentualCotitularidade(Double percentualCotitularidade) {
		this.percentualCotitularidade = percentualCotitularidade;
	}

	public Double getParticipacao() {
		return participacao;
	}

	public void setParticipacao(Double participacao) {
		this.participacao = participacao;
	}
	
	@Transient
	public String getOrigem(){
		String origem = "";
		if(docente != null && docente.getUnidade() != null)
			origem = docente.getUnidade().getSigla();
		else if(servidor != null && servidor.getUnidade() != null)
			origem = servidor.getUnidade().getSigla();
		else if(discente != null && discente.getCurso() != null)
			origem = discente.getCurso().getDescricao();
		else if(docenteExterno != null && docenteExterno.getInstituicao() != null)
			origem = docenteExterno.getInstituicao().getNome();
		return origem;
	}
}
