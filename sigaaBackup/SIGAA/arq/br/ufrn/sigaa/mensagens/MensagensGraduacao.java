/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 24/11/2009
 *
 */

package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/** Interface para as constantes de mensagens referentes às operações na graduação e que serão exibidas ao usuário.
 * @author Édipo Elder F. Melo
 *
 */
public interface MensagensGraduacao {
	
	/** Prefixo para estas mensagens. Valor atual: "2_10500_". */
	public static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.GRADUACAO.getId() + "_";
	
	/**
	 * Mensagem exibida quando a busca por turmas retorna vazio.
	 *  
	 * Conteúdo: Não foram encontradas turmas no ano-período informado.
	 * Tipo: ERROR
	 */
	public static final String NENHUMA_TURMA_NO_ANO_PERIODO = PREFIX + "001";
	
	/**
	 * Mensagem exibida quando o discente não possui data de colação de grau devidamente registrada.
	 *  
	 * Conteúdo: O discente selecionado não possui data de colação de grau registrada.
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_SEM_DATA_DE_COLACAO = PREFIX + "002";
	
	/**
	 * Mensagem exibida quando o usuário tenta registrar o diploma de um discente com data de colação anterior à definida.<br>
	 * Parâmetros: ano, período.<br>
	 * Conteúdo: Não é permitido registrar diplomas de discentes com data de colação de grau anterior ao período %d.%d.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String REGISTRO_DIPLOMA_ANTERIOR_AO_PERIODO_INICIO = PREFIX + "003";
	
	/**
	 * Mensagem exibida quando o usuário tenta registrar o diploma de uma turma de colação de grau,
	 * onde todos os discentes desta turma já possuem registro de diplomas.<br>
	 * 
	 * Conteúdo: Não há discentes sem registro de diploma nesta Turma.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String TODOS_DISCENTES_DA_TURMA_FORAM_REGISTRADOS = PREFIX + "004";
	
	/**
	 * Mensagem exibida quando o usuário tenta registrar o diploma de uma turma de colação de grau,
	 * e não seleciona (marca o checkbox) quais discentes terão o diploma registrado.<br>
	 * 
	 * Conteúdo: Selecione pelo menos um discente para registrar o diploma.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String SELECIONE_DISCENTE_PARA_REGISTAR_DIPLOMA = PREFIX + "005";

	/**
	 * Mensagem exibida quando o usuário tenta incluir um curso para registro em um livro que já possui o curso.<br>
	 * Conteúdo: Curso já incluído na lista.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String CURSO_JA_INCLUIDO = PREFIX + "006";

	/**
	 * Mensagem exibida quando o usuário tenta registrar o diploma de um curso que não possui livro aberto correspondente para registro.<br>
	 * Parâmetros: curso.<br>
	 * Conteúdo: Não há livro aberto para registro do curso %s.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String NAO_HA_LIVRO_ABERTO_PARA_REGISTRO_CURSO = PREFIX + "007";

	/**
	 * Mensagem exibida quando o usuário tenta incluir um curso para registro em um livro, e este curso está sendo registrado em outro livro aberto.<br>
	 * Parâmetros: curso, livro.<br>
	 * Conteúdo: Não é possível registrar o curso em mais de um livro aberto. O curso %s é registrado no livro %s.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String CURSO_REGISTRADO_EM_OUTRO_LIVRO_ABERTO = PREFIX + "008";
	
	/**
	 * Mensagem exibida quando o componente possui carga horária maior que o permitido no período letivo de férias
	 */
	public static final String ERRO_VALIDACAO_CARGA_HORARIA_DISCIPLINA_FERIAS = PREFIX + "009";
	
	/**
	 * Mensagem exibida quando se está tentando inativar um componente que possui dependências.<br/><br/>
	 *  
	 * Conteúdo: "Não pode inativar porque existem disciplinas que possuem dependência: %s".<br/>
	 * Tipo: ERROR
	 */
	public static final String DISCIPLINAS_COM_PENDENCIA = PREFIX + "010";
	
	/**
	 * Mensagem exibida quando se está tentando cadastrar um componente curricular com mais créditos para ead que o permitido, de acordo com o tipo do componente.<br/><br/>
	 *  
	 * Conteúdo: "A carga horária para educação à distância não pode ultrapassar %s% da carga horária total, de acordo com o tipo de componente selecionado."<br/>
	 * Tipo: ERROR
	 */
	public static final String CH_EAD_ACIMA_DO_LIMITE = PREFIX + "011";
	
