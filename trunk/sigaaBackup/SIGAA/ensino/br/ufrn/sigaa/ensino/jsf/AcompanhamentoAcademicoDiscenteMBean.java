package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.jsf.NotificacoesMBean;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.jsf.BuscarBolsaAuxilioMBean;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.AtestadoMatriculaMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

@Component @Scope("session")
public class AcompanhamentoAcademicoDiscenteMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente{

	public AcompanhamentoAcademicoDiscenteMBean() {
		obj = new Discente();
	}
	
	public String iniciar() {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ACOMPANHAMENTO_ACADEMICO_DISCENTE);
		return buscaDiscenteMBean.popular();
	}
	
	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		setObj(discente.getDiscente());
	}

	@Override
	public String selecionaDiscente() throws ArqException {
		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		try {
			setObj( discenteDao.findByPK(obj.getDiscente().getId()) );
		} finally {
			discenteDao.close();
		}
		return forward("/ensino/acompanhamento_discente/view.jsf");
	}
	
	public String gerarHistorico() throws ArqException {
		HistoricoMBean historico = (HistoricoMBean) getMBean("historico");
		historico.setDiscente(obj);
		return historico.selecionaDiscente();
	}
	
	public String gerarAtestadoMatricula() throws DAOException, SegurancaException {
		AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
		getCurrentSession().setAttribute("atestadoLiberado", obj.getId());
		atestado.setDiscente(obj);
		return atestado.selecionaDiscente();
	}

	public String verBolsas() throws HibernateException, DAOException {
		BolsaAuxilioDao dao = getDAO(BolsaAuxilioDao.class);
		try {
			BuscarBolsaAuxilioMBean mBean = getMBean("buscarBolsaAuxilioMBean");
			mBean.setBolsas( dao.findAllSolicitacoesBolsaAuxilio(obj.getDiscente().getId()) );
			if ( !mBean.getBolsas().isEmpty() ) {
				return forward("/sae/BolsaAuxilio/minhas_bolsas.jsp");
			} else {
				addMensagemErro("Não existe bolsa cadastrada para o discente selecionado.");
				return null;
			}
		} finally {
			dao.close();
		}
	}

	public String enviarMensagem() throws NumberFormatException, ArqException{
		
		String nome = null;
		String email = null;
		int idUsuario = 0;
		
		Usuario usuario = getDAO(DiscenteDao.class).findByUsuario(obj.getPessoa().getId());
		String remetente =  getUsuarioLogado().getNome();
		
		nome = obj.getPessoa().getNome();
		email = obj.getPessoa().getEmail();
		idUsuario = usuario.getId() ;
		
		NotificacoesMBean notificacao = getMBean("notificacoes");
		ArrayList<Destinatario> destinatarios = new ArrayList<Destinatario>();	
		Destinatario destinatario = new Destinatario(nome, email);
		destinatario.setIdusuario(idUsuario);			
		destinatarios.add(destinatario);
		notificacao.setDestinatarios(destinatarios);
		notificacao.setRemetente( remetente );
		notificacao.setTitulo("Enviar mensagem ao Discente");
		notificacao.iniciar();
		return null;
	}
	
}