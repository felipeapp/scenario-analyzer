/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/06/2009
 */

package br.ufrn.sigaa.biblioteca.circulacao.dominio;

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
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;


/**
 * <p>Classe que guarda as v�rias prorroga��es que um empr�stimo pode ter.</p>
 * 
 * <p>Prorroga��es podem ser por renova��es, final de semana, fechamento da biblioteca, etc...</p>
 *
 * @author Jadson
 * @since 10/07/2009
 * @version 1.0 cria��o da classe
 */
@Entity
@Table (name="prorrogacao_emprestimo", schema="biblioteca")
public class ProrrogacaoEmprestimo implements PersistDB{

	/**
	 * O id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.prorrogacao_emprestimo_sequence") })
	@Column(name = "id_prorrogacao_emprestimo", nullable = false)
	private int id;
	
	/** Prazo do empr�stimo antes de ser prorrogado. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_anterior")
	private Date dataAnterior;
	
	/** Prazo que o empr�stimo ficou depois de ser prorrogado. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atual")
	private Date dataAtual;
	
	/**
	 * O tipo da prorroga��o, se � por feriado, interrup��o_biblioteca, livro_perdido, renova��o, etc.
	 * @see br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo
	 */
	private int tipo;
	
	/**
	 * O empr�stimo a que a prorroga��o foi aplicada.
	 */
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="id_emprestimo", referencedColumnName="id_emprestimo")
	private Emprestimo emprestimo;
	
	
	///////////////////////////////// Auditoria ////////////////////////////////////////
	
	/** Registro entrada do usu�rio quem cadastrou a prorroga��o. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	private RegistroEntrada registroCadastro;

	/** Data de cadastro. */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/** Registro entrada do usu�rio que realizou a �ltima atualiza��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data da �ltima atualiza��o. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	/////////////////////////////////////////////////////////////////////////////
	
	/** O motivo, se for necess�rio. Por exemplo, no caso de prorroga��o por perda de material, o motivo � a justificativa do usu�rio. */
	private String motivo;

	
	/** Guarda temporariamente o nome do usu�rio que criou a prorroga��o */
	@Transient
	private String nomeUsuarioCriou;
	
	
	
	public ProrrogacaoEmprestimo () {
		super();
	}
	
	/**
	 * Construtor que recebe o tipo e a dataAnterior.
	 */
	public ProrrogacaoEmprestimo (Emprestimo emprestimo, int tipo, Date dataAnterior){
		this.emprestimo = emprestimo;
		this.tipo = tipo;
		this.dataAnterior = dataAnterior;
	}

	/**
	 * Construtor para uma prorroga��o j� persistida no banco.
	 */
	public ProrrogacaoEmprestimo (int id, Emprestimo emprestimo, Date dataAnterior, Date dataAtual, String motivo, String nomeUsuarioCriou
			, Date dataCadastro){
		this.id = id;
		this.emprestimo = emprestimo;
		this.dataAtual = dataAtual;
		this.dataAnterior = dataAnterior;
		this.motivo = motivo;
		this.nomeUsuarioCriou = nomeUsuarioCriou;
		this.dataCadastro = dataCadastro; // Para emitir o c�digo de autentica��o.
	}

	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public Date getDataAnterior() {
		return dataAnterior;
	}

	public void setDataAnterior(Date dataAnterior) {
		this.dataAnterior = dataAnterior;
	}

	public Date getDataAtual() {
		return dataAtual;
	}

	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public Emprestimo getEmprestimo() {
		return emprestimo;
	}

	public void setEmprestimo(Emprestimo emprestimo) {
		this.emprestimo = emprestimo;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

//	public Integer getIdentificacaoUsuario() {
//		return identificacaoUsuario;
//	}
//
//	public void setIdentificacaoUsuario(Integer identificacaoUsuario) {
//		this.identificacaoUsuario = identificacaoUsuario;
//	}

	public String getNomeUsuarioCriou() {
		return nomeUsuarioCriou;
	}

	public void setNomeUsuarioCriou(String nomeUsuarioCriou) {
		this.nomeUsuarioCriou = nomeUsuarioCriou;
	}
	
	
	
}