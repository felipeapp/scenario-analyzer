package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validaOrdemTemporalDatas;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.FeriadoDao;
import br.ufrn.comum.dominio.Feriado;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioTurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dao.GerenciarTurmaDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.GrupoHorarios;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.jsf.SolicitacaoTurmaMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.TurmaGraduacaoMBean;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.stricto.jsf.TurmaStrictoSensuMBean;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosLatoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/** Controller responsável exclusivamente para configurar horário para uma turma.
 * @author Édipo Elder F. Melo
 *
 */
@SuppressWarnings("serial")
@Component("horarioTurmaBean")
@Scope("request")
public class HorarioTurmaMBean extends SigaaAbstractController<Turma> {
	/** Define o link para o formulário de definição de horário da turma. */
	public static final String JSP_HORARIOS = "/ensino/turma/horario_turma.jsp";
	/** Define o link para o formulário de definição de horário da turma, quando os horários podem ser flexíveis */
	public static final String JSP_HORARIO_FLEXIVEL = "/graduacao/turma/horario_flexivel.jsp";
	/** Define o link para o formulário de definição de horário de turmas, de graduação */
	private static final String JSP_HORARIOS_GRADUACAO = "/graduacao/horario_turma/horario_turma_graduacao.jsp";
	/** Valores setado no momento de definir o horário quando um componente permite flexibilidade de horário */
	private Date periodoFim;
	/** Valores setado no momento de definir o horário quando um componente permite flexibilidade de horário */
	private Date periodoInicio;
	/** Lista de horários da turma. */
	private List<Horario> horariosGrade;
	/** Matriz de horários marcados pelo usuário.*/
	private String[] horariosMarcados;
	/** DataModel usado na exibição da lista de {@link GrupoHorarios} */
	private DataModel modelGrupoHorarios = new ListDataModel();
	/** Lista com os Horários agrupados por período */
	private List<GrupoHorarios> grupoHorarios = new ArrayList<GrupoHorarios>();
	
	/** Mapa de horários utilizado quando é possível escolher mais de uma grade de horários */
	private HashMap<Unidade,Collection<Horario>> mapaHorarios = new HashMap<Unidade,Collection<Horario>>();
	
	/** Solicitação de turma a ser atendida. */
	private SolicitacaoTurma solicitacao;
	
	/** Unidade da grade de horário da turma. */
	private Unidade unidadeGrade = new Unidade();
	
	/** MBean a partir do qual se requistou a definição de horário da turma. */
	private OperadorHorarioTurma mBean;
	
	/** Nome da operação (Para utilização no título da página de busca do componente curricular) */
	private String tituloOperacao;
	/** Expressão de horário a ser convertida na grade de horários. */
	private String expressaoHorario;
	
	// Variáveis utilizadas no cadastro de horários de graduação.
	
