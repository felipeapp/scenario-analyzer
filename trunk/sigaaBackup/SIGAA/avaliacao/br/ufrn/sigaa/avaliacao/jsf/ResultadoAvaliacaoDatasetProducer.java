/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 'DD/MM/aaaa'
 *
 */
package br.ufrn.sigaa.avaliacao.jsf;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.avaliacao.dominio.GrupoPerguntas;
import br.ufrn.sigaa.avaliacao.dominio.MediaNotas;
import br.ufrn.sigaa.avaliacao.dominio.PercentualSimNao;
import br.ufrn.sigaa.avaliacao.dominio.Pergunta;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoAvaliacaoDocente;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;
import de.laures.cewolf.taglib.tags.CewolfTag;
import de.laures.cewolf.tooltips.CategoryToolTipGenerator;

/**
 * Classe responsável pela produção de dados para geração de gráficos de
 * {@link ResultadoAvaliacaoDocente resultados da avaliação institucional}, no
 * {@link CewolfTag Cewolf}.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ResultadoAvaliacaoDatasetProducer implements DatasetProducer,
		CategoryToolTipGenerator, Serializable {

	/** Mapa de perguntas, a ser utilizado no mapa da imagem produzida. */
	private Map<Integer, String> perguntas;

	/** Retorna o ID do producer
	 * @see de.laures.cewolf.DatasetProducer#getProducerId()
	 */
	public String getProducerId() {
		return "ResultadoAvaliacaoProducer";
	}

	/** Indica se o cache do gráfico expirou.
	 * @see de.laures.cewolf.DatasetProducer#hasExpired(java.util.Map,
	 *      java.util.Date)
	 */
	//O SuppressWarnings foi colocado pois esse método é definido na interface de.laures.cewolf.DatasetProducer.hasExpired(java.util.Map, java.util.Date). Ou seja, não dá para especificar o tipo dos mapas. 
	@SuppressWarnings("rawtypes")
	public boolean hasExpired(Map params, Date date) {
		Boolean evolucao = (Boolean) params.get("evolucao");
		if (evolucao != null && evolucao.booleanValue())
			return false;
		else 
			return true;
	}

	/**  Produz o conjunto de dados a ser utilizado no gráfico.
	 * @see de.laures.cewolf.DatasetProducer#produceDataset(java.util.Map)
	 */
	//O SuppressWarnings foi colocado pois esse método é definido na interface de.laures.cewolf.DatasetProducer.produceDataset(java.util.Map). Ou seja, não dá para especificar o tipo dos mapas. 
	@SuppressWarnings("rawtypes")
	public Object produceDataset(Map params) throws DatasetProduceException {
		Integer idServidor = (Integer) params.get("idServidor");
		String grupo = (String) params.get("grupo");
		Integer idResultado = (Integer) params.get("idResultado");
		Integer idTurma = (Integer) params.get("idTurma");
		Boolean evolucao = (Boolean) params.get("evolucao");
		@SuppressWarnings("unchecked")
		List<ResultadoAvaliacaoDocente> resultados = (List<ResultadoAvaliacaoDocente>) params.get("resultados");
		if (evolucao != null && evolucao.booleanValue())
			return datasetEvolucaoGeral(idServidor);
		else {
			if (resultados == null) return null;
			if (idResultado == null)
				return datasetGeral(resultados, grupo, idTurma);
			else {
				for (ResultadoAvaliacaoDocente resultado : resultados) {
					if (resultado.getId() == idResultado)
						return datasetDetalhado(resultado, grupo);
				}
			}
		}
		return null;
	}

	/**
	 * Produz o {@link Dataset} com as avaliações institucionais de um docente em um
	 * ano-período para determinado {@link GrupoPerguntas}.
	 * 
	 * @param idServidor
	 * @param ano
	 * @param periodo
	 * @param grupo
	 * @return
	 * @throws DatasetProduceException
	 */
	private Object datasetGeral(List<ResultadoAvaliacaoDocente> resultados, String grupo, Integer idTurma) throws DatasetProduceException {
		perguntas = new HashMap<Integer, String>();
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		Map<Integer, Integer> reOrdem = new HashMap<Integer, Integer>();
		int ordemCorrigida = 1;
		for (ResultadoAvaliacaoDocente resultado : resultados) {
			if (idTurma == null) {
				for (MediaNotas media : resultado.getMediaNotas()) {
					Pergunta pergunta = media.getPergunta();
					if (pergunta.getGrupo().getTitulo().equals(grupo)) {
						if (!reOrdem.containsKey(pergunta.getOrdem())) {
							reOrdem.put(pergunta.getOrdem(), ordemCorrigida);
							perguntas.put(ordemCorrigida, pergunta.getDescricao());
							ordemCorrigida++;
						}
						String turma = resultado.getDocenteTurma().getTurma().getDisciplina().getDescricao()+ " Turma "+ resultado.getDocenteTurma().getTurma().getCodigo();
						ds.addValue(media.getMedia(), turma, reOrdem.get(pergunta.getOrdem()));
					}
				}
			} else {
				for (PercentualSimNao percentual : resultado.getPercentualRespostasSimNao()) {
					if (idTurma == resultado.getDocenteTurma().getTurma().getId()) {
						Pergunta pergunta = percentual.getPergunta();
						if (pergunta.getGrupo().getTitulo().equals(grupo)) {
							if (!reOrdem.containsKey(pergunta.getOrdem())) {
								reOrdem.put(pergunta.getOrdem(), ordemCorrigida);
								perguntas.put(ordemCorrigida, pergunta.getDescricao());
								ordemCorrigida++;
							}
							ds.addValue(percentual.getPercentualSim(), "% SIM", reOrdem.get(pergunta.getOrdem()));
							ds.addValue(percentual.getPercentualNao(), "% NÃO", reOrdem.get(pergunta.getOrdem()));
						}
					}
				}
			}
		}
		return ds;
	}

	/**
	 * Produz o {@link Dataset}  com as avaliações institucionais de um docente em um
	 * ano-período para determinada turma.
	 * 
	 * @param idResultado
	 * @param grupo
	 * @return
	 * @throws DatasetProduceException
	 */
	private Object datasetDetalhado(ResultadoAvaliacaoDocente resultado, String grupo)
			throws DatasetProduceException {
		perguntas = new HashMap<Integer, String>();
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		int ordemCorrigida = 1;
		Map<Integer, Integer> reOrdem = new HashMap<Integer, Integer>();
		for (MediaNotas media : resultado.getMediaNotas()) {
			Pergunta pergunta = media.getPergunta();
			if (pergunta.getGrupo().getTitulo().equals(grupo)) {
				if (!reOrdem.containsKey(pergunta.getOrdem())) {
					reOrdem.put(pergunta.getOrdem(), ordemCorrigida);
					perguntas.put(ordemCorrigida, pergunta.getDescricao());
					ordemCorrigida++;
				}
				String turma = resultado.getDocenteTurma().getTurma().getDisciplina().getDescricao()+ " Turma "+ resultado.getDocenteTurma().getTurma().getCodigo();
				ds.addValue(media.getMedia(), turma, reOrdem.get(pergunta.getOrdem()));
			}
		}
		for (PercentualSimNao percentual : resultado
				.getPercentualRespostasSimNao()) {
			Pergunta pergunta = percentual.getPergunta();
			if (pergunta.getGrupo().getTitulo().equals(grupo)) {
				if (!reOrdem.containsKey(pergunta.getOrdem())) {
					reOrdem.put(pergunta.getOrdem(), ordemCorrigida);
					perguntas.put(ordemCorrigida, pergunta.getDescricao());
					ordemCorrigida++;
				}
				ds.addValue(percentual.getPercentualSim(), "% SIM", reOrdem.get(pergunta.getOrdem()));
				ds.addValue(percentual.getPercentualNao(), "% NÃO", reOrdem.get(pergunta.getOrdem()));
			}
		}
		return ds;
	}

	/**
	 * Produz o {@link Dataset} de evolução das médias por período.
	 * 
	 * @param idServidor
	 * @return
	 * @throws DatasetProduceException
	 */
	private Object datasetEvolucaoGeral(int idServidor)
			throws DatasetProduceException {
		AvaliacaoInstitucionalDao dao = new AvaliacaoInstitucionalDao();
		perguntas = new HashMap<Integer, String>();
		try {
			DefaultCategoryDataset ds = new DefaultCategoryDataset();
			Map<String, Double> mapa = dao.findEvolucaoMediaGeralAnoPeriodo(idServidor, true);
			for (String anoPeriodo : mapa.keySet()) {
				double valor = mapa.get(anoPeriodo);
				ds.addValue(valor, "média", anoPeriodo);
			}
			return ds;
		} catch (DAOException e) {
			e.printStackTrace();
			throw new DatasetProduceException(e.getMessage());
		} finally {
			dao.close();
		}
	}

	/** Gera uma dica de texto sobre o gráfico.
	 * @see de.laures.cewolf.tooltips.CategoryToolTipGenerator#generateToolTip(org.jfree.data.category.CategoryDataset,
	 *      int, int)
	 */
	public String generateToolTip(CategoryDataset category, int series, int item) {
		String tip = perguntas.get(item + 1);
		return tip == null ? "" : tip;
	}

}
