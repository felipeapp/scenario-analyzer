/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/03/2007
 */
package br.ufrn.sigaa.monitoria.jsf;

import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.AtividadeMonitorDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.EnvioFrequencia;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Managed-Bean para tratar os quantitativos de projetos e o detalhamento do
 * mesmo
 *
 * @author Gleydson
 * @author ilueny
 *
 */

@Component("quantMonitoria")
@Scope("request")
public class QuantitativosMonitoriaMBean extends SigaaAbstractController<Object> {

	/** usado na listagem e atualizaçao dos quantitativos de projetos */
	private long cadastrados;
	private int idEdital;
	private long abertos;
	private long aguardandoAutDepartamentos;
	private long naoAutorizadosDepartamentos;
	private long aguardandoDistribuicao;
	private long aguardandoAvaliacaoPrograd;
	private long discrepantes;
	private long recomendados;
	private long naoRecomendados;
	private int monitoresAtivos;
	private int bolsistasAtivos;
	private int naoRemunerados;
	private int atividadesEnviadas;
	private int atividadesValidadasOrientador;
	private EnvioFrequencia envioFrequencia;
	private long emAndamento;	
	
	



	/**
	 * Usado para detalhar a lista
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul> 
	 * 	<li>sigaa.war\monitoria\Relatorios\informativo_sintetico.jsp</li>
	 *	<li>sigaa.war\menu_pesquisa.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String detalhaLista() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA , SigaaPapeis.PORTAL_PLANEJAMENTO); 

		ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
		String tipo = getParameter("atributoADetalhar");
		Integer idStatus = null;
		if (tipo != null) {

			if (tipo.equals("projetosCadastrados")) {
				idStatus = null;
			}

			if (tipo.equals("avaliacaoDepartamento")) {
				idStatus = TipoSituacaoProjeto.MON_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS;
				getCurrentRequest()
						.setAttribute(
								"status",
								dao
										.findByPrimaryKey(
												TipoSituacaoProjeto.MON_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS,
												TipoSituacaoProjeto.class));
			}

			if (tipo.equals("projetosAutorizados")) {
				idStatus = TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO;
				getCurrentRequest()
				.setAttribute(
						"status",
						dao
								.findByPrimaryKey(
										TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO,
										TipoSituacaoProjeto.class));
			}

			if (tipo.equals("projetosDiscrepantes")) {

			}
		}

		getCurrentSession().setAttribute(
				"projetos",
				dao.filter(null, null, idStatus, null, idEdital, null, null, null, null, null, null, true));

		return forward("/monitoria/ProjetoEnsino/detalhamento_quantitativo.jsp");

	}

	public int getIdEdital() {
		return idEdital;
	}

	public void setIdEdital(int idEdital) {
		this.idEdital = idEdital;
	}
	
	/**
	 * Atualiza detalhes dos projetos.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/Relatorios/informativo_sintetico.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	@SuppressWarnings("deprecation")
	public void refreshDados(ValueChangeEvent evt) throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA , SigaaPapeis.PORTAL_PLANEJAMENTO);

		Integer idEdital = 0;
		try {
			idEdital = new Integer(evt.getNewValue().toString());
		} catch (Exception e) {
			notifyError(e);
		}

		if (idEdital != null && idEdital != 0) {

			ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
			DiscenteMonitoriaDao daoDis = getDAO(DiscenteMonitoriaDao.class);
			AtividadeMonitorDao daoAtv = getDAO(AtividadeMonitorDao.class);

			
			cadastrados = dao.getTotalByStatus(-1, idEdital);
			
			

			abertos = dao.getTotalByStatus(
					TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO, idEdital);

			
			
			aguardandoAutDepartamentos = dao.getTotalByStatus(
							TipoSituacaoProjeto.MON_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS, idEdital);
			

			
			naoAutorizadosDepartamentos = dao.getTotalByStatus(
					TipoSituacaoProjeto.MON_NAO_AUTORIZADO_PELOS_DEPARTAMENTOS_ENVOLVIDOS, idEdital);

			
			
			aguardandoDistribuicao = dao.getTotalByStatus(
					TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_PROJETO, idEdital);


			
			aguardandoAvaliacaoPrograd = dao.getTotalByStatus(
					TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO, idEdital);
			

			
			discrepantes = dao.getTotalByStatus(TipoSituacaoProjeto.MON_AVALIADO_COM_DISCREPANCIA_DE_NOTAS, idEdital);



			emAndamento = dao.getTotalByStatus(
					TipoSituacaoProjeto.MON_EM_EXECUCAO, idEdital);

			
			
			recomendados = dao.getTotalByStatus(
					TipoSituacaoProjeto.MON_RECOMENDADO, idEdital);
			
			
			naoRecomendados = dao.getTotalByStatus(
					TipoSituacaoProjeto.MON_NAO_RECOMENDADO, idEdital);

			
			monitoresAtivos =  daoDis.countMonitoresByEdital(idEdital, null);

			
			bolsistasAtivos =  daoDis.countMonitoresByEdital(idEdital, 
				TipoVinculoDiscenteMonitoria.BOLSISTA, SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA, true);

			
			naoRemunerados =  daoDis.countMonitoresByEdital(idEdital, 
				TipoVinculoDiscenteMonitoria.NAO_REMUNERADO, SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA, true);
			

			 // verificando se o discente já cadastrou a atividade do mês atual, caso afirmativo popular o formulário com a atividade 
			 // cadastrada pois ele não pode cadastrar duas atividades para um mesmo mês.
			AtividadeMonitorDao daoAtividade = getDAO( AtividadeMonitorDao.class );		
			envioFrequencia = daoAtividade.findByExactField(EnvioFrequencia.class, "ativo", Boolean.TRUE).iterator().next();
			atividadesEnviadas =  daoAtv.countAtividadesByEditalMesAno(idEdital, envioFrequencia.getMes(), envioFrequencia.getAno(), null);
			atividadesValidadasOrientador =  daoAtv.countAtividadesByEditalMesAno(idEdital, envioFrequencia.getMes(), envioFrequencia.getAno(), true);
			
		}

	}

	public long getAbertos() {
		return abertos;
	}

	public void setAbertos(long abertos) {
		this.abertos = abertos;
	}

	public long getAguardandoAutDepartamentos() {
		return aguardandoAutDepartamentos;
	}

	public void setAguardandoAutDepartamentos(long aguardandoAutDepartamentos) {
		this.aguardandoAutDepartamentos = aguardandoAutDepartamentos;
	}

	public long getAguardandoAvaliacaoPrograd() {
		return aguardandoAvaliacaoPrograd;
	}

	public void setAguardandoAvaliacaoPrograd(long aguardandoAvaliacaoPrograd) {
		this.aguardandoAvaliacaoPrograd = aguardandoAvaliacaoPrograd;
	}

	public long getAguardandoDistribuicao() {
		return aguardandoDistribuicao;
	}

	public void setAguardandoDistribuicao(long aguardandoDistribuicao) {
		this.aguardandoDistribuicao = aguardandoDistribuicao;
	}

	public int getAtividadesEnviadas() {
		return atividadesEnviadas;
	}

	public void setAtividadesEnviadas(int atividadesEnviadas) {
		this.atividadesEnviadas = atividadesEnviadas;
	}

	public int getAtividadesValidadasOrientador() {
		return atividadesValidadasOrientador;
	}

	public void setAtividadesValidadasOrientador(int atividadesValidadasOrientador) {
		this.atividadesValidadasOrientador = atividadesValidadasOrientador;
	}

	public int getBolsistasAtivos() {
		return bolsistasAtivos;
	}

	public void setBolsistasAtivos(int bolsistasAtivos) {
		this.bolsistasAtivos = bolsistasAtivos;
	}

	public long getCadastrados() {
		return cadastrados;
	}

	public void setCadastrados(long cadastrados) {
		this.cadastrados = cadastrados;
	}

	public long getDiscrepantes() {
		return discrepantes;
	}

	public void setDiscrepantes(long discrepantes) {
		this.discrepantes = discrepantes;
	}

	public int getMonitoresAtivos() {
		return monitoresAtivos;
	}

	public void setMonitoresAtivos(int monitoresAtivos) {
		this.monitoresAtivos = monitoresAtivos;
	}

	public long getNaoAutorizadosDepartamentos() {
		return naoAutorizadosDepartamentos;
	}

	public void setNaoAutorizadosDepartamentos(long naoAutorizadosDepartamentos) {
		this.naoAutorizadosDepartamentos = naoAutorizadosDepartamentos;
	}

	public long getNaoRecomendados() {
		return naoRecomendados;
	}

	public void setNaoRecomendados(long naoRecomendados) {
		this.naoRecomendados = naoRecomendados;
	}

	public int getNaoRemunerados() {
		return naoRemunerados;
	}

	public void setNaoRemunerados(int naoRemunerados) {
		this.naoRemunerados = naoRemunerados;
	}

	public long getRecomendados() {
		return recomendados;
	}

	public void setRecomendados(long recomendados) {
		this.recomendados = recomendados;
	}

	public EnvioFrequencia getEnvioFrequencia() {
		return envioFrequencia;
	}

	public void setEnvioFrequencia(EnvioFrequencia envioFrequencia) {
		this.envioFrequencia = envioFrequencia;
	}

	public long getEmAndamento() {
		return emAndamento;
	}

	public void setEmAndamento(long emAndamento) {
		this.emAndamento = emAndamento;
	}


	

	
}
