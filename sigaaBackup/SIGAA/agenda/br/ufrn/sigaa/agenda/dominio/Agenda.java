package br.ufrn.sigaa.agenda.dominio;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.agenda.util.AgendaUtils;
import br.ufrn.sigaa.agenda.util.EventosUtils;

/**
 * Entidade que agrega diferentes eventos relacionados para organização de uma agenda a
 * ser visualizada no sistema
 * 
 * @author wendell
 *
 */
@Entity
@Table(schema = "agenda", name = "agenda")
public class Agenda implements Validatable{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
			parameters={ @Parameter(name="sequence_name", value="agenda.agenda_sequence") })
	@Column(name="id_agenda") 
	private int id;
	
	/** O nome da agenda */
	@Column(nullable=false) 
	private String nome;
	
	/** A descrição da agenda */
	private String descricao;

	/** Aguarda os eventos da agenda  */
	@OneToMany(cascade = {CascadeType.ALL}, mappedBy = "agenda")
	private Collection<Evento> eventos;
	
	/** Os eventos da agenda não podem coincidir as data */
	@Column(name = "permite_choque_eventos", nullable=false)
	private boolean permiteChoqueEventos;
	
	
	/**Para remoção de agendas */
	private boolean ativo = true;
	
	///////////////////////  Auditoria /////////////////////////////
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao", nullable=false)
	@CriadoPor
	private RegistroEntrada criadoPor;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_criacao", nullable=false)
	private Date dataCriacao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada atualizadoPor;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@AtualizadoEm
	@Column(name="data_ultima_atualizacao")
	private Date dataUltimaAtualizacao;
	
	////////////////////////////////////////////////////////////////
	
	public Agenda() {
		eventos = new ArrayList<Evento>();
	}
	
	public Agenda(int id) {
		this();
		this.id = id;
	}
	
	public Agenda(int id, String nome) {
		this(id);
		this.nome = nome;
	}
	
	/**
	 * Construtor completo de um agenda
	 * 
	 * @param id
	 * @param nome
	 * @param descricao
	 * @param permiteChoqueEventos
	 */
	public Agenda(int id, String nome, String descricao, boolean permiteChoqueEventos) {
		this(id, nome);
		this.descricao = descricao;
		this.permiteChoqueEventos = permiteChoqueEventos;
	}
	
	
	
	/**
	 *   Retorna os eventos que ocorrem entre as data passadas com o status passado.
	 *
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException 
	 * @throws ParseException 
	 */
	public Collection<Evento> getEventos(Date dataInicio, Date dataFim, Short statusEventosCarregaveis) throws DAOException, ParseException {
		
		/* ******************************************************************
		 *  Carrega os eventos da agenda no período solicitado
		 * ******************************************************************/
		
		List<Evento> eventosNoIntervalo = new ArrayList<Evento>();
		
		if(eventos != null)
		for (Evento evento : eventos) {
			if( evento.ocorreEm(dataInicio, dataFim)){
				
				if(statusEventosCarregaveis == null){
					eventosNoIntervalo.add(evento);
				}else{
					if( statusEventosCarregaveis.equals(evento.getStatus())){
						eventosNoIntervalo.add(evento);
					}
				}
			}		
		}
		
		return eventosNoIntervalo;
	}
	
	
	
	
	
	/**
	 *   Retorna os eventos que ocorrem da data passada até o presente momento
	 *
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException 
	 * @throws ParseException 
	 */
	public Collection<Evento> getEventos(Date inicio, Short statusEventosCarregaveis) throws DAOException, ParseException {
		return getEventos(inicio, new Date(), statusEventosCarregaveis);
	}

	
	
	/**
	 *   <p>Método que contém a lógica para verifica se um evento se choca com algum evento já existente na agenda,
	 *   levando em conta as recorrências dos eventos ou não. Podem ser verificados todos os eventos cadastrados da agenda caso ela utilize 
	 *   persistencia, os apenas os eventos em memória no momento.</p> 
	 *
	 *   <p><strong>Importante:</strong>  é preciso passar a agenda com as variáveis <code>id</code>, <code>permiteChoqueEventos</code> e <code>utilizaRegrasRecorrencia</code> setados corretamente. </p>
	 *
	 * @param agenda 
	 * @param novoEvento o evento que vai ser criado
	 * @return  Se os eventos da agenda se choquam ou não 
	 * @throws DAOException 
	 * @throws ParseException 
	 */
	public boolean novoEventoCriaChoqueDeHorarios(Evento novoEvento, boolean verificaEventosPersistidos, Short statusEventosCarregaveis) throws DAOException, ParseException{

		if(! this.isPermiteChoqueEventos()){
			
			 /* **********  Busca todos os eventos autorizados ou não para a agenda ********** */
			 Collection<Evento> eventosJaCriados =  new ArrayList<Evento>();
			 
			 if(verificaEventosPersistidos)
				 eventosJaCriados = AgendaUtils.carregaEventosPersistidos(this.getId(),  novoEvento.getDataInicio(), novoEvento.getDataFim(), statusEventosCarregaveis);
			 else
				 eventosJaCriados = this.getEventos(novoEvento.getDataInicio(), novoEvento.getDataFim(), null); // Trabalha com os eventos em memória da agenda
			 
			 // Criar os eventos da agenda a partir da regras de recorrência dos eventos
			 eventosJaCriados  = EventosUtils.criaEventosVirtuais((List<Evento>) eventosJaCriados);
			 
			 List<Evento> eventosVaoSerCriados =  new ArrayList<Evento>();
			 
			 if(novoEvento.isEventoRecorrente()) {  // Criar os eventos da agenda a partir da regras de recorrência dos eventos
				 novoEvento.getRecorrencia().configuraDiasSemana();
				 
				 List<Evento> listaEventosTemp = new ArrayList<Evento>();
				 listaEventosTemp.add(novoEvento);
				 
				 
				 eventosVaoSerCriados  = EventosUtils.criaEventosVirtuais(listaEventosTemp);
			 }else{
				 eventosVaoSerCriados.add(novoEvento);
			 }
			 
			 /* ********************************************************************************
			  *   Para cada evento da agenda verificar se os dias e horários coincidem         *
			  * ********************************************************************************/
			 for (Evento eventoVaiSerCriado : eventosVaoSerCriados) {
			
				 for (Evento eventoJaCriado : eventosJaCriados) {
					 
					 System.out.println("Evento vai ser criado: "+eventoVaiSerCriado.getDataInicio()+" <-> "+eventoVaiSerCriado.getDataFim());
					 System.out.println("Evento já criado: "+eventoJaCriado.getDataInicio()+" <-> "+eventoJaCriado.getDataFim());
					 
					 if(CalendarUtils.isIntervalosDeDatasConflitantes(eventoVaiSerCriado.getDataInicio(), eventoVaiSerCriado.getDataFim(), eventoJaCriado.getDataInicio(), eventoJaCriado.getDataFim())){
						System.out.println("Choca-se ? SIM");
						return true;
						 
					 }else 
						 System.out.println("Choca-se ? NÃO");
				}	
			 }
		}
		
		return false;
			
	}
	
	
	/**
	 *  Adiciona um evento criado externamento a essa agenda, retornando a agenda
	 *
	 * @return
	 */
	public Agenda addEvento(Evento evento) {
		evento.setAgenda(this);
		eventos.add(evento);
		return this;
	}
	
	/**
	 *  Cria um evento associando-o a agenda e retornando esse evento
	 *
	 * @return
	 */
	public Evento createEvento() {
		Evento evento = new Evento();
		this.addEvento(evento);
		return evento;
	}
	
	
	/**
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		if( StringUtils.isEmpty( this.getNome())){
			lista.addErro("É preciso informar o nome da Agenda");
		}
		
		return lista;
	}
	
	///////////////  set  e getters 

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	
	public Collection<Evento> getEventos() {
		return eventos;
	}

	public void setEventos(Collection<Evento> eventos) {
		this.eventos = eventos;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	
	public boolean isPermiteChoqueEventos() {
		return permiteChoqueEventos;
	}

	public void setPermiteChoqueEventos(boolean permiteChoqueEventos) {
		this.permiteChoqueEventos = permiteChoqueEventos;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}


	public RegistroEntrada getAtualizadoPor() {
		return atualizadoPor;
	}

	public void setAtualizadoPor(RegistroEntrada atualizadoPor) {
		this.atualizadoPor = atualizadoPor;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

}
