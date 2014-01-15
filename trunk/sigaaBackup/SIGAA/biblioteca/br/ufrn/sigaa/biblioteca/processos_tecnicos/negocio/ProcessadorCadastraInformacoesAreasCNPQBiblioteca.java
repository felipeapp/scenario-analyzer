/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/02/2012
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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InformacoesAreaCNPQBiblioteca;

/**
 * <p> Processador que atualiza as informações da áreas CNPq utilizadas na biblioteca. </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorCadastraInformacoesAreasCNPQBiblioteca extends AbstractProcessador{

	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {

		validate(mov);
		
		MovimentoCadastro m = (MovimentoCadastro) mov;
		
		@SuppressWarnings("unchecked")
		List<InformacoesAreaCNPQBiblioteca> lista = (List<InformacoesAreaCNPQBiblioteca>) m.getColObjMovimentado();
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(mov);
		
			for (InformacoesAreaCNPQBiblioteca inforArea : lista) {
				if(inforArea.getId() <= 0){
					// atribui manualmente o id ou vai ficar com 2 objetos iguais na sessão e dá erro
					inforArea.setId(dao.getNextSeq("biblioteca", "hibernate_sequence")); 
					dao.create(inforArea);
				}else{
					dao.update(inforArea);
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
		List<InformacoesAreaCNPQBiblioteca> lista  = (List<InformacoesAreaCNPQBiblioteca>) m.getColObjMovimentado();
		
		for (InformacoesAreaCNPQBiblioteca inforArea : lista) {
			erros.addAll(inforArea.validate());	
		}
	
		// Só administradores gerais podem alterar a classificação utilizada por uma biblioteca //
		checkRole(new int[]{SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}, mov);
		
		checkValidation(erros);
		
		
	}

}
