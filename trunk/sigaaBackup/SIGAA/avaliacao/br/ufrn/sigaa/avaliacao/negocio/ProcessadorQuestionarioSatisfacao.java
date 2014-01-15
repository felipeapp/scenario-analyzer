/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 09/06/2008 
 */
package br.ufrn.sigaa.avaliacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.avaliacao.dominio.QuestionarioSatisfacao;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processador para cadastro do question�rio de satisfa��o
 * 
 * @author David Pereira
 *
 */
public class ProcessadorQuestionarioSatisfacao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		QuestionarioSatisfacao obj = cMov.getObjMovimentado();
		
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		if (usuario.getVinculoAtivo().isVinculoDiscente())
			obj.setDiscente(usuario.getDiscenteAtivo().getDiscente());
		else
			obj.setDocente(usuario.getServidorAtivo());
		
		getGenericDAO(mov).create(obj);
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		GenericDAO dao = getGenericDAO(mov);
		Collection<QuestionarioSatisfacao> questionarios = null;
		
		if (usuario.getVinculoAtivo().isVinculoDiscente()) {
			questionarios = dao.findByExactField(QuestionarioSatisfacao.class, "discente.id", usuario.getDiscenteAtivo().getId());
		} else if (usuario.getVinculoAtivo().isVinculoDocenteExterno() ){
			questionarios = dao.findByExactField(QuestionarioSatisfacao.class, "docenteExterno.id", usuario.getVinculoAtivo().getDocenteExterno().getId());
		} else {
			questionarios = dao.findByExactField(QuestionarioSatisfacao.class, "docente.id", usuario.getServidorAtivo().getId());
		}
		
		if (!isEmpty(questionarios))
			throw new NegocioException("O preenchimento do question�rio de satisfa��o j� foi realizado anteriormente. N�o � poss�vel preench�-lo novamente.");
		
	}

}
