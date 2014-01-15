/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 31/01/2008
 * 
 */
package br.ufrn.sigaa.ava.dominio;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;

/**
 * Tarefa atribuída a uma turma no portal da Turma Virtual.
 *
 * @author David Pereira
 *
 */
@Entity  @HumanName(value="Tarefa", genero='F')
@Table(name = "tarefa", schema = "ava")
@PrimaryKeyJoinColumn (name="id_tarefa_turma", referencedColumnName="id_atividade_avaliavel")
public class TarefaTurma extends AtividadeAvaliavel implements DominioTurmaVirtual{

	/** Define o conteúdo da tarefa. */
	private String conteudo;

	/** Define até que data a tarefa pode ser entregue. */
	@Column(name = "data_entrega")
	private Date dataEntrega;
	
	/** Define até que horas a tarefa pode ser entregue. */
	@Column(name="hora_entrega")
	private int horaEntrega = 23;
	
	/** Define até que minutos a tarefa pode ser entregue. */
	@Column(name="minuto_entrega")
	private int minutoEntrega = 59;
	
	/** Define a data em que a tarefa inicia. */
	@Column(name="data_inicio")
	private Date dataInicio;
	
	/** Define a hora da data em que a tarefa inicia. */
	@Column(name="hora_inicio")
	private int horaInicio;
	
	/** Define o minuto da data em que a tarefa inicia. */
	@Column(name="minuto_inicio")
	private int minutoInicio;

	/** Se é para o aluno submeter um arquivo como resposta. */
	@Column(name = "envio_arquivo")
	private boolean envioArquivo = true;
	
	/** Se a tarefa deve ser respondida com um campo de texto na turma virtual. */
	@Column(name = "resposta_online")
	private boolean respostaOnline;

	/** Indica se o aluno pode enviar uma tarefa mais que uma vez. */
	@Column(name = "permite_novo_envio")
	private boolean permiteNovoEnvio;
	
	/** Indica se o aluno pode enviar uma tarefa mais que uma vez. */
	private boolean emGrupo = false;
	
	/** Indica se o aluno já enviou a tarefa. */
	@Transient
	private boolean alunoJaEnviou;
	
	/** Indica se o professor já corrigiu tarefa. */
	@Transient
	private boolean professorJaCorrigiu;
	
	/** Respostas dos alunos para esta tarefa. */
	@OneToMany(mappedBy="tarefa", cascade=CascadeType.ALL)
	private List<RespostaTarefaTurma> respostas;
	
	/** Grupos de alunos que estão fazendo a tarefa. */
	@OneToMany(mappedBy="tarefa", cascade=CascadeType.ALL)
	private List<GrupoTarefaTurma> grupos;
	
	/** Identificador do arquivo que foi enviado com a tarefa. */
	@Column(name="id_arquivo")
	private Integer idArquivo;
		
	/** Quantidade de respostas da tarefa. */
	@Transient
	private int qtdRespostas;

	/** Se a nota da tarefa está configurada como média ponderada. */
	@Transient
	private boolean mediaPonderada;
	
	/** Arquivo que foi enviado na tarefa pelo professor. */
	@Transient
	private UploadedFile arquivo;
	
