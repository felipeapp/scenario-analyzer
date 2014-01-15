/*
 * EmprestimosUsuarioMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software � confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * N�o se deve utilizar este produto em desacordo com as normas
 * da referida institui��o.
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
 *     <p>Esse MBean � utilizado para visualiza��o e renova��o dos empr�stimos do usu�rio atualmente logado.</p>
 *     
 *     <p><strong>IMPORTANTE:</strong> Ele � utilizado tamb�m pela p�blica do sistema, quando o usu�rio � 
 *         redirecionado para visualizar ou renovar os seus empr�stimos. Caso haja alguma altera��o no nome das p�ginas 
 *         � importante se atentar para mudar o link de redirecionamento da parte p�bluca.
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
 * @version 1.1 jadson dividindo a renova��o em duas p�ginas e enviando um email de confirma��o da renova��o.
 * @version 1.2 20/04/2011 jadson adaptando o caso de uso depois que foi introduzida a no��o de usu�rios quitados no sistema.
 */

@Component("meusEmprestimosBibliotecaMBean")
@Scope("request")
public class MeusEmprestimosBibliotecaMBean extends SigaaAbstractController <Emprestimo>{
	
	/** primeira p�gina onde mostra os empr�stimos renov�veis */
	public static final String PAGINA_RENOVA_MEUS_EMPRESTIMOS = "/biblioteca/circulacao/renovaMeusEmprestimos.jsp"; 
	
	/** primeira p�gina onde mostra os empr�stimos renov�veis */
	public static final String PAGINA_MEUS_EMPRESTIMOS_ATIVOS = "/biblioteca/circulacao/visualizaMeusEmprestimos.jsp"; 
	
	
	/** Segunda p�gina s� chega aqui se os empr�timos foram renovados, nessa p�gina tamb�m � impressa o comprovante de renova��o. */
	public static final String PAGINA_CONCLUIO_RENOVACAO = "/biblioteca/circulacao/concluiuRenovacaoMeusEmprestimos.jsp";
	
	
	/**
	 * Guarda todos os empr�stimos abertos que pode ser renovados do usu�rio
	 */
	private List <Emprestimo> emprestimosEmAbertoRenovaveis;
	
	/**
	 * Utilizado apenas no caso de uso de visualiza��o dos empr�stimos abertos para guard�-los
	 */
	private List <Emprestimo> emprestimosEmAberto;
	
	/**
	 * Guarda os empr�stimos selecionados pelo usu�rio, que ser�o renovados
	 */
	private List <Emprestimo> emprestimosSelecionados;
	
	
	/** Informa��es do us�rio para realizar as renova��es.*/
	private InformacoesUsuarioBiblioteca infoUsuario;

	/**
	 * Usu�rio cujos empr�stimos ser�o renovados
	 */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	
	/** Guarda a lista de empr�stimo renovados para poder emitir o comprovante de renova��o */
	private List<OperacaoBibliotecaDto> emprestimosRenovadosOp;
	
	/**
	 * Atributo que valida se o comprovante pode ser visualizado ou n�o.
	 */
	public boolean habilitarComprovante = false;
	
	
	
