package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

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
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;

@Entity
@Table(schema = "ensino", name = "bolsista")
public class Bolsistas implements Validatable {
	
	public static final int BOLSA_CNPQ = 81;
	
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="ensino.bolsista_sequence") })
	@Column(name = "id_bolsista", unique = true, nullable = false)
	private int id;
	
	/** Discente */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_discente")
	private Discente discente;
	
	
	/** Curso do discente */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	private Curso curso;
	
	@Transient
	private Unidade unidade;
	
	@Transient
	private Bolsas bolsa;


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Discente getDiscente() {
		return discente;
	}


	public void setDiscente(Discente discente) {
		this.discente = discente;
	}


	public Curso getCurso() {
		return curso;
	}


	public void setCurso(Curso curso) {
		this.curso = curso;
	}
	


	public Unidade getUnidade() {
		return unidade;
	}


	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}	


	public Bolsas getBolsa() {
		return bolsa;
	}


	public void setBolsa(Bolsas bolsa) {
		this.bolsa = bolsa;
	}


	@Override
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();		
		validateRequired(discente, "Discente",mensagens);
		validateRequired(curso, "Curso",mensagens);				
		return mensagens;
	}	
	

}
