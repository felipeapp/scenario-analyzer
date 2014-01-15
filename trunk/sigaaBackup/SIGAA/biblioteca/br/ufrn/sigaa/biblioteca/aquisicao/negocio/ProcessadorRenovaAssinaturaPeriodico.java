/*
 * ProcessadorRenovaAssinaturaPeriodico.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RenovacaoAssinatura;

/**
 *
 *   Classe que cont�m as regras da renova��o de uma assinatura de peri�dicos para o sistema de 
 * bibliotescas.
 *
 * @author jadson
 * @since 16/11/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorRenovaAssinaturaPeriodico extends AbstractProcessador{

	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		validate(mov);
		
		MovimentoRenovaAssinaturaPeriodico m = (MovimentoRenovaAssinaturaPeriodico) mov;
		
		GenericDAO dao = null;
		
		RenovacaoAssinatura renovacao = m.getRenovacao();
		Assinatura assinatura = m.getAssinatura();
		
		Date dataInicio = renovacao.getDataInicial();
		Date dataFinal = renovacao.getDataFinal();
		
		try{
			
			dao = getGenericDAO(m);	
			
			
			
			// renova��o � um hist�rioco das dadas da assinatura, ent�o � criada uma renova��o 
			// com a data antiga da assinatura
			renovacao.setAssinatura(assinatura);
			renovacao.setDataInicial(assinatura.getDataInicioAssinatura());
			renovacao.setDataFinal(assinatura.getDataTerminoAssinatura());
			
			// e a assinatura � atualizada com o novo per�odo
			assinatura.setDataInicioAssinatura(dataInicio);
			assinatura.setDataTerminoAssinatura(dataFinal);
			
			dao.update(assinatura);
			dao.create(renovacao);
			
		}finally{	
			if(dao != null) dao.close();
		}	
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		return sdf.format(dataInicio) +" a "+sdf.format(dataFinal);
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens listaErros = new ListaMensagens();
		
		MovimentoRenovaAssinaturaPeriodico m = (MovimentoRenovaAssinaturaPeriodico) mov;
		
		listaErros =  m.getRenovacao().validate();
		
		
		
		// S� aquisi��o pode criar assinaturas de compra
		if(m.getAssinatura().isAssinaturaDeCompra() && ! m.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			listaErros.addErro("Usu�rio: "+m.getUsuarioLogado().getNome()+" n�o tem permiss�o para renovar uma assinatura com a modalidade de aquisi��o de compras ");
		}
		
		try{
			// Sen�o � administrador geral, checa a unidade da cria��o da assinatura.
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				checkRole(m.getAssinatura().getUnidadeDestino().getUnidade(), m, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
			}
		
		}catch(SegurancaException se){
			listaErros.addErro("Usu�rio: "+m.getUsuarioLogado().getNome()+" n�o tem permiss�o para renovar uma assinatura para a biblioteca: "+m.getAssinatura().getUnidadeDestino().getDescricao());
		}
		
		checkValidation(listaErros);
		
	}

}
