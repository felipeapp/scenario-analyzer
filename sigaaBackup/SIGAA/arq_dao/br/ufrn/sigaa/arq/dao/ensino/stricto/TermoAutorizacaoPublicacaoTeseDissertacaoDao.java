/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/05/2010
 */
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import java.util.Collection;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.TermoAutorizacaoPublicacaoTeseDissertacao;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO responsável por gerenciar o acesso a dados de Termo de Autorização de Publicação 
 * de Tese/Dissertação de Stricto Sensu.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class TermoAutorizacaoPublicacaoTeseDissertacaoDao extends GenericSigaaDAO {

	
	/**
	 * Retorna Todos os Termos de Autorização para Publicação do Orientador passado como Parâmetro.
	 * @param servidor
	 * @return
	 * @throws DAOException 
	 * @throws HibernateExceion 
	 */
	public Collection<TermoAutorizacaoPublicacaoTeseDissertacao> findByOrientador(Servidor servidor, DocenteExterno docenteExterno, Integer status) throws DAOException{
		return findGeral(servidor, docenteExterno, status, false);
	}
	
	/**
	 * Retorna o Termo de Autorização do Discente Passado Como Parâmetro.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public TermoAutorizacaoPublicacaoTeseDissertacao findByDiscente(DiscenteStricto discente) throws DAOException{
		String hql ="from TermoAutorizacaoPublicacaoTeseDissertacao tede "+
		" where tede.discente.discente.id = "+discente.getId()+
		" and tede.ativo = trueValue() ";		
		return (TermoAutorizacaoPublicacaoTeseDissertacao) getSession().createQuery(hql).setMaxResults(1).uniqueResult();		
	}
	
	/**
	 * Retorna o Termo de Autorização de acordo com a matrícula passada Como Parâmetro.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public TermoAutorizacaoPublicacaoTeseDissertacao findByMatricula(long matricula) throws DAOException{
		String hql ="from TermoAutorizacaoPublicacaoTeseDissertacao tede "+
			" where tede.discente.discente.matricula = "+matricula+
			" and tede.ativo = trueValue() ";		
		return (TermoAutorizacaoPublicacaoTeseDissertacao) getSession().createQuery(hql).setMaxResults(1).uniqueResult();
	}	
	
	/**
	 * Retorna todos os Termos Aprovados que não foram publicados.
	 * @return
	 * @throws DAOException
	 */
	public Collection<TermoAutorizacaoPublicacaoTeseDissertacao> findAllPublicacao() throws DAOException{			
		return findGeral(null, null, null, true);
	}	
	
	/**
	 * Retorna todos os Termos conforme os parâmetros informados.
	 * @param servidor
	 * @param docenteExterno
	 * @param status
	 * @param publicacao
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<TermoAutorizacaoPublicacaoTeseDissertacao> findGeral(Servidor servidor, DocenteExterno docenteExterno, Integer status, Boolean publicacao) throws HibernateException, DAOException{
		String projecao = "id, discente.discente.gestoraAcademica.id, discente.discente.gestoraAcademica.nome, criadoEm, " +
				" discente.discente.matricula, discente.discente.pessoa.nome, discente.discente.nivel, " +
				" discente.discente.pessoa.cpf_cnpj, discente.discente.pessoa.email, discente.discente.pessoa.telefone, " +
				"parcial, dataPublicacao, banca.dadosDefesa.linkArquivo, status, ativo, banca.dadosDefesa.idArquivo ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select " +projecao+" from TermoAutorizacaoPublicacaoTeseDissertacao tede ");
		hql.append(" where tede.ativo = trueValue() ");		
		
		if (servidor != null || docenteExterno != null){
			hql.append(" and exists (select oa from OrientacaoAcademica oa "+
			" where oa.discente.id = tede.discente.id and oa.tipoOrientacao = '"+OrientacaoAcademica.ORIENTADOR+"'"+
			" and oa.cancelado = falseValue() ");			
			
			if (servidor != null)
				hql.append(" and oa.servidor.id = "+servidor.getId());						
			if (docenteExterno != null)
				hql.append(" and oa.docenteExterno.id = "+docenteExterno.getId());
			
			hql.append(")");
		}

		if (!ValidatorUtil.isEmpty(status))
			hql.append(" and tede.status = "+status);
		
		if (publicacao){
			hql.append(" and tede.status <> "+TermoAutorizacaoPublicacaoTeseDissertacao.REPROVADO+
			" and (length(trim(banca.dadosDefesa.linkArquivo)) = 0 or (banca.dadosDefesa.linkArquivo is null))");						
		}		
		
		hql.append("order by discente.discente.gestoraAcademica.nome, discente.discente.nivel, discente.discente.pessoa.nome ");
		
		@SuppressWarnings("unchecked")
		Collection<TermoAutorizacaoPublicacaoTeseDissertacao> lista = HibernateUtils.parseTo(getSession().createQuery(hql.toString()).list(), projecao, TermoAutorizacaoPublicacaoTeseDissertacao.class);	
		return lista;				
	}
	
	/**
	 * Retorna o total de Teses/Dissertações Pendentes de Publicação
	 * @return
	 * @throws DAOException
	 */
	public Integer findTotalPendentesPublicacao() throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append("select coalesce(count(*),0) from TermoAutorizacaoPublicacaoTeseDissertacao tede ");
		hql.append(" where tede.ativo = trueValue() ");	
		hql.append(" and tede.status <> "+TermoAutorizacaoPublicacaoTeseDissertacao.REPROVADO+
		" and (length(trim(banca.dadosDefesa.linkArquivo)) = 0 or (banca.dadosDefesa.linkArquivo is null))");
		return ((Long) getSession().createQuery(hql.toString()).setMaxResults(1).uniqueResult()).intValue();
	}
}
