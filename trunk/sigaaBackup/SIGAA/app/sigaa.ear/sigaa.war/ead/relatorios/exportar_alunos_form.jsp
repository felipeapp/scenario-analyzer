<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Relat�rio de Alunos por P�lo</h2>
	<h:outputText value="#{exportarAlunosEad.create}" />
	<h:messages showDetail="true"/>
	<h:form id="busca">
		<table class="formulario" width="70%">
			<caption>Dados do Relat�rio</caption>
			<tbody>
				<tr>
					<td>P�lo: </td>
					<td>
						<h:selectOneMenu value="#{exportarAlunosEad.polo.id}">
						<f:selectItem itemValue="0" itemLabel="Escolha um p�lo" />
						<f:selectItems value="#{tutorOrientador.polos}" />
						</h:selectOneMenu> 
					</td>
				</tr>
				<tr>
					<td>Curso: </td>
					<td>
						<h:selectOneMenu value="#{exportarAlunosEad.curso.id}">
						<f:selectItem itemValue="0" itemLabel="Escolha um curso" />
						<f:selectItems value="#{tutorOrientador.cursos}" />
						</h:selectOneMenu> 
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{exportarAlunosEad.gerar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{exportarAlunosEad.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
