/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean para realizar operações de cadastro e fechamento dos inventários do acervo das bibliotecas do sistema.</p>
 * 
 * <p>Também realiza a operação de pagar registros realizados no inventário, para o caso que alguém realizou 
 *  algum registro equivocado.
 *  </p>
 * 
 * @author Felipe
 *
 */
@Scope(value="request")
@Component(value="inventarioAcervoBibliotecaMBean")
public class InventarioAcervoBibliotecaMBean extends SigaaAbstractController<InventarioAcervoBiblioteca> implements PesquisarMateriaisInformacionais{
	
	
	/** Tela de listagem de inventários */
	private static final String PAGINA_LISTA = "/biblioteca/InventarioAcervo/lista.jsp";
	
	/** Tela de cadastro de inventários */
	private static final String PAGINA_FORM = "/biblioteca/InventarioAcervo/form.jsp";
	
	/** Lista dos inventários da biblioteca selecionada na busca de inventários. */
	private List<InventarioAcervoBiblioteca> inventarios;
	
	/** O id da biblioteca selecionada no filtro da busca de inventários */
	private Integer idBibliotecaFiltroPesquisa;
	
	/** A biblioteca selecionada no cadastro de inventários */
	private Integer idBibliotecaSelecionada;

	/** Guarda o ids da coleção selecionada. Caso o inventário possua uma coleção só poderão ser registrados materiais da coleção escolhida. */
	private Integer idColecaoSelecionada;
	
	/** Indica se o usuário está seleciondo um inventário para realizar outra ação, ou no caso de uso normal de inventários. */
	private boolean selecionandoInventario = false;
	
	/** Inventário selecionado quando se está utilizando a pesquisa para seleção */
	private InventarioAcervoBiblioteca inventarioSelecionado;
	
	/** Materiais buscados pelo usuário */
	private List<MaterialInformacional> materiaisBuscadosPeloUsuario;
	
	/**
	 * Construtor padrão.
	 */
	public InventarioAcervoBibliotecaMBean() {
		
	}
	
	/**
	 * Inicia o fluxo de navegação da página de listagem de inventários 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/menus/cadastros.jsp 
	 */
	public String iniciarPesquisaInventarios(){
		selecionandoInventario = false;
		return telaListaInventarios();
	}
	
	
	
	

