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

/** Controller respons�vel exclusivamente para configurar hor�rio para uma turma.
 * @author �dipo Elder F. Melo
 *
 */
@SuppressWarnings("serial")
@Component("horarioTurmaBean")
@Scope("request")
public class HorarioTurmaMBean extends SigaaAbstractController<Turma> {
	/** Define o link para o formul�rio de defini��o de hor�rio da turma. */
	public static final String JSP_HORARIOS = "/ensino/turma/horario_turma.jsp";
	/** Define o link para o formul�rio de defini��o de hor�rio da turma, quando os hor�rios podem ser flex�veis */
	public static final String JSP_HORARIO_FLEXIVEL = "/graduacao/turma/horario_flexivel.jsp";
	/** Define o link para o formul�rio de defini��o de hor�rio de turmas, de gradua��o */
	private static final String JSP_HORARIOS_GRADUACAO = "/graduacao/horario_turma/horario_turma_graduacao.jsp";
	/** Valores setado no momento de definir o hor�rio quando um componente permite flexibilidade de hor�rio */
	private Date periodoFim;
	/** Valores setado no momento de definir o hor�rio quando um componente permite flexibilidade de hor�rio */
	private Date periodoInicio;
	/** Lista de hor�rios da turma. */
	private List<Horario> horariosGrade;
	/** Matriz de hor�rios marcados pelo usu�rio.*/
	private String[] horariosMarcados;
	/** DataModel usado na exibi��o da lista de {@link GrupoHorarios} */
	private DataModel modelGrupoHorarios = new ListDataModel();
	/** Lista com os Hor�rios agrupados por per�odo */
	private List<GrupoHorarios> grupoHorarios = new ArrayList<GrupoHorarios>();
	
	/** Mapa de hor�rios utilizado quando � poss�vel escolher mais de uma grade de hor�rios */
	private HashMap<Unidade,Collection<Horario>> mapaHorarios = new HashMap<Unidade,Collection<Horario>>();
	
	/** Solicita��o de turma a ser atendida. */
	private SolicitacaoTurma solicitacao;
	
	/** Unidade da grade de hor�rio da turma. */
	private Unidade unidadeGrade = new Unidade();
	
	/** MBean a partir do qual se requistou a defini��o de hor�rio da turma. */
	private OperadorHorarioTurma mBean;
	
	/** Nome da opera��o (Para utiliza��o no t�tulo da p�gina de busca do componente curricular) */
	private String tituloOperacao;
	/** Express�o de hor�rio a ser convertida na grade de hor�rios. */
	private String expressaoHorario;
	
	// Vari�veis utilizadas no cadastro de hor�rios de gradua��o.
	
