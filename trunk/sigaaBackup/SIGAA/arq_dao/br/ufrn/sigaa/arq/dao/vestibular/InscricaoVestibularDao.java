/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '20/04/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.vestibular.dominio.EstatisticaInscritosPorDia;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/** Classe responsável por consultas específicas às Inscrições do Vestibular.
 * @author Édipo Elder F. Melo
 *
 */
public class InscricaoVestibularDao extends GenericSigaaDAO {

	/** Serve para informar qual o formato CSV */
	public static final int FORMATO_CSV = 1;
	/** Serve para informar qual o formato SQL */
	public static final int FORMATO_SQL = 2;
	/** tamanho máximo da lista em consultas do tipo "where item in (1,2,3, ..., n)" */
	private final static int TAMANHO_MAXIMO_CONSULTA_IN = 512;
	/** tamanho máximo da lista em consultas do tipo "where item in (1,2,3, ..., n)" */
	private final static int LIMITE_MAXIMO_CONSULTA = 500;

	/** Retorna uma coleção de inscrições que possuam o CPF indicado.
	 * @param cpf_cnpj
	 * @return
	 * @throws DAOException
	 */
	public InscricaoVestibular findValidadaByCpf(Long cpf_cnpj, int idProcessoSeletivo) throws DAOException {
		Criteria c = getSession()
				.createCriteria(InscricaoVestibular.class)
				.add(Restrictions.eq("validada", true));
		c.createCriteria("processoSeletivo")
			.add(Restrictions.eq("id", idProcessoSeletivo));
		c.createCriteria("pessoa").add(Restrictions.eq("cpf_cnpj", cpf_cnpj));
		c.setMaxResults(1);		
		return (InscricaoVestibular) c.uniqueResult();
	}
	
	/** Retorna uma coleção de inscrições que possuam o CPF indicado.
	 * @param cpf_cnpj
	 * @return
	 * @throws DAOException
	 */
	public Collection<InscricaoVestibular> findByCpf(Long cpf_cnpj, Integer idProcessoSeletivo) throws DAOException {
		Criteria c = getSession()
				.createCriteria(InscricaoVestibular.class);
		if (idProcessoSeletivo != null && idProcessoSeletivo > 0)
				c.createCriteria("processoSeletivo")
				.add(Restrictions.eq("id", idProcessoSeletivo));
		c.createCriteria("pessoa").add(Restrictions.eq("cpf_cnpj", cpf_cnpj));
		@SuppressWarnings("unchecked")
		Collection<InscricaoVestibular> lista = c.list();
		return lista;
	}
	
