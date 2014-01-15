/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dao.ParticipacaoEnadeDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.CalendarioEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.CursoGrauAcademicoEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ParticipacaoEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoENADE;

/**
 * Managed Bean responsável pelo cadastro da participação de discentes no ENADE
 * - Exame Nacional de Desempenho de Estudantes.
 * 
 * @author Ricardo Wendell
 * 
 */
@Component("participacaoDiscenteEnade")
@Scope("request")
public class ParticipacaoDiscenteEnadeMBean extends SigaaAbstractController<DiscenteGraduacao> implements OperadorDiscente{

	/** Coleção de SelectItem com os tipos de participação do ENADE para discentes ingressantes. */
	private Collection<SelectItem> tiposParticipacaoIngressante;
	/** Coleção de SelectItem com os tipos de participação do ENADE para discentes concluintes. */
	private Collection<SelectItem> tiposParticipacaoConcluinte;
	/** Coleção de SelectItem com todos os tipos de participação do ENADE. */
	private Collection<SelectItem> tiposParticipacao;
	/** Tipo de participação do ENADE para discentes ingressante. */
	private ParticipacaoEnade participacaoEnadeIngressante;
	/** Tipo de participação do ENADE para discentes concluintes. */
	private ParticipacaoEnade participacaoEnadeConcluinte;
	/** Tipo de ENADE (Ingressante / Concluinte) utilizado para buscar os discentes que terão a participação definida em lote. */
	private TipoENADE tipoEnade;
	/** Curso ao qual a busca por discente ser restringe. */
	private Curso curso;
	/** Ano de ingresso dos discentes  ao qual a busca por discente ser restringe. */
	private int ano;
	/** Período de ingresso dos discentes  ao qual a busca por discente ser restringe. */
	private int periodo;
	/** Percentual de conclusão do curso ao qual a busca por discente ser restringe. */
	private int percentualConcluido;
	/** Status dos discentes que o usuário pode definir como filtro na busca por discentes. */
	private Collection<SelectItem> statusDiscenteCombo;
	/** Agrupa por cursos o resultado da busca por discentes. */ 
	private boolean loteCurso;
	/** Calendário ENADE usado como referência para busca de discentes. */
	private CalendarioEnade calendarioEnade;
	/** Busca por cursos que não estão cadastrados no calendário ENADE. */
	private boolean outrosCursos;
	/** Indica se utiliza o calendário ENADE na busca por cursos. */
	private boolean usarCalendarioEnade;
	
	/** Construtor padrão. */
	public ParticipacaoDiscenteEnadeMBean() {
		this.obj = new DiscenteGraduacao();
		this.participacaoEnadeIngressante = new ParticipacaoEnade();
		this.participacaoEnadeConcluinte= new ParticipacaoEnade();
		this.tipoEnade = TipoENADE.INGRESSANTE;
		this.curso = new Curso();
		this.statusDiscenteCombo = null;
		this.calendarioEnade = new CalendarioEnade();
	}

