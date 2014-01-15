/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 22/09/2008
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao para realizar consultas sobre a entidade CoordenacaoCurso
 * @author Andre M Dantas
 * @author Victor Hugo
 */
public class CoordenacaoCursoDao extends GenericSigaaDAO {

	public CoordenacaoCursoDao() {
	}

	/**
	 * Retorna as coordenações ativas de curso existentes entre o servidor informado e o curso ou unidade informado.
	 *
	 * @param servidorId
	 * @param unidadeId
	 * @param nivel
	 * @param paging
	 * @return lista de CoordenacaoCurso
	 * @throws DAOException
	 */
	public Collection<CoordenacaoCurso> findByServidor(Integer servidorId, Integer unidadeId, Character nivel,
			PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from CoordenacaoCurso where ativo = trueValue()");
			hql.append(" and dataInicioMandato <= :hoje");
			hql.append(" and dataFimMandato >= :hoje");
			if ( !isEmpty(unidadeId) )
				hql.append("and curso.unidade.id = " + unidadeId);
			if (nivel != 0)
				hql.append(" and curso.nivel = '" + nivel+"' ");
			if ( !isEmpty(servidorId) )
				hql.append(" and servidor.id = " + servidorId);

			Query q = getSession().createQuery(hql.toString());
			q.setDate("hoje", new Date());
			preparePaging(paging, q);

			@SuppressWarnings("unchecked")
			List<CoordenacaoCurso> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * Retorna os níveis e tipos acadêmicos que o servidor é coordenador.
	 * Para o nível lato sensu, retorna somente os coordenadores de curso com a condição ACEITA,
	 *  e o período de mandato não se aplica.
	 *
	 * @param servidorId
	 * @param unidadeId
	 * @param nivel
	 * @param paging
	 * @return lista de CoordenacaoCurso
	 * @throws DAOException
	 */
	public Collection findNiveisCoordenadosServidor(int servidorId) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" SELECT c.nivel, u.tipoAcademica FROM ");
			hql.append(" CoordenacaoCurso cc LEFT JOIN cc.curso c ");
			hql.append(" LEFT JOIN cc.unidade u INNER JOIN cc.cargoAcademico ca ");
			hql.append(" WHERE cc.ativo = " + SQLDialect.TRUE ) ;
			
			hql.append(" AND ( ( SELECT count(cl.id) FROM CursoLato cl INNER JOIN cl.propostaCurso pc ");
			hql.append("		WHERE pc.situacaoProposta = " + SituacaoProposta.ACEITA );
			hql.append(" 	 	AND cl.id = c.id  ) > 0 OR c.nivel <> '" + NivelEnsino.LATO + "' OR u.id IS NOT NULL ) ");
			hql.append(" AND ( cc.dataInicioMandato <= :hoje AND cc.dataFimMandato >= :hoje ) " );
			hql.append(" AND cc.servidor.id = " + servidorId );
			hql.append(" AND ca.id IN (" + CargoAcademico.COORDENACAO + "," + CargoAcademico.VICE_COORDENACAO + " ) " );

			Query q = getSession().createQuery(hql.toString());
			q.setDate("hoje", new Date());

			@SuppressWarnings("unchecked")
			List<Character> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna a lista de coordenações de curso do curso/unidade informado.
	 *
	 * @param cursoId
	 * @param unidadeId
	 * @param nivel
	 * @param paging
	 * @return lista de CoordenacaoCurso
	 * @throws DAOException
	 */
	public Collection<CoordenacaoCurso> findByCurso(int cursoId, int unidadeId, char nivel, PagingInformation paging) throws DAOException {
		return findByCurso(cursoId, unidadeId, nivel, paging, new Integer[]{});
	}

