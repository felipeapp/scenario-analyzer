/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/11/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do Módulo de Extensão que serão exibidas para o usuário.
 * 
 * @author Daniel Augusto
 *
 */
public interface MensagensExtensao {
	
	/**
	 * Prefixo para estas mensagens.
	 */
	static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.EXTENSAO.getId() + "_";
	
	// Mensagens de notificações de inscrição pelo portal público
	
	/**
	 * Mensagem exibida quando usuário submeter o formulário de inscrição para cursos e eventos.
	 *  
	 * Conteúdo: Inscrição submetida com sucesso! Você receberá um email contendo as informações de como proceder.
	 * Tipo: INFORMATION
	 */
	public static final String INSCRICAO_EFETUADA_COM_SUCESSO = prefix + "01";
	
	/**
	 * Mensagem exibida para o usuário ao tentar acessar uma URL inválida para confirmação da inscrição.
	 *  
	 * Conteúdo: Endereço inválido! Verifique o email enviado após a inscrição e acesse o endereço informado.
	 * Tipo: ERROR
	 */
	public static final String URL_CONFIRMACAO_INSCRICAO_INVALIDA = prefix + "02";
	
	/**
	 * Mensagem exibida quando usuário tentar confirmar uma inscrição em uma ação, caso este possua
	 * uma outra inscrição mais recente confirmada para a mesma ação.
	 *  
	 * Conteúdo: Você já possui uma inscrição confirmada para esta ação. Para realizar uma nova inscrição é necessário 
	 * cancelar sua inscrição anterior. Acesse a área do inscrito pelo portal público do SIGAA e realize o cancelamento.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_ATIVA_RECENTE_EXISTENTE = prefix + "03";
	
	/**
	 * Mensagem exibida para o usuário ao tentar realizar o login na área de inscritos, 
	 * e sua inscrição estiver cancelada.
	 *  
	 * Conteúdo: Sua inscrição foi cancelada. O acesso a está área foi suspenso.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_CANCELADA_ACESSO_SUSPENSO = prefix + "04";
	
	/**
	 * Mensagem exibida para o usuário ao tentar realizar o login na área de inscritos,
	 * e sua inscrição não tiver sido confirmada.
	 *  
	 * Conteúdo: Sua inscrição não foi confirmada. Acesse seu email para realizar a confirmação.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_NAO_CONFIRMADA_ACESSO_SUSPENSO = prefix + "05";
	
	/**
	 * Mensagem exibida para o usuário ao tentar realizar o login na área de inscritos,
	 * e sua inscrição encontra-se recusada.
	 *  
	 * Conteúdo: Sua inscrição foi recusada pelo coordenador da ação. O acesso a essa área foi suspenso.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_RECUSADA_ACESSO_SUSPENSO = prefix + "06";
	
	/**
	 * Mensagem exibida para o usuário após realizar o login na área de inscritos,
	 * e sua inscrição encontra-se pendente da aprovação do coordenador da ação.
	 *  
	 * Conteúdo: Sua inscrição está aguardando a aprovação do coordenador da ação.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_PENDENTE_APROVACAO_COORDENDADOR = prefix + "07";
	
	/**
	 * Mensagem exibida para o usuário na área de inscritos caso tente cancelar sua inscrição
	 * que já foi aprovada.
	 *  
	 * Conteúdo: Não foi possível cancelar a inscrição. Ela já foi aceita pelo coordenador da ação.
	 * Tipo: ERROR
	 */
	public static final String NAO_EH_PERMITIDO_CANCELAR_INSCRICAO_ACEITA = prefix + "08";
	
	/**
	 * Mensagem exibida para o usuário na área de inscritos ao tentar imprimir o certificado de participação
	 * antes do período final da ação ou não possua frequência necessária.
	 *  
	 * Conteúdo: Não foi possível imprimir o certificado. Verifique o período do término da ação ou 
	 * entre em contato com o coordenador da ação.
	 * Tipo: ERROR
	 */
	public static final String NAO_EH_POSSIVEL_IMPRIMIR_CERTIFICADO_PENDENTE_ACAO = prefix + "09";
	
	/**
	 * Mensagem exibida para o usuário na área de inscritos ao tentar imprimir o certificado de participação
	 * caso sua inscrição não esteja aprovada.
	 *  
	 * Conteúdo: Sua participação está pendente de aprovação. Aguarde pela confirmação do coordenador.
	 * Tipo: WARNING
	 */
	public static final String NAO_EH_POSSIVEL_IMPRIMIR_CERTIFICADO_POR_NAO_PARTICIPANTE = prefix + "10";
	
