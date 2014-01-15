/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 24/11/2009
 *
 */

package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/** Interface para as constantes de mensagens referentes �s opera��es na gradua��o e que ser�o exibidas ao usu�rio.
 * @author �dipo Elder F. Melo
 *
 */
public interface MensagensGraduacao {
	
	/** Prefixo para estas mensagens. Valor atual: "2_10500_". */
	public static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.GRADUACAO.getId() + "_";
	
	/**
	 * Mensagem exibida quando a busca por turmas retorna vazio.
	 *  
	 * Conte�do: N�o foram encontradas turmas no ano-per�odo informado.
	 * Tipo: ERROR
	 */
	public static final String NENHUMA_TURMA_NO_ANO_PERIODO = PREFIX + "001";
	
	/**
	 * Mensagem exibida quando o discente n�o possui data de cola��o de grau devidamente registrada.
	 *  
	 * Conte�do: O discente selecionado n�o possui data de cola��o de grau registrada.
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_SEM_DATA_DE_COLACAO = PREFIX + "002";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta registrar o diploma de um discente com data de cola��o anterior � definida.<br>
	 * Par�metros: ano, per�odo.<br>
	 * Conte�do: N�o � permitido registrar diplomas de discentes com data de cola��o de grau anterior ao per�odo %d.%d.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String REGISTRO_DIPLOMA_ANTERIOR_AO_PERIODO_INICIO = PREFIX + "003";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta registrar o diploma de uma turma de cola��o de grau,
	 * onde todos os discentes desta turma j� possuem registro de diplomas.<br>
	 * 
	 * Conte�do: N�o h� discentes sem registro de diploma nesta Turma.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String TODOS_DISCENTES_DA_TURMA_FORAM_REGISTRADOS = PREFIX + "004";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta registrar o diploma de uma turma de cola��o de grau,
	 * e n�o seleciona (marca o checkbox) quais discentes ter�o o diploma registrado.<br>
	 * 
	 * Conte�do: Selecione pelo menos um discente para registrar o diploma.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String SELECIONE_DISCENTE_PARA_REGISTAR_DIPLOMA = PREFIX + "005";

	/**
	 * Mensagem exibida quando o usu�rio tenta incluir um curso para registro em um livro que j� possui o curso.<br>
	 * Conte�do: Curso j� inclu�do na lista.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String CURSO_JA_INCLUIDO = PREFIX + "006";

	/**
	 * Mensagem exibida quando o usu�rio tenta registrar o diploma de um curso que n�o possui livro aberto correspondente para registro.<br>
	 * Par�metros: curso.<br>
	 * Conte�do: N�o h� livro aberto para registro do curso %s.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String NAO_HA_LIVRO_ABERTO_PARA_REGISTRO_CURSO = PREFIX + "007";

	/**
	 * Mensagem exibida quando o usu�rio tenta incluir um curso para registro em um livro, e este curso est� sendo registrado em outro livro aberto.<br>
	 * Par�metros: curso, livro.<br>
	 * Conte�do: N�o � poss�vel registrar o curso em mais de um livro aberto. O curso %s � registrado no livro %s.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String CURSO_REGISTRADO_EM_OUTRO_LIVRO_ABERTO = PREFIX + "008";
	
	/**
	 * Mensagem exibida quando o componente possui carga hor�ria maior que o permitido no per�odo letivo de f�rias
	 */
	public static final String ERRO_VALIDACAO_CARGA_HORARIA_DISCIPLINA_FERIAS = PREFIX + "009";
	
	/**
	 * Mensagem exibida quando se est� tentando inativar um componente que possui depend�ncias.<br/><br/>
	 *  
	 * Conte�do: "N�o pode inativar porque existem disciplinas que possuem depend�ncia: %s".<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCIPLINAS_COM_PENDENCIA = PREFIX + "010";
	
	/**
	 * Mensagem exibida quando se est� tentando cadastrar um componente curricular com mais cr�ditos para ead que o permitido, de acordo com o tipo do componente.<br/><br/>
	 *  
	 * Conte�do: "A carga hor�ria para educa��o � dist�ncia n�o pode ultrapassar %s% da carga hor�ria total, de acordo com o tipo de componente selecionado."<br/>
	 * Tipo: ERROR
	 */
	public static final String CH_EAD_ACIMA_DO_LIMITE = PREFIX + "011";
	
	/**
	 * Mensagem exibida quando se est� tentando cadastrar um componente curricular com erro na express�o de pre-requisitos.<br/><br/>
	 *  
	 * Conte�do: "Express�o de pr�-requisito mal formada."<br/>
	 * Tipo: ERROR
	 */
	public static final String EXPRESSAO_PREREQUISITO_MAL_FORMADA = PREFIX + "012";
	
