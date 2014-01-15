/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/11/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do M�dulo de Extens�o que ser�o exibidas para o usu�rio.
 * 
 * @author Daniel Augusto
 *
 */
public interface MensagensExtensao {
	
	/**
	 * Prefixo para estas mensagens.
	 */
	static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.EXTENSAO.getId() + "_";
	
	// Mensagens de notifica��es de inscri��o pelo portal p�blico
	
	/**
	 * Mensagem exibida quando usu�rio submeter o formul�rio de inscri��o para cursos e eventos.
	 *  
	 * Conte�do: Inscri��o submetida com sucesso! Voc� receber� um email contendo as informa��es de como proceder.
	 * Tipo: INFORMATION
	 */
	public static final String INSCRICAO_EFETUADA_COM_SUCESSO = prefix + "01";
	
	/**
	 * Mensagem exibida para o usu�rio ao tentar acessar uma URL inv�lida para confirma��o da inscri��o.
	 *  
	 * Conte�do: Endere�o inv�lido! Verifique o email enviado ap�s a inscri��o e acesse o endere�o informado.
	 * Tipo: ERROR
	 */
	public static final String URL_CONFIRMACAO_INSCRICAO_INVALIDA = prefix + "02";
	
	/**
	 * Mensagem exibida quando usu�rio tentar confirmar uma inscri��o em uma a��o, caso este possua
	 * uma outra inscri��o mais recente confirmada para a mesma a��o.
	 *  
	 * Conte�do: Voc� j� possui uma inscri��o confirmada para esta a��o. Para realizar uma nova inscri��o � necess�rio 
	 * cancelar sua inscri��o anterior. Acesse a �rea do inscrito pelo portal p�blico do SIGAA e realize o cancelamento.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_ATIVA_RECENTE_EXISTENTE = prefix + "03";
	
	/**
	 * Mensagem exibida para o usu�rio ao tentar realizar o login na �rea de inscritos, 
	 * e sua inscri��o estiver cancelada.
	 *  
	 * Conte�do: Sua inscri��o foi cancelada. O acesso a est� �rea foi suspenso.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_CANCELADA_ACESSO_SUSPENSO = prefix + "04";
	
	/**
	 * Mensagem exibida para o usu�rio ao tentar realizar o login na �rea de inscritos,
	 * e sua inscri��o n�o tiver sido confirmada.
	 *  
	 * Conte�do: Sua inscri��o n�o foi confirmada. Acesse seu email para realizar a confirma��o.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_NAO_CONFIRMADA_ACESSO_SUSPENSO = prefix + "05";
	
	/**
	 * Mensagem exibida para o usu�rio ao tentar realizar o login na �rea de inscritos,
	 * e sua inscri��o encontra-se recusada.
	 *  
	 * Conte�do: Sua inscri��o foi recusada pelo coordenador da a��o. O acesso a essa �rea foi suspenso.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_RECUSADA_ACESSO_SUSPENSO = prefix + "06";
	
	/**
	 * Mensagem exibida para o usu�rio ap�s realizar o login na �rea de inscritos,
	 * e sua inscri��o encontra-se pendente da aprova��o do coordenador da a��o.
	 *  
	 * Conte�do: Sua inscri��o est� aguardando a aprova��o do coordenador da a��o.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_PENDENTE_APROVACAO_COORDENDADOR = prefix + "07";
	
	/**
	 * Mensagem exibida para o usu�rio na �rea de inscritos caso tente cancelar sua inscri��o
	 * que j� foi aprovada.
	 *  
	 * Conte�do: N�o foi poss�vel cancelar a inscri��o. Ela j� foi aceita pelo coordenador da a��o.
	 * Tipo: ERROR
	 */
	public static final String NAO_EH_PERMITIDO_CANCELAR_INSCRICAO_ACEITA = prefix + "08";
	
	/**
	 * Mensagem exibida para o usu�rio na �rea de inscritos ao tentar imprimir o certificado de participa��o
	 * antes do per�odo final da a��o ou n�o possua frequ�ncia necess�ria.
	 *  
	 * Conte�do: N�o foi poss�vel imprimir o certificado. Verifique o per�odo do t�rmino da a��o ou 
	 * entre em contato com o coordenador da a��o.
	 * Tipo: ERROR
	 */
	public static final String NAO_EH_POSSIVEL_IMPRIMIR_CERTIFICADO_PENDENTE_ACAO = prefix + "09";
	
	/**
	 * Mensagem exibida para o usu�rio na �rea de inscritos ao tentar imprimir o certificado de participa��o
	 * caso sua inscri��o n�o esteja aprovada.
	 *  
	 * Conte�do: Sua participa��o est� pendente de aprova��o. Aguarde pela confirma��o do coordenador.
	 * Tipo: WARNING
	 */
	public static final String NAO_EH_POSSIVEL_IMPRIMIR_CERTIFICADO_POR_NAO_PARTICIPANTE = prefix + "10";
	
