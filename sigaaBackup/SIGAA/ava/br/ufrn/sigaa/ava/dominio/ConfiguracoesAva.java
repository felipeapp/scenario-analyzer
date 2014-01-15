/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 18/01/2008
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe de dom�nio que guarda as configura��es para uma turma virtual.<br/>
 * Estas configura��es permitem ao professor personalizar uma turma virtual para melhor atender
 * �s necessidades da turma real.<br/>
 * Alguns atributos da turma virtual que s�o configur�veis s�o a possibilidade de o aluno criar f�rums ou enquetes
 * e com que forma as notas das unidade ser�o calculadas, de acordo com as avalia��es nelas cadastradas. 
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="configuracoes_ava", schema="ava") @HumanName(value="Configura��es", genero='F')
public class ConfiguracoesAva implements DominioTurmaVirtual {
	
	/** Define o tipo de listagem da turma virtual para o caso de uma lista */
	public static final int ESTILO_TOPICOS_LISTA = 1;
	/** Define o tipo de listagem da turma virtual para o caso de pagina��o */
	public static final int ESTILO_TOPICOS_PAGINADOS = 2;
	
	/** Define o valor que identifica o template de tr�s colunas. */
	public static final int TEMPLATE_TRES_COLUNAS = 1;
	/** Define o valor que identifica o template de duas colunas, com menu drop down. */
	public static final int TEMPLATE_DROP_DOWN = 2;

	/** Define o valor que identifica se o tipo de avalia��o � m�dia ponderada. */
	public static char TIPO_AVALIACAO_MEDIA_PONDERADA = 'P';
	/** Define o valor que identifica se o tipo de avalia��o � m�dia aritm�tica. */
	public static char TIPO_AVALIACAO_MEDIA_ARITMETICA = 'A';
	/** Define o valor que identifica se o tipo de avalia��o � soma das notas. */
	public static char TIPO_AVALIACAO_SOMA_AVALIACOES = 'S';
	/** Define o valor que identifica se o aluno s� deve visualizar sua pr�pria nota. */
	public static char TIPO_VISUALIZACAO_NOTA_INDIVIDUAL = 'I';
	/** Define o valor que identifica se o aluno pode visualisas todas as notas. */
	public static char TIPO_VISUALIZACAO_NOTAS_TODOS_ALUNOS = 'A';
	
	/** Define o n�mero de unidade m�ximas comtepladas pelos m�todos 'AvaliacaoMedia'. */
	private static final int NUMERO_UNIDADES_MAXIMO_IMPLEMENTADAS = 3;
	
	/**
	 * Define a unicidade da configura��o da turma virtual.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Associa a as configura��es a turma virtual */
	@ManyToOne @JoinColumn(name="id_turma")
	private Turma turma;
	
	/** Permite ao aluno da turma virtual criar um f�rum */
	@Column(name = "aluno_criar_forum")
	private boolean permiteAlunoCriarForum;

	/** Permite ao aluno criar uma enquete na turma virtual */
	@Column(name = "aluno_criar_enquete")
	private boolean permiteAlunoCriarEnquete;

	/** Permite ao aluno modificar o nome do seu grupo */
	@Column(name = "aluno_modificar_nome_grupo")
	private boolean permiteAlunoModificarNomeGrupo;
	
	/** Permite a visualiza��o da nota pelo aluno na turma virtual */
	@Column(name = "tipo_visualizacao_nota")
	private Character tipoVisualizacaoNota; 

	/** Permite visualizar a m�dia da turma */
	@Column(name = "media_turma")
	private boolean mostrarMediaDaTurma;
	
	/** Permite mostrar o relat�rio de estat�stica de notas. */
	@Column(name="mostrar_estatistica_notas")
	private boolean mostrarEstatisticaNotas;

	/** Data final da primeira unidade */
	private Date dataFimPrimeiraUnidade;
	
	/** Data final da segunda unidade */
	private Date dataFimSegundaUnidade;
	
	/** Data final da terceira unidade */
	private Date dataFimTerceiraUnidade;
	
	/** Permite a visualiza��o externa da turma virtual */
	@Column(name="permite_visualizacao_externa")
	private boolean permiteVisualizacaoExterna;
	
