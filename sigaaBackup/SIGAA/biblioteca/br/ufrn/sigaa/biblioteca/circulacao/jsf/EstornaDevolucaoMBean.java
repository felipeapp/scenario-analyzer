/*
 * EstornaDevolucaoMBean.java
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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoDesfazOperacao;
import br.ufrn.sigaa.biblioteca.integracao.dtos.OperacaoBibliotecaDto;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;

/**
 *
 *     Mbean cuja a principal fun��o � chamar o processador para estornar um empr�stimo.
 *
 * @author felipe
 * @since 30/01/2012
 * @version 1.0 criacao da classe
 *
 */
@Component("estornaDevolucaoMBean") 
@Scope("request")
public class EstornaDevolucaoMBean extends SigaaAbstractController<Object>{
	
	/**
	 * 
	 *    M�todo que chama o processador para estornar a devolu��o selecionada
	 *    
	 *    Utilizado na JSP: /sigaa.war/biblioteca/circulacao/listaDevolucoesRecentes.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String estornar() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Vem como par�metro da p�gina que lista as devolu��es recentes de um usu�rio //
		
		Integer idEmprestimo = getParameterInt("idEmprestimo");
		Integer idUsuarioLogado = getUsuarioLogado().getId();
		
		try {

			MovimentoDesfazOperacao mov  = new MovimentoDesfazOperacao(OperacaoBibliotecaDto.OPERACAO_DEVOLUCAO, idEmprestimo, idUsuarioLogado, idUsuarioLogado);
	
			mov.setCodMovimento(SigaaListaComando.DESFAZ_OPERACAO);
		
			execute(mov);
			
			addMensagemInformation("Estorno realizado com sucesso.");
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());

			return null;
		} 
		
		
		/* Retorna para a tela de busca do usu�rio 
		 * Se ele quiser realizar um novo estorno tem que listar as devolu��es recentes novamente, 
		 * para atualizar os empr�stimos e preparar o movimento novamente 
		 */
		
		BuscaUsuarioBibliotecaMBean bean = getMBean("buscaUsuarioBibliotecaMBean");
		return bean.telaBuscaUsuarioBiblioteca();
	}
	
}
