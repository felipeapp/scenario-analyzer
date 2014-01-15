/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em 06/09/07
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
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.sigaa.ensino.dominio.Turno;

/**
 * Registro de altera��o do relacionamento entre DiscenteGraduacao com
 * MatrizCurricular ou Curr�culo.
 * Deve-se registrar toda mudan�a de matriz ou curr�culo do discente.
 *
 * @author Andr�
 *
 */
@Entity
@Table(name = "mudanca_curricular", schema = "graduacao")
public class MudancaCurricular implements PersistDB, TipoMudancaCurricular {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_mudanca_curricular", nullable = false)
	private int id;

	/** Data da mudan�a. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	/** Matriz Curricular anterior do discente. */
	@ManyToOne
	@JoinColumn(name="id_matriz_antiga")
	private MatrizCurricular matrizAntiga;

	/** Matriz Curricular para o qual o discente mudou. */
	@ManyToOne
	@JoinColumn(name="id_matriz_nova")
	private MatrizCurricular matrizNova;
	
	/** Curr�culo anterior do discente. */
	@ManyToOne
	@JoinColumn(name="id_curriculo_antigo")
	private Curriculo curriculoAntigo;

	/** Curr�culo para o qual o discente mudou. */
	@ManyToOne
	@JoinColumn(name="id_curriculo_novo")
	private Curriculo curriculoNovo;

	/** Discente que teve mudan�a curricular registrada. */
	@ManyToOne
	@JoinColumn(name="id_discente")
	private DiscenteGraduacao discente;

	/** Tipo da mudan�a curricular.
	 * @see TipoMudancaCurricular. */
	@Column(name="tipo_mudanca")
	private Integer tipoMudanca;

	/** Usu�rio respons�vel pela modifica��o. */
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada entrada;

	@Column(name="ativo")
	private boolean ativo = true;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;
	
	/** Indica se a mudan�a deve ser persistida ou n�o. */
	@Transient
	private boolean simulacao;
	
	/** Retorna a data da mudan�a. 
	 * @return Data da mudan�a. 
	 */
	public Date getData() {
		return data;
	}

	/** Seta a data da mudan�a.
	 * @param data Data da mudan�a. 
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/** Retorna o usu�rio respons�vel pela modifica��o. 
	 * @return Usu�rio respons�vel pela modifica��o. 
	 */
	public RegistroEntrada getEntrada() {
		return entrada;
	}

	/** Seta o usu�rio respons�vel pela modifica��o.
	 * @param entrada Usu�rio respons�vel pela modifica��o. 
	 */
	public void setEntrada(RegistroEntrada entrada) {
		this.entrada = entrada;
	}

	/** 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o discente que teve mudan�a curricular registrada. 
	 * @return Discente que teve mudan�a curricular registrada. 
	 */
	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	/** Seta o discente que teve mudan�a curricular registrada.
	 * @param discente Discente que teve mudan�a curricular registrada. 
	 */
	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	/** Retorna a matriz Curricular anterior do discente. 
	 * @return Matriz Curricular anterior do discente. 
	 */
	public MatrizCurricular getMatrizAntiga() {
		return matrizAntiga;
	}

	/** Seta a matriz Curricular anterior do discente.
	 * @param matrizAntiga Matriz Curricular anterior do discente. 
	 */
	public void setMatrizAntiga(MatrizCurricular matrizAntiga) {
		this.matrizAntiga = matrizAntiga;
	}

	/** Retorna a matriz Curricular para o qual o discente mudou. 
	 * @return Matriz Curricular para o qual o discente mudou. 
	 */
	public MatrizCurricular getMatrizNova() {
		return matrizNova;
	}

	/** Seta a matriz Curricular para o qual o discente mudou.
	 * @param matrizNova Matriz Curricular para o qual o discente mudou. 
	 */
	public void setMatrizNova(MatrizCurricular matrizNova) {
		this.matrizNova = matrizNova;
	}

