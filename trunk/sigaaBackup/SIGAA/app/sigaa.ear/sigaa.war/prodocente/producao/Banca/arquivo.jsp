<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:inputHidden value="#{producao.dados.id}"/>
<h2> Enviar Arquivo para Produção </h2>
<h:form enctype="multipart/form-data">
	<table class="formulario" width="550px">
		<caption> Dados da Produção </caption>
		<tbody>
			<tr>
				<th class="rotulo" width="150px"> Titulo: </th>
				<td width="400px"> <h:outputText value="#{banca.dados.titulo}"/> </td>
			</tr>
			<tr>
				<th class="rotulo" width="150px"> Area: </th>
				<td> <h:outputText value="#{banca.dados.area.nome}"/> </td>
			</tr>
			<tr>
				<th class="rotulo" width="150px"> Ano: </th>
				<td> <h:outputText value="#{banca.dados.anoReferencia}"/> </td>
			</tr>
			<tr>
				<th class="rotulo" width="150px"> Exibir parte pública: </th>
				<td>  <h:selectBooleanCheckbox value="#{banca.dados.exibir}" /> </td>
			</tr>
			<tr>
				<td colspan="2"> 
					<table class="subFormulario" width="100%">
						<tr>
							<th width="150px"> Arquivo:</th>
							<td width="255px">
								<t:inputFileUpload value="#{banca.arquivo}"  size="30" />
							</td>
							<td>
								<ufrn:help>Informe um arquivo com tamanho máximo de ${producao.tamanhoMaximoArquivo} Mb</ufrn:help>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2" align="center">
				 <h:commandButton action="#{banca.enviarArquivo}" value="Enviar" id="enviar"/>
				 <h:commandButton action="#{banca.cancelArquivo}" value="<< Voltar" id="voltar" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
<br><br>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>