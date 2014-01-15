/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 01/11/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.EstacaoChamadaBiometrica;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Freqüências do aluno para as aulas de uma determinada turma. 
 * Utilizada para a construção do mapa de frequência populado. 
 * 
 * @author wendell
 *
 */
@Entity
@Table(name = "frequencia_aluno", schema = "ensino")
public class FrequenciaAluno implements DominioTurmaVirtual {

	
	/** Define a unicidade no banco */
	@Id
	@Column(name="id_frequencia")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="ava.frequencia_seq") })
	private int id;

	/** Define a data da frequência */
	private Date data;

	/** Define o número de faltas */
	@Column(name="frequencia")
	private short faltas;

	/** Define os horários */
	@Column(name="horarios")
	private short horarios;

	/** Define a turma associada a frequência */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;

	/** Define o discente associado a frequência */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private Discente discente;
	
	/**
	 *  Define o tipos que uma frequência pode ser captada.
	 *  @see br.ufrn.sigaa.ensino.dominio.TipoCaptcaoFrequencia
	 */
	@Column(name="tipo_captacao_frequencia")
	private Character tipoCaptcaoFrequencia;
	
	/**
	 * Define a data e hora da presença.
	 */
	@Temporal(TemporalType.TIME)
	@Column(name="hora_freq_digital")
	private Date horaPresencaDigital;

	/**
	 * Define se a turma tem chamada biometrica
	 * @see br.ufrn.sigaa.ava.dominio.EstacaoChamadaBiometrica
	 */
	@ManyToOne()
	@JoinColumn(name="id_estacao_biometrica")
	private EstacaoChamadaBiometrica estacaoChamadaBiometricaTurma;

	/** Data de cadastro do componente */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada do usuário que cadastrou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data da última atualização do componente */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Registro entrada do usuário que realizou a última atualização */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;
	
	/** Define se a frequência resgistrada foi removida. */
	private boolean ativo;

	public FrequenciaAluno(){
		
	}
	
	public FrequenciaAluno(Discente discente, Date data, Turma turma){
		this.discente = discente;
		this.data = data;
		this.turma = turma;
	}
	
	public FrequenciaAluno(int id, Short faltas, Long matricula, String nome) {
		this.id = id;
		this.faltas = faltas;
		discente = new Discente();
		discente.setMatricula(matricula);
		discente.setPessoa(new Pessoa());
		discente.getPessoa().setNome(nome);
	}
	
	public FrequenciaAluno(int id, Short faltas, Date data, int turma, Long matricula, String nome, Character tipoCaptcaoFrequencia) {
		this(id, faltas, matricula, nome);
		this.data = data;
		this.turma = new Turma(turma);
		this.tipoCaptcaoFrequencia = tipoCaptcaoFrequencia;
	}
	
	public FrequenciaAluno(int id, Short faltas, Date data, int turma, Long matricula, int idPessoa, String nome, Character tipoCaptcaoFrequencia) {
		this(id, faltas, matricula, nome);
		this.getDiscente().getPessoa().setId(idPessoa);
		this.data = data;
		this.turma = new Turma(turma);
		this.tipoCaptcaoFrequencia = tipoCaptcaoFrequencia;
	}
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

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

	/**
	 * Retorna as mensagens de aviso, caso ocorra 
	 * algum erro de validação dos atributos 
	 * 
	 * @return
	 */
	public ListaMensagens validate() {

		return null;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public short getFaltas() {
		return faltas;
	}

	public void setFaltas(short faltas) {
		this.faltas = faltas;
	}

	public short getHorarios() {
		return horarios;
	}

	public void setHorarios(short horarios) {
		this.horarios = horarios;
	}

	public String getMensagemAtividade() {
		return null;
	}

	public Character getTipoCaptcaoFrequencia() {
		return tipoCaptcaoFrequencia;
	}

	public void setTipoCaptcaoFrequencia(Character tipoCaptcaoFrequencia) {
		this.tipoCaptcaoFrequencia = tipoCaptcaoFrequencia;
	}

	public Date getHoraPresencaDigital() {
		return horaPresencaDigital;
	}

	public void setHoraPresencaDigital(Date horaPresencaDigital) {
		this.horaPresencaDigital = horaPresencaDigital;
	}

	public EstacaoChamadaBiometrica getEstacaoChamadaBiometricaTurma() {
		return estacaoChamadaBiometricaTurma;
	}

	public void setEstacaoChamadaBiometricaTurma(
			EstacaoChamadaBiometrica estacaoChamadaBiometricaTurma) {
		this.estacaoChamadaBiometricaTurma = estacaoChamadaBiometricaTurma;
	}
	
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result
				+ ((discente == null) ? 0 : discente.hashCode());
		result = prime * result + ((turma == null) ? 0 : turma.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FrequenciaAluno other = (FrequenciaAluno) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (discente == null) {
			if (other.discente != null)
				return false;
		} else if (!discente.equals(other.discente))
			return false;
		if (turma == null) {
			if (other.turma != null)
				return false;
		} else if (turma.getId() != other.turma.getId())
			return false;
		return true;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}