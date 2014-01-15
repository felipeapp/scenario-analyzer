<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
 	<h2><ufrn:subSistema /> > ${relatorioPorDepartamento.titulo}</h2>

	<h:form id="form">
		<h:inputHidden value="#{relatorioPorDepartamento.tipoRelatorio}" />
		<h:inputHidden value="#{relatorioPorDepartamento.titulo}" />
	
		<table align="center" class="formulario" width="95%">
			<caption>Critérios de Consulta</caption>


			<tr>
				<th ${relatorioPorDepartamento.unidadeSelecionavel ? 'class="required"' : 'class="rotulo"'}>Unidade:</th>
				<td>
					<h:inputHidden value="#{relatorioPorDepartamento.unidade.id}" rendered="#{!relatorioPorDepartamento.unidadeSelecionavel}" />				
					<h:outputText value="#{relatorioPorDepartamento.unidade.codigoNome}" rendered="#{!relatorioPorDepartamento.unidadeSelecionavel}" />				
					
					<h:selectOneMenu id="unidades" value="#{relatorioPorDepartamento.unidade.id}"  style="width: 95%" rendered="#{relatorioPorDepartamento.unidadeSelecionavel}">
						<c:choose>
							<c:when test="${relatorioPorDepartamento.relatorioComponenteComPrograma}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA UNIDADE ACADÊMICA --" />
							</c:when>
							<c:otherwise>
								<f:selectItem itemValue="0" itemLabel="-- TODAS --" />
							</c:otherwise>
						</c:choose>
						<f:selectItems value="#{unidade.allDetentorasComponentesCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<c:if test="${relatorioPorDepartamento.relatorioComponenteComPrograma}">
			<tr>
				<th width="25%"> Ano-Período: </th>
				<td>
					<h:inputText value="#{relatorioPorDepartamento.ano}" id="ano" size="4" maxlength="4" onkeyup="return formatarInteiro(this)" converter="#{ intConverter }" /> .
					<h:inputText value="#{relatorioPorDepartamento.periodo}" id="periodo" size="1" maxlength="1" onkeyup="return formatarInteiro(this)" converter="#{ intConverter }" />
				</td>
			</tr>
			</c:if>
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Gerar Relatório" id="gerarRelatorio"
						action="#{relatorioPorDepartamento.gerarRelatorio}" />
					<h:commandButton value="Cancelar" id="cancelar" onclick="#{confirm}"
						action="#{relatorioPorDepartamento.cancelar}" />
				</td>
			</tr>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
