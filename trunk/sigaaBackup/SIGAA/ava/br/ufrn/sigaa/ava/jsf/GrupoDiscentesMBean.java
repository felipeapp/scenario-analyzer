/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em '01/09/2010'
 *
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.GrupoDiscentesDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.GrupoDiscentes;
import br.ufrn.sigaa.ava.negocio.MovimentoGrupoDiscentes;
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean que gerencia os grupos de discentes da Turma Virtual.
 * 
 * @author Fred_Castro
 */

@Component("grupoDiscentes")
@Scope("request")
public class GrupoDiscentesMBean extends CadastroTurmaVirtual<GrupoDiscentes> {

	/** Quantidade de grupos da turma virtual. */
	private Integer numeroGrupos;
	/** Se os grupos deverão ser cadastrados de forma aleatória. */
	private boolean gerarGruposAleatorios;
	/** Lista de discentes da turma. */
	private List <Discente> discentes;
	/** Lista de grupos da turma. */
	private List <GrupoDiscentes> grupos;
	/** Lista de grupos a remover. */
	private List <GrupoDiscentes> gruposARemover = new ArrayList <GrupoDiscentes> ();
	/** Grupo de discentes movimentado. */
	private GrupoDiscentes grupoAluno;
	/** Desfaz os grupos. */
	private boolean desfazer = false;
	/** Nome do Grupo a ser alterado */
	private String nomeGrupo;
	
	public GrupoDiscentesMBean () {
	}
	
	/**
	 * Inicia o caso de uso.<br/><br/>
	 * Método não invocado por JSPs.<br/>
	 * @see br.ufrn.sigaa.ava.jsf.MenuTurmaMBean#acessarGerenciarGrupos()
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String gerenciar () throws ArqException, NegocioException {

		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		discentes = new ArrayList <Discente> ();
		List <MatriculaComponente> matriculas = (List<MatriculaComponente>) tBean.getDiscentesTurma();
		
		if ( matriculas == null )
		{	
			addMensagemErro("Esta turma não possui discentes matriculados!");
			return null;
		}
		for (MatriculaComponente m : matriculas){
			Discente d = m.getDiscente().getDiscente();
			// Indica se o discente trancou a matrícula.
			if (m.isTrancado())
				d.setSelecionado(true);
			
			discentes.add(d);
		}
		
		grupos = lista();
		
		// Descobre quais discentes estão com grupos.
		for (GrupoDiscentes g : grupos)
			for (Discente d : g.getDiscentes())
				if (discentes.indexOf(d) >= 0){
					// Indica se o discente trancou a matrícula para que este apareça destacado.
					d.setSelecionado(discentes.get(discentes.indexOf(d)).isSelecionado());
					discentes.remove(d);
				}
		
		// Remove os discentes trancados para que não seja possível criar grupos com eles.
		for (int i = 0; i < discentes.size(); i++)
			if (discentes.get(i).isSelecionado()){
				discentes.remove(i);
				i--;
			}
		
		numeroGrupos = grupos.size();
		tBean.setGrupos(null);
		
		return forward("/ava/GrupoDiscentes/index.jsp");
	}
	
	@Override
	public List <GrupoDiscentes> lista() {
		try {
			GrupoDiscentesDao dao = getDAO(GrupoDiscentesDao.class);
			List <GrupoDiscentes> rs = dao.findGruposDiscentesAtivosByTurma(turma().getId());

			if (rs.size() <= 0)
				return new ArrayList <GrupoDiscentes> ();
			
			return rs;
		} catch (DAOException e){
			tratamentoErroPadrao (e);
		}
		
		return null;
	}
	
	/**
	 * Exibe a página que lista os objetos do tipo do parâmetro T.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/GrupoDiscentes/ver_grupo.jsp</li>
	 * </ul>
	 * @return
	 */
	public String listar() {
		
		GrupoDiscentesDao gDao = null;
		
		try{
			gDao = getDAO(GrupoDiscentesDao.class);
			TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			if ( getUsuarioLogado().getDiscente() != null )
				grupoAluno = gDao.findGrupoDiscenteAtivosByDiscenteTurma(getUsuarioLogado().getDiscente().getId(),tBean.getTurma().getId());
			else
				addMensagemErro("O usuário não é um discente.");
			
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (gDao != null)
				gDao.close();
		}
		
		return forward("/ava/" + getClasse().getSimpleName() + "/ver_grupo.jsp");
	}
	
