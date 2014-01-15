/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '07/11/2008'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.integracao.dto.FormacaoAcademicaDTO;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.apedagogica.dao.RegistroParticipacaoAtualizacaoPedagogicaDAO;
import br.ufrn.sigaa.apedagogica.dominio.RegistroParticipacaoAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.arq.dao.prodocente.EstagioDao;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.arq.dao.prodocente.RelatorioRIDDao;
import br.ufrn.sigaa.arq.dao.projetos.PlanoTrabalhoProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoAssociadoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.Coordenacao;
import br.ufrn.sigaa.prodocente.atividades.dominio.Estagio;
import br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoTese;
import br.ufrn.sigaa.prodocente.atividades.dominio.Monografia;
import br.ufrn.sigaa.prodocente.atividades.dominio.OrientacaoICExterno;
import br.ufrn.sigaa.prodocente.atividades.dominio.QualificacaoDocente;
import br.ufrn.sigaa.prodocente.atividades.dominio.TeseOrientada;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;
import br.ufrn.sigaa.prodocente.producao.dominio.Artigo;
import br.ufrn.sigaa.prodocente.producao.dominio.Banca;
import br.ufrn.sigaa.prodocente.producao.dominio.Capitulo;
import br.ufrn.sigaa.prodocente.producao.dominio.CategoriaFuncional;
import br.ufrn.sigaa.prodocente.producao.dominio.Livro;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoColegiadoComissao;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoComissaoOrgEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.Patente;
import br.ufrn.sigaa.prodocente.producao.dominio.PremioRecebido;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.PublicacaoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ItemRID;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.PlanoTrabalhoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.rh.dominio.Designacao;

/**
 * Managed Bean para efetuar a geração do relatório RID
 * Relatório de Atividades Docentes para Progressão Funcional
 * 
 * @author Leonardo Campos
 *
 */
@Component("relatorioRID") @Scope("request")
public class RelatorioRIDMBean extends SigaaAbstractController<Object> {

	/** Atributo que define o servidor que está acessando o relatório.  */
	private Servidor servidor = new Servidor();
	/** 1 = valida, 0 = invalida , -1 = pendente, 2 = Todas */
	private int filtroValidacao = 2;
	/** Atributo que define o ano inicial a ser buscado */
	private Integer anoInicial = CalendarUtils.getAnoAtual() - 1;
	/** Atributo que define o ano final a ser buscado */
	private Integer anoFinal = CalendarUtils.getAnoAtual();
	/** Atributo que define o período inicial a ser buscado */
	private Integer periodoInicial = 1;
	/** Atributo que define o período final a ser buscado */
	private Integer periodoFinal = 1;
	/** Atributo que possui  a lista de produções resultante. */
	private Collection<Producao> listaProducoes = new ArrayList<Producao>();
	/** Lista dos itens do RID */
	private Collection<ItemRID> listaItensRID = new ArrayList<ItemRID>();
	/** Atributo que define a data da última progressão do servidor. */
	private Date dataUltimaProgressao;
	
	public Date getDataUltimaProgressao() {
		return dataUltimaProgressao;
	}

	public void setDataUltimaProgressao(Date dataUltimaProgressao) {
		this.dataUltimaProgressao = dataUltimaProgressao;
	}

	public RelatorioRIDMBean() {
	}
	
	/**
	 * Redireciona o formulário contendo o filtro da busca.
	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
 	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/rh_plan/abas/producao_docente.jsp</li>
 	 * </ul>
	 * @return
	 */
	public String popular(){
		return forward("/prodocente/relatorios/filtro_atividades_docentes.jsf");
	}
	
	/**
	 * Método que redireciona para o relatório.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/relatorios/filtro_toda_producao.jsp</li>
 	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 */
	public String emiteRelatorio() throws ArqException, NegocioException, RemoteException {
		
		if( !validaFiltros() )
			return null;
		
		prepararServidor();
		//Grupo I
		listaItensRID.addAll(getAtividadeEnsinoGraduacaoDocente());
		listaItensRID.addAll(getOrientacaoAcademicaDocente());
		listaItensRID.addAll(getAtividadeEnsinoPosDocente());
		listaItensRID.addAll(getAtividadeEnsinoExtensao());
		listaItensRID.addAll(getAtividadeEnsinoTecnico());
		listaItensRID.addAll(getEstagio());
		//Grupo II
		listaItensRID.addAll(getOrientacaoTrabalhoPesquisa());
		listaItensRID.addAll(getProjetosPesquisa());
		listaItensRID.addAll(getProducaoCientifica());
		//Grupo III
		listaItensRID.addAll(getAtividadesExtensao());
		listaItensRID.addAll(getDiscentesExtensao());
		//Grupo IV
		listaItensRID.addAll(getProjetosAssociado());
		listaItensRID.addAll(getBolsistasAssociado());
		//Grupo V
		listaItensRID.addAll(getParticipacoesEventos());
		listaItensRID.addAll(getParticipacoesBanca());
		listaItensRID.addAll(getParticipacoesComissoes());
		//Grupo VI
		listaItensRID.addAll(getFuncoesAdministrativas());
		//Grupo VII
		listaItensRID.addAll(getDocenteCursos());
		listaItensRID.addAll(getAtividadesAtualizacaoPedagogica());
		//Grupo VIII
		listaItensRID.addAll(getAtividadesTecnicas());
		ItemRID item = new ItemRID();
		item.motarView(item.RESUMO, false);
		listaItensRID.add(item);
		return forward("/prodocente/relatorios/atividades_docentes.jsf");
	}