	/** Data de início da turma */
	private Date dataTurmaInicio;
	/** Data de fim da turma */
	private Date dataTurmaFim;
	/** Porcentagem de aula em relação a sua carga horária */
	private int porcentagemAulas = 0;
	/** Armazena os horários os horários da turma que são alterados durante seu cadastro*/
	private ArrayList<HorarioTurma> novosHorarios;
	/** Horários escolhidos para criacão da turma em forma de String. */
	private List<String> horariosEscolhidos = null;
	/** Horários da página em formato de String */
	private String horariosString;
	/** Porcentagem máxima que o número de aulas deve possui em relação a carga horária da disciplina*/
	private int porcentagemMaxNumAulas = ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.PORCENTAGEM_MAX_NUM_AULAS_EM_RELACAO_CH_TURMA );
	/** Porcentagem mínima que o número de aulas deve possui em relação a carga horária da disciplina*/
	private int porcentagemMinNumAulas = ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.PORCENTAGEM_MIN_NUM_AULAS_EM_RELACAO_CH_TURMA );
	/** Mensagem de erro exibida quando a turma é de horário flexível. */
	private String mensagemErroHorario;
	/** Datas dos feriados da turma. O cálculo da poscentagem do número de aulas deve ignorar os feriados. */
	private List<Date> feriados;
	
	/** Construtor padrão. */
	public HorarioTurmaMBean() {
		clear();
	}
	
	/** Interpreta a expressão de horário informada pelo usuário.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/horario_turma.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public void parseExpressaoHorario(ActionEvent evt) throws ArqException, NegocioException {
		String expressao = this.expressaoHorario;
		HorarioDao dao = getDAO(HorarioDao.class);
		List<HorarioTurma> horarios = null;
		int idUnidade = getUnidadeGestora();
		String[] marcados = new String[1];
		horariosMarcados = null;
		try {
			horarios = HorarioTurmaUtil.parseCodigoHorarios(expressao, idUnidade, obj.getDisciplina().getNivel(), dao);
			for (HorarioTurma ht : horarios) {
				ht.setDataInicio(obj.getDataInicio());
				ht.setDataFim(obj.getDataFim());
			}
			marcados = HorarioTurmaUtil.parseHorarios(horarios, horariosGrade);
		} finally {
			// verifica se os horário foi totalmente convertido
			if (isEmpty(marcados) || isEmpty(horarios)) {
				addMensagemErro("Expressão de horário inválida");
				carregaHorariosExpressao(horarios);
				return;
			}
			horariosMarcados = marcados;
			Turma turmaTemp = UFRNUtils.deepCopy(obj);
			turmaTemp.setDataInicio(obj.getDataInicio());
			turmaTemp.setDataFim(obj.getDataFim());
			turmaTemp.setHorarios(new ArrayList<HorarioTurma>());
			List<HorarioTurma> listaHorariosMarcados = HorarioTurmaUtil.extrairHorariosEscolhidos(horariosMarcados, turmaTemp, horariosGrade, periodoInicio, periodoFim);
			for (HorarioTurma ht : listaHorariosMarcados) {
				ht.setDataInicio(obj.getDataInicio());
				ht.setDataFim(obj.getDataFim());
				turmaTemp.getHorarios().add(ht);
			}
			String expressaoReversa = "";
			try {
				expressaoReversa = HorarioTurmaUtil.formatarCodigoHorarios(turmaTemp);
			} catch (Exception e) {
				// Ignora o erro
			}
			if (!isEmpty(expressaoReversa)) {
				if (obj.getDisciplina().isPermiteHorarioFlexivel() && expressaoReversa.indexOf('(') > 1)
					expressaoReversa = expressaoReversa.substring(0, expressaoReversa.indexOf('(') - 1);
				if (!StringUtils.removeEspacosRepetidos(expressaoReversa).trim().equalsIgnoreCase(StringUtils.removeEspacosRepetidos(expressao).trim()))
					addMensagemWarning("A expressão de horário informada e a expressão de horário resultante não são exatamente iguais.");
				List<HorarioTurma> horariosReverso = HorarioTurmaUtil.parseCodigoHorarios(expressaoReversa, idUnidade, obj.getDisciplina().getNivel(), dao);
				if (horarios != null) Collections.sort(horarios);
				if (horariosReverso != null) Collections.sort(horariosReverso);
				if (horarios != null && horariosReverso != null && horarios.size() == horariosReverso.size()) {
					for (int i = 0; i < horarios.size(); i++ ) {
						if (horarios.get(i).equals(horariosReverso.get(i)))
							addMensagemWarning("O horário informado ("
							+ expressao
							+ ") não foi totalmente convertido no horário da grade. Por favor, verifique a expressão do horário informada.");
					}
				}
			} else {
				addMensagemErro("Expressão de horário inválida");
			}
			// verifica os domingos
			boolean domingo = false;
			if (horarios != null) {
				Iterator<HorarioTurma> iterator = horarios.iterator();
				while (iterator.hasNext()) {
					HorarioTurma horario = iterator.next();
					if (!getHabilitarDomingo() && Integer.parseInt(""+horario.getDia()) == Calendar.SUNDAY) {
						domingo = true;
						iterator.remove();
					}
				}
			}
			if (domingo) {
				horariosMarcados = HorarioTurmaUtil.parseHorarios(horarios, horariosGrade);
				addMensagemErro("Não é possível definir o horário para o domingo.");
			}
			carregaHorariosExpressao(horarios);	
		}
	}

	/**
	 * Carrega os horários da expressão para turmas de graduação;
	 * Método não invocado por JSPs
	 * @param horarios
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	private void carregaHorariosExpressao(List<HorarioTurma> horarios) throws DAOException, NegocioException {
		if (obj.isGraduacao()){
			
			novosHorarios = new ArrayList<HorarioTurma>();
			novosHorarios.addAll(horarios);
			horariosEscolhidos = new ArrayList<String>();
			obj.setHorarios(new ArrayList<HorarioTurma>());

			if (horariosMarcados != null)
				for (int i = 0; i<horariosMarcados.length; i++)
					horariosEscolhidos.add(horariosMarcados[i]);
			calcularPorcentagem();
		}
	}
	
	/** Limpa os atributos deste controller. */
	protected void clear() {
		gerarGradeHorarios();
		periodoInicio = null;
		periodoFim = null;
		grupoHorarios = new ArrayList<GrupoHorarios>();
		modelGrupoHorarios = new ListDataModel();
		solicitacao = null;
		setReadOnly(false);
	}

	/**
	 * Cancela a operação e volta para a listagem das turmas, caso a operação
	 * seja de alteração ou remoção de uma turma. <br/>
	 * Método não invocado por JSP´s.
	 */
	public String cancelar() {
		TurmaGraduacaoMBean graduacaoMBeam = (TurmaGraduacaoMBean) getMBean("turmaGraduacaoBean");
		if(graduacaoMBeam.getOperacaoTurma() == TurmaGraduacaoMBean.ATENDER_SOLICITACAO_TURMA){
			return graduacaoMBeam.cancelar();
		}
		SolicitacaoTurmaMBean solicitacaoMBeam = (SolicitacaoTurmaMBean) getMBean("solicitacaoTurma");
		if(solicitacaoMBeam.isAlterarSolicitacao()){
			return solicitacaoMBeam.cancelar();
		}
		TurmaStrictoSensuMBean strictoBean = getMBean("turmaStrictoSensuBean");
		if(strictoBean.getConfirmButton().equals("Alterar")) {
			BuscaTurmaMBean mBean = getMBean("buscaTurmaBean");
			try {
				return mBean.buscarGeral();
			} catch (DAOException e) {
				return super.cancelar();
			}
		}
		return super.cancelar();
	}

	// Início do fluxo da seleção dos horários
	
	/** Inicia a definição de horário da turma.
	 * <br/>Método não invocado por JSP´s.
	 * @param mBean
	 * @param tituloOperacao
	 * @param turma
	 * @return
	 */
	public String populaHorarioTurma(OperadorHorarioTurma mBean, String tituloOperacao, Turma turma){
		
		FeriadoDao feriadoDao = null;
		
		try{
			clear();			
			this.mBean = mBean;
			this.tituloOperacao = tituloOperacao;
			this.obj = turma;
			mapaHorarios.clear();
			// Evitando NPE ao realizar o parse dos horários marcados.
			if (obj.getHorarios() == null) 
				obj.setHorarios(new ArrayList<HorarioTurma>());
			if (obj.getDisciplina().isPermiteHorarioFlexivel()) {
				grupoHorarios = HorarioTurmaUtil.agruparHorarioPorPeriodo(obj.getHorarios());
				modelGrupoHorarios = new ListDataModel(grupoHorarios);
			} else {			
				
				Unidade unidade = new Unidade();
				Unidade primeiraUnidadeGrade = new Unidade(0);
										
				if(turma.getSolicitacao() != null && turma.getSolicitacao().getId()!=0 && turma.getHorarios().isEmpty()) {
					unidade = turma.getSolicitacao().getUnidade();												
				}
				else if(turma.getHorarios() != null && !turma.getHorarios().isEmpty()) {
					Horario h = getGenericDAO().findAndFetch(turma.getHorarios().iterator().next().getHorario().getId(), Horario.class, "unidade");
					unidade = h.getUnidade();					
				}else {
					procuraHorariosNaUnidade(turma.getDisciplina().getUnidade(),turma.getDisciplina().getNivel(), mapaHorarios,primeiraUnidadeGrade);														
					unidade = primeiraUnidadeGrade;					
				}						
				
				unidadeGrade.setId(unidade.getId());
				if(mapaHorarios.isEmpty())				
					procuraHorariosNaUnidade(turma.getDisciplina().getUnidade(),turma.getDisciplina().getNivel(), mapaHorarios,primeiraUnidadeGrade);
				horariosGrade = (List<Horario>)mapaHorarios.get(getGenericDAO().findByPrimaryKey(unidade.getId(), Unidade.class));
				horariosMarcados = HorarioTurmaUtil.parseHorarios(obj.getHorarios(), horariosGrade);			
				
			if (obj.getDisciplina().isPermiteHorarioFlexivel())				
					periodoInicio = obj.getDataInicio();
			}
			
			this.dataTurmaInicio = obj.getDataInicio();
			this.dataTurmaFim = obj.getDataFim();
			
			if (obj.isGraduacao()){
				
				// Carrega as variáveis que guardam os horários 
				novosHorarios = new ArrayList<HorarioTurma>();
				novosHorarios.addAll(obj.getHorarios());
				horariosEscolhidos = new ArrayList<String>();
				if (horariosMarcados != null)
					for (int i = 0; i<horariosMarcados.length; i++)
						horariosEscolhidos.add(horariosMarcados[i]);
				
				// Carrega a lista de feriados no perído letivo para não sobrecarregar o ajax com consultas.
				feriadoDao = getDAO(FeriadoDao.class, Sistema.COMUM);
				
				CalendarioAcademico cal = null;
				try {
					cal = CalendarioAcademicoHelper.getCalendario(obj);
				} catch (ConfiguracaoAmbienteException e) {
					erros.addErro(e.getMessage());
					return null;
				}
				
				Municipio m = obj.getDisciplina().getUnidade().getMunicipio();
				UnidadeFederativa uf = null; 
				if (m != null) {
					m = getGenericDAO().refresh(m);
					uf = m.getUnidadeFederativa();
				} else {
					addMensagemErro("A unidade " + obj.getDisciplina().getUnidade() + " não possui município cadastrado. Por favor, entre em contato com a administração para corrigir a falta desta informação.");
					return null;
				}
				
				Date inicio = null;
				Date fim = null;
				
				if (obj.isTurmaRegular()){
					inicio = cal.getInicioPeriodoLetivo();
					fim = cal.getFimPeriodoLetivo();
				} else {
					inicio = cal.getInicioFerias();
					fim = cal.getFimFerias();
				}				
				
				List<Feriado> feriadosPeriodoLetivo = feriadoDao.findByPeriodoFeriadosLocalidade(inicio, fim, m.getId(), uf.getId());
				
				feriados = new ArrayList<Date>();
				for (Feriado f : feriadosPeriodoLetivo)
					feriados.add(f.getDataFeriado());
				
				// Calcula a porcentagem de aulas na turma.
				calcularPorcentagem();
				
			}	
		} catch (Exception e) {
			return tratamentoErroPadrao(e,e.getMessage());
		} finally {
			if (feriadoDao != null)
				feriadoDao.close();
		}
		
		if (!obj.isGraduacao())
			return forward(JSP_HORARIOS);
		else
			return forward(JSP_HORARIOS_GRADUACAO);
	}
		
	/**
	 * Diz se é permitido ou não a escolha da grade de horários ao definir os horários de uma turma.
	 * 
	 * FUTURAMENTE PERMITIR QUE CADA UNIDADE DECIDA SE PERMITE OU NÃO
	 * A ESCOLHA DA GRADE. LOGO ESTE METODO NAO SERA NECESSARIO. COMO
	 * NivelEnsino ESTA EM ENTIDADES_COMUNS MANTER O METODO AQUI POR ENQUANTO.
	 *  
	 * @return
	 */
	private Boolean permiteFlexibilidadeGradeHorarios(Character nivelEnsino) {
		
		Boolean permissao = false;
		
		if( NivelEnsino.isAlgumNivelStricto(nivelEnsino) ) {
			permissao = ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.PERMITE_ESCOLHA_GRADE_HORARIOS);
		}
		else if(NivelEnsino.GRADUACAO == nivelEnsino ) {
			permissao = ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.PERMITE_ESCOLHA_GRADE_HORARIOS);
		}
		else if(NivelEnsino.LATO == nivelEnsino ) {
			permissao = ParametroHelper.getInstance().getParametroBoolean(ParametrosLatoSensu.PERMITE_ESCOLHA_GRADE_HORARIOS);
		}
		else if(NivelEnsino.TECNICO == nivelEnsino ) {
			permissao = ParametroHelper.getInstance().getParametroBoolean(ParametrosTecnico.PERMITE_ESCOLHA_GRADE_HORARIOS);
		}			
			
		return permissao;
	}

	/**
	 * Retorna para o controler que solicitou a definição de horário da turma. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/horario_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarPassoAnterior(){
		return mBean.definicaoHorarioTurmaVoltar();
	}

	/**
	 * redireciona o usuário para a tela de horários.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String telaHorarios(){
		return forward(JSP_HORARIOS);
	}

	/**
	 * Verifica a definição de horário da turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/horario_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String submeterHorarios() throws ArqException, NegocioException{
		obj.setDisciplina(getGenericDAO().findByPrimaryKey(obj.getDisciplina().getId(), ComponenteCurricular.class));
		
		if (obj.isGraduacao()){
			// Verifica se as datas inicial ou final da turma foram adicionadas antes da validação.
			if (dataTurmaInicio == null || dataTurmaFim == null){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Início-Fim");
				return null;
			}
		
			obj.setDataInicio(dataTurmaInicio);
			obj.setDataFim(dataTurmaFim);

		}
		
		if (obj.getDisciplina().isPermiteHorarioFlexivel())
			submeterHorariosFlexivel();
		else
			submeterHorariosNormal();
				
		GerenciarTurmaDao gerDao = getDAO( GerenciarTurmaDao.class );
		HorarioTurmaDao horarioTurmaDao = getDAO( HorarioTurmaDao.class );
		
		Collection<Turma> subturmas = gerDao.findSubturmasByTurma(obj.getTurmaAgrupadora());		
		if(permiteFlexibilidadeGradeHorarios(obj.getDisciplina().getNivel()) && obj.getDisciplina().isAceitaSubturma() && !subturmas.isEmpty()) {
			Unidade unidadeBanco = subturmas.iterator().next().getHorarios().iterator().next().getHorario().getUnidade();
			Iterator<HorarioTurma> iterator = obj.getHorarios().iterator();
			if (iterator.hasNext()) {
				Unidade unidadeInserindo = iterator.next().getHorario().getUnidade();
				if( unidadeInserindo.getId() != unidadeBanco.getId() ) {					
					addMensagemErro("Você selecionou uma turma agrupadora que utiliza a Grade de Horários do(a) " + unidadeBanco.getNome()+". Por favor selecione a Grade de Horários correta." );
					return null;
				}
			}
		}
		
		
		// Se for a criação de turmas de componentes que permitem sub-turmas e
		// não for a partir de uma
		// solicitação ao submeter os horários deve ser verificado se o horário
		// selecionado tem algum horário igual aos horários das sub-turmas
		if( solicitacao == null && obj.getDisciplina().isAceitaSubturma() && obj.getTurmaAgrupadora() != null ){
			if( !subturmas.contains(obj) )
				subturmas.add(obj);
			if(subturmas.size() > 1 && HorarioTurmaUtil.verificarChoqueHorario(subturmas).size() == 0){
				String msg = "Para adicionar esta subturma ao grupo de subturmas selecionado é necessário que haja pelo menos um horário em comum " +
				"entre todas as subturmas do grupo.";
		
				if (isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE))
					addMensagemWarning(msg);
				else
					addMensagemErro(msg);		
			}
		}
		
		// Se for alteração de turma, validar o choque de horário dos discentes
		// Se for administrador DAE, a validação é feita, mas não impede do usuário prosseguir.
		if( obj.getId() != 0 ){
			List<HorarioTurma> horarioOriginal = horarioTurmaDao.findByTurma(obj);
			
			// Indica se está inserindo o horário em uma turma que já existe pela primeira vez ou
			// se está modificando um horario que já existe
			// Nesses dois casos, é necessário verificar choque de horário nos discentes.
			boolean isHorarioNovoOuModificado = ValidatorUtil.isEmpty( horarioOriginal ) || !isEqualCollectionTransient(horarioOriginal, obj.getHorarios());
			
			if( isHorarioNovoOuModificado ) {
				TurmaValidator.validaHorariosDiscentesTurma(obj, getUsuarioLogado(),erros);
				TurmaValidator.validaHorariosTurmaPlanoMatricula(obj, erros);
			}
		}
		
		// Verifica choque de horário entre as disciplinas de uma mesma turma de ensino médio.
		if( obj.isMedio() ){
			TurmaValidator.validaHorariosDisciplinasTurmaMedio(obj, getUsuarioLogado(), erros);
		}
		
		if (obj.isGraduacao()){
							
			CalendarioAcademico cal = null;
			try {
				cal = CalendarioAcademicoHelper.getCalendario(obj);
			} catch (ConfiguracaoAmbienteException e) {
				erros.addErro(e.getMessage());
				return null;
			}
			
			if (obj.isTurmaFerias()) {
				validateRange(obj.getDataInicio(), cal.getInicioFerias(), cal.getFimFerias(), "Data de Início da Turma", erros);
				validateRange(obj.getDataFim(), cal.getInicioFerias(), cal.getFimFerias(), "Data de Fim da Turma", erros);
			} else {
				validateRange(obj.getDataInicio(), cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), "Data de Início da Turma", erros);
				validateRange(obj.getDataFim(), cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), "Data de Fim da Turma", erros);
			}
			
			if (obj.getSolicitacao() != null && obj.getSolicitacao().isTurmaEnsinoIndividual() && obj.getId() == 0) {
				// não valida horário, pois no cadastro não é definido horário para EI
			} 
			
			// valida se a data informada / alterada pelo chefe de departamento está dentro do período de férias
			if (obj.isTurmaFerias() && getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_TURMAS_UNIDADE)){
				CalendarioAcademico calTurma = CalendarioAcademicoHelper.getCalendario(obj);
				validateMinValue(obj.getDataInicio(), calTurma.getInicioFerias(), "Início", erros);
				validateMaxValue(obj.getDataFim(), calTurma.getFimFerias(), "Fim", erros);
			}
			
			if (hasErrors())
				return null;
	
			if (!isUserInRole(SigaaPapeis.DAE) && obj.getDataInicio() == null ) {
				if( obj.isTurmaRegular() )
					obj.setDataInicio(getCalendario().getInicioPeriodoLetivo());
				else if( obj.isTurmaFerias() )
					obj.setDataInicio(getCalendario().getInicioFerias());
			}
			if (!isUserInRole(SigaaPapeis.DAE) && obj.getDataFim() == null ) {
				if( obj.isTurmaRegular() )
					obj.setDataFim(getCalendario().getFimPeriodoLetivo());
				else if( obj.isTurmaFerias() )
					obj.setDataFim(getCalendario().getFimFerias());
			}
		}
		
		if (hasOnlyErrors()) 
			return null;
		
		if (obj.isGraduacao()){
			mBean.definePeriodosTurma(dataTurmaInicio, dataTurmaFim);
		}
		
		return mBean.defineHorariosTurma(obj.getHorarios());
	}
	
	/**
	 * Compara os dois horários desconsiderando os id's.
	 * 
	 * @param aSrc
	 * @param bSrc
	 * @return
	 */
    public boolean isEqualCollectionTransient(final List<HorarioTurma> aSrc, final List<HorarioTurma> bSrc) {
    	
    	List<HorarioTurma> copyA = new ArrayList<HorarioTurma>(aSrc);
    	List<HorarioTurma> copyB = new ArrayList<HorarioTurma>(bSrc);
    	
    	for (PersistDB p : copyA) {
			p.setId(0);
		}
    	
    	for (PersistDB p : copyB) {
			p.setId(0);
		}
    	
    	return CollectionUtils.isEqualCollection(copyA, copyB);
    }	
    
	/**
	 * Invocado para realizar operações específicas quando a turma possuir componente que permite horário flexível.
	 * 
	 * @see ComponenteCurricular#isPermiteHorarioFlexivel()
	 * @throws DAOException
	 */
	private void submeterHorariosFlexivel() throws DAOException {
		erros = new ListaMensagens();

		obj.setDescricaoHorario(HorarioTurmaUtil.formatarCodigoHorarios(obj));
		
		if (obj.isGraduacao())
			validarDatasInicioFimHorariosFlexiveis(obj.getHorarios(), dataTurmaInicio, dataTurmaFim);
		
		carregarPorcentagemAulas();
		
		// validar horários da turma
		TurmaValidator.validaHorarios(obj, erros, getUsuarioLogado());
		TurmaValidator.validaPeriodoHorario(obj, erros);

		// O administrador DAE pode mudar as datas início e fim da turma independente dos horários escolhidos.
		if (!erros.isErrorPresent() && !getAcessoMenu().isAdministradorDAE()){
			Date menorData = null;
			Date maiorData = null;
			if (obj.getHorarios() != null)
				for (HorarioTurma h : obj.getHorarios()){
					if (menorData == null || menorData.getTime() > h.getDataInicio().getTime())
						menorData = h.getDataInicio();
					if (maiorData == null || maiorData.getTime() < h.getDataFim().getTime())
						maiorData = h.getDataFim();
				}
			if (menorData != null)
				dataTurmaInicio = menorData;
			if (maiorData != null)
				dataTurmaFim = maiorData;
		}
	}
	
	/**
	 * Invocado para realizar operações específicas quando a turma NÃO possuir componente que permite horário flexível
	 * 
	 * @see ComponenteCurricular#isPermiteHorarioFlexivel()
	 * @throws DAOException
	 */
	private void submeterHorariosNormal() throws DAOException {
		erros = new ListaMensagens();

		String[] horariosEscolhidosArray = null;
		
		if (obj.isGraduacao()){
			int size = horariosEscolhidos != null && !horariosEscolhidos.isEmpty()? horariosEscolhidos.size()-1 : 0;
			horariosEscolhidosArray = new String [size];
			if ( mBean.isPodeAlterarHorarios() ) 
				horariosEscolhidosArray = horariosEscolhidos.toArray(horariosEscolhidosArray);
			else
				horariosEscolhidosArray = horariosMarcados;
			
			if (horariosEscolhidosArray != null) {
				HorarioTurmaUtil.formataHorarios(horariosEscolhidosArray, obj, horariosGrade, dataTurmaInicio, dataTurmaFim);
				obj.setDescricaoHorario(HorarioTurmaUtil.formatarCodigoHorarios(obj));
			}
		} else {
			if ( mBean.isPodeAlterarHorarios() ) 
				horariosEscolhidosArray = getCurrentRequest().getParameterValues("horEscolhidos");
			else
				horariosEscolhidosArray = horariosMarcados;
			
			if (horariosEscolhidosArray != null) {
				HorarioTurmaUtil.formataHorarios(horariosEscolhidosArray, obj, horariosGrade, obj.getDataInicio(), obj.getDataFim());
				obj.setDescricaoHorario(HorarioTurmaUtil.formatarCodigoHorarios(obj));
			}
		}
				
		if (horariosEscolhidosArray == null) {
			obj.setHorarios(new ArrayList<HorarioTurma>());
			obj.setDescricaoHorario("");
		}
		horariosMarcados = HorarioTurmaUtil.parseHorarios(obj.getHorarios(), horariosGrade);

		for (HorarioTurma h : obj.getHorarios()){
			h.setDataInicio(dataTurmaInicio);
			h.setDataFim(dataTurmaFim);
		}	
		
		carregarPorcentagemAulas();		
		// validar horários da turma
		TurmaValidator.validaHorarios(obj, erros, getUsuarioLogado());
	}
	
	/**
	 * Verifica se já foi adicionado uma horário na data indicada
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void validarPeriodoEscolhido() {
		
		validateRequired(periodoInicio, "Período Inicial do Horário", erros);
		validateRequired(periodoFim, "Período Final do Horário", erros);
		if (hasErrors()) return;
		if (periodoInicio.before(obj.getDataInicio()))
			erros.addErro("Data de Início anterior ao início do Período Letivo.");
		if (periodoInicio.after(obj.getDataFim()))
			erros.addErro("Data de Início posterior ao fim do Período Letivo.");
		if (periodoFim.before(obj.getDataInicio()))
			erros.addErro("Data de Fim anterior ao início do Período Letivo.");
		if (periodoFim.after(obj.getDataFim()))
			erros.addErro("Data de Fim posterior ao fim do Período Letivo.");
		validaOrdemTemporalDatas(periodoInicio, periodoFim, true, "Início e Fim do Horário", erros);
		if (hasErrors()) return;
		
		Collection<HorarioTurma> horariosAgrupadosPorData = CollectionUtils.select(obj.getHorarios(),new Predicate(){
			public boolean evaluate(Object arg) {
				HorarioTurma ht = (HorarioTurma) arg;
				return CalendarUtils.isIntervalosDeDatasConflitantes(ht.getDataInicio(), ht.getDataFim(), periodoInicio, periodoFim);
			}
		});
		
		if (!horariosAgrupadosPorData.isEmpty())
			addMensagemErro("Não deve haver choque de datas nos períodos de horários escolhidos.");
	}
	
	/**
	 * Quando a turma possuir horários flexíveis, este método será invocado para
	 * ir adicionando os horários.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/horarios.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws Exception
	 */
	public void adicionarHorario (ActionEvent evt) throws Exception {
		
		validarPeriodoEscolhido();
		
		// Criando uma cópia para retornar ao estado anterior em caso de erro.
		Turma turmaTemp = UFRNUtils.deepCopy(obj);
		
		String[] horariosEscolhidosArray = null;
		
		if (obj.isGraduacao()){
			int size = horariosEscolhidos != null && !horariosEscolhidos.isEmpty()? horariosEscolhidos.size()-1 : 0;
			horariosEscolhidosArray = new String [size];
			if ( mBean.isPodeAlterarHorarios() ) {
				horariosEscolhidosArray = horariosEscolhidos.toArray(horariosEscolhidosArray);
			}
			// Limpa a grade para o próximo horário.
			horariosEscolhidos = new ArrayList<String>();
			expressaoHorario = null;
			horariosMarcados = null;
		} else {
			if ( mBean.isPodeAlterarHorarios() ) {
				horariosEscolhidosArray = getCurrentRequest().getParameterValues("horEscolhidos");
			}
		}

		List<HorarioTurma> listaHorariosEscolhidos = null;
		if (horariosEscolhidosArray != null) {
			listaHorariosEscolhidos = HorarioTurmaUtil.extrairHorariosEscolhidos(horariosEscolhidosArray, turmaTemp, horariosGrade, periodoInicio, periodoFim);
			turmaTemp.getHorarios().addAll(listaHorariosEscolhidos);
			turmaTemp.setDescricaoHorario(HorarioTurmaUtil.formatarCodigoHorarios(turmaTemp));
		}else{
			turmaTemp.setHorarios(new ArrayList<HorarioTurma>());
			turmaTemp.setDescricaoHorario("");
		}
		
		if(hasErrors()) {
			if (listaHorariosEscolhidos != null && !listaHorariosEscolhidos.isEmpty())
				horariosMarcados = HorarioTurmaUtil.parseHorarios(listaHorariosEscolhidos, horariosGrade);			
			return;
		}
		

		// validar horários da turma
		TurmaValidator.validaHorarios(turmaTemp, erros, getUsuarioLogado());
		
		if (hasErrors())
			return;

		// Aplicando alterações
		
		float numAulasTotal = HorarioTurmaUtil.calcularNumAulas(turmaTemp.getHorarios(),feriados);
		porcentagemAulas = getPorcentagemAulas(numAulasTotal);
		
		obj = turmaTemp;
		turmaTemp = null;
		Collections.sort(grupoHorarios, HorarioTurmaUtil.comparatorGrupoHorarios);
		modelGrupoHorarios = new ListDataModel(getPeriodosHorariosEscolhidos());
		periodoInicio = CalendarUtils.adicionaUmDia(periodoFim);
		periodoFim = null;
		addMensagemInformation("Período Adicionado com Sucesso.");
	}
	
	/**
	 * Retorna os horários agrupados por período
	 * 
	 * @return
	 */
	private List<GrupoHorarios> getPeriodosHorariosEscolhidos() {
		grupoHorarios = new ArrayList<GrupoHorarios>();
		grupoHorarios = HorarioTurmaUtil.agruparHorarioPorPeriodo(obj.getHorarios());
		
		return grupoHorarios;
	}

	/**
	 * Remove da coleção (e da view) o grupo de horário escolhido na view.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/horarios.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public void removerPeriodoHorarioFlexivel(ActionEvent evt) throws DAOException, NegocioException {
		
		if (!modelGrupoHorarios.isRowAvailable()) {
			addMensagemErro("Horário já foi removido");
			return;
		}
		
		final GrupoHorarios grupo = (GrupoHorarios) modelGrupoHorarios.getRowData();
		
		@SuppressWarnings("unchecked")
		Collection<HorarioTurma> horariosSelecionados = CollectionUtils.select(obj.getHorarios(),new Predicate(){
			public boolean evaluate(Object arg) {
				HorarioTurma ht = (HorarioTurma) arg;
				if (ht.getDataInicio().equals(grupo.getPeriodo().getInicio()) && ht.getDataFim().equals( grupo.getPeriodo().getFim() ))
					return true;
				return false;
			}
		});
		
		obj.getHorarios().removeAll(horariosSelecionados);
		float numAulasTotal = HorarioTurmaUtil.calcularNumAulas(obj.getHorarios(),feriados);	
		porcentagemAulas = getPorcentagemAulas(numAulasTotal);
		grupoHorarios.remove(grupo);
		modelGrupoHorarios = new ListDataModel(grupoHorarios);
		horariosMarcados = null;
	}
	
	/**
	 * Gera a grade de horários para popular a JSP de acordo com a unidade gestora e nível do usuário.
	 */
	private void gerarGradeHorarios() {
		try {
			HorarioDao dao = getDAO(HorarioDao.class);
			if (horariosGrade == null || horariosGrade.size() == 0)
				horariosGrade = (List<Horario>) dao.findAtivoByUnidade(new Unidade(getUnidadeGestora()), getNivelEnsino());
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
	}

	/**
	 * Define o período de início e fim do horário a partir do início e fim do
	 * período da turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/horario_turma.jsp</li>
	 * </ul>
	 * 
	 */
	public void periodoCompleto(){
		periodoInicio = obj.getDataInicio();
		periodoFim = obj.getDataFim();
	}
	
	public Collection<SelectItem> getAllGradesHorario(){
		return toSelectItems(mapaHorarios.keySet(), "id", "nome");
	}

	/**
	 * Carrega a grade de horários. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/horario_turma.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public void carregarGrade(ValueChangeEvent e) throws DAOException, SegurancaException {
		if((Integer)e.getNewValue() != 0) {		
		Unidade und = getGenericDAO().findByPrimaryKey((Integer)e.getNewValue(), Unidade.class);
		horariosGrade = (List<Horario>) mapaHorarios.get(und);
		}
	}
	
	public boolean isMostrarOpcaoMudarGradeHorarios() {
		return mapaHorarios.size() > 1  &&  isPodeAlterarHorarios() && permiteFlexibilidadeGradeHorarios(getNivelEnsino());
	}
	
	/** Retorna a turma selecionada.
	 * @return
	 * @throws DAOException
	 */
	public Turma getTurma() throws DAOException {
		int id = getParameterInt("id", 0);
		return getGenericDAO().findByPrimaryKey(id, Turma.class);
	}
	
	/**
	 * 
	 * Faz uma varredura recursivamente por horários disponíveis para o cadastro de turmas.
	 * Observe que a base da recursão obriga o retorno do método ao chegar na UFRN. <br/>Método não invocado por JSP´s.
	 * 
	 * @param unidade
	 * @param todosOsHorarios
	 * @return
	 */
	public HashMap<Unidade,Collection<Horario>> procuraHorariosNaUnidade(Unidade unidade, char nivel, HashMap<Unidade,Collection<Horario>> mapaHorarios, Unidade primeiraUnidadeGrade) {
		HorarioDao dao = getDAO(HorarioDao.class);
		try {		
			getGenericDAO().clearSession();
			unidade = getGenericDAO().findAndFetch(unidade.getId(), Unidade.class, "unidadeResponsavel");			
			Collection<Horario> horariosDaUnidade = dao.findAtivoByUnidade(unidade, nivel);
			
			if(horariosDaUnidade != null && !horariosDaUnidade.isEmpty()) {				
				if(mapaHorarios.isEmpty())
					primeiraUnidadeGrade.setId(unidade.getId());				
				mapaHorarios.put(unidade, horariosDaUnidade);				
			}
			
			//Se a unidade não for responsável por ela mesma e o mapa estiver vazio.			
			if(unidade.getId() != unidade.getUnidadeResponsavel().getId())				
				procuraHorariosNaUnidade(unidade.getUnidadeResponsavel(),nivel,mapaHorarios,primeiraUnidadeGrade);
			
			
			return mapaHorarios;
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		return null;
	}
	
	/** Retorna o nível de ensino em uso no subsistema atual.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#getNivelEnsino()
	 */
	@Override
	public char getNivelEnsino() {
		if( isPortalGraduacao())
			return NivelEnsino.GRADUACAO;
		return super.getNivelEnsino();
	}

	public Date getPeriodoFim() {
		return periodoFim;
	}

	public void setPeriodoFim(Date periodoFim) {
		this.periodoFim = periodoFim;
	}

	public Date getPeriodoInicio() {
		return periodoInicio;
	}

	public void setPeriodoInicio(Date periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	public DataModel getModelGrupoHorarios() {
		return modelGrupoHorarios;
	}

	public void setModelGrupoHorarios(DataModel modelGrupoHorarios) {
		this.modelGrupoHorarios = modelGrupoHorarios;
	}

	public List<GrupoHorarios> getGrupoHorarios() {
		return grupoHorarios;
	}

	public void setGrupoHorarios(List<GrupoHorarios> grupoHorarios) {
		this.grupoHorarios = grupoHorarios;
	}

	public SolicitacaoTurma getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(SolicitacaoTurma solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getTituloOperacao() {
		return tituloOperacao;
	}

	public void setTituloOperacao(String tituloOperacao) {
		this.tituloOperacao = tituloOperacao;
	}
	
	/** Retorna a lista de horários da turma. 
	 * @return
	 */
	public List<Horario> getHorariosGrade() {
		return horariosGrade;
	}

	/** Seta a lista de horários da turma.
	 * @param horariosGrade
	 */
	public void setHorariosGrade(List<Horario> horariosGrade) {
		this.horariosGrade = horariosGrade;
	}

	/** Retorna a matriz de horários marcados pelo usuário.
	 * @return
	 */
	public String[] getHorariosMarcados() {
		return horariosMarcados;
	}

	/** Seta a matriz de horários marcados pelo usuário.
	 * @param horariosMarcados
	 */
	public void setHorariosMarcados(String[] horariosMarcados) {
		this.horariosMarcados = horariosMarcados;
	}
	
	public boolean isPodeAlterarHorarios(){
		return mBean != null && mBean.isPodeAlterarHorarios();
	}

	public HashMap<Unidade, Collection<Horario>> getMapaHorarios() {
		return mapaHorarios;
	}

	public void setMapaHorarios(HashMap<Unidade, Collection<Horario>> mapaHorarios) {
		this.mapaHorarios = mapaHorarios;
	}

	public Unidade getUnidadeGrade() {
		return unidadeGrade;
	}

	public void setUnidadeGrade(Unidade unidadeGrade) {
		this.unidadeGrade = unidadeGrade;
	}

	public String getExpressaoHorario() {
		return expressaoHorario;
	}

	public void setExpressaoHorario(String expressaoHorario) {
		this.expressaoHorario = expressaoHorario;
	}
	
	public void setDataTurmaInicio(Date dataTurmaInicio) {
		this.dataTurmaInicio = dataTurmaInicio;
	}

	public Date getDataTurmaInicio() {
		return dataTurmaInicio;
	}

	public void setDataTurmaFim(Date dataTurmaFim) {
		this.dataTurmaFim = dataTurmaFim;
	}

	public Date getDataTurmaFim() {
		return dataTurmaFim;
	}
	
	// Métodos Chamados pela interface
	
	/**
	 * Calcula os horários da turma e a sua porcentagem em relação a carga horária do componente
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/horario_turma_graducao.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException 
	 * @throws SegurancaException
	 */
	public void calcularHorarioTurma(ActionEvent evt) throws DAOException {
		
		String escolhido = getParameter("linha");
		int index = horariosEscolhidos.indexOf(escolhido);

		// Adicionao ou remove o horário
		if (index == -1)
			horariosEscolhidos.add(escolhido);
		else
			horariosEscolhidos.remove(escolhido);
			
		String[] horariosEscolhidosArray;
		Collections.sort(horariosEscolhidos);
		if (!isEmpty(horariosEscolhidos))
			horariosEscolhidosArray = new String [horariosEscolhidos.size()-1];
		else
			horariosEscolhidosArray = new String [0];
		
		// Carrega os objetos que controlam os horários
		horariosEscolhidosArray = horariosEscolhidos.toArray(horariosEscolhidosArray);
		ArrayList<HorarioTurma> horariosAntigos = new ArrayList<HorarioTurma>();
		horariosAntigos.addAll(obj.getHorarios());
		HorarioTurmaUtil.formataHorarios(horariosEscolhidosArray, obj, horariosGrade, obj.getDataInicio(), obj.getDataFim());
		
		novosHorarios =  new ArrayList<HorarioTurma>();
		novosHorarios.addAll(obj.getHorarios());
		obj.setHorarios(horariosAntigos);
		
		// Atualiza a porcentagem
		if (!obj.getDisciplina().isPermiteHorarioFlexivel())
			calcularPorcentagem();
		
	}
	
	/**
	 * Calcula a data final da turma de acordo com uma porcentagem referenta a carga horária da disciplina. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/horario_turma_graducao.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException 
	 * @throws SegurancaException
	 */
	public void calcularDataFim (ActionEvent evt) throws DAOException {
		calcularDataFim();	
	}
	
	/**
	 * Calcula a data final da turma de acordo com uma porcentagem referenta a carga horária da disciplina. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/horario_turma_graducao.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException 
	 * @throws SegurancaException
	 */
	private void calcularDataFim() throws DAOException {
		if (porcentagemAulas > 0 && !isEmpty(novosHorarios)){
			
			 float numAulasCH = obj.getChTotalTurma() * 60 / getParametrosAcademicos().getMinutosAulaRegular();
			 float numAulasDataFim = porcentagemAulas * numAulasCH / 100;
			 float numAulasAtual = HorarioTurmaUtil.calcularNumAulas(novosHorarios,dataTurmaInicio,dataTurmaFim,feriados);
			 Date dataFinal = dataTurmaFim;
			 
			 if (numAulasAtual == numAulasDataFim){
				 dataTurmaFim = dataFinal;
				 return;
			 }
			 if (numAulasAtual < numAulasDataFim){
				 while (numAulasAtual < numAulasDataFim){
					 dataFinal = CalendarUtils.adicionaDias(dataFinal, 1);
					 numAulasAtual = HorarioTurmaUtil.calcularNumAulas(novosHorarios, dataTurmaInicio, dataFinal,feriados);
				 }
			 }
			 if (numAulasAtual > numAulasDataFim){
				 while (numAulasAtual > numAulasDataFim){
					 dataFinal = CalendarUtils.subtraiDias(dataFinal, 1);
					 numAulasAtual = HorarioTurmaUtil.calcularNumAulas(novosHorarios, dataTurmaInicio, dataFinal,feriados);
				 }
			 }	
			 		 
			 dataTurmaFim = dataFinal;
		}
	}

	/**
	 * Calcula a porcentagem do número de aulas em relação a carga horária da disciplina. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/horario_turma_graducao.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException 
	 * @throws SegurancaException
	 */
	public void calcularPorcentagem (ActionEvent evt) throws DAOException {
		calcularPorcentagem();	
	}
	
	/**
	 * Calcula a porcentagem do número de aulas em relação a carga horária da disciplina. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/horario_turma_graducao.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException 
	 * @throws NegocioException 
	 * @throws SegurancaException
	 */
	private void calcularPorcentagem() throws DAOException {
		if (dataTurmaInicio == null || dataTurmaFim == null)
			porcentagemAulas = 0;
		else {
			
			float numAulasAtual = 0;
			// Os horários flexíveis são adicionados a turma 
			if (obj.getDisciplina().isPermiteHorarioFlexivel()){
				
				validarDatasInicioFimHorariosFlexiveis(obj.getHorarios(),dataTurmaInicio,dataTurmaFim);			
				numAulasAtual = HorarioTurmaUtil.calcularNumAulasTurmaHorarioFlexivel(obj.getHorarios(),dataTurmaInicio,dataTurmaFim);
				
			} else
				numAulasAtual = HorarioTurmaUtil.calcularNumAulas(novosHorarios,dataTurmaInicio,dataTurmaFim,feriados);
			
			porcentagemAulas = getPorcentagemAulas(numAulasAtual);
		}	
	}
	
	/**
	 * Retorna a porcentagem de aulas cadastradas no horário da turma em relação a número de aulas do componente.
	 * 
	 * Primeiro encontra o número de aulas que a turma deve ter com sua CH. 
	 * Para isso transforma a CH em minutos e depois divide pelo parâmetro que define o número de minutos de cada aula (minutosAulaRegular). 
	 * Após isso, faz uma regra de três para calcular a porcentagem do número de aulas total em relação ao número de aulas definidos pela CH do componente.  
	 * 
	 * Método não invocado por JSP(s):
	 * 
	 * @param numAulasTotal: Número de aulas total calculado em cima dos horários cadastrados para a turma, removendo os feriados.
	 * @throws DAOException
	 */
	private int getPorcentagemAulas (float numAulasTotal) throws DAOException {
		float numAulasCH = obj.getChTotalTurma() * 60 / getParametrosAcademicos().getMinutosAulaRegular();
		porcentagemAulas = (int) (numAulasTotal * 100 / numAulasCH);
		return porcentagemAulas;
	}
	
	/**
	 * Carrega a porcentagem de aulas na turma.
	 * Método não invocado por JSPs
	 * @throws DAOException
	 */
	private void carregarPorcentagemAulas() throws DAOException {
		if (obj.getDisciplina().isExigeHorarioEmTurmas()){
			int numAulasTotal = HorarioTurmaUtil.calcularNumAulas(obj.getHorarios(),feriados);	
			// O número de aulas do horário/período deve ser maior ou igual (120% >= percentual >= 100%) que a carga horária do componente curricular. 
			float porcentagemAula =  getPorcentagemAulas(numAulasTotal);
			// Seta a porcentagem de aulas para fazer a validação no Validator.
			obj.setPorcentagemAulas(porcentagemAula);
		}
	}
	
	/**
	 * Verifica se a data inicial e final da turma contém os horários flexíveis. <br />
	 * Ou seja, se a turma possuir um horário flexível que vá até 30/06 a data final da turma deve ser maior ou igual que 30/06.
	 * Isso acontece porque caso a data final da turma for inferior a data final do horário flexível será necessário cortar o horário flexível.
	 * O que pode ocasionar problemas na interface da tela de seleção de horários.  
	 * 
	 * Método não invocado por JSP(s):
	 * 
	 * @param e
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	private void validarDatasInicioFimHorariosFlexiveis(List<HorarioTurma> horarios, Date dataTurmaInicio, Date dataTurmaFim) {
		
		HorarioTurma menorHorarioAux = null;
		HorarioTurma maiorHorarioAux = null;
				
		for (HorarioTurma h : horarios){
			if ( menorHorarioAux == null || h.getDataInicio().getTime() < menorHorarioAux.getDataInicio().getTime() )
				menorHorarioAux = h;
			if ( maiorHorarioAux == null || h.getDataFim().getTime() > maiorHorarioAux.getDataFim().getTime())
				maiorHorarioAux = h;
		}
		
		String msgErroInicio = "";
		String msgErroFim = "";
		
		if (menorHorarioAux != null && dataTurmaInicio.getTime() > menorHorarioAux.getDataInicio().getTime() ){
			msgErroInicio = "A data início da turma não deve ser superior a data inicial dos horários flexíveis adicionados. Para aumentar a data-início da turma primeiro remova o horário flexível.";
			addMensagemErroAjax(msgErroInicio);
		}
		
		if (maiorHorarioAux != null && dataTurmaFim.getTime() < maiorHorarioAux.getDataFim().getTime()){
			msgErroFim = "A data final da turma não deve ser inferior a data final dos horários flexíveis adicionados. Para diminuir a data-fim da turma primeiro remova o horário flexível.";
			addMensagemErroAjax(msgErroFim);
		}
		
		if (!msgErroInicio.isEmpty() && !msgErroFim.isEmpty()) {
			// As duas mensagens estão preenchidas.
			mensagemErroHorario = msgErroInicio+"<br/>"+msgErroFim;
		} else {
			// Apenas uma mensagem está preenchida.
			mensagemErroHorario = msgErroInicio+msgErroFim;
		}	
		
	}
	
	/**
	 * Verifica se os dados estão de acordo com a JSP. Utilizado pra precaver erros quando o usuário utiliza o botão voltar.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/horario_turma_graducao.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public void controleDados (ActionEvent evt){
		if (!obj.getDisciplina().isPermiteHorarioFlexivel()) {
			String [] hs = null;
			
			if (horariosString.equals(""))
				hs = null;
			else	
				hs = horariosString.split(";"); 
			
			boolean igual = true;
			
			if (hs == null && horariosEscolhidos != null && !horariosEscolhidos.isEmpty())
				igual = false;
				
			if (hs != null)
				for (int i = 0; i < hs.length; i++) {
					boolean possuiHorario = false;
					for (String horario : horariosEscolhidos)
						if (horario.equals(hs[i]))
							possuiHorario = true;
					if (!possuiHorario)
						igual = false;
				}
			
			if (!igual){
				addMensagemErro("Você utilizou o botão voltar do browser causando inconsistência na operação." +
				" Reinicie o processo utilizando os links oferecidos pelo sistema.");
				redirectJSF(getSubSistema().getLink());
				return;
			}		
		}
	}
	
	/**
	 * Carrega o calendário correto para realizar análise das solicitações
	 * @throws DAOException
	 */
	protected CalendarioAcademico getCalendario() throws DAOException {
		CalendarioAcademico cal = getCalendarioVigente();
		if (getCursoAtualCoordenacao() != null && getCursoAtualCoordenacao().isProbasica()) {
			cal = CalendarioAcademicoHelper.getCalendario(getCursoAtualCoordenacao());
		}
		if (cal == null) {
			addMensagemErro("Erro ao carregar calendário acadêmico no ano-período informado: " + obj.getAnoPeriodo());
			return null;
		} else {
			return cal;
		}
	}
	
	public void setPorcentagemAulas(int porcentagemAulas) {
		this.porcentagemAulas = porcentagemAulas;
	}
	
	public int getPorcentagemAulas() {
		return porcentagemAulas;
	}
	
	public void setNovosHorarios(ArrayList<HorarioTurma> novosHorarios) {
		this.novosHorarios = novosHorarios;
	}

	public ArrayList<HorarioTurma> getNovosHorarios() {
		return novosHorarios;
	}

	public void setPorcentagemMinNumAulas(int porcentagemMinNumAulas) {
		this.porcentagemMinNumAulas = porcentagemMinNumAulas;
	}

	public int getPorcentagemMinNumAulas() {
		return porcentagemMinNumAulas;
	}

	public void setPorcentagemMaxNumAulas(int porcentagemMaxNumAulas) {
		this.porcentagemMaxNumAulas = porcentagemMaxNumAulas;
	}

	public int getPorcentagemMaxNumAulas() {
		return porcentagemMaxNumAulas;
	}

	/**
	 * Busca nos parâmetros da unidade gestora acadêmica se deve habilitar/desabilitar os horários do domingo.
	 * @return
	 * @throws DAOException
	 */
	public boolean getHabilitarDomingo() throws DAOException{
		return getParametrosAcademicos().isHabilitarHorariosDomingo();
	}

	public void setHorariosString(String horariosString) {
		this.horariosString = horariosString;
	}

	public String getHorariosString() {
		return horariosString;
	}

	public String getMensagemErroHorario() {
		return mensagemErroHorario;
	}

	public void setMensagemErroHorario(String mensagemErroHorario) {
		this.mensagemErroHorario = mensagemErroHorario;
	}

	public void setFeriados(List<Date> feriados) {
		this.feriados = feriados;
	}

	public List<Date> getFeriados() {
		return feriados;
	}

}
