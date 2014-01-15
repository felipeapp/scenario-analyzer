/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '21/02/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/*******************************************************************************
 * Dao para realizar consultas sobre a lista de interessados em participar de
 * ações de extensão
 * 
 * @author ilueny santos
 * 
 ******************************************************************************/
public class InscricaoSelecaoExtensaoDao extends GenericSigaaDAO {

	/** Variável que define o número máximo de registros q deve retornar. */
	private static final long LIMITE_RESULTADOS = 1000;
	
	/**
	 * retorna uma InscricaoSelecaoExtensao do discente e atividade passado por
	 * parâmetro
	 * 
	 * @param discente
	 * @param atividade
	 * @return
	 * @throws DAOException
	 */
	public InscricaoSelecaoExtensao findByDiscenteAtividade(int idDiscente,
			int idAtividade) throws DAOException {

		String projecao = "insc.id, insc.discente.id, insc.atividade.id, insc.situacaoDiscenteExtensao.id, insc.discenteExtensao.id, insc.tipoVinculo.id";		
		String hql = "select " + projecao + " from InscricaoSelecaoExtensao insc where insc.discente.id = :idDiscente and insc.atividade.id = :idAtividade ";

		Query q = getSession().createQuery(hql);
		q.setInteger("idDiscente", idDiscente);
		q.setInteger("idAtividade", idAtividade);
		List<Object[]> lista = new ArrayList<Object[]>();
		lista.add((Object[]) q.uniqueResult());
		
		return HibernateUtils.parseTo(lista, projecao, InscricaoSelecaoExtensao.class, "insc").iterator().next();
		
	}

	/**
	 * Lista todos os discentes inscritos para seleção que ainda não foram
	 * selecionados
	 * 
	 * @param idAtividade
	 * @return
	 */
	public Collection<InscricaoSelecaoExtensao> findInscritosProcessoSeletivoByAtividade(
			int idAtividade) throws DAOException {

		String hql = "select i.id, p.id, p.nome, d.id, d.matricula from InscricaoSelecaoExtensao i "
				+ "inner join i.atividade a "
				+ "inner join i.situacaoDiscenteExtensao s "
				+ "inner join i.discente d "
				+ "inner join d.pessoa p "
				+ " where a.id = :idAtividade and s.id = :idAguardandoSelecao ";

		Query q = getSession().createQuery(hql);
		q.setInteger("idAtividade", idAtividade);
		q.setInteger("idAguardandoSelecao",
				TipoSituacaoDiscenteExtensao.INSCRITO_PROCESSO_SELETIVO);
		
		@SuppressWarnings("unchecked")
		List lista = q.list();

		Collection<InscricaoSelecaoExtensao> result = new ArrayList<InscricaoSelecaoExtensao>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);

			InscricaoSelecaoExtensao i = new InscricaoSelecaoExtensao();
			i.setId((Integer) colunas[col++]);

			Pessoa p = new Pessoa();
			p.setId((Integer) colunas[col++]);
			p.setNome((String) colunas[col++]);

			Discente d = new Discente();
			d.setPessoa(p);
			d.setId((Integer) colunas[col++]);
			d.setMatricula((Long) colunas[col++]);

