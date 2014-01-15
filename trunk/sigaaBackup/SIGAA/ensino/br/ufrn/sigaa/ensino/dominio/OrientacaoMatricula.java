/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 28/01/2008
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe de domínio utilizada para cadastrar 
 * uma orientação de matrícula para um discente
 * em um determinado ano-período
 * 
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name = "orientacao_matricula", schema = "ensino", uniqueConstraints = {})
public class OrientacaoMatricula implements Validatable {

	private int id;

	private Discente discente;
	
	private int ano;
	
	private int periodo;
	
	private String orientacao;
	
	private Servidor orientador;
	
	private DocenteExterno orientadorExterno;
	
	private TutorOrientador tutor;
	
	@CriadoPor
	private RegistroEntrada criadoPor;
	
	@CriadoEm
	private Date criadoEm;

	public OrientacaoMatricula() {
		
	}

	public int getAno() {
		return this.ano;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getCriadoEm() {
		return this.criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	public RegistroEntrada getCriadoPor() {
		return this.criadoPor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente", unique = false, nullable = true, insertable = true, updatable = true)
	public Discente getDiscente() {
		return this.discente;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_orientacao_matricula", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public String getOrientacao() {
		return this.orientacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_orientador", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getOrientador() {
		return this.orientador;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_orientador_externo", unique = false, nullable = true, insertable = true, updatable = true)
	public DocenteExterno getOrientadorExterno() {
		return orientadorExterno;
	}

	public void setOrientadorExterno(DocenteExterno docenteExterno) {
		this.orientadorExterno = docenteExterno;
	}

	public int getPeriodo() {
		return this.periodo;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setOrientacao(String orientacao) {
		this.orientacao = orientacao;
	}

	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	@ManyToOne
	@JoinColumn(name = "id_tutor")
	public TutorOrientador getTutor() {
		return tutor;
	}

	public void setTutor(TutorOrientador tutor) {
		this.tutor = tutor;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(getDiscente(), "Discente", erros );
		ValidatorUtil.validateRequired(getAno(), "Ano", erros );
		ValidatorUtil.validateRequired(getPeriodo(), "Período", erros );
		ValidatorUtil.validateRequired(getOrientacao(), "Orientação", erros );
		return erros;
	}
	
	@Transient
	public String getAnoPeriodo() {
		return ano + "." + periodo;
	}

}