	/**
	 * Mensagem exibida para o usu�rio na �rea de inscritos ao cancelar sua inscri��o.
	 *  
	 * Conte�do: Sua Inscri��o foi cancelada com sucesso!
	 * Tipo: INFORMATION
	 */
	public static final String INSCRICAO_CANCELADA_SUCESSO = prefix + "11";
	
	/**
	 * Mensagem exibida para o usu�rio ao informar a senha recebida por email na tela de confirma��o da inscri��o.
	 *  
	 * Conte�do: Sua Inscri��o foi confirmada com sucesso!
	 * Tipo: INFORMATION
	 */
	public static final String INSCRICAO_CONFIRMADA_SUCESSO = prefix + "12";
	
	/**
	 * Mensagem exibida para o usu�rio ao informar a senha recebida por email na tela de confirma��o da inscri��o, e este
	 * possua uma outra inscri��o confirmada anteriormente.
	 *  
	 * Conte�do: Voc� possui uma inscri��o anterior confirmada que foi cancelada. Utilize a inscri��o que foi confirmada 
	 * para participar do processo de sele��o.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_RECENTE_CANCELADA = prefix + "13";
	
	/**
	 * Mensagem exibida para o usu�rio ao se inscrever na mesma a��o pela �rea p�blica ou ao tentar confirmar uma inscri��o, 
	 * caso ele j� seja participante na a��o.
	 *  
	 * Conte�do: Voc� possui uma inscri��o aceita pelo coordenador da a��o. Acesse a �rea de inscritos pelo portal do SIGAA 
	 * com a senha enviada para o seu e-mail e consulte os dados referente a a��o de extens�o da qual voc� est� participando.
	 * Tipo: WARNING
	 */
	public static final String INSCRITO_EH_PARTICIPANTE_ACAO = prefix + "14";
	
	/**
	 * Mensagem exibida ao usu�rio quando este selecionar a inscri��o para um curso ou evento que possua o n�mero de vagas 
	 * esgotado.
	 *  
	 * Conte�do: Essa a��o possui o n�mero de vagas esgotado, por�m, ainda est� aberta para inscri��es. Sua aceita��o como 
	 * participante estar� dependente do aumento no n�mero de vagas. Ap�s realizada a inscri��o, consulte seu email para 
	 * maiores informa��es.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_NUMERO_VAGAS_ESGOTADO = prefix + "15";
	
	/**
	 * Mensagem exibida para o usu�rio ao tentar realizar o login na �rea do inscrito com email ou senha incorretos e/ou em 
	 * branco.
	 *  
	 * Conte�do: E-mail e/ou Senha inv�lidos.
	 * Tipo: ERROR
	 */
	public static final String EMAIL_SENHA_INVALIDOS = prefix + "16";
	
	/**
	 * Mensagem exibida ao usu�rio durante as etapas do processo de inscri��o, caso este tenha sido inv�lidado.
	 *  
	 * Conte�do: Opera��o Inv�lida! Reinicie o processo de inscri��o.
	 * Tipo: ERROR
	 */
	public static final String OPERACAO_INVALIDA = prefix + "17";
	
	/**
	 * Mensagem exibida para o usu�rio ap�s informar seu CPF no formul�rio de inscri��o, caso seus dados n�o existam na 
	 * base de dados.
	 *  
	 * Conte�do: Seu CPF n�o est� cadastrado no sistema. Preencha o formul�rio contendo seus dados para concluir sua inscri��o.
	 * Tipo: WARNING
	 */
	public static final String CPF_NAO_CADASTRADO = prefix + "18";
	
	/**
	 * Mensagem exibida para o usu�rio ao informar sua senha na tela de confirma��o de inscri��o.
	 *  
	 * Conte�do: Senha incorreta ou inscri��o j� confirmada.
	 * Tipo: ERROR
	 */
	public static final String ERRO_CONFIRMACAO_INSCRICAO = prefix + "19";
	
	/**
	 * Mensagem exibida para o coordenador da a��o na tela de cria��o de inscri��es, caso exista uma inscri��o j� aberta (ativa)
	 * para a mesma a��o.
	 *  
	 * Conte�do: Essa a��o possui uma inscri��o (%s) com per�odo aberto de %s � %s.
	 * Tipo: WARNING
	 */
	public static final String PERIODO_INSCRICAO_ATIVO = prefix + "20";
	
