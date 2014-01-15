package br.ufrn.sigaa.ava.questionario.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.questionarios.dominio.CategoriaPerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.PerguntaQuestionarioTurma;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO que realiza as consultas referentes as categorias de perguntas da turma virtual. 
 * 
 * @author Fred_Castro
 *
 */
public class CategoriaPerguntaQuestionarioTurmaDao extends GenericSigaaDAO {
	
	/**
	 * Retorna todas as categorias de uma turma com suas perguntas.
	 * 
	 * @param idCategoria
	 * @return
	 * @throws DAOException
	 */
	public ArrayList <CategoriaPerguntaQuestionarioTurma> findCategoriasByDono  (int idDono) throws DAOException{
		
		Query q = getSession().createQuery("select distinct c from CategoriaPerguntaQuestionarioTurma c " +
											" join fetch c.perguntas p " +
											" where c.ativo = trueValue() and p.ativo = trueValue() and c.dono.id = " + idDono + 
											" order by c.nome , p.nome");
		
		@SuppressWarnings("unchecked")
		ArrayList <CategoriaPerguntaQuestionarioTurma> result = (ArrayList<CategoriaPerguntaQuestionarioTurma>) q.list();
		
		return result;
	}
	
	/**
	 * Retorna todas as categorias de uma turma com suas perguntas.
	 * 
	 * @param idCategoria
	 * @return
	 * @throws DAOException
	 */
	public ArrayList <CategoriaPerguntaQuestionarioTurma> findCategoriasByCompartilhamento  (int idPessoaCompartilhamento) throws DAOException{
		
		Query q = getSession().createQuery("select distinct c from CategoriaPerguntaQuestionarioTurma c " +
											" join fetch c.perguntas p " +
											" join c.pessoasComCompartilhamento pc " +
											" where c.ativo = trueValue() and p.ativo = trueValue() and pc.id = " + idPessoaCompartilhamento + 
											" order by c.nome , p.nome");
		
		@SuppressWarnings("unchecked")
		ArrayList <CategoriaPerguntaQuestionarioTurma> result = (ArrayList<CategoriaPerguntaQuestionarioTurma>) q.list();
		
		return result;
	}
	
	/**
	 * Busca por nome todos os docentes externos ou servidores ativos com usuário ativo cadastrado no sistema.
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public List <Object []> buscarDocentesPorNome (String nome) throws DAOException {
		@SuppressWarnings("unchecked")
		List <Object []> rs = getSession().createSQLQuery(
				"select distinct on (p.nome, p.id_pessoa) p.id_pessoa, p.nome, u.login, case when s.id_servidor is not null then 1 else 2 end as tipo_usuario " +
				"from comum.pessoa p " +
				"join comum.usuario u using (id_pessoa) " +
				"left join rh.servidor s using (id_pessoa) " +
				"left join ensino.docente_externo d using (id_pessoa) " +
				"where p.nome_ascii like :nome and (s.id_servidor is not null and s.id_ativo = 1 or d.id_docente_externo is not null) " +
				"and u.inativo = false " +
				"order by p.nome"
		).setString("nome", "%" +  StringUtils.toAsciiAndUpperCase(nome) + "%").list();
		
		return rs;
	}
	
	/**
	 * Lista todas as categorias compartilhadas com o docente.
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public List <CategoriaPerguntaQuestionarioTurma> listarCategoriasCompartilhadas (int idPessoa) throws DAOException{
		@SuppressWarnings("unchecked")
		List <Object []> cs = getSession().createSQLQuery("select c.id_categoria_pergunta_questionario_turma, c.nome, u.id_usuario, p.id_pessoa, p.nome as nome_dono from ava.categoria_questao_compartilhamento comp join ava.categoria_pergunta_questionario_turma c using (id_categoria_pergunta_questionario_turma) join comum.usuario u on u.id_usuario = c.id_dono join comum.pessoa p on p.id_pessoa = u.id_pessoa where comp.id_pessoa = " + idPessoa + " order by c.nome").list();
		List <CategoriaPerguntaQuestionarioTurma> rs = new ArrayList<CategoriaPerguntaQuestionarioTurma>();
		
		for (Object [] c : cs){
			CategoriaPerguntaQuestionarioTurma categoria = new CategoriaPerguntaQuestionarioTurma();
			categoria.setId((Integer) c[0]);
			categoria.setNome((String) c[1]);
			categoria.setDono(new Usuario((Integer) c[2]));
			categoria.getDono().setPessoa(new Pessoa((Integer) c[3]));
			categoria.getDono().getPessoa().setNome((String) c[4]);
			
			@SuppressWarnings("unchecked")
			List <Object []> ps = getSession().createSQLQuery("select p.id_pergunta_questionario_turma, p.nome, p.tipo from ava.pergunta_questionario_turma p where p.id_categoria = " + c[0]).list();
			categoria.setPerguntas(new ArrayList <PerguntaQuestionarioTurma> ());
			
			for (Object [] p : ps){
				PerguntaQuestionarioTurma pergunta = new PerguntaQuestionarioTurma ();
				pergunta.setId((Integer) p[0]);
				pergunta.setNome((String) p[1]);
				pergunta.setTipo((Integer) p[2]);
				pergunta.setCategoria(categoria);
				
				categoria.getPerguntas().add(pergunta);
			}
			
			rs.add(categoria);
		}
		
		return rs;
	}
}