	/**
	 * Prepara o grupo para atualização do seu nome.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/GrupoDiscentes/index.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public void atualizar(ActionEvent evt) throws DAOException {
		
		Integer id = getParameterInt("idGrupo",0);
		GenericDAO dao = null;
		
		try {
			if ( id == 0 ) {
				int numCriacao = getParameterInt("numCriacao",0);
				
				for ( GrupoDiscentes grupo : grupos )
					if ( grupo.getNumCriacao() == numCriacao )
						grupoAluno = grupo;
				return;
			}	
			dao = getGenericDAO();
			grupoAluno = dao.findByPrimaryKey(id,GrupoDiscentes.class);
			nomeGrupo = grupoAluno.getNome();
		} finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Altera o nome do grupo.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/GrupoDiscentes/alterar.jsp</li>
	 * <li>sigaa.war/ava/GrupoDiscentes/index.jsp</li>
	 * </ul>
	 * @return
	 */
	public void alterar(ActionEvent evt) {
		
		// Trecho utilizado quando o professor está alterando o nome do grupo
		if ( !isEmpty(nomeGrupo) )
			grupoAluno.setNome(nomeGrupo);
			
		// O grupo cujo o nome está sendo alterado ainda não foi criado
		if (grupoAluno.getId() == 0)
			return;
			
		prepare(SigaaListaComando.ATUALIZAR_AVA);
		registrarAcao(grupoAluno.getNome(), EntidadeRegistroAva.GRUPO, AcaoAva.INICIAR_ALTERACAO, grupoAluno.getId());
		
		Specification specification = getEspecificacaoAtualizacao();
		if (specification == null || specification instanceof NullSpecification ) 
			specification = getEspecificacaoCadastro();
		
		execute(SigaaListaComando.ATUALIZAR_AVA, grupoAluno, specification);
		
		// Trecho utilizado quando o professor está alterando o nome do grupo
		if ( grupos != null ) {
			
			for ( GrupoDiscentes g : grupos )
				if ( g.getId() == grupoAluno.getId() )
					g.setNome(grupoAluno.getNome());
			
			Collections.sort(grupos, new Comparator<GrupoDiscentes>(){
				public int compare(GrupoDiscentes g1, GrupoDiscentes g2) {
					return g1.getNome().compareToIgnoreCase(g2.getNome());
				}
			});
		}			
		registrarAcao(grupoAluno.getNome(), EntidadeRegistroAva.GRUPO, AcaoAva.ALTERAR, grupoAluno.getId());
	}
		
	/**
	 * Modifica a turma para possuir a quantidade certa de grupos.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/GrupoDiscentes/index.jsp</li>
	 * </ul>
	 * @param e
	 */
	@SuppressWarnings("null")
	public void gerenciarNumeroGrupos (ActionEvent e) {
		if (numeroGrupos > grupos.size())
			for (int i = grupos.size(); i < numeroGrupos; i++){
				GrupoDiscentes g = new GrupoDiscentes ();
				g.setNome("Grupo " + (grupos.size() + 1));
				g.setNumCriacao(grupos.size() + 1);
				g.setTurma(turma());
				grupos.add(g);
			}
		else
			while (grupos.size() > numeroGrupos){
				GrupoDiscentes g = grupos.remove(grupos.size() -1);
				
				for (Discente d : g.getDiscentes())
					discentes.add(d);
				
				// Se é um grupo já salvo no banco, marca para desativação.
				if (g.getId() > 0)
					gruposARemover.add(g);
			}
		
		// Se o docente selecionou para o sistema gerar grupos aleatórios, aloca os discentes restantes nos novos grupos.
		if (gerarGruposAleatorios && grupos.size() > 0){
			while (discentes.size() > 0){
				
				// Descobre qual o grupo possui menos discentes.
				GrupoDiscentes grupo = null;
				for (GrupoDiscentes g : grupos)
					if (grupo == null)
						grupo = g;
					else if (g.getDiscentes().size() < grupo.getDiscentes().size())
						grupo = g;
				
				
				int indice = (int) ((Math.random()*1000) % discentes.size());
				grupo.getDiscentes().add(discentes.remove(indice));
				grupo.setAlterado(true);
			}
		}
	}
	
	/**
	 * Move um discente de um grupo para outro.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/GrupoDiscentes/index.jsp</li>
	 * </ul>
	 * @param e
	 */
	@SuppressWarnings("null")
	public void moverDiscente (org.richfaces.event.DropEvent e){
		Discente discente = null;
		Discente auxDiscente = new Discente((Integer) e.getDragValue());
		String nomeGrupoDestino = (String) e.getDropValue();
		
		// Remove o discente do grupo anterior.
		if (discentes.contains(auxDiscente))
			discente = discentes.remove(discentes.indexOf(auxDiscente));
		else
			for (GrupoDiscentes g : grupos)
				if (g.getDiscentes().contains(auxDiscente)){
					discente = g.getDiscentes().remove(g.getDiscentes().indexOf(auxDiscente));
					g.setAlterado(true);
					break;
				}
		
		// Adiciona o discente ao novo grupo.
		if (!discente.isSelecionado()){
			if (nomeGrupoDestino.equals("semGrupo"))
				discentes.add(discente);
			else
				for (GrupoDiscentes g : grupos)
					if (g.getNome().equals(nomeGrupoDestino)){
						g.getDiscentes().add(discente);
						g.setAlterado(true);
						break;
					}
		}
		setOperacaoAtiva(SigaaListaComando.GERENCIAR_GRUPOS_DISCENTES.getId());
	}
	
