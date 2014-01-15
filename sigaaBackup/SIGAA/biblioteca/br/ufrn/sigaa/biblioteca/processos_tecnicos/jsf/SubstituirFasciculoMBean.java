/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/03/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import org.hibernate.Hibernate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoSubstituirFasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarFasciculoMBean;


/**
 *
 *    MBean que gerencia o caso de uso Substituir fasc�culo por outro similar. Essa opera��o ocorre
 * quando um usu�rio perde ou danifica um exemplar e n�o o encontra mais para substitui-lo.
 *     � necess�rio informa que o fasc�culo vai ser substitu�do por outro.
 *     Substitui��o implica em da baixa no fasc�culo que esta sendo substitu�do.
 * 
 * @author Jadson
 * @since 25/03/2009
 * @version 1.0 cria��o da classe
 *
 */
@Component("substituirFasciculoMBean")
@Scope("request")
public class SubstituirFasciculoMBean extends SigaaAbstractController<Fasciculo>{

	
	/** A p�gina onde o usu�rio vai confirmar a substitui��o do fasc�culo. */
	public static final String PAGINA_CONFIRMA_SUBSTITUICAO_FASCICULO_POR_SIMILAR
		= "/biblioteca/processos_tecnicos/catalogacao/confirmaSubstituicaoFasciculo.jsp";

	
	
	/** o fasc�culo que vai substituir o fasc�culo que esta na vari�vel "obj" */
	private Fasciculo fasciculoSubstituidor;
	
	
	
	
	/**
	 *    Recebe o id do fasc�culo, que vai ser substitu�do, selecionado pelo usu�rio e retorna
	 *  para a p�gina de pesquisa de fasc�culo novamente, para o usu�rio escolher o fasc�culo que vai
	 *  substituir esse.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaFasciculo.jsp
	 */
	public String iniciar() throws SegurancaException, DAOException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		int idFasciculoSubstituicao = getParameterInt("idFasciculoSubstituicao", 0);
		
		obj = getGenericDAO().findByPrimaryKey(idFasciculoSubstituicao, Fasciculo.class);
		
		Hibernate.initialize(obj.getAssinatura());
		Hibernate.initialize(obj.getSituacao());
		
		return telaConfirmaSubstituicao();
		
		
		
	}
	
	
	
	/**
	 *  Vai iniciar a pesquisa de fasc�culos para o usu�rio escolher o fasc�culo que vai
	 *  ser baixado em substitui��o a um novo fasc�culo.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processsos_tecnicos/catalogacao/confirmaSubstituicaoFasciculo.jsp
	 */
	public String escollheFasciculoSubstituido() throws SegurancaException{
		
		PesquisarFasciculoMBean bean = getMBean("pesquisarFasciculoMBean");
		
		return bean.iniciarSubstituicaoFasciculo();
	}
	
	
	
	/**
	 *   Vai iniciar a pesquisa de fasc�culos para o usu�rio escolher o fasc�culo que vai
	 *   substituir o fasc�culo baixado.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processsos_tecnicos/catalogacao/confirmaSubstituicaoFasciculo.jsp
	 * @throws ArqException 
	 */
	public String escollheFasciculoSubstituidor() throws ArqException{
		
		PesquisarFasciculoMBean bean = getMBean("pesquisarFasciculoMBean");
		
		
		
		return bean.iniciarProcuraFasciculoParaSubstituir();
	}
	
	
	/**
	 *     M�todo chamado quando o usu�rio escolheu o fasc�culo que vai substituir o anteriormente
	 *  escolhido.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaFasciculo.jsp
	 */
	public String confirmarSubstituicao()throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		int idFasciculoSubstituidor = getParameterInt("idFasciculoSubstituidor", 0);
		
		prepareMovimento(SigaaListaComando.SUBSTITUI_FASCICULO);
		
		fasciculoSubstituidor = getGenericDAO().findByPrimaryKey(idFasciculoSubstituidor, Fasciculo.class);
		Hibernate.initialize(fasciculoSubstituidor.getAssinatura());
		
		return telaConfirmaSubstituicao();
	}
	
	
	/**
	 * Depois que o usu�rio escolheu os dois fasc�culos, chama o
	 * processador para trocar um pelo outro. Finalizando o caso de uso!
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processsos_tecnicos/catalogacao/confirmaSubstituicaoFasciculo.jsp
	 */
	public String substituirFasciculo()throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		try {
			
			MovimentoSubstituirFasciculo mov = new MovimentoSubstituirFasciculo(fasciculoSubstituidor, obj);
			mov.setCodMovimento(SigaaListaComando.SUBSTITUI_FASCICULO);
			execute(mov);
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;         // fica na mesma pagina
		}
		
		addMensagemInformation(" Fasc�culo substitu�do com sucesso");
		
		return forward("/biblioteca/index.jsp"); // volta para a pagina principal da aplica��o
	}
	
	
	/**
	 *    Usado pelo bot�o na p�gina de pesquisa de fasc�culos para voltar a tela de confirma��o da substitui��o.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaFasciculo.jsp
	 */
	public String voltaTelaConfirmaSubstituicao(){
		return telaConfirmaSubstituicao();
	}
	
	
	
	//////////////// telas de navega��o /////////////////
	
	/**
	 * M�todo n�o chamado por JSP.
	 */
	public String telaConfirmaSubstituicao(){
		return forward(PAGINA_CONFIRMA_SUBSTITUICAO_FASCICULO_POR_SIMILAR);
	}

	

	
	
	// sets e gets
	

	public Fasciculo getFasciculoSubstituidor() {
		return fasciculoSubstituidor;
	}

	public void setFasciculoSubstituidor(Fasciculo fasciculoSubstituidor) {
		this.fasciculoSubstituidor = fasciculoSubstituidor;
	}
	
}
