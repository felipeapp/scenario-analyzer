/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 14/06/2011
 *
 */
package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ead.dao.CargaHorariaEadDAO;
import br.ufrn.sigaa.ead.dominio.CargaHorariaEad;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * Controlador respons�vel pelo formul�rio de cadastro de carga hor�ria dedicada
 * pelo docente no ensino � dist�ncia.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Component("cargaHorariaEadMBean") 
@Scope("session")
public class CargaHorariaEadMBean extends SigaaAbstractController<CargaHorariaEad> {
	
	/** Mapa de observa��es a serem informadas ao usu�rio sobre poss�veis mudan�as no registro que tornaram-se desatualizados. */
	private Map<Integer, StringBuilder> cadastrosDesatualizados;
	
	/** Cole��o de cadastros de carga hor�rias a serem removidas porque n�o h� mais turma para o componente curricular lecionada pelo docente. */
	private Collection<CargaHorariaEad> chRemovidas;
	
	/** Nome do docente para filtrar na lista de CH dedicadas. */
	private String nomeDocente;
	
	/** Contrutor padr�o. */
	public CargaHorariaEadMBean() {
		obj = new CargaHorariaEad();
	}

	/**
	 * Busca por carga hor�rias cadastradas em um ano-per�odo e inclui no
	 * resultado da busca, as cargas hor�rias que ainda n�o foram cadastradas.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/chDedicada/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		validateMinValue(obj.getAno(), ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ANO_INICIO_CADASTRO_CH_DEDICADA_EAD), "Ano", erros);
		validateMinValue(obj.getPeriodo(), 1, "Per�odo", erros);
		validateMaxValue(obj.getPeriodo(), 4, "Per�odo", erros);
		if (hasErrors()) return null;
		CargaHorariaEadDAO dao = getDAO(CargaHorariaEadDAO.class);
		int ano = obj.getAno();
		int periodo = obj.getPeriodo();
		Collection<CargaHorariaEad> antigos = dao.findAllPersistidoByAnoPeriodo(ano, periodo, nomeDocente);
		Collection<CargaHorariaEad> transientes = dao.findAllTransienteByAnoPeriodo(ano, periodo, nomeDocente);
		// realiza um merge revisando se houve altera��es no banco
		cadastrosDesatualizados = new HashMap<Integer, StringBuilder>();
		chRemovidas = new ArrayList<CargaHorariaEad>();
		Set<CargaHorariaEad> resultado = new TreeSet<CargaHorariaEad>();
		resultado.addAll(antigos);
		resultado.addAll(transientes);
		// cria uma lista de mensagens com altera��es no cadastro da CH
		for (CargaHorariaEad chAntigo : antigos) {
			cadastrosDesatualizados.put(chAntigo.getId(), new StringBuilder());
			boolean remover = true;
			for (CargaHorariaEad chTransiente : transientes) {
				if (chAntigo.equals(chTransiente)) {
					remover = false;
					StringBuilder msg = cadastrosDesatualizados.get(chAntigo.getId());
					if (chAntigo.getQtdDiscentes() != chTransiente.getQtdDiscentes()) {
						msg.append(" Quantidade de discentes foi alterada de ")
						.append(chAntigo.getQtdDiscentes())
						.append(" para ").append(chTransiente.getQtdDiscentes())
						.append(". ");
						chAntigo.setQtdDiscentes(chTransiente.getQtdDiscentes());
					}
					if (chAntigo.getQtdTurmas() != chTransiente.getQtdTurmas()) {
						msg.append(" Quantidade de turmas foi alterada de ")
						.append(chAntigo.getQtdTurmas())
						.append(" para ").append(chTransiente.getQtdTurmas())
						.append(".");
						chAntigo.setQtdTurmas(chTransiente.getQtdTurmas());
					}
				}
			}
			if (remover) {
				StringBuilder msg = cadastrosDesatualizados.get(chAntigo.getId());
				msg.append("Este registro ser� removido, pois n�o h� mais turmas lecionadas pelo docente para este componente curricular.");
				chRemovidas.add(chAntigo);
			}
		}
		resultadosBusca = resultado;
		return redirectMesmaPagina();
	}
	
	/** Atualiza as carga hor�rias dedicadas ao ensino a dist�ncia em um ano-per�odo.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/chDedicada/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		for (CargaHorariaEad chEad : resultadosBusca) {
			erros.addAll(chEad.validate());
		}
		if (hasErrors()) return null;
		prepareMovimento(SigaaListaComando.ATUALIZAR_CARGA_HORARIA_EAD);
		setOperacaoAtiva(SigaaListaComando.ATUALIZAR_CARGA_HORARIA_EAD.getId());
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(resultadosBusca);
		mov.setCodMovimento(SigaaListaComando.ATUALIZAR_CARGA_HORARIA_EAD);
		mov.setObjAuxiliar(chRemovidas);
		try {
			execute(mov);
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
			return null;
		}
		addMensagemInformation("Carga(s) hor�ria(s) atualizadas com sucesso!");
		return null;
	}
	
	/** Valida as permiss�es para o cadastro das cargas hor�rias.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		super.checkChangeRole();
	}

	/** Busca por cargas hor�rias dedicadas ao ensino � dist�ncia.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		obj.setAno(getCalendarioVigente().getAno());
		obj.setPeriodo(getCalendarioVigente().getPeriodo());
		this.resultadosBusca = null;
		return super.listar();
	}
	
	/** Retorna o link para a p�gina de listagem.<br/>
	 * M�todo n�o invocado por JSP�s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/ead/chDedicada/lista.jsp";
	}

	public Map<Integer, StringBuilder> getCadastrosDesatualizados() {
		return cadastrosDesatualizados;
	}

	public Collection<CargaHorariaEad> getChRemovidas() {
		return chRemovidas;
	}

	public String getNomeDocente() {
		return nomeDocente;
	}

	public void setNomeDocente(String nomeDocente) {
		this.nomeDocente = nomeDocente;
	}
}