	/** Retorna uma coleção de inscrições, buscando por nome, cpf ou inscrição do candidato.
	 * @param idProcessoSeletivo
	 * @param nome
	 * @param cpf_cnpj
	 * @param inscricao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<InscricaoVestibular> findByNomeCpfInscricao(int idProcessoSeletivo, String nome, 
			Long cpf_cnpj, int inscricao, boolean limitar) throws DAOException {
		String projecao = "inscricao.id," +
			" inscricao.numeroInscricao," +
			" inscricao.pessoa.nome," +
			" inscricao.processoSeletivo.nome," +
			" inscricao.pessoa.cpf_cnpj," +
			" inscricao.gruQuitada," +
			" inscricao.validada," +
			" inscricao.idGRU";

		StringBuilder hql = new StringBuilder(" from InscricaoVestibular inscricao" +
				" where 1 = 1 ");
		if (idProcessoSeletivo > 0)
			hql.append(" and inscricao.processoSeletivo.id = " + idProcessoSeletivo );
		if (!ValidatorUtil.isEmpty(nome))
			hql.append(" and inscricao.pessoa.nomeAscii like '%"+ StringUtils.toAscii(nome).toUpperCase() + "%'");
		if (cpf_cnpj != null && cpf_cnpj > 0)
			hql.append(" and inscricao.pessoa.cpf_cnpj = "+ cpf_cnpj);
		if (inscricao > 0)
			hql.append(" and inscricao.numeroInscricao = "+ inscricao);
		
		Long total = countTotal(hql);
		hql.insert(0, projecao);
		hql.insert(0, "select ");
		hql.append(" order by inscricao.pessoa.nome, inscricao.numeroInscricao");
		
		if (total > LIMITE_MAXIMO_CONSULTA)
			throw new LimiteResultadosException("A consulta retornou "
					+ total	+ "	resultados. Por favor, restrinja mais a busca.");
		
		Query q = getSession().createQuery(hql.toString());
		return HibernateUtils.parseTo(q.list(), projecao, InscricaoVestibular.class, "inscricao");
	}
	
	/**
	 * Realiza um count para ver se entra entro o limite máximo permitido, para exibição.
	 * 
	 * @param c
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Long countTotal(StringBuilder sb) throws HibernateException, DAOException{
		String consulta = "select count (*)" + sb.toString();
		return (Long) getSession().createQuery(consulta).uniqueResult(); 
	}
	
	/** Retorna a estatísica de inscrições de candidatos por dia de inscrição.
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<EstatisticaInscritosPorDia> estatiscaInscritosPorDia(int idProcessoSeletivo) throws DAOException {
		String sql = "select cast(data_inscricao as date)," +
				" sum(case when cast(data_inscricao as time) <  cast('06:00:00' as time) then 1 else 0 end) as primeiroQuarto," +
				" sum(case when cast(data_inscricao as time) >= cast('06:00:00' as time) and cast(data_inscricao as time) < cast('12:00:00' as time) then 1 else 0 end) as segundoQuarto," +
				" sum(case when cast(data_inscricao as time) >= cast('12:00:00' as time) and cast(data_inscricao as time) < cast('18:00:00' as time) then 1 else 0 end) as terceiroQuarto," +
				" sum(case when cast(data_inscricao as time) >= cast('18:00:00' as time) then 1 else 0 end) as quartoQuarto," +
				" sum(case when cast(data_inscricao as time) <  cast('18:00:00' as time) then 1 else 0 end) as ate18Horas," +
				" count(id_inscricao_vestibular) as totalInscricoes," +
				" count(distinct iv.id_pessoa) as totalCandidatos," +
				" sum(case when valor_inscricao = 0 then 1 else 0 end) as totalIsentos" +
				" from vestibular.inscricao_vestibular iv" +
				" inner join vestibular.pessoa_vestibular pv on (iv.id_pessoa = pv.id_pessoa )"+
				" left join vestibular.isento_taxa_vestibular itv " +
				" on (iv.id_processo_seletivo = itv.id_processo_seletivo and itv.cpf = pv.cpf_cnpj)"+
				" where iv.id_processo_seletivo = :idProcessoSeletivo" +
				" group by 1" +
				" order by 1 desc";
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		@SuppressWarnings("unchecked")
		Collection<Object[]> bulk = q.list();
		Collection<EstatisticaInscritosPorDia> lista = new ArrayList<EstatisticaInscritosPorDia>();
		if (bulk != null) {
			for (Object[] obj : bulk){
				int k = 0;
				EstatisticaInscritosPorDia estatistica = new EstatisticaInscritosPorDia();
				estatistica.setData((Date) obj[k++]);
				estatistica.setPrimeiroQuartoDia(((BigInteger) obj[k++]).intValue());
				estatistica.setSegundoQuartoDia(((BigInteger) obj[k++]).intValue());
				estatistica.setTerceiroQuartoDia(((BigInteger) obj[k++]).intValue());
				estatistica.setQuartoQuartoDia(((BigInteger) obj[k++]).intValue());
				estatistica.setTotalAte18Horas(((BigInteger) obj[k++]).intValue());
				estatistica.setTotalInscricoes(((BigInteger) obj[k++]).intValue());
				estatistica.setTotalCandidatos(((BigInteger) obj[k++]).intValue());
				estatistica.setTotalCandidatosIsentos(((BigInteger) obj[k++]).intValue());
				lista.add(estatistica);
			}
		}
		return lista;
	}
	
	/** Retorna uma coleção de inscrições associadas à uma {@link PessoaVestibular Pessoa}.
	 * @param idPessoaVestibular
	 * @return
	 * @throws DAOException
	 */
	public Collection<InscricaoVestibular> findByPessoaVestibular(int idPessoaVestibular, int idProcessoSeletivo) throws DAOException {
		Criteria c = getSession().createCriteria(InscricaoVestibular.class);
		c.createCriteria("pessoa").add(Restrictions.eq("id", idPessoaVestibular));
		if (idProcessoSeletivo > 0)
			c.createCriteria("processoSeletivo").add(Restrictions.eq("id", idProcessoSeletivo));
		c.addOrder(Order.desc("numeroInscricao"));
		@SuppressWarnings("unchecked")
		Collection<InscricaoVestibular> lista = c.list();
		return lista;
	}
	
	/** Retorna uma coleção de objetos de inscrições a partir de uma lista de números de inscrições. 
	 * @param listaNumInscricao
	 * @return
	 * @throws DAOException
	 */
	public Collection<InscricaoVestibular> findByListaInscricao(Collection<Integer> listaNumInscricao, ProcessoSeletivoVestibular processoSeletivo) throws DAOException{
		if (ValidatorUtil.isEmpty(listaNumInscricao)) return null;
		String projecao = "inscricao.id, inscricao.numeroInscricao, inscricao.pessoa.id, inscricao.pessoa.nome, inscricao.pessoa.cpf_cnpj, inscricao.pessoa.idFoto," +
				"inscricao.processoSeletivo.id, inscricao.processoSeletivo.nome, inscricao.processoSeletivo.sigla, inscricao.validada";
		StringBuilder hql = new StringBuilder("select " + projecao +
				" from InscricaoVestibular inscricao" +
				" where numeroInscricao in (:listaNumInscricao)");
		if (processoSeletivo != null)
				hql.append(" and inscricao.processoSeletivo.id = :idProcessoSeletivo");
		hql.append(" order by inscricao.validada");
		Query q = getSession().createQuery(hql.toString());
		// para evita comando SQL muito longo, quebra-se a lista de número de inscrições em sublistas menores
		Collection<InscricaoVestibular> resultado = new ArrayList<InscricaoVestibular>();
		while (!listaNumInscricao.isEmpty()) {
			int k = 0;
			Collection<Integer> subLista = new ArrayList<Integer>();
			for (java.util.Iterator<Integer> iterator = listaNumInscricao.iterator(); iterator.hasNext() && k < TAMANHO_MAXIMO_CONSULTA_IN; k++ ) {
				subLista.add(iterator.next());
				iterator.remove();
			}
			if (subLista.size() > 0) {
				q.setParameterList("listaNumInscricao", subLista);
				if (processoSeletivo != null)
					q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
				@SuppressWarnings("unchecked")
				Collection<InscricaoVestibular> lista = HibernateUtils.parseTo(q.list(), projecao, InscricaoVestibular.class, "inscricao");
				if (lista != null)
					resultado.addAll(lista);
			}
		}
		return resultado;
	}
	
