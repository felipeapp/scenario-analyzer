/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 22/06/2010
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.vestibular.StatusFotoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.vestibular.dominio.StatusFoto;

/**
 * Controller respons�vel pelo cadastro de status para a valida��o de fotos de
 * candidatos do vestibular.
 * 
 * @author �dipo Elder F. Melo
 */
@Component("statusFotoMBean")
@Scope("request")
public class StatusFotoMBean extends SigaaAbstractController<StatusFoto> {

	/** Cole��o de SelectItem de todos status de fotos de candidatos do vestibular. */
	private Collection<SelectItem> allCombo;

	/** Construtor padr�o. */
	public StatusFotoMBean() {
		obj = new StatusFoto();
	}

	/** Redireciona o candidato para a p�gina de listagem de fotos a validar.<br/>M�todo n�o invocado por JSP�s.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	/** Retorna o link para o formul�rio 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/vestibular/StatusFoto/form.jsf";
	}

	/** Retorna o link para a p�gina de listagem de fotos de candidatos.<br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/vestibular/StatusFoto/lista.jsf";
	}

	/** Retorna a cole��o de SelectItem de todos status de fotos de candidatos do vestibular. 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		if (allCombo == null) { 
			allCombo = toSelectItems(getGenericDAO().findAll(StatusFoto.class, "descricao", "asc"), "id", "descricao");
		}
		return allCombo;
	}
	
	/** Valida os dados do status.<br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		try {
			ValidatorUtil.validateRequired(obj.getDescricao(), "Descri��o do Status da Foto", mensagens);
			ValidatorUtil.validateMaxLength(obj.getRecomendacao(), 250, "Recomenda��o", mensagens);
			StatusFotoDao dao = getDAO(StatusFotoDao.class);
			if (obj.getId() == 0 && dao.hasStatusMesmaDescricao(obj.getDescricao()))
				mensagens.addErro("Existe Status de Foto cadastrado com a mesma descri��o.");
			dao.close();
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
		return mensagens.isErrorPresent();
	}

	/**
	 * Cadastra/altera o status da foto.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/StatusFoto/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,	NegocioException {
		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} else {
			validacaoDados(erros);
			if (hasErrors()) return null;
			return super.cadastrar();
		}
	}
}
