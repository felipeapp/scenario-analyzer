/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Criação: 21/05/2009
 */
package br.ufrn.comum.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.TelaAvisoLogon;
import br.ufrn.comum.dominio.notificacoes.GrupoDestinatarios;

/**
 * DAO para buscas de telas de aviso do logon.
 * 
 * @author David Pereira
 *
 */
@SuppressWarnings("unchecked")
public class TelaAvisoLogonDao extends GenericSharedDBDao {

	/**
	 * Retorna uma lista de telas de aviso que devem ser mostradas para o usuário
	 * em questão.
	 * 
	 * @param idUsuario
	 * @param sistema
	 * @return
	 * @throws DAOException 
	 */
	public List<TelaAvisoLogon> findTelasAtivas(int idUsuario, int sistema) throws DAOException {
		Criteria criteria = getSession().createCriteria(TelaAvisoLogon.class);
		criteria.add(Restrictions.eq("ativo", true));
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.le("inicio", new Date()), Restrictions.ge("fim", new Date())), Restrictions.and(Restrictions.le("inicio", new Date()), Restrictions.isNull("fim"))));
		criteria.createCriteria("sistemas").add(Restrictions.eq("id", sistema));
		criteria.setFetchMode("destinatarios", FetchMode.JOIN);
		List<TelaAvisoLogon> telas = criteria.list();
		List<TelaAvisoLogon> result = new ArrayList<TelaAvisoLogon>();
		if (!isEmpty(telas)) {
			for (TelaAvisoLogon tela : telas) {
				for (GrupoDestinatarios grupo : tela.getDestinatarios()) {
					List<Map<String, Object>> usuarios = new JdbcTemplate(getDataSource(sistema)).queryForList(grupo.getSqlDestinatarios());
					for (Map<String, Object> usr : usuarios) {
						Integer id = (Integer) usr.get("id_usuario");
						if (idUsuario == id && !result.contains(tela)) {
							result.add(tela);
							break;
						}
					}
				}
			}
		}
		
		return result;
	}

}
