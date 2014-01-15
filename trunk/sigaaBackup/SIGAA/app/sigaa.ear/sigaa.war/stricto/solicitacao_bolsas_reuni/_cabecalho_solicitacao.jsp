<tr>
	<th> Edital: </th>
	<td>
		<c:choose>
			<c:when test="${not empty solicitacaoBolsasReuniBean.obj.edital.idArquivoEdital}">
				<h:commandLink action="#{solicitacaoBolsasReuniBean.viewArquivo}" value="#{solicitacaoBolsasReuniBean.obj.edital}" 
				 id="verArquivo" target="_blank"/>
			</c:when>
			<c:otherwise>
				${ solicitacaoBolsasReuniBean.obj.edital }
			</c:otherwise>
		</c:choose>
	
	</td>
</tr>	
<tr>
	<th> Programa de Pós-Graduação: </th>
	<td> <h:outputText value="#{ solicitacaoBolsasReuniBean.obj.programa }"/> </td>
</tr>