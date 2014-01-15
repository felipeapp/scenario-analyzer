/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 25/06/2009
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.Date;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;

/**
 * Movimento que envia os dados quando um operador da bibioteca
 * comunica que um usuário perdeu um material emprestado.
 * 
 * @author Fred_Castro
 *
 */
public class MovimentoComunicarMaterialPerdido extends MovimentoCadastro{

	
	/** Empréstimo que o material foi perdido */
	private Emprestimo emprestimo;
	/** Motivo da perda */
	private String motivo;
	/** Novo Prazo para entregar o novo material */
	private Date novoPrazo;
	
	
	/** Construtor da classe, passando o empréstimo, o motivo e o novo prazo para entrega*/
	public MovimentoComunicarMaterialPerdido(Emprestimo emprestimo, String motivo, Date novoPrazo) {
		super();
		this.emprestimo = emprestimo;
		this.motivo = motivo;
		this.novoPrazo = novoPrazo;
		
		setCodMovimento(SigaaListaComando.COMUNICAR_MATERIAL_PERDIDO);
	}

	
	public Emprestimo getEmprestimo (){
		return emprestimo;
	}

	public String getMotivo() {
		return motivo;
	}
	
	public Date getNovoPrazo() {
		return novoPrazo;
	}

	public void setEmprestimo(Emprestimo emprestimo) {
		this.emprestimo = emprestimo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public void setNovoPrazo(Date novoPrazo) {
		this.novoPrazo = novoPrazo;
	}
}