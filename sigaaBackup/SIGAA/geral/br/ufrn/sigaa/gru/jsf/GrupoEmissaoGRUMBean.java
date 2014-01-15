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
import static br.ufrn.arq.util.ValidatorUtil.validateMinLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.LinkedList;

import javax.faces.event.ValueChangeEvent;
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
import br.ufrn.comum.gru.dominio.GrupoEmissaoGRU;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;

/**
 * Controller responsável pelo cadastro de {@link GrupoEmissaoGRU Grupo de Emissão de GRU}.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Component("grupoEmissaoGRUMBean") @Scope("session")
public class GrupoEmissaoGRUMBean extends SigaaAbstractController<GrupoEmissaoGRU> {

	/** Coleção de SelectItem de Grupos de Emissão de GRU. */
	private Collection<SelectItem> grupoEmissaoGRUCombo;
	/** Indica se o usuário está cadastrando um grupo para GRU Simples. */
	private boolean gruSimples;
	
	/** Construtor padrão. */
	public GrupoEmissaoGRUMBean() {
		this.obj = new GrupoEmissaoGRU();
	}
	
	/** Inicia o cadastro do Grupo de Emissão da GRU.<br />
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
		this.obj = new GrupoEmissaoGRU(); 
		this.grupoEmissaoGRUCombo = null;
		this.gruSimples = true;
		prepareMovimento(SigaaListaComando.CADASTRAR_GRUPO_EMISSAO_GRU);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_GRUPO_EMISSAO_GRU.getId());
		setConfirmButton("Cadastrar");
		return forward("/administracao/cadastro/GrupoEmissaoGRU/form.jsp");
	}
	
	/** Ajusta os parâmetros do tipo da GRU
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/administracao/cadastro/GrupoEmissaoGRU/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public void tipoGRUListener(ValueChangeEvent evt) {
		if (!isOperacaoAtiva(SigaaListaComando.CADASTRAR_GRUPO_EMISSAO_GRU.getId())) {
			addMensagemErro("A operação já havia sido concluída. Por favor, reinicie os procedimentos.");
			cancelar();
		}
		gruSimples = (Boolean) evt.getNewValue();
		if (!gruSimples) {
			obj.setCodigoGestao(null);
			obj.setCodigoUnidadeGestora(null);
		} else {
			obj.setAgencia(null);
			obj.setCodigoCedente(null);
			obj.setConvenio(null);
		}
		redirectMesmaPagina();
	}
	
	/** Cadastra o Grupo de Emissão da GRU <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/administracao/cadastro/GrupoEmissaoGRU/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		if (!isOperacaoAtiva(SigaaListaComando.CADASTRAR_GRUPO_EMISSAO_GRU.getId())) {
			addMensagemErro("A operação já havia sido concluída. Por favor, reinicie os procedimentos.");
			return null;
		}
		validacaoDados(erros);
		if (hasErrors()) return null;
		if (!gruSimples) {
			obj.setCodigoGestao(null);
			obj.setCodigoUnidadeGestora(null);
		} else {
			obj.setAgencia(null);
			obj.setCodigoCedente(null);
			obj.setConvenio(null);
		}
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_GRUPO_EMISSAO_GRU);
			mov.setObjMovimentado(obj);
			mov.setObjAuxiliar(this.gruSimples);
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
	
	/** Cancela o caso de uso.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/administracao/cadastro/GrupoEmissaoGRU/form.jsp</li>
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
	
	/** Atualiza um Grupo de Emissão da GRU.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/administracao/cadastro/GrupoEmissaoGRU/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		prepareMovimento(SigaaListaComando.CADASTRAR_GRUPO_EMISSAO_GRU);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_GRUPO_EMISSAO_GRU.getId());
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		this.obj = dao.findByPrimaryKey(getParameterInt("id", 0), GrupoEmissaoGRU.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			obj = new GrupoEmissaoGRU();
			return null;
		}
		if (isEmpty(obj.getCodigoGestao())) {
			gruSimples = false;
		} else {
			gruSimples = true;
		}
		setConfirmButton("Alterar");
		return forward("/administracao/cadastro/GrupoEmissaoGRU/form.jsp");
	}

	/** Lista os Grupos de Emissão de GRU cadastrados. <br />
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
		return forward("/administracao/cadastro/GrupoEmissaoGRU/lista.jsp");
	}

	/** Verifica as permissões do usuário para o caso de uso.
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
		if (gruSimples) {
			validateRequired(obj.getCodigoGestao(), "Código da Gestão", lista);
			if (!isEmpty(obj.getCodigoGestao())) validateMinLength(obj.getCodigoGestao(), 5, "Código da Gestão", lista);
			validateRequired(obj.getCodigoUnidadeGestora(), "Código da Unidade Gestora", lista);
			if (!isEmpty(obj.getCodigoUnidadeGestora())) validateMinLength(obj.getCodigoUnidadeGestora(), 6, "Código da Unidade Gestora", lista);
		} else {
			validateRequired(obj.getAgencia(), "Agência", lista);
			if (!isEmpty(obj.getCodigoUnidadeGestora())) validateMinLength(obj.getCodigoUnidadeGestora(), 6, "Agência", lista);
			validateRequired(obj.getCodigoCedente(), "Código Cedente", lista);
			if (!isEmpty(obj.getCodigoCedente())) validateMinLength(obj.getCodigoCedente(), 8, "Código Cedente", lista);
			validateRequired(obj.getConvenio(), "Número do Convênio", lista);
			if (!isEmpty(obj.getConvenio())) validateMinLength(obj.getConvenio().toString(), 7, "Número do Convênio", lista);
		}
		return lista.isErrorPresent();
	}
	
	/** Retorna uma coleção de SelectItem de Grupos de Emissão de GRU.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getGrupoEmissaoGRUCombo() throws DAOException {
		if (grupoEmissaoGRUCombo == null) {
			grupoEmissaoGRUCombo = new LinkedList<SelectItem>();
			GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
			for (GrupoEmissaoGRU grupo : dao.findAllAtivos(GrupoEmissaoGRU.class, "agencia", "ativo", "codigoCedente", "codigoGestao", "codigoUnidadeGestora", "convenio")){
				grupoEmissaoGRUCombo.add(new SelectItem(grupo.getId(), grupo.toString()));
			}
		}
		return grupoEmissaoGRUCombo;
	}
	
	/** Retorna uma coleção de Grupos de Emissão de GRU.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection<GrupoEmissaoGRU> getAll() throws ArqException {
		if (all == null) {
			GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
			try {
				String[] orderBy = {"agencia","codigoCedente","convenio","codigoGestao","codigoUnidadeGestora"};
				String[] ascDesc = {"asc", "asc", "asc", "asc", "asc"};
				all = dao.findAll(GrupoEmissaoGRU.class, orderBy, ascDesc);
			} finally {
				dao.close();
			}
		}
		return all;
	}

	/** Indica se o usuário está cadastrando um grupo para GRU Simples.
	 * @return
	 */
	public boolean isGruSimples() {
		return gruSimples;
	}

	/** Seta se o usuário está cadastrando um grupo para GRU Simples.
	 * @param gruSimples
	 */
	public void setGruSimples(boolean gruSimples) {
		this.gruSimples = gruSimples;
	}
}
