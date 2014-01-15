package br.ufrn.sigaa.agenda.jsf;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.primefaces.model.LazyScheduleModel;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.agenda.dominio.Agenda;
import br.ufrn.sigaa.agenda.dominio.Evento;
import br.ufrn.sigaa.agenda.util.AgendaUtils;

/**
 * Modelo de dados para manipula��o dos dados da agenda pelo componente JSF <p:schedule>.
 * 
 * @author wendell
 *
 */
public class AgendaDataModel extends LazyScheduleModel {
	
	/** Valor para exibir a agenda mensal do espa�o f�sico */
	public final static int AGENDA_MENSAL = 1;
	
	/** Valor para exibir a agenda semanal do espa�o f�sico */
	public final static int AGENDA_SEMANAL = 2;
	
	/** Valor para exibir a agenda diaria do espa�o f�sico */
	public final static int AGENDA_DIARIA = 3;
	
	
	/** Valor para exibir a agenda mensal do espa�o f�sico */
	private final static String MODO_EXIBICAO_MENSAL = "month";
	
	/** Valor para exibir a agenda semanal do espa�o f�sico */
	private final static String MODO_EXIBICAO_SEMANAL = "agendaWeek";
	
	/** Valor para exibir a agenda diaria do espa�o f�sico */
	private final static String MODO_EXIBICAO_DIARIO = "agendaDay";
	
	
	/** <p>Controla qual o modo de exibi��o inicial da agenda, por padr�o � semanal. </p>
	 *  <p>Depois da cria��o o modo de exibi��o vai ser controlado pelo pr�prio componente via ajax e esse valor n�o � atualizado nesse processo, 
	 *  ou seja, esse valor s� serve para cria��o da agenda, depois ele fica desatualizado.</p>
	 */
	private int moduloExebicaoAgenda = AGENDA_SEMANAL;
	
	/** Vari�vel que permite ou n�o o usu�rio editar os eventos da agenda. */
	private boolean editavel = false;
	
	/** Vari�vel que cont�m o status dos eventos da agenda que devem ser carregados para o usu�rio.
	 * Caso seja nulo todos os eventos v�o ser carregados
	 */
	private Short statusEventosCarregaveis;
	
	/**
	 * A data inicial em que a agenda come�a a exibir os seus dados, por padr�o � a data da cria��o da agenda.
	 */
	private Date dataInicialExibicao = new Date();
	
	
	/**
	 *  O objeto encapsulado cujos dados ser�o mostrados na p�gina
	 */
	private Agenda agenda; 
	
	
	/** Vari�vel que indica que os eventos da agenda v�o ser carregados do banco ou n�o no m�todo <code>loadEvents</code> 
	 * chamado toda vida que o usu�rio avan�a ou recua no componente p:schedule
	 * */
	private boolean carregaEventosPersistidos = false;
	
	
	public AgendaDataModel() {
		clear();
	}

	public AgendaDataModel(Agenda agenda) {
		clear();
		this.agenda = agenda;
	}
	
