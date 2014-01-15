/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/02/2012
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
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;

/**
 *
 *   Processador que reabre os inventários do acervo as bibliotecas do sistema
 *
 * @author felipe
 * @since 14/03/2012
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorAbreInventarioAcervoBiblioteca extends AbstractProcessador{

	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		GenericDAO dao = null;
		
		validate(mov);
		
		try {
			dao = getGenericDAO(mov);
			
			MovimentoCadastro movimento = (MovimentoCadastro) mov;

			InventarioAcervoBiblioteca obj = (InventarioAcervoBiblioteca) movimento.getObjMovimentado();
			
			dao.updateFields(InventarioAcervoBiblioteca.class, obj.getId(), new String[] { "aberto", "dataFechamento" }, new Object[] { true, null });
		} finally {
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		GenericDAO dao = null;
		
		ListaMensagens lista = new ListaMensagens();
		
		try {
			dao = getGenericDAO(movimento);

			InventarioAcervoBiblioteca obj = dao.refresh((InventarioAcervoBiblioteca) movimento.getObjMovimentado());
			
			List<InventarioAcervoBiblioteca> inventariosAbertos = (List<InventarioAcervoBiblioteca>) dao.findByExactField(InventarioAcervoBiblioteca.class, new String[] { "biblioteca", "aberto" }, new Object[] { (Integer) obj.getBiblioteca().getId(), (Boolean) true });
			
			if (obj.isAberto()) {
				lista.addErro("Este inventário já se encontra aberto.");
			}

			if (inventariosAbertos.size() > 0 && !inventariosAbertos.get(0).equals(obj)) {
				lista.addErro("Já existe um inventário aberto para essa biblioteca.");
			}
			
			
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
				Biblioteca bibliotecaBanco = dao.findByPrimaryKey(obj.getBiblioteca().getId(), Biblioteca.class, "unidade.id", "descricao");
				
				try{
					checkRole(bibliotecaBanco.getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
				}catch (SegurancaException se) {
					lista.addErro("O usuário(a): "+ mov.getUsuarioLogado().getNome()
								+ " não tem permissão para fechar inventários da biblioteca "
								+bibliotecaBanco.getDescricao());
				}
			
				dao.detach(bibliotecaBanco);
				
			}
			
		} finally {			
			if(dao != null) dao.close();

			checkValidation(lista);			
		}
		
	}

}
