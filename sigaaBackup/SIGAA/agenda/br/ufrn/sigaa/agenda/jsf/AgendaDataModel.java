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
 * Modelo de dados para manipulação dos dados da agenda pelo componente JSF <p:schedule>.
 * 
 * @author wendell
 *
 */
public class AgendaDataModel extends LazyScheduleModel {
	
	/** Valor para exibir a agenda mensal do espaço físico */
	public final static int AGENDA_MENSAL = 1;
	
	/** Valor para exibir a agenda semanal do espaço físico */
	public final static int AGENDA_SEMANAL = 2;
	
	/** Valor para exibir a agenda diaria do espaço físico */
	public final static int AGENDA_DIARIA = 3;
	
	
	/** Valor para exibir a agenda mensal do espaço físico */
	private final static String MODO_EXIBICAO_MENSAL = "month";
	
	/** Valor para exibir a agenda semanal do espaço físico */
	private final static String MODO_EXIBICAO_SEMANAL = "agendaWeek";
	
	/** Valor para exibir a agenda diaria do espaço físico */
	private final static String MODO_EXIBICAO_DIARIO = "agendaDay";
	
	
	/** <p>Controla qual o modo de exibição inicial da agenda, por padrão é semanal. </p>
	 *  <p>Depois da criação o modo de exibição vai ser controlado pelo próprio componente via ajax e esse valor não é atualizado nesse processo, 
	 *  ou seja, esse valor só serve para criação da agenda, depois ele fica desatualizado.</p>
	 */
	private int moduloExebicaoAgenda = AGENDA_SEMANAL;
	
	/** Variável que permite ou não o usuário editar os eventos da agenda. */
	private boolean editavel = false;
	
	/** Variável que contém o status dos eventos da agenda que devem ser carregados para o usuário.
	 * Caso seja nulo todos os eventos vão ser carregados
	 */
	private Short statusEventosCarregaveis;
	
	/**
	 * A data inicial em que a agenda começa a exibir os seus dados, por padrão é a data da criação da agenda.
	 */
	private Date dataInicialExibicao = new Date();
	
	
	/**
	 *  O objeto encapsulado cujos dados serão mostrados na página
	 */
	private Agenda agenda; 
	
	
	/** Variável que indica que os eventos da agenda vão ser carregados do banco ou não no método <code>loadEvents</code> 
	 * chamado toda vida que o usuário avança ou recua no componente p:schedule
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
	 * Construtor com a data inicial da exebição da genda
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
	 * Retorna o módulo de exibição <strong>inicial</strong> da agenda.
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
	 * Verifica se a agenda está no módulo <strong>inicial</strong> de exibição da agenda era MENSAL
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
	 * Configura o módulo de exibição da agenda
	 *
	 * @return
	 */
	public void configuraModuloExibicao(){
		if(isAgendaDiaria() ) moduloExebicaoAgenda = AGENDA_DIARIA;
		if(isAgendaSemanal() ) moduloExebicaoAgenda = AGENDA_SEMANAL;
		if(isAgendaMensal() ) moduloExebicaoAgenda = AGENDA_MENSAL;
	}
	
	
	/**
	 * Verifica se a agenda está no módulo <strong>inicial</strong>  de exibição era SEMANAL
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
	 * Verifica se a agenda está no módulo de exibição <strong>inicial</strong> era DIARIA
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
	 * <p><strong>Método chamado automaticamente pelo componente <code>p:schedule</code> TODA vez que o usuário
	 * altera o período de visualização da agenda a tela. </strong> </p>
	 * 
	 * <p>Esse método é o responsável por fazer a paginação dos eventos da agenda na tela.</p>
	 * 
	 * @see org.primefaces.model.LazyScheduleModel#loadEvents(java.util.Date, java.util.Date)
	 */
	@Override
	public void loadEvents(Date dataInicio, Date dataFim) {
		
		Collection<Evento> eventos = new ArrayList<Evento>();
		
		try {
			
			/* ************************************************ 
			 * Carrega os eventos da agenda entre o período passado
			 * ************************************************/
			
			if(! carregaEventosPersistidos)
				eventos = agenda.getEventos(dataInicio, dataFim, statusEventosCarregaveis); // Busca os eventos que já estão na agenda
			else{
				
				eventos = AgendaUtils.carregaEventosPersistidos(agenda.getId(), dataInicio, dataFim, statusEventosCarregaveis);
			}
			
			
			/* ************************************************ 
			 * Cria o modelo dos eventos para visualização
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
