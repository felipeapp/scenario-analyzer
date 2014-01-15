/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/09/2009
 *
 */
package br.ufrn.sigaa.diploma.jsf;

import java.util.Collection;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dominio.ControleNumeroRegistro;

/** Controller responsável pela requisição e auditoria de Números de Registro de Diplomas.
 * @author Édipo Elder F. Melo
 *
 */
@Component("requisicaoNumeroRegistro")
@Scope("request")
public class RequisicaoNumeroRegistroMBean extends SigaaAbstractController<ControleNumeroRegistro> {

	/** Indica se a busca deverá filtrar pelo número do registro do diploma. */
	private boolean buscaRegistro;
	/** Indica se a busca deverá filtrar pelo nome do usuário. */
	private boolean buscaUsuario;
	/** Indica se a busca deverá filtrar pela data de requisição do número de registro do diploma. */
	private boolean buscaData;
	
	/** Número do registro do diploma usado na busca.*/
	private int numeroRegistro;
	/** Nome do usuário que requisitou o número de registro do diploma. */
	private String nomeUsuario;
	/** Data que o número de registro do diploma foi requisitado.*/
	private Date solicitadoEm;
	
	/** Quantidade de números requisitados para registro de diplomas externos. */
	private int quantidade;
	
	/** Lista de requisições de números de diplomas encontrados na busca. */
	private Collection<ControleNumeroRegistro> listaControleNumeroRegistro;
	
	/** Lista de Números de Registro de Diplomas a ser utilizado em registro. */
	private Collection<Integer> listaNumeros;

