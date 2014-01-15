<%-- pagina que representa uma aba da pagina de informações completas de um título --%>

<style type="text/css">
	
	<!--
	
	/* coloca o fundo da tabela com os dados transparente para pegar a color da linha da tabela mais esterna */
	
	.tabelaTransparete {
		background : transparent;
	}

	.tabelaTransparete tr td{
		background : transparent;
	}

	table.tabelaTransparete tbody{
		background : transparent;
	}


	-->
</style>



	<table width="100%"  class="tabelaTransparete">
	
		<tbody>
			<%--  
			<tr>
				<th colspan="2" style="text-align:left;padding-top: 5px"> Nº do Sistema: </th>
				<td colspan="2" style="padding-top: 5px"> ${geraInformacoesBibliograficasTituloMBean.obj.numeroDoSistema}</td>
			</tr> --%>
		
			<tr>
				<th colspan="2" style="text-align:left;padding-top: 5px;color:green;"> FMT </th>
				<td colspan="2" style="padding-top: 5px;color:blue"> ${geraInformacoesBibliograficasTituloMBean.obj.formatoMaterial.descricaoCompleta}</td>
			</tr>
			
	
			<%-- Os Campos de Controle do Titulo  --%>
	
			<c:forEach items="#{geraInformacoesBibliograficasTituloMBean.obj.camposControleOrdenadosByEtiqueta}"  var="campoControle" varStatus="loop">
					<tr>
						<td colspan="2" style="color:green;padding-top: 5px"> ${campoControle.etiqueta.tag} </td>
						<td colspan="2" style="color:blue;padding-top: 5px"> ${campoControle.dadoParaExibicao}</td>
					</tr>
			
			</c:forEach>
		
	
	
			<%-- Os Campos de Dados do Titulo  --%>	
	
			<c:forEach items="#{geraInformacoesBibliograficasTituloMBean.obj.camposDadosOrdenadosByEtiqueta}"  var="campoDados" varStatus="loop">
					
					<%-- Tudo numa linha só igual ao aleph : Título - 10011 $aautor$bautorsecundario--%>
					<tr>
	
						<td colspan="2" style="color:green;padding-top: 5px; width: 40%;">  ${campoDados.etiqueta.tag}	
							<c:if test="${campoDados.indicador1 == ' '}">
								&nbsp
							</c:if> 
							<c:if test="${campoDados.indicador1 != ' '}">
								${campoDados.indicador1}
							</c:if>
							
							<c:if test="${campoDados.indicador2 == ' '}">
								&nbsp
							</c:if> 
							<c:if test="${campoDados.indicador2 != ' '}">
								${campoDados.indicador2}
							</c:if>		
						</td>
	
						
						<td colspan="2" style="color:blue;padding-top: 5px">
	
							<c:forEach items="#{campoDados.subCampos}"  var="subCampo" varStatus="loop2">
									
									<%-- o $$ nao é erro, é para aparecer assim mesmo $a $b $c, etc... --%>				
									$${subCampo.codigo} ${subCampo.dado}
								
							</c:forEach>
				
						</td>
	
					</tr>
					
			
			</c:forEach>
	
		</tbody>
	
	</table>
