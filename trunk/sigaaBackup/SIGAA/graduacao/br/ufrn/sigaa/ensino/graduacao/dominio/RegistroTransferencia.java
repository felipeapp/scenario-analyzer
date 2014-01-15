/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;


/**
 * Classe utilizada para registrar as transferências de alunos entre turmas
 * 
 * @author leonardo
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name = "registro_transferencia", schema = "graduacao", uniqueConstraints = {})
public class RegistroTransferencia implements Validatable {

	public static final int AUTOMATICA = 1;
	public static final int MANUAL = 2;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_registro_transferencia", nullable = false)
	private int id;
	
	@JoinColumn(name = "id_discente")
	@ManyToOne
	private Discente discente;
	
	@JoinColumn(name = "id_turma_origem")
	@ManyToOne
	private Turma turmaOrigem;
	
	@JoinColumn(name = "id_turma_destino")
	@ManyToOne
	private Turma turmaDestino;
	
	@Column(name = "data", nullable = false)
	@CriadoEm
	private Date data;
	
	@JoinColumn(name = "id_registro_entrada")
	@ManyToOne
	@CriadoPor
	private RegistroEntrada registroEntrada;
	
	// Armazena o tipo da transferência realizada, se manual ou automática
	private boolean automatica;
	
	@JoinColumn(name = "id_matricula_componente")
	@ManyToOne
	private MatriculaComponente matricula;
	
	@JoinColumn(name = "id_solicitacao_matricula")
	@ManyToOne
	private SolicitacaoMatricula solicitacaoMatricula;
	
	public RegistroTransferencia(){
		
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Turma getTurmaDestino() {
		return turmaDestino;
	}

	public void setTurmaDestino(Turma turmaDestino) {
		this.turmaDestino = turmaDestino;
	}

	public Turma getTurmaOrigem() {
		return turmaOrigem;
	}

	public void setTurmaOrigem(Turma turmaOrigem) {
		this.turmaOrigem = turmaOrigem;
	}

	public MatriculaComponente getMatricula() {
		return this.matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public SolicitacaoMatricula getSolicitacaoMatricula() {
		return this.solicitacaoMatricula;
	}

	public void setSolicitacaoMatricula(SolicitacaoMatricula solicitacaoMatricula) {
		this.solicitacaoMatricula = solicitacaoMatricula;
	}

	public boolean isAutomatica() {
		return this.automatica;
	}

	public void setAutomatica(boolean automatica) {
		this.automatica = automatica;
	}

}
