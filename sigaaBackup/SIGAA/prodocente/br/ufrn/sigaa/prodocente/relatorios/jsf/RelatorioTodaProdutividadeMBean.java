/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/05/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.integracao.dto.FormacaoAcademicaDTO;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.GrupoPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.TeseOrientada;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;
import br.ufrn.sigaa.prodocente.producao.dominio.ApresentacaoEmEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.Banca;
import br.ufrn.sigaa.prodocente.producao.dominio.BolsaObtida;
import br.ufrn.sigaa.prodocente.producao.dominio.Capitulo;
import br.ufrn.sigaa.prodocente.producao.dominio.Livro;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoColegiadoComissao;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoComissaoOrgEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoSociedade;
import br.ufrn.sigaa.prodocente.producao.dominio.PremioRecebido;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoArtisticaLiterariaVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoTecnologica;
import br.ufrn.sigaa.prodocente.producao.dominio.PublicacaoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TextoDidatico;
import br.ufrn.sigaa.prodocente.producao.dominio.TextoDiscussao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca;
import br.ufrn.sigaa.prodocente.producao.dominio.VisitaCientifica;

/**
 * Relatório utilizado para mostrar toda a produção de um docente.
 *
 * @author Gleydson
 *
 */
@Component("todaProducao") @Scope("request")
public class RelatorioTodaProdutividadeMBean extends SigaaAbstractController<Servidor> {

	private static ArrayList<SelectItem> anos = new ArrayList<SelectItem>();

	static {
		for ( int a = 2000; a <= CalendarUtils.getAnoAtual(); a++) {
			anos.add(new SelectItem(String.valueOf(a), String.valueOf(a)));
		}
	}

	private Servidor servidor = new Servidor();

	/** 1 = valida, 0 = invalida , -1 = pendente, 2 = Todas */
	private int filtroValidacao = 2;

	private int anoInicial = CalendarUtils.getAnoAtual() - 2;
	private int anoFinal = CalendarUtils.getAnoAtual() - 1;
	
	public int getFiltroValidacao() {
		return this.filtroValidacao;
	}

	public void setFiltroValidacao(int validacao) {
		this.filtroValidacao = validacao;
	}

	private Collection<Producao> listaProducoes = new ArrayList<Producao>();

