/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 16/08/2012
 *
 */
package br.ufrn.sigaa.gru.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.LinkedList;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.comum.gru.dominio.TipoArrecadacao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;

/**
 * Controller responsável pelo cadastro de {@link ConfiguracaoGRU Configurações
 * da GRU}.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Component("configuracaoGRUMBean") @Scope("request")
public class ConfiguracaoGRUMBean extends SigaaAbstractController<ConfiguracaoGRU> {

	/** Coleção de SelectItem de Tipos de Arrecadação. */
	private Collection<SelectItem> tipoArrecadacaoCombo;
	
	/** Construtor padrão. */
	public ConfiguracaoGRUMBean() {
		this.obj = new ConfiguracaoGRU();
	}

	/** Inicia o cadastro da Configuração da GRU.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/menus/cadastro.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		this.tipoArrecadacaoCombo = null;
		prepareMovimento(SigaaListaComando.CADASTRAR_CONFIGURACAO_GRU);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_CONFIGURACAO_GRU.getId());
		setConfirmButton("Cadastrar");
		return forward("/administracao/cadastro/ConfiguracaoGRU/form.jsp");
	}
	
	/** Cadastra a Configuração da GRU <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/administracao/cadastro/ConfiguracaoGRU/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		if (!isOperacaoAtiva(SigaaListaComando.CADASTRAR_CONFIGURACAO_GRU.getId())) {
			addMensagemErro("A operação já havia sido concluída. Por favor, reinicie os procedimentos.");
			return null;
		}
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_CONFIGURACAO_GRU.getId());
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			dao.initialize(obj.getGrupoEmissaoGRU());
		} finally {
			dao.close();
		}
		validacaoDados(erros);
		if (hasErrors()) return null;
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_CONFIGURACAO_GRU);
			mov.setObjMovimentado(obj);
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			notifyError(e);
			e.printStackTrace();
		}
		removeOperacaoAtiva();
		return cancelar();
	}
	
	/** Atualiza a Configuração da GRU.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/administracao/cadastro/ConfiguracaoGRU/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		prepareMovimento(SigaaListaComando.CADASTRAR_CONFIGURACAO_GRU);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_CONFIGURACAO_GRU.getId());
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			this.obj = dao.findByPrimaryKey(getParameterInt("id", 0), ConfiguracaoGRU.class);
			if (obj == null) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				obj = new ConfiguracaoGRU();
				return null;
			}
		} finally {
			dao.close();
		}
		setConfirmButton("Alterar");
		return forward("/administracao/cadastro/ConfiguracaoGRU/form.jsp");
	}
	
	/** Cancela o caso de uso.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/administracao/cadastro/ConfiguracaoGRU/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		boolean retornaLista = obj.getId() > 0;
		resetBean();
		try {
			if (retornaLista)
				return listar();
			else
				return redirectJSF(getSubSistema().getLink());
		} catch (ArqException e) {
			addMensagemErroPadrao();
			notifyError(e);
			e.printStackTrace();
		}
		return null;
	}

	/** Lista as Configurações de GRU cadastradas. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/menus/cadastro.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		checkChangeRole();
		all = null;
		return forward("/administracao/cadastro/ConfiguracaoGRU/lista.jsp");
	}

	/**  Verifica as permissões do usuário para o caso de uso.
	 * <br/>Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
	}
	
	/** Valida os dados antes de cadastrar.
	 * <br/>Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens lista) {
		if (!isEmpty(obj.getUnidade()) && isEmpty(obj.getUnidade().getNome()))
			obj.setUnidade(new UnidadeGeral());
		validateRequired(obj.getDescricao(), "Descrição", lista);
		if (isEmpty(obj.getGrupoEmissaoGRU()))
			validateRequired(obj.getGrupoEmissaoGRU(), "Grupo de Emissão da GRU", lista);
		lista.addAll(obj.validate());
		if (obj.isGruSimples() &&
				(isEmpty(obj.getGrupoEmissaoGRU().getCodigoGestao())
						|| isEmpty(obj.getGrupoEmissaoGRU().getCodigoUnidadeGestora()))) {
			lista.addErro("O Grupo de Emissão da GRU selecionado é para GRU Cobrança");
		}
		return lista.isErrorPresent();
	}
	
	/** Retorna uma coleção de Configurações de GRU.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllPaginado()
	 */
	@Override
	public Collection<ConfiguracaoGRU> getAllPaginado() throws ArqException {
		if (all == null) {
			GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
			try {
				all = dao.findAll(ConfiguracaoGRU.class, getPaginacao(), "descricao", "asc");
			} finally {
				dao.close();
			}
		}
		return all;
	}
	
	/** Retorna uma coleção de SelectItem de Tipos de Arrecadação.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getTipoArrecadacaoCombo() throws DAOException {
		if (tipoArrecadacaoCombo == null) {
			tipoArrecadacaoCombo = new LinkedList<SelectItem>();
			GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
			for (TipoArrecadacao grupo : dao.findAll(TipoArrecadacao.class, "codigoRecolhimento", "asc")){
				tipoArrecadacaoCombo.add(new SelectItem(grupo.getId(), grupo.toString()));
			}
		}
		return tipoArrecadacaoCombo;
	}

}
