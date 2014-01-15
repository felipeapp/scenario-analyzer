package br.ufrn.sigaa.assistencia.jsf;

import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioAuxiliarDao;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioPeriodo;
import br.ufrn.sigaa.assistencia.dominio.ResidenciaUniversitaria;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.negocio.BolsaAuxilioValidation;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

@Component @Scope("session")
public class BuscarBolsaAuxilioMBean extends SigaaAbstractController<BolsaAuxilio> implements OperadorDiscente {

	private Object bolsaAuxiliar;
	private Collection<TipoBolsaAuxilio> tiposBolsa;
	private List<BolsaAuxilioPeriodo> bolsas;
	private DiscenteAdapter discente;
	
	public BuscarBolsaAuxilioMBean() {
		clear();
	}

	private void clear() {
		obj = new BolsaAuxilio();
		obj.setTipoBolsaAuxilio(new TipoBolsaAuxilio());
		obj.setSituacaoBolsa(new SituacaoBolsaAuxilio());
		obj.setDiscente(new Discente());
		obj.setResidencia(new ResidenciaUniversitaria());
	}
	
	private String listarTodasSolicitacoesBolsaAuxilioPorAluno(int idOperacaoDiscente) {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(idOperacaoDiscente);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Exibe todas as solicitações de Bolsa Auxílio feitas por um determinado aluno
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> /sigaa.war/sae/menu.jsp </ul>
	 * @throws SegurancaException 
	 */
	public String iniciarNovaSolicitacao() throws SegurancaException {
		checkRole(SigaaPapeis.SAE_COORDENADOR);
		clear();
		return listarTodasSolicitacoesBolsaAuxilioPorAluno(OperacaoDiscente.SOLICITAR_BOLSA_AUXILIO);
	}
	
	/**
	 * Consulta todas as bolsas solicitadas pelo Discente
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> /sigaa.war/sae/menu.jsp </ul>
	 * @throws SegurancaException 
	 */
	public String consultarBolsasAuxilio() throws SegurancaException {
		checkRole(SigaaPapeis.SAE_COORDENADOR);
		clear();
		return listarTodasSolicitacoesBolsaAuxilioPorAluno(OperacaoDiscente.LISTA_BOLSA_AUXILIO);
	}

	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj.setDiscente(discente.getDiscente());
	}

	@Override
	public String selecionaDiscente() throws ArqException {
		if ( isNovaSolicitacao() ) {
			tiposBolsa = getGenericDAO().findAll(TipoBolsaAuxilio.class,new String[] {"denominacao"}, new String[] {"asc"});
			return forward("/sae/BolsaAuxilio/selecionar_bolsa.jsf");
		} else {
			BolsaAuxilioDao dao = getDAO(BolsaAuxilioDao.class);
			DiscenteDao discenteDao = getDAO(DiscenteDao.class);
			try {
				bolsas = dao.findAllSolicitacoesBolsaAuxilio(obj.getDiscente().getId());
				if ( !bolsas.isEmpty() ) {
					discente = discenteDao.findByPK(obj.getDiscente().getId());
					return forward("/sae/BolsaAuxilio/minhas_bolsas.jsp");
				} else {
					addMensagemErro("Não existe bolsa cadastrada para o discente selecionado.");
					return null;
				}
			} finally {
				dao.close();
				discenteDao.close();
			}
		}
	}
	
	@Override
	public String atualizar() throws ArqException {
		BolsaAuxilioDao dao = getDAO(BolsaAuxilioDao.class);
		try {
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_BOLSA_AUXILIO.getId());
			setId();
			setObj( dao.findByPrimaryKey(obj.getId(), BolsaAuxilio.class) );
			carregarBolsasAuxiliares();
			BolsaAuxilioMBean mBean = getMBean("bolsaAuxilioMBean");
			mBean.setObj(obj);
			mBean.setBolsaAuxiliar( bolsaAuxiliar );
			mBean.setConfirmButton("Alterar");
			return mBean.direcionar();
		} finally {
			dao.close();
		}
	}

	private void carregarBolsasAuxiliares() throws HibernateException, DAOException {
		if ( obj.getTipoBolsaAuxilio().isCreche() || obj.getTipoBolsaAuxilio().isAtleta() ) {
			BolsaAuxilioAuxiliarDao dao = getDAO(BolsaAuxilioAuxiliarDao.class);
			try {
				if ( obj.getTipoBolsaAuxilio().isCreche() ) {
					bolsaAuxiliar = dao.findBolsaAuxilioCrecheByIdBolsaAuxilio( obj.getId() );  
				} else if ( obj.getTipoBolsaAuxilio().isAtleta() ) {
					bolsaAuxiliar = dao.findBolsaAuxilioAtletaByIdBolsaAuxilio( obj.getId() );
				}
			} finally {
				dao.close();
			}
		}
	}
	
	public String selecionaTipoBolsa() throws DAOException{
		obj.getTipoBolsaAuxilio().setId( getParameterInt("id", 0) );
		obj.setTipoBolsaAuxilio( getGenericDAO().findByPrimaryKey(obj.getTipoBolsaAuxilio().getId(), TipoBolsaAuxilio.class) );
		obj.setTermoConcordancia(Boolean.TRUE);
		BolsaAuxilioValidation.validacaoDadosBasico(getUsuarioLogado(), obj, erros);
		BolsaAuxilioValidation.validacaoBolsaPromisaes(obj, erros);
		
		if ( hasErrors() ) {
			addMensagens(erros);
			return null;
		}
		
		setConfirmButton("Cadastrar");
		BolsaAuxilioMBean mBean = getMBean("bolsaAuxilioMBean");
		mBean.clear();
		mBean.setObj(obj);
		return mBean.solicitacaoBolsaAuxilio();		
	}
	
	private boolean isNovaSolicitacao() {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		return buscaDiscenteMBean.getCodigoOperacao() == OperacaoDiscente.SOLICITAR_BOLSA_AUXILIO;
	}
	
	public Object getBolsaAuxiliar() {
		return bolsaAuxiliar;
	}

	public void setBolsaAuxiliar(Object bolsaAuxiliar) {
		this.bolsaAuxiliar = bolsaAuxiliar;
	}

	public Collection<TipoBolsaAuxilio> getTiposBolsa() {
		return tiposBolsa;
	}

	public void setTiposBolsa(Collection<TipoBolsaAuxilio> tiposBolsa) {
		this.tiposBolsa = tiposBolsa;
	}

	public List<BolsaAuxilioPeriodo> getBolsas() {
		return bolsas;
	}

	public void setBolsas(List<BolsaAuxilioPeriodo> bolsas) {
		this.bolsas = bolsas;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}
	
}