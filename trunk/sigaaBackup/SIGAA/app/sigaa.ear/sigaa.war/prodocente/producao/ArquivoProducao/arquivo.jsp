<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:inputHidden value="#{producao.dados.id}"/>
<h2> Enviar Arquivo para Produção </h2>

<div id="ajuda" class="descricaoOperacao" style="text-align: justify;">
	<ul>
		<li> <b>Exibir parte pública</b>: Caso seja do interesse do usuário que o arquivo anexado seja exibido na parte pública, o que possibilita que os qual quer 
			pessoa possa realizar o download do arquivo. </li>
	</ul>
	
</div>
	
<h:form enctype="multipart/form-data">

	<table class="formulario" width="550px">
		<caption> Dados da Produção </caption>
		<tbody>
			<tr>
				<th class="rotulo" width="150px"> Titulo: </th>
				<td width="400px"> <h:outputText value="#{producao.dados.titulo}"/> </td>
			</tr>
			<tr>
				<th class="rotulo" width="150px"> Area: </th>
				<td> <h:outputText value="#{producao.dados.area.nome}"/> </td>
			</tr>
			<tr>
				<th class="rotulo" width="150px"> Tipo de Participação: </th>
				<td> <h:outputText value="#{producao.dados.tipoParticipacao.descricao}"/> </td>
			</tr>
			<tr>
				<th class="rotulo" width="150px"> Exibir parte pública: </th>
				<td>  <h:selectBooleanCheckbox value="#{producao.dados.exibir}" /> </td>
			</tr>
			<tr>
				<td colspan="2"> 
					<table class="subFormulario" width="100%">
						<tr>
							<th width="150px"> Arquivo:</th>
							<td width="255px">
								<t:inputFileUpload value="#{producao.arquivo}"  size="30" />
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
				 <h:commandButton action="#{producao.enviarArquivo}" value="Enviar" id="enviar"/>
				 <h:commandButton action="#{producao.cancelArquivo}" value="<< Voltar" id="voltar" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
<br><br>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>