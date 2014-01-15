/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> MBean responsável por gerenciar a parte na qual os usuários da biblioteca poderão registrar seu interesse em algum tema cadastrado. 
 * Para receber avisos sobre esses temas quando um novo material é incluído no acervo.
 * </p>
 *
 *
 * <p> <i> Os temas referidos aqui são os assuntos e autores da base de autoridades do sistema.</i> </p>
 * 
 * @author jadson
 * @see Autoridade
 */
@Component(value="configuraPerfilInteresseUsuarioBibliotecaMBean")
@Scope(value="request")
public class ConfiguraPerfilInteresseUsuarioBibliotecaMBean extends SigaaAbstractController<PerfilInteresseUsuarioBiblioteca>{

	/** Página de lista os dados do perfil de interesse do usuário da biblioteca atualmente logado no sistema. */
	public static final String PAGINA_LISTA_MEU_PERFIL_DE_INTERESSE = "/biblioteca/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp";
	
	/** A quantidade máxima de autoridades que o usuário pode registrar interesse (30 assunto + 30 Autores) */
	public static final Integer QUANTIDADE_MAXIMA_AUTORIDADES_INTERESSE = 30;
	
	

	
	 /** Lista com todas as grandes áreas de conhecimento ativas do sistema.*/
	private List<AreaConhecimentoCnpq> areasCNPQ;
	
	
	/** O tipo de abrangência escolhido pelo usuário @deprecated Mudado a forma de visualizar as bibliotecas, mas não apague esse código. caso queira voltar a forma antiga. */
	//private AbrangenciaDisseminacao tipoAbrangecia = AbrangenciaDisseminacao.TODAS_BIBLIOTECAS;
	
	/** Se o usuário escolher receber o informativo de todas as bibliotecas. O padrão é sim. */
	private boolean escolheuTodasAsBibliotecas = true;
	
	/** Guarda a lista a lista de bibliotecas do sistema para não precisar realizar uma nova consulta a cada atualização da página.*/
	private List<Biblioteca> bibliotecaInternasAtivas;
	
	/** Vai ter um perfil por ub ativo no sistema.  */
	private UsuarioBiblioteca ubAtualUsuario; 
	
	/** O perfil de interesse do usuário atualmente logado. Que é o alvo desse caso de uso. */
	private PerfilInteresseUsuarioBiblioteca perfilInteresse; 
	
	
	
	/** Flag para imperdir que os dados do usuário sejam carregados sempre que a página é atualizada
	 *  Os dados salvos do perfil do usuário devem apenas ser carregados na primeira vez.
	 *  
	 *  É necessário colocar um carregamento diretamente na página, pois esse caso de uso pode ser 
	 *  redirecionado diretamente para a página apartir da áreas pública.*/
	private boolean carregandoAPagina = true;
	
	
	
	
	
	
	//////////////  Os dados marcados pelo usuário que vão fazer parte do seu perfil  /////////////////
	
	
	/** Guarda os ids da bibliotecas selecionadas pelo usuário, caso ele queira receber informações apenas de algumas bibliotecas */
	private List<String> bibliotecasSelecionadas = new ArrayList<String>();
	
	
	/** 
	 *  <p>Guarda a listagem de assuntos autorizados disponíveis no sistema para o usuário registrar interresse.</p>
	 *  
	 *  <p>O carregamento é por demanda, porém um vez carregado o assunto é mantido em memória para não apagar aqueles que o usuário por acaso já tenha marcado
	 *   na mudança entre páginas.
	 *  Por isso existe uma lógica mais completa no paginamento desses resultados.
	 *  </p>
	 */
	private List<CacheEntidadesMarc> assuntosAutorizados = new ArrayList<CacheEntidadesMarc>();
	
	
	/** 
	 *  <p>Guarda a listagem de autores autorizados disponíveis no sistema para o usuário registrar interresse.</p>
	 *  
	 *  <p>O carregamento é por demanda, porém um vez carregado o assunto é mantido em memória para não apagar aqueles que o usuário por acaso já tenha marcado
	 *   na mudança entre páginas.
	 *  Por isso existe uma lógica mais completa no paginamento desses resultados.
	 *  </p>
	 */
	private List<CacheEntidadesMarc> autoresAutorizados = new ArrayList<CacheEntidadesMarc>();
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////// A parte de busca autocomplete /////////////////////////////////////
	
	/** Guarda a informação do id da autoridade de assunto da busca pelo campo de auto complete. */
	private int idAutoridadeAssuntoBuscada;
	