	/** Define a data em que a turma virutal foi disponibilizada para p�blico externo */
	@Column(name="data_visualizacao_externa")
	private Date dataVisualizacaoExterna;
	
	/** Indica se o cadastro de avalia��es utilizar� m�dia ponderada ou soma de notas para o c�lculo das m�dias dos alunos na consolida��o. 
	 * Caracteres de acordo com o seguinte significado: P - Com Pesos; A - M�dia Aritm�tica; S - Soma */
	@Column(name="tipo_media_avaliacoes_1")
	private Character tipoMediaAvaliacoes1;
	
	/** Tipo m�dia da segunda unidade  para avalia��o da turma */
	@Column(name="tipo_media_avaliacoes_2")
	private Character tipoMediaAvaliacoes2;
	
	/** Tipo m�dia da terceira unidade para avalia��o da turma */
	@Column(name="tipo_media_avaliacoes_3")
	private Character tipoMediaAvaliacoes3;
	
	/** Tamanho m�ximo para os arquivos enviados pelos alunos nesta turma. Padr�o � 10. */
	@Column(name="tamanho_upload_aluno")
	private int tamanhoUploadAluno = 10;
	
	/**
	 * Senha utilizada pelos usu�rios (geralmente professores) que ir�o iniciar o sistema
	 * de chamada biom�trica na sala de aula.
	 */
	@Column(name="senha_chamada_bio")
	private String senhaChamadaBiometrica;
	
	/**
	 * Limite de tempo m�ximo que permite mesmo os alunos chegando atrasados, 
	 * receberem presen�a completa  da aula correspondente, quando registram a presen�a
	 * por biometria.
	 */
	@Column(name="tempo_tolerancia")
	private Integer tempoTolerancia;
	
	/**
	 * Informa se a aula deve ser iniciada exibindo os t�picos em lista ou paginados.
	 */
	@Column(name="estilo_visualizacao_topicos")
	private int estiloVisualizacaoTopicos = ESTILO_TOPICOS_LISTA;
	
	/**
	 * Informa se os alunos devem visualizar suas notas.
	 */
	@Column(name="ocultar_notas")
	private boolean ocultarNotas;
	
	/**
	 * Informa se o docente deseja que sua foto seja mostrada nos t�picos de aula, caso exista mais de um docente na turma.
	 */
	@Column(name="mostrar_foto_topico")
	private boolean mostrarFotoTopico;
	
	/** Indica o template do layout da turma virtual. */
	private int template = TEMPLATE_TRES_COLUNAS;
	
	/**
	 * Retorna a unicidade da configura��o da turma virtual.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Define a unicidade da configura��o da turma virtual.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retorna a turma que est� associada a configura��o 
	 */
	public Turma getTurma() {
		return turma;
	}

	/**
	 * Define a turma que est� associada a configura��o 
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/**
	 * Retorna se um aluno pode criar f�rum
	 * @return
	 */
	public boolean isPermiteAlunoCriarForum() {
		return permiteAlunoCriarForum;
	}

	/**
	 * Define que o aluno pode criar f�rum
	 * @param permiteAlunoCriarForum
	 */
	public void setPermiteAlunoCriarForum(boolean permiteAlunoCriarForum) {
		this.permiteAlunoCriarForum = permiteAlunoCriarForum;
	}

	/**
	 * Retorna se o aluno pode criar enquete na turma virtual.
	 * @return
	 */
	public boolean isPermiteAlunoCriarEnquete() {
		return permiteAlunoCriarEnquete;
	}

	/**
	 * Define se o aluno pode criar enquete na turma virtual.
	 * @param permiteAlunoCriarEnquete
	 */
	public void setPermiteAlunoCriarEnquete(boolean permiteAlunoCriarEnquete) {
		this.permiteAlunoCriarEnquete = permiteAlunoCriarEnquete;
	}

	/**
	 * Retorna o tipo definido de visualiza��o de nota na turma.
	 * @return
	 */
	public Character getTipoVisualizacaoNota() {
		return tipoVisualizacaoNota;
	}

	/**
	 * Define o tipo de visualiza��o da nota na turma virtual.
	 * @param tipoVisualizacaoNota
	 */
	public void setTipoVisualizacaoNota(Character tipoVisualizacaoNota) {
		this.tipoVisualizacaoNota = tipoVisualizacaoNota;
	}

