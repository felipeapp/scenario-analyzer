/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 *    MBean que gerencia o caso de uso Substituir fascículo por outro similar. Essa operação ocorre
 * quando um usuário perde ou danifica um exemplar e não o encontra mais para substitui-lo.
 *     É necessário informa que o fascículo vai ser substituído por outro.
 *     Substituição implica em da baixa no fascículo que esta sendo substituído.
 * 
 * @author Jadson
 * @since 25/03/2009
 * @version 1.0 criação da classe
 *
 */
@Component("substituirFasciculoMBean")
@Scope("request")
public class SubstituirFasciculoMBean extends SigaaAbstractController<Fasciculo>{

	
	/** A página onde o usuário vai confirmar a substituição do fascículo. */
	public static final String PAGINA_CONFIRMA_SUBSTITUICAO_FASCICULO_POR_SIMILAR
		= "/biblioteca/processos_tecnicos/catalogacao/confirmaSubstituicaoFasciculo.jsp";

	
	
	/** o fascículo que vai substituir o fascículo que esta na variável "obj" */
	private Fasciculo fasciculoSubstituidor;
	
	
	
	
	/**
	 *    Recebe o id do fascículo, que vai ser substituído, selecionado pelo usuário e retorna
	 *  para a página de pesquisa de fascículo novamente, para o usuário escolher o fascículo que vai
	 *  substituir esse.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaFasciculo.jsp
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
	 *  Vai iniciar a pesquisa de fascículos para o usuário escolher o fascículo que vai
	 *  ser baixado em substituição a um novo fascículo.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processsos_tecnicos/catalogacao/confirmaSubstituicaoFasciculo.jsp
	 */
	public String escollheFasciculoSubstituido() throws SegurancaException{
		
		PesquisarFasciculoMBean bean = getMBean("pesquisarFasciculoMBean");
		
		return bean.iniciarSubstituicaoFasciculo();
	}
	
	
	
	/**
	 *   Vai iniciar a pesquisa de fascículos para o usuário escolher o fascículo que vai
	 *   substituir o fascículo baixado.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processsos_tecnicos/catalogacao/confirmaSubstituicaoFasciculo.jsp
	 * @throws ArqException 
	 */
	public String escollheFasciculoSubstituidor() throws ArqException{
		
		PesquisarFasciculoMBean bean = getMBean("pesquisarFasciculoMBean");
		
		
		
		return bean.iniciarProcuraFasciculoParaSubstituir();
	}
	
	
	/**
	 *     Método chamado quando o usuário escolheu o fascículo que vai substituir o anteriormente
	 *  escolhido.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaFasciculo.jsp
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
	 * Depois que o usuário escolheu os dois fascículos, chama o
	 * processador para trocar um pelo outro. Finalizando o caso de uso!
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processsos_tecnicos/catalogacao/confirmaSubstituicaoFasciculo.jsp
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
		
		addMensagemInformation(" Fascículo substituído com sucesso");
		
		return forward("/biblioteca/index.jsp"); // volta para a pagina principal da aplicação
	}
	
	
	/**
	 *    Usado pelo botão na página de pesquisa de fascículos para voltar a tela de confirmação da substituição.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaFasciculo.jsp
	 */
	public String voltaTelaConfirmaSubstituicao(){
		return telaConfirmaSubstituicao();
	}
	
	
	
	//////////////// telas de navegação /////////////////
	
	/**
	 * Método não chamado por JSP.
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
