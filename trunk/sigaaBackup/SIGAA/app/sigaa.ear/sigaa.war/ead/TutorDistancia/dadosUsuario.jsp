<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="tutorDistancia" />

	<h2 class="title"><ufrn:subSistema /> > Cadastro de Tutores à Distância > Dados do Usuário</h2>

	<h:messages showDetail="true"></h:messages>
	<br>
		<div class="descricaoOperacao">
			<b>Caro usuário,</b> 
			<br/><br/>
			Nesta tela é possível verificar os dados do usuário selecionado e remover as turmas do tutor.
			<br/><br/>
			As turmas só serão removidas quando a operação for terminada.
			<br/><br/>
		</div>
	<h:form id="formulario">

		<table class="formulario" width="100%">
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
		<br/>
		<div class="infoAltRem">
			<img src="/sigaa/ava/img/bin.png">: Remover Turma
		</div>		
		<br/>

		<table class="formulario" width="30%">
		<caption>Busca por Turmas</caption>
		<tbody>
			<tr>
				<td align="right"><label>Ano-Período das Turmas: </label></td>
				<td align="left">			
					<h:inputText value="#{ tutorDistancia.ano }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="ano"/>-
					<h:inputText value="#{ tutorDistancia.periodo }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodo"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
	
				<td colspan="3"><h:commandButton value="Buscar" action="#{tutorDistancia.buscarTurmasTutor}" /> </td>
			</tr>
		</tfoot>
		</table>
		<br/>

		<table class="listagem">
			<caption>${fn:length(tutorDistancia.turmasTutor)} turmas encontradas</caption>
			<thead>
				<tr>
					<th style="text-align: center;">Ano Período</th>
					<th>Docente(s)</th>
					<th>Tipo</th>
					<th>Situação</th>
					<th>Local</th>
					<th style="text-align: center;">Qtd. Alunos</th>
					<th></th>
				</tr>
			</thead>
			<c:set var="disciplinaAtual" value="0" />
			<c:forEach items="#{tutorDistancia.turmasTutor}" var="t" varStatus="loop">
				<c:if test="${ disciplinaAtual != t.disciplina.id}">
					<c:set var="disciplinaAtual" value="${t.disciplina.id}" />
					<tr style="background-color:#C8D5EC;font-weight:bold;"><td colspan="8" style="font-variant: small-caps;" style="text-align: left;">
						${t.disciplina.descricaoResumida} <small>(${t.disciplina.nivelDesc})</small>
					</td></tr>
				</c:if>
				<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td style="text-align: center;">${t.ano}.${t.periodo}</td>
					<td id="colDocente">${empty t.docentesNomes ? "A DEFINIR" : t.docentesNomes}</td>
					<td>${t.tipoString}</td>
					<td>${t.situacaoTurma.descricao}</td>
					<td>${t.local}</td>
					<td style="text-align: center;">${t.qtdMatriculados}</td>
					<td>
						<h:commandLink action="#{ tutorDistancia.removerTurmaTutor }"  title="Remover Turma">
							<h:graphicImage value="/img/delete.gif" />
							<f:param name="idTurma" value="#{ t.id }" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			<tfoot>
			<tr>
				<td style="text-align:center;" colspan="8">
					<h:commandButton action="#{tutorDistancia.salvar}" value="Salvar" id="buttonSalvar" />
					<h:commandButton action="#{tutorDistancia.telaListar}" value="Cancelar" onclick="#{confirm}" id="buttonCancel" />
					<h:commandButton action="#{tutorDistancia.telaBuscarTurmas}" value="Próxima >>" id="buttonConfirmar" />
				</td>
			</tr>
		</tfoot>
		</table>
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
