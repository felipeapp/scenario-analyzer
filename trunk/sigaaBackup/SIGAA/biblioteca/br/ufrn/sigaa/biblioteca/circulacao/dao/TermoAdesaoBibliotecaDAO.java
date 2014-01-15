/* Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 22/05/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TermoAdesaoSistemaBibliotecas;

/**
 * <p>DAO exclusivo para o relatório Termo de Adesão.</p>
 * 
 * @author Deyvyd
 * @version 2.0 - jadson - refactory na classe porque as consultas dela precisa retornar o termo já montado, não um array de objetos.
 */

public class TermoAdesaoBibliotecaDAO extends GenericSigaaDAO {	
	
	/**
	 * Retorna o termo de adesão que a pessoa tenha assindao
	 * 
	 * @return { nome, cpf_passaporte,
	 *           data, texto, hash_md5,
	 *           nome_unidade, matricula}
	 */
	public List<TermoAdesaoSistemaBibliotecas> findTermoAdesaoAssinadoPelaPessoa(int idPessoa)throws DAOException {
		
		StringBuilder sql = new StringBuilder(
			" SELECT t.nome_pessoa, t.cpf_passaporte,  t.data, t.texto, t.hash_md5, t.nome_unidade, t.matricula " +
			" FROM biblioteca.termo_adesao_sistema_bibliotecas t "+
			" INNER JOIN biblioteca.usuario_biblioteca ub ON ub.id_usuario_biblioteca = t.id_usuario_biblioteca " +
			" WHERE ub.id_pessoa = :idPessoa ");
		
		Query q = getSession().createSQLQuery( sql.toString() );
		
		q.setInteger("idPessoa", idPessoa);
		
		List<TermoAdesaoSistemaBibliotecas> termosAdesao = new ArrayList<TermoAdesaoSistemaBibliotecas>();
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		for (Object[] resultado : list) {
			
			TermoAdesaoSistemaBibliotecas termoAdesao = new TermoAdesaoSistemaBibliotecas();
			
			termoAdesao.setNomePessoa((String) resultado[0]);
			termoAdesao.setCpfPassaporte((String) resultado[1]);
			termoAdesao.setData( (Date) resultado[2] );
			termoAdesao.setTexto((String) resultado[3]);
			termoAdesao.setHashMd5((String) resultado[4]);
			termoAdesao.setNomeUnidade((String) resultado[5]);		

			
			if (resultado[6] != null) // se for discente ou servidor
				termoAdesao.setMatricula(Long.valueOf(resultado[6].toString()));
			
			termosAdesao.add(termoAdesao);
			
		}
		
		
		return termosAdesao;
	}
	
}
