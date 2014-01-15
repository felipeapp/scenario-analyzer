<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="tutorDistancia" />

	<h2 class="title"><ufrn:subSistema /> > Cadastro de Tutores à Distância > Cadastrar</h2>

	<h:messages showDetail="true"></h:messages>
		<div class="descricaoOperacao">
			<b>Caro usuário,</b> 
			<br/><br/>
			Nesta tela é possível selecionar as turmas virtuais que o tutor à distância terá acesso.
			<br/><br/>
		</div>
	<h:form id="form">

		<c:if test="${!tutorDistancia.alterar }">
			<table class="formulario" width="90%">
			<caption class="listagem">Dados do Tutor</caption>
				<tr>
					<th style="font-weight:bold;">Nome:</th>
					<td><h:outputText value="#{tutorDistancia.obj.pessoa.nome }" /></td>
				</tr>
				<tr>
					<th style="font-weight:bold;">CPF:</th>
					<td><ufrn:format type="cpf_cnpj" valor="${tutorDistancia.obj.pessoa.cpf_cnpj}"></ufrn:format></td>
				</tr>
			</table>
		</c:if>

		<br/>
		<table class="formulario" width="90%">
		<caption class="listagem">Informe os critérios de busca das turmas</caption>
			<tr>
				<td width="1px;"><h:selectBooleanCheckbox value="true" readonly="true" disabled="true" styleClass="check" /></td>
				<td width="20%">Ano-Período:</td>
				<td>
					<h:inputText value="#{tutorDistancia.ano}" size="4" maxlength="4" 
							id="inputAno" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" /> -
					<h:inputText value="#{tutorDistancia.periodo}" size="1" maxlength="1" 
							id="inputPeriodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" />
				</td>
			</tr>
			<tr>
				<td width="1px;"><h:selectBooleanCheckbox value="#{tutorDistancia.filtroNomeDocente}" id="checkNomeDocente" styleClass="noborder" /></td>
				<td><label for="checkNomeDocente" onclick="$('form:checkNomeDocente').checked = !$('form:checkNomeDocente').checked;">Nome do Docente:</label></td>
				<td>
					<h:inputText value="#{tutorDistancia.nomeDocente}" maxlength="60" style="width: 60%;" id="inputNomeDocente" onfocus="$('form:checkNomeDocente').checked = true;" onkeyup="CAPS(this)"/>
				</td>
			</tr>
			<tr>
				<td width="1px;"><h:selectBooleanCheckbox value="#{tutorDistancia.filtroNomeComponente}" id="checkNomeComponente" styleClass="noborder" /></td>
				<td><label for="checkNomeComponente" onclick="$('form:checkNomeComponente').checked = !$('form:checkNomeComponente').checked;">Nome do Componente:</label></td>
				<td>
					<h:inputText value="#{tutorDistancia.nomeComponente}" maxlength="60" style="width: 60%;" id="inputNomeComponente" onfocus="$('form:checkNomeComponente').checked = true;" onkeyup="CAPS(this)" />
				</td>
			</tr>
			<tr>
				<td width="1px;"><h:selectBooleanCheckbox value="#{tutorDistancia.filtroCodigoComponente}" id="checkCodigoComponente" styleClass="noborder" /></td>
				<td><label for="checkCodigoComponente" onclick="$('form:checkCodigoComponente').checked = !$('form:checkCodigoComponente').checked;">Código do Componente:</label></td>
				<td><h:inputText value="#{tutorDistancia.codigoComponente}" size="10" maxlength="9" id="inputNomeDisciplina" onfocus="$('form:checkCodigoComponente').checked = true;" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
				<td width="1px;" style="vertical-align:top"><h:selectBooleanCheckbox value="#{tutorDistancia.filtroPolo}" id="checkPolo" styleClass="noborder" /></td>
				<td style="vertical-align:top"><label for="checkPolo" onclick="$('form:checkPolo').checked = !$('form:checkPolo').checked;">Pólo:</label></td>
				<td>
					<t:selectManyCheckbox id="polo" value="#{tutorDistancia.buscarEm}" onclick="$('form:checkPolo').checked = true;" layout="pageDirection" layoutWidth="2">
						<f:selectItems value="#{tutorOrientador.polos}" />
					</t:selectManyCheckbox>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{tutorDistancia.buscarTurma}" value="Buscar" id="buttonBuscar" />
						<h:commandButton action="#{tutorDistancia.voltarSelecaoTurma}" value="<< Voltar" id="buttonVoltar" />
						<h:commandButton action="#{tutorDistancia.cancelar}" value="Cancelar" onclick="#{confirm}" id="buttonCancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<c:if test="${not empty tutorDistancia.turmasBuscadas}">
			<br/>
			<table class="listagem">
				<caption>${fn:length(tutorDistancia.turmasBuscadas)} turmas encontradas</caption>
				<thead>
					<tr>
						<th style="width:1%"><input type="checkbox" onclick="checkAll()"/></th>
						<th style="text-align: left;width:9%;">Ano Período</th>
						<th>Docente(s)</th>
						<th>Tipo</th>
						<th>Situação</th>
						<th>Local</th>
						<th style="text-align: center;">Qtd. Alunos</th>
					</tr>
				</thead>
				<c:set var="disciplinaAtual" value="0" />
				<c:forEach items="#{tutorDistancia.turmasBuscadas}" var="t" varStatus="loop">
					<c:if test="${ disciplinaAtual != t.disciplina.id}">
						<c:set var="disciplinaAtual" value="${t.disciplina.id}" />
						<tr style="background-color:#C8D5EC;font-weight:bold;"><td colspan="8" style="font-variant: small-caps;" style="text-align: left;">
							${t.disciplina.descricaoResumida} <small>(${t.disciplina.nivelDesc})</small>
						</td></tr>
					</c:if>
					<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="width:1%"><h:selectBooleanCheckbox value="#{t.selecionada}" styleClass="check" /></td>
						<td style="text-align: left;width:9%;">${t.ano}.${t.periodo}</td>
						<td id="colDocente">${empty t.docentesNomes ? "A DEFINIR" : t.docentesNomes}</td>
						<td>${t.tipoString}</td>
						<td>${t.situacaoTurma.descricao}</td>
						<td>${t.local}</td>
						<td style="text-align: center;">${t.qtdMatriculados}</td>
					</tr>
				</c:forEach>
				<tfoot>
				<tr>
					<td colspan="8" style="text-align:center">
						<h:commandButton action="#{tutorDistancia.selecionarTurmas}" value="Próximo >>" id="buttonProximo" />
					</td>
				</tr>
			</tfoot>
			</table>
		</c:if>	
	</h:form>	
	
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

<script type="text/javascript">
function checkAll() {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = !e.checked;
	});
}
</script>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