	/** Guarda a informação do id da autoridade de autor da busca pelo campo de auto complete. */
	private int idAutoridadeAutorBuscada;
	
	/** Guarda a descrição da autoridade busca no campo de auto complete*/
	private String descricaoAutoridadeAssuntoBuscada;
	
	/** Guarda a descrição da autoridade busca no campo de auto complete*/
	private String descricaoAutoridadeAutorBuscada;
	
	/** Aguardas as autoridades buscasdas pelo usuário mas não adicionadas à lista */
	private List<CacheEntidadesMarc> autoridadesAssuntosTemp = new ArrayList<CacheEntidadesMarc>();
	
	/** Aguardas as autoridades buscasdas pelo usuário mas não adicionadas à lista */
	private List<CacheEntidadesMarc> autoridadesAutoresTemp = new ArrayList<CacheEntidadesMarc>();
	
	/** DataModel para preenchimento da tabela de pessoas do filtro de várias pessoas */
	private DataModel autoridadesAssuntoDataModel = new ListDataModel(assuntosAutorizados);
	
	/** DataModel para preenchimento da tabela de pessoas do filtro de várias pessoas */
	private DataModel autoridadesAutorDataModel = new ListDataModel(assuntosAutorizados);
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	/** Os tipos de abrangências na disseminação da informação, se de todas as biblioteca ou alguma específica */
	public enum AbrangenciaDisseminacao{
		/**Indica que o usuário selecionou todas as biblioteca para receber as notificaçõse */
		TODAS_BIBLIOTECAS("Todas"), 
		/**Indica que o usuário selecionou 1 biblioteca específica para receber as notificaçõse */
		BIBLIOTECAS_ESPECIFICA("Selecione...");
		
		
		private AbrangenciaDisseminacao(String descricao){
			this.descricao = descricao;
		}
		
		/** O valor mostrado ao usuário*/
		private String descricao;
		
		/**
		 * Retorna a tipo de abragência a partir do seu valor ordinal
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
	 * <p>Iniciar o caso de uso no qual o usuário vai poder ver as informações do seu prefil de interesse na biblioteca 
	 * e adicionar ou remover interesse em algum tema.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Carrega as informações do usuário atualmente logado. Método chamado ao carregar a página 
	 * porque a página pode ser acessada diretamente por meio de um redirecionamento da busca pública.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String getCarregarMeusPersilInteresse() throws ArqException{
		
		if(carregandoAPagina){
		
			carregandoAPagina = false; // só carrega na primeira vez que o usuário acessa a página.
			
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
				
				// Em caso de erro, redirecionar para a página principal do sistema, para pode mostrar a mensagem para o usuário, já que o caso de uso é chamada diretamente da página //
				
				addMensagemErro("É preciso primeiro ter uma conta na biblioteca para utilizar essa operação. Não foi encontrada nenhuma conta ativa para o seu usuário.");
				return cancelar();
			}finally{
				if(dao != null) dao.close();
				if(areaDao != null) areaDao.close();
			}
		
		}
		
		return "";
	}
	
	
	
	
	/**
	 * Método que responde às requisições de autocomplete com o componente rich:suggestionBox e 
	 * realiza a busca de autores ou assuntos desejados pelo usuário.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
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
		
		
		String descricao = event.toString();        //Texto digitado pelo usuário no auto complete

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
	 * Método que responde às requisições de autocomplete com o componente rich:suggestionBox e 
	 * realiza a busca de autores ou assuntos desejados pelo usuário.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
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
		
		
		String descricao = event.toString();        //Texto digitado pelo usuário no auto complete

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
	 * AJAX - Adiciona uma nova pessoa do filtro de várias pessoas.
	 * <p>Este método é chamado pelas seguintes JSPs:
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
					addMensagemErroAjax("A quantidade máxima de assuntos permitida para cadastrar interesse é "+QUANTIDADE_MAXIMA_AUTORIDADES_INTERESSE);
					return;
				}
					
				assuntosAutorizados.add(autoridadesAssuntosTemp.get(  autoridadesAssuntosTemp.indexOf(new CacheEntidadesMarc(idAutoridadeAssuntoBuscada))  ));
				descricaoAutoridadeAssuntoBuscada = null;
				idAutoridadeAssuntoBuscada = 0;
				addMensagemInfoAjax("Assunto adicionado com sucesso! ");
			}else{
				addMensagemErroAjax("O assunto já está entre os assuntos de seu interesse! ");
			}
		}
		
		
	
	}
	
	
	/**
	 * AJAX - Adiciona uma nova pessoa do filtro de várias pessoas.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 * </p>
	 */
	public void adicionarAutoridadeAutorSelecionada(ActionEvent evt) {
		
		if(idAutoridadeAutorBuscada == 0)
			addMensagemErroAjax("Digite um autor de seu interesse. ");
		else{
			if (! autoresAutorizados.contains(new CacheEntidadesMarc(idAutoridadeAutorBuscada))) {
	
				if(autoresAutorizados.size() >= QUANTIDADE_MAXIMA_AUTORIDADES_INTERESSE){
					addMensagemErroAjax("A quantidade máxima de autores permitida para cadastrar interesse é "+QUANTIDADE_MAXIMA_AUTORIDADES_INTERESSE);
					return;
				}
					
				autoresAutorizados.add(autoridadesAutoresTemp.get(  autoridadesAutoresTemp.indexOf(new CacheEntidadesMarc(idAutoridadeAutorBuscada))  ));
				descricaoAutoridadeAutorBuscada = null;
				idAutoridadeAutorBuscada = 0;
				addMensagemInfoAjax("Autor adicionado com sucesso! ");
			}else{
				addMensagemErroAjax("O autor já está entre os assuntos de seu interesse! ");
			}
		}
		
		
	}
	
	
	
