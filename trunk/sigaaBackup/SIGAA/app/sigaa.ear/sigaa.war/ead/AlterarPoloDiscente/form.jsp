<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Alterar Pólo de Discente de Ensino a Distância</h2>

	<h:messages showDetail="true"></h:messages>
	<br>
	<h:form id="formulario">
	
	<h:outputText value="#{alterarPoloDiscente.create}" />
	<h:inputHidden value="#{alterarPoloDiscente.obj.id}" />
	
	<table class="visualizacao" >
		<tr>
			<th width="20%"> Matrícula: </th>
			<td> ${alterarPoloDiscente.obj.matricula } </td>
		</tr>
		<tr>
			<th> Discente: </th>
			<td> ${alterarPoloDiscente.obj.pessoa.nome } </td>
		</tr>
		<tr>
			<th> Curso: </th>
			<td> ${alterarPoloDiscente.obj.curso.descricao } </td>
		</tr>
		<tr>
			<th> Status: </th>
			<td> ${alterarPoloDiscente.obj.statusString } </td>
		</tr>
		<tr>
			<th> Polo Atual: </th>
			<c:if test="${alterarPoloDiscente.distancia}">
			<td> ${alterarPoloDiscente.obj.polo.cidade} </td>
			</c:if>
			<c:if test="${!alterarPoloDiscente.distancia}">
			<td> ${alterarPoloDiscente.obj.curso.municipio} </td>
			</c:if>
		</tr>
	</table>
	<br />	
	
	<table class="formulario" width="650px">
			<caption class="listagem">Selecione o Novo Pólo Para Este Aluno</caption>

			<tr>
				<th>Pólo</th>
				<td>
					<c:if test="${alterarPoloDiscente.distancia}">
					<h:selectOneMenu id="formaIngresso" value="#{alterarPoloDiscente.polo.id}">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{alterarPoloDiscente.polosCombo}" />
					</h:selectOneMenu>
					<span class="required">&nbsp;</span>
					</c:if>
					<c:if test="${!alterarPoloDiscente.distancia}">
					<h:selectOneMenu id="formaIngresso" value="#{alterarPoloDiscente.curso.id}">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{alterarPoloDiscente.polosCombo}" />
					</h:selectOneMenu>
					<span class="required">&nbsp;</span>
					</c:if>
				</td>
			</tr>

			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Alterar Pólo" action="#{alterarPoloDiscente.chamaModelo}" id="btnAlterar" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alterarPoloDiscente.cancelar}" id="btnCancelar" />
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

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