	/**
	 * Mensagem exibida quando se está tentando cadastrar um componente curricular com erro na expressão de pre-requisitos.<br/><br/>
	 *  
	 * Conteúdo: "Expressão de pré-requisito mal formada."<br/>
	 * Tipo: ERROR
	 */
	public static final String EXPRESSAO_PREREQUISITO_MAL_FORMADA = PREFIX + "012";
	
	/**
	 * Mensagem exibida quando se está tentando cadastrar um componente curricular com erro na expressão de co-requisitos.<br/><br/>
	 *  
	 * Conteúdo: "Expressão de co-requisito mal formada."<br/>
	 * Tipo: ERROR
	 */
	public static final String EXPRESSAO_COREQUISITO_MAL_FORMADA = PREFIX + "013";
	
	/**
	 * Mensagem exibida quando se está tentando cadastrar um componente curricular com erro na expressão de equivalências.<br/><br/>
	 *  
	 * Conteúdo: "Expressão de equivalência mal formada."<br/>
	 * Tipo: ERROR
	 */
	public static final String EXPRESSAO_EQUIVALENCIA_MAL_FORMADA = PREFIX + "014";
	
	
	/**
	 * Mensagem exibida quando se está tentando cadastrar um componente curricular com equivalência a um componente que já pertence ao currículo do mesmo.<br/><br/>
	 *  
	 * Conteúdo: "O componente %s não pode ser informado na equivalência porque ele faz parte de um currículo no qual %s também faz parte."<br/>
	 * Tipo: ERROR
	 */
	public static final String EQUIVALENTE_JA_PERTENCE_AO_CURRICULO = PREFIX + "015";
	
	/**
	 * Mensagem exibida quando se está cadastrando uma sub turma a um componente sem créditos de laboratório.<br/><br/>
	 *  
	 * Conteúdo: "Apenas turmas que possuem carga horária de laboratório permitem a criação de subturmas. Para que este componente permita a criação de subturmas é necessário que a carga horária de laboratório seja maior do que zero."
	 * Tipo: ERROR
	 */
	public static final String SOMENTE_TURMA_COM_LABORATORIO_PODE_TER_SUBTURMA = PREFIX + "016";
	
	/**
	 * Mensagem exibida quando não se consegue cadastrar a sub-unidade ao novo componente.
	 *  
	 * Conteúdo: "Não foi possível adicionar a sub-unidade. Provavelmente ela já foi adicionada."
	 * Tipo: ERROR
	 */
	public static final String NAO_FOI_POSSIVEL_ADICIONAR_SUBUNIDADE = PREFIX + "017";
	
	/**
	 * Mensagem exibida quando não se consegue remover a sub-unidade do componente.
	 *  
	 * Conteúdo: "Não foi possível remover a sub-unidade"
	 * Tipo: ERROR
	 */
	public static final String NAO_FOI_POSSIVEL_REMOVER_A_SUBUNIDADE = PREFIX + "018";
	
	/**
	 * Mensagem exibida para informar ao usuário que a operação não permite alterar informações que não sejam ementa ou referências.<br/><br/>
	 *  
	 * Conteúdo: "Esta operação permite apenas que sejam alteradas a ementa e referências da disciplina. Se desejar alterar mais informações, entre em contato com a PPG."
	 * Tipo: WARNING
	 */
	public static final String OPERACAO_PERMITE_SOMENTE_ALTERAR_EMENTA_E_REFERENCIAS = PREFIX + "019";
	
	/**
	 * Mensagem exibida quando não se consegue remover a sub-unidade do componente.<br/><br/>
	 *  
	 * Conteúdo: "Não foi possível carregar os requisitos dessa disciplina"<br/>
	 * Tipo: ERROR
	 */
	public static final String NAO_FOI_POSSIVEL_CARREGAR_REQUISITOS = PREFIX + "020";
	
	/**
	 * Mensagem exibida quando o curso não possui uma unidade de coordenação.<br/><br/>
	 *  
	 * Conteúdo: "É necessário que o curso %s tenha unidade de coordenação.<br>Para resolver essa pendência o CDP deve informar a unidade de coordenação no cadastro de curso."<br/>
	 * Tipo: ERROR
	 */
	public static final String CURSO_SEM_UNIDADE_COORDENACAO = PREFIX + "021";

