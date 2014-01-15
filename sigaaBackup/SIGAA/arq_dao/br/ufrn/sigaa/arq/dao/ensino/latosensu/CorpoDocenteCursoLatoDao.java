package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO para realizar consultas do Corpo Docente dos Curso de Lato Sensu.
 * 
 * @author guerethes
 */
public class CorpoDocenteCursoLatoDao extends GenericSigaaDAO {

	/**
	 * Retorna todo o corpo docente do Curso Lato Sensu passado como parâmetro.
	 * 
	 * @param cursoLato
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CorpoDocenteCursoLato> findByAllDocenteCurso(Curso cursoLato) throws DAOException {
		StringBuilder hql = new StringBuilder("from CorpoDocenteCursoLato cdcl where cdcl.cursoLato.id = :cursoLato ");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("cursoLato", cursoLato.getId());
		
		Collection<CorpoDocenteCursoLato> coordDocente = new ArrayList<CorpoDocenteCursoLato>();
		Collection<CorpoDocenteCursoLato> result = q.list();
		
		if (result != null) {
			for ( CorpoDocenteCursoLato linha : result ) {
				if (ValidatorUtil.isNotEmpty(linha.getDocenteExterno()) && ValidatorUtil.isNotEmpty(linha.getDocenteExterno().getIdPerfil()))
					linha.setLinkCurriculoLattes( PerfilPessoaDAO.getDao().get(linha.getDocenteExterno().getIdPerfil()).getEnderecoLattes() );
				else if (ValidatorUtil.isNotEmpty(linha.getServidor()) && ValidatorUtil.isNotEmpty(linha.getServidor().getIdPerfil())) {
					PerfilPessoa perfil = PerfilPessoaDAO.getDao().get(linha.getServidor().getIdPerfil());
					linha.setLinkCurriculoLattes( perfil != null ? perfil.getEnderecoLattes() : null );
				} else
					linha.setLinkCurriculoLattes(null);
				
				coordDocente.add(linha);
			}
		}
		
		return coordDocente;
	}
	
	/**
	 * O corpo docente do Curso Lato Sensu e Servidor passado como parâmetro.
	 * 
	 * @param cursoLato
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public CorpoDocenteCursoLato findByDocenteCurso(CursoLato cursoLato, Integer servidor) throws DAOException {
		String hql = "from CorpoDocenteCursoLato cdcl where cdcl.cursoLato.id = :cursoLato " +
				"and cdcl.servidor.id = :servidor";
		Query q = getSession().createQuery(hql);
		q.setInteger("cursoLato", cursoLato.getId());
		q.setInteger("servidor", servidor);
		q.setMaxResults(1);
		return (CorpoDocenteCursoLato) q.uniqueResult();
	}

	/**
	 * Verificação para ver se o docente já faz parte do Corpo Docente do Curso Lato. 
	 * 
	 * @param docente
	 * @param cursoLato
	 * @return
	 * @throws DAOException
	 */
	public boolean validarDocenteDisciplina(Servidor docente, CursoLato cursoLato) throws DAOException{
		Query q = getSession().createQuery("from CorpoDocenteCursoLato cdcl " +
				" where cdcl.servidor = :docente and cdcl.cursoLato = :cursoLato");
		q.setInteger("cursoLato", cursoLato.getId());
		q.setInteger("docente", docente.getId());
		if (q.list().isEmpty())
			return false;
		return true;
	}
	
	/**
	 * Retorna os doscente de um determinado Curso Lato Sensu
	 * 
	 * @param docente
	 * @param cursoLato
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> findServidorCurso(String nomeDocente, Curso curso) throws DAOException{
		Query q = getSession().createQuery("SELECT cdcl.servidor.id, cdcl.servidor.pessoa.id, cdcl.servidor.pessoa.nome, cdcl.servidor.ativo.descricao  " +
				" FROM CorpoDocenteCursoLato cdcl " +
				" INNER JOIN cdcl.servidor " +
				" INNER JOIN cdcl.servidor.ativo " +
				" where cdcl.cursoLato = :cursoLato and " + 
				UFRNUtils.toAsciiUpperUTF8("cdcl.servidor.pessoa.nome") + " like " +
				UFRNUtils.toAsciiUTF8("'" + nomeDocente.toUpperCase() + "%'"));
		
		q.setInteger("cursoLato", curso.getId());
		
		Collection<Servidor> lista = new ArrayList<Servidor>();
		@SuppressWarnings("unchecked")
		List<Object[]> l = q.list();
		
		for(Object[] obj : l){
			int col = 0;
			
			Servidor s = new Servidor();
			s.setId((Integer) obj[col++]);
			s.getPessoa().setId((Integer) obj[col++]);
			s.getPessoa().setNome((String) obj[col++]);
			
			Ativo a = new Ativo();
			a.setDescricao((String) obj[col++]);
			s.setAtivo(a);
			
			lista.add(s);
		}

		return lista;
	}
	
}