	/**
	 * Mensagem exibida para o usuário na área de inscritos ao cancelar sua inscrição.
	 *  
	 * Conteúdo: Sua Inscrição foi cancelada com sucesso!
	 * Tipo: INFORMATION
	 */
	public static final String INSCRICAO_CANCELADA_SUCESSO = prefix + "11";
	
	/**
	 * Mensagem exibida para o usuário ao informar a senha recebida por email na tela de confirmação da inscrição.
	 *  
	 * Conteúdo: Sua Inscrição foi confirmada com sucesso!
	 * Tipo: INFORMATION
	 */
	public static final String INSCRICAO_CONFIRMADA_SUCESSO = prefix + "12";
	
	/**
	 * Mensagem exibida para o usuário ao informar a senha recebida por email na tela de confirmação da inscrição, e este
	 * possua uma outra inscrição confirmada anteriormente.
	 *  
	 * Conteúdo: Você possui uma inscrição anterior confirmada que foi cancelada. Utilize a inscrição que foi confirmada 
	 * para participar do processo de seleção.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_RECENTE_CANCELADA = prefix + "13";
	
	/**
	 * Mensagem exibida para o usuário ao se inscrever na mesma ação pela área pública ou ao tentar confirmar uma inscrição, 
	 * caso ele já seja participante na ação.
	 *  
	 * Conteúdo: Você possui uma inscrição aceita pelo coordenador da ação. Acesse a área de inscritos pelo portal do SIGAA 
	 * com a senha enviada para o seu e-mail e consulte os dados referente a ação de extensão da qual você está participando.
	 * Tipo: WARNING
	 */
	public static final String INSCRITO_EH_PARTICIPANTE_ACAO = prefix + "14";
	
	/**
	 * Mensagem exibida ao usuário quando este selecionar a inscrição para um curso ou evento que possua o número de vagas 
	 * esgotado.
	 *  
	 * Conteúdo: Essa ação possui o número de vagas esgotado, porém, ainda está aberta para inscrições. Sua aceitação como 
	 * participante estará dependente do aumento no número de vagas. Após realizada a inscrição, consulte seu email para 
	 * maiores informações.
	 * Tipo: WARNING
	 */
	public static final String INSCRICAO_NUMERO_VAGAS_ESGOTADO = prefix + "15";
	
	/**
	 * Mensagem exibida para o usuário ao tentar realizar o login na área do inscrito com email ou senha incorretos e/ou em 
	 * branco.
	 *  
	 * Conteúdo: E-mail e/ou Senha inválidos.
	 * Tipo: ERROR
	 */
	public static final String EMAIL_SENHA_INVALIDOS = prefix + "16";
	
	/**
	 * Mensagem exibida ao usuário durante as etapas do processo de inscrição, caso este tenha sido inválidado.
	 *  
	 * Conteúdo: Operação Inválida! Reinicie o processo de inscrição.
	 * Tipo: ERROR
	 */
	public static final String OPERACAO_INVALIDA = prefix + "17";
	
	/**
	 * Mensagem exibida para o usuário após informar seu CPF no formulário de inscrição, caso seus dados não existam na 
	 * base de dados.
	 *  
	 * Conteúdo: Seu CPF não está cadastrado no sistema. Preencha o formulário contendo seus dados para concluir sua inscrição.
	 * Tipo: WARNING
	 */
	public static final String CPF_NAO_CADASTRADO = prefix + "18";
	
	/**
	 * Mensagem exibida para o usuário ao informar sua senha na tela de confirmação de inscrição.
	 *  
	 * Conteúdo: Senha incorreta ou inscrição já confirmada.
	 * Tipo: ERROR
	 */
	public static final String ERRO_CONFIRMACAO_INSCRICAO = prefix + "19";
	
	/**
	 * Mensagem exibida para o coordenador da ação na tela de criação de inscrições, caso exista uma inscrição já aberta (ativa)
	 * para a mesma ação.
	 *  
	 * Conteúdo: Essa ação possui uma inscrição (%s) com período aberto de %s à %s.
	 * Tipo: WARNING
	 */
	public static final String PERIODO_INSCRICAO_ATIVO = prefix + "20";
	
