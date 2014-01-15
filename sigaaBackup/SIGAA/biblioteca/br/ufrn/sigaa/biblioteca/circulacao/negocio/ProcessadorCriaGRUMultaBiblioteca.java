/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.util.MultaUsuarioBibliotecaUtil;

/**
 *
 * <p> Classe que cria a GRU para multas para biblioteca e no momento da criação associa essa GRU gerada a
 * multa correspondente</p>
 * 
 * @author jadson
 *
 */
public class ProcessadorCriaGRUMultaBiblioteca extends ProcessadorCadastro{

	/**
	 * Ver comentário na classe pai
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException,ArqException, RemoteException {
		
		validate(movimento);
		
		MovimentoCriaGRUMultaBiblioteca movi = (MovimentoCriaGRUMultaBiblioteca) movimento;
		
		if(movi.isEmitindoGRUVariasMultas()){
			return emiteGRUVariasMultas(movi);
		}else{
			return emiteGRUMultaSimples(movi);
		}
		
		
	}

		
	/**
	 * Gera e salva as informções da GRU para uma única multa da biblioteca
	 *
	 * @MultaUsuarioBiblioteca
	 */
	private MultaUsuarioBiblioteca emiteGRUMultaSimples(MovimentoCriaGRUMultaBiblioteca movimento) throws DAOException, NegocioException {

		MultaUsuarioBiblioteca multaASerPaga =  movimento.getMulta();
		
		// Cria a gru
		GuiaRecolhimentoUniao gru = MultaUsuarioBibliotecaUtil.montaInformacoesGRUMultaBiblioteca(movimento.getMulta());
		
		// Atribui a multa passada e atualiza a multa para contém a gru salva //
		
		multaASerPaga.setIdGRUQuitacao( gru.getId());
		multaASerPaga.setNumeroReferencia( gru.getNumeroReferenciaNossoNumero()); // guarda duplicado para não precisar busca no banco comum
		
		GenericDAO dao = null;
		
		try{
		
			dao = getGenericDAO(movimento);
		
			dao.updateFields(MultaUsuarioBiblioteca.class, multaASerPaga.getId(), new String[]{"idGRUQuitacao", "numeroReferencia"}
				, new Object[]{multaASerPaga.getIdGRUQuitacao(), multaASerPaga.getNumeroReferencia()});
			
		}finally{
			if(dao != null)  dao.close();
		}
				
		return multaASerPaga;
		
	}


	/**
	 * Gera e salva as informções da GRU para várias multas da biblioteca
	 * 
	 * @throws DAOException 
	 * @throws NegocioException 
	 *
	 * @void
	 */
	private List<MultaUsuarioBiblioteca> emiteGRUVariasMultas(MovimentoCriaGRUMultaBiblioteca movimento) throws DAOException, NegocioException {
		
		List<MultaUsuarioBiblioteca> multasASeremPagas =  movimento.getMultas();
		
		// Cria a gru
		GuiaRecolhimentoUniao gru = MultaUsuarioBibliotecaUtil.montaInformacoesGRUMultasBibliotecas(multasASeremPagas);
		
		// Atribui a multa passada e atualiza a multa para contém a gru salva //
		
		GenericDAO dao = null;
		
		try{
			
			dao = getGenericDAO(movimento);
			
			for (MultaUsuarioBiblioteca multaASerPaga : multasASeremPagas) {
				
				multaASerPaga.setIdGRUQuitacao( gru.getId());
				multaASerPaga.setNumeroReferencia( gru.getNumeroReferenciaNossoNumero()); // guarda duplicado para não precisar busca no banco comum
				
				dao.updateFields(MultaUsuarioBiblioteca.class, multaASerPaga.getId(), new String[]{"idGRUQuitacao", "numeroReferencia"}
						, new Object[]{multaASerPaga.getIdGRUQuitacao(), multaASerPaga.getNumeroReferencia()});
					
			}
		
		}finally{
			if(dao != null)  dao.close();
		}
				
		return multasASeremPagas;
		
	}

	
	/**
	 * Ver comentário na classe pai
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCriaGRUMultaBiblioteca movi = (MovimentoCriaGRUMultaBiblioteca) mov;
		
		if(movi.isEmitindoGRUVariasMultas()){
			
			if(movi.getMultas() == null || movi.getMultas().size() <= 0){
				erros.addErro("Selecione pelo menos uma multa para emitir a GRU.");
			}
			
			Biblioteca bibliotecaRecolhimento = null;
			
			//
			// Verifica se a GRU não foi emitida ainda se todas tem unidade de recolhimento //
			//
			for (MultaUsuarioBiblioteca multa : movi.getMultas()){
			
				if( multa.getIdGRUQuitacao() != null){
					erros.addErro("A GRU para a multa de valor "+multa.getValorFormatado()+" já foi gerada.");
				}
					
				if ( multa.isManual() ){
					if(bibliotecaRecolhimento == null)
						bibliotecaRecolhimento = multa.getBibliotecaRecolhimento();
					else{
						if( ! bibliotecaRecolhimento.equals(multa.getBibliotecaRecolhimento())){
							erros.addErro("As unidade de recolhimento das multas devem ser iguais para estarem na mesma GRU.");
						}
					}
					
				}else{
					
					if(bibliotecaRecolhimento == null)
						bibliotecaRecolhimento = multa.getEmprestimo().getMaterial().getBiblioteca();
					else{
						if( ! bibliotecaRecolhimento.equals(multa.getEmprestimo().getMaterial().getBiblioteca())){
							erros.addErro("As unidade de recolhimento das multas devem ser iguais para estarem na mesma GRU.");
						
						}
					}
					
				}
			}
			
		}else{
			// Por enquanto não tem validações para a emissão de uma GRU por multa
		}
		
		
		checkValidation(erros);
		
	}
	
	
}
