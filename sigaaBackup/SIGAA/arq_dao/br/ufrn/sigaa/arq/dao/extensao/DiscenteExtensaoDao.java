/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 23/04/2009
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.PlanoTrabalhoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;


/**
 * Dao responsável por realizar as consultas
 * relacionadas a discentes de extensão e suas
 * atividades.
 * 
 * 
 * @author UFRN
 *
 */
public class DiscenteExtensaoDao extends GenericSigaaDAO {

	/** Constante que determina o limite de resultados retornados na busca. */
	private static final long LIMITE_RESULTADOS = 1000;

	/** Construtor padrão. */
	public DiscenteExtensaoDao() {
	}

	/**
	 * Retorna o total de bolsistas ativos em uma atividade de extensão.
	 * 
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public int findTotalBolsistasByAtividade(int idAtividade)
			throws DAOException {

		try {
			String hql = "select count(*) from DiscenteExtensao de "
					+ "inner join de.situacaoDiscenteExtensao s "
					+ "inner join de.planoTrabalhoExtensao pt "
					+ "inner join de.tipoVinculo tv "
					+ "inner join pt.atividade a "
					+ "where a.id = :idAtividade and "
					+ "pt.ativo = trueValue() and s.id = :idAtivo and de.ativo = trueValue() and tv.id = :idBolsista";
			Query query = getSession().createQuery(hql);
			query.setInteger("idAtividade", idAtividade);
			query.setInteger("idAtivo", TipoSituacaoDiscenteExtensao.ATIVO);
			query.setInteger("idBolsista", TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO);

			if (query.uniqueResult() != null)
				return ((Long) query.uniqueResult()).intValue();
			else
				return 0;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna DiscenteExtensao com o status passado, caso nulo, desconsidera o
	 * status.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public DiscenteExtensao findByDiscenteExtensao(int idDiscente, Integer idTipoSituacaoDiscenteExtensao)
			throws DAOException {

		try {

			String hql = "SELECT de.id as id, de.dataInicio as dataInicio, de.dataFim as dataFim, " +
					" de.situacaoDiscenteExtensao as situacaoDiscenteExtensao, de.tipoVinculo as tipoVinculo " +
					" from DiscenteExtensao de " + 
					" WHERE (de.discente.id = :idDiscente) AND (de.ativo = trueValue()) ";
			

			if (idTipoSituacaoDiscenteExtensao != null) {
				hql += " AND (de.situacaoDiscenteExtensao.id = :idTipoSituacaoDiscenteExtensao)";
			}

			Query q = getSession().createQuery(hql);
			q.setInteger("idDiscente", idDiscente);

			if (idTipoSituacaoDiscenteExtensao != null) {
				q.setInteger("idTipoSituacaoDiscenteExtensao", idTipoSituacaoDiscenteExtensao);
			}

			if (q.list().size() > 1) {
				return null;
			}

			return (DiscenteExtensao) q.setResultTransformer(
					Transformers.aliasToBean(DiscenteExtensao.class))
					.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	/**
	 * Retorna todos os discentes com o tipo de situação informado.
	 * 
	 * @param idTipoSituacaoDiscenteExtensao
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteExtensao> findDiscenteExtensaoBySituacao(Integer idTipoSituacaoDiscenteExtensao, Integer idTipoVinculo)
	throws DAOException {

		try {

			String hql = " SELECT distinct de.id, de.dataInicio, de.dataFim, de.dataCadastro, " +
					"de.planoTrabalhoExtensao.id, d.id, d.matricula, d.pessoa.nome, d.curso.nome " +
						 " from DiscenteExtensao de " +
						 " inner join de.discente d " + 
						 " WHERE 1 = 1 ";	

			if (idTipoSituacaoDiscenteExtensao != null) {
				hql += " AND (de.situacaoDiscenteExtensao.id = :idTipoSituacaoDiscenteExtensao)";
			}


			if (idTipoVinculo != null) {
				hql += " AND (de.tipoVinculo.id = :idTipoVinculo)";
			}

			Query q = getSession().createQuery(hql);
			

			if (idTipoSituacaoDiscenteExtensao != null) {
				q.setInteger("idTipoSituacaoDiscenteExtensao", idTipoSituacaoDiscenteExtensao);
			}
			
			if (idTipoVinculo != null) {
				q.setInteger("idTipoVinculo", idTipoVinculo);
			}
			
			@SuppressWarnings("unchecked")
			List<Object> lista = q.list();

			ArrayList<DiscenteExtensao> result = new ArrayList<DiscenteExtensao>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				
				Object[] colunas = (Object[]) lista.get(a);
				DiscenteExtensao de = new DiscenteExtensao();
				de.setId((Integer) colunas[col++]);				
				de.setDataInicio((Date) colunas[col++]);
				de.setDataFim((Date) colunas[col++]);
				de.setDataCadastro((Date) colunas[col++]);
				de.setPlanoTrabalhoExtensao(new PlanoTrabalhoExtensao((Integer) colunas[col++]));
				de.getDiscente().setId((Integer) colunas[col++]);
				de.getDiscente().setMatricula((Long) colunas[col++]);
				Pessoa p = new Pessoa();
				p.setNome((String) colunas[col++]);
				de.getDiscente().setPessoa(p);
				Curso c = new Curso();
				c.setNome((String) colunas[col++]);
				de.getDiscente().setCurso(c);
				
				result.add(de);
			}
			
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna DiscenteExtensao com o status passado, caso nulo, desconsidera o
	 * status cadastrado em algum plano de trabalho de extensão
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */	
	@SuppressWarnings("unchecked")
	public Collection<DiscenteExtensao> findByDiscenteComPlanoTrabalho(
			int idDiscente, Integer idTipoSituacaoDiscenteExtensao) throws DAOException {

		try {

			String hql ="SELECT de " +
						"from DiscenteExtensao de" +
						" inner join de.planoTrabalhoExtensao plano " +
						" inner join plano.atividade ativ " +
						" inner join ativ.situacaoProjeto  situacaoAtv " +
						" inner join ativ.projeto proj " +
						" WHERE (de.discente.id = :idDiscente)" +
						" AND (de.ativo = trueValue()) " +
						" AND (plano.ativo = trueValue()) " +
						" AND (plano.dataEnvio is not null) " +
						" AND (situacaoAtv.id = :EXTENSAO_EM_EXECUCAO or situacaoAtv.id = :EXTENSAO_CONCLUIDO )";

			if (idTipoSituacaoDiscenteExtensao != null) {
				hql += " AND (de.situacaoDiscenteExtensao.id = :idTipoSituacaoDiscenteExtensao)";
			}

			Query q = getSession().createQuery(hql);
			q.setInteger("idDiscente", idDiscente);

			if (idTipoSituacaoDiscenteExtensao != null) {
				q.setInteger("idTipoSituacaoDiscenteExtensao", idTipoSituacaoDiscenteExtensao);
			}
			q.setInteger("EXTENSAO_EM_EXECUCAO", TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO);
			q.setInteger("EXTENSAO_CONCLUIDO", TipoSituacaoProjeto.EXTENSAO_CONCLUIDO);

			return q.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Método para buscar os discentes de extensão de acordo com uma série de
	 * filtros opcionais
	 * 
	 * @param idAtividade
	 *            id do atividade
	 * @param idSituacaoDiscente
	 *            id da situação do DiscenteExtensao
	 * @param tipoVinculo
	 *            tipo de vinculo com a atividade de extensão (bolsista,
	 *            voluntário)
	 * @param anoAtividade
	 *            - usado para o Prodocente
	 * @param dataFimPlano data de início do plano de trabalho
	 * @param dataInicioPlano fim do plano de trabalho
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */	
	public Collection<DiscenteExtensao> filter(Integer idEdital, String titulo,
			Integer idDiscente, Integer idSituacaoDiscenteExtensao,
			Integer anoAtividade, Boolean discenteAtivo, Integer idCurso,
			Integer idVinculo, Date dataInicioPlano, Date dataFimPlano,
			Date dataInicioBolsa, Date dataFimBolsa, Date dataInicioFinalizacao,
			Date dataFimFinalizacao, Date dataInicioCadastro, Date dataFimCadastro, boolean ordenarDataCadastro) throws DAOException, LimiteResultadosException {

		StringBuilder hqlCount = new StringBuilder();
		hqlCount.append(" SELECT  count(distinct de.id) "
			+ " FROM DiscenteExtensao as de "						
			+ " LEFT JOIN de.banco as banco "
			+ " JOIN de.situacaoDiscenteExtensao as situacaoDiscente "
			+ " JOIN de.planoTrabalhoExtensao as plano "
			+ " JOIN de.discente as d "
			+ " LEFT JOIN d.curso as curso "
			+ " LEFT JOIN d.pessoa as p "
			+ " LEFT JOIN p.enderecoContato endereco "
			+ " LEFT JOIN endereco.municipio municipio "
			+ " LEFT JOIN municipio.unidadeFederativa uf "						
			+ " JOIN plano.atividade as atividade "
			+ " LEFT JOIN atividade.editalExtensao as editalExtensao "
			+ " JOIN atividade.tipoAtividadeExtensao ta "						
			+ " JOIN atividade.projeto as projeto "
			+ " LEFT JOIN projeto.equipe as mequip "
			+ " LEFT JOIN projeto.unidade as unidade "
				);
		hqlCount.append(" WHERE 1 = 1 ");

		StringBuilder hqlConsulta = new StringBuilder();


		hqlConsulta.append(" SELECT DISTINCT de.id, de.ativo, de.dataCadastro, de.dataInicio, de.dataFim, "
						
						+ " banco.codigo, banco.denominacao, de.agencia, de.conta, de.operacao, "
						
						+ " situacaoDiscente.id, situacaoDiscente.descricao, "
						+ " de.tipoVinculo.id, de.tipoVinculo.descricao, "
						
						+ " d.id, d.matricula, d.nivel, p.id, p.nome, endereco.logradouro,  "						
						+ " endereco.numero, endereco.complemento, endereco.bairro, "
						+ " endereco.cep, municipio.nome,  uf.sigla, "
						
						+ " p.telefone, p.celular, p.email, p.cpf_cnpj, p.dataNascimento, "
						+ " plano.id, plano.dataInicio, plano.dataFim, plano.dataEnvio, "
						+ " atividade.id, projeto.ano, unidade.id, "
						+ " projeto.titulo, atividade.sequencia, "
						+ " ta.id, curso.id, curso.nome, " 
						+ " mequip.id, mequip.pessoa, mequip.funcaoMembro, mequip.ativo "
						
						+ " FROM DiscenteExtensao as de "						
						+ " LEFT JOIN de.banco as banco "
						+ " JOIN de.situacaoDiscenteExtensao as situacaoDiscente "
						+ " JOIN de.planoTrabalhoExtensao as plano "
						+ " JOIN de.discente as d "
						+ " LEFT JOIN d.curso as curso "
						+ " LEFT JOIN d.pessoa as p "
						+ " LEFT JOIN p.enderecoContato endereco "
						+ " LEFT JOIN endereco.municipio municipio "
						+ " LEFT JOIN municipio.unidadeFederativa uf "						
						+ " JOIN plano.atividade as atividade "
						+ " LEFT JOIN atividade.editalExtensao as editalExtensao "
						+ " JOIN atividade.tipoAtividadeExtensao ta "						
						+ " JOIN atividade.projeto as projeto "
						+ " LEFT JOIN projeto.equipe as mequip "
						+ " LEFT JOIN projeto.unidade as unidade "						
						);
		hqlConsulta.append(" WHERE 1 = 1 ");

		StringBuilder hqlFiltros = new StringBuilder();

		// Filtros para a busca
		if ((dataInicioCadastro != null) && (dataFimCadastro != null)) {
			hqlFiltros.append( " AND cast(de.dataCadastro as date) >= :dataInicioCadastro AND cast(de.dataCadastro as date) <= :dataFimCadastro " );
		}
		
		if ((dataInicioBolsa != null) && (dataFimBolsa != null)) {
			hqlFiltros.append( " AND cast(de.dataInicio as date) >= :dataInicioBolsa AND cast(de.dataInicio as date) <= :dataFimBolsa " );
		}
		
		if ((dataInicioFinalizacao != null) && (dataFimFinalizacao != null)) {
			hqlFiltros.append( " AND cast(de.dataFim as date) >= :dataInicioFinalizacao AND cast(de.dataFim as date) <= :dataFimFinalizacao " );
		}		
		
		if (titulo != null) {
			hqlFiltros.append(" AND "
					+ UFRNUtils.toAsciiUpperUTF8("projeto.titulo") + " like "
					+ UFRNUtils.toAsciiUTF8(":titulo"));
		}

		if (idSituacaoDiscenteExtensao != null) {
			hqlFiltros.append(" AND situacaoDiscente.id = :idSituacaoDiscenteExtensao ");
		}

		if (idVinculo != null) {
			hqlFiltros.append(" AND de.tipoVinculo.id = :idVinculo ");
		}

		if (anoAtividade != null) {
			hqlFiltros.append(" AND (extract(year from projeto.dataInicio) <= :anoAtividade and extract(year from projeto.dataFim) >= :anoAtividade) ");
		}
		
		if (dataInicioPlano != null && dataFimPlano != null) {
			hqlFiltros.append(" AND (plano.dataInicio >= :dataInicioPlano AND plano.dataFim <= :dataFimPlano) ");
		}

		if (idEdital != null) {
			hqlFiltros.append(" AND editalExtensao.id = :idEdital ");
		}

		if (idDiscente != null) {
			hqlFiltros.append(" AND d.id = :idDiscente ");
		}

		if (idCurso != null) {
			hqlFiltros.append(" AND curso.id = :idCurso ");
		}

		if (discenteAtivo != null) {
			hqlFiltros.append(" AND de.ativo = :discenteAtivo ");
		}

		// Não mostra os excluídos nem o com cadastro em andamento...
		hqlFiltros.append(" AND situacaoDiscente.id != :idExcluido AND plano.dataEnvio is not null ");

		hqlCount.append(hqlFiltros.toString());
		hqlConsulta.append(hqlFiltros.toString());
		
		if (!ordenarDataCadastro)
			hqlConsulta.append(" ORDER BY projeto.ano DESC, projeto.titulo, atividade.sequencia, atividade.id, p.nome, de.dataCadastro ");
		else
			hqlConsulta.append(" ORDER BY de.dataCadastro DESC, projeto.titulo, atividade.sequencia, atividade.id, p.nome ");

		/*
		 * DiscenteExtensao discenteExtensao = null;
		 * discenteExtensao.getPlanoTrabalhoExtensao
		 * ().getAtividade().getCodigoTitulo();
		 */
		// Criando consulta
		Query queryCount = getSession().createQuery(hqlCount.toString());
		Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

		// Não mostrar nem contar com os excluídos
		queryCount.setInteger("idExcluido",
				TipoSituacaoDiscenteExtensao.EXCLUIDO);
		queryConsulta.setInteger("idExcluido",
				TipoSituacaoDiscenteExtensao.EXCLUIDO);

		// Populando os valores dos filtros
		if ((dataInicioCadastro != null) && (dataFimCadastro != null)) {
			queryCount.setDate("dataInicioCadastro", dataInicioCadastro);
			queryCount.setDate("dataFimCadastro", dataFimCadastro);
			queryConsulta.setDate("dataInicioCadastro", dataInicioCadastro);
			queryConsulta.setDate("dataFimCadastro", dataFimCadastro);
		}
		
		if ((dataInicioBolsa != null) && (dataFimBolsa != null)) {
			queryCount.setDate("dataInicioBolsa", dataInicioBolsa);
			queryCount.setDate("dataFimBolsa", dataFimBolsa);
			queryConsulta.setDate("dataInicioBolsa", dataInicioBolsa);
			queryConsulta.setDate("dataFimBolsa", dataFimBolsa);
		}
		
		if ((dataInicioFinalizacao != null) && (dataFimFinalizacao != null)) {
			queryCount.setDate("dataInicioFinalizacao", dataInicioFinalizacao);
			queryCount.setDate("dataFimFinalizacao", dataFimFinalizacao);
			queryConsulta.setDate("dataInicioFinalizacao", dataInicioFinalizacao);
			queryConsulta.setDate("dataFimFinalizacao", dataFimFinalizacao);
		}
		
		if (titulo != null) {
			queryCount.setString("titulo", "%" + titulo.toUpperCase() + "%");
			queryConsulta.setString("titulo", "%" + titulo.toUpperCase() + "%");
		}

		if (idDiscente != null) {
			queryCount.setInteger("idDiscente", idDiscente);
			queryConsulta.setInteger("idDiscente", idDiscente);
		}

		if (idCurso != null) {
			queryCount.setInteger("idCurso", idCurso);
			queryConsulta.setInteger("idCurso", idCurso);
		}

		if (idSituacaoDiscenteExtensao != null) {
			queryCount.setInteger("idSituacaoDiscenteExtensao",
					idSituacaoDiscenteExtensao);
			queryConsulta.setInteger("idSituacaoDiscenteExtensao",
					idSituacaoDiscenteExtensao);
		}

		// Tipo de vínculo do discente : bolsista, voluntário, em
		if (idVinculo != null) {
			queryCount.setInteger("idVinculo", idVinculo);
			queryConsulta.setInteger("idVinculo", idVinculo);
		}

		if (anoAtividade != null) {
			queryCount.setInteger("anoAtividade", anoAtividade);
			queryConsulta.setInteger("anoAtividade", anoAtividade);
		}
		
		if (dataInicioPlano != null && dataFimPlano != null) {
			queryCount.setDate("dataInicioPlano", dataInicioPlano);
			queryCount.setDate("dataFimPlano", dataFimPlano);
			queryConsulta.setDate("dataInicioPlano", dataInicioPlano);
			queryConsulta.setDate("dataFimPlano", dataFimPlano);
		}

		if (idEdital != null) {
			queryCount.setInteger("idEdital", idEdital);
			queryConsulta.setInteger("idEdital", idEdital);
		}

		if (discenteAtivo != null) {
			queryCount.setBoolean("discenteAtivo", discenteAtivo);
			queryConsulta.setBoolean("discenteAtivo", discenteAtivo);
		}

		Long total = (Long) queryCount.uniqueResult();

		if (total > LIMITE_RESULTADOS)
			throw new LimiteResultadosException("A consulta retornou " + total
					+ " resultados. Por favor, restrinja mais a busca.");

		@SuppressWarnings("unchecked")
		List<Object> lista = queryConsulta.list();

		ArrayList<DiscenteExtensao> result = new ArrayList<DiscenteExtensao>();
		for (int a = 0; a < lista.size(); a++) {

			int col = 0;
			
			Object[] colunas = (Object[]) lista.get(a);

			DiscenteExtensao de1 = new DiscenteExtensao((Integer) colunas[0]);
			Discente dis1 = new Discente((Integer) colunas[14]);
			de1.setDiscente(dis1);

			if (!result.contains(de1)) {

				DiscenteExtensao de = new DiscenteExtensao();
				de.setId((Integer) colunas[col++]);
				de.setAtivo((Boolean) colunas[col++]);
				de.setDataCadastro((Date) colunas[col++]);
				de.setDataInicio((Date) colunas[col++]);
				de.setDataFim((Date) colunas[col++]);

				Banco banco = new Banco();

				Integer codigo = (Integer) colunas[col++];
				if (codigo != null)
					banco.setCodigo(codigo);

				String nome = (String) colunas[col++];
				if (nome != null)
					banco.setDenominacao(nome);

				de.setBanco(banco);
				de.setAgencia((String) colunas[col++]);
				de.setConta((String) colunas[col++]);
				de.setOperacao((String) colunas[col++]);

				TipoSituacaoDiscenteExtensao situacao = new TipoSituacaoDiscenteExtensao();
				situacao.setId((Integer) colunas[col++]);
				situacao.setDescricao((String) colunas[col++]);
				de.setSituacaoDiscenteExtensao(situacao);

				TipoVinculoDiscente vinculo = new TipoVinculoDiscente();
				vinculo.setId((Integer) colunas[col++]);
				vinculo.setDescricao((String) colunas[col++]);
				de.setTipoVinculo(vinculo);

				Discente discente = new Discente();
				discente.setId((Integer) colunas[col++]);
				discente.setMatricula((Long) colunas[col++]);
				discente.setNivel((Character) colunas[col++]);				
				Pessoa p = new Pessoa((Integer) colunas[col++]);
				p.setNome((String) colunas[col++]);				
				
				p.setEnderecoContato(new Endereco());
				p.getEnderecoContato().setLogradouro((String) colunas[col++]);
				p.getEnderecoContato().setNumero((String) colunas[col++]);
				p.getEnderecoContato().setComplemento((String) colunas[col++]);
				p.getEnderecoContato().setBairro((String) colunas[col++]);
				p.getEnderecoContato().setCep((String) colunas[col++]);
				p.getEnderecoContato().setMunicipio(new Municipio());
				p.getEnderecoContato().getMunicipio().setUnidadeFederativa(new UnidadeFederativa());
				p.getEnderecoContato().getMunicipio().setNome((String) colunas[col++]);
				p.getEnderecoContato().getMunicipio().getUnidadeFederativa().setSigla((String) colunas[col++]);
				p.setTelefone((String) colunas[col++]);
				p.setCelular((String) colunas[col++]);
				p.setEmail((String) colunas[col++]);
				p.setCpf_cnpj((Long) colunas[col++]);
				p.setDataNascimento((Date) colunas[col++]);
				
				discente.setPessoa(p);
				de.setDiscente(discente);

				PlanoTrabalhoExtensao plano = new PlanoTrabalhoExtensao();
				plano.setId((Integer) colunas[col++]);
				plano.setDataInicio((Date) colunas[col++]);
				plano.setDataFim((Date) colunas[col++]);
				plano.setDataEnvio((Date) colunas[col++]);
				AtividadeExtensao atividade = new AtividadeExtensao();
				atividade.setId((Integer) colunas[col++]);				
				atividade.setAno((Integer) colunas[col++]);
				atividade.setUnidade(new Unidade((Integer) colunas[col++]));
				atividade.setTitulo((String) colunas[col++]);
				atividade.setSequencia((Integer) colunas[col++]);
				atividade.setTipoAtividadeExtensao(new TipoAtividadeExtensao(
						(Integer) colunas[col++]));
				//atividade.setMembrosEquipe(new ArrayList<MembroProjeto>());
				
				//Se for aluno regular (aluno especial não tem curso)
				
				if ((Integer) colunas[41] != null) {
						//int i = colunas.length;
        				Curso curso = new Curso();
        				curso.setId((Integer) colunas[col++]);
        				curso.setNome((String) colunas[col++]);
        				de.getDiscente().setCurso(curso);
				}
				
				if ((Integer) colunas[43] != null) {
					MembroProjeto mp = new MembroProjeto();
					if(col!=43)
						col = 43;
					mp.setId((Integer) colunas[col++]);
					mp.setPessoa((Pessoa) colunas[col++]);
					mp.setFuncaoMembro((FuncaoMembro) colunas[col++]);
					mp.setAtivo((Boolean) colunas[col++]);
					atividade.getMembrosEquipe().add(mp);
					plano.setAtividade(atividade);				
				}
				de.setPlanoTrabalhoExtensao(plano);
				result.add(de);
			}
		}

		return result;

	}

	/**
	 * Retorna a lista de todos os discentes de extensão ativos de um
	 * determinado edital e vínculo informados. Usado principalmente 
	 * na homologação de bolsas de extensão.
	 * 
	 * 
	 * @param idEdital
	 * @param idVinculo
	 * @return Lista de discentes encontrados
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<DiscenteExtensao> findByEdital(Integer idEdital,
			Integer idVinculo) throws DAOException, LimiteResultadosException {
		
		
			
			StringBuilder hql = new StringBuilder();
			hql.append(
					" select de.id, de.dataCadastro, de.dataInicio, de.dataFim, de.agencia, de.conta, de.operacao, banco.codigo, " +
					" 		 tipoVinculo.id, tipoVinculo.descricao, discente.matricula, discente.nivel, " +
					"		 pessoa.id, pessoa.nome, curso.id, curso.nome, " +
					"        tipoAtiv.id, tipoAtiv.descricao, at.sequencia, und.id, und.nome, proj.ano, plano.id " +
					" from DiscenteExtensao de " +
					" inner join de.discente discente " +					
					" inner join de.planoTrabalhoExtensao plano " +
					" inner join de.tipoVinculo tipoVinculo " +
					" inner join de.banco banco " +
					" inner join discente.pessoa pessoa " +
					" inner join discente.curso curso " +
					" inner join plano.atividade at " +					
					" inner join at.projeto proj " +
					" inner join proj.unidade und " +
					" inner join at.tipoAtividadeExtensao tipoAtiv " +
					" inner join at.editalExtensao edital " +
					" where de.ativo = trueValue() and plano.ativo = trueValue() "				   
					   );
			
			if( idEdital != null) {
				hql.append(" and edital.id = :idEdital ");
			}
			
			if( idVinculo != null) {
				hql.append(" and tipoVinculo.id = :idVinculo ");
			}
			hql.append(" order by proj.ano DESC ");
			
			Query q = getSession().createQuery(hql.toString());
			
			if( idEdital != null) {
				q.setInteger("idEdital", idEdital);
			}
			
			if( idVinculo != null) {
				q.setInteger("idVinculo", idVinculo);
			}
			
			@SuppressWarnings("unchecked")
			Collection<Object> objetos = q.list();
			Collection<DiscenteExtensao> discentes = new ArrayList<DiscenteExtensao>();
			
			for(Object obj : objetos) {
				
				int col = 0;
				Object[] dados = (Object[]) obj;
				
				
				DiscenteExtensao de = new DiscenteExtensao();
				de.setId((Integer)dados[col++]);
				de.setDataCadastro((Date)dados[col++]);
				de.setDataInicio((Date)dados[col++]);
				de.setDataFim((Date)dados[col++]);
				de.setAgencia((String)dados[col++]);
				de.setConta((String)dados[col++]);
				de.setOperacao((String)dados[col++]);
				de.setBanco(new Banco());
				de.getBanco().setCodigo((Integer)dados[col++]);
				de.getTipoVinculo().setId((Integer)dados[col++]);
				de.getTipoVinculo().setDescricao((String)dados[col++]);
				de.getDiscente().setMatricula((Long)dados[col++]);				
				de.getDiscente().setNivel((Character)dados[col++]);
				de.getDiscente().getPessoa().setId((Integer)dados[col++]);
				de.getDiscente().getPessoa().setNome((String)dados[col++]);
				de.getDiscente().getCurso().setId((Integer)dados[col++]);
				de.getDiscente().getCurso().setNome((String)dados[col++]);
				
				de.getPlanoTrabalhoExtensao().getAtividade().setTipoAtividadeExtensao(new TipoAtividadeExtensao());
				de.getPlanoTrabalhoExtensao().getAtividade().getTipoAtividadeExtensao().setId((Integer)dados[col++]);
				de.getPlanoTrabalhoExtensao().getAtividade().getTipoAtividadeExtensao().setDescricao((String)dados[col++]);
				de.getPlanoTrabalhoExtensao().getAtividade().setSequencia((Integer)dados[col++]);
				de.getPlanoTrabalhoExtensao().getAtividade().getUnidade().setId((Integer)dados[col++]);
				de.getPlanoTrabalhoExtensao().getAtividade().getUnidade().setNome((String)dados[col++]);
				de.getPlanoTrabalhoExtensao().getAtividade().getProjeto().setAno((Integer)dados[col++]);
				de.getPlanoTrabalhoExtensao().setId((Integer)dados[col++]);
				
				
				discentes.add(de);
				
			}		
			
			return discentes;
			
		}
	

	/**
	 * Lista de todos os ids de discentes de extensão que já solicitaram cadastro de bolsas no sipac via sigaa.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	@SuppressWarnings("unchecked") 
	public Collection<Integer> findDiscentesBolsasSolicitadasSipac() throws DAOException, LimiteResultadosException {
	    	int idTipoBolsaExtensao = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_EXTENSAO);
		String consulta = "select id_discente_projeto from projetos.sincronizacao_bolsa_sipac where id_tipo_bolsa = " + idTipoBolsaExtensao;
		return getJdbcTemplate().query(consulta, new RowMapper() {
			public Object mapRow(ResultSet rs, int pos) throws SQLException {
				return new Integer(rs.getInt(1));
			}
		});
	}

	/**
	 * Retorna todos os discentes de ativos em uma ação de extensão específica.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteExtensao> findByAtividade(Integer id) throws DAOException {
		try {
			String hql = "SELECT de from DiscenteExtensao de inner join de.atividade atv " +
				" WHERE de.ativo = trueValue() AND atv.id = :id ORDER BY de.discente.pessoa.nome";
			Query q = getSession().createQuery(hql);
			q.setInteger("id", id);
			return q.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Método que retorna a quantidade de bolsistas de uma atividade de extensão com seus respectivos vínculos.
	 * 
	 * @param idAtividade
	 * @param vinculos
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int countBolsistaByAtividadeOuVinculos(Integer idAtividade, Integer[] vinculos) throws HibernateException, DAOException{
		
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT COUNT(DISTINCT de.id) FROM DiscenteExtensao de ");
		hql.append(" WHERE de.ativo = trueValue() ");
		hql.append(" AND (de.situacaoDiscenteExtensao.id = :idSituacaoAtiva OR (de.dataInicio <= :hoje AND de.dataFim >= :hoje)) ");
		
		if(idAtividade != null){
			hql.append(" AND de.atividade.id = :idAtividade ");
		}
		
		if(vinculos != null){
			hql.append(" AND de.tipoVinculo.id IN (:idsVinculos) ");
		}
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idSituacaoAtiva", TipoSituacaoDiscenteExtensao.ATIVO);
		q.setDate("hoje", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		
		if(idAtividade != null){
			q.setInteger("idAtividade", idAtividade);
		}
		
		if(vinculos != null){
			q.setParameterList("idsVinculos", vinculos);
		}
		
		Long result = (Long) q.uniqueResult();
		return result.intValue();
	}
	
	/**
	 * Método que retorna a soma da quantidade de bolsas concedidas de cada ação de extensão do edital 
	 * 
	 * @param idEdital
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int countBolsasConcedidasByEditalExtensao(Integer idEdital) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT SUM(atv.bolsasConcedidas) FROM AtividadeExtensao atv ");
		hql.append(" WHERE atv.editalExtensao.id = :idEdital AND atv.ativo = trueValue() ");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idEdital", idEdital);
		Long result = (Long) q.uniqueResult();
		return result.intValue();
	}
	
	/**
	 * Busca os discentes de Extensão que fazem parte de uma plano de trabalho do docente informando. No período informando.
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteExtensao> findByDiscenteExtensaobyServidor(Servidor servidor, Integer anoInicio, Integer anoFim, 
			Integer periodoInicio, Integer periodoFim) throws DAOException {

		try {
			String hql = "select distinct pes.id_pessoa, pes.nome, p.titulo, de.data_inicio, de.data_fim " +
					" from extensao.discente_extensao de" +
					" join extensao.plano_trabalho_extensao pte using ( id_plano_trabalho_extensao )" +
					" join extensao.atividade at on (pte.id_atividade=at.id_atividade)" +
					" join projetos.projeto p on (p.id_projeto=at.id_projeto)" +
					" join projetos.membro_projeto mb on ( mb.id_projeto=p.id_projeto )" +
					" join discente d on ( de.id_discente  = d.id_discente ) " +
					" join comum.pessoa pes on ( d.id_pessoa = pes.id_pessoa)" +
			String.format(" where  (%1$s = %3$s or (%1$s > %3$s and %1$s <= %4$s) or (%1$s < %3$s and (%3$s <= %2$s or %2$s is null)))", 
					" de.data_inicio", "de.data_fim", ":dataInicio", ":dataFim") + 
					" and mb.id_funcao_membro = :funcaoCoordenador  " +
					" and mb.id_servidor = :idServidor" +
					" and pte.ativo = trueValue() " +
					" and de.ativo = trueValue()" +
					" and at.id_tipo_situacao_projeto in " + UFRNUtils.gerarStringIn(TipoSituacaoProjeto.EXTENSAO_GRUPO_VALIDO);
			
			Query query = getSession().createSQLQuery(hql);
			query.setInteger("idServidor", servidor.getId());
			query.setDate("dataInicio", 
					CalendarUtils.createDate(1, periodoInicio == 1 ? 0 : 5, anoInicio));
			query.setDate("dataFim", 
					CalendarUtils.createDate(periodoFim == 1 ? 30 : 31, periodoFim == 1 ? 5 : 11, anoFim));
			query.setInteger("funcaoCoordenador", FuncaoMembro.COORDENADOR);
			
			List<Object> lista = query.list();

			ArrayList<DiscenteExtensao> result = new ArrayList<DiscenteExtensao>();
			for (int a = 0; a < lista.size(); a++) {

			    int col = 0;
			    Object[] colunas = (Object[]) lista.get(a);
				DiscenteExtensao de = new DiscenteExtensao();
				de.getDiscente().getDiscente().getPessoa().setId((Integer) colunas[col++]);
				de.getDiscente().getDiscente().getPessoa().setNome((String) colunas[col++]);
				de.getPlanoTrabalhoExtensao().getAtividade().getProjeto().setTitulo((String) colunas[col++]);
			    de.setDataInicio((Date) colunas[col++]);
			    de.setDataFim((Date) colunas[col++]);
				
				result.add(de);
			}

			return result;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
}