	/**
	 * Mensagem exibida quando se est� tentando cadastrar um componente curricular com erro na express�o de co-requisitos.<br/><br/>
	 *  
	 * Conte�do: "Express�o de co-requisito mal formada."<br/>
	 * Tipo: ERROR
	 */
	public static final String EXPRESSAO_COREQUISITO_MAL_FORMADA = PREFIX + "013";
	
	/**
	 * Mensagem exibida quando se est� tentando cadastrar um componente curricular com erro na express�o de equival�ncias.<br/><br/>
	 *  
	 * Conte�do: "Express�o de equival�ncia mal formada."<br/>
	 * Tipo: ERROR
	 */
	public static final String EXPRESSAO_EQUIVALENCIA_MAL_FORMADA = PREFIX + "014";
	
	
	/**
	 * Mensagem exibida quando se est� tentando cadastrar um componente curricular com equival�ncia a um componente que j� pertence ao curr�culo do mesmo.<br/><br/>
	 *  
	 * Conte�do: "O componente %s n�o pode ser informado na equival�ncia porque ele faz parte de um curr�culo no qual %s tamb�m faz parte."<br/>
	 * Tipo: ERROR
	 */
	public static final String EQUIVALENTE_JA_PERTENCE_AO_CURRICULO = PREFIX + "015";
	
	/**
	 * Mensagem exibida quando se est� cadastrando uma sub turma a um componente sem cr�ditos de laborat�rio.<br/><br/>
	 *  
	 * Conte�do: "Apenas turmas que possuem carga hor�ria de laborat�rio permitem a cria��o de subturmas. Para que este componente permita a cria��o de subturmas � necess�rio que a carga hor�ria de laborat�rio seja maior do que zero."
	 * Tipo: ERROR
	 */
	public static final String SOMENTE_TURMA_COM_LABORATORIO_PODE_TER_SUBTURMA = PREFIX + "016";
	
	/**
	 * Mensagem exibida quando n�o se consegue cadastrar a sub-unidade ao novo componente.
	 *  
	 * Conte�do: "N�o foi poss�vel adicionar a sub-unidade. Provavelmente ela j� foi adicionada."
	 * Tipo: ERROR
	 */
	public static final String NAO_FOI_POSSIVEL_ADICIONAR_SUBUNIDADE = PREFIX + "017";
	
	/**
	 * Mensagem exibida quando n�o se consegue remover a sub-unidade do componente.
	 *  
	 * Conte�do: "N�o foi poss�vel remover a sub-unidade"
	 * Tipo: ERROR
	 */
	public static final String NAO_FOI_POSSIVEL_REMOVER_A_SUBUNIDADE = PREFIX + "018";
	
	/**
	 * Mensagem exibida para informar ao usu�rio que a opera��o n�o permite alterar informa��es que n�o sejam ementa ou refer�ncias.<br/><br/>
	 *  
	 * Conte�do: "Esta opera��o permite apenas que sejam alteradas a ementa e refer�ncias da disciplina. Se desejar alterar mais informa��es, entre em contato com a PPG."
	 * Tipo: WARNING
	 */
	public static final String OPERACAO_PERMITE_SOMENTE_ALTERAR_EMENTA_E_REFERENCIAS = PREFIX + "019";
	
	/**
	 * Mensagem exibida quando n�o se consegue remover a sub-unidade do componente.<br/><br/>
	 *  
	 * Conte�do: "N�o foi poss�vel carregar os requisitos dessa disciplina"<br/>
	 * Tipo: ERROR
	 */
	public static final String NAO_FOI_POSSIVEL_CARREGAR_REQUISITOS = PREFIX + "020";
	
	/**
	 * Mensagem exibida quando o curso n�o possui uma unidade de coordena��o.<br/><br/>
	 *  
	 * Conte�do: "� necess�rio que o curso %s tenha unidade de coordena��o.<br>Para resolver essa pend�ncia o CDP deve informar a unidade de coordena��o no cadastro de curso."<br/>
	 * Tipo: ERROR
	 */
	public static final String CURSO_SEM_UNIDADE_COORDENACAO = PREFIX + "021";

	/**
	 * Mensagem exibida quando discente se matricula numa carga hor�ria maior que a permitida por turno e por dia para uma turma de f�rias.<br/><br/>
	 *  
	 * Conte�do: "O n�mero de aulas, por disciplina, em um per�odo letivo especial de f�rias, n�o dever� exceder o limite de %d horas por turno e %d horas di�rias (Art. 243 da Resolu��o N� 227/2009-CONSEPE)."
	 * Tipo: ERROR
	 */
	public static final String CARGA_HORARIA_MAXIMA_TURMA_FERIAS = PREFIX + "022";
	
