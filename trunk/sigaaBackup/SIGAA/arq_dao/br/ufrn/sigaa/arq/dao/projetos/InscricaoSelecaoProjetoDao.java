package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.InscricaoSelecaoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoDiscenteProjeto;


/*******************************************************************************
 * Dao para realizar consultas sobre a lista de interessados em participar de
 * ações de extensão
 * 
 * @author geyson
 * 
 ******************************************************************************/
public class InscricaoSelecaoProjetoDao extends GenericSigaaDAO {

	private static final long LIMITE_RESULTADOS = 1000;
	
	/**
	 * retorna uma InscricaoSelecaoProjeto do discente e projeto passado por
	 * parâmetro
	 * 
	 * @param discente
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	public InscricaoSelecaoProjeto findByDiscenteAtividade(int idDiscente,
			int idProjeto) throws DAOException {

		String hql = "select insc from InscricaoSelecaoProjeto insc where "
				+ "insc.discente.id = :idDiscente "
				+ "and insc.projeto.id = :idProjeto";

		Query q = getSession().createQuery(hql);
		q.setInteger("idDiscente", idDiscente);
		q.setInteger("idProjeto", idProjeto);

		return (InscricaoSelecaoProjeto) q.uniqueResult();

	}
	
	/**
	 * Lista todos os discentes inscritos para seleção que ainda não foram
	 * selecionados
	 * 
	 * @param idProjeto
	 * @return
	 */
	public Collection<InscricaoSelecaoProjeto> findInscritosProcessoSeletivoByProjeto(
			int idProjeto) throws DAOException {

		String hql = "select i.id, p.id, p.nome, d.id, d.matricula from InscricaoSelecaoProjeto i "
				+ "inner join i.projeto pro "
				+ "inner join i.situacaoDiscenteProjeto s "
				+ "inner join i.discente d "
				+ "inner join d.pessoa p "
				+ " where pro.id = :idProjeto and s.id = :idAguardandoSelecao ";

		Query q = getSession().createQuery(hql);
		q.setInteger("idProjeto", idProjeto);
		q.setInteger("idAguardandoSelecao",
				TipoSituacaoDiscenteProjeto.INSCRITO_PROCESSO_SELETIVO);
		
		@SuppressWarnings("rawtypes")
		List lista = q.list();

		Collection<InscricaoSelecaoProjeto> result = new ArrayList<InscricaoSelecaoProjeto>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);

			InscricaoSelecaoProjeto i = new InscricaoSelecaoProjeto();
			i.setId((Integer) colunas[col++]);

			Pessoa p = new Pessoa();
			p.setId((Integer) colunas[col++]);
			p.setNome((String) colunas[col++]);

			Discente d = new Discente();
			d.setPessoa(p);
			d.setId((Integer) colunas[col++]);
			d.setMatricula((Long) colunas[col++]);

			i.setDiscente(d);
			result.add(i);
		}

		return result;
	}
	
	
	
}
