/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 29/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dao.DisseminacaoDaInformacaoDao;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.PerfilInteresseUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.AreaConhecimentoCNPqBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 *
 * <p> MBean respons�vel por gerenciar a parte na qual os usu�rios da biblioteca poder�o registrar seu interesse em algum tema cadastrado. 
 * Para receber avisos sobre esses temas quando um novo material � inclu�do no acervo.
 * </p>
 *
 *
 * <p> <i> Os temas referidos aqui s�o os assuntos e autores da base de autoridades do sistema.</i> </p>
 * 
 * @author jadson
 * @see Autoridade
 */
@Component(value="configuraPerfilInteresseUsuarioBibliotecaMBean")
@Scope(value="request")
public class ConfiguraPerfilInteresseUsuarioBibliotecaMBean extends SigaaAbstractController<PerfilInteresseUsuarioBiblioteca>{

	/** P�gina de lista os dados do perfil de interesse do usu�rio da biblioteca atualmente logado no sistema. */
	public static final String PAGINA_LISTA_MEU_PERFIL_DE_INTERESSE = "/biblioteca/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp";
	
	/** A quantidade m�xima de autoridades que o usu�rio pode registrar interesse (30 assunto + 30 Autores) */
	public static final Integer QUANTIDADE_MAXIMA_AUTORIDADES_INTERESSE = 30;
	
	

	
	 /** Lista com todas as grandes �reas de conhecimento ativas do sistema.*/
	private List<AreaConhecimentoCnpq> areasCNPQ;
	
	
	/** O tipo de abrang�ncia escolhido pelo usu�rio @deprecated Mudado a forma de visualizar as bibliotecas, mas n�o apague esse c�digo. caso queira voltar a forma antiga. */
	//private AbrangenciaDisseminacao tipoAbrangecia = AbrangenciaDisseminacao.TODAS_BIBLIOTECAS;
	
	/** Se o usu�rio escolher receber o informativo de todas as bibliotecas. O padr�o � sim. */
	private boolean escolheuTodasAsBibliotecas = true;
	
	/** Guarda a lista a lista de bibliotecas do sistema para n�o precisar realizar uma nova consulta a cada atualiza��o da p�gina.*/
	private List<Biblioteca> bibliotecaInternasAtivas;
	
	/** Vai ter um perfil por ub ativo no sistema.  */
	private UsuarioBiblioteca ubAtualUsuario; 
	
	/** O perfil de interesse do usu�rio atualmente logado. Que � o alvo desse caso de uso. */
	private PerfilInteresseUsuarioBiblioteca perfilInteresse; 
	
	
	
	/** Flag para imperdir que os dados do usu�rio sejam carregados sempre que a p�gina � atualizada
	 *  Os dados salvos do perfil do usu�rio devem apenas ser carregados na primeira vez.
	 *  
	 *  � necess�rio colocar um carregamento diretamente na p�gina, pois esse caso de uso pode ser 
	 *  redirecionado diretamente para a p�gina apartir da �reas p�blica.*/
	private boolean carregandoAPagina = true;
	
	
	
	
	
	
	//////////////  Os dados marcados pelo usu�rio que v�o fazer parte do seu perfil  /////////////////
	
	
	/** Guarda os ids da bibliotecas selecionadas pelo usu�rio, caso ele queira receber informa��es apenas de algumas bibliotecas */
	private List<String> bibliotecasSelecionadas = new ArrayList<String>();
	
	
	/** 
	 *  <p>Guarda a listagem de assuntos autorizados dispon�veis no sistema para o usu�rio registrar interresse.</p>
	 *  
	 *  <p>O carregamento � por demanda, por�m um vez carregado o assunto � mantido em mem�ria para n�o apagar aqueles que o usu�rio por acaso j� tenha marcado
	 *   na mudan�a entre p�ginas.
	 *  Por isso existe uma l�gica mais completa no paginamento desses resultados.
	 *  </p>
	 */
	private List<CacheEntidadesMarc> assuntosAutorizados = new ArrayList<CacheEntidadesMarc>();
	
	
	/** 
	 *  <p>Guarda a listagem de autores autorizados dispon�veis no sistema para o usu�rio registrar interresse.</p>
	 *  
	 *  <p>O carregamento � por demanda, por�m um vez carregado o assunto � mantido em mem�ria para n�o apagar aqueles que o usu�rio por acaso j� tenha marcado
	 *   na mudan�a entre p�ginas.
	 *  Por isso existe uma l�gica mais completa no paginamento desses resultados.
	 *  </p>
	 */
	private List<CacheEntidadesMarc> autoresAutorizados = new ArrayList<CacheEntidadesMarc>();
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////// A parte de busca autocomplete /////////////////////////////////////
	
	/** Guarda a informa��o do id da autoridade de assunto da busca pelo campo de auto complete. */
	private int idAutoridadeAssuntoBuscada;
	
	/** Guarda a informa��o do id da autoridade de autor da busca pelo campo de auto complete. */
	private int idAutoridadeAutorBuscada;
	
	/** Guarda a descri��o da autoridade busca no campo de auto complete*/
	private String descricaoAutoridadeAssuntoBuscada;
	
	/** Guarda a descri��o da autoridade busca no campo de auto complete*/
	private String descricaoAutoridadeAutorBuscada;
	
	/** Aguardas as autoridades buscasdas pelo usu�rio mas n�o adicionadas � lista */
	private List<CacheEntidadesMarc> autoridadesAssuntosTemp = new ArrayList<CacheEntidadesMarc>();
	
	/** Aguardas as autoridades buscasdas pelo usu�rio mas n�o adicionadas � lista */
	private List<CacheEntidadesMarc> autoridadesAutoresTemp = new ArrayList<CacheEntidadesMarc>();
	
	/** DataModel para preenchimento da tabela de pessoas do filtro de v�rias pessoas */
	private DataModel autoridadesAssuntoDataModel = new ListDataModel(assuntosAutorizados);
	
