/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 02/05/2012
 *
 */
package br.ufrn.sigaa.ensino.latosensu.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.latosensu.dao.MensalidadeCursoLatoDao;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.MensalidadeCursoLato;

/**
 * Controller responsável por gerar as GRUs para pagamento de mensalidades de
 * cursos Lato Sensu.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Component("mensalidadeCursoLatoMBean")
@Scope("request")
public class MensalidadeCursoLatoMBean extends SigaaAbstractController<MensalidadeCursoLato> {

	/**
	 * Construtor padrão.
	 */
	public MensalidadeCursoLatoMBean() {
		obj = new MensalidadeCursoLato();
	}
	
	/**
	 * Lista as mensalidades do curso do discente.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String listarDiscente() throws ArqException {
		if (!getDiscenteUsuario().getCurso().isLato()) {
			addMensagemErro("A lista de mensalidades está disponível apenas para discentes de lato sensu");
			return null;
		}
		MensalidadeCursoLatoDao dao = getDAO(MensalidadeCursoLatoDao.class);
		CursoLato cursoLato = dao.findByPrimaryKey(getDiscenteUsuario().getCurso().getId(), CursoLato.class);
		if (cursoLato.getValor() <= 0 || cursoLato.getQtdMensalidades() <= 0) {
			addMensagemErro("Na proposta do curso não foi definido a quantidade de mensalidades do curso");
			return null;
		} else if (cursoLato.getIdConfiguracaoGRUMensalidade() == null) {
			addMensagemErro("As mensalidades não podem ser geradas automaticamente. Entre em contato com a secretaria do seu curso para o pagamento das mensalidades.");
			return null;
		}
		resultadosBusca = dao.findAllByDiscente(getDiscenteUsuario().getId());
		// se não há mensalidade cadastrada, chamar o processador para criar em banco.
		if (isEmpty(resultadosBusca)) {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(getDiscenteUsuario());
			mov.setCodMovimento(SigaaListaComando.CRIAR_MENSALIDADES_CURSO_LATO);
			prepareMovimento(SigaaListaComando.CRIAR_MENSALIDADES_CURSO_LATO);
			try {
				execute(mov);
			} catch (Exception e) {
				if (e instanceof NegocioException)
					addMensagemErro(e.getMessage());
				else
					addMensagemErroPadrao();
				notifyError(e);
				return null;
			}
			resultadosBusca = dao.findAllByDiscente(getDiscenteUsuario().getId());
		} else {
			// verifica se há GRU quitada e marca a mensalidade como quitada
			quitarMensalidadesPagas();
			resultadosBusca = dao.findAllByDiscente(getDiscenteUsuario().getId());
		}
		// carrega os objetos GRU transientes
		Collection<Integer> ids = new ArrayList<Integer>();
		for (MensalidadeCursoLato mensalidade : resultadosBusca)
			ids.add(mensalidade.getIdGRU());
		for (GuiaRecolhimentoUniao gru : GuiaRecolhimentoUniaoHelper.getGRUByID(ids)) {
			for (MensalidadeCursoLato mensalidade : resultadosBusca) {
				if (mensalidade.getIdGRU() == gru.getId())
					mensalidade.setGru(gru);
			}
		}
		
		if (hasErrors()) return null;
		return forward("/lato/mensalidade/lista_discente.jsp");
	}

	/** Marca as mensalidades que tiveram a GRU paga como quitadas.
	 * @throws ArqException
	 */
	private void quitarMensalidadesPagas() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(getDiscenteUsuario());
		mov.setCodMovimento(SigaaListaComando.QUITAR_MENSALIDADES_CURSO_LATO);
		prepareMovimento(SigaaListaComando.QUITAR_MENSALIDADES_CURSO_LATO);
		try {
			execute(mov);
		} catch (Exception e) {
			if (e instanceof NegocioException)
				addMensagemErro(e.getMessage());
			else
				addMensagemErroPadrao();
			notifyError(e);
		}
	}

	/** Imprime a GRU referente à uma mensalidade.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws IOException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String imprimirGRU() throws ArqException, NegocioException, IOException {
		populateObj(true);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_NAO_FOI_SELECIONADO);
			obj = new MensalidadeCursoLato();
			return null;
		}
		getCurrentResponse().setContentType("application/pdf");
		getCurrentResponse().addHeader("Content-Disposition", "attachment;filename=mensalidade_"+obj.getOrdem()+".pdf");
		GuiaRecolhimentoUniaoHelper.gerarPDF(getCurrentResponse().getOutputStream(), obj.getIdGRU());
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}
}
