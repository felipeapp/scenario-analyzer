package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;

/**
 * Classe Utilit�ria para manipula��o de solicita��es de matr�cula.
 * 
 * @author Bernardo
 *
 */
public class SolicitacaoMatriculaHelper {
	
	/**
	 * Efetua a anula��o das solicita��es passadas como parametro e envia uma notifica��o aos discentes com o motivo determinado.
	 * 
	 * @param usuarioLogado
	 * @param motivo
	 * @param solicitacoes
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public static void anularSolicitacoes(UsuarioGeral usuarioLogado, String motivo, List<SolicitacaoMatricula> solicitacoes) throws ArqException, NegocioException {
		
		SolicitacaoMatriculaDao solicitacaoDao = DAOFactory.getInstance().getDAO(SolicitacaoMatriculaDao.class);
		
		try {
			MovimentoSolicitacaoMatricula movAnular = new MovimentoSolicitacaoMatricula();
			ProcessadorSolicitacaoMatricula proc = new ProcessadorSolicitacaoMatricula();
			
			for (SolicitacaoMatricula solicitacao : solicitacoes) {
				SolicitacaoMatricula solicitacaoCompleta = solicitacaoDao.findByPrimaryKey(solicitacao.getId(), SolicitacaoMatricula.class);
				solicitacaoCompleta.setObservacaoAnulacao(motivo);
				solicitacaoCompleta.setAnulado(true);
				
				Collection<SolicitacaoMatricula> anular = new ArrayList<SolicitacaoMatricula>();
				anular.add(solicitacaoCompleta);
				
				movAnular.setSolicitacoes(anular);
				movAnular.setDiscente(solicitacaoCompleta.getDiscente());
				movAnular.setSistema(Sistema.SIGAA);
				movAnular.setUsuarioLogado(usuarioLogado);
				movAnular.setCodMovimento(SigaaListaComando.ANULAR_SOLICITACAO_MATRICULA_AUTOMATICO);
				
				proc.execute(movAnular);
			}
		} finally {
			if (solicitacaoDao !=  null)
				solicitacaoDao.close();		
		}
		
	}
	
	/**
	 * Muda o status das solicita��es de matr�cula
	 * 
	 * @param solicitacoes
	 * @param status
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static void alterarStatusSolicitacao(ArrayList<SolicitacaoMatricula> solicitacoes, Integer status) throws NegocioException, DAOException {
		
		if (isEmpty(solicitacoes))
			return ;
		
		SolicitacaoMatriculaDao solicitacaoDao = DAOFactory.getInstance().getDAO(SolicitacaoMatriculaDao.class);
		try {
			solicitacaoDao.alterarStatusSolicitacoes(solicitacoes, status);
		} finally {
			if (solicitacaoDao != null) 
				solicitacaoDao.close();
		}
	}
	

	/**
	 * Evita que traga solicita��es que possuam matr�cula consolidada
	 * 
	 * @param jaCadastradas
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection<SolicitacaoMatricula> filtrarSomenteMatriculadas(Collection<SolicitacaoMatricula> jaCadastradas) {
		
		if (isEmpty(jaCadastradas))
			return new ArrayList<SolicitacaoMatricula>();
		
		return CollectionUtils.select(jaCadastradas, new Predicate() {
			
			@Override
			public boolean evaluate(Object obj) {
				
				SolicitacaoMatricula sol = (SolicitacaoMatricula) obj;
				
				if ( sol.isProcessada() && !sol.getMatriculaGerada().getSituacaoMatricula().equals(SituacaoMatricula.MATRICULADO) ) {
					return false;
				}
				return true;
			}
		});
	}	
	
}