			i.setDiscente(d);
			result.add(i);
		}

		return result;
	}

	/**
	 * Esse método serve para localizar as ações que se encontram disponíveis. 
	 * Que seja estejam em execução. 
	 * 
	 * @param palavraChave
	 * @param idUnidadeProponente
	 * @param idCentro
	 * @param idAreaTematicaPrincipal
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> localizarAcoesDisponiveis(
			String palavraChave, Integer idUnidadeProponente, Integer idCentro,
			Integer idAreaTematicaPrincipal) throws DAOException {

		try {

			StringBuilder hqlCount = new StringBuilder();
			hqlCount.append(" SELECT  count(distinct atv.id) "
					+ "FROM AtividadeExtensao atv "
					+ "INNER JOIN atv.projeto pj "
					+ "LEFT OUTER JOIN pj.equipe as me "
					+ "LEFT OUTER JOIN me.pessoa as pessoa ");
			hqlCount.append(" WHERE 1 = 1 ");

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta
					.append(" SELECT distinct atv.id, "
							+ "pj.ano, pj.titulo, "
							+ "pj.unidade.id, pj.unidade.sigla, "
							+ "pj.unidade.nome, pj.situacaoProjeto.id, pj.situacaoProjeto.descricao, "
							+ "atv.tipoAtividadeExtensao.id, atv.tipoAtividadeExtensao.descricao, "
							+ "atv.areaTematicaPrincipal.id, atv.areaTematicaPrincipal.descricao, atv.sequencia, atv.bolsasSolicitadas, atv.bolsasConcedidas, "
							+ "me.id, pessoa.id, pessoa.nome, me.funcaoMembro.id, me.ativo  "
							+ "FROM AtividadeExtensao atv "
							+ "INNER JOIN atv.projeto pj "
							+ "LEFT OUTER JOIN pj.equipe as me "
							+ "LEFT OUTER JOIN me.pessoa as pessoa ");

			hqlConsulta
					.append(" WHERE 1=1 ");
			

			StringBuilder hqlFiltros = new StringBuilder();
			
			hqlFiltros.append(" AND pj.situacaoProjeto.id = :idSituacaoEmExecucao ");
			
			// Filtros para a busca
			if (palavraChave != null) {
				hqlFiltros.append(" AND (("
						+ UFRNUtils.toAsciiUpperUTF8("pj.titulo") + " like "
						+ UFRNUtils.toAsciiUTF8(":palavraChave"));
				hqlFiltros.append(") OR ("
						+ UFRNUtils.toAsciiUpperUTF8("pj.objetivos") + " like "
						+ UFRNUtils.toAsciiUTF8(":palavraChave"));
				hqlFiltros.append(" ))");
			}

			if (idUnidadeProponente != null) {
				hqlFiltros.append(" AND pj.unidade.id = :idUnidadeProponente");
			}

			if (idCentro != null) {
				hqlFiltros.append(" AND pj.unidade.gestora.id = :idCentro");
			}

			if (idAreaTematicaPrincipal != null) {
				hqlFiltros
						.append(" AND atv.areaTematicaPrincipal.id = :idAreaTematicaPrincipal");
			}

			hqlCount.append(hqlFiltros.toString());
			hqlConsulta.append(hqlFiltros.toString());
			hqlConsulta.append(" ORDER BY pj.unidade.sigla ");

			// Criando consulta
			Query queryCount = getSession().createQuery(hqlCount.toString());
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

			// só ações em execução
			queryCount.setInteger("idSituacaoEmExecucao", TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO);
			queryConsulta.setInteger("idSituacaoEmExecucao", TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO);


			// Populando os valores dos filtros
			if (palavraChave != null) {
				queryCount.setString("palavraChave", "%" + palavraChave.toUpperCase() + "%");
				queryConsulta.setString("palavraChave", "%" + palavraChave.toUpperCase() + "%");
			}

			if (idUnidadeProponente != null) {
				queryCount.setInteger("idUnidadeProponente",
						idUnidadeProponente);
				queryConsulta.setInteger("idUnidadeProponente",
						idUnidadeProponente);
			}

			if (idCentro != null) {
				queryCount.setInteger("idCentro", idCentro);
				queryConsulta.setInteger("idCentro", idCentro);
			}

			if (idAreaTematicaPrincipal != null) {
				queryCount.setInteger("idAreaTematicaPrincipal",
						idAreaTematicaPrincipal);
				queryConsulta.setInteger("idAreaTematicaPrincipal",
						idAreaTematicaPrincipal);
			}

			Long total = (Long) queryCount.uniqueResult();

			if (total > LIMITE_RESULTADOS)
				throw new LimiteResultadosException("A consulta retornou "
						+ total
						+ " resultados. Por favor, restrinja mais a busca.");

			@SuppressWarnings("unchecked")
			List lista = queryConsulta.list();

			/*atv.id, atv.ano, atv.titulo, atv.unidade.id, atv.unidade.sigla,atv.unidade.nome, atv.situacaoProjeto.id, atv.situacaoProjeto.descricao,
			atv.tipoAtividadeExtensao.id, atv.tipoAtividadeExtensao.descricao,
			atv.areaTematicaPrincipal.id, atv.areaTematicaPrincipal.descricao, atv.sequencia, atv.bolsasSolicitadas, atv.bolsasConcedidas,
			me.id, pessoa.id, pessoa.nome, me.funcaoMembro.id, me.ativo*/			
			ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();

			int idOld = 0;
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				AtividadeExtensao at = new AtividadeExtensao();

				int idNew = (Integer) colunas[col++];
				at.setId(idNew);

				if (idOld != idNew) {

					idOld = idNew;

					at.setAno((Integer) colunas[col++]);
					at.setTitulo((String) colunas[col++]);

					Unidade unidade = new Unidade();
					unidade.setId((Integer) colunas[col++]);
					unidade.setSigla((String) colunas[col++]);
					unidade.setNome((String) colunas[col++]);
					at.setUnidade(unidade);

					TipoSituacaoProjeto situacao = new TipoSituacaoProjeto();
					situacao.setId((Integer) colunas[col++]);
					situacao.setDescricao((String) colunas[col++]);
					at.setSituacaoProjeto(situacao);

					TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();
					tipo.setId((Integer) colunas[col++]);
					tipo.setDescricao((String) colunas[col++]);
					at.setTipoAtividadeExtensao(tipo);

					AreaTematica area = new AreaTematica();
					area.setId((Integer) colunas[col++]);
					area.setDescricao((String) colunas[col++]);
					at.setAreaTematicaPrincipal(area);

					at.setSequencia((Integer) colunas[col++]);
					at.setBolsasSolicitadas((Integer) colunas[col++]);
					at.setBolsasConcedidas((Integer) colunas[col++]);
					result.add(at);
				}

				// Adicionando o coordenador na ação encontrada...
				if (((Integer) colunas[16] != null)
						&& ((Integer) colunas[16] == FuncaoMembro.COORDENADOR)
						&& ((Boolean) colunas[17])) {
					MembroProjeto m = new MembroProjeto();
					m.setId((Integer) colunas[13]);
					Pessoa p = new Pessoa();
					p.setId((Integer) colunas[14]);
					p.setNome((String) colunas[15]);
					m.setPessoa(p);
					m.setFuncaoMembro(new FuncaoMembro((Integer) colunas[16]));
					m.setAtivo((Boolean) colunas[17]);

					result.get(result.indexOf(at)).getMembrosEquipe().add(m);
				}

			}

			return result;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new DAOException(ex.getMessage(), ex);
		}
	}

	/**
	 * Serve para carregar o carregar o id do usuário de acordo com o cpf do mesmo.
	 *  
	 * @param cpf
	 * @return
	 */
	public Integer findUsuarioGeralParaEnvioMensagem( long cpf ){
		return  (Integer) getJdbcTemplate(getDataSource(Sistema.COMUM)).queryForObject("select u.id_usuario from comum.usuario u join comum.pessoa p using ( id_pessoa ) where p.cpf_cnpj=?", 
				new Object[] { cpf }, Integer.class);
	}
	
}