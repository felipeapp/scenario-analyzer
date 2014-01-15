package br.ufrn.sigaa.extensao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.ObjetivoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.MembroAtividade;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.ObjetivoAtividades;
import br.ufrn.sigaa.extensao.jsf.helper.AtividadeExtensaoHelper;
import br.ufrn.sigaa.extensao.negocio.AtividadeExtensaoValidator;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

@Component @Scope("request")
public class ObjetivoMBean extends SigaaAbstractController<Objetivo> {

	/** Atributo utilizado para informar se vai haver alteração ou não. */
	private boolean alterar = false;

	/** Atributo utilizado para representar o objetivo das atividades desenvolvida na ação de extensão */
	private ObjetivoAtividades objetivoAtividades = new ObjetivoAtividades();
	
	public ObjetivoMBean() {
		obj = new Objetivo();
	}

	public void adicionarMembro() throws ArqException, NegocioException {
		ListaMensagens lista = new ListaMensagens();
		
		if (objetivoAtividades.getMembroAtividade().getMembroProjeto().getId() <= 0){
			lista.addErro("Nenhum Membro selecionado");
		}
		
		ValidatorUtil.validateRange(objetivoAtividades.getMembroAtividade().getCargaHoraria(), 1, objetivoAtividades.getCargaHoraria(), "Carga horária Membro", lista);
		if ( objetivoAtividades.getMembrosAtividade().contains( objetivoAtividades.getMembroAtividade() ) )
			lista.addErro("Membro já presente nessa Atividade.");

		if (!lista.isEmpty()) {
			addMensagensAjax(lista);
			return;
		}
		
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
		
		try {
			objetivoAtividades.getMembroAtividade().setMembroProjeto( dao.findByPrimaryKey(objetivoAtividades.getMembroAtividade().getMembroProjeto().getId(), MembroProjeto.class) );
			if ( !hasErrors() && !objetivoAtividades.getMembrosAtividade().contains( objetivoAtividades.getMembroAtividade() ) ) {
				objetivoAtividades.getMembroAtividade().setObjetivoAtividade( objetivoAtividades );
				objetivoAtividades.getMembrosAtividade().add( objetivoAtividades.getMembroAtividade() );
				objetivoAtividades.setMembroAtividade(new MembroAtividade());
				objetivoAtividades.getMembroAtividade().setMembroProjeto(new MembroProjeto());
			}
			
		} finally {
			dao.close();
		}
	}

	public void removeMembro(ActionEvent event) throws DAOException, SegurancaException {
		Integer idMembro = ((Integer) event.getComponent().getAttributes().get("idMembro"));
		Integer idMembroAtividade = ((Integer) event.getComponent().getAttributes().get("idMembroAtividade"));
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
		try {
			if ( objetivoAtividades.getMembroAtividade() == null )
				objetivoAtividades.setMembrosAtividade( new ArrayList<MembroAtividade>() );
			objetivoAtividades.setMembroAtividade(new MembroAtividade(idMembro));
			objetivoAtividades.getMembroAtividade().setMembroProjeto( dao.findByPrimaryKey(objetivoAtividades.getMembroAtividade().getMembroProjeto().getId(), MembroProjeto.class) );
			if ( objetivoAtividades.getMembrosAtividade().contains( objetivoAtividades.getMembroAtividade() ) ) {
				objetivoAtividades.getMembrosAtividade().remove( objetivoAtividades.getMembroAtividade() );
				if ( idMembroAtividade > 0 )
					getGenericDAO().updateField(MembroAtividade.class, idMembroAtividade, "ativo", Boolean.FALSE);
			}
			
		} finally {
			dao.close();
		}
	}

