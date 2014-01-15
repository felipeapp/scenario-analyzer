/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 22/06/2009
 *
 */
package br.ufrn.sigaa.portal.jsf;
/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 22/06/2009 
 *
 */
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.cv.dao.TopicoComunidadeDao;
import br.ufrn.sigaa.ava.dominio.ConteudoTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.jsf.TopicoAulaMBean;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.MaterialComunidade;
import br.ufrn.sigaa.cv.dominio.TipoComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * MBean utilizado nas consultas p�blicas das turmas e comunidades virtuais.
 * 
 * @author Mario Rizzi
 * 
 */
@Component(value = "consultaPublicaTurmas") @Scope("request")
public class ConsultaPublicaTurmasMBean extends SigaaAbstractController<Turma> {

	
	/** Define o n�mero m�ximo de turmas publicadas na p�gina principal do portal dos cursos abertos */
	public static final int MAX_TURMAS_PUBLICAS = 5;
	
	/** Define a mensagem de valida��o dos filtros utilizados na consulta. */
	public static final String MSG_CAMPOS_OBRIGATORIOS = "Pelo menos um campo deve ser preenchido para realiza��o da busca. " +
														"Por favor selecione o centro, departamentos, ou se preferir digite " +
														"uma palavra que identifique a turma.";
	
	/** Indica a turmas encontradas na consulta dos cursos abertos. */
	private Collection<Turma> turmas;
	/** Indica as turma encontradas na consulta da turma virtual.  */
	private Collection<DocenteTurma> docenteTurma;
	/** Indica todos os centros e departamentos a serem visualizados no menu dos cursos abertos.  */
	private Collection<Unidade> centrosDeptos;
	/** Indica o ano da turma selecionado na consulta. */
	private Integer anoTurma;
	/** Indica o per�odo da turma selecionado na consulta. */
	private Integer periodoTurma;
	/** Indica o as situa��es da turma que devem ser consultadas. */
	private Integer[] situacao;
	/** Indica o n�vel da turma selecionado na consulta */
	private Character nivelTurma;
	
	
	/** Indica o departamento selecionado na consulta. */
	private Integer idDepto;
	/** Indica o centro selecionado na consulta. */
	private Integer idCentro;
	/** Indica a palavra-chave digitda na consulta. */
	private String palavraChave;
	/** Indica os dados do material do t�pico de aula da turma selecionada. */
	private ConteudoTurma conteudoMaterial;
	/** Indica os dados dos t�picos de aulas associados a turma selecionada. */
	private Collection<TopicoAula> topicosAulas;
	
	/**
	 * Indica se a busca vai ser feita por Turmas ou por Comunidades Virtuais
	 */
	public boolean buscaTurmas = true;

	/**
	 * Comunidades que foram configuradas para serem exibidas na busca do Portal P�blico
	 */
	private List<ComunidadeVirtual> listaComunidadesAbertaPortalPublico;
	
	/**
	 * Indica a comunidade virtual selecionada.
	 */
	private ComunidadeVirtual comunidadeVirtual;
	
	/** Quantidade m�nima de caracter que um palavra contida na frase dever� ser buscada. */
	public static final int MINIMO_CARACTER = 3;
		
	/**
	 * Construtor que inicializa os atributos envolvidos nos casos de uso da consulta p�blica das turmas
	 * e comunidades.
	 * M�todo n�o invocado por JSP's.
	 * @throws ArqException
	 */
	public ConsultaPublicaTurmasMBean()  throws ArqException {
		
		nivelTurma = null;
		periodoTurma= anoTurma != null?periodoTurma:getPeriodoAtual();
		anoTurma = anoTurma != null?anoTurma:CalendarUtils.getAnoAtual();
		idCentro = 0;
		idDepto = 0;
		turmas = null;
		topicosAulas = null;
		
		Integer tid = getParameterInt("tid");
		if(obj == null && tid != null )
			obj = getGenericDAO().findByPrimaryKey(tid, Turma.class);
	
		
		if(!isEmpty(getParameter("idCentro")))
			this.idCentro = Integer.parseInt(getParameter("idCentro"));
		
	}

