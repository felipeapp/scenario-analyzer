/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.biblioteca.MultaUsuariosBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoCriaGRUMultaBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategyFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * <p>Mbean por que controla o caso de uso no qual o usu�rio vai imprimir GRUs para pagar suas multas na biblioteca</p>
 * 
 * @author jadson
 *
 */
@Component("emitirGRUPagamentoMultasBibliotecaMBean")
@Scope ("request")
public class EmitirGRUPagamentoMultasBibliotecaMBean  extends SigaaAbstractController<MultaUsuarioBiblioteca>{

	
	/** P�gina que lista as suspens�es ativas de um usu�rio. */
	public static final String PAGINA_LISTA_MULTAS_USUARIO_ATUALMENTE_LOGADO = "/biblioteca/circulacao/multas/listaMultasUsuarioAtualmenteLogado.jsp";
	
	/** P�gina na qual o operador pode emitir uma �nica GRU para v�rias multas do usu�rio. */
	public static final String PAGINA_EMITE_GRU_VARIAS_MULTAS_USUARIO_ATUALMENTE_LOGADO = "/biblioteca/circulacao/multas/emiteGRUUnicaVariasMultasUsuarioAtualmenteLogado.jsp";
	
	
	/** Armazena o usu�rio da biblioteca selecionado na pesquias */
	//private UsuarioBiblioteca usuarioBiblioteca = new UsuarioBiblioteca();
	
	
	/** Armazena as informa��es do usu�rio da Biblioteca selecionado na pesquias */
	private InformacoesUsuarioBiblioteca informacaoUsuario;
	
	/** Guarda a lista de multas ativas do usu�rio.  */
	private List<MultaUsuarioBiblioteca> multasAtivasUsuario;
	
	/** Guarda a GRU na emiss�o de v�rias multa numa �nica GRU. */
	private ByteArrayOutputStream outputSteam;
	
	
	
	/**
	 * <p> Inicia o caso de uso com o usu�rio visualizando as suas multas no sistema </p>
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/modulo_circulacao_servidor.jsp</li>
	 *   	<li>/sigaa.war/portais/discentes/menu_discente.jsp</li>
	 *   	<li>/sigaa.war/portais/discentes/menu_docente.jsp</li>
	 *   </ul> 
	 */
	public String listarMinhasMultasAtivas() throws ArqException{

		prepareMovimento(SigaaListaComando.CRIA_GRU_MULTA_BIBLIOTECA);
		
		UsuarioBibliotecaDao dao = null;
		MultaUsuariosBibliotecaDao multaDao = null;
		
		try {
			
			dao = getDAO(UsuarioBibliotecaDao.class);
			multaDao = getDAO(MultaUsuariosBibliotecaDao.class);
		
			/*
			 * Armazena as informa��es pessoas do usu�rio, mesmo que j� n�o possua conta na biblioteca (todas esteja quitadas)
			 * 
			 * Obs.: S� pessoas utilizam esse caso de uso, nunca bibliotecas.
			 */
			informacaoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(null, getUsuarioLogado().getPessoa().getId(), null);
			
			multasAtivasUsuario = multaDao.findMultasAtivasDoUsuario(getUsuarioLogado().getPessoa().getId(), null );
			
			return telaListaMultasUsuario();
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} finally {
			if (dao != null)  dao.close();
			if (multaDao != null)  multaDao.close();
		}
	}
	
