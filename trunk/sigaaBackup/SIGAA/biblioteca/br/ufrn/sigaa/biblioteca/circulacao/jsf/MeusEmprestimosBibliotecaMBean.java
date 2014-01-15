/*
 * EmprestimosUsuarioMBean.java
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

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoRenovaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.integracao.dtos.OperacaoBibliotecaDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.RetornoOperacoesCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.mensagens.MensagensBiblioteca;

/**
 *
 *     <p>Esse MBean é utilizado para visualização e renovação dos empréstimos do usuário atualmente logado.</p>
 *     
 *     <p><strong>IMPORTANTE:</strong> Ele é utilizado também pela pública do sistema, quando o usuário é 
 *         redirecionado para visualizar ou renovar os seus empréstimos. Caso haja alguma alteração no nome das páginas 
 *         é importante se atentar para mudar o link de redirecionamento da parte públuca.
 *         
 *     	   <ul>
 *     		   <li>verTelaLogin.do?urlRedirect=/sigaa/biblioteca/circulacao/visualizaMeusEmprestimos.jsf</li>
 *      	   <li>verTelaLogin.do?urlRedirect=/sigaa/biblioteca/circulacao/renovaMeusEmprestimos.jsf</li>
 *         </ul>    
 *     </p>
 *
 * @author Fred_Castro
 * @since 24/10/2008
 * @version 1.0 criacao da classe
 * @version 1.1 jadson dividindo a renovação em duas páginas e enviando um email de confirmação da renovação.
 * @version 1.2 20/04/2011 jadson adaptando o caso de uso depois que foi introduzida a noção de usuários quitados no sistema.
 */

@Component("meusEmprestimosBibliotecaMBean")
@Scope("request")
public class MeusEmprestimosBibliotecaMBean extends SigaaAbstractController <Emprestimo>{
	
	/** primeira página onde mostra os empréstimos renováveis */
	public static final String PAGINA_RENOVA_MEUS_EMPRESTIMOS = "/biblioteca/circulacao/renovaMeusEmprestimos.jsp"; 
	
	/** primeira página onde mostra os empréstimos renováveis */
	public static final String PAGINA_MEUS_EMPRESTIMOS_ATIVOS = "/biblioteca/circulacao/visualizaMeusEmprestimos.jsp"; 
	
	
	/** Segunda página só chega aqui se os emprétimos foram renovados, nessa página também é impressa o comprovante de renovação. */
	public static final String PAGINA_CONCLUIO_RENOVACAO = "/biblioteca/circulacao/concluiuRenovacaoMeusEmprestimos.jsp";
	
	
	/**
	 * Guarda todos os empréstimos abertos que pode ser renovados do usuário
	 */
	private List <Emprestimo> emprestimosEmAbertoRenovaveis;
	
	/**
	 * Utilizado apenas no caso de uso de visualização dos empréstimos abertos para guardá-los
	 */
	private List <Emprestimo> emprestimosEmAberto;
	
	/**
	 * Guarda os empréstimos selecionados pelo usuário, que serão renovados
	 */
	private List <Emprestimo> emprestimosSelecionados;
	
	
	/** Informações do usário para realizar as renovações.*/
	private InformacoesUsuarioBiblioteca infoUsuario;

	/**
	 * Usuário cujos empréstimos serão renovados
	 */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	
	/** Guarda a lista de empréstimo renovados para poder emitir o comprovante de renovação */
	private List<OperacaoBibliotecaDto> emprestimosRenovadosOp;
	
	/**
	 * Atributo que valida se o comprovante pode ser visualizado ou não.
	 */
	public boolean habilitarComprovante = false;
	
	
	
	/**
	 *    <p>Uma maneira de o usuário visualizar seus empréstimos ativos sem ter que emitir o histórico.</p> 
	 *  
	 * Chamado no portal do discente e do docente  -> Biblioteca -> Empréstimos -> Renovar meus Empréstimos.
	 *
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 *    <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 *     <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 * @throws DAOException 
	 */
	public String iniciarVisualizarEmprestimosAtivos() throws ArqException{
		
		habilitarComprovante = false;
		
		return telaVisualizaEmprestimosAtivos();
	}
	
	
	/**
	 *    <p>Inicia o caso de uso para o usuário renovar os próprios empréstimos. Quando o usuário acessa o caso de uso de algum portal do sistema</p> 
	 *  
	 * Chamado no portal do discente e do docente  -> Biblioteca -> Empréstimos -> Renovar meus Empréstimos.
	 *
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 *    <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 *     <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 * @throws DAOException 
	 */
	public String iniciarVisualizarEmprestimosRenovaveis() throws ArqException{
		
		habilitarComprovante = false;
		
		prepareMovimento(SigaaListaComando.RENOVA_EMPRESTIMO);
		
		return telaRenovaEmprestimos();
	}
	
	
	
	
	
	
	
