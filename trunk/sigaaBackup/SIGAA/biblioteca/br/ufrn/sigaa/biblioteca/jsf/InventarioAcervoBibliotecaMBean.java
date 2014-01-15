/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 08/03/2012
 *
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.InventarioAcervoBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoRegistraMateriaisInventario;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * <p>MBean para realizar opera��es de cadastro e fechamento dos invent�rios do acervo das bibliotecas do sistema.</p>
 * 
 * <p>Tamb�m realiza a opera��o de pagar registros realizados no invent�rio, para o caso que algu�m realizou 
 *  algum registro equivocado.
 *  </p>
 * 
 * @author Felipe
 *
 */
@Scope(value="request")
@Component(value="inventarioAcervoBibliotecaMBean")
public class InventarioAcervoBibliotecaMBean extends SigaaAbstractController<InventarioAcervoBiblioteca> implements PesquisarMateriaisInformacionais{
	
	
	/** Tela de listagem de invent�rios */
	private static final String PAGINA_LISTA = "/biblioteca/InventarioAcervo/lista.jsp";
	
	/** Tela de cadastro de invent�rios */
	private static final String PAGINA_FORM = "/biblioteca/InventarioAcervo/form.jsp";
	
	/** Lista dos invent�rios da biblioteca selecionada na busca de invent�rios. */
	private List<InventarioAcervoBiblioteca> inventarios;
	
	/** O id da biblioteca selecionada no filtro da busca de invent�rios */
	private Integer idBibliotecaFiltroPesquisa;
	
	/** A biblioteca selecionada no cadastro de invent�rios */
	private Integer idBibliotecaSelecionada;

	/** Guarda o ids da cole��o selecionada. Caso o invent�rio possua uma cole��o s� poder�o ser registrados materiais da cole��o escolhida. */
	private Integer idColecaoSelecionada;
	
	/** Indica se o usu�rio est� seleciondo um invent�rio para realizar outra a��o, ou no caso de uso normal de invent�rios. */
	private boolean selecionandoInventario = false;
	
	/** Invent�rio selecionado quando se est� utilizando a pesquisa para sele��o */
	private InventarioAcervoBiblioteca inventarioSelecionado;
	
	/** Materiais buscados pelo usu�rio */
	private List<MaterialInformacional> materiaisBuscadosPeloUsuario;
	
	/**
	 * Construtor padr�o.
	 */
	public InventarioAcervoBibliotecaMBean() {
		
	}
	
	/**
	 * Inicia o fluxo de navega��o da p�gina de listagem de invent�rios 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/menus/cadastros.jsp 
	 */
	public String iniciarPesquisaInventarios(){
		selecionandoInventario = false;
		return telaListaInventarios();
	}
	
	
	
	