	/**
	 * Retorna se a m�dia da turma pode ser visualizada.
	 * @return
	 */
	public boolean isMostrarMediaDaTurma() {
		return mostrarMediaDaTurma;
	}

	/**
	 * Define a exibi��o da m�dia da turma.
	 * @param mostrarMediaDaTurma
	 */
	public void setMostrarMediaDaTurma(boolean mostrarMediaDaTurma) {
		this.mostrarMediaDaTurma = mostrarMediaDaTurma;
	}
	
	/**
	 * Retorna se � poss�vel visualizar todas as notas.
	 * Somente se todas as notas das avalia��es estiverem preenchidas.
	 * @return
	 */
	public boolean isMostrarTodasAsNotas() {
		if (tipoVisualizacaoNota != null)
			return tipoVisualizacaoNota == 'A';
		else
			return false;
	}

	/**
	 * Retorna a data final da 1� unidade
	 * @param dataFimTerceiraUnidade
	 */
	public Date getDataFimPrimeiraUnidade() {
		return dataFimPrimeiraUnidade;
	}

	/**
	 * Define a data final da 1� unidade
	 * @param dataFimTerceiraUnidade
	 */
	public void setDataFimPrimeiraUnidade(Date dataFimPrimeiraUnidade) {
		this.dataFimPrimeiraUnidade = dataFimPrimeiraUnidade;
	}

	/**
	 * Retorna a data final definida da 2� unidade
	 * @param dataFimTerceiraUnidade
	 */
	public Date getDataFimSegundaUnidade() {
		return dataFimSegundaUnidade;
	}

	/**
	 * Define a data final da 2� unidade
	 * @param dataFimTerceiraUnidade
	 */
	public void setDataFimSegundaUnidade(Date dataFimSegundaUnidade) {
		this.dataFimSegundaUnidade = dataFimSegundaUnidade;
	}

	/**
	 * Retorna a data final definida da 3� unidade
	 * @param dataFimTerceiraUnidade
	 */
	public Date getDataFimTerceiraUnidade() {
		return dataFimTerceiraUnidade;
	}

	/**
	 * Define a data final da 3� unidade
	 * @param dataFimTerceiraUnidade
	 */
	public void setDataFimTerceiraUnidade(Date dataFimTerceiraUnidade) {
		this.dataFimTerceiraUnidade = dataFimTerceiraUnidade;
	}

	/**
	 * Retorna uma mensagem da atividade da turma virtual.
	 */
	public String getMensagemAtividade() {
		return null;
	}

	/**
	 * Retorna se � permitido visualiza��o da turma ao p�blico externo.
	 * @return
	 */
	public boolean isPermiteVisualizacaoExterna() {
		return permiteVisualizacaoExterna;
	}

	/**
	 * Popula se � permitido visualiza��o da turma ao p�blico externo.
	 * @return
	 */
	public void setPermiteVisualizacaoExterna(boolean permiteVisualizacaoExterna) {
		this.permiteVisualizacaoExterna = permiteVisualizacaoExterna;
	}

	/**
	 * Retorna o tipo da m�dia da 1� Avalia��o.
	 * @return
	 */
	public Character getTipoMediaAvaliacoes1() {
		return tipoMediaAvaliacoes1;
	}

	/**
	 * Popula o tipo da m�dia da 1� Avalia��o.
	 * @return
	 */
	public void setTipoMediaAvaliacoes1(Character tipoMediaAvaliacoes1) {
		this.tipoMediaAvaliacoes1 = tipoMediaAvaliacoes1;
	}

	/**
	 * Retorna o tipo da m�dia ad 2� avalia��o
	 * @return
	 */
	public Character getTipoMediaAvaliacoes2() {
		return tipoMediaAvaliacoes2;
	}

	/**
	 * Popula o tipo da m�dia ad 2� avalia��o
	 * @return
	 */
	public void setTipoMediaAvaliacoes2(Character tipoMediaAvaliacoes2) {
		this.tipoMediaAvaliacoes2 = tipoMediaAvaliacoes2;
	}

	/**
	 * Retorna o tipo da m�dia da 3� avalia��o.
	 * @return
	 */
	public Character getTipoMediaAvaliacoes3() {
		return tipoMediaAvaliacoes3;
	}

