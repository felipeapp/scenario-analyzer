/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 30/01/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.aquisicao.jsf;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.aquisicao.negocio.MovimentoRemoveAssinaturaPeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;

/**
 *
 * <p>Gerencia a parte de removação das assinaturas de periódicos </p>
 * 
 * @author jadson
 *
 * @version 1.5 31/01/2012 alterando para utilizar a interface de busca de assinaturas em vez de chamar diretamente a busca.
 *
 */
@Component("removeAssinaturaPeriodicosMBean") 
@Scope("request")
public class RemoveAssinaturaPeriodicosMBean extends SigaaAbstractController<Assinatura> implements PesquisarAcervoAssinaturas{

	/** Página de confirmação da remoção de uma assinatura. */
	public static final String PAGINA_CONFIRMA_REMOCAO_ASSINATURA = "/biblioteca/aquisicao/paginaConfirmaRemocaoAssinatura.jsp";
	
	
	/**
	 *    Inicia a busca para selecionar a assinatura que vai ser removida do sistema, esses são os
	 * casos de assinaturas criadas por engano.<br/><br/>
	 * 
	 *     Chamado a partir da página: /sigaa.war/biblioteca/menu/aquisicoes.jsp
	 */
	public String iniciarRemocaoAssinatura() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		obj = new Assinatura();
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		return bean.iniciarPesquisaSelecionarAssinatura(this);
	}
	
	
	//////////////   Métodos da interface de busca de assinaturas  //////////////
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <br><br>
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#setAssinatura(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura)
	 */
	@Override
	public void setAssinatura(Assinatura assinatura) throws ArqException {
		this.obj = assinatura;
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <br><br>
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#selecionaAssinatura()
	 */
	@Override
	public String selecionaAssinatura() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.REMOVE_ASSINATURAS_DE_PERIODICO);
		
		obj = getGenericDAO().refresh(new Assinatura(obj.getId()));
		
		if(obj.getRegistroCriacao() != null && obj.getDataCriacao() != null)
			obj.setNomeCriador(obj.getRegistroCriacao().getUsuario().getNome()+" em: "+new SimpleDateFormat("dd/MM/yyyy").format( obj.getDataCriacao()));
		else
			obj.setNomeCriador(" Sem registros de criação ");
		
		return telaConfirmaRemocaoAssinatura();
	}

	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#voltarBuscaAssinatura()
	 */
	@Override
	public String voltarBuscaAssinatura() throws ArqException {
		return null;
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#isUtilizaVoltarBuscaAssinatura()
	 */
	
	@Override
	public boolean isUtilizaVoltarBuscaAssinatura() {
		return false;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	/**
	 *  Remove uma assinatura de periódicos do sistema
	 *  <br/><br/>
	 *  Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/paginaConfirmaRemocaoAssinatura.jsp
	 */
	public String removeAssinatura() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
			
		MovimentoRemoveAssinaturaPeriodico mov = new MovimentoRemoveAssinaturaPeriodico(obj);
		
		mov.setCodMovimento(SigaaListaComando.REMOVE_ASSINATURAS_DE_PERIODICO);
		
		String codigo = obj.getCodigo();
		
		try {
			
			execute(mov);
			
			addMensagemInformation("Assinatura "+codigo+" removida com sucesso.");
			
			return cancelar();
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	
	
	
	/**
	 * Redireciona para a página que o usuário confirma a remoção.
	 * <br/><br/>
	 * Método invocado de nenhuma página jsp
	 */
	public String telaConfirmaRemocaoAssinatura(){
		return forward(PAGINA_CONFIRMA_REMOCAO_ASSINATURA);
	}
	
	
}
