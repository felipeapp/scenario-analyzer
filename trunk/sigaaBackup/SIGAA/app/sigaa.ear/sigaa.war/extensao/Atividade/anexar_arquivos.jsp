<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>

<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Anexar Arquivos</h2>


<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="50%">
			Nesta tela devem ser anexados os arquivos de uma A��o.
			</td>
			<td>
				<%@include file="passos_atividade.jsp"%>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<p><strong>OBSERVA��O:</strong> Os dados informados s� s�o cadastrados na base de dados quando clica-se em "Avan�ar >>".</p> 
			</td>
		</tr>
	</table>
</div>
<div class="descricaoOperacao">
<table width="100%" id="aviso">
	<tr>
		<td width="40" valign="top"><html:img page="/img/warning.gif"/> </td>
		<td style="text-align: justify">
		<b><font color="black" size="2">Aten��o:</font></b>
			Utilize este espa�o para enviar o arquivo completo da Proposta da A��o
			de Extens�o caso tenha sido elaborada tamb�m em outro formato (Word, Excel, PDF e outros).<br/>
			Utilize-o tamb�m para anexar outros documentos que julgar indispens�veis para aprova��o e/ou execu��o
			da A��o de Extens�o que est� sendo cadastrada.<br /> 
			Os campos s�o obrigat�rios caso queira anexar um arquivo.
		<br/>
		<br/>
		</td>
	</tr>
</table>
</div>

<h:form id="formAnexosAtividade" enctype="multipart/form-data">
<table class=formulario width="100%">

	<caption class="listagem">Informe os dados do Arquivo</caption>

	<h:inputHidden value="#{atividadeExtensao.confirmButton}"/>
	<h:inputHidden value="#{atividadeExtensao.obj.id}"/>

	<tr>
		<th width="20%"><b>T�tulo:</b></th>
		<td>
			<h:outputText value="#{atividadeExtensao.obj.titulo}" >
				<f:attribute name="lineWrap" value="100"/>
				<f:converter converterId="convertTexto"/>
			</h:outputText>
		</td>
	</tr>

	<tr>
		<th  class="required"> Descri��o:</th>
		<td>
			<h:inputText  id="descricao" value="#{atividadeExtensao.descricaoArquivo}" size="83" maxlength="90"/>
		</td>
	</tr>

	<tr >
		<th  class="required" width="20%">Arquivo:</th>
		<td>
			<t:inputFileUpload id="uFile" value="#{atividadeExtensao.file}" storage="file" size="70"/>
		</td>
	</tr>

	<tr style="background: #DEDFE3;">
		<td colspan="2" align="center">
			<h:commandButton action="#{atividadeExtensao.anexarArquivo}" value="Anexar Arquivo" id="btAnexarArqui"/>
		</td>
	</tr>
	</h:form>
	<h:form>
	<tr>
		<td colspan="2">
			<br/>
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Arquivo	    		
	    		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Arquivo	    		
			</div>
		</td>		
	</tr>

	<tr>
		<td colspan="2" class="subFormulario">	Lista de Arquivos anexados com sucesso </td>
	</tr>

	<tr>
		<td colspan="2">			
			<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>
			<input type="hidden" value="0" id="idArquivoExtensao" name="idArquivoExtensao"/>
			<table class="listagem" width="100%">
				
				<thead>
					<tr>
						<td> Descri��o do Arquivo </td>
						<td width="5%"></td>
						<td width="5%"></td>
					</tr>
				</thead>

				<c:forEach items="#{ atividadeExtensao.arquivosProjeto }" var="anexo">
					<tr>
						<td width="90%"> ${anexo.descricao} </td>
						<td width="5%">
						 	<a href="${ctx}/verProducao?idProducao=${ anexo.idArquivo }&&key=${ sf:generateArquivoKey(anexo.idArquivo) }" target="_blank" title="Visualizar Arquivo">
						 		<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>
						 	</a>
						</td>
						<td width="5%">
							<h:commandLink action="#{ atividadeExtensao.removeAnexo }"
									onclick="$(idArquivo).value=#{anexo.idArquivo};$(idArquivoExtensao).value=#{anexo.id};
									return confirm('Deseja remover este anexo?')">
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Arquivo" />
							<!-- 	<f:param name="idArquivo" value="#{ anexo.idArquivo }"/>
								<f:param name="idArquivoExtensao" value="#{ anexo.id }"/> -->
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>

	<tfoot>
		<tr> 
			<td colspan=2>
				<h:commandButton value="<< Voltar" action="#{atividadeExtensao.passoAnterior}" />
				<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}"   onclick="#{confirm}"/>
				<h:commandButton value="Avan�ar >> " action="#{atividadeExtensao.submeterArquivos}" />
			</td> 
		</tr>
	</tfoot>

</h:form>

</table>
<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> </center><br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>