	/** Retorna uma coleção de inscrições validadas de um processo seletivo vestibular.
	 * @param idVestibular
	 * @return
	 * @throws DAOException
	 */
	public Collection<InscricaoVestibular> findValidadasByVestibular(int idVestibular) throws DAOException{
		return findByVestibularFaixaInscricao(idVestibular, 0, 0, true);
	}
	
	/** Retorna uma coleção de inscrições validadas de um processo seletivo vestibular dentro de uma faixa de inscrições. 
	 * @param idVestibular
	 * @return
	 * @throws DAOException
	 */
	public Collection<InscricaoVestibular> findByVestibularFaixaInscricao(int idVestibular, int inscricaoMinima, int inscricaoMaxima, Boolean validada) throws DAOException{
		String projecao = "inscricao.id, inscricao.numeroInscricao, inscricao.pessoa.id, inscricao.pessoa.nome, inscricao.pessoa.cpf_cnpj, inscricao.pessoa.idFoto";
		StringBuilder hql = new StringBuilder("select " + projecao +
				" from InscricaoVestibular inscricao" +
				" where inscricao.processoSeletivo.id = :idProcessoSeletivo");
		if (validada != null)
			hql.append(" and inscricao.validada = :validada");
		if (inscricaoMinima > 0)
			hql.append(" and numeroInscricao >= :inscricaoMinima");
		if (inscricaoMaxima > 0)
			hql.append(" and numeroInscricao <= :inscricaoMaxima");
		Query q = getSession().createQuery(hql.toString());
		if (validada != null)
			q.setBoolean("validada", validada.booleanValue());
		q.setInteger("idProcessoSeletivo", idVestibular);
		if (inscricaoMinima > 0)
			q.setInteger("inscricaoMinima", inscricaoMinima);
		if (inscricaoMaxima > 0)
			q.setInteger("inscricaoMaxima",inscricaoMaxima);
		@SuppressWarnings("unchecked")
		Collection<InscricaoVestibular> lista = HibernateUtils.parseTo(q.list(), projecao, InscricaoVestibular.class, "inscricao");
		return lista;
	}

