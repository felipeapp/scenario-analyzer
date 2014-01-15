/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/10/2009
 *
 */

package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.WordUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.SiglaUnidadePesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;

/**
 * Controlador para operações sobre os centros ou unidades acadêmicas especializadas
 * utilizados na pesquisa
 * 
 * @author Leonardo Campos
 * @author Jean Guerethes
 */
@Component @Scope("request")
public class SiglaUnidadePesquisaMBean extends SigaaAbstractController<SiglaUnidadePesquisa> {

	/** Construtor Padrão. */
	public SiglaUnidadePesquisaMBean() {
		obj = new SiglaUnidadePesquisa();
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		addMensagens(obj.validate());
		if (hasErrors())
			return null;
		
		SiglaUnidadePesquisaDao dao = getDAO(SiglaUnidadePesquisaDao.class);
		Collection<SiglaUnidadePesquisa> siglas;
		
		try {
			siglas = dao.findSiglasUnidadesCadastradas(obj);
			if ( !siglas.isEmpty() ) {
				for (SiglaUnidadePesquisa siglaUnid : siglas) {
					if ( siglaUnid.getUnidade().getId() == obj.getUnidade().getId() && siglaUnid.getId() != obj.getId() )
						addMensagemErro("A unidade ( " + WordUtils.capitalize( siglaUnid.getUnidade().getNome().toLowerCase() )+ " ) possui sigla cadastrada.");
					if ( ( siglaUnid.getSigla() != null && obj.getSigla() != null ) && siglaUnid.getSigla().equals( obj.getSigla() ) && siglaUnid.getId() != obj.getId() )
						addMensagemErro("Essa sigla foi cadastrada para a unidade ( " + WordUtils.capitalize( siglaUnid.getUnidade().getNome().toLowerCase() )+ " )");
				}
			}

		} finally {
			dao.close();
		}

		if ( hasErrors() )
			return null;
		else {
			
			if ( ValidatorUtil.isEmpty( obj.getUnidadeCic().getId() ) )
				obj.setUnidadeCic(null);
			if ( ValidatorUtil.isEmpty( obj.getUnidadeClassificacao().getId() ) )
				obj.setUnidadeClassificacao(null);

			if (obj.getId() > 0) {
				prepareMovimento(ArqListaComando.ALTERAR);
				setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			} else { 
				prepareMovimento(ArqListaComando.CADASTRAR);
				setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
			}
			
			return super.cadastrar();
		}
	}
	
	/**
	 * Carrega a sigla da Unidade para efetuar os ajustes necessários.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li> /sigaa.war/graduacao/componente/geral.jsp </li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		setId();
		setObj( getGenericDAO().findByPrimaryKey(obj.getId(), SiglaUnidadePesquisa.class) );
		
		if ( ValidatorUtil.isEmpty( obj.getUnidadeCic() ) )
			obj.setUnidadeCic(new Unidade());
		if ( ValidatorUtil.isEmpty( obj.getUnidadeClassificacao() ) )
			obj.setUnidadeClassificacao(new Unidade());
		
		setConfirmButton("Alterar");
		return forward(getFormPage());
	}
	
	@Override
	public String remover() throws ArqException {
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), SiglaUnidadePesquisa.class);
		if (obj == null || obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {
			prepareMovimento( ArqListaComando.REMOVER );
			return super.remover();
		}
	}
	
	/**
	 * Retorna as unidades vínculados os centros de Pesquisa
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	  <li> /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/avaliacao_apresentacao_resumo/ajustes_distribuicao.jsp </li>
	 * 	  <li> /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/OrganizacaoPaineisCIC/form_organizacao.jsp </li>
	 * </ul>
	 */
	public Collection<SelectItem> getUnidadesCombo() throws ArqException{
		return toSelectItems(getDAO(ProjetoPesquisaDao.class).findCentrosPesquisa(), "id", "codigoNome");
	}

	@Override
	public String getDirBase() {
		return "/pesquisa/siglaUnidadePesquisa";
	}
	
}