/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/07/2006
 */
package br.ufrn.arq.dominio;

import br.ufrn.arq.seguranca.log.SessionLogger;

/**
 * Constantes gerais de parâmetros utilizados pela arquitetura.
 * 
 * @author Gleydson Lima
 *
 */
public class ConstantesParametroGeral {

	 /** Indica se mensagens devem ser enviadas quando uma exceção for disparada */
    public static final String EMAIL_DE_ALERTA = "3_1_15";

    /** Email do administrador do sistema para envio de alertas */
    public static final String EMAIL_ALERTAS_ADMISTRADOR = "3_1_2";

    /** Email do administrador do sistema para envio de alertas */
    public static final String EMAIL_ALERTA_ERRO = "3_1_3";

    /** Caminho para recursos no NFS */
	public static final String CAMINHO_RECURSOS = "3_1_4";

	/** Indica se a instituição está usando LDAP para sincronização dos usuários. */
	public static final String LDAP_ATIVO = "3_1_9";
	
	/**Enviar e-mail quando ocorre alguma exceção no sipac*/ 
	 public static final String EXCECAO_SIPAC = "1_1_1";
	 
	/**Enviar e-mail quando ocorre alguma exceção no sigaa*/ 
	 public static final String EXCECAO_SIGAA = "2_1_1";

	/**Enviar e-mail quando ocorre alguma exceção na arquitetura*/ 
	 public static final String EXCECAO_ARQ = "3_1_15";

	/**Enviar e-mail quando ocorre alguma exceção no SCO*/ 
	 public static final String EXCECAO_SCO = "4_1_1";

	/**Enviar e-mail quando ocorre alguma exceção no sistema de protocolo*/ 
	 public static final String EXCECAO_PROTOCOLO = "5_1_1";

	/**Enviar e-mail quando ocorre alguma exceção no iProject*/ 
	 public static final String EXCECAO_IPROJECT = "6_1_1";

	/**Enviar e-mail quando ocorre alguma exceção no sigrh*/ 
	 public static final String EXCECAO_SIGRH = "7_1_1";


	/**Enviar e-mail quando ocorre alguma exceção no sigadmin*/ 
	 public static final String EXCECAO_SIGADMIN = "8_1_1";

	 
	/**
	 * Identifica o remetente padrão de uma notificação gerada pelo SIGAdmin.
	 */
	public static final String REMETENTE_PADRAO_NOTIFICACAO = "8_1_2";
	 
	 
    /**
	 * Id da pessoa do gestor da instituição (reitor UFRN) na tabela pessoa.
	 */
	public static final String GESTOR_INSTITUICAO = "3_1_10";
	
	
	/**
	 * Id da pessoa do superintendente da fundação na tabela pessoa.
	 */
	public static final String SUPERINTENDENTE_FUNDACAO = "3_1_11";

	
	/** 
	 * Indica o limite máximo de dias que um servidor pode ficar sem ler um memorando eletrônico recebido.
	 * Após este limite o memorando eletrônico deve receber uma identificação e o usuário deve ser informado para que leia o mais rápido possível.
	 */
	public static final String LIMITE_DIAS_LEITURA_MEMORANDO_ELETRONICO = "3_1_12";
	
	/**
	 * Indica se um aluno é prioritário de acordo com o cadastro único
	 */
	public static final String ALUNO_PRIORITARIO = "3_1_13";

	/**
	 * Endereço de reply-to para mensagens enviadas pelo SIPAC.
	 */
	public static final String REPLYTO_SIPAC = "3_1_14";

	/**
	 * Estratégia de autenticação para os sistemas
	 */
	public static final String ESTRATEGIA_AUTENTICACAO = "3_1_16";

	/**
	 * Prazo para a autorização de projetos de pesquisa, monitoria e extensão. Se não for
	 * autorizado dentro do prazo, as requisições ficam bloqueadas na unidade.
	 */
	public static final String PRAZO_AUTORIZACAO_PROJETOS = "3_1_17";

	/** Tempo necessário para a expiração de um passaporte de logon */
	public static final String TEMPO_EXPIRACAO_PASSAPORTE = "3_1_18";
	
	/** Unidade de direito global */
	public static final String UNIDADE_DIREITO_GLOBAL = "1_0_1";
	
	/** Código da instituição */
	public static final String CODIGO_INSTITUICAO = "1_0_14";
	
	/** Endereço do host SMTP para envio de e-mails */
	public static final String EMAIL_AUTOMATICO_HOST = "3_1_20";
	
	/** Usuário de autenticação no host SMTP */
	public static final String EMAIL_AUTOMATICO_HOST_USUARIO = "3_1_21";
	
	/** Senha do usuário de autenticação no host SMTP */
	public static final String EMAIL_AUTOMATICO_HOST_SENHA = "3_1_22";
	
	/** Endereço de e-mail do FROM */
	public static final String EMAIL_AUTOMATICO_REMETENTE_ENDERECO = "3_1_23";
	
	/** Nome do endereço de e-mail do FROM */
	public static final String EMAIL_AUTOMATICO_REMETENTE_NOME = "3_1_24";
	
