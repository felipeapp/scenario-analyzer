package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.StatusCotaPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;

/**
 * Controlador para operações de associar um status de um plano de 
 * trabalho a um determinado grupo.
 * 
 * @author Jean Guerethes
 */
@Component() @Scope("request")
public class StatusCotaPlanoTrabalhoMBean extends SigaaAbstractController<StatusCotaPlanoTrabalho> {

	/** Construtor Padrão */
	public StatusCotaPlanoTrabalhoMBean(){
		obj = new StatusCotaPlanoTrabalho();
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		addMensagens(obj.validate());
		if (hasErrors())
			return null;
		
		Collection<StatusCotaPlanoTrabalho> status = 
			getGenericDAO().findByExactField(StatusCotaPlanoTrabalho.class, 
				new String[]{"ordemStatus", "statusPlanoTrabalho"}, 
					new Object[]{obj.getOrdemStatus(), obj.getStatusPlanoTrabalho()});
		
		if ( !status.isEmpty() ) {
			addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Situação/Status");
			return null;
		} else
			return super.cadastrar(); 
	}
	
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	@Override
	public String getDirBase() {
		return "/pesquisa/StatusCotaPlanoTrabalho";
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return toSelectItems( StatusCotaPlanoTrabalho.status() );
	}
	
	/** Retorna todos os status de plano de trabalho de possíveis de pesquisa */
	public Collection<SelectItem> getAllTiposPlanoTrabalho() throws ArqException {
		return toSelectItems(TipoStatusPlanoTrabalho.getTipos());
	}

	@Override
	public Collection<StatusCotaPlanoTrabalho> getAll() throws ArqException {
		return getGenericDAO().findAll(StatusCotaPlanoTrabalho.class, "ordemStatus", "asc");
	}

	@Override
	public String atualizar() throws ArqException {
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), StatusCotaPlanoTrabalho.class);
		if (obj == null || obj.getId() == 0) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO, TipoMensagemUFRN.ERROR);
			return null;
		} else {
			prepareMovimento( ArqListaComando.ALTERAR );
			
			super.atualizar();
			return forward(getFormPage());
		}
	}
	
	@Override
	public String remover() throws ArqException {
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), StatusCotaPlanoTrabalho.class);
		if (obj == null || obj.getId() == 0) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO, TipoMensagemUFRN.ERROR);
			return null;
		} else {
			prepareMovimento( ArqListaComando.REMOVER );
			return super.remover();
		}
	}
	
	/**
	 * Serve para direcionar o usuário para a tela de listagem ou para a tela 
	 * principal do sistema de dependendo de onde a requisição tenha se originado.
	 */
	@Override
	public String cancelar() {
		if ( obj.getId() > 0 )
			try {
				return listar();
			} catch (ArqException e) {
				e.printStackTrace();
			}
		else 
			return super.cancelar();

		return null;
	}
	
}