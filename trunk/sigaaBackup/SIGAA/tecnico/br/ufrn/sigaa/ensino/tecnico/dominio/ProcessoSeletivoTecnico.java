/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;

/**
 * Define um Processo Seletivo / Vestibular
 * 
 * @author �dipo Elder F. de Melo
 * @author Fred_Castro
 * 
 */
@Entity
@Table(name = "processo_seletivo_tecnico", schema = "tecnico", uniqueConstraints = {})
public class ProcessoSeletivoTecnico implements PersistDB, Validatable {

	/** Indica se o processo seletivo est� ativo. */
	@CampoAtivo
	private boolean ativo = true;
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_processo_seletivo_tecnico", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Nome do processo seletivo. */
	private String nome;
	
	/** Ano da aplica��o das provas. */
	@Column(name = "ano_entrada")
	private int anoEntrada;
	
	/** Sigla/nome abreviado do processo seletivo. */
	private String sigla;
	
	/** Forma de ingresso dos candidatos aprovados. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_forma_ingresso")
	private FormaIngresso formaIngresso;
	
	/** Construtor padr�o. */
	public ProcessoSeletivoTecnico() {
		formaIngresso = new FormaIngresso();
	}
	
	/** Construtor com ID. 
	 *  @param id
	 */
	public ProcessoSeletivoTecnico(int id) {
		this();
		this.id = id;
	}

	public int getAnoEntrada() {
		return anoEntrada;
	}

	public void setAnoEntrada(int anoEntrada) {
		this.anoEntrada = anoEntrada;
	}

	/** Indica se o processo seletivo est� ativo. 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Retorna a forma de ingresso dos candidatos aprovados. 
	 * @return
	 */
	public FormaIngresso getFormaIngresso() {
		return formaIngresso;
	}

	/**
	 * Retorna a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Retorna o nome do processo seletivo. 
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/** Retorna a sigla/nome abreviado do processo seletivo. 
	 * @return
	 */
	public String getSigla() {
		return sigla;
	}

	/** Seta se o processo seletivo est� ativo.
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Seta a forma de ingresso dos candidatos aprovados.
	 * @param formaIngresso
	 */
	public void setFormaIngresso(FormaIngresso formaIngresso) {
		this.formaIngresso = formaIngresso;
	}

	/**
	 * Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Seta o nome do processo seletivo.
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/** Seta a sigla/nome abreviado do processo seletivo. 
	 * @param sigla
	 */
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	/** Valida os dados do processo seletivo (nome, sigla, forma de ingresso, ano/per�odo, etc.)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(getNome(), "Nome do Processo Seletivo", lista);
		validateRequired(getSigla(), "Sigla/Nome Abreviado", lista);
		validateRequired(formaIngresso, "Forma de Ingresso", lista);
		if (anoEntrada < 1900)
			lista.addErro("Ano inv�lido");

		return lista;
	}

	/** Retorna uma representa��o textual do processo seletivo, no formato:
	 * ID, seguido de v�rgula, seguido do nome.
	 */
	@Override
	public String toString() {
		return getId() + ", " + getNome();
	}
}