	/**
	 * Inicia o fluxo de navega��o da p�gina de cadstro de invent�rios para a opera��o de cadastro
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public String preCadastrar() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRA_INVENTARIO_ACERVO_BIBLIOTECA);

		limparCampos();
		
		obj = new InventarioAcervoBiblioteca();
		
		setConfirmButton("Cadastrar");
		
		return forward(PAGINA_FORM);
	}

	/**
	 * Inicia o fluxo de navega��o da p�gina de cadastro de invent�rios para a opera��o de atualiza��o
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public String preAtualizar() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRA_INVENTARIO_ACERVO_BIBLIOTECA);

		limparCampos();
		
		int id = getParameterInt("idInventario", 0);
		
		if (id == 0) {
			addMensagemErro("O Invent�rio n�o foi selecionado corretamente.");
			return null;
		}
		
		for (InventarioAcervoBiblioteca inventario : inventarios) {
			if(inventario.getId() == id){
				obj = inventario;
				preencherCampos();
				break;
			}
		}
		
		setConfirmButton("Alterar");
		
		return forward(PAGINA_FORM);		
	}

	
	/**
	 * Lista os invent�rios da biblioteca selecionada no filtro de busca 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public String buscarInventarios() throws DAOException {
		
		if (idBibliotecaFiltroPesquisa == -1) {
			addMensagemErro("Selecione uma biblioteca.");
			
			return telaListaInventarios();
		}
		
		InventarioAcervoBibliotecaDao dao = null;
		
		try {
			dao = getDAO(InventarioAcervoBibliotecaDao.class);
			
			inventarios = dao.findAllByBibliotecaAgrupado(idBibliotecaFiltroPesquisa);
			
			if (inventarios.size() == 0) {
				addMensagemErro("Esta biblioteca n�o possui invent�rios.");
			}
			
			return telaListaInventarios();
		} finally {
			if(dao != null) dao.close();
		}
	}

	/**
	 * Cadastra/atualiza um invent�rio, e ao final retorna para a tela de listagem 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/form.jsp 
	 */
	public String cadastrar() throws ArqException {
		boolean isAtualizacao = obj.getId() > 0;
		
		try {
			validarCampos();
			
			MovimentoCadastro mov = new MovimentoCadastro();
			
			mov.setCodMovimento(SigaaListaComando.CADASTRA_INVENTARIO_ACERVO_BIBLIOTECA);
			mov.setObjMovimentado(obj);

			montarCampos();
			
			if (!isAtualizacao) {
				obj.setAberto(true);
			}
			
			obj = execute(mov);
			
			if(obj != null && obj.getBiblioteca() != null)
				idBibliotecaFiltroPesquisa = obj.getBiblioteca().getId(); // para atualiza a listagem dos invent�rios com a biblioteca cadatrada.
			
			if (isAtualizacao) {
				addMensagemInformation("Invent�rio alterado com sucesso.");
			} else {
				addMensagemInformation("Invent�rio cadastrado com sucesso.");
			}
			
			return buscarInventarios(); // para atualiza a listagem dos invent�rios com a biblioteca cadatrada.
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	/**
	 * Fecha um invent�rio aberto 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public String fecharInventario() throws ArqException {
		
		prepareMovimento(SigaaListaComando.FECHA_INVENTARIO_ACERVO_BIBLIOTECA);
		
		int id = getParameterInt("idInventario", 0);
		
		if (id == 0) {
			addMensagemErro("Invent�rio inv�lido.");
			return null;
		}
		
		obj = new InventarioAcervoBiblioteca(id);
		
		try {			
			MovimentoCadastro mov = new MovimentoCadastro();
			
			mov.setCodMovimento(SigaaListaComando.FECHA_INVENTARIO_ACERVO_BIBLIOTECA);
			mov.setObjMovimentado(obj);
			
			execute(mov);
			
			addMensagemInformation("Invent�rio fechado com sucesso.");
			
			return buscarInventarios();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	/**
	 * Reabre um invent�rio fecahdo 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public String abrirInventario() throws ArqException {
		prepareMovimento(SigaaListaComando.ABRE_INVENTARIO_ACERVO_BIBLIOTECA);
		
		int id = getParameterInt("idInventario", 0);
		
		if (id == 0) {
			addMensagemErro("Invent�rio inv�lido.");
			
			return null;
		}
		
		obj = new InventarioAcervoBiblioteca(id);
		
		try {			
			MovimentoCadastro mov = new MovimentoCadastro();
			
			mov.setCodMovimento(SigaaListaComando.ABRE_INVENTARIO_ACERVO_BIBLIOTECA);
			mov.setObjMovimentado(obj);
			
			execute(mov);
			
			addMensagemInformation("Invent�rio reaberto com sucesso.");
			
			return buscarInventarios();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////  A parte de apagar registros dos invent�rios em aberto  ////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Inicia o fluxo de navega��o da p�gina de listagem de invent�rios 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/menus/cadastros.jsp 
	 */
	public String iniciarRemocaoRegistro(){
		selecionandoInventario = true;
		return telaListaInventarios();
	}
	
	/**
	 * A partir do invent�rio selecionado, encaminha o usu�rio para buscar os registros que deseja remover desse invent�rio.
	 * 
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<li>/sigaa/biblioteca/InventarioAcervo/lista.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionouInventario() throws ArqException{
		
		int idInventarioSelecionado = getParameterInt("idInventario", 0);
		
		for (InventarioAcervoBiblioteca inventario : inventarios) {
			if(inventario.getId() == idInventarioSelecionado){
				inventarioSelecionado = inventario;
				break;
			}
		}
		
		if(! inventarioSelecionado.isAberto()){
			addMensagemErro("Esse invent�rio j� est� fechado. N�o pode ter seus registros alterados.");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.REMOVE_REGISTRO_MATERIAIS_INVENTARIO);
		
		PesquisaMateriaisInformacionaisMBean pBean = getMBean ("pesquisaMateriaisInformacionaisMBean");
		return pBean.iniciarBusca(this, "Pesquisar Materiais Registrados Acervo", 
				" <p>Caro usu�rio,</p> <p>Por favor, busque os materiais que foram registrados indevidamente no Invent�rio. </p> "
				, "Remover Registro");
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	////////////////////////////       M�todos da interface de busca  ////////////////////////////////////
	
	/**
	 * Ver coment�rio na interface implementada.
	 */
	@Override
	public void setMateriaisPesquisaPadraoMateriais(List<MaterialInformacional> materiais) throws ArqException {
		materiaisBuscadosPeloUsuario = materiais;
	}

	/**
	 *  Ver coment�rio na interface implementada.
	 * 
	 *   <br/>
	 *  M�todo n�o chamado por nenhuma JSP.
	 */
	@Override
	public String selecionouMateriaisPesquisaPadraoMateriais()throws ArqException {
		
		try {			
			
			List<String> codigosDeBarrasEnviados = new ArrayList<String>();
			
			for (MaterialInformacional material : materiaisBuscadosPeloUsuario) {
				codigosDeBarrasEnviados.add(material.getCodigoBarras());
			}
			
			MovimentoRegistraMateriaisInventario mov = new MovimentoRegistraMateriaisInventario(inventarioSelecionado, materiaisBuscadosPeloUsuario);
			mov.setCodMovimento(SigaaListaComando.REMOVE_REGISTRO_MATERIAIS_INVENTARIO);
			
			List<String> codigosDeBarrasRegistrosRemovidos =  execute(mov);
			
			formataMensagemUsuario(codigosDeBarrasEnviados, codigosDeBarrasRegistrosRemovidos);
				
			return telaListaInventarios();
			
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}

	
	
	/** Forma a mensagem para o usu�rio de quais registros foram removidos e quais n�o. */
	private void formataMensagemUsuario(List<String> codigosDeBarrasEnviados, List<String> codigosDeBarrasRegistrosRemovidos) {
		
		List<String> codigosDeBarrasRegistrosNaoRemovidos = new ArrayList<String>();
		
		for (String codigoBarrasRegistroEnviado : codigosDeBarrasEnviados) {
			
			if(! codigosDeBarrasRegistrosRemovidos.contains(codigoBarrasRegistroEnviado)){
				codigosDeBarrasRegistrosNaoRemovidos.add(codigoBarrasRegistroEnviado);
			}
			
		} 
		
		StringBuilder bufferRemovidos = new StringBuilder(" Os registros dos materiais: ");
		StringBuilder bufferNaoRemovidos = new StringBuilder(" Os materiais: ");
		
		int contador1 = 0;
		for (String codigoBarras : codigosDeBarrasRegistrosRemovidos) {
			
			if(contador1 < codigosDeBarrasRegistrosRemovidos.size()-1)
				bufferRemovidos.append(codigoBarras+", ");
			else
				bufferRemovidos.append(codigoBarras);
				
			contador1++;
		}
		int contador2 = 0;
		for (String codigoBarras: codigosDeBarrasRegistrosNaoRemovidos) {
			
			if(contador2 < codigosDeBarrasRegistrosNaoRemovidos.size()-1)
				bufferNaoRemovidos.append(codigoBarras+", ");
			else
				bufferNaoRemovidos.append(codigoBarras);
				
			contador2++;
		}
		
		
		
		bufferRemovidos.append(" foram removidos com sucesso.");
		bufferNaoRemovidos.append(" n�o estavam registrados para o invent�rio selecionado.");
		
		if(codigosDeBarrasRegistrosRemovidos.size() > 0)
			addMensagemInformation(bufferRemovidos.toString());
		
		if(codigosDeBarrasRegistrosNaoRemovidos.size() > 0)
			addMensagemErro(bufferNaoRemovidos.toString());
		
	}

	/**
	 *  Ver coment�rio na interface implementada.
	 * 
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    	<li>/sigaa/biblioteca/pesquisaPadraoMaterialInformacional.jsp</li>
	 *   </ul>
	 */
	@Override
	public String voltarBuscaPesquisaPadraoMateriais() throws ArqException {
		return telaListaInventarios();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Preenche os campos da tela com os valores atuais do objeto a ser atualizado  
	 * 
	 * N�o � utilizado em JSP
	 */
	private void preencherCampos() {
		idBibliotecaSelecionada = obj.getBiblioteca().getId();
		idColecaoSelecionada = obj.getColecao() != null ? obj.getColecao().getId() : null; 
	}

	/**
	 * Monta os campos do objeto a ser cadastrado com os valores selecionados na tela  
	 * 
	 * N�o � utilizado em JSP
	 */
	private void montarCampos() {
		obj.setBiblioteca(new Biblioteca(idBibliotecaSelecionada));
		if(idColecaoSelecionada != null && idColecaoSelecionada > 0)
			obj.setColecao(new Colecao(idColecaoSelecionada));
	}

	/**
	 * Valida os campos preenchidos pelo usu�rio 
	 * 
	 * N�o � utilizado em JSP 
	 * @throws NegocioException 
	 */
	private void validarCampos() throws NegocioException {
		ListaMensagens erros = new ListaMensagens();
		
		if (idBibliotecaSelecionada == -1) {
			erros.addErro("Selecione uma biblioteca.");
		}
		
		if (erros.size() > 0) {			
			throw new NegocioException(erros);
		}
	}
	
	
	/**
	 *  Retorna todas as bibliotecas internas ativas do sistema para as quais o usu�rio tem permiss�o de cadastrar um novo invent�rio
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/InventarioAcervo/form.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getAllPermissaoUsuarioCombo() throws DAOException{
		
		BibliotecaDao dao = null;
		
		try {
			dao = getDAO(BibliotecaDao.class);
			Collection <Biblioteca> biblbiotecas = new ArrayList<Biblioteca>();
			
			if(! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
				biblbiotecas  = dao.findAllBibliotecasInternasAtivasPorUnidade(idUnidades);
			}else{
				biblbiotecas  = dao.findAllBibliotecasInternasAtivas();
			}
			
			return toSelectItems(biblbiotecas, "id", "descricaoCompleta");
		} finally {
			if (dao != null) dao.close();
		}
		
	}
	

	/**
	 * Limpa os campos da tela 
	 * 
	 * N�o � utilizado em JSP
	 */
	private void limparCampos() {
		obj = null;
		idBibliotecaSelecionada = null;
	}

	/**
	 * Limpa os campos da tela 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public void limparCamposBusca() {
		idBibliotecaFiltroPesquisa = null;
		inventarios = null;
	}

	/**
	 * Redireciona o fluxo de navega��o para a tela de listagem de invent�rios 
	 * 
	 * N�o utilizado em JSP 
	 */
	private String telaListaInventarios() {
		return forward(PAGINA_LISTA);
	}
	
	
	
	/**
	 * Volta para a tela de listagem de invent�rios 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/form.jsp 
	 */
	public String voltar() {
		return telaListaInventarios();
	}

	/**
	 * Volta para a tela de listagem de invent�rios 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public String voltarBusca() {
		return cancelar();
	}
	
	
	
	// Gets e sets

	

	

	public List<InventarioAcervoBiblioteca> getInventarios() {
		return inventarios;
	}

	public Integer getIdBibliotecaFiltroPesquisa() {
		return idBibliotecaFiltroPesquisa;
	}

	public void setIdBibliotecaFiltroPesquisa(Integer idBibliotecaFiltroPesquisa) {
		this.idBibliotecaFiltroPesquisa = idBibliotecaFiltroPesquisa;
	}

	public void setInventarios(List<InventarioAcervoBiblioteca> inventarios) {
		this.inventarios = inventarios;
	}

	public Integer getIdBibliotecaSelecionada() {
		return idBibliotecaSelecionada;
	}

	public void setIdBibliotecaSelecionada(Integer idBibliotecaSelecionada) {
		this.idBibliotecaSelecionada = idBibliotecaSelecionada;
	}

	public Integer getIdColecaoSelecionada() {
		return idColecaoSelecionada;
	}

	public void setIdColecaoSelecionada(Integer idColecaoSelecionada) {
		this.idColecaoSelecionada = idColecaoSelecionada;
	}

	public boolean isSelecionandoInventario() {
		return selecionandoInventario;
	}

	public void setSelecionandoInventario(boolean selecionandoInventario) {
		this.selecionandoInventario = selecionandoInventario;
	}

	
	
}
