/* Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 28/05/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
*
* <p>DAO com as consultas exclusivas do caso de uso do Cadastro de Usuários no Sistema das Bibliotecas. </p>
* 
* @author Deyvyd
*
*/

public class CadastroUsuarioBibliotecaDAO extends GenericSigaaDAO{
	
	
	/**
	 * Retorna as informações do usuário selecionado para montar o Termo de Adesão em formado de impressão.
	 * 
	 * @return { lotacao_docente, siape_docente
	 * 							ou
	 *           nome_curso_discente, matricula_discente,
	 * 							ou
	 *           nome_instituicao_docente_externo }
	 */
	public Object[] findInformacoesTermoAdesao(int idUsuarioBiblioteca, VinculoUsuarioBiblioteca vinculo) throws HibernateException, DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		if (vinculo.isVinculoServidor()){
			sql = new StringBuilder(
					" SELECT u.nome AS lotacao_docente, s.siape AS siape_docente " + // Informações do Docente
					" FROM biblioteca.usuario_biblioteca b " +
					" INNER JOIN rh.servidor s ON id_servidor = b.identificacao_vinculo " +
					" INNER JOIN comum.unidade u ON u.id_unidade = s.id_unidade " +
					" WHERE b.ativo = trueValue() " +
					" AND b.id_usuario_biblioteca = :idUsuarioBiblioteca ");
		}
		else if (vinculo.isVinculoAluno()){
			sql = new StringBuilder(
					" SELECT c.nome AS nome_curso_discente, d.matricula AS matricula_discente " + // Informações do Discente
					" FROM biblioteca.usuario_biblioteca b " +
					" INNER JOIN discente d ON d.id_discente = b.identificacao_vinculo " +
					" LEFT JOIN curso c ON c.id_curso = d.id_curso " +
					" WHERE b.ativo = trueValue() " +
					" AND b.id_usuario_biblioteca = :idUsuarioBiblioteca ");
		}
		else if (vinculo == VinculoUsuarioBiblioteca.DOCENTE_EXTERNO){
			sql = new StringBuilder(
					" SELECT i.nome AS nome_instituicao_docente_externo, o.id_docente_externo " + // Informações do Docente Externo
					" FROM biblioteca.usuario_biblioteca b " +
					" INNER JOIN ensino.docente_externo o ON o.id_docente_externo = b.identificacao_vinculo " +
					" INNER JOIN comum.instituicoes_ensino i ON i.id = o.id_instituicao " +
					" WHERE b.ativo = trueValue() " +
					" AND b.id_usuario_biblioteca = :idUsuarioBiblioteca ");
		}
		
		
		Query q = getSession().createSQLQuery( sql.toString() );
		
		q.setInteger("idUsuarioBiblioteca", idUsuarioBiblioteca);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		Object[] resultado = list.get(0);
		
		return resultado;
	}
}
