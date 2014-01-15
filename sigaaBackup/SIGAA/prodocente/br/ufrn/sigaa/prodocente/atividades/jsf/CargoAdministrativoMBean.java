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

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.CargoAdministrativo;
import br.ufrn.sigaa.prodocente.atividades.dominio.DesignacaoCargo;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
public class CargoAdministrativoMBean
		extends AbstractControllerAtividades<CargoAdministrativo> {
	
	public CargoAdministrativoMBean() {
		obj = new CargoAdministrativo();
		obj.setDesignacaoCargo(new DesignacaoCargo());
		obj.setServidor(new Servidor());
		setBuscaServidor(true);
	}

	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(CargoAdministrativo.class, "id", "servidor.pessoa.nome");
	}

	@Override
	protected void afterCadastrar() {
		obj = new CargoAdministrativo();
	}
	
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.PRODOCENTE_PRH);
	}
	
	@Override
	public String getUrlRedirecRemover(){
		return "/sigaa/prodocente/atividades/CargoAdministrativo/lista.jsf";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		return super.cadastrar();
	}
	
	@Override
	public String cancelar() {
		return redirectJSF(getSubSistema().getLink());
	}

	@Override
	public String buscar() throws Exception {
		if (idServidor == -1 && !buscaServidor) 
			addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Docente");
		else
			setBuscaServidor(true);
		return null;
	}
	
	@Override
	public String atualizar() throws ArqException {
		obj.setId(getParameterInt("id"));
		super.atualizar();
		return forward("/prodocente/atividades/CargoAdministrativo/form.jsp");
	}
	
	@Override
	public Collection<CargoAdministrativo> getAllAtividades() throws DAOException {
		
		erros.getMensagens().clear();
		if (buscaServidor) {
		
		Servidor servidorLogado = null;
		
			if (getUsuarioLogado().getVinculoAtivo().getServidor() != null) {
					servidorLogado = getUsuarioLogado().getVinculoAtivo().getServidor();
			}
		
			if (idServidor != -1) {
					getUsuarioLogado().getVinculoAtivo().getServidor().setId(idServidor);
					atividades = super.getAllAtividades();
					if (atividades.size() == 0) {
						addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
					}
					return atividades;
			}else {
				setBuscaServidor(false);
				atividades = null;
			}
		}
		return atividades;
	}
}	