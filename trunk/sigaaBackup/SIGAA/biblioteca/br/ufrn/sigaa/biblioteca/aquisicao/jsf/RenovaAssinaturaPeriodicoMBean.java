/*
 * RenovaAssinaturaPeriodicoMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.aquisicao.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.aquisicao.negocio.MovimentoRenovaAssinaturaPeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RenovacaoAssinatura;

/**
 *      Mbean respons�vel por gerenciar o caso de uso de renovar assinaturas. <br/>
 *      Quando uma assinatura � renovada, um hist�rico dos per�odos da renova��o da assinatura
 *   � guardado pelo sistema. <br/>
 *
 * @author jadson
 * @since 16/11/2009
 * @version 1.0 criacao da classe
 * @version 1.5 31/01/2012 alterando para utilizar a interface de busca de assinaturas em vez de chamar diretamente a busca.
 */
@Component("renovaAssinaturaPeriodicoMBean") 
@Scope("request")
public class RenovaAssinaturaPeriodicoMBean extends SigaaAbstractController<RenovacaoAssinatura> implements PesquisarAcervoAssinaturas{

	/** a p�gina para realizar a renova��o da assinatura */
	public static final String PAGINA_RENOVA_ASSINATURA = "/biblioteca/aquisicao/paginaRenovaAssinatura.jsp";
	
	/** a assintura que vai ser renovada selecionada na tela de busca */
	private Assinatura assinaturaSelecionada; 
	
	/** as renova��es que j� foram feita na assinatura */
	private List<RenovacaoAssinatura> renovacoes = new ArrayList<RenovacaoAssinatura>();
	
	
	/**
	 *   Inicia o caso de uso de renovar uma assinatura de peri�dico. <br/> 
	 *   
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/menu/aquisicoes.jsp <br/>
	 *    
	 * @throws ArqException 
	 */
	public String iniciarRenovacaoAssinatura() throws ArqException{
		
		obj = new RenovacaoAssinatura();
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		return bean.iniciarPesquisaSelecionarAssinatura(this);
	}
	
	
	
	//////////////   M�todos da interface de busca de assinaturas  //////////////
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#setAssinatura(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura)
	 */
	@Override
	public void setAssinatura(Assinatura assinatura) throws ArqException {
		this.assinaturaSelecionada = assinatura;
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#selecionaAssinatura()
	 */
	@Override
	public String selecionaAssinatura() throws ArqException {
		
		prepareMovimento(SigaaListaComando.RENOVA_ASSINATURA_PERIODICO);
		
		assinaturaSelecionada = getGenericDAO().findByPrimaryKey(assinaturaSelecionada.getId(), Assinatura.class);
		
		renovacoes = assinaturaSelecionada.getRenovacoes();
		
		///// O per�odo de renova��o da assinatura � sugerido com base na data de vencimento atual da assintura /////
		obj.setDataInicial(assinaturaSelecionada.getDataTerminoAssinatura());
		obj.setDataFinal(assinaturaSelecionada.getDataTerminoAssinatura());
		
		return telaRenovaAsssinatura();
	}

	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#voltarBuscaAssinatura()
	 */
	@Override
	public String voltarBuscaAssinatura() throws ArqException {
		return null;
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#isUtilizaVoltarBuscaAssinatura()
	 */
	
	@Override
	public boolean isUtilizaVoltarBuscaAssinatura() {
		return false;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	/**
	 *     Realiza a opera��o de renovar a assinatura
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/paginaRenovaAssinatura.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String renovarAssinatura() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		try{
			
			MovimentoRenovaAssinaturaPeriodico mov = new MovimentoRenovaAssinaturaPeriodico(obj, assinaturaSelecionada);
			mov.setCodMovimento(SigaaListaComando.RENOVA_ASSINATURA_PERIODICO);
			
			String periodoRenovacao = (String) execute(mov);
			
			addMensagemInformation(" Assinatura renovada com sucesso no per�odo de: "+periodoRenovacao);
			
			return cancelar();
			
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return telaRenovaAsssinatura();
		}
		
	}
	
	
	
	
	
	
	
	/**
	 *    Volta para a tela de busca de assinaturas
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/paginaRenovaAssinatura.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String voltar() throws DAOException{
		
		AssinaturaPeriodicoMBean bean =  getMBean("assinaturaPeriodicoMBean");
		return bean.telaBuscaAssinaturas();
	}
	
	
	
	
	
	
	
	
	
	//////////////////   Tela de navega��o   ////////////////////////
	
	/**
	 * Redireciona para a p�gina de renova��o da assinatura.
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 */
	public String telaRenovaAsssinatura(){
		return forward(PAGINA_RENOVA_ASSINATURA);
	}

	/////////////////////////////////////////////////////////////////
	
	
	


	public List<RenovacaoAssinatura> getRenovacoes() {
		return renovacoes;
	}


	public Assinatura getAssinaturaSelecionada() {
		return assinaturaSelecionada;
	}


	public void setAssinaturaSelecionada(Assinatura assinaturaSelecionada) {
		this.assinaturaSelecionada = assinaturaSelecionada;
	}



	public void setRenovacoes(List<RenovacaoAssinatura> renovacoes) {
		this.renovacoes = renovacoes;
	}
	
	
}
