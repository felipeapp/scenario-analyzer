/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 18/01/2008
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe que auxilia na importação dos dados anteriores para turmas novas.
 * 
 * @author David Pereira
 *
 */
public class ImportacaoDadosTurmasAnteriores implements PersistDB {

	/**Chave primária */
	private int id;
	
	/** Turma para onde serão importados os dados. */
	private Turma turmaAtual;
	
	/** Turma de onde estão sendo importados os dados. */
	private Turma turmaAnterior;
	
	/** Data da importação dos dados. */
	private Date data;
	
	/** Usuário que está importando os dados. */
	private Usuario usuario;
	
	/** Se os tópicos de aula serão importados. */
	private boolean topicosDeAula = true;
	/** Se o plano de ensino será importado. */
	private boolean planoDeEnsino;
	/** Se os conteudos serão importados. */
	private boolean conteudos;
	/** Se os arquivos serão importados. */
	private boolean arquivos;
	/** Se os fóruns serão importados. */
	private boolean foruns;
	/** Se as enuetes serão importadas. */
	private boolean enquetes;
	/** Se as referências serão importadas. */
	private boolean referencias;
	/** Se o plano de curso será importado. */
	private boolean planoCurso;
	/** Se os videos serão importado. */
	private boolean videos;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Turma getTurmaAtual() {
		return turmaAtual;
	}

	public void setTurmaAtual(Turma turmaAtual) {
		this.turmaAtual = turmaAtual;
	}

	public Turma getTurmaAnterior() {
		return turmaAnterior;
	}

	public void setTurmaAnterior(Turma turmaAnterior) {
		this.turmaAnterior = turmaAnterior;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isTopicosDeAula() {
		return topicosDeAula;
	}

	public void setTopicosDeAula(boolean topicosDeAula) {
		this.topicosDeAula = topicosDeAula;
	}

	public boolean isPlanoDeEnsino() {
		return planoDeEnsino;
	}

	public void setPlanoDeEnsino(boolean planoDeEnsino) {
		this.planoDeEnsino = planoDeEnsino;
	}

	public boolean isConteudos() {
		return conteudos;
	}

	public void setConteudos(boolean conteudos) {
		this.conteudos = conteudos;
	}

	public boolean isArquivos() {
		return arquivos;
	}

	public void setArquivos(boolean arquivos) {
		this.arquivos = arquivos;
	}

	public boolean isForuns() {
		return foruns;
	}

	public void setForuns(boolean foruns) {
		this.foruns = foruns;
	}

	public boolean isEnquetes() {
		return enquetes;
	}

	public void setEnquetes(boolean enquetes) {
		this.enquetes = enquetes;
	}

	public boolean isReferencias() {
		return referencias;
	}

	public void setReferencias(boolean referencias) {
		this.referencias = referencias;
	}

	public void setPlanoCurso(boolean planoCurso) {
		this.planoCurso = planoCurso;
	}

	public boolean isPlanoCurso() {
		return planoCurso;
	}

	public void setVideos(boolean videos) {
		this.videos = videos;
	}

	public boolean isVideos() {
		return videos;
	}

}