	/**
	 * Inicia o cadastro de participação no ENADE.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		checkRole(new int[] {SigaaPapeis.DAE});

		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_PARTICIPACAO_ENADE);
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}
		return buscarDiscente();
	}
	
	/**
	 * Inicia o cadastro de participação no ENADE.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarLote() throws SegurancaException {
		checkRole(new int[] {SigaaPapeis.DAE});
		this.loteCurso = false;
		this.curso = new Curso();
		this.ano = getCalendarioVigente().getAno();
		this.periodo = 0;
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_PARTICIPACAO_ENADE_LOTE.getId());
		return formBusca();
	}
	/**
	 * Retorna à tela de busca.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String formBusca() throws SegurancaException {
		return forward("/graduacao/participacao_enad/busca_discentes.jsp");
	}
	
	/**
	 * Inicia o cadastro de participação no ENADE.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarLoteCurso() throws SegurancaException {
		checkRole(new int[] {SigaaPapeis.DAE});
		this.loteCurso = true;
		this.curso = new Curso();
		this.ano = getCalendarioVigente().getAno();
		this.periodo = 0;
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_PARTICIPACAO_ENADE_LOTE.getId());
		return formBusca();
	}

	/**
	 * Cadastra a participação do discente no ENADE. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/participacao_enad/participacao.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String cadastrar() {
		checkOperacaoAtiva(SigaaListaComando.CADASTRAR_PARTICIPACAO_ENADE.getId());
		obj.setParticipacaoEnadeConcluinte(this.participacaoEnadeConcluinte);
		obj.setParticipacaoEnadeIngressante(this.participacaoEnadeIngressante);
		validacaoDados(erros);
		if (hasErrors())
			return null;
		// seta os valores
		if (!isEmpty(participacaoEnadeIngressante))
			obj.setParticipacaoEnadeIngressante(participacaoEnadeIngressante);
		else
			obj.setParticipacaoEnadeIngressante(null);
		if (!isEmpty(participacaoEnadeConcluinte))
			obj.setParticipacaoEnadeConcluinte(participacaoEnadeConcluinte);
		else
			obj.setParticipacaoEnadeConcluinte(null);
		
		try {
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setObjMovimentado(obj);
			movimento.setCodMovimento(SigaaListaComando.CADASTRAR_PARTICIPACAO_ENADE);
			execute(movimento, getCurrentRequest() );
			addMessage("Participação no ENADE cadastrada com sucesso!",
					TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
			return null;
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}
		return cancelar();
	}
	
	/**
	 * Cadastra a participação do discente no ENADE em lote. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/participacao_enad/participacao_lote.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 */
	public String cadastrarLote() throws DAOException {
		checkOperacaoAtiva(SigaaListaComando.CADASTRAR_PARTICIPACAO_ENADE_LOTE.getId());
		validacaoDados(erros);
		if (hasErrors())
			return null;
		// seta os valores
		GenericDAO dao = getGenericDAO();
		int id = tipoEnade == TipoENADE.INGRESSANTE ? this.participacaoEnadeIngressante.getId() : this.participacaoEnadeConcluinte.getId();
		ParticipacaoEnade participacao = dao.findByPrimaryKey(id, ParticipacaoEnade.class);
		for (DiscenteGraduacao dg : resultadosBusca) {
			if (dg.isMatricular()) {
				if (tipoEnade == TipoENADE.INGRESSANTE) {
					dg.setParticipacaoEnadeIngressante(participacao);
					dg.setDataProvaEnadeIngressante(obj.getDataProvaEnadeIngressante());
				} else {
					dg.setParticipacaoEnadeConcluinte(participacao);
					dg.setDataProvaEnadeConcluinte(obj.getDataProvaEnadeConcluinte());
				}
			}
		}
		
		try {
			MovimentoCadastro movimento = new MovimentoCadastro();
			movimento.setColObjMovimentado(resultadosBusca);
			movimento.setObjAuxiliar(tipoEnade);
			movimento.setCodMovimento(SigaaListaComando.CADASTRAR_PARTICIPACAO_ENADE_LOTE);
			execute(movimento, getCurrentRequest() );
			addMessage("Participação no ENADE cadastrada com sucesso!",
					TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
			return null;
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}
		return cancelar();
	}
	
	/** Valida os dados da participação do discente no ENADE
	 * <br/>Método não invocado por JSP´s 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens lista) {
		// participação de um discente
		if (isEmpty(resultadosBusca)) {
			int anoPeriodo;
			validateRequiredId(obj.getId(), "Discente", lista);
			if (obj.getDataProvaEnadeIngressante() != null) {
				anoPeriodo = CalendarUtils.getAno(obj.getDataProvaEnadeIngressante()) * 10
						+ (CalendarUtils.getMesByData(obj.getDataProvaEnadeIngressante())  / 7 + 1);
				if (obj.getAnoIngresso() * 10 + obj.getPeriodoIngresso() > anoPeriodo)
					addMensagemErro("A data da prova no ENADE Ingressante é anterior ao ano-período de ingresso do discente.");
				validateRequired(obj.getParticipacaoEnadeIngressante(), "Participação no ENADE Ingressante", lista);
			}
			if (obj.getDataProvaEnadeConcluinte() != null) {
				anoPeriodo = CalendarUtils.getAno(obj.getDataProvaEnadeConcluinte()) * 10
						+ (CalendarUtils.getMesByData(obj.getDataProvaEnadeConcluinte())  / 7 + 1);
				if (obj.getAnoIngresso() * 10 + obj.getPeriodoIngresso() > anoPeriodo)
					addMensagemErro("A data da prova no ENADE Concluinte é anterior ao ano-período de ingresso do discente.");
				validateRequired(obj.getParticipacaoEnadeConcluinte(), "Participação no ENADE Concluinte", lista);
			}
			validateMinValue(obj.getDataProvaEnadeConcluinte(), obj.getDataProvaEnadeIngressante(), "Data da prova no ENADE Concluinte", lista);
		} else {
			//participação em lote de discentes
			if (isTipoEnadeIngressante()) {
				if (obj.getDataProvaEnadeIngressante() != null) {
					if (CalendarUtils.getAno(obj.getDataProvaEnadeIngressante()) < ParticipacaoEnade.ANO_INICIO_ENADE_INGRESSANTE)
						addMensagemErro("A data da prova no ENADE Ingressante é anterior ao ano de início do ENADE Ingressante.");
					if (CalendarUtils.getAno(obj.getDataProvaEnadeIngressante()) < ano)
						addMensagemErro("A data da prova no ENADE Ingressante é anterior ao ano de ingresso.");
				}
			} else {
				if (obj.getDataProvaEnadeConcluinte() != null) {
					if (CalendarUtils.getAno(obj.getDataProvaEnadeConcluinte()) < ParticipacaoEnade.ANO_INICIO_ENADE_CONCLUINTE)
						addMensagemErro("A data da prova no ENADE Concluinte é anterior ao ano de início do ENADE Concluinte.");
					for (DiscenteGraduacao discente : resultadosBusca)
						if (CalendarUtils.getAno(obj.getDataProvaEnadeConcluinte()) < discente.getAnoIngresso())
							addMensagemErro("A data da prova no ENADE Concluinte é anterior ao ano de ingresso do discente " + discente.getMatriculaNome());
				}
			}
			boolean selecionado = false;
			for (DiscenteGraduacao dg : resultadosBusca)
				selecionado = selecionado || dg.isMatricular();
			if (!selecionado)
				lista.addErro("Selecione pelo menos um discente.");
		}
		return !lista.isErrorPresent();
	}

	/**
	 * Retorna os tipos de participação ingressantes ativas.
	 *
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getTiposParticipacaoIngressante() throws DAOException {
		if (tiposParticipacaoIngressante == null) {
			tiposParticipacaoIngressante = getTiposParticipacao(TipoENADE.INGRESSANTE);
		}
		return tiposParticipacaoIngressante;
	}
	
	/**
	 * Retorna os tipos de participação ingressantes ativas.
	 *
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getTiposParticipacaoConcluinte() throws DAOException {
		if (tiposParticipacaoConcluinte == null) {
			tiposParticipacaoConcluinte = getTiposParticipacao(TipoENADE.CONCLUINTE);
		}
		return tiposParticipacaoConcluinte;
	}
	
	/**
	 * Retorna todos os tipos de participação ativos.
	 *
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getAllTiposParticipacaoCombo() throws DAOException {
		if (tiposParticipacao == null) {
			tiposParticipacao = new ArrayList<SelectItem>();
			Collection<ParticipacaoEnade> lista = getGenericDAO().findAllAtivos(ParticipacaoEnade.class, "tipoEnade"); 
			for (ParticipacaoEnade p : lista) {
				StringBuilder str = new StringBuilder(p.getTipoEnade().toString()).append( " - ").append(p.getDescricao());
				tiposParticipacao.add(new SelectItem(p.getId(), str.toString()));
			}
		}
		return tiposParticipacao;
	}
	
	/**
	 * Retorna os tipos de participação ingressantes ativas.
	 *
	 * @return
	 * @throws DAOException 
	 */
	private Collection<SelectItem> getTiposParticipacao(TipoENADE tipo) throws DAOException {
		Collection<SelectItem> tiposParticipacao;
		String[] fields = {"tipoEnade", "ativo"};
		Object[] values = {tipo.ordinal(), true};
		Collection<ParticipacaoEnade> lista = getGenericDAO().findByExactField(ParticipacaoEnade.class, fields, values); 
		tiposParticipacao = toSelectItems(lista, "id", "descricao");
		return tiposParticipacao;
	}

	/**
	 * Redireciona para o Managed Bean para a busca de discentes de graduação.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/participacao_enad/participacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String buscarDiscente() throws SegurancaException{
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.PARTICIPACAO_ENADE);
		return buscaDiscenteMBean.popular();
	}
	
	/** Busca por discentes para definir a participação do discente no ENADE.
	 * 
	 * <ul>
	 * <li>sigaa.war/graduacao/participacao_enad/busca_discentes.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	public String buscar() throws HibernateException, ArqException {
		ParticipacaoEnadeDao dao = getDAO(ParticipacaoEnadeDao.class);
		Collection<CursoGrauAcademicoEnade> cursosGraus = null;
		if (!loteCurso) {
				validateRequiredId(this.curso.getId(), "Curso", erros);
				cursosGraus = new TreeSet<CursoGrauAcademicoEnade>();
				Collection<MatrizCurricular> matrizesCurso = 
					getDAO(MatrizCurricularDao.class).findByCurso(curso.getId(), true);
				for (MatrizCurricular matriz : matrizesCurso){
					CursoGrauAcademicoEnade cursoGrau = new CursoGrauAcademicoEnade();
					cursoGrau.setCurso(matriz.getCurso());
					cursoGrau.setGrauAcademico(matriz.getGrauAcademico());
					cursosGraus.add(cursoGrau);
				}
				// busca o calendário ENADE cadastrado, se houver, e definie a data da prova no formulário
				String fields[] = {"ano", "tipoEnade"};
				Object values[] = {this.ano, this.tipoEnade.ordinal()};
				Collection<CalendarioEnade> calendarios = dao.findByExactField(CalendarioEnade.class, fields, values);
				if (!isEmpty(calendarios)) {
					calendarioEnade = calendarios.iterator().next();
					if (calendarioEnade != null) {
						obj.setDataProvaEnadeIngressante(calendarioEnade.getDataProva());
						obj.setDataProvaEnadeConcluinte(calendarioEnade.getDataProva());
					}
				} else {
					calendarioEnade = new CalendarioEnade();
				}
		} else {
			if (usarCalendarioEnade) {
				validateRequiredId(this.calendarioEnade.getId(), "Calendário", erros);
				calendarioEnade = dao.refresh(calendarioEnade);
				tipoEnade = calendarioEnade.getTipoEnade();
				ano = calendarioEnade.getAno();
				cursosGraus = calendarioEnade.getCursosGrauAcademico();
				periodo = 0;
				if (calendarioEnade.getDataProva() != null) {
					if (calendarioEnade.getTipoEnade().isIngressante())
						obj.setDataProvaEnadeIngressante(calendarioEnade.getDataProva());
					else
						obj.setDataProvaEnadeConcluinte(calendarioEnade.getDataProva());
				} else {
					if (calendarioEnade.getTipoEnade().isIngressante())
						obj.setDataProvaEnadeIngressante(null);
					else
						obj.setDataProvaEnadeConcluinte(null);
				}
			} else {
				if (tipoEnade == TipoENADE.INGRESSANTE) {
					validateMinValue(this.ano, ParticipacaoEnade.ANO_INICIO_ENADE_INGRESSANTE, "Ano", erros);
					if (hasErrors()) return null;
				} else {
					if (obj.getStatus() != StatusDiscente.GRADUANDO)
						validateRange(this.percentualConcluido, 1, 100, "Percentual concluído", erros);
					if (hasErrors()) return null;
				}
			}
		}
		if (tipoEnade == TipoENADE.INGRESSANTE) {
			validateMinValue(this.ano, ParticipacaoEnade.ANO_INICIO_ENADE_INGRESSANTE, "Ano", erros);
			if (hasErrors()) return null;
			resultadosBusca = dao.findDiscentesParaEnadeIngressante(cursosGraus, outrosCursos, ano, periodo, 0);
		} else {
			if (obj.getStatus() != StatusDiscente.GRADUANDO)
				validateRange(this.percentualConcluido, 1, 100, "Percentual concluído", erros);
			if (hasErrors()) return null;
			resultadosBusca = dao.findDiscentesParaEnadeConcluinte(cursosGraus, outrosCursos, percentualConcluido, obj.getStatus());
		}
		if (isEmpty(resultadosBusca)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		} else {
			prepareMovimento(SigaaListaComando.CADASTRAR_PARTICIPACAO_ENADE_LOTE);
			if (!loteCurso) {
				for (DiscenteGraduacao dg : resultadosBusca)
					dg.setMatricular(true);
				this.curso = dao.refresh(curso);
				return forward("/graduacao/participacao_enad/participacao_lote.jsp");
			} else {
				return forward("/graduacao/participacao_enad/participacao_lote_curso.jsp");
			}
		}
	}
	
	/** Marca/desmarca discentes de um grupo de curso que terão a participação do ENADE definida em lote.
	 * 
	 * <ul>
	 * <li>sigaa.war/graduacao/participacao_enad/participacao_lote.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	public void selecionaCursoListener(ValueChangeEvent evt) {
		boolean selected = (Boolean) evt.getNewValue();
		int idCurso = getParameterInt("idCurso", 0);
		for (DiscenteGraduacao d : this.resultadosBusca) {
			if (d.getCurso().getId() == idCurso)
				d.setSelecionado(selected);
		}
	}

	/** Método invocado pelo {@link BuscaDiscenteMBean}.
	 * <br/>Método não invocado por JSP´s
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() {
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_PARTICIPACAO_ENADE.getId());
		
		List<Integer> statusConcluido = new ArrayList<Integer>();
		statusConcluido.add( StatusDiscente.CONCLUIDO );
		statusConcluido.add( StatusDiscente.CANCELADO );
		statusConcluido.add( StatusDiscente.EXCLUIDO );
		
		if( statusConcluido.contains( obj.getStatus() ) ){
			addMensagemWarning("Atenção! O discente selecionado está com status " + StatusDiscente.getDescricao( obj.getStatus() ) );
		}
		
		return forward("/graduacao/participacao_enad/participacao.jsp");
	}

	/** Seta o discente selecionado na busca por discentes.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) {
		try {
			obj = getGenericDAO().findByPrimaryKey(discente.getId(), DiscenteGraduacao.class);
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Não foi possível carregar o discente escolhido");
		}
		if (!isEmpty(obj.getParticipacaoEnadeConcluinte()))
			this.participacaoEnadeConcluinte = obj.getParticipacaoEnadeConcluinte();
		else
			this.participacaoEnadeConcluinte = new ParticipacaoEnade();
		if (!isEmpty(obj.getParticipacaoEnadeIngressante()))
			this.participacaoEnadeIngressante = obj.getParticipacaoEnadeIngressante();
		else
			this.participacaoEnadeIngressante = new ParticipacaoEnade();
	}
	
	/** Recupera os demais dados do calendário ENADE quando o usuário seleciona um novo no formulário.
	 * <br/>
	 * Métodos invodados por JSP's:
	 *  <br/>
	 *  <ul>
	 *  	<li>sigaa.war/graduacao/participacao_enad/busca_discentes.jsp</li>
	 *  </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void calendarioEnadeListener(ValueChangeEvent evt) throws DAOException {
		int id = (Integer) evt.getNewValue();
		CalendarioEnade calendarioEnade = getGenericDAO().findByPrimaryKey(id, CalendarioEnade.class);
		if (calendarioEnade != null)
			this.calendarioEnade = calendarioEnade;
	}
	
	public ParticipacaoEnade getParticipacaoEnadeIngressante() {
		return participacaoEnadeIngressante;
	}

	public void setParticipacaoEnadeIngressante(
			ParticipacaoEnade participacaoEnadeIngressante) {
		this.participacaoEnadeIngressante = participacaoEnadeIngressante;
	}

	public ParticipacaoEnade getParticipacaoEnadeConcluinte() {
		return participacaoEnadeConcluinte;
	}

	public void setParticipacaoEnadeConcluinte(
			ParticipacaoEnade participacaoEnadeConcluinte) {
		this.participacaoEnadeConcluinte = participacaoEnadeConcluinte;
	}

	public TipoENADE getTipoEnade() {
		return tipoEnade;
	}

	public void setTipoEnade(TipoENADE tipoEnade) {
		this.tipoEnade = tipoEnade;
	}
	
	public boolean isTipoEnadeIngressante() {
		return this.tipoEnade == TipoENADE.INGRESSANTE;
	}
	
	/** Status dos discentes que o usuário pode definir como filtro na busca por discentes. 
	 * @return
	 */
	public Collection<SelectItem> getStatusDiscenteCombo() {
		if (statusDiscenteCombo == null) {
			statusDiscenteCombo = new ArrayList<SelectItem>();
			int[] status = {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO};
			for (Integer s : status) 
				statusDiscenteCombo.add(new SelectItem(s, StatusDiscente.getDescricao(s)));
		}
		return statusDiscenteCombo;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public int getPercentualConcluido() {
		return percentualConcluido;
	}

	public void setPercentualConcluido(int percentualConcluido) {
		this.percentualConcluido = percentualConcluido;
	}

	public boolean isLoteCurso() {
		return loteCurso;
	}

	public void setLoteCurso(boolean loteCurso) {
		this.loteCurso = loteCurso;
	}

	public CalendarioEnade getCalendarioEnade() {
		return calendarioEnade;
	}

	public void setCalendarioEnade(CalendarioEnade calendarioEnade) {
		this.calendarioEnade = calendarioEnade;
	}

	public boolean isOutrosCursos() {
		return outrosCursos;
	}

	public void setOutrosCursos(boolean outrosCursos) {
		this.outrosCursos = outrosCursos;
	}

	public boolean isUsarCalendarioEnade() {
		return usarCalendarioEnade;
	}

	public void setUsarCalendarioEnade(boolean usarCalendarioEnade) {
		this.usarCalendarioEnade = usarCalendarioEnade;
	}
}