	/**
	 * Inicia o fluxo de navegação da página de cadstro de inventários para a operação de cadastro
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
	 * Inicia o fluxo de navegação da página de cadastro de inventários para a operação de atualização
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public String preAtualizar() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRA_INVENTARIO_ACERVO_BIBLIOTECA);

		limparCampos();
		
		int id = getParameterInt("idInventario", 0);
		
		if (id == 0) {
			addMensagemErro("O Inventário não foi selecionado corretamente.");
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
	 * Lista os inventários da biblioteca selecionada no filtro de busca 
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
				addMensagemErro("Esta biblioteca não possui inventários.");
			}
			
			return telaListaInventarios();
		} finally {
			if(dao != null) dao.close();
		}
	}

	/**
	 * Cadastra/atualiza um inventário, e ao final retorna para a tela de listagem 
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
				idBibliotecaFiltroPesquisa = obj.getBiblioteca().getId(); // para atualiza a listagem dos inventários com a biblioteca cadatrada.
			
			if (isAtualizacao) {
				addMensagemInformation("Inventário alterado com sucesso.");
			} else {
				addMensagemInformation("Inventário cadastrado com sucesso.");
			}
			
			return buscarInventarios(); // para atualiza a listagem dos inventários com a biblioteca cadatrada.
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	/**
	 * Fecha um inventário aberto 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public String fecharInventario() throws ArqException {
		
		prepareMovimento(SigaaListaComando.FECHA_INVENTARIO_ACERVO_BIBLIOTECA);
		
		int id = getParameterInt("idInventario", 0);
		
		if (id == 0) {
			addMensagemErro("Inventário inválido.");
			return null;
		}
		
		obj = new InventarioAcervoBiblioteca(id);
		
		try {			
			MovimentoCadastro mov = new MovimentoCadastro();
			
			mov.setCodMovimento(SigaaListaComando.FECHA_INVENTARIO_ACERVO_BIBLIOTECA);
			mov.setObjMovimentado(obj);
			
			execute(mov);
			
			addMensagemInformation("Inventário fechado com sucesso.");
			
			return buscarInventarios();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	/**
	 * Reabre um inventário fecahdo 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/lista.jsp 
	 */
	public String abrirInventario() throws ArqException {
		prepareMovimento(SigaaListaComando.ABRE_INVENTARIO_ACERVO_BIBLIOTECA);
		
		int id = getParameterInt("idInventario", 0);
		
		if (id == 0) {
			addMensagemErro("Inventário inválido.");
			
			return null;
		}
		
		obj = new InventarioAcervoBiblioteca(id);
		
		try {			
			MovimentoCadastro mov = new MovimentoCadastro();
			
			mov.setCodMovimento(SigaaListaComando.ABRE_INVENTARIO_ACERVO_BIBLIOTECA);
			mov.setObjMovimentado(obj);
			
			execute(mov);
			
			addMensagemInformation("Inventário reaberto com sucesso.");
			
			return buscarInventarios();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////  A parte de apagar registros dos inventários em aberto  ////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Inicia o fluxo de navegação da página de listagem de inventários 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/menus/cadastros.jsp 
	 */
	public String iniciarRemocaoRegistro(){
		selecionandoInventario = true;
		return telaListaInventarios();
	}
	
	/**
	 * A partir do inventário selecionado, encaminha o usuário para buscar os registros que deseja remover desse inventário.
	 * 
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Esse inventário já está fechado. Não pode ter seus registros alterados.");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.REMOVE_REGISTRO_MATERIAIS_INVENTARIO);
		
		PesquisaMateriaisInformacionaisMBean pBean = getMBean ("pesquisaMateriaisInformacionaisMBean");
		return pBean.iniciarBusca(this, "Pesquisar Materiais Registrados Acervo", 
				" <p>Caro usuário,</p> <p>Por favor, busque os materiais que foram registrados indevidamente no Inventário. </p> "
				, "Remover Registro");
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	////////////////////////////       Métodos da interface de busca  ////////////////////////////////////
	
	/**
	 * Ver comentário na interface implementada.
	 */
	@Override
	public void setMateriaisPesquisaPadraoMateriais(List<MaterialInformacional> materiais) throws ArqException {
		materiaisBuscadosPeloUsuario = materiais;
	}

	/**
	 *  Ver comentário na interface implementada.
	 * 
	 *   <br/>
	 *  Método não chamado por nenhuma JSP.
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

	
	
	/** Forma a mensagem para o usuário de quais registros foram removidos e quais não. */
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
		bufferNaoRemovidos.append(" não estavam registrados para o inventário selecionado.");
		
		if(codigosDeBarrasRegistrosRemovidos.size() > 0)
			addMensagemInformation(bufferRemovidos.toString());
		
		if(codigosDeBarrasRegistrosNaoRemovidos.size() > 0)
			addMensagemErro(bufferNaoRemovidos.toString());
		
	}

	/**
	 *  Ver comentário na interface implementada.
	 * 
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Não é utilizado em JSP
	 */
	private void preencherCampos() {
		idBibliotecaSelecionada = obj.getBiblioteca().getId();
		idColecaoSelecionada = obj.getColecao() != null ? obj.getColecao().getId() : null; 
	}

	/**
	 * Monta os campos do objeto a ser cadastrado com os valores selecionados na tela  
	 * 
	 * Não é utilizado em JSP
	 */
	private void montarCampos() {
		obj.setBiblioteca(new Biblioteca(idBibliotecaSelecionada));
		if(idColecaoSelecionada != null && idColecaoSelecionada > 0)
			obj.setColecao(new Colecao(idColecaoSelecionada));
	}

	/**
	 * Valida os campos preenchidos pelo usuário 
	 * 
	 * Não é utilizado em JSP 
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
	 *  Retorna todas as bibliotecas internas ativas do sistema para as quais o usuário tem permissão de cadastrar um novo inventário
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Não é utilizado em JSP
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
	 * Redireciona o fluxo de navegação para a tela de listagem de inventários 
	 * 
	 * Não utilizado em JSP 
	 */
	private String telaListaInventarios() {
		return forward(PAGINA_LISTA);
	}
	
	
	
	/**
	 * Volta para a tela de listagem de inventários 
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/InventarioAcervo/form.jsp 
	 */
	public String voltar() {
		return telaListaInventarios();
	}

	/**
	 * Volta para a tela de listagem de inventários 
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