	/**
	 * <p>Método utilizado para carregar as informações dos empréstimos ativos quando o usuário acessa 
	 * a página para renovar os seus empréstimos.</p>
	 * 
	 * 
	 * <p><strong>IMPORTANTE:</strong>  É necessário que esse método exista porque caso o usuário tente acesar
	 * a opção de renovação diretamente da parte pública do sistema, o método <code>iniciarVisualizarEmprestimosRenovaveis()</code> 
	 * não é chamado, é feito um direcionamento diretamente para o página do caso de uso logo após o login.
	 * Com a ação : verTelaLogin.do?urlRedirect=/sigaa/biblioteca/circulacao/renovaMeusEmprestimos.jsf
	 * </p>
	 * 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/renovaMeusEmprestimos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String getCarregarEmprestimosAtivosRenovaveis() throws DAOException{
		
		long tempo = System.currentTimeMillis();
		
		EmprestimoDao emprestimoDao = null;
		UsuarioBibliotecaDao usuarioDao = null;
		
		
		try {
			emprestimoDao = getDAO(EmprestimoDao.class);
			usuarioDao = getDAO(UsuarioBibliotecaDao.class);
			
			
			emprestimosEmAbertoRenovaveis = new ArrayList <Emprestimo> ();
			
			usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), usuarioDao);
			infoUsuario =  new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioBiblioteca, getUsuarioLogado().getPessoa().getId(), null);
		
			List <Emprestimo> emprestimosAbertos = emprestimoDao.findEmprestimosByUsuarioSituacaoPeriodo(usuarioBiblioteca, false, null, null);
			
			for (Emprestimo e : emprestimosAbertos)
				if (e.podeRenovar()){
					emprestimosEmAbertoRenovaveis.add(e);
					e.getMaterial().setInformacao(BibliotecaUtil.obtemDadosMaterialInformacional(e.getMaterial().getId()));
				}
			
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
		} finally {
			if (emprestimoDao != null) emprestimoDao.close();
			if(usuarioDao != null) usuarioDao.close();
			
			System.out.println(">>> Obter emprétimos renováveis: "+  (  ( System.currentTimeMillis() - tempo) )+"ms");
		}
		
		return "";
	}
	
	
	/**
	 * <p>Método utilizado para carregar as informações dos empréstimos ativos quando o usuário acessa 
	 * a página para visualizar seus empréstimos.</p>
	 * 
	 * 
	 * <p><strong>IMPORTANTE:</strong>  É necessário que esse método exista porque o usuário acessa a página diretamente
	 * redirecionado da área pública atraves da ação: 
	 *    verTelaLogin.do?urlRedirect=/sigaa/biblioteca/circulacao/visualizarMeusEmprestimos.jsf
	 * </p>
	 * 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/visualizaMeusEmprestimos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String getCarregaEmprestimosEmAberto() throws DAOException{
		
		long tempo = System.currentTimeMillis();
		
		EmprestimoDao emprestimoDao = null;
		
		
		
		try {
			emprestimoDao = getDAO(EmprestimoDao.class);
			
			
			
			emprestimosEmAberto = new ArrayList <Emprestimo> ();
			
			usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), null);
		
			emprestimosEmAberto = emprestimoDao.findEmprestimosByUsuarioSituacaoPeriodo(usuarioBiblioteca, false, null, null);
			
			for (Emprestimo e : emprestimosEmAberto)
				e.getMaterial().setInformacao(BibliotecaUtil.obtemDadosMaterialInformacional(e.getMaterial().getId()));
				
			
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
		} finally {
			if (emprestimoDao != null) emprestimoDao.close();
			
			System.out.println(">>> Obter emprétimos abertos: "+  (  ( System.currentTimeMillis() - tempo) )+"ms");
		}
		
		return "";
	}
	
	/**
	 * 
	 * Chama o processador para renovar os empréstimos selecionados pelo usuário
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/confirmaRenovacaoMeusEmprestimos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String renovarEmprestimos() throws ArqException{
		
		habilitarComprovante = false;
		
		emprestimosSelecionados = new ArrayList <Emprestimo> ();
		
		for (Emprestimo e : emprestimosEmAbertoRenovaveis){
			if (e.getSelecionado())
				emprestimosSelecionados.add(e);
		}
			
		if (emprestimosSelecionados.size() == 0){
			addMensagem(MensagensBiblioteca.NENHUM_EMPRESTIMO_SELECIONADO);
			return null;
		}
		
		UsuarioBibliotecaDao dao = null;
		
		if(emprestimosSelecionados != null && emprestimosSelecionados.size() > 0){
		
			try{
			
				// Monta as informações que o processador precisa, apesar de algumas não serem necessárias
				// aqui. Só quando o módulo desktop é usado.
				
				dao = getDAO(UsuarioBibliotecaDao.class);
				
				List <Integer> idsMateriaisARenovar  = new ArrayList <Integer> ();
				
				for (Emprestimo e : emprestimosSelecionados)
					idsMateriaisARenovar.add(e.getMaterial().getId());
				
				// Chama o processador que realiza a renovação
				MovimentoRenovaEmprestimo mov = new MovimentoRenovaEmprestimo(idsMateriaisARenovar, usuarioBiblioteca, usuarioBiblioteca.getSenha());
				
				mov.setCodMovimento(SigaaListaComando.RENOVA_EMPRESTIMO);
		
				// Retorna uma lista de operações feitas
				//emprestimosRenovadosOp = execute(mov, getCurrentRequest());
			
				RetornoOperacoesCirculacaoDTO retorno = execute(mov, getCurrentRequest());
				
				for (String mensagem : retorno.mensagemAosUsuarios) {
					addMensagemWarning(mensagem);
				}
				
				emprestimosRenovadosOp = retorno.getOperacoesRealizadas();
				
				for (OperacaoBibliotecaDto renovacoes : emprestimosRenovadosOp) {
					addMensagemInformation(renovacoes.infoMaterial+"<br/> Prazo para Devolução: "+renovacoes.getPrazoFormatado());
				}
				
				habilitarComprovante = true;
				
				addMensagem(MensagensBiblioteca.EMPRESTIMOS_RENOVADOS);
				
				return telaConcluiuRenovacaoEmprestimos();
				
			} catch (NegocioException ne){
				ne.printStackTrace();
				addMensagemErro(ne.getMessage());
				return null;
			} finally {
				if (dao != null)
					dao.close();
			}
		
		}else{
			addMensagemErro("Nenhum emprétimo foi selecionado.");
			return null;
		}
	}
	
	
	
	/**
	 * 
	 * Método que redireciona para a tela que permite ao usuário gerar um comprovante de renovações<br/>
	 * 
	 * O comprovante é uma opção extrar que o usuário tem para garantir que os empréstimos foram renovados. 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/concluiuRenovacaoMeusEmprestimos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String geraComprovanteRenovacao(){
		
		getCurrentRequest().setAttribute("liberaEmissaoComprovante", true); // para impedir que o usuário tente acessar diretamente a página
		// vários MBean podem emitir o comprovante de renovação, o nome na página está padronizado
		getCurrentRequest().setAttribute("_mBeanRealizouRenovacao", this); 
		return telaComprovanteRenovacao();
	}
	
	
	
	/**
	 *   Retorna os ids dos materiais que foram renovados pelo usuário para serem impressos na 
	 *   página de confirmação da renovação com um parametro, para ser ter mais uma ferramenta para confirmar a renovação
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/concluiuRenovacaoMeusEmprestimos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String getIdsEmprestimoRenovados(){
		
		StringBuilder idsEmprestimosRenovados = new StringBuilder();
		
		if(emprestimosRenovadosOp != null)
		for(OperacaoBibliotecaDto operacaoBiblioteca : emprestimosRenovadosOp){
			idsEmprestimosRenovados.append("___"+operacaoBiblioteca.idEmprestimo+"___");
		}
		
		return idsEmprestimosRenovados.toString();
	}
	
	
	/**
	 *  Não faz nada, só para quando submeter a página com o "<a4j:keepAlive>" não dar erro de campo "not writable" 
	 */
	public void setIdsEmprestimoRenovados(String idsRenovados){
		
	}
	
	
	/**
	 *  Exibe o código de autenticação na página do comprovante.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>>/biblioteca/circulacao/comprovanteComunicacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String getCodigoAutenticacaoRenovacao() throws DAOException{
		return CirculacaoUtil.getCodigoAutenticacaoRenovacao(emprestimosRenovadosOp);
	}
	
	
	
	
	public List<Emprestimo> getEmprestimosAtivosRenovaveis() {
		return emprestimosEmAbertoRenovaveis;
	}

	public void setEmprestimosAtivosRenovaveis(
			List<Emprestimo> emprestimosAtivosRenovaveis) {
		this.emprestimosEmAbertoRenovaveis = emprestimosAtivosRenovaveis;
	}
	
	public List<Emprestimo> getEmprestimosEmAbertoRenovaveis() {
		return emprestimosEmAbertoRenovaveis;
	}

	public void setEmprestimosEmAbertoRenovaveis(
			List<Emprestimo> emprestimosEmAbertoRenovaveis) {
		this.emprestimosEmAbertoRenovaveis = emprestimosEmAbertoRenovaveis;
	}
	
	
	///////////// telas de navegação /////////////////
	

	/**
	 * Redireciona para a tela de conclusão da renovação.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaConcluiuRenovacaoEmprestimos(){
		return forward(PAGINA_CONCLUIO_RENOVACAO);
	}
	
	/**
	 * Redireciona para a tela  onde se visualiza os empréstimos ativo passíveis de renovação.
	 * 
	 *  <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaVisualizaEmprestimosAtivos(){
		return forward(PAGINA_MEUS_EMPRESTIMOS_ATIVOS);
	}
	
	/**
	 * Redireciona para a tela  onde se visualiza os empréstimos ativo passíveis de renovação.
	 * 
	 *  <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaRenovaEmprestimos(){
		return forward(PAGINA_RENOVA_MEUS_EMPRESTIMOS);
	}
	
	/**
	 * Redireciona para a tela de impressão do comprovante de renovação.
	 * 
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaComprovanteRenovacao(){
		return forward(ModuloCirculacaoMBean.PAGINA_COMPROVANTE_RENOVACAO);
	}

	///////////////////////////////////////////////////
	
	public InformacoesUsuarioBiblioteca getInfoUsuario() {
		return infoUsuario;
	}

	public void setInfoUsuario(InformacoesUsuarioBiblioteca infoUsuario) {
		this.infoUsuario = infoUsuario;
	}

//	public List<MaterialInformacional> getMateriaisComprovanteRenovacao() {
//		return materiaisComprovanteRenovacao;
//	}
//
//	public void setMateriaisComprovanteRenovacao(List<MaterialInformacional> materiaisComprovanteRenovacao) {
//		this.materiaisComprovanteRenovacao = materiaisComprovanteRenovacao;
//	}

	public List<Emprestimo> getEmprestimosSelecionados() {
		return emprestimosSelecionados;
	}

	public void setEmprestimosSelecionados(List<Emprestimo> emprestimosSelecionados) {
		this.emprestimosSelecionados = emprestimosSelecionados;
	}

	public boolean isHabilitarComprovante() {
		return habilitarComprovante;
	}

	public void setHabilitarComprovante(boolean habilitarComprovante) {
		this.habilitarComprovante = habilitarComprovante;
	}


	public List<OperacaoBibliotecaDto> getEmprestimosRenovadosOp() {
		return emprestimosRenovadosOp;
	}


	public void setEmprestimosRenovadosOp(List<OperacaoBibliotecaDto> emprestimosRenovadosOp) {
		this.emprestimosRenovadosOp = emprestimosRenovadosOp;
	}

	public List<Emprestimo> getEmprestimosEmAberto() {
		return emprestimosEmAberto;
	}

	public void setEmprestimosEmAberto(List<Emprestimo> emprestimosEmAberto) {
		this.emprestimosEmAberto = emprestimosEmAberto;
	}
	
	
	
	
}