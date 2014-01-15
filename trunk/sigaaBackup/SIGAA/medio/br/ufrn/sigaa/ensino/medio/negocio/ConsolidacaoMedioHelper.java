/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 28/07/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.ParseException;
import java.util.Collection;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe com métodos utilitários para a consolidação de turmas do ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class ConsolidacaoMedioHelper {

	
	/**
	 * Busca notas em request e popula na coleção de matrículas.
	 * Método não invocado por JSPs.
	 * @throws ParseException
	 * @throws NumberFormatException
	 * @throws DAOException
	 */
	public static  Collection<MatriculaComponente> popularNotas(boolean tarefa, Turma turma, ConfiguracoesAva config) throws NegocioException {

		if (!isEmpty(turma.getMatriculasDisciplina())) {
			for (MatriculaComponente matricula : turma.getMatriculasDisciplina()) {
	
				if (!matricula.isConsolidada()) {
				
					if (!tarefa){
						String faltasStr = getParameter("faltas_" + matricula.getId());
						Integer faltas = 0;
						if (!StringUtils.isEmpty(faltasStr))
							faltas = convertFalta(faltasStr);
						
						if (faltas < 0)
							throw new NegocioException (UFRNUtils.getMensagem(MensagensArquitetura.VALOR_MAIOR_IGUAL_ZERO, "Faltas").getMensagem());
			
						matricula.setNumeroFaltas(faltas);
						
						String paramRecuperacao = "recup_" + matricula.getId();
						if (existeNota(paramRecuperacao))
							matricula.setRecuperacao(getNota(paramRecuperacao));
					}
				
					matricula.getNotas().iterator();
					for (NotaUnidade nota : matricula.getNotas()) {
						if (!tarefa) {
							String paramNota = "nota_" + nota.getId();
							if (existeNota(paramNota))
								nota.setNota(getNota(paramNota));
						}
						
						if (nota.getAvaliacoes() != null && !nota.getAvaliacoes().isEmpty()) {
							if (config != null && config.getTipoMediaAvaliacoes(nota.getUnidade()) != null) {
								if (config.isAvaliacoesMediaPonderada(nota.getUnidade())) {
									double notas = 0.0;
									double pesos = 0.0;
		
									for (Avaliacao avaliacao : nota.getAvaliacoes()) {
										
										if (!tarefa) {
											String paramAvaliacao = "aval_" + avaliacao.getId();
											if (existeNota(paramAvaliacao))
												avaliacao.setNota(getNota(paramAvaliacao));
										}
										
										if (avaliacao.getNota() != null) {
											notas += avaliacao.getNota() * 10D * avaliacao.getPeso();
											pesos += avaliacao.getPeso();
										}
									}
									
									double n = Math.round (notas / (pesos == 0 ? 10 : pesos * 10) * 10D) / 10D;
									nota.setNota(n);
								} else if (config.isAvaliacoesMediaAritmetica(nota.getUnidade())) {
									double notas = 0.0;
									
									for (Avaliacao avaliacao : nota.getAvaliacoes()) {
										
										if (!tarefa) {
											String paramAvaliacao = "aval_" + avaliacao.getId();
											if (existeNota(paramAvaliacao))
												avaliacao.setNota(getNota(paramAvaliacao));
										}
										
										if (avaliacao.getNota() != null)
											notas += avaliacao.getNota() * 10D;
									}
									
									double n = Math.round(notas / (nota.getAvaliacoes().size() * 10D) * 10D ) / 10D;
									nota.setNota(n);
								} else if (config.isAvaliacoesSoma(nota.getUnidade())) {
									double notas = 0.0;
									for (Avaliacao avaliacao : nota.getAvaliacoes()) {
										
										if ( !tarefa ) {
											String paramAvaliacao = "aval_" + avaliacao.getId();
											if (existeNota(paramAvaliacao))
												avaliacao.setNota(getNota(paramAvaliacao));
										}
										
										if (avaliacao.getNota() != null)
											notas += avaliacao.getNota() * 10D;
									}
									
									nota.setNota(notas > 100D ? 10D : notas / 10D);
								}
							}
						}
		
					}
		
					if (!tarefa) {
						matricula.setMediaFinal(matricula.calculaMediaFinal());
					}
				}
			}
		}
		return turma.getMatriculasDisciplina();
	}
	
	/**
	 * Converte as faltas de String para Integer.
	 * Usado pelo popularNotas().
	 * @param paramName
	 * @return
	 * @throws NegocioException 
	 * @throws ParseException
	 */
	private static Integer convertFalta(String falta) throws NegocioException {

		try {
			Integer faltas = Integer.parseInt(falta); 
			
			return faltas;
		} catch(Exception e) {
			throw new NegocioException("Faltas inválidas: " + falta);
		}
	}
	
	/**
	 * Indica se foi enviado um novo valor para a nota passada.
	 * 
	 * @param paramName
	 * @return
	 * @throws NegocioException
	 */
	private static boolean existeNota (String paramName) throws NegocioException {
		return getParameter(paramName) != null;
	}
	
	/**
	 * Pega uma nota em request. Se a nota não existir, retorna nulo.
	 * Usado pelo popularNotas().<br/><br/>
	 * 
	 * <strong>Cuidado ao utilizar este método:</strong> Caso uma nota não tenha sido enviada,
	 * o objeto pode receber a nota nula erroneamente.<br/>
	 * Para evitar este problema, deve-se chamar o método existeNota(nota) para verificar se 
	 * foi enviado um novo valor para o objeto.
	 * 
	 * @param paramName
	 * @return
	 * @throws NegocioException 
	 * @throws ParseException
	 */
	private static Double getNota(String paramName) throws NegocioException {
		String param = getParameter(paramName);

		if (param != null && !"".equals(param.trim())) {
			param = param.trim().replaceAll(",", ".");
			
			try {
				Double nota = Double.parseDouble(param);
				
				// Garante que as notas estão entre zero e dez.
				if (nota > 10 || nota < 0)
					throw new Exception ();
				
				return nota;
			} catch(Exception e) {
				throw new NegocioException("Nota inválida: " + getParameter(paramName));
			}
		}

		return null;
	}
	
	/**
	 * Pega um parâmetro em request
	 * @param param Nome do parâmetro
	 * @return valor do parâmetro em request 
	 */
	public static String getParameter(String param) {
		return RequestUtils.getParameter(param,  getCurrentRequest());
	}
	
	/**
	 * Possibilita o acesso ao HttpServletRequest.
	 */
	public static HttpServletRequest getCurrentRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}
	
	/**
	 * Acessa o external context do JavaServer Faces
	 **/
	private static ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}
}
