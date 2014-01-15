/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '10/02/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoParecerAvaliacaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * DAO responsável por realizar consultas no banco de dados em relação ao Relatório de Ações de Extensão
 */
public class RelatorioAcaoExtensaoDao extends GenericSigaaDAO {

	public RelatorioAcaoExtensaoDao() {
	}

	/**
	 * Lista os relatórios ativos do tipo informado da atividade informada
	 * Se o tipo não for informado, retorna todos os tipos de relatório da atividade
	 * 
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioAcaoExtensao> findByAtividadeTipoRelatorio(
			Integer idAtividade, Integer idTipoRelatorio) throws DAOException {

		try {
			StringBuffer hql = new StringBuffer(
					"select r from RelatorioAcaoExtensao r  "
							+ "where r.atividade.id = :idAtividade "
							+ "and r.ativo = trueValue() ");

			if (idTipoRelatorio != null)
				hql.append(" and r.tipoRelatorio.id = :idTipoRelatorio ");

			hql.append(" order by r.atividade.sequencia ");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idAtividade", idAtividade);

			if (idTipoRelatorio != null)
				query.setInteger("idTipoRelatorio", idTipoRelatorio);

			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	
	/**
	 * utilizado na busca de relatórios através do menu da proex
	 * @param titulo
	 * @param idTipoRelatorio
	 * @param idsTipoAtividade
	 * @param ano
	 * @param inicio
	 * @param fim
	 * @param inicioConclusao
	 * @param fimConclusao
	 * @param idEdital
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<RelatorioAcaoExtensao> buscaRelatorioAcaoExtensao(String titulo,
			Integer idTipoRelatorio, Integer[] idsTipoAtividade, Integer ano,
			Date inicio, Date fim, Date inicioConclusao, Date fimConclusao,  Integer idEdital, Integer idServidor) throws DAOException {

		try {

			StringBuffer hql = new StringBuffer();

			hql.append("select " 
							+ " distinct r.id, "
							+ " r.ativo, "
							+ " r.dataCadastro, "
							+ " r.dataEnvio, "
							+ " r.tipoRelatorio, "
							+ " r.dataValidacaoDepartamento, "
							+ " tipoParecerDepartamento, "
							+ " r.dataValidacaoProex, "
							+ " tipoParecerProex,"
							+ " atv.id, atv.sequencia, pj.ano, " 
							+ " pj.titulo, atv.tipoAtividadeExtensao "
							+ " from RelatorioAcaoExtensao r "
							+ " left join r.tipoParecerDepartamento tipoParecerDepartamento "
							+ " left join r.tipoParecerProex tipoParecerProex "
							+ " inner join r.atividade atv "
							+ " inner join atv.projeto pj "
							+ " inner join pj.equipe equipe "
							+ " inner join equipe.servidor servidor "
							+ " left join atv.editalExtensao edital "							
							+ " where r.ativo = trueValue() and r.dataEnvio is not null ");
			

			
			if (idServidor != null) {
				hql.append(" AND servidor.id = :idServidor ");
			}
			
			if (idEdital != null) {
				hql.append(" AND edital.id = :idEdital ");
			}			
			

			if (titulo != null)
				hql.append("and upper(pj.titulo) like :titulo ");

			if (idsTipoAtividade != null)
				hql
						.append("and atv.tipoAtividadeExtensao.id IN (:idsTipoAtividade) ");

			if (idTipoRelatorio != null)
				hql.append(" and r.tipoRelatorio.id = :idTipoRelatorio ");

			if (ano != null)
				hql.append("and pj.ano = :ano ");

			if ((inicio != null) && (fim != null))
				hql.append("and ((r.dataEnvio >= :inicio) and (r.dataEnvio <= :fim))");
			
			if ((inicioConclusao != null) && (fimConclusao != null))
				hql.append(" and pj.dataFim >= :inicioConclusao AND pj.dataFim <= :fimConclusao  ");

			hql.append("order by  pj.ano DESC, atv.sequencia DESC, atv.tipoAtividadeExtensao.id ");

			Query query = getSession().createQuery(hql.toString());

			if (titulo != null)
				query.setString("titulo", "%" + titulo.toUpperCase() + "%");

			if (idsTipoAtividade != null)
				query.setParameterList("idsTipoAtividade", idsTipoAtividade);

			if (idTipoRelatorio != null)
				query.setInteger("idTipoRelatorio", idTipoRelatorio);

			if (ano != null)
				query.setInteger("ano", ano);
			
			if (idServidor != null)
				query.setInteger("idServidor", idServidor);

			if ((inicio != null) && (fim != null)) {
				query.setDate("inicio", inicio);
				query.setDate("fim", fim);
			}
			
			if ((inicioConclusao != null) && (fimConclusao != null)) {
				query.setDate("inicioConclusao", inicioConclusao);
				query.setDate("fimConclusao", fimConclusao);
			}
			
			if (idEdital != null) {				
				query.setInteger("idEdital", idEdital);
			}

			List lista = query.list();

			ArrayList<RelatorioAcaoExtensao> result = new ArrayList<RelatorioAcaoExtensao>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				RelatorioAcaoExtensao rel = new RelatorioAcaoExtensao();
				rel.setId((Integer) colunas[col++]);
				rel.setAtivo((Boolean) colunas[col++]);
				rel.setDataCadastro((Date) colunas[col++]);
				rel.setDataEnvio((Date) colunas[col++]);
				rel.setTipoRelatorio((TipoRelatorioExtensao) colunas[col++]);
				rel.setDataValidacaoDepartamento((Date) colunas[col++]);
				rel.setTipoParecerDepartamento((TipoParecerAvaliacaoExtensao) colunas[col++]);
				rel.setDataValidacaoProex((Date) colunas[col++]);
				rel.setTipoParecerProex((TipoParecerAvaliacaoExtensao) colunas[col++]);

				AtividadeExtensao atividade = new AtividadeExtensao();
				atividade.setId((Integer) colunas[col++]);
				atividade.setSequencia((Integer) colunas[col++]);
				atividade.setAno((Integer) colunas[col++]);
				atividade.setTitulo((String) colunas[col++]);
				atividade.setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
				rel.setAtividade(atividade);

				result.add(rel);

			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Lista os relatórios pendentes das unidades informadas utilizado para listar
	 * todos os relatórios para o chefe do departamento autorizar
	 * 
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioAcaoExtensao> findByUnidades(Collection<UnidadeGeral> unidades)
			throws DAOException {

		try {
			ArrayList<RelatorioAcaoExtensao> result = new ArrayList<RelatorioAcaoExtensao>();
			
			if ((unidades != null) && (unidades.size() > 0)) {
				
				String hql = "select " 
						+ " r.id, "
						+ " r.ativo, "
						+ " r.dataCadastro, "
						+ " r.dataEnvio, "
						+ " r.tipoRelatorio, "
						+ " r.dataValidacaoDepartamento, "
						+ " tipoParecerDepartamento, "
						+ " r.dataValidacaoProex, "
						+ " tipoParecerProex, "
						+ " atv.id, atv.sequencia, pj.ano, pj.titulo, atv.tipoAtividadeExtensao "
						+ " from RelatorioAcaoExtensao r " 
						+ " left  join r.tipoParecerDepartamento tipoParecerDepartamento "
						+ " left  join r.tipoParecerProex tipoParecerProex "
						+ " inner join r.atividade atv " 
						+ " inner join atv.projeto pj "
						+ " inner join pj.unidade unidade "
						+ " where unidade.id IN (:idsUnidades) and r.ativo = trueValue() and r.dataEnvio is not null and pj.ativo=trueValue() "
						+ " order by pj.ano DESC ";
				Query query = getSession().createQuery(hql);
				
				ArrayList<Integer> idsUnidades = new ArrayList<Integer>();
				for (UnidadeGeral u: unidades) 
					idsUnidades.add(u.getId());
				
				query.setParameterList("idsUnidades", idsUnidades);
	
				List lista = query.list();
	
				for (int a = 0; a < lista.size(); a++) {
	
					int col = 0;
					Object[] colunas = (Object[]) lista.get(a);
	
					RelatorioAcaoExtensao rel = new RelatorioAcaoExtensao();
					rel.setId((Integer) colunas[col++]);
					rel.setAtivo((Boolean) colunas[col++]);
					rel.setDataCadastro((Date) colunas[col++]);
					rel.setDataEnvio((Date) colunas[col++]);
					rel.setTipoRelatorio((TipoRelatorioExtensao) colunas[col++]);
					rel.setDataValidacaoDepartamento((Date) colunas[col++]);
					rel.setTipoParecerDepartamento((TipoParecerAvaliacaoExtensao) colunas[col++]);
					rel.setDataValidacaoProex((Date) colunas[col++]);
					rel.setTipoParecerProex((TipoParecerAvaliacaoExtensao) colunas[col++]);
	
					AtividadeExtensao atividade = new AtividadeExtensao();
					atividade.setId((Integer) colunas[col++]);
					atividade.setSequencia((Integer) colunas[col++]);
					atividade.setAno((Integer) colunas[col++]);
					atividade.setTitulo((String) colunas[col++]);
					atividade.setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
					rel.setAtividade(atividade);
	
					result.add(rel);
	
				}
			}

			return result;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Lista os relatórios pendentes de análise pela proex. Utilizado para
	 * listar todos os relatórios que os coordenadores dos cursos, eventos,
	 * programas e projetos possam autorizar.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioAcaoExtensao> findAllRelatoriosPendentesProex(
			Integer[] idsTipoAtividade) throws DAOException {

		try {
			StringBuilder hql = new StringBuilder(
					"select "
							+ " r.id, "
							+ " r.ativo, "
							+ " r.dataCadastro, "
							+ " r.dataEnvio, "
							+ " r.tipoRelatorio, "
							+ " r.dataValidacaoDepartamento, "
							+ " tipoParecerDepartamento, "
							+ " r.dataValidacaoProex, "
							+ " tipoParecerProex, "
							+ " atv.id, atv.sequencia, p.ano, p.titulo, atv.tipoAtividadeExtensao "
							+ " from RelatorioAcaoExtensao r "
							+ " left  join r.tipoParecerDepartamento tipoParecerDepartamento "
							+ " left  join r.tipoParecerProex tipoParecerProex "
							+ " inner join r.atividade atv " 
							+ " inner join atv.projeto p "
							+ " where (r.ativo = trueValue()) and " +
									"( ((r.dataValidacaoDepartamento is not null) and (r.tipoParecerDepartamento.id = :APROVADO)) and " +
									" (r.dataValidacaoProex is null) ) ");

			if (idsTipoAtividade != null)
				hql.append(" and atv.tipoAtividadeExtensao.id IN (:idsTipoAtividade) ");

			hql.append(" order by p.ano DESC, atv.tipoAtividadeExtensao.id, atv.sequencia ");

			Query query = getSession().createQuery(hql.toString());

			if (idsTipoAtividade != null)
				query.setParameterList("idsTipoAtividade", idsTipoAtividade);
			query.setInteger("APROVADO", TipoParecerAvaliacaoExtensao.APROVADO);

			List lista = query.list();

			ArrayList<RelatorioAcaoExtensao> result = new ArrayList<RelatorioAcaoExtensao>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				RelatorioAcaoExtensao rel = new RelatorioAcaoExtensao();
				rel.setId((Integer) colunas[col++]);
				rel.setAtivo((Boolean) colunas[col++]);
				rel.setDataCadastro((Date) colunas[col++]);
				rel.setDataEnvio((Date) colunas[col++]);
				rel.setTipoRelatorio((TipoRelatorioExtensao) colunas[col++]);
				rel.setDataValidacaoDepartamento((Date) colunas[col++]);
				rel.setTipoParecerDepartamento((TipoParecerAvaliacaoExtensao) colunas[col++]);
				rel.setDataValidacaoProex((Date) colunas[col++]);
				rel.setTipoParecerProex((TipoParecerAvaliacaoExtensao) colunas[col++]);

				AtividadeExtensao atividade = new AtividadeExtensao();
				atividade.setId((Integer) colunas[col++]);
				atividade.setSequencia((Integer) colunas[col++]);
				atividade.setAno((Integer) colunas[col++]);
				atividade.setTitulo((String) colunas[col++]);
				atividade.setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
				rel.setAtividade(atividade);

				result.add(rel);

			}

			return result;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Lista os relatórios onde o servidor é coordenador utilizado para listar
	 * todos os relatórios para o coordenador alterar de todos os projetos
	 * aprovados
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioAcaoExtensao> findAllRelatoriosCoordenador(
			int idServidor) throws DAOException {

		try {
			String hql = "select r from RelatorioAcaoExtensao r  "
					+ " inner join r.atividade atividade "
					+ " inner join atividade.projeto.equipe membro "
					+ " inner join membro.funcaoMembro funcao " + " where "
					+ " (atividade.projeto.situacaoProjeto.id IN  (:idSituacao))"
					+ " and membro.servidor.id = :idServidor "
					+ " and membro.ativo = trueValue() "
					+ " and funcao.id = :idFuncao " + " and r.ativo = trueValue() "
					+ " order by atividade.id";

			Query query = getSession().createQuery(hql);
			query.setParameterList("idSituacao", new Integer[] {
					TipoSituacaoProjeto.EXTENSAO_CONCLUIDO,
					TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO });
			query.setInteger("idServidor", idServidor);
			query.setInteger("idFuncao", FuncaoMembro.COORDENADOR);

			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	/**
	 * 
	 * Procura por ações de extensão pendentes quanto ao envio de relatórios parciais ou finais de uma determinada pessoa.
	 * 
	 * @param pessoa
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> findAcosPendentesRelatorio(Pessoa pessoa) throws HibernateException, DAOException {		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" select distinct atv.id, atv.dataEmailCobrancaRelatorioFinal, atv.sequencia, proj.id, tipoAtiv.id,tipoAtiv.descricao, proj.ano, proj.titulo, situacaoProj.id, " +
				   "        situacaoProj.descricao, proj.extensao, proj.dataInicio, proj.dataFim, pessoa.nome, pessoa.email, " +
				   "        rel.id, rel.dataEnvio, rel.dataValidacaoDepartamento, rel.dataValidacaoProex, tipoRel.descricao, tipoRel.id, rel.ativo, tipoParecerDepartamento,tipoParecerProex, " +
				   "        rel.parecerDepartamento, rel.parecerProex " +
				   " from AtividadeExtensao atv " +
				   " inner join atv.projeto proj " +
				   " inner join atv.tipoAtividadeExtensao tipoAtiv " +
				   " inner join proj.situacaoProjeto situacaoProj  " +
				   " inner join proj.coordenador as coord " +
				   " inner join coord.pessoa as pessoa " +
				   " left  join atv.relatorios rel " +
				   " left  join rel.tipoRelatorio tipoRel " +
				   " left  join rel.tipoParecerDepartamento tipoParecerDepartamento  " +
				   " left  join rel.tipoParecerProex tipoParecerProex  " +
				   " where pessoa.id = :idPessoa " +				   
				   " and situacaoProj.id IN (:SITUACOES_PROJETO) " +
				   " and (proj.tipoProjeto.id = :idTipoProjeto) " +
				   " and cast( now() as date) >= cast ( proj.dataFim as date ) " +
				   " and atv.id not in (" +
		   			" select rela.atividade.id" +
		   			" from RelatorioAcaoExtensao rela" +
		   			" where rela.atividade.id = atv.id" +
		   			" and rela.dataEnvio is not null" +
		   			" and rela.tipoRelatorio.id = :idTipoRelatorio" +
		   			" )" +
				   " order by  atv.id ");		
		
		Query query = getSession().createQuery(hql.toString());	
		
		// Obs.: Projetos concluídos não entrão aqui, se já está concluído teoricamente já entregou o relatório e não precisa verificar //
		query.setParameterList("SITUACOES_PROJETO", new Integer[] {TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO, TipoSituacaoProjeto.EXTENSAO_PENDENTE_DE_RELATORIO});
		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
		query.setInteger("idPessoa", pessoa.getId());
		query.setInteger("idTipoRelatorio", TipoRelatorioExtensao.RELATORIO_FINAL);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();

		ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
		
		for (int cont = 0; cont < lista.size(); cont++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(cont);
			
			AtividadeExtensao atv = new AtividadeExtensao();
			
			atv.setId((Integer) colunas[col++]);
			atv.setDataEmailCobrancaRelatorioFinal((Date) colunas[col++]);
			atv.setSequencia((Integer) colunas[col++]);
			atv.getProjeto().setId((Integer) colunas[col++]);
			atv.getTipoAtividadeExtensao().setId((Integer) colunas[col++]);
			atv.getTipoAtividadeExtensao().setDescricao((String) colunas[col++]);
			atv.getProjeto().setAno((Integer) colunas[col++]);
			atv.getProjeto().setTitulo((String) colunas[col++]);
			atv.getProjeto().getSituacaoProjeto().setId((Integer) colunas[col++]);
			atv.getProjeto().getSituacaoProjeto().setDescricao((String) colunas[col++]);
			atv.getProjeto().setExtensao((Boolean) colunas[col++]);
			atv.getProjeto().setDataInicio((Date) colunas[col++]);
			atv.getProjeto().setDataFim((Date) colunas[col++]);
			atv.getProjeto().setCoordenador(new MembroProjeto());
			atv.getProjeto().getCoordenador().getPessoa().setNome((String) colunas[col++]);
			atv.getProjeto().getCoordenador().getPessoa().setEmail((String) colunas[col++]);
			
			//Enquanto for  a mesma ação, adicione os relatórios.
			int i = cont;
			while(atv.getId() == (Integer) colunas[0])   {	
				
				col = 15;
				if(  colunas[15] != null) {
					RelatorioAcaoExtensao rel = new RelatorioAcaoExtensao();

					rel.setId((Integer) colunas[col++]);
					rel.setDataEnvio((Date) colunas[col++]);
					rel.setDataValidacaoDepartamento((Date) colunas[col++]);
					rel.setDataValidacaoProex((Date) colunas[col++]);
					rel.getTipoRelatorio().setDescricao((String) colunas[col++]);
					rel.getTipoRelatorio().setId((Integer) colunas[col++]);
					rel.setAtivo((Boolean) colunas[col++]);					
					rel.setTipoParecerDepartamento((TipoParecerAvaliacaoExtensao) colunas[col++]);
					rel.setTipoParecerProex((TipoParecerAvaliacaoExtensao) colunas[col++]);
					rel.setParecerDepartamento((String) colunas[col++]);
					rel.setParecerProex((String) colunas[col++]);					
					atv.getRelatorios().add(rel);
					
				}
				
				i++;
				if( i < lista.size())
					colunas = (Object[]) lista.get(i);
				else
					break;
				
				
				
			}
			cont =i-1;
			result.add(atv);
		}
		return result;		
	}
	
	/**
	 * 
	 * Informa se uma pessoa está pendente quanto ao envio de relatórios parciais ou finais
	 * 
	 * @param pessoa
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Boolean isPendenteRelatorios(Pessoa pessoa) throws HibernateException, DAOException {		
		return findAcosPendentesRelatorio(pessoa).size() > 0;
	}
	
}