	/**
	 * Retorna a lista de coordenações de curso do curso/unidade informado.
	 * @param cursoId
	 * @param unidadeId
	 * @param nivel
	 * @param paging
	 * @param idCargoAcademico
	 * @return
	 * @throws DAOException
	 */
	public Collection<CoordenacaoCurso> findByCurso(
			int cursoId, int unidadeId, char nivel, PagingInformation paging, Integer... idCargoAcademico)	throws DAOException {

		try {
			Map<String, Object> parametros = new HashMap<String, Object>();

			String hql = " FROM CoordenacaoCurso WHERE ativo = trueValue() ";

			if (unidadeId > 0){
				hql += " AND curso.unidade.id = :idUnidade ";
				parametros.put("idUnidade", unidadeId);
			}

			if (nivel != 0){
				hql += " AND curso.nivel  = :nivel ";
				parametros.put("nivel", nivel);
			}

			if (cursoId > 0){
				hql += " AND curso.id = :idCurso ";
				parametros.put("idCurso", cursoId);
			}

			if (!isEmpty(idCargoAcademico)){
				hql += " AND cargoAcademico.id IN (:cargoAcademico) ";
				parametros.put("cargoAcademico", idCargoAcademico);
			}

			hql += " ORDER BY dataFimMandato DESC ";

			Query q = getSession().createQuery(hql);
			q.setProperties(parametros);

			preparePaging(paging, q);

			@SuppressWarnings("unchecked")
			List<CoordenacaoCurso> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna os coordenadores de cursos de especialização. 
	 *
	 * @param ativo caso true retorna apenas os ativos
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<CoordenacaoCurso> findAllLatoSensu(boolean ativo, PagingInformation paging) throws DAOException {
		return findListaCoordenadores(null, NivelEnsino.LATO, ativo, paging);
	}
	
	/**
	 * Retorna uma lista de coordenadores de cursos com base nos critérios informados. 
	 *
	 * @param idUnidade
	 * @param ativo caso true retorna apenas os ativos
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<CoordenacaoCurso> findListaCoordenadores(Integer idUnidade, Character nivel, boolean ativo, PagingInformation paging) throws DAOException {
		try {
			Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String hj = df.format(hoje);
StringBuffer hql = new StringBuffer();
			
			hql.append(" SELECT cc.id, cc.telefoneContato1, cc.telefoneContato2, cc.emailContato, cc.servidor.id, cc.servidor.siape, ");
			hql.append(" cc.servidor.pessoa.id, cc.servidor.pessoa.nome, cc.servidor.pessoa.telefone, cc.servidor.pessoa.celular, ");
			hql.append(" cc.curso.unidade.id, cc.curso.unidade.nome,cc.curso.unidade.sigla, cc.cargoAcademico.id, cc.cargoAcademico.descricao, ");
			hql.append(" u.id, u.login, u.pessoa.id, cc.dataInicioMandato, cc.dataFimMandato, c.id, c.nome, c.nivel, m.nome, mod.id, mod.descricao ");
			
			hql.append(" FROM  Usuario u, CoordenacaoCurso cc, Servidor s RIGHT JOIN u.pessoa p ");
			hql.append(" 	LEFT JOIN cc.curso c INNER JOIN c.modalidadeEducacao mod LEFT JOIN c.municipio m  ");
			hql.append(" WHERE cc.servidor.id = s.id AND cc.ativo = trueValue() AND u.inativo = falseValue() ");
			hql.append(" 	AND (cc.servidor.pessoa.id = u.pessoa.id or s.pessoa.id = u.pessoa.id ) ");
			
					
			if(idUnidade != null)
				hql.append(" AND cc.curso.unidade.id = " + idUnidade);
			hql.append(" AND cc.curso.nivel = '" + nivel +"'");
			
			if (ativo)
				hql.append(" AND cc.ativo = trueValue() AND cc.dataInicioMandato <= '" + hj + "' AND (cc.dataFimMandato = null OR cc.dataFimMandato >= '" + hj + "') ");
			
			hql.append(" ORDER BY cc.curso.unidade.nome, c.nome, m.nome, cc.cargoAcademico.descricao, cc.servidor.pessoa.nome ");
			
			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			Collection<CoordenacaoCurso> coordenadores = new ArrayList<CoordenacaoCurso>();
			for (Object[] obj : lista) {
				
				CoordenacaoCurso cc = new CoordenacaoCurso((Integer) obj[0]);
				cc.setTelefoneContato1((String) obj[1]);
				cc.setTelefoneContato2((String) obj[2]);
				cc.setEmailContato((String) obj[3]);
				
				cc.setServidor(new Servidor((Integer) obj[4], (String) obj[7], (Integer) obj[5]));
				cc.getServidor().setPessoa(new Pessoa((Integer) obj[6], (String) obj[7]));
				cc.getServidor().getPessoa().setTelefone((String) obj[8]);
				cc.getServidor().getPessoa().setCelular((String) obj[9]);
				
				cc.setCargoAcademico(new CargoAcademico((Integer) obj[13], (String) obj[14]));
				cc.setDataInicioMandato((Date) obj[18]);
				cc.setDataFimMandato((Date) obj[19]);
				
				//Popula os dados referentes ao curso
				Integer idCurso = (Integer) obj[20];
				if(idCurso != null){
					
					cc.setCurso(new Curso(idCurso));
					cc.getCurso().setNome((String) obj[21]);
					cc.getCurso().setUnidade(new Unidade((Integer)  obj[10], null, (String) obj[11], (String) obj[12]));
					cc.getCurso().setNivel((Character) obj[22]);
					
					String municipio = (String) obj[23];
					if(municipio != null)
						cc.getCurso().setMunicipio(new Municipio(municipio));
					
				}
				
				Usuario primeiroUsuario = new Usuario((Integer) obj[15], (String) obj[7], (String) obj[16]);
				Integer idPessoaPrimeiroUsuario = (obj[17] == null) ? null : (Integer) obj[17];
				
				ModalidadeEducacao mod = new ModalidadeEducacao((Integer) obj[24], (String) obj[25]);
				cc.getCurso().setModalidadeEducacao(mod);
				
				if (idPessoaPrimeiroUsuario != null && 
						cc.getServidor().getPessoa().getId() == idPessoaPrimeiroUsuario.intValue()) {
					cc.getServidor().setPrimeiroUsuario(primeiroUsuario);
				}
				
				if(!coordenadores.contains(cc))
					coordenadores.add(cc);
				
			}
			return coordenadores;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna os coordenadores de cursos técnicos de uma determinada unidade.
	 *
	 * @param idUnidade
	 * @param ativo caso true retorna apenas os ativos
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<CoordenacaoCurso> findAllTecnico(Integer idUnidade, boolean ativo,PagingInformation paging) throws DAOException {
		return findListaCoordenadores(idUnidade, NivelEnsino.TECNICO, ativo, paging);
	}
	
	
	/**
	 * Retorna os coordenadores de um programa de pós graduação 
	 * ou residência em saúde especificado.
	 * @param idPrograma
	 * @param tipo
	 * @param ativo
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<CoordenacaoCurso> findByPrograma(Integer idPrograma, int tipo, boolean ativo, PagingInformation paging) throws DAOException{
		List<Integer> idsPrograma = null;
		if( !isEmpty(idPrograma) ){
			idsPrograma = new ArrayList<Integer>();
			idsPrograma.add(idPrograma);
		}	
		return findByProgramas(idsPrograma, tipo, ativo, paging);
	}
	
	/**
	 * Retorna os coordenadores dos programas de pós graduação 
	 * ou residência em saúde.
	 *
	 * @param idPrograma se for nulo retorna de todos os programas
	 * @param tipo TipoUnidadeAcademica.PROGRAMA_POS ou TipoUnidadeAcademica.PROGRAMA_RESIDENCIA
	 * @param ativo caso true retorna apenas os ativos
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CoordenacaoCurso> findByProgramas(List<Integer> idsPrograma, int tipo, boolean ativo, PagingInformation paging) throws DAOException {
		
			StringBuilder sql = new StringBuilder();
			List<Object> param = new ArrayList<Object>();
			
			sql.append(" SELECT cc.id_coordenacao_curso,cc.telefone_contato1,cc.telefone_contato2,cc.email_contato, s.id_servidor, s.siape, p.id_pessoa, ");
			sql.append(" p.nome as nome_pessoa,p.telefone_fixo, p.telefone_celular,u.id_unidade, u.nome as nome_unidade, u.sigla,  ");
			sql.append(" r.id_unidade as id_unidade_responsavel, r.nome as unidade_responsavel, ca.id_cargo_academico, ca.descricao as descricao_cargo, ");
			sql.append(" us.id_usuario, us.login, us.id_pessoa as id_pessoa_usuario, cc.data_inicio_mandato, cc.data_fim_mandato, ");
			sql.append(" c.id_curso,c.nome as nome_curso, c.nivel,m.nome as nome_municipio,cc.ramal_telefone1,cc.ramal_telefone2 ");
			
			sql.append(" FROM  comum.usuario us RIGHT JOIN comum.pessoa pus ON (us.id_pessoa = pus.id_pessoa) ");
			sql.append(" 	INNER JOIN rh.servidor s ON (s.id_pessoa = pus.id_pessoa) ");
			sql.append(" 	INNER JOIN comum.pessoa p ON (p.id_pessoa = pus.id_pessoa OR p.id_pessoa = s.id_pessoa) ");
			sql.append(" 	INNER JOIN ensino.coordenacao_curso cc ON (cc.id_servidor = s.id_servidor) ");
			sql.append(" 	INNER JOIN comum.unidade u ON (u.id_unidade = cc.id_unidade) ");
			sql.append(" 	INNER JOIN comum.unidade r ON (r.id_unidade = u.unidade_responsavel) ");
			sql.append(" 	INNER JOIN ensino.cargo_academico ca ON (ca.id_cargo_academico = cc.id_cargo_academico) ");
			sql.append(" 	LEFT JOIN curso c on (cc.id_curso = c.id_curso ) ");
			sql.append(" 	LEFT JOIN comum.municipio m on c.id_municipio = m.id_municipio "); 
			sql.append(" WHERE u.tipo_academica = ? AND us.inativo = falseValue() "); 
			
			param.add( tipo );
			
			if( !isEmpty(idsPrograma) ){
				sql.append(" AND u.id_unidade IN " + UFRNUtils.gerarStringIn(idsPrograma));
			}
			
			if (ativo){
				Date dataAtual = CalendarUtils.descartarHoras(new Date());
				sql.append(" AND cc.ativo = trueValue() AND cc.data_inicio_mandato <= ? AND (cc.data_fim_mandato = null OR cc.data_fim_mandato >= ? )");
				param.add( dataAtual );
				param.add( dataAtual );
			}	
			
			sql.append(" ORDER BY u.nome, ca.descricao, p.nome  ");
			
			Collection<CoordenacaoCurso> coordenadores = new ArrayList<CoordenacaoCurso>();
			
			Collection lista = getJdbcTemplate().queryForList(sql.toString(), param.toArray());
			Iterator it = lista.iterator();
			
			while(it.hasNext()){
				
				Map<String, Object> mapa = (Map) it.next();
							
				CoordenacaoCurso cc = new CoordenacaoCurso((Integer) mapa.get("id_coordenacao_curso") );
				cc.setTelefoneContato1((String) mapa.get("telefone_contato1"));
				cc.setTelefoneContato2((String) mapa.get("telefone_contato2"));
				cc.setEmailContato((String) mapa.get("email_contato"));
					
				cc.setServidor(new Servidor((Integer) mapa.get("id_servidor")));
				cc.getServidor().setPessoa(new Pessoa((Integer) mapa.get("id_pessoa"), (String) mapa.get("nome_pessoa")));
				cc.getServidor().getPessoa().setTelefone((String) mapa.get("telefone_fixo"));
				cc.getServidor().getPessoa().setCelular((String) mapa.get("telefone_celular"));
				
				cc.setUnidade(new Unidade((Integer) mapa.get("id_unidade"), null,(String)  mapa.get("nome_unidade"),(String)  mapa.get("sigla")));
				cc.getUnidade().setUnidadeResponsavel(new Unidade((Integer) mapa.get("id_unidade_responsavel"), null, (String) mapa.get("unidade_responsavel"), null));
				cc.setCargoAcademico(new CargoAcademico((Integer) mapa.get("id_cargo_academico"), (String) mapa.get("descricao_cargo")));
				cc.setDataInicioMandato((Date) mapa.get("data_inicio_mandato"));
				cc.setDataFimMandato((Date) mapa.get("data_fim_mandato"));
					
				//Popula os dados referentes ao curso
				Integer idCurso = (Integer) mapa.get("id_curso");
				if(idCurso != null){
					
					cc.setCurso(new Curso(idCurso));
					cc.getCurso().setNome((String) mapa.get("nome_curso"));
						cc.getCurso().setUnidade(cc.getUnidade());
					
					if( mapa.get("nivel") != null )
						cc.getCurso().setNivel((Character) mapa.get("nivel"));
					
					String municipio = (String) mapa.get("nome_municipio");
					if(municipio != null)
						cc.getCurso().setMunicipio(new Municipio(municipio));
				
				}
					
				cc.setRamalTelefone1((String) mapa.get("ramal_telefone1"));
				cc.setRamalTelefone2((String) mapa.get("ramal_telefone2"));
				
				Usuario primeiroUsuario = new Usuario((Integer) mapa.get("id_usuario"), (String) mapa.get("nome_pessoa"), (String) mapa.get("login"));
				Integer idPessoaPrimeiroUsuario = ((Integer) mapa.get("id_pessoa_usuario") == 0 ) ? null : (Integer)mapa.get("id_pessoa_usuario");
								
				if (idPessoaPrimeiroUsuario != null && 
						cc.getServidor().getPessoa().getId() == idPessoaPrimeiroUsuario.intValue()) {
					cc.getServidor().setPrimeiroUsuario(primeiroUsuario);
				}
				
				if(!coordenadores.contains(cc))
					coordenadores.add(cc);

			}

			return coordenadores;		
		
	}

	
	
	/**
	 * Retorna uma lista com os coordenadores e vice-coordenadores de programa de pós graduação 
	 * ou residência em saúde.
	 * 
	 * ATENÇÃO: Este método retorna na lista objetos CoordenacaoCurso com o atributo id e todos os outros exceto unidade, 
	 * caso existe uma unidade que não tenha nenhum coordenador de curso cadastrado. 
	 * É necessário este objeto para que a unidade apareça na lista com a informação de que não possui coordenador. 
	 *
	 * @param idPrograma se for nulo retorna de todos os programas
	 * @param tipo TipoUnidadeAcademica.PROGRAMA_POS ou TipoUnidadeAcademica.PROGRAMA_RESIDENCIA
	 * @param ativo caso true retorna apenas os ativos
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CoordenacaoCurso> findProgramaECoordenacoes(List<Integer> idsPrograma, int tipo, boolean ativo, PagingInformation paging) throws DAOException {
		
			StringBuilder sql = new StringBuilder();
			List<Object> param = new ArrayList<Object>();
			
			sql.append(" SELECT DISTINCT cc.id_coordenacao_curso,cc.telefone_contato1,cc.telefone_contato2,cc.email_contato, s.id_servidor, s.siape, p.id_pessoa, ");
			sql.append("	p.nome as nome_pessoa,p.telefone_fixo, p.telefone_celular,u.id_unidade, u.nome as nome_unidade, u.sigla,  ");
			sql.append(" 	unid_resp.id_unidade as id_unidade_responsavel, unid_resp.nome as unidade_responsavel, ca.id_cargo_academico, ca.descricao as descricao_cargo, ");
			sql.append(" 	usr.id_usuario, usr.login, usr.id_pessoa as id_pessoa_usuario, cc.data_inicio_mandato, cc.data_fim_mandato, ");
			sql.append(" 	c.id_curso,c.nome as nome_curso, c.nivel,m.nome as nome_municipio,cc.ramal_telefone1,cc.ramal_telefone2 ");
			
			sql.append(" FROM comum.unidade u  ");
			
			if (ativo){
				Date dataAtual = CalendarUtils.descartarHoras(new Date());
				sql.append(" 	LEFT JOIN ensino.coordenacao_curso cc on (u.id_unidade = cc.id_unidade and cc.ativo = trueValue() AND (cc.data_inicio_mandato <= ?  AND (cc.data_fim_mandato = null OR cc.data_fim_mandato >= ? ) or cc.data_inicio_mandato >= ? )  ) ");
				param.add( dataAtual );
				param.add( dataAtual );
				param.add( dataAtual );
			} else{
				sql.append(" 	LEFT JOIN ensino.coordenacao_curso cc on (u.id_unidade = cc.id_unidade) ");
			}
			
			sql.append(" 	LEFT JOIN rh.servidor s ON (cc.id_servidor = s.id_servidor) ");
			sql.append(" 	LEFT JOIN comum.pessoa p on(p.id_pessoa = s.id_pessoa) ");
			sql.append(" 	LEFT JOIN comum.usuario usr on (usr.id_pessoa = p.id_pessoa ) ");
			sql.append(" 	LEFT JOIN ensino.cargo_academico ca ON (ca.id_cargo_academico = cc.id_cargo_academico)  	 ");
			sql.append(" 	LEFT JOIN comum.unidade unid_resp ON (unid_resp.id_unidade = u.unidade_responsavel) ");
			sql.append(" 	LEFT JOIN curso c on (cc.id_curso = c.id_curso)	 ");
			sql.append(" 	LEFT JOIN comum.municipio m on (c.id_municipio = m.id_municipio) "); 
			sql.append(" WHERE u.tipo_academica = ? "); 
			
			param.add( tipo );
			
			if( !isEmpty(idsPrograma) ){
				sql.append(" AND u.id_unidade IN " + UFRNUtils.gerarStringIn(idsPrograma));
			}
			
			sql.append(" ORDER BY u.nome, ca.descricao, p.nome  ");
			
			Collection<CoordenacaoCurso> coordenadores = new ArrayList<CoordenacaoCurso>();
			
			
			Collection lista = getJdbcTemplate().queryForList(sql.toString(), param.toArray());
			Iterator it = lista.iterator();
			
			while(it.hasNext()){
				
				Map<String, Object> mapa = (Map) it.next();
							
				CoordenacaoCurso cc = new CoordenacaoCurso();
				Integer idCoordCurso = (Integer) mapa.get("id_coordenacao_curso");
				if( idCoordCurso != null ){
					
					cc.setId(idCoordCurso);
					//CoordenacaoCurso cc = new CoordenacaoCurso((Integer) mapa.get("id_coordenacao_curso") );
					cc.setTelefoneContato1((String) mapa.get("telefone_contato1"));
					cc.setTelefoneContato2((String) mapa.get("telefone_contato2"));
					cc.setRamalTelefone1((String) mapa.get("ramal_telefone1"));
					cc.setRamalTelefone2((String) mapa.get("ramal_telefone2"));
					cc.setEmailContato((String) mapa.get("email_contato"));
					
					cc.setServidor(new Servidor((Integer) mapa.get("id_servidor")));
					cc.getServidor().setPessoa(new Pessoa((Integer) mapa.get("id_pessoa"), (String) mapa.get("nome_pessoa")));
					cc.getServidor().getPessoa().setTelefone((String) mapa.get("telefone_fixo"));
					cc.getServidor().getPessoa().setCelular((String) mapa.get("telefone_celular"));
					
					cc.setCargoAcademico(new CargoAcademico((Integer) mapa.get("id_cargo_academico"), (String) mapa.get("descricao_cargo")));
					cc.setDataInicioMandato((Date) mapa.get("data_inicio_mandato"));
					cc.setDataFimMandato((Date) mapa.get("data_fim_mandato"));
					
					//Popula os dados referentes ao curso
					Integer idCurso = (Integer) mapa.get("id_curso");
					if(idCurso != null){
						
						cc.setCurso(new Curso(idCurso));
						cc.getCurso().setNome((String) mapa.get("nome_curso"));
							cc.getCurso().setUnidade(cc.getUnidade());
						
						if( mapa.get("nivel") != null )
							cc.getCurso().setNivel((Character) mapa.get("nivel"));
						
						String municipio = (String) mapa.get("nome_municipio");
						if(municipio != null)
							cc.getCurso().setMunicipio(new Municipio(municipio));
					
					}
						
					Integer idUsr = (Integer) mapa.get("id_usuario");
					if( idUsr != null ){
						Usuario primeiroUsuario = new Usuario(idUsr, (String) mapa.get("nome_pessoa"), (String) mapa.get("login"));
						Integer idPessoaPrimeiroUsuario = ((Integer) mapa.get("id_pessoa_usuario") == 0 ) ? null : (Integer)mapa.get("id_pessoa_usuario");
						
						if (idPessoaPrimeiroUsuario != null && 
								cc.getServidor().getPessoa().getId() == idPessoaPrimeiroUsuario.intValue()) {
							cc.getServidor().setPrimeiroUsuario(primeiroUsuario);
						}
					}
									
				}
					
				
				cc.setUnidade(new Unidade((Integer) mapa.get("id_unidade"), null,(String)  mapa.get("nome_unidade"),(String)  mapa.get("sigla")));
				cc.getUnidade().setUnidadeResponsavel(new Unidade((Integer) mapa.get("id_unidade_responsavel"), null, (String) mapa.get("unidade_responsavel"), null));
				
				if( cc.getId() == 0 ){
					coordenadores.add(cc);
				}
				else if( !coordenadores.contains(cc)){
					coordenadores.add(cc);
				}

			}

			return coordenadores;		
		
	}
	
	
	/**
	 * Retorna os vínculos CoordenacaoCurso de um servidor em programas de pós-graduação.
	 * @param servidorId
	 * @param unidadeId
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public List<CoordenacaoCurso> findCoordPosByServidor(int servidorId, boolean ativo, PagingInformation paging) throws DAOException {
		try {
			Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			StringBuffer hql = new StringBuffer();
			hql.append("FROM CoordenacaoCurso WHERE unidade.id is not null ");
			hql.append(" AND unidade.tipoAcademica = " + TipoUnidadeAcademica.PROGRAMA_POS);
			hql.append(" AND curso.id is null ");
			hql.append(" and servidor.id = " + servidorId);
			if( ativo ){
				hql.append(" AND ativo = trueValue()");
				hql.append(" AND dataInicioMandato <= :hoje");
				hql.append(" AND dataFimMandato >= :hoje");
			}
			
			Query q = getSession().createQuery(hql.toString());
			q.setDate("hoje", hoje);
			preparePaging(paging, q);
			
			@SuppressWarnings("unchecked")
			List<CoordenacaoCurso> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna os vínculos CoordenacaoCurso de um servidor em programas de residência em saúde.
	 * @param servidorId
	 * @param unidadeId
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public List<CoordenacaoCurso> findCoordResidenciaByServidor(int servidorId, boolean ativo, PagingInformation paging) throws DAOException {
		try {
			Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			StringBuffer hql = new StringBuffer();
			hql.append("FROM CoordenacaoCurso WHERE unidade.id is not null ");
			hql.append(" AND unidade.tipoAcademica = " + TipoUnidadeAcademica.PROGRAMA_RESIDENCIA);
			hql.append(" AND curso.id is null ");
			hql.append(" and servidor.id = " + servidorId);
			if( ativo ){
				hql.append(" AND ativo = trueValue()");
				hql.append(" AND dataInicioMandato <= :hoje");
				hql.append(" AND (dataFimMandato is null OR dataFimMandato >= :hoje) ");
			}

			Query q = getSession().createQuery(hql.toString());
			q.setDate("hoje", hoje);
			preparePaging(paging, q);

			@SuppressWarnings("unchecked")
			List<CoordenacaoCurso> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todas as coordenações ativas ordenadas por Curso e Nome.
	 * @return lista de CoordenacaoCurso ordenada
	 * @throws DAOException
	 */
	public Collection<CoordenacaoCurso> findAllAtivosOrdenadoCursoNome() throws DAOException {
		Criteria c = getSession().createCriteria(CoordenacaoCurso.class);
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.ge("dataFimMandato", new Date()));
		c.add(Restrictions.le("dataInicioMandato", new Date()));

		Criteria curso = c.createCriteria("curso");
		curso.addOrder(Order.asc("nome"));
		curso.add(Restrictions.eq("nivel", NivelEnsino.GRADUACAO));

		c.createCriteria("cargoAcademico").addOrder(Order.asc("descricao"));

		@SuppressWarnings("unchecked")
		List<CoordenacaoCurso> lista = c.list();
		return lista;
	}

	/**
	 * Retorna todas as coordenações de curso de uma unidade/nível específicos.
	 * @param unidadeId
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<CoordenacaoCurso> findAll(int unidadeId, char nivel,
			PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from CoordenacaoCurso ");
			if (unidadeId > 0)
				hql.append("where curso.unidade.id = "+unidadeId);
			if (!ValidatorUtil.isEmpty(nivel))
				hql.append(" and curso.nivel = '" + nivel+"'");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);

			@SuppressWarnings("unchecked")
			List<CoordenacaoCurso> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna o coordenador e vice atual (Stricto-Sensu, Graduação e Técnico).
	 * @param id (Curso ou Unidade)
	 * @return
	 * @throws DAOException
	 */
	public Collection<CoordenacaoCurso> findCoordViceByCursoNivel(Integer id, Character nivel) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CoordenacaoCurso.class);
			if(NivelEnsino.isAlgumNivelStricto(nivel))
				c.add(Expression.eq("unidade.id", id));
			else
				c.add(Expression.eq("curso.id", id));
			c.add(Expression.eq("ativo", true));
			c.add(Expression.or(Expression.isNull("dataFimMandato"), Expression.gt("dataFimMandato", new Date())));
			c.addOrder(Order.desc("dataFimMandato"));
			c.addOrder(Order.asc("cargoAcademico"));
			c.setMaxResults(2);

