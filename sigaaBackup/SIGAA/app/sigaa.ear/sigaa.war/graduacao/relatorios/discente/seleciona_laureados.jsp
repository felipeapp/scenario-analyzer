<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.DAE, SigaaPapeis.PORTAL_PLANEJAMENTO , SigaaPapeis.ADMINISTRADOR_DAE} ); %>

<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDocente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Lista de Alunos Laureados</h2>
<h:form id="form">
<table align="center" class="formulario" width="70%">
	<caption>Dados do Relat�rio</caption>
	<tr>
		<th>Ano-Per�odo: </th>
		<td><h:selectOneMenu value="#{relatorioDiscente.ano}"
					id="ano">
					<f:selectItems value="#{relatorioDiscente.anos}" />
				</h:selectOneMenu>.<h:selectOneMenu value="#{relatorioDiscente.periodo}"
					id="periodo">
					<f:selectItems value="#{relatorioDiscente.periodos}" />
				</h:selectOneMenu></td>
	</tr>
	
	<tr>
		<th>�ndice Acad�mico: </th>
		<td>
			<h:selectOneMenu value="#{relatorioDiscente.indiceSelecionado.id}" id="indice">
				<f:selectItems value="#{relatorioDiscente.indicesLaureados}" />
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tfoot>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio"
				action="#{relatorioDiscente.gerarRelatorioListaAlunoLaureados}"/>
			<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" 
				 onclick="return confirm('Deseja cancelar a opera��o?');" />
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>