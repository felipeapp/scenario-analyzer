/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Processador que implementa as regras para desfazer a quita��o de um v�nculo de um usu�rio na biblioteca.  </p>
 *
 * <p> <i> Usado em situa��es raras, quando um v�nculo foi quitado por engano e o usu�rio necessita continuar realizando empr�timos na biblioteca.</i> </p>
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
		 * Valida se o usu�rio tem alguma conta n�o quitada, pois s� pode existir 1 por vez
		 */
		
		MovimentoDesfazQuitacaoUsuarioBiblioteca movimento = (MovimentoDesfazQuitacaoUsuarioBiblioteca) mov;
		
		for (UsuarioBiblioteca usuario : movimento.getContasUsuario()) {	
			if(usuario.isQuitado() == false){
				throw new NegocioException("O usu�rio j� possui um v�nculo ativo, por isso n�o � poss�vel ativar outro.");
			}	
		}
		
		if(movimento.getContaSelecionada() != null && ! movimento.getContaSelecionada().getVinculo().isVinculoBiblioteca()){
			ObtemVinculoUsuarioBibliotecaStrategy estrategia = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo();
		
			if(! estrategia.isVinculoAtivo( movimento.getContaSelecionada().getPessoa().getId(),movimento.getContaSelecionada().getVinculo(), movimento.getContaSelecionada().getIdentificacaoVinculo())){
				throw new NegocioException("A quita��o do v�nculo selecionado n�o pode ser desfeita porque ele n�o est� mais ativo." );
			}
		}
	}

}
