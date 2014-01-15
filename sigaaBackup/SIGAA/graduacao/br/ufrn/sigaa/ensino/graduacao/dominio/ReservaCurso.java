/*
 * ReservaCurso.java
 *
 * Created on 10 de Janeiro de 2007, 11:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.ufrn.sigaa.ensino.graduacao.dominio;

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
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.dominio.Turno;

/**
 * Entity class ReservaCurso
 *
 * @author Gleydson
 */
@Entity
@Table(name = "reserva_curso", schema = "graduacao")
public class ReservaCurso implements Validatable, Comparable<ReservaCurso> {

	/** Identificador do registro de Reserva de Curso */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="graduacao.reserva_curso_seq") })
	@Column(name = "id_reserva_curso", nullable = false)
	private int id;

	/**
	 * numero de vagas solicitadas na reserva
	 */
	@Column(name = "numero_vagas", nullable = false)
	private Short vagasSolicitadas = 0;

	/**
	 * numero de vagas atendidas para a reserva
	 */
	@Column(name = "vagas_atendidas", nullable = false)
	private short vagasReservadas = 0;

	/** Número de vagas ocupadas. */
	@Column(name = "vagas_ocupadas")
	private Short vagasOcupadas;

	/** Matriz Curricular ao qual esta reserva de vaga se destina. */
	@JoinColumn(name = "id_matriz_curricular")
	@ManyToOne
	private MatrizCurricular matrizCurricular = new MatrizCurricular();

	/** Solicitação de turma que gerou esta reserva de vaga. */
	@JoinColumn(name = "id_solicitacao")
	@ManyToOne(fetch = FetchType.EAGER)
	private SolicitacaoTurma solicitacao = new SolicitacaoTurma();

	/** Turma ao qual esta reserva de vaga se refere. */
	@JoinColumn(name = "id_turma")
	@ManyToOne
	private Turma turma;

	/** Indica que pode remover a reserva de vaga da turma. */
	@Transient
	private boolean podeRemover;

	/** Curso e Turno são migrados do Ponto@, os do SIGAA devem ter a matriz preenchida
	 * Curso ao qual esta reserva de vaga pertence.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso")
	private Curso curso;

	/** Turno do curso ao qual esta reserva de vaga pertence. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turno")
	private Turno turno;

	/** Data em que a solicitação de reserva foi atendida. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atendimento", unique = false, insertable = true, updatable = true)
	private Date dataAtendimento;

	/** A referencia para o usuário que atendeu esta solicitação de turma */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_atendimento")
	private Usuario usuarioAtendimento;

	/** Atributo utilizado para informar se a reserva possui vagas destinadas à alunos ingressantes. */
	@Column(name = "possui_vaga_ingressantes")
	private Boolean possuiVagaIngressantes = false;
	
	/** numero de vagas solicitadas na reserva para ingressantes. */
	@Column(name = "numero_vagas_ingressantes")
	private Short vagasSolicitadasIngressantes = 0;

	/** numero de vagas atendidas para a reserva para ingressantes. */
	@Column(name = "vagas_atendidas_ingressantes")
	private short vagasReservadasIngressantes = 0;

	/** Número de vagas ocupadas para ingressantes. */
	@Column(name = "vagas_ocupadas_ingressantes")
	private Short vagasOcupadasIngressantes;
	
	/** A referencia para o usuário que criou esta solicitação de turma */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Data que foi criada esta reserva de vaga. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** ID da solicitação de turma referente à esta reserva de vaga. */
	@Transient
	private int idSolicitacao;
	
	
	/** Creates a new instance of ReservaCurso */
	public ReservaCurso() {
	}

	public ReservaCurso(int id) {
		this.id = id;
	}

	/** Responsável por retornar o valor total de vagas reservadas contemplando reservas normais e de alunos ingressantes.*/
	public int getTotalVagasReservadas(){
		return vagasReservadas + vagasReservadasIngressantes;
	}
	
	/** Responsável por retornar o valor total de vagas solicitadas contemplando reservas normais e de alunos ingressantes.*/
	public int getTotalVagasSolicitadas(){
		return vagasSolicitadas + vagasSolicitadasIngressantes;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}

	public Short getVagasSolicitadas() {
		return vagasSolicitadas;
	}

	public void setVagasSolicitadas(Short numeroVagas) {
		this.vagasSolicitadas = numeroVagas;
	}

	public Short getVagasOcupadas() {
		return vagasOcupadas;
	}

	public void setVagasOcupadas(Short vagasOcupadas) {
		this.vagasOcupadas = vagasOcupadas;
	}

	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	public SolicitacaoTurma getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(SolicitacaoTurma solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public short getVagasReservadas() {
		return vagasReservadas;
	}

	public void setVagasReservadas(short vagasAtendidas) {
		this.vagasReservadas = vagasAtendidas;
	}

	public boolean isAtendida() {
		return dataAtendimento != null;
	}

	public boolean isPodeRemover() {
		return podeRemover;
	}

	public void setPodeRemover(boolean podeRemover) {
		this.podeRemover = podeRemover;
	}

	@Override
	public boolean equals(Object obj) {
		// caso esteja adicionando a reserva à uma turma, não deve verficar o ID,
		// pois daria que os objetos são iguais (os dois ID == 0) quando não o são.
		if (this.id == 0 || ((PersistDB) obj).getId() == 0)
			return EqualsUtil.testEquals(this, obj, "matrizCurricular");
		else
			return EqualsUtil.testEquals(this, obj, "id", "solicitacao");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Retorna uma descrição desta reserva de vaga.
	 * @return
	 */
	public String getDescricao() {
		if (ValidatorUtil.isEmpty(matrizCurricular)) {
			return curso.getDescricaoCompleta() + (ValidatorUtil.isNotEmpty(turno) ? " - " + turno.getSigla() : "");
		} else {
			return matrizCurricular.getDescricao();
		}
	}

	@Override
	public int compareTo(ReservaCurso o) {
		if ( ValidatorUtil.isNotEmpty(matrizCurricular) && o != null && o.matrizCurricular != null)
			return matrizCurricular.getDescricao().compareTo(o.matrizCurricular.getDescricao());
		if ( ValidatorUtil.isNotEmpty(curso) && o != null && o.curso != null)
			return curso.getDescricao().compareTo(o.curso.getDescricao());
		return 0;
	}
	
	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Turno getTurno() {
		return turno;
	}

	public void setTurno(Turno turno) {
		this.turno = turno;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public Usuario getUsuarioAtendimento() {
		return usuarioAtendimento;
	}

	public void setUsuarioAtendimento(Usuario usuarioAtendimento) {
		this.usuarioAtendimento = usuarioAtendimento;
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

	/** Retorna uma descrição textual do solicitante da reserva de vaga.
	 * 
	 * @return
	 */
	public String getSolicitante(){
		if( registroEntrada != null )
			return registroEntrada.toString();
		else if( solicitacao != null )
			return solicitacao.getRegistroEntrada().toString();
		return null;

	}

	public int getIdSolicitacao() {
		return idSolicitacao;
	}

	public void setIdSolicitacao(int idSolicitacao) {
		this.idSolicitacao = idSolicitacao;
	}

	public Boolean getPossuiVagaIngressantes() {
		return possuiVagaIngressantes;
	}

	public void setPossuiVagaIngressantes(Boolean possuiVagaIngressantes) {
		this.possuiVagaIngressantes = possuiVagaIngressantes;
	}

	public Short getVagasSolicitadasIngressantes() {
		return vagasSolicitadasIngressantes;
	}

	public void setVagasSolicitadasIngressantes(Short vagasSolicitadasIngressantes) {
		this.vagasSolicitadasIngressantes = vagasSolicitadasIngressantes;
	}

	public short getVagasReservadasIngressantes() {
		return vagasReservadasIngressantes;
	}

	public void setVagasReservadasIngressantes(short vagasReservadasIngressantes) {
		this.vagasReservadasIngressantes = vagasReservadasIngressantes;
	}

	public Short getVagasOcupadasIngressantes() {
		return vagasOcupadasIngressantes;
	}

	public void setVagasOcupadasIngressantes(Short vagasOcupadasIngressantes) {
		this.vagasOcupadasIngressantes = vagasOcupadasIngressantes;
	}

}