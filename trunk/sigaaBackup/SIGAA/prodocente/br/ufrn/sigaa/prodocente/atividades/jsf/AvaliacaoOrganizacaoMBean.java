/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;
import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.AvaliacaoOrganizacao;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoAvaliacaoOrganizacaoEvento;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
/**
 	Gerado pelo CrudBuilder
**/
public class AvaliacaoOrganizacaoMBean extends AbstractControllerAtividades<AvaliacaoOrganizacao> {
	public AvaliacaoOrganizacaoMBean() { 
		obj = new AvaliacaoOrganizacao();
		obj.setArea(new AreaConhecimentoCnpq());
		obj.setServidor(new Servidor());
		obj.setSubArea(new AreaConhecimentoCnpq());
		obj.setTipoAvaliacaoOrganizacao(new TipoAvaliacaoOrganizacaoEvento());
		obj.setTipoParticipacao(new TipoParticipacao());
		obj.setTipoRegiao(new TipoRegiao());
		setBuscaServidor(true);
	}
	@Override
	public Collection<SelectItem> getAllCombo() { 
		
		return getAllAtivo(AvaliacaoOrganizacao.class, "id", "descricao"); 
		
	}
	@Override
	protected void afterCadastrar() {
		obj = new AvaliacaoOrganizacao();
	}
	
	@Override
	public String getUrlRedirecRemover()
	{
		return "/sigaa/prodocente/atividades/AvaliacaoOrganizacao/lista.jsf";
	}
}
