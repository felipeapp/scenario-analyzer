<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<ufrn:checkRole
	papeis="<%= new int[] { SigaaPapeis.GESTOR_MONITORIA,  SigaaPapeis.MEMBRO_COMITE_INTEGRADO } %>">

<f:view>

	<h2><ufrn:subSistema /> > Alterar Situação do Projeto de Ensino</h2>
	<br>
	<h:form id="form">

		<h:outputText value="#{projetoMonitoria.create}" />
		<h:inputHidden value="#{projetoMonitoria.confirmButton}" />
		<h:inputHidden value="#{projetoMonitoria.obj.id}" />

		<table class="formulario" width="100%">
			<caption class="listagem">Alterar Situação do Projeto de
			Ensino</caption>

			<tr>
				<th width="18%">Projeto de Ensino:</th>
				<td><b><h:outputText value="#{projetoMonitoria.obj.titulo}" /></b></td>
			</tr>
			<tr>
				<th>Ano:</th>
				<td><b><h:outputText value="#{projetoMonitoria.obj.ano}" /></b></td>
			</tr>

			<tr>
				<th>Situação:</th>
				<td><h:selectOneMenu
					value="#{projetoMonitoria.obj.situacaoProjeto.id}">
					<f:selectItems value="#{projetoMonitoria.tipoSituacaoProjetoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<td><br />
				</td>
			</tr>


			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{projetoMonitoria.confirmButton}"
						action="#{projetoMonitoria.alterarSituacaoProjeto}"/> 
						<h:commandButton value="Cancelar" action="#{projetoMonitoria.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>

	</table>
</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>