/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/04/2007
 *
 */

package br.ufrn.sigaa.arq.dao.ensino;


import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.EditalProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.StatusInscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.StatusProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.vestibular.dominio.AreaConhecimentoVestibular;

/**
 * DAO para efetuar consultas de processos seletivos
 *
 * @author Leonardo
 *
 */
public class ProcessoSeletivoDao extends GenericSigaaDAO {

	
	/**
	 * Retorna uma lista de processos seletivos de um determinado Curso.
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivo> findByCursoLato(int idCurso) throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append(" from ProcessoSeletivo ps ");
		hql.append(" where ps.curso.id = :idCurso ");
		hql.append(" and ativo = trueValue() ");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idCurso", idCurso);

		return q.list();
	}
	
	/**
	 * Retorna uma lista de processos seletivos de um edital
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivo> findByEdital(int idEdital) throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append(" from ProcessoSeletivo ps ");
		hql.append(" where ps.editalProcessoSeletivo.id = :idEdital");
		hql.append(" and ativo = trueValue() ");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idEdital", idEdital);

		return q.list();
	}

	/**
	 * Busca todos os processo seletivos cadastrados
	 * para os cursos da unidade informada, sem considerar o nível de ensino.
	 *
	 * @param unidade
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public Collection<ProcessoSeletivo> findByUnidadeCurso(Unidade unidade, Curso curso) throws HibernateException, DAOException {
		return findByUnidadeCurso(unidade, curso, null);
	}
	
	/**
	 * Busca todos os processo seletivos cadastrados
	 * para os cursos da unidade e nível informados
	 *
	 * @param unidade
	 * @throws DAOException
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivo> findByUnidadeCurso(Unidade unidade, Curso curso, Character nivel) throws HibernateException, DAOException {
		
		if(!isEmpty(unidade) || !isEmpty(curso)){
			
			StringBuilder hql = new StringBuilder();
			
			hql.append(" FROM ProcessoSeletivo ps ");
			hql.append(" WHERE 1 = 1 ");
			
			if( !isEmpty(unidade) )
				hql.append(" AND ps.curso.unidade.id = :idUnidade ");
			if( !isEmpty(curso) )
				hql.append(" AND ps.curso.id = :idCurso ");
			if( !isEmpty(nivel) )
				hql.append(" AND ps.curso.nivel = :nivel ");
			
			hql.append(" AND ativo = trueValue() ");
			hql.append(" ORDER BY ps.editalProcessoSeletivo.inicioInscricoes DESC, ps.editalProcessoSeletivo.id, ps.curso.nome ASC");

			Query q = getSession().createQuery(hql.toString());
			
			if( !isEmpty(unidade) )
				q.setInteger("idUnidade", unidade.getId());
			if( !isEmpty(curso) )
				q.setInteger("idCurso", curso.getId());
			if( !isEmpty(nivel) )
				q.setCharacter("nivel", nivel.charValue());
			
			return q.list();
			
		}else 
			return null;
		
	}
	

	/**
	 * Retorna todos os processos seletivos abertos
	 * do nível de ensino passado como argumento
	 * 
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivo> findAllAbertos(char nivel) throws DAOException {
		StringBuilder hql = new StringBuilder();
		Query q = getConsultaAbertos(nivel, hql, true);
		return  q.list();
	}
	
	/**
	 * Método que retorna uma query das transferência voluntárias referente ao integrador
	 * EditalTransferenciaVoluntária
	 * 
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivo>  findAllByEdital(EditalProcessoSeletivo edital) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT ps FROM ProcessoSeletivo ps JOIN FETCH ps.curso ");
		hql.append(" WHERE ps.editalProcessoSeletivo.id = :edital AND ps.ativo = trueValue()");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("edital", edital.getId());
		return q.list();
	}
	
	/**
	 * Método que retorna uma query dos processo seletivos relacionado a um nível de ensino
	 * 
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivo>  findByNivel(char nivel) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT  c.nome, c.nivel , c.municipio.nome, ");
		
		if (nivel == NivelEnsino.GRADUACAO)
			hql.append(" h.nome, t.sigla, g.descricao,");
		
		hql.append(" a.descricao ,ps.vaga, eps.inicioInscricoes, eps.fimInscricoes, ps.id,eps.id, eps.nome, ps.ativo, eps.status, eps.possuiAgendamento, eps.taxaInscricao ");
		hql.append(" FROM ProcessoSeletivo ps INNER JOIN ps.editalProcessoSeletivo eps ");
		
		if (nivel == NivelEnsino.GRADUACAO){
			
			hql.append(" INNER JOIN ps.matrizCurricular m LEFT JOIN m.turno t LEFT JOIN m.grauAcademico g");
			hql.append(" LEFT JOIN m.habilitacao h LEFT JOIN m.curso c ");
		}else if(NivelEnsino.getDescricao(nivel)!="DESCONHECIDO")	
			hql.append(" INNER JOIN ps.curso c ");	
		
		hql.append( " LEFT JOIN c.areaVestibular a  WHERE 1=1 ");
		
		if (nivel == NivelEnsino.STRICTO){
			hql.append(" AND c.nivel IN ('" + NivelEnsino.MESTRADO + "','" + NivelEnsino.DOUTORADO + "')" );
			hql.append(" AND eps.status = " + StatusProcessoSeletivo.PUBLICADO ); 
		}else if(NivelEnsino.getDescricao(nivel)!="DESCONHECIDO")	
			hql.append(" AND c.nivel = '"+nivel+"'");
		
		hql.append(" AND ps.ativo = trueValue() ");
		
		if (nivel == NivelEnsino.GRADUACAO){
			hql.append(" ORDER BY eps.id DESC, eps.criadoEm DESC, a.descricao, c.nome,  c.nivel ");
		}else if(NivelEnsino.getDescricao(nivel)!="DESCONHECIDO")
			hql.append(" ORDER BY eps.id DESC,c.nome,  c.nivel ");		

		Query q = getSession().createQuery(hql.toString());
		List<Object[]> lista = q.list();

		Set<ProcessoSeletivo> result = new LinkedHashSet<ProcessoSeletivo>();
		
		for (int a = 0; a < lista.size(); a++) {
			ProcessoSeletivo p = new ProcessoSeletivo();
			int col = 0;
			Object[] colunas = lista.get(a);
			
			if (nivel == NivelEnsino.GRADUACAO){
				p.setMatrizCurricular(new MatrizCurricular());
				p.setCurso(null);
				p.getMatrizCurricular().setCurso(new Curso());
				p.getMatrizCurricular().getCurso().setNome((String) colunas[col++]);
				p.getMatrizCurricular().getCurso().setNivel((Character) colunas[col++]);
				p.getMatrizCurricular().getCurso().setMunicipio(new Municipio());
				p.getMatrizCurricular().getCurso().getMunicipio().setNome((String) colunas[col++]);
				
				p.getMatrizCurricular().setHabilitacao(new Habilitacao());
				p.getMatrizCurricular().getHabilitacao().setNome((String) colunas[col++]);
				
				p.getMatrizCurricular().setTurno(new Turno());
				p.getMatrizCurricular().getTurno().setSigla((String) colunas[col++]);
				
				p.getMatrizCurricular().setGrauAcademico(new GrauAcademico());
				p.getMatrizCurricular().getGrauAcademico().setDescricao((String) colunas[col++]);
				
				p.getMatrizCurricular().getCurso().setAreaVestibular(new AreaConhecimentoVestibular());
				p.getMatrizCurricular().getCurso().getAreaVestibular().setDescricao((String) colunas[col++]);
								
			}else {
				p.setMatrizCurricular(null);
				p.setCurso(new Curso());
				p.getCurso().setNome((String) colunas[col++]);
				p.getCurso().setNivel((Character) colunas[col++]);
				p.getCurso().setMunicipio(new Municipio());
				p.getCurso().getMunicipio().setNome((String) colunas[col++]);
				p.getCurso().setAreaVestibular(new AreaConhecimentoVestibular());
				p.getCurso().getAreaVestibular().setDescricao((String) colunas[col++]);
			}
	
			p.setVaga((Integer) colunas[col++]);
			p.setEditalProcessoSeletivo(new EditalProcessoSeletivo());
			p.getEditalProcessoSeletivo().setInicioInscricoes((Date) colunas[col++]);
			p.getEditalProcessoSeletivo().setFimInscricoes((Date) colunas[col++]);
			p.setId((Integer) colunas[col++]);
			p.getEditalProcessoSeletivo().setId((Integer) colunas[col++]);
			p.getEditalProcessoSeletivo().setNome((String) colunas[col++]);
			p.setAtivo((Boolean) colunas[col++]);
			p.getEditalProcessoSeletivo().setStatus((Integer) colunas[col++]);
			p.getEditalProcessoSeletivo().setPossuiAgendamento((Boolean) colunas[col++]);
			p.getEditalProcessoSeletivo().setTaxaInscricao((Double) colunas[col++]);
			result.add(p);
		}

		return result;
	}

	/**
	 * Método auxiliar para construir uma Query de ProcessoSeletivo.
	 * 
	 * @param nivel
	 * @param hql
	 * @return
	 * @throws DAOException
	 */
	private Query getConsultaAbertos(char nivel, StringBuilder hql, boolean order) throws DAOException {
		hql.append(" from ProcessoSeletivo ps ");

		if (nivel == NivelEnsino.STRICTO) {
			hql.append(" where ps.curso.nivel in ('" + NivelEnsino.MESTRADO + "','" + NivelEnsino.DOUTORADO + "')" );
		} else if (nivel == NivelEnsino.GRADUACAO) {
			hql.append(" where ps.matrizCurricular.curso.nivel = '" + nivel + "'");
		}else {	
			hql.append(" where ps.curso.nivel = '" + nivel + "'");
		}

		hql.append(" and ps.editalProcessoSeletivo.inicioInscricoes <= :dataAtual ");
		hql.append(" and ps.editalProcessoSeletivo.fimInscricoes >= :dataAtual ");
		hql.append(" and ativo = trueValue() ");
		if(order){
			if (nivel == NivelEnsino.GRADUACAO)
				hql.append(" order by ps.editalProcessoSeletivo.fimInscricoes desc, ps.matrizCurricular.curso.nome ");
			else
				hql.append(" order by ps.editalProcessoSeletivo.fimInscricoes desc, ps.curso.nome ");
		}
		Query q = getSession().createQuery(hql.toString());
		q.setDate("dataAtual", new java.util.Date());
		return q;
	}
	
