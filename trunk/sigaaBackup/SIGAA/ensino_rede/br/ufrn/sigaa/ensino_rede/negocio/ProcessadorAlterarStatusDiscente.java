/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.negocio;

import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino_rede.dominio.AlteracaoStatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;

/**
 * Processador responsável pela alteração dos status dos Discentes
 *
 * @author Jean Guerethes
 */
public class ProcessadorAlterarStatusDiscente  extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException {

		validate(mov);
		
		MovimentoAlterarStatusMatriculaRede cmov = (MovimentoAlterarStatusMatriculaRede) mov;
		GenericDAO dao = null;
		
		try {
						
			dao = getGenericDAO(mov);
			List<GrupoDiscentesAlterados> todosGrupos = cmov.getGruposDiscentesAlterados();
			
			for (GrupoDiscentesAlterados grupoDiscentes : todosGrupos) {
				for (DiscenteAssociado discente : grupoDiscentes.getDiscentes()) {
					criarAlteracaoSituacaoDiscente(mov, dao, discente , grupoDiscentes.getStatusNovo().getId() );
					dao.updateField(DiscenteAssociado.class, discente.getId(), "status.id", grupoDiscentes.getStatusNovo().getId());
				}
			}
			

		} finally {
			if (dao != null)
				dao.close();
		}
		return null;
	}

	private void criarAlteracaoSituacaoDiscente(Movimento mov, GenericDAO dao, DiscenteAssociado discente, int statusNovo ) throws DAOException {
		AlteracaoStatusDiscenteAssociado alteracao = new AlteracaoStatusDiscenteAssociado();
		alteracao.setData(new Date());
		alteracao.setDiscente(discente);
		alteracao.setMovimento(mov.getCodMovimento().getId());
		alteracao.setStatus(discente.getStatus().getId());
		alteracao.setStatusNovo(statusNovo);
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
		
		dao.create(alteracao);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
//		ListaMensagens mensagens = new ListaMensagens();
//		MovimentoCadastro cmov = (MovimentoCadastro) mov;
//		Collection<DiscenteAssociado> discentes = (Collection<DiscenteAssociado>) cmov.getColObjMovimentado(); 
//		
//		if ( discentes.isEmpty() ) {
//			mensagens.addErro("Nenhum Discente foi Selecionado.");
//		} else {
//			for (DiscenteAssociado discenteAssociado : discentes) {
//				if ( discenteAssociado.getStatus().getId() != StatusDiscenteAssociado.PRE_CADASTRADO ) {
//					mensagens.addErro("O discente " + discenteAssociado.getPessoa().getNome() + 
//							" já se encontra " + discenteAssociado.getStatus().getDescricao() );
//				}
//			}
//		}
//	
//		checkValidation(mensagens);
	}

}