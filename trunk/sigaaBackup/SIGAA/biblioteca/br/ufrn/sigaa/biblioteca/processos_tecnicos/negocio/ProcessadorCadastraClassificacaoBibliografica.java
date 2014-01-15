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

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;

/**
 *
 *   Processador que cadastra as classificações bibliográficas do sistema
 *
 * @author felipe
 * @since 15/02/2012
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorCadastraClassificacaoBibliografica extends AbstractProcessador{

	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, mov);
		
		GenericDAO dao = null;
		
		validate(mov);
		
		try {
			dao = getGenericDAO(mov);
			
			MovimentoCadastraClassificacaoBibliografica movimento = (MovimentoCadastraClassificacaoBibliografica) mov;

			ClassificacaoBibliografica obj = movimento.getClassificacaoBibliografica();
			
			if (obj.getId() == 0) {
				obj.setId(dao.getNextSeq("biblioteca", "hibernate_sequence"));
			}
			
			dao.create(obj);
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
		
		MovimentoCadastraClassificacaoBibliografica movimento = (MovimentoCadastraClassificacaoBibliografica) mov;
		
		GenericDAO dao = null;
		
		ListaMensagens lista = new ListaMensagens();
		
		try {
			dao = getGenericDAO(movimento);
			
			ClassificacaoBibliografica obj = movimento.getClassificacaoBibliografica();
			
			lista.addAll(obj.validate());
			
			ClassificacaoBibliografica c = dao.findByExactField(ClassificacaoBibliografica.class, "descricao", obj.getDescricao(), true);
			
			if (c != null && c.isAtiva()){
				lista.addErro("Já existe uma classificação com este nome.");
			}
			
			c = dao.findByExactField(ClassificacaoBibliografica.class, "ordem", obj.getOrdem().getValor(), true);

			if (c != null && c.isAtiva()){
				lista.addErro("Já existe uma classificação com esta ordem.");
			}
			
			c = dao.findByExactField(ClassificacaoBibliografica.class, "campoMARC", obj.getCampoMARC().getValor(), true);

			if (c != null && c.isAtiva()){
				lista.addErro("Já existe uma classificação com este campo MARC.");
			}	
		} finally {			
			if(dao != null) dao.close();

			checkValidation(lista);			
		}
		
	}

}
