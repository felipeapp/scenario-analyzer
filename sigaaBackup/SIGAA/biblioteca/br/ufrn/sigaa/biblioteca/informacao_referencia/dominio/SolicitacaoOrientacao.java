/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Sep 19, 2008
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;

/**
 *	<p>Esta entidade registra uma solicitação de orientação (através de agendamento).</p>
 *
 *  <p>Utilizado quando a biblioteca não presta o serviço de normalização, mas presta um auxilio agendado ao usuário.</p>
 *
 * 	@author Felipe Rivas
 */
@Entity
@Table(name = "solicitacao_orientacao", schema = "biblioteca")
public class SolicitacaoOrientacao extends SolicitacaoServico {

	/**
	 * Define os valores de turno que o usuário pode indicar disponibilidade 
	 * para atendimento da solicitação de orientação
	 */
	public enum TurnoDisponibilidadeUsuario {
		/** Todos os turnos */
		TODOS(0, "Todos"),
		/** Turno da manhã */
		MANHA(1, "Manhã"),
		/** Turno da tarde */
		TARDE(2, "Tarde"),
		/** Turno da noite */
		NOITE(3, "Noite"),
		/** Turnos manhã e tarde */
		MANHA_TARDE(4, "Manhã/Tarde"),
		/** Turnos manhã e noite */
		MANHA_NOITE(5, "Manhã/Noite"),
		/** Turnos tarde e noite */
		TARDE_NOITE(6, "Tarde/Noite");
		
		/**
		 * Valor numérico do item da enumeração
		 */
		private int valor;
		/**
		 * Descrição do item da enumeração
		 */
		private String descricao;
		
		private TurnoDisponibilidadeUsuario(int valor, String descricao) {
			this.valor = valor;
			this.descricao = descricao;
		}
		
		public String getDescricao() {
			return descricao;
		}

		@Override
		public String toString() {
			return String.valueOf(this.valor);
		}
	}
	
	/** Id da solicitação */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.solicitacoes_usuario_sequence") })
	@Column(name = "id_solicitacao_orientacao", nullable = false)
	private int id;

	/** Data e horário de início do agendamento da orientação */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atendimento_inicio")
	private Date dataInicio;

	/** Horário de término do agendamento da orientação */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atendimento_fim")
	private Date dataFim;

	/** Comentários feitos pelo solicitante */
	@Column(name = "comentarios_usuario")
	private String comentariosUsuario;

	/** Comentários feitos pelo bibliotecário */
	@Column(name = "comentarios_bibliotecario")
	private String comentariosBibliotecario;

