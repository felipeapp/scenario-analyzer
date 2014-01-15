/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 31/05/2007
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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Representa uma solicitação de trancamento de matrícula em disciplina
 *
 * @author Victor Hugo
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "solicitacao_trancamento_matricula", schema = "ensino")
public class SolicitacaoTrancamentoMatricula implements Validatable {

	/** Trancamento solicitado */
	public static final int SOLICITADO 	= 1;
	/** Solicitação de trancamento atendida*/
	public static final int TRANCADO 	= 2;
	/** Solicitação de Trancamento cancelado */
	public static final int CANCELADO	= 3; //aluno desistiu de trancar
	/** Solicitação Trancamento vista pelo coordenado */
	public static final int VISTO 	= 4; //coordenador já deu o visto na solicitação, ou para orientar o não trancamento ou apenas para confirmá-lo.
	/** Solicitação Trancamento recusada */
	public static final int RECUSADO = 5; // Coordenador não aceitou o trancamento da matrícula

	/** Chave primária */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
				parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_solicitacao_trancamento_matricula", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Matricula em componentes curriculares e atividades. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_matricula_componente", unique = false, nullable = false, insertable = true, updatable = true)
	private MatriculaComponente matriculaComponente;

	/**
	 * situação da solicitação
	 */
	@Column(name = "situacao")
	private int situacao;

	/** Data que a solicitação foi  cadastrada. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", nullable = false, unique = false, insertable = true, updatable = true)
	private Date dataCadastro;
	
	/** Registro de entrada do usuário que efetuou o cadastro*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;

	/**
	 * data do atendimento da solicitação
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atendimento", unique = false, insertable = true, updatable = true)
	private Date dataAtendimento;

	/**
	 * registro de entrada de quem atendeu a solicitação
	 * caso esta solicitação tenha sido atendida pelo esgotamento do prazo do trancamento
	 * este atributo será NULL
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atendedor", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroAtendendor;

	/** Motivo de trancamento selecionado pelo discente*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_motivo_trancamento", unique = false, nullable = true, insertable = true, updatable = true)
	private MotivoTrancamento motivo;

	/**
	 * justificativa do trancamento, a ser utilizado quando o motivo escolhido é OUTROS
	 */
	private String justificativa;

	/**
	 * replica do coordenador tentando aconselhar o aluno a desistir do trancamento.
	 */
	private String replica;

	/** Data que o aluno cancelou a solicitação*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cancelamento", unique = false, insertable = true, updatable = true)
	private Date dataCancelamento;

	/** Número sequencial gerado para a solicitação*/
	@Column( name = "numero_solicitacao" )
	private Integer numeroSolicitacao;

	/**
	 * A referência para a AlteracaoMatricula do trancamento desta solicitação
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_alteracao_matricula", unique = false, nullable = true, insertable = true, updatable = true)
	private AlteracaoMatricula alteracaoMatricula;

	/** Campo boolean responsável por tratar a opção de parecer da solicitação do trancamento. (VISTO = true, Orientar Não Trancamento = false)*/
	@Transient
	private boolean parecerFavoravelSolicitacao;
	
	/** Campo boolean usado para o nivel stricto, define se esta dentro do prazo maximo de trancamento*/
	@Transient
	private Date prazoLimeTrancamento;
	
	public ListaMensagens validate() {
		return null;
	}

	public SolicitacaoTrancamentoMatricula() {
		super();
	}

	public SolicitacaoTrancamentoMatricula(int id, int situacao, Discente discente) {
		this.id = id;
		this.situacao = situacao;
		this.matriculaComponente = new MatriculaComponente();
		this.matriculaComponente.setDiscente(discente);
	}

	public SolicitacaoTrancamentoMatricula(int id) {
		this.id = id;
	}

	public SolicitacaoTrancamentoMatricula(int id, int idMatricula,	int idComponente, String nomeComponente,
			String codigoComp, int tipoComponente, int situacao, Date dataCadastro, Date dataAtendimento, String motivoDesc, String replica, int ano,
			int periodo, int idTurma, String codigoTurma, int idDiscente, long matricula,
			String nomeDiscente, char nivel, int gestora, Integer tipoAcademica, String justificativa, Integer idCurso  ) {
		this.id = id;
		this.justificativa = justificativa;
		matriculaComponente = new MatriculaComponente();
		matriculaComponente.setId(idMatricula);
		matriculaComponente.getComponente().setId(idComponente);
		matriculaComponente.getComponente().setNome(nomeComponente);
		matriculaComponente.getComponente().setCodigo(codigoComp);
		matriculaComponente.setAno( (short) ano );
		matriculaComponente.setPeriodo( (byte) periodo );
		matriculaComponente.getComponente().getTipoAtividade().setId(tipoComponente);
		this.setSituacao(situacao);
		this.dataCadastro =  dataCadastro;
		this.dataAtendimento =  dataAtendimento;
		this.replica = replica;
		this.motivo = new MotivoTrancamento();

		motivo.setDescricao(motivoDesc);
		Turma turma = new Turma();
		turma.setAno(ano);
		turma.setPeriodo(periodo);
		turma.setId(idTurma);
		turma.setCodigo(codigoTurma);

		Discente d = new Discente();
		d.setId(idDiscente);
		d.setNivel(nivel);
		d.setMatricula(matricula);
		d.setGestoraAcademica(new Unidade(gestora));
		d.getGestoraAcademica().setTipoAcademica( tipoAcademica );
		d.getPessoa().setNome(nomeDiscente);
		
		if( ValidatorUtil.isNotEmpty(idCurso) ){
			d.setCurso(new Curso(idCurso));
		} else {
			d.setCurso(null);
		}

		matriculaComponente.setTurma(turma);
		matriculaComponente.setDiscente(d);

	}

	public SolicitacaoTrancamentoMatricula(int id, int idMatricula,	int idComponente, String nomeComponente,
			String codigoComp, int tipoComponente, int situacao, Date dataCadastro, Date dataAtendimento, String motivoDesc, String replica, int ano,
			int periodo, int idTurma, String codigoTurma, int idDiscente, long matricula,
			String nomeDiscente, char nivel, int gestora, Integer tipoAcademica, String justificativa, String nomeAtendente, Integer idCurso  ) {
		
		this(id, idMatricula, idComponente, nomeComponente, codigoComp, tipoComponente, situacao, dataCadastro, dataAtendimento, motivoDesc, replica, ano,
				periodo, idTurma, codigoTurma,idDiscente, matricula,
				nomeDiscente, nivel, gestora, tipoAcademica, justificativa, idCurso);
		
		registroAtendendor = new RegistroEntrada();
		registroAtendendor.setUsuario(new UsuarioGeral());
		registroAtendendor.getUsuario().setPessoa(new PessoaGeral());
		registroAtendendor.getUsuario().getPessoa().setNome(nomeAtendente);
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public MatriculaComponente getMatriculaComponente() {
		return matriculaComponente;
	}

	public void setMatriculaComponente(MatriculaComponente matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}

	public RegistroEntrada getRegistroAtendendor() {
		return registroAtendendor;
	}

	public void setRegistroAtendendor(RegistroEntrada registroAtendendor) {
		this.registroAtendendor = registroAtendendor;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}
	
	/** Retorna a discrição da variavel situacao */
	public String getSituacaoString() {
		switch (situacao) {
		case SOLICITADO:
			return "Solicitado";
		case TRANCADO:
			return "Trancado";
		case VISTO:
			return "Visto";
		case CANCELADO:
			return "Cancelado";
		case RECUSADO:
			return "Recusado";
		default:
			return "Indefinido";
		}
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getReplica() {
		return replica;
	}

	public void setReplica(String replica) {
		this.replica = replica;
	}

	public MotivoTrancamento getMotivo() {
		return motivo;
	}

	public void setMotivo(MotivoTrancamento motivo) {
		this.motivo = motivo;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public AlteracaoMatricula getAlteracaoMatricula() {
		return alteracaoMatricula;
	}

	public void setAlteracaoMatricula(AlteracaoMatricula alteracaoMatricula) {
		this.alteracaoMatricula = alteracaoMatricula;
	}

	public boolean isParecerFavoravelSolicitacao() {
		return parecerFavoravelSolicitacao;
	}
	
	public void setParecerFavoravelSolicitacao(boolean parecerFavoravelSolicitacao) {
		this.parecerFavoravelSolicitacao = parecerFavoravelSolicitacao;
	}

	/**
	 * verifica se a data atual é inferior a data limite para o trancamento da turma nivel Stricto .
	 * @param prazoTrancamento
	 * @return
	 */
	public boolean isDentroPrazoTrancamento(Date prazoTrancamento){
		Date hoje = new Date();
		boolean anteriorPrazoLimite = CalendarUtils.compareTo(prazoTrancamento,hoje) > 0;
		return anteriorPrazoLimite;
	}
	
	/**
	 * diz se uma solicitação pode ser cancelada.
	 * ela pode ser cancelada SE tiver com a situação SOLICITADO ou VISTO
	 * @return
	 */
	public boolean isPodeCancelar(){
		if(matriculaComponente.getDiscente().isStricto() && prazoLimeTrancamento != null)
			return (situacao == SOLICITADO || situacao == VISTO) && isDentroPrazoTrancamento(prazoLimeTrancamento);
		return situacao == SOLICITADO || situacao == VISTO;
	}

	public boolean isTrancado() {
		return situacao == TRANCADO;
	}


	public Date getPrazoLimeTrancamento() {
		return prazoLimeTrancamento;
	}

	public void setPrazoLimeTrancamento(Date prazoLimeTrancamento) {
		this.prazoLimeTrancamento = prazoLimeTrancamento;
	}

}
