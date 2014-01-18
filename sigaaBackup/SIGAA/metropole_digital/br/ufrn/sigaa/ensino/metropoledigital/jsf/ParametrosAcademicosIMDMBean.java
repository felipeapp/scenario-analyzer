package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ParametrosAcademicosIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ParametrosAcademicosIMD;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MovimentoCadastroParametros;


/**
 * Classe respons�vel por gerenciar os par�metros acad�micos do IMD.
 * 
 * @author Rafael Silva
 *
 */
@Scope("request")
@Component("parametrosAcademicosIMD")
public class ParametrosAcademicosIMDMBean extends
		SigaaAbstractController<ParametrosAcademicosIMD> {
	/** Redireciona para a p�gina de altera��o dos par�metros */
	private final static String JSP_FORM_PARAMETROS = "/metropole_digital/parametros_academicos/form.jsf";
	/** Url referente ao menu do m�dulo */
	private final static String JSP_MENU = "/metropole_digital/menus/menu_imd.jsf";
	/**
	 * Guarda os par�metros antigos;
	 */
	private ParametrosAcademicosIMD parametrosAntigos = new ParametrosAcademicosIMD();

	/**
	 * Construtor da Classe
	 */
	public ParametrosAcademicosIMDMBean() {
		obj = new ParametrosAcademicosIMD();
	}

	/**
	 * Redireciona o sistema para a p�gina de configura��o dos par�metros
	 * acad�mico.<br/>
	 * 
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
     * 		<li>/sigaa.war/metropole_digital/menus/curso.jsp</li>
     * </ul>
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public String initParametros() throws HibernateException, DAOException {
		ParametrosAcademicosIMDDao parametrosDao = getDAO(ParametrosAcademicosIMDDao.class);

		try {
			parametrosAntigos = parametrosDao.getParametrosAtivo();
			obj.setFrequenciaMinimaAprovacao(parametrosAntigos
					.getFrequenciaMinimaAprovacao());
			obj.setMediaAprovacao(parametrosAntigos.getMediaAprovacao());
			obj.setNotaMinimaRecuperacao(parametrosAntigos
					.getNotaMinimaRecuperacao());
			obj.setNotaReprovacaoComponente(parametrosAntigos
					.getNotaReprovacaoComponente());
			obj.setVigente(true);

		} finally {
			parametrosDao.close();
		}
		return forward(JSP_FORM_PARAMETROS);
	}

	/**
	 * Cancela a opera��o de cadastro e redireciona para o menu principal.<br/>
	 * 
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/metropole_digital/parametros_academicos/form.jsp</li>
     * </ul>
	 */
	public String cancelar() {
		clear();
		return super.cancelar();
	}

	/**
	 * Cadastra os par�metros acad�micos vigentes para o IMD.<br/>
	 *
	 * M�todo invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
     * 		<li>/sigaa.war/metropole_digital/parametros_academicos/form.jsp</li>
     * </ul>
	 */
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		if (!hasErrors()) {
			MovimentoCadastroParametros mov = new MovimentoCadastroParametros();
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setParametrosAntigos(parametrosAntigos);
			mov.setParametrosNovos(obj);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_PARAMETROS_ACADEMICOS_IMD);
			prepareMovimento(SigaaListaComando.CADASTRAR_PARAMETROS_ACADEMICOS_IMD);
			execute(mov);
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Par�metros");
			clear();
		}

		return redirect(JSP_MENU);
	}

	/**
	 * Limpa os dados do formul�rio.<br/>
	 * <br />
	 * M�todo n�o invocado por JSP(s):
	 * 
	 */
	public void clear() {
		obj = new ParametrosAcademicosIMD();
		parametrosAntigos = new ParametrosAcademicosIMD();

	}
}
