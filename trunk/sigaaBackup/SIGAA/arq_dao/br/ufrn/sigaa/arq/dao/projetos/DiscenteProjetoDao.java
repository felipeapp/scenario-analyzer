/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '04/01/2011'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.PlanoTrabalhoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoDiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/***
 * Classe responsável pelo acesso aos dados dos discentes de projeto no banco de dados.
 *  
 * @author ilueny
 *
 */
public class DiscenteProjetoDao extends GenericSigaaDAO {

	/**
	 * Método responsável pela busca principal de discentes no projeto.
	 *  
	 * @param titulo
	 * @param ano
	 * @param dataInicioBolsa
	 * @param dataFimBolsa
	 * @param dataInicioFinalizacao
	 * @param dataFimFinalizacao
	 * @param idVinculo
	 * @param idEdital
	 * @param idDiscente
	 * @param idCurso
	 * @param idCoordenador
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<DiscenteProjeto> filter(String titulo, Integer ano, 
			Date dataInicioBolsa, Date dataFimBolsa, 
			Date dataInicioFinalizacao, Date dataFimFinalizacao, Integer idVinculo,
			Integer idEdital, Integer idDiscente, Integer idCurso, Integer idCoordenador) throws DAOException {
		
		String projecao = "dp.id, dp.ativo, dp.dataInicio, dp.dataFim, "
					+ " banco.codigo, banco.denominacao, dp.agencia, dp.conta, dp.operacao, "					
					+ " dp.tipoVinculo.id, dp.tipoVinculo.descricao, dp.situacaoDiscenteProjeto.id, dp.situacaoDiscenteProjeto.descricao, "
					
					+ " dp.discente.id, dp.discente.matricula, dp.discente.nivel, " 
					+ " dp.discente.pessoa.id, dp.discente.pessoa.nome, "						
					+ " enderecoContato.logradouro, enderecoContato.numero, enderecoContato.complemento, enderecoContato.bairro, "
					+ " enderecoContato.cep, municipio.nome, uf.sigla, "					
					+ " dp.discente.pessoa.telefone, dp.discente.pessoa.celular, dp.discente.pessoa.email, dp.discente.pessoa.cpf_cnpj, " 
					+ " dp.discente.pessoa.dataNascimento, "
					
					+ " dp.planoTrabalhoProjeto.id, dp.planoTrabalhoProjeto.dataInicio, dp.planoTrabalhoProjeto.dataFim, reg.id ,reg.data, "
					+ " dp.projeto.id, dp.projeto.ano, dp.projeto.unidade.id, dp.projeto.titulo, "
					+ " curso.id, curso.nome ";
		
		
		StringBuilder hql = new StringBuilder("SELECT ");
		hql.append(projecao);
		hql.append(" FROM DiscenteProjeto as dp " 
		+ " LEFT JOIN dp.banco as banco "
		+ " JOIN dp.projeto as projeto "		
		+ " JOIN dp.planoTrabalhoProjeto as plano "
		+ " JOIN plano.registroEntrada reg "
		+ " JOIN dp.discente as d "
		+ " LEFT JOIN d.curso as curso "
		+ " LEFT JOIN d.pessoa as p "
		+ " LEFT JOIN p.enderecoContato enderecoContato "
		+ " LEFT JOIN enderecoContato.municipio municipio "
		+ " LEFT JOIN municipio.unidadeFederativa uf "						
		+ " JOIN dp.projeto as projeto "
		+ " LEFT JOIN projeto.unidade as unidade ");
		hql.append(" WHERE dp.ativo = trueValue() ");
		
		if ((dataInicioBolsa != null) && (dataFimBolsa != null)) {
			hql.append( " AND cast(dp.dataInicio as date) >= :dataInicioBolsa AND cast(dp.dataInicio as date) <= :dataFimBolsa " );
		}
		
		if ((dataInicioFinalizacao != null) && (dataFimFinalizacao != null)) {
			hql.append( " AND cast(dp.dataFim as date) >= :dataInicioFinalizacao AND cast(dp.dataFim as date) <= :dataFimFinalizacao " );
		}		
		
		if (titulo != null) {
			hql.append(" AND "
					+ UFRNUtils.toAsciiUpperUTF8("projeto.titulo") + " like "
					+ UFRNUtils.toAsciiUTF8(":titulo"));
		}

		if (idVinculo != null) {
			hql.append(" AND dp.tipoVinculo.id = :idVinculo ");
		}

		if (ano != null) {
			hql.append(" AND (extract(year from projeto.dataInicio) <= :ano and extract(year from projeto.dataFim) >= :ano) ");
		}
		
		if (idEdital != null) {
			hql.append(" AND projeto.edital.id = :idEdital ");
		}

		if (idDiscente != null) {
			hql.append(" AND d.id = :idDiscente ");
		}

		if (idCoordenador != null) {
			hql.append(" AND projeto.coordenador.servidor.id = :idCoordenador ");
		}
		
		if (idCurso != null) {
			hql.append(" AND curso.id = :idCurso ");
		}
		
		hql.append(" ORDER BY reg.data, p.nome, dp.dataInicio, projeto.titulo, projeto.ano ");
		
		// Criando consulta
		Query query = getSession().createQuery(hql.toString());
		
		if ((dataInicioBolsa != null) && (dataFimBolsa != null)) {
			query.setDate("dataInicioBolsa", dataInicioBolsa);
			query.setDate("dataFimBolsa", dataFimBolsa);
		}
		
		if ((dataInicioFinalizacao != null) && (dataFimFinalizacao != null)) {
			query.setDate("dataInicioFinalizacao", dataInicioFinalizacao);
			query.setDate("dataFimFinalizacao", dataFimFinalizacao);
		}
		
		if (titulo != null) {
			query.setString("titulo", "%" + titulo.toUpperCase() + "%");
			query.setString("titulo", "%" + titulo.toUpperCase() + "%");
		}

		if (idDiscente != null) {
			query.setInteger("idDiscente", idDiscente);
		}

		if (idCoordenador != null) {
			query.setInteger("idCoordenador", idCoordenador);
		}
		
		if (idCurso != null) {
			query.setInteger("idCurso", idCurso);
		}

		if (idVinculo != null) {
			query.setInteger("idVinculo", idVinculo);
		}

		if (ano != null) {
			query.setInteger("ano", ano);
		}
		
		if (idEdital != null) {
			query.setInteger("idEdital", idEdital);
		}

		List<Object> lista = query.list();
				
		ArrayList<DiscenteProjeto> result = new ArrayList<DiscenteProjeto>();
		for (int a = 0; a < lista.size(); a++) {

			int col = 0;			
			Object[] colunas = (Object[]) lista.get(a);

				DiscenteProjeto dp = new DiscenteProjeto();
				dp.setId((Integer) colunas[col++]);
				dp.setAtivo((Boolean) colunas[col++]);
				dp.setDataInicio((Date) colunas[col++]);
				dp.setDataFim((Date) colunas[col++]);

				Banco banco = new Banco();
				Integer codigo = (Integer) colunas[col++];
				if (codigo != null) {
					banco.setCodigo(codigo);
				}
				banco.setDenominacao((String) colunas[col++]);

				dp.setBanco(banco);
				dp.setAgencia((String) colunas[col++]);
				dp.setConta((String) colunas[col++]);
				dp.setOperacao((String) colunas[col++]);

				TipoVinculoDiscente vinculo = new TipoVinculoDiscente();
				vinculo.setId((Integer) colunas[col++]);
				vinculo.setDescricao((String) colunas[col++]);
				dp.setTipoVinculo(vinculo);

				TipoSituacaoDiscenteProjeto situacao = new TipoSituacaoDiscenteProjeto();
				situacao.setId((Integer) colunas[col++]);
				situacao.setDescricao((String) colunas[col++]);
				dp.setSituacaoDiscenteProjeto(situacao);

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
				dp.setDiscente(discente);

				PlanoTrabalhoProjeto plano = new PlanoTrabalhoProjeto();
				plano.setId((Integer) colunas[col++]);
				plano.setDataInicio((Date) colunas[col++]);
				plano.setDataFim((Date) colunas[col++]);
				plano.setRegistroEntrada(new RegistroEntrada());
				plano.getRegistroEntrada().setId((Integer) colunas[col++]);
				plano.getRegistroEntrada().setData((Date) colunas[col++]);
				Projeto proj = new Projeto();
				proj.setId((Integer) colunas[col++]);				
				proj.setAno((Integer) colunas[col++]);
				proj.setUnidade(new Unidade((Integer) colunas[col++]));
				proj.setTitulo((String) colunas[col++]);
				
				//Se for aluno regular (aluno especial não tem curso)				
				if ((Integer) colunas[39] != null) {
        				Curso curso = new Curso();
        				curso.setId((Integer) colunas[col++]);
        				curso.setNome((String) colunas[col++]);
        				dp.getDiscente().setCurso(curso);
				}
				
				dp.setPlanoTrabalhoProjeto(plano);
				plano.setProjeto(proj);
				dp.setProjeto(proj);
				result.add(dp);
		}

		return result;
		
	}
	
	/**
	 * Retorna Discente de Ações Associadas com o status passado, caso nulo, desconsidera o
	 * status cadastrado em algum plano de trabalho.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */	
	@SuppressWarnings("unchecked")
	public Collection<DiscenteProjeto> findByDiscenteComPlanoTrabalho(int idDiscente, Integer idTipoSituacaoDiscente) throws DAOException {

		try {

			String projecao = "dp.id, dp.situacaoDiscenteProjeto.id, dp.situacaoDiscenteProjeto.descricao, dp.tipoVinculo.id, dp.tipoVinculo.descricao, " +
					"dp.planoTrabalhoProjeto.id, dp.planoTrabalhoProjeto.ativo, dp.projeto.id, dp.projeto.titulo, dp.projeto.ano, " +
					"dp.projeto.situacaoProjeto.id, dp.dataInicio, dp.dataFim, dp.ativo ";
			
			String hql ="SELECT " + projecao +
						" FROM DiscenteProjeto dp" +
						" JOIN dp.planoTrabalhoProjeto plano " +
						" JOIN plano.projeto pj " +
						" JOIN pj.situacaoProjeto sp " +						
						" WHERE (dp.discente.id = :idDiscente)" +
						" AND dp.ativo = trueValue() " +
						" AND pj.ativo = trueValue() " +
						" AND plano.ativo = trueValue() " +
						" AND sp.id NOT IN " + UFRNUtils.gerarStringIn(TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

			if (idTipoSituacaoDiscente != null) {
				hql += " AND (dp.situacaoDiscenteProjeto.id = :idTipoSituacaoDiscente)";
			}

			Query q = getSession().createQuery(hql);
			q.setInteger("idDiscente", idDiscente);

			if (idTipoSituacaoDiscente != null) {
				q.setInteger("idTipoSituacaoDiscente", idTipoSituacaoDiscente);
			}

			return HibernateUtils.parseTo(q.list(), projecao, DiscenteProjeto.class, "dp");

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna todas as participações do discente informado em projetos integrados. 
	 * 
	 * @param idDiscente
	 * @param idTipoSituacaoDiscente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteProjeto> findByDiscentesEmProjetos(int idDiscente) throws DAOException {	
		try {

			String projecao = "dp.id, dp.situacaoDiscenteProjeto.id, dp.situacaoDiscenteProjeto.descricao, " +
					"dp.tipoVinculo.id, dp.tipoVinculo.descricao, dp.tipoVinculo.remunerado, " +
					"dp.planoTrabalhoProjeto.id, dp.planoTrabalhoProjeto.ativo, " +
					"dp.projeto.id, dp.projeto.titulo, dp.projeto.ano, " +
					"dp.dataInicio, dp.dataFim, dp.ativo ";
			
			String hql ="SELECT " + projecao +
						" FROM DiscenteProjeto dp" +
						" JOIN dp.planoTrabalhoProjeto plano " +
						" JOIN plano.projeto pj " +
						" JOIN pj.situacaoProjeto sp " +						
						" WHERE (dp.discente.id = :idDiscente)" +
						" AND dp.ativo = trueValue() " +
						" AND pj.ativo = trueValue() " +
						" AND plano.ativo = trueValue() " +
						" AND sp.id IN (:PROJ_BASE_EM_EXECUCAO) " +
						" AND (dp.situacaoDiscenteProjeto.id IN (:idTipoSituacaoDiscente))";

			Query q = getSession().createQuery(hql);
			q.setInteger("idDiscente", idDiscente);

			q.setParameterList("idTipoSituacaoDiscente", new Integer[]{TipoSituacaoDiscenteProjeto.ASSUMIU, TipoSituacaoDiscenteProjeto.SELECIONADO});
			q.setInteger("PROJ_BASE_EM_EXECUCAO", TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO);
			return HibernateUtils.parseTo(q.list(), projecao, DiscenteProjeto.class, "dp");

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
	}
	
	/**
	 * Retorna todas as participações do discente informado em projetos integrados, conforme as situações informadas. 
	 * 
	 * @param idDiscente
	 * @param idTipoSituacaoDiscente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteProjeto> findByDiscentesEmProjetosBySituacao(int idDiscente, Integer... tiposSituacaoDiscente ) throws DAOException {	
		try {

			String hql ="SELECT dp " + 
						" FROM DiscenteProjeto dp" +
						" JOIN dp.planoTrabalhoProjeto plano " +
						" JOIN plano.projeto pj " +
						" JOIN pj.situacaoProjeto sp " +						
						" WHERE (dp.discente.id = :idDiscente)" +
						" AND dp.ativo = trueValue() " +
						" AND pj.ativo = trueValue() " +
						" AND plano.ativo = trueValue() " +
						" AND sp.id IN (:PROJ_BASE_EM_EXECUCAO) " +
						" AND (dp.situacaoDiscenteProjeto.id IN (:idTipoSituacaoDiscente))";

			Query q = getSession().createQuery(hql);
			q.setInteger("idDiscente", idDiscente);

			q.setParameterList("idTipoSituacaoDiscente", tiposSituacaoDiscente);
			q.setInteger("PROJ_BASE_EM_EXECUCAO", TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO);
			return q.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
	}
	
}