	/** Data de in�cio da turma */
	private Date dataTurmaInicio;
	/** Data de fim da turma */
	private Date dataTurmaFim;
	/** Porcentagem de aula em rela��o a sua carga hor�ria */
	private int porcentagemAulas = 0;
	/** Armazena os hor�rios os hor�rios da turma que s�o alterados durante seu cadastro*/
	private ArrayList<HorarioTurma> novosHorarios;
	/** Hor�rios escolhidos para criac�o da turma em forma de String. */
	private List<String> horariosEscolhidos = null;
	/** Hor�rios da p�gina em formato de String */
	private String horariosString;
	/** Porcentagem m�xima que o n�mero de aulas deve possui em rela��o a carga hor�ria da disciplina*/
	private int porcentagemMaxNumAulas = ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.PORCENTAGEM_MAX_NUM_AULAS_EM_RELACAO_CH_TURMA );
	/** Porcentagem m�nima que o n�mero de aulas deve possui em rela��o a carga hor�ria da disciplina*/
	private int porcentagemMinNumAulas = ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.PORCENTAGEM_MIN_NUM_AULAS_EM_RELACAO_CH_TURMA );
	/** Mensagem de erro exibida quando a turma � de hor�rio flex�vel. */
	private String mensagemErroHorario;
	/** Datas dos feriados da turma. O c�lculo da poscentagem do n�mero de aulas deve ignorar os feriados. */
	private List<Date> feriados;
	
	/** Construtor padr�o. */
	public HorarioTurmaMBean() {
		clear();
	}
	
	/** Interpreta a express�o de hor�rio informada pelo usu�rio.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			// verifica se os hor�rio foi totalmente convertido
			if (isEmpty(marcados) || isEmpty(horarios)) {
				addMensagemErro("Express�o de hor�rio inv�lida");
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
					addMensagemWarning("A express�o de hor�rio informada e a express�o de hor�rio resultante n�o s�o exatamente iguais.");
				List<HorarioTurma> horariosReverso = HorarioTurmaUtil.parseCodigoHorarios(expressaoReversa, idUnidade, obj.getDisciplina().getNivel(), dao);
				if (horarios != null) Collections.sort(horarios);
				if (horariosReverso != null) Collections.sort(horariosReverso);
				if (horarios != null && horariosReverso != null && horarios.size() == horariosReverso.size()) {
					for (int i = 0; i < horarios.size(); i++ ) {
						if (horarios.get(i).equals(horariosReverso.get(i)))
							addMensagemWarning("O hor�rio informado ("
							+ expressao
							+ ") n�o foi totalmente convertido no hor�rio da grade. Por favor, verifique a express�o do hor�rio informada.");
					}
				}
			} else {
				addMensagemErro("Express�o de hor�rio inv�lida");
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
				addMensagemErro("N�o � poss�vel definir o hor�rio para o domingo.");
			}
			carregaHorariosExpressao(horarios);	
		}
	}

	/**
	 * Carrega os hor�rios da express�o para turmas de gradua��o;
	 * M�todo n�o invocado por JSPs
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
	 * Cancela a opera��o e volta para a listagem das turmas, caso a opera��o
	 * seja de altera��o ou remo��o de uma turma. <br/>
	 * M�todo n�o invocado por JSP�s.
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

	// In�cio do fluxo da sele��o dos hor�rios
	
	/** Inicia a defini��o de hor�rio da turma.
	 * <br/>M�todo n�o invocado por JSP�s.
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
			// Evitando NPE ao realizar o parse dos hor�rios marcados.
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
				
				// Carrega as vari�veis que guardam os hor�rios 
				novosHorarios = new ArrayList<HorarioTurma>();
				novosHorarios.addAll(obj.getHorarios());
				horariosEscolhidos = new ArrayList<String>();
				if (horariosMarcados != null)
					for (int i = 0; i<horariosMarcados.length; i++)
						horariosEscolhidos.add(horariosMarcados[i]);
				
				// Carrega a lista de feriados no per�do letivo para n�o sobrecarregar o ajax com consultas.
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
					addMensagemErro("A unidade " + obj.getDisciplina().getUnidade() + " n�o possui munic�pio cadastrado. Por favor, entre em contato com a administra��o para corrigir a falta desta informa��o.");
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
	 * Diz se � permitido ou n�o a escolha da grade de hor�rios ao definir os hor�rios de uma turma.
	 * 
	 * FUTURAMENTE PERMITIR QUE CADA UNIDADE DECIDA SE PERMITE OU N�O
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
	 * Retorna para o controler que solicitou a defini��o de hor�rio da turma. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * redireciona o usu�rio para a tela de hor�rios.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Verifica a defini��o de hor�rio da turma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			// Verifica se as datas inicial ou final da turma foram adicionadas antes da valida��o.
			if (dataTurmaInicio == null || dataTurmaFim == null){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "In�cio-Fim");
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
					addMensagemErro("Voc� selecionou uma turma agrupadora que utiliza a Grade de Hor�rios do(a) " + unidadeBanco.getNome()+". Por favor selecione a Grade de Hor�rios correta." );
					return null;
				}
			}
		}
		
		
		// Se for a cria��o de turmas de componentes que permitem sub-turmas e
		// n�o for a partir de uma
		// solicita��o ao submeter os hor�rios deve ser verificado se o hor�rio
		// selecionado tem algum hor�rio igual aos hor�rios das sub-turmas
		if( solicitacao == null && obj.getDisciplina().isAceitaSubturma() && obj.getTurmaAgrupadora() != null ){
			if( !subturmas.contains(obj) )
				subturmas.add(obj);
			if(subturmas.size() > 1 && HorarioTurmaUtil.verificarChoqueHorario(subturmas).size() == 0){
				String msg = "Para adicionar esta subturma ao grupo de subturmas selecionado � necess�rio que haja pelo menos um hor�rio em comum " +
				"entre todas as subturmas do grupo.";
		
				if (isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE))
					addMensagemWarning(msg);
				else
					addMensagemErro(msg);		
			}
		}
		
		// Se for altera��o de turma, validar o choque de hor�rio dos discentes
		// Se for administrador DAE, a valida��o � feita, mas n�o impede do usu�rio prosseguir.
		if( obj.getId() != 0 ){
			List<HorarioTurma> horarioOriginal = horarioTurmaDao.findByTurma(obj);
			
			// Indica se est� inserindo o hor�rio em uma turma que j� existe pela primeira vez ou
			// se est� modificando um horario que j� existe
			// Nesses dois casos, � necess�rio verificar choque de hor�rio nos discentes.
			boolean isHorarioNovoOuModificado = ValidatorUtil.isEmpty( horarioOriginal ) || !isEqualCollectionTransient(horarioOriginal, obj.getHorarios());
			
			if( isHorarioNovoOuModificado ) {
				TurmaValidator.validaHorariosDiscentesTurma(obj, getUsuarioLogado(),erros);
				TurmaValidator.validaHorariosTurmaPlanoMatricula(obj, erros);
			}
		}
		
		// Verifica choque de hor�rio entre as disciplinas de uma mesma turma de ensino m�dio.
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
				validateRange(obj.getDataInicio(), cal.getInicioFerias(), cal.getFimFerias(), "Data de In�cio da Turma", erros);
				validateRange(obj.getDataFim(), cal.getInicioFerias(), cal.getFimFerias(), "Data de Fim da Turma", erros);
			} else {
				validateRange(obj.getDataInicio(), cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), "Data de In�cio da Turma", erros);
				validateRange(obj.getDataFim(), cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), "Data de Fim da Turma", erros);
			}
			
			if (obj.getSolicitacao() != null && obj.getSolicitacao().isTurmaEnsinoIndividual() && obj.getId() == 0) {
				// n�o valida hor�rio, pois no cadastro n�o � definido hor�rio para EI
			} 
			
			// valida se a data informada / alterada pelo chefe de departamento est� dentro do per�odo de f�rias
			if (obj.isTurmaFerias() && getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_TURMAS_UNIDADE)){
				CalendarioAcademico calTurma = CalendarioAcademicoHelper.getCalendario(obj);
				validateMinValue(obj.getDataInicio(), calTurma.getInicioFerias(), "In�cio", erros);
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
	 * Compara os dois hor�rios desconsiderando os id's.
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
	 * Invocado para realizar opera��es espec�ficas quando a turma possuir componente que permite hor�rio flex�vel.
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
		
		// validar hor�rios da turma
		TurmaValidator.validaHorarios(obj, erros, getUsuarioLogado());
		TurmaValidator.validaPeriodoHorario(obj, erros);

		// O administrador DAE pode mudar as datas in�cio e fim da turma independente dos hor�rios escolhidos.
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
	 * Invocado para realizar opera��es espec�ficas quando a turma N�O possuir componente que permite hor�rio flex�vel
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
		// validar hor�rios da turma
		TurmaValidator.validaHorarios(obj, erros, getUsuarioLogado());
	}
	
	/**
	 * Verifica se j� foi adicionado uma hor�rio na data indicada
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void validarPeriodoEscolhido() {
		
		validateRequired(periodoInicio, "Per�odo Inicial do Hor�rio", erros);
		validateRequired(periodoFim, "Per�odo Final do Hor�rio", erros);
		if (hasErrors()) return;
		if (periodoInicio.before(obj.getDataInicio()))
			erros.addErro("Data de In�cio anterior ao in�cio do Per�odo Letivo.");
		if (periodoInicio.after(obj.getDataFim()))
			erros.addErro("Data de In�cio posterior ao fim do Per�odo Letivo.");
		if (periodoFim.before(obj.getDataInicio()))
			erros.addErro("Data de Fim anterior ao in�cio do Per�odo Letivo.");
		if (periodoFim.after(obj.getDataFim()))
			erros.addErro("Data de Fim posterior ao fim do Per�odo Letivo.");
		validaOrdemTemporalDatas(periodoInicio, periodoFim, true, "In�cio e Fim do Hor�rio", erros);
		if (hasErrors()) return;
		
		Collection<HorarioTurma> horariosAgrupadosPorData = CollectionUtils.select(obj.getHorarios(),new Predicate(){
			public boolean evaluate(Object arg) {
				HorarioTurma ht = (HorarioTurma) arg;
				return CalendarUtils.isIntervalosDeDatasConflitantes(ht.getDataInicio(), ht.getDataFim(), periodoInicio, periodoFim);
			}
		});
		
		if (!horariosAgrupadosPorData.isEmpty())
			addMensagemErro("N�o deve haver choque de datas nos per�odos de hor�rios escolhidos.");
	}
	
	/**
	 * Quando a turma possuir hor�rios flex�veis, este m�todo ser� invocado para
	 * ir adicionando os hor�rios.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/horarios.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws Exception
	 */
	public void adicionarHorario (ActionEvent evt) throws Exception {
		
		validarPeriodoEscolhido();
		
		// Criando uma c�pia para retornar ao estado anterior em caso de erro.
		Turma turmaTemp = UFRNUtils.deepCopy(obj);
		
		String[] horariosEscolhidosArray = null;
		
		if (obj.isGraduacao()){
			int size = horariosEscolhidos != null && !horariosEscolhidos.isEmpty()? horariosEscolhidos.size()-1 : 0;
			horariosEscolhidosArray = new String [size];
			if ( mBean.isPodeAlterarHorarios() ) {
				horariosEscolhidosArray = horariosEscolhidos.toArray(horariosEscolhidosArray);
			}
			// Limpa a grade para o pr�ximo hor�rio.
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
		

		// validar hor�rios da turma
		TurmaValidator.validaHorarios(turmaTemp, erros, getUsuarioLogado());
		
		if (hasErrors())
			return;

		// Aplicando altera��es
		
		float numAulasTotal = HorarioTurmaUtil.calcularNumAulas(turmaTemp.getHorarios(),feriados);
		porcentagemAulas = getPorcentagemAulas(numAulasTotal);
		
		obj = turmaTemp;
		turmaTemp = null;
		Collections.sort(grupoHorarios, HorarioTurmaUtil.comparatorGrupoHorarios);
		modelGrupoHorarios = new ListDataModel(getPeriodosHorariosEscolhidos());
		periodoInicio = CalendarUtils.adicionaUmDia(periodoFim);
		periodoFim = null;
		addMensagemInformation("Per�odo Adicionado com Sucesso.");
	}
	
	/**
	 * Retorna os hor�rios agrupados por per�odo
	 * 
	 * @return
	 */
	private List<GrupoHorarios> getPeriodosHorariosEscolhidos() {
		grupoHorarios = new ArrayList<GrupoHorarios>();
		grupoHorarios = HorarioTurmaUtil.agruparHorarioPorPeriodo(obj.getHorarios());
		
		return grupoHorarios;
	}

	/**
	 * Remove da cole��o (e da view) o grupo de hor�rio escolhido na view.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/horarios.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public void removerPeriodoHorarioFlexivel(ActionEvent evt) throws DAOException, NegocioException {
		
		if (!modelGrupoHorarios.isRowAvailable()) {
			addMensagemErro("Hor�rio j� foi removido");
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
	 * Gera a grade de hor�rios para popular a JSP de acordo com a unidade gestora e n�vel do usu�rio.
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
	 * Define o per�odo de in�cio e fim do hor�rio a partir do in�cio e fim do
	 * per�odo da turma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Carrega a grade de hor�rios. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Faz uma varredura recursivamente por hor�rios dispon�veis para o cadastro de turmas.
	 * Observe que a base da recurs�o obriga o retorno do m�todo ao chegar na UFRN. <br/>M�todo n�o invocado por JSP�s.
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
			
			//Se a unidade n�o for respons�vel por ela mesma e o mapa estiver vazio.			
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
	
	/** Retorna o n�vel de ensino em uso no subsistema atual.
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
	
	/** Retorna a lista de hor�rios da turma. 
	 * @return
	 */
	public List<Horario> getHorariosGrade() {
		return horariosGrade;
	}

	/** Seta a lista de hor�rios da turma.
	 * @param horariosGrade
	 */
	public void setHorariosGrade(List<Horario> horariosGrade) {
		this.horariosGrade = horariosGrade;
	}

	/** Retorna a matriz de hor�rios marcados pelo usu�rio.
	 * @return
	 */
	public String[] getHorariosMarcados() {
		return horariosMarcados;
	}

	/** Seta a matriz de hor�rios marcados pelo usu�rio.
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
	
	// M�todos Chamados pela interface
	
	/**
	 * Calcula os hor�rios da turma e a sua porcentagem em rela��o a carga hor�ria do componente
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

		// Adicionao ou remove o hor�rio
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
		
		// Carrega os objetos que controlam os hor�rios
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
	 * Calcula a data final da turma de acordo com uma porcentagem referenta a carga hor�ria da disciplina. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Calcula a data final da turma de acordo com uma porcentagem referenta a carga hor�ria da disciplina. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Calcula a porcentagem do n�mero de aulas em rela��o a carga hor�ria da disciplina. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Calcula a porcentagem do n�mero de aulas em rela��o a carga hor�ria da disciplina. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			// Os hor�rios flex�veis s�o adicionados a turma 
			if (obj.getDisciplina().isPermiteHorarioFlexivel()){
				
				validarDatasInicioFimHorariosFlexiveis(obj.getHorarios(),dataTurmaInicio,dataTurmaFim);			
				numAulasAtual = HorarioTurmaUtil.calcularNumAulasTurmaHorarioFlexivel(obj.getHorarios(),dataTurmaInicio,dataTurmaFim);
				
			} else
				numAulasAtual = HorarioTurmaUtil.calcularNumAulas(novosHorarios,dataTurmaInicio,dataTurmaFim,feriados);
			
			porcentagemAulas = getPorcentagemAulas(numAulasAtual);
		}	
	}
	
	/**
	 * Retorna a porcentagem de aulas cadastradas no hor�rio da turma em rela��o a n�mero de aulas do componente.
	 * 
	 * Primeiro encontra o n�mero de aulas que a turma deve ter com sua CH. 
	 * Para isso transforma a CH em minutos e depois divide pelo par�metro que define o n�mero de minutos de cada aula (minutosAulaRegular). 
	 * Ap�s isso, faz uma regra de tr�s para calcular a porcentagem do n�mero de aulas total em rela��o ao n�mero de aulas definidos pela CH do componente.  
	 * 
	 * M�todo n�o invocado por JSP(s):
	 * 
	 * @param numAulasTotal: N�mero de aulas total calculado em cima dos hor�rios cadastrados para a turma, removendo os feriados.
	 * @throws DAOException
	 */
	private int getPorcentagemAulas (float numAulasTotal) throws DAOException {
		float numAulasCH = obj.getChTotalTurma() * 60 / getParametrosAcademicos().getMinutosAulaRegular();
		porcentagemAulas = (int) (numAulasTotal * 100 / numAulasCH);
		return porcentagemAulas;
	}
	
	/**
	 * Carrega a porcentagem de aulas na turma.
	 * M�todo n�o invocado por JSPs
	 * @throws DAOException
	 */
	private void carregarPorcentagemAulas() throws DAOException {
		if (obj.getDisciplina().isExigeHorarioEmTurmas()){
			int numAulasTotal = HorarioTurmaUtil.calcularNumAulas(obj.getHorarios(),feriados);	
			// O n�mero de aulas do hor�rio/per�odo deve ser maior ou igual (120% >= percentual >= 100%) que a carga hor�ria do componente curricular. 
			float porcentagemAula =  getPorcentagemAulas(numAulasTotal);
			// Seta a porcentagem de aulas para fazer a valida��o no Validator.
			obj.setPorcentagemAulas(porcentagemAula);
		}
	}
	
	/**
	 * Verifica se a data inicial e final da turma cont�m os hor�rios flex�veis. <br />
	 * Ou seja, se a turma possuir um hor�rio flex�vel que v� at� 30/06 a data final da turma deve ser maior ou igual que 30/06.
	 * Isso acontece porque caso a data final da turma for inferior a data final do hor�rio flex�vel ser� necess�rio cortar o hor�rio flex�vel.
	 * O que pode ocasionar problemas na interface da tela de sele��o de hor�rios.  
	 * 
	 * M�todo n�o invocado por JSP(s):
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
			msgErroInicio = "A data in�cio da turma n�o deve ser superior a data inicial dos hor�rios flex�veis adicionados. Para aumentar a data-in�cio da turma primeiro remova o hor�rio flex�vel.";
			addMensagemErroAjax(msgErroInicio);
		}
		
		if (maiorHorarioAux != null && dataTurmaFim.getTime() < maiorHorarioAux.getDataFim().getTime()){
			msgErroFim = "A data final da turma n�o deve ser inferior a data final dos hor�rios flex�veis adicionados. Para diminuir a data-fim da turma primeiro remova o hor�rio flex�vel.";
			addMensagemErroAjax(msgErroFim);
		}
		
		if (!msgErroInicio.isEmpty() && !msgErroFim.isEmpty()) {
			// As duas mensagens est�o preenchidas.
			mensagemErroHorario = msgErroInicio+"<br/>"+msgErroFim;
		} else {
			// Apenas uma mensagem est� preenchida.
			mensagemErroHorario = msgErroInicio+msgErroFim;
		}	
		
	}
	
	/**
	 * Verifica se os dados est�o de acordo com a JSP. Utilizado pra precaver erros quando o usu�rio utiliza o bot�o voltar.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
				addMensagemErro("Voc� utilizou o bot�o voltar do browser causando inconsist�ncia na opera��o." +
				" Reinicie o processo utilizando os links oferecidos pelo sistema.");
				redirectJSF(getSubSistema().getLink());
				return;
			}		
		}
	}
	
	/**
	 * Carrega o calend�rio correto para realizar an�lise das solicita��es
	 * @throws DAOException
	 */
	protected CalendarioAcademico getCalendario() throws DAOException {
		CalendarioAcademico cal = getCalendarioVigente();
		if (getCursoAtualCoordenacao() != null && getCursoAtualCoordenacao().isProbasica()) {
			cal = CalendarioAcademicoHelper.getCalendario(getCursoAtualCoordenacao());
		}
		if (cal == null) {
			addMensagemErro("Erro ao carregar calend�rio acad�mico no ano-per�odo informado: " + obj.getAnoPeriodo());
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
	 * Busca nos par�metros da unidade gestora acad�mica se deve habilitar/desabilitar os hor�rios do domingo.
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