	public void adicionarObjetivoAtividades() throws ArqException, NegocioException {

		AtividadeExtensao atividade = AtividadeExtensaoHelper.getAtividadeMBean().getObj();
		ListaMensagens lista= new ListaMensagens();
		
		ValidatorUtil.validateRequired(obj.getObjetivo(), "Objetivos do Projeto", lista);
		ValidatorUtil.validateRequired(objetivoAtividades.getDescricao(), "Descrição das Atividades", lista);
		ValidatorUtil.validateRequired(objetivoAtividades.getCargaHoraria(), "Carga horária", lista);
		ValidatorUtil.validateRequired(objetivoAtividades.getDataInicio(), "Período (data inicial)", lista);
		ValidatorUtil.validateRequired(objetivoAtividades.getDataFim(), "Período (data final)", lista);
		
		
		if ((objetivoAtividades.getDataInicio() != null) && (objetivoAtividades.getDataFim() != null)){
			ValidatorUtil.validaOrdemTemporalDatas(objetivoAtividades.getDataInicio(), objetivoAtividades.getDataFim(), 
					true, "Período da Atividade: Data do término deve ser maior que a data de início.", lista);
			
			if(!CalendarUtils.isDentroPeriodo(atividade.getDataInicio(), atividade.getDataFim(), objetivoAtividades.getDataInicio()))
				lista.addErro("Data início da atividade vinculada ao projeto fora do período do projeto (início do projeto: " + DateFormatUtils.format(atividade.getDataInicio(), "dd-MM-yyyy") + ").");
			if(!CalendarUtils.isDentroPeriodo(atividade.getDataInicio(), atividade.getDataFim(), objetivoAtividades.getDataFim()))
				lista.addErro("Data fim da atividade vinculada ao projeto fora do período do projeto (fim do projeto: " + DateFormatUtils.format(atividade.getDataFim(), "dd-MM-yyyy") + ").");

			ValidatorUtil.validateEmptyCollection("É necessário informar pelo menos uma atividade.", objetivoAtividades.getMembrosAtividade(), lista);
		    
		    if ( objetivoAtividades.getMembrosAtividade().isEmpty() ) {
		    	lista.addErro("É necessário adicionar um membro para a realização da atividade.");
			}
		    
			if (!lista.isEmpty()) {
				addMensagensAjax(lista);
				return;
			}
		}
		
		if (!lista.isEmpty()) {
			addMensagensAjax(lista);
			return;
		}
		
		if ((obj != null) && (obj.getAtividadesPrincipais() != null)) {
			objetivoAtividades.setPosicao(obj.getAtividadesPrincipais().size());
			obj.getAtividadesPrincipais().clear();
			obj.addAtividadesPrincipais(objetivoAtividades);
			gravarObjetivo();
			setObj( getDAO(ObjetivoDao.class).findObjetivos(null, obj.getId()).get(0) );
			// limpa atividades da view
			objetivoAtividades = new ObjetivoAtividades();
			objetivoAtividades.setMembrosAtividade(new ArrayList<MembroAtividade>());
			objetivoAtividades.setMembroAtividade(new MembroAtividade());
		}

	}

	public String adicionaObjetivo() throws NegocioException, ArqException {
		// validacao
		ListaMensagens mensagens = new ListaMensagens();
		mensagens = obj.validate();
		if (!mensagens.isEmpty()) {
			addMensagensAjax(mensagens);
			return null;
		}

		return forward(ConstantesNavegacao.OBJETIVOS_ESPERADOS_EXTENSAO);
	}
	
