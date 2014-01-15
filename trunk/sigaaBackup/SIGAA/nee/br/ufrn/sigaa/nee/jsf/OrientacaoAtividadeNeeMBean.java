/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 20/10/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.nee.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrm.sigaa.nee.dao.OrientacaoAtividadeNeeDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.ensino.jsf.OrientacaoAtividadeMBean;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controller responsável por operações de busca em orientação de atividades de Discentes com Necessidades Educativas Especiais (NEE).
 * 
 * @author Rafael Gomes
 *
 */

@Component("orientacaoAtividadeNee")
@Scope("request")
public class OrientacaoAtividadeNeeMBean extends OrientacaoAtividadeMBean{

	/** Mantém o nível de ensino da busca de componentes curriculares do tipo atividade. */
	private String nivel;
	
	/** Texto exibido no selectItem de Atividades.*/
	private String textoSelectItem;
	
	/** Indica se filtra a busca pelo nome do discente. */
	private boolean filtroNivelEnsino;
	
	/** Construtor padrão. */
	public OrientacaoAtividadeNeeMBean() {
		initObj();
	}
	
	@Override
	public String iniciarBusca() throws SegurancaException, DAOException {
		initObj();
		
		setOrientador( new Servidor() );
		setEscolheOrientador( true );
		textoSelectItem =  "-- SELECIONE UM NÍVEL DE ENSINO --";
		return forward(getListPage());
	}
		
	@Override
	public String getDirBase() {
		return "/nee/orientacao_atividade";
	}
	
	@Override
	public String getViewPage() {
		return getDirBase() + "/view.jsp";
	}
	
	@Override
	public String getListPage() {
		return getDirBase() + "/lista.jsp";
	}


	/**
	 * Preenche a lista de atividades de acordo com o nível de ensino.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li>/nee/orientacao_atividade/lista.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void selecionarNivelEnsino(ValueChangeEvent e) throws DAOException {
		if (isFiltroNivelEnsino()){
			if (e.getNewValue() == null || e.getNewValue().toString().equals("0")) {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nível de Ensino");
				return;
			}
			char nivel = e.getNewValue().toString().charAt(0);
			ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
			setListaAtividades( dao.findAtividades(nivel, null, null, null, null) );
			if (getListaAtividades().isEmpty())
				setTextoSelectItem("-- NÃO FORAM LOCALIZADAS ATIVIDADES PARA O NÍVEL DE ENSINO SELECIONADO--");
			else 
				setTextoSelectItem("-- SELECIONE UM NÍVEL DE ENSINO --");
		}	
	}
	
	/** Realiza a busca por orientação de atividades.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/orientacao_atividade/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		String nomeDiscente = null;
		Integer ano = null, periodo = null;
		Integer idComponente = null;
		Collection<Integer> situacoesMatricula = null;
		Character nivelEnsino = null;
		
		int paramCount = 0;
		
		if (isFiltroDiscente()) {
			ValidatorUtil.validateRequired(getDiscente().getNome(), "Discente", erros);
			nomeDiscente = getDiscente().getNome();
			paramCount++;
		}
		if (isFiltroNivelEnsino()){
			ValidatorUtil.validateRequiredId(nivel.charAt(0), "Nível de Ensino", erros);
			nivelEnsino = nivel.charAt(0);
			paramCount++;
		}
		if (isFiltroComponente()) {
			ValidatorUtil.validateRequired(getComponenteCurricular(), "Atividade", erros);
			idComponente = getComponenteCurricular().getId();
			paramCount++;
		}
		if (isFiltroAnoPeriodo()) {
			ValidatorUtil.validateRequiredId(getAno(), "Ano", erros);
			ValidatorUtil.validateRequiredId(getPeriodo(), "Período", erros);
			ano = getAno();
			periodo = getPeriodo();
			paramCount++;
		}
		if (isFiltroResultado()) {
			ValidatorUtil.validateRequired(getResultados(), "Resultado", erros);
			paramCount++;
			// cast para integer
			situacoesMatricula = new ArrayList<Integer>();
			for (Object i : getResultados())
				situacoesMatricula.add(Integer.parseInt(i.toString()));
		}
		
		if (hasErrors())
			return null;
		
		if (paramCount < 1) {
			addMensagemErro("Utilize pelo menos um critério para realizar a busca");
			return null;
		}
		
		// realiza a busca
		
		OrientacaoAtividadeNeeDao dao = getDAO(OrientacaoAtividadeNeeDao.class);
		setListaOrientacoesEncontradas(dao.findByOrientadorDiscenteComponente(null, nomeDiscente, idComponente, 
					ano, periodo, situacoesMatricula, null, isBuscaRegistroAtivEspecificas(),
					nivelEnsino,null));
		
		if (getListaOrientacoesEncontradas().isEmpty()) {
			addMensagemErro("Não foram encontradas orientações de atividades com os critérios informados.");
		}
		
		return null;
	}
	
	/* Getters and Setters*/
	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public boolean isFiltroNivelEnsino() {
		return filtroNivelEnsino;
	}

	public void setFiltroNivelEnsino(boolean filtroNivelEnsino) {
		this.filtroNivelEnsino = filtroNivelEnsino;
	}

	public String getTextoSelectItem() {
		return textoSelectItem;
	}

	public void setTextoSelectItem(String textoSelectItem) {
		this.textoSelectItem = textoSelectItem;
	}
	
}