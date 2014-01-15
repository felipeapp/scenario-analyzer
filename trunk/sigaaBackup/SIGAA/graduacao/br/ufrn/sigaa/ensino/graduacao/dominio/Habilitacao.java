/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 * Created on 10 de Janeiro de 2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.AreaSesu;
import br.ufrn.sigaa.vestibular.dominio.LinguaEstrangeira;

/**
 * � uma especialidade do curso que o aluno escolhe para cursar. Ex: Eng
 * Computa��o o aluno pode escolher: Automa��o industrial ou Sistemas.
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "habilitacao", schema = "graduacao")
public class Habilitacao implements Validatable {

	/** Chave prim�ria. */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_habilitacao", nullable = false)
	private int id;

	/** Nome da habilita��o. */
	@Column(name = "nome", nullable = false)
	private String nome;

	/** C�digo IES. */
	@Column(name = "codigo_ies")
	private String codigoIes;

	/** C�digo da habilita��o no INEP. */
	@Column(name = "codigo_habilitacao_inep")
	private Integer codigoHabilitacaoInep;

	/** Indica se possui op��o para outra habilita��o. */
	@Column(name = "opcao_para_habilitacao")
	private Boolean opcaoParaHabilitacao;

	/** Indica se possui v�nculo ISE. */
	@Column(name = "vinculo_ise")
	private Boolean vinculoISE = true;

	/** �rea Sesu*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_sesu", unique = false, nullable = true, insertable = true, updatable = true)
	private AreaSesu areaSesu;
	
	/** Curso associado a essa habilita��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	private Curso curso;
	
	/** L�ngua estrangeira obrigat�ria na prova de l�ngua estrangeira do Vestibular. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_lingua_estrangeira")
	private LinguaEstrangeira linguaObrigatoriaVestibular;

	/** Construtor padr�o. */
	public Habilitacao() {
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param nome
	 */
	public Habilitacao(int id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	/** Construtor parametrizado.
	 * @param iden
	 */
	public Habilitacao(int id) {
		this.id = id;
	}

	/** Construtor parametrizado. 
	 * @param sesu
	 */
	public Habilitacao(AreaSesu sesu) {
		this.areaSesu = sesu;
	}

	/** Construtor parametrizado.
	 * @param nome
	 */
	public Habilitacao(String nome) { 
		this.nome = nome;
	}

	/** Retorna a �rea Sesu
	 * @return
	 */
	public AreaSesu getAreaSesu() {
		return areaSesu;
	}

	/** Seta a �rea Sesu
	 * @param areaSesu
	 */
	public void setAreaSesu(AreaSesu areaSesu) {
		this.areaSesu = areaSesu;
	}

	/** Retorna o c�digo da habilita��o no INEP. 
	 * @return
	 */
	public Integer getCodigoHabilitacaoInep() {
		return codigoHabilitacaoInep;
	}

	/** Seta o c�digo da habilita��o no INEP.
	 * @param codigoHabilitacaoInep
	 */
	public void setCodigoHabilitacaoInep(Integer codigoHabilitacaoInep) {
		this.codigoHabilitacaoInep = codigoHabilitacaoInep;
	}

	/** Retorna o c�digo IES. 
	 * @return
	 */
	public String getCodigoIes() {
		return codigoIes;
	}

	/** Seta o c�digo IES.
	 * @param codigoIes
	 */
	public void setCodigoIes(String codigoIes) {
		this.codigoIes = codigoIes;
	}

	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o nome da habilita��o. 
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/** Seta o nome da habilita��o.
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/** Indica se possui op��o para outra habilita��o. 
	 * @return
	 */
	public Boolean getOpcaoParaHabilitacao() {
		return opcaoParaHabilitacao;
	}

	/** Seta se possui op��o para outra habilita��o. 
	 * @param opcaoParaHabilitacao
	 */
	public void setOpcaoParaHabilitacao(Boolean opcaoParaHabilitacao) {
		this.opcaoParaHabilitacao = opcaoParaHabilitacao;
	}

	/** Indica se possui v�nculo ISE. 
	 * @return
	 */
	public Boolean getVinculoISE() {
		return vinculoISE;
	}

	/** Seta se possui v�nculo ISE. 
	 * @param vinculoISE
	 */
	public void setVinculoISE(Boolean vinculoISE) {
		this.vinculoISE = vinculoISE;
	}

	/** Retorna o curso associado a essa habilita��o. 
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso associado a essa habilita��o.
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Valida os dados: nome, codigoIes, curso, codigoHabilitacaoInep.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(nome, "Nome", erros);
		ValidatorUtil.validateRequired(curso, "Curso", erros);

		return erros;
	}

	/**
	 * Serve pra validar os dados quando cadastro de uma nova matriz curricular
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate1()
	 */
	public ListaMensagens validate1() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(nome, "Nome", lista);
		ValidatorUtil.validateRequired(codigoIes, "C�digo", lista);

		return lista;
	}
	
	/** Compara se os objetos possuem o mesmo ID.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/** Retorna um c�digo hash, calculado sobre o ID.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Retorna uma representa��o textual da habilita��o.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return nome;
	}

	/** Retorna a l�ngua estrangeira obrigat�ria na prova de l�ngua estrangeira do Vestibular. 
	 * @return
	 */
	public LinguaEstrangeira getLinguaObrigatoriaVestibular() {
		return linguaObrigatoriaVestibular;
	}

	/** Seta a l�ngua estrangeira obrigat�ria na prova de l�ngua estrangeira do Vestibular.
	 * @param linguaObrigatoriaVestibular
	 */
	public void setLinguaObrigatoriaVestibular(
			LinguaEstrangeira linguaObrigatoriaVestibular) {
		this.linguaObrigatoriaVestibular = linguaObrigatoriaVestibular;
	}
}