	/**
	 * Mensagem exibida para o coordenador da a��o na tela de suspens�o do per�odo de inscri��o, caso existam membros inscritos 
	 * para nesse per�odo.
	 *  
	 * Conte�do: Est� inscri��o possui membros inscritos aguardando pelo processo de sele��o para participante. Caso confirme a 
	 * suspens�o do per�odo de inscri��o, todos os inscritos ter�o suas inscri��es canceladas.
	 * Tipo: WARNING
	 */
	public static final String REMOCAO_INSCRICAO_MEMBROS_INSCRITOS = prefix + "21";
	
	/**
	 * Mensagem exibida para o coordenador da a��o ao realizar a opera��o de suspens�o do per�odo de inscri��o, sem informar os
	 * motivos para da suspens�o do per�odo de inscri��o.
	 *  
	 * Conte�do: Informe o(s) motivo(s) da suspens�o do per�odo de inscri��o para que os inscritos possam ser notificados por 
	 * e-mail.
	 * Tipo: ERROR
	 */
	public static final String INFORMAR_MOTIVOS_SUSPENSAO_PERIODO = prefix + "22";
	
	/**
	 * Mensagem exibida para o usu�rio ao logar na �rea de inscritos e cuja a inscri��o tenha sido aceita pelo coordenador da a��o.
	 *  
	 * Conte�do: Voc� est� atualmente como participante nessa a��o de extens�o.
	 * Tipo: INFORMATION
	 */
	public static final String ACEITA_INSCRICAO_PARA_PARTICIPANTE = prefix + "23";
	
	/**
	 * Mensagem exibida para o usu�rio para informar que uma a��o deve ter um coordenador.
	 *  
	 * Conte�do: Deve haver pelo menos um(a) Coordenador(a) da a��o.
	 * Tipo: ERRO
	 */
	public static final String COORDENADOR_ACAO = prefix + "24";
	
	/**
	 * Mensagem exibida para o usu�rio para informar os requisitos necess�rios para ser um coordenador de a��o de extens�o. 
	 *  
	 * Conte�do: Somente docentes ou t�cnicos-administrativos com n�vel superior do quadro permanente da UFRN podem ser Coordenadores.
	 * Tipo: ERRO
	 */
	public static final String REQUISITOS_COORDENADOR_ACAO = prefix + "25";
	
	/**
	 * Mensagem exibida para solicitar ao usu�rio a atualiza��o de escolaridade de um servidor. 
	 *  
	 * Conte�do: Atualize a escolaridade do(a) servidor(a) %s junto ao Departamento de Administra��o de Pessoal (DAP).
	 * Tipo: ERRO
	 */
	public static final String SOLICITACAO_ATUALIZAR_ESCOLARIDADE = prefix + "26";
	
	/**
	 * Mensagem exibida para informar ao usu�rio a carga hor�ria semanal m�xima permitida a um membro de projeto. 
	 *  
	 * Conte�do: A carga hor�ria semanal dedicada n�o poder� ser maior que %s horas.
	 * Tipo: ERRO
	 */
	public static final String CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA = prefix + "27";
	
	/**
	 * Mensagem exibida para informar ao usu�rio as regras necess�rias para um servidor aposentado coordenar um projeto. 
	 *  
	 * Conte�do: Servidores aposentados s� podem coordenar projetos se forem registrados no DAP como Colaboradores Volunt�rio
	 * Tipo: ERRO
	 */
	public static final String REGRA_SERVIDOR_APOSENTADO_COORDENAR_PROJETO = prefix + "28";
	
	/**
	 * Mensagem exibida para informar ao usu�rio que para um docente ser coordenador de projeto precisa ter o email cadastrado. 
	 *  
	 * Conte�do: Confirma��o de e-mail necess�ria! Para coordenar este tipo de projeto, o docente %s dever� acesssar a op��o
	 *           'Alterar senha' no cabe�alho do sistema e confirmar seu e-mail. Ap�s a confirma��o do e-mail no formul�rio
	 *           pode ser necess�rio aguardar alguns instantes para que o sistema carregue esta informa��o.
	 * Tipo: ERRO
	 */
	public static final String REGRA_EMAIL_COORDENAR_PROJETO = prefix + "29";
	
	/**
	 * Mensagem exibida para informar ao usu�rio que um membro que ele pretende adicionar foi coordenador de a��o
	 * e esta pendente quanto ao envio de relat�rio final. 
	 *  
	 * Conte�do: N�o � poss�vel inserir %s como membro da A��o de Extens�o, pois ele � coordenador de A��o de Extens�o
	 * 			 pendente quanto ao envio de Relat�rio Final. O Coordenador de uma A��o de Extens�o � considerado pendente
	 *           quanto ao envio de Relat�rio Final quando n�o enviou o Relat�rio Final %s dias ap�s o t�rmino da A��o coordenada.
	 * Tipo: ERRO
	 */
	public static final String REGRA_INSERIR_MEMBRO_PENDENTE_QUANTO_ENVIO_DE_RELATORIO = prefix + "30";
	
