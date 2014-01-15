/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
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
 *	<p>Esta entidade registra uma solicita��o de orienta��o (atrav�s de agendamento).</p>
 *
 *  <p>Utilizado quando a biblioteca n�o presta o servi�o de normaliza��o, mas presta um auxilio agendado ao usu�rio.</p>
 *
 * 	@author Felipe Rivas
 */
@Entity
@Table(name = "solicitacao_orientacao", schema = "biblioteca")
public class SolicitacaoOrientacao extends SolicitacaoServico {

	/**
	 * Define os valores de turno que o usu�rio pode indicar disponibilidade 
	 * para atendimento da solicita��o de orienta��o
	 */
	public enum TurnoDisponibilidadeUsuario {
		/** Todos os turnos */
		TODOS(0, "Todos"),
		/** Turno da manh� */
		MANHA(1, "Manh�"),
		/** Turno da tarde */
		TARDE(2, "Tarde"),
		/** Turno da noite */
		NOITE(3, "Noite"),
		/** Turnos manh� e tarde */
		MANHA_TARDE(4, "Manh�/Tarde"),
		/** Turnos manh� e noite */
		MANHA_NOITE(5, "Manh�/Noite"),
		/** Turnos tarde e noite */
		TARDE_NOITE(6, "Tarde/Noite");
		
		/**
		 * Valor num�rico do item da enumera��o
		 */
		private int valor;
		/**
		 * Descri��o do item da enumera��o
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
	
	/** Id da solicita��o */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.solicitacoes_usuario_sequence") })
	@Column(name = "id_solicitacao_orientacao", nullable = false)
	private int id;

	/** Data e hor�rio de in�cio do agendamento da orienta��o */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atendimento_inicio")
	private Date dataInicio;

	/** Hor�rio de t�rmino do agendamento da orienta��o */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atendimento_fim")
	private Date dataFim;

	/** Coment�rios feitos pelo solicitante */
	@Column(name = "comentarios_usuario")
	private String comentariosUsuario;

	/** Coment�rios feitos pelo bibliotec�rio */
	@Column(name = "comentarios_bibliotecario")
	private String comentariosBibliotecario;

	/** Turno em que o usu�rio tem maior disponibilidade para receber atendimento */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="turno_disponivel")
	private TurnoDisponibilidadeUsuario turnoDisponivel;

	////////////////////////////////  Dados da Auditoria //////////////////////////////

	/** Data que foi validado. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_confirmacao")
	private Date dataConfirmacao;

	/** Usu�rio que validou. */
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
	 * Retorna a pessoa que validou o solicita��o
	 * @return
	 */
	public String getConfirmador(){
		return registroConfirmacao.getUsuario().getPessoa().getNome();
	}
	
	/**
	 * Retorna o hor�rio agendado para orienta��o em uma forma descritiva
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
					" �s " + 
					(calFim.get(Calendar.MINUTE) == 0 ? 
							CalendarUtils.format(dataFim, "HH'h'") : CalendarUtils.format(dataFim, "HH'h'mm'min'"));
		}
		
		return null;
	}

	@Override
	public String getTipoServico() {
		return "Orienta��o";
	}
	
	/**
	 * Valida o preenchimento dos campos do objeto.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = super.validate();
		
		if (StringUtils.isNotEmpty(comentariosUsuario) && comentariosUsuario.length() > 100) {
			erros.addErro("O campo 'Coment�rios' n�o pode ultrapassar 100 caracteres.");
		}
		
		if (StringUtils.isNotEmpty(comentariosBibliotecario) && comentariosBibliotecario.length() > 100) {
			erros.addErro("O campo 'Coment�rios' n�o pode ultrapassar 100 caracteres.");
		}
		
		if (dataInicio != null && dataFim != null) {
			if (dataInicio.after(dataFim)) {
				erros.addErro("O hor�rio de in�cio do atendimento n�o pode ser maior ou igual ao hor�rio de fim.");
			}

			if (CalendarUtils.compareTo(new Date(), dataInicio) > 0) {
				erros.addErro("A data de atendimento n�o pode ser anterior � data de hoje.");
			}
		}
		else if (dataInicio != null && dataFim == null) {
			erros.addErro("N�o � permitido definir o hor�rio de in�cio do atendimento sem definir o hor�rio de fim.");
		}
		else if (dataInicio == null && dataFim != null) {
			erros.addErro("N�o � permitido definir o hor�rio de fim do atendimento sem definir o hor�rio de in�cio.");
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