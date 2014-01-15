package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Esta interface deve ser implementada por todas as classes que implementam filtros de docente
 * para serem exibidos na tela de geração do relatório de produtividade dos docentes.
 * @author Victor Hugo
 *
 */
public interface FiltroDocente {

	/**
	 * Método que deve executar a busca de docentes e retornar para o bean
	 * @param editaisSelecionados
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> getDocentes(List<Integer> editaisSelecionados) throws DAOException;
	
}
