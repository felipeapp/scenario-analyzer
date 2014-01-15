/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 08/12/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;

/**
 * �nfase � uma especifica��o de conte�do associado a uma determinada modalidade
 * de um curso de gradua��o, destinada a aprofundar a forma��o do egresso em uma
 * sub-�rea espec�fica do conhecimento ou a permitir uma transi��o curricular
 * adequada de um curso de primeiro ciclo para um curso de segundo ciclo,
 * sendo vedado seu registro no hist�rico escolar e diploma do aluno.
 * 
 * @author �dipo Elder F. Melo
 * @author Rafael Gomes
 * 
 */
@Entity
@Table(name = "enfase", schema="graduacao")
public class Enfase implements Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_enfase", nullable = false)
	private int id;

	/** Nome da �nfase. */
	@Column(name = "nome", nullable = true)
	private String nome;

	/** {@link Curso} da �nfase. */
	@ManyToOne()
	@JoinColumn(name = "id_curso")
	private Curso curso = new Curso();
	
	/** Data de cadastro da �nfase. */
	@CriadoEm
	@Column(name = "criado_em", nullable = true)
	private Date criadoEm;
	
	/** Registro de entrada do usu�rio que cadastrou a �nfase. */
	@CriadoPor
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	/** Indica se a �nfase est� ativa para utiliza��o em novos cadastros. */
	@CampoAtivo
	@Column(name = "ativo", nullable = false)
	private boolean ativo;
	
	/**
	 * Construtor padr�o.
	 */
	public Enfase() {
		ativo = true;
	}
	
	/** Valida os dados: nome, matrizCurricular
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(curso, "Curso", lista);
		ValidatorUtil.validateRequired(nome, "Nome", lista);
		return lista;
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


	/** Retorna o nome da �nfase.
	 * @return
	 */
	public String getNome() {
		return nome;
	}


	/** Seta o nome da �nfase.
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}


	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna a data de cadastro da �nfase.
	 * @return
	 */
	public Date getCriadoEm() {
		return criadoEm;
	}


	/** Seta a data de cadastro da �nfase.
	 * @param criadoEm
	 */
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	/** Retorna o registro de entrada do usu�rio que cadastrou a �nfase.
	 * @return
	 */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Seta o registro de entrada do usu�rio que cadastrou a �nfase.
	 * @param registroEntrada
	 */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/** Retorna uma representa��o textual deste objeto, exibindo o atributo nome.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return nome;
	}

	/** Indica se a �nfase est� ativa para utiliza��o em novos cadastros
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se a �nfase est� ativa para utiliza��o em novos cadastros
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