	/**
	 * Popula o tipo da m�dia da 3� avalia��o.
	 * @return
	 */
	public void setTipoMediaAvaliacoes3(Character tipoMediaAvaliacoes3) {
		this.tipoMediaAvaliacoes3 = tipoMediaAvaliacoes3;
	}
	
	/**
	 * Retorna a o tipo da m�dia da avalia��o.
	 * @param unidade
	 * @return
	 */
	public Character getTipoMediaAvaliacoes(int unidade) {
		if (unidade == 1) return getTipoMediaAvaliacoes1();
		else if (unidade == 2) return getTipoMediaAvaliacoes2();
		else if (unidade == 3) return getTipoMediaAvaliacoes3();
		else return null;
	}

	/**
	 * Retorna se deve ser efetuado a m�dia ponderada das avalia��es.
	 * @param unidade
	 * @return
	 */
	public boolean isAvaliacoesMediaPonderada(int unidade) {
		if (unidade == 1) return isAvaliacoesMediaPonderada1();
		else if (unidade == 2) return isAvaliacoesMediaPonderada2();
		else if (unidade == 3) return isAvaliacoesMediaPonderada3();
		else return false;
	}
	
	/**
	 * Retorna se deve ser somada as avalia��es de acordo com o tipo
	 * da m�dia da avalia��o.
	 * @param unidade
	 * @return
	 */
	public boolean isAvaliacoesSoma(int unidade) {
		if (unidade == 1) return isAvaliacoesSoma1();
		else if (unidade == 2) return isAvaliacoesSoma2();
		else if (unidade == 3) return isAvaliacoesSoma3();
		else return false;
	}
	
	/**
	 * Retorna se deve ser realizado a m�dia arimtm�tica das avalia��es.
	 * @param unidade
	 * @return
	 */
	public boolean isAvaliacoesMediaAritmetica(int unidade) {
		if (unidade == 1) return isAvaliacoesMediaAritmetica1();
		else if (unidade == 2) return isAvaliacoesMediaAritmetica2();
		else if (unidade == 3) return isAvaliacoesMediaAritmetica3();
		else return false;
	}
	
	/**
	 * Retorna se deve ser realizado a m�dia ponderada da 1� avalia��o
	 * @return
	 */
	public boolean isAvaliacoesMediaPonderada1() {
		return tipoMediaAvaliacoes1 != null && tipoMediaAvaliacoes1 == 'P';
	}
	
	/**
	 * Retorna se deve ser realizado a m�dia aritm�tica da 1� avalia��o
	 * @return
	 */
	public boolean isAvaliacoesMediaAritmetica1() {
		return tipoMediaAvaliacoes1 != null && tipoMediaAvaliacoes1 == 'A';
	}
	
	/**
	 * Retorna se deve ser realizado soma da 1� avalia��o
	 * @return
	 */
	public boolean isAvaliacoesSoma1() {
		return tipoMediaAvaliacoes1 != null && tipoMediaAvaliacoes1 == 'S';
	}
	
	/**
	 * Retorna se deve ser realizado a m�dia ponderada da 2� avalia��o
	 * @return
	 */
	public boolean isAvaliacoesMediaPonderada2() {
		return tipoMediaAvaliacoes2 != null && tipoMediaAvaliacoes2 == 'P';
	}
	
	/**
	 * Retorna se deve ser realizado a m�dia aritm�tica da 2� avalia��o
	 * @return
	 */
	public boolean isAvaliacoesMediaAritmetica2() {
		return tipoMediaAvaliacoes2 != null && tipoMediaAvaliacoes2 == 'A';
	}
	
	/**
	 * Retorna se deve ser realizado soma da 2� avalia��o
	 * @return
	 */
	public boolean isAvaliacoesSoma2() {
		return tipoMediaAvaliacoes2 != null && tipoMediaAvaliacoes2 == 'S';
	}
	
	/**
	 * Retorna se deve ser realizado a m�dia ponderada da 3� avalia��o
	 * @return
	 */
	public boolean isAvaliacoesMediaPonderada3() {
		return tipoMediaAvaliacoes3 != null && tipoMediaAvaliacoes3 == 'P';
	}
	
