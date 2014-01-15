package br.ufrn.sigaa.pesquisa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;

/**
 * MBean responsável por realizar o CRUD das linhas de pesquisa. 
 *  
 * @author guerethes
 */
@Component @Scope("request")
public class LinhaPesquisaMBean extends SigaaAbstractController<LinhaPesquisa> {

	public LinhaPesquisaMBean() {
		obj = new LinhaPesquisa();
		obj.setGrupoPesquisa( new GrupoPesquisa() );
	}
	
	@Override
	public String getDirBase() {
		return "/pesquisa/LinhaPesquisa";
	}	

}