	/**
	 * Mensagem exibida ao usu�rio no formul�rio de busca de atividades de extens�o na parte p�blica.
	 *  
	 * Conte�do: Op��o selecionada n�o informada.
	 * Tipo: ERROR
	 */
	public static final String OPCAO_SELECIONADA_NAO_INFORMADO = prefix + "31";
	
	/**
	 * Mensagem exibida aos Coordenadores de a��es de extens�o que est�o pendentes quanto ao envio de relat�rios.
	 *  
	 * Conte�do: Prezados Coordenadores de A��es de Extens�o Universit�ria,
     *   		 Considerando a necessidade de assegurar a consolida��o de informa��es
     *   		 para os relat�rios institucionais, bem como em obedi�ncia � determina��o
     *   		 da Resolu��o 053/2008 - CONSEPE, de 15 de Abril de 2008, que disp�e sobre
     *   		 as Normas que Regulamentam as A��es de Extens�o Universit�ria na UFRN,
     *   		 solicitamos a Vossa Senhoria o preenchimento dos relat�rios finais das
     *   		 a��es de extens�o universit�ria e o seu devido envio via SIGAA.
     *   		 Oportuno esclarecer que de acordo com o Artigo 31 da citada Resolu��o, os Coordenadores
     *   		 de quaisquer a��es de Extens�o Universit�ria devem apresentar o Relat�rio Final � PROEX,
     *   		 at� no m�ximo 30 (trinta) dias ap�s a data prevista de conclus�o da atividade e que a n�o
     *   		 apresenta��o do relat�rio final ou parcial ao final do exerc�cio implicar� em n�o aprova��o
     *   		 de Projetos futuros. Na ocasi�o, ressaltamos que para UFRN cumprir com a sua miss�o � importante
     *   		 a presta��o de informa��es coerentes com a realidade aos �rg�os de controle externo, e por isso
     *   		 solicitamos mais uma vez a vossa senhoria que preencha os relat�rios final ou parcial no SIGAA das
     *   		 a��es de extens�o sob vossa responsabilidade e registre explicitamente os impactos causados na
     *   		 comunidade bem como a sua relev�ncia social. Outro aspecto imprescind�vel a ser registrado � a rela��o
     *   		 existente da atividade de extens�o com o ensino e/ou a pesquisa, enfatizando a relev�ncia acad�mica da
     *   		 a��o e a sua influ�ncia para uma forma��o cidad�.
	 *
     *   		 Na certeza do atendimento � solicita��o, agradecemos antecipadamente o empenho de Vossa Senhoria no desenvolvimento de atividades acad�micas no campo da Extens�o Universit�ria.
     *  	
     *   		 Atenciosamente,
	 *
     *   		 Assessoria T�cnica da PROEx.
     *   	
     *   		 3215-3234
	 * Tipo: ERROR
	 */
	public static final String COORDENADOR_PENDENTE_RELATORIO = prefix + "32";

	/**
	 * Mensagem exibida ao usu�rio no formul�rio cadastro de Relat�rios de Atividades
	 * quando o usu�rio deixa de preencher pelo menos uma data.
	 *  
	 * Conte�do: O campo Per�odo cont�m campos vazios ou inv�lidos.
	 * Tipo: ERROR
	 */
	public static final String PERIODO_INVALIDO = prefix + "33";
	
	/**
	 * Mensagem exibida ao usu�rio na totaliza��o do p�blico atingido pelas 
	 * a��es de extens�o quando o ano � inv�lido.
	 *  
	 * Conte�do: Ano da A��o n�o pode ser maior que o ano atual.
	 * Tipo: ERROR
	 */
	public static final String ANO_INVALIDO = prefix + "34";
	
/**
	 * Mensagem exibida ao usu�rio no formul�rio de cadastro de planos de trabalho.
	 *  
	 * Conte�do: O cadastro de planos de trabalho para esta A��o de Extens�o n�o est� autorizado.
	 * Tipo: ERROR
	 */
	
	public static final String REGRA_CADASTRO_PLANO_TRABALHO_ACAO_EXTENSAO = prefix + "35";
	
   /**
	 * Mensagem exibida quando n�o � poss�vel cadastrar um plano de trabalho para uma a��o de extens�o.
	 *  
	 * Conte�do: O cadastro de planos de trabalho para esta A��o de Extens�o n�o est� autorizado.
	 * Tipo: ERROR
	 */
	public static final String CADASTRO_PLANO_NAO_PERMITIDO_PARA_UMA_ACAO = prefix + "40";
		
	
}