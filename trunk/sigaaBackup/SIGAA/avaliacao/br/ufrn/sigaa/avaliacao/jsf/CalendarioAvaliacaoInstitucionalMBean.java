/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/09/2010
 *
 */
package br.ufrn.sigaa.avaliacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.avaliacao.dao.CalendarioAvaliacaoDao;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.FormularioAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.TipoAvaliacaoInstitucional;

/** Controller respons�vel por opera��es no calend�rio de Avalia��o Institucional. 
 * @author �dipo Elder F. Melo
 *
 */
@Component("calendarioAvaliacaoInstitucionalBean") 
@Scope("request")
public class CalendarioAvaliacaoInstitucionalMBean extends SigaaAbstractController<CalendarioAvaliacao> {
	
	/** Cole��o de todos calend�rios de avalia��o que o discente logado preencheu. */
	private Collection<CalendarioAvaliacao> allPreenchidosDiscentes;
	/** Cole��o de todos calend�rios pass�veis de preenchimento pelo usu�rio. */
	private Collection<CalendarioAvaliacao> allPassivelProcessamento;

	/** Construtor padr�o. */
	public CalendarioAvaliacaoInstitucionalMBean() {
		obj = new CalendarioAvaliacao();
	}

	/**
	 * Redireciona o usu�rio para o preenchimento do formul�rio de Avalia��o
	 * Institucional selecionado.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/calendario/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String responderFormulario() throws ArqException, NegocioException {
		obj = new CalendarioAvaliacao();
		populateObj(true);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		FormularioAvaliacaoInstitucionalMBean bean = getMBean("formularioAvaliacaoInstitucionalBean");
		switch (obj.getFormulario().getTipoAvaliacao()) {
		case TipoAvaliacaoInstitucional.AVALIACAO_DOCENTE_GRADUACAO:
			return bean.iniciarAvaliacaoDocente(obj.getFormulario(), obj.getAno(), obj.getPeriodo());
		case TipoAvaliacaoInstitucional.AVALIACAO_DOCENCIA_ASSISTIDA:
			return bean.iniciarDocenciaAssistida(obj.getFormulario(), obj.getAno(), obj.getPeriodo());
		case TipoAvaliacaoInstitucional.AVALIACAO_DISCENTE_GRADUACAO:
			return bean.iniciarAvaliacaoDiscente(obj.getFormulario(), obj.getAno(), obj.getPeriodo());
		case TipoAvaliacaoInstitucional.AVALIACAO_TUTOR_EAD:
			return bean.iniciarAvaliacaoTutor(obj.getFormulario(), obj.getAno(), obj.getPeriodo());
		default:
			addMensagemErro("Tipo de Avalia��o indefindo.");
			return null;
		}
	}
	
	/** Retorna a p�gina de listagem do calend�rio.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/avaliacao/calendario/lista.jsp";
	}
	
	/** Retorna o formul�rio para cadastro de um calend�rio de avalia��o
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getFormPage() {
		return "/avaliacao/calendario/form.jsp";
	}
	
	/** Retorna o link para a p�gina de listagem ap�s cadastrar.
	 * <br/> M�todo n�o invocado por JSP's
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	/** Retorna o link para a p�gina de listagem ap�s remover.
	 * <br/> M�todo n�o invocado por JSP's
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardRemover()
	 */
	@Override
	protected String forwardRemover() {
		return getListPage();
	}
	