	/** Endereço de e-mail do REPLY TO */
	public static final String EMAIL_AUTOMATICO_RESPONDER_PARA_ENDERECO = "3_1_25";
	
	/** Nome do endereço de e-mail do REPLY TO */
	public static final String EMAIL_AUTOMATICO_RESPONDER_PARA_NOME = "3_1_26";
	
	/** Tamanho máximo (em bytes) de um arquivo anexado a um chamado */
	public static final String TAMANHO_MAX_ARQUIVO_CHAMADO = "3_1_27";
	
	/** Número máximo de requisições concorrentes por usuário */
	public static final String NUMERO_MAXIMO_REQUISICOES_CONCORRENTES = "3_1_28";

	/** Utilizada para definição da unidade acadêmica de alunos especiais de graduação */
	public static final String UNIDADE_ACADEMICA_ALUNO_ESPECIAL = "3_1_29";
	
	/** Parâmetro que guarda usuário e senha para o web service de integração com o SIGEO (MJ) */
	public static final String CREDENCIAIS_AUTENTICACAO_INTEGRACAO_SIGEO = "3_1_30";

	/** Host para recebimento de e-mails pela arquitetura */
	public static final String HOST_RECEBIMENTO_EMAIL = "3_1_31";
	
	/** Usuário para recebimento de e-mails pela arquitetura */
	public static final String USERNAME_RECEBIMENTO_EMAIL = "3_1_32";
	
	/** Senha para recebimento de e-mails pela arquitetura */
	public static final String PASSWORD_RECEBIMENTO_EMAIL = "3_1_33";

	/** Parâmetro que armazena a escola de música */
	public static final String ESCOLA_MUSICA = "3_1_34";
	
	/** Permitir a criação de usuários independente do tipo de unidade(gestora/fato) **/
	public static final String PERMITE_CRIAR_USUARIO_UNIDADE_GESTORA = "1_12000_5";
	
	/** Unidade a qual o usuário de cooperação será vinculado. **/
	public static final String UNIDADE_USUARIO_COOPERACAO = "1_12000_6";
	
	/** Quantidade de dias em que a senha dos usuário devem expirar após a alteração */
	public static final String EXPIRA_SENHA = "1_0_2";
	
	/** Indica a quantidade mínima de memorandos exibidos no painel de memorandos. */
	public static final String QUANTIDADE_MINIMA_PAINEL_MEMORANDOS = "3_1_35";
	
	/** Define se é Permitido consultar um servidor em qualquer busca com qualquer parte do nome. */ 
	public static final String PERMITE_CONSULTA_SERVIDOR_QUALQUER_PARTE_NOME = "7_100500_1"; //Servidor (Funcional)
	
	/** Indica se o sistema realiza a geração de matrícula para servidores internamente 
	 * (além de utilizar a matrícula SIAPE) e permite utlizar/exibir esta matrícula em consultas, relatórios, etc. */
	public static final String UTILIZA_CONCEITO_MATRICULA_INTERNA = "7_100500_3"; //Servidor (Funcional)

	/** Unidade pagadora de bolsas de extensão. */
	public static final String UNIDADE_BOLSAS_EXTENSAO = "2_10900_6";

	/** Unidade pagadora de bolsas de monitoria.*/
	public static final String UNIDADE_BOLSAS_MONITORIA = "2_11000_8";

	/** Unidade da Secretaria de Educação a Distância.*/
	public static final String UNIDADE_SEDIS = "2_11200_1";

	/** Unidade da Seção de Atendimento do DAP.*/
	public static final String SECAO_ATENDIMENTO_DAP = "7_100100_125";

	/** Unidade do Departamento de Administração Pessoal.*/
	public static final String DEPARTAMENTO_ADMINISTRACAO_PESSOAL = "7_100100_124";

	/** Indica a unidade responsável por realizar os treinamentos dos servidores da instituição em cursos de capacitação profissional.*/
	public static final String ID_UNIDADE_DIVISAO_DE_TREINAMENTO_E_DESENVOLVIMENTO_DE_RH = "7_100200_1";

	/** Unidade do Centro de Ensino Superior do Seridó.*/
	public static final String UNIDADE_CERES = "2_13000_1";

	/** Se a base de log será replicada ou não (booleano) */
	public static final String REPLICAR_BASE_LOGS = "3_1_38";

	/** Parâmetro contendo o caminho para o arquivo de log da sessão do Hibernate */
	public static final String CAMINHO_LOG_SESSAO = "3_1_39";

	/** Tempo máximo que uma sessão fica aberta antes de ser logada pelo {@link SessionLogger} */
	public static final String TEMPO_MAXIMO_SESSAO_ABERTA = "3_1_40";
	
	/** Os valores possíveis são: ENTRADA, MOSTRAR_NAO_LIDAS. No caso do parametro ser mostrar lidas o sistema não 
	 * deve redirecionar para a caixa postal e ir direto para o menu mostrando o total de mensagens não lidas ao 
	 * lado do ícone da caixa postal no cabeçalho. */
	public static final String MODO_OPERACAO_CAIXA_POSTAL = "3_1_41";
	
