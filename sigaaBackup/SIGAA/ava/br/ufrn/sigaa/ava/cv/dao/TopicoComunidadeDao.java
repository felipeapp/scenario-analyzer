/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/10/2008 
 */
package br.ufrn.sigaa.ava.cv.dao;

import static org.hibernate.criterion.Restrictions.eq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.cv.dominio.ArquivoComunidade;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.ConteudoComunidade;
import br.ufrn.sigaa.cv.dominio.IndicacaoReferenciaComunidade;
import br.ufrn.sigaa.cv.dominio.MaterialComunidade;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * @author David Pereira
 *
 */
public class TopicoComunidadeDao extends GenericSigaaDAO {
	
	/**
	 * Retorna os tópicos da comunidade - retornando os visíveis e invisíveis para usuários com permissão de moderador ou para o usuário 
	 * que criou o tópico
	 * 
	 * @param comunidade
	 * @param moderador
	 * @param usuario
	 * @throws DAOException
	 */
	public List<TopicoComunidade> findByComunidade(ComunidadeVirtual comunidade,boolean moderador, Usuario usuario) throws DAOException {

		String topicosVisiveis = " ";
		if ( !moderador && usuario != null )
			topicosVisiveis = " and ( t.visivel = trueValue() or t.usuario.id = " +usuario.getId() + " ) ";
		else if (!moderador && usuario == null)
			topicosVisiveis = " AND t.visivel = trueValue() ";
		
		Query q = getSession().createQuery(
						"select t "	+ 
						"from TopicoComunidade t where t.comunidade.id = ? " +
						 topicosVisiveis +
						" order by t.dataCadastro asc, t.topicoPai desc");
		q.setInteger(0, comunidade.getId());
		
		@SuppressWarnings("unchecked")
		List<TopicoComunidade> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna os tópicos da comunidade - retornando os visíveis e invisíveis para usuários com permissão de moderador ou para o usuário 
	 * que criou o tópico
	 * 
	 * @param comunidade
	 * @param moderador
	 * @param usuario
	 * @throws DAOException
	 */
	public Collection<TopicoComunidade> findTopicosVisiveisByComunidade(ComunidadeVirtual comunidade) throws DAOException {
		Query q = getSession().createQuery(
						"select new TopicoComunidade(t.id, t.descricao, t.conteudo, t.dataCadastro, t.topicoPai.id, t.comunidade.id, t.usuario) "
						+ "from TopicoComunidade t where t.comunidade.id = ? and t.visivel = trueValue() order by t.dataCadastro asc, t.topicoPai desc");
		q.setInteger(0, comunidade.getId());
		
		@SuppressWarnings("unchecked")
		Collection<TopicoComunidade> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna os conteúdos da comunidade - retornando os visíveis e invisíveis para usuários com permissão de moderador ou para o usuário 
	 * que criou o tópico
	 * 
	 * @param comunidade
	 * @param moderador
	 * @param usuario
	 * @throws DAOException
	 */
	public List<ConteudoComunidade> findConteudoComunidade(ComunidadeVirtual comunidae, boolean moderador , Usuario usuario) {
		try {
			String topicosVisiveis = " AND ct.topico.visivel = trueValue() ";
			if ( !moderador && usuario != null )
				topicosVisiveis = " AND ( ct.topico.visivel = trueValue() OR ct.topico.usuario.id = " +usuario.getId() + " ) ";
			else if ( !moderador && usuario == null )
				topicosVisiveis = " AND ct.topico.visivel = trueValue() ";
			
			@SuppressWarnings("unchecked")
			List<ConteudoComunidade> lista = getSession().createQuery("SELECT ct FROM ConteudoComunidade ct " +
					" WHERE (ct.ativo=trueValue() or ct.ativo=null)  " +
					" AND ct.topico.comunidade.id= " + comunidae.getId() + topicosVisiveis).list();
			return lista;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Busca os materiais, verificando se o material é visível para o usuário 
	 * 
	 * @param comunidade
	 * @return
	 */
	public Map<TopicoComunidade, List<MaterialComunidade>> findMateriaisByComunidadeUsuario(ComunidadeVirtual comunidade, boolean moderador, Usuario usuario) {
		Map<TopicoComunidade, List<MaterialComunidade>> result = new HashMap<TopicoComunidade, List<MaterialComunidade>>();

		List<IndicacaoReferenciaComunidade> referencias = findReferenciasComunidade(comunidade,moderador,usuario);
		List<ArquivoComunidade> arquivos = findArquivosByComunidade(comunidade,moderador,usuario);
		List<ConteudoComunidade> conteudos = findConteudoComunidade(comunidade,moderador,usuario);

		for (ArquivoComunidade arq : arquivos) {
			if (arq.getTopico() != null) {
				if (result.get(arq.getTopico()) == null)
					result.put(arq.getTopico(), new ArrayList<MaterialComunidade>());
				result.get(arq.getTopico()).add(arq);
			}
		}

		for (IndicacaoReferenciaComunidade ref : referencias) {
			if (ref.getTopico() != null) {
				if (result.get(ref.getTopico()) == null)
					result.put(ref.getTopico(), new ArrayList<MaterialComunidade>());
				result.get(ref.getTopico()).add(ref);
			}
		}

		for (ConteudoComunidade con : conteudos) {
			if (con.getTopico() != null) {
				if (result.get(con.getTopico()) == null)
					result.put(con.getTopico(), new ArrayList<MaterialComunidade>());
				result.get(con.getTopico()).add(con);
			}
		}


		for (Entry<TopicoComunidade, List<MaterialComunidade>> topico : result.entrySet()) {
			Collections.sort(topico.getValue(),
					new Comparator<MaterialComunidade>() {
						public int compare(MaterialComunidade o1, MaterialComunidade o2) {
							if (o1.getDataCadastro() == null && o2.getDataCadastro() == null)
								return 0;
							else if (o1.getDataCadastro() == null && o2.getDataCadastro() != null)
								return -1;
							else if (o1.getDataCadastro() != null && o2.getDataCadastro() == null)
								return 1;
							else
								return o1.getDataCadastro().compareTo(o2.getDataCadastro());
						}
					});
		}

		return result;
	}
	
	/**
	 * Retorna os arquivos da comunidade - retornando os visíveis e invisíveis para usuários com permissão de moderador ou para o usuário 
	 * que criou o tópico
	 * 
	 * @param comunidade
	 * @param moderador
	 * @param usuario
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ArquivoComunidade> findArquivosByComunidade(ComunidadeVirtual comunidade, boolean moderador , Usuario usuario) {
		
		String topicosVisiveis = "";
		if ( !moderador && usuario != null )
			topicosVisiveis = " AND ( at.topico.visivel = trueValue() OR at.topico.usuario.id = " +usuario.getId() + " ) ";
		else if ( !moderador && usuario == null )
			topicosVisiveis = " AND at.topico.visivel = trueValue() ";
		
		List<ArquivoComunidade> result = getHibernateTemplate().find(
						"select at.id, at.nome, at.data, at.topico.id, au.id, au.nome, au.idArquivo, at.descricao "
								+ "from ArquivoComunidade at, ArquivoUsuario au "
								+ "where at.arquivo.id = au.id and at.topico.comunidade.id = ? "+ topicosVisiveis, comunidade.getId());
		List<ArquivoComunidade> arquivos = new ArrayList<ArquivoComunidade>();
		for (Iterator it = result.iterator(); it.hasNext();) {
			Object[] linha = (Object[]) it.next();
			ArquivoComunidade at = new ArquivoComunidade();
			at.setId((Integer) linha[0]);
			at.setNome((String) linha[1]);
			at.setData((Date) linha[2]);
			at.setTopico(new TopicoComunidade());
			at.getTopico().setId((Integer) linha[3]);
			at.setArquivo(new ArquivoUsuario());
			at.getArquivo().setId((Integer) linha[4]);
			at.getArquivo().setNome((String) linha[5]);
			at.getArquivo().setIdArquivo((Integer) linha[6]);
			at.setDescricao((String) linha[7]);
			
			arquivos.add(at);
		}

		return arquivos;
	}
	
	/**
	 * Retorna as referências da comunidade - retornando os visíveis e invisíveis para usuários com permissão de moderador ou para o usuário 
	 * que criou o tópico
	 * 
	 * @param comunidade
	 * @param moderador
	 * @param usuario
	 * @throws DAOException
	 */
	public List<IndicacaoReferenciaComunidade> findReferenciasComunidade(ComunidadeVirtual comunidade, boolean moderador , Usuario usuario) {
		try {
			String topicosVisiveis = "";
			if ( !moderador && usuario != null)
				topicosVisiveis = " AND ( r.topico.visivel = trueValue() OR r.topico.usuario.id = " +usuario.getId() + " ) ";
			else if ( !moderador && usuario == null )
				topicosVisiveis = " AND r.topico.visivel = trueValue() ";
			
			@SuppressWarnings("unchecked")
			List<IndicacaoReferenciaComunidade> lista = getSession().createQuery("SELECT r FROM IndicacaoReferenciaComunidade r " +
					" WHERE r.topico.comunidade.id= " + comunidade.getId() + topicosVisiveis + " )"
					).list();
			return lista;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}

	public TopicoComunidade findByDescricao(ComunidadeVirtual t, String descricao) {
		DetachedCriteria dc = DetachedCriteria.forClass(TopicoComunidade.class);
		dc.add(eq("descricao", descricao));
		dc.add(eq("comunidade", t));
		return (TopicoComunidade) getHibernateTemplate().uniqueResult(dc);
	}
	
}