	/** Cancela o caso de uso.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/calendario/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		try {
			if (isOperacaoAtiva(ArqListaComando.ALTERAR.getId())) {
				resetBean();
				redirectJSF(getListPage());
			} else {
				resetBean();
				redirectJSF(getSubSistema().getLink());
			}
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}
	
	/** Retorna uma cole��o de selectItem com os formul�rios cadastrados.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getFormulariosCombo() throws DAOException{
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		List<FormularioAvaliacaoInstitucional> ativos = (List<FormularioAvaliacaoInstitucional>) getGenericDAO().findAll(FormularioAvaliacaoInstitucional.class);
		if (ativos != null) {
			Collections.sort(ativos, new Comparator<FormularioAvaliacaoInstitucional>() {
				@Override
				public int compare(FormularioAvaliacaoInstitucional o1, FormularioAvaliacaoInstitucional o2) {
					int comp = 0;
					comp = o1.getTitulo().compareTo(o2.getTitulo());
					if (comp == 0)
						comp = o1.getTipoAvaliacao() - o2.getTipoAvaliacao();
					return comp;
				}
			});
			for (FormularioAvaliacaoInstitucional form : ativos) {
				lista.add(new SelectItem(form.getId(), form.getTitulo() + " / " + form.getDescricaoTipoAvaliacao() + (form.isEad() ? " (EAD)" : "")));
			}
		}
		return lista;
	}
	
	/** Retorna uma cole��o de todos calend�rios de avalia��o ativos.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllAtivos()
	 */
	@Override
	public Collection<CalendarioAvaliacao> getAllAtivos() throws ArqException {
		if (all == null) {
			GenericDAO dao = getGenericDAO();
			all =  dao.findByExactField(CalendarioAvaliacao.class, "ativo", Boolean.TRUE);
			// filtra por perfil do entrevistado e se est� no prazo para preencher
			if (!isPortalAvaliacaoInstitucional() && !isEmpty(all)) {
				Iterator<CalendarioAvaliacao> iterator = all.iterator();
				while (iterator.hasNext()) {
					CalendarioAvaliacao cal = iterator.next();
					FormularioAvaliacaoInstitucional form = cal.getFormulario();
					if (!CalendarUtils.isDentroPeriodo(cal.getInicio(), cal.getFim()) ||
						!(getAcessoMenu().isDocente() && form.getTipoAvaliacao() == TipoAvaliacaoInstitucional.AVALIACAO_DOCENTE_GRADUACAO
						||getAcessoMenu().isDocente() && form.getTipoAvaliacao() == TipoAvaliacaoInstitucional.AVALIACAO_DOCENCIA_ASSISTIDA
						||getAcessoMenu().isDiscente() && form.getTipoAvaliacao() == TipoAvaliacaoInstitucional.AVALIACAO_DISCENTE_GRADUACAO
						||getAcessoMenu().isDiscente() && form.getTipoAvaliacao() == TipoAvaliacaoInstitucional.AVALIACAO_DOCENCIA_ASSISTIDA
						||getAcessoMenu().isTutorEad() && form.getTipoAvaliacao() == TipoAvaliacaoInstitucional.AVALIACAO_TUTOR_EAD)
						||getAcessoMenu().isDiscente() && getDiscenteUsuario().getCurso().isADistancia() != form.isEad())
						iterator.remove();
				}
			}
			// ordena por ano-per�odo
			Collections.sort((List<CalendarioAvaliacao>) all, new Comparator<CalendarioAvaliacao>() {
				@Override
				public int compare(CalendarioAvaliacao o1, CalendarioAvaliacao o2) {
					return o2.getAno() * 10 + o2.getPeriodo() - (o1.getAno() * 10 + o1.getPeriodo());
				}
			});
		}
		return all;
	}
	
	/** Retorna uma cole��o de todos calend�rios de avalia��o que o discente logado preencheu.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllAtivos()
	 */
	public Collection<CalendarioAvaliacao> getAllPreenchidosDiscentes() throws ArqException {
		if (allPreenchidosDiscentes == null) {
			CalendarioAvaliacaoDao dao = getDAO(CalendarioAvaliacaoDao.class);
			allPreenchidosDiscentes =  dao.findAllPreenchidoByDiscente(getDiscenteUsuario());
		}
		return allPreenchidosDiscentes;
	}
	
	/** Retorna uma cole��o de todos calend�rios de avalia��o que podem ser processados.
	 * Neste momento, somente as avalia��es preenchidas por discentes s�o process�veis.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllAtivos()
	 */
	public Collection<CalendarioAvaliacao> getAllPassivelProcessamento() throws ArqException {
		if (allPassivelProcessamento == null) {
			GenericDAO dao = getGenericDAO();
			allPassivelProcessamento =  dao.findByExactField(CalendarioAvaliacao.class, "ativo", Boolean.TRUE);
			if (allPassivelProcessamento != null) {
				Iterator<CalendarioAvaliacao> iterator = allPassivelProcessamento.iterator();
				while (iterator.hasNext())
					if (!iterator.next().getFormulario().isAvaliacaoDiscente())
						iterator.remove();
			}
			// ordena por ano-per�odo
			Collections.sort((List<CalendarioAvaliacao>) allPassivelProcessamento, new Comparator<CalendarioAvaliacao>() {
				@Override
				public int compare(CalendarioAvaliacao o1, CalendarioAvaliacao o2) {
					return o2.getAno() * 10 + o2.getPeriodo() - (o1.getAno() * 10 + o1.getPeriodo());
				}
			});
		}
		return allPassivelProcessamento;
	}
	
	/** Valida os dados antes de cadastrar/alterar
	 * <br/> M�todo n�o invocado por JSP's
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#doValidate()
	 */
	@Override
	protected void doValidate() throws ArqException {
		CalendarioAvaliacaoDao dao = getDAO(CalendarioAvaliacaoDao.class);
		String fields[] = {"ano", "periodo", "formulario.id"};
		Integer values[] = {obj.getAno(), obj.getPeriodo(), obj.getFormulario().getId()};
		Collection<CalendarioAvaliacao> lista = dao.findByExactField(CalendarioAvaliacao.class, fields, values);
		if (!isEmpty(lista)) {
			CalendarioAvaliacao banco = lista.iterator().next();
			if (banco.getId() != obj.getId()) {
				FormularioAvaliacaoInstitucional form = dao.refresh(obj.getFormulario());
				addMensagemErro("J� existe um calend�rio cadastrado para o formul�rio "+form.getTitulo() +" em " + obj.getAno() + "." + obj.getPeriodo());
			}
		}
	}

	public void setAllPreenchidosDiscentes(
			Collection<CalendarioAvaliacao> allPreenchidosDiscentes) {
		this.allPreenchidosDiscentes = allPreenchidosDiscentes;
	}
}
