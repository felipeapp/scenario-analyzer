/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '04/04/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioCentroDepartamento;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioProjetosArea;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioProjetosAreaAnalitico;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioProjetosDocente;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioResumoCotas;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioSinteticoFinanciamentos;
import br.ufrn.sigaa.pesquisa.relatorios.ParametrosRelatorioBolsaPesquisa;

/**
 * DAO utilizado para as consultas que geram relatórios de
 * gerenciamento do modulo de pesquisa
 *
 * @author ricardo
 *
 */
public class RelatoriosPesquisaDao extends GenericSigaaDAO {

	/** Retorna um relatório sintético de financiamentos.
	 * @param anoInicial
	 * @param anoFinal
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, LinhaRelatorioSinteticoFinanciamentos> findFinanciamentosSintetico(int anoInicial, int anoFinal)
		throws DAOException {

		Map<Integer, LinhaRelatorioSinteticoFinanciamentos> relatorio = new TreeMap<Integer, LinhaRelatorioSinteticoFinanciamentos>();

		// Buscar projetos internos
		StringBuilder hql = new StringBuilder();
		hql.append(" select p.codigo.ano, count(p.id) ");
		hql.append(" from ProjetoPesquisa p ");
		hql.append(" where p.codigo.ano between :anoInicial and :anoFinal ");
		hql.append(" and p.projeto.interno = trueValue() ");
		hql.append(" group by p.codigo.ano ");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("anoInicial", anoInicial);
		q.setInteger("anoFinal", anoFinal);

		List internos = q.list();
		Iterator it = internos.iterator();
		while (it.hasNext()) {
			int col = 0;
			Object[] colunas = (Object[]) it.next();

			int ano = (Integer) colunas[col++];
			LinhaRelatorioSinteticoFinanciamentos linha = relatorio.get( ano );
			if ( linha == null ) {
				linha = new LinhaRelatorioSinteticoFinanciamentos();
			}

			linha.setAno( ano );
			linha.setTotalInternos((Long) colunas[col++]);
			relatorio.put(ano, linha);
		}

		// Buscar projetos externos
		hql = new StringBuilder();
		hql.append( " select f.projetoPesquisa.codigo.ano, f.entidadeFinanciadora.nome, count(f.id) " );
		hql.append( " from FinanciamentoProjetoPesq f" );
		hql.append(" where f.projetoPesquisa.codigo.ano between :anoInicial and :anoFinal ");
		hql.append(" and f.projetoPesquisa.projeto.interno = falseValue() ");
		hql.append(" group by f.projetoPesquisa.codigo.ano, f.entidadeFinanciadora.nome ");
		hql.append(" order by f.projetoPesquisa.codigo.ano, f.entidadeFinanciadora.nome  ");
		q = getSession().createQuery(hql.toString());
		q.setInteger("anoInicial", anoInicial);
		q.setInteger("anoFinal", anoFinal);

		List externos = q.list();
		it = externos.iterator();
		while (it.hasNext()) {
			int col = 0;
			Object[] colunas = (Object[]) it.next();

			int ano = (Integer) colunas[col++];
			LinhaRelatorioSinteticoFinanciamentos linha = relatorio.get( ano );
			if ( linha == null ) {
				linha = new LinhaRelatorioSinteticoFinanciamentos();
			}

			linha.setAno( ano );
			linha.getFinanciamentos().put((String) colunas[col++], (Long) colunas[col++]);
			relatorio.put(ano, linha);
		}

		return relatorio;
	}

	/** Retorna um relatório de projetos por docentes. 
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, LinhaRelatorioProjetosDocente> findProjetosDocente(int ano) throws DAOException {
		
		Map<String, LinhaRelatorioProjetosDocente> relatorio =  new TreeMap<String, LinhaRelatorioProjetosDocente>();
		
		StringBuilder hql = new StringBuilder();
		hql.append(" select s.siape, p.cpf_cnpj, p.nome, s.lotacao, f.denominacao, s.id_categoria, count(proj.id_projeto) ");
		hql.append(" from comum.pessoa p, rh.servidor s, projetos.membro_projeto m, pesquisa.projeto_pesquisa proj, rh.formacao f ");
		hql.append(" where p.id_pessoa = s.id_pessoa ");
		hql.append(" and s.id_servidor = m.id_servidor ");
		hql.append(" and proj.id_projeto = m.id_projeto_pesquisa ");
		hql.append(" and s.id_formacao = f.id_formacao ");
		hql.append(" and proj.cod_ano = :ano ");
		hql.append(" group by m.id_servidor, s.siape, p.cpf_cnpj, p.nome, s.lotacao, s.id_categoria, f.denominacao ");
		hql.append(" order by p.nome ");
		Query q = getSession().createSQLQuery(hql.toString());
		q.setInteger("ano", ano);

		List docentes = q.list();
		Iterator it = docentes.iterator();
		
		while(it.hasNext()){

			Object[] colunas = (Object[]) it.next();
			
			String formacao = (String) colunas[4];
			LinhaRelatorioProjetosDocente linha = relatorio.get(formacao);
			if(linha == null)
				linha = new LinhaRelatorioProjetosDocente();
			
			BigInteger aux = (BigInteger) colunas[6];
			
			int numProjetos = aux.intValue();
			
			if(numProjetos == 1)
				linha.setCom29menos(linha.getCom29menos() + 1);
			else if(numProjetos == 2)
				linha.setCom30a70(linha.getCom30a70() + 1);
			else if(numProjetos >= 3)
				linha.setCom71mais(linha.getCom71mais() + 1);
			
			linha.setFormacao(formacao);
			relatorio.put(formacao, linha);
		}
		
		return relatorio;
	}
	
	/** Retorna um relatório de projetos por área.
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, LinhaRelatorioProjetosArea> findProjetosArea(int ano) throws DAOException {
		
		Map<String, LinhaRelatorioProjetosArea> relatorio =  new TreeMap<String, LinhaRelatorioProjetosArea>();
		
		StringBuilder hql = new StringBuilder();
		hql.append(" select s.siape, p.cpf_cnpj, p.nome, s.lotacao, ga.nome, s.id_categoria, count(proj.id_projeto) ");
		hql.append(" from comum.pessoa p, rh.servidor s, projetos.membro_projeto m, pesquisa.projeto_pesquisa proj, comum.area_conhecimento_cnpq a, comum.area_conhecimento_cnpq ga ");
		hql.append(" where p.id_pessoa = s.id_pessoa ");
		hql.append(" and s.id_servidor = m.id_servidor ");
		hql.append(" and proj.id_projeto = m.id_projeto_pesquisa ");
		hql.append(" and proj.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
		hql.append(" and a.id_grande_area = ga.id_area_conhecimento_cnpq ");
		hql.append(" and proj.cod_ano = :ano ");
		hql.append(" group by m.id_servidor, s.siape, p.cpf_cnpj, p.nome, s.lotacao, s.id_categoria, ga.nome ");
		hql.append(" order by p.nome ");
		Query q = getSession().createSQLQuery(hql.toString());
		q.setInteger("ano", ano);

		List docentes = q.list();
		Iterator it = docentes.iterator();
		
		while(it.hasNext()){

			Object[] colunas = (Object[]) it.next();
			
			String area = (String) colunas[4];
			LinhaRelatorioProjetosArea linha = relatorio.get(area);
			if(linha == null)
				linha = new LinhaRelatorioProjetosArea();
			
			BigInteger aux = (BigInteger) colunas[6];
			
			int numProjetos = aux.intValue();
			
			if(numProjetos == 1)
				linha.setCom29menos(linha.getCom29menos() + 1);
			else if(numProjetos == 2)
				linha.setCom30a70(linha.getCom30a70() + 1);
			else if(numProjetos >= 3)
				linha.setCom71mais(linha.getCom71mais() + 1);
			
			linha.setArea(area);
			relatorio.put(area, linha);
		}
		
		return relatorio;
	}
	
	/** Retorna um relatório analítico de projeto por área.
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, LinhaRelatorioProjetosAreaAnalitico> findProjetosAreaAnalitico(int ano) throws DAOException {
		
		Map<String, LinhaRelatorioProjetosAreaAnalitico> relatorio =  new TreeMap<String, LinhaRelatorioProjetosAreaAnalitico>();
		
		StringBuilder hql = new StringBuilder();
		hql.append(" select s.siape, p.cpf_cnpj, p.nome, u.nome as lotacao, ga.nome as nome_area, count(proj.id_projeto) ");
		hql.append(" from comum.pessoa p, rh.servidor s, projetos.membro_projeto m, pesquisa.projeto_pesquisa proj, comum.area_conhecimento_cnpq a, comum.area_conhecimento_cnpq ga, comum.unidade u ");
		hql.append(" where p.id_pessoa = s.id_pessoa ");
		hql.append(" and s.id_servidor = m.id_servidor ");
		hql.append(" and s.id_unidade = u.id_unidade ");
		hql.append(" and proj.id_projeto = m.id_projeto_pesquisa ");
		hql.append(" and proj.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
		hql.append(" and a.id_grande_area = ga.id_area_conhecimento_cnpq ");
		hql.append(" and proj.cod_ano = :ano ");
		hql.append(" group by m.id_servidor, s.siape, p.cpf_cnpj, p.nome, u.nome, s.id_categoria, ga.nome ");
		hql.append(" order by ga.nome, p.nome ");
		Query q = getSession().createSQLQuery(hql.toString());
		q.setInteger("ano", ano);

		List docentes = q.list();
		Iterator it = docentes.iterator();
		
		while(it.hasNext()){
			int col = 0;
			Object[] colunas = (Object[]) it.next();
			
			int siape = (Integer) colunas[col++];
			BigInteger aux1 = (BigInteger) colunas[col++];
			Long cpf = aux1 != null ? aux1.longValue() : 0;
			String nome = (String) colunas[col++];
			String lotacao = (String) colunas[col++];
			String area = (String) colunas[col++];
			int numProjetos = ((BigInteger) colunas[col++]).intValue();
			
			LinhaRelatorioProjetosAreaAnalitico linha = relatorio.get(area); 
			if(linha == null)
				linha = new LinhaRelatorioProjetosAreaAnalitico();
			
			LinhaRelatorioProjetosAreaAnalitico novaLinha = new LinhaRelatorioProjetosAreaAnalitico();
			novaLinha.setSiape( siape );
			novaLinha.setCpf( cpf );
			novaLinha.setNome( nome );
			novaLinha.setLotacao( lotacao );
			novaLinha.setArea( area );
			novaLinha.setNum_projetos(numProjetos);
			
			linha.setArea(area);
			linha.getLinhas().put(nome, novaLinha);
			
			relatorio.put(area, linha);
		}
		
		return relatorio;
	}
	
	/**
	 * Retorna um relatório quantitativo para bolsas de pesquisa ativas.
	 * @param tipoBolsa
	 * @param param
	 * @param ordenarPor
	 * @param filtroCentro
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, LinhaRelatorioBolsaPesquisa> findBolsasPesquisa(List<Integer> tiposBolsa, ParametrosRelatorioBolsaPesquisa param, int ordenarPor, int filtroCentro) throws DAOException {
		
		Map<Integer, LinhaRelatorioBolsaPesquisa> relatorio =  new TreeMap<Integer, LinhaRelatorioBolsaPesquisa>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct p.nome, dep.nome as depto_nome, cen.sigla as centro ");
		
		if(tiposBolsa.contains((new Integer(1)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 1 then pt.id_plano_trabalho end) as propesq_ic ");
		
		if(tiposBolsa.contains((new Integer(2)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 2 then pt.id_plano_trabalho end) as pibic_ic ");
		
		if(tiposBolsa.contains((new Integer(3)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 3 then pt.id_plano_trabalho end) as balcao_ic ");
		
		if(tiposBolsa.contains((new Integer(4)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 4 then pt.id_plano_trabalho end) as voluntario_ic ");
		
		if(tiposBolsa.contains((new Integer(5)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 5 then pt.id_plano_trabalho end) as outros_ic ");
		
		if(tiposBolsa.contains((new Integer(6)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 6 then pt.id_plano_trabalho end) as propesq_inducao_ic ");
		
		if(tiposBolsa.contains((new Integer(7)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 7 then pt.id_plano_trabalho end) as propesq_it ");
		
		if(tiposBolsa.contains((new Integer(8)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 8 then pt.id_plano_trabalho end) as pibit_it ");
		
		if(tiposBolsa.contains((new Integer(9)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 9 then pt.id_plano_trabalho end) as voluntario_it ");
				
		if(tiposBolsa.contains((new Integer(11491050)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 11491050 then pt.id_plano_trabalho end) as dinter_ic ");
		
		if(tiposBolsa.contains((new Integer(11491051)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 11491051 then pt.id_plano_trabalho end) as dinter_it ");
		
		if(tiposBolsa.contains((new Integer(11491052)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 11491052 then pt.id_plano_trabalho end) as prh_anp_ic ");
		
		if(tiposBolsa.contains((new Integer(11511580)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 11511580 then pt.id_plano_trabalho end) as reuni_ic ");
		
		if(tiposBolsa.contains((new Integer(11511581)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 11511581 then pt.id_plano_trabalho end) as reuni_it ");
		
		if(tiposBolsa.contains((new Integer(13266400)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 13266400 then pt.id_plano_trabalho end) as reuni_dinter_it ");
		
		if(tiposBolsa.contains((new Integer(14165150)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 14165150 then pt.id_plano_trabalho end) as bolsas_ic_it_picme ");
		
		if(tiposBolsa.contains((new Integer(16710600)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 16710600 then pt.id_plano_trabalho end) as pnaes_ic ");
		
		if(tiposBolsa.contains((new Integer(16710601)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 16710601 then pt.id_plano_trabalho end) as pnaes_it ");
		
		if(tiposBolsa.contains((new Integer(17931220)).toString()))
			sql.append(",count(case when pt.tipo_bolsa = 17931220 then pt.id_plano_trabalho end) as pesquisa_pet_ic ");
		
		sql.append("from pesquisa.plano_trabalho pt ");
		sql.append("join pesquisa.tipo_bolsa_pesquisa tbp on pt.tipo_bolsa=tbp.id_tipo_bolsa ");
		sql.append("join rh.servidor s on pt.id_orientador=s.id_servidor ");
		sql.append("join comum.pessoa p on s.id_pessoa=p.id_pessoa ");
		sql.append("join comum.unidade dep on s.id_unidade=dep.id_unidade ");
		sql.append("left join comum.unidade cen on dep.id_gestora=cen.id_unidade ");
		sql.append("where pt.data_inicio < now() ");
		sql.append("and (pt.data_fim is null or pt.data_fim > now()) ");
		sql.append("and pt.tipo_bolsa <> 100 ");
		
		//filtra pelo centro
		if(filtroCentro != -1)
			sql.append("and cen.id_unidade = "+filtroCentro+" ");
		
		sql.append("group by cen.sigla, dep.nome, p.nome ");
		
		//ordena pelo centro
		if(ordenarPor == 1)
			sql.append("order by cen.sigla;");
		
		//ordena pelo departamento
		else if(ordenarPor == 2)
			sql.append("order by dep.nome;");
		
		//ordena pelo docente
		else if(ordenarPor == 3)
			sql.append("order by p.nome;");
		
		Query q = getSession().createSQLQuery(sql.toString());

		List resultado = q.list();
		Iterator it = resultado.iterator();
		int indice = 0;
		
		while(it.hasNext()){
			int col = 0;
			indice++;
			Object[] colunas = (Object[]) it.next();
			
			String docente = (String)colunas[col++];
			String departamento = (String)colunas[col++];
			String centro = (String) colunas[col++];			
			
			LinhaRelatorioBolsaPesquisa linha = relatorio.get(indice); 
			if(linha == null)
				linha = new LinhaRelatorioBolsaPesquisa();
			
			linha.setDocente(docente);
			linha.setDepartamento(departamento);
			linha.setCentro(centro);
			
			if(tiposBolsa.contains((new Integer(1)).toString())){
				BigInteger propesqIC = (BigInteger)colunas[col++];
				linha.setContPropesqIC(propesqIC.intValue());
				param.setPropesqIC(true);
			}
				
			if(tiposBolsa.contains((new Integer(2)).toString())){
				BigInteger pibic = (BigInteger)colunas[col++];
				linha.setContPibic(pibic.intValue());
				param.setPibic(true);
			}
			
			if(tiposBolsa.contains((new Integer(3)).toString())){
				BigInteger balcao = (BigInteger)colunas[col++];
				linha.setContBalcao(balcao.intValue());
				param.setBalcao(true);
			}
			
			if(tiposBolsa.contains((new Integer(4)).toString())){
				BigInteger voluntarioIC = (BigInteger)colunas[col++];
				linha.setContVoluntarioIC(voluntarioIC.intValue());
				param.setVoluntarioIC(true);
			}
			
			if(tiposBolsa.contains((new Integer(5)).toString())){
				BigInteger outros = (BigInteger)colunas[col++];
				linha.setContOutros(outros.intValue());
				param.setOutros(true);
			}
			
			if(tiposBolsa.contains((new Integer(6)).toString())){
				BigInteger propesqInducaoIC = (BigInteger)colunas[col++];
				linha.setContPropesqInducaoIC(propesqInducaoIC.intValue());
				param.setPropesqInducaoIC(true);
			}
			
			if(tiposBolsa.contains((new Integer(7)).toString())){
				BigInteger propesqIT = (BigInteger)colunas[col++];
				linha.setContPropesqIT(propesqIT.intValue());
				param.setPropesqIT(true);
			}
			
			if(tiposBolsa.contains((new Integer(8)).toString())){
				BigInteger pibit = (BigInteger)colunas[col++];
				linha.setContPibit(pibit.intValue());
				param.setPibit(true);
			}
			
			if(tiposBolsa.contains((new Integer(9)).toString())){
				BigInteger voluntarioIT = (BigInteger)colunas[col++];
				linha.setContVoluntarioIT(voluntarioIT.intValue());
				param.setVoluntarioIT(true);
			}
					
			if(tiposBolsa.contains((new Integer(11491050)).toString())){
				BigInteger dinterIC = (BigInteger)colunas[col++];
				linha.setContDinterIC(dinterIC.intValue());
				param.setDinterIC(true);
			}
			
			if(tiposBolsa.contains((new Integer(11491051)).toString())){
				BigInteger dinterIT = (BigInteger)colunas[col++];
				linha.setContDinterIT(dinterIT.intValue());
				param.setDinterIT(true);
			}
			
			if(tiposBolsa.contains((new Integer(11491052)).toString())){
				BigInteger prhAnp = (BigInteger)colunas[col++];
				linha.setContPrhAnp(prhAnp.intValue());
				param.setPrhAnp(true);
			}
			
			if(tiposBolsa.contains((new Integer(11511580)).toString())){
				BigInteger reuniIC = (BigInteger)colunas[col++];
				linha.setContReuniIC(reuniIC.intValue());
				param.setReuniIC(true);
			}
			
			if(tiposBolsa.contains((new Integer(11511581)).toString())){
				BigInteger reuniIT = (BigInteger)colunas[col++];
				linha.setContReuniIT(reuniIT.intValue());
				param.setReuniIT(true);
			}
			
			if(tiposBolsa.contains((new Integer(13266400)).toString())){
				BigInteger reuniDinterIT = (BigInteger)colunas[col++];
				linha.setContReuniDinterIT(reuniDinterIT.intValue());
				param.setReuniDinterIT(true);
			}
			
			if(tiposBolsa.contains((new Integer(14165150)).toString())){
				BigInteger picme = (BigInteger)colunas[col++];
				linha.setContPicme(picme.intValue());
				param.setPicme(true);
			}
			
			if(tiposBolsa.contains((new Integer(16710600)).toString())){
				BigInteger pnaesIC = (BigInteger)colunas[col++];
				linha.setContPnaesIC(pnaesIC.intValue());
				param.setPnaesIC(true);
			}
			
			if(tiposBolsa.contains((new Integer(16710601)).toString())){
				BigInteger pnaesIT = (BigInteger)colunas[col++];
				linha.setContPnaesIT(pnaesIT.intValue());
				param.setPnaesIT(true);
			}
			
			if(tiposBolsa.contains((new Integer(17931220)).toString())){
				BigInteger pet = (BigInteger)colunas[col++];
				linha.setContPet(pet.intValue());
				param.setPet(true);
			}
			
			linha.getLinhas().put(indice, linha);
			
			relatorio.put(indice, linha);
		}
		
		return relatorio;
	}
	
	/**
	 * Retorna um relatório de Cotas de Bolsas.
	 * 
	 * @param idEdital
	 * @param tiposBolsa
	 * @param param
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, LinhaRelatorioResumoCotas> findResumoCotas(int idEdital, List<Integer> tiposBolsa, ParametrosRelatorioBolsaPesquisa param) throws DAOException {
		
		StringBuilder consulta = new StringBuilder();
		
		consulta.append("select nome, s.id_servidor ");
		
		consulta.append(",sum(case when pt.id_edital=:idEdital then 1 else 0 end) as solicitacoes ");
		
		if(tiposBolsa.contains((new Integer(1)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 1 then 1 else 0 end) as propesq_ic ");
		
		if(tiposBolsa.contains((new Integer(2)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 2 then 1 else 0 end) as pibic_ic ");
		
		if(tiposBolsa.contains((new Integer(3)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 3 then 1 else 0 end) as balcao_ic ");
		
		if(tiposBolsa.contains((new Integer(4)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 4 then 1 else 0 end) as voluntario_ic ");
		
		if(tiposBolsa.contains((new Integer(5)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 5 then 1 else 0 end) as outros_ic ");
		
		if(tiposBolsa.contains((new Integer(6)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 6 then 1 else 0 end) as propesq_inducao_ic ");
		
		if(tiposBolsa.contains((new Integer(7)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 7 then 1 else 0 end) as propesq_it ");
		
		if(tiposBolsa.contains((new Integer(8)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 8 then 1 else 0 end) as pibit_it ");
		
		if(tiposBolsa.contains((new Integer(9)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 9 then 1 else 0 end) as voluntario_it ");
				
		if(tiposBolsa.contains((new Integer(11491050)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 11491050 then 1 else 0 end) as dinter_ic ");
		
		if(tiposBolsa.contains((new Integer(11491051)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 11491051 then 1 else 0 end) as dinter_it ");
		
		if(tiposBolsa.contains((new Integer(11491052)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 11491052 then 1 else 0 end) as prh_anp_ic ");
		
		if(tiposBolsa.contains((new Integer(11511580)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 11511580 then 1 else 0 end) as reuni_ic ");
		
		if(tiposBolsa.contains((new Integer(11511581)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 11511581 then 1 else 0 end) as reuni_it ");
		
		if(tiposBolsa.contains((new Integer(13266400)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 13266400 then 1 else 0 end) as reuni_dinter_it ");
		
		if(tiposBolsa.contains((new Integer(14165150)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 14165150 then 1 else 0 end) as bolsas_ic_it_picme ");
		
		if(tiposBolsa.contains((new Integer(16710600)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 16710600 then 1 else 0 end) as pnaes_ic ");
		
		if(tiposBolsa.contains((new Integer(16710601)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 16710601 then 1 else 0 end) as pnaes_it ");
		
		if(tiposBolsa.contains((new Integer(17931220)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 17931220 then 1 else 0 end) as pesquisa_pet_ic ");
		
		if(tiposBolsa.contains((new Integer(2178879)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 2178879 then 1 else 0 end) as pesquisa_reuni_nuplam_it ");
		
		if(tiposBolsa.contains((new Integer(3264511)).toString()))
			consulta.append(",sum(case when pt.tipo_bolsa = 3264511 then 1 else 0 end) as pesquisa_pibic_af ");
		
		consulta.append("from pesquisa.plano_trabalho pt ");
		consulta.append("join rh.servidor s on ( s.id_servidor = pt.id_orientador) ");
		consulta.append("join comum.pessoa p on ( p.id_pessoa = s.id_pessoa) ");
		consulta.append("where pt.data_fim > :hoje ");
		consulta.append("and pt.id_orientador in  ");
		consulta.append("( select distinct id_orientador from pesquisa.plano_trabalho where id_edital = :idEdital ) ");
		consulta.append("group by id_orientador, nome, s.id_servidor ");
		consulta.append("order by nome ");
		
		Query q = getSession().createSQLQuery(consulta.toString());
		q.setInteger("idEdital", idEdital);
		q.setDate("hoje", new Date());
		
		Map<Integer, LinhaRelatorioResumoCotas> relatorio =  new TreeMap<Integer, LinhaRelatorioResumoCotas>();
		List resultado = q.list();
		Iterator it = resultado.iterator();
		int indice = 0;
		
		while(it.hasNext()){
			int col = 0;
			indice++;
			Object[] colunas = (Object[]) it.next();
			
			String docente = (String)colunas[col++];
			int idDocente = (Integer) colunas[col++]; 
			
			int solicitacoes = Integer.parseInt(colunas[col++].toString());
			
			LinhaRelatorioResumoCotas linha = relatorio.get(indice); 
			if(linha == null)
				linha = new LinhaRelatorioResumoCotas();
			
			linha.setDocente(docente);
			linha.setIdDocente(idDocente);
			linha.setDocenteProdutividade(false);
			linha.setSolicitacoes(solicitacoes);
			
			if(tiposBolsa.contains((new Integer(1)).toString())){
				BigInteger propesqIC = (BigInteger)colunas[col++];
				linha.setContPropesqIC(propesqIC.intValue());
				param.setPropesqIC(true);
			}
				
			if(tiposBolsa.contains((new Integer(2)).toString())){
				BigInteger pibic = (BigInteger)colunas[col++];
				linha.setContPibic(pibic.intValue());
				param.setPibic(true);
			}
			
			if(tiposBolsa.contains((new Integer(3)).toString())){
				BigInteger balcao = (BigInteger)colunas[col++];
				linha.setContBalcao(balcao.intValue());
				param.setBalcao(true);
			}
			
			if(tiposBolsa.contains((new Integer(4)).toString())){
				BigInteger voluntarioIC = (BigInteger)colunas[col++];
				linha.setContVoluntarioIC(voluntarioIC.intValue());
				param.setVoluntarioIC(true);
			}
			
			if(tiposBolsa.contains((new Integer(5)).toString())){
				BigInteger outros = (BigInteger)colunas[col++];
				linha.setContOutros(outros.intValue());
				param.setOutros(true);
			}
			
			if(tiposBolsa.contains((new Integer(6)).toString())){
				BigInteger propesqInducaoIC = (BigInteger)colunas[col++];
				linha.setContPropesqInducaoIC(propesqInducaoIC.intValue());
				param.setPropesqInducaoIC(true);
			}
			
			if(tiposBolsa.contains((new Integer(7)).toString())){
				BigInteger propesqIT = (BigInteger)colunas[col++];
				linha.setContPropesqIT(propesqIT.intValue());
				param.setPropesqIT(true);
			}
			
			if(tiposBolsa.contains((new Integer(8)).toString())){
				BigInteger pibit = (BigInteger)colunas[col++];
				linha.setContPibit(pibit.intValue());
				param.setPibit(true);
			}
			
			if(tiposBolsa.contains((new Integer(9)).toString())){
				BigInteger voluntarioIT = (BigInteger)colunas[col++];
				linha.setContVoluntarioIT(voluntarioIT.intValue());
				param.setVoluntarioIT(true);
			}
					
			if(tiposBolsa.contains((new Integer(11491050)).toString())){
				BigInteger dinterIC = (BigInteger)colunas[col++];
				linha.setContDinterIC(dinterIC.intValue());
				param.setDinterIC(true);
			}
			
			if(tiposBolsa.contains((new Integer(11491051)).toString())){
				BigInteger dinterIT = (BigInteger)colunas[col++];
				linha.setContDinterIT(dinterIT.intValue());
				param.setDinterIT(true);
			}
			
			if(tiposBolsa.contains((new Integer(11491052)).toString())){
				BigInteger prhAnp = (BigInteger)colunas[col++];
				linha.setContPrhAnp(prhAnp.intValue());
				param.setPrhAnp(true);
			}
			
			if(tiposBolsa.contains((new Integer(11511580)).toString())){
				BigInteger reuniIC = (BigInteger)colunas[col++];
				linha.setContReuniIC(reuniIC.intValue());
				param.setReuniIC(true);
			}
			
			if(tiposBolsa.contains((new Integer(11511581)).toString())){
				BigInteger reuniIT = (BigInteger)colunas[col++];
				linha.setContReuniIT(reuniIT.intValue());
				param.setReuniIT(true);
			}
			
			if(tiposBolsa.contains((new Integer(13266400)).toString())){
				BigInteger reuniDinterIT = (BigInteger)colunas[col++];
				linha.setContReuniDinterIT(reuniDinterIT.intValue());
				param.setReuniDinterIT(true);
			}
			
			if(tiposBolsa.contains((new Integer(14165150)).toString())){
				BigInteger picme = (BigInteger)colunas[col++];
				linha.setContPicme(picme.intValue());
				param.setPicme(true);
			}
			
			if(tiposBolsa.contains((new Integer(16710600)).toString())){
				BigInteger pnaesIC = (BigInteger)colunas[col++];
				linha.setContPnaesIC(pnaesIC.intValue());
				param.setPnaesIC(true);
			}
			
			if(tiposBolsa.contains((new Integer(16710601)).toString())){
				BigInteger pnaesIT = (BigInteger)colunas[col++];
				linha.setContPnaesIT(pnaesIT.intValue());
				param.setPnaesIT(true);
			}
			
			if(tiposBolsa.contains((new Integer(17931220)).toString())){
				BigInteger pet = (BigInteger)colunas[col++];
				linha.setContPet(pet.intValue());
				param.setPet(true);
			}
			
			if(tiposBolsa.contains((new Integer(2178879)).toString())){
				BigInteger reuniNuplmaIt = (BigInteger)colunas[col++];
				linha.setContReuniNuplamIT(reuniNuplmaIt.intValue());
				param.setReuniNuplamIT(true);
			}
			
			if(tiposBolsa.contains((new Integer(3264511)).toString())){
				BigInteger pibicAf = (BigInteger)colunas[col++];
				linha.setContPibicAf(pibicAf.intValue());
				param.setPibicAf(true);
			}
			
			linha.getLinhas().put(indice, linha);
			
			relatorio.put(indice, linha);
		}
		
		return relatorio;
	}
	
	/**
	 * Quantitativos de bolsistas por centro é por departamento.    
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Map<String, Collection<LinhaRelatorioCentroDepartamento>> findRelatorioQuantBolsasCentroDepartamento() throws DAOException {
			Map<String, Collection<LinhaRelatorioCentroDepartamento>> result = new HashMap<String, Collection<LinhaRelatorioCentroDepartamento>>(); 
		
		String sql = "select ur.nome as centro, u.nome as departamento," +
				" sum(case when pt.tipo_bolsa in ( 4, 9 ) then 1 else 0 end) as voluntario," +
				" sum(case when pt.tipo_bolsa = 2 then 1 else 0 end) as pibic," +
				" sum(case when pt.tipo_bolsa = 8 then 1 else 0 end) as pibit," +
				" sum(case when pt.tipo_bolsa = 3264511 then 1 else 0 end) as pibic_aa," +
				" sum(case when pt.tipo_bolsa = 4779164 then 1 else 0 end) as pibic_em," +
				" sum(case when pt.tipo_bolsa = 3 then 1 else 0 end) as balcao," +
				" sum(case when pt.tipo_bolsa = 1 then 1 else 0 end) as propesq_ic," +
				" sum(case when pt.tipo_bolsa = 7 then 1 else 0 end) as propesq_it," +
				" sum(case when pt.tipo_bolsa = 6 then 1 else 0 end) as propesq_inducao," +
				" sum(case when pt.tipo_bolsa = 11511580 then 1 else 0 end) as reuni_ic," +
				" sum(case when pt.tipo_bolsa = 11511581 then 1 else 0 end) as reuni_it," +
				" sum(case when pt.tipo_bolsa = 2178879 then 1 else 0 end) as reuni_nuplam," +
				" sum(case when pt.tipo_bolsa = 13266400 then 1 else 0 end) as reuni_dinter," +
				" sum(case when pt.tipo_bolsa = 11491050 then 1 else 0 end) as dinter_ic," +
				" sum(case when pt.tipo_bolsa = 11491051 then 1 else 0 end) as dinter_it," +
				" sum(case when pt.tipo_bolsa = 2621672 then 1 else 0 end) as fapern_ic," +
				" sum(case when pt.tipo_bolsa = 11491052 then 1 else 0 end) as prh_anp," +
				" sum(case when pt.tipo_bolsa = 14165150 then 1 else 0 end) as picme," +
				" sum(case when pt.tipo_bolsa = 16710600 then 1 else 0 end) as pnaes_ic," +
				" sum(case when pt.tipo_bolsa = 16710601 then 1 else 0 end) as pnaes_it," +
				" sum(case when pt.tipo_bolsa = 17931220 then 1 else 0 end) as pesquisa_pet" +
				" from pesquisa.plano_trabalho pt" +
				" join rh.servidor s on ( s.id_servidor = pt.id_orientador)" +
				" join comum.unidade u on ( s.id_unidade = u.id_unidade)" +
				" join comum.unidade ur on ( ur.id_unidade = u.unidade_responsavel )" +
				" where pt.data_fim >= now()" +
				" and pt.status = " + TipoStatusPlanoTrabalho.EM_ANDAMENTO +
				" and pt.id_membro_projeto_discente is not null" +
				" group by ur.nome, u.nome" +
				" order by ur.nome, u.nome";
		
		Query q = getSession().createSQLQuery(sql);
		List consulta = q.list();
		String centroAntigo = ""; 
		Collection<LinhaRelatorioCentroDepartamento> resultados = new ArrayList<LinhaRelatorioCentroDepartamento>();
		
		for (Object linha : consulta) {
			Object[] values = (Object[])linha;
			String centro = (String) values[0];
			
			if ( !centroAntigo.equals(centro) && !centroAntigo.equals("") ) {
				result.put(centroAntigo, resultados);
				resultados = new ArrayList<LinhaRelatorioCentroDepartamento>();
			}
			
			LinhaRelatorioCentroDepartamento linhaRelatorio = new LinhaRelatorioCentroDepartamento();
			linhaRelatorio = LinhaRelatorioCentroDepartamento.setValores(linhaRelatorio, values);
			resultados.add(linhaRelatorio);
			centroAntigo = centro;
		}
		
		return result;
	}
	
}