	/** DataModel para preenchimento da tabela de pessoas do filtro de v�rias pessoas */
	private DataModel autoridadesAutorDataModel = new ListDataModel(assuntosAutorizados);
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	/** Os tipos de abrang�ncias na dissemina��o da informa��o, se de todas as biblioteca ou alguma espec�fica */
	public enum AbrangenciaDisseminacao{
		/**Indica que o usu�rio selecionou todas as biblioteca para receber as notifica��se */
		TODAS_BIBLIOTECAS("Todas"), 
		/**Indica que o usu�rio selecionou 1 biblioteca espec�fica para receber as notifica��se */
		BIBLIOTECAS_ESPECIFICA("Selecione...");
		
		
		private AbrangenciaDisseminacao(String descricao){
			this.descricao = descricao;
		}
		
		/** O valor mostrado ao usu�rio*/
		private String descricao;
		
		/**
		 * Retorna a tipo de abrag�ncia a partir do seu valor ordinal
		 *
		 * @param valorOrdinal
		 * @return
		 */
		public static AbrangenciaDisseminacao getAbrangencia(int valorOrdinal){
			
			if(valorOrdinal == AbrangenciaDisseminacao.TODAS_BIBLIOTECAS.ordinal())
				return TODAS_BIBLIOTECAS;
			else
				return BIBLIOTECAS_ESPECIFICA;
		}

		public String getDescricao() {
			return descricao;
		}
	}     
	
	
	
	/**
	 * 
	 * <p>Iniciar o caso de uso no qual o usu�rio vai poder ver as informa��es do seu prefil de interesse na biblioteca 
	 * e adicionar ou remover interesse em algum tema.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/portais/discente/include/biblioteca.jsp</li>
	 *    <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *    <li>/sigaa.war/portais/discente/medio/menu_discente_medio.jsp</li>
	 *    <li>/sigaa.war/portais/docente/include/menu_docente.jsp</li>
	 *    <li>/sigaa.war/biblioteca/menus_modulo_biblioteca_servidor.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		
		return telaListaMeuPerfilDeInteresse();
	}
	
	
	
	/**
	 * 
	 * <p>Carrega as informa��es do usu�rio atualmente logado. M�todo chamado ao carregar a p�gina 
	 * porque a p�gina pode ser acessada diretamente por meio de um redirecionamento da busca p�blica.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String getCarregarMeusPersilInteresse() throws ArqException{
		
		if(carregandoAPagina){
		
			carregandoAPagina = false; // s� carrega na primeira vez que o usu�rio acessa a p�gina.
			
			DisseminacaoDaInformacaoDao dao = null;
			AreaConhecimentoCNPqBibliotecaDao areaDao = null;
			
			prepareMovimento(SigaaListaComando.CADASTRA_ATUALIZA_PERFIL_INTERESSE_USUARIO_BIBLIOTECA);
			
			try{
				
				
				ubAtualUsuario  = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), null);
				
				dao = getDAO(DisseminacaoDaInformacaoDao.class);
				
				perfilInteresse = dao.findPerfilUsuarioBiblioteca(ubAtualUsuario);
				
				areaDao = getDAO(AreaConhecimentoCNPqBibliotecaDao.class);
				areasCNPQ = (List<AreaConhecimentoCnpq>) areaDao.findGrandesAreasCNPqBibliotecaComProjecao();
				
				if(perfilInteresse.getId() > 0)
					configuraInformacoesSalvasPerfilUsuario(dao);
				
				autoridadesAssuntoDataModel = new ListDataModel(assuntosAutorizados);
				autoridadesAutorDataModel = new ListDataModel(autoresAutorizados);
				
			} catch (NegocioException e) {
				
				// Em caso de erro, redirecionar para a p�gina principal do sistema, para pode mostrar a mensagem para o usu�rio, j� que o caso de uso � chamada diretamente da p�gina //
				
				addMensagemErro("� preciso primeiro ter uma conta na biblioteca para utilizar essa opera��o. N�o foi encontrada nenhuma conta ativa para o seu usu�rio.");
				return cancelar();
			}finally{
				if(dao != null) dao.close();
				if(areaDao != null) areaDao.close();
			}
		
		}
		
		return "";
	}
	
	
	
	
	/**
	 * M�todo que responde �s requisi��es de autocomplete com o componente rich:suggestionBox e 
	 * realiza a busca de autores ou assuntos desejados pelo usu�rio.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * 	<ul>
	 * 		<li>/sigaa.war/biblioteca/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
	 * 	</ul>
	 * </p>
	 *
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<CacheEntidadesMarc> buscaAutoridadeAssuntoAutocomplete(Object event) throws DAOException {
		
		
		String descricao = event.toString();        //Texto digitado pelo usu�rio no auto complete

		DisseminacaoDaInformacaoDao dao = null;
		
		autoridadesAssuntosTemp = new ArrayList<CacheEntidadesMarc>();
		try{
			dao = getDAO(DisseminacaoDaInformacaoDao.class);
			
			autoridadesAssuntosTemp = dao.findAssuntosAutorizadosByDescricao(descricao);
			
		}finally{
			if(dao != null) dao.close();
		}
		return autoridadesAssuntosTemp;
	}
	
	
	/**
	 * M�todo que responde �s requisi��es de autocomplete com o componente rich:suggestionBox e 
	 * realiza a busca de autores ou assuntos desejados pelo usu�rio.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * 	<ul>
	 * 		<li>/sigaa.war/biblioteca/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
	 * 	</ul>
	 * </p>
	 *
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<CacheEntidadesMarc> buscaAutoridadeAutorAutocomplete(Object event) throws DAOException {
		
		
		String descricao = event.toString();        //Texto digitado pelo usu�rio no auto complete

		DisseminacaoDaInformacaoDao dao = null;
		
		
		autoridadesAutoresTemp = new ArrayList<CacheEntidadesMarc>();
		try{
			dao = getDAO(DisseminacaoDaInformacaoDao.class);
			
			autoridadesAutoresTemp = dao.findAutoresAutorizadosByDescricao(descricao);
			
		}finally{
			if(dao != null) dao.close();
		}
		return autoridadesAutoresTemp;
	}
	
	
	
	
	
	/**
	 * AJAX - Adiciona uma nova pessoa do filtro de v�rias pessoas.
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 * </p>
	 */
	public void adicionarAutoridadeAssuntoSelecionada(ActionEvent evt) {
		
		System.out.println(idAutoridadeAssuntoBuscada);
		
		if(idAutoridadeAssuntoBuscada <= 0)
			addMensagemErroAjax("Digite um assunto de seu interesse. ");
		else{	
		
			if (! assuntosAutorizados.contains(new CacheEntidadesMarc(idAutoridadeAssuntoBuscada))) {
	
				if(assuntosAutorizados.size() >= QUANTIDADE_MAXIMA_AUTORIDADES_INTERESSE){
					addMensagemErroAjax("A quantidade m�xima de assuntos permitida para cadastrar interesse � "+QUANTIDADE_MAXIMA_AUTORIDADES_INTERESSE);
					return;
				}
					
				assuntosAutorizados.add(autoridadesAssuntosTemp.get(  autoridadesAssuntosTemp.indexOf(new CacheEntidadesMarc(idAutoridadeAssuntoBuscada))  ));
				descricaoAutoridadeAssuntoBuscada = null;
				idAutoridadeAssuntoBuscada = 0;
				addMensagemInfoAjax("Assunto adicionado com sucesso! ");
			}else{
				addMensagemErroAjax("O assunto j� est� entre os assuntos de seu interesse! ");
			}
		}
		
		
	
	}
	
	
	/**
	 * AJAX - Adiciona uma nova pessoa do filtro de v�rias pessoas.
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 * </p>
	 */
	public void adicionarAutoridadeAutorSelecionada(ActionEvent evt) {
		
		if(idAutoridadeAutorBuscada == 0)
			addMensagemErroAjax("Digite um autor de seu interesse. ");
		else{
			if (! autoresAutorizados.contains(new CacheEntidadesMarc(idAutoridadeAutorBuscada))) {
	
				if(autoresAutorizados.size() >= QUANTIDADE_MAXIMA_AUTORIDADES_INTERESSE){
					addMensagemErroAjax("A quantidade m�xima de autores permitida para cadastrar interesse � "+QUANTIDADE_MAXIMA_AUTORIDADES_INTERESSE);
					return;
				}
					
				autoresAutorizados.add(autoridadesAutoresTemp.get(  autoridadesAutoresTemp.indexOf(new CacheEntidadesMarc(idAutoridadeAutorBuscada))  ));
				descricaoAutoridadeAutorBuscada = null;
				idAutoridadeAutorBuscada = 0;
				addMensagemInfoAjax("Autor adicionado com sucesso! ");
			}else{
				addMensagemErroAjax("O autor j� est� entre os assuntos de seu interesse! ");
			}
		}
		
		
	}
	
	
	