	/**
	 * AJAX - Remove um assunto ou autor adicionado pelo usuário.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 */
	public void removerAutoridadeAssuntoSelecionada(ActionEvent evt) {
		assuntosAutorizados.remove(autoridadesAssuntoDataModel.getRowIndex());
	
	}
	
	/**
	 * AJAX - Remove um assunto ou autor adicionado pelo usuário.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 */
	public void removerAutoridadeAutorSelecionada(ActionEvent evt) {
		autoresAutorizados.remove(autoridadesAutorDataModel.getRowIndex());
	}
	
	
	
	/**
	 * <p>Configura na tela as informações já salvas do perfil do usuário que foram recuperadas do banco.</p>
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
//	 * Atualiza os resultados quando o usuário altera entre "assuntos" e "autores".
//	 * 
//	 * <p>
//	 *    <strong>I M P O R T A N T E:</strong> 
//	 *    Atualiza todos os resultados (quantiade de páginas, quantidade total de resultados, págians percorridas) 
//	 *    sempre que o filtro da letra é alterado.
//	 * </p> 
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//			paginasPercorridas = new HashSet<Integer>(); // quando altera a letra, muda a quantidade de páginas, então busca todas novamente
//			quantidadeTotalResultados = countTotalResultadosEspecificosLetra(dao);
//			quantidadePaginas = calculaQuantidadePaginas();
//			paginaAtual =1;
//			
//			//zeraResultadosBusca();
//			
//			//quantidadeTotalResultados =  countTotalResultadosEspecificosLetra(dao);
//			//iniciaColecaoDeResuldados();
//			
//			// Se alterou a primeira vez para autoridades de autores, precisa iniciar a coleção //
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
	 * Realiza a ação de atualizar o perfil do usuário.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
				addMensagemErro("Selecione uma opção para as bibliotecas de seu interesse: Todas ou uma biblioteca específica.");
				return null;
			}
			
			for (String idBibliotecaSelecionada : bibliotecasSelecionadas) {
				perfilInteresse.adicionaBibliotecaInteresse(new Biblioteca( new Integer( idBibliotecaSelecionada )));
			}
		}
		
			
		// Para assuntos	
		for (CacheEntidadesMarc cacheAutoridade : assuntosAutorizados) {
			
			if(cacheAutoridade.getIdAutoridade() != null) { // Se existe nessa possição ou foi adicionado apenas para completar o array
				Autoridade a = new Autoridade(cacheAutoridade.getIdAutoridade());
				a.setNumeroDoSistema(cacheAutoridade.getNumeroDoSistema());
				perfilInteresse.adicionaAssuntosDeInteresse(a);
			}
		}

		
		
		// Para autores		
		
		for (CacheEntidadesMarc cacheAutoridade : autoresAutorizados) {
			
			if(cacheAutoridade.getIdAutoridade() != null) { // Se existe nessa possição ou foi adicionado apenas para completar o array
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
		 *  Aqui tem que verificar a listagem de assuntos e autores que o usuário visualizou na página
		 *  
		 *  Se ela contém a autoridade do perfil do usuário e está deselecionada, então remove do perfil do usuário.
		 *  
		 *  Se tem alguma autoridade seleciona que não está no perfil do usuário, adiciona ao perfil.
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
	 * <p> Retorna os tipos de abragência para o usuário escolher.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * <p> Retorna os tipos de autoridade para o usuário escolher qual vai querer marcar interesse, ele vai poder ter interesse nos 
//	 * 2 tipos ao mesmo tempo, apesar de só poder marcar 1 tipo por vez.</p>
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna todas as biblioteca ativas do sistema para o usuário escolher de qual deseja receber informação 
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Retorna a descrição da informações que se está trabalhando no momento "Assuntos" ou "Autores"
//	 * 
//	 * <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Diz se  o tipo de autoridades que se está trabalhando no momento é assunto
//	 * 
//	 * <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Diz se o tipo de autoridades que se está trabalhando no momento é autor
//	 * 
//	 * <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Redireciona para a página.
	 *   
	 * <p>Método não chamado por nenhuma página jsp.</p>
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

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/// CÓDIGO ANTIGO ONDE ERA LISTADO TODOS OS RESULTADOS PÁGINADOS POR DEMANDA, GUARDANDO PARA SE PRECISAR UTILIZAR NÃO TER QUE DESENVOLVER NOVAMENTE !! ///
	
	
	
	
//	/**
//	 * Retorna o cache da autoridade passada se ele tiver entre os dados percorridos pelo usuário.
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
	
	
//	/** Retorna a posição da primeira autoridade dentro do total para ser adicionado à listagem geral 
//	 * e é também a posição a partir da qual os resultados vão ser mostrados na página para os usuários.
//	 * 
//	 *    <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//					primeiro += dao.findPosicaoAssuntoDentroDoTotalAssuntos(letraSelecionada); // retorna a posição do primeiro assunto que começa com a letra selecionada dentro do total de assuntos
//				if(tipoAutoridadeEscolhida == Autoridade.TIPO_AUTOR)
//					primeiro += dao.findPosicaoAutorDentroDoTotalAutores(letraSelecionada); // retorna a posição do primeiro autor que começa com a letra selecionada dentro do total de autores
//			}finally{
//				if(dao != null) dao.close();
//			}
//		}
//		
//		return primeiro;
//	}
	
	
//	/**
//	 * <p>Calcula a quantidade de assuntos a serem mostrados por página.</p>
//	 * 
//	 * <ul>
//	 *    <li>Se a quantidade total não ultrapassou o limite final da página: retorna a quantidade máxima </li>
//	 *    <li>Se a quantidade total é menor que a quantidade de 1 página: retorna a quantidade de resultados existentes. </li>
//	 *    <li>Se a quantidade total ultrapassou o limite da página: retorna esse limite menos a quantidade total, é o que falta mostrar, por exemplo 
//	 *      100 resultados = 3 páginas x 40 resultados =  40 * 3 -100  = 20 resultados mostrados na última página.</li>
//	 *   </ul>
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Calcula a quantidade de páginas necessárias para mostras todos os resultados.
//	 *
//	 *   <p>Método não chamado por nenhuma página jsp.</p>
//	 *
//	 * @return
//	 */
//	private int calculaQuantidadePaginas(){
//		return (quantidadeTotalResultados / QTD_ASSUNTOS_POR_PAGINA) + (quantidadeTotalResultados % QTD_ASSUNTOS_POR_PAGINA == 0  ?  0 : 1 );
//	}
	
	
	
//	/** 
//	 * Retorna a lista de página que o usuário pode percorrer na consulta 
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * <p>Retorna a lista de letras para as quais o usuário pode filtrar os assuntos de seu interesse 
//	 * para diminuir a quantidade mostrada e facilitar encontrar um assunto.<p>
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	
	
//	/** Conta a quantidade total de resultados começando com a letra selecionada dependendo do tipo de autoridade escolhida */
//	private int countTotalResultadosEspecificosLetra(DisseminacaoDaInformacaoDao dao) throws DAOException{
//		if(tipoAutoridadeEscolhida == Autoridade.TIPO_ASSUNTO)
//			return dao.countAllAssuntosAutorizados(letraSelecionada);
//		if(tipoAutoridadeEscolhida == Autoridade.TIPO_AUTOR)
//			return dao.countAllAutoresAutorizados(letraSelecionada);
//		return 0;
//	}
	
	
	
	
//	///////////// Para realizar a paginação  /////////////////
//	
//	/** A página atual da paginção dos resultados de assuntos mostrados aos usuários */
//	public int paginaAtual = 1;
//	
//	/** A quantidade total de páginas da páginação */
//	protected int quantidadePaginas = 1;
//	
//	/** A quantidade máxima de materiais mostrados em uma página */
//	public static  final Integer QTD_ASSUNTOS_POR_PAGINA = 40;
//	
//	/** A quantidade total de resultados que serão páginados */
//	public int quantidadeTotalResultados = 0;
//	
//	/** Guarda as página já percorridas pelo usuário para não precisar ficar buscando os resultados toda vez que o usuário passar pela página. */
//	public Set<Integer> paginasPercorridas = new HashSet<Integer>();
//	
//	/** Guarda a posição relativa inicial do assunto dentro da listagem geral de assuntos. Utilizado quando são recuperados apenas assuntos começando com uma determinada letra */
//	//public Integer posicaoRelativaInicialAssunto;
//	
//	
//	/** Utilizado Para buscar assuntos que começam com uma letra específica*/
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
//	 * <p>Buscas os próximos assuntos autorizados de acordo com o página atual que o usuário se encontra.</p>
//	 *  
//	 * <p>A busca é paginada, porém deve manter todos os resultados já buscados em memória, para não apagar
//	 * as seleções feitas pelo usuário.</p> 
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsp</li>
//	 *   </ul>
//	 *
//	 * @param event
//	 * @throws DAOException 
//	 */
//	public void buscaProximosAssuntosAutorizados(ActionEvent event) throws DAOException{
//	
//		///// O parametro é passado quando o usuário seleciona a página atual que deseja ir /////
//		Integer numeroPaginaAtual = getParameterInt("_numero_pagina_atual");
//		
//		if(numeroPaginaAtual == null)
//			paginaAtual = 1;
//		else
//			paginaAtual = numeroPaginaAtual;
//		
//		///// Verifica se não estorou os limiter de resultados /////
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
//			///// Se o usuário está percorrendo uma página já buscada no banco não precisa busca novamente /////
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
//				int indexTemp = 0; // o indice do resultado temporário buscado no banco
//				
//				/* ********************************************************************************************
//				 *  Percorre os dados do primeiro resultado até o último da página e seta os novos resultados *
//				 *  carregados nas possições corretas                                                         *
//				 * ********************************************************************************************/
//				int indexPrimeiroResultado = getRetornaPosicaoAutoridadeDentroDoTotalAutoridades();
//				int indexUltimoResultado =  indexPrimeiroResultado + dadosTemp.size();
//				
//				for (; indexPrimeiroResultado <= indexUltimoResultado && indexTemp < dadosTemp.size() ; indexPrimeiroResultado++) {
//					
//					if(tipoAutoridadeEscolhida == Autoridade.TIPO_ASSUNTO){
//						if(assuntosAutorizados.get(indexPrimeiroResultado).getId() == 0) // se não foi adicionado nada na posição ainda
//							assuntosAutorizados.set(indexPrimeiroResultado, dadosTemp.get(indexTemp));
//					}
//					
//					if(tipoAutoridadeEscolhida == Autoridade.TIPO_AUTOR){
//						if(autoresAutorizados.get(indexPrimeiroResultado).getId() == 0) // se não foi adicionado nada na posição ainda
//							autoresAutorizados.set(indexPrimeiroResultado, dadosTemp.get(indexTemp));
//					}
//					
//					indexTemp++;
//				}
//				
//				// Sempre ao terminar de montar os dados retornados do banco, verifica o que o usuário marcou no perfil dele //
//				configuraInformacoesSalvasPerfilUsuario(dao);
//			}
//			
//		}finally{
//			if(dao != null) dao.close();
//		}
//		
//	}
	
	
//	/**
//	 * <p>Método chamado sempre que o usuário escolhe buscar assuntos ou autores que começem por uma letra específica.</p>
//	 * <p>
//	 *    <strong>I M P O R T A N T E:</strong> 
//	 *    Atualiza todos os resultados (quantiade de páginas, quantidade total de resultados, págians percorridas) 
//	 *    sempre que o filtro da letra é alterado.
//	 * </p>
//	 *  
//	 * <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//			paginasPercorridas = new HashSet<Integer>(); // quando altera a letra, muda a quantidade de páginas, então busca todas novamente
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




