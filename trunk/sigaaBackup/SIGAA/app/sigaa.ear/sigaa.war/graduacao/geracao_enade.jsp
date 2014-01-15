<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2><ufrn:subSistema/> -
Gera��o de Arquivo do ENADE</h2>
<br>
	<h:form id="formulario">
		<table class="formulario" >
			<caption>Matr�culas</caption>
			<tr>
				<th>
					Tipo de discente:
				</th>
				<td>
					<h:selectOneMenu value="#{gerarArquivoENADE.tipo}" id="tipo">
						<f:selectItem itemValue="Ingressante"/>
						<f:selectItem itemValue="Concluinte"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th valign="top">
					Matr�culas para gera��o do arquivo:<br/>
					(separadas por espa�o)
				</th>
				<td>
					<h:inputTextarea value="#{gerarArquivoENADE.matriculas}" rows="20" cols="15" id="matricula"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
					 	<h:commandButton action="#{gerarArquivoENADE.gerarArquivo}" value="Gerar Arquivo" id="btnArquivos"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>