/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '30/04/2010'
 *
 */

package br.ufrn.sigaa.arq.dao.ensino;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.AvisoFaltaDocenteHomologada;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DadosAvisoFalta;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAvisoFaltaHomologado;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.jsf.AvisoFaltaDocenteMBean.BuscaAvisoFaltaCampos;
import br.ufrn.sigaa.ensino.graduacao.jsf.AvisoFaltaDocenteMBean.BuscaAvisoFaltaChecks;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

public class DadosAvisoFaltaDao extends GenericSigaaDAO {

	public DadosAvisoFalta findByDocenteTurmaAula(int idDocente, int idTurma, Date dataAula, boolean isServidor) throws DAOException {
		
		StringBuilder hql = new StringBuilder("select d from DadosAvisoFalta d where d.turma.id = :turma and d.dataAula = :dataAula");

		if (isServidor)
			hql.append(" and d.docente.id = :docente ");
		else
			hql.append(" and d.docenteExterno.id = :docente ");
			 
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("docente", idDocente);
		q.setInteger("turma", idTurma);
		q.setDate("dataAula", dataAula);
		
		return (DadosAvisoFalta) q.uniqueResult();
	}
	
	/**
	 * Pesquisa todos os avisos de falta docente pelo docente e ano-período
	 * 
	 * @param idDocente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	@SuppressWarnings("unchecked")
	public List<DadosAvisoFalta> findGeral(BuscaAvisoFaltaCampos campos, BuscaAvisoFaltaChecks checks) throws DAOException {
		
		List<Object> params = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(		" select dados.id_dados_aviso_falta, p.nome, cc.codigo as disc_codigo, det.nome as det_nome, t.codigo as turma_codigo, dados.data_aula, s.id_servidor, t.id_turma, " + 
						" 	(select count(af.id_aviso_falta_docente) from ensino.aviso_falta_docente af where af.id_falta_docente = dados.id_dados_aviso_falta ) as qtd_aviso, " +
						" 	(select h.id_falta_homologada from ensino.aviso_falta_docente_homologada h where h.id_dados_aviso = dados.id_dados_aviso_falta order by data_cadastro desc limit 1) as id_falta_homologada, " +
						" 	(select h.id_movimentacao from ensino.aviso_falta_docente_homologada h where h.id_dados_aviso = dados.id_dados_aviso_falta order by data_cadastro desc limit 1) as id_movimentacao " +
						" from ensino.dados_aviso_falta dados " + 
						" 	inner join ensino.turma t on (dados.id_turma = t.id_turma) " +
						" 	inner join ensino.componente_curricular cc on (cc.id_disciplina = t.id_disciplina) " +
						" 	inner join ensino.componente_curricular_detalhes det on (det.id_componente_detalhes = cc.id_detalhe  ) " +
						" 	left join rh.servidor s on (s.id_servidor = dados.id_docente) " +
						" 	left join ensino.docente_externo de on (de.id_docente_externo = dados.id_docente_externo) " +
						"   inner join comum.unidade u on (u.id_unidade in (s.id_unidade,de.id_unidade)) " +
						" 	inner join comum.pessoa p on (p.id_pessoa = de.id_pessoa or p.id_pessoa = s.id_pessoa ) " +
						" where 1=1 ");
		
		if (checks.isCheckAnoPeriodo()) {
			sql.append(	"	and t.ano = ? " +
						"	and t.periodo = ? ");
			
			params.add(campos.getAno());
			params.add(campos.getPeriodo());
		}
		
		if(checks.isCheckCentro()){
			sql.append(" and u.id_gestora = ? ");
			params.add(campos.getCentro());
		}
		
		if(checks.isCheckDepartamento() || checks.isCheckUnidade())
			sql.append(" and ? in (s.id_unidade, de.id_unidade) ");
		
		if(checks.isCheckDepartamento()){
			params.add(campos.getDepartamento());
		}else if(checks.isCheckUnidade()){
			params.add(campos.getUnidade().getId());
		}
		
		if (checks.isCheckDocente()) {
			if (campos.getDocente().isServidor())
				sql.append(" and s.id_servidor = ? ");
			else
				sql.append(" and de.id_docente_externo = ? ");
			
			params.add(campos.getDocente().getIdProfessor());
		}
		
		sql.append(" order by p.nome");
		
		return getJdbcTemplate().query(sql.toString(), params.toArray(), new RowMapper() {
			
			@Override
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				
				Pessoa p = new Pessoa();
				p.setNome(rs.getString("nome"));
				
				DadosAvisoFalta dados = new DadosAvisoFalta();
				dados.setId(rs.getInt("id_dados_aviso_falta"));
				dados.setQtdAvisos(rs.getInt("qtd_aviso"));
				dados.setDataAula(rs.getDate("data_aula"));
				dados.setTurma(new Turma(rs.getInt("id_turma")));
				
				dados.getTurma().setDisciplina(new ComponenteCurricular());
				dados.getTurma().getDisciplina().setDetalhes(new ComponenteDetalhes());
				
				if (rs.getInt("id_falta_homologada") != 0) {
					dados.setAvisoHomologado(new AvisoFaltaDocenteHomologada());
					dados.getAvisoHomologado().setId(rs.getInt("id_falta_homologada"));
					dados.getAvisoHomologado().setMovimentacao(new MovimentacaoAvisoFaltaHomologado(rs.getInt("id_movimentacao")));
				}
				
				if (rs.getInt("id_servidor") != 0) {
					dados.setDocente(new Servidor());
					dados.getDocente().setPessoa(p);
				} else {
					dados.setDocenteExterno(new DocenteExterno());
					dados.getDocenteExterno().setPessoa(p);
				}
				
				dados.getTurma().setCodigo(rs.getString("turma_codigo"));
				dados.getTurma().getDisciplina().setCodigo(rs.getString("disc_codigo"));
				dados.getTurma().getDisciplina().getDetalhes().setNome(rs.getString("det_nome"));
				
				return dados;
			}
		});
	}	
}
