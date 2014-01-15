/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 16/10/2009
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.biblioteca.SuspensaoUsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SuspensaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;

/**
 * Processador que altera uma suspensão já lançada para um usuário da biblioteca.
 * @author Fred de Castro
 */

public class ProcessadorAlterarSuspensaoUsuarioBiblioteca extends AbstractProcessador{

	/**
	 * Para a suspensão passada, altera todas as futuras suspensões de acordo com a quantidade de
	 * dias que a primeira foi encurtada ou prolongada.
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		MovimentoCadastro personalMov = (MovimentoCadastro) mov;
		
		SuspensaoUsuarioBibliotecaDao dao = null;
		
		validate(mov);
		
		SuspensaoUsuarioBiblioteca suspensao = personalMov.getObjMovimentado();
		
		try {
			dao = getDAO (SuspensaoUsuarioBibliotecaDao.class, personalMov);
			
			// A suspensão modificada, com os valores originais das datas
			SuspensaoUsuarioBiblioteca suspensaoValorOriginal = dao.findByPrimaryKey(suspensao.getId(), SuspensaoUsuarioBiblioteca.class);
			
			Date dataFimOriginal = suspensaoValorOriginal.getDataFim();
			
			
			
			Integer idUsuarioBiblioteca = null;
			if (suspensaoValorOriginal.getEmprestimo() != null)
				idUsuarioBiblioteca = suspensaoValorOriginal.getEmprestimo().getUsuarioBiblioteca().getId();
			else
				idUsuarioBiblioteca = suspensaoValorOriginal.getUsuarioBiblioteca().getId();
			
			dao.detach(suspensaoValorOriginal); // Senão dá erro
			
			
			int diasAAlterar = CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(dataFimOriginal, suspensao.getDataFim());
			
			UsuarioBiblioteca usuarioBibliotecaCompleto = dao.findByPrimaryKey(idUsuarioBiblioteca, UsuarioBiblioteca.class, "pessoa.id", "biblioteca.id");
			
			List <SuspensaoUsuarioBiblioteca> suspesoesFuturas = dao.findSuspensoesFuturasDoUsuario(
					  usuarioBibliotecaCompleto.getIdentificadorPessoa()
					, usuarioBibliotecaCompleto.getIdentificadorBiblioteca()
					, dataFimOriginal);
			
			dao.clearSession();
			
			for (SuspensaoUsuarioBiblioteca suspensaoFutura : suspesoesFuturas){
				Calendar cal = Calendar.getInstance();
				cal.setTime(suspensaoFutura.getDataInicio());
				cal.add(Calendar.DAY_OF_MONTH, diasAAlterar);
				suspensaoFutura.setDataInicio(cal.getTime());
				
				cal.setTime(suspensaoFutura.getDataFim());
				cal.add(Calendar.DAY_OF_MONTH, diasAAlterar);
				suspensaoFutura.setDataFim(cal.getTime());
				
				// Atualiza a suspensão futura.
				dao.update(suspensaoFutura);
			}
			
			// Atualiza a suspensão alterada.
			dao.update(suspensao);
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;
	}

	/**
	 * Valida a suspensão
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastro personalMov = (MovimentoCadastro) mov;
		
		SuspensaoUsuarioBiblioteca suspensaoUsuarioBiblioteca = personalMov.getObjMovimentado();
		
		if (suspensaoUsuarioBiblioteca == null || suspensaoUsuarioBiblioteca.getId() <= 0)
			throw new NegocioException ("Uma suspensão deve ser informada.");
	
		checkValidation(suspensaoUsuarioBiblioteca.validate());
		
	}
	
}
