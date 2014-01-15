package br.ufrn.sigaa.cv.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ava.cv.dao.TopicoComunidadeDao;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.ConfiguracoesComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.MaterialComunidade;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe auxiliar para Tópicos de Aula.
 * 
 * @author Diego Jácome
 *
 */
public class TopicoComunidadeHelper {

	/**
	 * Retorna os tópicos de aulas ordenados de uma comunidade virtual, assumindo que o usuário possui permissão para moderar.
	 * 
	 * @param comunidade
	 * @return
	 * @throws DAOException 

	 */
	public static List<TopicoComunidade> getTopicosOrdenados ( ComunidadeVirtual comunidade ) throws DAOException {
		return getTopicosOrdenados(comunidade,true,null);
	}
	
	/**
	 * Retorna os tópicos de aulas ordenados de uma comunidade virtual.
	 * 
	 * @param comunidade
	 * @param moderador
	 * @param usuario
	 * @return
	 * @throws DAOException 
	 */
	public static List<TopicoComunidade> getTopicosOrdenados ( ComunidadeVirtual comunidade , boolean moderador , Usuario usuario ) throws DAOException{
		
		TopicoComunidadeDao dao = null;
		
		try{
			dao = getDAO(TopicoComunidadeDao.class);
			List<TopicoComunidade> res = new ArrayList<TopicoComunidade>();
			
			Collection<TopicoComunidade> topicos = dao.findByComunidade(comunidade,moderador,usuario);
			Map<TopicoComunidade, List<MaterialComunidade>> materiais = dao.findMateriaisByComunidadeUsuario(comunidade,moderador,usuario);
			
			
			Map<Integer, Integer> referenciaColecao = new HashMap<Integer, Integer>();
			
			// Construir árvore de tópicos
			for (TopicoComunidade topico : topicos) {
				List<MaterialComunidade> materiaisTopico = materiais.get(topico);
				if (!isEmpty(materiaisTopico))
					topico.getMateriais().addAll(materiaisTopico);
				
				referenciaColecao.put(topico.getId(), topico.getTopicoPai() == null ? 0 : topico.getTopicoPai().getId());
			}
			
			// Calcular o nível de cada tópico
			for (TopicoComunidade aula : topicos) {
				int idPai = referenciaColecao.get(aula.getId());
				int nivel = 0;
				while (idPai != 0 && referenciaColecao.get(idPai) != null && nivel < 50) {
					idPai = referenciaColecao.get(idPai);
					nivel++;
				}
				aula.setNivel(nivel);
			}
			
			ordenarTopicos(topicos, null, res, comunidade ,dao);
	
			
			return res;
		} finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Faz a ordenação dos tópicos levando em consideração os pais/filhos.
	 * 
	 * @param aulas
	 * @param pai
	 * @param result
	 * @param comunidade
	 * @throws DAOException 
	 */
	//Suppress usado para que o CollectionUtils.select possa receber um cast
	//para List<TopicoComunidade>
	@SuppressWarnings("unchecked")
	private static void ordenarTopicos(Collection<TopicoComunidade> aulas, final Integer pai, List<TopicoComunidade> result, ComunidadeVirtual comunidade, GenericDAO dao) throws DAOException {
		
		List<TopicoComunidade> itens = (List<TopicoComunidade>) CollectionUtils.select(aulas, new Predicate() {
			public boolean evaluate(Object o) {
				TopicoComunidade ta = (TopicoComunidade) o;
				if (ta.getTopicoPai() == null) {
					return pai == null;
				} else {
					if (pai == null) return false;
					else return ta.getTopicoPai().getId() == pai;
				}
			}
		});
		
		if (!isEmpty(itens)) {
			ConfiguracoesComunidadeVirtual config = dao.findByExactField(ConfiguracoesComunidadeVirtual.class, "comunidade.id", comunidade.getId(),true);

			if (config == null || config.isOrdemTopicoDecrescente())
				Collections.sort(itens, new Comparator<TopicoComunidade>() {
					public int compare(TopicoComunidade o1, TopicoComunidade o2) {
						return o2.getDataCadastro().compareTo(o1.getDataCadastro());
					}
				});
			else if ( config.isOrdemTopicoCrescente())
				Collections.sort(itens, new Comparator<TopicoComunidade>() {
					public int compare(TopicoComunidade o1, TopicoComunidade o2) {
						return o1.getDataCadastro().compareTo(o2.getDataCadastro());
					}
				});
			else if ( config.isOrdemTopicoLivre())
				Collections.sort(itens, new Comparator<TopicoComunidade>() {
					public int compare(TopicoComunidade o1, TopicoComunidade o2) {
						return o1.getOrdem().compareTo(o2.getOrdem());
					}
				});
			
			if (pai == null) {
				if (result == null) result = new ArrayList<TopicoComunidade>();
				result.addAll(itens);
			} else {
				int index = result.indexOf(new TopicoComunidade(pai));
				result.addAll(index + 1, itens);
			}
			
			for (TopicoComunidade ta : itens) {
				ordenarTopicos(aulas, ta.getId(), result, comunidade, dao);
			}
		}
	}
	
	/**
	 * Atualiza a nova ordem dos tópicos, sendo o tópico mais novo inserido acima
	 *
	 * @param topico
	 * @param comunidade
	 * @throws DAOException
	 */
	public static void reOrdenarTopicosParaRemocao(TopicoComunidade topico, ComunidadeVirtual comunidade) throws DAOException {
		
		TopicoComunidadeDao dao = null;
		
		try{
			
			dao = getDAO(TopicoComunidadeDao.class);
			ConfiguracoesComunidadeVirtual config = dao.findByExactField(ConfiguracoesComunidadeVirtual.class, "comunidade.id", comunidade.getId(),true);
			
			if ( !isEmpty(config) && config.isOrdemTopicoLivre()){
				
				List<TopicoComunidade> topicos = getTopicosOrdenados(comunidade);
				if ( !isEmpty(topicos) ){
					for ( TopicoComunidade tc : topicos )
						if ( tc.getOrdem() != null && tc.getOrdem() > topico.getOrdem() ){
							tc.setOrdem(tc.getOrdem()-1);
							dao.updateField(TopicoComunidade.class, tc.getId(), "ordem", tc.getOrdem());
						}	
				}
			}
			
		}finally {
			if ( dao != null )
				dao.close();
		} 
	}
	
	/**
	 * Atualiza a nova ordem dos tópicos.
	 * 
	 * @param topico
	 * @param comunidade
	 * @throws DAOException
	 */
	public static void reOrdenarTopicosParaInsercao(TopicoComunidade topico, ComunidadeVirtual comunidade) throws DAOException {
		
		TopicoComunidadeDao dao = null;
		
		try{
			
			dao = getDAO(TopicoComunidadeDao.class);
			ConfiguracoesComunidadeVirtual config = dao.findByExactField(ConfiguracoesComunidadeVirtual.class, "comunidade.id", comunidade.getId(),true);
			
			if ( !isEmpty(config) && config.isOrdemTopicoLivre()){
				
				List<TopicoComunidade> topicos = getTopicosOrdenados(comunidade);
				if ( !isEmpty(topicos) ){
					for ( TopicoComunidade tc : topicos ){
						tc.setOrdem(tc.getOrdem()+1);
						dao.updateField(TopicoComunidade.class, tc.getId(), "ordem", tc.getOrdem());
					}
				}
				topico.setOrdem(0);
			}
			
		}finally {
			if ( dao != null )
				dao.close();
		} 
	}
	
	/**
	 * Recupera uma instância do DAO
	 */	
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}
}