	/**
	 * Mensagem exibida para o coordenador da ação na tela de suspensão do período de inscrição, caso existam membros inscritos 
	 * para nesse período.
	 *  
	 * Conteúdo: Está inscrição possui membros inscritos aguardando pelo processo de seleção para participante. Caso confirme a 
	 * suspensão do período de inscrição, todos os inscritos terão suas inscrições canceladas.
	 * Tipo: WARNING
	 */
	public static final String REMOCAO_INSCRICAO_MEMBROS_INSCRITOS = prefix + "21";
	
	/**
	 * Mensagem exibida para o coordenador da ação ao realizar a operação de suspensão do período de inscrição, sem informar os
	 * motivos para da suspensão do período de inscrição.
	 *  
	 * Conteúdo: Informe o(s) motivo(s) da suspensão do período de inscrição para que os inscritos possam ser notificados por 
	 * e-mail.
	 * Tipo: ERROR
	 */
	public static final String INFORMAR_MOTIVOS_SUSPENSAO_PERIODO = prefix + "22";
	
	/**
	 * Mensagem exibida para o usuário ao logar na área de inscritos e cuja a inscrição tenha sido aceita pelo coordenador da ação.
	 *  
	 * Conteúdo: Você está atualmente como participante nessa ação de extensão.
	 * Tipo: INFORMATION
	 */
	public static final String ACEITA_INSCRICAO_PARA_PARTICIPANTE = prefix + "23";
	
	/**
	 * Mensagem exibida para o usuário para informar que uma ação deve ter um coordenador.
	 *  
	 * Conteúdo: Deve haver pelo menos um(a) Coordenador(a) da ação.
	 * Tipo: ERRO
	 */
	public static final String COORDENADOR_ACAO = prefix + "24";
	
	/**
	 * Mensagem exibida para o usuário para informar os requisitos necessários para ser um coordenador de ação de extensão. 
	 *  
	 * Conteúdo: Somente docentes ou técnicos-administrativos com nível superior do quadro permanente da UFRN podem ser Coordenadores.
	 * Tipo: ERRO
	 */
	public static final String REQUISITOS_COORDENADOR_ACAO = prefix + "25";
	
	/**
	 * Mensagem exibida para solicitar ao usuário a atualização de escolaridade de um servidor. 
	 *  
	 * Conteúdo: Atualize a escolaridade do(a) servidor(a) %s junto ao Departamento de Administração de Pessoal (DAP).
	 * Tipo: ERRO
	 */
	public static final String SOLICITACAO_ATUALIZAR_ESCOLARIDADE = prefix + "26";
	
	/**
	 * Mensagem exibida para informar ao usuário a carga horária semanal máxima permitida a um membro de projeto. 
	 *  
	 * Conteúdo: A carga horária semanal dedicada não poderá ser maior que %s horas.
	 * Tipo: ERRO
	 */
	public static final String CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA = prefix + "27";
	
	/**
	 * Mensagem exibida para informar ao usuário as regras necessárias para um servidor aposentado coordenar um projeto. 
	 *  
	 * Conteúdo: Servidores aposentados só podem coordenar projetos se forem registrados no DAP como Colaboradores Voluntário
	 * Tipo: ERRO
	 */
	public static final String REGRA_SERVIDOR_APOSENTADO_COORDENAR_PROJETO = prefix + "28";
	
	/**
	 * Mensagem exibida para informar ao usuário que para um docente ser coordenador de projeto precisa ter o email cadastrado. 
	 *  
	 * Conteúdo: Confirmação de e-mail necessária! Para coordenar este tipo de projeto, o docente %s deverá acesssar a opção
	 *           'Alterar senha' no cabeçalho do sistema e confirmar seu e-mail. Após a confirmação do e-mail no formulário
	 *           pode ser necessário aguardar alguns instantes para que o sistema carregue esta informação.
	 * Tipo: ERRO
	 */
	public static final String REGRA_EMAIL_COORDENAR_PROJETO = prefix + "29";
	
	/**
	 * Mensagem exibida para informar ao usuário que um membro que ele pretende adicionar foi coordenador de ação
	 * e esta pendente quanto ao envio de relatório final. 
	 *  
	 * Conteúdo: Não é possível inserir %s como membro da Ação de Extensão, pois ele é coordenador de Ação de Extensão
	 * 			 pendente quanto ao envio de Relatório Final. O Coordenador de uma Ação de Extensão é considerado pendente
	 *           quanto ao envio de Relatório Final quando não enviou o Relatório Final %s dias após o término da Ação coordenada.
	 * Tipo: ERRO
	 */
	public static final String REGRA_INSERIR_MEMBRO_PENDENTE_QUANTO_ENVIO_DE_RELATORIO = prefix + "30";
	
