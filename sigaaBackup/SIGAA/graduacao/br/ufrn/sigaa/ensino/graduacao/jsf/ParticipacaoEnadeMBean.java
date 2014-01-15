/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 12/08/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.graduacao.dominio.ParticipacaoEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoENADE;

/**
 * Controller respons�vel por cadastrar/alterar os poss�veis tipos de
 * participa��es no ENADE.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Component("participacaoEnade")
@Scope("request")
public class ParticipacaoEnadeMBean extends SigaaAbstractController<ParticipacaoEnade> {
	
	/** Construtor padr�o. */
	public ParticipacaoEnadeMBean() {
		obj = new ParticipacaoEnade();
	}
	
	/** Inicia o cadastro de uma nova participa��o no ENADE.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/participacao_enad/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		setReadOnly(false);
		obj = new ParticipacaoEnade();
		return super.preCadastrar();
	}
	
	/**
	 * Cadastra/remove uma participa��o ENADE do banco.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/participacao_enad/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		checkChangeRole();
		validacaoDados(erros);
		if (hasErrors()) return null;
		if (getConfirmButton().equalsIgnoreCase("remover")) {
			MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.REMOVER);
			if (obj == null || obj.getId() == 0) {
				addMensagem(NAO_HA_OBJETO_REMOCAO);
				return null;
			} else {
				try {
					execute(mov);
					addMensagem(OPERACAO_SUCESSO);
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
					return forward(getListPage());
				} 
				setResultadosBusca(null);
				return redirectJSF(getListPage());
			}
		} else {
			return super.cadastrar();
		}
	}
	
	/** Valida os dados antes de cadastrar.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		try {
			Collection<ParticipacaoEnade> lista = getGenericDAO().findAllAtivos(ParticipacaoEnade.class, "descricao");
			if (lista != null) {
				for (ParticipacaoEnade participacao : lista) {
					if (participacao.getId() != obj.getId()
							&& participacao.getTipoEnade() == obj .getTipoEnade()
							&& StringUtils.toAscii(participacao.getDescricao())
								.equalsIgnoreCase(StringUtils.toAscii(obj .getDescricao()))) {
						mensagens.addErro("J� existe uma Participa��o ENADE cadastrada com o mesmo tipo e descri��o.");
						break;
					}
				}
			}
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			return false;
		}
		return super.validacaoDados(mensagens);
	}
	
	/** Retorna uma cole��o com todas participa��es ENADE.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection<ParticipacaoEnade> getAll() throws ArqException {
		if (all == null) {
			GenericDAO dao = null;
			dao = new GenericDAOImpl(getSistema(), getSessionRequest());
			String[] orderBy = {"tipoEnade", "descricao"};
			String[] ascDesc = {"asc","asc"};
			all = dao.findAll(ParticipacaoEnade.class, orderBy, ascDesc);
		}
		return all;
	}
	
	/** Retorna uma cole��o de SelecItem com os tipos de ENADE. 
	 * @return
	 */
	public Collection<SelectItem> getTipoEnadeCombo(){
		Collection<SelectItem> tipos = new ArrayList<SelectItem>();
		for(TipoENADE t: TipoENADE.values()) {
			tipos.add(new SelectItem(t.name(), t.toString()));
		    }
		return tipos;
	}
	
	/**
	 * Redireciona o usu�rio para a p�gina de listagem, ap�s o cadastro.
	 * <br/>M�todo n�o invocado por JSP�s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	/** Retorna o link para o formul�rio de cadastro.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/graduacao/participacao_enad/form.jsp";
	}
	
	/** Retorna o link para o formul�rio de listagem.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/graduacao/participacao_enad/lista.jsp";
	}
}
