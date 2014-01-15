<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema/> &gt; Enviar Arquivo do Trabalho de Fim de Curso </h2>

<h:form enctype="multipart/form-data">
	<h:inputHidden id="idTrabalho" value="#{trabalhoFimCurso.obj.id}"/>
	<table class="visualizacao">
		<caption> Dados do Trabalho de Fim de Curso </caption>
		<tr>
			<th> Título: </th>
			<td> <h:outputText value="#{trabalhoFimCurso.obj.titulo}"/> </td>
		</tr>
		<tr>
			<th> Orientando: </th>
			<td> 
				<c:if test="${ not empty trabalhoFimCurso.obj.orientando.pessoa.nome}">
					${trabalhoFimCurso.obj.orientando.pessoa.nome}
				</c:if>
				<c:if test="${empty  trabalhoFimCurso.obj.orientando.pessoa.nome}">
					${trabalhoFimCurso.obj.orientandoString}
				</c:if>			
			</td>
		</tr>
		<tr>
			<th> Tipo de Trabalho de Conclusão: </th>
			<td> <h:outputText value="#{trabalhoFimCurso.obj.tipoTrabalhoConclusao.descricao}"/> </td>
		</tr>
		<tr>
			<td style="text-align: right;"> Arquivo: </td>
			<td> 
				<t:inputFileUpload id="arquivoAnexo" value="#{trabalhoFimCurso.arquivo}" size="70"/> 
				<ufrn:help>Informe um arquivo com tamanho máximo de ${trabalhoFimCurso.tamanhoMaximoArquivo} Mb</ufrn:help>
			</td>	
		</tr>
		<tfoot>
		<tr>
			<td colspan="2" align="center">
				 <h:commandButton action="#{trabalhoFimCurso.enviarArquivo}" value="Enviar"/>
				<h:commandButton action="#{trabalhoFimCurso.cancelar}" value="Cancelar" onclick="#{confirm}"/> </td>
		</tr>
		</tfoot>

	</table>
</h:form>
<br><br>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>