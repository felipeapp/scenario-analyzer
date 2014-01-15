/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 14/10/2010
 * 
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.NotaCirculacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 *
 * <p> Dao para consultas em notas de circulação </p>
 * 
 * @author jadson
 *
 */
public class NotaCirculacaoDao  extends GenericSigaaDAO{

	
	/**
	 * <p> Retorna todos os materiais com notas ativas na circulação para a biblioteca específica onde o usuário 
	 * tem permissão de circulação, caso não seja passado a unidade busca todos os materiais com notas ativas </p>
	 * 
	 *  <p> Observação: Esse método retorna todas as informações necessárias do material para ser usadas no cado de uso de incluir nota de circulação
	 *  , então cuidado ao alterar as projeções do select </p>
	 * 
	 * 
	 * @return  o id e código de barras e aa biblioteca dos materiais que possuem alguma nota de circulação
	 * @throws DAOException
	 */
	public List <NotaCirculacao> findAllMateriaisComNotasByBibliotecas(List<Integer> idUnidades) throws DAOException {
		
		List <NotaCirculacao> notas = new ArrayList <NotaCirculacao> ();
		
		// usando sql para trazer saber se o material é exemplar ou fascículo
		
		String sql = " SELECT material.id_material_informacional, material.codigo_barras, material.ativo, biblioteca.id_biblioteca, biblioteca.descricao, unidadeBiblioteca.id_unidade "
			+" , situcao.id_situacao_material_informacional, situacao.situacao_disponivel, situacao.situacao_emprestado, situacao.situacao_de_baixa "
			+" , notaCirculacao.id_nota_circulacao, notaCirculacao.nota, notaCirculacao.bloquear_material "
			+" , notaCirculacao.mostrar_emprestimo, notaCirculacao.mostrar_renovacao, notaCirculacao.mostrar_devolucao, exemplar.id_exemplar "
			+" FROM biblioteca.nota_circulacao notaCirculacao "
			+" INNER JOIN biblioteca.material_informacional material on notaCirculacao.id_material_informacional = material.id_material_informacional "
			+" INNER JOIN biblioteca.biblioteca biblioteca on material.id_biblioteca = biblioteca.id_biblioteca "
			+" INNER JOIN comum.unidade unidadeBiblioteca on unidadeBiblioteca.id_unidade = biblioteca.id_unidade "
			+" INNER JOIN biblioteca.situacao_material_informacional situcao on situcao.id_situacao_material_informacional = material.id_situacao_material_informacional "
			+" INNER JOIN comum.unidade unidade on biblioteca.id_unidade = unidade.id_unidade "
			+" INNER JOIN biblioteca.situacao_material_informacional situacao on situacao.id_situacao_material_informacional = material.id_situacao_material_informacional "
			+" LEFT JOIN biblioteca.exemplar exemplar on exemplar.id_exemplar = material.id_material_informacional "
			+" WHERE material.ativo = trueValue() AND situacao.situacao_de_baixa = falseValue() AND ( notaCirculacao.bloquear_material = trueValue() OR notaCirculacao.mostrar_emprestimo = trueValue() OR notaCirculacao.mostrar_renovacao = trueValue() OR notaCirculacao.mostrar_devolucao = trueValue() ) ";
		
		if(idUnidades != null && idUnidades.size() > 0){
			sql += " AND unidade.id_unidade in ( :idsUnidades ) ";
		}
		
		Query q = getSession().createSQLQuery(sql);
		if(idUnidades != null && idUnidades.size() > 0){
			q.setParameterList("idsUnidades", idUnidades);
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> objetos = q.list();
		
		for (Object[] objects : objetos) {
			
			int contador = 0;
			
			NotaCirculacao nota = new NotaCirculacao( (Integer) objects[contador++],
			                                         (String)   objects[contador++],
			                                         (Boolean)   objects[contador++],
			                                         (Integer)  objects[contador++],
			                                         (String)   objects[contador++],
			                                         (Integer)  objects[contador++],
			                                         (Integer)   objects[contador++],
			                                         (Boolean)   objects[contador++],
			                                         (Boolean)   objects[contador++],
			                                         (Boolean)   objects[contador++],
			                                         (Integer)  objects[contador++],
			                                         (String)  objects[contador++],
			                                         (Boolean) objects[contador++],
			                                         (Boolean) objects[contador++],
			                                         (Boolean) objects[contador++],
			                                         (Boolean) objects[contador++],
			                                         objects[contador++] != null ? true: false);
			
			notas.add(nota);
		}
		
		
		return notas;
	}
	
	
	

	
	/**
	 *  Método que retorna as notas de circulação do material, utilizado nas operações de circulação para mostrar ao usuário
	 */
	public NotaCirculacao getNotaAtivaDoMaterial(int idMaterial) throws DAOException{
		List<NotaCirculacao> lista = getNotasAtivasDoMaterial(idMaterial);
		
		if (lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		
		return null;
	}
	
	/**
	 *  Método que retorna as notas de circulação do material, utilizado nas operações de circulação para mostrar ao usuário
	 */
	public List<NotaCirculacao> getNotasAtivasDoMaterial(int idMaterial) throws DAOException{
		
		String hql = new String( " SELECT nota.id, nota.nota, nota.bloquearMaterial, nota.mostrarEmprestimo, nota.mostrarRenovacao, nota.mostrarDevolucao "
				+" FROM NotaCirculacao nota "
				+" WHERE nota.material.id = :idMaterial "
				+" AND ( nota.bloquearMaterial = trueValue() OR nota.mostrarEmprestimo = trueValue() OR nota.mostrarRenovacao = trueValue() OR nota.mostrarDevolucao = trueValue() ) ");
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idMaterial", idMaterial);
		
		List<NotaCirculacao> lista = new ArrayList<NotaCirculacao>();
		
		@SuppressWarnings("unchecked")
		List<Object[]> objetos = q.list();
		
		for (Object[] objects : objetos) {
			
			int contador = 0;
			
			NotaCirculacao nota = new NotaCirculacao( (Integer) objects[contador++],
			                                         (String)   objects[contador++],  
			                                         (Boolean) objects[contador++],
			                                         (Boolean) objects[contador++],
			                                         (Boolean) objects[contador++],
			                                         (Boolean) objects[contador++]);
			
			lista.add(nota);
		}
		
		return lista;
	}
		
	
	
	
	
	
	/**
	 *  Método que verifica se existe alguma nova de circulação ativa para o material passado
	 */
	public boolean existeNotaCirculacaoAtivasDoMaterial(int idMaterial) throws DAOException{
		
		String hql = new String( " SELECT count(nota) FROM NotaCirculacao nota "
				+" WHERE nota.material.id = :idMaterial "
				+" AND ( nota.bloquearMaterial = trueValue() OR nota.mostrarEmprestimo = trueValue() OR nota.mostrarRenovacao = trueValue() OR nota.mostrarDevolucao = trueValue() ) ");
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idMaterial", idMaterial);
		
		if ( (Long) q.uniqueResult() > 0 )
			return true;
		else
			return false;
	}





	/**
	 *  Método que retorna as notas de circulação dos materiais.
	 */
	public List<NotaCirculacao> findAtivaByMaterial(List<MaterialInformacional> materiais) throws DAOException {

		String hqlProjecao = "nota.id, nota.nota, nota.bloquearMaterial, nota.mostrarEmprestimo, nota.mostrarRenovacao, nota.mostrarDevolucao, nota.material.id";
		String hql = new String( " SELECT " + hqlProjecao
				+" FROM NotaCirculacao nota "
				+" WHERE nota.material IN " + UFRNUtils.gerarStringIn(materiais)
				+" AND ( nota.bloquearMaterial = trueValue() OR nota.mostrarEmprestimo = trueValue() OR nota.mostrarRenovacao = trueValue() OR nota.mostrarDevolucao = trueValue() ) ");
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		
		List<NotaCirculacao> notas = new ArrayList<NotaCirculacao>();
		NotaCirculacao nota = null;
		int coluna = 0;
		int idMaterial = 0;
		
		for (Object[] linha : result) {
			nota = new NotaCirculacao();

			nota.setId((Integer) linha[coluna++]);
			nota.setNota((String) linha[coluna++]);
			nota.setBloquearMaterial((Boolean) linha[coluna++]);
			nota.setMostrarEmprestimo((Boolean) linha[coluna++]);
			nota.setMostrarRenovacao((Boolean) linha[coluna++]);
			nota.setMostrarDevolucao((Boolean) linha[coluna++]);
			
			idMaterial = (Integer) linha[coluna++];

			for (MaterialInformacional material : materiais) {
				if (material.getId() == idMaterial) {
					nota.setMaterial(material);
					
					break;
				}
			}
			
			notas.add(nota);
			
			coluna = 0;
		}
		
		return notas;
		
	}

	/**
	 * Indica se uma nota é bloqueante a partir do seu id.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public boolean isNotaBloqueanteById(int id) throws DAOException {
		String hql = "SELECT nota.bloquearMaterial FROM NotaCirculacao nota WHERE id = :id";
		
		Query q = getSession().createQuery(hql);
		
		q.setParameter("id", id);
		
		return (Boolean) q.uniqueResult();
	}
	
}