	/**
	 * Mensagem exibida quando discente se matricula numa carga horária maior que a permitida por turno e por dia para uma turma de férias.<br/><br/>
	 *  
	 * Conteúdo: "O número de aulas, por disciplina, em um período letivo especial de férias, não deverá exceder o limite de %d horas por turno e %d horas diárias (Art. 243 da Resolução Nº 227/2009-CONSEPE)."
	 * Tipo: ERROR
	 */
	public static final String CARGA_HORARIA_MAXIMA_TURMA_FERIAS = PREFIX + "022";
	
    /**
	 * Mensagem exibida quando discente se matricula numa carga horária maior que a permitida por turno e por dia para uma turma de férias.<br/><br/>
	 *  
	 * Conteúdo: "Cada aluno poderá obter matrícula em apenas um componente curricular por período letivo especial de férias. (Art. 246 da Resolução Nº 227/2009-CONSEPE)."
	 * Tipo: ERROR
	 */
	public static final String NUMERO_MAXIMO_MATRICULA_TURMA_FERIAS = PREFIX + "023";
	
    /**
	 * Mensagem exibida quando uma tentativa de aproveitamento de componente curricular do tipo atividade é realizada.<br/><br/>
	 *  
	 * Conteúdo: "Não é possível aproveitar estudos de componentes do tipo atividade."
	 * Tipo: ERROR
	 */
	public static final String NEGAR_APROVEITAMENTO_COMPONENTE_TIPO_ATIVIDADE = PREFIX + "025";
	
	
	/**
	 * Mensagem exibida ao notificar falta de docente junto a departamento sem chefe.<br/><br/>
	 *  
	 * Conteúdo: "O Departamento ligado a está disciplina não tem um chefe associado, apenas a PROGRAD receberá o aviso de falta."
	 * Tipo: WARNING
	 */
	public static final String AVISO_FALTA_DOCENTE_DEPARTAMENTO_SEM_CHEFE  = PREFIX + "026";
	
	
	/**
	 * Mensagem de erro exibida ao não conseguir carregar as matrizes curriculares de um curso.
	 *  
	 * Conteúdo: Não foi possível carregar as matrizes curriculares do seu curso para efetuar esta operação. Por favor, entre em contato com o CDP.
	 * Tipo: ERRO
	 * 
	 */
	public static final String ERRO_AO_CARREGAR_MATRIZES_CURRICULARES  = PREFIX + "027";
	
	
	/**
	 * Mensagem de erro exibida quando um componente curricular não possui número de unidades definidas.
	 *  
	 * Conteúdo: Esse componente curricular não possui número de unidades definida. Por favor, contate o DAE para regularizar a situação.
	 * Tipo: ERRO
	 * 
	 */
	public static final String COMPONENTE_SEM_NUMERO_UNIDADES_DEFINIDO  = PREFIX + "028";
	
	
	/**
	 * Mensagem de erro exibida quando o ano e período de férias não esta definido no calendário acadêmico.
	 *  
	 * Conteúdo: É necessário que o ano.período de férias vigente seja definido no calendário acadêmico para que você possa realizar a matrícula, favor entrar em contato com o DAE.
	 * Tipo: ERRO
	 * 
	 */
	public static final String ANO_PERIODO_FERIAS_NAO_DEFINIDO_CALENDARIO_ACADEMICO  = PREFIX + "029";
	
	/**
	 * Mensagem de erro exibida quando o ano e período de férias não esta definido no calendário acadêmico.
	 *  
	 * Conteúdo: Somente é permitida alteração para a atividade do tipo selecionado com situação matriculada.
	 * Tipo: ERRO
	 * 
	 */
	public static final String PERMITIDO_ALTERAR_ATIVIDADE_SITUACAO_MATRICULADO  = PREFIX + "030";
	
	
	