	/**
	 * Mensagem exibida ao usuário no formulário de busca de atividades de extensão na parte pública.
	 *  
	 * Conteúdo: Opção selecionada não informada.
	 * Tipo: ERROR
	 */
	public static final String OPCAO_SELECIONADA_NAO_INFORMADO = prefix + "31";
	
	/**
	 * Mensagem exibida aos Coordenadores de ações de extensão que estão pendentes quanto ao envio de relatórios.
	 *  
	 * Conteúdo: Prezados Coordenadores de Ações de Extensão Universitária,
     *   		 Considerando a necessidade de assegurar a consolidação de informações
     *   		 para os relatórios institucionais, bem como em obediência à determinação
     *   		 da Resolução 053/2008 - CONSEPE, de 15 de Abril de 2008, que dispõe sobre
     *   		 as Normas que Regulamentam as Ações de Extensão Universitária na UFRN,
     *   		 solicitamos a Vossa Senhoria o preenchimento dos relatórios finais das
     *   		 ações de extensão universitária e o seu devido envio via SIGAA.
     *   		 Oportuno esclarecer que de acordo com o Artigo 31 da citada Resolução, os Coordenadores
     *   		 de quaisquer ações de Extensão Universitária devem apresentar o Relatório Final à PROEX,
     *   		 até no máximo 30 (trinta) dias após a data prevista de conclusão da atividade e que a não
     *   		 apresentação do relatório final ou parcial ao final do exercício implicará em não aprovação
     *   		 de Projetos futuros. Na ocasião, ressaltamos que para UFRN cumprir com a sua missão é importante
     *   		 a prestação de informações coerentes com a realidade aos órgãos de controle externo, e por isso
     *   		 solicitamos mais uma vez a vossa senhoria que preencha os relatórios final ou parcial no SIGAA das
     *   		 ações de extensão sob vossa responsabilidade e registre explicitamente os impactos causados na
     *   		 comunidade bem como a sua relevância social. Outro aspecto imprescindível a ser registrado é a relação
     *   		 existente da atividade de extensão com o ensino e/ou a pesquisa, enfatizando a relevância acadêmica da
     *   		 ação e a sua influência para uma formação cidadã.
	 *
     *   		 Na certeza do atendimento à solicitação, agradecemos antecipadamente o empenho de Vossa Senhoria no desenvolvimento de atividades acadêmicas no campo da Extensão Universitária.
     *  	
     *   		 Atenciosamente,
	 *
     *   		 Assessoria Técnica da PROEx.
     *   	
     *   		 3215-3234
	 * Tipo: ERROR
	 */
	public static final String COORDENADOR_PENDENTE_RELATORIO = prefix + "32";

	/**
	 * Mensagem exibida ao usuário no formulário cadastro de Relatórios de Atividades
	 * quando o usuário deixa de preencher pelo menos uma data.
	 *  
	 * Conteúdo: O campo Período contém campos vazios ou inválidos.
	 * Tipo: ERROR
	 */
	public static final String PERIODO_INVALIDO = prefix + "33";
	
	/**
	 * Mensagem exibida ao usuário na totalização do público atingido pelas 
	 * ações de extensão quando o ano é inválido.
	 *  
	 * Conteúdo: Ano da Ação não pode ser maior que o ano atual.
	 * Tipo: ERROR
	 */
	public static final String ANO_INVALIDO = prefix + "34";
	
/**
	 * Mensagem exibida ao usuário no formulário de cadastro de planos de trabalho.
	 *  
	 * Conteúdo: O cadastro de planos de trabalho para esta Ação de Extensão não está autorizado.
	 * Tipo: ERROR
	 */
	
	public static final String REGRA_CADASTRO_PLANO_TRABALHO_ACAO_EXTENSAO = prefix + "35";
	
   /**
	 * Mensagem exibida quando não é possível cadastrar um plano de trabalho para uma ação de extensão.
	 *  
	 * Conteúdo: O cadastro de planos de trabalho para esta Ação de Extensão não está autorizado.
	 * Tipo: ERROR
	 */
	public static final String CADASTRO_PLANO_NAO_PERMITIDO_PARA_UMA_ACAO = prefix + "40";
		
	
}