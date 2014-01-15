/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 09/01/2009 
 */
package br.ufrn.sigaa.ava.jsf;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.jfree.data.general.DefaultPieDataset;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;


/**
 * DatasetProducer do Cewolf para exibir as estatísticas de uma turma.
 *  
 * @author David Pereira
 *
 */
public class EstatisticasTurma implements DatasetProducer, Serializable {

	public String getProducerId() {
		return "EstatisticasTurma";
	}

	@SuppressWarnings("unchecked")
	public boolean hasExpired(Map params, Date data) {
		return (System.currentTimeMillis() - data.getTime()) > 5000;
	}

	@SuppressWarnings("unchecked")
	public Object produceDataset(Map params) throws DatasetProduceException {
		Integer idTurma = (Integer) params.get("idTurma");
		
		TurmaDao dao = new TurmaDao();
		try {
			Collection<MatriculaComponente> aprovados = null;
			Collection<MatriculaComponente> reprovados = null;
			Collection<MatriculaComponente> repFaltas = null;
			Collection<MatriculaComponente> repMediaFaltas = null;
			Collection<MatriculaComponente> trancados = null;
			Collection<MatriculaComponente> matriculados = null;
		
			Turma turma = dao.findByPrimaryKeyOtimizado(idTurma);
			
			if ( !turma.isAgrupadora() ) {
				aprovados = dao.findParticipantesTurma(idTurma, null, false, false, SituacaoMatricula.APROVADO);
				reprovados = dao.findParticipantesTurma(idTurma, null, false, false, SituacaoMatricula.REPROVADO);
				repFaltas = dao.findParticipantesTurma(idTurma, null, false, false, SituacaoMatricula.REPROVADO_FALTA);
				repMediaFaltas = dao.findParticipantesTurma(idTurma, null, false, false, SituacaoMatricula.REPROVADO_MEDIA_FALTA);
				trancados = dao.findParticipantesTurma(idTurma, null, false, false, SituacaoMatricula.TRANCADO);
				matriculados = dao.findParticipantesTurma(idTurma, null, false, false, SituacaoMatricula.MATRICULADO);
			} else {
				aprovados = dao.findParticipantesTurma(idTurma, null, false, true, SituacaoMatricula.APROVADO);
				reprovados = dao.findParticipantesTurma(idTurma, null, false, true, SituacaoMatricula.REPROVADO);
				repFaltas = dao.findParticipantesTurma(idTurma, null, false, true, SituacaoMatricula.REPROVADO_FALTA);
				repMediaFaltas = dao.findParticipantesTurma(idTurma, null, false, true, SituacaoMatricula.REPROVADO_MEDIA_FALTA);
				trancados = dao.findParticipantesTurma(idTurma, null, false, true, SituacaoMatricula.TRANCADO);
				matriculados = dao.findParticipantesTurma(idTurma, null, false, true, SituacaoMatricula.MATRICULADO);
			}
			
			int qtdAprovados = aprovados == null ? 0 : aprovados.size();
			int qtdReprovados = reprovados == null ? 0 : reprovados.size();
			int qtdRepFaltas = repFaltas == null ? 0 : repFaltas.size();
			int qtdRepMediaFaltas = repMediaFaltas == null ? 0 : repMediaFaltas.size();
			int qtdTrancados = trancados == null ? 0 : trancados.size();
			int qtdMatriculados = matriculados == null ? 0 : matriculados.size();
		
			double total = (qtdAprovados + qtdRepFaltas + qtdReprovados + qtdRepMediaFaltas + qtdTrancados + qtdMatriculados);
			double porcentagemAprovados =  Math.round(((qtdAprovados*100)/total)*10D)/10D ;
			double porcentagemReprovados = Math.round(((qtdReprovados*100)/total)*10D)/10D;
			double porcentagemRepFaltas = Math.round(((qtdRepFaltas*100)/total)*10D)/10D;
			double porcentagemRepMediaFaltas = Math.round(((qtdRepMediaFaltas*100)/total)*10D)/10D;
			double porcentagemTrancados = Math.round(((qtdTrancados*100)/total)*10D)/10D;
			double porcentagemMatriculados = Math.round(((qtdMatriculados*100)/total)*10D)/10D;
			
			//Descrição das situações + porcentagem
			String descAprovados = SituacaoMatricula.APROVADO.getDescricao() + " (" + porcentagemAprovados + "%)";
			String descReprovados = SituacaoMatricula.REPROVADO.getDescricao() + " (" + porcentagemReprovados + "%)";
			String descRepFaltas = SituacaoMatricula.REPROVADO_FALTA.getDescricao() + " (" + porcentagemRepFaltas + "%)";
			String descRepMediaFaltas = SituacaoMatricula.REPROVADO_MEDIA_FALTA.getDescricao() + "(" + porcentagemRepMediaFaltas + "%)";
			String descTrancados = SituacaoMatricula.TRANCADO.getDescricao() + " (" + porcentagemTrancados + "%)";
			String descMatriculados = SituacaoMatricula.MATRICULADO.getDescricao() + " (" + porcentagemMatriculados + "%)";

			
			DefaultPieDataset ds = new DefaultPieDataset();
			ds.setValue(descAprovados, qtdAprovados);
			ds.setValue(descReprovados, qtdReprovados);
			ds.setValue(descRepFaltas, qtdRepFaltas);
			ds.setValue(descRepMediaFaltas, qtdRepMediaFaltas);
			ds.setValue(descTrancados, qtdTrancados);
			ds.setValue(descMatriculados, qtdMatriculados);
			
			return ds;
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return null;
	}
	
}