	/**
	 * Indica se a unidade do usuário que será utilizada é a de lotação/função ou a unidade já definida.
	 * Neste caso, se o parâmetro tiver true, o sistema define a unidade do usuário para a unidade de lotação ou da função. A função tem prioridade sobre a lotação.
	 */
	public static final String UTILIZA_UNIDADE_LOTACAO_COMO_UNIDADE_USUARIO  = "3_1_42";

	/**
	 * Endereço de e-mail padrão para resposta dos e-mails enviados pelos sistemas.
	 */
	public static final String DEFAULT_REPLY_TO = "3_1_43";

	
	/**
	 * Este paramêtro mapeia os IDs das atividades (rh.atividade) que representam a atividade de chefe de departamento.
	 */
	public static final String ATIVIDADES_CHEFE_DEPARTAMENTO = "3_1_44";
	
	/**
	 * Este paramêtro mapeia os IDs das atividades (rh.atividade) que representam a atividade de diretor de centro.
	 */
	public static final String ATIVIDADES_DIRETOR_CENTRO = "3_1_45";
	
	/**
	 * Este paramêtro mapeia os IDs das atividades (rh.atividade) que representam a atividade de vice-diretor de centro.
	 */
	public static final String ATIVIDADES_VICE_DIRETOR_CENTRO = "3_1_46";
	
	/**
	 * Este paramêtro mapeia os IDs das atividades (rh.atividade) que representam a atividade de diretor de unidade.
	 */
	public static final String ATIVIDADES_DIRETOR_UNIDADE = "3_1_47";
	
	/**
	 * Este paramêtro mapeia os IDs das atividades (rh.atividade) que representam a atividade de diretor de museu.
	 */
	public static final String ATIVIDADES_DIRETOR_MUSEU = "3_1_48";
	
	/**
	 * Forma de exibição das unidades na árvore de unidades.
	 * 'S', 'N' -> SIGLA OU NOME COMPLETO.
	 */
	public static final String ARVORE_UNIDADES_FORMA_EXIBICAO = "3_1_49";

	/**
	 * Salt utilizado para fazer o hash das senhas dos usuários. Para não
	 * usar salt, deixar o parâmetro null.
	 */
	public static final String SALT_SENHAS_USUARIOS = "3_1_50";

	/**
	 * Tamanho máximo da imagem que pode ser enviada através do editor TinyMCE.
	 */
	public static final String TAMANHO_MAXIMO_UPLOAD_IMAGEM_TINYMCE = "3_1_51";
	
	
	/**
	 * id da UF (Unidade Federativa, tabela: comum.unidade_federativa padrão a ser utilizado nos sistemas, nos formulários de cadastro de dados pessoais e outros.. 
	 */
	public static final String UF_PADRAO = "3_1_52";
	
	/**
	 * Id do Município (tabela comum.municipio) padrão a ser utilizado nos sistemas, nos formulários de cadastro de dados pessoais e outros.. 
	 */
	public static final String MUNICIPIO_PADRAO = "3_1_53";
	
	/**
	 * CEP padrão a ser utilizado nos sistemas, nos formulários de cadastro de dados pessoais e outros.. 
	 */
	public static final String CEP_PADRAO = "3_1_54";
	
	/**
	 * DDD padrão a ser utilizado nos sistemas, nos formulários de cadastro de dados pessoais e outros.. 
	 */
	public static final String DDD_PADRAO = "3_1_55";
	
	/**
	 * usuário utilizado para operações automáticas e rotinas de migração
	 * onde eh necessário registrar o usuário que realizou a operação.
	 */
	public static final String USUARIO_SISTEMA = "3_1_56";
	
	/**
	 * usuário utilizado para operações realizadas dentro de um timer
	 */
	public static final String TIMER_SISTEMA = "3_1_57";

	/**
	 * Indica se o envio de e-mails pela arquitetura deve usar SSL.
	 */
	public static final String EMAIL_AUTOMATICO_USAR_SSL = "3_1_58";

	/**
	 * Indica a porta do servidor SMTP para envio de e-mails.
	 */
	public static final String EMAIL_AUTOMATICO_PORTA_SMTP = "3_1_59";
	
	/** Parâmetro contendo todas as configurações necessárias para o envio de e-mails pelos sistemas. */
	public static final String CONFIGURACOES_ENVIO_EMAIL = "3_1_60";

	/**
	 * Identifica se deve ser registrado no banco de dados o nome do método de um DAO
	 * que está realizando a interação com o banco.
	 */
	public static final String REGISTRAR_METODO_INVOCADOR_SESSAO = "3_1_61";

	/** Duração dos caches do componente de árvore de unidades, em minutos. */
	public static final String ARVORE_UNIDADES_DURACAO_CACHE = "3_1_62";
	
	
	/** Diretório que contém os arquivos com registro de entrada dos usuários que estão online. */
	public static final String DIRETORIO_USUARIOS_ONLINE = "3_1_63";
	
	
}
