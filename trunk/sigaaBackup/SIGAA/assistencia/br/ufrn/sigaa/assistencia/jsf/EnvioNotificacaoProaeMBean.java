package br.ufrn.sigaa.assistencia.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.NotificacoesMBean;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;

@Component @Scope("session")
public class EnvioNotificacaoProaeMBean extends SigaaAbstractController<BolsaAuxilio> {

	/** Lista de bolsas auxílios . */
	private List<BolsaAuxilio> listaBolsaAuxilio;

	public EnvioNotificacaoProaeMBean() {
		clear();
	}

	private void clear() {
		obj = new BolsaAuxilio();
		listaBolsaAuxilio = new ArrayList<BolsaAuxilio>();
	}

	public String iniciarNotificacao() throws DAOException {
		clear();
		ConsultaBolsaAuxilioMBean mBean = getMBean("consultaBolsaAuxilioMBean");
		mBean.clear();
		return forward("/sae/NotificacaoDiscente/busca_discente.jsp");
	}
	
	/**
	 * Realiza a busca de Bolsa Auxílio de acordo com os critérios
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> sigaa.war/sae/busca_bolsa_auxilio.jsp </ul>
	 * @throws Exception 
	 */
	public String buscar() throws Exception {
		ConsultaBolsaAuxilioMBean mBean = getMBean("consultaBolsaAuxilioMBean");
		listaBolsaAuxilio = mBean.buscarBolsaAuxilio();
		if (listaBolsaAuxilio.size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		return null;
	}

	public String enviarMensagem() throws NumberFormatException, ArqException{
		ArrayList<Destinatario> destinatarios = new ArrayList<Destinatario>();	
		for (BolsaAuxilio bolsa : listaBolsaAuxilio) {
			if ( bolsa.getDiscente().isSelecionado() ) {
				bolsa.getDiscente().setUsuario(
						getDAO(DiscenteDao.class).findByUsuario(bolsa.getDiscente().getPessoa().getId()));
				Destinatario destinatario = new Destinatario(
						bolsa.getDiscente().getNome(), 
							bolsa.getDiscente().getPessoa().getEmail());
				destinatario.setIdusuario(bolsa.getDiscente().getUsuario().getId());			
				destinatarios.add(destinatario);
			}
		}
		
		NotificacoesMBean notificacao = getMBean("notificacoes");
		notificacao.setDestinatarios(destinatarios);
		notificacao.setRemetente(getUsuarioLogado().getNome());
		notificacao.setTitulo("Notificação PROAE");
		notificacao.iniciar();
		return null;
	}

	public List<BolsaAuxilio> getListaBolsaAuxilio() {
		return listaBolsaAuxilio;
	}

	public void setListaBolsaAuxilio(List<BolsaAuxilio> listaBolsaAuxilio) {
		this.listaBolsaAuxilio = listaBolsaAuxilio;
	}
	
}