	private void prepararServidor() throws DAOException {
		if ( isUserInRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE, SigaaPapeis.GESTOR_PESQUISA) ) {
			servidor = getGenericDAO().findByPrimaryKey(servidor.getId(), Servidor.class);
		} else {
			servidor = getServidorUsuario();
		}
	}

	public void filtrarValidacao(ValueChangeEvent evt) throws DAOException {
		// idProjeto = new Integer(evt.getNewValue().toString());
		int filtro = new Integer(evt.getNewValue().toString());
		setFiltroValidacao(filtro);
		prepararServidor();
		emiteRelatorioSemRedirecionar();
	}

	/**
	 * Retorna uma coleção de Produções onde o tipo de banca examinadora é CURSO. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getBancasCurso() throws DAOException {
		return filterBanca(TipoBanca.CURSO);
	}

	/**
	 * Retorna uma coleção de Produções onde o tipo de banca examinadora é CONCURSO.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getBancasConcurso() throws DAOException {
		return filterBanca(TipoBanca.CONCURSO);
	}

	/**
	 * Retorna uma coleção de Produções do tipo ARTIGO.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<?> getArtigos() throws DAOException {
		ProducaoDao dao = getDAO(ProducaoDao.class);
		return dao.findProducaoProjection("Artigo", servidor, null, anoInicial, anoFinal, filtroValidacao);
	}

	/**
	 * Retorna uma coleção de Produções do tipo PUBLICAÇÃO.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getPublicacoes() throws DAOException {
		return filterByClass(PublicacaoEvento.class);
	}

	/**
	 * Retorna uma coleção de Produções do tipo CAPÍTULO.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getCapitulos() throws DAOException {
		return filterByClass(Capitulo.class);
	}

	/**
	 * Retorna uma coleção de Produções do tipo LIVRO.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getLivro() throws DAOException {
		return filterByClass(Livro.class);
	}

	/**
	 * Retorna uma coleção de Produções do tipo TEXTO DIDÁTICO.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getTextoDidatico() throws DAOException {
		return filterByClass(TextoDidatico.class);
	}

	/**
	 * Retorna uma coleção de Produções do tipo TEXTO DE DISCUSSÃO.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getTextoDiscussao() throws DAOException {
		return filterByClass(TextoDiscussao.class);
	}

	/**
	 * Retorna uma coleção de Produções ARTÍSTICAS.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getProducoesArtisticas() throws DAOException {
		return filterByClass(ProducaoArtisticaLiterariaVisual.class);
	}

	/**
	 * Retorna uma coleção de Produções TECNOLÓGICAS.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getProducaoTecnologica() throws DAOException {
		return filterByClass(ProducaoTecnologica.class);
	}

	/**
	 * Retorna uma coleção de Produções do tipo APRESENTAÇÃO DE EVENTO.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getApresentacaoEvento() throws DAOException {
		return filterByClass(ApresentacaoEmEvento.class);
	}

	/**
	 * Retorna uma coleção de Produções do tipo PRÊMIO RECEBIDO.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getPremioRecebido() throws DAOException {
		return filterByClass(PremioRecebido.class);
	}

	/**
	 * Retorna uma coleção de Produções do tipo BOLSA OBTIDA.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getBolsaObtida() throws DAOException {
		return filterByClass(BolsaObtida.class);
	}

	/**
	 * Retorna uma coleção de Produções do tipo PARTICIPAÇÃO DE EVENTOS.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getOrganizacaoEventos() throws DAOException {
		return filterByClass(ParticipacaoComissaoOrgEventos.class);
	}

	/**
	 * Retorna uma coleção de Produções do tipo PARTICIPAÇÃO EM SOCIEDADES Científicas e Culturais.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getParticipacaoSociedade() throws DAOException {
		return filterByClass(ParticipacaoSociedade.class);
	}

	/**
	 * Retorna uma coleção de Produções do tipo PARTICIPAÇÃO DE EVENTOS.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getParticipacaoColegiado() throws DAOException {
		return filterByClass(ParticipacaoColegiadoComissao.class);
	}

	public Collection<FormacaoAcademicaDTO> getFormacaoAcademica() throws NegocioException, ArqException, RemoteException {
		FormacaoAcademicaRemoteService service = getMBean("formacaoAcademicaInvoker");		
		if (service != null)
			return service.consultarFormacaoAcademica(servidor.getId(), null, null, null, null, null, null);
		else 
			return new ArrayList<FormacaoAcademicaDTO>();
	}

	/**
	 * Retorna uma coleção de todos os grupos de pesquisa em que o servidor foi Coordenador.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<?> getCoordenacaoGrupoPesquisa() throws DAOException {
		GrupoPesquisaDao dao = getDAO(GrupoPesquisaDao.class);
		return dao.findByCoordenador(servidor,null,anoInicial,anoFinal);
	}

	/**
	 * Retorna uma coleção de participações de um docente como membro de projetos cujo relatório foi aprovado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<?> getRelatorioPesquisa() throws DAOException {
		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class);
		return dao.findByRelatorioFinalizado(servidor, anoFinal);
	}

	/**
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ProjetoPesquisa> getCoordenacaoProjetoExterno()
			throws DAOException, LimiteResultadosException {
		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class);
		return dao.filter(false, null, anoFinal, servidor.getId(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, false);
	}

	/**
	 * Retorna uma coleção de Produções do tipo VISITA CIENTÍFICA.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<Producao> getVisitaCientifica() throws DAOException {
		return filterByClass(VisitaCientifica.class);
	}

	/**
	 * Retorna uma coleção de orientação concluídas de pós-graduação do docente.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<TeseOrientada> getOrientacoes() throws DAOException {
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		return dao.findOrientacoesConcluidas(servidor, anoInicial, anoFinal ,null); /** ao passar validade nula estamos pegando entre dois anos, como era antes do método mudar. @Author: Edson Anibal (ambar@info.ufrn.br) */
	}

	/**
	 * Retorna uma coleção de todas as orientação de pós-graduação do docente.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<TeseOrientada> getTodasOrientacoes() throws DAOException {
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		return dao.findOrientacoes(servidor, anoFinal);
	}

	/**
	 * Redireciona para a página de filtragem das produções para geração do relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/abas/relatorios.jsp</li>
	 *		<li>sigaa.war/portais/menu_docente.jsp</li>
	 *		<li>sigaa.war/prodocente/index.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/prodocente.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String exibirOpcoes() throws DAOException {
		return forward("/prodocente/relatorios/filtro_toda_producao.jsp");
	}

	/**
	 * Responsável por emitir o relatório de toda produção do docente.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/filtro_toda_producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String emiteRelatorio() throws DAOException {
		if ( !getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isResponsavel() )
			setServidor( getUsuarioLogado().getServidor() );
		
		if ( isEmpty( servidor.getId() ) || isEmpty( servidor.getNome() ) ) {
			addMensagemErro("Docente: Campo Obrigatório não informado.");
			return null;
		}
		if (anoInicial > anoFinal) {
			addMensagemErro("A data Final deve ser inferior a data Inicial.");
			return null;
		}
		
		ProducaoDao dao = getDAO(ProducaoDao.class);
		setAnoInicial(anoInicial);
		listaProducoes = dao.findByProducaoServidor(servidor,
				Producao.class, filtroValidacao, anoInicial, anoFinal, null, null);
		return forward("/prodocente/relatorios/toda_producao.jsp");
	}

	public boolean isVinculoChefia(){ 
		return getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isResponsavel();
	}
	
	/**
	 * @return
	 * @throws DAOException
	 */
	public void emiteRelatorioPublico() throws DAOException {
		ProducaoDao dao = getDAO(ProducaoDao.class);
		listaProducoes = dao.findByProducaoServidor(servidor,
				Producao.class, 1, anoInicial, anoFinal, null, null);
	}

	/**
	 * Responsável por emitir o relatório sem redirencionar para alguma jsp.
	 * @throws DAOException
	 */
	public void emiteRelatorioSemRedirecionar() throws DAOException {
		ProducaoDao dao = getDAO(ProducaoDao.class);

		listaProducoes = dao.findByProducaoServidor(servidor,
				Producao.class, filtroValidacao, anoInicial, anoFinal, null, null);
	}

	/**
	 * Retorna uma coleção de todas as orientação de graduação do servidor.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<TrabalhoFimCurso> getTrabalhoFimCurso() throws DAOException {
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		return dao.findOrientacoesGraduacaoTrabalhoFimCursoDocente(servidor, anoFinal, (anoFinal - anoInicial + 1) );
	}

	/**
	 * Retorna uma coleção de todas as orientação de especialização concluidas do servidor.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<TeseOrientada> getMonografiasEspecializacao() throws DAOException {
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		return dao.findOrientacoesPosEspecializacaoConcluidaDocente(servidor, anoFinal, (anoFinal - anoInicial + 1));

	}

	/**
	 * Retorna uma coleção de discentes de monitoria.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteMonitoria> getBolsasMonitoria() throws DAOException {
		DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class);
		return dao.filter(null,servidor.getId(),null,null,anoFinal, null,null,null, null, null);

	}

	/**
	 * Retorna uma coleção de discentes de iniciação à pesquisa de um servidor.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/prodocente/relatorios/toda_producao.jsp</li>
	 *		<li>sigaa.war/public/docente/producao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjetoDiscente> getBolsasIC() throws DAOException {
		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class);
		return dao.findByOrientador(servidor.getId(), anoInicial, anoFinal);
	}

	/**
	 * Retorna uma coleção de Produções de acordo com a classe informada.
	 * @param classe
	 * @return
	 */
	private Collection<Producao> filterByClass(Class<?> classe) {
		ArrayList<Producao> result = new ArrayList<Producao>();
		for (Producao p : listaProducoes) {
			if (classe.isInstance(p)) {
				result.add(p);
			}
		}
		return result;
	}

	/**
	 * Retorna uma coleção de Produções de acordo com o tipo da banca examinadora.
	 * @param tipo
	 * @return
	 */
	private Collection<Producao> filterBanca(int tipo) {
		ArrayList<Producao> result = new ArrayList<Producao>();
		for (Producao p : listaProducoes) {
			if (p instanceof Banca) {
				if (((Banca) p).getTipoBanca().getId() == tipo) {
					result.add(p);
				}
			}
		}
		return result;
	}

	public int getAnoFinal() {
		return anoFinal;
	}

	public void setAnoFinal(int anoFinal) {
		this.anoFinal = anoFinal;
	}

	public int getAnoInicial() {
		return anoInicial;
	}

	public void setAnoInicial(int anoInicial) {
		this.anoInicial = anoInicial;
	}

	@Override
	public ArrayList<SelectItem> getAnos() {
		return anos;
	}

	public Servidor getServidor() {
		return this.servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

}
