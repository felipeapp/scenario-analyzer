/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '14/11/2011'
 *
 */
package br.ufrn.sigaa.mobile.touch.dao;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe responsável por por consultas específicas de Turma Virtual para dispositivos móveis
 * sensíveis ao toque.
 * 
 * @author David Pereira
 * @author ilueny santos
 *
 */
public class TurmaVirtualTouchDao  extends GenericSigaaDAO {
	

	/**
	 * Retorna as configurações que determinada turma possui 
	 * @param turma
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public ConfiguracoesAva findConfiguracoes(Turma turma) throws DAOException {
		String projecao = "id, permiteVisualizacaoExterna, turma.id, permiteAlunoCriarForum, " +
				"permiteAlunoCriarEnquete, tipoVisualizacaoNota, mostrarMediaDaTurma, mostrarEstatisticaNotas, " +
				"tempoTolerancia, ocultarNotas ";
		
		String hql = "SELECT " + projecao + " FROM ConfiguracoesAva WHERE turma.id = :idTurma ";
		Query q = getSession().createQuery(hql);
		q.setMaxResults(1);
		if (turma.getTurmaAgrupadora() != null) {
			q.setInteger("idTurma", turma.getTurmaAgrupadora().getId());
		}else {
			q.setInteger("idTurma", turma.getId());
		}			
		
		Collection<ConfiguracoesAva> conf = HibernateUtils.parseTo(q.list(), projecao, ConfiguracoesAva.class);
		if (ValidatorUtil.isNotEmpty(conf)) {
			return conf.iterator().next();
		}else{
			return null;
		}
	}
	
}
