/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 28/08/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.GrupoOptativas;
import br.ufrn.sigaa.ensino.graduacao.negocio.DiscenteCalculosHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;

/**
 *
 * MBean para validar se os cálculos do discente estão feito corretamente
 *
 * @author Andre Dantas
 *
 */
public class CalculosDiscenteMBean extends SigaaAbstractController<Object> {

	/**
	 * Índices acadêmicos calculados para o discente
	 * selecionado.
	 */
	private List<IndiceAcademicoDiscente> indices;
	
	/**
	 * Discente selecionado para ser calculado na graduação
	 */
	private DiscenteGraduacao discente;

	/**
	 * Discente selecionado para ser calculado em stricto
	 */
	private DiscenteStricto discenteStricto;
	
	/**
	 * Opção de calculo usado na graduação
	 */
	private String opcao;

	/**
	 * Matriculas
	 */
	private Collection<MatriculaComponente> matriculas;

	/**
	 * Grupo de optativas
	 */
	private List<GrupoOptativas> gruposOptativas;

	/** Se irá utilizar os novos cálculos (após o refactoring, com o regulamento novo e novos índices) ou os cálculos antigos */
	private boolean novo;
	
	/** Se irá zerar as integralizações dos discentes antes de iniciar os cálculos */
	private boolean zerarIntegralizacoes;
	
	/**
	 * Inicializa objetos
	 */
	public CalculosDiscenteMBean() {

		discente = new DiscenteGraduacao();
		discenteStricto = new DiscenteStricto();
		
		matriculas = new ArrayList<MatriculaComponente>(0);
	}

	/**
	 * Inicia o fluxo para alunos de graduação
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
	 * 	<li>/graduacao/calculos_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.CDP);
		opcao = "todos";
		novo = true;
		
		return forward("/graduacao/calculos_discente.jsp");
	}

	/**
	 * Inicia o fluxo para alunos de pós-graduação stricto sensu
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/stricto/calculo_discente_stricto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarStricto() throws ArqException {
		discenteStricto = new DiscenteStricto();
		checkRole(SigaaPapeis.PPG);
		opcao = "todos";
		novo = true;
		prepareMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE_STRICTO);
		return forward("/stricto/calculo_discente_stricto.jsp");
	}
	
	/**
	 * Faz os cálculos para graduação
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/graduacao/calculos_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String calcular() throws ArqException, NegocioException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		
		try {
			discente = dao.findByPrimaryKey(discente.getId(), DiscenteGraduacao.class);
	
			if (discente == null ) {
				discente = new DiscenteGraduacao();
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Discente");
				return null;
			}
	
	
			if (!StatusDiscente.getStatusComVinculo().contains(discente.getStatus())) {
				addMensagemErro("Os cálculos de discente são permitidos à discente ativos, formandos, graduandos e trancados");
				return null;
			}
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setObjMovimentado(discente);
			mov.setObjAuxiliar(new Object[] { zerarIntegralizacoes, novo });
			
			if (novo) {
				prepareMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE);
				mov.setCodMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE);
			} else {
				prepareMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE_ANTIGO_REGULAMENTO);
				mov.setCodMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE_ANTIGO_REGULAMENTO);
			}
			
			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
			} catch (NegocioException ne) {
				addMensagemErro(ne.getMessage());
				return null;
			} catch (Exception e) {
				addMensagemErroPadrao();
				notifyError(e);
				return null;
			}
	
			dao.clearSession();
			matriculas = dao.findByDiscente(discente.getDiscente(), SituacaoMatricula.getSituacoesPagasEMatriculadas());
			discente = getGenericDAO().findByPrimaryKey(discente.getId(), DiscenteGraduacao.class);
			if (novo) {
				indices = getDAO(IndiceAcademicoDao.class).findIndicesAcademicoDiscente(discente.getDiscente());
			}
			
			gruposOptativas = new ArrayList<GrupoOptativas>();
			Map<GrupoOptativas, Integer> gruposMap = DiscenteCalculosHelper.verificaGruposOptativas(discente, SituacaoMatricula.getSituacoesPagas());
			for (Entry<GrupoOptativas, Integer> item : gruposMap.entrySet()) {
				GrupoOptativas grupo = item.getKey();
				grupo.setChPendente(item.getValue());
				gruposOptativas.add(grupo);
			}
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} finally {
			dao.close();
		}
		
		return null;
	}

	/**
	 * Faz os cálculos para o discente de stricto
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
	 * 	<li>/stricto/calculo_discente_stricto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String calcularStricto() throws ArqException {
		
		if(discenteStricto.getPessoa().getNome() == null || discenteStricto.getPessoa().getNome().trim().equals("")){
			addMensagemErro("Campo discente vazio.");
			return null;
		}
		
		discenteStricto = getGenericDAO().findByPrimaryKey(discenteStricto.getId(), DiscenteStricto.class);
		
		if(discenteStricto == null){
			addMensagemErro("Discente não encontrado.");
			return null;
		}
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setCodMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE_STRICTO);
			mov.setObjMovimentado(discenteStricto);			
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMensagemInformation("Cálculos realizados com sucesso.");
		} catch (NegocioException e) {
			addMensagemErro("Erro durante os cálculos");
			return null;
		}
		return cancelar();
	}
	
	@Override
	public String cancelar() {
		matriculas = null;
		gruposOptativas = null;
		discente = new DiscenteGraduacao();
		
		return super.cancelar();
	}
	
	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public String getOpcao() {
		return opcao;
	}

	public void setOpcao(String opcao) {
		this.opcao = opcao;
	}

	public Collection<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(Collection<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	public List<GrupoOptativas> getGruposOptativas() {
		return gruposOptativas;
	}

	public void setGruposOptativas(List<GrupoOptativas> gruposOptativas) {
		this.gruposOptativas = gruposOptativas;
	}

	public DiscenteStricto getDiscenteStricto() {
		return discenteStricto;
	}

	public void setDiscenteStricto(DiscenteStricto discenteStricto) {
		this.discenteStricto = discenteStricto;
	}

	public List<IndiceAcademicoDiscente> getIndices() {
		return indices;
	}

	public void setIndices(List<IndiceAcademicoDiscente> indices) {
		this.indices = indices;
	}

	public boolean isNovo() {
		return novo;
	}

	public void setNovo(boolean novo) {
		this.novo = novo;
	}

	public boolean isZerarIntegralizacoes() {
		return zerarIntegralizacoes;
	}

	public void setZerarIntegralizacoes(boolean zerarIntegralizacoes) {
		this.zerarIntegralizacoes = zerarIntegralizacoes;
	}
	
}
