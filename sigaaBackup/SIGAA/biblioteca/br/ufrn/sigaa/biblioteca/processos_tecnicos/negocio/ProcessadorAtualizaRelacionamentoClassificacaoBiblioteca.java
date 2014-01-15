/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 14/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RelacionaClassificacaoBibliograficaBiblioteca;

/**
 *
 * <p>Processador que cont�m as regras de neg�cio para atualizar o relacionamento entres as classifica��es bibliogr�ficas e quais bibliotecas as utilizam </p>
 *
 * <p> <i> Esse relacionaemento � utilizado no momento da inclus�o de um material no acervo para n�o deixar materiais sem classifica��o. </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorAtualizaRelacionamentoClassificacaoBiblioteca extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoCadastro m = (MovimentoCadastro) mov;
		
		@SuppressWarnings("unchecked")
		List<RelacionaClassificacaoBibliograficaBiblioteca> lista 
				= (List<RelacionaClassificacaoBibliograficaBiblioteca>) m.getColObjMovimentado();
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(mov);
		
			for (RelacionaClassificacaoBibliograficaBiblioteca relacionamento : lista) {
				if(relacionamento.getId() <= 0){
					// atribui manualmente o id ou vai ficar com 2 objetos iguais na sess�o e d� erro
					relacionamento.setId(dao.getNextSeq("biblioteca", "hibernate_sequence")); 
					dao.create(relacionamento);
				}else{
					dao.update(relacionamento);
				}
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}
	

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro m = (MovimentoCadastro) mov;
		
		ListaMensagens erros = new ListaMensagens();
		
		@SuppressWarnings("unchecked")
		List<RelacionaClassificacaoBibliograficaBiblioteca> lista 
				= (List<RelacionaClassificacaoBibliograficaBiblioteca>) m.getColObjMovimentado();
		
		for (RelacionaClassificacaoBibliograficaBiblioteca relacionamento : lista) {
			erros.addAll(relacionamento.validate());	
		}
	
		// S� administradores gerais podem alterar a classifica��o utilizada por uma biblioteca //
		checkRole(new int[]{SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}, mov);
		
		checkValidation(erros);
		
	}

}