	/**
	 *  Emite a GRU para o usu�rio pagar a multa
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/listaMultasUsuarioAtualmenteLogado.jsp</li>
	 *   </ul>
	 *
	 * @throws ArqException 
	 * @throws NegocioException 
	 *
	 */
	public String emitirGRU() throws ArqException, NegocioException{
		
		int idMultaSelecionada = getParameterInt("idMultaSelecionada");
		
		
		for (MultaUsuarioBiblioteca multa : multasAtivasUsuario) {
			
			if(multa.getId() == idMultaSelecionada){
				obj = multa;
			}
		}
		
		if(obj == null){
			addMensagemErro("Erro ao selecionar a multa para ser paga. Por favor, reinicie o processo! "); // Mesagem que n�o era para ser mostrada
			return null;
		}
			
		// N�o precisa, n�o � mais impresso na GRU
		//obj.setInfoIdentificacaoMultaGerada( MultaUsuarioBibliotecaUtil.montaInformacoesMultaComprovante(obj, false));
		
		if(obj.getIdGRUQuitacao() == null){ // A GRU n�o foi gerada ainda
			
			try {
			
				MovimentoCriaGRUMultaBiblioteca movGRU = new MovimentoCriaGRUMultaBiblioteca(obj);
				movGRU.setCodMovimento(SigaaListaComando.CRIA_GRU_MULTA_BIBLIOTECA);
			
				obj = (MultaUsuarioBiblioteca) execute(movGRU);    // A multa com o id da GRU gerada
				
			} catch (NegocioException ne) {
				addMensagens(ne.getListaMensagens());
				return null;
			} finally{
				prepareMovimento(SigaaListaComando.CRIA_GRU_MULTA_BIBLIOTECA);  // prepara o pr�ximo, caso o usu�rio tente reimprimir
			}  
			
			
		}
		
		try{
			
			outputSteam = new ByteArrayOutputStream();
			
			GuiaRecolhimentoUniaoHelper.gerarPDF(outputSteam, obj.getIdGRUQuitacao());
			
			
			outputSteam.flush();
			outputSteam.close();
			
		} catch (IOException e) {
			addMensagemErro("Erro ao tentar gerar a GRU, por favor tente novamente, caso o problema persista, contacte o suporte.");
		}
		
		return forward(PAGINA_LISTA_MULTAS_USUARIO_ATUALMENTE_LOGADO);
		
	}

	
	
	/**
	 *  <p>Redireciona para a p�gina na qual o bibliotec�rio vai escolher v�rias multas e emitir 1 �nica  GRU para pagar todas eles e economizar papel, agilizando o processo.</p>
	 *  
	 *  <p>O operador vai escolher a unidade de recebimento da GRU e selecionar mas multas.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/multas/listaMultasUsuarioAtualmenteLogado.jsp</li>
	 *   </ul>
	 * @throws DAOException 
	 *
	 * @throws ArqException 
	 * @throws NegocioException 
	 *
	 */
	public String preEmitirGRUUnicaParaMultasAbertas() throws DAOException{
		
		Biblioteca bibliotecaCentral = BibliotecaUtil.getBibliotecaCentral(new String[]{"id", "descricao", "unidade.id"});
		
		// Caso alguma multa n�o tenha unidade de recolhimento, coloca a unidade de recolhimento da biblioteca central  //
		// que � obrigat�ria no sistema                                                                                 //
		for (MultaUsuarioBiblioteca multa : multasAtivasUsuario){
			
			if(multa.getIdGRUQuitacao() == null)
				multa.setSelecionado(true);  // Por padr�o seleciona todas cuja GRU n�o foi emitada ainda !
			else
				multa.setSelecionado(false);
			
			if ( multa.isManual() ){
				if( multa.getBibliotecaRecolhimento() == null ){
					multa.setBibliotecaRecolhimento(bibliotecaCentral);
				}
			}else{
				if( multa.getEmprestimo().getMaterial().getBiblioteca() == null ){
					multa.getEmprestimo().getMaterial().setBiblioteca(bibliotecaCentral);
				}
			}
		}
		
		return forward(PAGINA_EMITE_GRU_VARIAS_MULTAS_USUARIO_ATUALMENTE_LOGADO);
	}
	
	
	
	
	/**
	 *  <p>Realiza a a��o de emitir uma �nica GRU para as multas selecionados no passo anterior.</p>
	 *  
	 *  <p>O operador tem que selecionar as multas porque caso o sistema trabalhe com v�rias unidade de recolhimento o operador tem que escolher a unidade de recolhimento da GRU.</p>
	 * @throws ArqException 
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/multas/emiteGRUUnicaVariasMultasUsuarioAtualmenteLogado.jsp</li>
	 *   </ul>
	 *
	 * @String
	 */
	public String emitirGRUUnicaParaMultasAbertas() throws ArqException{
		
		List<MultaUsuarioBiblioteca> multasASeremPagas =  new ArrayList<MultaUsuarioBiblioteca>();
		
		for (MultaUsuarioBiblioteca multa : multasAtivasUsuario){	
			if(multa.isSelecionado()){
				multasASeremPagas.add(multa);
			}
		}
		
		
		try {
			
			MovimentoCriaGRUMultaBiblioteca movGRU = new MovimentoCriaGRUMultaBiblioteca(multasASeremPagas);
			movGRU.setCodMovimento(SigaaListaComando.CRIA_GRU_MULTA_BIBLIOTECA);
			
			@SuppressWarnings("unchecked")
			List<MultaUsuarioBiblioteca> retorno = (List<MultaUsuarioBiblioteca>) execute(movGRU);
			
			multasASeremPagas = retorno;    // As multas com o id da GRU gerada
		
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} finally{
			prepareMovimento(SigaaListaComando.CRIA_GRU_MULTA_BIBLIOTECA);  // prepara o pr�ximo, caso o usu�rio tente reimprimir
		}  
	
		
		try{
			
			outputSteam = new ByteArrayOutputStream();
			
			// Pega apenas o id para primeira GRU que a mesma GRU das outras multas.
			GuiaRecolhimentoUniaoHelper.gerarPDF(outputSteam, multasASeremPagas.get(0).getIdGRUQuitacao()); 
			
			outputSteam.flush();
			outputSteam.close();
			
		} catch (IOException e) {
			addMensagemErro("Erro ao tentar gerar a GRU, por favor tente novamente, caso o problema persista, contacte o suporte.");
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
		}finally{
			
		}
	
	
		
		return forward(PAGINA_LISTA_MULTAS_USUARIO_ATUALMENTE_LOGADO);
	}
	
	
	/**
	 * <p>Envia o PDF para a sa�da do usu�rio.</p>
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/circulacao/multas/emiteGRUUnicaVariasMultasUsuarioAtualmenteLogado.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws IOException 
	 */
	public void visualizarGRUUnicaGerada(ActionEvent evt) {
		if(outputSteam != null){
			
			try {
				
				DataOutputStream dos = new DataOutputStream(getCurrentResponse().getOutputStream());
			
				dos.write(outputSteam.toByteArray());
				getCurrentResponse().setContentType("Content-Type: application/pdf;");
				getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=GRU.pdf");
				FacesContext.getCurrentInstance().responseComplete();
				
				outputSteam = null;
				
			} catch (IOException e) {
				e.printStackTrace();
				addMensagemErro("Erro ao tentar gerar a GRU, por favor tente novamente, caso o problema persista, contacte o suporte.");
			}
		}
		
		
	}
	
	
	/**
	 * Retorna a quantidade de multas cujas GRUs ainda n�o foram emitidas
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/multas/emiteGRUUnicaVariasMultasUsuarioAtualmenteLogado.jsp</li>
	 *   </ul>
	 *
	 * @int
	 */
	public int getQuantidadeGRUsNaoEmitidas(){
	
		int qtdGRUsNaoEmitidas = 0;
		
		if(multasAtivasUsuario != null && multasAtivasUsuario.size() > 0){
			for (MultaUsuarioBiblioteca multa : multasAtivasUsuario){
			
				if(multa.getIdGRUQuitacao() == null){
					qtdGRUsNaoEmitidas++;
				}
			}
		}
		
		return qtdGRUsNaoEmitidas;
	}
	
	
	