			@SuppressWarnings("unchecked")
			List<CoordenacaoCurso> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/**
	 * Retorna a última coordenação de curso ATIVA do servidor/curso informado.
	 * @param servidor
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public CoordenacaoCurso findUltimaByServidorCurso(Servidor servidor, Curso curso) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CoordenacaoCurso.class);
			c.add(Expression.eq("servidor", servidor));
			c.add(Expression.eq("curso", curso));
			c.add(Expression.eq("ativo", true));
			c.add(Expression.or(Expression.isNull("dataFimMandato"), Expression.ge("dataFimMandato", new Date())));
			c.addOrder(Order.desc("dataFimMandato"));
			c.setMaxResults(1);
			return (CoordenacaoCurso) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna o último registro de CoordenacaoCurso do servidor e programa informado.
	 * @param servidor
	 * @param programa
	 * @return
	 * @throws DAOException
	 */
	public CoordenacaoCurso findUltimaByServidorPrograma(Servidor servidor, Unidade programa) throws DAOException{
			Criteria c = getSession().createCriteria(CoordenacaoCurso.class);
			c.add(Expression.eq("servidor", servidor));
			c.add(Expression.eq("unidade", programa));
			c.add(Expression.eq("ativo", true));
			c.add(Expression.or(Expression.isNull("dataFimMandato"), Expression.gt("dataFimMandato", new Date())));
			c.addOrder(Order.desc("dataFimMandato"));
			c.setMaxResults(1);
			return (CoordenacaoCurso) c.uniqueResult();
	}

