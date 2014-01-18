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
 * Classe responsável por gerenciar os parâmetros acadêmicos do IMD.
 * 
 * @author Rafael Silva
 *
 */
@Scope("request")
@Component("parametrosAcademicosIMD")
public class ParametrosAcademicosIMDMBean extends
		SigaaAbstractController<ParametrosAcademicosIMD> {
	/** Redireciona para a página de alteração dos parâmetros */
	private final static String JSP_FORM_PARAMETROS = "/metropole_digital/parametros_academicos/form.jsf";
	/** Url referente ao menu do módulo */
	private final static String JSP_MENU = "/metropole_digital/menus/menu_imd.jsf";
	/**
	 * Guarda os parâmetros antigos;
	 */
	private ParametrosAcademicosIMD parametrosAntigos = new ParametrosAcademicosIMD();

	/**
	 * Construtor da Classe
	 */
	public ParametrosAcademicosIMDMBean() {
		obj = new ParametrosAcademicosIMD();
	}

	/**
	 * Redireciona o sistema para a página de configuração dos parâmetros
	 * acadêmico.<br/>
	 * 
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
	 * Cancela a operação de cadastro e redireciona para o menu principal.<br/>
	 * 
	 * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/metropole_digital/parametros_academicos/form.jsp</li>
     * </ul>
	 */
	public String cancelar() {
		clear();
		return super.cancelar();
	}

	/**
	 * Cadastra os parâmetros acadêmicos vigentes para o IMD.<br/>
	 *
	 * Método invocado pela(s) seguinte(s) JSP(s):
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
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Parâmetros");
			clear();
		}

		return redirect(JSP_MENU);
	}

	/**
	 * Limpa os dados do formulário.<br/>
	 * <br />
	 * Método não invocado por JSP(s):
	 * 
	 */
	public void clear() {
		obj = new ParametrosAcademicosIMD();
		parametrosAntigos = new ParametrosAcademicosIMD();

	}
}
