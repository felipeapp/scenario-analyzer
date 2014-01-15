/*
 * ProcessadorRemoveAssinaturaPeriodico.java
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

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;

/**
 *   Processador que implementa as regras de neg�cio para remover assinaturas.
 *
 * @author jadson
 * @since 19/11/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorRemoveAssinaturaPeriodico extends AbstractProcessador{

	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		GenericDAO dao = null;
		
		validate(mov);
		
		MovimentoRemoveAssinaturaPeriodico m = (MovimentoRemoveAssinaturaPeriodico) mov;
		
		Assinatura assinatura = m.getAssinatura();
		
		try{
			
			dao = getGenericDAO(m);		

			assinatura.setAtiva(false);
			
			dao.update(assinatura);
			
		}finally{	
			if(dao != null) dao.close();
		}	
		
		return null;
	}

	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens listaErros = new ListaMensagens();
		
		FasciculoDao dao = null;
		
		MovimentoRemoveAssinaturaPeriodico m = (MovimentoRemoveAssinaturaPeriodico) mov;
		
		Assinatura assinatura = m.getAssinatura();
		
		try{
			
			// S� aquisi��o pode remover assinaturas de compra
			if(m.getAssinatura().isAssinaturaDeCompra() && ! m.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				listaErros.addErro("Usu�rio: "+m.getUsuarioLogado().getNome()+" n�o tem permiss�o para remover uma assinatura com a modalidade de aquisi��o de compra ");
			}
			
			try{
				// Sen�o � administrador geral, checa a unidade da cria��o da assinatura.
				if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					checkRole(m.getAssinatura().getUnidadeDestino().getUnidade(), m, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
				}
			
			}catch(SegurancaException se){
				listaErros.addErro("Usu�rio: "+m.getUsuarioLogado().getNome()+" n�o tem permiss�o para remover uma assinatura para a biblioteca: "+m.getAssinatura().getUnidadeDestino().getDescricao());
			}
			
			dao = getDAO(FasciculoDao.class, m);		

			long quantidadeFasciculos = dao.countFasciculosAtivosNoAcervoOURegistradosDaAssinatura(assinatura.getId());
			
			if(quantidadeFasciculos > 0)
				listaErros.addErro("A assinatura n�o p�de ser removida porque ela possui fasc�culos ativos no sistema, primeiro remova todos os fasc�culos da assinatura. ");
			
		}finally{	
			if(dao != null) dao.close();
			checkValidation(listaErros);
		}	
		
	}

}
