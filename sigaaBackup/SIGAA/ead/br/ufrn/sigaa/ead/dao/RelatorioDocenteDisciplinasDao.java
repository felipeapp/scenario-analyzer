/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 17/04/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ead.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao responsável por realizar consultas necessária para gerar o relatório de docentes com disciplina por semestre.
 * 
 * @author Rafael Gomes
 *
 */
public class RelatorioDocenteDisciplinasDao extends GenericSigaaDAO{

	/**
	 * Retorna os docentes e as disciplinas ministradas por semestre.
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteTurma> findByDocenteDisciplinasByAnoPeriodo(Integer ano, Integer periodo, 
			Unidade unidadeResponsavel, Unidade departamento, Polo polo) throws DAOException {
		
		 String projecao =	" dt.id, " +
  						 	" t.id as dt.turma.id, " +
							" t.codigo as dt.turma.codigo, " +
							" cc.id as dt.turma.disciplina.id, " +
							" cc.codigo as dt.turma.disciplina.codigo, " +
							" ccd.nome as dt.turma.disciplina.detalhes.nome, " +
							" unidComponente.id as dt.turma.disciplina.unidade.id, " +
							" unidComponente.nome as dt.turma.disciplina.unidade.nome, " +
							" unidComponente.unidadeResponsavel.id as dt.turma.disciplina.unidade.unidadeResponsavel.id, " +
							" unidComponente.unidadeResponsavel.nome as dt.turma.disciplina.unidade.unidadeResponsavel.nome, " +
							" cidade.id as dt.turma.polo.cidade.id, " +
							" cidade.nome as dt.turma.polo.cidade.nome, " +
							" uf.sigla as dt.turma.polo.cidade.unidadeFederativa.sigla, " +
							" d.id as dt.docente.id, " +
							" d.siape as dt.docente.siape, " +
							" de.id as dt.docenteExterno.id, " +
							" pDocente.id as dt.docente.pessoa.id, " +
							" pDocente.nome as dt.docente.pessoa.nome, " +
							" pDocente.cpf_cnpj as dt.docente.pessoa.cpf_cnpj, " +
							" unidDocente.id as dt.docente.unidade.id, " +
							" unidDocente.nome as dt.docente.unidade.nome, " +
							" pDocenteExterno.id as dt.docenteExterno.pessoa.id, " +
							" pDocenteExterno.nome as dt.docenteExterno.pessoa.nome, " +
							" pDocenteExterno.cpf_cnpj as dt.docenteExterno.pessoa.cpf_cnpj, " +
							" unidDocenteExterno.id as dt.docenteExterno.unidade.id, " +
							" unidDocenteExterno.nome as dt.docenteExterno.unidade.nome, " +
							" instituicaoDocenteExterno.id as dt.docenteExterno.instituicao.id, " +
							" instituicaoDocenteExterno.nome as dt.docenteExterno.instituicao.nome, " +
							" instituicaoDocenteExterno.sigla as dt.docenteExterno.instituicao.sigla ";
		
		StringBuffer hql = new StringBuffer("select ");
		hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
		hql.append(" from DocenteTurma dt ");
		hql.append(" inner join dt.turma t ");
		hql.append(" inner join t.polo polo ");
		hql.append(" inner join polo.cidade cidade "); 
		hql.append(" inner join cidade.unidadeFederativa uf ");
		hql.append(" inner join t.disciplina cc ");
		hql.append(" inner join cc.detalhes ccd ");
		hql.append(" inner join cc.unidade unidComponente "); 
		hql.append(" inner join unidComponente.unidadeResponsavel unidResponsavel ");
		hql.append(" left join dt.docente d ");
		hql.append(" left join d.pessoa pDocente ");
		hql.append(" left join d.unidade unidDocente "); 
		hql.append(" left join dt.docenteExterno de ");
		hql.append(" left join de.pessoa pDocenteExterno ");
		hql.append(" left join de.unidade unidDocenteExterno ");
		hql.append(" left join de.instituicao instituicaoDocenteExterno ");
		
		hql.append(" where t.ano = :ano ");
		hql.append(" and t.periodo = :periodo ");
		hql.append(" and t.distancia = :distancia ");
		
		if (ValidatorUtil.isNotEmpty(unidadeResponsavel) && unidadeResponsavel.getId() > 0)
			hql.append(" and unidResponsavel.id = :idUnidadeResponsavel ");
		
		if (ValidatorUtil.isNotEmpty(departamento) && departamento.getId() > 0)
			hql.append(" and unidComponente.id = :idDepartamento ");
		
		if (ValidatorUtil.isNotEmpty(polo) && polo.getId() > 0)
			hql.append(" and polo.id = :idPolo ");
		
		hql.append(" order by unidResponsavel.nome, pDocente.nome, pDocenteExterno.nome, ccd.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		if (ValidatorUtil.isNotEmpty(unidadeResponsavel) && unidadeResponsavel.getId() > 0)
			q.setInteger("idUnidadeResponsavel", unidadeResponsavel.getId());
		if (ValidatorUtil.isNotEmpty(departamento) && departamento.getId() > 0)
			q.setInteger("idDepartamento", departamento.getId());
		if (ValidatorUtil.isNotEmpty(polo) && polo.getId() > 0)
			q.setInteger("idPolo", polo.getId());
		q.setBoolean("distancia", true);
		
		@SuppressWarnings("unchecked")
		List<DocenteTurma> lista =  (List<DocenteTurma>) HibernateUtils.parseTo(q.list(), projecao, DocenteTurma.class, "dt");

		return lista;
	}	

	/**
	 * Retorna os docentes e as disciplinas ministradas por semestre.
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteTurma> findListByDocenteDisciplinasByAnoPeriodo(Integer ano, Integer periodo, 
			Unidade unidadeResponsavel, Unidade departamento, Polo polo) throws DAOException {
		
		 String projecao =	" dt.id as ID, " +
							" d.id as DOCENTE_ID, " +
							" d.siape as DOCENTE_SIAPE, " +
							" de.id as DOCENTE_EXTERNO_ID, " +
							" pDocente.id as PESSOA_DOCENTE_ID, " +
							" pDocente.nome as PESSOA_DOCENTE_NOME, " +
							" pDocente.cpf_cnpj as PESSOA_DOCENTE_CPF, " +
							" unidDocente.id as UNIDADE_DOCENTE_ID, " +
							" unidDocente.nome as UNIDADE_DOCENTE_NOME, " +
							" pDocenteExterno.id as PESSOA_DOCENTE_EXTERNO_ID, " +
							" pDocenteExterno.nome as PESSOA_DOCENTE_EXTERNO_NOME, " +
							" pDocenteExterno.cpf_cnpj as PESSOA_DOCENTE_EXTERNO_CPF, " +
							" unidDocenteExterno.id as UNIDADE_DOCENTE_EXTERNO_ID, " +
							" unidDocenteExterno.nome as UNIDADE_DOCENTE_EXTERNO_NOME, " +
							" t.id as TURMA_ID, " +
							" t.codigo as TURMA_CODIGO, " +
							" cc.id as COMPONENTE_ID, " +
							" cc.codigo as COMPONENTE_CODIGO, " +
							" ccd.nome as COMPONENTE_NOME, " +
							" unidComponente.id as UNIDADE_COMPONENTE_ID, " +
							" unidComponente.nome as UNIDADE_COMPONENTE_NOME, " +
							" unidComponente.unidadeResponsavel.id as UNIDADE_RESPONSAVEL_ID, " +
							" unidComponente.unidadeResponsavel.nome as UNIDADE_RESPONSAVEL_NOME, " +
							" cidade.id as CIDADE_ID, " +
							" cidade.nome as CIDADE_NOME, " +
							" uf.sigla as UF, " +
							" instituicaoDocenteExterno.id as INSTITUICAO_ID, " +
							" instituicaoDocenteExterno.nome as INSTITUICAO_NOME, " +
							" instituicaoDocenteExterno.sigla as INSTITUICAO_SIGLA ";
		
		StringBuffer hql = new StringBuffer("select ");
		hql.append(projecao);
		hql.append(" from DocenteTurma dt ");
		hql.append(" inner join dt.turma t ");
		hql.append(" inner join t.polo polo ");
		hql.append(" inner join polo.cidade cidade "); 
		hql.append(" inner join cidade.unidadeFederativa uf ");
		hql.append(" inner join t.disciplina cc ");
		hql.append(" inner join cc.detalhes ccd ");
		hql.append(" inner join cc.unidade unidComponente "); 
		hql.append(" inner join unidComponente.unidadeResponsavel unidResponsavel ");
		hql.append(" left join dt.docente d ");
		hql.append(" left join d.pessoa pDocente ");
		hql.append(" left join d.unidade unidDocente "); 
		hql.append(" left join dt.docenteExterno de ");
		hql.append(" left join de.pessoa pDocenteExterno ");
		hql.append(" left join de.unidade unidDocenteExterno ");
		hql.append(" left join de.instituicao instituicaoDocenteExterno ");
		
		hql.append(" where t.ano = :ano ");
		hql.append(" and t.periodo = :periodo ");
		hql.append(" and t.distancia = :distancia ");
		
		if (ValidatorUtil.isNotEmpty(unidadeResponsavel) && unidadeResponsavel.getId() > 0)
			hql.append(" and unidResponsavel.id = :idUnidadeResponsavel ");
		
		if (ValidatorUtil.isNotEmpty(departamento) && departamento.getId() > 0)
			hql.append(" and unidComponente.id = :idDepartamento ");
		
		if (ValidatorUtil.isNotEmpty(polo) && polo.getId() > 0)
			hql.append(" and polo.id = :idPolo ");
		
		hql.append(" order by unidResponsavel.nome, pDocente.nome, pDocenteExterno.nome, ccd.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		if (ValidatorUtil.isNotEmpty(unidadeResponsavel) && unidadeResponsavel.getId() > 0)
			q.setInteger("idUnidadeResponsavel", unidadeResponsavel.getId());
		if (ValidatorUtil.isNotEmpty(departamento) && departamento.getId() > 0)
			q.setInteger("idDepartamento", departamento.getId());
		if (ValidatorUtil.isNotEmpty(polo) && polo.getId() > 0)
			q.setInteger("idPolo", polo.getId());
		q.setBoolean("distancia", true);
		
	
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		
		List<DocenteTurma> resultado = new ArrayList<DocenteTurma>();
		for( Map<String, Object> map : lista ){
			DocenteTurma dt = new DocenteTurma();
			
			dt.setId( (Integer) map.get( "ID" ) );
			if( map.get( "DOCENTE_SIAPE" ) != null ){
				dt.setDocente( new Servidor() );
				dt.getDocente().setId( (Integer) map.get( "DOCENTE_ID" ) );
				dt.getDocente().setSiape( (Integer) map.get( "DOCENTE_SIAPE" ) );
				dt.getDocente().setPessoa(new Pessoa((Integer) map.get( "PESSOA_DOCENTE_ID" )));
				dt.getDocente().getPessoa().setNome( (String) map.get( "PESSOA_DOCENTE_NOME" ) );
				dt.getDocente().getPessoa().setCpf_cnpj( (Long) map.get( "PESSOA_DOCENTE_CPF" ) );
				dt.getDocente().setUnidade(new Unidade());
				dt.getDocente().getUnidade().setId((Integer) map.get( "UNIDADE_DOCENTE_ID" ));
				dt.getDocente().getUnidade().setNome((String) map.get( "UNIDADE_DOCENTE_NOME" ));
			
			}else if( map.get( "PESSOA_DOCENTE_EXTERNO_CPF" ) != null ){
				dt.setDocenteExterno(new DocenteExterno());
				dt.getDocenteExterno().setId( (Integer) map.get( "DOCENTE_EXTERNO_ID" ) );
				dt.getDocenteExterno().setPessoa(new Pessoa((Integer) map.get( "PESSOA_DOCENTE_EXTERNO_ID" )));
				dt.getDocenteExterno().getPessoa().setCpf_cnpj( (Long) map.get( "PESSOA_DOCENTE_EXTERNO_CPF" ) );
				dt.getDocenteExterno().getPessoa().setNome( (String) map.get( "PESSOA_DOCENTE_EXTERNO_NOME" ) );
				dt.getDocenteExterno().setUnidade(new Unidade());
				dt.getDocenteExterno().getUnidade().setId((Integer) map.get( "UNIDADE_DOCENTE_EXTERNO_ID" ));
				dt.getDocenteExterno().getUnidade().setNome((String) map.get( "UNIDADE_DOCENTE_EXTERNO_NOME" ));
			}
			
			dt.setTurma( new Turma() );
			dt.getTurma().setId( (Integer) map.get( "TURMA_ID" ) );
			dt.getTurma().setCodigo( (String) map.get( "TURMA_CODIGO" ) );
			dt.getTurma().setDisciplina( new ComponenteCurricular((Integer) map.get( "COMPONENTE_ID" )) );
			dt.getTurma().getDisciplina().setCodigo( (String) map.get( "COMPONENTE_CODIGO" ) );
			dt.getTurma().getDisciplina().getDetalhes().setCodigo( (String) map.get( "COMPONENTE_CODIGO" ) );
			dt.getTurma().getDisciplina().setDetalhes( new ComponenteDetalhes() );
			dt.getTurma().getDisciplina().getDetalhes().setNome( (String) map.get( "COMPONENTE_NOME" ) );
			dt.getTurma().getDisciplina().setUnidade(new Unidade((Integer) map.get( "UNIDADE_COMPONENTE_ID" )));
			dt.getTurma().getDisciplina().getUnidade().setNome((String) map.get( "UNIDADE_COMPONENTE_NOME" ));
			dt.getTurma().getDisciplina().getUnidade().setUnidadeResponsavel(new Unidade((Integer) map.get( "UNIDADE_RESPONSAVEL_ID" )));
			dt.getTurma().getDisciplina().getUnidade().getUnidadeResponsavel().setNome((String) map.get( "UNIDADE_RESPONSAVEL_NOME" ));
			dt.getTurma().setPolo(new Polo());
			dt.getTurma().getPolo().setCidade(new Municipio((Integer) map.get( "CIDADE_ID" )));
			dt.getTurma().getPolo().getCidade().setNome((String) map.get( "CIDADE_NOME" ));
			dt.getTurma().getPolo().getCidade().setUnidadeFederativa( new UnidadeFederativa() );
			dt.getTurma().getPolo().getCidade().getUnidadeFederativa().setSigla((String) map.get( "UF" ));
			
			resultado.add(dt);
		}
		
		return resultado;
	}	
	
}