	/** Exporta os dados do candidato para um formato especificado.
	 * @param id
	 * @param formatoCsv Caso 1, exporta os dados no formato CSV. Caso 2, exporta os dados no formato SQL.
	 * @return
	 * @throws ArqException 
	 */
	public String exportaValidadas(int id, int formato) throws ArqException {
		Connection con = null;
		StringBuilder dados = new StringBuilder();
		try {
			con = Database.getInstance().getSigaaConnection();		
		
			// dados do candidato
			String sqlCandidato = "select id_processo_seletivo as idconcurso," +
				" id_inscricao_vestibular as idcandidato," +
				" numero_inscricao as inscricao," +
				" optou_beneficio_inclusao," +
				" sexo," +
				" numero_identidade as rg," +
				" orgao_expedicao_identidade as orgaorg," +
				" identidade_uf.sigla as estadorg," +
				" data_nascimento as datanascimento," +
				" endereco.logradouro as endereco," +
				" endereco.numero," +
				" endereco.complemento as complemento," +
				" endereco_uf.sigla as estado," +
				" endereco.cep," +
				" codigo_area_nacional_telefone_celular as ddd1," +
				" telefone_fixo as telefone," +
				" case when id_tipo_necessidade_especial = 2 then 'S' else null end as deficienciavisual," +
				" case when id_tipo_necessidade_especial = 1 then 'S' else null end as deficienciaauditiva," +
				" case when id_tipo_necessidade_especial = 3 then 'S' else null end as deficienciafisica," +
				" substr(lingua_estrangeira.denominacao, 1, 1) as lingua," +
				" case when valor_inscricao > 0 then 'S' else 'N' end as pagante," +
				" id_regiao_preferencial_prova as idlocalprova," +
				" cpf_cnpj as cpf," +
				" telefone_celular as celular," +
				" data_inscricao::date as datainscricao," +
				" data_inscricao::time as horainscricao," +
				" pessoa_vestibular.email," +
				" endereco_municipio.nome as cidade," +
				" naturalidade_municipio.nome as cidadenascimento," +
				" naturalidade_uf.sigla as estadonascimento," +
				" pais_nascimento.nome as paisnascimento," +
				" nome_mae as nomemae," +
				" nome_pai as nomepai," +
				" titulo_eleitor.numero as tituloeleitornumero," +
				" titulo_eleitor.zona as tituloeleitorzona," +
				" titulo_eleitor.secao as tituloeleitorsecao," +
				" titulo_eleitor_uf.sigla as tituloeleitorestado," +
				" case when id_tipo_necessidade_especial = 4 then 'S' else null end as deficienciamultipla," +
				" case when id_tipo_necessidade_especial = 5 then 'S' else null end as deficienciamental," +
				" COALESCE( escola_inep.nome, segundograuescola ) as escolanome," +
				" segundograuanoconclusao as escolaano," +
				" escola_endereco_municipio.nome as escolacidade," +
				" COALESCE( escola_endereco_uf.sigla, segundograuuf, escola_digitada.sigla ) as escolaestado," +
				" endereco.bairro," +
				" pessoa_vestibular.nome" +
				" from vestibular.inscricao_vestibular" +
				" inner join vestibular.pessoa_vestibular using (id_pessoa)" +
				" left join comum.unidade_federativa identidade_uf on (id_uf_identidade = identidade_uf.id_unidade_federativa)" +
				" left join comum.endereco endereco on (id_endereco = id_endereco_contato)" +
				" left join comum.unidade_federativa endereco_uf on (endereco.id_unidade_federativa = endereco_uf.id_unidade_federativa)" +
				" inner join vestibular.lingua_estrangeira using (id_lingua_estrangeira)" +
				" left join comum.municipio endereco_municipio on (endereco.id_municipio = endereco_municipio.id_municipio)" +
				" left join comum.municipio naturalidade_municipio on (id_municipio_naturalidade = naturalidade_municipio.id_municipio)" +
				" left join comum.unidade_federativa naturalidade_uf on (id_uf_naturalidade = naturalidade_uf.id_unidade_federativa)" +
				" left join comum.pais pais_nascimento on (id_pais_nacionalidade = pais_nascimento.id_pais)" +
				" left join comum.titulo_eleitor using (id_titulo_eleitor)" +
				" left join comum.unidade_federativa titulo_eleitor_uf on (id_uf_titulo_eleitor = titulo_eleitor_uf.id_unidade_federativa)" +
				" left join vestibular.escola_inep on (escola_inep.id_escola = pessoa_vestibular.id_escola)" +
				" left join comum.endereco endereco_escola on (escola_inep.id_endereco = endereco_escola.id_endereco)" +
				" left join comum.municipio escola_endereco_municipio on (endereco_escola.id_municipio = escola_endereco_municipio.id_municipio)" +
				" left join comum.unidade_federativa escola_endereco_uf on (endereco_escola.id_unidade_federativa = escola_endereco_uf.id_unidade_federativa)" +
				" left join comum.unidade_federativa escola_digitada on ( pessoa_vestibular.id_uf_conclusao_em = escola_digitada.id_unidade_federativa )" +
				" where id_processo_seletivo = " + id;
			
			PreparedStatement st = con.prepareStatement(sqlCandidato);
			ResultSet rs = st.executeQuery();
			if (formato == FORMATO_CSV) {
				dados.append("\nDADOS DOS CANDIDATOS\n");
				dados.append(UFRNUtils.resultSetToCSV(rs));
			} else {
				dados.append("\n-- DADOS DOS CANDIDATOS\n");
				dados.append(UFRNUtils.resultSetToSQLInserts("sigaa_candidato", rs, true));
			}

			// opções de curso
			String sqlOpcoes = "select" +
			" id_processo_seletivo as idconcurso," +
			" id_inscricao_vestibular as idcandidato," +
			" ordem + 1 as idopcao," +
			" id_matriz_curricular as idoferta" +
			" from vestibular.inscricao_vestibular" +
			" inner join vestibular.opcao_candidato using (id_inscricao_vestibular)" +
			" where id_processo_seletivo = " + id;
			
			st = con.prepareStatement(sqlOpcoes);
			rs = st.executeQuery();
			if (formato == FORMATO_CSV) {
				dados.append("\nOPCOES DOS CANDIDATOS\n");
				dados.append(UFRNUtils.resultSetToCSV(rs));
			} else {
				dados.append("\n-- OPCOES DOS CANDIDATOS\n");
				dados.append(UFRNUtils.resultSetToSQLInserts("sigaa_opcoescandidato", rs, true));
			}
 
			// informações auxiliares
			// oferta de curso
			String sqlOfertaCurso = "select processo_seletivo.id_processo_seletivo as idconcurso," +
				"  id_matriz_curricular as idoferta, " +
				"  curso.nome||" +
				"  coalesce(' - '||habilitacao.nome,'')||" +
				"  coalesce(' - '||enfase.nome,'')||" +
				"  coalesce(' - '||turno.sigla)||" +
				"  coalesce(' - '||grau_academico.descricao,'') as nome," +
				"  curso.id_area_conhecimento_vestibular as idarea," +
				"  id_municipio as idcidade," +
				"  vagas_periodo_1 as vagassemestre1," +
				"  vagas_periodo_2 as vagassemestre2" +
				" from ensino.oferta_vagas_curso" +
				" left join graduacao.matriz_curricular using (id_matriz_curricular)" +
				" left join ensino.turno on (matriz_curricular.id_turno = turno.id_turno)" +
				" left join ensino.grau_academico on (matriz_curricular.id_grau_academico = grau_academico.id_grau_academico)" +
				" left join curso on (matriz_curricular.id_curso = curso.id_curso)" +
				" left join graduacao.habilitacao on (matriz_curricular.id_habilitacao = habilitacao.id_habilitacao)" +
				" left join graduacao.enfase on (matriz_curricular.id_enfase = enfase.id_enfase)" +
				" left join vestibular.processo_seletivo on (oferta_vagas_curso.ano = ano_entrada and oferta_vagas_curso.id_forma_ingresso = processo_seletivo.id_forma_ingresso)" +
				" where processo_seletivo.id_processo_seletivo = " + id;
			
			st = con.prepareStatement(sqlOfertaCurso);
			rs = st.executeQuery();
			if (formato == FORMATO_CSV) {
				dados.append("\nOFERTA DE CURSO\n");
				dados.append(UFRNUtils.resultSetToCSV(rs));
			} else {
				dados.append("\n-- OFERTA DE CURSO\n");
				dados.append(UFRNUtils.resultSetToSQLInserts("sigaa_ofertacurso", rs, true));
			}
			
			// região preferencial de prova
			String sqlRegiaoPreferencial = "select id_regiao_preferencial_prova, denominacao from vestibular.regiao_preferencial_prova";
			
			st = con.prepareStatement(sqlRegiaoPreferencial);
			rs = st.executeQuery();
			if (formato == FORMATO_CSV) {
				dados.append("\nREGIAO PREFERENCIAL DE PROVA\n");
				dados.append(UFRNUtils.resultSetToCSV(rs));
			} else {
				dados.append("\n-- REGIAO PREFERENCIAL DE PROVA\n");
				dados.append(UFRNUtils.resultSetToSQLInserts("sigaa_regiaopreferencialprova", rs, true));
			}
			
			// área
			String sqlArea = "select id_area_conhecimento_vestibular as idarea, descricao as nome from vestibular.area_conhecimento_vestibular";
			
			st = con.prepareStatement(sqlArea);
			rs = st.executeQuery();
			if (formato == FORMATO_CSV) {
				dados.append("\nAREA\n");
				dados.append(UFRNUtils.resultSetToCSV(rs));
			} else {
				dados.append("\n-- AREA\n");
				dados.append(UFRNUtils.resultSetToSQLInserts("sigaa_area", rs, true));
			}
			
			// município
			String sqlMunicipio = "select distinct municipio.id_municipio as idcidade," +
					" municipio.nome as nome" +
					" from ensino.oferta_vagas_curso" +
					" inner join graduacao.matriz_curricular using (id_matriz_curricular)" +
					" left join curso on (matriz_curricular.id_curso = curso.id_curso)" +
					" left join comum.municipio using (id_municipio)" +
					" left join vestibular.processo_seletivo on (oferta_vagas_curso.ano = ano_entrada and oferta_vagas_curso.id_forma_ingresso = processo_seletivo.id_forma_ingresso)" +
					" where processo_seletivo.id_processo_seletivo = " + id;
			
			st = con.prepareStatement(sqlMunicipio);
			rs = st.executeQuery();
			if (formato == FORMATO_CSV) {
				dados.append("\nCIDADE\n");
				dados.append(UFRNUtils.resultSetToCSV(rs));
			} else {
				dados.append("\n-- CIDADE\n");
				dados.append(UFRNUtils.resultSetToSQLInserts("sigaa_cidade", rs, true));
			}
			
			return dados.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}

	/**
	 * Gerar um relatório com o total de incritos, estudante e Funcionário.
	 * 
	 * @param vestibular
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 * @throws SQLException
	 */
    @SuppressWarnings("unchecked")
	public Map<String,Object> findTotalInscritos(Integer idProcessoSeletivo) throws DAOException, HibernateException, SQLException {
    	Map<String, Object> mapa = new TreeMap<String, Object>();

        String sql = " select CAST(sum(case when pv.funcionario  = 'true' then 1 else 0 end) AS double precision) as totalIsentosFunc," +
        		" CAST(count(id_isento_taxa_vestibular) AS double precision) as totalIsencao from vestibular.isento_taxa_vestibular itv" +
        		" left join comum.pessoa pv on (itv.cpf = pv.cpf_cnpj) left join rh.servidor serv on " +
        		" (pv.id_pessoa = serv.id_pessoa and serv.id_ativo = " +Ativo.SERVIDOR_ATIVO + ") where id_processo_seletivo = " + idProcessoSeletivo;

		SQLQuery q1 = getSession().createSQLQuery(sql);
		List <Object []> rs1 = q1.list();
		
		for (Object linha : rs1) {
			 Object[] values = (Object[])linha;
			 mapa.put("0", (values[1] != null ? ((Double)values[1]).intValue() : 0) - (values[0] != null ? ((Double)values[0]).intValue() : 0));
			 mapa.put("1", values[0] != null ? ((Double)values[0]).intValue() : 0);
			 mapa.put("2", values[0] != null ? ((Double)values[1]).intValue() : 0);
		}

		String sqlconsulta = " select CAST(sum(case when funcionario  = 'false' then 1 else 0 end) AS double precision) " +
        		" as totalIsentosEst, CAST(sum(case when funcionario  = 'true' then 1 else 0 end) AS double precision) " +
        		" as totalIsentosFunc, CAST(count(id_isento_taxa_vestibular) AS double precision) as totalIsencao" +
        		" from (select distinct pv.funcionario, id_isento_taxa_vestibular" +
        		" from vestibular.isento_taxa_vestibular itv inner join vestibular.pessoa_vestibular pv on (itv.cpf = pv.cpf_cnpj)" +
        		" inner join vestibular.inscricao_vestibular iv on " +
        		" (itv.id_processo_seletivo = iv.id_processo_seletivo and iv.id_pessoa = pv.id_pessoa ) " +
        		" where itv.id_processo_seletivo = " + idProcessoSeletivo +") inscricoes_unicas";
        
        
        SQLQuery q = getSession().createSQLQuery(sqlconsulta);
		List <Object []> rs = q.list();
		
		for (Object linha : rs) {
			 Object[] values = (Object[])linha;
			 mapa.put("3", values[0] != null ? ((Double)values[0]).intValue() : 0);  
			 mapa.put("4", values[1] != null ? ((Double)values[1]).intValue() : 0);
			 mapa.put("5", values[2] != null ? ((Double)values[2]).intValue() : 0);
		}
		
		return mapa;
    }

	/**
	 * Gera um relatório da demanda parcial de candidatos por curso.
	 * 
	 * @param vestibular
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 * @throws SQLException
	 */
    @SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> demandaParcialCandidatoCurso(Integer idProcessoSeletivo, boolean demandaFinal) throws DAOException, HibernateException, SQLException {
           StringBuilder sqlconsulta = new StringBuilder("select m.nome as centro, acv.descricao as area," +
           						" c.nome, substring(ga.descricao from 1 for 1) as grau, t.sigla," +
           						" habilitacao.nome as habilitacao, enfase.nome as enfase,"+   
           						" CAST(count(distinct iv.id_inscricao_vestibular) AS double precision) as totalCandidatosDistintos,"+
           						" CAST(ovc.vagas_periodo_1 AS double precision)as vagas_periodo_1,  "+
           						" CAST(ovc.vagas_periodo_2 AS double precision)as vagas_periodo_2"+
           						" from vestibular.inscricao_vestibular iv"+
           						" inner join vestibular.processo_seletivo ps on (ps.id_processo_seletivo = iv.id_processo_seletivo)"+  
           						" inner join vestibular.opcao_candidato oc on (oc.id_inscricao_vestibular = iv.id_inscricao_vestibular and oc.ordem = 0)"+  
           						" inner join graduacao.matriz_curricular mc on (oc.id_matriz_curricular = mc.id_matriz_curricular)  "+
           						" inner join ensino.oferta_vagas_curso ovc on (" +
           						"            ovc.id_matriz_curricular = oc.id_matriz_curricular " +
           						"            and ovc.id_forma_ingresso = ps.id_forma_ingresso" +
           						"            and ovc.ano = ps.ano_entrada)  "+
           						" inner join curso c on (mc.id_curso = c.id_curso) " +
           						" left join graduacao.habilitacao using (id_habilitacao)" +
           						" left join graduacao.enfase using (id_enfase)"+
           						" inner join comum.municipio m on (c.id_municipio = m.id_municipio)"+
           						" inner join ensino.turno t on (mc.id_turno=t.id_turno)"+  
           						" inner join ensino.grau_academico ga on (mc.id_grau_academico=ga.id_grau_academico)"+  
           						" inner join vestibular.area_conhecimento_vestibular acv on (c.id_area_conhecimento_vestibular = acv.id_area_conhecimento_vestibular)"+
           						" where iv.id_processo_seletivo = "+idProcessoSeletivo);
           				if (demandaFinal) 
           						sqlconsulta.append("and iv.validada = trueValue()");						

           				sqlconsulta.append(" group by mc.id_matriz_curricular, c.nome, ga.descricao,  "+
           						" t.sigla, habilitacao.nome, enfase.nome," +
           						" ovc.vagas_periodo_1, ovc.vagas_periodo_2, m.nome, "+
           						" acv.id_area_conhecimento_vestibular, acv.descricao, c.id_area_conhecimento_vestibular"+
           						" order by m.nome, c.id_area_conhecimento_vestibular, c.nome, habilitacao.nome, enfase.nome, t.sigla");
            	
		SQLQuery q = getSession().createSQLQuery(sqlconsulta.toString());
		q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
		return q.list();
    }

	/**
     * Quantitativo os candidatos pagantes. 
     * 
     * @param idProcessoSeletivo
     * @return
     * @throws DAOException
     */
	public Integer quantCandidatosPagantes(int idProcessoSeletivo) throws DAOException {
		Query q = getSession().createSQLQuery("select count(*) from vestibular.inscricao_vestibular" +
				" inner join vestibular.pessoa_vestibular pv using(id_pessoa) where id_processo_seletivo = " + idProcessoSeletivo +
				" and cpf_cnpj not in (select cpf from vestibular.isento_taxa_vestibular " +
				" where id_processo_seletivo = " + idProcessoSeletivo+")");
		return Integer.valueOf( String.valueOf( q.uniqueResult() ));
	}

    /**
     * Quantitativo os candidatos pagantes, com o cpf distinto. 
     * 
     * @param idProcessoSeletivo
     * @return
     * @throws DAOException
     */
	public Integer quantCandidatosPagantesDistintos(int idProcessoSeletivo) throws DAOException {
		Query q = getSession().createSQLQuery("select count(distinct(pv.cpf_cnpj)) from vestibular.inscricao_vestibular" +
				" inner join vestibular.pessoa_vestibular pv using(id_pessoa) where id_processo_seletivo = " + idProcessoSeletivo +
				" and cpf_cnpj not in (select cpf from vestibular.isento_taxa_vestibular " +
				" where id_processo_seletivo = " + idProcessoSeletivo+")");
		return Integer.valueOf( String.valueOf( q.uniqueResult() ));
	}

	/**
	 * Quantitativo do total de Inscrições em um determinado processo Seletivo.
	 * 
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Integer quantTotalInscricoes(int idProcessoSeletivo, boolean demandaFinal) throws DAOException {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(id_pessoa) as totalInscricoes from vestibular.inscricao_vestibular " +
			"where id_processo_seletivo = " + idProcessoSeletivo);
			
			if (demandaFinal) 
				sql.append(" and validada is true");
		
		Query q = getSession().createSQLQuery(sql.toString());
		return Integer.valueOf( String.valueOf( q.uniqueResult() ));
	}

	/**
	 * Quantitativo do total de Inscrições de candidatos distintos.
	 * 
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Integer quantTotalInscricoesDistintas(int idProcessoSeletivo, boolean demandaFinal) throws DAOException {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(distinct (id_pessoa)) as totalInscricoesDistintos from vestibular.inscricao_vestibular " +
				" where id_processo_seletivo = " + idProcessoSeletivo);
		
		if (demandaFinal) 
			sql.append(" and validada is true");

		Query q = getSession().createSQLQuery(sql.toString());
		return Integer.valueOf( String.valueOf( q.uniqueResult() ));
	}

	/** Retorna um mapa de pares de inscrição de um processo seletivo.
	 * @param quantidadePorFaixa
	 * @param idProcessoSeletivo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<Integer, Integer> findFaixasInscricao(int quantidadePorFaixa, int idProcessoSeletivo) throws HibernateException, DAOException {
		String hql = "select min(numeroInscricao), max(numeroInscricao)" +
				" from InscricaoVestibular" +
				" where processoSeletivo.id = :idProcessoSeletivo";
		Query q = getSession().createQuery(hql);
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		Object[] minMax = (Object[]) q.uniqueResult();
		Map<Integer, Integer> mapa = new TreeMap<Integer, Integer>();
		if (minMax != null && minMax[0] != null) {
			// "puxa para baixo", para um valor terminado em "00". Exemplos: 100, 200, 1500
			int casas = String.valueOf(quantidadePorFaixa).length();
			int min = casas > 2 ? (Integer) minMax[0] / 100 * 100 + 1: 1;
			int max = (Integer) minMax[1];
			while (min <= max) {
				mapa.put(min, min + quantidadePorFaixa - 1);
				min += quantidadePorFaixa;
			}
		}
		return mapa;
	}
	
	/**
	 * Retorna a InscricaoVestibular pelo número da Inscrição do candidato.
	 * @param numeroInscricao
	 * @return
	 * @throws DAOException
	 */
	public InscricaoVestibular findByNumeroInscricao(Integer numeroInscricao) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT new InscricaoVestibular(id) FROM InscricaoVestibular ");
			hql.append("WHERE numeroInscricao = :numeroInscricao");
			
			Query q = getSession().createQuery(hql.toString());
			q.setLong("numeroInscricao", numeroInscricao);
			
			return (InscricaoVestibular) q.uniqueResult();

		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		
	}	
	
