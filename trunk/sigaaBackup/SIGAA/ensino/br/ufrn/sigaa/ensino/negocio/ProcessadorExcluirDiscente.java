/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 29/10/2007
 * 
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;

/**
 * Processador que realiza a exclus�o de discentes, e a
 * persist�ncia da movimenta��o referente a essa a��o.
 *
 * @author Victor Hugo
 *
 */
public class ProcessadorExcluirDiscente extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		validate(movimento);
		MovimentoCadastro mov = (MovimentoCadastro) movimento;

		DiscenteAdapter discente = (DiscenteAdapter) mov.getObjMovimentado();
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
		GenericDAO dao = getGenericDAO(mov);
		
		try {
			if( mov.getCodMovimento().equals( SigaaListaComando.EXCLUIR_DISCENTE ) ){
	
				/**
				 * registrando a movimenta��o do aluno
				 */
				MovimentacaoAluno movimentacao = new MovimentacaoAluno();
				movimentacao.setDiscente(discente);
				movimentacao.setDataOcorrencia(new Date());
				movimentacao.setAnoReferencia( cal.getAno() );
				movimentacao.setPeriodoReferencia( cal.getPeriodo() );
				movimentacao.setAnoOcorrencia( cal.getAno() );
				movimentacao.setPeriodoOcorrencia( cal.getPeriodo() );
				movimentacao.setAtivo(true);
				movimentacao.setUsuarioCadastro( (Usuario) mov.getUsuarioLogado() );
				movimentacao.setTipoMovimentacaoAluno( new TipoMovimentacaoAluno(TipoMovimentacaoAluno.EXCLUIDO) );
				dao.create(movimentacao);
				
				/**
				 * Cancela as orienta��es do discente
				 */
				Collection<OrientacaoAcademica> orientacoes = new ArrayList<OrientacaoAcademica>();
				orientacoes = dao.findByExactField(OrientacaoAcademica.class, "discente.id", discente.getId());
				for(OrientacaoAcademica o : orientacoes) {
					dao.updateField(OrientacaoAcademica.class, o.getId(), "cancelado", true);
				}
				
	
				/**
				 * registrando em ObservacaoDiscente o motivo da exclus�o
				 */
				ObservacaoDiscente observacao = new ObservacaoDiscente();
				observacao.setDiscente(discente.getDiscente());
				observacao.setMovimentacao(movimentacao);
				observacao.setData(new Date());
				observacao.setAtivo(true);
				observacao.setObservacao(discente.getObservacao());
				dao.create(observacao);
	
				/**
				 * efetuando a exclus�o do discente
				 */
				DiscenteHelper.alterarStatusDiscente(discente, StatusDiscente.EXCLUIDO, mov, dao);
				
				/**
				 * Setando a matr�cula para nulo para que possamos reaproveitar o n�mero de matr�cula. 
				 */
				if (discente.getMatricula() != null)
					getGenericDAO(movimento).updateField(Discente.class,  discente.getId(), "matriculaAntiga", discente.getMatricula());
				getGenericDAO(movimento).updateField(Discente.class,  discente.getId(), "matricula", null);
				
	
			} else{
				throw new NegocioException("Opera��o inv�lida.");
			}
		}finally {	
			dao.close();
		}	
		return null;
	}

	public void validate(Movimento movimento) throws NegocioException, ArqException {
		checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG, 
				SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA }, movimento);
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		DiscenteAdapter discente = (DiscenteAdapter) mov.getObjMovimentado();

		ListaMensagens erros = new ListaMensagens();

		if( discente.getStatus() != StatusDiscente.CADASTRADO && discente.getStatus() != StatusDiscente.ATIVO ){
			erros.addErro("S� � poss�vel excluir discentes com o status ATIVO ou CADASTRADO.");
		}

		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
		int totalMatriculas = dao.countMatriculasByDiscente( discente, SituacaoMatricula.getSituacoesPagasEMatriculadasArray() );
		if( totalMatriculas > 0 ){
			erros.addErro("N�o � poss�vel excluir este discente pois ele possui matr�culas em componentes ativas ou aproveitadas.");
		}

		if( discente.getObservacao().trim().length() == 0 ){
			erros.addErro("Entre com a justificativa da exclus�o.");
		}

		checkValidation(erros);
	}

}
