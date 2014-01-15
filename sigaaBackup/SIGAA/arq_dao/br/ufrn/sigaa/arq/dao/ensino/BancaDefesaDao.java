/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/02/2011
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.BancaDefesa;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.StatusBanca;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/** 
 * DAO responsável por consultas específicas à Banca de Defesa 
 * 
 * @author arlindo
 *
 */
public class BancaDefesaDao extends GenericSigaaDAO {
	
	
	/**
	 * Retorna as bancas de defesa conforme os parâmetros informados
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @param tituloTrabalho
	 * @param docente
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public List<BancaDefesa> consultarBancas(Integer idCurso, Date dataInicio, Date dataFim, String tituloTrabalho, String docente, String discente) throws DAOException {
		
		String projecao = " banca.id, discente.id, discente.matricula, pessoa.nome, curso.id, " +
		" curso.nome, curso.unidade.sigla, banca.titulo, banca.dataDefesa, status.id, status.descricao, " +
		" mc.id, mc.ano, mc.periodo, componente.codigo, detalhes.nome " ;	
		
		StringBuffer hql = new StringBuffer();
		
		hql.append("select distinct "+projecao+" from BancaDefesa banca ");
		
		hql.append(" inner join banca.status status ");
		hql.append(" inner join banca.discente discente ");
		hql.append(" inner join discente.pessoa pessoa ");
		hql.append(" inner join discente.curso curso ");
		hql.append(" inner join curso.unidade unidade ");
		hql.append(" left join banca.matriculaComponente mc ");
		hql.append(" left join banca.matriculaComponente.componente componente ");
		hql.append(" left join banca.matriculaComponente.componente.detalhes detalhes  ");
		
		if (!StringUtils.isEmpty(docente)){
			hql.append( " left join banca.membrosBanca membro " +
				        " left join membro.pessoaMembroExterno membroExterno "+
			            " left join membro.servidor.pessoa docente "+
			            " left join membro.docenteExterno.pessoa docenteExternoPrograma ");
		}		
		
		hql.append(" where 1 = 1 ");
		
		if (idCurso != null && idCurso > 0)
			hql.append(" and curso.id = "+idCurso);
		
		if (dataInicio != null) {
			hql.append(" and banca.dataDefesa >= :dataInicio ");
		}
		
		if (dataFim != null) {
			hql.append(" and banca.dataDefesa <= :dataFim ");
		}
		
		if (!StringUtils.isEmpty(tituloTrabalho)) {
			hql.append(" and ");
			hql.append(UFRNUtils.convertUtf8UpperLatin9("banca.titulo") 
			+ " like " + UFRNUtils.toAsciiUpperUTF8(" '%' || :titulo || '%' "));
		}
		
		if (!StringUtils.isEmpty(docente)){
			hql.append(" and (");
			hql.append(" membroExterno.nomeAscii like "+UFRNUtils.toAsciiUpperUTF8(":docente||'%'"));
			hql.append(" or docente.nomeAscii like "+UFRNUtils.toAsciiUpperUTF8(":docente||'%'"));
			hql.append(" or docenteExternoPrograma.nomeAscii like "+UFRNUtils.toAsciiUpperUTF8(":docente||'%'"));
			hql.append(" )");			
		}		
		
		if (!StringUtils.isEmpty(discente)){
			hql.append(" and pessoa.nomeAscii like "+UFRNUtils.toAsciiUpperUTF8(":discente||'%' "));
		}		
		
		hql.append(" order by pessoa.nome, banca.dataDefesa desc, banca.titulo");
		
		Query q = getSession().createQuery(hql.toString());
		
		// pesquisa apenas dataInicio
		if (dataInicio != null && !dataInicio.equals("") ) {
			q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(dataInicio, 0, 0, 0, 0));
		}
		
		// pesquisa apenas dataFinal
		if (dataFim != null&& !dataFim.equals("")) {
			q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(dataFim, 23, 59, 59, 999)); 
		}

		if (tituloTrabalho != null && !tituloTrabalho.trim().isEmpty()) {
			q.setParameter("titulo",  tituloTrabalho.trim());
		}
		
		if (!StringUtils.isEmpty(docente)){
			q.setParameter("docente", docente.trim());
		}		
		
		if (!StringUtils.isEmpty(discente)){
			q.setParameter("discente", discente.trim());
		}		
		
		List<BancaDefesa> lista = new ArrayList<BancaDefesa>();
		@SuppressWarnings("unchecked")
        Collection<Object[]> res = q.list();
        if (res != null ) {
        	for (Object[] reg : res) {
        		int a = 0;

        		BancaDefesa banca = new BancaDefesa();
        		banca.setId((Integer) reg[a++]);
        		
        		banca.setDiscente(new Discente());
        		banca.getDiscente().getDiscente().setId((Integer) reg[a++]);
        		banca.getDiscente().getDiscente().setMatricula((Long) reg[a++]);
        		banca.getDiscente().getDiscente().setPessoa(new Pessoa());
        		banca.getDiscente().getDiscente().getPessoa().setNome((String) reg[a++]);
        		
        		banca.getDiscente().setCurso(new Curso((Integer) reg[a++]));
        		banca.getDiscente().getCurso().setNome((String) reg[a++]);
        		banca.getDiscente().getCurso().setUnidade(new Unidade());
        		banca.getDiscente().getCurso().getUnidade().setSigla((String) reg[a++]);
        		
        		banca.setTitulo((String) reg[a++]);
        		banca.setDataDefesa((Date) reg[a++]);
        		
        		banca.setStatus(new StatusBanca((Integer) reg[a++]));
        		banca.getStatus().setDescricao((String) reg[a++]);
        		
        		Integer idMatricula = (Integer) reg[a++];
        		if (idMatricula != null && idMatricula > 0){
        			banca.setMatriculaComponente(new MatriculaComponente(idMatricula));
        			banca.getMatriculaComponente().setAno((Short) reg[a++]);
        			banca.getMatriculaComponente().setPeriodo((Byte) reg[a++]);
        			banca.getMatriculaComponente().setComponente(new ComponenteCurricular());
        			banca.getMatriculaComponente().getComponente().setCodigo((String) reg[a++]);
        			banca.getMatriculaComponente().getComponente().setDetalhes(new ComponenteDetalhes());
        			banca.getMatriculaComponente().getComponente().getDetalhes().setNome((String) reg[a++]);
        		}
        		
        		lista.add(banca);
        	}
        }
		
		return lista;
		
	}
	
	/**
	 * Consulta Banca de Defesa de acordo com o discente informado
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public BancaDefesa findByDiscente(DiscenteAdapter discente) throws DAOException {
		try {
			Query q = getSession()
					.createQuery(
					"FROM BancaDefesa "
						+ " WHERE discente.id = :discente"
						+ " and status.id = "+StatusBanca.ATIVO);
			q.setInteger("discente", discente.getId());
			q.setMaxResults(1);
			return (BancaDefesa) q.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}	
	
	/**
	 * Remove todos os Membros pertencentes a uma Banca de Defesa
	 * 
	 * @param b
	 */
	public void removerMembrosBanca(BancaDefesa b) {
		update("delete from ensino.membro_banca where id_banca = ?", new Object[] { b.getId() });
	}	

}
