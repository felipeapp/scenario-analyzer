/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe com m�todos utilit�rios sobre matr�cula de discentes em s�rie
 *
 * @author Rafael Gomes
 *
 */
public class MatriculaDiscenteSerieHelper {

	/**
	 * M�todo utilizado para registrar uma altera��o de matr�cula de discente em s�rie.
	 * registra o hist�rico de altera��o da situa��o da matr�cula do componente
	 *
	 * @param matriculaNova
	 *            nova matr�cula j� setada que a nova situa��o
	 * @param dao
	 * @param usuarioLogado
	 *            usu�rio que est� realizando a altera��o
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static AlteracaoMatriculaSerie alterarSituacaoMatriculaSerie(MatriculaDiscenteSerie matriculaSerieAtual, SituacaoMatriculaSerie novaSituacao,
			Movimento mov, MatriculaDiscenteSerieDao dao) throws NegocioException, ArqException {

		AlteracaoMatriculaSerie alteracao = new AlteracaoMatriculaSerie();
		
		if (matriculaSerieAtual == null || dao == null || novaSituacao == null)
			throw new IllegalArgumentException("Erro ao registrar altera��o de matr�culas em s�rie.");
		
		if (matriculaSerieAtual.getId() == 0) {
			throw new NegocioException("Erro ao registrar altera��o de matr�culas em s�rie.");
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
	 * M�todo respons�vel pela altera��o do status do discente, quando este for realizada a altera��o da situa��o da matricula na s�rie.
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