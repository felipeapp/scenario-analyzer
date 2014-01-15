/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 17/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * <p> Entidade que implementa no sistema a fila de reservas que os materiais podem possuir.</p>
 * 
 * <p> Reservas de materias � um mecanismo muito utilizado pelas biblioteca quando a quantidade de materiais n�o � suficiente para atender a demanda.</p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(name = "reserva_material_biblioteca", schema = "biblioteca")
public class ReservaMaterialBiblioteca implements PersistDB{
	
	/**
	 * <p>Representa os poss�veis estados que uma reserva de um material pode assumir durande o seu ciclo de vida </p>
	 * 
	 * @author jadson
	 *
	 */
	public enum StatusReserva{
		/** 
		 * <p>SOLICITADA = O usu�rio solicitou a reserva de um material</p>
		 * <p>EM_ESPERA = A comunica��o foi enviada para o usu�rio, mas ele ainda n�o apareceu para realizar o empr�stimo</p>
		 * <p>CANCELADA = A reserva foi cancelado porque expirou o per�odo.</p>
		 * <p>CANCELADA_MANUALMENTE = A reserva foi cancelada pelo bibliotec�rio.</p>
		 * <p>CONCRETIZADA = A reserva n�o estava mais ativa porque o usu�rio realizou o empr�stimo</p>
		 */ 
		SOLICITADA(0, "SOLICITADA"), EM_ESPERA(1, "EM ESPERA"), CANCELADA(2, "CANCELADA"), CANCELADA_MANUALMENTE(3,"CANCELADA MANUALMENTE"), CONCRETIZADA(4, "CONCRETIZADA");
		
		/**  O valor do enum, deve ser igual a posi��o que foi declarado. J� que n�o tem como - de uma maneira f�cil de simples, sem 
		 * herdar de outras classes e sobrescrever um monte de m�todo  - fazer o hibernate 
		 * pegar o valor desejado, o hibernate simplemente pega o valor da posi��o onde a vari�vel do 
		 * enum foi declarada para realizar a persist�ncia. <br/>
		 * Diferentemente da pesquisa, onde o valor utilizado  pelo hibernate � o valor do m�todo toString() <br/>.
		 * 
		 * 
		 * {@link http://www.hibernate.org/265.html}
		 * {@link http://www.guj.com.br/java/144147-persistencia-de-enum-enumtypeordinal}
		 */
		private int valor;
		
		private String descricao;
		
		
		private StatusReserva(int valor, String descricao){
			this.valor = valor;
			this.descricao = descricao;
		}
		
		/**
		 * retorna a descri��o para exibi��o nas p�ginas.
		 * @return
		 */
		public String getDescricao() {
			return descricao;
		}

		/**  M�todo chamado pelo hibernate para pegar o valor do enum nas consultas HQLs  */
		@Override
		public String toString() {
			return String.valueOf(valor);
		}
		
	}
	
	
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.reservas_material_sequence") })
	@Column(name = "id_reserva_material_biblioteca", nullable = false)
	private int id;

	
	/** Uma reserva � feita para materiais de um t�tulo do sistema. */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_titulo_catalografico", nullable = false)
	private TituloCatalografico tituloReservado;
	
	/**
	 * Usu�rio para quem a reserva foi feita
	 */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_usuario_biblioteca", nullable = false)
	private UsuarioBiblioteca usuarioReserva;
	
	/**
	 * O status da reserva
	 */
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as vari�veis s�o declaradas
	@Column(name="status", nullable=false)
	private StatusReserva status = StatusReserva.SOLICITADA;
	
	
	/**
	 * A data que a solicita��o da reserva foi feita.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_solicitacao", nullable=false)
	private Date dataSolicitacao;
	
	/**
	 * A data em que o material ficou dispon�vel para o usu�rio e a reserva ficou aguardando o usu�rio comparecer 
	 * na biblioteca para retirar o material
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_em_espera", nullable=true)
	private Date dataEmEspera;
	
	
	/**
	 * <p>A data com o prazo que o usu�rio vai ter para retirar o material.</p>
	 * 
	 * <p>Armazema o prazo j� descontando os finais de semana e interrup��es da biblioteca.</p>
	 */
	@Temporal(TemporalType.DATE)
	@Column(name="prazo_retirada_material", nullable=true)
	private Date prazoRetiradaMaterial;
	
	
	/**
	 * A data em que a reserva foi cancelada.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cancelamento", nullable=true)
	private Date dataCancelamento;
	
	
	/** Usu�rio que cancelou a reserva, no caso de reservas canceladas manualmente. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cancelamento", nullable=true)
	private Usuario usuarioCancelamento;
	
	
	/**  Uma pequena justificativa do porqu� que o bibliotec�rio est� cancelando essa reserva. */
	@Column(name="motivo_cancelamento", nullable=true, length=300)
	private String motivoCancelamento;
	
	
	/** O empr�stimo que foi gerado a partir dessa reserva, caso a reserva ter sido concretizada.*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_emprestimo", nullable=true)
	private Emprestimo emprestimosGerado;
	
	
	/**
	 * <p>Cont�m a previs�o de quando o usu�rio vai retirar a reserva e cada reserva tem a sua, para o usu�rio acompanhar passo-a-passo
	 * a previs�o.</p>
	 * <p>A previs�o final para retirar um material vai ser a data de previs�o da �ltima reserva ativa.</p>
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_previsao_retirada_material", nullable=false)
	private Date dataPrevisaoRetiradaMaterial;
	
	
	/** Guarda a informa��o textual do do t�tulo reservado, normalmente: autor t�tulo ano e edi��o */
	@Transient
	private String infoTitulo;
	
	
	public ReservaMaterialBiblioteca(){}
	
	
	
