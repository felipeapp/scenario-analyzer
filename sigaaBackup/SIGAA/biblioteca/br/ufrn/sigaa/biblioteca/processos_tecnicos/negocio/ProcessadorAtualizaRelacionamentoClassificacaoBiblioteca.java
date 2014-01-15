/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Processador que contém as regras de negócio para atualizar o relacionamento entres as classificações bibliográficas e quais bibliotecas as utilizam </p>
 *
 * <p> <i> Esse relacionaemento é utilizado no momento da inclusão de um material no acervo para não deixar materiais sem classificação. </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorAtualizaRelacionamentoClassificacaoBiblioteca extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
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
					// atribui manualmente o id ou vai ficar com 2 objetos iguais na sessão e dá erro
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
	 * Ver comentários da classe pai.<br/>
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
	
		// Só administradores gerais podem alterar a classificação utilizada por uma biblioteca //
		checkRole(new int[]{SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}, mov);
		
		checkValidation(erros);
		
	}

}