	/**
	 * Retorna o último coordenador do curso informado.
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public CoordenacaoCurso findUltimaByCurso(Curso curso) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CoordenacaoCurso.class);
			c.add(Expression.eq("curso", curso));
			c.add(Expression.eq("ativo", true));
			c.add(Expression.or(Expression.isNull("dataFimMandato"), Expression.gt("dataFimMandato", new Date())));
			c.add(Expression.eq("cargoAcademico.id", CargoAcademico.COORDENACAO));
			c.addOrder(Order.desc("dataFimMandato"));
			c.setMaxResults(1);
			return (CoordenacaoCurso) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna o último coordenador do curso informado por cargo.
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public CoordenacaoCurso findUltimaByCursoCargo(Curso curso, int idCargoAcademico) throws DAOException{
		return findUltima(curso, null, idCargoAcademico);
	}
	

	/**
	 * Retorna o último coordenador do curso ou programa informado por cargo.
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public CoordenacaoCurso findUltima(Curso curso, Unidade unidade, int idCargoAcademico) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CoordenacaoCurso.class);
			if( !isEmpty(curso) )
				c.add(Expression.eq("curso.id", curso.getId()));
			else 
				c.add(Expression.eq("unidade.id", unidade.getId()));
			c.add(Expression.eq("ativo", true));
			c.add(Expression.eq("cargoAcademico.id", idCargoAcademico));
			c.add(Expression.le("dataInicioMandato", new Date()));
			c.add(Expression.or(Expression.isNull("dataFimMandato"), Expression.gt("dataFimMandato", new Date())));
			c.addOrder(Order.desc("dataFimMandato"));
			c.setMaxResults(1);
			return (CoordenacaoCurso) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna o último coordenador do programa informado.
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public CoordenacaoCurso findUltimaByPrograma(Unidade unidade) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CoordenacaoCurso.class);
			c.add(Expression.eq("unidade", unidade));
			c.add(Expression.eq("ativo", true));
			c.add(Expression.or(Expression.isNull("dataFimMandato"), Expression.gt("dataFimMandato", new Date())));
			c.add(Expression.eq("cargoAcademico.id", CargoAcademico.COORDENACAO));
			c.addOrder(Order.desc("dataFimMandato"));
			c.setMaxResults(1);
			return (CoordenacaoCurso) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna o coordenador ativo de um curso em uma determinada data. 
	 * @param data
	 * @param curso
	 * @return
	 * @throws DAOException 
	 */
	public CoordenacaoCurso findAtivoByData(Date data, Curso curso) throws DAOException{
		data = CalendarUtils.incluirHoraFimDia(data);
		Criteria c = getSession().createCriteria(CoordenacaoCurso.class);
		c.add(Restrictions.eq("curso.id", curso.getId()));
		c.add(Restrictions.and(
				Restrictions.le("dataInicioMandato", data), Restrictions.ge("dataFimMandato", data)));
		c.add(Restrictions.eq("cargoAcademico.id", CargoAcademico.COORDENACAO));
		c.addOrder(Order.desc("dataFimMandato"));
		c.setMaxResults(1);
		return (CoordenacaoCurso) c.uniqueResult();
	}

}