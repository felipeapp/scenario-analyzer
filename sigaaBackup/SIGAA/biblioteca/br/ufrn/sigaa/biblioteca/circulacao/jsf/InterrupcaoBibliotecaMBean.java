/*
 * InterrupcaoBibliotecaMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software é confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Não se deve utilizar este produto em desacordo com as normas
 * da referida instituição.
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.InterrupcaoBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InterrupcaoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoCadastraInterrupcaoBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * MBean que gerencia o cadastro dos Interrupções para as bibliotecas.
 * 
 * @author Fred_Castro
 *
 */

@Component("interrupcaoBibliotecaMBean")
@Scope("request")
public class InterrupcaoBibliotecaMBean  extends SigaaAbstractController <InterrupcaoBiblioteca>{

	/** Página para buscar no histório de interrruções realizadas em um determinado período. */
	public static final String PAGINA_FILTRO_HISTORICO_INTERRUPCOES = "/biblioteca/InterrupcaoBiblioteca/paginaFiltrosHistoricoInterrupcoes.jsp";
	
	/** Lista todas as interrupções cadastrada no sistema. */
	public static final String PAGINA_HISTORICO_INTERRUPCOES = "/biblioteca/InterrupcaoBiblioteca/paginaHistoricoInterrupcoes.jsp";
	
	
	
	/**
	 * O período da nova interrupções que vai ser cadastrada.  Também usado como data início na emissão do histórico
	 */
	private Date dataInicio;
	
	/**
	 * O período da nova interrupções que vai ser cadastrada. Também usado como data fim na emissão do histórico
	 */
	private Date dataFim;
	
	/**
	 * A biblioteca escolhida pelo usuário para visualizar o histório de interrupções.
	 */
	private Integer idBibliotecaHistorico;
	
	
	/** Interrupções cadastradas no sistema que são mostras ao usuário na página de listagem antes do cadastro. */
	private List<DadosInterrupcoesCadastradas> interrupcoesCadastradas;
	
	/**
	 * A lista de bibliotecas para quais o usuário pode cadastra novos interrupções.
	 */
	private List<Biblioteca> bibliotecasPermissaoCadastroInterrupcao;
	
	
	
	/**
	 *  Construtor padrão.
	 */
	public InterrupcaoBibliotecaMBean() {
		obj = new InterrupcaoBiblioteca();
	}
	
	
	
	/**
	 * <p>Como o caso de uso de cadastrar uma nova interrupção mostrando para os usuários as interrupções 
	 * que estão cadastradas.</p>
	 * 
	 * Chamado a partir da página:  /biblioteca/menu/circulacao.jsp
	 * 
	 * @throws ArqException 
	 * @throws ArqException 
	 */
	public String iniciarCasdastroInterrupcao() throws ArqException{
		
		montaDadosDasInterrupcoesCadastradas();
		
		return telaInterrupcoesCadastradas();
	}
	
	
	
