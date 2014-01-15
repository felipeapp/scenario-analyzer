/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 25/04/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.ObjectNotFoundException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioNovasAquisicoesDAO;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.timer.EnviaInformativoNovasAquisicoesTimer;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

import com.sun.star.auth.InvalidArgumentException;

/**
 *
 *     MBean que controla o relat�rio de novas requisi��es dispon�veis no acervo.
 *
 * @author Felipe Rivas
 *
 */
@Component(value="relatorioNovasAquisicoesMBean")
@Scope(value="request")
public class RelatorioNovasAquisicoesMBean extends SigaaAbstractController<Object> {
	
	/**
	 * Link para a p�gina de busca de novas aquisi��es, onde os filtros de consulta s�o definidos.
	 */
	public static final String PAGINA_BUSCA_NOVAS_AQUISICOES = "/public/biblioteca/buscaPublicaNovasAquisicoes.jsp";
	/**
	 * Link para a p�gina de exibi��o do resultado da consulta de novas aquisi��es.
	 */
	public static final String PAGINA_RESULTADO_BUSCA = "/public/biblioteca/resultadoBuscaPublicaNovasAquisicoes.jsp";
	

	/**
	 * A lista dos registros que ser�o exibidos. Cont�m os formatos de refer�ncia dos t�tulos. 
	 */
	private String[] registros;
	/**
	 * Lista com os IDs das bibliotecas selecionadas no filtro da consulta.
	 */
	private List<String> bibliotecasID;
	/**
	 * Lista com as todas bibliotecas ativas do sistema.
	 */
	private List<Biblioteca> bibliotecas;
	/**
	 * ID da �rea de conhecimento selecionada no filtro da consulta.
	 */
	private Integer areaCNPQID;
	/**
	 * Lista com todas as grandes �reas de conhecimento ativas do sistema.
	 */
	private List<AreaConhecimentoCnpq> areasCNPQ;
	/**
	 * In�cio de p�riodo definido no filtro de consulta.
	 */
	private Date inicioPeriodo;
	/**
	 * Fim de per�odo definido no filtro de consulta.
	 */
	private Date fimPeriodo;
	
	/** O dao utilizado para realizar a consulta do relat�rio
	 *  Quando a consulta � feita a partir do <code>EnviaInformativoNovasAquisicoesTimer </code> ele � aberto e fechado por l�
	 *  utilizando a DaoFactory,  j� que o Timer n�o tem a informa��o da sess�o para usar o m�todo getDao() normal.*/
	private RelatorioNovasAquisicoesDAO daoNovasAquisicao;
	
	
	/**
	 * Construtor utilizado pelo caso de uso normal de gerar novas aquisi��es
	 * 
	 * @throws DAOException
	 */
	public RelatorioNovasAquisicoesMBean() throws DAOException {
		fimPeriodo = CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0);
		inicioPeriodo = CalendarUtils.subtrairMeses(fimPeriodo, 1);
		