	/**
	 * AJAX - Remove um assunto ou autor adicionado pelo usu�rio.
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 */
	public void removerAutoridadeAssuntoSelecionada(ActionEvent evt) {
		assuntosAutorizados.remove(autoridadesAssuntoDataModel.getRowIndex());
	
	}
	
	/**
	 * AJAX - Remove um assunto ou autor adicionado pelo usu�rio.
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 */
	public void removerAutoridadeAutorSelecionada(ActionEvent evt) {
		autoresAutorizados.remove(autoridadesAutorDataModel.getRowIndex());
	}
	
	
	
	/**
	 * <p>Configura na tela as informa��es j� salvas do perfil do usu�rio que foram recuperadas do banco.</p>
	 * @throws DAOException 
	 *
	 */
	private void configuraInformacoesSalvasPerfilUsuario(DisseminacaoDaInformacaoDao dao) throws DAOException {
		
		if(perfilInteresse == null)
			return;
		
		if(perfilInteresse.getBibliotecasDeInteresse() != null){
			bibliotecasSelecionadas = new ArrayList<String>();
			//tipoAbrangecia = AbrangenciaDisseminacao.BIBLIOTECAS_ESPECIFICA;
			escolheuTodasAsBibliotecas = false;
			for (Biblioteca  biblioteca : perfilInteresse.getBibliotecasDeInteresse()) {
				bibliotecasSelecionadas.add(String.valueOf(biblioteca.getId() ) );
			}
		}
		
		if(perfilInteresse.getAssuntosDeInteresse() != null){
			assuntosAutorizados.addAll(dao.polulaInformacoesAutoridadeAssunto( new ArrayList<Autoridade>(perfilInteresse.getAssuntosDeInteresse()) ) );
		}
		
		
		if(perfilInteresse.getAutoresDeInteresse() != null){
			autoresAutorizados.addAll(dao.polulaInformacoesAutoridadeAutor( new ArrayList<Autoridade>(perfilInteresse.getAutoresDeInteresse()) ) );
		}
		
		
	}



	
	
	
//	/**
//	 * 
//	 * Atualiza os resultados quando o usu�rio altera entre "assuntos" e "autores".
//	 * 
//	 * <p>
//	 *    <strong>I M P O R T A N T E:</strong> 
//	 *    Atualiza todos os resultados (quantiade de p�ginas, quantidade total de resultados, p�gians percorridas) 
//	 *    sempre que o filtro da letra � alterado.
//	 * </p> 
//	 *  
//	 *  <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *      <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *    
//	 *   </ul>
//	 *
//	 *
//	 * @return
//	 * @throws DAOException 
//	 * @throws ArqException 
//	 */
//	public void alterouTipoAutoridade(ValueChangeEvent evt) throws DAOException{
//	
//		DisseminacaoDaInformacaoDao dao = null;
//		
//		try{
//			dao = getDAO(DisseminacaoDaInformacaoDao.class);
//		
//			tipoAutoridadeEscolhida  = (Integer) evt.getNewValue();
//			
//			if(isTipoAutoridadeAssunto()){
//				autoridadesDataModel = new ListDataModel(assuntosAutorizados);
//			}	
//				
//			if(isTipoAutoridadeAutor()){
//				autoridadesDataModel = new ListDataModel(autoresAutorizados);
//			}
//				
//			
//			
//			
//			
//			
//			paginasPercorridas = new HashSet<Integer>(); // quando altera a letra, muda a quantidade de p�ginas, ent�o busca todas novamente
//			quantidadeTotalResultados = countTotalResultadosEspecificosLetra(dao);
//			quantidadePaginas = calculaQuantidadePaginas();
//			paginaAtual =1;
//			
//			//zeraResultadosBusca();
//			
//			//quantidadeTotalResultados =  countTotalResultadosEspecificosLetra(dao);
//			//iniciaColecaoDeResuldados();
//			
//			// Se alterou a primeira vez para autoridades de autores, precisa iniciar a cole��o //
//			if(tipoAutoridadeEscolhida == Autoridade.TIPO_AUTOR && autoresAutorizados.size() == 0){
//				for (int index = 0; index < quantidadeTotalResultados; index++) {
//					autoresAutorizados.add(new CacheEntidadesMarc());
//				}
//			}
//			
//			//buscaProximosAssuntosAutorizados(null);
//			
//		}finally{
//			if (dao != null) dao.close();
//		}
//		
//	}
	

	
	

	
	
	
	
