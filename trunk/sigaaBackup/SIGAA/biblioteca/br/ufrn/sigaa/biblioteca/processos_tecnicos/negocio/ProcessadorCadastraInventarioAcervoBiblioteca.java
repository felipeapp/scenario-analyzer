/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.InventarioAcervoBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;

/**
 *
 *   Processador que cadastra os invent�rios do acervo das bibliotecas do sistema
 *
 * @author felipe
 * @since 09/03/2012
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorCadastraInventarioAcervoBiblioteca extends AbstractProcessador{

	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		GenericDAO dao = null;
		
		validate(mov);
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;

		InventarioAcervoBiblioteca obj = (InventarioAcervoBiblioteca) movimento.getObjMovimentado();
		
		try {
			dao = getGenericDAO(mov);
			
			dao.createOrUpdate(obj);
		} finally {
			if(dao != null) dao.close();
		}
		
		return obj;
	}

	
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		InventarioAcervoBibliotecaDao dao = null;
		
		ListaMensagens lista = new ListaMensagens();
		
		try {
			dao = getDAO(InventarioAcervoBibliotecaDao.class, movimento);

			InventarioAcervoBiblioteca obj = (InventarioAcervoBiblioteca) movimento.getObjMovimentado();
			
			List<InventarioAcervoBiblioteca> inventariosAbertos = (List<InventarioAcervoBiblioteca>) dao.findByExactField(InventarioAcervoBiblioteca.class, new String[] { "biblioteca", "aberto" }, new Object[] { (Integer) obj.getBiblioteca().getId(), (Boolean) true });
			List<InventarioAcervoBiblioteca> inventariosComDescricao = (List<InventarioAcervoBiblioteca>) dao.findByExactField(InventarioAcervoBiblioteca.class, new String[] { "biblioteca", "ano", "descricao" }, new Object[] { (Integer) obj.getBiblioteca().getId(), obj.getAno(), obj.getDescricao() });

			boolean isAberto = obj.isAberto();
			
			if(obj.getId() > 0){
				
				InventarioAcervoBiblioteca inventarioBanco = dao.findByPrimaryKey(obj.getId(), InventarioAcervoBiblioteca.class);
			
				isAberto = inventarioBanco.isAberto();
				
				Long quantidadeRegistros = dao.countMateriaisRegistradosInventario(obj.getId());
				
				// N�o pode alterar a biblioteca nem a cole��o do invent�rio caso algu�m j� tenha feito algum registro para o invent�rio.
				if(quantidadeRegistros > 0){ 
				
					if(obj.getBiblioteca().getId() != inventarioBanco.getBiblioteca().getId()){
						lista.addErro("N�o � poss�vel alterar a biblioteca do invent�rio, pois j� foram feitos registro para ele.");
					}
					
					if( ( obj.getColecao() == null && inventarioBanco.getColecao() != null ) 
						||	( obj.getColecao() != null && inventarioBanco.getColecao() == null )
						||	( obj.getColecao() != null && inventarioBanco.getColecao() != null && obj.getColecao().getId() != inventarioBanco.getColecao().getId() )
							){
						lista.addErro("N�o � poss�vel alterar a cole��o do invent�rio, pois j� foram feitos registro para ele.");
					}
				}
				
				dao.detach(inventarioBanco);
			}
			
			

			if (isAberto && inventariosAbertos.size() > 0 && !inventariosAbertos.get(0).equals(obj)) {
				lista.addErro("J� existe um invent�rio aberto para essa biblioteca.");
			}
			
			if (inventariosComDescricao.size() > 0 && !inventariosComDescricao.get(0).equals(obj)) {
				lista.addErro("J� existe um invent�rio com essa descri��o para essa biblioteca e ano.");
			}
			
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
				Biblioteca bibliotecaBanco = dao.findByPrimaryKey(obj.getBiblioteca().getId(), Biblioteca.class, "unidade.id", "descricao");
				
				try{
					checkRole(bibliotecaBanco.getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
				}catch (SegurancaException se) {
					lista.addErro("O usu�rio(a): "+ mov.getUsuarioLogado().getNome()
								+ " n�o tem permiss�o para alterar invent�rios da biblioteca "
								+bibliotecaBanco.getDescricao());
				}
			
				dao.detach(bibliotecaBanco);
				
			}
			
			lista.addAll(obj.validate());
			
		} finally {			
			if(dao != null) dao.close();

			checkValidation(lista);			
		}
		
	}

}