	public AgendaDataModel(Agenda agenda, int modoExibicao, boolean editavel, Short statusEventosCarregaveis, boolean carregaEventosPerssistindos) {
		this(agenda);
		this.moduloExebicaoAgenda = modoExibicao;
		this.editavel = editavel;
		this.statusEventosCarregaveis = statusEventosCarregaveis;
		this.carregaEventosPersistidos = carregaEventosPerssistindos;
	}
	
	
	/**
	 * Construtor com a data inicial da exebi��o da genda
	 * @param agenda
	 * @param modoExibicao
	 * @param editavel
	 * @param statusEventosCarregaveis
	 * @param dataInicial
	 */
	public AgendaDataModel(Agenda agenda, int modoExibicao, boolean editavel, Short statusEventosCarregaveis, Date dataInicialExibicao, boolean carregaEventosPerssistindos) {
		this(agenda, modoExibicao, editavel, statusEventosCarregaveis, carregaEventosPerssistindos);
		this.dataInicialExibicao = dataInicialExibicao; 
	}
	
	
	public boolean isAgendaEditavel(){
		return editavel;
	}
	
	
	/**
	 * Retorna o m�dulo de exibi��o <strong>inicial</strong> da agenda.
	 *
	 * @return
	 */
	public String getModoExibicao(){
		
		if(isAgendaMensal())
			return MODO_EXIBICAO_MENSAL;
		
		if(isAgendaSemanal())
			return MODO_EXIBICAO_SEMANAL;
		
		if(isAgendaDiaria())
			return MODO_EXIBICAO_DIARIO;
		
		return "";
	}
	
	
	/**
	 * Verifica se a agenda est� no m�dulo <strong>inicial</strong> de exibi��o da agenda era MENSAL
	 *
	 * @return
	 */
	public boolean isAgendaMensal(){
		if(moduloExebicaoAgenda == AGENDA_MENSAL) 
			return true;
		else 
			return false;
	}
	
	
	/**
	 * Configura o m�dulo de exibi��o da agenda
	 *
	 * @return
	 */
	public void configuraModuloExibicao(){
		if(isAgendaDiaria() ) moduloExebicaoAgenda = AGENDA_DIARIA;
		if(isAgendaSemanal() ) moduloExebicaoAgenda = AGENDA_SEMANAL;
		if(isAgendaMensal() ) moduloExebicaoAgenda = AGENDA_MENSAL;
	}
	
	
	/**
	 * Verifica se a agenda est� no m�dulo <strong>inicial</strong>  de exibi��o era SEMANAL
	 *
	 * @return
	 */
	public boolean isAgendaSemanal(){
		if(moduloExebicaoAgenda == AGENDA_SEMANAL) 
			return true;
		else 
			return false;
	}
	
	/**
	 * Verifica se a agenda est� no m�dulo de exibi��o <strong>inicial</strong> era DIARIA
	 *
	 * @return
	 */
	public boolean isAgendaDiaria(){
		if(moduloExebicaoAgenda == AGENDA_DIARIA) 
			return true;
		else 
			return false;
	}
	
	
	
	/**
	 * <p><strong>M�todo chamado automaticamente pelo componente <code>p:schedule</code> TODA vez que o usu�rio
	 * altera o per�odo de visualiza��o da agenda a tela. </strong> </p>
	 * 
	 * <p>Esse m�todo � o respons�vel por fazer a pagina��o dos eventos da agenda na tela.</p>
	 * 
	 * @see org.primefaces.model.LazyScheduleModel#loadEvents(java.util.Date, java.util.Date)
	 */
	@Override
	public void loadEvents(Date dataInicio, Date dataFim) {
		
		Collection<Evento> eventos = new ArrayList<Evento>();
		
		try {
			
			/* ************************************************ 
			 * Carrega os eventos da agenda entre o per�odo passado
			 * ************************************************/
			
			if(! carregaEventosPersistidos)
				eventos = agenda.getEventos(dataInicio, dataFim, statusEventosCarregaveis); // Busca os eventos que j� est�o na agenda
			else{
				
				eventos = AgendaUtils.carregaEventosPersistidos(agenda.getId(), dataInicio, dataFim, statusEventosCarregaveis);
			}
			
			
			/* ************************************************ 
			 * Cria o modelo dos eventos para visualiza��o
			 * ************************************************/
			for (Evento evento : eventos) {
				evento.setAgenda(agenda);
				if (evento.isEventoRecorrente()) {
					for (Evento eventoRecorrente : evento.getRecorrencia().getOcorrencias()){
						eventoRecorrente.setAgenda(agenda);
						addEvent(new EventoDataModel(eventoRecorrente));
					}
					
				} else {
					EventoDataModel eventoModel = new EventoDataModel(evento);
					addEvent(eventoModel);
				}
			}	
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
	}

	public Date getDataInicialExibicao() {
		return dataInicialExibicao;
	}

	public void setDataInicialExibicao(Date dataInicialExibicao) {
		this.dataInicialExibicao = dataInicialExibicao;
	}

}