	/**
	 * 
	 * Realiza a a��o de atualizar o perfil do usu�rio.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
	 *    
	 *   </ul>
	 *
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String atualizarPerfil() throws ArqException{
		
		perfilInteresse.setBibliotecasDeInteresse(null);
		perfilInteresse.setAssuntosDeInteresse(null);
		perfilInteresse.setAutoresDeInteresse(null);
		
		if(! escolheuTodasAsBibliotecas ){
			
			if(bibliotecasSelecionadas == null || bibliotecasSelecionadas.size() == 0){
				addMensagemErro("Selecione uma op��o para as bibliotecas de seu interesse: Todas ou uma biblioteca espec�fica.");
				return null;
			}
			
			for (String idBibliotecaSelecionada : bibliotecasSelecionadas) {
				perfilInteresse.adicionaBibliotecaInteresse(new Biblioteca( new Integer( idBibliotecaSelecionada )));
			}
		}
		
			
		// Para assuntos	
		for (CacheEntidadesMarc cacheAutoridade : assuntosAutorizados) {
			
			if(cacheAutoridade.getIdAutoridade() != null) { // Se existe nessa possi��o ou foi adicionado apenas para completar o array
				Autoridade a = new Autoridade(cacheAutoridade.getIdAutoridade());
				a.setNumeroDoSistema(cacheAutoridade.getNumeroDoSistema());
				perfilInteresse.adicionaAssuntosDeInteresse(a);
			}
		}

		
		
		// Para autores		
		
		for (CacheEntidadesMarc cacheAutoridade : autoresAutorizados) {
			
			if(cacheAutoridade.getIdAutoridade() != null) { // Se existe nessa possi��o ou foi adicionado apenas para completar o array
				Autoridade a = new Autoridade(cacheAutoridade.getIdAutoridade());
				a.setNumeroDoSistema(cacheAutoridade.getNumeroDoSistema());
				perfilInteresse.adicionaAutoresDeInteresse(a);
			}
		}
		

		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CADASTRA_ATUALIZA_PERFIL_INTERESSE_USUARIO_BIBLIOTECA);
		mov.setObjMovimentado(perfilInteresse);
		
		try {
			execute(mov);
			addMensagemInformation("Perfil de interesse atualizado com sucesso !");
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} 
		
		return cancelar(); // Voltar para o portal que chamou esse caso de uso (discente, docente, servidor, etc...)
		
		
		
		
		
//		if(perfilInteresse.getAssuntosDeInteresse() != null){
//		Iterator<Autoridade> iterator = perfilInteresse.getAssuntosDeInteresse().iterator();
//		while (iterator.hasNext()) {
//			
//			Autoridade a  = iterator.next();
//			
//			CacheEntidadesMarc assuntoCorrespondente = getCacheAutoridadeCorrespondente(a);
//			
//			if(assuntoCorrespondente != null && ! assuntoCorrespondente.isSelecionada() ){
//				iterator.remove();
//			}
//		}
//	}
	/////////
		
		
		/* **************************************************************************************
		 *  Aqui tem que verificar a listagem de assuntos e autores que o usu�rio visualizou na p�gina
		 *  
		 *  Se ela cont�m a autoridade do perfil do usu�rio e est� deselecionada, ent�o remove do perfil do usu�rio.
		 *  
		 *  Se tem alguma autoridade seleciona que n�o est� no perfil do usu�rio, adiciona ao perfil.
		 *  
		 * **************************************************************************************/
		
//		if(perfilInteresse.getAutoresDeInteresse() != null){
//		
//		Iterator<Autoridade> iterator = perfilInteresse.getAutoresDeInteresse().iterator();
//		
//		while (iterator.hasNext()) {
//			
//			Autoridade a  = iterator.next();
//			
//			CacheEntidadesMarc autorCorrespondente = getCacheAutoridadeCorrespondente(a);
//			
//			if(autorCorrespondente != null && ! autorCorrespondente.isSelecionada() ){
//				iterator.remove();
//			}
//		}
//	}
	/////////
		
		
	}


	/**
	 * 
	 * <p> Retorna os tipos de abrag�ncia para o usu�rio escolher.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
	 *   </ul>
	 * @throws DAOException 
	 *
	 *
	 */
	public Collection <SelectItem> getAbrangenciasDisseminacao() throws DAOException{
		
		List<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(AbrangenciaDisseminacao.TODAS_BIBLIOTECAS.ordinal(), AbrangenciaDisseminacao.TODAS_BIBLIOTECAS.getDescricao()));
		itens.add(new SelectItem(AbrangenciaDisseminacao.BIBLIOTECAS_ESPECIFICA.ordinal(), AbrangenciaDisseminacao.BIBLIOTECAS_ESPECIFICA.getDescricao()));	
		return itens;
	}
	
	
	
