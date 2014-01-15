/*
 * ProrrogarPrazosEmprestimosMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 *
 * MBean que gerencia a alteração de prazos de todos os empréstimos que caem em um certo período.
 * Ex: Se os prazos de alguns empréstimos caem no dia 25 de Dezembro, um operador da biblioteca pode
 * prorrogar esses prazos para a próxima data em que a biblioteca estará aberta. Se depois do dia 24, a
 * biblioteca só funcionará no dia 10 de janeiro, o funcionário informará que o período de 25 dezembro a 9 de
 * janeiro deve ser prorrogado. 
 *
 * @author jadson
 * @since 22/10/2008
 * @version 1.0 criação da classe
 *
 */
@SuppressWarnings("unchecked")
@Component("prorrogarPrazosEmprestimos") 
@Scope("request")
@Deprecated
public class ProrrogarPrazosEmprestimosMBean  extends SigaaAbstractController{

//	public static final String PAGINA_PRORROGA_PRAZOS_EMPRESTIMOS= "/biblioteca/circulacao/prorrogaPrazosEmprestimos.jsp";
//	
//	
//	private Date  dataInicioPeriodo = new Date();
//	
//	private Date dataFinalPeriodo;
//
//	private String senhaSigaaProrrogarPrazo;
//	
//	
//	public String iniciar() throws SegurancaException{
//		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
//		return telaProrrogaPrazosEmprestimos();
//	}
//	
//	/**
//	 * Chama o processador para atualizar os empréstimos, setando os seus prazos para um dia após
//	 * a dataFinalPeriodo.
//	 *
//	 * @return
//	 * @throws SegurancaException 
//	 */
//	public String prorrogar() throws SegurancaException{
//		
//		
//		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
//		
//		
//		if ( BibliotecaUtil.senhaSigaaCorreta(senhaSigaaProrrogarPrazo, getUsuarioLogado()) ) {
//		
//			try{
//			
//				
//				prepareMovimento(SigaaListaComando.PRORROGA_PRAZOS_EMPRESTIMOS);
//				
//				
//				MovimentoProrrogaPrazoEmprestimo mov 
//						= new MovimentoProrrogaPrazoEmprestimo(dataInicioPeriodo, dataFinalPeriodo);
//		
//				mov.setCodMovimento(SigaaListaComando.PRORROGA_PRAZOS_EMPRESTIMOS);
//			
//				execute(mov);
//				
//				addMensagemInformation("Prazos prorrogados com sucesso.");
//				return forward( "/biblioteca/index.jsp");
//		
//			}catch(DAOException de){
//				de.printStackTrace();
//				addMensagemErro("Ocorreu um erro as buscar os empréstimo ativos - por favor contate o suporte");
//			}catch(NegocioException ne){	
//				ne.printStackTrace();
//				addMensagemErro(ne.getMessage());
//			}catch(Exception e){	
//				e.printStackTrace();
//				addMensagemErroPadrao();
//			}
//			
//			
//			return null;
//			
//		}else{
//			addMensagemErro("A senha do sistema SIGAA está incorreta!");
//			return null;
//		}
//			
//	}
//	
//	
//	/**
//	 * Apenas volta para a página inicial.
//	 * 
//	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
//	 */
//	public String cancelar(){
//		return forward("/biblioteca/index.jsp");
//	}
//	
//	
//	
//	// Telas de navegação. 
//	
//	
//	public String telaProrrogaPrazosEmprestimos(){
//		return forward(PAGINA_PRORROGA_PRAZOS_EMPRESTIMOS);
//	}
//	
//	
//	
//	
//	
//	//Sets e gets.
//
//
//	public String getSenhaSigaaProrrogarPrazo() {
//		return senhaSigaaProrrogarPrazo;
//	}
//
//
//	
//
//	public Date getDataInicioPeriodo() {
//		return dataInicioPeriodo;
//	}
//
//
//	public void setDataInicioPeriodo(Date dataInicioPeriodo) {
//		this.dataInicioPeriodo = dataInicioPeriodo;
//	}
//
//
//	public Date getDataFinalPeriodo() {
//		return dataFinalPeriodo;
//	}
//
//
//	public void setDataFinalPeriodo(Date dataFinalPeriodo) {
//		this.dataFinalPeriodo = dataFinalPeriodo;
//	}
//
//
//	public void setSenhaSigaaProrrogarPrazo(String senhaSigaaProrrogarPrazo) {
//		this.senhaSigaaProrrogarPrazo = senhaSigaaProrrogarPrazo;
//	}
//	
	
}
