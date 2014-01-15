/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Classe auxiliar para Avaliações.
 * 
 * @author Diego Jácome
 *
 */
public class AvaliacaoHelper {

	/**
	 * Valida o cadastro de uma avaliação a partir de uma atividade avaliável. 
	 * 
	 * @param atividade
	 * @param turma
	 * @param erros
	 * @param cadastrar - Se a atividade está sendo cadastrada ou alterada
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
				
				validateMaxLength(atividade.getTitulo(),100,"Título",erros);
				
				boolean nomeAvaliacaoDisponivel = aDao.isNomeAvaliacaoDisponivel(turma, atividade.getTitulo(),atividade.getAbreviacao());
				
				// Cadastro de uma nova avaliação
				if (!possuiAvaliacao && !nomeAvaliacaoDisponivel)
					erros.addErro("Esta turma já possui uma avaliacao com denominação \""+atividade.getTitulo()+"\" e abreviação \""+atividade.getAbreviacao()+"\"");
				
				// Alteração de uma avaliação - Verifica se está sendo alterado o título e a abreviação da avaliação antiga
				if (possuiAvaliacao && titulo != null && abrev != null && !titulo.equals(atividade.getTitulo()) && !abrev.equals(atividade.getAbreviacao()) && !nomeAvaliacaoDisponivel)
					erros.addErro("Esta turma já possui uma avaliacao com denominação \""+atividade.getTitulo()+"\" e abreviação \""+atividade.getAbreviacao()+"\"");
				
				if ( config != null ) {	
					
					if (config.isAvaliacoesMediaPonderada(atividade.getUnidade())) {
						validateRequired(atividade.getPeso(), "Peso", erros);
						validateRange(atividade.getPeso(), 1, 99, "Peso", erros);
					}
					
					if ( atividade.getNotaMaxima() != null && config.isAvaliacoesMediaAritmetica(atividade.getUnidade())) 
						atividade.setNotaMaxima(null);
					
					if ( atividade.getNotaMaxima() != null && config.isAvaliacoesMediaPonderada(atividade.getUnidade()))
						atividade.setNotaMaxima(null);			
					
					// A unidade está configurada para ser soma das notas.
					if (config.isAvaliacoesSoma(atividade.getUnidade())) {
						
						validateRequired(atividade.getNotaMaxima(), "Nota Máxima", erros);
						validateRange(atividade.getNotaMaxima(), 0.1, 10.0, "Nota Máxima", erros);
						
						if ( erros.isErrorPresent() )
							return;
						
						double notaMaxima = atividade.getNotaMaxima();
						if ( notaMaxima > 10 )
							notaMaxima = notaMaxima/10;
						
						// Caso a tarefa esteja sendo atualizada - variável guarda a última nota máxima da avaliação gerada por esta tarefa.
						// Ex: Tarefa foi criada com nota máxima = 8
						// A tarefa está sendo atualizada para que sua nota máxima seja igual a 6
						// notaMaximaAvaliacao = 8
						Double notaMaximaAvaliacao = null;
						if ( avaliacoes != null && !avaliacoes.isEmpty() ){
							notaMaximaAvaliacao = avaliacoes.get(0).getNotaMaxima();
							// Se estiver trocando de unidade é a mesma coisa de está cadastrando
							if ( ((Number) avaliacoes.get(0).getUnidade().getUnidade()).intValue() != atividade.getUnidade())
								cadastrar = true;
						}

						if ( notaMaximaAvaliacao != null && notaMaxima != notaMaximaAvaliacao && !isCorrigidas(avaliacoes)){
							erros.addErro("Não é possível alterar a nota máxima de uma atividade que já foi corrigida.");
						}
												
						Double nota = aDao.getSomaNotaAvaliacao(turma, atividade.getUnidade());
						Double diferencaNota = null;
						// Atualização de uma tarefa 

						if ( notaMaximaAvaliacao != null && notaMaximaAvaliacao <= nota )
							diferencaNota = nota - notaMaximaAvaliacao;
						
						if ( !cadastrar && possuiNotaMaiorQueNotaMaxima(avaliacoes,atividade.getNotaMaxima()) )
							erros.addErro("Já foi lançada as notas desta atividade. Um aluno recebeu uma nota que excede a nota máxima que está sendo configurada para esta atividade. Não é possível alterar uma atividade que um aluno possui nota superior a nota máxima que está sendo configurada.");
						
						if ( cadastrar && nota >= 10.0) {
							erros.addErro("A unidade " + atividade.getUnidade() + " já possui um conjunto de avaliações cuja soma das notas máximas é 10,0. Não é possível cadastrar mais uma avaliação.");
						} // Soma das Notas + Nota que está sendo cadastrada/atualizada deve ser menor ou igual a dez. 
						else if ( (cadastrar || diferencaNota == null) && nota < 10.0 && nota + notaMaxima > 10.0) {
							erros.addErro("A soma das notas máximas das avaliações da unidade " + atividade.getUnidade() + " excede 10,0. O valor máximo para a nota desta avaliação deve ser " + Formatador.getInstance().formatarDecimal1(10.0 - nota) + ".");
						} 
						else if (!cadastrar && diferencaNota != null && nota < 10.0 && diferencaNota + notaMaxima > 10.0)
							erros.addErro("A soma das notas máximas das avaliações da unidade " + atividade.getUnidade() + " excede 10,0. O valor máximo para a nota desta avaliação deve ser " + Formatador.getInstance().formatarDecimal1(10.0 - diferencaNota) + ".");
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
	 * Cria uma avaliação correspondente a atividade.<br/><br/>
	 * Método usado para criar uma integração com a consolidação quando a atividade possui nota.
	 *  
	 * @return 
	 */
	public static Avaliacao criarAvaliacao ( AtividadeAvaliavel atividade ) {
		Avaliacao av = new Avaliacao();
		av.setAbreviacao(atividade.getAbreviacao());
		av.setDenominacao(atividade.getTitulo());
		
		// Se a nota máxima máxima for configurada
		if (atividade.getNotaMaxima() != null)
			// Caso a nota máxima seja maior que dez, a avaliação terá a nota máxima igual a um décimo desta, para garantir que fique entre zero e dez. 
			av.setNotaMaxima(atividade.getNotaMaxima() > 10 ? atividade.getNotaMaxima() / 10 : atividade.getNotaMaxima());
		
		// O peso não pode ser menor que um;
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
