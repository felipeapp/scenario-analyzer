package br.ufrn.sigaa.assistencia.negocio;

import java.util.List;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.sigaa.assistencia.cadunico.dominio.QuantidadeItemConfortoCadastroUnico;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.dominio.Resposta;

/**
 * Classe auxiliar para geração da pontuação do índice de carência do aluno
 * 
 * @author gleydson
 *
 */
public class PontuacaoCadastroUnicoHelper {

	/**
	 * Método que gera a quantidade de ítens de conforto
	 *  
	 * @param questionario
	 * @param itensConforto
	 * @return
	 */
	public static int geraPontuacao(QuestionarioRespostas questionario, List<QuantidadeItemConfortoCadastroUnico> itensConforto ) {
		
		/* Soma dos ítens do questionário */
		int somaPerguntas = 0;
		
		for (Resposta r : questionario.getRespostas()) {
			if ( r.getAlternativa() != null && r.getAlternativa().getPeso() != null)
				somaPerguntas+=r.getAlternativa().getPeso();
		}
		
		/* Soma dos ítens dos ítens de conforto */
		
		int somaItensConforto = 0;
		
		JdbcTemplate template = new JdbcTemplate(Database.getInstance().getSigaaDs());
		
		for ( QuantidadeItemConfortoCadastroUnico item : itensConforto ) {
			int pontos = 0;
			
			if (item.getQuantidade() > 0)
				pontos = template.queryForInt("select pontos from sae.item_conforto_pontuacao where id_item_conforto = " + item.getItem().getId() + " and quantidade = " + item.getQuantidade() );
				
			somaItensConforto += pontos;
		}
		
		return somaPerguntas + somaItensConforto;
		
		
	}
	
}
