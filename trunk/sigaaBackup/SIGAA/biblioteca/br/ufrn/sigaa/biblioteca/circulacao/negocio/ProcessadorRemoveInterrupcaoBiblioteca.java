/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 14/04/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.InterrupcaoBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InterrupcaoBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 *
 *  <p>Processador de desativa uma interrup��o cadastrada por engano no sistema. </p>
 *
 *  <p> <strong>Somente interrup��es que n�o geraram prorroga��es nos prazos dos empr�timos poderam ser desativas </strong> </p>
 *
 *  <p> 
 *    <i>Como as interrup��es mexem nos prazos dos empr�timos fica muito dif�cil, em alguns casos, calcular para que 
 *     data o prazo do empr�stimo deve voltar.<br/> Al�m do que, como � enviado um email para os usu�rios avisando do novo prazo dos 
 *     empr�stimos, teria que enviar um novo email informando que o prazo foi cancelado, o usu�rio pode alegar que n�o viu o 
 *     aviso de cancelamento, etc.. 
 *    </i> 
 *  </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorRemoveInterrupcaoBiblioteca extends AbstractProcessador{

	
	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov); 
		
		MovimentoCadastro movi = (MovimentoCadastro) mov;
		
		InterrupcaoBiblioteca inter = movi.getObjMovimentado();
		
		Biblioteca biblioteca =  (Biblioteca) movi.getObjAuxiliar();
		
		InterrupcaoBibliotecaDao dao = null;
		
		try{
			dao = getDAO(InterrupcaoBibliotecaDao.class, movi);
	
			inter = dao.refresh(inter);
			
			inter.getBibliotecas().remove( biblioteca );
			
			dao.update(inter);  // remover a liga��o com a biblioteca do banco.
			
			if(inter.getBibliotecas().size() == 0 ){ // Se removeu para todas as bibliotecas do sistema.
				
				dao.updateField(InterrupcaoBiblioteca.class, inter.getId(), "ativo", false); // desativa a interrup��o
			}
			
		}finally{
			if (dao != null) dao.close();
		}
				
		return null;
	}

	
	
	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movi = (MovimentoCadastro) mov;
		
		InterrupcaoBiblioteca inter = movi.getObjMovimentado();
	
		Biblioteca biblioteca =  (Biblioteca) movi.getObjAuxiliar();
		
		InterrupcaoBibliotecaDao dao = null;
		
		int quantidadeProrrocacoes = 1;
		
		try{
			
			dao = getDAO(InterrupcaoBibliotecaDao.class, movi);
		
			quantidadeProrrocacoes = dao.countAllProrrogacoesGeradasPelaInterrupcaoByBiblioteca(inter, biblioteca.getId());
		
			
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
				biblioteca =  dao.refresh(biblioteca);
				
				try{
					checkRole(biblioteca.getUnidade(), movi, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
				}catch (SegurancaException se) {
					throw new NegocioException("O usu�rio(a): "+ mov.getUsuarioLogado().getNome()
								+ " n�o tem permiss�o para remover interrup��es da biblioteca: "+biblioteca.getDescricao());
				}
			
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		if(quantidadeProrrocacoes > 0)
			throw new NegocioException(" A interrup��o n�o p�de ser removida, pois ela gerou prorroga��es nos prazos dos empr�stimos");
	}

}
