/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.PublicacaoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * Controlador para publicação em eventos
 */
public class PublicacaoEventoMBean extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.PublicacaoEvento> {
	
	public PublicacaoEventoMBean() {
		obj = new PublicacaoEvento();
	}

	public Collection<SelectItem> getAllNatureza() {
		ArrayList<SelectItem> tipos = new ArrayList<SelectItem>();
		SelectItem sl1 = new SelectItem("T", "Trabalho completo");
		SelectItem sl2 = new SelectItem("R", "Resumo");
		SelectItem sl4 = new SelectItem("E", "Resumo Expandido");
		SelectItem sl3 = new SelectItem("O", "Outros");

		tipos.add(sl1);
		tipos.add(sl2);
		tipos.add(sl3);
		tipos.add(sl4);

		return tipos;
	}

	/**
	 * Inicia o cadastro de uma nova publicação em evento. 
	 * 
	 * JSP: 
	 *		/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/PublicacaoEvento/form.jsp
	 * 		/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/PublicacaoEvento/lista.jsp
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		resetBean("publicacaoEvento");
		return super.preCadastrar();
	}
	
	/**
	 * Retornar todos os tipo de publicação em evento. 
	 */
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(PublicacaoEvento.class, "id", "descricao");
	}

	/**
	 * Realizar algumas validações antes de validar. 
	 * 
	 */
	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.PUBLICACOES_EVENTOS);
		obj.setServidor(getServidorUsuario());

		super.beforeCadastrarAndValidate();
	}

	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();
		obj = new PublicacaoEvento();
	}

	@Override
	public void popularObjeto(Producao p) {
		obj = (PublicacaoEvento) p;
	}

	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.PUBLICACOES_EVENTOS);
	}

	@Override
	public void afterAtualizar() throws ArqException {
		super.afterAtualizar();

		if (obj.getTipoEvento() == null) {
			obj.setTipoEvento( new TipoEvento() );
		}
		if (obj.getTipoRegiao() == null) {
			obj.setTipoRegiao( new TipoRegiao() );
		}
		if (obj.getTipoParticipacao() == null) {
			obj.setTipoParticipacao( new TipoParticipacao() );
		}
	}
}