	/**
	 * Mensagem de informação exibida 
	 *  
	 * Conteúdo: Este texto irá aparecer ao discente quando ele tentar efetuar a operação de 'Destrancar o Curso'. Irá aparecer em
	 *  um div de descrição de operação, portanto tem html no campo mensagem da tabela mensagem_aviso 
	 *  
	 *  '<h4>Regulamento dos Cursos Regulares de Graduação, Resolução N° 227/2009-CONSEPE, de 3 de dezembro de 2009
	 *						<br />SUBSEÇÃO V - DA OBRIGATORIEDADE DE MATRÍCULA
     *	</h4>
     *	<p> 
     *		Art. 202. Os alunos regularmente cadastrados em cursos de graduação que não efetivarem sua matrícula em um determinado período letivo regular terão o vínculo automaticamente cancelado com a instituição.
     *	</p>'
	 *  
	 * Tipo: Information
	 * 
	 */
	public static final String DESCRICAO_OPERACAO_DESTRANCAR_CURSO  = PREFIX + "031";
	
	
	/**
	 * Mensagem de informação exibida 
	 *  
	 * Conteúdo: Este texto irá aparecer ao discente quando ele tentar efetuar a operação de 'Solicitar Turma de Férias'. Irá aparecer em
	 *  um div de descrição de operação, portanto tem html no campo mensagem da tabela mensagem_aviso 
	 *  
	 *  '<h4>Regulamento dos Cursos Regulares de Graduação, Resolução N° 227/2009-CONSEPE, de 3 de dezembro de 2009
					<br />SEÇÃO V - DA OFERTA DE COMPONENTES CURRICULARES EM PERÍODOS LETIVOS ESPECIAIS DE FÉRIAS</h4>
				 	<p>Art. 244. Somente os componentes curriculares com carga horária total de até 90 (noventa) 
				       horas poderão ser oferecidos em período letivo especial de férias.</p>
			        <p>Art. 245. A quantidade mínima de alunos por turma em um componente curricular oferecido 
						no período letivo especial de férias não deve ser inferior a cinco. </p>
				    <p>Art.  246.  Cada  aluno  poderá  obter  matrícula  em  apenas  um  componente  curricular  por 
						período letivo especial de férias. </p>
					<p>Art.  247.  Não  será  permitido  ao  aluno  o  trancamento  de  matrícula  em  período  letivo 
						especial de férias.</p> 
					<p>Art.  248.  Encerradas  as  atividades,  a  consolidação  da  turma  deverá  ser  realizada  pelo 
						professor responsável pela turma até o fim do prazo fixado no Calendário Universitário.</p>
					<p>Art. 249. As disposições  relativas à oferta de componentes curriculares em período  letivo 
						especial de férias aplicam-se aos módulos, blocos e atividades especiais coletivas.</p>'
	 *  
	 * Tipo: Information
	 * 
	 */
	public static final String DESCRICAO_OPERACAO_SOLICITAR_TURMA_FERIAS  = PREFIX + "032";
	
	
	/**
	 * Mensagem de informação exibida 
	 *  
	 * Conteúdo: Este texto irá aparecer ao discente quando ele tentar efetuar a operação de 'SOLICITAÇÃO DE TURMAS DE ENSINO INDIVIDUALIZADO'. 
	 * Irá aparecer em um div de descrição de operação, portanto tem html no campo mensagem da tabela mensagem_aviso 
	 *  
	 *  '<h4>Regulamento dos Cursos Regulares de Graduação, Resolução N° 227/2009-CONSEPE, de 3 de dezembro de 2009
					<br />SEÇÃO VII - DO ENSINO INDIVIDUAL EM COMPONENTES CURRICULARES</h4>
				<p>Art.  251. O  ensino  individual,  restrito  apenas  aos  componentes  curriculares  obrigatórios, 
				permite  que  um  aluno  regular  da  UFRN,  nos  dois  últimos  períodos  de  integralização  do  seu 
				programa,  curse  individualmente,  por  período  letivo,  no máximo,  dois  componente  curriculares, 
				quando atendidos aos seguintes requisitos:</p> 
				<br>I - o componente curricular pretendido ou equivalente não for oferecido no período corrente 
				ou for oferecida de modo incompatível com o plano de matrícula do aluno; 
				<br>II - o aluno não tiver reprovação por falta no componente curricular pretendido; 
				<br>III  -  o  aluno  tiver,  no  máximo,  uma  reprovação  por  média  no  componente  curricular 
				pretendido; 
				<br>IV  -  o  aluno  tiver,  no  máximo,  um  trancamento  de matrícula  no  componente  curricular 
				pretendido; 
				<br>V  -  o  componente  curricular  pretendido  envolver  procedimentos  de  ensino/aprendizagem 
				compatíveis com o ensino individual.'
	 *  
	 * Tipo: Information
	 * 
	 */
	public static final String DESCRICAO_OPERACAO_SOLICITAR_ENSINO_INDIVIDUALIZADO  = PREFIX + "033";
	
	/**
	 * Conteúdo: A matriz curricular associada a esse discente possui apenas uma ênfase.
	 */
	public static final String MATRIZ_CURRICULAR_POSSUI_APENAS_UMA_ENFASE  = PREFIX + "034";
	
	/**
	 * Conteúdo: A matriz curricular associada a esse discente não possui ênfase.
	 */
	public static final String MATRIZ_CURRICULAR_NAO_POSSUI_ENFASE  = PREFIX + "035";
	
	

}