	/**
	 * Retorna a ResultadoClassificacaoCandidato pelo número da Inscrição do candidato.
	 * @param numeroInscricao
	 * @return
	 * @throws DAOException
	 */
	public boolean existeResultadoClassificacaoByNumeroInscricao(Integer numeroInscricao) throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select id_resultado_classificacao_candidato from vestibular.resultado_classificacao_candidato " +
				" where numero_inscricao = " + numeroInscricao);
		
		Query q = getSession().createSQLQuery(sql.toString());
		return ( q.uniqueResult() != null );
	}	
	
	/**
	 * Retorna uma lista de número de inscrição de vestibular do resultado de classificação,
	 * baseado em uma possível lista de inscrições passadas por parâmetro.
	 * @param listaNumInscricao
	 * @return
	 * @throws DAOException
	 */
	public Collection<Integer> findByListaInscricaoCadastradas(Collection<Integer> listaNumInscricao, ProcessoSeletivoVestibular processoSeletivo) throws DAOException {
		Collection<Integer> numerosInscricao = listaNumInscricao;
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT rcc.numeroInscricao FROM ResultadoClassificacaoCandidato rcc ");
		hql.append("WHERE rcc.numeroInscricao in (:listaNumInscricao)");
		if (processoSeletivo != null)
			hql.append(" and rcc.inscricaoVestibular.processoSeletivo.id = :idProcessoSeletivo");
		
		Query q = getSession().createQuery(hql.toString());
		
		// para evita comando SQL muito longo, quebra-se a lista de número de inscrições em sublistas menores
		Collection<Integer> resultado = new ArrayList<Integer>();
		while (!numerosInscricao.isEmpty()) {
			int k = 0;
			Collection<Integer> subLista = new ArrayList<Integer>();
			for (java.util.Iterator<Integer> iterator = numerosInscricao.iterator(); iterator.hasNext() && k < TAMANHO_MAXIMO_CONSULTA_IN; k++ ) {
				subLista.add(iterator.next());
				iterator.remove();
			}
			if (subLista.size() > 0) {
				q.setParameterList("listaNumInscricao", subLista);
				if (processoSeletivo != null)
					q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
				
				@SuppressWarnings("unchecked")
				Collection<Integer> lista = q.list();
				if (lista != null)
					resultado.addAll(lista);
			}
		}
		return resultado;
	}
	
	/** Retorna uma coleção de inscrições que possuem GRU quitadas, verificando nos dados comuns aos sistemas.
	 * @param idProcessoSeletivo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<InscricaoVestibular> findAllInscricaoGRUPaga(int idProcessoSeletivo) throws HibernateException, DAOException {
		String projecao = "inscricao.id," +
				" inscricao.numeroInscricao," +
				" inscricao.pessoa.id," +
				" inscricao.pessoa.nome," +
				" inscricao.pessoa.cpf_cnpj," +
				" inscricao.idGRU";
		String hql = "select " + projecao +
				" from InscricaoVestibular inscricao" +
				" where inscricao.processoSeletivo.id = :idProcessoSeletivo" +
				" and inscricao.gruQuitada = true";
		Query q = getSession().createQuery(hql);
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		@SuppressWarnings("unchecked")
		Collection<InscricaoVestibular> lista = HibernateUtils.parseTo(q.list(), projecao, InscricaoVestibular.class, "inscricao");
		return lista;
	}

	/** Retorna uma coleção de inscrições que possuem GRU quitadas, verificando nos dados comuns aos sistemas.
	 * @param idProcessoSeletivo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<InscricaoVestibular> verificaInscricaoGRUPaga(int idProcessoSeletivo) throws HibernateException, DAOException {
		String projecao = "inscricao.id," +
				" inscricao.numeroInscricao," +
				" inscricao.pessoa.id," +
				" inscricao.pessoa.nome," +
				" inscricao.pessoa.cpf_cnpj," +
				" inscricao.idGRU";
		String hql = "select " + projecao +
				" from InscricaoVestibular inscricao" +
				" where inscricao.processoSeletivo.id = :idProcessoSeletivo" +
				" and (inscricao.gruQuitada is null or inscricao.gruQuitada = false)";
		Query q = getSession().createQuery(hql);
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		@SuppressWarnings("unchecked")
		Collection<InscricaoVestibular> lista = HibernateUtils.parseTo(q.list(), projecao, InscricaoVestibular.class, "inscricao");
		if (!isEmpty(lista)) {
			Collection<Integer> idsGRUsPagas = new ArrayList<Integer>();
			for (InscricaoVestibular inscricao : lista)
				if (inscricao.getIdGRU() != null)
					idsGRUsPagas.add(inscricao.getIdGRU());
			idsGRUsPagas = GuiaRecolhimentoUniaoHelper.isGRUQuitada(idsGRUsPagas);
			if (!isEmpty(idsGRUsPagas)) {
				Iterator<InscricaoVestibular> iterator = lista.iterator();
				while (iterator.hasNext()) {
					if (!idsGRUsPagas.contains(iterator.next().getIdGRU()))
						iterator.remove();
				}
				return lista;
			}
		}
		return null;
	}
}