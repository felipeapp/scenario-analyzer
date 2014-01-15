package br.ufrn.sigaa.cv.jsf;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import br.ufrn.sigaa.cv.dominio.DominioComunidadeVirtual;


public abstract class PaginacaoComunidadeController <T extends DominioComunidadeVirtual> extends CadastroComunidadeVirtual <T> {

	protected void iniciarListagem() {
		zerarListagem();
		
		// Quando o usuário entra na listagem pela primeira vez, pega a ordem da configuração da comunidade.
		ComunidadeVirtualMBean cvBean = getMBean("comunidadeVirtualMBean");
		if (cvBean.getComunidade() != null)
			crescente = cvBean.getComunidade().isOrdemCrescente();
	}
	
	protected void zerarListagem () {
		getPaginacao().setPaginaAtual(0);
		listagem = null;
	}
	
	public String inverterOrdem () {
		crescente = !crescente;
		
		listagem = null;
		
		return null;
	}
	
	public void changePage (ValueChangeEvent e){
		listagem = null;
		getPaginacao().changePage(e);
	}
	
	public void previousPage (ActionEvent e){
		listagem = null;
		getPaginacao().previousPage(e);
	}
	
	public void nextPage (ActionEvent e){
		listagem = null;
		getPaginacao().nextPage(e);
	}
	
	public void primeiraPagina (ActionEvent e){
		listagem = null;
		getPaginacao().primeiraPagina(e);
	}
	
	public void ultimaPagina (ActionEvent e){
		listagem = null;
		getPaginacao().ultimaPagina(e);
	}
	
	public void mudarRegistrosPorPagina (ValueChangeEvent e){
		zerarListagem();
	}
}