	/**
	 * Efetua a busca das turmas relacionadas a unidade ou curso atrav�s dos filtros.
	 * M�todo n�o invocado por JSP's.
	*/
	private void buscar(String msgErro) throws ArqException {
		
		if(isEmpty(msgErro) && isEmpty(docenteTurma)){	
			DocenteTurmaDao dao = getDAO(DocenteTurmaDao.class);
							
			try {
				docenteTurma = dao.findGeral(Integer.valueOf(idDepto), nivelTurma, anoTurma, periodoTurma, situacao);
				if(isEmpty(docenteTurma))
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				
			} catch (LimiteResultadosException e) {
				addMensagemWarning(e.getMessage());
			} finally {
				dao.close();
			}
			
		}
		
		if(!isEmpty(msgErro))
			addMensagemWarning(msgErro);
	
	}

	
	/**
	 * M�todo de valida��o dos campos para a busca de Turmas.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>\SIGAA\app\sigaa.ear\sigaa.war\public\turmas\turma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String buscarTurmasGeral() throws ArqException{
		
		String msgErro = "";
		
		if(isEmpty(idDepto))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade");
		if(isEmpty(periodoTurma))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Per�odo");
		if(isEmpty(anoTurma))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");

		if(hasErrors())
			return "";
		
		situacao = new Integer[]{SituacaoTurma.CONSOLIDADA, SituacaoTurma.ABERTA};
		buscar(msgErro);
		
		return "";
		
	}
	
	/**
	 * Carrega as �ltimas turmas abertas na p�gina principal do portal dos cursos abertos.
	 * M�todo n�o invocado por JSP's.
	 * utilizado juntamente com pretty-faces.
	 * {@link /sigaa.war/WEB-INF/pretty-config.xml#acessarPortalCursosAbertos}
	 * @throws Exception
	 */
	public void carregarUltimosCursosAbertos() throws Exception{
		
		if( !isEmpty(idCentro) ){
			Unidade unidade = getGenericDAO().findByPrimaryKey(idCentro, Unidade.class);
			if( !isEmpty(unidade) && !unidade.isCentro()){
				idDepto = idCentro;
				idCentro = null;
			}	
		}
		
		if(isEmpty(palavraChave) && ( isEmpty(idDepto) || isEmpty(idCentro) ) && buscaTurmas)
			turmas = getDAO(TurmaDao.class).findVisiveisByUnidade(null, null,null,
				MAX_TURMAS_PUBLICAS, MINIMO_CARACTER);
		
	}
	