    /**
	 * Mensagem exibida quando discente se matricula numa carga hor�ria maior que a permitida por turno e por dia para uma turma de f�rias.<br/><br/>
	 *  
	 * Conte�do: "Cada aluno poder� obter matr�cula em apenas um componente curricular por per�odo letivo especial de f�rias. (Art. 246 da Resolu��o N� 227/2009-CONSEPE)."
	 * Tipo: ERROR
	 */
	public static final String NUMERO_MAXIMO_MATRICULA_TURMA_FERIAS = PREFIX + "023";
	
    /**
	 * Mensagem exibida quando uma tentativa de aproveitamento de componente curricular do tipo atividade � realizada.<br/><br/>
	 *  
	 * Conte�do: "N�o � poss�vel aproveitar estudos de componentes do tipo atividade."
	 * Tipo: ERROR
	 */
	public static final String NEGAR_APROVEITAMENTO_COMPONENTE_TIPO_ATIVIDADE = PREFIX + "025";
	
	
	/**
	 * Mensagem exibida ao notificar falta de docente junto a departamento sem chefe.<br/><br/>
	 *  
	 * Conte�do: "O Departamento ligado a est� disciplina n�o tem um chefe associado, apenas a PROGRAD receber� o aviso de falta."
	 * Tipo: WARNING
	 */
	public static final String AVISO_FALTA_DOCENTE_DEPARTAMENTO_SEM_CHEFE  = PREFIX + "026";
	
	
	/**
	 * Mensagem de erro exibida ao n�o conseguir carregar as matrizes curriculares de um curso.
	 *  
	 * Conte�do: N�o foi poss�vel carregar as matrizes curriculares do seu curso para efetuar esta opera��o. Por favor, entre em contato com o CDP.
	 * Tipo: ERRO
	 * 
	 */
	public static final String ERRO_AO_CARREGAR_MATRIZES_CURRICULARES  = PREFIX + "027";
	
	
	/**
	 * Mensagem de erro exibida quando um componente curricular n�o possui n�mero de unidades definidas.
	 *  
	 * Conte�do: Esse componente curricular n�o possui n�mero de unidades definida. Por favor, contate o DAE para regularizar a situa��o.
	 * Tipo: ERRO
	 * 
	 */
	public static final String COMPONENTE_SEM_NUMERO_UNIDADES_DEFINIDO  = PREFIX + "028";
	
	
	/**
	 * Mensagem de erro exibida quando o ano e per�odo de f�rias n�o esta definido no calend�rio acad�mico.
	 *  
	 * Conte�do: � necess�rio que o ano.per�odo de f�rias vigente seja definido no calend�rio acad�mico para que voc� possa realizar a matr�cula, favor entrar em contato com o DAE.
	 * Tipo: ERRO
	 * 
	 */
	public static final String ANO_PERIODO_FERIAS_NAO_DEFINIDO_CALENDARIO_ACADEMICO  = PREFIX + "029";
	
	/**
	 * Mensagem de erro exibida quando o ano e per�odo de f�rias n�o esta definido no calend�rio acad�mico.
	 *  
	 * Conte�do: Somente � permitida altera��o para a atividade do tipo selecionado com situa��o matriculada.
	 * Tipo: ERRO
	 * 
	 */
	public static final String PERMITIDO_ALTERAR_ATIVIDADE_SITUACAO_MATRICULADO  = PREFIX + "030";
	
	
	
