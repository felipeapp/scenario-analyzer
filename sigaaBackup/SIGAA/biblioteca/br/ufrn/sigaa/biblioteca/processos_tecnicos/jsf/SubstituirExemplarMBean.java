/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 20/03/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoSubstituirExemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarExemplarMBean;


/**
 *
 *     <p>MBean que gerencia o caso de uso de substituir um exemplar. Essa opera��o ocorre quando um 
 *   usu�rio perde ou danifica um exemplar e n�o encontra mais o mesmo exemplar para a reposi��o.</p>
 *     <p>Neste caso, o usu�rio deve obter um exemplar similar, indicado por um professor, e o bibliotec�rio deve 
 *   substitu�-lo no acervo.</p> 
 *     <p> <i> <strong> Observa��o: </strong> O sistema realiza a baixa no exemplar anterior, e guarda uma refer�ncia que o novo exemplar inclu�do foi em 
 * decorr�ncia da substitui��o do anterior. Isso para poss�veis levantamentos de materiais substitu�dos. </i> </p>
 *
 * @author jadson
 * @since 20/03/2009
 * @version 1.0 criacao da classe
 *
 */
@Component("substituirExemplarMBean") 
@Scope("request")
public class SubstituirExemplarMBean extends SigaaAbstractController<Exemplar>{
	
	/** P�gina onde o usu�rio confirma a substitui��o do exemplar*/
	public static final String PAGINA_CONFIRMA_SUBSTITUICAO_EXEMPLAR 
		= "/biblioteca/processos_tecnicos/catalogacao/confirmaSubstituicaoExemplar.jsp";

	/** o exemplar que vai substituir o exemplar que est�  na variavel "obj" */
	private Exemplar exemplarSubstituidor;  
	
	
	
	/**
	 * M�todo chamado quando o usu�rio seleciona o exemplar que vai ser substitu�do.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/procesos_tencnicos/pesquisa_acervo/resultadoPesquisaExemplar.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String selecionaExemplarSubstituicao() throws SegurancaException, DAOException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
		
		int idExemplarSubstituicao = getParameterInt("idExemplarSubstituicao", 0);
		
		obj = getGenericDAO().findByPrimaryKey(idExemplarSubstituicao, Exemplar.class);
		
		return telaConfirmaSubstituicaoExemplar();
		
	}
	
	
	/**
	 *    Usu�rio vai iniciar a busca pelo o exemplar que vai substituir o anteriormente escolhido.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/confirmaSubstituicaoExemplar.jsp"
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String pesquisaExemplarSubstituidor() throws SegurancaException{
		
		PesquisarExemplarMBean beanPesquisa = getMBean("pesquisarExemplarMBean");
		
		return beanPesquisa.iniciarProcuraExemplarParaSubstituir();
	}
	
	
	
	/**
	 *  Vai iniciar a pesquisa de exemplar que vai ser substitu�do.<br/>
	 *  M�todo usado para o usu�rio queira trocar o exemplar que vai ser substitu�do j� selecionado.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processsos_tecnicos/catalogacao/confirmaSubstituicaoExemplar.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String pesquisaExemplarSubstituido() throws SegurancaException{
		
		PesquisarExemplarMBean bean = getMBean("pesquisarExemplarMBean");
		
		return bean.iniciarSubstituicaoExemplar();
	}
	
	
	
	/**
	 *    <p>M�todo que redireciona o usu�rio para a p�gina na qual ele deve confirmar a substitui��o 
	 *    do exemplar selecionado.  </p>
	 *    <p><i> ( Esse m�todo � chamado depois que o usu�rio seleciona o exemplar que vai substituir o que vai ser 
	 *    baixado. ) </i> </p>
	 *
	 *    <br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/procesos_tencnicos/pesquisa_acervo/resultadoPesquisaExemplar.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionaExemplarSubstituidor()throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.SUBSTITUI_EXEMPLAR);
		
		int idExemplarSubstituidor = getParameterInt("idExemplarSubstituidor", 0);
		
		exemplarSubstituidor = getGenericDAO().findByPrimaryKey(idExemplarSubstituidor, Exemplar.class);
		
		return telaConfirmaSubstituicaoExemplar();
	}
	

	
	
	
	
	
	
	
	
	
	/**
	 *  Confirma a substitui��o do exemplar
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/confirmaSubstituicaoExemplar.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String substituirExemplar()throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		try {
			
			MovimentoSubstituirExemplar mov = new MovimentoSubstituirExemplar(exemplarSubstituidor, obj);
			mov.setCodMovimento(SigaaListaComando.SUBSTITUI_EXEMPLAR);
			execute(mov);
		
			addMensagemInformation(" Exemplar substitu�do com sucesso !");
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;         // fica na mesma pagina
		}
	
		return cancelar(); // volta para a p�gina principal da aplicacao
	}
	
	
	
	
	//////////// telas de navegacao /////////////
	
	
	/**
	 *   M�todo n�o chamado por nenhuma jsp.
	 */
	public String telaConfirmaSubstituicaoExemplar(){
		return forward(PAGINA_CONFIRMA_SUBSTITUICAO_EXEMPLAR);
	}

	
	
	///////// sets e gets  ////////
	

	public Exemplar getExemplarSubstituidor() {
		return exemplarSubstituidor;
	}

	public void setExemplarSubstituidor(Exemplar exemplarSubstituidor) {
		this.exemplarSubstituidor = exemplarSubstituidor;
	}
	
}