	/**
	 * Desfaz os grupos de discentes.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/GrupoDiscentes/index.jsp</li>
	 * </ul>
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String desfazer () throws ArqException, NegocioException {
		gruposARemover.addAll(grupos);
		desfazer = true;
		salvar();
		return preparar();
	}
	
	/**
	 * Salva os grupos de discentes.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/GrupoDiscentes/index.jsp</li>
	 * </ul>
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String salvar () throws ArqException, NegocioException {
		
		if ( isOperacaoAtiva(SigaaListaComando.GERENCIAR_GRUPOS_DISCENTES.getId()) ){
			setOperacaoAtiva(null);

			if ( !desfazer )
				registrarAcao(null, EntidadeRegistroAva.GRUPO, AcaoAva.INICIAR_INSERCAO, turma().getId());
			else
				registrarAcao(null, EntidadeRegistroAva.GRUPO, AcaoAva.INICIAR_REMOCAO, turma().getId());
		
			if ( numeroGrupos == null ) {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Número de Grupos");
				return null;
			}
			MovimentoGrupoDiscentes mov = new MovimentoGrupoDiscentes (turma(), grupos, gruposARemover);
			
			try {
				execute(mov);
				
				if ( !desfazer )
					registrarAcao(null, EntidadeRegistroAva.GRUPO, AcaoAva.INSERIR, turma().getId());
				else
					registrarAcao(null, EntidadeRegistroAva.GRUPO, AcaoAva.REMOVER, turma().getId());
				
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				
				gerenciar();
				TurmaVirtualMBean tBean = getMBean("turmaVirtual");
				return tBean.entrar();			
				
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			} finally {
				desfazer = false;
			}			
		}
		return null;
	}
	
	/**
	 * Inicia o caso de uso.<br/><br/>
	 * Método não invocado por JSPs.<br/>
	 * @see br.ufrn.sigaa.ava.jsf.MenuTurmaMBean#acessarGerenciarGrupos()
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String preparar () throws ArqException, NegocioException {
		
		prepareMovimento(SigaaListaComando.GERENCIAR_GRUPOS_DISCENTES);
		setOperacaoAtiva(SigaaListaComando.GERENCIAR_GRUPOS_DISCENTES.getId());
		
		gerenciar();
		
		return forward("/ava/GrupoDiscentes/index.jsp");
	}
	
	/**
	 * Verifica se o aluno pode alterar o nome do grupo
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/GrupoDiscentes/ver_grupo.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPermiteAlunoModificarNomeGrupo () {
		
		TurmaVirtualDao dao = null;
		 try{
			 dao = getDAO(TurmaVirtualDao.class);
			 ConfiguracoesAva config = dao.findConfiguracoes(turma());
			 if ( config != null && config.isPermiteAlunoModificarNomeGrupo() )
				 return true;
			 else return false;
		 } finally {
			 if (dao != null)
				 dao.close();
		 }

	}
	
	/**
	 * Acessa o caso de uso das configurações da turma virtual.<br/><br/>
	 * 
	 * Método chamado na seguinte JSP:
	 * <ul>
	 * 		<li>sigaa.war/ava/GrupoDiscentes/index.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String iniciarConfiguracoesAva() throws Exception {

		ConfiguracoesAvaMBean cBean = getMBean("configuracoesAva");
		return cBean.configurar(null);
	}
	
	
	public List<GrupoDiscentes> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<GrupoDiscentes> grupos) {
		this.grupos = grupos;
	}

	public List<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(List<Discente> discentes) {
		this.discentes = discentes;
	}

	public Integer getNumeroGrupos() {
		return numeroGrupos;
	}

	public void setNumeroGrupos(Integer numeroGrupos) {
		this.numeroGrupos = numeroGrupos;
	}

	public boolean isGerarGruposAleatorios() {
		return gerarGruposAleatorios;
	}

	public void setGerarGruposAleatorios(boolean gerarGruposAleatorios) {
		this.gerarGruposAleatorios = gerarGruposAleatorios;
	}

	public List<GrupoDiscentes> getGruposARemover() {
		return gruposARemover;
	}

	public void setGruposARemover(List<GrupoDiscentes> gruposARemover) {
		this.gruposARemover = gruposARemover;
	}

	public void setGrupoAluno(GrupoDiscentes grupoAluno) {
		this.grupoAluno = grupoAluno;
	}

	public GrupoDiscentes getGrupoAluno() {
		return grupoAluno;
	}
	
	public int getNumDiscentesSemGrupo () {
		return discentes.size();
	}

	public void setNomeGrupo(String nomeGrupo) {
		this.nomeGrupo = nomeGrupo;
	}

	public String getNomeGrupo() {
		return nomeGrupo;
	}
}