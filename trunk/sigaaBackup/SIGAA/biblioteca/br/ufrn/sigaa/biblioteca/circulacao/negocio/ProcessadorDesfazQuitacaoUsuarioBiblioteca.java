/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 26/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 * <p>Processador que implementa as regras para desfazer a quitação de um vínculo de um usuário na biblioteca.  </p>
 *
 * <p> <i> Usado em situações raras, quando um vínculo foi quitado por engano e o usuário necessita continuar realizando emprétimos na biblioteca.</i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorDesfazQuitacaoUsuarioBiblioteca extends AbstractProcessador{

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		
		validate(mov);
		
		MovimentoDesfazQuitacaoUsuarioBiblioteca movimento = (MovimentoDesfazQuitacaoUsuarioBiblioteca) mov;
		
		GenericDAO dao = null;

		try {
			dao = getGenericDAO(movimento);
			dao.updateFields(UsuarioBiblioteca.class, movimento.getContaSelecionada().getId(), new String[] {"quitado", "dataQuitacao", "usuarioDesfezQuitacao" }, new Object[] { false, null, (Usuario) movimento.getUsuarioLogado() });
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;
	}

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		/*
		 * Valida se o usuário tem alguma conta não quitada, pois só pode existir 1 por vez
		 */
		
		MovimentoDesfazQuitacaoUsuarioBiblioteca movimento = (MovimentoDesfazQuitacaoUsuarioBiblioteca) mov;
		
		for (UsuarioBiblioteca usuario : movimento.getContasUsuario()) {	
			if(usuario.isQuitado() == false){
				throw new NegocioException("O usuário já possui um vínculo ativo, por isso não é possível ativar outro.");
			}	
		}
		
		if(movimento.getContaSelecionada() != null && ! movimento.getContaSelecionada().getVinculo().isVinculoBiblioteca()){
			ObtemVinculoUsuarioBibliotecaStrategy estrategia = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo();
		
			if(! estrategia.isVinculoAtivo( movimento.getContaSelecionada().getPessoa().getId(),movimento.getContaSelecionada().getVinculo(), movimento.getContaSelecionada().getIdentificacaoVinculo())){
				throw new NegocioException("A quitação do vínculo selecionado não pode ser desfeita porque ele não está mais ativo." );
			}
		}
	}

}
