/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/07/2012
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO com consultas relativas ao fechamento compulsório de atividades.
 * 
 * @author Diego Jácome
 *
 */
public class FechamentoCompulsorioDao extends GenericSigaaDAO  {

	/**
	 * Retorna todas as matrícula abertas ou e espera do ano e período passados como parâmetro e todos os anos e periodos antes dele. 
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings({ "null", "unchecked" })
	public ArrayList<MatriculaComponente> findMatriculasFechamentoByAnoPeriodo(int ano, int periodo, boolean ead , PagingInformation paging) throws DAOException {
		
		String projecao = " m.id_matricula_componente , m.ano, m.periodo , sm.id_situacao_matricula , sm.descricao , d.id_discente, d.matricula , d.status , p.id_pessoa, " +
						  " p.nome as pnome , cc.id_disciplina, ccd.id_componente_detalhes, ccd.nome as ccdnome, c.id_curso, c.nome as cnome, mu.id_municipio, mu.nome as munome";
		
		String sql = "select " +projecao+
						" from ensino.matricula_componente m "+
						" join ensino.situacao_matricula sm on sm.id_situacao_matricula = m.id_situacao_matricula" +
						" join discente d on d.id_discente = m.id_discente "+
						" join comum.pessoa p on p.id_pessoa = d.id_pessoa "+
						" join curso c on c.id_curso = d.id_curso "+
						" join comum.municipio mu on mu.id_municipio = c.id_municipio " +
						" join ensino.componente_curricular cc on cc.id_disciplina = m.id_componente_curricular "+
						" left join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes  "+
						" where m.id_turma is null "+
						" and m.ano <= "+ano+" and not (ano = "+ano+" and m.periodo > "+periodo+") "+
						" and m.id_situacao_matricula in "+ gerarStringIn( new int[] {SituacaoMatricula.EM_ESPERA.getId(),SituacaoMatricula.MATRICULADO.getId()} )+ " "+
						" and cc.nivel ilike '"+NivelEnsino.GRADUACAO+ "' "+
						" and cc.id_tipo_componente = "+TipoComponenteCurricular.ATIVIDADE +
						" and c.id_modalidade_educacao = " +(ead ? ModalidadeEducacao.A_DISTANCIA : ModalidadeEducacao.PRESENCIAL)+ " "+
						" order by c.nome, mu.nome,  m.ano , m.periodo , ccd.nome , p.nome , d.status";

		
		Query q = getSession().createSQLQuery(sql);
		
		if(paging != null && q != null){
			paging.setTotalRegistros(count(sql));
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
		
		ArrayList<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
		
		List<Object[]> result = q.list();
		
		if ( result != null )
		{	
			for (Object[] linha : result) {
				Integer i = 0;
				
				MatriculaComponente m = new MatriculaComponente();
				m.setId((Integer) linha[i++]);
				m.setAno((Short) linha[i++]);
				m.setPeriodo(((Number) linha[i++]).byteValue());
				
				SituacaoMatricula sm = new SituacaoMatricula();
				sm.setId((Integer) linha[i++]);
				sm.setDescricao((String) linha[i++]);
				
				Discente d = new Discente();
				d.setId((Integer) linha[i++]);
				d.setMatricula(((Number) linha[i++]).longValue());
				d.setStatus((Short) linha[i++]);
				
				Pessoa p = new Pessoa();
				p.setId((Integer) linha[i++]);
				p.setNome((String) linha[i++]);

				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setId((Integer) linha[i++]);
				
				ComponenteDetalhes ccd = new ComponenteDetalhes();
				ccd.setId((Integer) linha[i++]);
				ccd.setNome((String) linha[i++]);

				Curso c = new Curso();
				c.setId((Integer) linha[i++]);
				c.setNome((String) linha[i++]);

				Municipio mu = new Municipio();
				mu.setId((Integer) linha[i++]);
				mu.setNome((String) linha[i++]);
				
				c.setMunicipio(mu);
				d.setPessoa(p);
				d.setCurso(c);
				cc.setDetalhes(ccd);
				m.setSituacaoMatricula(sm);
				m.setDiscente(d);
				m.setComponente(cc);
				
				matriculas.add(m);
			}
			
			return matriculas;
		}	
	
		return null;
	}
	
}
