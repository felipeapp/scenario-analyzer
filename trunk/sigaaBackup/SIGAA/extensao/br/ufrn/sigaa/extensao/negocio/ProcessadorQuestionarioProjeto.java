package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.QuestionarioProjetoExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.GrupoQuestionarioExtensao;
import br.ufrn.sigaa.extensao.dominio.QuestionarioProjetoExtensao;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.negocio.ProcessadorQuestionarioRespostas;

public class ProcessadorQuestionarioProjeto extends AbstractProcessador {
	
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		validate(mov);
		QuestionarioProjetoExtensao qpe = (QuestionarioProjetoExtensao) mov.getObjMovimentado();
		
		if (SigaaListaComando.ASSOCIAR_QUESTIONARIO_PROJETO.equals(mov.getCodMovimento())){
			QuestionarioProjetoExtensaoDao dao = getDAO(QuestionarioProjetoExtensaoDao.class, mov);
			try {
				Collection<QuestionarioProjetoExtensao> questionarios = new ArrayList<QuestionarioProjetoExtensao>();
				GrupoQuestionarioExtensao grupoQuest = dao.findByPrimaryKey(qpe.getTipoGrupo(), GrupoQuestionarioExtensao.class);
				if ( qpe.getAno() == null ) qpe.setAno(0);
				Collection<MembroProjeto> projetos = dao.findProjetos(grupoQuest, qpe.getEdital(), qpe.getAno(), qpe.getTipoAtividade().getId());
				
				for (MembroProjeto membroProjeto : projetos) {
					QuestionarioProjetoExtensao q = new QuestionarioProjetoExtensao();
					q.setEdital(qpe.getEdital());
					q.setPessoa(membroProjeto.getPessoa());
					q.setProjeto(membroProjeto.getProjeto());
					q.setQuestionario(qpe.getQuestionario());
					q.setTipoGrupo(qpe.getTipoGrupo());
					q.setDataCadastro(new Date());
					q.setRegistroCadastro(mov.getUsuarioLogado().getRegistroEntrada());
					q.setTipoAtividade( qpe.getTipoAtividade() );
					q.setObrigatoriedade( qpe.isObrigatoriedade() );
					questionarios.add(q);
				}
				
				dao.getHibernateTemplate().saveOrUpdateAll(questionarios);
				
			} finally {
				dao.close();
			}
		}
		
		if (SigaaListaComando.RESPONDER_QUESTIONARIO_PROJETO.equals(mov.getCodMovimento())){
			QuestionarioRespostas questRespo = salvarQuestionarioResposta(mov);
			QuestionarioProjetoExtensaoDao dao = getDAO(QuestionarioProjetoExtensaoDao.class, mov);
			try {
				dao.updateFields(QuestionarioProjetoExtensao.class, qpe.getId(), 
						new String[] {"dataResposta","registroResposta", "questionarioResposta.id"}, 
						new Object[] {new Date(), mov.getUsuarioLogado().getRegistroEntrada(), questRespo.getId()});
			} finally {
				dao.close();
			}
		}
		
		if (SigaaListaComando.REMOVER_ASSOCIACAO_QUESTIONARIO_PROJETO.equals(mov.getCodMovimento())){
			QuestionarioProjetoExtensaoDao dao = getDAO(QuestionarioProjetoExtensaoDao.class, mov);
			try {
				dao.inativarQuestionarios(qpe);
			} finally {
				dao.close();
			}
		}
			
		return null;
	}

	private QuestionarioRespostas salvarQuestionarioResposta(Movimento movimento) throws NegocioException, ArqException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		QuestionarioProjetoExtensao qpe = (QuestionarioProjetoExtensao) mov.getObjMovimentado();
		QuestionarioRespostas questRespo = (QuestionarioRespostas) mov.getObjAuxiliar();
		questRespo.setQuestionarioExtensao(qpe);
		ProcessadorQuestionarioRespostas processadorQuestionarioRespostas = new ProcessadorQuestionarioRespostas();
		processadorQuestionarioRespostas.cadastrarRespostas(mov, questRespo);
		return questRespo;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		QuestionarioProjetoExtensao qpe = (QuestionarioProjetoExtensao) movCad.getObjMovimentado();
		if (SigaaListaComando.ASSOCIAR_QUESTIONARIO_PROJETO.equals(mov.getCodMovimento())){
			QuestionarioProjetoExtensaoDao dao = getDAO(QuestionarioProjetoExtensaoDao.class, mov);
			try {
				boolean haQuestionarioCadastrado = 
						dao.haQuestionarioEdital(qpe.getQuestionario().getId(), qpe.getTipoGrupo(), qpe.getTipoAtividade().getId());
				if ( haQuestionarioCadastrado ) {
					throw new NegocioException("Esse questionário já foi associado para esse grupo de Usuário.");
				}
				
			} finally {
				dao.close();
			}
		}
		
		if (SigaaListaComando.REMOVER_ASSOCIACAO_QUESTIONARIO_PROJETO.equals(mov.getCodMovimento())){
			QuestionarioProjetoExtensaoDao dao = getDAO(QuestionarioProjetoExtensaoDao.class, mov);
			boolean haQuestionarioCadastrado = 
					dao.haQuestionarioEdital(qpe.getQuestionario().getId(), qpe.getTipoGrupo(), qpe.getTipoAtividade().getId());
			if ( !haQuestionarioCadastrado ) {
				throw new NegocioException("Todos os questionário destinados para esse grupo de usuário já foi removido.");
			}
		}

	}
	
}