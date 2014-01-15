/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Nov 20, 2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Esta entidade armazena a confirma��o de interesse de matricula de um discente numa turma de f�rias que j� foi previamente solicitada por ele
 * Quando o aluno confirma o interesse da matricula � gerada uma Solicita��o de matricula e o aluno fica apto a ser matriculado ap�s o processamento das solicita��es
 * @author Victor Hugo
 */
@Entity
@Table(name = "confirmacao_matricula_ferias", schema="graduacao")
public class ConfirmacaoMatriculaFerias implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_confirmacao_matricula_ferias", nullable = false)
	private int id;
	
	/**
	 * turma de f�rias criada que o discente est� confirmando o interesse da matricula
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma", unique = false, insertable = true, updatable = false, nullable = false)
	private Turma turma;
	
	/**
	 * discente que est� confirmando
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente", unique = false, insertable = true, updatable = false, nullable = false)
	private DiscenteGraduacao discente;
	
	/**
	 * este atributo indica se o aluno confirmou ou n�o o interesse da matricula
	 */
	@Column(name = "confirmou", unique = false, insertable = true, updatable = true, nullable = false)
	private boolean confirmou = false;

	/**
	 * n�mero sequ�ncia da confirma��o, utilizado para identificar unicamente o comprovante de confirma��o exibido ao aluno
	 */
	@Column(name = "numero_confirmacao", unique = true, insertable = true, updatable = true, nullable = false)
	private int numeroConfirmacao;
	
	/**
	 * a matr�cula que foi  gerada caso o aluno confirme o interesse
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matricula_gerada", unique = false, insertable = true, updatable = false)
	private MatriculaComponente MatriculaGerada;
	
	///** indica se esta confirma��o de matr�cula est� ativa. � utilizado caso o aluno */
	//private boolean ativo = true;
	
	/**
	 * registro de entrada do usu�rio que confirmou/rejeitou o interesse ( o aluno)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = false, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/**
	 * data da confirma��o/rejei��o
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, insertable = true, updatable = true, nullable = false)
	@CriadoEm
	private Date dataCadastro;
	
	/**
	 * registro de entrada do usu�rio que alterou o interesse ( o aluno)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao", unique = false, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * data da altera��o 
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, insertable = true, updatable = true)
	@AtualizadoEm
	private Date dataAtualizacao;


	public ConfirmacaoMatriculaFerias() {}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public boolean isConfirmou() {
		return confirmou;
	}

	public void setConfirmou(boolean confirmou) {
		this.confirmou = confirmou;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public MatriculaComponente getMatriculaGerada() {
		return MatriculaGerada;
	}

	public void setMatriculaGerada(MatriculaComponente matriculaGerada) {
		MatriculaGerada = matriculaGerada;
	}

	public int getNumeroConfirmacao() {
		return numeroConfirmacao;
	}

	public void setNumeroConfirmacao(int numeroConfirmacao) {
		this.numeroConfirmacao = numeroConfirmacao;
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
	
}
