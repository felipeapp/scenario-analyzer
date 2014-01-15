<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.direita {
	text-align: right;
}

.esquerda {
	text-align: left;
}
</style>

<f:view>
<h2> <ufrn:subSistema /> > Registro Coletivo de ${registroDiplomaColetivo.obj.livroRegistroDiploma.tipoRegistroDescricao } </h2>

<table class="visualizacao" width="100%">
	<caption>Dados do Registro Coletivo</caption>
	<tbody>
		<tr>
			<th width="25%"> Curso: </th>
			<td>
				<h:outputText value="#{registroDiplomaColetivo.obj.curso.descricao}"/>
			</td>
		</tr>
		<tr>
			<th width="25%"> Livro: </th>
			<td>
				<h:outputText value="#{registroDiplomaColetivo.obj.livroRegistroDiploma.titulo}"/>
			</td>
		</tr>
		<tr>
			<th> Processo: </th>
			<td>
				<h:outputText value="#{registroDiplomaColetivo.obj.processo}"/>
			</td>
		</tr>
		<tr>
			<th>
				<c:choose>
					<c:when test="${acesso.graduacao}">Data da Colação:</c:when>
					<c:otherwise>Data de Conclusão:</c:otherwise> 
				</c:choose>
			</th>
			<td>
				<fmt:formatDate value="${registroDiplomaColetivo.obj.dataColacao}" pattern="dd/MM/yyyy"/>
			</td>
		</tr>
		<tr>
			<th> Data do Registro: </th>
			<td >
				<fmt:formatDate value="${registroDiplomaColetivo.obj.dataRegistro}" pattern="dd/MM/yyyy"/>
			</td>
		</tr>
		<tr>
			<th> Data de Expedição: </th>
			<td >
				<fmt:formatDate value="${registroDiplomaColetivo.obj.dataExpedicao}" pattern="dd/MM/yyyy"/>
			</td>
		</tr>
	</tbody>
</table>
<c:if test="${not empty registroDiplomaColetivo.obj.registrosDiplomas}">
<br>
<table width="100%">
	<thead>
		<tr>
			<td class="subFormulario" colspan="6">
				${registroDiplomaColetivo.obj.livroRegistroDiploma.tipoRegistroDescricao} Registrados: (${fn:length(registroDiplomaColetivo.obj.registrosDiplomas) })
			</td>
		</tr>
		<tr>
			<td style="text-align: center;">Matricula</td>
			<td style="text-align: left;">Nome</td>
			<td style="text-align: left;">Livro</td>
			<td style="text-align: right;">Folha</td>
			<td style="text-align: right;">Nº do<br>Registro</td>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="#{registroDiplomaColetivo.obj.registrosDiplomas}" var="registro">
		<tr valign="top">
			<td style="text-align: center;">${registro.discente.matricula}</td>
			<td style="text-align: left;">${registro.discente.nome}</td>
			<td style="text-align: left;">${registro.livroRegistroDiploma.titulo}</td>
			<td style="text-align: right;">${registro.folha.numeroFolha}</td>
			<td style="text-align: right;">${registro.numeroRegistro}</td>
		</tr>
		</c:forEach>
	</tbody>
</table>
</c:if>
<table class="formulario" width="100%">
	<tfoot>
		<tr>
			<td colspan="2" align="center">
			<h:form>
				<h:commandButton value="Imprimir a Lista de Registro" action="#{registroDiplomaColetivo.formComprovanteColetivo}" id="registroDiplomaColetivo_formComprovanteColetivo"/>
				<h:commandButton value="Imprimir os Diplomas" action="#{impressaoDiploma.gerarDiplomaColetivo}" id="impressaoDiploma_gerarDiplomaColetivo" rendered="#{not empty impressaoDiploma.listaIdDiscente}"/>
				<h:commandButton value="<< Voltar" action="#{registroDiplomaColetivo.formBuscaCurso}"  rendered="#{registroDiplomaColetivo.exibirVoltar}"  id="registroDiplomaColetivo_formBuscaCurso"/>
				<h:commandButton value="<< Voltar" action="#{impressaoDiploma.telaResultadoBuscaColetivo}" id="impressaoDiploma_telaResultadoBuscaColetivo" rendered="#{not empty impressaoDiploma.listaIdDiscente}"/>
				<h:commandButton value="Cancelar" action="#{registroDiplomaColetivo.cancelar}" onclick="#{confirm}" id="registroDiplomaColetivo_cancelar"/>
			</h:form>
			</td>
		</tr>
	</tfoot>
</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>