	/**
	 * Retorna se deve ser realizado a m�dia aritm�tica da 3� avalia��o
	 * @return
	 */
	public boolean isAvaliacoesMediaAritmetica3() {
		return tipoMediaAvaliacoes3 != null && tipoMediaAvaliacoes3 == 'A';
	}
	
	/**
	 * Retorna se deve ser realizado soma da 3� avalia��o
	 * @return
	 */
	public boolean isAvaliacoesSoma3() {
		return tipoMediaAvaliacoes3 != null && tipoMediaAvaliacoes3 == 'S';
	}

	/**
	 * Retorna a senha da chamada biom�trica.
	 * @return
	 */
	public String getSenhaChamadaBiometrica() {
		return senhaChamadaBiometrica;
	}

	/**
	 * Popula a senha da chamada biom�trica informada pelo usu�rio.
	 * @return
	 */
	public void setSenhaChamadaBiometrica(String senhaChamadaBiometrica) {
		this.senhaChamadaBiometrica = senhaChamadaBiometrica;
	}

	/**
	 * Retorna o tempo m�ximo de toler�ncia.
	 * @return
	 */
	public Integer getTempoTolerancia() {
		return tempoTolerancia;
	}

	/**
	 * Popula o tempo m�ximo de toler�ncia.
	 * @param tempoTolerancia
	 */
	public void setTempoTolerancia(Integer tempoTolerancia) {
		this.tempoTolerancia = tempoTolerancia;
	}

	/**
	 * Retorna o tamanho m�ximo permitido do arquivo para upload 
	 * @return
	 */
	public int getTamanhoUploadAluno() {
		return tamanhoUploadAluno;
	}

	/**
	 * Popula o tamanho m�ximo permitido do arquivo para upload 
	 * @return
	 */
	public void setTamanhoUploadAluno(int tamanhoUploadAluno) {
		this.tamanhoUploadAluno = tamanhoUploadAluno;
	}

	/**
	 * Retorna o estilo definido para visualiza��o dos t�picos de aula.
	 * @return
	 */
	public int getEstiloVisualizacaoTopicos() {
		return estiloVisualizacaoTopicos;
	}

	/**
	 * Popula o estilo definido para visualiza��o dos t�picos de aula.
	 * @return
	 */
	public void setEstiloVisualizacaoTopicos(int estiloVisualizacaoTopicos) {
		this.estiloVisualizacaoTopicos = estiloVisualizacaoTopicos;
	}

	/**
	 * Retorna a data definida para visualiza��o externa da turma virtual.
	 * @return
	 */
	public Date getDataVisualizacaoExterna() {
		return dataVisualizacaoExterna;
	}

	/**
	 * Popula a data definida para visualiza��o externa da turma virtual.
	 * @param dataVisualizacaoExterna
	 */
	public void setDataVisualizacaoExterna(Date dataVisualizacaoExterna) {
		this.dataVisualizacaoExterna = dataVisualizacaoExterna;
	}

	public int getTemplate() {
		return template;
	}

	public void setTemplate(int template) {
		this.template = template;
	}

	public void setOcultarNotas(boolean ocultarNotas) {
		this.ocultarNotas = ocultarNotas;
	}

	public boolean isOcultarNotas() {
		return ocultarNotas;
	}

	public boolean isMostrarEstatisticaNotas() {
		return mostrarEstatisticaNotas;
	}

	public void setMostrarEstatisticaNotas(boolean mostrarEstatisticaNotas) {
		this.mostrarEstatisticaNotas = mostrarEstatisticaNotas;
	}

	public boolean isMostrarFotoTopico() {
		return mostrarFotoTopico;
	}

	public void setMostrarFotoTopico(boolean mostrarFotoTopico) {
		this.mostrarFotoTopico = mostrarFotoTopico;
	}
	
	public int getNumeroUnidadesMaximoImplementadas() {
		return NUMERO_UNIDADES_MAXIMO_IMPLEMENTADAS;
	}

	public void setPermiteAlunoModificarNomeGrupo(
			boolean permiteAlunoModificarNomeGrupo) {
		this.permiteAlunoModificarNomeGrupo = permiteAlunoModificarNomeGrupo;
	}

	public boolean isPermiteAlunoModificarNomeGrupo() {
		return permiteAlunoModificarNomeGrupo;
	}
	
}
