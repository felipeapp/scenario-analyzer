/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/07/2010
 *
 */
package br.ufrn.sigaa.extensao.negocio;

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
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.PlanoTrabalhoExtensao;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.AutorizacaoDepartamento;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/***
 * 
 * Processador respons�vel por recuperar atividades de extens�o removidas.
 * @author Geyson
 *
 */
public class ProcessadorRecuperarAcao extends AbstractProcessador {
	
	/**
	 * Recupera a��o de extens�o removida do sistema.
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		if(mov.getCodMovimento().equals(SigaaListaComando.RECUPERAR_ACAO_EXTENSAO_REMOVIDA)){
			validate(mov);
			AtividadeExtensao atividade = ((MovimentoCadastro) mov).getObjMovimentado();
			MembroProjetoDao dao = getDAO(MembroProjetoDao.class, mov);
			HistoricoSituacaoProjetoDao daoH = getDAO(HistoricoSituacaoProjetoDao.class, mov);
			try {
				
				//Ativando a a��o de extens�o e o projeto base.
				atividade = dao.findByPrimaryKey(atividade.getId(), AtividadeExtensao.class);
				dao.updateField(AtividadeExtensao.class, atividade.getId(), "ativo", true);
				dao.updateField(Projeto.class, atividade.getProjeto().getId(), "ativo", true);

				//Membros da equipe removidos
				Collection<MembroProjeto> equipe = dao.findByProjeto(atividade.getProjeto().getId(), false);
				for (MembroProjeto membroProjeto : equipe) {
					dao.updateField(MembroProjeto.class, membroProjeto.getId(), "ativo", true);
				}

				//Discente com planos de trabalho
				for (PlanoTrabalhoExtensao pt : atividade.getPlanosTrabalho()) {
					if (pt.getDiscenteExtensao() != null) {
						dao.updateField(DiscenteExtensao.class, pt.getDiscenteExtensao().getId(), "ativo", true);
					}
				}

				//Autoriza��es dos departamentos
				for (AutorizacaoDepartamento autorizacao : atividade.getAutorizacoesDepartamentos()) {
					dao.updateField(AutorizacaoDepartamento.class, autorizacao.getId(), "ativo", true);
				}

				//Inscri��es para sele��o
				for (InscricaoSelecaoExtensao ins : atividade.getInscricoesSelecao()) {
					dao.updateField(InscricaoSelecaoExtensao.class, ins.getId(), "ativo", true);
				}

				//Participantes da a��o
				for (ParticipanteAcaoExtensao part : atividade.getParticipantesNaoOrdenados()) {
					dao.updateField(ParticipanteAcaoExtensao.class, part.getId(), "ativo", true);
				}

				//Retornando para �ltima situa��o ativa,
				TipoSituacaoProjeto tipo = daoH.ultimaSituacaoHistorico(atividade.getProjeto().getId());
				dao.updateField(AtividadeExtensao.class, atividade.getId(), "situacaoProjeto.id", tipo.getId());

				atividade.setSituacaoProjeto(tipo);
				atividade.setAtivo(true);
				ProjetoHelper.gravarHistoricoSituacaoProjeto(atividade.getSituacaoProjeto().getId(), atividade.getProjeto().getId(), mov
						.getUsuarioLogado().getRegistroEntrada());
				ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
			} finally {
				dao.close();
				daoH.close();
			}
			return null;
		}

		return null;
	}

	/**
	 * valida se a��o de extens�o tem a situa��o de removida.
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO, mov);
		AtividadeExtensao atividade =  ((MovimentoCadastro) mov).getObjMovimentado();
		ListaMensagens erros = new ListaMensagens();

		if(atividade.getSituacaoProjeto().getId() !=  TipoSituacaoProjeto.EXTENSAO_REMOVIDO){
			erros.addErro("A��o '" + atividade.getTitulo() + "' n�o pode ser recuperada.");
		}

		checkValidation(erros);
	}

}
