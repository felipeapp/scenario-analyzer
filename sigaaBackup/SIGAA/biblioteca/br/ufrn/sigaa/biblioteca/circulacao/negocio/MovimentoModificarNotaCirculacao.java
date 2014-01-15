/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Classe que passa os dados para o processador que inclui notas de circulação </p>
 * 
 * @author jadson
 *
 */
public class MovimentoModificarNotaCirculacao extends AbstractMovimentoAdapter{

	/**
	 * As notas quem vão ser criadas.
	 */
	private List<NotaCirculacao> notas;

	/**
	 * Os materiais selecionados para remoção das notas
	 */
	private List<MaterialInformacional> materiais;

	/**
	 * Construtor usado para incluir uma nota de circulação.
	 * 
	 * @param notas
	 */
	public MovimentoModificarNotaCirculacao(List<NotaCirculacao> notas) {
		this.notas = notas;
	}

	/**
	 * Construtor usado para remover uma nota de circulação, normalmente as única notas que são removidas 
	 * manualmente são as notas bloqueantes, as outras são removidas pelos processadores de circulação
	 * a medida que as mensagem são mostradas para o usuário. 
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
