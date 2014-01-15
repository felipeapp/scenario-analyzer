/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 13/07/2011
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.Date;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;


/**
 * Classe utilizada para registrar as transferências de alunos entre turmas de ensino Médio
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "registro_transferencia_medio", schema = "medio", uniqueConstraints = {})
public class RegistroTransferenciaMedio implements Validatable {
	
	/** Constante designada para marcar e registrar a transferência do aluno de forma automática.*/
	public static final int AUTOMATICA = 1;
	/** Constante designada para marcar e registrar a transferência do aluno de forma manual.*/
	public static final int MANUAL = 2;
	
	/** Chave Primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_registro_transferencia_medio", nullable = false)
	private int id;
	
	/** Registro do Discente transferido na turma.*/
	@JoinColumn(name = "id_discente")
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	private DiscenteMedio discente;
	
	/** Objeto referente a turma de origem da transferência.*/
	@JoinColumn(name = "id_turma_serie_origem")
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	private TurmaSerie turmaSerieOrigem;
	
	/** Objeto referente a turma de destino da transferência.*/
	@JoinColumn(name = "id_turma_serie_destino")
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	private TurmaSerie turmaSerieDestino;
	
	/** Data de registro de transferência.*/
	@Column(name = "data", nullable = false)
	@CriadoEm
	private Date data;
	
	/** Registro de entrada da transferência.*/
	@JoinColumn(name = "id_registro_entrada")
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@CriadoPor
	private RegistroEntrada registroEntrada;
	
	/** Armazena o tipo da transferência realizada, se manual ou automática */
	private boolean automatica;
	
	/** Matricula do discente na série relacionada a transferência da turma. */
	@JoinColumn(name = "id_matricula_discente_serie")
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	private MatriculaDiscenteSerie matriculaDiscenteSerie;
	
	/** Construtor */
	public RegistroTransferenciaMedio(){
		
	}

	/** Getters and Setters */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public DiscenteMedio getDiscente() {
		return discente;
	}
	public void setDiscente(DiscenteMedio discente) {
		this.discente = discente;
	}
	public TurmaSerie getTurmaSerieOrigem() {
		return turmaSerieOrigem;
	}
	public void setTurmaSerieOrigem(TurmaSerie turmaSerieOrigem) {
		this.turmaSerieOrigem = turmaSerieOrigem;
	}
	public TurmaSerie getTurmaSerieDestino() {
		return turmaSerieDestino;
	}
	public void setTurmaSerieDestino(TurmaSerie turmaSerieDestino) {
		this.turmaSerieDestino = turmaSerieDestino;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	public boolean isAutomatica() {
		return automatica;
	}
	public void setAutomatica(boolean automatica) {
		this.automatica = automatica;
	}
	public MatriculaDiscenteSerie getMatriculaDiscenteSerie() {
		return matriculaDiscenteSerie;
	}
	public void setMatriculaDiscenteSerie(
			MatriculaDiscenteSerie matriculaDiscenteSerie) {
		this.matriculaDiscenteSerie = matriculaDiscenteSerie;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}