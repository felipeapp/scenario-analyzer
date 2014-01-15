/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 09/01/2009 
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.dao.EstatisticasNotasDao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.stricto.negocio.ConceitoNotaHelper;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;


/**
 * DatasetProducer do Cewolf para exibir as estatísticas de notas
 * dos alunos de uma turma.
 *  
 * @author David Pereira
 *
 */
public class EstatisticasNotas extends SigaaAbstractController<Object> implements DatasetProducer, Serializable {

	public String getProducerId() {
		return "EstatisticasNotas";
	}

	public boolean hasExpired(@SuppressWarnings("rawtypes") Map params, Date data) {
		return (System.currentTimeMillis() - data.getTime()) > 5000;
	}

	public Object produceDataset(@SuppressWarnings("rawtypes") Map params) throws DatasetProduceException {
		Integer idTurma = (Integer) params.get("idTurma");
		Integer unidade = null;
		if(isNotEmpty(params.get("unidade")))
			unidade = Integer.valueOf((Byte) params.get("unidade"));
		boolean recup = ((Boolean) params.get("recup")) == null ? false : true;
		boolean media = ((Boolean) params.get("media")) == null ? false : true;
		
		EstatisticasNotasDao dao = DAOFactory.getInstance().getDAO(EstatisticasNotasDao.class);
		MatriculaComponenteDao mcDao = DAOFactory.getInstance().getDAO(MatriculaComponenteDao.class);
		
		try {
			Map<Double, Integer> histograma = null;
			
			if (unidade != null)
				histograma = dao.findHistogramaNotasUnidade(idTurma, unidade);
			else if (recup)
				histograma = dao.findHistogramaNotasRecuperacao(idTurma);
			else
				histograma = dao.findHistogramaNotasMediaFinal(idTurma);
			
			MatriculaComponente mc = dao.findMatriculaAleatoriaByTurma(idTurma);
			
			DefaultCategoryDataset ds = new DefaultCategoryDataset();
			
			if(isNotEmpty(mc)) {
				ParametrosGestoraAcademica p = ParametrosGestoraAcademicaHelper.getParametros(mc.getComponente());
				mc.setMetodoAvaliacao(p.getMetodoAvaliacao());
				
				if(mc.isMetodoNota()) {
					for (int i = 0; i <= 10; i++) {
						Integer valor = histograma.get(i);
						if (valor == null) valor = 0;
						
						if (unidade != null) {
							ds.addValue(valor, "Unidade " + unidade, String.valueOf(i));
						} else if (recup){
							ds.addValue(valor, "Recup.", String.valueOf(i));
						} else if (media) {
							ds.addValue(valor, "Final", String.valueOf(i));
						}
					}
				}
				else if(mc.isMetodoConceito()) {
					for (int i = 0; i <= 5; i++) {
						Integer valor = histograma.get(i);
						if (valor == null) valor = 0;
						
						String conceito = ConceitoNotaHelper.getDescricaoConceito(new Double(i));
						if(conceito == null) {
							conceito = "s/n";
						}
						
						if (unidade != null) {
							ds.addValue(valor, "Unidade " + unidade, conceito);
						} else if (recup){
							ds.addValue(valor, "Recup.", conceito);
						} else if (media) {
							ds.addValue(valor, "Final", conceito);
						}
					}
				}
				else if(mc.isMetodoAptidao()) {
					String chart = null;
					
					if (unidade != null) {
						chart = "Unidade " + unidade;
					}
					else if (recup){
						chart = "Recup.";
					}
					else if (media) {
						chart = "Final";
					}
					
					int semNota = 0;
					int apto = 0;
					int inapto = 0;
					
					Collection<MatriculaComponente> matriculas = dao.findMatriculadosAptidaoByTurma(idTurma);
					
					for (MatriculaComponente matricula : matriculas) {
						if(matricula.getApto() == null) {
							semNota++;
						}
						else if (matricula.getApto()) {
							apto++;
						}
						else if (!matricula.getApto()) {
							inapto++;
						}
					}
					
					ds.addValue(semNota, chart, "s/n");
					ds.addValue(apto, chart, "Apto");
					ds.addValue(inapto, chart, "Inapto");
				}
			}
			
			return ds;
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		} finally {
			dao.close();
		}
		
		return null;
	}
	
}