		listarBibliotecas();
		listarAreasCNPQ();
	}

	
	/**
	 * Construtor utilizado para gerar o relat�rio de novas aquisi��es a partir do caso de uso de dissemina��o seletiva da informa��o.
	 * 
	 * @param dao
	 */
	public RelatorioNovasAquisicoesMBean(RelatorioNovasAquisicoesDAO daoNovasAquisicao){
		this.daoNovasAquisicao = daoNovasAquisicao;
	}
	
	
	/**
	 * M�todo principal da funcionalidade que faz a consulta dos t�tulos baseados nos filtros definidos pelo usu�rio. 
	 * A partir dos t�tulos s�o montados os registros de formato de refer�ncia.
	 * 
	 * Utilizado na JSP /sigaa.war/public/biblioteca/buscaPublicaNovasAquisicoes.jsp
	 * 
	 * @param e
	 * @return P�gina de exibi��o dos resultados
	 * @throws ArqException
	 */
	public String pesquisarNovasAquisicoes(ActionEvent e) throws ArqException {
		
		try {
			validarCampos();
			
			List<Biblioteca> bibliotecasSelecionadas = getBibliotecasSelecionadas();
			AreaConhecimentoCnpq areaCNPQ = getAreaCNPQSelecionada();
			
			
			registros = geraDadosRelatorioNovasAquisicoes(bibliotecasSelecionadas, areaCNPQ, inicioPeriodo, fimPeriodo, true);
			
			if (registros.length > 0) {
				
				if (registros.length  == RelatorioNovasAquisicoesDAO.MAXIMO_RESULTADOS) {
					addMensagem(MensagensArquitetura.BUSCA_MAXIMO_RESULTADOS, RelatorioNovasAquisicoesDAO.MAXIMO_RESULTADOS);
				}
				
				return forward(PAGINA_RESULTADO_BUSCA);
				
			}else {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		}catch (InvalidArgumentException iaex) {
			addMensagemErro(iaex.getMessage());
			return null;
		}
		
	}

	
	
	/**
	 * 
	 * <p>M�todo que retorna uma array de string contendo os dados do relat�rio de novas aquisi��es.</p>
	 *  
	 * <p>Cada posi��o do array � o formato de refer�ncia de um T�tulo que teve um novo material inclu�do do acervo do sistema.</p> 
	 *
	 * <p>Obs.: Esse m�todo � usado tamb�m pela rotina que envia o informativo de novas aquisi��es para os usu�rios, utilizada na parte 
	 * de Dissemina��o Seletiva da Informa��o.  </p> 
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws DAOException
	 * @see EnviaInformativoNovasAquisicoesTimer
	 */
	public String[] geraDadosRelatorioNovasAquisicoes(List<Biblioteca> bibliotecasSelecionadas, AreaConhecimentoCnpq areaCNPQ,
			Date inicioPeriodo, Date fimPeriodo, boolean saidaEmHTML) throws DAOException{
		
		String[] retorno = null;
		
		boolean iniciouDaoInternamente = false;
		
		try {
			
			/* *******************************************************************************
			 * Inicia o internamente dao se ele n�o foi passado
			 * 
			 * O dao j� � passado instaciado quando esse relat�rio � chamado a partir do caso de 
			 * uso de "dissemina��o seletiva da informa��o"  e � fechado por l� tamb�m.
			 * *******************************************************************************/
			if(daoNovasAquisicao == null ){
				daoNovasAquisicao = getDAO(RelatorioNovasAquisicoesDAO.class);
				iniciouDaoInternamente = true;
			}
				
			List<TituloCatalografico> titulosAdiquiridosRecentemente = daoNovasAquisicao.buscarAquisicoes(bibliotecasSelecionadas, areaCNPQ, inicioPeriodo, fimPeriodo);
		
			if(titulosAdiquiridosRecentemente== null || titulosAdiquiridosRecentemente.size() == 0)
				return new String[0];
			
			String resultado = new FormatosBibliograficosUtil().gerarFormatosReferencia(titulosAdiquiridosRecentemente, saidaEmHTML);
			
			String novaLinha = "";
			
			String inicioNegrito = FormatosBibliograficosUtil.INICIO_NEGRITO;
			String finalNegrito = FormatosBibliograficosUtil.FINAL_NEGRITO;
			
			if(saidaEmHTML){
				novaLinha = FormatosBibliograficosUtil.NOVA_LINHA_HTML;
			}else{
				novaLinha = FormatosBibliograficosUtil.NOVA_LINHA_JAVA;
			}
			
			retorno = resultado.split(novaLinha);
			
			Map<Integer, String> numerosChamada = daoNovasAquisicao.buscarNumeroChamada(titulosAdiquiridosRecentemente);
			
			for (int i=0; i<titulosAdiquiridosRecentemente.size(); i++) {
				retorno[i] += novaLinha + inicioNegrito + numerosChamada.get(titulosAdiquiridosRecentemente.get(i).getId()) + finalNegrito;
			}
			
			return retorno;
			
		}finally {
			if(daoNovasAquisicao != null && iniciouDaoInternamente) { daoNovasAquisicao.close(); daoNovasAquisicao = null; }
		}
	}
	
	
	
	
	/**
	 * Valida��o dos campos do formul�rio
	 * @throws InvalidArgumentException
	 */
	private void validarCampos() throws InvalidArgumentException {
		if (inicioPeriodo.getTime() < CalendarUtils.subtrairMeses(fimPeriodo, 6).getTime()) {
			throw new InvalidArgumentException("S� podem ser visualizadas as aquisi��es dos �ltimos seis meses.");
		}
	}

	/**
	 * Consulta do banco a lista das bibliotecas ativas do sistema.
	 * 
	 * @throws DAOException
	 */
	private void listarBibliotecas() throws DAOException {
		BibliotecaDao dao = getDAO(BibliotecaDao.class);
		
		try {
			bibliotecas = dao.findAllBibliotecasInternasAtivas();
		} finally {
			dao.close();
		}
	}

	/**
	 * Consulta do banco a lista das grandes �reas de conhecimento ativas do sistema.
	 * 
	 * @throws DAOException
	 */
	private void listarAreasCNPQ() throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);

		try {
			areasCNPQ = (List<AreaConhecimentoCnpq>) dao.findGrandeAreasConhecimentoCnpq();
		} finally {
			dao.close();
		}
	}

	/**
	 * Retorna a lista das bibliotecas selecionadas no filtro de consulta preenchidas apenas com o ID.
	 * 
	 * @return As bibliotecas selecionadas no filtro de consulta.
	 */
	private List<Biblioteca> getBibliotecasSelecionadas() {
		List<Biblioteca> bibliotecasSelecionadas = new ArrayList<Biblioteca>();
		
		for (String id : bibliotecasID) {
			bibliotecasSelecionadas.add(new Biblioteca(Integer.parseInt(id)));
		}
		
		return bibliotecasSelecionadas;
	}

	/**
	 * Retorna a �rea de conhecimento selecionada no filtro de consulta preenchida apenas com o ID.
	 * 
	 * @return As �reas de conhecimento selecionadas no filtro de consulta.
	 */
	private AreaConhecimentoCnpq getAreaCNPQSelecionada() {
		if (areaCNPQID != -1) {
			return new AreaConhecimentoCnpq(areaCNPQID);
		}
		
		return null;
	}

	/**
	 * Retorna os nomes das bibliotecas selecionadas no filtro de consulta.
	 * 
	 * @return Nomes das bibliotecas selecionadas no filtro de consulta.
	 * @throws ObjectNotFoundException
	 */
	public List<String> getNomeBibliotecasSelecionadas() throws ObjectNotFoundException {
		List<String> nomes = new ArrayList<String>();
		
		if (bibliotecasID.size() > 0) {
			List<Biblioteca> bibliotecas = buscarBibliotecasSelecionadasNaLista(bibliotecasID);
			
			for (Biblioteca biblioteca : bibliotecas) {
				nomes.add(biblioteca.getDescricao());
			}
		}
		else {
			nomes.add("Todas");
		}
		
		return nomes;
	}

	/**
	 * Retorna o nome da �rea de conhecimento selecionada no filtro de consulta.
	 * 
	 * @return Nome da �rea de conhecimento selecionada no filtro de consulta.
	 * @throws ObjectNotFoundException
	 */
	public String getNomeAreaCNPQSelecionada() throws ObjectNotFoundException {
		if (areaCNPQID != -1) {
			return buscarAreaCNPQSelecionadaNaLista(areaCNPQID).getNome();
		}
		else {
			return "Todas";
		}
	}
	
	/**
	 * Consulta a lista contendo as bibliotecas ativas do sistema e retorna apenas aquelas referentes � sele��o do usu�rio.
	 * 
	 * @param ids Identificador das bibliotecas selecionadas no filtro de consulta.
	 * @return As bibliotecas totalmente preenchidas correspondentes � sele��o do usu�rio.
	 * @throws ObjectNotFoundException
	 */
	private List<Biblioteca> buscarBibliotecasSelecionadasNaLista(List<String> ids) throws ObjectNotFoundException {
		List<Biblioteca> bibliotecasEncontradas = new ArrayList<Biblioteca>();

		for (String id : ids) {
			Integer intID = Integer.parseInt(id);
			
			for (Biblioteca biblioteca : bibliotecas) {
				if (biblioteca.getId() == intID) {
					bibliotecasEncontradas.add(biblioteca);
				}
			}
		}
		
		if (bibliotecasEncontradas.size() == 0) {
			throw new ObjectNotFoundException();
		}
		
		return bibliotecasEncontradas;
	}

	/**
	 * Consulta a lista contendo as grandes �reas de conhecimento ativas do sistema e retorna apenas aquela referente � sele��o do usu�rio.
	 * 
	 * @param id Identificador da �rea de conhecimento selecionada no filtro de consulta.
	 * @return A �rea de conhecimento totalmente preenchida correspondente � sele��o do usu�rio.
	 * @throws ObjectNotFoundException
	 */
	private AreaConhecimentoCnpq buscarAreaCNPQSelecionadaNaLista(Integer id) throws ObjectNotFoundException {
		for (AreaConhecimentoCnpq areaCNPQ : areasCNPQ) {
			if (areaCNPQ.getId() == id) {
				return areaCNPQ;
			}
		}
		
		throw new ObjectNotFoundException();
	}

	/**
	 * Redireciona o fluxo de navega��o para a p�gina de busca.
	 * 
	 * Utilizado na JSP /sigaa.war/public/biblioteca/resultadoBuscaPublicaNovasAquisicoes.jsp
	 * 
	 * @return O fluxo de navega��o para a p�gina de busca
	 */
	public String voltar() {
		return forward(PAGINA_BUSCA_NOVAS_AQUISICOES);
	}

	/**
	 * Limpa os dados de filtro da consulta.
	 * 
	 * Utilizado na JSP /sigaa.war/public/biblioteca/buscaPublicaNovasAquisicoes.jsp
	 * 
	 * @param e
	 */
	public void limpar(ActionEvent e) {
		bibliotecasID = null;
		areaCNPQID = null;
		//inicioPeriodo = null;
		//fimPeriodo = null;
	}
	
	
	/**
	 * Sobre escreve o m�todo cancelar do abstractcontroller para retornar para a p�gina p�blica 
	 * inicial, j� que o cancelar normal retorna para a primeira p�gina do m�dulo.<br/>
	 *
	 * Utilizado na JSP /sigaa.war/public/biblioteca/buscaPublicaNovasAquisicoes.jsp
	 *
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		return forward("/public/home.jsf");
	}
	
	
	/////////////////////////// Gets e Sets ///////////////////////////
	
	


	public Collection<SelectItem> getBibliotecasCombo() {
		return toSelectItems(bibliotecas, "id", "descricao");
	}
	
	public Collection<SelectItem> getAreasCNPQCombo() {
		return toSelectItems(areasCNPQ, "id", "nome");
	}
	
	public String[] getRegistros() {
		return registros;
	}

	public void setRegistros(String[] registros) {
		this.registros = registros;
	}

	public List<String> getBibliotecasID() {
		return bibliotecasID;
	}

	public void setBibliotecasID(List<String> bibliotecasID) {
		this.bibliotecasID = bibliotecasID;
	}

	public List<Biblioteca> getBibliotecas() {
		return bibliotecas;
	}

	public void setBibliotecas(List<Biblioteca> bibliotecas) {
		this.bibliotecas = bibliotecas;
	}

	public Integer getAreaCNPQID() {
		return areaCNPQID;
	}

	public void setAreaCNPQID(Integer areaCNPQID) {
		this.areaCNPQID = areaCNPQID;
	}

	public List<AreaConhecimentoCnpq> getAreasCNPQ() {
		return areasCNPQ;
	}

	public void setAreasCNPQ(List<AreaConhecimentoCnpq> areasCNPQ) {
		this.areasCNPQ = areasCNPQ;
	}

	public Date getInicioPeriodo() {
		return inicioPeriodo;
	}

	public void setInicioPeriodo(Date inicioPeriodo) {
		this.inicioPeriodo = inicioPeriodo;
	}

	public Date getFimPeriodo() {
		return fimPeriodo;
	}

	public void setFimPeriodo(Date fimPeriodo) {
		this.fimPeriodo = fimPeriodo;
	}
	
}
