/*
 * ProcessadorAssociaAssinaturaTransferenciaFasciculos.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 03/02/2010
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.aquisicao.negocio.MovimentoCadastraAlteraAssinaturaDePeriodico;
import br.ufrn.sigaa.biblioteca.aquisicao.negocio.ProcessadorCadastraAlteraAssinaturaDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RegistroMovimentacaoMaterialInformacional;

/**
 *
 *  <p>Processador que cria a assinatura de peri�dico dos fasc�culos pendentes de transfer�ncia que 
 * estavam sem assinatura. </p>
 * <p> Associa essa assinatura criada a todos so registros de transfer�ncia
 * que n�o possuem assiantura ainda do mesmo t�tulo para da mesma biblioteca destino dos fasc�culos da transfer�ncia. </p>
 *
 * @author jadson
 * @since 03/02/2010
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorAssociaAssinaturaTransferenciaFasciculos extends AbstractProcessador{

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoAssociaAssinaturaTransferenciaFasciculos movimento 
			= (MovimentoAssociaAssinaturaTransferenciaFasciculos) mov;
		
		validate(movimento);
		
		// A assinatura que vai ser associada os registros de movimenta��o //
		Assinatura assinatura = null;
		
		AssinaturaDao dao = null;
		
		try{
			dao = getDAO(AssinaturaDao.class, mov);
		
		
			if(movimento.isCriarNovaAssinatura()){
			
				// Chama o processador que cria a assinatura ///
				
				ProcessadorCadastraAlteraAssinaturaDePeriodico processador = new ProcessadorCadastraAlteraAssinaturaDePeriodico();
				
				MovimentoCadastraAlteraAssinaturaDePeriodico movimentoAuxiliar 
					= new MovimentoCadastraAlteraAssinaturaDePeriodico(movimento.getAssinatura(), false, movimento.isGerarCodigoAssinatuaCompra());
				movimentoAuxiliar.setCodMovimento(SigaaListaComando.CADASTRA_ALTERA_ASSINATURAS_DE_PERIODICO);
				movimentoAuxiliar.setUsuarioLogado(movimento.getUsuarioLogado());
				movimentoAuxiliar.setSistema(movimento.getSistema());
				
				String codigoAssinatuara = (String) processador.execute(movimentoAuxiliar);
				
				assinatura = dao.findByExactField(Assinatura.class, "codigo", codigoAssinatuara, true);
			}else{
				
				assinatura = dao.refresh(new Assinatura (movimento.getIdAssinaturaAssociacao()));
				
			}
			
			
			
			
			/* ***** Para cada registro pendente sem assinatura da biblioteca destino do mesmo t�tulo do fasc�culos **** */
			
			List<RegistroMovimentacaoMaterialInformacional>  registros 
				=  dao.encontraRegistrosMovimentacaoFasciculosSemAssinatura(movimento.getIdBibliotecaDestino(), movimento.getIdTituloFasciculos());
			
			for (RegistroMovimentacaoMaterialInformacional reg : registros) {	
				reg.setAssinturaDestino(assinatura);    /* *** Adiciona a assintura destino recem criada pele bibliotec�rio *** */
				dao.update(assinatura);
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return assinatura.getCodigo();
	}

	
	
	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoAssociaAssinaturaTransferenciaFasciculos movimento 
			= (MovimentoAssociaAssinaturaTransferenciaFasciculos) mov;
		
		/* Verifica se a assinatuar que o biblioteca escolheu para criar ou associar possua � da mesma         //
		 * biblioteca para onde as transfer�ncias foram solicitadas, sen�o n�o faz sentido criar e/ou associar a assinatura 
		 * aos registros de movimenta��o.
		 */
		if(movimento.getAssinatura().getUnidadeDestino().getId() != movimento.getIdBibliotecaDestino()){
			
			if(movimento.isCriarNovaAssinatura())
				lista.addErro(" A assinatura que vai ser criada precisa ser da mesma biblioteca para onde a transfer�ncia foi solicitada. ");
			else
				lista.addErro(" A assinatura que vai ser associada precisa ser da mesma biblioteca para onde a transfer�ncia foi solicitada. ");
		}
		
		checkValidation(lista);
		
	}

}