	/**
	 *    <p>Uma maneira de o usu�rio visualizar seus empr�stimos ativos sem ter que emitir o hist�rico.</p> 
	 *  
	 * Chamado no portal do discente e do docente  -> Biblioteca -> Empr�stimos -> Renovar meus Empr�stimos.
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 *    <p>Inicia o caso de uso para o usu�rio renovar os pr�prios empr�stimos. Quando o usu�rio acessa o caso de uso de algum portal do sistema</p> 
	 *  
	 * Chamado no portal do discente e do docente  -> Biblioteca -> Empr�stimos -> Renovar meus Empr�stimos.
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>M�todo utilizado para carregar as informa��es dos empr�stimos ativos quando o usu�rio acessa 
	 * a p�gina para renovar os seus empr�stimos.</p>
	 * 
	 * 
	 * <p><strong>IMPORTANTE:</strong>  � necess�rio que esse m�todo exista porque caso o usu�rio tente acesar
	 * a op��o de renova��o diretamente da parte p�blica do sistema, o m�todo <code>iniciarVisualizarEmprestimosRenovaveis()</code> 
	 * n�o � chamado, � feito um direcionamento diretamente para o p�gina do caso de uso logo ap�s o login.
	 * Com a a��o : verTelaLogin.do?urlRedirect=/sigaa/biblioteca/circulacao/renovaMeusEmprestimos.jsf
	 * </p>
	 * 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
			
			System.out.println(">>> Obter empr�timos renov�veis: "+  (  ( System.currentTimeMillis() - tempo) )+"ms");
		}
		
		return "";
	}
	
	
	/**
	 * <p>M�todo utilizado para carregar as informa��es dos empr�stimos ativos quando o usu�rio acessa 
	 * a p�gina para visualizar seus empr�stimos.</p>
	 * 
	 * 
	 * <p><strong>IMPORTANTE:</strong>  � necess�rio que esse m�todo exista porque o usu�rio acessa a p�gina diretamente
	 * redirecionado da �rea p�blica atraves da a��o: 
	 *    verTelaLogin.do?urlRedirect=/sigaa/biblioteca/circulacao/visualizarMeusEmprestimos.jsf
	 * </p>
	 * 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
			
			System.out.println(">>> Obter empr�timos abertos: "+  (  ( System.currentTimeMillis() - tempo) )+"ms");
		}
		
		return "";
	}
	
	/**
	 * 
	 * Chama o processador para renovar os empr�stimos selecionados pelo usu�rio
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
			
				// Monta as informa��es que o processador precisa, apesar de algumas n�o serem necess�rias
				// aqui. S� quando o m�dulo desktop � usado.
				
				dao = getDAO(UsuarioBibliotecaDao.class);
				
				List <Integer> idsMateriaisARenovar  = new ArrayList <Integer> ();
				
				for (Emprestimo e : emprestimosSelecionados)
					idsMateriaisARenovar.add(e.getMaterial().getId());
				
				// Chama o processador que realiza a renova��o
				MovimentoRenovaEmprestimo mov = new MovimentoRenovaEmprestimo(idsMateriaisARenovar, usuarioBiblioteca, usuarioBiblioteca.getSenha());
				
				mov.setCodMovimento(SigaaListaComando.RENOVA_EMPRESTIMO);
		
				// Retorna uma lista de opera��es feitas
				//emprestimosRenovadosOp = execute(mov, getCurrentRequest());
			
				RetornoOperacoesCirculacaoDTO retorno = execute(mov, getCurrentRequest());
				
				for (String mensagem : retorno.mensagemAosUsuarios) {
					addMensagemWarning(mensagem);
				}
				
				emprestimosRenovadosOp = retorno.getOperacoesRealizadas();
				
				for (OperacaoBibliotecaDto renovacoes : emprestimosRenovadosOp) {
					addMensagemInformation(renovacoes.infoMaterial+"<br/> Prazo para Devolu��o: "+renovacoes.getPrazoFormatado());
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
			addMensagemErro("Nenhum empr�timo foi selecionado.");
			return null;
		}
	}
	
	
	
	/**
	 * 
	 * M�todo que redireciona para a tela que permite ao usu�rio gerar um comprovante de renova��es<br/>
	 * 
	 * O comprovante � uma op��o extrar que o usu�rio tem para garantir que os empr�stimos foram renovados. 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/concluiuRenovacaoMeusEmprestimos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String geraComprovanteRenovacao(){
		
		getCurrentRequest().setAttribute("liberaEmissaoComprovante", true); // para impedir que o usu�rio tente acessar diretamente a p�gina
		// v�rios MBean podem emitir o comprovante de renova��o, o nome na p�gina est� padronizado
		getCurrentRequest().setAttribute("_mBeanRealizouRenovacao", this); 
		return telaComprovanteRenovacao();
	}
	
	
	
	/**
	 *   Retorna os ids dos materiais que foram renovados pelo usu�rio para serem impressos na 
	 *   p�gina de confirma��o da renova��o com um parametro, para ser ter mais uma ferramenta para confirmar a renova��o
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 *  N�o faz nada, s� para quando submeter a p�gina com o "<a4j:keepAlive>" n�o dar erro de campo "not writable" 
	 */
	public void setIdsEmprestimoRenovados(String idsRenovados){
		
	}
	
	
	/**
	 *  Exibe o c�digo de autentica��o na p�gina do comprovante.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
	
	///////////// telas de navega��o /////////////////
	

	/**
	 * Redireciona para a tela de conclus�o da renova��o.
	 * 
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaConcluiuRenovacaoEmprestimos(){
		return forward(PAGINA_CONCLUIO_RENOVACAO);
	}
	
	/**
	 * Redireciona para a tela  onde se visualiza os empr�stimos ativo pass�veis de renova��o.
	 * 
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaVisualizaEmprestimosAtivos(){
		return forward(PAGINA_MEUS_EMPRESTIMOS_ATIVOS);
	}
	
	/**
	 * Redireciona para a tela  onde se visualiza os empr�stimos ativo pass�veis de renova��o.
	 * 
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaRenovaEmprestimos(){
		return forward(PAGINA_RENOVA_MEUS_EMPRESTIMOS);
	}
	
	/**
	 * Redireciona para a tela de impress�o do comprovante de renova��o.
	 * 
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
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