	/**
	 * 
	 *  Permite ao usuário visualizar todas as interrupções cadastradas inclusive as interrupções 
	 *  cuja a data já tenha passado
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/menu/circulacao.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarVisualizarHistoricoInterrupcao () throws ArqException{
		
		/// Por padrão busca o histório do início do ano até hoje.
		
		dataFim = new Date();// hoje
		dataInicio = CalendarUtils.createDate(1, 0, CalendarUtils.getAno(dataFim));  // começo do ano
		
		return telaFiltrosHistoricoInterrupcoes();
	}
	
	
	/**
	 *    Carrega os dados necessários e redireciona o sistema para a página na qual o usuário vai
	 *  entra os dados da um nova interrrupção.
	 *
	 *   Chamado de: /biblioteca/InterrupcaoBiblioteca/lista.jsp.
	 *
	 * @return
	 * @throws ArqException
	 */
	public String preCadastraNovaInterrupcao() throws ArqException{
	
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
		
		prepareMovimento(SigaaListaComando.CADASTRA_INTERRUPCAO_BIBLIOTECA);
		
		apagaDadosFormulario();
		
		BibliotecaDao dao = null;
		try {
			dao = getDAO(BibliotecaDao.class);
		
		if(isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			bibliotecasPermissaoCadastroInterrupcao = dao.findAllBibliotecasInternasAtivas();
		}else{
			
			// ENCONTRA A BIBLIOTECA ONDE O USUÁRIO TEM PERMISSÃO DE ADMINISTRADOR LOCAL //
			
			List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
						getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
			
			bibliotecasPermissaoCadastroInterrupcao  = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades);
		}
		
		if(bibliotecasPermissaoCadastroInterrupcao.size() == 1){ // Se só tem 1 biblioteca
			bibliotecasPermissaoCadastroInterrupcao.get(0).setSelecionada(true); // já seleciona ela por padrão
		}
		
		if(bibliotecasPermissaoCadastroInterrupcao.size() == 0 ){ // Usuário não tem permissão em nenhuma biblioteca
			addMensagemWarning("Usuário não é administrador de nenhuma biblioteca do sistema.");
			return null;
		}
		
		} finally {
			if (dao != null)
				dao.close();
		}
		return telaCadastraNovaInterrupcao();
	}
	
	
	
	/**
	 * Método que realiza a operação de cadastrar uma Interrupção.
	 * 
	 * Chamado a partir da JSP em /biblioteca/InterrupcaoBiblioteca/form.jsp
	 * @throws ArqException 
	 */
	@Override
	public String cadastrar () throws ArqException {
		
		long tempo = System.currentTimeMillis();
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
		
		List<Biblioteca> bibliotecasSelecionadas = new ArrayList<Biblioteca>();
		
		List<Integer> idsBiblitocasSelecionadas = new ArrayList<Integer>();
		
		/* Guarda os dis da biblioteca que estavam com o serviço de empréstimo ativo antes de interrupção 
		 * e esse serviço foi interrompido para o cadastro da interrução.*/
		List<Integer> idBibliotecasServicosEmprestimosEstavaAtivo = null;
		
		// Adiciona à interrrupção as bibliotecas selecionadas pelo usuário //
		for (Biblioteca b : bibliotecasPermissaoCadastroInterrupcao){
			if (b.isSelecionada()){
				bibliotecasSelecionadas.add(b);
				idsBiblitocasSelecionadas.add(b.getId());
			}
		}
		
		obj.setBibliotecas(bibliotecasSelecionadas);
		
		obj.setData(dataInicio); // Para ser validado se o usuário informou.
		
		try {
			
			addMensagens(obj.validate());
			
			if(hasErrors()) // para não ficar desabilitando os serviços de empréstimos se o usuário digiou algo errado.
				return null;
			
			/* ****************** DESABILITA OS EMPRÉSTIMOS E RENOVAÇÕES ANTES DE COMEÇAR AS INTERRUPÇÕES ************************* */
			idBibliotecasServicosEmprestimosEstavaAtivo = desabilitaServicosEmprestimosBibliotecasInterrupcao(idsBiblitocasSelecionadas);
			
			try{ Thread.sleep(10000); }catch (Exception e) { } // dorme 10 segundos para terminar as operação que estavam sendo feitas.
			
			MovimentoCadastraInterrupcaoBiblioteca mov = new MovimentoCadastraInterrupcaoBiblioteca(obj, dataInicio, dataFim);
			mov.setCodMovimento(SigaaListaComando.CADASTRA_INTERRUPCAO_BIBLIOTECA);
			
			List<Emprestimo> emprestimosProrrogados = execute(mov);
			
			
			enviarEmailProrrogacaoPrazo(emprestimosProrrogados);
			
			
			addMensagemInformation(" Em "+ (   (System.currentTimeMillis()-tempo)/1000/60  ) +" minutos. ");
			
			if(emprestimosProrrogados != null)
				addMensagemInformation(emprestimosProrrogados.size()+" empréstimos foram prorrogados. ");
			
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			if(dataInicio != null && dataFim != null)
				addMensagemInformation("Interrupções para o período: "+format.format( dataInicio)+" a "+format.format( dataFim)+" cadastradas com sucesso.<br/> ");
			else
				addMensagemInformation("Interrupções para a data: "+format.format( dataInicio)+" cadastradas com sucesso.<br/> ");
			
			
			montaDadosDasInterrupcoesCadastradas();
			
			return telaInterrupcoesCadastradas();
			
		} catch (NegocioException ne){
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
		}catch (ArqException arqEx){
			
			// Em alguns casos esse ero ocorre, acho que é devido ao caso de uso ficar esperando conseguir o lock das tabelas
			
			if(arqEx != null && arqEx.getMessage() != null 
					&& ( arqEx.getMessage().contains("The transaction is not active") ||  arqEx.getMessage().contains("org.jboss.tm.JBossTransactionRolledbackException") )){
				arqEx.printStackTrace();
				notifyError(arqEx);
				addMensagemErro("A operação demorou muito para ocorrer, talvez haja uma quantidade muito grande de empréstimos a zerem prorrogados. " +
						"Tente cadastrar as interrupções utilizando uma quantidade menor de dias por vez.");
			}else
				throw arqEx;
		}finally{
		
			/* ****************** HABILITA OS EMPRÉSTIMOS E RENOVAÇÕES DEPOIS DE FINALIZAR AS INTERRUPÇÕES ************************* */
			habilitaServicosEmprestimosBibliotecasInterrupcao(idBibliotecasServicosEmprestimosEstavaAtivo);
			
		}
		
		return null;
	}

	
	/*
	 * Envia um email informando ao usuário que os prazos dos empréstimo dele foram prorrogados
	 */
	private void enviarEmailProrrogacaoPrazo(List<Emprestimo> emprestimosProrrogados) throws DAOException{

	
		UsuarioBibliotecaDao dao = null;
		
		try{
		
			dao = getDAO( UsuarioBibliotecaDao.class);
			
			
			for (Emprestimo emprestimo : emprestimosProrrogados) {		
			
				UsuarioBiblioteca usuario = emprestimo.getUsuarioBiblioteca();;
				MaterialInformacional material = emprestimo.getMaterial();
				Integer idEmprestimo = emprestimo.getId();
				String motivo =  obj.getMotivo();
				Date prazoNovo = emprestimo.getPrazo();
					
				// informacoesUsuario[0] == nome Usuario
				// informacoesUsuario[1] == email Usuario
				Object[] informacoesUsuario = dao.findNomeEmailUsuarioBiblioteca(usuario);
				
				SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				
				String assunto = " Aviso de Prorrogação do prazo do Empréstimo ";
				String titulo = " Prorrogação do prazo do seu Empréstimo ";
				String mensagemUsuario = "O empréstimo do material: <i>"+BibliotecaUtil.obtemDadosMaterialInformacional(material.getId())+"</i>";
				
				String mensagemNivel1Email =  " Foi prorrogado para o dia: <strong>"+formatador.format(prazoNovo)+"</strong> , devido ao motivo: ";
				String mensagemNivel3Email =  motivo;
				
				String codigoAutenticacao = BibliotecaUtil.geraNumeroAutenticacaoComprovantes(idEmprestimo, prazoNovo);
				
				new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
						, EnvioEmailBiblioteca.AVISO_PRORROGACAO_EMPRESTIMO, mensagemUsuario, mensagemNivel1Email, null, mensagemNivel3Email, null
						, null, null,  codigoAutenticacao, null);
				
			}

		}finally{
			if(dao != null) dao.close();
		}
	}
	
	

	/**
	 * Tem que ser chamado fora do processador para poder atualizar os dados no banco para as outras transações.
	 *  
	 * @param idsBiblitocasSelecionadas
	 * @param bibliotecaDao
	 * @return
	 * @throws DAOException
	 */
	private List<Integer> desabilitaServicosEmprestimosBibliotecasInterrupcao(List<Integer> idsBiblitocasSelecionadas) throws DAOException {
		
		BibliotecaDao bibliotecaDao = null;
		
		try{
			bibliotecaDao = getDAO(BibliotecaDao.class);
			
			List<Integer> idBibliotecasServicosEmprestimosEstavaAtivo;
			idBibliotecasServicosEmprestimosEstavaAtivo = bibliotecaDao.desativaServicoDeEmprestimoBibliotecas(idsBiblitocasSelecionadas);
			
			return idBibliotecasServicosEmprestimosEstavaAtivo;
			
		}finally{
			if(bibliotecaDao != null ) { bibliotecaDao.getSession().flush(); bibliotecaDao.close(); }
		}
	}
	
	
	

	/**
	 * Tem que ser chamado fora do processador para poder atualizar os dados no banco para as outras transações.
	 *
	 * @param idBibliotecasServicosEmprestimosEstavaAtivo
	 * @throws DAOException 
	 */
	private void habilitaServicosEmprestimosBibliotecasInterrupcao(List<Integer> idBibliotecasServicosEmprestimosEstavaAtivo) throws DAOException {
		
		BibliotecaDao bibliotecaDao = null;
		
		try{
			bibliotecaDao = getDAO(BibliotecaDao.class);
			bibliotecaDao.ativaServicoDeEmprestimoBibliotecas(idBibliotecasServicosEmprestimosEstavaAtivo);
			
		}finally{
			if(bibliotecaDao != null ) { bibliotecaDao.getSession().flush(); bibliotecaDao.close(); }
		}
	}



	
	
	
	
	
	/**
	 * <p>Remove a Interrupção selecionada pelo usuário cadastrada mo sistema, caso ela ainda não tenha 
	 * gerado nenhuma prorrogação nos empréstimos. </p>
	 * 
	 * Chamado a partir da JSP em /biblioteca/InterrupcaoBiblioteca/lista.jsp
	 *
	 * 
	 * @throws SegurancaException 
	 */
	@Override
	public String remover() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
		
		int idInterrupcao = getParameterInt("idInterrupcaoRemocao");
		int idBibliotecaInterrupcao = getParameterInt("idBibliotecaRemocaoInterrupcao");
		
		try {
			
			GenericDAO dao = getGenericDAO();
			
			InterrupcaoBiblioteca inter = dao.refresh(new InterrupcaoBiblioteca(idInterrupcao));
			Biblioteca biblioteca = dao.refresh(new Biblioteca(idBibliotecaInterrupcao));
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado( inter );
			mov.setObjAuxiliar( biblioteca ); // A biblioteca para onde a interrupção vai ser removida
			mov.setCodMovimento(SigaaListaComando.REMOVE_INTERRUPCAO_BIBLIOTECA );
				
			execute(mov);
			
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			
			addMensagemInformation("Interrupção para a "+biblioteca.getDescricao()+" no dia "+format.format(inter.getData())+" removida com sucesso.");
			
			montaDadosDasInterrupcoesCadastradas();
			
			return telaInterrupcoesCadastradas();
			
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		
	}
	
	
	
	
	/**
	 *   Busca as interrupções de acordo com os filtros do usuário, monta a visualização e redireciona 
	 *   para a página para o usuário visualizar essas informações encontradas.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/InterrupcaoBiblioteca/paginaFiltrosHistoricoInterrupcoes.jsp
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String visualizarHistorico() throws DAOException{
		
		interrupcoesCadastradas = new ArrayList<DadosInterrupcoesCadastradas>();
		
		InterrupcaoBibliotecaDao dao = null;
		
		try{
			dao = getDAO(InterrupcaoBibliotecaDao.class);
		
		List<Object[]> dadosInterrupcoes = dao.findAllInterrupcoesAtivasCadastradasNoPeriodo(idBibliotecaHistorico, dataInicio, dataFim);
		
		for (Object[] objects : dadosInterrupcoes) {
			interrupcoesCadastradas.add(  new DadosInterrupcoesCadastradas((Integer) objects[0], (Integer) objects[1], (String)objects[2]
					, (Date) objects[3], (String)objects[4] , (String)objects[5]) );
		}
		
		Collections.sort(interrupcoesCadastradas); // Ordena pela biblioteca
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return telaHistoricoInterrupcoes();
	}
	
	
	
	
	/*
	 * Apaga os dados digitados pelo usuário.
	 */
	private void apagaDadosFormulario(){
		this.obj = new InterrupcaoBiblioteca();
		this.dataFim = null;
		this.dataInicio = null;
	}
	
	
	
	
	
	/**
	 * 
	 * Retorna a descrição da biblioteca selecionada
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/InterrupcaoBiblioteca/paginaHistoricoInterrupcoes.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String getDescricaoBibliotecaHistorico() throws DAOException{
		if(idBibliotecaHistorico != null && idBibliotecaHistorico > 0){
			BibliotecaDao dao = null;
			try{
				dao = getDAO(BibliotecaDao.class);
				return dao.findDescricaoBibliotecaAtiva(idBibliotecaHistorico, true);
			}finally{
				if(dao != null) dao.close();
			}
		}else
			return "Todas";
	}
	
	
	
	/*
	 *  Busca as interrupçõse cadastradas e monta os dados para serem visualizadas na página pelo usuário.
	 *
	 *
	 * @throws DAOException
	 */
	private void montaDadosDasInterrupcoesCadastradas() throws DAOException{
		
		interrupcoesCadastradas = new ArrayList<DadosInterrupcoesCadastradas>();
		
		
		InterrupcaoBibliotecaDao dao = null;
		
		try{
			dao = getDAO(InterrupcaoBibliotecaDao.class);
			
			List<Object[]> dadosInterrupcoes = dao.findAllInterrupcoesAtivasCadastradas();
			
			for (Object[] objects : dadosInterrupcoes) {
				interrupcoesCadastradas.add(  new DadosInterrupcoesCadastradas((Integer) objects[0], (Integer) objects[1], (String)objects[2]
						, (Date) objects[3], (String)objects[4] , (String)objects[5]) );
			}
			
			Collections.sort(interrupcoesCadastradas); // Ordena pela biblioteca
		
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	/**
	 * Classe que guarda os dados das interrupções cadastradas que vão ser exibidos na página 
	 * para o usuários
	 *
	 * @author jadson
	 * @since 10/12/2009
	 * @version 1.0 criacao da classe
	 *
	 */
	public class DadosInterrupcoesCadastradas implements Comparable<DadosInterrupcoesCadastradas>{

		private int idPermisao;
		
		private int idBiblioteca;
		
		private String descricaoBiblioteca;
		
		private Date dataInterrupcao;
		
		private String motivoInterrupcao;
		
		private String criadoPor;
		
		public DadosInterrupcoesCadastradas(int idBiblioteca, String descricaoBiblioteca, 
				Date dataInterrupcao, String motivoInterrupcao, String criadoPor) {
			this.idBiblioteca = idBiblioteca;
			this.descricaoBiblioteca = descricaoBiblioteca;
			this.dataInterrupcao = dataInterrupcao;
			this.motivoInterrupcao = motivoInterrupcao;
			this.criadoPor = criadoPor;
		}
		
		
		public DadosInterrupcoesCadastradas(int idPermisao, int idBiblioteca, String descricaoBiblioteca, 
				Date dataInterrupcao, String motivoInterrupcao, String criadoPor) {
			this(idBiblioteca, descricaoBiblioteca, dataInterrupcao, motivoInterrupcao, criadoPor);
			this.idPermisao = idPermisao;
		}

		/**
		 * Ordena as interrupções para os usuário pela biblioteca
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		
		public int compareTo(DadosInterrupcoesCadastradas arg0) {
			int resultado = new Integer(idBiblioteca).compareTo( arg0.getIdBiblioteca());
			
			if(resultado == 0) // mesma biblioteca
				resultado = this.dataInterrupcao.compareTo(arg0.dataInterrupcao);
			
			return resultado;
		}

		public int getIdBiblioteca() {
			return idBiblioteca;
		}

		public String getDescricaoBiblioteca() {
			return descricaoBiblioteca;
		}

		public Date getDataInterrupcao() {
			return dataInterrupcao;
		}

		public String getMotivoInterrupcao() {
			return motivoInterrupcao;
		}

		public String getCriadoPor() {
			return criadoPor;
		}

		public int getIdPermisao() {
			return idPermisao;
		}

		public void setIdPermisao(int idPermisao) {
			this.idPermisao = idPermisao;
		}
		
	}

	
	
	
	///////////////////////////////// Telas de Nevegação //////////////////////////////
	
	
	
	/**
	 *  Redireciona para a página de listagem das interrupções cadastradas. <br/>
	 *
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String telaInterrupcoesCadastradas() throws ArqException{
		
		prepareMovimento(SigaaListaComando.REMOVE_INTERRUPCAO_BIBLIOTECA ); // na página de listagem é possível remover as interrução
		return forward(getListPage());
	}
	
	
	/**
	 * Redireciona para a página de cadastro de uma nova interrupção. <br/>
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/InterrupcaoBiblioteca/lista.jsp
	 *
	 * @return
	 */
	public String telaCadastraNovaInterrupcao(){
		return forward(getFormPage());
	}
	
	
	/**
	 *  Redireciona para a página de filtro do histórico de interrupções <br/>
	 *
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 */
	public String telaFiltrosHistoricoInterrupcoes(){
		return forward(PAGINA_FILTRO_HISTORICO_INTERRUPCOES);
	}
	
	
	/**
	 *  Redireciona para a página que mostra o histórico de interrções. <br/>
	 *
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 */
	public String telaHistoricoInterrupcoes(){
		return forward(PAGINA_HISTORICO_INTERRUPCOES);
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	
	
	
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public List<Biblioteca> getBibliotecasPermissaoCadastroInterrupcao() {
		return bibliotecasPermissaoCadastroInterrupcao;
	}

	public void setBibliotecasPermissaoCadastroInterrupcao(List<Biblioteca> bibliotecasPermissaoCadastroInterrupcao) {
		this.bibliotecasPermissaoCadastroInterrupcao = bibliotecasPermissaoCadastroInterrupcao;
	}

	public List<DadosInterrupcoesCadastradas> getInterrupcoesCadastradas() {
		return interrupcoesCadastradas;
	}

	public void setInterrupcoesCadastradas(List<DadosInterrupcoesCadastradas> interrupcoesCadastradas) {
		this.interrupcoesCadastradas = interrupcoesCadastradas;
	}

	public Integer getIdBibliotecaHistorico() {
		return idBibliotecaHistorico;
	}

	public void setIdBibliotecaHistorico(Integer idBibliotecaHistorico) {
		this.idBibliotecaHistorico = idBibliotecaHistorico;
	}
	
}