/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/08/2009
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;


/**
 * Classe que registra a participação de membros nos institutos de ciência e tecnologia
 * 
 * @author geyson karlos
 */
@Entity
@Table(name = "membro_instituto_ciencia_tecnologia", schema = "pesquisa", uniqueConstraints = {})
public class MembroInstitutoCienciaTecnologia implements PersistDB {

	private int id;
	
	private InstitutoCienciaTecnologia institutoCienciaTecnologia;
	
	private CategoriaMembro categoriaMembro;

	private Pessoa pessoa = new Pessoa();

	private Discente discente = new Discente();

	private Servidor servidor = new Servidor();
	
	private Date dataInicio;

	private Date dataFim;
	
	private boolean selecionado = false;

	
	// Constructors

	/** default constructor */
	public MembroInstitutoCienciaTecnologia(){}
	
	/** minimal constructor */
	public MembroInstitutoCienciaTecnologia(int idMembroInstitutoCienciaTecnologia){
		this.id = idMembroInstitutoCienciaTecnologia;
	}
	
	/** full constructor */
	public MembroInstitutoCienciaTecnologia(int idMembroInstitutoCienciaTecnologia, InstitutoCienciaTecnologia instituto, 
											CategoriaMembro categoriaMembro, Discente discente, Servidor servidor,Date dataInicio, 
											Date dataFim)
	{
		this.id = idMembroInstitutoCienciaTecnologia;
		this.institutoCienciaTecnologia = instituto;
		this.categoriaMembro = categoriaMembro;
		this.discente = discente;
		this.servidor = servidor;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
	}
	
	// Property accessors
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_equipe_instituto_ciencia_tecnologia", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_instituto_ciencia_tecnologia", unique = false, nullable = true, insertable = true, updatable = true)
	public InstitutoCienciaTecnologia getInstitutoCienciaTecnologia() {
		return institutoCienciaTecnologia;
	}

	public void setInstitutoCienciaTecnologia(
			InstitutoCienciaTecnologia institutoCienciaTecnologia) {
		this.institutoCienciaTecnologia = institutoCienciaTecnologia;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria_equipe", unique = false, nullable = true, insertable = true, updatable = true)
	public CategoriaMembro getCategoriaMembro() {
		return categoriaMembro;
	}

	public void setCategoriaMembro(CategoriaMembro categoriaMembro) {
		this.categoriaMembro = categoriaMembro;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa", unique = false, nullable = true, insertable = true, updatable = true)
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente", unique = false, nullable = true, insertable = true, updatable = true)
	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Transient
	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	@Transient
	public boolean isDiscenteUFRN() {
		return categoriaMembro != null && categoriaMembro.getId() == CategoriaMembro.DISCENTE;
	}
	
	@Transient
	public boolean isDocenteUFRN() {
		return categoriaMembro != null && categoriaMembro.getId() == CategoriaMembro.DOCENTE;
	}
	
	@Transient
	public boolean isServidorUFRN() {
		return categoriaMembro != null && categoriaMembro.getId() == CategoriaMembro.SERVIDOR;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "pessoa.id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, pessoa != null? pessoa.getId() : 0);
	}
	
	
}