	/**
	 * Tela que lista as multas do usu�rio.
	 * 
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaListaMultasUsuario(){
		return forward(PAGINA_LISTA_MULTAS_USUARIO_ATUALMENTE_LOGADO);
	}

	
	
	/**
	 * Verifica se o sistema est� trabalhando com multas para permitir o usu�rio emitir a GRU para pagamento.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/listaMultasUsuarioAtualmenteLogado.jsp</li>
	 *   </ul>
	 * @return
	 */
	public boolean isSistemaTrabalhaComMultas(){
		return PunicaoAtrasoEmprestimoStrategyFactory.sistemaTrabalhaComMultas();
	}
	public InformacoesUsuarioBiblioteca getInformacaoUsuario() {
		return informacaoUsuario;
	}

	public void setInformacaoUsuario(InformacoesUsuarioBiblioteca informacaoUsuario) {
		this.informacaoUsuario = informacaoUsuario;
	}

	public List<MultaUsuarioBiblioteca> getMultasAtivasUsuario() {
		return multasAtivasUsuario;
	}

	public void setMultasAtivasUsuario(
			List<MultaUsuarioBiblioteca> multasAtivasUsuario) {
		this.multasAtivasUsuario = multasAtivasUsuario;
	}

	public ByteArrayOutputStream getOutputSteam() {
		return outputSteam;
	}

	public void setOutputSteam(ByteArrayOutputStream outputSteam) {
		this.outputSteam = outputSteam;
	}
	
	
	
}
