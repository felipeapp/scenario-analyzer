/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/12/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.Parametro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroDao;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.form.ParametrosPesquisaForm;
import br.ufrn.sigaa.pesquisa.negocio.ParametrosPesquisaValidator;

/**
 * Action respons�vel pelo cadastro e atualiza��o dos par�metros do m�dulo de pesquisa
 * 
 * @author Ricardo Wendell
 * @author Leonardo Campos
 */

public class ParametrosPesquisaAction extends AbstractCrudAction {

	/**
	 * Popula os dados do formul�rio com os par�metros que est�o armazenados no
	 * banco.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/WEB-INF/jsp/pesquisa/menu/projetos.jsp</li>
	 * </ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		ParametrosPesquisaForm parametrosForm = (ParametrosPesquisaForm) form;

		// Popular campos do formul�rio
		ParametroHelper helper = ParametroHelper.getInstance();

		Formatador f = Formatador.getInstance();

		parametrosForm.setQtdMaximaRenovacoes(helper.getParametroInt(ConstantesParametro.NUMERO_MAXIMO_RENOVACOES_PROJETO));
		parametrosForm.setToleranciaSubmissao(helper.getParametroInt(ConstantesParametro.HORAS_TOLERANCIA_SUBMISSAO));
		parametrosForm.setDuracaoMaximaProjetos(helper.getParametroInt(ConstantesParametro.DURACAO_MAXIMA_NOVOS_PROJETOS));
		parametrosForm.setLimiteCotasProjeto(helper.getParametroInt(ConstantesParametro.LIMITE_COTAS_PROJETO));
		parametrosForm.setLimiteCotasOrientador(helper.getParametroInt(ConstantesParametro.LIMITE_COTAS_ORIENTADOR));

		// Per�odo de submiss�o de relat�rios parciais
		parametrosForm.setInicioSubmissaoRelatorioParcial(f
				.formatarData(helper.getParametroDate(ConstantesParametro.INICIO_SUBMISSAO_RELATORIO_PARCIAL)));
		parametrosForm.setFimSubmissaoRelatorioParcial(f
				.formatarData(helper.getParametroDate(ConstantesParametro.FIM_SUBMISSAO_RELATORIO_PARCIAL)));

		// Per�odo de consultoria
		parametrosForm.setInicioAvaliacaoConsultores(
				f.formatarData(helper.getParametroDate(ConstantesParametro.INICIO_PERIODO_CONSULTORIA)));
		parametrosForm.setFimAvaliacaoConsultores(
				f.formatarData(helper.getParametroDate(ConstantesParametro.FIM_PERIODO_CONSULTORIA)));

		// Limite de indica��o de bolsista
		parametrosForm.setLimiteSubstituicao(helper.getParametroInt(ConstantesParametro.LIMITE_ALTERACAO_BOLSISTA));
		
		// Email de notifica��o de altera��es em bolsistas
		parametrosForm.setEmailNotificacao(helper.getParametro(ConstantesParametro.EMAIL_NOTIFICACAO_BOLSISTA));
		
		// Email para recebimento de notifica��es de inven��o submetidas no SIGAA
		parametrosForm.setEmailNotificacaoInvencao(helper.getParametro(ConstantesParametro.EMAIL_NOTIFICACAO_INVENCAO));
		
		// Permite envio de relat�rios parciais pelos alunos de inicia��o cient�fica? (Sim/N�o)
		parametrosForm.setEnvioRelatorioParcial(helper.getParametroBoolean(ParametrosPesquisa.ENVIO_RELATORIO_PARCIAL));
		
		// Permite envio de resumos do CIC independentes? (Sim/N�o)
		parametrosForm.setResumoIndependente(helper.getParametroBoolean(ParametrosPesquisa.ENVIO_RESUMO_INDEPENDENTE));

		req.setAttribute(mapping.getName(), parametrosForm);
		return mapping.findForward(FORM);
	}

	/**
	 * Persiste no banco as altera��es nos par�metros realizadas pelo usu�rio.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/WEB-INF/jsp/pesquisa/ParametrosPesquisa/form.jsp</li>
	 * </ul>
	 */
	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		ParametrosPesquisaForm parametrosForm = (ParametrosPesquisaForm) form;