	/**
	 * <p>Construtor para criacao de uma nova reserva </p>
	 * 
	 * @param tituloReservado
	 * @param usuarioReserva
	 * @param status
	 * @param dataSolicitacao
	 * @param dataPrevisaoRetiradaMaterial
	 */
	public ReservaMaterialBiblioteca(TituloCatalografico tituloReservado,
			UsuarioBiblioteca usuarioReserva, StatusReserva status,
			Date dataSolicitacao, Date dataPrevisaoRetiradaMaterial) {
		this.tituloReservado = tituloReservado;
		this.usuarioReserva = usuarioReserva;
		this.status = status;
		this.dataSolicitacao = dataSolicitacao;
		this.dataPrevisaoRetiradaMaterial = dataPrevisaoRetiradaMaterial;
	}

	
	
	
	/**
	 * Verifica se a reserva est� na situa��o de criada
	 *
	 * @return
	 */
	public boolean isReservaSolicitada(){
		return status == StatusReserva.SOLICITADA;
	}
	
	/**
	 * Verifica se o usu�rio da reserva foi avisado sobre 
	 *
	 * @return
	 */
	public boolean isReservaEmEspera(){
		return status == StatusReserva.EM_ESPERA;
	}
	
	/**
	 * Verifica se a reserva est� na situa��o de criada
	 *
	 * @return
	 */
	public boolean isReservaCancelada(){
		return status == StatusReserva.CANCELADA;
	}
	
	/**
	 * Verifica se a reserva est� na situa��o de criada
	 *
	 * @return
	 */
	public boolean isReservaCanceladaManualmente(){
		return status == StatusReserva.CANCELADA_MANUALMENTE;
	}
	
	/**
	 * Verifica se a reserva est� na situa��o de criada
	 *
	 * @return
	 */
	public boolean isReservaConcretizada(){
		return status == StatusReserva.CONCRETIZADA;
	}

	/**
	 * Verique em quais status uma reserva � considerada ativa.
	 *
	 * @return
	 */
	public boolean isReservaAtiva(){
		if(isReservaSolicitada() && isReservaEmEspera())
			return true;
		else
			return false;
	}
	
	/**
	 * Retorna os status que s�o considerados ativos.
	 *
	 * @return
	 */
	public static StatusReserva[] getReservasAtivas(){
		return new StatusReserva[]{StatusReserva.SOLICITADA, StatusReserva.EM_ESPERA};
	}
	
	///// sets e gets ////
	
	/**
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */	
	@Override
	public int getId() {
		return id;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

	public TituloCatalografico getTituloReservado() {
		return tituloReservado;
	}

	public void setTituloReservado(TituloCatalografico tituloReservado) {
		this.tituloReservado = tituloReservado;
	}

	public UsuarioBiblioteca getUsuarioReserva() {
		return usuarioReserva;
	}

	public void setUsuarioReserva(UsuarioBiblioteca usuarioReserva) {
		this.usuarioReserva = usuarioReserva;
	}

	public StatusReserva getStatus() {
		return status;
	}

	public void setStatus(StatusReserva status) {
		this.status = status;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public Date getDataEmEspera() {
		return dataEmEspera;
	}

	public void setDataEmEspera(Date dataEmEspera) {
		this.dataEmEspera = dataEmEspera;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public Usuario getUsuarioCancelamento() {
		return usuarioCancelamento;
	}

	public void setUsuarioCancelamento(Usuario usuarioCancelamento) {
		this.usuarioCancelamento = usuarioCancelamento;
	}

	public Emprestimo getEmprestimosGerado() {
		return emprestimosGerado;
	}

	public void setEmprestimosGerado(Emprestimo emprestimosGerado) {
		this.emprestimosGerado = emprestimosGerado;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public Date getDataPrevisaoRetiradaMaterial() {
		return dataPrevisaoRetiradaMaterial;
	}

	public void setDataPrevisaoRetiradaMaterial(Date dataPrevisaoRetiradaMaterial) {
		this.dataPrevisaoRetiradaMaterial = dataPrevisaoRetiradaMaterial;
	}

	public String getInfoTitulo() {
		return infoTitulo;
	}

	public void setInfoTitulo(String infoTitulo) {
		this.infoTitulo = infoTitulo;
	}

	public Date getPrazoRetiradaMaterial() {
		return prazoRetiradaMaterial;
	}

	public void setPrazoRetiradaMaterial(Date prazoRetiradaMaterial) {
		this.prazoRetiradaMaterial = prazoRetiradaMaterial;
	}
	
	
	
}

