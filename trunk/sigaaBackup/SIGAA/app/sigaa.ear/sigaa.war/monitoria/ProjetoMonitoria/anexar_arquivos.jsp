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
			Nesta tela devem ser anexados os arquivos de um Projeto.
			</td>
			<td>
				<%@include file="passos_projeto.jsp"%>
			</td>
		</tr>
	</table>
</div>
<div class="descricaoOperacao">
<table width="100%" id="aviso">
	<tr>
		<td width="40" valign="top"><html:img page="/img/warning.gif"/> </td>
		<td style="text-align: justify">
		<b><font color="black" size="2">Atenção:</font></b>
			Utilize este espaço para enviar o arquivo completo do Projeto de Monitoria caso tenha sido elaborada também em outro formato (Word, Excel, PDF e outros).<br/>
			Utilize-o também para anexar outros documentos que julgar indispensáveis para aprovação e/ou execução
			do Projeto de Monitoria que está sendo cadastrada.<br /> 
			Os campos são obrigatórios caso queira anexar um arquivo.
		<br/>
		<br/>
		</td>
	</tr>
</table>
</div>

<h:form id="formAnexosAtividade" enctype="multipart/form-data">
<table class=formulario width="100%">

	<caption class="listagem">Informe os dados do Arquivo</caption>

	<h:inputHidden value="#{projetoMonitoria.confirmButton}"/>
	<h:inputHidden value="#{projetoMonitoria.obj.id}"/>

	<tr>
		<th width="20%"><b>Título:</b></th>
		<td>
			<h:outputText value="#{projetoMonitoria.obj.titulo}" >
				<f:attribute name="lineWrap" value="100"/>
				<f:converter converterId="convertTexto"/>
			</h:outputText>
		</td>
	</tr>

	<tr>
		<th  class="required"> Descrição:</th>
		<td>
			<h:inputText  id="descricao" value="#{projetoMonitoria.descricaoArquivo}" size="83" maxlength="90"/>
		</td>
	</tr>

	<tr >
		<th  class="required" width="20%">Arquivo:</th>
		<td>
			<t:inputFileUpload id="uFile" value="#{projetoMonitoria.fileArquivo}" storage="file" size="70"/>
		</td>
	</tr>

	<tr style="background: #DEDFE3;">
		<td colspan="2" align="center">
			<h:commandButton action="#{projetoMonitoria.anexarArquivo}" value="Anexar Arquivo" id="btAnexarArqui"/>
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
			<input type="hidden" value="0" id="idArquivoProjeto" name="idArquivoProjeto"/>

		
			<table class="listagem" width="100%">
				
				<thead>
					<tr>
						<td> Descrição do Arquivo </td>
						<td width="5%"></td>
						<td width="5%"></td>
					</tr>
				</thead>

				<c:forEach items="#{ projetoMonitoria.arquivosProjeto }" var="anexo">
					<tr>
						<td width="90%"> ${anexo.descricao} </td>
						<td width="5%">
						 	<a href="${ctx}/verProducao?idProducao=${ anexo.idArquivo }&&key=${ sf:generateArquivoKey(anexo.idArquivo) }" target="_blank" title="Visualizar Arquivo">
						 		<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>
						 	</a>
						</td>
						<td width="5%">
							<h:commandLink action="#{ projetoMonitoria.removeAnexo }" onclick="#{confirmDelete}">
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Arquivo" />
								<f:param name="idArquivo" value="#{ anexo.idArquivo }"/>
								<f:param name="idArquivoProjeto" value="#{ anexo.id }"/>
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
				<h:commandButton value="Gravar Proposta" action="#{ projetoMonitoria.cadastrarParcialDadosGerais }" title="Gravar Proposta para Continuar Depois." id="btGravar" />
				<h:commandButton value="<< Voltar" action="#{projetoMonitoria.passoAnterior}" />
				<h:commandButton value="Cancelar" action="#{projetoMonitoria.cancelar}"   onclick="#{confirm}"/>
				<h:commandButton value="Avançar >> " action="#{projetoMonitoria.submeterArquivos}" />
			</td> 
		</tr>
	</tfoot>

</h:form>

</table>
<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>