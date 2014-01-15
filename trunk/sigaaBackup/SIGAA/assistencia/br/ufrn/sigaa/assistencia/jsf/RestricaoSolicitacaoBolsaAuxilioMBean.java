package br.ufrn.sigaa.assistencia.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dao.RestricaoSolicitacaoBolsaAuxilioDao;
import br.ufrn.sigaa.assistencia.dominio.RestricaoSolicitacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;

@Component @Scope("session")
public class RestricaoSolicitacaoBolsaAuxilioMBean extends SigaaAbstractController<RestricaoSolicitacaoBolsaAuxilio> {

	private DataModel lista;
	private RestricaoSolicitacaoBolsaAuxilio restricao;
	
	public RestricaoSolicitacaoBolsaAuxilioMBean() {
		clearAll();
	}

	private void clear() {
		restricao = new RestricaoSolicitacaoBolsaAuxilio();
		restricao.setTipoBolsaAuxilio( new TipoBolsaAuxilio() );
		restricao.setSituacao(new SituacaoBolsaAuxilio());
		restricao.setBolsasAuxilioRestricao(new TipoBolsaAuxilio());
	}
	
	private void clearAll() {
		obj = new RestricaoSolicitacaoBolsaAuxilio();
		obj.setTipoBolsaAuxilio( new TipoBolsaAuxilio() );
		obj.setSituacao(new SituacaoBolsaAuxilio());
		obj.setBolsasAuxilioRestricao(new TipoBolsaAuxilio());
		clear();
	}

	public void carregarRestricoes(ValueChangeEvent evento) throws HibernateException, DAOException {
		RestricaoSolicitacaoBolsaAuxilioDao dao = getDAO(RestricaoSolicitacaoBolsaAuxilioDao.class);
		int idEscolhido = (Integer) evento.getNewValue();
		try {
			clear();
			obj.setRestricao(new ArrayList<RestricaoSolicitacaoBolsaAuxilio>());
			obj.setRestricao( dao.findAllRestricoes(new TipoBolsaAuxilio(idEscolhido) )); 
			lista = new ListDataModel((List<RestricaoSolicitacaoBolsaAuxilio>) obj.getRestricao()); 
			if ( lista.isRowAvailable() )
				setConfirmButton("Alterar");
			else 
				setConfirmButton("Cadastrar");
		} finally {
			dao.close();
		}
	}
	
	public String adicionarRestricaoBolsa() throws DAOException {
		RestricaoSolicitacaoBolsaAuxilioDao dao = getDAO(RestricaoSolicitacaoBolsaAuxilioDao.class);
		try {
			ListaMensagens listaMsgs = obj.validate(restricao);
			if (listaMsgs != null && !listaMsgs.isEmpty()) {
				addMensagens(listaMsgs);
				return null;
			} else {
				addRestricaoBolsa(dao);
			}
		} finally {
			dao.close();
			lista = new ListDataModel((List<RestricaoSolicitacaoBolsaAuxilio>) obj.getRestricao() ); 
		}
		
		return null;
	}
	
	private void addRestricaoBolsa(RestricaoSolicitacaoBolsaAuxilioDao dao) throws DAOException {
		restricao.setBolsasAuxilioRestricao(
			dao.findByPrimaryKey(restricao.getTipoBolsaAuxilio().getId(), TipoBolsaAuxilio.class));
		restricao.setSituacao(
				dao.findByPrimaryKey(restricao.getSituacao().getId(), SituacaoBolsaAuxilio.class));
		restricao.setTipoBolsaAuxilio(
				dao.findByPrimaryKey(obj.getTipoBolsaAuxilio().getId(), TipoBolsaAuxilio.class));
		restricao.setAtivo(Boolean.TRUE);
		restricao.setDataCadastro(new Date());
		restricao.setRegistroCadastro(getUsuarioLogado().getRegistroEntrada());
		obj.getRestricao().add(restricao);
		clear();
	}
	
	public void removerRestricao() {
		RestricaoSolicitacaoBolsaAuxilio restricao = (RestricaoSolicitacaoBolsaAuxilio) lista.getRowData();
		obj.getRestricao().remove(restricao);
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		clearAll();
		obj.setRestricao(new ArrayList<RestricaoSolicitacaoBolsaAuxilio>());
		return forward("/sae/RestricaoSolicitacaoBolsa/form.jsp");
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_RESTRICAO_SAE);
			prepareMovimento(SigaaListaComando.CADASTRAR_RESTRICAO_SAE);
			execute(mov);
			if (isReprepare())
				prepareMovimento(SigaaListaComando.CADASTRAR_RESTRICAO_SAE);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Restrição");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		return preCadastrar();
	}

	public DataModel getLista() {
		return lista;
	}

	public void setLista(DataModel lista) {
		this.lista = lista;
	}

	public RestricaoSolicitacaoBolsaAuxilio getRestricao() {
		return restricao;
	}

	public void setRestricao(RestricaoSolicitacaoBolsaAuxilio restricao) {
		this.restricao = restricao;
	}

}