	public TarefaTurma() {
		tipoAtividade = AtividadeAvaliavel.TIPO_TAREFA;
		material = new MaterialTurma (TipoMaterialTurma.TAREFA);
	}
	
	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Date getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}
	
	public boolean isPermiteNovoEnvio() {
		return permiteNovoEnvio;
	}

	public void setPermiteNovoEnvio(boolean permiteNovoEnvio) {
		this.permiteNovoEnvio = permiteNovoEnvio;
	}

	public boolean isEnvioArquivo() {
		return envioArquivo;
	}

	public void setEnvioArquivo(boolean envioArquivo) {
		this.envioArquivo = envioArquivo;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		if (!StringUtils.notEmpty(titulo))
			lista.addErro("Digite o nome da tarefa.");
		
		if (!StringUtils.notEmpty(conteudo))
			lista.addErro("Digite o Texto da tarefa.");		
		
		if (StringUtils.notEmpty(titulo) && titulo.length() > 250)
			lista.addMensagem(MensagensTurmaVirtual.TAMANHO_TEXTO_MAIOR_MAXIMO, "Título", titulo.length(), 250);

		if (dataEntrega == null)
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data para a entrega da tarefa");

		if (getId() == 0){
			if (dataEntrega != null && !DateUtils.isSameDay(new Date(), dataEntrega) && new Date().after(dataEntrega))
				lista.addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Data para entrega", "Data atual");			
		}

		if (getAula().getId() <= 0)
			lista.addErro("Selecione um tópico para a tarefa.");

		if (!envioArquivo && permiteNovoEnvio)
			lista.addErro("\"Permitir novo envio\" só pode ser selecionado se o tipo da tarefa for \"Envio de Arquivo\"");
		
		if (possuiNota && unidade <= 0)
			lista.addErro("Como a tarefa tem uma nota associada, é necessário escolher a unidade correspondente.");

		return lista;
	}

	@Override
	public String getNome() {
		return titulo;
	}

	public List<RespostaTarefaTurma> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<RespostaTarefaTurma> respostas) {
		this.respostas = respostas;
	}

	public boolean isEmGrupo() {
		return emGrupo;
	}

	public void setEmGrupo(boolean emGrupo) {
		this.emGrupo = emGrupo;
	}

	public List<GrupoTarefaTurma> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<GrupoTarefaTurma> grupos) {
		this.grupos = grupos;
	}

	/**
	 * Retorna a turma do grupo.
	 * @Deprecated Não usar. Foi criado por imposição da interface mas não está implementado.
	 */
	@Deprecated
	public Turma getTurma() {
		return null;
	}

	/**
	 * Seta a turma do grupo
	 * @Deprecated Não usar. Foi criado por imposição da interface mas não está implementado.
	 */
	@Deprecated
	public void setTurma(Turma turma) {
		
	}

	public String getMensagemAtividade() {
		return "Nova tarefa: " + titulo;
	}

	@Override
	public boolean isSite() {
		return false;
	}

	public int getHoraEntrega() {
		return horaEntrega;
	}

	public void setHoraEntrega(int horaEntrega) {
		this.horaEntrega = horaEntrega;
	}

	public int getMinutoEntrega() {
		return minutoEntrega;
	}

	public void setMinutoEntrega(int minutoEntrega) {
		this.minutoEntrega = minutoEntrega;
	}

	public int getQtdRespostas() {
		return qtdRespostas;
	}

	public void setQtdRespostas(int qtdRespostas) {
		this.qtdRespostas = qtdRespostas;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public boolean isMediaPonderada() {
		return mediaPonderada;
	}

	public void setMediaPonderada(boolean mediaPonderada) {
		this.mediaPonderada = mediaPonderada;
	}

	public boolean isRespostaOnline() {
		return respostaOnline;
	}

	public void setRespostaOnline(boolean respostaOnline) {
		this.respostaOnline = respostaOnline;
	}

	public boolean isAlunoJaEnviou() {
		return alunoJaEnviou;
	}

	public void setAlunoJaEnviou(boolean alunoJaEnviou) {
		this.alunoJaEnviou = alunoJaEnviou;
	}

	public boolean isProfessorJaCorrigiu() {
		return professorJaCorrigiu;
	}

	public void setProfessorJaCorrigiu(boolean professorJaCorrigiu) {
		this.professorJaCorrigiu = professorJaCorrigiu;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public int getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(int horaInicio) {
		this.horaInicio = horaInicio;
	}

	public int getMinutoInicio() {
		return minutoInicio;
	}

	public void setMinutoInicio(int minutoInicio) {
		this.minutoInicio = minutoInicio;
	}
	
	/**
	 * Informa se está no período para responder a esta tarefa.
	 * 
	 * @return true se a data atual estiver entre a dataInicio e a dataEntrega
	 */
	public boolean isDentroPeriodoEntrega () {
		
		Date dataEntrega = new Date();
	    
	    Calendar c = Calendar.getInstance();
	    
	    c.setTime(getDataInicio());
	    c.set(Calendar.HOUR, getHoraInicio());
	    c.set(Calendar.MINUTE, getMinutoInicio());
	    Date dataInicio = c.getTime();
	    
	    c.setTime(getDataEntrega());
	    c.set(Calendar.HOUR, getHoraEntrega());
	    c.set(Calendar.MINUTE, getMinutoEntrega());
	    c.set(Calendar.SECOND, 59);
	    Date dataFim = c.getTime();
	    
	    return dataEntrega.after(dataInicio) && dataEntrega.before(dataFim);
	}

	/**
	 * Identifica o tipo de atividade.
	 *	<ul>
	 * 		<li>TAREFA = 1;</li>
	 * 		<li>QUESTIONÁRIO = 2;</li>
	 *	</ul> 
	 */
	public int getAtividade(){
		return AtividadeAvaliavel.TIPO_TAREFA;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	@Override
	public String getDescricaoGeral() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String result = "Inicia em " + sdf.format(dataInicio) + " às " + horaInicio + "h " + minutoInicio +"min " +
				"e finaliza em " + sdf.format(dataEntrega) + " às " + horaEntrega + "h " + minutoEntrega  + "min";
		return result;
	}
}