		ListaMensagens lista = new ListaMensagens();
		ParametrosPesquisaValidator.validaParametros( parametrosForm, lista );

		if( lista.isErrorPresent() )
			return mapping.findForward(FORM);

		// Persistir altera��es
		persistirParametro(ConstantesParametro.NUMERO_MAXIMO_RENOVACOES_PROJETO, parametrosForm.getQtdMaximaRenovacoes(), req);
		persistirParametro(ConstantesParametro.HORAS_TOLERANCIA_SUBMISSAO, parametrosForm.getToleranciaSubmissao(), req);
		persistirParametro(ConstantesParametro.DURACAO_MAXIMA_NOVOS_PROJETOS, parametrosForm.getDuracaoMaximaProjetos(), req);
		persistirParametro(ConstantesParametro.LIMITE_COTAS_PROJETO, parametrosForm.getLimiteCotasProjeto(), req);
		persistirParametro(ConstantesParametro.LIMITE_COTAS_ORIENTADOR, parametrosForm.getLimiteCotasOrientador(), req);

		persistirParametro(ConstantesParametro.LIMITE_ALTERACAO_BOLSISTA, parametrosForm.getLimiteSubstituicao(), req);
		persistirParametro(ConstantesParametro.EMAIL_NOTIFICACAO_BOLSISTA, parametrosForm.getEmailNotificacao(), req);
		persistirParametro(ConstantesParametro.EMAIL_NOTIFICACAO_INVENCAO, parametrosForm.getEmailNotificacaoInvencao(), req);
		persistirParametro(ParametrosPesquisa.ENVIO_RELATORIO_PARCIAL, String.valueOf(parametrosForm.isEnvioRelatorioParcial()), req);
		persistirParametro(ParametrosPesquisa.ENVIO_RESUMO_INDEPENDENTE, String.valueOf(parametrosForm.isResumoIndependente()), req);
		
		addInformation("Par�metros atualizados com sucesso!", req);

		return getMappingSubSistema(req, mapping);
	}

	/**
	 * M�todo auxiliar utilizado para persistir um par�metro de valor num�rico no banco
	 * 
	 * @param codigoParametro
	 * @param valor
	 * @param req
	 * @throws ArqException
	 * @throws RemoteException
	 * @throws NegocioException
	 */
	private void persistirParametro(String codigoParametro, long valor, HttpServletRequest req) throws ArqException, RemoteException, NegocioException {

		ParametroDao dao = getDAO(ParametroDao.class, req);

		Parametro parametro = new Parametro();
		parametro = dao.findByPrimaryKey(codigoParametro);

		if (valor != Long.parseLong(parametro.getValor())) {
			prepareMovimento(SigaaListaComando.ALTERAR_PARAMETROS, req);

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ALTERAR_PARAMETROS);

			parametro.setValor(String.valueOf(valor));
			mov.setObjAuxiliar(parametro);
			execute(mov, req);
		}

	}
	
	/**
	 * M�todo auxiliar utilizado para persistir um par�metro de valor texto no banco
	 * 
	 * @param codigoParametro
	 * @param valor
	 * @param req
	 * @throws ArqException
	 * @throws RemoteException
	 * @throws NegocioException
	 */
	private void persistirParametro(String codigoParametro, String valor, HttpServletRequest req) throws ArqException, RemoteException, NegocioException {

		ParametroDao dao = getDAO(ParametroDao.class, req);

		Parametro parametro = new Parametro();
		parametro = dao.findByPrimaryKey(codigoParametro);

		if ( !valor.equals(parametro.getValor()) ) {
			prepareMovimento(SigaaListaComando.ALTERAR_PARAMETROS, req);

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ALTERAR_PARAMETROS);

			parametro.setValor(valor);
			mov.setObjAuxiliar(parametro);
			execute(mov, req);
		}

	}

}
