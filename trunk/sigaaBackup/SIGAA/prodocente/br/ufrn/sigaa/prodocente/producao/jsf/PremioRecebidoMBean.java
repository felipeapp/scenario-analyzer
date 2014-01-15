/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.prodocente.producao.dominio.PremioRecebido;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Managed Bean para o caso de uso de Premio Recebido da Produção Intelectual
 * 
 * Gerado pelo CrudBuilder
 */
@Component("premioRecebido")
@Scope("session")
public class PremioRecebidoMBean
		extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.PremioRecebido> {
	
	public PremioRecebidoMBean() {
		obj = new PremioRecebido();
		obj.setInstituicao(new InstituicoesEnsino());

	}
	
	/**
	 * Método não é invocado por jsp
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(PremioRecebido.class, "id", "descricao");
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
		obj.setTipoProducao(TipoProducao.PREMIO_RECEBIDO);
		obj.setTitulo(""); 		
		obj.setArea(AreaConhecimentoCnpq.INDEFINIDO);
		obj.setSubArea(AreaConhecimentoCnpq.INDEFINIDO);
		obj.setServidor(getServidorUsuario());
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	protected void afterCadastrar() {
		obj = new PremioRecebido();
		obj.setInstituicao(new InstituicoesEnsino());
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public void popularObjeto(Producao p) {
		obj = (PremioRecebido) p;
	}

	/**
	 * Método não é invocado por jsp
	 */
	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.PREMIO_RECEBIDO);
	}
	
	/**
	 * Chamada por:
	 * /prodocente/nova_producao.jsp
	 * /prodocente/producao/PremioRecebido/lista.jsp
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		obj = new PremioRecebido();
		obj.getInstituicao().setIdObject(InstituicoesEnsino.UFRN); 
		checkChangeRole();
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		setDirBase("/prodocente/producao/");
		if (verificaBloqueio()) {
			return forward("/prodocente/aviso_bloqueio.jsp");
		} else {
			return forward(getFormPage());
		}
		
	}
}
