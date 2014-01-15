/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 30/07/2010
 *
 */
package br.ufrn.sigaa.ava.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AtividadeAvaliavel;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe auxiliar para Avalia��es.
 * 
 * @author Diego J�come
 *
 */
public class AvaliacaoHelper {

	/**
	 * Valida o cadastro de uma avalia��o a partir de uma atividade avali�vel. 
	 * 
	 * @param atividade
	 * @param turma
	 * @param erros
	 * @param cadastrar - Se a atividade est� sendo cadastrada ou alterada
	 * 
	 * @throws DAOException 
	 */
	public static void validarAvaliacao(AtividadeAvaliavel atividade, Turma turma , ListaMensagens erros, boolean cadastrar) throws DAOException {
		if (atividade.isPossuiNota() && !isEmpty(atividade.getUnidade()) )  {
			
			AvaliacaoDao aDao = null;
			TurmaVirtualDao tDao = null;
			
			try{
			
				aDao = getDAO(AvaliacaoDao.class);
				tDao = getDAO(TurmaVirtualDao.class);
				ConfiguracoesAva config = tDao.findConfiguracoes(turma);
				
				List<Avaliacao> avaliacoes = aDao.findAvaliacaoByAtividade(atividade.getId());
				
				String titulo = null;
				String abrev = null;
				boolean possuiAvaliacao = false; 
				
				if (avaliacoes != null && !avaliacoes.isEmpty()){
					possuiAvaliacao = true;
					titulo = avaliacoes.get(0).getDenominacao();
					abrev = avaliacoes.get(0).getAbreviacao();
				}
				
				validateMaxLength(atividade.getTitulo(),100,"T�tulo",erros);
				
				boolean nomeAvaliacaoDisponivel = aDao.isNomeAvaliacaoDisponivel(turma, atividade.getTitulo(),atividade.getAbreviacao());
				
				// Cadastro de uma nova avalia��o
				if (!possuiAvaliacao && !nomeAvaliacaoDisponivel)
					erros.addErro("Esta turma j� possui uma avaliacao com denomina��o \""+atividade.getTitulo()+"\" e abrevia��o \""+atividade.getAbreviacao()+"\"");
				
				// Altera��o de uma avalia��o - Verifica se est� sendo alterado o t�tulo e a abrevia��o da avalia��o antiga
				if (possuiAvaliacao && titulo != null && abrev != null && !titulo.equals(atividade.getTitulo()) && !abrev.equals(atividade.getAbreviacao()) && !nomeAvaliacaoDisponivel)
					erros.addErro("Esta turma j� possui uma avaliacao com denomina��o \""+atividade.getTitulo()+"\" e abrevia��o \""+atividade.getAbreviacao()+"\"");
				
				if ( config != null ) {	
					
					if (config.isAvaliacoesMediaPonderada(atividade.getUnidade())) {
						validateRequired(atividade.getPeso(), "Peso", erros);
						validateRange(atividade.getPeso(), 1, 99, "Peso", erros);
					}
					
					if ( atividade.getNotaMaxima() != null && config.isAvaliacoesMediaAritmetica(atividade.getUnidade())) 
						atividade.setNotaMaxima(null);
					
					if ( atividade.getNotaMaxima() != null && config.isAvaliacoesMediaPonderada(atividade.getUnidade()))
						atividade.setNotaMaxima(null);			
					
					// A unidade est� configurada para ser soma das notas.
					if (config.isAvaliacoesSoma(atividade.getUnidade())) {
						
						validateRequired(atividade.getNotaMaxima(), "Nota M�xima", erros);
						validateRange(atividade.getNotaMaxima(), 0.1, 10.0, "Nota M�xima", erros);
						
						if ( erros.isErrorPresent() )
							return;
						
						double notaMaxima = atividade.getNotaMaxima();
						if ( notaMaxima > 10 )
							notaMaxima = notaMaxima/10;
						
						// Caso a tarefa esteja sendo atualizada - vari�vel guarda a �ltima nota m�xima da avalia��o gerada por esta tarefa.
						// Ex: Tarefa foi criada com nota m�xima = 8
						// A tarefa est� sendo atualizada para que sua nota m�xima seja igual a 6
						// notaMaximaAvaliacao = 8
						Double notaMaximaAvaliacao = null;
						if ( avaliacoes != null && !avaliacoes.isEmpty() ){
							notaMaximaAvaliacao = avaliacoes.get(0).getNotaMaxima();
							// Se estiver trocando de unidade � a mesma coisa de est� cadastrando
							if ( ((Number) avaliacoes.get(0).getUnidade().getUnidade()).intValue() != atividade.getUnidade())
								cadastrar = true;
						}

						if ( notaMaximaAvaliacao != null && notaMaxima != notaMaximaAvaliacao && !isCorrigidas(avaliacoes)){
							erros.addErro("N�o � poss�vel alterar a nota m�xima de uma atividade que j� foi corrigida.");
						}
												
						Double nota = aDao.getSomaNotaAvaliacao(turma, atividade.getUnidade());
						Double diferencaNota = null;
						// Atualiza��o de uma tarefa 

						if ( notaMaximaAvaliacao != null && notaMaximaAvaliacao <= nota )
							diferencaNota = nota - notaMaximaAvaliacao;
						
						if ( !cadastrar && possuiNotaMaiorQueNotaMaxima(avaliacoes,atividade.getNotaMaxima()) )
							erros.addErro("J� foi lan�ada as notas desta atividade. Um aluno recebeu uma nota que excede a nota m�xima que est� sendo configurada para esta atividade. N�o � poss�vel alterar uma atividade que um aluno possui nota superior a nota m�xima que est� sendo configurada.");
						
						if ( cadastrar && nota >= 10.0) {
							erros.addErro("A unidade " + atividade.getUnidade() + " j� possui um conjunto de avalia��es cuja soma das notas m�ximas � 10,0. N�o � poss�vel cadastrar mais uma avalia��o.");
						} // Soma das Notas + Nota que est� sendo cadastrada/atualizada deve ser menor ou igual a dez. 
						else if ( (cadastrar || diferencaNota == null) && nota < 10.0 && nota + notaMaxima > 10.0) {
							erros.addErro("A soma das notas m�ximas das avalia��es da unidade " + atividade.getUnidade() + " excede 10,0. O valor m�ximo para a nota desta avalia��o deve ser " + Formatador.getInstance().formatarDecimal1(10.0 - nota) + ".");
						} 
						else if (!cadastrar && diferencaNota != null && nota < 10.0 && diferencaNota + notaMaxima > 10.0)
							erros.addErro("A soma das notas m�ximas das avalia��es da unidade " + atividade.getUnidade() + " excede 10,0. O valor m�ximo para a nota desta avalia��o deve ser " + Formatador.getInstance().formatarDecimal1(10.0 - diferencaNota) + ".");
					}
				}
			}finally{
				if (aDao != null)
					aDao.close();
				if (tDao != null)
					tDao.close();
			}
		}
	}
	
