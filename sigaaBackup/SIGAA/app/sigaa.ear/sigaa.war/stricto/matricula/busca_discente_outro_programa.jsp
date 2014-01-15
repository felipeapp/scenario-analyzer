<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:form id="formulario">

	<h2> <ufrn:subSistema /> > Matricular Discente de Outro Programa &gt; Buscar Discente</h2>

	<table class="formulario" style="width:80%;">
		<caption> Informe os critérios de busca</caption>
		<tbody>
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{matriculaDiscenteOutroProgramaBean.buscaMatricula}" styleClass="noborder" id="checkMatricula"/>
				</td>
				<th> <label for="checkMatricula">Matrícula:</label></th>
				<td> 
					<h:inputText value="#{matriculaDiscenteOutroProgramaBean.obj.matricula}" size="15" id="matriculaDiscente" 
						onfocus="getEl('formulario:checkMatricula').dom.checked = true;" 
						onkeyup="formatarInteiro(this)"/>
				</td>
			</tr>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{matriculaDiscenteOutroProgramaBean.buscaNome}" styleClass="noborder" id="checkNome"/>
				</td>
				<th> <label for="checkNome">Nome do Discente:</label></th>
				<td> <h:inputText  value="#{matriculaDiscenteOutroProgramaBean.obj.pessoa.nome}" size="60" id="nomeDiscente" onfocus="getEl('formulario:checkNome').dom.checked = true;"/> </td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{matriculaDiscenteOutroProgramaBean.buscar}" value="Buscar"	id="buscar"/>
					<h:commandButton id="cancelar" action="#{matriculaDiscenteOutroProgramaBean.cancelar}" value="Cancelar" onclick="limpaMatricula();#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>

	</h:form>

	<c:if test="${not empty matriculaDiscenteOutroProgramaBean.discentes}">
		<br />
		<table class="listagem">
			<caption> Selecione abaixo o discente (${fn:length(matriculaDiscenteOutroProgramaBean.discentes)}) </caption>
			<thead>
			<tr>
				<th colspan="3"> Aluno </th>
				<th> Status </th>
				<th> </th>
			</tr>
			<tbody>
				<h:form>
				<c:set var="idFiltro" value="-1" />

				<c:forEach items="#{matriculaDiscenteOutroProgramaBean.discentes}" var="discente" varStatus="status">

					<c:set var="idLoop" value="${discente.curso.id}" />
					<c:if test="${not empty cursoAtual and discente.graduacao}">
						<c:set var="idLoop" value="${discente.matrizCurricular.id}" />
						<c:if test="${discente.EAD}">
						<c:set var="idLoop" value="${discente.polo.id}" />
						</c:if>
					</c:if>
				<c:if test="${ idFiltro != idLoop}">
					<c:set var="idFiltro" value="${discente.curso.id}" />
					<c:if test="${not empty cursoAtual and discente.graduacao}">
						<c:set var="idFiltro" value="${discente.matrizCurricular.id}" />
						<c:if test="${discente.EAD}">
						<c:set var="idFiltro" value="${discente.polo.id}" />
						</c:if>
					</c:if>
					<tr class="curso">
						<td colspan="5">
							<c:if test="${discente.curso.id gt 0 and empty cursoAtual}">
							${discente.curso.descricao}
							</c:if>
							<c:if test="${!(discente.curso.id gt 0 and empty cursoAtual)}">
							ALUNO ESPECIAL
							</c:if>
						</td>
					</tr>
				</c:if>

				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td width="9%">${discente.matricula}</td>
					<td width="10">
					<c:if test="${discente.idFoto != null}">
					<%-- TODO: Colocar painel com foto --%>
					</c:if>
					</td>
					<td>${discente.nome} 
					<c:if test="${ discente.stricto && !discente.regular }">
						(${ discente.nivelDesc }${ discente.gestoraAcademica == null ? '' : ' - ' }${ discente.gestoraAcademica == null ? '' : discente.gestoraAcademica.nome })
					</c:if>
					</td>
					<td width="8%">${discente.statusString}</td>
					<td align="right" width="2%">
					<%--
						<h:commandButton image="/img/seta.gif"
							action="#{matriculaDiscenteOutroProgramaBean.selecionarDiscente}"
							title="Selecionar Discente">
							<f:setPropertyActionListener target="#{matriculaDiscenteOutroProgramaBean.obj.id}" value="#{discente.id}"/>
						</h:commandButton>
						--%>
						<h:commandLink action="#{matriculaDiscenteOutroProgramaBean.selecionarDiscente}" style="border: 0;" id="botaoDeSelecionarODiscente">
					       <f:param name="idDiscente" value="#{discente.id}"/>
			               <h:graphicImage url="/img/seta.gif" />
						</h:commandLink>
					</td>
				</tr>
				</c:forEach>

				</h:form>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="5" style="text-align: center; font-weight: bold;">
						${fn:length(matriculaDiscenteOutroProgramaBean.discentes)} discente(s) encontrado(s)
					</td>
				</tr>
			</tfoot>
		</table>
	</c:if>
<%--
<script type="text/javascript">
<!--
$('formulario:matriculaDiscente').focus();
//-->
</script>


<script type="text/javascript">
<!--
function isNumeric(valor) {
  return !(/\D/.test(valor));
}

String.prototype.trim = function() { return this.replace(/^\s+|\s+$/, ''); };

function limpaMatricula() {
	var matricula = $('formulario:matriculaDiscente').value;
	if (!isNumeric(matricula)) {
		$('formulario:matriculaDiscente').value = '';
	}

}
//-->
</script>
--%>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>