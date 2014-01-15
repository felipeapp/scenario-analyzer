/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 19/08/2013
 *
 */
package br.ufrn.sigaa.diploma.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiplomaColetivo;

/** DAO responsável por consultas especializadas aos Registros de Diplomas Coletivos
 * @author Édipo Elder F. Melo
 *
 */
public class RegistroDiplomaColetivoDao extends GenericSigaaDAO {

	/**
	 * Busca de forma otimizada um Registro de Diploma Coletivo.
	 * @param idRegistroColetivo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public RegistroDiplomaColetivo findOtimizado(Integer idRegistroColetivo) throws HibernateException, DAOException {
		RegistroDiplomaColetivo registroColetivo = null;
		// busca informações do registro coletivo
		//Curso:Livro:Processo:	Data de Conclusão:	Data do Registro:	Data de Expedição:	
		String projecao = "r.id, r.curso.id, r.curso.nome, m.id as r.curso.municipio.id, m.nome as r.curso.municipio.nome," +
				"r.processo, r.dataColacao, r.dataRegistro, r.dataExpedicao, r.livroRegistroDiploma.id, r.livroRegistroDiploma.titulo";
		String hql = "select " + HibernateUtils.removeAliasFromProjecao(projecao) + 
				" from RegistroDiplomaColetivo r" +
				" inner join r.curso c" +
				" left join c.municipio m" +
				" where r.id = :idRegistroColetivo";
		Query q = getSession().createQuery(hql);
		q.setInteger("idRegistroColetivo", idRegistroColetivo);
		@SuppressWarnings("unchecked")
		List<RegistroDiplomaColetivo> registros = (List<RegistroDiplomaColetivo>) HibernateUtils.parseTo(q.list(), projecao, RegistroDiplomaColetivo.class, "r");
		if (!isEmpty(registros)) registroColetivo = registros.get(0);
		else return null;
		// busca as informações dos registros coletivos.
		projecao = "registro.id, registro.discente.id, registro.discente.matricula," +
				" registro.discente.pessoa.nome, registro.folha.id, registro.folha.numeroFolha," +
				" registro.numeroRegistro, registro.folha.livro.id, registro.folha.livro.titulo"; 
		hql = "select " + projecao +
				" from RegistroDiploma registro" +
				" where registro.registroDiplomaColetivo.id = :idRegistroColetivo" +
				" order by registro.numeroRegistro";
		q = getSession().createQuery(hql);
		q.setInteger("idRegistroColetivo", idRegistroColetivo);
		@SuppressWarnings("unchecked")
		Collection<RegistroDiploma> lista = HibernateUtils.parseTo(q.list(), projecao, RegistroDiploma.class, "registro");
		if (lista != null) {
			registroColetivo.setRegistrosDiplomas(new LinkedList<RegistroDiploma>());
			for (RegistroDiploma registro : lista)
				registroColetivo.getRegistrosDiplomas().add(registro);
		}
		return registroColetivo;
	}

}
