/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/01/2013
 *
 */
package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.ValidacaoVinculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/** 
 * Dao responsável pelas consultas do caso de uso de validação de vínculo de alunos ingressantes.
 * 
 * @author Diego Jácome
 *
 */
public class ValidacaoVinculoDao extends GenericSigaaDAO { 

	public Collection<DiscenteAdapter> findDiscentesIngressantes (int anoIngresso, int periodoIngresso, Integer idCurso) throws HibernateException, DAOException {
		return findDiscentesIngressantes(anoIngresso,periodoIngresso,null,null,null,idCurso,null);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<DiscenteAdapter> findDiscentesIngressantes (int anoIngresso, int periodoIngresso, Long matricula, String nome, Long cpf, Integer idCurso, Integer idMatriz) throws HibernateException, DAOException {
		
		String sql = " select d.id_discente , d.matricula , d.status , d.nivel , d.ano_ingresso , d.periodo_ingresso , p.id_pessoa , p.nome pnome, p.cpf_cnpj , "+
				       	" c.id_curso , c.nome cnome, v.id_validacao_vinculo , v.data_validacao , m.id_municipio , m.nome mnome, me.id_modalidade_educacao , me.descricao medescricao, "+ 
						" mc.id_matriz_curricular , h.id_habilitacao , h.nome hnome , e.id_enfase , e.nome enome , g.id_grau_academico , g.descricao gdescricao "+
						" from discente d "+
						" left join ensino.validacao_vinculo v on v.id_discente = d.id_discente " +
						" left join graduacao.discente_graduacao dg on dg.id_discente_graduacao = d.id_discente "+
						" left join comum.pessoa p on p.id_pessoa = d.id_pessoa "+
						" left join curso c on c.id_curso = d.id_curso "+
						" left join comum.municipio m on c.id_municipio = m.id_municipio "+
						" left join comum.modalidade_educacao me on c.id_modalidade_educacao = me.id_modalidade_educacao "+
						" left join graduacao.matriz_curricular mc on mc.id_matriz_curricular = dg.id_matriz_curricular "+
						" left join graduacao.habilitacao h on mc.id_habilitacao = h.id_habilitacao "+
						" left join graduacao.enfase e on mc.id_enfase = e.id_enfase "+
						" left join ensino.turno t on mc.id_turno = t.id_turno "+
						" left join ensino.grau_academico g on mc.id_grau_academico = g.id_grau_academico "+
						" join ensino.forma_ingresso f on d.id_forma_ingresso = f.id_forma_ingresso "+
						" where d.ano_ingresso = "+anoIngresso+" and d.periodo_ingresso = "+periodoIngresso+" "+
						" and d.status in ("+StatusDiscente.ATIVO+") "+
						" and d.nivel = '"+NivelEnsino.GRADUACAO+"' "+ 
						" and f.id_forma_ingresso in ( 34110 , 51252808) "; // VESTIBULAR / SiSU
		
		if (matricula != null) 
			sql += " and d.matricula = " + matricula;
		if (!StringUtils.isEmpty(nome))
			sql += " and " + UFRNUtils.convertUtf8UpperLatin9("p.nome_ascii")
						+ " like '" + UFRNUtils.trataAspasSimples(StringUtils.toAscii(nome.toUpperCase()))
						+ "%'";
		if (!isEmpty(idCurso))
			sql += " and c.id_curso = "+idCurso;
		if (!isEmpty(cpf))
			sql += " and p.cpf_cnpj = "+cpf;
		if (!isEmpty(idMatriz))
			sql += " and mc.id_matriz_curricular = "+idMatriz;
		
		sql +=	" order by c.nome , mc.id_matriz_curricular , p.nome asc";
		
		Query q = getSession().createSQLQuery(sql);		
		List<Object[]> result = q.list();
		ArrayList<DiscenteAdapter> discentes = new ArrayList<DiscenteAdapter>();
		
		if (result != null){
			for ( Object[] linha : result ){
				
				Integer i = 0;
				DiscenteAdapter d = new DiscenteGraduacao();
				
				d.setId((Integer)linha[i++]);
				Number mat = (Number)linha[i++];
				d.setMatricula(mat!=null?mat.longValue():null);
				d.setStatus((Short)linha[i++]);
				d.setNivel((Character)linha[i++]); 
				d.setAnoIngresso((Integer)linha[i++]);
				d.setPeriodoIngresso((Integer)linha[i++]);
				
				Pessoa p = new Pessoa();
				p.setId((Integer)linha[i++]);
				p.setNome((String)linha[i++]);
				p.setCpf_cnpj(((Number)linha[i++]).longValue());
				
				Curso c = new Curso();
				c.setId((Integer)linha[i++]);
				c.setNome((String)linha[i++]);
											
				Integer idValidacao = (Integer)linha[i++];
				ValidacaoVinculo v = new ValidacaoVinculo();
				v.setId(idValidacao!=null?idValidacao:0);
				v.setDataValidacao((Date)linha[i++]);
				if (idValidacao != null)
					d.getDiscente().setMarcado(false);
				else
					d.getDiscente().setMarcado(true);
				
				Municipio m = new Municipio();
				m.setId((Integer)linha[i++]);
				m.setNome((String)linha[i++]);
				
				ModalidadeEducacao me = new ModalidadeEducacao();
				me.setId((Integer)linha[i++]);
				me.setDescricao((String)linha[i++]);
				
				d.setPessoa(p);
				d.getDiscente().setValidacaoVinculo(v);
				c.setMunicipio(m);
				c.setModalidadeEducacao(me);
				d.setCurso(c);
				
				DiscenteGraduacao dg = (DiscenteGraduacao) d;
				dg.getDiscente().setId(dg.getId());
				
				MatrizCurricular mc = new MatrizCurricular();
				Integer idMatrizCurricular = (Integer)linha[i++];
				mc.setId(idMatrizCurricular!=null?idMatrizCurricular:0);
				
				Habilitacao h = new Habilitacao();
				Integer idHabilitacao = (Integer)linha[i++];
				h.setId(idHabilitacao!=null?idHabilitacao:0);
				h.setNome((String)linha[i++]);
				
				Enfase e = new Enfase();
				Integer idEnfase = (Integer)linha[i++];
				e.setId(idEnfase!=null?idEnfase:0);
				e.setNome((String)linha[i++]);
				
				GrauAcademico g = new GrauAcademico();
				g.setId((Integer)linha[i++]);
				g.setDescricao((String)linha[i++]);
				
				mc.setCurso(c);
				mc.setHabilitacao(h);
				mc.setEnfase(e);
				mc.setGrauAcademico(g);
				dg.setMatrizCurricular(mc);
				
				discentes.add(d);
			}
		}
		
		return discentes;
	}
	
}