	/**
	 * Método que define o servidor que está acessando o relatório.
	 * Método não invocado por JSP's.
	 * @throws DAOException
	 */
	private void prepararServidor() throws DAOException {
		/* Se o usuário tem papel de gestor e se encontra no subsistema correspondente */
		if (   (isUserInRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE) && getSubSistema().equals(SigaaSubsistemas.PROD_INTELECTUAL))
			|| (isUserInRole(SigaaPapeis.GESTOR_PESQUISA) && getSubSistema().equals(SigaaSubsistemas.PESQUISA))
			|| (isUserInRole(SigaaPapeis.PORTAL_PLANEJAMENTO) && getSubSistema().equals(SigaaSubsistemas.PORTAL_PLANEJAMENTO)) ) {
			servidor = getGenericDAO().findByPrimaryKey(servidor.getId(), Servidor.class);
		} else {
			/* Caso esteja no portal do docente */
			servidor = getServidorUsuario();
		}
	}
	
	/**
	 * Retorna a quantidade de semestres.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/relatorios/filtro_toda_producao.jsp</li>
 	 * </ul>
	 * @return
	 */
	public int getNumeroSemestres(){
		int semestres = (anoFinal - anoInicial + 1) * 2;
		if(periodoInicial > 1) semestres--;
		if(periodoFinal < 2) semestres--;
		return semestres;
	}
	
	/**
	 * Retorna o valor podenrado de acordo com o regime de trabalho
	 * Método não invocado por JSP's.
	 * @return
	 */
	public double getPonderacao(){
		if(servidor.getRegimeTrabalho() == 40 || servidor.getRegimeTrabalho() == 99)
			return 1.0;
		else
			return 0.5;
	}
	
	/**
	 * Retorna uma string com o semestre referente à data informada (1º ou 2º semestre)
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/relatorios/filtro_toda_producao.jsp</li>
 	 * </ul>
	 * @return
	 */
	public String getSemestre(Date data){
		if(data != null){
			String periodo = " ";
			Calendar c = Calendar.getInstance();
			c.setTime(data);
			periodo = c.get(Calendar.MONTH) <= Calendar.JUNE ? "1" : "2";
			return c.get(Calendar.YEAR) + "." + periodo;
		} else
			return "Indefinido";
	}
	
	/** GRUPO I - 1. Ensino de graduação */
	public Collection<ItemRID> getAtividadeEnsinoGraduacaoDocente() throws DAOException{
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		Collection<DocenteTurma> atividades = dao.findAtividadeEnsinoGraduacaoDocente(servidor, anoFinal, (anoFinal - anoInicial));
		Map<String, String> disciplinas = new HashMap<String,String>();
		Formatador f = Formatador.getInstance();
		for(DocenteTurma dt: atividades){
			ItemRID item = new ItemRID(7);
			Turma t = dao.findByPrimaryKey(dt.getTurma().getId(), Turma.class);
			if(isVerificaPeriodo(anoInicial, anoFinal, periodoInicial, periodoFinal, t.getAno(), t.getPeriodo()))continue;
			dt.setTurma(t);
			item.setO(dt);
			double crTotal = 0;
			double totalDocenteTurma = 0;
			double crDedicado =  0;
			if (dt.getDisciplina() != null && dt.getDisciplina().getDetalhes() != null) 
				crTotal = dt.getDisciplina().getDetalhes().getChTotal() / 15;
			if (dt.getTurma()!=null && dt.getTurma().getDocentesTurmas() != null) 
				totalDocenteTurma = dt.getTurma().getDocentesTurmas().size(); 
			if (dt.getChDedicadaPeriodo() != null) 
				crDedicado =  dt.getChDedicadaPeriodo().doubleValue() / 15;
			item.getColunas()[0] = dt.getTurma() != null ? dt.getTurma().getAnoPeriodo() : "";
			item.getColunas()[1] = dt.getDisciplina() != null ? dt.getDisciplina().getCodigo() : "";
			item.getColunas()[2] = dt.getDisciplina() != null ? dt.getDisciplina().getNatureza() : "";
			item.getColunas()[3] = f.formatarDecimal1(crTotal);
			item.getColunas()[4] = dt.getTurma() != null ? dt.getTurma().getCodigo() : "";
			String qtdDocente = Double.toString(totalDocenteTurma);
			item.getColunas()[5] = !isEmpty(qtdDocente) ? qtdDocente.substring(0, qtdDocente.indexOf(".")) : "";
			item.getColunas()[6] = f.formatarMoeda3(crDedicado);
			item.motarView(item.ENSINO_GRADUACAO, true);
			String anoPeriodo = dt.getTurma() != null ? dt.getTurma().getAnoPeriodo() : "";
			String codigo = dt.getDisciplina() != null ? dt.getDisciplina().getCodigo() : "";
			int creditos = dt.getChDedicadaPeriodo() != null ? dt.getChDedicadaPeriodo() / 15 : 0;
			if(disciplinas.get(anoPeriodo) == null || disciplinas.get(anoPeriodo).equals(codigo)){
				item.setPontos(6d);
			}else{
				item.setPontos(7d);
			}
			item.setTotal(creditos * item.getPontos());
			disciplinas.put(anoPeriodo, codigo);
			items.add(item);
		}
		if (atividades.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.ENSINO_GRADUACAO, false);
			items.add(item);
		}
		return items; 
	}
	
	/** Verifica se a turma está dentro do período informado periodo está dentro do período informado. */
	private boolean isVerificaPeriodo(int anoInicial, int anoFinal, int periodoInicial, int periodoFinal, Integer anoTurma, Integer periodoTurma) {
		if ( anoTurma >= anoInicial || anoTurma <= anoFinal ) {
			String periodoInicio = periodoInicial == 1 ? "1,2,3,4" : "2,4";
			String periodoFim = periodoFinal == 1 ? "1,3" : "1,2,3,4"; 
			if ( anoTurma == anoInicial || anoTurma == anoFinal ) {
				if ( anoTurma == anoInicial && periodoInicio.contains(periodoTurma.toString()) || 
						anoTurma == anoFinal && periodoFim.contains(periodoTurma.toString()) )
					return false;
			} else {
				return false;
			}
		} 
		return true;
	}

	/** GRUPO I - 2. Orientação acadêmica */
	public Collection<ItemRID> getOrientacaoAcademicaDocente() throws DAOException {
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		Date dataInicial, dataFinal;
		if (periodoInicial == 1) {
			dataInicial = CalendarUtils.createDate(1, 1, anoInicial);
		}else {
			dataInicial = CalendarUtils.createDate(31, 6, anoInicial);
		}
		if (periodoFinal == 1) {
			dataFinal = CalendarUtils.createDate(30, 6, anoFinal);
		}else {
			dataFinal = CalendarUtils.createDate(31, 11, anoFinal);
		}
		Collection<OrientacaoAcademica> colecao = dao.findAllByServidorPeriodoOtimizado(servidor, dataInicial, dataFinal);
		Formatador f = Formatador.getInstance();
		for(OrientacaoAcademica o: colecao){
			ItemRID item = new ItemRID(4);
			item.setO(o);
			item.setPontos(4d);
			item.setTotal(item.getPontos() * (o.getQtdMeses()/6.0));
			Integer qntMes = o.getQtdMeses();
			item.getColunas()[0] = f.formatarData(o.getInicio()) + " - " + f.formatarData(o.getFim()).toString();
			item.getColunas()[1] = qntMes.toString();
			item.getColunas()[2] = o.getDiscente() != null && o.getDiscente().getCurso() != null ? o.getDiscente().getCurso().getDescricao() : "";
			item.getColunas()[3] = o.getDiscente() != null && o.getDiscente().getPessoa() != null ? o.getDiscente().getPessoa().getNome() : "";
			item.motarView(item.ORIENTACAO_ACADEMICA, true);
			items.add(item);
		}
		if (colecao.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.ORIENTACAO_ACADEMICA, false);
			items.add(item);
		}
		return items;
	}
	
	/** GRUPO I - 3. Ensino de pós-graduação */
	public Collection<ItemRID> getAtividadeEnsinoPosDocente() throws DAOException{
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		Collection<DocenteTurma> atividades = dao.findAtividadeEnsinoPosDocente(servidor, anoFinal, (anoFinal - anoInicial)+1);
		Formatador f = Formatador.getInstance();
		for(DocenteTurma dt: atividades){
			ItemRID item = new ItemRID(6);
			Turma turma = dao.findByPrimaryKey(dt.getTurma().getId(), Turma.class);
			if(turma.getAno() == anoInicial && turma.getPeriodo() < periodoInicial) continue;
			if(turma.getAno() == anoFinal && turma.getPeriodo() > periodoFinal) continue;
			dt.setTurma(turma);
			item.setO(dt);
			double chTotal = 0;
			double totalDocente = 0;
			double chTotalPeriodo = 0;
			if (dt.getDisciplina() != null && dt.getDisciplina().getDetalhes() != null) 
				chTotal = ((Integer) dt.getDisciplina().getDetalhes().getChTotal()).doubleValue() / 15;
			if (dt.getTurma() != null && dt.getTurma().getDocentesTurmas() != null) 
				totalDocente = dt.getTurma().getDocentesTurmas().size();
			if (dt.getChDedicadaPeriodo() != null) 
				chTotalPeriodo = dt.getChDedicadaPeriodo().doubleValue() / 15;
			item.getColunas()[0] = dt.getTurma() != null ? dt.getTurma().getAnoPeriodo() : "";
			item.getColunas()[1] = dt.getDisciplina() != null ? dt.getDisciplina().getCodigo() : "";
			if (dt.getTurma() != null && dt.getTurma().getDisciplina() != null && dt.getTurma().getDisciplina().getNivel() == 'L') 
				item.getColunas()[2] = "Aperfeiçoamento/Especialização";
			else if (dt.getTurma() != null && dt.getTurma().getDisciplina() != null && dt.getTurma().getDisciplina().getNivel() == 'E') 
				item.getColunas()[2] = "Mestrado";
			else if (dt.getTurma() != null && dt.getTurma().getDisciplina() != null && dt.getTurma().getDisciplina().getNivel() == 'D')
				item.getColunas()[2] = "Doutorado";
			else
				item.getColunas()[2] = "";
			
			item.getColunas()[3] = f.formatarMoeda3(chTotal);
			String qtdDocente = Double.toString(totalDocente);
			item.getColunas()[4] = !isEmpty(qtdDocente) ? qtdDocente.substring(0, qtdDocente.indexOf(".")) : "";
			item.getColunas()[5] = f.formatarMoeda3(chTotalPeriodo);
			item.motarView(item.ENSINO_POS_GRADUACAO, true);
			double creditos = dt.getChDedicadaPeriodo() / 15.0;
			if(dt.getDisciplina().getNivel() == NivelEnsino.LATO)
				item.setPontos(7d);
			else
				item.setPontos(8d);
			item.setTotal(item.getPontos() * creditos);
			items.add(item);
		}
		if (atividades.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.ENSINO_POS_GRADUACAO, false);
			items.add(item);
		}
		return items;
	}
	
	/** GRUPO I - 4. Ensino em nível de extensão */
	public Collection<ItemRID> getAtividadeEnsinoExtensao() throws DAOException{
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		Collection<MembroProjeto> atividades = dao.findRelatorioAtividadeCurso2(servidor, anoFinal, (anoFinal - anoInicial)+1);
		for(MembroProjeto m: atividades){
			ItemRID item = new ItemRID(4);
			item.setO(m);
			Integer semestre = (CalendarUtils.isDentroPeriodo(CalendarUtils.createDate(01, 01, m.getProjeto().getAno()), 
					CalendarUtils.createDate(30, 06, m.getProjeto().getAno())) ? 1 : 2);
			item.getColunas()[0] = m.getProjeto().getAno() + "."+ semestre.toString();
			item.getColunas()[1] = m.getProjeto() != null ? m.getProjeto().getTitulo() : "";
			Integer total = 0;
			int totalSemana = CalendarUtils.calculaQuantidadeSemanasEntreDatasIntervaloFechado( m.getDataInicio(), m.getDataFim() );
			if ( totalSemana == 0 )
				total = m.getProjeto().getCoordenador().getChDedicada();
			else 
				total = totalSemana * m.getProjeto().getCoordenador().getChDedicada();
			item.getColunas()[2] = total.toString();
			item.getColunas()[3] = String.valueOf(m.getProjeto().getEquipe().size());
			int mult20 = (m.getChPreparacao() != null ? m.getChPreparacao()/20 : 1);
			item.setPontos(8d);
			item.motarView(item.ENSINO_EM_NIVEL_DE_EXTENSAO, true);
			item.setTotal(item.getPontos() * mult20);
			items.add(item);
		}
		if (atividades.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.ENSINO_EM_NIVEL_DE_EXTENSAO, false);
			items.add(item);
		}
		return items;
	}

	/** GRUPO I - 5. Ensino em nível de técnico */
	public Collection<ItemRID> getAtividadeEnsinoTecnico() throws DAOException{
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		Collection<DocenteTurma> atividades = dao.findAtividadeEnsinoTecnicoDocente(servidor, anoFinal, (anoFinal - anoInicial));
		Map<String, String> disciplinas = new HashMap<String,String>();
		Formatador f = Formatador.getInstance();
		for(DocenteTurma dt: atividades){
			ItemRID item = new ItemRID(7);
			Turma t = dao.findByPrimaryKey(dt.getTurma().getId(), Turma.class);
			if(t.getAno() == anoInicial && t.getPeriodo() < periodoInicial)continue;
			if(t.getAno() == anoFinal && t.getPeriodo() > periodoFinal)continue;
			dt.setTurma(t);
			item.setO(dt);
			double chTotal = 0;
			double totalDocenteTurma = 0;
			double chDedicada =  0;
			if (dt.getDisciplina() != null && dt.getDisciplina().getDetalhes() != null) 
				chTotal = dt.getDisciplina().getDetalhes().getChTotal() / 15;
			if (dt.getTurma()!=null && dt.getTurma().getDocentesTurmas() != null) 
				totalDocenteTurma = dt.getTurma().getDocentesTurmas().size(); 
			if (dt.getChDedicadaPeriodo() != null) 
				chDedicada =  dt.getChDedicadaPeriodo().doubleValue() / 15;
			item.getColunas()[0] = dt.getTurma() != null ? dt.getTurma().getAnoPeriodo() : "";
			item.getColunas()[1] = dt.getDisciplina() != null ? dt.getDisciplina().getCodigo() : "";
			item.getColunas()[2] = dt.getDisciplina() != null ? dt.getDisciplina().getNatureza() : "";
			item.getColunas()[3] = Double.toString(chTotal);
			item.getColunas()[4] = dt.getTurma() != null ? dt.getTurma().getCodigo() : "";
			String qtdDocente = Double.toString(totalDocenteTurma);
			item.getColunas()[5] = !isEmpty(qtdDocente) ? qtdDocente.substring(0, qtdDocente.indexOf(".")) : "";
			item.getColunas()[6] = f.formatarDecimal1(chDedicada);
			item.motarView(item.ENSINO_TECNICO, true);
			String anoPeriodo = dt.getTurma() != null ? dt.getTurma().getAnoPeriodo() : "";
			String codigo = dt.getDisciplina() != null ? dt.getDisciplina().getCodigo() : "";
			int creditos = dt.getChDedicadaPeriodo() != null ? dt.getChDedicadaPeriodo() / 15 : 0;
			if(disciplinas.get(anoPeriodo) == null || disciplinas.get(anoPeriodo).equals(codigo)){
				item.setPontos(6d);
			}else{
				item.setPontos(7d);
			}
			item.setTotal(creditos * item.getPontos());
			disciplinas.put(anoPeriodo, codigo);
			items.add(item);
		}
		if (atividades.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.ENSINO_TECNICO, false);
			items.add(item);
		}
		return items; 
	}
	
	/** 1.6 - Estágio */
	public Collection<ItemRID> getEstagio() throws DAOException{
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		EstagioDao dao = getDAO(EstagioDao.class);
		Collection<Estagio> estagios = dao.findByEstagioDocente(servidor.getId(), anoInicial, anoFinal, periodoInicial, periodoFinal);
		Formatador f = Formatador.getInstance();
		for(Estagio e : estagios){
			ItemRID item = new ItemRID(7);
			item.setO(e);
			item.getColunas()[0] = f.formatarData(e.getPeriodoInicio()) + " - " + f.formatarData(e.getPeriodoFim());
			item.getColunas()[1] = String.valueOf(CalendarUtils.calculoMeses(e.getPeriodoInicio(), e.getPeriodoFim()));
			item.getColunas()[2] = e.getMatricula().getComponente().getDetalhes().getNome();
			item.getColunas()[3] = e.getAluno().getNome();
			item.motarView(item.ESTAGIO, true);
			items.add(item);
		}
		if ( estagios.size() == 0 ) {
			ItemRID item = new ItemRID();
			item.motarView(item.ESTAGIO, false);
			items.add(item);
		}
		return items; 
	}
	
	/** GRUPO II - 1. Orientação e co-orientação de trabalho de pesquisa */
	public Collection<ItemRID> getOrientacaoTrabalhoPesquisa() throws DAOException{
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		Formatador f = Formatador.getInstance();
		Collection<MembroProjetoDiscente> colecao = dao.findOrientacaoIniciacaoCientificaRID(servidor, anoFinal, (anoFinal - anoInicial)+1);
		for(MembroProjetoDiscente m: colecao){
			ItemRID item = new ItemRID(5);
			item.getColunas()[0] = f.formatarData(m.getDataInicio()) + " - " + f.formatarData(m.getDataFim());
			item.getColunas()[1] = String.valueOf(CalendarUtils.calculoMeses(m.getDataInicio(), m.getDataFim()));
			item.getColunas()[2] = "Iniciação Científica";
			item.getColunas()[3] = "1";
			item.getColunas()[4] = m.getDiscente() != null ? m.getDiscente().getNome() : "";
			item.motarView(item.ORIENTACAO_E_CO_ORIENTACAO_DE_TRAB_PESQUISA, true);
			item.setPontos(6d);
			item.setTotal(item.getPontos());
			items.add(item);
		}
		Collection<OrientacaoICExterno> findOrientacaoIniciacaoCientificaExterna = dao.findOrientacaoIniciacaoCientificaExterna(servidor, anoFinal, (anoFinal - anoInicial)+1);
		for(OrientacaoICExterno o: findOrientacaoIniciacaoCientificaExterna){
			ItemRID item = new ItemRID(5);
			item.getColunas()[0] = f.formatarData(o.getDataInicio()) + " - " + f.formatarData(o.getDataFim());
			item.getColunas()[1] = String.valueOf(CalendarUtils.calculoMeses(o.getDataInicio(), o.getDataFim()));
			item.getColunas()[2] = "Iniciação Científica em outra IES";
			item.getColunas()[3] = "1";
			item.getColunas()[4] = o.getNomeOrientando();
			item.motarView(item.ORIENTACAO_E_CO_ORIENTACAO_DE_TRAB_PESQUISA, true);
			item.setPontos(6d);
			item.setTotal(item.getPontos());
			items.add(item);
		}
		Collection<TrabalhoFimCurso> findOrientacoesGraduacaoTrabalhoFimCursoConcluidas = dao.findOrientacoesGraduacaoTrabalhoFimCursoConcluidas(servidor, anoFinal, (anoFinal - anoInicial)+1);
		for(TrabalhoFimCurso t: findOrientacoesGraduacaoTrabalhoFimCursoConcluidas){
			ItemRID item = new ItemRID(5);
			item.getColunas()[0] = f.formatarData(t.getDataInicio()) + " - " + f.formatarData(t.getDataDefesa());
			item.getColunas()[1] = String.valueOf(CalendarUtils.calculoMeses(t.getDataInicio(), t.getDataDefesa()));
			item.getColunas()[2] = "Trabalho Final de Curso";
			item.getColunas()[3] = "1";
			item.getColunas()[4] = t.getTitulo() != null ? t.getTitulo() : "";
			item.motarView(item.ORIENTACAO_E_CO_ORIENTACAO_DE_TRAB_PESQUISA, true);
			item.setPontos(8d);
			item.setTotal(item.getPontos());
			items.add(item);
		}
		Collection<Monografia> findOrientacoesMonografiaGraduacaoConcluidas = dao.findOrientacoesMonografiaGraduacaoConcluidas(servidor, anoFinal, (anoFinal - anoInicial)+1);
		for(Monografia m: findOrientacoesMonografiaGraduacaoConcluidas){
			ItemRID item = new ItemRID(5);
			item.getColunas()[0] = f.formatarData(m.getPeriodoInicio()) + " - " + f.formatarData(m.getPeriodoFim());
			item.getColunas()[1] = String.valueOf(CalendarUtils.calculoMeses(m.getPeriodoInicio(), m.getPeriodoFim()));
			item.getColunas()[2] = "Monografia de Graduação";
			item.getColunas()[3] = "1";
			item.getColunas()[4] = m.getTitulo() != null ? m.getTitulo() : "";
			item.motarView(item.ORIENTACAO_E_CO_ORIENTACAO_DE_TRAB_PESQUISA, true);
			item.setPontos(8d);
			item.setTotal(item.getPontos());
			items.add(item);
		}
		Collection<TeseOrientada> findOrientacoesPosEspecializacaoDocente = dao.findOrientacoesPosEspecializacaoDocente(servidor, anoFinal, (anoFinal - anoInicial)+1);
		for(TeseOrientada t: findOrientacoesPosEspecializacaoDocente){
			ItemRID item = new ItemRID(5);
			item.getColunas()[0] = f.formatarData(t.getPeriodoInicio()) + " - " + f.formatarData(t.getDataPublicacao());
			item.getColunas()[1] = String.valueOf(CalendarUtils.calculoMeses(t.getPeriodoInicio(), t.getDataPublicacao()));
			item.getColunas()[2] = "Monografia de Especialização";
			item.getColunas()[3] = isEmpty( t.getTotalOrientadores() ) || t.getTotalOrientadores() == 0 ? "1": t.getTotalOrientadores().toString();
			item.getColunas()[4] = t.getOrientandoDiscente() != null ? t.getOrientandoDiscente().getNome() : t.getOrientando();
			item.motarView(item.ORIENTACAO_E_CO_ORIENTACAO_DE_TRAB_PESQUISA, true);
			item.setPontos(8d);
			item.setTotal(item.getPontos());
			items.add(item);
		}
		Collection<TeseOrientada> findOrientacoesPosMestradoDocente = dao.findOrientacoesPosMestradoDocente(servidor, anoFinal);
		findOrientacoesPosMestradoDocente.addAll(dao.findOrientacoesPosMestradoConcluidoDocente(servidor, anoFinal, (anoFinal - anoInicial)+1));
		for(TeseOrientada t: findOrientacoesPosMestradoDocente){
			ItemRID item = new ItemRID(5);
			item.getColunas()[0] = f.formatarData(t.getPeriodoInicio()) + " - " + f.formatarData(t.getDataPublicacao());
			item.getColunas()[1] = String.valueOf(CalendarUtils.calculoMeses(t.getPeriodoInicio(), t.getDataPublicacao()));
			item.getColunas()[2] = "Dissertação de Mestrado";
			item.getColunas()[3] = isEmpty( t.getTotalOrientadores() ) ? "": t.getTotalOrientadores().toString();
			item.getColunas()[4] = t.getOrientandoDiscente() != null ? t.getOrientandoDiscente().getNome() : t.getOrientando();
			item.motarView(item.ORIENTACAO_E_CO_ORIENTACAO_DE_TRAB_PESQUISA, true);
			item.setPontos(16d);
			item.setTotal(item.getPontos());
			items.add(item);
		}
		Collection<TeseOrientada> findOrientacoesPosDoutoradoDocente = dao.findOrientacoesPosDoutoradoDocente(servidor, anoFinal);
		findOrientacoesPosDoutoradoDocente.addAll(dao.findOrientacoesPosDoutoradoConcluidoDocente(servidor, anoFinal, (anoFinal - anoInicial)+1));
		for(TeseOrientada t: findOrientacoesPosDoutoradoDocente){
			ItemRID item = new ItemRID(5);
			item.getColunas()[0] = f.formatarData(t.getPeriodoInicio()) + " - " + f.formatarData(t.getDataPublicacao());
			item.getColunas()[1] = String.valueOf(CalendarUtils.calculoMeses(t.getPeriodoInicio(), t.getDataPublicacao()));
			item.getColunas()[2] = "Tese de Doutorado";
			item.getColunas()[3] = isEmpty( t.getTotalOrientadores() ) ? "": t.getTotalOrientadores().toString();
			item.getColunas()[4] = t.getOrientandoDiscente() != null ? t.getOrientandoDiscente().getNome() : t.getTitulo();
			item.motarView(item.ORIENTACAO_E_CO_ORIENTACAO_DE_TRAB_PESQUISA, true);
			item.setPontos(24d);
			item.setTotal(item.getPontos());
			items.add(item);
		}
		
		if (colecao.size() == 0 && findOrientacoesGraduacaoTrabalhoFimCursoConcluidas.size() == 0 && findOrientacoesMonografiaGraduacaoConcluidas.size() == 0 &&
				findOrientacoesPosEspecializacaoDocente.size() == 0 && findOrientacoesPosMestradoDocente.size() == 0 && findOrientacoesPosDoutoradoDocente.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.ORIENTACAO_E_CO_ORIENTACAO_DE_TRAB_PESQUISA, false);
			items.add(item);
		}
		return items;
	}
	
	/** GRUPO II - 2. Projetos de pesquisa */
	public Collection<ItemRID> getProjetosPesquisa() throws DAOException {
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class);
		Collection<ProjetoPesquisa> projetosCoord = new ArrayList<ProjetoPesquisa>();
		Collection<ProjetoPesquisa> projetosColab = new ArrayList<ProjetoPesquisa>();
		for(int ano = anoInicial; ano <= anoFinal; ano++){
			projetosCoord.addAll(dao.findTodosByMembro(servidor, true, ano));
			projetosColab.addAll(dao.findTodosByMembro(servidor, false, ano));
		}
		int meses;
		for(ProjetoPesquisa p: projetosCoord){
				ItemRID item = new ItemRID(4);
				item.getColunas()[0] = Formatador.getInstance().formatarData(p.getDataInicio()) + " - " + Formatador.getInstance().formatarData(p.getDataFim());
				meses = CalendarUtils.calculoMeses(p.getDataInicio(), p.getDataFim());
				item.getColunas()[1] = String.valueOf(meses);
				item.getColunas()[2] = p.getTitulo() != null ? p.getTitulo() : "";
				item.getColunas()[3] = p.getProjeto().getCoordenador().getFuncaoMembro().getDescricao();
				item.motarView(item.PROJETO_DE_PESQUISA, true);
				item.setPontos(24d);
				item.setO(p);
				item.setTotal(item.getPontos() * (meses/6.0));
				if (!items.contains(item)) 
					items.add(item);	
		}

		for(ProjetoPesquisa p: projetosColab){
			ItemRID item = new ItemRID(4);
			item.getColunas()[0] = Formatador.getInstance().formatarData(p.getDataInicio()) + " - " + Formatador.getInstance().formatarData(p.getDataFim());
			meses = CalendarUtils.calculoMeses(p.getDataInicio(), p.getDataFim());
			item.getColunas()[1] = String.valueOf(meses);
			item.getColunas()[2] = p.getTitulo() != null ? p.getTitulo() : "";
			item.getColunas()[3] = p.getProjeto().getCoordenador().getFuncaoMembro().getDescricao();
			item.motarView(item.PROJETO_DE_PESQUISA, true);
			item.setPontos(16d);
			item.setTotal(item.getPontos() * (meses/6.0));
			items.add(item);
		}
		
		if (projetosCoord.size() == 0 && projetosColab.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.PROJETO_DE_PESQUISA, false);
			items.add(item);
		}
		return items;
	}
	
	/** GRUPO II - 3. Produção científica */
	public Collection<ItemRID> getProducaoCientifica() throws DAOException {
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		ProducaoDao daoProducao = getDAO(ProducaoDao.class);
		RelatorioRIDDao ridDao = getDAO(RelatorioRIDDao.class);
		// Livro
		Formatador formatador = Formatador.getInstance();
		Collection<Producao> findByProducaoServidor = daoProducao.findByProducaoServidor(servidor, Livro.class, filtroValidacao, anoInicial, anoFinal, periodoInicial, periodoFinal);
		for(Producao p: findByProducaoServidor){
			ItemRID item = new ItemRID(5);
			item.setO(p);
			item.getColunas()[0] = p.getSemestre() != null ? p.getSemestre() : "";
			item.getColunas()[1] = formatador.formatarData(p.getDataProducao());
			item.getColunas()[2] = p.getTipoProducao() != null ? p.getTipoProducao().getDescricao() : "";
			item.getColunas()[3] = p.getTitulo() != null ? p.getTitulo() : "";
			Livro l = (Livro) p;
			item.getColunas()[4] = l.getTipoRegiao() != null ? l.getTipoRegiao().getDescricao() : "";
			item.motarView(item.PRODUCAO_INTELECTUAL, true);			
			double pontuacao = 0;
			if(l.getTipoParticipacao() != null && l.getTipoParticipacao().getId() == TipoParticipacao.TRADUTOR_LIVRO)
				pontuacao = 12;
			else // Autor
				pontuacao = 40;
			item.setPontos(pontuacao);
			item.setTotal(item.getPontos());
			items.add(item);
		}
		// Capítulo de Livro
		findByProducaoServidor = daoProducao.findByProducaoServidor(servidor, Capitulo.class, filtroValidacao, anoInicial, anoFinal, periodoInicial, periodoFinal);
		for(Producao p: findByProducaoServidor){
			ItemRID item = new ItemRID(5);
			item.setO(p);
			Capitulo l = (Capitulo) p;
			item.getColunas()[0] = p.getSemestre() != null ? p.getSemestre() : "";
			item.getColunas()[1] = formatador.formatarData(p.getDataProducao());
			item.getColunas()[2] = p.getTipoProducao() == null ? "" : p.getTipoProducao().getDescricao();
			item.getColunas()[3] = p.getTitulo() != null ? p.getTitulo() : "";
			item.getColunas()[4] = l.getTipoRegiao() != null ? l.getTipoRegiao().getDescricao() : "";
			item.motarView(item.PRODUCAO_INTELECTUAL, true);
			double pontuacao = 0;
			item.setPontos(pontuacao);
			item.setTotal(item.getPontos());
			items.add(item);
		}
		// Patente Registrada
		Collection<Patente> findRelatorioPatenteProduto = dao.findRelatorioPatenteProduto(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Producao p: findRelatorioPatenteProduto){
			ItemRID item = new ItemRID(5);
			item.setO(p);
			item.getColunas()[0] = p.getSemestre() != null ? p.getSemestre() : "";
			item.getColunas()[1] = formatador.formatarData(p.getDataProducao());
			item.getColunas()[2] = p.getTipoProducao() != null ? p.getTipoProducao().getDescricao() : "";
			item.getColunas()[3] = p.getTitulo() != null ? p.getTitulo() : "";
			item.getColunas()[4] = "";
			item.setPontos(30d);
			item.setTotal(item.getPontos());
			item.motarView(item.PRODUCAO_INTELECTUAL, true);
			items.add(item);
		}
		// Obra cultural internacional
		Collection<PremioRecebido> findRelatorioObraArtisticaInternacional = dao.findRelatorioObraArtisticaInternacional(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Producao p: findRelatorioObraArtisticaInternacional){
			ItemRID item = new ItemRID(5);
			item.setO(p);
			item.getColunas()[0] = periodoFormatado(p.getDataProducao());
			item.getColunas()[1] = formatador.formatarData(p.getDataProducao());
			item.getColunas()[2] = "Prêmio Recebido";
			item.getColunas()[3] = p.getTitulo() != null ? p.getTitulo() : "";
			PremioRecebido l = (PremioRecebido) p;
			item.getColunas()[4] = l.getTipoRegiao() != null ? l.getTipoRegiao().getDescricao() : "";
			item.setPontos(30d);
			item.setTotal(item.getPontos());
			item.motarView(item.PRODUCAO_INTELECTUAL, true);
			items.add(item);
		}
		// Obra cultural nacional
		Collection<PremioRecebido> findRelatorioObraArtisticaNacional = dao.findRelatorioObraArtisticaNacional(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Producao p: findRelatorioObraArtisticaNacional){
			ItemRID item = new ItemRID(5);
			item.setO(p);
			item.getColunas()[0] = periodoFormatado(p.getDataProducao());
			item.getColunas()[1] = formatador.formatarData(p.getDataProducao());
			item.getColunas()[2] = "Prêmio Recebido";
			item.getColunas()[3] = p.getTitulo() != null ? p.getTitulo() : "";
			PremioRecebido l = (PremioRecebido) p;
            item.getColunas()[4] = l.getTipoRegiao() != null ? l.getTipoRegiao().getDescricao() : "";
			item.setPontos(30d);
			item.setTotal(item.getPontos());
			item.motarView(item.PRODUCAO_INTELECTUAL, true);
			items.add(item);
		}
		// Obra cultural regional e local
		Collection<PremioRecebido> findRelatorioObraArtisticaRegionalLocal = dao.findRelatorioObraArtisticaRegionalLocal(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(PremioRecebido p: findRelatorioObraArtisticaRegionalLocal){
			ItemRID item = new ItemRID(5);
			item.setO(p);
			item.getColunas()[0] = p.getSemestre() != null ? p.getSemestre() : "";
			item.getColunas()[1] = formatador.formatarData(p.getDataProducao());
			item.getColunas()[2] = p.getTipoProducao() != null ? p.getTipoProducao().getDescricao() : "";
			item.getColunas()[3] = p.getTitulo() != null ? p.getTitulo() : "";
			PremioRecebido l = p;
            item.getColunas()[4] = l.getTipoRegiao() != null ? l.getTipoRegiao().getDescricao() : "";
            item.motarView(item.PRODUCAO_INTELECTUAL, true);
			double pontuacao = 0;
			if(p.getTipoRegiao() != null && p.getTipoRegiao().getId() == TipoRegiao.REGIONAL)
				pontuacao = 15;
			else
				pontuacao = 10;
			item.setPontos(pontuacao);
			item.setTotal(item.getPontos());
			items.add(item);
		}
		// Artigos
		findByProducaoServidor = ridDao.findByProducaoServidor(servidor, Artigo.class, filtroValidacao, anoInicial, anoFinal, periodoInicial, periodoFinal);
		for(Producao p: findByProducaoServidor){
			ItemRID item = new ItemRID(5);
			item.setO(p);
			item.getColunas()[0] = p.getSemestre() != null ? p.getSemestre() : "";
			item.getColunas()[1] = formatador.formatarData(p.getDataProducao());
			item.getColunas()[2] = p.getTipoProducao() != null ? p.getTipoProducao().getDescricao() : "";
			item.getColunas()[3] = p.getTitulo() != null ? p.getTitulo() : "";
			Artigo a = (Artigo) p;
			item.getColunas()[4] = a.getTipoRegiao() != null ? a.getTipoRegiao().getDescricao() : "";
			double pontuacao = 0;
			if(a.getTipoRegiao() != null && a.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL)
				pontuacao = 24;
			else if(a.getTipoRegiao() != null && a.getTipoRegiao().getId() == TipoRegiao.NACIONAL)
				pontuacao = 16;
			else if(a.getTipoRegiao() != null && (a.getTipoRegiao().getId() == TipoRegiao.LOCAL || a.getTipoRegiao().getId() == TipoRegiao.REGIONAL))
				pontuacao = 8;
			item.motarView(item.PRODUCAO_INTELECTUAL, true);
			item.setPontos(pontuacao);
			item.setTotal(item.getPontos());
			items.add(item);
		}

		if (findByProducaoServidor.size() == 0 && findRelatorioPatenteProduto.size() == 0 && 
				findRelatorioObraArtisticaInternacional.size() == 0  && findRelatorioObraArtisticaNacional.size() == 0 &&
				findRelatorioObraArtisticaRegionalLocal.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.PRODUCAO_INTELECTUAL, false);
			items.add(item);
		}

		return items;
	}
	
	/** Grupo III: Atividades de Extensão */
	public Collection<ItemRID> getAtividadesExtensao() throws DAOException {
		
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
		Collection<AtividadeExtensao> findByServidorFuncaoPeriodoExcetoCurso = dao.findByServidorFuncaoPeriodoExcetoCurso(
				servidor, FuncaoMembro.COORDENADOR, anoInicial, anoFinal, periodoInicial, periodoFinal);
		for(AtividadeExtensao a: findByServidorFuncaoPeriodoExcetoCurso){
			ItemRID item = new ItemRID(4);
			item.setO(a);
			item.setMembroDocente(retornarMembroDocente(a.getProjeto().getEquipe()));
			item.getColunas()[0] = a.getAno() != null ? a.getAno().toString() : "";
			item.getColunas()[1] = a.getTipoAtividadeExtensao() != null ? a.getTipoAtividadeExtensao().getDescricao() : "";
			item.getColunas()[2] = a.getTitulo() != null ? a.getTitulo() : "";
			item.getColunas()[3] = item.getMembroDocente() != null && item.getMembroDocente().getFuncaoMembro() != null ? item.getMembroDocente().getFuncaoMembro().getDescricao() : "";
			item.setPontos(24d);
			item.setTotal(item.getPontos());
			item.motarView(item.ATIVIDADES_DE_EXTENSAO, true);
			items.add(item);
		}
		Collection<AtividadeExtensao> findByServidorFuncaoPeriodoExcetoCurso2 = dao.findByServidorFuncaoPeriodoExcetoCurso(
				servidor, new Integer[] {FuncaoMembro.COLABORADOR, FuncaoMembro.VICE_COORDENADOR, 
						FuncaoMembro.INSTRUTOR_SUPERVISOR, FuncaoMembro.MINISTRANTE, FuncaoMembro.CONSULTOR_TUTOR}, anoInicial, anoFinal, periodoInicial, periodoFinal);
		for(AtividadeExtensao a: findByServidorFuncaoPeriodoExcetoCurso2){
			ItemRID item = new ItemRID(4);
			item.setO(a);
			item.setMembroDocente(retornarMembroDocente(a.getProjeto().getEquipe()));
			item.getColunas()[0] = a.getAno() != null ? a.getAno().toString() : "";
			item.getColunas()[1] = a.getTipoAtividadeExtensao() != null ? a.getTipoAtividadeExtensao().getDescricao() : "";
			item.getColunas()[2] = a.getTitulo() != null ? a.getTitulo() : "";
			item.getColunas()[3] = item.getMembroDocente() != null && item.getMembroDocente().getFuncaoMembro() != null ? item.getMembroDocente().getFuncaoMembro().getDescricao() : "";
			item.setPontos(16d);
			item.setTotal(item.getPontos());
			item.motarView(item.ATIVIDADES_DE_EXTENSAO, true);
			items.add(item);
		}
		if (findByServidorFuncaoPeriodoExcetoCurso.size() == 0 && findByServidorFuncaoPeriodoExcetoCurso2.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.ATIVIDADES_DE_EXTENSAO, false);
			items.add(item);
		}
		return items;
	}

	/** Grupo III: Atividades de Extensão */
	public Collection<ItemRID> getDiscentesExtensao() throws DAOException {
		
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		DiscenteExtensaoDao dao = getDAO(DiscenteExtensaoDao.class);
		Collection<DiscenteExtensao> findByDiscentesExtensaoServidores = dao.findByDiscenteExtensaobyServidor(servidor, anoInicial, anoFinal, periodoInicial, periodoFinal);
		Formatador formatador = Formatador.getInstance();
		for( DiscenteExtensao a: findByDiscentesExtensaoServidores ){
			ItemRID item = new ItemRID(4);
			item.setO(a);
			item.getColunas()[0] = a.getDiscente().getDiscente().getPessoa().getNome();
			item.getColunas()[1] = a.getPlanoTrabalhoExtensao().getAtividade().getProjeto().getTitulo();
			item.getColunas()[2] = formatador.formatarData( a.getDataInicio() );
			item.getColunas()[3] = formatador.formatarData( a.getDataFim() );
			item.setPontos(6d);
			item.setTotal(item.getPontos());
			item.motarView(item.DISCENTE_DE_EXTENSAO, true);
			items.add(item);
		}
		if ( findByDiscentesExtensaoServidores.size() == 0 ) {
			ItemRID item = new ItemRID();
			item.motarView(item.DISCENTE_DE_EXTENSAO, false);
			items.add(item);
		}
		return items;
	}

	/** GRUPO IV: ATIVIDADES DE ASSOCIADOS */
	/** Retorna todos os Projeto Integrados associados ao usuário, no período selecionado */
	public Collection<ItemRID> getProjetosAssociado() throws DAOException {
		
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		ProjetoAssociadoDao dao = getDAO(ProjetoAssociadoDao.class);
		try {
			Collection<Projeto> projetosAssociados = dao.findByProjetoAssociadosServidor(
					servidor, anoInicial, anoFinal, periodoInicial, periodoFinal);
			int meses;
			for(Projeto a: projetosAssociados){
				ItemRID item = new ItemRID(4);
				item.setO(a);
				item.getColunas()[0] = Formatador.getInstance().formatarData( a.getDataInicio() ) + " - " +  Formatador.getInstance().formatarData( a.getDataFim() );
				meses = CalendarUtils.calculoMeses(a.getDataInicio(), a.getDataFim());
				item.getColunas()[1] = String.valueOf(meses);
				item.getColunas()[2] = a.getTitulo() != null ? a.getTitulo() : "";
				item.setMembroDocente(retornarMembroDocente(a.getEquipe()));
				item.getColunas()[3] = item.getMembroDocente() != null && item.getMembroDocente().getFuncaoMembro() != null ? item.getMembroDocente().getFuncaoMembro().getDescricao() : "";
				item.setPontos(24d);
				item.setTotal(item.getPontos());
				item.motarView(item.PROJETOS_ASSOCIADOS, true);
				items.add(item);
			}
			if ( projetosAssociados.size() == 0 ) {
				ItemRID item = new ItemRID();
				item.motarView(item.PROJETOS_ASSOCIADOS, false);
				items.add(item);
			}
			return items;
			
		} finally {
			dao.close();
		}
	}

	/** Retorna	todos os bolsistas associados ao plano de trabalho do usuário no período selecionado */
	public Collection<ItemRID> getBolsistasAssociado() throws DAOException {
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		PlanoTrabalhoProjetoDao dao = getDAO(PlanoTrabalhoProjetoDao.class);
		try {
			Collection<PlanoTrabalhoProjeto> planosAssociados = dao.findByPlanosAssociados(
					servidor, anoInicial, anoFinal, periodoInicial, periodoFinal);
			int meses;
			for(PlanoTrabalhoProjeto a: planosAssociados){
				ItemRID item = new ItemRID(4);
				item.setO(a);
				item.getColunas()[0] = Formatador.getInstance().formatarData( a.getDataInicio() ) + " - " +  Formatador.getInstance().formatarData( a.getDataFim() );
				meses = CalendarUtils.calculoMeses(a.getDataInicio(), a.getDataFim());
				item.getColunas()[1] = String.valueOf(meses);
				String dimensao = a.getProjeto().isEnsino() ? "Mon" : "";
				dimensao += a.getProjeto().isPesquisa() ? isEmpty(dimensao) ? "Pesq" : "/Pesq" : "";
				dimensao += a.getProjeto().isExtensao() ? isEmpty(dimensao) ? "Ext" : "/Ext" : "";
				item.getColunas()[2] = dimensao;
				item.getColunas()[3] = a.getDiscenteProjeto().getDiscente().getNome();
				item.setPontos(24d);
				item.setTotal(item.getPontos());
				item.motarView(item.PLANOS_ASSOCIADOS, true);
				items.add(item);
			}
			if ( planosAssociados.size() == 0 ) {
				ItemRID item = new ItemRID();
				item.motarView(item.PLANOS_ASSOCIADOS, false);
				items.add(item);
			}
			return items;
			
		} finally {
			dao.close();
		}
	}

	/** Grupo IV: 1. Participação em exposição, congresso, simpósio, encontro, seminário, mesa-redonda e outros eventos. */
	public Collection<ItemRID> getParticipacoesEventos() throws DAOException {
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		ProducaoDao daoProducao = getDAO(ProducaoDao.class);
		
		//Publicações em eventos
		Collection<Producao> findByProducaoServidor = daoProducao.findByProducaoServidor(servidor, PublicacaoEvento.class, 1, anoInicial, anoFinal, periodoInicial, periodoFinal);
		for(Producao p: findByProducaoServidor){
			ItemRID item = new ItemRID(5);
			item.setO(p);
			PublicacaoEvento pe = (PublicacaoEvento) p;
			item.getColunas()[0] = pe.getSemestre() != null ? pe.getSemestre() : "";
			item.getColunas()[1] = pe.getNomeEvento() != null ? pe.getNomeEvento() : "";
			item.getColunas()[2] = pe.getTipoParticipacao() != null ? pe.getTipoParticipacao().getDescricao() : "Não informado";
			item.getColunas()[3] = pe.getTitulo() != null ? pe.getTitulo() : "";
			item.getColunas()[4] = pe.getTipoRegiao() != null ? pe.getTipoRegiao().getDescricao() : "Não informado";
			item.motarView(item.PART_EXP_CONG_SIMPO_ENC_SEM_MESA_REDONDA_OUTRSO, true);
			items.add(item);
		}
		findByProducaoServidor.clear();
		//Participação Organização Eventos
		findByProducaoServidor = daoProducao.findByProducaoServidor(servidor, ParticipacaoComissaoOrgEventos.class, 1, anoInicial, anoFinal, periodoInicial, periodoFinal);
		for(Producao a: findByProducaoServidor){
			ItemRID item = new ItemRID(5);
			item.setO(a);
			ParticipacaoComissaoOrgEventos p = (ParticipacaoComissaoOrgEventos) a;
			item.getColunas()[0] = p.getSemestre() != null ? p.getSemestre() : "";
			item.getColunas()[1] = p.getVeiculo() != null ? p.getVeiculo() : "";
			item.getColunas()[2] = p.getTipoParticipacaoOrganizacao() != null ? p.getTipoParticipacaoOrganizacao().getDescricao() : "Não informado";
			item.getColunas()[3] = p.getLocal() != null ? p.getLocal() : "";
			item.getColunas()[4] = p.getAmbito() != null ? p.getAmbito().getDescricao() : "Não informado";
			item.setPontos(20d);
			item.setTotal(item.getPontos());
			item.motarView(item.PART_EXP_CONG_SIMPO_ENC_SEM_MESA_REDONDA_OUTRSO, true);
			items.add(item);
		}

		if (findByProducaoServidor.size() == 0 && findByProducaoServidor.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.PART_EXP_CONG_SIMPO_ENC_SEM_MESA_REDONDA_OUTRSO, false);
			items.add(item);
		}
		return items;
	}
	
	/** Grupo IV: 2. Participação em bancas de concursos */
	public Collection<ItemRID> getParticipacoesBanca() throws DAOException {
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		Formatador formatador = Formatador.getInstance();
		// Banca Professor Titular e Livre docência
		Collection<Banca> findRelatorioBancaConcursoProfessorTitular = dao.findRelatorioBancaConcursoProfessorTitular(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Banca b: findRelatorioBancaConcursoProfessorTitular){
			ItemRID item = new ItemRID(5);
			item.setO(b);
			item.getColunas()[0] = b.getSemestre() != null ? b.getSemestre() : "";
			item.getColunas()[1] = b.getTitulo() != null ? b.getTitulo() : "";
			item.getColunas()[2] = formatador.formatarData(b.getData());
			item.getColunas()[3] = "Superior";
			item.getColunas()[4] = b.getCategoriaFuncional() != null ? b.getCategoriaFuncional().getDescricao() : "Não informado";
			item.setPontos(12d);
			item.setTotal(item.getPontos());
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, true);
			items.add(item);
		}
		Collection<Banca> findRelatorioBancaConcursoProfessorAdjAssAux = dao.findRelatorioBancaConcursoProfessorAdjAssAux(servidor, anoFinal, (anoFinal-anoInicial)+1);		
		// Banca Professor Adjunto, Assistente e Auxiliar
		for(Banca b: findRelatorioBancaConcursoProfessorAdjAssAux){
			ItemRID item = new ItemRID(5);
			item.setO(b);
			item.getColunas()[0] = b.getSemestre() != null ? b.getSemestre() : "";
			item.getColunas()[1] = b.getTitulo() != null ? b.getTitulo() : "";
			item.getColunas()[2] = formatador.formatarData(b.getData());
			item.getColunas()[3] = "Superior";
			item.getColunas()[4] = b.getCategoriaFuncional() != null ? b.getCategoriaFuncional().getDescricao() : "Não informado";
			double pontuacao = 0;
			if(b.getCategoriaFuncional() != null && b.getCategoriaFuncional().getId() == CategoriaFuncional.PROF_ADJUNTO.getId())
				pontuacao = 12;
			else if(b.getCategoriaFuncional() != null && b.getCategoriaFuncional().getId() == CategoriaFuncional.PROF_ASSISTENTE.getId())
				pontuacao = 10;
			else if(b.getCategoriaFuncional() != null && b.getCategoriaFuncional().getId() == CategoriaFuncional.PROF_AUXILIAR.getId())
				pontuacao = 8;
			item.setPontos(pontuacao);
			item.setTotal(item.getPontos());
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, true);
			items.add(item);
		}
		Collection<Banca> findRelatorioBancaConcursoProfessorSubstituto = dao.findRelatorioBancaConcursoProfessorSubstituto(servidor, anoFinal, (anoFinal-anoInicial)+1);		
		// Banca Professor Adjunto, Assistente e Auxiliar
		for(Banca b: findRelatorioBancaConcursoProfessorSubstituto){
			ItemRID item = new ItemRID(5);
			item.setO(b);
			item.getColunas()[0] = b.getSemestre() != null ? b.getSemestre() : "";
			item.getColunas()[1] = b.getTitulo() != null ? b.getTitulo() : "";
			item.getColunas()[2] = formatador.formatarData(b.getData());
			item.getColunas()[3] = "Superior";
			item.getColunas()[4] = b.getCategoriaFuncional() != null ? b.getCategoriaFuncional().getDescricao() : "Não informado";
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, true);
			items.add(item);
		}
		// Banca Tese Doutorado
		Collection<Banca> findRelatorioBancaTeseDoutorado = dao.findRelatorioBancaTeseDoutorado(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Banca b: findRelatorioBancaTeseDoutorado){
			ItemRID item = new ItemRID(5);
			item.setO(b);
			item.getColunas()[0] = b.getSemestre() != null ? b.getSemestre() : "";
			item.getColunas()[1] = b.getTitulo() != null ? b.getTitulo() : "";
			item.getColunas()[2] = formatador.formatarData(b.getData());
			item.getColunas()[3] = "Superior";
			item.getColunas()[4] = "Tese de Doutorado";
			item.setPontos(12d);
			item.setTotal(item.getPontos());
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, true);
			items.add(item);
		}
		//Qualificação Tese Doutorado
		Collection<Banca> findRelatorioBancaQualificacaoDoutorado = dao.findRelatorioBancaQualificacaoDoutorado(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Banca b: findRelatorioBancaQualificacaoDoutorado){
			ItemRID item = new ItemRID(5);
			item.setO(b);
			item.getColunas()[0] = b.getSemestre() != null ? b.getSemestre() : "";
			item.getColunas()[1] = b.getTitulo() != null ? b.getTitulo() : "";
			item.getColunas()[2] = formatador.formatarData(b.getData());
			item.getColunas()[3] = "Superior";
			item.getColunas()[4] = "Qualificação de Doutorado";
			item.setPontos(12d);
			item.setTotal(item.getPontos());
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, true);
			items.add(item);
		}
		// Banca Tese Mestrado
		Collection<Banca> findRelatorioBancaTeseMestrado = dao.findRelatorioBancaTeseMestrado(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Banca b: findRelatorioBancaTeseMestrado){
			ItemRID item = new ItemRID(5);
			item.setO(b);
			item.getColunas()[0] = b.getSemestre() != null ? b.getSemestre() : "";
			item.getColunas()[1] = b.getTitulo() != null ? b.getTitulo() : "";
			item.getColunas()[2] = formatador.formatarData(b.getData());
			item.getColunas()[3] = "Superior";
			item.getColunas()[4] = "Dissertação de Mestrado";
			item.setPontos(10d);
			item.setTotal(item.getPontos());
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, true);
			items.add(item);
		}
		//Qualificação de Mestrado
		Collection<Banca> findRelatorioBancaQualificacaoMestrado = dao.findRelatorioBancaQualificacaoMestrado(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Banca b: findRelatorioBancaQualificacaoMestrado){
			ItemRID item = new ItemRID(5);
			item.setO(b);
			item.getColunas()[0] = b.getSemestre() != null ? b.getSemestre() : "";
			item.getColunas()[1] = b.getTitulo() != null ? b.getTitulo() : "";
			item.getColunas()[2] = formatador.formatarData(b.getData());
			item.getColunas()[3] = "Superior";
			item.getColunas()[4] = "Qualificação de Mestrado";
			item.setPontos(10d);
			item.setTotal(item.getPontos());
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, true);
			items.add(item);
		}
		// Banca Monografia Graduação
		Collection<Banca> findRelatorioBancaMonografiaGraduacao = dao.findRelatorioBancaMonografiaGraduacao(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Banca b: findRelatorioBancaMonografiaGraduacao){
			ItemRID item = new ItemRID(5);
			item.setO(b);
			item.getColunas()[0] = b.getSemestre() != null ? b.getSemestre() : "";
			item.getColunas()[1] = b.getTitulo() != null  ? b.getTitulo() : "";
			item.getColunas()[2] = formatador.formatarData(b.getData());
			item.getColunas()[3] = "Superior";
			item.getColunas()[4] = "Monografia de Graduação";
			item.setPontos(4d);
			item.setTotal(item.getPontos());
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, true);
			items.add(item);
		}
		// Banca Monografia Especialização
		Collection<Banca> findRelatorioBancaMonografiaEspecializacao = dao.findRelatorioBancaMonografiaEspecializacao(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Banca b: findRelatorioBancaMonografiaEspecializacao){
			ItemRID item = new ItemRID(5);
			item.setO(b);
			item.getColunas()[0] = b.getSemestre() != null ? b.getSemestre() : "";
			item.getColunas()[1] = b.getTitulo() != null ? b.getTitulo() : "";
			item.getColunas()[2] = formatador.formatarData(b.getData());
			item.getColunas()[3] = "Superior";
			item.getColunas()[4] = "Monografia de Especialização";
			item.setPontos(4d);
			item.setTotal(item.getPontos());
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, true);
			items.add(item);
		}
		// Banca Concurso Professor Nível Médio
		Collection<Banca> findRelatorioBancaConcursoProfessorMedio = dao.findRelatorioBancaConcursoProfessorMedio(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Banca b: findRelatorioBancaConcursoProfessorMedio){
			ItemRID item = new ItemRID(5);
			item.setO(b);
			item.getColunas()[0] = b.getSemestre() != null ? b.getSemestre() : "";
			item.getColunas()[1] = b.getTitulo() != null ? b.getTitulo() : "";
			item.getColunas()[2] = formatador.formatarData(b.getData());
			item.getColunas()[3] = "Médio";
			item.getColunas()[4] = "Cargo de magistério público";
			item.setPontos(5d);
			item.setTotal(item.getPontos());
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, true);
			items.add(item);
		}
		// Banca Concurso Professor Nível Técnico
		Collection<Banca> findRelatorioBancaConcursoTecnico = dao.findRelatorioBancaConcursoTecnico(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Banca b: findRelatorioBancaConcursoTecnico){
			ItemRID item = new ItemRID(5);
			item.setO(b);
			item.getColunas()[0] = b.getSemestre() != null ? b.getSemestre() : "";
			item.getColunas()[1] = b.getTitulo() != null ? b.getTitulo() : "";
			item.getColunas()[2] = formatador.formatarData(b.getData());
			item.getColunas()[3] = "Médio";
			item.getColunas()[4] = "Cargo técnico";
			item.setPontos(3d);
			item.setTotal(item.getPontos());
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, true);
			items.add(item);
		}

		if (findRelatorioBancaConcursoProfessorTitular.size() == 0 && findRelatorioBancaConcursoProfessorAdjAssAux.size() == 0 &&
				findRelatorioBancaTeseDoutorado.size() == 0 && findRelatorioBancaTeseMestrado.size() == 0 &&
				findRelatorioBancaMonografiaGraduacao.size() == 0 && findRelatorioBancaMonografiaEspecializacao.size() == 0 &&
				findRelatorioBancaConcursoProfessorMedio.size() == 0 && findRelatorioBancaConcursoTecnico.size() == 0 &&
				findRelatorioBancaConcursoProfessorSubstituto.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS, false);
			items.add(item);
		}
		return items;
	}
	
	/** Grupo IV: 3. Participação em colegiados e comissões */
	public Collection<ItemRID> getParticipacoesComissoes() throws DAOException {
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		Formatador formatador = Formatador.getInstance();
		// Colegiado e comissões
		Collection<ParticipacaoColegiadoComissao> findRelatorioParticipacaoColegiado = dao.findAllParticipacoesColegiadoComissao(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(ParticipacaoColegiadoComissao p: findRelatorioParticipacaoColegiado){
			ItemRID item = new ItemRID(4, 24d);
			item.setO(p);
			int meses = CalendarUtils.calculoMeses(p.getPeriodoInicio(), p.getPeriodoFim());
			item.getColunas()[0] = formatador.formatarData(p.getPeriodoInicio()) + " - " + formatador.formatarData(p.getPeriodoFim());
			item.getColunas()[1] = String.valueOf(meses);
			item.getColunas()[2] = p.getTitulo() != null ? p.getTitulo() : "";
			item.getColunas()[3] = p.getNumeroReunioes() != null ? p.getNumeroReunioes().toString() : "";
			item.setPontos(1d);
			int numeroReunioes = p.getNumeroReunioes() != null ? p.getNumeroReunioes() : 0;
			item.setTotal(item.getPontos() * numeroReunioes);
			item.motarView(item.PART_COLEGIADO_COMISSAO_OFICIAL_UFRN_REPRESENTACAO, true);
			items.add(item);
		}
		if ( findRelatorioParticipacaoColegiado != null && findRelatorioParticipacaoColegiado.isEmpty() ) {
			ItemRID item = new ItemRID();
			item.motarView(item.PART_COLEGIADO_COMISSAO_OFICIAL_UFRN_REPRESENTACAO, false);
			items.add(item);
		}
		return items;
	}
	
	/** Grupo V: Funções Administrativas */
	public Collection<ItemRID> getFuncoesAdministrativas() throws DAOException {
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		Formatador formatador = Formatador.getInstance();
		
		Collection<Designacao> findCargosDirecaoFuncaoGratificada = dao.findCargosDirecaoFuncaoGratificada(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Designacao d: findCargosDirecaoFuncaoGratificada){
			ItemRID item = new ItemRID(3);
			item.getColunas()[0] = formatador.formatarData(d.getInicio()) + " - " + formatador.formatarData(d.getFim());
			int meses = CalendarUtils.calculoMeses(d.getInicio(), d.getFim());
			item.getColunas()[1] = String.valueOf(meses);
			item.getColunas()[2] = d.getDescricaoAtividade();
			int pontuacao = d.getAtividade() != null && d.getAtividade().getPontuacao() != null ? d.getAtividade().getPontuacao() : 0;
			
			item.setPontos(new Double(pontuacao));
			item.setTotal(item.getPontos() * (meses/6));
			item.motarView(item.FUNCOES_ADMINISTRATIVAS, true);
			items.add(item);
		}
		Collection<Coordenacao> findRelatorioChefiaCoordenacaoCursoLato = dao.findRelatorioChefiaCoordenacaoCursoLato(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(Coordenacao c: findRelatorioChefiaCoordenacaoCursoLato){
			ItemRID item = new ItemRID(3);
			item.getColunas()[0] = formatador.formatarData(c.getPeriodoInicio()) + " - " + formatador.formatarData(c.getPeriodoFim());
			int meses = CalendarUtils.calculoMeses(c.getPeriodoInicio(), c.getPeriodoFim());
			item.getColunas()[1] = String.valueOf(meses);
			item.getColunas()[2] = "Coordenador de Curso de Especialização ou Aperfeiçoamento";
			item.setPontos(20d);
			item.setTotal(item.getPontos() * (meses/6));
			item.motarView(item.FUNCOES_ADMINISTRATIVAS, true);
			items.add(item);
		}
		Collection<CoordenacaoCurso> findRelatorioCoordenacaoCursoLato = dao.findRelatorioCoordenacaoCursoLato(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(CoordenacaoCurso c: findRelatorioCoordenacaoCursoLato){
			ItemRID item = new ItemRID(3);
			item.getColunas()[0] = formatador.formatarData(c.getDataInicioMandato()) + " - " + formatador.formatarData(c.getDataFimMandato());
			int meses = CalendarUtils.calculoMeses(c.getDataInicioMandato(), c.getDataFimMandato());
			item.getColunas()[1] = String.valueOf(meses);
			item.getColunas()[2] = "Coordenador de Curso de Especialização ou Aperfeiçoamento";
			item.setPontos(20d);
			item.setTotal(item.getPontos() * (meses/6));
			item.motarView(item.FUNCOES_ADMINISTRATIVAS, true);
			items.add(item);
		}
		Collection<CoordenacaoCurso> findRelatorioCoordenacaoCursoGraduacaoEPos = dao.findRelatorioCoordenacaoCursoGraduacaoEPos(servidor, anoFinal, (anoFinal-anoInicial)+1);
		for(CoordenacaoCurso c: findRelatorioCoordenacaoCursoGraduacaoEPos){
			ItemRID item = new ItemRID(3);
			item.getColunas()[0] = formatador.formatarData(c.getDataInicioMandato()) + " - " + formatador.formatarData(c.getDataFimMandato());
			int meses = CalendarUtils.calculoMeses(c.getDataInicioMandato(), c.getDataFimMandato());
			item.getColunas()[1] = String.valueOf(meses);
			double pontuacao = 0;
			String descricao = "";
			if(c.getCargoAcademico().getId() == CargoAcademico.COORDENACAO){
				pontuacao = 60;
				if(c.getUnidade() != null)
					descricao = "Coordenador de Programa de Pós-Graduação";
				else if(c.getCurso() != null && c.getCurso().getNivel() == NivelEnsino.GRADUACAO)
					descricao = "Coordenador de Curso de Graduação";
				else
					continue;
			}else if(c.getCargoAcademico().getId() == CargoAcademico.VICE_COORDENACAO){
				pontuacao = 40;
				if(c.getUnidade() != null)
					descricao = "Vice-Coordenador de Programa de Pós-Graduação";
				else if(c.getCurso() != null && c.getCurso().getNivel() == NivelEnsino.GRADUACAO)
					descricao = "Vice-Coordenador de Curso de Graduação";
				else
					continue;
			}
			item.getColunas()[2] = descricao;
			item.setPontos(pontuacao);
			item.setTotal(item.getPontos() * (meses/6));
			item.motarView(item.FUNCOES_ADMINISTRATIVAS, true);
			items.add(item);
		}
		if (findCargosDirecaoFuncaoGratificada.size() == 0 && findRelatorioChefiaCoordenacaoCursoLato.size() == 0 &&
				findRelatorioCoordenacaoCursoLato.size() == 0 && findRelatorioCoordenacaoCursoGraduacaoEPos.size() == 0) {
			ItemRID item = new ItemRID();
			item.motarView(item.FUNCOES_ADMINISTRATIVAS, false);
			items.add(item);
		}
		return items;
	}
	
	/** Grupo VI: Docente realizando cursos */
	public Collection<ItemRID> getDocenteCursos() throws DAOException {
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		Formatador formatador = Formatador.getInstance();
		AvaliacaoDocenteDao dao = getDAO(AvaliacaoDocenteDao.class);
		
		Collection<QualificacaoDocente> qualificacoesDocente = dao.findRelatorioQualificacaoDocente(servidor, anoFinal, (anoFinal-anoInicial)+1);
		
		if ( isNotEmpty(qualificacoesDocente) ) {
			for( QualificacaoDocente quali : qualificacoesDocente ){
				ItemRID item = new ItemRID(3);
				item.setO(quali);
				item.getColunas()[0] = formatador.formatarData(quali.getDataInicial()) + " - " + formatador.formatarData(quali.getDataFinal());  
				Integer meses = 0; 
				if (!ValidatorUtil.isEmpty(quali.getDataInicial()) &&  !ValidatorUtil.isEmpty(quali.getDataFinal()))
					meses =	CalendarUtils.calculoMeses(quali.getDataInicial(), quali.getDataFinal());
				item.getColunas()[1] = meses != 0 ? String.valueOf(meses) : "";
				item.getColunas()[2] = quali.getTipoQualificacao().getDescricao();
				item.setPontos(80d);
				item.setTotal(item.getPontos() * (meses/6));			
				item.motarView(item.DOCENTE_EM_QUALIFICACAO, true);
				items.add(item);
			}
		}
		
		if (isEmpty(qualificacoesDocente) || qualificacoesDocente.size() == 0 ) {
			ItemRID item = new ItemRID();
			item.motarView(item.DOCENTE_EM_QUALIFICACAO, false);
			items.add(item);
		}
		return items;
	}
	
	/** Grupo VI: Atividades de Atualizaçao Pedagógica realizando cursos */
	public Collection<ItemRID> getAtividadesAtualizacaoPedagogica() throws DAOException {
		
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		Formatador formatador = Formatador.getInstance();
		

		Collection<RegistroParticipacaoAtualizacaoPedagogica> registroParticipacaoAP = 
			getDAO(RegistroParticipacaoAtualizacaoPedagogicaDAO.class).
			findGeral( servidor.getId(), null, anoInicial, periodoInicial, anoFinal, periodoFinal);
		
		for (RegistroParticipacaoAtualizacaoPedagogica r : registroParticipacaoAP) {
			ItemRID item = new ItemRID(5);
			item.setO(r);
			item.getColunas()[0] = r.getTitulo(); 
			item.getColunas()[1] = formatador.formatarData(r.getDataInicio()) + " - " + formatador.formatarData(r.getDataFim());  
			item.getColunas()[2] = r.getCargaHoraria()+"h";
			item.getColunas()[3] = "";
            item.getColunas()[4] = "";
			//item.setPontos(80d);
			//item.setTotal(item.getPontos());			
			item.motarView(item.ATIVIDADE_ATUALIZACAO_PEDAGOGICA, true);
			items.add(item);
		}
		if (registroParticipacaoAP.size() == 0 ) {
			ItemRID item = new ItemRID();
			item.motarView(item.ATIVIDADE_ATUALIZACAO_PEDAGOGICA, false);
			items.add(item);
		}
		return items;
		
	}
	
	/** 
	 * Grupo VII: Atividades Técnicas 
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public Collection<ItemRID> getAtividadesTecnicas() throws NegocioException, ArqException, RemoteException {
		Collection<ItemRID> items = new ArrayList<ItemRID>();
		FormacaoAcademicaRemoteService serviceFormacao = getMBean("formacaoAcademicaInvoker");
		if (serviceFormacao != null){
			
			Collection<FormacaoAcademicaDTO> findRelatorioCursoMestradoDoutoradoEstagioDocente = serviceFormacao.consultarFormacaoAcademica(servidor.getId(), 
					null, null, null, null, CalendarUtils.createDate(01, Calendar.JANUARY, anoInicial), 
					CalendarUtils.createDate(31, Calendar.DECEMBER, anoFinal),
					Formacao.ESPECIALISTA,Formacao.DOUTOR,FormacaoTese.POS_DOUTORADO);
			
			if (isNotEmpty(findRelatorioCursoMestradoDoutoradoEstagioDocente)) {
				for(FormacaoAcademicaDTO f: findRelatorioCursoMestradoDoutoradoEstagioDocente){
					ItemRID item = new ItemRID(1);
					item.setO(f);
					int meses =	CalendarUtils.calculoMeses(f.getInicio(), f.getFim());
					item.getColunas()[0] = String.valueOf(meses);
					item.setPontos(80d);
					item.setTotal(item.getPontos() * (meses/6));
					item.motarView(item.ATIVIDADES_TECNICAS, false);
					items.add(item);
				}			
			}
			
			if (isEmpty(findRelatorioCursoMestradoDoutoradoEstagioDocente) || findRelatorioCursoMestradoDoutoradoEstagioDocente.size() == 0 ) {
				ItemRID item = new ItemRID();
				item.motarView(item.ATIVIDADES_TECNICAS, false);
				items.add(item);
			}
		}
		return items;
	}
	
	/**
	 * Retorna o membro do projeto de acorodo com o servidor logado.
	 * @param equipeProjeto
	 * @return
	 */
	private MembroProjeto retornarMembroDocente(Collection<MembroProjeto> equipeProjeto){
		for(MembroProjeto mp : equipeProjeto)
			if (mp.getServidor() != null && mp.getServidor().getId() == servidor.getId())
				return mp;
		return null;
	}
	
	/**
	 * Formata a data informada para o formato de ano.período.
	 * 
	 * @param data
	 * @return
	 */
	private static String periodoFormatado(Date data){
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.get(Calendar.MONTH);
		if (c.get(Calendar.MONTH) >= 0 && c.get(Calendar.MONTH) <= 5 )
			return c.get(Calendar.YEAR) + ".1";
		else
			return c.get(Calendar.YEAR) + ".2";
	}
	
	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Integer getAnoInicial() {
		return anoInicial;
	}

	public void setAnoInicial(Integer anoInicial) {
		this.anoInicial = anoInicial;
	}

	public Integer getAnoFinal() {
		return anoFinal;
	}

	public void setAnoFinal(Integer anoFinal) {
		this.anoFinal = anoFinal;
	}

	public Collection<Producao> getListaProducoes() {
		return listaProducoes;
	}

	public void setListaProducoes(Collection<Producao> listaProducoes) {
		this.listaProducoes = listaProducoes;
	}

	public Integer getPeriodoInicial() {
		return periodoInicial;
	}

	public void setPeriodoInicial(Integer periodoInicial) {
		this.periodoInicial = periodoInicial;
	}

	public Integer getPeriodoFinal() {
		return periodoFinal;
	}

	public void setPeriodoFinal(Integer periodoFinal) {
		this.periodoFinal = periodoFinal;
	}

	public Collection<ItemRID> getListaItensRID() {
		return listaItensRID;
	}

	public void setListaItensRID(Collection<ItemRID> listaItensRID) {
		this.listaItensRID = listaItensRID;
	}
	
	/**
	 * Valida os filtros antes de emitir o relatório.
	 * Método não invocado por JSP's.
	 * @return
	 */
	private boolean validaFiltros(){
		ValidatorUtil.validaInt(getAnoInicial(), "Ano-Período Inicial", erros);
		ValidatorUtil.validaInt(getAnoFinal(), "Ano-Período Final", erros);
		if( !hasErrors() ){
			
			Date dataInicial = CalendarUtils.createDate(01, getPeriodoInicial() == 1 ? 0 : 11 , getAnoInicial());
			Date dataFim = CalendarUtils.createDate(01, getPeriodoFinal() == 1 ? 0 : 11 , getAnoFinal());
		
			if (dataInicial.after(dataFim)) {
				addMensagemErro("Ano-Período Incial não deve ser inferior ao Ano-Período Final");
			}
		}

		return !hasErrors();
	}
	
}