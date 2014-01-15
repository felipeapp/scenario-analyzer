/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/10/2009
 *
 */
package br.ufrn.sigaa.arq.dao.rh;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.rh.dominio.Designacao;

/**
 * DAO para efetuar busca por designações que um Servidor possui. Utiliza a entidade 
 * {@link br.ufrn.sigaa.rh.dominio.Designacao}
 * 
 * @author agostinho campos
 *
 */
public class DesignacaoDAO extends GenericSigaaDAO {

	/**
	 * Busca todas as designações do servidor (br.ufrn.sigaa.rh.dominio.Designacao)
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<br.ufrn.sigaa.rh.dominio.Designacao> findAllDesignacoesByServidor(int idServidor) throws HibernateException, DAOException {
		StringBuilder sb = new StringBuilder();
		sb.append("select d from br.ufrn.sigaa.rh.dominio.Designacao d where d.servidorSigaa.id = ? ");
		
		Query q = getSession().createQuery(sb.toString()).setInteger(0, idServidor);
		List<Designacao> designacoesDocente = q.list();

		Set<Integer> listasCodigoAtividade = new LinkedHashSet<Integer>();
		ArrayList<Designacao> listaSemDup = new ArrayList<Designacao>();
		
		for (Designacao designacao : designacoesDocente) {
			if (designacao.getFim() != null) { // se a dataFim está setada, tem que verificar sua validade.
				if ( CalendarUtils.isDentroPeriodo(designacao.getInicio(), designacao.getFim()) )
					// vários docentes tem designações com idAtividade repetida, necessário 
					// para não repetir várias vezes as mesmas designações do docente
					listasCodigoAtividade.add(designacao.getAtividade().getId());
			}
			else // se as designações tem dataFim null é porque está ativa.
				listasCodigoAtividade.add(designacao.getAtividade().getId());
		}
		
		for (Designacao designacao : designacoesDocente) {
			if (designacao.getFim() != null) {
				if ( CalendarUtils.isDentroPeriodo(designacao.getInicio(), designacao.getFim()) )
					if ( listasCodigoAtividade.contains( designacao.getAtividade().getId() ) ) {
						listaSemDup.add(designacao);
						listasCodigoAtividade.remove( designacao.getAtividade().getId() );
					}
			}
			else {
				if ( listasCodigoAtividade.contains( designacao.getAtividade().getId() ) ) {
					listaSemDup.add(designacao);
					listasCodigoAtividade.remove( designacao.getAtividade().getId() );
				}
			}
		}
		
		return listaSemDup;
	}
	
	/**
	 * Busca todas as designações que o servidor teve num ano e período (br.ufrn.sigaa.rh.dominio.Designacao)
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public List<br.ufrn.sigaa.rh.dominio.Designacao> findAllDesignacoesByServidorAnoPeriodo(int idServidor, int ano, int periodo) throws HibernateException, DAOException {
		
		StringBuilder sb = new StringBuilder();
		sb.append("select d from br.ufrn.sigaa.rh.dominio.Designacao d where d.servidorSigaa.id = ? ");
		
		Query q = getSession().createQuery(sb.toString()).setInteger(0, idServidor);
		List<Designacao> designacoesDocente = q.list();

		Set<Integer> listasCodigoAtividade = new LinkedHashSet<Integer>();
		ArrayList<Designacao> listaSemDup = new ArrayList<Designacao>();	
		
		for (Designacao designacao : designacoesDocente) {
						
			boolean dentroPeriodo = dentroPeriodo(ano, periodo, designacao);

			if (dentroPeriodo)
				listasCodigoAtividade.add(designacao.getAtividade().getId());

		}
		
		for (Designacao designacao : designacoesDocente) {
					
			boolean dentroPeriodo = dentroPeriodo(ano, periodo, designacao);
			
			if (dentroPeriodo)
				if ( listasCodigoAtividade.contains( designacao.getAtividade().getId() ) ) {
					listaSemDup.add(designacao);
					listasCodigoAtividade.remove( designacao.getAtividade().getId() );
				}

		}
		
		return listaSemDup;
	}

	private boolean dentroPeriodo(int ano, int periodo, Designacao designacao) {
		Calendar cal = Calendar.getInstance();
		
		if (designacao.getFim() != null)
			cal.setTime(designacao.getFim());
		else
			cal.setTime(new Date());

		int anoFimDesignacao = cal.get(Calendar.YEAR);
		int mesFimDesignacao = cal.get(Calendar.MONTH);
		int periodoFimDesignacao = (( mesFimDesignacao < 6 ) ? 1 :  2);
		
		if (designacao.getInicio() != null)
			cal.setTime(designacao.getInicio());
		else
			cal.setTime(new Date());
		
		int anoInicioDesignacao = cal.get(Calendar.YEAR);
		int mesInicioDesignacao = cal.get(Calendar.MONTH);
		int periodoInicioDesignacao = (( mesInicioDesignacao < 6 ) ? 1 :  2);
		
		boolean dentroPeriodo = false;

		if ((anoFimDesignacao > ano || (anoFimDesignacao == ano && periodoFimDesignacao >= periodo)) &&
			(anoInicioDesignacao < ano || (anoInicioDesignacao == ano && periodoInicioDesignacao <= periodo))
		   )
			dentroPeriodo = true;
		return dentroPeriodo;
	}
	
	/**
	 * Retorna todos os servidores com as designações de diretor e chefe das unidades 
	 * @param atividades
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Designacao>  findChefesDiretoresByAtividade(List<Integer> atividades) throws DAOException {
			
		StringBuilder hql = new StringBuilder(" SELECT s.id, u.id, u.nome, c.id, c.denominacao, p.id, p.nome, ");
		hql.append(" us.id, us.ramal, us.email, g.id, g.nome, g.sigla ");
		hql.append(" FROM Usuario us,Designacao d INNER JOIN d.servidorSigaa s INNER JOIN s.pessoa p INNER JOIN d.atividade a ");
		hql.append(" INNER JOIN s.cargo c INNER JOIN d.unidadeDesignacao u INNER JOIN u.unidadeResponsavel g ");
		hql.append(" WHERE s.ativo = 1 AND us.inativo = falseValue() AND d.gerencia = 'T' ");
		hql.append(" AND a.codigoRH IN " + UFRNUtils.gerarStringIn(atividades));
		
		if(atividades.containsAll(AtividadeServidor.CHEFE_DEPARTAMENTO))
			hql.append(" AND u.tipoAcademica = " + TipoUnidadeAcademica.DEPARTAMENTO ); 
		else if(atividades.containsAll(AtividadeServidor.DIRETOR_UNIDADE) && atividades.containsAll(AtividadeServidor.DIRETOR_CENTRO))
			hql.append(" AND u.tipoAcademica IN (" + TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA + "," + TipoUnidadeAcademica.CENTRO + ")" );
		
		hql.append(" AND us.pessoa.id = p.id  AND (d.fim IS NULL OR d.fim >= NOW()) ");
		hql.append(" ORDER BY g.nome , u.nome, p.nome ");

		Query q = getSession().createQuery( hql.toString() );
		List<Object[]> lista = q.list();
		
		Set<Designacao> designacoes = new LinkedHashSet<Designacao>();
		for (Object[] obj : lista) {
			
			Servidor s = new Servidor();
			s.setId((Integer) obj[0]);
			s.setCargo(new Cargo((Integer) obj[3]));
			s.getCargo().setDenominacao((String) obj[4]);	
			s.setPessoa(new Pessoa((Integer) obj[5], (String) obj[6]));
			s.setPrimeiroUsuario(new Usuario((Integer) obj[7]));
			s.getPrimeiroUsuario().setRamal((String) obj[8]);
			s.getPrimeiroUsuario().setEmail((String) obj[9]);
			
			Designacao d = new Designacao();
			d.setServidorSigaa(s);
			d.setUnidadeDesignacao(new Unidade((Integer) obj[1], null, (String) obj[2], null));
			d.getUnidadeDesignacao().setGestoraAcademica(new Unidade((Integer) obj[10], null, (String) obj[11], (String) obj[12]));
			designacoes.add(d);
		}	
		
		return designacoes;
		
	}
	
	
}
