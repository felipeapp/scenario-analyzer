/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 14/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.NotaCirculacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 *
 * <p> Classe que passa os dados para o processador que inclui notas de circula��o </p>
 * 
 * @author jadson
 *
 */
public class MovimentoModificarNotaCirculacao extends AbstractMovimentoAdapter{

	/**
	 * As notas quem v�o ser criadas.
	 */
	private List<NotaCirculacao> notas;

	/**
	 * Os materiais selecionados para remo��o das notas
	 */
	private List<MaterialInformacional> materiais;

	/**
	 * Construtor usado para incluir uma nota de circula��o.
	 * 
	 * @param notas
	 */
	public MovimentoModificarNotaCirculacao(List<NotaCirculacao> notas) {
		this.notas = notas;
	}

	/**
	 * Construtor usado para remover uma nota de circula��o, normalmente as �nica notas que s�o removidas 
	 * manualmente s�o as notas bloqueantes, as outras s�o removidas pelos processadores de circula��o
	 * a medida que as mensagem s�o mostradas para o usu�rio. 
	 * 
	 */
	public MovimentoModificarNotaCirculacao(List<NotaCirculacao> notas, List<MaterialInformacional> materiais) {
		this.notas = notas;
		this.materiais = materiais;
	}

	public List<NotaCirculacao> getNotas() {
		return notas;
	}

	public List<MaterialInformacional> getMateriais() {
		return materiais;
	}
	
}
