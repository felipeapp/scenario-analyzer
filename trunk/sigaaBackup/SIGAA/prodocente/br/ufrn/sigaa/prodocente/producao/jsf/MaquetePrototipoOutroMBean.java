/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.prodocente.producao.dominio.Patente;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoTecnologica;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Gerado pelo CrudBuilder
 */
@Component("maquetePrototipoOutro")
@Scope("session")
public class MaquetePrototipoOutroMBean extends AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.ProducaoTecnologica> {

	private Comando comando;

	public MaquetePrototipoOutroMBean() {
		obj = new ProducaoTecnologica();
		obj.setArea(new AreaConhecimentoCnpq());
		obj.setSubArea(new AreaConhecimentoCnpq());
	}

	@Override
	public String getFormPage(){ return "/prodocente/producao/MaquetePrototipoOutro/form.jsp"; }
	@Override 
	public String getListPage() { return "/prodocente/producao/MaquetePrototipoOutro/lista.jsp"; }
	@Override
	public String getUrlRedirecRemover() { return "/sigaa/prodocente/producao/MaquetePrototipoOutro/lista.jsf"; }
	
	
	@Override
	public void afterAtualizar() throws ArqException {
		if (obj instanceof Patente) {
			((Patente) obj).getPatrocinadora().iterator();
		}
	}
	
	
	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.MAQUETES_PROTOTIPOS_OUTROS);
		obj.setServidor(getServidorUsuario());
		super.beforeCadastrarAndValidate();
	}

	@Override
	protected void afterCadastrar() {
		obj = new ProducaoTecnologica();
	}


	@Override
	public void popularObjeto(Producao p) {
		obj =(ProducaoTecnologica) p;
	}
	public List<SelectItem> getTipoParticipacaoSelect() throws DAOException {
		return getTipoParticipacao(TipoProducao.TEXTO_DISCUSSAO );
	}

	public Comando getComando() {
		return comando;
	}

	public void setComando(Comando comando) {
		this.comando = comando;
	}
}