//	/**
//	 * <p> Retorna os tipos de autoridade para o usu�rio escolher qual vai querer marcar interesse, ele vai poder ter interesse nos 
//	 * 2 tipos ao mesmo tempo, apesar de s� poder marcar 1 tipo por vez.</p>
//	 *  
//	 *  <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *   </ul>
//	 * @throws DAOException 
//	 *
//	 *
//	 */
//	public Collection <SelectItem> getTipoAutoridadeComboBox() throws DAOException{
//		
//		List<SelectItem> itens = new ArrayList<SelectItem>();
//		itens.add(new SelectItem(Autoridade.TIPO_ASSUNTO, "Assuntos"));
//		itens.add(new SelectItem(Autoridade.TIPO_AUTOR, "Autores"));	
//		return itens;
//	}
	
	
	
	/**
	 * Retorna todas as biblioteca ativas do sistema para o usu�rio escolher de qual deseja receber informa��o 
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
	 *   </ul>
	 * @throws SegurancaException 
	 */
	public Collection <SelectItem> getAllBibliotecasInternasAtivasSelectItem() throws DAOException, SegurancaException{
		
		BibliotecaDao dao = null;
		
		try{
			if(bibliotecaInternasAtivas == null || bibliotecaInternasAtivas.size() == 0){
				dao = getDAO(BibliotecaDao.class);
				bibliotecaInternasAtivas = dao.findAllBibliotecasInternasAtivas();
			}
			
			Collection<SelectItem> temp = new ArrayList<SelectItem>();
			temp.addAll(toSelectItems(bibliotecaInternasAtivas, "id", "descricaoCompleta"));
			return temp;
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	/**
	 * Retorna a lista da areas CNPq para o combobox
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
	 *   </ul>
	 * @throws SegurancaException 
	 */
	public Collection<SelectItem> getAreasCNPQCombo() {
		return toSelectItems(areasCNPQ, "id", "nome");
	}
	
	
	//////////////////////////////////////////
	

//	/**
//	 * Retorna a descri��o da informa��es que se est� trabalhando no momento "Assuntos" ou "Autores"
//	 * 
//	 * <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *   </ul>
//	 * @throws SegurancaException 
//	 */
//	public String getDescricaoTipoAutoridadeEscolhida(){
//		if(isTipoAutoridadeAssunto())
//			return "Assuntos";
//		if(isTipoAutoridadeAutor())
//			return "Autores";
//		return "";
//	}
	
//	/**
//	 * Diz se  o tipo de autoridades que se est� trabalhando no momento � assunto
//	 * 
//	 * <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *   </ul>
//	 * @throws SegurancaException 
//	 */
//	public boolean isTipoAutoridadeAssunto(){
//		return tipoAutoridadeEscolhida == Autoridade.TIPO_ASSUNTO;
//	}
//	
//	
//	/**
//	 * Diz se o tipo de autoridades que se est� trabalhando no momento � autor
//	 * 
//	 * <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *   </ul>
//	 * @throws SegurancaException 
//	 */
//	public boolean isTipoAutoridadeAutor(){
//		return tipoAutoridadeEscolhida == Autoridade.TIPO_AUTOR;
//	}
	
	
	
	/////////////////////////////////////////////
	
	/**
	 * Redireciona para a p�gina.
	 *   
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaListaMeuPerfilDeInteresse(){
		return forward(PAGINA_LISTA_MEU_PERFIL_DE_INTERESSE);
	}

	
	/////////  Sets  e Gets  /////////
	

	
	
//	public int getTipoAbrangecia() {
//		return tipoAbrangecia.ordinal();
//	}
//
//	public void setTipoAbrangecia(int tipoAbrangecia) {
//		this.tipoAbrangecia = AbrangenciaDisseminacao.getAbrangencia(tipoAbrangecia);
//	}
//	
//	public boolean isTipoAbragenciaBibliotecaEspecifica(){
//		return this.tipoAbrangecia == AbrangenciaDisseminacao.BIBLIOTECAS_ESPECIFICA;
//	}

	public List<String> getBibliotecasSelecionadas() {
		return bibliotecasSelecionadas;
	}

	public boolean isEscolheuTodasAsBibliotecas() {
		return escolheuTodasAsBibliotecas;
	}



	public void setEscolheuTodasAsBibliotecas(boolean escolheuTodasAsBibliotecas) {
		this.escolheuTodasAsBibliotecas = escolheuTodasAsBibliotecas;
	}



	public void setBibliotecasSelecionadas(List<String> bibliotecasSelecionadas) {
		this.bibliotecasSelecionadas = bibliotecasSelecionadas;
	}

	public List<CacheEntidadesMarc> getAssuntosAutorizados() {
		return assuntosAutorizados;
	}

	public void setAssuntosAutorizados(List<CacheEntidadesMarc> assuntosAutorizados) {
		this.assuntosAutorizados = assuntosAutorizados;
	}

	public List<CacheEntidadesMarc> getAutoresAutorizados() {
		return autoresAutorizados;
	}

	public void setAutoresAutorizados(List<CacheEntidadesMarc> autoresAutorizados) {
		this.autoresAutorizados = autoresAutorizados;
	}

	
	public UsuarioBiblioteca getUbAtualUsuario() {
		return ubAtualUsuario;
	}

	public void setUbAtualUsuario(UsuarioBiblioteca ubAtualUsuario) {
		this.ubAtualUsuario = ubAtualUsuario;
	}

	public PerfilInteresseUsuarioBiblioteca getPerfilInteresse() {
		return perfilInteresse;
	}

	public void setPerfilInteresse(PerfilInteresseUsuarioBiblioteca perfilInteresse) {
		this.perfilInteresse = perfilInteresse;
	}

	
	public int getTipoAutoridadeAssunto() {
		return Autoridade.TIPO_ASSUNTO;
	}
	
	public int getTipoAutoridadeAutor() {
		return Autoridade.TIPO_AUTOR;
	}



	public int getIdAutoridadeAssuntoBuscada() {
		return idAutoridadeAssuntoBuscada;
	}



	public void setIdAutoridadeAssuntoBuscada(int idAutoridadeAssuntoBuscada) {
		this.idAutoridadeAssuntoBuscada = idAutoridadeAssuntoBuscada;
	}



	public int getIdAutoridadeAutorBuscada() {
		return idAutoridadeAutorBuscada;
	}



	public void setIdAutoridadeAutorBuscada(int idAutoridadeAutorBuscada) {
		this.idAutoridadeAutorBuscada = idAutoridadeAutorBuscada;
	}



	public String getDescricaoAutoridadeAssuntoBuscada() {
		return descricaoAutoridadeAssuntoBuscada;
	}



	public void setDescricaoAutoridadeAssuntoBuscada(
			String descricaoAutoridadeAssuntoBuscada) {
		this.descricaoAutoridadeAssuntoBuscada = descricaoAutoridadeAssuntoBuscada;
	}



	public String getDescricaoAutoridadeAutorBuscada() {
		return descricaoAutoridadeAutorBuscada;
	}



	public void setDescricaoAutoridadeAutorBuscada(
			String descricaoAutoridadeAutorBuscada) {
		this.descricaoAutoridadeAutorBuscada = descricaoAutoridadeAutorBuscada;
	}



	public List<CacheEntidadesMarc> getAutoridadesAssuntosTemp() {
		return autoridadesAssuntosTemp;
	}



	public void setAutoridadesAssuntosTemp(
			List<CacheEntidadesMarc> autoridadesAssuntosTemp) {
		this.autoridadesAssuntosTemp = autoridadesAssuntosTemp;
	}



	public List<CacheEntidadesMarc> getAutoridadesAutoresTemp() {
		return autoridadesAutoresTemp;
	}



	public void setAutoridadesAutoresTemp(
			List<CacheEntidadesMarc> autoridadesAutoresTemp) {
		this.autoridadesAutoresTemp = autoridadesAutoresTemp;
	}



	public DataModel getAutoridadesAssuntoDataModel() {
		return autoridadesAssuntoDataModel;
	}



	public void setAutoridadesAssuntoDataModel(DataModel autoridadesAssuntoDataModel) {
		this.autoridadesAssuntoDataModel = autoridadesAssuntoDataModel;
	}



	public DataModel getAutoridadesAutorDataModel() {
		return autoridadesAutorDataModel;
	}



	public void setAutoridadesAutorDataModel(DataModel autoridadesAutorDataModel) {
		this.autoridadesAutorDataModel = autoridadesAutorDataModel;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/// C�DIGO ANTIGO ONDE ERA LISTADO TODOS OS RESULTADOS P�GINADOS POR DEMANDA, GUARDANDO PARA SE PRECISAR UTILIZAR N�O TER QUE DESENVOLVER NOVAMENTE !! ///
	
	
	
	
//	/**
//	 * Retorna o cache da autoridade passada se ele tiver entre os dados percorridos pelo usu�rio.
//	 *
//	 * @param a
//	 * @return
//	 */
//	private CacheEntidadesMarc getCacheAutoridadeCorrespondente(Autoridade a) {
//		
//		if(tipoAutoridadeEscolhida == Autoridade.TIPO_ASSUNTO){
//			
//			if(assuntosAutorizados != null)
//			for (CacheEntidadesMarc assunto : assuntosAutorizados) {
//				if(assunto.getIdAutoridade() != null && assunto.getIdAutoridade().equals( a.getId()) )
//					return assunto;
//			}
//			
//		}
//		
//		if(tipoAutoridadeEscolhida == Autoridade.TIPO_AUTOR){
//			if(autoresAutorizados != null)
//			for (CacheEntidadesMarc autor : autoresAutorizados) {
//				if(autor.getIdAutoridade() != null && autor.getIdAutoridade().equals( a.getId()) )
//					return autor;
//			}
//		}
//		
//		return null;
//	}
	
	
//	/** Retorna a posi��o da primeira autoridade dentro do total para ser adicionado � listagem geral 
//	 * e � tamb�m a posi��o a partir da qual os resultados v�o ser mostrados na p�gina para os usu�rios.
//	 * 
//	 *    <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *      <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *    
//	 *   </ul> 
//	 */
//	public int getRetornaPosicaoAutoridadeDentroDoTotalAutoridades() throws DAOException{
//		
//		int primeiro = (paginaAtual-1)*QTD_ASSUNTOS_POR_PAGINA;
//		
//		if(letraSelecionada != null && ! letraSelecionada.equals('?')){
//			
//			DisseminacaoDaInformacaoDao dao = null;
//			
//			try{
//				dao = getDAO(DisseminacaoDaInformacaoDao.class);
//				
//				if(tipoAutoridadeEscolhida == Autoridade.TIPO_ASSUNTO)
//					primeiro += dao.findPosicaoAssuntoDentroDoTotalAssuntos(letraSelecionada); // retorna a posi��o do primeiro assunto que come�a com a letra selecionada dentro do total de assuntos
//				if(tipoAutoridadeEscolhida == Autoridade.TIPO_AUTOR)
//					primeiro += dao.findPosicaoAutorDentroDoTotalAutores(letraSelecionada); // retorna a posi��o do primeiro autor que come�a com a letra selecionada dentro do total de autores
//			}finally{
//				if(dao != null) dao.close();
//			}
//		}
//		
//		return primeiro;
//	}
	
	
//	/**
//	 * <p>Calcula a quantidade de assuntos a serem mostrados por p�gina.</p>
//	 * 
//	 * <ul>
//	 *    <li>Se a quantidade total n�o ultrapassou o limite final da p�gina: retorna a quantidade m�xima </li>
//	 *    <li>Se a quantidade total � menor que a quantidade de 1 p�gina: retorna a quantidade de resultados existentes. </li>
//	 *    <li>Se a quantidade total ultrapassou o limite da p�gina: retorna esse limite menos a quantidade total, � o que falta mostrar, por exemplo 
//	 *      100 resultados = 3 p�ginas x 40 resultados =  40 * 3 -100  = 20 resultados mostrados na �ltima p�gina.</li>
//	 *   </ul>
//	 *  
//	 *  <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *   </ul>
//	 *
//	 * @return
//	 */
//	public int getQuantidadeResuldosPorPagina() {
//		if(quantidadeTotalResultados > QTD_ASSUNTOS_POR_PAGINA*paginaAtual)
//			return QTD_ASSUNTOS_POR_PAGINA;
//		else
//			if(quantidadeTotalResultados < QTD_ASSUNTOS_POR_PAGINA)
//				return quantidadeTotalResultados;
//			else
//				return (QTD_ASSUNTOS_POR_PAGINA*paginaAtual) - quantidadeTotalResultados ;
//	}
	
	
	
	
//	/**
//	 * Calcula a quantidade de p�ginas necess�rias para mostras todos os resultados.
//	 *
//	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
//	 *
//	 * @return
//	 */
//	private int calculaQuantidadePaginas(){
//		return (quantidadeTotalResultados / QTD_ASSUNTOS_POR_PAGINA) + (quantidadeTotalResultados % QTD_ASSUNTOS_POR_PAGINA == 0  ?  0 : 1 );
//	}
	
	
	
//	/** 
//	 * Retorna a lista de p�gina que o usu�rio pode percorrer na consulta 
//	 *  
//	 *  <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *      <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *    
//	 *   </ul>
//	 * 
//	 */
//	public final List<Integer> getListaPaginasVisiveis(){
//		return PaginacaoBibliotecaUtil.getListaPaginasVisiveis(paginaAtual, quantidadePaginas);
//	}
	
	
//	/** 
//	 * <p>Retorna a lista de letras para as quais o usu�rio pode filtrar os assuntos de seu interesse 
//	 * para diminuir a quantidade mostrada e facilitar encontrar um assunto.<p>
//	 *  
//	 *  <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *      <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *    
//	 *   </ul>
//	 * 
//	 */
//	public final List<Character> getListaLetrasFiltrarAssunto(){
//
//		List<Character> letras = new ArrayList<Character>();
//		
//		
//		letras.add(new Character('?'));
//		letras.add(new Character('A'));
//		letras.add(new Character('B'));
//		letras.add(new Character('C'));
//		letras.add(new Character('D'));
//		letras.add(new Character('E'));
//		letras.add(new Character('F'));
//		letras.add(new Character('G'));
//		letras.add(new Character('H'));
//		letras.add(new Character('I'));
//		letras.add(new Character('J'));
//		letras.add(new Character('K'));
//		letras.add(new Character('L'));
//		letras.add(new Character('M'));
//		letras.add(new Character('N'));
//		letras.add(new Character('O'));
//		letras.add(new Character('P'));
//		letras.add(new Character('Q'));
//		letras.add(new Character('R'));
//		letras.add(new Character('S'));
//		letras.add(new Character('T'));
//		letras.add(new Character('U'));
//		letras.add(new Character('V'));
//		letras.add(new Character('W'));
//		letras.add(new Character('X'));
//		letras.add(new Character('Y'));
//		letras.add(new Character('Z'));
//		
//		return letras;
//	}
	
	
//	/** Conta a quantidade total de resultados come�ando com a letra selecionada dependendo do tipo de autoridade escolhida */
//	private int countTotalResultadosEspecificosLetra(DisseminacaoDaInformacaoDao dao) throws DAOException{
//		if(tipoAutoridadeEscolhida == Autoridade.TIPO_ASSUNTO)
//			return dao.countAllAssuntosAutorizados(letraSelecionada);
//		if(tipoAutoridadeEscolhida == Autoridade.TIPO_AUTOR)
//			return dao.countAllAutoresAutorizados(letraSelecionada);
//		return 0;
//	}
	
	
	
	
//	///////////// Para realizar a pagina��o  /////////////////
//	
//	/** A p�gina atual da pagin��o dos resultados de assuntos mostrados aos usu�rios */
//	public int paginaAtual = 1;
//	
//	/** A quantidade total de p�ginas da p�gina��o */
//	protected int quantidadePaginas = 1;
//	
//	/** A quantidade m�xima de materiais mostrados em uma p�gina */
//	public static  final Integer QTD_ASSUNTOS_POR_PAGINA = 40;
//	
//	/** A quantidade total de resultados que ser�o p�ginados */
//	public int quantidadeTotalResultados = 0;
//	
//	/** Guarda as p�gina j� percorridas pelo usu�rio para n�o precisar ficar buscando os resultados toda vez que o usu�rio passar pela p�gina. */
//	public Set<Integer> paginasPercorridas = new HashSet<Integer>();
//	
//	/** Guarda a posi��o relativa inicial do assunto dentro da listagem geral de assuntos. Utilizado quando s�o recuperados apenas assuntos come�ando com uma determinada letra */
//	//public Integer posicaoRelativaInicialAssunto;
//	
//	
//	/** Utilizado Para buscar assuntos que come�am com uma letra espec�fica*/
//	private Character letraSelecionada;
//	
//	////////////////////////////////////////////////////////////
	
	
	
//	public int getPaginaAtual() {
//		return paginaAtual;
//	}
//
//	public void setPaginaAtual(int paginaAtual) {
//		this.paginaAtual = paginaAtual;
//	}
//
//	public int getQuantidadePaginas() {
//		return quantidadePaginas;
//	}
//	
//	public void setQuantidadePaginas(int quantidadePaginas) {
//		this.quantidadePaginas = quantidadePaginas;
//	}
//
//	public int getQuantidadeTotalResultados() {
//		return quantidadeTotalResultados;
//	}
//
//	public void setQuantidadeTotalResultados(int quantidadeTotalResultados) {
//		this.quantidadeTotalResultados = quantidadeTotalResultados;
//	}
//
//	public Character getLetraSelecionada() {
//		return letraSelecionada;
//	}
//
//	public void setLetraSelecionada(Character letraSelecionada) {
//		this.letraSelecionada = letraSelecionada;
//	}

	
	
	//
//	/**
//	 * Zera os resultados da busca;
//	 *
//	 */
//	private void zeraResultadosBusca() {
//		paginaAtual = 1;
//		quantidadePaginas = 1;
//		quantidadeTotalResultados = 0;
//		paginasPercorridas = new HashSet<Integer>();
//		assuntosAutorizados = new ArrayList<CacheEntidadesMarc>();
//		autoresAutorizados = new ArrayList<CacheEntidadesMarc>();
//	}



//	/**
//	 * <p>Buscas os pr�ximos assuntos autorizados de acordo com o p�gina atual que o usu�rio se encontra.</p>
//	 *  
//	 * <p>A busca � paginada, por�m deve manter todos os resultados j� buscados em mem�ria, para n�o apagar
//	 * as sele��es feitas pelo usu�rio.</p> 
//	 *  
//	 *  <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *   </ul>
//	 *
//	 * @param event
//	 * @throws DAOException 
//	 */
//	public void buscaProximosAssuntosAutorizados(ActionEvent event) throws DAOException{
//	
//		///// O parametro � passado quando o usu�rio seleciona a p�gina atual que deseja ir /////
//		Integer numeroPaginaAtual = getParameterInt("_numero_pagina_atual");
//		
//		if(numeroPaginaAtual == null)
//			paginaAtual = 1;
//		else
//			paginaAtual = numeroPaginaAtual;
//		
//		///// Verifica se n�o estorou os limiter de resultados /////
//		if(paginaAtual > quantidadePaginas)
//			paginaAtual = quantidadePaginas;
//		
//		if(paginaAtual <= 0)
//			paginaAtual = 1;
//		
//		DisseminacaoDaInformacaoDao dao = null;
//		
//		try{
//			
//			dao = getDAO(DisseminacaoDaInformacaoDao.class);
//			
//			///// Se o usu�rio est� percorrendo uma p�gina j� buscada no banco n�o precisa busca novamente /////
//			if(! paginasPercorridas.contains(paginaAtual)){
//				paginasPercorridas.add(paginaAtual);
//				
//				/////   Lista dos novos assuntos buscados no banco /////
//				List<CacheEntidadesMarc> dadosTemp = new ArrayList<CacheEntidadesMarc>();
//				
//				if(tipoAutoridadeEscolhida == Autoridade.TIPO_ASSUNTO)
//					dadosTemp = dao.findAllAssuntosAutorizados(letraSelecionada, paginaAtual, QTD_ASSUNTOS_POR_PAGINA);
//				
//				if(tipoAutoridadeEscolhida == Autoridade.TIPO_AUTOR)
//					dadosTemp = dao.findAllAutoresAutorizados(letraSelecionada, paginaAtual, QTD_ASSUNTOS_POR_PAGINA);
//				
//				
//				int indexTemp = 0; // o indice do resultado tempor�rio buscado no banco
//				
//				/* ********************************************************************************************
//				 *  Percorre os dados do primeiro resultado at� o �ltimo da p�gina e seta os novos resultados *
//				 *  carregados nas possi��es corretas                                                         *
//				 * ********************************************************************************************/
//				int indexPrimeiroResultado = getRetornaPosicaoAutoridadeDentroDoTotalAutoridades();
//				int indexUltimoResultado =  indexPrimeiroResultado + dadosTemp.size();
//				
//				for (; indexPrimeiroResultado <= indexUltimoResultado && indexTemp < dadosTemp.size() ; indexPrimeiroResultado++) {
//					
//					if(tipoAutoridadeEscolhida == Autoridade.TIPO_ASSUNTO){
//						if(assuntosAutorizados.get(indexPrimeiroResultado).getId() == 0) // se n�o foi adicionado nada na posi��o ainda
//							assuntosAutorizados.set(indexPrimeiroResultado, dadosTemp.get(indexTemp));
//					}
//					
//					if(tipoAutoridadeEscolhida == Autoridade.TIPO_AUTOR){
//						if(autoresAutorizados.get(indexPrimeiroResultado).getId() == 0) // se n�o foi adicionado nada na posi��o ainda
//							autoresAutorizados.set(indexPrimeiroResultado, dadosTemp.get(indexTemp));
//					}
//					
//					indexTemp++;
//				}
//				
//				// Sempre ao terminar de montar os dados retornados do banco, verifica o que o usu�rio marcou no perfil dele //
//				configuraInformacoesSalvasPerfilUsuario(dao);
//			}
//			
//		}finally{
//			if(dao != null) dao.close();
//		}
//		
//	}
	
	
//	/**
//	 * <p>M�todo chamado sempre que o usu�rio escolhe buscar assuntos ou autores que come�em por uma letra espec�fica.</p>
//	 * <p>
//	 *    <strong>I M P O R T A N T E:</strong> 
//	 *    Atualiza todos os resultados (quantiade de p�ginas, quantidade total de resultados, p�gians percorridas) 
//	 *    sempre que o filtro da letra � alterado.
//	 * </p>
//	 *  
//	 * <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *      <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *    
//	 *   </ul>
//	 *
//	 * @param evt
//	 * @throws ArqException 
//	 */
//	public void verificaAlteracaoFiltroLetra(ActionEvent evt) throws ArqException{
//		String letraStr = getParameter("_letra_selecionada");
//		if(letraStr != null)
//			letraSelecionada = letraStr.charAt(0);
//	
//		
//		
//		DisseminacaoDaInformacaoDao dao = null;
//
//		try{
//		
//			dao = getDAO(DisseminacaoDaInformacaoDao.class);
//			
//			paginasPercorridas = new HashSet<Integer>(); // quando altera a letra, muda a quantidade de p�ginas, ent�o busca todas novamente
//			quantidadeTotalResultados = countTotalResultadosEspecificosLetra(dao);
//			quantidadePaginas = calculaQuantidadePaginas();
//			paginaAtual =1;
//
//		}finally{
//			if(dao != null) dao.close();
//		}
//			
//		buscaProximosAssuntosAutorizados(null);
//		
//	}
}




