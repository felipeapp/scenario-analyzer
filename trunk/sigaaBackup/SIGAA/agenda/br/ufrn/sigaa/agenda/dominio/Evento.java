package br.ufrn.sigaa.agenda.dominio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;

import com.google.ical.values.Frequency;
import com.google.ical.values.Weekday;

/**
 * <p>Entidade que representa um evento de uma agenda. Contém o título e a descrição do evento e seu início e fim. </p>
 * <p>É possível definir ainda uma regra de recorrência para um evento (semanal, mensal, todas as terças e quintas, etc.) através do atributo 
 * <code>recorrencia</code>.  Caso o evento não possua uma recorrência, ele ocorre apenas na data informada pelo usuário. 
 * </p>
 * 
 *  
 * <p><strong> Observação:</strong> O sistema suporta eventos com repetição diária, semanal, mensal e anual.</p> 
 *  
 * @author wendell
 *
 */
@Entity
@Table(schema = "agenda", name = "evento")
public class Evento implements Validatable, Cloneable {

	/** 
	 * <p>O evento existe mais não é visualizado na agenda, pode ser visualizado apenas em alguma página de autorização.</p>
	 * <p>Essa situação é utilizado para os caso em que o evento precisar ser autorizados por terceiros para entrar em vigor.
	 * Caso que esteja criando o evento seja o próprio dono da agenda, o evento pode ser criado diretamente como VISIVEL. </p>
	 * </p>
	 *  */
	public static final short NAO_VISIVEL = 1;
	
	
	/** O evento existe e é visíel na agenda por todos aqueles que tem permissão de visualizar a agenda.*/
	public static final short VISIVEL = 2;
	
	
	/** <p>O evento foi cancelado, não é mais visível no sistema em parte alguma.</p> 
	 *  <p> Essa situção pode ser utilizada por aqueles casos de uso que desejem guardar os eventos cancelados 
	 *  na própria agenda, para efeitos de auditoria ou histórico.</p>
	 *  <p>O número de linha da tabela evento pode crescer bastante caso esse status seja utilizado</p> */
	public static final short CANCELADO = 3;
	
	
	/**
	 * Constante com o tamanho máximo que a descriação do evento pode ter.
	 */
	public static final int TAMANHO_DESCRICAO_EVENTO = 300;
	
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
			parameters={ @Parameter(name="sequence_name", value="agenda.evento_sequence") })
	@Column(name="id_evento") 
	private int id;
	
	/** 
	 * <p>A agenda do evento.</p>
	 */
	@ManyToOne(cascade={}, fetch = FetchType.LAZY )
	@JoinColumn(name = "id_agenda", referencedColumnName = "id_agenda", nullable=false)
	private Agenda agenda;
	
	
	///////////////   Informações básica de um evento       ////////////////////
	
	/** Título do evento */
	@Column(nullable=false)
	private String titulo;
	
	/** A data de início e o horário de início do evento */
	@Column(name="data_inicio", nullable=false)
	private Date dataInicio;
	
	/** 
	 * <p>A data final e o horário final do evento.</p> 
	 * <p><strong> Caso o evento não seja recorrente: </strong> a data fim vai ser igual a data de início, pois o evento ocorre apenas uma vez.  </p> 
	 * <p><strong> Caso o evento seja recorrente: </strong> a data fim vai ser igual a data limite para a recorrência do evento ocorrer. </p>
	 */
	@Column(name="data_fim", nullable=false)
	private Date dataFim;

	/** Guarda de forma temporária o horário de início do evento. Será persitido na variável <code>dataInicio</code> */
	@Transient
	private Date horarioInicio;
	
	/** Guarda de forma temporária o horário de fim do evento .  Será persitido na variável <code>dataFim</code>*/
	@Transient
	private Date  horarioFim;
	
	
	/** Se o evento possui uma hora específica ou ocorre durante o dia todo */
	@Column(name = "dia_todo", nullable=false)
	private boolean diaTodo = false; 
	
	///////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	////////////////// Informações Extra sobre o evento  /////////////////////////
	
	
	/** Alguma descrição sobre o evento, como participantes, publico alvo, etc...*/
	private String descricao;
	
	/** Informações sobre onde o evento vai ocorrer  */
	private String local;
	
	
	/** <p> O status do evento, por padrão um evento quando é criado não é visualizado na agenda.</p>
	 *  
	 *  <p>Usado nos casos em que a criação do evento necessida da autorização de uma terceira pessoa. </p>
	 * */
	@Column(nullable=false)
	private short status = NAO_VISIVEL;
	
	
	///////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 *   <p>Informações sobre a perioticidade em que o evento ocorre </p>
	 *   <p>Para otimizar as buscas, é aconselhado que todo evento tenha uma recorrência, mesmo que com as regras de recorrências vazias</p>
	 *   
	 */
	@OneToOne(mappedBy="evento")
	private RecorrenciaEvento recorrencia;
	
	/**
	 *   Informações sobre a perioticidade em que o evento não ocorre.
	 *   <p>Para otimizar as buscas, é aconselhado que todo evento tenha uma excecaoRecorrencia, mesmo que com as regras de recorrências vazias</p>
	 */
	@OneToOne(mappedBy="evento")
	private RecorrenciaEvento excecaoRecorrencia;
	
	
	/** 
	 * Guarda a data do evento real cadastrado no banco para ser mostrado ao usuário, já que a data que o evento vai possuir
	 * vai ser gerada a partir da regras da sua recorrência. */
	@Transient
	private Date dataInicioOriginal;
	
	/** Guarda a data do evento real cadastrado no banco para ser mostrado ao usuário, , já que a data que o evento vai possuir
	 * vai ser gerada a partir da regras da sua recorrência. */
	@Transient
	private Date dataFimOriginal;
	
	public Evento() {
		super();
		//createRecorrencia();
	}
	
	public Evento(int id) {
		this();
		this.id = id;
	}
	
	/**
	 * Construtor mínimo de um evento
	 * @param titulo
	 * @param dataInicio
	 * @param dataFim
	 */
	public Evento(String titulo, Date dataInicio, Date dataFim) {
		this();
		this.titulo = titulo;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
	}
	
	/**
	 * Construtor de um evento não recorrente
	 * 
	 * @param titulo
	 * @param dataInicio
	 * @param dataFim
	 */
	public Evento(int id, String titulo, Date dataInicio, Date dataFim, boolean diaTodo, String descricao, String local) {
		this(titulo, dataInicio, dataFim);
		this.id = id;
		this.diaTodo = diaTodo;
		this.descricao = descricao;
		this.local = local;
	}
	
	
	/**
	 * Construtor de um evento com suas recorrencias
	 * 
	 * @param titulo
	 * @param dataInicio
	 * @param dataFim
	 * @throws ParseException 
	 * @throws ParseException 
	 */
	public Evento(int id, String titulo, Date dataInicio, Date dataFim, boolean diaTodo, String descricao, String local, String iCal, String exICal) throws ParseException {
		
		this(id, titulo, dataInicio, dataFim, diaTodo, descricao, local);
		
				
		if( StringUtils.notEmpty(iCal)){	
			this.recorrencia = new RecorrenciaEvento(iCal);
			this.recorrencia.setEvento(this);
		}
			
		if(StringUtils.notEmpty(exICal)){
			this.excecaoRecorrencia = new RecorrenciaEvento(exICal);
			this.excecaoRecorrencia.setEvento(this);
		}
		
	}
	
	
	
	/**
	 * Cria a recorrencia para ao evento retornando-a.
	 *
	 * @return
	 */
	public RecorrenciaEvento createRecorrencia() {
		setRecorrencia (new RecorrenciaEvento());
		return getRecorrencia();
	}
	
	/**
	 *  Verifica se um evento está finalizado
	 *
	 * @return
	 */
	public boolean isFinalizado() {
		return dataFim.after(new Date());
	}

	
	/**
	 * Verifica se o evento ocorre o dia todo.
	 * @return
	 */
	public boolean isDiaTodo() {
		return diaTodo;
	}

	
	/**
	 *   Método que verifica se um evento é recorrente. 
	 *   
	 *   IMPORTANTE: Um evento é considerado recorrente se ele possui uma recorrência
	 *
	 * @return
	 */
	public boolean isEventoRecorrente() {
		if(this.getRecorrencia() != null  )
			return true;
		else
			return false;
	}
	
	
	/**
	 * Verifica se a frequencia escolhida foi a frequencia diaria
	 * @return
	 */
	public boolean isEventoDiario(){
		
		if(! this.isEventoRecorrente()) return false;
		
		if( this.getRecorrencia().getFrequencia() == Frequency.DAILY){
			return true;
			
		}else{
			return false;
		}
		
	}
	
	
	/**
	 * Verifica se a frequencia escolhida foi a frequencia diaria
	 * @return
	 */
	public boolean isEventoSemanal(){
		
		if(! this.isEventoRecorrente()) return false;
		
		if( this.getRecorrencia().getFrequencia() == Frequency.WEEKLY){
			return true;
			
		}else{
			return false;
		}
	}
	
	
	/**
	 * Verifica se a frequencia escolhida foi a frequencia diaria
	 * @return
	 */
	public boolean isEventoMensal(){
		
		if(! this.isEventoRecorrente()) return false;
		
		if( this.getRecorrencia().getFrequencia() == Frequency.MONTHLY){
			return true;
			
		}else{
			return false;
		}
	}
	
	/**
	 * Verifica se a frequencia escolhida foi a frequencia diaria
	 * @return
	 */
	public boolean isEventoAnual(){
		
		if(! this.isEventoRecorrente()) return false;
		
		if( this.getRecorrencia().getFrequencia() == Frequency.YEARLY){
			return true;
			
		}else{
			return false;
		}
	}
	
	
	/**
	 * <p>Método que retorna as regras dos horários de um eventos em uma linguagem mais fácil do usuário final entender.</p>
	 * <p>Usado principalmente quando se deseja mostrar ao usuário as regras dos horários de um espaço físico em um espaço resumido 
	 * na tela, sem precisar abrir a genda com os eventos desenhados. </p>
	 *
	 * @return
	 */
	public String getDefinicaoEvento(){
		
		StringBuilder builder = new StringBuilder();
		
		SimpleDateFormat formatDia = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatHora = new SimpleDateFormat("HH:mm");

		if( isEventoRecorrente() && recorrencia.getRepetirAte() != null){
			builder.append("Inicia-se em: "+formatDia.format(dataInicio));
		}else{
			builder.append("Ocorre em: "+formatDia.format(dataInicio));
		}
	
		if(diaTodo)
			builder.append(", durante o dia todo");
		else
			builder.append(", das "+formatHora.format(dataInicio)+ " às "+formatHora.format(dataFim));
		
		if(isEventoRecorrente()){
			
			int intervalo = recorrencia.getRRule().getInterval();
			
			if(intervalo == 1){  // Diariamente, semanalmente, mensalmente ....
				String descricaoFrequencia = getDescricaoFrequencia();
				if(StringUtils.notEmpty(descricaoFrequencia))
					builder.append(", "+descricaoFrequencia);
			
			}else{  // A cada 2 dias, 2 semanas, 4 meses, 2 anos
				
				String descricaoReteticao = "";
				
				if(isEventoDiario()){
					descricaoReteticao = " DIAS";
				}
				if(isEventoSemanal()){
					descricaoReteticao = " SEMANAS";
				}
				if(isEventoMensal()){
					descricaoReteticao = " MESES";
				}
				if(isEventoAnual()){
					descricaoReteticao = " ANOS";
				}
				
				builder.append(", a cada "+intervalo+" "+descricaoReteticao);
			}
			
			recorrencia.recuperaDiasSemana();
			
			if(recorrencia.isOcorreTodosOsDias()){
				builder.append(", TODOS OS DIAS DA SEMANA");
			}else{
				builder.append(", OCORRENDO as(os): ");
				
				boolean primeriaOcorrencia = true;
				
				if(recorrencia.isOcorreAosDomingos()){
					
					if(primeriaOcorrencia){
						primeriaOcorrencia = false;
						builder.append(" DOMINGOS");
					}else{
						builder.append(", DOMINGOS");
					}
				}
				if(recorrencia.isOcorreAsSegundas()){
					
					if(primeriaOcorrencia){
						primeriaOcorrencia = false;
						builder.append(" SEGUNDAS");
					}else{
						builder.append(", SEGUNDAS");
					}
				}
				if(recorrencia.isOcorreAsTercas()){
					if(primeriaOcorrencia){
						primeriaOcorrencia = false;
						builder.append(" TERÇAS");
					}else{
						builder.append(", TERÇAS");
					}
				}
				if(recorrencia.isOcorreAsQuartas()){
					
					if(primeriaOcorrencia){
						primeriaOcorrencia = false;
						builder.append(" QUARTAS");
					}else{
						builder.append(", QUARTAS");
					}
				}
				if(recorrencia.isOcorreAsQuintas()){
					if(primeriaOcorrencia){
						primeriaOcorrencia = false;
						builder.append(" QUINTAS");
					}else{
						builder.append(", QUINTAS");
					}
				}
				if(recorrencia.isOcorreAsSextas()){
					
					if(primeriaOcorrencia){
						primeriaOcorrencia = false;
						builder.append(" SEXTAS");
					}else{
						builder.append(", SEXTAS");
					}
				}
				if(recorrencia.isOcorreAosSabados()){
					
					if(primeriaOcorrencia){
						primeriaOcorrencia = false;
						builder.append(" SÁBADOS");
					}else{
						builder.append(", SÁBADOS");
					}
				}
				
			}
			
			if(recorrencia.getRepetirAte() != null){
				builder.append(", terminando em: "+formatDia.format(recorrencia.getRepetirAte()));
			}
		}
			
		builder.append(". ");
		
		return builder.toString();
	}
	

	
	
	/**
	 *  Verifica se um evento ocorre no internvalo passado
	 *
	 * @return
	 */
	public boolean ocorreEm(Date dataInicio, Date dataFim) {
		try {
			// se o evento não possui recorrência, verifica apenas as datas de início e fim.
			if (! isEventoRecorrente())
				return CalendarUtils.isIntervalosDeDatasConflitantes(this.dataInicio, this.dataFim, dataInicio, dataFim);
			else {
				// caso contrário, verifica a recorrência.
				for (Evento evento : getRecorrencia().getOcorrencias()) { 
					if (CalendarUtils.isIntervalosDeDatasConflitantes(evento.getDataInicio(), evento.getDataFim(), dataInicio, dataFim))
						return true;
				}
			}
		} catch (ParseException e) {
			return false;
		}
		return false;
	}
	
	
	

	
	
	/**
	 * Retorna os tipos de frequencias aceitas pelo sistema para a visualização do usuário.
	 * 
	 * @return
	 */
	public String getDescricaoFrequencia() {
		
		if(recorrencia == null || recorrencia.getFrequencia() == null)
			return "";
			
		switch (recorrencia.getFrequencia()) {
			case DAILY:
			return "DIARIAMENTE";
			case WEEKLY:
				return "SEMANALMENTE";
			case MONTHLY:
				return "MENSALMENTE";
			case YEARLY:
				return "ANUALMENTE";
		default:
			return "";
		}
	}
	
	
	/**
	 * Retorna os tipos de frequencias aceitas pelo sistema para a visualização do usuário.
	 * 
	 * @return
	 */
	public Collection<SelectItem> getTiposFrequencia() {
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add( new SelectItem( Frequency.DAILY , "DIARIAMENTE") );
		itens.add( new SelectItem( Frequency.WEEKLY , "SEMANALMENTE") );
		itens.add( new SelectItem( Frequency.MONTHLY , "MENSALMENTE") );
		itens.add( new SelectItem( Frequency.YEARLY , "ANUALMENTE") );
		
		return itens;
	}
	
	
	/**
	 * Retorna os dias da semana que o evento pode ocorrer. 
	 * Por exemplo ocorre todas segunda, quarta e quinta, até uma determinada data.
	 * 
	 * @return
	 */
	public Collection<SelectItem> getDiasDaSemana() {
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add( new SelectItem( Weekday.SU , "DOMINGO") );
		itens.add( new SelectItem( Weekday.MO , "SEGUNDA") );
		itens.add( new SelectItem( Weekday.TU , "TERÇA") );
		itens.add( new SelectItem( Weekday.WE , "QUARTA") );
		itens.add( new SelectItem( Weekday.TH , "QUINTA") );
		itens.add( new SelectItem( Weekday.FR , "SEXTA") );
		itens.add( new SelectItem( Weekday.SA , "SÁBADO") );
		
		return itens;
	}
	
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(agenda, dataInicio);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "agenda", "titulo", "dataInicio", "dataFim", "recorrencia", "excecaoRecorrencia");
	}
	
	@Override
	protected Evento clone() {
		Evento clone = new Evento();
		clone.setTitulo(titulo);
		
		return clone;
	}

	/**
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		
		if( StringUtils.isEmpty(titulo)){
			lista.addErro("O título do evento precisa ser informado. ");
		}
		
		if( ! StringUtils.isEmpty(descricao)){
			if(descricao.length() > TAMANHO_DESCRICAO_EVENTO)
			lista.addErro("A descrição do evento deve possuir no máximo "+TAMANHO_DESCRICAO_EVENTO+" caracteres.");
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, -1); // volta para ontem
		
		Date ontem = CalendarUtils.configuraTempoDaData(c.getTime(), 23, 59, 59, 999);
		
		if( dataInicio == null || dataInicio.before(ontem) ){
			lista.addErro("A data de início do evento precisa ser uma data futura, não é possível criar eventos que já ocorreram. ");
		}
		
		/* ******************************************************************************************
		 * Valida se a hora de início e termino foi informada pelo usuário, antes do evento ser salvo
		 * essa informação tem que ser copiada para a dataInicio e dataFim, já que ela não é persistida
		 * *******************************************************************************************/
		if(! diaTodo){
			if(horarioInicio == null )
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Hora de início");
			
			if(horarioFim == null)	
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Hora de término");
			
			if(horarioInicio != null && horarioFim != null){
				Calendar hi = Calendar.getInstance();
				hi.setTime(horarioInicio);
				
				
				Calendar hf = Calendar.getInstance();
				hf.setTime(horarioFim);
				
				if(hf.get(Calendar.HOUR_OF_DAY) < hi.get(Calendar.HOUR_OF_DAY) ){
					lista.addErro("A hora de término do evento não pode ser antes da hora de início");
				}else{
					if(hf.get(Calendar.HOUR_OF_DAY) == hi.get(Calendar.HOUR_OF_DAY) ){
						if(hf.get(Calendar.MINUTE) < hi.get(Calendar.MINUTE) ){
							lista.addErro("A hora de término do evento não pode ser antes da hora de início");
						}
					}
				}
				
			}
			
		}
		
		/*
		 *  Verifica se a data inicial do evento é um dos dias da semana denifido pelo usuário 
		 */
		if(this.recorrencia != null){
			
			if(! this.recorrencia.isOcorreTodosOsDias()){
				
				boolean iniciaDiaDaSemanaCorreto = false;
				
				Calendar cDataInicio = Calendar.getInstance();
				cDataInicio.setTime(dataInicio);
				
				if(this.recorrencia.isOcorreAosDomingos() && cDataInicio.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
					iniciaDiaDaSemanaCorreto = true;
				}
				if(this.recorrencia.isOcorreAsSegundas() && cDataInicio.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
					iniciaDiaDaSemanaCorreto = true;
				}
				if(this.recorrencia.isOcorreAsTercas() && cDataInicio.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
					iniciaDiaDaSemanaCorreto = true;
				}
				if(this.recorrencia.isOcorreAsQuartas() && cDataInicio.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
					iniciaDiaDaSemanaCorreto = true;
				}
				if(this.recorrencia.isOcorreAsQuintas() && cDataInicio.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
					iniciaDiaDaSemanaCorreto = true;
				}
				if(this.recorrencia.isOcorreAsSextas() && cDataInicio.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
					iniciaDiaDaSemanaCorreto = true;
				}
				if(this.recorrencia.isOcorreAosSabados() && cDataInicio.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
					iniciaDiaDaSemanaCorreto = true;
				}
				
				
				if(! iniciaDiaDaSemanaCorreto){ // Se a data de inicio não é nenhum dia em que o evento ocorre.
					lista.addErro("A data inicial do evento não é uma data válida, pois não ocorre em um dos dias da semana escolhido.");
				}
				
			}
			
		}
		
		
		return lista;
	}
	
	//////// sets e gets  /////////
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
		if (dataFim == null) {
			setDataFim(dataInicio);
		}
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public RecorrenciaEvento getRecorrencia() {
		return recorrencia;
	}

	public void setRecorrencia(RecorrenciaEvento recorrencia) {
		if (recorrencia != null)
			recorrencia.setEvento(this);
		this.recorrencia = recorrencia;
	}

	public RecorrenciaEvento getExcecaoRecorrencia() {
		return excecaoRecorrencia;
	}

	public void setExcecaoRecorrencia(RecorrenciaEvento excecaoRecorrencia) {
		this.excecaoRecorrencia = excecaoRecorrencia;
	}

	
	public void setDiaTodo(boolean diaTodo) {
		this.diaTodo = diaTodo;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public Date getHorarioInicio() {
		return horarioInicio;
	}

	public void setHorarioInicio(Date horarioInicio) {
		this.horarioInicio = horarioInicio;
	}

	public Date getHorarioFim() {
		return horarioFim;
	}

	public void setHorarioFim(Date horarioFim) {
		this.horarioFim = horarioFim;
	}

	public Date getDataInicioOriginal() {
		return dataInicioOriginal;
	}

	public void setDataInicioOriginal(Date dataInicioOriginal) {
		this.dataInicioOriginal = dataInicioOriginal;
	}

	public Date getDataFimOriginal() {
		return dataFimOriginal;
	}

	public void setDataFimOriginal(Date dataFimOriginal) {
		this.dataFimOriginal = dataFimOriginal;
	}
	
	/**
	 * Retorna uma representação textual deste evento, informando a data e hora
	 * inicial e final, e o título do evento.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Formatador fmt = Formatador.getInstance();
		StringBuilder str = new StringBuilder();
		str.append(fmt.formatarData(dataInicio));
		str.append(" ");
		str.append(fmt.formatarHora(dataInicio));
		str.append(" a ");
		str.append(fmt.formatarHora(dataFim));
		if (isEventoRecorrente()) {
			str.append(" recorre até ");
			str.append(fmt.formatarData(recorrencia.getRepetirAte()));
		}
		str.append(": ");
		str.append(titulo);
		return str.toString();
	}

}

