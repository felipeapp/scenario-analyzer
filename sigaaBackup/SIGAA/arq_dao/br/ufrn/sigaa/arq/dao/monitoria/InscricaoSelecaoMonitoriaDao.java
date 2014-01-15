/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/03/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.monitoria.dominio.InscricaoSelecaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Dao para realizar consultas sobre a entidade InscricaoSelecaoMonitoria
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 */
public class InscricaoSelecaoMonitoriaDao extends GenericSigaaDAO {

	/**
	 * retorna uma InscricaoSelecaoMonitoria do discente e projeto passado por
	 * parâmetro
	 * 
	 * @param discente
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	public InscricaoSelecaoMonitoria findByDiscenteProjeto(DiscenteAdapter discente,
			ProvaSelecao prova) throws DAOException {

		String hql = "select inscricao from InscricaoSelecaoMonitoria inscricao where "
				+ "inscricao.discente.id = :idDiscente "
				+ "and inscricao.provaSelecao.id = :idProva and inscricao.ativo = trueValue()";

		Query q = getSession().createQuery(hql);
		q.setMaxResults(1);

		q.setInteger("idDiscente", discente.getId());
		q.setInteger("idProva", prova.getId());

		return (InscricaoSelecaoMonitoria) q.uniqueResult();

	}

	/**
	 * Retorna todas as inscrições ativas de uma prova seletiva
	 * 
	 * @param idProva
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<InscricaoSelecaoMonitoria> findByProvaSeletiva(Integer idProva) throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append("select insc.id, insc.dataCadastro, insc.ativo, insc.projetoEnsino.projeto.titulo as tituloProjeto, prova.titulo as tituloProva,"
				+ " pes.id, pes.nome, pes.email, grad.id, dis.id, dis.matricula, dis.nivel, dis.status, usr.id, usr.login"
				+ " from InscricaoSelecaoMonitoria insc "
				+ " inner join insc.provaSelecao prova "
				+ " inner join insc.discente grad "
				+ " inner join grad.discente dis "
				+ " inner join dis.pessoa pes, "
				+ " Usuario usr "
				+ " where insc.ativo = trueValue() "
				+ " and usr.pessoa.id = pes.id");
    	
		if (idProva != null) {
		    hql.append(" and prova.id = :idProva ");
		}
		
		hql.append(" order by insc.dataCadastro ");
		Query q = getSession().createQuery(hql.toString());
		if (idProva != null) {
			q.setInteger("idProva", idProva);
		}
		List<Object[]> lista = q.list();

		Collection<InscricaoSelecaoMonitoria> result = new ArrayList<InscricaoSelecaoMonitoria>();
		for (Object[] colunas : lista) {
			int col = 0;
			InscricaoSelecaoMonitoria i = new InscricaoSelecaoMonitoria();
			i.setId((Integer) colunas[col++]);
			i.setDataCadastro((Date) colunas[col++]);
			i.setAtivo((Boolean) colunas[col++]);
			i.setProjetoEnsino(new ProjetoEnsino());
			i.getProjetoEnsino().setProjeto(new Projeto());
			i.getProjetoEnsino().getProjeto().setTitulo((String) colunas[col++]);
			i.setProvaSelecao(new ProvaSelecao());
			i.getProvaSelecao().setTitulo((String) colunas[col++]);
	    	
			Pessoa p = new Pessoa();
			p.setId((Integer) colunas[col++]);
			p.setNome((String) colunas[col++]);
			p.setEmail((String) colunas[col++]);
			
			DiscenteGraduacao d = new DiscenteGraduacao();
			d.setId((Integer) colunas[col++]);
			d.setDiscente(new Discente((Integer) colunas[col++]));
			d.getDiscente().getDiscente().setIndices(new ArrayList<IndiceAcademicoDiscente>());
			d.setMatricula((Long) colunas[col++]);
			d.setNivel((Character) colunas[col++]);
			d.setStatus((Integer) colunas[col++]);
			
			Usuario usr = new Usuario();
			usr.setId((Integer) colunas[col++]);
			usr.setLogin((String) colunas[col++]);
			
			d.setPessoa(p);
			d.setUsuario(usr);
			i.setDiscente(d);
			result.add(i);
		}

		return result;

	}
}
