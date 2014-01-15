<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2> <ufrn:subSistema /> > Associar Alunos a Tutor </h2>

<f:view>

	<h:form id="formBotoesSuperiores">
	<table align="center">
	<tr>
		<td style="padding-left: 15px" valign="top" >
			<table class="botoes">
			<tr><td align="center"><h:commandButton title="Alterar Tutor" image="/img/graduacao/mat_alteracao_solicitacoes.png"
				 action="#{tutoriaAluno.telaTutor}" /></td></tr>
			<tr><td>
			<h:commandLink action="#{tutoriaAluno.telaTutor}" value="Alterar Tutor"/>
			</td></tr>
			</table>
		</td>
		
		<td style="padding-left: 15px" valign="top" >
			<table class="botoes">
			<tr><td align="center"><h:commandButton title="Buscar Discentes" image="/img/graduacao/add_extra_curriculo.png"
				 action="#{tutoriaAluno.telaBusca}" /></td></tr>
			<tr><td>
			<h:commandLink action="#{tutoriaAluno.telaBusca}" value="Buscar Discentes"/>
			</td></tr>
			</table>
		</td>
	</tr>
	</table>
	</h:form>
	<br/><br/>


	<table width="100%" class="visualizacao">
		<tr>
			<th width="30%">Tutor Orientador:</th>
			<td><h:outputText value="#{tutoriaAluno.tutor.nome}"/></td>
		</tr>
	</table>
	
	<br/><br/>

	<table class="formulario" width="90%">
		<caption> Discente Selecionados </caption>
		
		<c:if test="${not empty tutoriaAluno.discentes}">
		<c:forEach var="disc" items="${tutoriaAluno.discentes}" varStatus="status">
			<tr class="${ status.index % 2 == 0 ? "linhaPar":"linhaImpar" }">
				<td>${disc}</td>
				<h:form>
					<td width=25>
						<input type="hidden" value="${disc.id}" name="id" /> 
						<h:commandButton image="/img/delete.gif" styleClass="noborder" alt="Remover" action="#{tutoriaAluno.removerDiscente}" />
					</td>
				</h:form>
			</tr>
		</c:forEach>
		</c:if>
		
		<c:if test="${empty tutoriaAluno.discentes}">
		<tr>
			<td align="center"><font color="red">Nenhum Discente Selecionado</font></td>
		</tr>
		</c:if>
		
		<h:form id="formBotoesForm">
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="Confirmar" action="#{ tutoriaAluno.cadastrar }"/> 
				<h:commandButton value="Cancelar" action="#{ tutoriaAluno.cancelar }" onclick="#{confirm }"/>
			</td></tr>
		</tfoot>
		</h:form>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>