	/**
	 * Mensagem de informa��o exibida 
	 *  
	 * Conte�do: Este texto ir� aparecer ao discente quando ele tentar efetuar a opera��o de 'Destrancar o Curso'. Ir� aparecer em
	 *  um div de descri��o de opera��o, portanto tem html no campo mensagem da tabela mensagem_aviso 
	 *  
	 *  '<h4>Regulamento dos Cursos Regulares de Gradua��o, Resolu��o N� 227/2009-CONSEPE, de 3 de dezembro de 2009
	 *						<br />SUBSE��O V - DA OBRIGATORIEDADE DE MATR�CULA
     *	</h4>
     *	<p> 
     *		Art. 202. Os alunos regularmente cadastrados em cursos de gradua��o que n�o efetivarem sua matr�cula em um determinado per�odo letivo regular ter�o o v�nculo automaticamente cancelado com a institui��o.
     *	</p>'
	 *  
	 * Tipo: Information
	 * 
	 */
	public static final String DESCRICAO_OPERACAO_DESTRANCAR_CURSO  = PREFIX + "031";
	
	
	/**
	 * Mensagem de informa��o exibida 
	 *  
	 * Conte�do: Este texto ir� aparecer ao discente quando ele tentar efetuar a opera��o de 'Solicitar Turma de F�rias'. Ir� aparecer em
	 *  um div de descri��o de opera��o, portanto tem html no campo mensagem da tabela mensagem_aviso 
	 *  
	 *  '<h4>Regulamento dos Cursos Regulares de Gradua��o, Resolu��o N� 227/2009-CONSEPE, de 3 de dezembro de 2009
					<br />SE��O V - DA OFERTA DE COMPONENTES CURRICULARES EM PER�ODOS LETIVOS ESPECIAIS DE F�RIAS</h4>
				 	<p>Art. 244. Somente os componentes curriculares com carga hor�ria total de at� 90 (noventa) 
				       horas poder�o ser oferecidos em per�odo letivo especial de f�rias.</p>
			        <p>Art. 245. A quantidade m�nima de alunos por turma em um componente curricular oferecido 
						no per�odo letivo especial de f�rias n�o deve ser inferior a cinco. </p>
				    <p>Art.  246.  Cada  aluno  poder�  obter  matr�cula  em  apenas  um  componente  curricular  por 
						per�odo letivo especial de f�rias. </p>
					<p>Art.  247.  N�o  ser�  permitido  ao  aluno  o  trancamento  de  matr�cula  em  per�odo  letivo 
						especial de f�rias.</p> 
					<p>Art.  248.  Encerradas  as  atividades,  a  consolida��o  da  turma  dever�  ser  realizada  pelo 
						professor respons�vel pela turma at� o fim do prazo fixado no Calend�rio Universit�rio.</p>
					<p>Art. 249. As disposi��es  relativas � oferta de componentes curriculares em per�odo  letivo 
						especial de f�rias aplicam-se aos m�dulos, blocos e atividades especiais coletivas.</p>'
	 *  
	 * Tipo: Information
	 * 
	 */
	public static final String DESCRICAO_OPERACAO_SOLICITAR_TURMA_FERIAS  = PREFIX + "032";
	
	
	/**
	 * Mensagem de informa��o exibida 
	 *  
	 * Conte�do: Este texto ir� aparecer ao discente quando ele tentar efetuar a opera��o de 'SOLICITA��O DE TURMAS DE ENSINO INDIVIDUALIZADO'. 
	 * Ir� aparecer em um div de descri��o de opera��o, portanto tem html no campo mensagem da tabela mensagem_aviso 
	 *  
	 *  '<h4>Regulamento dos Cursos Regulares de Gradua��o, Resolu��o N� 227/2009-CONSEPE, de 3 de dezembro de 2009
					<br />SE��O VII - DO ENSINO INDIVIDUAL EM COMPONENTES CURRICULARES</h4>
				<p>Art.  251. O  ensino  individual,  restrito  apenas  aos  componentes  curriculares  obrigat�rios, 
				permite  que  um  aluno  regular  da  UFRN,  nos  dois  �ltimos  per�odos  de  integraliza��o  do  seu 
				programa,  curse  individualmente,  por  per�odo  letivo,  no m�ximo,  dois  componente  curriculares, 
				quando atendidos aos seguintes requisitos:</p> 
				<br>I - o componente curricular pretendido ou equivalente n�o for oferecido no per�odo corrente 
				ou for oferecida de modo incompat�vel com o plano de matr�cula do aluno; 
				<br>II - o aluno n�o tiver reprova��o por falta no componente curricular pretendido; 
				<br>III  -  o  aluno  tiver,  no  m�ximo,  uma  reprova��o  por  m�dia  no  componente  curricular 
				pretendido; 
				<br>IV  -  o  aluno  tiver,  no  m�ximo,  um  trancamento  de matr�cula  no  componente  curricular 
				pretendido; 
				<br>V  -  o  componente  curricular  pretendido  envolver  procedimentos  de  ensino/aprendizagem 
				compat�veis com o ensino individual.'
	 *  
	 * Tipo: Information
	 * 
	 */
	public static final String DESCRICAO_OPERACAO_SOLICITAR_ENSINO_INDIVIDUALIZADO  = PREFIX + "033";
	
	/**
	 * Conte�do: A matriz curricular associada a esse discente possui apenas uma �nfase.
	 */
	public static final String MATRIZ_CURRICULAR_POSSUI_APENAS_UMA_ENFASE  = PREFIX + "034";
	
	/**
	 * Conte�do: A matriz curricular associada a esse discente n�o possui �nfase.
	 */
	public static final String MATRIZ_CURRICULAR_NAO_POSSUI_ENFASE  = PREFIX + "035";
	
	

}