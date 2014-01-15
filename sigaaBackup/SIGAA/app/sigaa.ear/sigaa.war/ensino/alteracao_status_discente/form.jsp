<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Alterar Status de Discente</h2>

	<br>
	<h:form id="formulario">
	
	<h:outputText value="#{alteracaoStatusDiscente.create}" />
	<h:inputHidden value="#{alteracaoStatusDiscente.obj.id}" />
	
	<table class="visualizacao">
		<tr>
			<th width="20%"> Matrícula: </th>
			<td> ${alteracaoStatusDiscente.obj.matricula } </td>
		</tr>
		<tr>
			<th> Discente: </th>
			<td> ${alteracaoStatusDiscente.obj.pessoa.nome } </td>
		</tr>
		<tr>
			<th> Curso: </th>
			<td> ${alteracaoStatusDiscente.obj.curso.descricao } </td>
		</tr>
		<tr>
			<th> Status Atual: </th>
			<td> ${alteracaoStatusDiscente.obj.statusString } </td>
		</tr>
		<tr>
			<th> ${alteracaoStatusDiscente.obj.periodoIngresso != null ? 'Período' : 'Ano'} de Ingresso: </th>
			<td> ${alteracaoStatusDiscente.obj.periodoIngresso != null ? alteracaoStatusDiscente.obj.anoPeriodoIngresso : alteracaoStatusDiscente.obj.anoIngresso} </td>
		</tr>
	</table>
	<br />	
	
	<table class="formulario" width="70%">
			<caption class="listagem">Selecione o Novo Status Para Este Discente</caption>

			<tr>
				<th class="obrigatorio">Status</th>
				<td><h:selectOneMenu id="novoStatus" value="#{alteracaoStatusDiscente.status.id}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<c:if test="${acesso.graduacao and alteracaoStatusDiscente.portalGraduacao}">
						<f:selectItems value="#{statusDiscente.allGraduacaoCombo}" />
					</c:if>
					<c:if test="${acesso.stricto and alteracaoStatusDiscente.portalPpg}">
						<f:selectItems value="#{statusDiscente.allStrictoCombo}" />
					</c:if>
					<c:if test="${(acesso.lato and alteracaoStatusDiscente.latoSensu) or (acesso.complexoHospitalar and alteracaoStatusDiscente.portalComplexoHospitalar)}">
						<f:selectItems value="#{statusDiscente.allLatoCombo}" />
					</c:if>
					<c:if test="${acesso.tecnico and alteracaoStatusDiscente.tecnico}">
						<f:selectItems value="#{statusDiscente.allTecnicoCombo}" />
					</c:if>
					<c:if test="${acesso.formacaoComplementar and alteracaoStatusDiscente.formacaoComplementar}">
						<f:selectItems value="#{statusDiscente.allTecnicoCombo}" />
					</c:if>
					<c:if test="${acesso.medio and alteracaoStatusDiscente.medio}">
						<f:selectItems value="#{statusDiscente.allMedioCombo}" />
					</c:if>
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="obrigatorio">Observação</th>
				<td>
					<h:inputTextarea id="observacao" value="#{alteracaoStatusDiscente.observacao}" rows="2" cols="70"/>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<c:set var="exibirApenasSenha" value="true" scope="request"/>
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden value="#{alteracaoStatusDiscente.ignorarPendencias}" id="hiddenIgnorarPendencias"/>
						<h:commandButton value="#{alteracaoStatusDiscente.confirmButton}" action="#{alteracaoStatusDiscente.chamaModelo}" id="btnChamada" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alteracaoStatusDiscente.cancelar}" id="btnCancelar" />
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