	/** Turno em que o usuário tem maior disponibilidade para receber atendimento */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="turno_disponivel")
	private TurnoDisponibilidadeUsuario turnoDisponivel;

	////////////////////////////////  Dados da Auditoria //////////////////////////////

	/** Data que foi validado. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_confirmacao")
	private Date dataConfirmacao;

	/** Usuário que validou. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_confirmacao", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroConfirmacao;
	
	/////////////////////////// sets e gets ////////////////////////////
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getComentariosUsuario() {
		return comentariosUsuario;
	}

	public void setComentariosUsuario(String comentariosUsuario) {
		this.comentariosUsuario = comentariosUsuario;
	}

	public String getComentariosBibliotecario() {
		return comentariosBibliotecario;
	}

	public void setComentariosBibliotecario(String comentariosBibliotecario) {
		this.comentariosBibliotecario = comentariosBibliotecario;
	}

	public Date getDataConfirmacao() {
		return dataConfirmacao;
	}

	public void setDataConfirmacao(Date dataConfirmacao) {
		this.dataConfirmacao = dataConfirmacao;
	}

	public RegistroEntrada getRegistroConfirmacao() {
		return registroConfirmacao;
	}

	public void setRegistroConfirmacao(RegistroEntrada registroConfirmacao) {
		this.registroConfirmacao = registroConfirmacao;
	}
	
	public boolean isConfirmado(){
		return getSituacao() == TipoSituacao.CONFIRMADO;
	}
	
	/**
	 * Retorna a pessoa que validou o solicitação
	 * @return
	 */
	public String getConfirmador(){
		return registroConfirmacao.getUsuario().getPessoa().getNome();
	}
	
	/**
	 * Retorna o horário agendado para orientação em uma forma descritiva
	 */
	public String getDescricaoHorarioAtendimento() {
		if (dataInicio != null && dataFim != null) {
			Calendar calInicio = Calendar.getInstance();
			Calendar calFim = Calendar.getInstance();

			calInicio.setTime(dataInicio);
			calFim.setTime(dataFim);
			
			return "Dia " + CalendarUtils.format(dataInicio, "dd/MM/yyyy") + ", das " + 
					(calInicio.get(Calendar.MINUTE) == 0 ? 
							CalendarUtils.format(dataInicio, "HH'h'") : CalendarUtils.format(dataInicio, "HH'h'mm'min'")) + 
					" às " + 
					(calFim.get(Calendar.MINUTE) == 0 ? 
							CalendarUtils.format(dataFim, "HH'h'") : CalendarUtils.format(dataFim, "HH'h'mm'min'"));
		}
		
		return null;
	}

	@Override
	public String getTipoServico() {
		return "Orientação";
	}
	
	/**
	 * Valida o preenchimento dos campos do objeto.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = super.validate();
		
		if (StringUtils.isNotEmpty(comentariosUsuario) && comentariosUsuario.length() > 100) {
			erros.addErro("O campo 'Comentários' não pode ultrapassar 100 caracteres.");
		}
		
		if (StringUtils.isNotEmpty(comentariosBibliotecario) && comentariosBibliotecario.length() > 100) {
			erros.addErro("O campo 'Comentários' não pode ultrapassar 100 caracteres.");
		}
		
		if (dataInicio != null && dataFim != null) {
			if (dataInicio.after(dataFim)) {
				erros.addErro("O horário de início do atendimento não pode ser maior ou igual ao horário de fim.");
			}

			if (CalendarUtils.compareTo(new Date(), dataInicio) > 0) {
				erros.addErro("A data de atendimento não pode ser anterior à data de hoje.");
			}
		}
		else if (dataInicio != null && dataFim == null) {
			erros.addErro("Não é permitido definir o horário de início do atendimento sem definir o horário de fim.");
		}
		else if (dataInicio == null && dataFim != null) {
			erros.addErro("Não é permitido definir o horário de fim do atendimento sem definir o horário de início.");
		}

		if (turnoDisponivel == null) {
			erros.addErro("Pelo menos um turno de disponibilidade deve ser definido.");
		}
		
		return erros;
	}

	public TurnoDisponibilidadeUsuario getTurnoDisponivel() {
		return turnoDisponivel;
	}

	public void setTurnoDisponivel(TurnoDisponibilidadeUsuario turnoDisponivel) {
		this.turnoDisponivel = turnoDisponivel;
	}

	public boolean isDisponivelManha() {
		return turnoDisponivel == TurnoDisponibilidadeUsuario.TODOS || 
				turnoDisponivel == TurnoDisponibilidadeUsuario.MANHA || 
				turnoDisponivel == TurnoDisponibilidadeUsuario.MANHA_TARDE || 
				turnoDisponivel == TurnoDisponibilidadeUsuario.MANHA_NOITE;
	}

	public boolean isDisponivelTarde() {
		return turnoDisponivel == TurnoDisponibilidadeUsuario.TODOS || 
				turnoDisponivel == TurnoDisponibilidadeUsuario.TARDE || 
				turnoDisponivel == TurnoDisponibilidadeUsuario.MANHA_TARDE || 
				turnoDisponivel == TurnoDisponibilidadeUsuario.TARDE_NOITE;
	}

	public boolean isDisponivelNoite() {
		return turnoDisponivel == TurnoDisponibilidadeUsuario.TODOS || 
				turnoDisponivel == TurnoDisponibilidadeUsuario.NOITE || 
				turnoDisponivel == TurnoDisponibilidadeUsuario.MANHA_NOITE || 
				turnoDisponivel == TurnoDisponibilidadeUsuario.TARDE_NOITE;
	}
	
}