package br.ufrn.sigaa.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.GrupoPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.HistoricoGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.StatusGrupoPesquisa;

/**
 * Controlador responsável pela emissão de parecer sobre proposta de criação de grupo de pesquisa. 
 * As instâncias que emitem pareceres sobre as propostas são:
 * 		1 - Departamento de origem da proposta;
 * 		2 - Centro Acadêmico ao qual o Departamento de origem está vinculado;
 * 		3 - Comissão de Pesquisa.
 * @author Jean Guerethes
 */
@Scope("session") @Component
public class AutorizacaoGrupoPesquisaMBean extends SigaaAbstractController<GrupoPesquisa> {

	/** DataModel utilizado para a exibição do grupo de pesquisa */
	private DataModel gruposPesquisa;
	/** Área de conhecimento do grupo de pesquisa */
	private AreaConhecimentoCnpq area = new AreaConhecimentoCnpq();
		
	public AutorizacaoGrupoPesquisaMBean() {
		clear();
	}

	/** Inicializa todos os atributos necessários para a realização das operaçõesl */
	private void clear() {
		obj = new GrupoPesquisa();
	}
	
	/**
	 * Realiza a busca por todas as Propostas de de Criação de Grupo de Pesquisa, que estão aguardando a 
	 * autorização do chefe de departamento.
	 *    
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String listarGruposPesquisaPendentes() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PESQUISA, SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.MEMBRO_COMITE_PESQUISA, SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO);
		GrupoPesquisaDao dao = getDAO(GrupoPesquisaDao.class);
		
		if(isPesquisa() && isUserInRole(SigaaPapeis.MEMBRO_COMITE_PESQUISA, SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.ADMINISTRADOR_PESQUISA))
			gruposPesquisa = new ListDataModel( 
					dao.findByUnidadeStatus(null, StatusGrupoPesquisa.APROVACAO_COMISSAO_PESQUISA) );
		else if(isPortalDocente() && isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO) && isUserInRole(SigaaPapeis.DIRETOR_CENTRO) 
				&& getUsuarioLogado().getVinculoAtivo().getUnidade().isUnidadeAcademicaEspecializada() ){
			gruposPesquisa = new ListDataModel( 
					dao.findByUnidadeCentroStatus(getUsuarioLogado().getVinculoAtivo().getUnidade(), StatusGrupoPesquisa.APROVACAO_DEPARTAMENTO, StatusGrupoPesquisa.APROVACAO_CENTRO) );
		} 
		else if( isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO) && isPortalDocente() && getUsuarioLogado().getVinculoAtivo().getUnidade().isDepartamento() )
			gruposPesquisa = new ListDataModel( 
					dao.findByUnidadeStatus(getUsuarioLogado().getVinculoAtivo().getUnidade(), StatusGrupoPesquisa.APROVACAO_DEPARTAMENTO) );
		else if( isUserInRole(SigaaPapeis.DIRETOR_CENTRO) && isPortalDocente() )
			gruposPesquisa = new ListDataModel( 
					dao.findByUnidadeCentroStatus(getUsuarioLogado().getVinculoAtivo().getUnidade(), StatusGrupoPesquisa.APROVACAO_CENTRO) );
		
		return forward(ConstantesNavegacaoPesquisa.AUTORIZACAO_GRUPO_PESQUISA_LISTA);
	}

	/** Carrega todos os dados presente na proposta de criação do grupo de pesquisa 
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/autorizacao/lista.jsp</li>
	 * </ul>
	 */
	public String visualizar() throws DAOException {
		obj = (GrupoPesquisa) gruposPesquisa.getRowData(); 
		carregarInformacoes(obj);
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_VISUALIZAR);
	}

	/**
	 * Responsável por carregar as informações necessárias para a exibição 
	 * 
	 * @param grupoPesquisa
	 * @throws DAOException
	 */
	private void carregarInformacoes(GrupoPesquisa grupoPesquisa) throws DAOException {
		PropostaGrupoPesquisaMBean mBean = getMBean("propostaGrupoPesquisaMBean");
		mBean.setObj(obj);
		mBean.carregarCamposLazy();
		setArea( mBean.getObj().getAreaConhecimentoCnpq().getGrandeArea() );
		setObj( mBean.getObj() );	
	}

	/**
	 * Método responsável por iniciar o envio do paracer do chefe de departamento 
	 * sobre a criação do grupo de pesquisa. 
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/autorizacao/lista.jsp</li>
	 * </ul>
	 */
	public String enviarParecer() throws ArqException {
		obj = (GrupoPesquisa) gruposPesquisa.getRowData();
		carregarInformacoes(obj);
		obj.setParecer(null);
		prepareMovimento(SigaaListaComando.ENVIAR_PROPOSTA_GRUPO_PESQUISA);
		setOperacaoAtiva(SigaaListaComando.ENVIAR_PROPOSTA_GRUPO_PESQUISA.getId());
		return forward(ConstantesNavegacaoPesquisa.AUTORIZACAO_GRUPO_PESQUISA_PARECER);
	}
	
	/**
	 * Carrega as opções de status possíveis de acordo com o perfil do usuário que está
	 * emitindo o parecer.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/GrupoPesquisa/autorizacao/parecer.jsp</li>
	 *	</ul> 
	 * @throws NegocioException 
	 */
	public Collection<SelectItem> getTiposStatusParecerCombo() throws NegocioException {
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(StatusGrupoPesquisa.NECESSITA_CORRECAO, StatusGrupoPesquisa.getTiposStatus().get(StatusGrupoPesquisa.NECESSITA_CORRECAO)));
		
		if(isUserInRole(SigaaPapeis.MEMBRO_COMITE_PESQUISA, SigaaPapeis.GESTOR_PESQUISA, SigaaPapeis.ADMINISTRADOR_PESQUISA) && getSubSistema().equals(SigaaSubsistemas.PESQUISA))
			lista.add(new SelectItem(StatusGrupoPesquisa.APROVADO, "Aprovado"));
		else {
			Integer tipoAcademica = getUsuarioLogado().getVinculoAtivo().getUnidade().getTipoAcademica();
			if (tipoAcademica != null && tipoAcademica.equals(TipoUnidadeAcademica.DEPARTAMENTO))
				lista.add(new SelectItem(StatusGrupoPesquisa.APROVACAO_CENTRO, "Aprovado"));
			else if (tipoAcademica != null && 
					(tipoAcademica.equals(TipoUnidadeAcademica.CENTRO)
					|| tipoAcademica.equals(TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA)))
				lista.add(new SelectItem(StatusGrupoPesquisa.APROVACAO_COMISSAO_PESQUISA, "Aprovado"));
			else
				throw new NegocioException("Unidade do usuário não identificada para esta operação. Verifique se a unidade do seu vínculo atual é um Departamento, Centro Acadêmico ou Unidade Acadêmica Especializada.");
		}
		return lista;
	}
	
	/**
	 * Método responsável pela emissão do parecer do chefe de departamento sobre a 
	 * proposta de criação de um grupo de pesquisa.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/autorizacao/parecer.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String emitirParecer() throws ArqException{
		
		checkOperacaoAtiva(SigaaListaComando.ENVIAR_PROPOSTA_GRUPO_PESQUISA.getId());
		if ( !hasErrors() ) {
			
			ValidatorUtil.validateRequiredId(obj.getStatus(), "Status", erros);
			ValidatorUtil.validateRequired(obj.getParecer(), "Parecer", erros);
			
			if ( hasOnlyErrors() ) {
				addMensagens(erros);
				return null;
			}
			
			try {
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
				mov.setUsuarioLogado(getUsuarioLogado());
				mov.setCodMovimento(SigaaListaComando.ENVIAR_PROPOSTA_GRUPO_PESQUISA);
				execute(mov);
				addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Parecer");
				removeOperacaoAtiva();
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			}
		}
		
		return listarGruposPesquisaPendentes();
	}
	
	public DataModel getGruposPesquisa() {
		return gruposPesquisa;
	}

	public void setGruposPesquisa(DataModel gruposPesquisa) {
		this.gruposPesquisa = gruposPesquisa;
	}

	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}
	
	/**
	 * Retorna o histórico de modificações de status realizadas no grupo de pesquisa.
	 *  
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/autorizacao/parecer.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<HistoricoGrupoPesquisa> getHistoricoGrupoPesquisa() throws DAOException{
		return getGenericDAO().findByExactField(HistoricoGrupoPesquisa.class, "grupoPesquisa.id", obj.getId(), "asc", "data");
	}
	
	/**
	 * Verifica se não há mais propostas de criação de grupos de pesquisa pendentes de parecer.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/autorizacao/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean getSemPendencias() {
		return gruposPesquisa != null && gruposPesquisa.getRowCount() == 0;
	}
}