	/**
	 * Inicia a operação de requisição de número de registro de diploma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/diplomas.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.REQUISITAR_NUMERO_REGISTRO_DIPLOMA);
		setOperacaoAtiva(SigaaListaComando.REQUISITAR_NUMERO_REGISTRO_DIPLOMA.getId());
		return forward(getFormPage());
	}

	/**
	 * Busca por números de registro de diplomas e retorna ao usuário.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/req_numero_registro/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		validacaoDados(erros);
		if (hasErrors()) return null;
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setUsuarioLogado(getUsuarioLogado());
		mov.setCodMovimento(SigaaListaComando.REQUISITAR_NUMERO_REGISTRO_DIPLOMA);
		mov.setAcao(quantidade);
		listaNumeros = execute(mov);
		return forward(getViewPage());
	}
	
	/** Valida os dados da requisição do número de registro de diploma.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		ValidatorUtil.validateMinValue(getQuantidade(), 1, "Quantidade", mensagens);
		return mensagens.isEmpty();
	}

	/**
	 * Busca por requisições de números de diplomas de acordo com os atributos
	 * setados.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/req_numero_registro/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String buscarRequisicoes() throws  DAOException, SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG, SigaaPapeis.GESTOR_LATO);
		int numeroRegistro = 0;
		String nomeUsuario = null;
		Date solicitadoEm = null;
		if (buscaData) {
			solicitadoEm = this.solicitadoEm;
		}
		if (buscaRegistro) {
			numeroRegistro = this.numeroRegistro;
		}
		if (buscaUsuario) {
			nomeUsuario = this.nomeUsuario;
		}
		if (getNiveisHabilitados().length == 1){
			obj.setNivel(getNiveisHabilitados()[0]);
		} else if (obj.getNivel() == '0') {
			addMensagemErro("Selecione um Nível de Ensino válido.");
		}
		if (solicitadoEm == null &&
				numeroRegistro == 0 &&
				nomeUsuario == null &&
				solicitadoEm == null) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		} 
		if (hasErrors())
			return null;
		RegistroDiplomaDao dao = getDAO(RegistroDiplomaDao.class);
		listaControleNumeroRegistro = dao.findControleNumeroDiploma(numeroRegistro, nomeUsuario, null, null, solicitadoEm, obj.getNivel());
		if (listaControleNumeroRegistro == null || listaControleNumeroRegistro.isEmpty()) 
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		return null;
	}

	/**
	 * Lista as requisições de número de registro de diplomas efetuadas. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/diplomas.jsp</li>
	 * <li>/sigaa.war/stricto/menus/diplomas.jsp</li>
	 * </ul>
	 * 
	 * 
	 */
	@Override
	public String listar() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG, SigaaPapeis.GESTOR_LATO);
		obj = new ControleNumeroRegistro();
		return super.listar();
	}
	
	/**
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/diplomas/req_numero_registro/form.jsp";
	}
	
	/**
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/diplomas/req_numero_registro/lista.jsp";
	}
	
	/**
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getViewPage()
	 */
	@Override
	public String getViewPage() {
		return "/diplomas/req_numero_registro/view.jsp";
	}

	/**
	 * Retorna a lista de números de registro de diplomas, separados por
	 * vírgula.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/req_numero_registro/view.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String getListaNumeros() {
		StringBuilder lista = new StringBuilder();
		if (listaNumeros == null || listaNumeros.isEmpty())
			return "Não há números disponíveis.";
		else {
			int k = 0;
			for (Integer i : listaNumeros) {
				lista.append(i);
				if (++k < listaNumeros.size())
					lista.append(", ");
			}
		}
		return lista.toString();
	}

	/** Retorna o nome do usuário que gerou o diploma.
	 * @return Nome do usuário que gerou o diploma. 
	 */
	public String getNomeUsuario() {
		return nomeUsuario;
	}

	/** Retorna o número do registro do diploma usado na busca.
	 * @return Número do registro do diploma usado na busca.
	 */
	public int getNumeroRegistro() {
		return numeroRegistro;
	}

	/** Indica se a busca deverá filtrar pela data de geração do diploma. 
	 * @return
	 */
	public boolean isBuscaData() {
		return buscaData;
	}

	/** Indica se a busca deverá filtrar pelo número do registro do diploma. 
	 * @return
	 */
	public boolean isBuscaRegistro() {
		return buscaRegistro;
	}

	/** Indica se a busca deverá filtrar pelo nome do usuário. 
	 * @return
	 */
	public boolean isBuscaUsuario() {
		return buscaUsuario;
	}

	/** Seta se a busca deverá filtrar pela data de geração do diploma. 
	 * @param buscaData
	 */
	public void setBuscaData(boolean buscaData) {
		this.buscaData = buscaData;
	}

	/** Seta se a busca deverá filtrar pelo número do registro do diploma. 
	 * @param buscaRegistro
	 */
	public void setBuscaRegistro(boolean buscaRegistro) {
		this.buscaRegistro = buscaRegistro;
	}

	/** Seta se a busca deverá filtrar pelo nome do usuário. 
	 * @param buscaUsuario
	 */
	public void setBuscaUsuario(boolean buscaUsuario) {
		this.buscaUsuario = buscaUsuario;
	}

	/** Seta o nome do usuário que gerou o diploma. 
	 * @param nomeUsuario Nome do usuário que gerou o diploma. 
	 */
	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	/** Seta o número do registro do diploma usado na busca.
	 * @param numeroRegistro Número do registro do diploma usado na busca.
	 */
	public void setNumeroRegistro(int numeroRegistro) {
		this.numeroRegistro = numeroRegistro;
	}

	/** Retorna a data que o número de registro do diploma foi requisitado.
	 * @return Data que o número de registro do diploma foi requisitado.
	 */
	public Date getSolicitadoEm() {
		return solicitadoEm;
	}

	/** Seta a data que o número de registro do diploma foi requisitado.
	 * @param solicitadoEm  Data que o número de registro do diploma foi requisitado.
	 */
	public void setSolicitadoEm(Date solicitadoEm) {
		this.solicitadoEm = solicitadoEm;
	}

	/** Retorna a quantidade de números requisitados para registro de diplomas externos.
	 * @return Quantidade de números requisitados para registro de diplomas externos.
	 */
	public int getQuantidade() {
		return quantidade;
	}

	/** Seta a quantidade de números requisitados para registro de diplomas externos.
	 * @param quantidade Quantidade de números requisitados para registro de diplomas externos.
	 */
	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	/** Retorna a lista de requisições de números de diplomas encontrados na busca.
	 * @return Lista de requisições de números de diplomas encontrados na busca.
	 */
	public Collection<ControleNumeroRegistro> getListaControleNumeroRegistro() {
		return listaControleNumeroRegistro;
	}

	/** Seta a lista de requisições de números de diplomas encontrados na busca.
	 * @param listaControleNumeroRegistro Lista de requisições de números de diplomas encontrados na busca.
	 */
	public void setListaControleNumeroRegistro(
			Collection<ControleNumeroRegistro> listaControleNumeroRegistro) {
		this.listaControleNumeroRegistro = listaControleNumeroRegistro;
	}

}