	/**
	 * Retorna uma cole��o das turmas abertas de acordo com filtro passado pelo formul�rio.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>P�gina \SIGAA\app\sigaa.ear\sigaa.war\public\curso_aberto\portal.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String buscarCursosAbertos() throws Exception{

		topicosAulas = null;
		turmas = null;
		
		if (isBuscaTurmas()) {// se estiver buscando por turmas
			
			if(!isEmpty(palavraChave) && palavraChave.length() <= MINIMO_CARACTER)	{	
				addMensagem(MensagensArquitetura.MINIMO_CARACTERES, "Palavra Chave");
				return null;
			}
			try{
				
				if(turmas == null){
					
					if( !isEmpty(idCentro) ){
						Unidade unidade = getGenericDAO().findByPrimaryKey(idCentro, Unidade.class);
						if( !isEmpty(unidade) && !unidade.isCentro()){
							idDepto = idCentro;
							idCentro = null;
						}	
					}
					
					turmas = getDAO(TurmaDao.class).findVisiveisByUnidade(idDepto,idCentro,
							palavraChave, null, MINIMO_CARACTER);
					
					if(idCentro == null)
						idCentro = idDepto;
					
						Collections.sort((List<Turma>) turmas, new Comparator<Turma>() {
							public int compare(Turma t1,Turma t2) {
								return -(new CompareToBuilder()
								.append(t1.getAno(), t2.getAno())	
								.append(t1.getPeriodo(), t2.getPeriodo())
								.toComparison());
							}
						});
				}
				if(isEmpty(turmas))
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			} catch (LimiteResultadosException e) {
				addMensagemWarning(e.getMessage());
			}
		} 
		else { // est� buscando por Comunidades Virtuais
			
			if(isEmpty(palavraChave)) {
				addMensagemErro("Por favor, digite uma palavra que identifique a comunidade virtual.");
				return null;
			}
			if(!isEmpty(palavraChave)) {
				ComunidadeVirtualDao dao = getDAO(ComunidadeVirtualDao.class);
				listaComunidadesAbertaPortalPublico = dao.findComunidadesByDescricaoTipo(palavraChave, TipoComunidadeVirtual.BUSCAR_TODOS_TIPOS_COMUNIDADE_VIRTUAL, getPaginacao(), true);
					if(isEmpty(listaComunidadesAbertaPortalPublico))
						addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			}
		}
			
		return forward("/public/curso_aberto/portal.jsp");
	}
	
	/**
	 * Retorna todos os centros e departamentos. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /public/curso_aberto/include/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<Unidade> getCentrosDeptos() throws ArqException {

		if(centrosDeptos == null){
			centrosDeptos = new ArrayList<Unidade>(0);		
			centrosDeptos = getDAO(UnidadeDao.class).findCentroDepto();
		}
		return centrosDeptos;
		
	}
	
	/**
	 * Limpar a managed bean e redireciona para p�gina principal do SIGAA.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /public/curso_aberto/portal.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {		
		resetBean();
		return redirect("/sigaa/public/home.jsf");
	}
	
	/**
	 * Retorna uma cole��o das turmas. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>\SIGAA\app\sigaa.ear\sigaa.war\public\turmas\turma.jsp</li>
	 * 	<li>\SIGAA\app\sigaa.ear\sigaa.war\public\curso_aberto\portal.jsp</li></ul>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<Turma> getTurmas() {
		return turmas;
	}

	/**
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> SIGAA/app/sigaa.ear/sigaa.war/public/turmas/turma.jsp </li>
	 * </ul>
	 * @param turmas
	 */
	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}
	
	/**
	 * Exibe todas as informa��es sobre a turma selecionada, como t�picos de aulas e materiais.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>SIGAA/app/sigaa.ear/sigaa.war/public/curso_aberto/portal.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String detalhesTopicoAula() throws DAOException{
		
		//Limpa os t�picos
		TopicoAulaMBean topicoMBean = getMBean("topicoAula");
		topicoMBean.setTopicosAulas(null);
		topicosAulas = topicoMBean.getAulas();
		
		//Limpa a turma
		TurmaVirtualMBean turmaMBean = (TurmaVirtualMBean) getMBean("turmaVirtual");
		turmaMBean.setTurma(null);
		
		//Limpa os materiais
		conteudoMaterial = null;
		return forward("/public/curso_aberto/resumo.jsf");
	}
	
	/**
	 * Exibe os t�picos da Comunidade Virtual que foi selecionada
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>SIGAA/app/sigaa.ear/sigaa.war/public/curso_aberto/portal.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String visualizarComundiadeVirtual() throws DAOException {
		Integer idComunidade = getParameterInt("idComunidade");
		setComunidadeVirtual( getGenericDAO().findByPrimaryKey(idComunidade, ComunidadeVirtual.class) );
		
		TopicoComunidadeDao dao = getDAO(TopicoComunidadeDao.class);
		List<TopicoComunidade> topicos = (List<TopicoComunidade>) dao.findTopicosVisiveisByComunidade(getComunidadeVirtual());
		Map<TopicoComunidade, List<MaterialComunidade>> materiais = dao.findMateriaisByComunidadeUsuario(getComunidadeVirtual(),false,getUsuarioLogado());
		
		// Construir �rvore de t�picos
		for (TopicoComunidade topico : topicos) {
			List<MaterialComunidade> materiaisTopico = materiais.get(topico);
			if (!isEmpty(materiaisTopico))
				topico.getMateriais().addAll(materiaisTopico);
		}
		
		getComunidadeVirtual().setTopicos(topicos);
		return forward("/public/curso_aberto/comunidade_virtual/resumo_cv.jsf");
	}
	
	/**
	 * Popula conte�do do t�pico de aula para visualiza��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>SIGAA/app/sigaa.ear/sigaa.war/public/curso_aberto/aulas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String mostrarMaterial() throws ArqException{
		Integer mid = getParameterInt("mid");
		if(mid != null)
			conteudoMaterial = getGenericDAO().findByPrimaryKey(mid, ConteudoTurma.class);
		return forward("/public/curso_aberto/resumo.jsf?mid="+mid);
	}
	
	/**
	 * Retorna o ano da turma selecionado na consulta.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> SIGAA/app/sigaa.ear/sigaa.war/public/turmas/turma.jsp </li>
	 * </ul>
	 * @return
	 */
	public Integer getAnoTurma() {
		return anoTurma;
	}

	/**
	 * 	Define o ano da turma na consulta.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  SIGAA/app/sigaa.ear/sigaa.war/public/turmas/turma.jsp </li>
	 * </ul>
	 * @param anoTurma
	 */
	public void setAnoTurma(Integer anoTurma) {
		this.anoTurma = anoTurma;
	}

	/**
	 * Retorna o per�odo da turma selecionado na consulta.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> SIGAA/app/sigaa.ear/sigaa.war/public/turmas/turma.jsp </li>
	 * </ul>
	 * @return
	 */
	public Integer getPeriodoTurma() {
		return periodoTurma;
	}

	/**
	 * Define o per�odo da turma na consulta.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> SIGAA/app/sigaa.ear/sigaa.war/public/turmas/turma.jsp </li>
	 * </ul>
	 * @return
	 */
	public void setPeriodoTurma(Integer periodoTurma) {
		this.periodoTurma = periodoTurma;
	}

	public Character getNivelTurma() {
		return nivelTurma;
	}

	/**
	 * Define o n�vel da turma na consulta. 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  SIGAA/app/sigaa.ear/sigaa.war/public/turmas/turma.jsp </li>
	 * </ul.
	 * @param nivelTurma
	 */
	public void setNivelTurma(Character nivelTurma) {
		this.nivelTurma = nivelTurma;
	}

	/**
	 * Retorna o id do departamento que foi setado no formul�rio de busca. 
	 * <ul>
	 * 	<li> SIGAA/app/sigaa.ear/sigaa.war/public/turmas/turma.jsp</li>
	 *  <li>SIGAA/app/sigaa.ear/sigaa.war/public/curso_aberto/portal.jsp</li>
	 *  </ul>
	 * @return
	 */
	public Integer getIdDepto() {
		return idDepto;
	}

	/**
	 * Seta o id do departamento. 
	 * M�todo n�o invocado por JSP's.
	 * @return
	 */
	public void setIdDepto(Integer idDepto) {
		this.idDepto = idDepto;
	}

	/**
	 * Retorna o id do centro que foi setado no formul�rio de busca. 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> SIGAA/app/sigaa.ear/sigaa.war/public/turmas/turma.jsp</li>
	 *  <li>SIGAA/app/sigaa.ear/sigaa.war/public/curso_aberto/portal.jsp</li>
	 * </ul>
	 * @return
	 */
	public Integer getIdCentro() {
		return idCentro;
	}

	/**
	 * Seta o id do centro. 
	 * M�todo n�o invocado por JSP's.
	 * @return
	 */
	public void setIdCentro(Integer idCentro) {
		this.idCentro = idCentro;
	}

	/**
	 * Seta a palavra-chave que identifique uma turma ou t�pico de aula.
	 * M�todo n�o invocado por JSP's.
	 * @return
	 */
	public String getPalavraChave() {
		return palavraChave;
	}

	/**
	 * Seta a palavra-chave que identifique uma turma ou t�pico de aula no formul�rio de busca.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> SIGAA/app/sigaa.ear/sigaa.war/public/curso_aberto/portal.jsp</li>
	 * </ul>
	 * @return
	 */
	public void setPalavraChave(String palavraChave) {
		this.palavraChave = palavraChave.trim().toUpperCase().replace("'", "").replace("\"", "").replace("&", "");
	}

	/**
	 * Retorna todas unidades do tipo acad�mico (Centro e Escolas Especializadas)
	 * M�todo n�o invocado por JSP's.
	 * @param centrosDeptos
	 */
	public void setCentrosDeptos(Collection<Unidade> centrosDeptos) {
		this.centrosDeptos = centrosDeptos;
	}

	/**
	 * Retorna o conte�do do material referente a um t�pico de aula.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> SIGAA/app/sigaa.ear/sigaa.war/public/curso_aberto/material.jsp </li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public ConteudoTurma getConteudoMaterial() throws ArqException {
		return conteudoMaterial;
	}

	/**
	 * Define o conte�do do material relacionado ao t�pico de aula da turma selecionada.
	 * M�todo n�o invocado por JSP's.
	 * @param conteudoMaterial
	 */
	public void setConteudoMaterial(ConteudoTurma conteudoMaterial) {
		this.conteudoMaterial = conteudoMaterial;
	}

	/**
	 * Retorna os t�picos de aula de uma turma.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  SIGAA/app/sigaa.ear/sigaa.war/public/curso_aberto/topicos.jsp</li>
	 * </ul> 
	 * @return
	 */
	public Collection<TopicoAula> getTopicosAulas() {
		return topicosAulas;
	}

	/**
	 * Define os t�picos de aula de uma turma.
	 * M�todo n�o invocado por JSP's.
	 * @param topicosAulas
	 */
	public void setTopicosAulas(Collection<TopicoAula> topicosAulas) {
		this.topicosAulas = topicosAulas;
	}

	/**
	 * Retorna se consulta � de turmas.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  SIGAA/app/sigaa.ear/sigaa.war/public/curso_aberto/portal.jsp</li>
	 * </ul> 
	 * @return
	 */
	public boolean isBuscaTurmas() {
		return buscaTurmas;
	}

	/**
	 * Define na consulta � de turmas.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  SIGAA/app/sigaa.ear/sigaa.war/public/curso_aberto/portal.jsp</li>
	 * </ul> 
	 * @return
	 */
	public void setBuscaTurmas(boolean buscaTurmas) {
		this.buscaTurmas = buscaTurmas;
	}

	/**
	 * Retorna todas as comunidade virtuais abertas caso a consulta seja 
	 * das comunidades.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  SIGAA/app/sigaa.ear/sigaa.war/public/curso_aberto/portal.jsp</li>
	 * </ul> 
	 * @return
	 */
	public List<ComunidadeVirtual> getListaComunidadesAbertaPortalPublico() {
		return listaComunidadesAbertaPortalPublico;
	}

	/**
	 * Define as comunidade virtuais abertas encontrada de acordo com os par�metros 
	 * passados na consulta das comunidades.
	 * M�todo n�o Invocado por JSP's.
	 * @param listaComunidadesAbertaPortalPublico
	 */
	public void setListaComunidadesAbertaPortalPublico(
			List<ComunidadeVirtual> listaComunidadesAbertaPortalPublico) {
		this.listaComunidadesAbertaPortalPublico = listaComunidadesAbertaPortalPublico;
	}

	/**
	 * Retorna a comunidade virtual selecionada no resultado da consulta.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/public/curso_aberto/comunidade_virtual/resumo_cv.jsp</li>
	 * </ul>
	 * @return
	 */
	public ComunidadeVirtual getComunidadeVirtual() {
		return comunidadeVirtual;
	}

	/**
	 * Define a comunidade virtual no resultado da consulta.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/public/curso_aberto/comunidade_virtual/topicos_cv.jsp</li>
	 * </ul>
	 * @param comunidadeVirtual
	 */
	public void setComunidadeVirtual(ComunidadeVirtual comunidadeVirtual) {
		this.comunidadeVirtual = comunidadeVirtual;
	}

	/**
	 * Retorna todas as turmas na consulta p�blica ads turmas.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/public/curso_aberto/resumo.jsp</li>
	 * 	<li> /sigaa.war/public/turmas/turma.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<DocenteTurma> getDocenteTurma() {
		return docenteTurma;
	}

	/**
	 * Define todas as turmas resutantes da consulta p�blica ads turmas.
	 * M�todo n�o invocado por JSP's.
	 * @param docenteTurma
	 */
	public void setDocenteTurma(Collection<DocenteTurma> docenteTurma) {
		this.docenteTurma = docenteTurma;
	}
	
}	
	
