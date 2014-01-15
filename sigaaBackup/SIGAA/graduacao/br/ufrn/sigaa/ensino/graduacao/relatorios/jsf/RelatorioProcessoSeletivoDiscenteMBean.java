/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 19/01/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioProcessoSeletivoDiscenteDao;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDao;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Controller responsável pela geração de diversos relatórios de processo seletivo relacionados aos discentes.
 * @author Rafael Gomes
 *
 */
@Component("relatorioProcessoSeletivoDiscente") @Scope("request")
public class RelatorioProcessoSeletivoDiscenteMBean extends AbstractRelatorioGraduacaoMBean {

	// 	Dados do relatório.
	
	/** Representa os Laureados para o UC Lista de Laureados. */
	private List<Map<String,Object>> listaDiscente = new ArrayList<Map<String,Object>>();
	
	/** Chamada que compõe o código de migração da COMPERVE de um discente. */
	private Integer chamada;
	/** Convocação a qual o discente recebeu para realizar seu cadastramento. */
	private Integer convocacao;
	/** Processo Seletivo da migração da COMPERVE de um discente. */
	private Integer processoSeletivo;
	/** Descrição da chamada que compõe o código de migração da COMPERVE de um discente. */
	private String descricaoChamada;
	/** Descrição do processo seletivo que compõe o código de migração da COMPERVE de um discente. */
	private String descricaoProcessoSeletivo;
	/** Armazena um seleção de chamadas */
	private Collection<SelectItem> chamadas = new ArrayList<SelectItem>(0);
	
	/** String para armazenar ao texto da chamada que será concatenado a ordem da chamada.*/
	private static final String TEXTO_CHAMADA = "ª Chamada";
	
	// JSPs
	/** Link para geração de um relatório de alunos já na UFRN, mas ingressantes em outros cursos. */
	private static final String JSP_SELECIONA_ALUNOS_CONVOCADOS_EXCLUIDOS = "seleciona_aluno_convocado_excluido.jsp";
	/** Link do relatório de alunos da UFRN ingressantes em outro curso. */
	private static final String JSP_RELATORIO_ALUNOS_CONVOCADOS_EXCLUIDOS = "lista_alunos_convocados_excluidos.jsp";
	
	
	/** Construtor padrão. */
	public RelatorioProcessoSeletivoDiscenteMBean(){
		initObj();
		setAmbito("discente/");
	}
	
	
	/**
	 * Método Responsável por retornar uma lista contendo os alunos convocados pelo processo seletivo,
	 * que foram excluídos durante o período de convocação dos candidatos aprovados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sigaa.war/graduacao/relatorios/discente/seleciona_aluno_convocado_excluido.jsp
	 * </li></ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String alunosConvocadosExcluidos() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		
		RelatorioProcessoSeletivoDiscenteDao dao = getDAO(RelatorioProcessoSeletivoDiscenteDao.class);
		ConvocacaoProcessoSeletivo convocacaoPS = new ConvocacaoProcessoSeletivo(convocacao);
		
		if( isEmpty( processoSeletivo ) ){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Processo Seletivo");
			return null;
		} else if ( convocacao == 0 ){
			ProcessoSeletivoVestibular ps = new ProcessoSeletivoVestibular(processoSeletivo);
			ps = dao.refresh(ps);
			descricaoProcessoSeletivo = ps.getNome();
			descricaoChamada = "TODAS";
		} else {
			convocacaoPS = dao.refresh(convocacaoPS);
			descricaoProcessoSeletivo = convocacaoPS.getProcessoSeletivo().getNome();
			descricaoChamada = convocacaoPS.getDescricao();
		}	
		
		listaDiscente = dao.findalunosConvocadosExcluidos(convocacaoPS, processoSeletivo);
		
		if (listaDiscente.size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return forward(CONTEXTO + "discente/" + JSP_SELECIONA_ALUNOS_CONVOCADOS_EXCLUIDOS);
		}
		
		return forward(CONTEXTO + "discente/" + JSP_RELATORIO_ALUNOS_CONVOCADOS_EXCLUIDOS);
	}
	
	/**
	 * Tem função de carregar as chamadas do processo seletivo selecionado.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/relatorios/discente/seleciona_aluno_ingressante_outro_curso.jsp</li>
	 *  <li>/sigaa.war/graduacao/relatorios/discente/seleciona_aluno_convocado_excluido.jsp</li>
	 * </ul>
	 * 
	 * @param vce
	 * @throws DAOException
	 */
	public void carregarChamadas() throws DAOException {
		
		ConvocacaoProcessoSeletivoDao convocacaoDao = getDAO(ConvocacaoProcessoSeletivoDao.class);
		convocacao = 0;
		if (processoSeletivo == 0) {
			chamadas = new ArrayList<SelectItem>();
		} else {
			try {
				Collection<ConvocacaoProcessoSeletivo> convocacoesProcessoSeletivo = convocacaoDao.findByProcessoSeletivo(processoSeletivo);
				chamadas = new ArrayList<SelectItem>();
				chamadas.addAll(toSelectItems(convocacoesProcessoSeletivo, "id", "descricao"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * @return the listaDiscente
	 */
	public List<Map<String, Object>> getListaDiscente() {
		return listaDiscente;
	}


	/**
	 * @param listaDiscente the listaDiscente to set
	 */
	public void setListaDiscente(List<Map<String, Object>> listaDiscente) {
		this.listaDiscente = listaDiscente;
	}


	/**
	 * @return the chamada
	 */
	public Integer getChamada() {
		return chamada;
	}


	/**
	 * @param chamada the chamada to set
	 */
	public void setChamada(Integer chamada) {
		this.chamada = chamada;
	}


	/**
	 * @return the convocacao
	 */
	public Integer getConvocacao() {
		return convocacao;
	}


	/**
	 * @param convocacao the convocacao to set
	 */
	public void setConvocacao(Integer convocacao) {
		this.convocacao = convocacao;
	}


	/**
	 * @return the processoSeletivo
	 */
	public Integer getProcessoSeletivo() {
		return processoSeletivo;
	}


	/**
	 * @param processoSeletivo the processoSeletivo to set
	 */
	public void setProcessoSeletivo(Integer processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}


	/**
	 * @return the descricaoChamada
	 */
	public String getDescricaoChamada() {
		return descricaoChamada;
	}


	/**
	 * @param descricaoChamada the descricaoChamada to set
	 */
	public void setDescricaoChamada(String descricaoChamada) {
		this.descricaoChamada = descricaoChamada;
	}


	/**
	 * @return the descricaoProcessoSeletivo
	 */
	public String getDescricaoProcessoSeletivo() {
		return descricaoProcessoSeletivo;
	}


	/**
	 * @param descricaoProcessoSeletivo the descricaoProcessoSeletivo to set
	 */
	public void setDescricaoProcessoSeletivo(String descricaoProcessoSeletivo) {
		this.descricaoProcessoSeletivo = descricaoProcessoSeletivo;
	}


	/**
	 * @return the chamadas
	 */
	public Collection<SelectItem> getChamadas() {
		return chamadas;
	}


	/**
	 * @param chamadas the chamadas to set
	 */
	public void setChamadas(Collection<SelectItem> chamadas) {
		this.chamadas = chamadas;
	}
	
}
