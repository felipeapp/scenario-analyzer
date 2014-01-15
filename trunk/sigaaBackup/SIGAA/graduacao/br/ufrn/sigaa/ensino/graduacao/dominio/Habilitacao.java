/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * É uma especialidade do curso que o aluno escolhe para cursar. Ex: Eng
 * Computação o aluno pode escolher: Automação industrial ou Sistemas.
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "habilitacao", schema = "graduacao")
public class Habilitacao implements Validatable {

	/** Chave primária. */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_habilitacao", nullable = false)
	private int id;

	/** Nome da habilitação. */
	@Column(name = "nome", nullable = false)
	private String nome;

	/** Código IES. */
	@Column(name = "codigo_ies")
	private String codigoIes;

	/** Código da habilitação no INEP. */
	@Column(name = "codigo_habilitacao_inep")
	private Integer codigoHabilitacaoInep;

	/** Indica se possui opção para outra habilitação. */
	@Column(name = "opcao_para_habilitacao")
	private Boolean opcaoParaHabilitacao;

	/** Indica se possui vínculo ISE. */
	@Column(name = "vinculo_ise")
	private Boolean vinculoISE = true;

	/** Área Sesu*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_sesu", unique = false, nullable = true, insertable = true, updatable = true)
	private AreaSesu areaSesu;
	
	/** Curso associado a essa habilitação. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	private Curso curso;
	
	/** Língua estrangeira obrigatória na prova de língua estrangeira do Vestibular. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_lingua_estrangeira")
	private LinguaEstrangeira linguaObrigatoriaVestibular;

	/** Construtor padrão. */
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

	/** Retorna a Área Sesu
	 * @return
	 */
	public AreaSesu getAreaSesu() {
		return areaSesu;
	}

	/** Seta a Área Sesu
	 * @param areaSesu
	 */
	public void setAreaSesu(AreaSesu areaSesu) {
		this.areaSesu = areaSesu;
	}

	/** Retorna o código da habilitação no INEP. 
	 * @return
	 */
	public Integer getCodigoHabilitacaoInep() {
		return codigoHabilitacaoInep;
	}

	/** Seta o código da habilitação no INEP.
	 * @param codigoHabilitacaoInep
	 */
	public void setCodigoHabilitacaoInep(Integer codigoHabilitacaoInep) {
		this.codigoHabilitacaoInep = codigoHabilitacaoInep;
	}

	/** Retorna o código IES. 
	 * @return
	 */
	public String getCodigoIes() {
		return codigoIes;
	}

	/** Seta o código IES.
	 * @param codigoIes
	 */
	public void setCodigoIes(String codigoIes) {
		this.codigoIes = codigoIes;
	}

	/** Retorna a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o nome da habilitação. 
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/** Seta o nome da habilitação.
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/** Indica se possui opção para outra habilitação. 
	 * @return
	 */
	public Boolean getOpcaoParaHabilitacao() {
		return opcaoParaHabilitacao;
	}

	/** Seta se possui opção para outra habilitação. 
	 * @param opcaoParaHabilitacao
	 */
	public void setOpcaoParaHabilitacao(Boolean opcaoParaHabilitacao) {
		this.opcaoParaHabilitacao = opcaoParaHabilitacao;
	}

	/** Indica se possui vínculo ISE. 
	 * @return
	 */
	public Boolean getVinculoISE() {
		return vinculoISE;
	}

	/** Seta se possui vínculo ISE. 
	 * @param vinculoISE
	 */
	public void setVinculoISE(Boolean vinculoISE) {
		this.vinculoISE = vinculoISE;
	}

	/** Retorna o curso associado a essa habilitação. 
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso associado a essa habilitação.
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
		ValidatorUtil.validateRequired(codigoIes, "Código", lista);

		return lista;
	}
	
	/** Compara se os objetos possuem o mesmo ID.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/** Retorna um código hash, calculado sobre o ID.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Retorna uma representação textual da habilitação.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return nome;
	}

	/** Retorna a língua estrangeira obrigatória na prova de língua estrangeira do Vestibular. 
	 * @return
	 */
	public LinguaEstrangeira getLinguaObrigatoriaVestibular() {
		return linguaObrigatoriaVestibular;
	}

	/** Seta a língua estrangeira obrigatória na prova de língua estrangeira do Vestibular.
	 * @param linguaObrigatoriaVestibular
	 */
	public void setLinguaObrigatoriaVestibular(
			LinguaEstrangeira linguaObrigatoriaVestibular) {
		this.linguaObrigatoriaVestibular = linguaObrigatoriaVestibular;
	}
}
