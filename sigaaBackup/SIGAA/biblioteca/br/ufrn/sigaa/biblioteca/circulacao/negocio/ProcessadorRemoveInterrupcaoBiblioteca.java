/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 *  <p>Processador de desativa uma interrupção cadastrada por engano no sistema. </p>
 *
 *  <p> <strong>Somente interrupções que não geraram prorrogações nos prazos dos emprétimos poderam ser desativas </strong> </p>
 *
 *  <p> 
 *    <i>Como as interrupções mexem nos prazos dos emprétimos fica muito difícil, em alguns casos, calcular para que 
 *     data o prazo do empréstimo deve voltar.<br/> Além do que, como é enviado um email para os usuários avisando do novo prazo dos 
 *     empréstimos, teria que enviar um novo email informando que o prazo foi cancelado, o usuário pode alegar que não viu o 
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
			
			dao.update(inter);  // remover a ligação com a biblioteca do banco.
			
			if(inter.getBibliotecas().size() == 0 ){ // Se removeu para todas as bibliotecas do sistema.
				
				dao.updateField(InterrupcaoBiblioteca.class, inter.getId(), "ativo", false); // desativa a interrupção
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
					throw new NegocioException("O usuário(a): "+ mov.getUsuarioLogado().getNome()
								+ " não tem permissão para remover interrupções da biblioteca: "+biblioteca.getDescricao());
				}
			
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		if(quantidadeProrrocacoes > 0)
			throw new NegocioException(" A interrupção não pôde ser removida, pois ela gerou prorrogações nos prazos dos empréstimos");
	}

}
