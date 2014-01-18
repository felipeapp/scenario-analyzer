/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '21/08/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ParticipacaoSid;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ResumoSid;
import br.ufrn.sigaa.monitoria.dominio.StatusRelatorio;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao responsável pelas consultas sobre os relatórios parciais e finais
 *  do projeto e do monitor
 * @author ilueny santos
 *
 */
public class ResumoSidDao extends GenericSigaaDAO {

	/** Limite de resultados da consulta. */
	private static final long LIMITE_RESULTADOS = 1000;
	
	/**
	 * Retorna todos os resumos sid do projeto informado.
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ResumoSid> findByProjeto(ProjetoEnsino projeto) throws DAOException{

		Criteria c = getSession().createCriteria(ResumoSid.class);
		c.add( Expression.eq("ativo", Boolean.TRUE) );
		c.add( Expression.eq("projetoEnsino.id", projeto.getId()) );

		return c.list();
	}


	/**
	 * Retorna o resumo do ano informado
	 * para o projeto informado
	 * 
	 * É permitido o cadastro de apenas um resumo por ano e por projeto
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	public Collection<ResumoSid> findByProjetoAnoSid(Integer idProjeto, Integer anoSid) throws DAOException{

		Criteria c = getSession().createCriteria(ResumoSid.class);
		c.setFetchMode("projetoEnsino", FetchMode.JOIN);
		c.add( Expression.eq("projetoEnsino.id", idProjeto) );
		c.add( Expression.eq("anoSid", anoSid) );
		c.add( Expression.eq("ativo", true) );
		return c.list();
	}
	
	/**
	 * Método para buscar os resumos de projetos de acordo com uma série de
	 * filtros opcionais
	 * 
	 * @param projeto
	 * @param anoProjeto
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ResumoSid> filter(String tituloProjeto,
			Integer anoProjeto, Integer idServidorCoordenador,	Boolean resumoAtivo, Integer anoSid) throws DAOException {

		try {

			StringBuilder hqlCount = new StringBuilder();
			hqlCount.append(" SELECT  count(distinct res.id) " +
							"FROM ResumoSid as res " +
							"LEFT JOIN res.projetoEnsino pm " +
							"LEFT JOIN pm.equipeDocentes as equipe " +
							"LEFT JOIN res.participacoesSid p");
			
			hqlCount.append(" WHERE 1 = 1 ");

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta.append(" SELECT distinct res.id, res.anoSid, res.dataEnvio, res.status.id, res.status.descricao, " +
							"pm.id, pm.projeto.ano, pm.projeto.titulo, " +
							"p.id, p.apresentou, p.participou, dm.id, d.id, pes.id, pes.nome " +
							"FROM ResumoSid res " +
							"LEFT JOIN res.participacoesSid p " +
							"LEFT JOIN p.discenteMonitoria dm " +
							"LEFT JOIN dm.discente d " +
							"LEFT JOIN d.discente.pessoa pes " +
							"INNER JOIN res.projetoEnsino pm " +
							"LEFT JOIN pm.equipeDocentes as equipe ");
			
			hqlConsulta.append(" WHERE 1 = 1 ");

			StringBuilder hqlFiltros = new StringBuilder();
			// Filtros para a busca
			if (tituloProjeto != null) {
				hqlFiltros.append(" AND  "
						    + UFRNUtils.toAsciiUpperUTF8("pm.projeto.titulo")+ " like "
						    + UFRNUtils.toAsciiUTF8(":tituloProjeto"));
			}
			
			
			if (anoProjeto != null) {
				hqlFiltros.append(" AND pm.projeto.ano = :anoProjeto");
			}
			
			if (idServidorCoordenador != null) {
				hqlFiltros.append(" AND (equipe.servidor.id = :idServidorCoordenador AND equipe.coordenador=trueValue())");
			}
			
			if (resumoAtivo != null) {
				hqlFiltros.append(" AND res.ativo = :resumoAtivo");
			}

			if (anoSid != null) {
				hqlFiltros.append(" AND res.anoSid = :anoSid");
			}

			
			hqlCount.append(hqlFiltros.toString());
			hqlConsulta.append(hqlFiltros.toString());

			hqlConsulta.append(" ORDER BY pm ");

			// Criando consulta
			Query queryCount = getSession().createQuery(hqlCount.toString());
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

			// Populando os valores dos filtros
			if (tituloProjeto != null) {
				queryCount.setString("tituloProjeto", tituloProjeto.toUpperCase());
				queryConsulta.setString("tituloProjeto", "%"+tituloProjeto.toUpperCase()+"%");
			}
			
			
			if (anoProjeto != null) {
				queryCount.setInteger("anoProjeto", anoProjeto);
				queryConsulta.setInteger("anoProjeto", anoProjeto);
			}
			
			if (idServidorCoordenador != null) {
				queryCount.setInteger("idServidorCoordenador", idServidorCoordenador);
				queryConsulta.setInteger("idServidorCoordenador", idServidorCoordenador);
			}

			
			if (resumoAtivo != null) {
				queryCount.setBoolean("resumoAtivo", resumoAtivo);
				queryConsulta.setBoolean("resumoAtivo", resumoAtivo);
			}
		 
			
			if (anoSid != null) {
				queryCount.setInteger("anoSid", anoSid);
				queryConsulta.setInteger("anoSid", anoSid);
			}

			
			
			Long total = (Long) queryCount.uniqueResult();
			if (total > LIMITE_RESULTADOS)
				throw new LimiteResultadosException("A consulta retornou "
						+ total
						+ "	resultados. Por favor, restrinja mais a busca.");
			

			// res.id, res.anoSid, res.status.id, res.status.descricao, res.projetoEnsino.id, res.projetoEnsino.ano, res.projetoEnsino.titulo
			List lista = queryConsulta.list();

			ArrayList<ResumoSid> result = new ArrayList<ResumoSid>();
			for (int a = 0; a < lista.size(); a++) {
				
				int col = 0;				
				Object[] colunas = (Object[]) lista.get(a);
				
				//colunas repetidas se já tiver o resumo, pula todas as colunas e vai pra o que é diferente (participações)...
				ResumoSid r = new ResumoSid((Integer) colunas[0]);
				if (!result.contains(r)){
				
					ResumoSid res = new ResumoSid();
					res.setId((Integer) colunas[col++]);
					res.setAnoSid((Integer) colunas[col++]);
					res.setDataEnvio((Date) colunas[col++]);
					
					StatusRelatorio status = new StatusRelatorio();
					status.setId((Integer) colunas[col++]);
					status.setDescricao((String) colunas[col++]);
					
					res.setStatus(status);				
	
					ProjetoEnsino projeto = new ProjetoEnsino();
					projeto.setId((Integer) colunas[col++]);
					projeto.setAno((Integer) colunas[col++]);
					projeto.setTitulo((String) colunas[col++]);				
	
					res.setProjetoEnsino(projeto);
					
					result.add(res);
				}
				
				col = 8;

				
				//participações sid não repete nas linhas retornadas, várias participações para o mesmo resumo
				if (colunas[col] != null){
					ParticipacaoSid p = new ParticipacaoSid();
						p.setId((Integer) colunas[col++]);
						p.setApresentou((Boolean)colunas[col++]);
						p.setParticipou((Boolean)colunas[col++]);
						DiscenteMonitoria dm = new DiscenteMonitoria((Integer) colunas[col++]);
							DiscenteGraduacao dg = new DiscenteGraduacao();
								dg.setId((Integer) colunas[col++]);
							Pessoa pes = new Pessoa((Integer) colunas[col++]); 
								pes.setNome((String) colunas[col++]);
								dg.setPessoa(pes);
							dm.setDiscente(dg);
						p.setDiscenteMonitoria(dm);
						
					result.get(result.indexOf(r)).getParticipacoesSid().add(p);
				}
				
				
				col = 15;
				
				
				
			}

			return result;
			

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}

	/**
	 * Método utilizado para realizar uma busca no banco de dados por um resumo a partir do Discente passado
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ResumoSid> findByAluno(Discente discente) throws DAOException {
		if (discente != null)
			return getSession().createQuery("select rs from ResumoSid rs join rs.participacoesSid as participante " +
				"where participante.discenteMonitoria.discente.id = " + discente.getId()).list();
		else
			return null;
	}
	
	
	/**
	 * Método para buscar os resumos de projetos que o membro da comissão pode avaliar
	 * 
	 * @param membroComissao 
	 * @return colecao de resumos
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ResumoSid> findByMembroComissao(Integer idServidor) throws DAOException {

		try {

			// Filtros para a busca
			if ((idServidor != null)) {
				
				StringBuilder hqlConsulta = new StringBuilder();
				hqlConsulta.append(" SELECT distinct res FROM ResumoSid as res INNER JOIN res.avaliacoes as ava");

				hqlConsulta.append(" WHERE ava.avaliador.servidor.id = :idServidor  ORDER BY res.projetoEnsino ");
				
				// Criando consulta
				Query queryConsulta = getSession().createQuery(hqlConsulta.toString());
				queryConsulta.setInteger("idServidor", idServidor);
				
				return queryConsulta.list();
				
			}else
				return null;		


		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}

	
	
	
}