	/**  
	 * Retorna todos os processos seletivos de uma unidade, nível de ensino, podendo ainda
	 * setar somente um status do processo seletivo.
	 * @param nivel
	 * @param diasPassados
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProcessoSeletivo> findAllVisiveis(char nivel, int diasPassados, Integer status) throws DAOException {
		if(!ValidatorUtil.isEmpty(status))
			return  findAllVisiveis(null, nivel, diasPassados, status);
		else
			return  findAllVisiveis(null, nivel, diasPassados);
	}
	
	
	/**
	 * Retorna todos os processos seletivos de uma unidade, nível de ensino, podendo ainda
	 * setar um ou mais status do processo seletivo. 
	 * do processo seletivo.
	 * 
	 * @param nivel
	 * @param diasPassados
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProcessoSeletivo> findAllVisiveis(Integer idUnidade, char nivel, int diasPassados, Integer... status) 
		throws DAOException {
		return findGeral(idUnidade, nivel, null, diasPassados, status);
	}
	
	/**
	 * Retorna todos os processo seletivos de acordo com a unidade.
	 * @param idUnidade
	 * @param nivel
	 * @param ano
	 * @param diasPassados
	 * @param status
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ProcessoSeletivo> findGeral(Integer idUnidade, char nivel, Integer ano, 
			int diasPassados, Integer... status) throws HibernateException, DAOException{
		return findGeral(idUnidade, null, nivel, ano, diasPassados, status);
	}
	
	/**
	 * Retorna todos os processo seletivos de acordo com curso.
	 * @param curso
	 * @param ano
	 * @param diasPassados
	 * @param status
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ProcessoSeletivo> findGeral(Curso curso, Integer ano, 
			int diasPassados, Integer... status) throws HibernateException, DAOException{
		return findGeral(null, curso.getId(), curso.getNivel(), ano, diasPassados, status);
	}
	
	/**
	 * Retorna todos os processos seletivos conforme os parâmetros setados
	 * @param idUnidade
	 * @param nivel
	 * @param ano
	 * @param diasPassados
	 * @param status
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivo> findGeral(Integer idUnidade, Integer idCurso, char nivel, Integer ano, 
			int diasPassados, Integer... status) 
			throws HibernateException, DAOException{
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT  c.nome, c.nivel, c.modalidadeEducacao.id, c.modalidadeEducacao.descricao, ");
		
		if (nivel == NivelEnsino.GRADUACAO)
			hql.append(" c.municipio.nome, h.nome, t.sigla, g.descricao, a.descricao ,");
		
		hql.append(" ps.vaga, eps.inicioInscricoes, eps.fimInscricoes, eps.taxaInscricao, ");
		hql.append(" ps.id, eps.status, eps.id, eps.nome, u.id FROM ProcessoSeletivo ps INNER JOIN ps.editalProcessoSeletivo eps ");
		
		if (nivel == NivelEnsino.GRADUACAO){
			hql.append(" INNER JOIN ps.matrizCurricular m LEFT JOIN m.turno t LEFT JOIN m.grauAcademico g");
			hql.append(" LEFT JOIN m.habilitacao h LEFT JOIN m.curso c LEFT JOIN c.areaVestibular a  ");
		}else 	
			hql.append(" INNER JOIN ps.curso c ");	
		
		hql.append( " INNER JOIN c.unidade u WHERE 1=1 ");
		
		if (nivel == NivelEnsino.STRICTO)
			hql.append(" AND c.nivel IN " + UFRNUtils.gerarStringIn(NivelEnsino.getNiveisStricto()));
		else if(NivelEnsino.getDescricao(nivel)!="DESCONHECIDO")	
			hql.append(" AND c.nivel = '"+nivel+"'");
		
		if(diasPassados >= 0){
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new java.util.Date());
			cal.add(Calendar.DAY_OF_MONTH, -diasPassados);
			java.util.Date fim = cal.getTime();	
			
			hql.append(" AND ( eps.fimInscricoes >= '" + fim + "' )");
		}
		
		if(!ValidatorUtil.isEmpty(ano))
			hql.append(" AND ( year(eps.inicioInscricoes) = " + ano +
						" OR  year(eps.fimInscricoes) = " + ano + " )");
		
		hql.append(" AND ps.ativo = trueValue() ");
		
		if (!ValidatorUtil.isEmpty(status))
			hql.append(" AND eps.status in " + gerarStringIn(status));	

		if (!ValidatorUtil.isEmpty(idUnidade))
			hql.append(" AND u.id = " + idUnidade);
		
		if (!ValidatorUtil.isEmpty(idCurso))
			hql.append(" AND c.id = " + idCurso);
		
		if (nivel == NivelEnsino.GRADUACAO){
			hql.append(" ORDER BY eps.inicioInscricoes DESC, a.descricao, c.nome,  c.nivel ");
		}else if(NivelEnsino.getDescricao(nivel)!="DESCONHECIDO")
			hql.append(" ORDER BY eps.inicioInscricoes DESC, eps.nome, c.nome, c.nivel ");		
	
		Query q = getSession().createQuery(hql.toString());
		
		List<Object[]> lista = q.list();

		ArrayList<ProcessoSeletivo> result = new ArrayList<ProcessoSeletivo>();
		
		for (int a = 0; a < lista.size(); a++) {
			ProcessoSeletivo p = new ProcessoSeletivo();
			int col = 0;
			Object[] colunas = lista.get(a);
			
			if (nivel == NivelEnsino.GRADUACAO){
				p.setMatrizCurricular(new MatrizCurricular());
				
				
				p.setCurso(null);
				p.getMatrizCurricular().setCurso(new Curso());
				p.getMatrizCurricular().getCurso().setNome((String) colunas[col++]);
				
				p.getMatrizCurricular().getCurso().setNivel((Character) colunas[col++]);
				
				p.getMatrizCurricular().getCurso().setModalidadeEducacao(new ModalidadeEducacao((Integer) colunas[col++], (String) colunas[col++]));
				
				p.getMatrizCurricular().getCurso().setMunicipio(new Municipio());
				p.getMatrizCurricular().getCurso().getMunicipio().setNome((String) colunas[col++]);
				
				p.getMatrizCurricular().setHabilitacao(new Habilitacao());
				p.getMatrizCurricular().getHabilitacao().setNome((String) colunas[col++]);
				
				p.getMatrizCurricular().setTurno(new Turno());
				p.getMatrizCurricular().getTurno().setSigla((String) colunas[col++]);
				
				p.getMatrizCurricular().setGrauAcademico(new GrauAcademico());
				p.getMatrizCurricular().getGrauAcademico().setDescricao((String) colunas[col++]);
				p.getMatrizCurricular().getCurso().setAreaVestibular(new AreaConhecimentoVestibular());
				p.getMatrizCurricular().getCurso().getAreaVestibular().setDescricao((String) colunas[col++]);
			
			}else {
				p.setMatrizCurricular(null);
				p.setCurso(new Curso());
				p.getCurso().setNome((String) colunas[col++]);
				p.getCurso().setNivel((Character) colunas[col++]);
				p.getCurso().setModalidadeEducacao(new ModalidadeEducacao((Integer) colunas[col++], (String) colunas[col++]));
			}
	
			p.setVaga((Integer) colunas[col++]);
			p.setEditalProcessoSeletivo(new EditalProcessoSeletivo());
			p.getEditalProcessoSeletivo().setInicioInscricoes((Date) colunas[col++]);
			p.getEditalProcessoSeletivo().setFimInscricoes((Date) colunas[col++]);
			p.getEditalProcessoSeletivo().setTaxaInscricao((Double) colunas[col++]);
			p.setId((Integer) colunas[col++]);
			p.getEditalProcessoSeletivo().setStatus((Integer) colunas[col++]);	
			p.getEditalProcessoSeletivo().setId((Integer) colunas[col++]);
			p.getEditalProcessoSeletivo().setNome((String) colunas[col++]);
			
			
			Unidade u = new Unidade((Integer) colunas[col++]);
			
			if (nivel == NivelEnsino.GRADUACAO)
				p.getMatrizCurricular().getCurso().setUnidade(u);
			else
				p.getCurso().setUnidade(u);
			
			result.add(p);
		}

		return result;
		
		
	}
	
	/**
	 * Retorna a lista de Todos os Processos Seletivos publicados com a quantidade de vagas disponível e o total de inscritos.
	 *  
	 * @param status
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaDemandaVagas(int idUnidade, Character nivel, java.util.Date dataInicio, java.util.Date dataFim)	throws DAOException {
		
		String sql = 
 		 "	select c.nome, c.nivel, eps.inicio_inscricoes, eps.fim_inscricoes, p.vagas, count(i.id) as totalInscritos "
		+"	from ensino.processo_seletivo p "
		+"	join ensino.edital_processo_seletivo eps using (id_edital_processo_seletivo) "
		+"	join public.curso c using (id_curso) "
		+"	join comum.unidade u using (id_unidade) "
		+"	left join ensino.inscricao_selecao i on (i.id_processo_seletivo = p.id_processo_seletivo) and i.status <> "+StatusInscricaoSelecao.CANCELADA
		+"	where p.ativo = trueValue() "
		+(idUnidade > 0 ? " and u.id_unidade = "+idUnidade : "" )
		+"  and eps.status = "+StatusProcessoSeletivo.PUBLICADO;
		
		if (nivel != null && NivelEnsino.isValido(nivel))
			sql += " and c.nivel = '"+nivel+"'";
		else {
			Collection<Character> niveis = NivelEnsino.getNiveisStricto();
			niveis.add( NivelEnsino.LATO);			
			sql += " and c.nivel in "+UFRNUtils.gerarStringIn(niveis);
		}
		
		if (dataInicio != null)
			sql += " and eps.inicio_inscricoes >= :dataInicio ";

		if (dataFim != null)
			sql += " and eps.fim_inscricoes <= :dataFim ";
		
		sql += " group by c.nome, c.nivel, eps.inicio_inscricoes, eps.fim_inscricoes, p.vagas "
		+"  order by eps.inicio_inscricoes desc, eps.fim_inscricoes desc, c.nome, p.vagas desc ";
		
		Query q = getSession().createSQLQuery(sql);
		
		if (dataInicio != null)
			q.setDate("dataInicio", dataInicio);

		if (dataFim != null)
			q.setDate("dataFim", dataFim); 
		
		// consulta o resultado
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
				
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("curso", obj[0]);
			linha.put("nivel", NivelEnsino.getDescricao(String.valueOf(obj[1]).toCharArray()[0]) );			
			linha.put("inicioInscricoes", obj[2]);
			linha.put("fimInscricoes", obj[3]);
			if (obj[4] != null){
				linha.put("vagas", obj[4]);
			} else
				linha.put("vagas", 0);
			
			if (obj[4] != null && (Integer) obj[4] > 0){
				Float demanda = Float.valueOf( String.valueOf(obj[5]) ) / Float.valueOf( String.valueOf(obj[4]) );
				linha.put("demanda", demanda);				
			} else {
				linha.put("demanda", 0);
			}
			
			linha.put("totalInscritos", obj[5]);
			resultado.add(linha);
		}
		
		return resultado;					
	}
	
}