	private static boolean possuiNotaMaiorQueNotaMaxima ( List<Avaliacao> avaliacoes , Double notaMaxima) {
		if ( avaliacoes != null )
			for ( Avaliacao a : avaliacoes )
				if ( a.getNota() != null && a.getNotaMaxima() != null && a.getNota() > notaMaxima )
					return true;
		return false;		
	}

	private static boolean isCorrigidas ( List<Avaliacao> avaliacoes) {
		if ( avaliacoes != null )
			for ( Avaliacao a : avaliacoes )
				if ( a.getNota() != null )
					return true;
		return false;		
	}
	
	/**
	 * Cria uma avalia��o correspondente a atividade.<br/><br/>
	 * M�todo usado para criar uma integra��o com a consolida��o quando a atividade possui nota.
	 *  
	 * @return 
	 */
	public static Avaliacao criarAvaliacao ( AtividadeAvaliavel atividade ) {
		Avaliacao av = new Avaliacao();
		av.setAbreviacao(atividade.getAbreviacao());
		av.setDenominacao(atividade.getTitulo());
		
		// Se a nota m�xima m�xima for configurada
		if (atividade.getNotaMaxima() != null)
			// Caso a nota m�xima seja maior que dez, a avalia��o ter� a nota m�xima igual a um d�cimo desta, para garantir que fique entre zero e dez. 
			av.setNotaMaxima(atividade.getNotaMaxima() > 10 ? atividade.getNotaMaxima() / 10 : atividade.getNotaMaxima());
		
		// O peso n�o pode ser menor que um;
		if (atividade.getPeso() < 1)
			atividade.setPeso(1);
		
		av.setPeso(atividade.getPeso());
		av.setNota(null);
		av.setEntidade(atividade.getAtividade());
		av.setAtividadeQueGerou(atividade);
		return av;
	}
	
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}
	
}
