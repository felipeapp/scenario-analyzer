/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/10/2009
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;
import java.util.HashSet;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.Invencao;
import br.ufrn.sigaa.pesquisa.dominio.ParecerInvencao;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusNotificacaoInvencao;

/**
 * MBean responsável pela emissão de pareceres sobre as invenções cadastradas
 * 
 * @author Leonardo Campos
 */
@Component("parecerInvencaoBean") @Scope("request")
public class ParecerInvencaoMBean extends SigaaAbstractController<ParecerInvencao> {

	public final String JSP_PARECER = "/pesquisa/invencao/parecer.jsf";
	
	private Collection<ParecerInvencao> pareceresAnteriores;
	
	public Collection<SelectItem> getStatusInvencaoCombo(){
		return toSelectItems(TipoStatusNotificacaoInvencao.getTipos());
	}
	
	/**
	 * Construtor padrão
	 */
	public ParecerInvencaoMBean() {
		clear();
	}

	/**
	 * Inicialização padrão do obj
	 */
	private void clear() {
		obj = new ParecerInvencao();
		obj.setInvencao(new Invencao());
		
		pareceresAnteriores = new HashSet<ParecerInvencao>();
	}

	/**
	 * Método serve para popular um parecer sobre uma invenção.
	 * <br/><br/>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/invencao/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public String popularParecer() throws ArqException {
		checkRole(SigaaPapeis.NIT);
		
		int id = getParameterInt("idInvencao");
		
		Invencao invencao = getGenericDAO().findByPrimaryKey(id, Invencao.class);
		pareceresAnteriores = getGenericDAO().findByExactField(ParecerInvencao.class, "invencao.id", invencao.getId(), "asc", "data");
		
		obj.setInvencao(invencao);
		
		prepareMovimento(SigaaListaComando.EMITIR_PARECER_INVENCAO);
		
		return forward(JSP_PARECER);
	}
	
	/**
	 * Esse método tem como finalidade como finalidade
	 * 
	 * parecerInvencaoBean.emitirParecer
	 * @return
	 * @throws ArqException
	 */
	public String emitirParecer() throws ArqException {
		
		erros = obj.validate();
		
		if(hasErrors()){
			addMensagens(erros);
			return null;
		}
		
		Invencao invencao = getGenericDAO().findByPrimaryKey(obj.getInvencao().getId(),	Invencao.class);
		obj.setInvencao(invencao);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.EMITIR_PARECER_INVENCAO);
		mov.setObjMovimentado(obj);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			e.printStackTrace();
			return null;
		}
		
		addMensagemInformation("Parecer emitido com sucesso!");
		
		InvencaoMBean invencaoBean = (InvencaoMBean) getMBean("invencao");
		return invencaoBean.listar();
	}

	public Collection<ParecerInvencao> getPareceresAnteriores() {
		return pareceresAnteriores;
	}

	public void setPareceresAnteriores(
			Collection<ParecerInvencao> pareceresAnteriores) {
		this.pareceresAnteriores = pareceresAnteriores;
	}
}
