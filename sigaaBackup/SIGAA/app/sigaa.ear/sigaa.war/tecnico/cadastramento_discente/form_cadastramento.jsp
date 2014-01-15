<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>
<script type="text/javascript">
function mudarTodos(status) {
	var re= new RegExp('select${cadastramentoDiscenteTecnico.preCadastro ? "Compareceu" : cadastramentoDiscenteTecnico.enviarEmail ? "Enviar" : "Deferir"}', 'g')
	var elements = document.getElementsByTagName('select');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].id.match(re)) {
			elements[i].selectedIndex = status.selectedIndex;
		}
	}
}
</script>
<f:view>
	
	<p:resources />
	<link rel="stylesheet" type="text/css" href="/sigaa/ava/primefaces/redmond/skin.css" />
	
	<a4j:keepAlive beanName="cadastramentoDiscenteTecnico"></a4j:keepAlive>
	<h2><ufrn:subSistema /> &gt; <h:outputText value="Gerenciar Cadastramento de Discentes" rendered="#{!cadastramentoDiscenteTecnico.enviarEmail }" /><h:outputText value="Enviar Email aos Candidatos" rendered="#{cadastramentoDiscenteTecnico.enviarEmail }" /></h2>
	<div class="descricaoOperacao">
		<h:outputText value="
		<p> Esta operação permite confirmar os alunos que compareceram ao cadastramento após uma convocação,
		bem como identificar aqueles que não compareceram, excluindo suas matrículas do sistema e abrindo vagas para
		uma posterior reconvocação dos próximos classificados. </p>
		<ul>
			<li>Para indicar que um aluno <strong>não compareceu</strong> ao cadastramento selecione <strong>AUSENTE</strong> na caixa de seleção correspondente ao aluno.</li>
		</ul>
		<p> Também nesta operação é possível cancelar as convocações dos alunos já cadastrados mas que tiveram seus vínculos cancelados no sistema pelo não comparecimento à matrícula.</p>
		" rendered="#{!cadastramentoDiscenteTecnico.enviarEmail }" escape="false" />
		<h:outputText value="<p>Esta operação permite enviar discente para um grupo de candidatos do processo seletivo do IMD.</p><p>Após filtrar os candidatos para os quais deseja enviar o email, marque-os com <strong>SIM</strong> na primeira coluna da listagem e, em seguida, selecione a opção <strong>Selecionar Candidatos >></strong>.</p>" rendered="#{cadastramentoDiscenteTecnico.enviarEmail }" escape="false" />
	</div>
	
	<h:form id="form">
	
	<table class="formulario" width="80%">
		<caption>Parâmetros do Cadastramento</caption>
		<tr>
			<th class="obrigatorio" width="25%">Processo Seletivo:</th>
			<td>
				<h:selectOneMenu id="selectPsVestibular" value="#{cadastramentoDiscenteTecnico.processoSeletivo.id}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems id="itensPsVestibular" value="#{convocacaoProcessoSeletivoTecnico.processosCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th width="25%">Convocação:</th>
			<td>
				<h:selectOneMenu id="convocacao" value="#{cadastramentoDiscenteTecnico.idConvocacao}">
					<f:selectItem itemValue="0" itemLabel="-- TODAS --" />
					<f:selectItems value="#{cadastramentoDiscenteTecnico.convocacoesCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th width="25%">Pólo / Grupo:</th>
			<td>
				<h:selectOneMenu id="selectOpcao" value="#{cadastramentoDiscenteTecnico.opcao}">
					<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
					<f:selectItems value="#{convocacaoProcessoSeletivoTecnico.polosCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
			<tr>
			<th width="25%">Status:</th>
			<td>
				<h:selectOneMenu id="selectStatus" value="#{cadastramentoDiscenteTecnico.status}">
					<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
					<f:selectItem itemValue="1" itemLabel="ATIVO" />
					<f:selectItem itemValue="2" itemLabel="CADASTRADO" />
					<f:selectItem itemValue="10" itemLabel="EXCLUÍDO" />
					<f:selectItem itemValue="13" itemLabel="PENDENTE CADASTRO" />
					<f:selectItem itemValue="15" itemLabel="PRÉ CADASTRADO" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th width="25%">Nome:</th>
			<td>
				<h:inputText id="nome" value="#{cadastramentoDiscenteTecnico.nome}" style="display:inline;" />
				<rich:suggestionbox for="nome" height="100" width="500" minChars="3" id="suggestionNome" suggestionAction="#{cadastramentoDiscenteTecnico.autocompleteNomeDiscente}" var="_c" fetchValue="#{_c[0]}">
					<h:column>
						<h:outputText value="#{_c[0]}"/>
					</h:column>
					<h:column>
						<h:outputText value="#{_c[1]}"/>
					</h:column>
				</rich:suggestionbox> 
			</td>
		</tr>
		<tr>
			<th width="25%">CPF:</th>
			<td>
				<h:inputText id="cpf" value="#{cadastramentoDiscenteTecnico.cpf}" size="16" maxlength="14" 
					onkeypress="return formataCPF(this, event, null);">
					<f:converter converterId="convertCpf"/>
					<f:param name="type" value="cpf" />
				</h:inputText>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="btnBuscar" value="Buscar Discentes" action="#{cadastramentoDiscenteTecnico.buscar}"/>
					<h:commandButton value="Cancelar" action="#{ cadastramentoDiscenteTecnico.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	<c:if test="${ not empty cadastramentoDiscenteTecnico.convocacoes}" >
	
	<br/><br/>
	
	<div class="infoAltRem" style="width:80%">
		<h:graphicImage value="/img/alterar.gif" /> : Alterar os dados pessoais do candidato
		<h:graphicImage value="/img/email_go.png" /> : Reenviar email de confirmação de cadastro ao discente
	</div>
	
		<table class="listagem" id="resultadoBusca">
			<caption>Discentes encontrados (${fn:length(cadastramentoDiscenteTecnico.convocacoes)})</caption>
			<thead>
				<tr>
					<th></th>
					<th style="text-align: center;">Ingresso</th>
					<th style="text-align: center;">CPF</th>
					<th style="text-align: center;">Matrícula</th>
					<th>Nome</th>
					<th>Email</th>
					<th>Status</th>
					<th colspan="2">Ação</th>
				</tr>
			</thead>
			<tr>
				<td colspan="9" class="subFormulario">
					Definir o status de todos discente para: 
					<h:selectOneMenu id="selectStatusTodosPre" value="#{cadastramentoDiscenteTecnico.idStatusTodosDiscentes}" onchange="mudarTodos(this)" rendered="#{cadastramentoDiscenteTecnico.preCadastro}">
						<f:selectItems value="#{cadastramentoDiscenteTecnico.opcoesPreCadastramentoCombo}"/>
					</h:selectOneMenu>
					<h:selectOneMenu id="selectStatusTodos" value="#{cadastramentoDiscenteTecnico.idStatusTodosDiscentes}" onchange="mudarTodos(this)" rendered="#{!cadastramentoDiscenteTecnico.preCadastro && !cadastramentoDiscenteTecnico.enviarEmail}">
						<f:selectItems value="#{cadastramentoDiscenteTecnico.opcoesCadastramentoCombo}"/>
					</h:selectOneMenu>
					<h:selectOneMenu id="selectStatusTodosEnvioEmail" onchange="mudarTodos(this)" rendered="#{cadastramentoDiscenteTecnico.enviarEmail}">
						<f:selectItem itemValue="false" itemLabel="Não" />
						<f:selectItem itemValue="true" itemLabel="Sim" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<a4j:repeat value="#{cadastramentoDiscenteTecnico.convocacoes}" var="conv" rowKeyVar="loop">
				<tr class="<h:outputText value="linhaImpar" rendered="#{ loop % 2 == 0 }" />">
					<td rowspan="2">
						<h:selectOneMenu id="selectCompareceu" value="#{conv.discente.opcaoCadastramento}" rendered="#{cadastramentoDiscenteTecnico.preCadastro && conv.discente.discente.pendenteCadastro}" title="#{conv.discente.pessoa.nome}">
							<f:selectItems value="#{cadastramentoDiscenteTecnico.opcoesPreCadastramentoCombo}"/>
						</h:selectOneMenu>
						<h:selectOneMenu id="selectDeferir" value="#{conv.discente.opcaoCadastramento}" rendered="#{!cadastramentoDiscenteTecnico.preCadastro && !cadastramentoDiscenteTecnico.enviarEmail && conv.discente.preCadastrado}" title="#{conv.discente.pessoa.nome}">
							<f:selectItems value="#{cadastramentoDiscenteTecnico.opcoesCadastramentoCombo}"/>
						</h:selectOneMenu>
						<h:selectOneMenu id="selectEnviar" value="#{conv.selecionado}" rendered="#{cadastramentoDiscenteTecnico.enviarEmail}" title="#{conv.discente.pessoa.nome}">
							<f:selectItem itemValue="false" itemLabel="Não" />
							<f:selectItem itemValue="true" itemLabel="Sim" />
						</h:selectOneMenu>
					</td>
					<td style="text-align: center;"><h:outputText value="#{conv.discente.anoPeriodoIngresso}" /></td>
					<td style="text-align: center;"><h:outputText value="#{conv.discente.pessoa.cpf_cnpjString}" /></td>
					<td style="text-align: center;"><h:outputText value="#{conv.discente.matricula}" rendered="#{ conv.discente.status == 2 || conv.discente.status == 1 }" /></td>
					<td><strong><h:outputText value="#{conv.discente.pessoa.nome}"/></strong></td>
					<td><h:outputText value="#{conv.discente.pessoa.email}"/></td>
					<td><strong><h:outputText value="#{conv.discente.statusString}"/></strong></td>
					<td rowspan="2" style="vertical-align:top;"><a onclick='return exibibirDialogDadosCandidato(<h:outputText value="#{conv.discente.pessoa.id}" />, "<h:outputText value="#{conv.discente.pessoa.nome}"/>", "<h:outputText value="#{conv.discente.pessoa.email}"/>", <h:outputText value="#{conv.discente.pessoa.cpf_cnpj}"/>);' title="Atualizar Dados Pessoais" style="cursor:pointer;"><h:graphicImage id="alterar" value="/img/alterar.gif" /></a></td>
					<td rowspan="2" style="vertical-align:top;"><h:commandLink action="#{ cadastramentoDiscenteTecnico.enviarConfirmacaoDeCadastroADiscente }" rendered="#{conv.discente.status == 2 }" ><f:param name="idCandidato" value="#{ conv.id }" /><h:graphicImage id="imgEnviarEmail" value="/img/email_go.png" /></h:commandLink></td>
				</tr>
				<tr class="<h:outputText value="linhaImpar" rendered="#{ loop % 2 == 0 }" />">
					<td colspan="7" style="text-align:right;font-size:80%;">
					<strong>RG:</strong> <h:outputText value="#{conv.discente.pessoa.registroGeral}"/> -
					<strong>Nascimento:</strong> <h:outputText value="#{conv.discente.pessoa.dataNascimento}"/> -
						<strong>Classificação:</strong> <h:outputText value="#{conv.resultado.classificacaoAprovado}"/> -
						<strong>Convocação:</strong> <h:outputText value="#{conv.convocacaoProcessoSeletivo.descricao}"/><br/> 
						<strong>Pólo / Grupo:</strong> <h:outputText value="#{conv.inscricaoProcessoSeletivo.opcao.descricao}" /> - 
						<strong>Res. Vagas:</strong> <h:outputText value="#{conv.inscricaoProcessoSeletivo.reservaVagas ? 'Sim' : 'Não'}" />
					</td>
				</tr>
			</a4j:repeat>
			<tfoot>
			<tr>
				<td colspan="9" style="text-align: center;">
					<h:commandButton value="Processar Pré-Cadastramento >>" action="#{ cadastramentoDiscenteTecnico.preProcessar }" id="btnPreProcessar" rendered="#{cadastramentoDiscenteTecnico.preCadastro}" />
					<h:commandButton value="Processar Cadastramento >>" action="#{ cadastramentoDiscenteTecnico.processar }" id="btnProcessar" rendered="#{!cadastramentoDiscenteTecnico.preCadastro && !cadastramentoDiscenteTecnico.enviarEmail}" />
					<h:commandButton value="Selecionar Candidatos >>" action="#{ cadastramentoDiscenteTecnico.processarEnvioEmail }" id="btnProcessarEnvioEmail" rendered="#{cadastramentoDiscenteTecnico.enviarEmail}" />
				</td>
			</tr>
			</tfoot>
		</table>
	</c:if>
	
		<p:dialog header="Alterar dados do candidato" widgetVar="dialogDadosCandidato" modal="true" width="380" height="180">
			<h:inputHidden value="#{ cadastramentoDiscenteTecnico.pessoa.id }" id="idAlterar" />
			<table class="formulario">
				<tr>
					<th>Nome:</th>
					<td><h:inputText value="#{ cadastramentoDiscenteTecnico.pessoa.nome }" id="nomeAlterar" size="60" /></td>
				</tr>
				<tr>
					<th>Email:</th>
					<td><h:inputText value="#{ cadastramentoDiscenteTecnico.pessoa.email }" id="emailAlterar" size="50" /></td>
				</tr>
				<tr>
					<th>CPF:</th>
					<td>
						<h:inputText value="#{ cadastramentoDiscenteTecnico.pessoa.cpf_cnpj }" size="16" maxlength="14"  id="cpfAlterar" onkeypress="return formataCPF(this, event, null);">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />
						</h:inputText>
					</td>
				</tr>
				<tfoot>
					<tr><td colspan="2"><h:commandButton action="#{cadastramentoDiscenteTecnico.atualizarDadosCandidato}" value="Alterar" /></td></tr>
				</tfoot>
			</table>
		</p:dialog>
	
	</h:form>
	
	<script>
		function exibibirDialogDadosCandidato (idPessoa, nome, email, cpf){
			
			document.getElementById("form:idAlterar").value = idPessoa;
			document.getElementById("form:nomeAlterar").value = nome;
			document.getElementById("form:emailAlterar").value = email;
			
			cpf = "" + cpf;
			
			if (cpf.length < 11)
				while (cpf.length < 11)
					cpf = "0" + cpf;
			
			cpf = cpf[0]+cpf[1]+cpf[2]+"."+cpf[3]+cpf[4]+cpf[5]+"."+cpf[6]+cpf[7]+cpf[8]+"-"+cpf[9]+cpf[10];
			
			document.getElementById("form:cpfAlterar").value = cpf;
			
			dialogDadosCandidato.show();
			return false;
		}
	</script>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>