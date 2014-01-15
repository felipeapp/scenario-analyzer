/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 11/10/2010
 */
package br.ufrn.sigaa.arq.dao.estagio;

import java.util.Collection;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.estagio.dominio.ConcedentePessoaFuncao;
import br.ufrn.sigaa.estagio.dominio.OfertaEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusOfertaEstagio;

/**
 * DAO responsável por gerenciar o acesso a dados de Oferta de Estágio
 * 
 * @author Arlindo Rodrigues
 */
 
public class OfertaEstagioDao extends GenericSigaaDAO {

	/**
	 * Retorna a lista de Oferta de Estágios conforme os Parâmetros informados.
	 * @param curso
	 * @param status
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<OfertaEstagio> findGeral(Curso curso, String concedente, String responsavel, String titulo, Date dataInicio, Date dataFim, 
			DiscenteAdapter discenteInteresse, boolean apenasAbertas) throws HibernateException, DAOException{
		String projecao = " oe.id, oe.titulo, oe.descricao, oe.numeroVagas, oe.valorBolsa, oe.concedente.pessoa.nome, oe.concedente.pessoa.id, " +
				"oe.dataInicioPublicacao, oe.dataFimPublicacao, oe.dataCadastro, oe.status.id, oe.status.descricao";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select "+projecao+" from OfertaEstagio oe ");
		hql.append("inner join oe.concedente ");
		hql.append("inner join oe.status ");
		hql.append("inner join oe.concedente.pessoa ");
		hql.append("inner join oe.concedente.convenioEstagio ");
		hql.append("where oe.concedente.ativo = trueValue() ");
		
		if (!ValidatorUtil.isEmpty(curso))
			hql.append(" and "+curso.getId()+" in elements (oe.cursosOfertados)");
		
		if (!ValidatorUtil.isEmpty(discenteInteresse)){
			hql.append(" and oe.id in ( " +
								"select o.id from InteresseOferta i " +
								" inner join i.oferta o "+
								" where i.discente.id = " +discenteInteresse.getId()+
								" and i.ativo = trueValue() " +
								")");
		}
			
		Long cnpjEmpresa = null;
		if (!ValidatorUtil.isEmpty(concedente)){
			cnpjEmpresa = StringUtils.extractLong(concedente);
			if (cnpjEmpresa != null)
				hql.append(" and oe.concedente.pessoa.cpf_cnpj = "+cnpjEmpresa);
			else
				hql.append(" and "+UFRNUtils.toAsciiUpperUTF8("oe.concedente.pessoa.nomeAscii") + " like :concedente ");
		}
		
		Long cpfReponsavel = null;
		if (!ValidatorUtil.isEmpty(responsavel)){
			cpfReponsavel = StringUtils.extractLong(responsavel);
			hql.append(" and oe.concedente.id in ( " +
						" select p.id from ConcedenteEstagioPessoa pe " +
						" inner join pe.concedente p " +
						" where pe.funcao.id = "+ConcedentePessoaFuncao.ADMINISTRADOR);
			if (cpfReponsavel != null)
				hql.append(" and pe.pessoa.cpf_cnpj = :responsavel ) ");
			else
				hql.append(" and "+UFRNUtils.toAsciiUpperUTF8("pe.pessoa.nomeAscii") + " like :responsavel )");
		}	
		
		if (!ValidatorUtil.isEmpty(titulo))
			hql.append(" and oe.titulo like :titulo ");		
		if (apenasAbertas){
			hql.append(" and oe.status = "+StatusOfertaEstagio.APROVADO);				
		}
		if (!ValidatorUtil.isEmpty(dataInicio) && !ValidatorUtil.isEmpty(dataFim)) {
			hql.append(" and oe.dataInicioPublicacao <= :dataInicio ");
			hql.append(" and oe.dataFimPublicacao >= :dataFim ");
		}
		
		hql.append(" order by oe.concedente.pessoa.nome, oe.dataFimPublicacao ");
		
		Query q = getSession().createQuery(hql.toString());
		
		if (!ValidatorUtil.isEmpty(concedente) && cnpjEmpresa == null)
			q.setString("concedente", "%"+StringUtils.toAscii(concedente.trim().toUpperCase()) + "%");
		
		if (!ValidatorUtil.isEmpty(responsavel)){
			if (cpfReponsavel != null)
				q.setLong("responsavel", cpfReponsavel);
			else
				q.setString("responsavel", "%"+StringUtils.toAscii(responsavel.trim().toUpperCase()) + "%");
		}	
		
		if (!ValidatorUtil.isEmpty(titulo))
			q.setString("titulo", "%"+titulo.trim().toUpperCase() + "%");
		
		if (!ValidatorUtil.isEmpty(dataInicio) && !ValidatorUtil.isEmpty(dataFim)) {
			q.setDate("dataInicio", dataInicio);
			q.setDate("dataFim", dataFim);			
		}
							
		@SuppressWarnings("unchecked")
		Collection<OfertaEstagio> lista = HibernateUtils.parseTo(q.list(), projecao, OfertaEstagio.class, "oe");		
		return lista;
	}
	
	/**
	 * Retorna todas as ofertas que possui vagas para o Curso informado.
	 * @param curso
	 * @param apenasAbertas
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<OfertaEstagio> findbyCurso(Curso curso, boolean apenasAbertas) throws HibernateException, DAOException{
		return findGeral(curso, null, null, null, null, null, null, apenasAbertas);
	}
	
	
	
}
