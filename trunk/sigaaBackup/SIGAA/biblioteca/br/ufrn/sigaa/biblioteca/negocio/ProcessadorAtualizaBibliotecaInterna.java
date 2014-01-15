/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 07/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.negocio;

import java.rmi.RemoteException;
import java.util.Iterator;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ServicosEmprestimosBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 *
 * <p> Processador utilizado para o alteração das bibliotecas internas do sistema, ja que necessita atualizar 
 * mais de um objeto de maneira transacional.</p>
 * 
 * <p><i> Ao atualiza a biblioteca, precisa atualizar as informações dos seus serviços que ficam em outros objetos. </i>/<p>
 * 
 * @author jadson
 *
 */
public class ProcessadorAtualizaBibliotecaInterna extends AbstractProcessador{

	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		validate(movimento);
		
		MovimentoAtualizaBibliotecaInterna mov = (MovimentoAtualizaBibliotecaInterna) movimento;
		Biblioteca biblioteca = mov.getBiblioteca();
		
		BibliotecaDao dao = null;
		
		try{
			dao = getDAO(BibliotecaDao.class, mov);
			
			dao.update(biblioteca); // atualiza a biblioteca
			
			if(mov.getServicosAosUsuarios() != null)
				dao.update(mov.getServicosAosUsuarios()); // atualiza os dados do serviço ao usuário 
			
			// atualiza os dados do serviço de empréstimos //
			for (ServicosEmprestimosBiblioteca servico : mov.getServicosDeEmprestimos()) {
				
				if(! servico.isEmprestimoInstitucionalExterno() && ! servico.isEmprestimoInstitucionalInterno()){
					
					Iterator<TipoEmprestimo> iterator = servico.getTiposEmprestimos().iterator();
					while ( iterator.hasNext()){
						TipoEmprestimo tipo = iterator.next();
						if(! tipo.isSelecionado())
							iterator.remove();               // Se o usuário não selecionou o tipo de empréstimo, remove da lista
						
					}
				}
				dao.update(servico);
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
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		
		MovimentoAtualizaBibliotecaInterna mov = (MovimentoAtualizaBibliotecaInterna) movimento;
		
		ListaMensagens erros = new ListaMensagens();
		
		erros.addAll(mov.getBiblioteca().validate());
		for (ServicosEmprestimosBiblioteca servico : mov.getServicosDeEmprestimos()) {
			erros.addAll(servico.validate());
		}
		checkValidation(erros);
	}
	
}