	/** Retorna o curr�culo anterior do discente. 
	 * @return Curr�culo anterior do discente. 
	 */
	public Curriculo getCurriculoAntigo() {
		return curriculoAntigo;
	}

	/** Seta o curr�culo anterior do discente.
	 * @param curriculoAntigo Curr�culo anterior do discente. 
	 */
	public void setCurriculoAntigo(Curriculo curriculoAntigo) {
		this.curriculoAntigo = curriculoAntigo;
	}

	/** Retorna o curr�culo para o qual o discente mudou. 
	 * @return Curr�culo para o qual o discente mudou. 
	 */
	public Curriculo getCurriculoNovo() {
		return curriculoNovo;
	}

	/** Seta o curr�culo para o qual o discente mudou.
	 * @param curriculoNovo Curr�culo para o qual o discente mudou. 
	 */
	public void setCurriculoNovo(Curriculo curriculoNovo) {
		this.curriculoNovo = curriculoNovo;
	}

	/** Retorna o tipo da mudan�a curricular.
	 * @return Tipo da mudan�a curricular.
	 */
	public Integer getTipoMudanca() {
		return tipoMudanca;
	}

	/** Seta o tipo da mudan�a curricular.
	 * @param tipoMudanca Tipo da mudan�a curricular.
	 */
	public void setTipoMudanca(Integer tipoMudanca) {
		this.tipoMudanca = tipoMudanca;
	}


	/** Indica se a mudan�a de curr�culo � do tipo MUDANCA_CURRICULO.
	 * @return
	 */
	@Transient
	public boolean isMudancaCurriculo( ) {
		return tipoMudanca == MUDANCA_CURRICULO;
	}
	
	/** Indica se a mudan�a de curr�culo � do tipo MUDANCA_CURSO.
	 * @return
	 */
	@Transient
	public boolean isMudancaCurso( ) {
		return tipoMudanca == MUDANCA_CURSO;
	}
	
	/** Indica se a mudan�a de curr�culo � do tipo MUDANCA_ENFASE.
	 * @return
	 */
	@Transient
	public boolean isMudancaEnfase( ) {
		return tipoMudanca == MUDANCA_ENFASE;
	}

	/**
	 * Indica se a mudan�a de curr�culo � do tipo MUDANCA_MATRIZ ou
	 * MUDANCA_HABILITACAO ou MUDANCA_TURNO ou MUDANCA_MODALIDADE.
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@Transient
	public boolean isMudancaMatriz( ) {
		return tipoMudanca == MUDANCA_MATRIZ || tipoMudanca == MUDANCA_HABILITACAO
		|| tipoMudanca == MUDANCA_TURNO || tipoMudanca == MUDANCA_MODALIDADE 
		|| tipoMudanca == MUDANCA_GRAU_ACADEMICO;
	}

	/** Indica se a mudan�a de curr�culo � do tipo MUDANCA_TURNO.
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@Transient
	public boolean isMudandoTurno() {
		if (tipoMudanca == MUDANCA_TURNO) return true;

		// se tipo de mudan�a ainda for gen�rica, testar a mudan�a pelos IDs dos turnos
		if (tipoMudanca == MUDANCA_MATRIZ) {
			Turno turnoAntigo = matrizAntiga.getTurno();
			Turno turnoNovo = matrizNova.getTurno();
			if (turnoAntigo != null && turnoNovo != null)
				return turnoNovo.getId() != turnoAntigo.getId();
			else if (turnoNovo == null && turnoAntigo == null)
				return false;
			else
				return true; // um tem turno e outro n�o ... ou seja, est� mudando de turno sim
		} else {
			return false;
		}
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Indica se a mudan�a deve ser simulada. 
	 * @return
	 */
	public boolean isSimulacao() {
		return simulacao;
	}

	/** Seta se a mudan�a deve ser simulada.
	 * @param simulacao
	 */
	public void setSimulacao(boolean simulacao) {
		this.simulacao = simulacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}
	
}
