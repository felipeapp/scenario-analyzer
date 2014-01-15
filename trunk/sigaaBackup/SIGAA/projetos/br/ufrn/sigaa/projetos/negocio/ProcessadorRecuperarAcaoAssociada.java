/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/07/2010
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.projetos.HistoricoSituacaoProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.PlanoTrabalhoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/***
 * 
 * Processador respons�vel por recuperar a��es associadas removidas.
 * @author Amanda Priscilla
 *
 */
public class ProcessadorRecuperarAcaoAssociada extends AbstractProcessador {
	
	/**
	 * Recupera a��o associada removida do sistema.
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		if(mov.getCodMovimento().equals(SigaaListaComando.RECUPERAR_ACAO_ASSOCIADA_REMOVIDA)){
			validate(mov);
			// ATIVIDADE = PROJETO BASE = A��O ASSOCIADA
			Projeto projeto = ((MovimentoCadastro) mov).getObjMovimentado();
			MembroProjetoDao dao = getDAO(MembroProjetoDao.class, mov);
			
			HistoricoSituacaoProjetoDao daoH = getDAO(HistoricoSituacaoProjetoDao.class, mov);
			try {
				
				//Ativando o projeto base.
				projeto = dao.findByPrimaryKey(projeto.getId(), Projeto.class);
				dao.updateField(Projeto.class, projeto.getId(), "ativo", true);
		
				//Membros da equipe removidos
				Collection<MembroProjeto> equipe = dao.findByProjeto(projeto.getId(), false);
				for (MembroProjeto membroProjeto : equipe) {
					dao.updateField(MembroProjeto.class, membroProjeto.getId(), "ativo", true);
				}

				//Discente com planos de trabalho
				for (PlanoTrabalhoProjeto pt : dao.findByExactField(PlanoTrabalhoProjeto.class, "projeto.id", projeto.getId())) {
					if (pt.getDiscenteProjeto() != null) {
						dao.updateField(DiscenteProjeto.class, pt.getDiscenteProjeto().getId(), "ativo", true);
					}
				}

	
				//Retornando para �ltima situa��o ativa,
				TipoSituacaoProjeto tipo = daoH.ultimaSituacaoHistorico(projeto.getId());
				dao.updateField(Projeto.class, projeto.getId(), "situacaoProjeto.id", tipo.getId());

				projeto.setSituacaoProjeto(tipo);
				projeto.setAtivo(true);
				ProjetoHelper.gravarHistoricoSituacaoProjeto(projeto.getSituacaoProjeto().getId(), projeto.getId(), mov
						.getUsuarioLogado().getRegistroEntrada());
			
			} finally {
				dao.close();
				daoH.close();
			}
			return null;
		}

		return null;
	}

	/**
	 * valida se a��o associada tem a situa��o de removida.
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, mov);
		Projeto projeto =  ((MovimentoCadastro) mov).getObjMovimentado();
		ListaMensagens erros = new ListaMensagens();

		if(projeto.getSituacaoProjeto().getId() !=  TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO){
			erros.addErro("A��o '" + projeto.getTitulo() + "' n�o pode ser recuperada.");
		}

		checkValidation(erros);
	}

}