	private void gravarObjetivo() throws ArqException, NegocioException{
		try {
	    	obj.setAtivo(true);
	    	AtividadeExtensaoMBean mBean = AtividadeExtensaoHelper.getAtividadeMBean();
    
	    	obj.setAtividadeExtensao( mBean.getObj() );
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.SUBMETER_OBJETIVO);
			prepareMovimento(SigaaListaComando.SUBMETER_OBJETIVO);
			execute(mov, getCurrentRequest());
    		if ( mBean.getObj().getObjetivo() == null ) 
    			mBean.getObj().setObjetivo( new ArrayList<Objetivo>() );
			mBean.getObj().getObjetivo().add(obj);
		} finally {
			AtividadeExtensaoHelper.getAtividadeMBean().getObj().setObjetivo(null);
		}
	}
	
	public String alterarObjetivo() throws DAOException {
		int idObjetivo = getParameterInt("idObjetivo", 0);
		ObjetivoDao dao = getDAO(ObjetivoDao.class);
		try {
			if (dao.findObjetivos(null, idObjetivo).isEmpty()){
				addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
				return null;
			}

			setObj(dao.findObjetivos(null, idObjetivo).get(0));

			if ( isEmpty(obj) || !obj.isAtivo() ) {
				addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
				return null;
			}

		} finally {
			dao.close();
		}
		return forward(ConstantesNavegacao.CADASTRO_OBJETIVO);
	}
	
	public void alterarAtividade(ActionEvent event) throws DAOException, SegurancaException {
		Integer posicao = ((Integer) event.getComponent().getAttributes().get("posicao"));
		Integer idAtividade = ((Integer) event.getComponent().getAttributes().get("idAtividade"));

		// removendo atividade não cadastrada no banco
		if (posicao >= 0 && idAtividade == 0) {
			for (Iterator<ObjetivoAtividades> it = getObj().getAtividadesPrincipais().iterator(); it.hasNext();) {
				ObjetivoAtividades objAtiv = it.next(); 
				if (objAtiv.getPosicao() == posicao) {
					objetivoAtividades = new ObjetivoAtividades();
					objetivoAtividades = (ObjetivoAtividades) objAtiv;
					it.remove();
					break;
				}
			}
		}

		if (idAtividade > 0 && posicao == 0) {

		    //removendo do banco.
		    ObjetivoAtividades o = getGenericDAO().findByPrimaryKey(idAtividade, ObjetivoAtividades.class);
		    if (o != null) {
		    	for (Iterator<MembroAtividade> ito =  o.getMembrosAtividade().iterator(); ito.hasNext();) {
		    		if ( !ito.next().isAtivo() )
						ito.remove();
		    	}
		    	objetivoAtividades = o;
		    }
		    
		    // remove da view
		    for (Iterator<ObjetivoAtividades> ito =  getObj().getAtividadesPrincipais().iterator(); ito.hasNext();) {
				for (Iterator<ObjetivoAtividades> it = ito; it.hasNext();) {
				    if (it.next().getId() == idAtividade) {
				    	it.remove();
				    	break;
				    }
				}
		    }
		}
	}
	
	/**
	 * Realiza a inativação do objetivo, dos Objetivos Atividades e dos membros Atividade cadastrados 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void inativarObjetivo() throws ArqException, NegocioException {
		int idObjetivo = getParameterInt("idObjetivo", 0);
		ObjetivoDao dao = getDAO(ObjetivoDao.class);
		try {
			setObj( dao.findAndFetch(idObjetivo, Objetivo.class, "atividadesPrincipais.membrosAtividade") );
			if ( isEmpty(obj) || !obj.isAtivo() ) {
				addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
				return;
			}
			// Carregar Membros da Atividade
	    	for (ObjetivoAtividades objetivoAtividade : obj.getAtividadesPrincipais())
	    		objetivoAtividade.getMembrosAtividade().iterator();
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.REMOVER_OBJETIVO);
			prepareMovimento(SigaaListaComando.REMOVER_OBJETIVO);
			execute(mov, getCurrentRequest());
			
	    	AtividadeExtensaoMBean mBean = AtividadeExtensaoHelper.getAtividadeMBean();
	    	mBean.getObj().getObjetivo().remove(obj);

		} finally {
			dao.close();
		}
	}

	public void removerObjetivoAtividades(ActionEvent event) throws DAOException, SegurancaException {
	    
		Integer posicao = ((Integer) event.getComponent().getAttributes().get("posicao"));
		Integer idAtividade = ((Integer) event.getComponent().getAttributes().get("idAtividade"));
		
		// removendo atividade não cadastrada no banco
		if (posicao >= 0 && idAtividade == 0) {
			for (Iterator<ObjetivoAtividades> it = getObj().getAtividadesPrincipais().iterator(); it.hasNext();) {
				if (it.next().getPosicao() == posicao) {
					it.remove();
				}
			}
		}

		if (idAtividade > 0 && posicao == 0) {
			
			// Valida se existe pelo menos uma atividade vinculada (caso contrário, não remove a atividade)
			if (obj.getAtividadesPrincipais().size() <= 1){
				ListaMensagens lista = new ListaMensagens();
				lista.addErro("Não foi possível remover a Atividade: deve haver pelo menos uma Atividade cadastrada.");
				addMensagensAjax(lista);
				return;
			}
			
		    //removendo do banco.
		    ObjetivoAtividades o = getGenericDAO().findByPrimaryKey(idAtividade, ObjetivoAtividades.class);
		    if (o != null) {
		    	AtividadeExtensaoHelper.getAtividadeMBean().remover(o);
		    }
		    
		    // remove da view
		    for (Iterator<ObjetivoAtividades> ito =  getObj().getAtividadesPrincipais().iterator(); ito.hasNext();) {
				for (Iterator<ObjetivoAtividades> it = ito; it.hasNext();) {
					ObjetivoAtividades objAtiv = it.next(); 
					if (objAtiv.getPosicao() == posicao) {
						objetivoAtividades = new ObjetivoAtividades();
						objetivoAtividades = (ObjetivoAtividades) objAtiv;
						it.remove();
						break;
					}
				}
		    }
		}
		
		setObj( getDAO(ObjetivoDao.class).findObjetivos(null, obj.getId()).get(0) );
	}
	
	public String submeterObjetivos() throws NegocioException, ArqException {
	    ValidatorUtil.validateEmptyCollection("É necessário informar pelo menos um Objetivo.", 
	    		AtividadeExtensaoHelper.getAtividadeMBean().getObjetivos(), erros);

	    AtividadeExtensaoValidator.validaEquipeExecutora(
	    		AtividadeExtensaoHelper.getAtividadeMBean().getMembrosEquipe(), 
	    		AtividadeExtensaoHelper.getAtividadeMBean().getObjetivos(), erros);
	    
	    if (hasErrors())
			return null;
	    
		MovimentoCadastro mov = new MovimentoCadastro();
		obj.setAtividadeExtensao( AtividadeExtensaoHelper.getAtividadeMBean().getObj() );
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.ALTERAR_CH_OBJETIVO);
		prepareMovimento(SigaaListaComando.ALTERAR_CH_OBJETIVO);
		execute(mov, getCurrentRequest());
	    
	    return AtividadeExtensaoHelper.getAtividadeMBean().proximoPasso(); 
	}
	
	public String cadastrarObjetivo() {
		obj = new Objetivo();
		this.obj.setAtividadesPrincipais(new ArrayList<ObjetivoAtividades>());
		return forward(ConstantesNavegacao.CADASTRO_OBJETIVO);
	}

	public String voltarObjetivo() {
		AtividadeExtensaoHelper.getAtividadeMBean().getObj().setObjetivo(null);
		return forward(ConstantesNavegacao.OBJETIVOS_ESPERADOS_EXTENSAO);
	}

	public ObjetivoAtividades getObjetivoAtividades() {
		return objetivoAtividades;
	}

	public void setObjetivoAtividades(ObjetivoAtividades objetivoAtividades) {
		this.objetivoAtividades = objetivoAtividades;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

}