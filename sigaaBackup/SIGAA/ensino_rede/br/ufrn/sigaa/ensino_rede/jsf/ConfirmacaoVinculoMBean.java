package br.ufrn.sigaa.ensino_rede.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino_rede.dao.ConfirmacaoVinculoDao;
import br.ufrn.sigaa.ensino_rede.dominio.ConvocacaoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.OfertaCursoAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.negocio.GrupoDiscentesAlterados;
import br.ufrn.sigaa.ensino_rede.negocio.MovimentoAlterarStatusMatriculaRede;

@Component @Scope("session")
public class ConfirmacaoVinculoMBean extends EnsinoRedeAbstractController<DiscenteAssociado> implements SelecionaCampus {

	private static final String CONVOCACAO = "/ensino_rede/confirmacao_vinculo/convocacao.jsp";
	private static final String RESUMO = "/ensino_rede/confirmacao_vinculo/resumo.jsp";;

	private List<DiscenteAssociado> discentes;
	private List<OfertaCursoAssociado> ofertas;
	private CampusIes campus;
	private ConvocacaoDiscenteAssociado convocacao;
	
	public ConfirmacaoVinculoMBean() {
		clear();
	}

	private void clear() {
		discentes = new ArrayList<DiscenteAssociado>();
		campus = new CampusIes();
		convocacao = new ConvocacaoDiscenteAssociado();
		setOperacaoAtiva(SigaaListaComando.ALTERAR_SITUACAO_DISCENTE.getId());
	}
	
	public String iniciarCoordenadorCampus() throws ArqException {
		clear();
		setCampus(getCampusIes());
		return selecionaCampus();
	}

	public String iniciarGestor() {
		clear();
		SelecionaCampusIesMBean mBean = getMBean("selecionaCampusRedeMBean");
		mBean.setRequisitor(this);
		return mBean.iniciar();
	}

	@Override
	public void setCampus(CampusIes campus) throws ArqException {
		this.campus = campus;
	}

	@Override
	public String selecionaCampus() throws ArqException {
		return forward(CONVOCACAO);
	}

	public String resumo() throws ArqException {
		if ( getDiscenteConfirmados().isEmpty() && getDiscenteNaoConfirmados().isEmpty() ) {
			addMensagemErro("Não foi encontrado nenhum Discente Pre Cadastrado para a convocação Selecionada.");
			return null;
		}
		
		return forward(RESUMO);
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		if(!isOperacaoAtiva(SigaaListaComando.ALTERAR_SITUACAO_DISCENTE.getId())){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			return cancelar();			
		}

		GrupoDiscentesAlterados grupoConfirmado = new GrupoDiscentesAlterados();
		grupoConfirmado.setDiscentes(new HashSet<DiscenteAssociado>(getDiscenteConfirmados()));
		grupoConfirmado.setStatusNovo(new StatusDiscenteAssociado(StatusDiscenteAssociado.ATIVO));

		GrupoDiscentesAlterados grupoAusente = new GrupoDiscentesAlterados();
		grupoAusente.setDiscentes(new HashSet<DiscenteAssociado>(getDiscenteNaoConfirmados()));
		grupoAusente.setStatusNovo( new StatusDiscenteAssociado(StatusDiscenteAssociado.EXCLUIDO));		

		
		prepareMovimento(SigaaListaComando.ALTERAR_SITUACAO_DISCENTE);
		MovimentoAlterarStatusMatriculaRede movMatricula = new MovimentoAlterarStatusMatriculaRede();
		movMatricula.setColObjMovimentado(discentes);
		movMatricula.setCodMovimento(SigaaListaComando.ALTERAR_SITUACAO_DISCENTE);
		movMatricula.addGrupo(grupoConfirmado);
		movMatricula.addGrupo(grupoAusente);
		
		try {

			execute(movMatricula);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return cancelar();
			
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
		} finally {
			setOperacaoAtiva(null);
		} 
		
		return null;
	}
	
	public void carregaDiscentes() throws HibernateException, DAOException{
		discentes.clear();
		ConfirmacaoVinculoDao dao = getDAO(ConfirmacaoVinculoDao.class);
		try {
			discentes = dao.findDiscenteByCampusConvocacao(campus.getId(), convocacao.getId());
			
			if ( discentes.isEmpty() && convocacao.getId() > 0 )
				addMensagemWarningAjax("Não foi encontrado nenhum Discente Pre Cadastrado para a convocação Selecionada.");

		} finally {
			dao.close();
		}
	}
	
	public Collection<DiscenteAssociado> getDiscenteConfirmados() {
		Collection<DiscenteAssociado> discentesConfirmados = new ArrayList<DiscenteAssociado>();
		for (DiscenteAssociado discente : discentes) {
			if ( discente.isSelected() )
				discentesConfirmados.add(discente);
		}
		return discentesConfirmados;
	}
	
	public Collection<DiscenteAssociado> getDiscenteNaoConfirmados() {
		Collection<DiscenteAssociado> discentesNaoConfirmados = new ArrayList<DiscenteAssociado>();
		for (DiscenteAssociado discente : discentes) {
			if ( !discente.isSelected() )
				discentesNaoConfirmados.add(discente);
		}
		return discentesNaoConfirmados;
	}

	public Collection<SelectItem> getAllConvocacoesCombo() throws ArqException {
		ConvocacaoDiscenteAssociadoMBean mBean = getMBean("convocacaoDiscenteAssociadoMBean");
		return mBean.getAllConvocacoesByCampusCombo(campus.getId(), getProgramaRede().getId()); 
	}
	
	public List<DiscenteAssociado> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(List<DiscenteAssociado> discentes) {
		this.discentes = discentes;
	}

	public ConvocacaoDiscenteAssociado getConvocacao() {
		return convocacao;
	}

	public void setConvocacao(ConvocacaoDiscenteAssociado convocacao) {
		this.convocacao = convocacao;
	}

	public CampusIes getCampus() {
		return campus;
	}

	public List<OfertaCursoAssociado> getOfertas() {
		return ofertas;
	}

	public void setOfertas(List<OfertaCursoAssociado> ofertas) {
		this.ofertas = ofertas;
	}
	
}