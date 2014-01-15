/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 10/11/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import java.util.Date;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.AlteracaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe com métodos utilitários sobre matrícula de discentes em série
 *
 * @author Rafael Gomes
 *
 */
public class MatriculaDiscenteSerieHelper {

	/**
	 * Método utilizado para registrar uma alteração de matrícula de discente em série.
	 * registra o histórico de alteração da situação da matrícula do componente
	 *
	 * @param matriculaNova
	 *            nova matrícula já setada que a nova situação
	 * @param dao
	 * @param usuarioLogado
	 *            usuário que está realizando a alteração
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static AlteracaoMatriculaSerie alterarSituacaoMatriculaSerie(MatriculaDiscenteSerie matriculaSerieAtual, SituacaoMatriculaSerie novaSituacao,
			Movimento mov, MatriculaDiscenteSerieDao dao) throws NegocioException, ArqException {

		AlteracaoMatriculaSerie alteracao = new AlteracaoMatriculaSerie();
		
		if (matriculaSerieAtual == null || dao == null || novaSituacao == null)
			throw new IllegalArgumentException("Erro ao registrar alteração de matrículas em série.");
		
		if (matriculaSerieAtual.getId() == 0) {
			throw new NegocioException("Erro ao registrar alteração de matrículas em série.");
		}
		
		alteracao.setMatriculaSerie(matriculaSerieAtual);
		alteracao.setSituacaoAntiga(matriculaSerieAtual.getSituacaoMatriculaSerie());
		alteracao.setSituacaoNova(novaSituacao);
		alteracao.setDataAlteracao(new Date());
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setCodMovimento(mov.getCodMovimento().getId());
		if (SigaaListaComando.AFASTAR_DISCENTE_MEDIO.equals(mov.getCodMovimento())) {
			MovimentacaoAluno afastamento = (MovimentacaoAluno) ((MovimentoCadastro) mov).getObjMovimentado();
			if (afastamento.isTrancamento() || afastamento.isCancelamento())
				alteracao.setMovimentacaoAluno(afastamento);
		}

		dao.create(alteracao);
		dao.updateField(MatriculaDiscenteSerie.class, matriculaSerieAtual.getId(), "situacaoMatriculaSerie", novaSituacao);

		dao.detach(matriculaSerieAtual);
	
		return alteracao;
	}
	
	/** 
	 * Método responsável pela alteração do status do discente, quando este for realizada a alteração da situação da matricula na série.
	 * @param matriculaDiscenteSerie
	 * @param novaSituacao
	 * @param mov
	 * @throws ArqException
	 */
	public static void alterarStatusDiscente(MatriculaDiscenteSerie matriculaDiscenteSerie, SituacaoMatriculaSerie novaSituacao, Movimento mov, MatriculaDiscenteSerieDao dao ) throws ArqException{
		
		DiscenteAdapter discente = matriculaDiscenteSerie.getDiscenteMedio();
		ProcessadorDiscente processadorDiscente = new ProcessadorDiscente();
		
		int statusDiscente = discente.getStatus();
		if ( novaSituacao.equals(SituacaoMatriculaSerie.APROVADO_DEPENDENCIA) )
			statusDiscente = StatusDiscente.ATIVO_DEPENDENCIA;
		else if ( SituacaoMatriculaSerie.getSituacoesMatriculadoOuConcluido().contains(novaSituacao) )
			statusDiscente = statusDiscente != StatusDiscente.ATIVO_DEPENDENCIA ? StatusDiscente.ATIVO : StatusDiscente.ATIVO_DEPENDENCIA;
		else if ( novaSituacao.equals(SituacaoMatriculaSerie.TRANCADO) )
			statusDiscente = StatusDiscente.TRANCADO;
		else if ( novaSituacao.equals(SituacaoMatriculaSerie.CANCELADO) )
			statusDiscente = StatusDiscente.CANCELADO;
		
		if (discente.getStatus() != statusDiscente) {
			discente = dao.findByPrimaryKey(discente.getId(), Discente.class, "id", "status");
			processadorDiscente.persistirAlteracaoStatus(discente, statusDiscente, mov);
		}	
	}
	
}