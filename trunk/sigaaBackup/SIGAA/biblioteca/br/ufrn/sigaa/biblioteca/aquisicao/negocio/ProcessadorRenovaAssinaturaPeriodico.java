/*
 * ProcessadorRenovaAssinaturaPeriodico.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
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
 *   Classe que contém as regras da renovação de uma assinatura de periódicos para o sistema de 
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
			
			
			
			// renovação é um histórioco das dadas da assinatura, então é criada uma renovação 
			// com a data antiga da assinatura
			renovacao.setAssinatura(assinatura);
			renovacao.setDataInicial(assinatura.getDataInicioAssinatura());
			renovacao.setDataFinal(assinatura.getDataTerminoAssinatura());
			
			// e a assinatura é atualizada com o novo período
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
		
		
		
		// Só aquisição pode criar assinaturas de compra
		if(m.getAssinatura().isAssinaturaDeCompra() && ! m.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			listaErros.addErro("Usuário: "+m.getUsuarioLogado().getNome()+" não tem permissão para renovar uma assinatura com a modalidade de aquisição de compras ");
		}
		
		try{
			// Senão é administrador geral, checa a unidade da criação da assinatura.
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				checkRole(m.getAssinatura().getUnidadeDestino().getUnidade(), m, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
			}
		
		}catch(SegurancaException se){
			listaErros.addErro("Usuário: "+m.getUsuarioLogado().getNome()+" não tem permissão para renovar uma assinatura para a biblioteca: "+m.getAssinatura().getUnidadeDestino().getDescricao());
		}
		
		checkValidation(listaErros);
		
	}

}
