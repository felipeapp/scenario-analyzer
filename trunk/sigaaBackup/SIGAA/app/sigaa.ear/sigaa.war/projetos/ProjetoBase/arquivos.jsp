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
				Nesta tela devem ser anexados os arquivos do Projeto.
			</td>
			<td>
				<%@include file="passos_projeto.jsp"%>
			</td>
		</tr>
	</table>
</div>

<div class="descricaoOperacao">
	<table width="80%" id="aviso">
		<tr>
			<td width="40" valign="top"><html:img page="/img/warning.gif"/> </td>
			<td style="text-align: justify">
			<b>Atenção:</b>
				Utilize este espaço para enviar o arquivo completo da Proposta de Ação Acadêmica Integrada
				caso tenha sido elaborada também em outro formato (Word, Excel, PDF ou outros).<br/>
				Utilize-o também para anexar outros documentos que julgar indispensáveis para aprovação e/ou execução
				da Ação Acadêmica que está sendo cadastrada. Os campos são obrigatórios caso queira anexar um arquivo.
			<br/>
			<br/>
			</td>
		</tr>
	</table>
</div>

<h:form id="frmArquivos" enctype="multipart/form-data">
	<table class=formulario width="100%"">
	
		<caption>Informe os dados do Arquivo</caption>	
		<h:inputHidden value="#{projetoBase.confirmButton}"/>
		<h:inputHidden value="#{projetoBase.obj.id}"/>
	
        <tr>	
	       <td>
	           <table width="100%">
				<tr>
					<th width="20%"> Título:</th>
					<td>
						<b><h:outputText id="titulo" value="#{projetoBase.obj.titulo}"/></b>
					</td>
				</tr>
			
				<tr>
					<th  class="required"> Descrição:</th>
					<td>
						<h:inputText  id="descricao" value="#{projetoBase.descricaoArquivo}" size="83" maxlength="90"/>
					</td>
				</tr>
			
				<tr>
					<th  class="required" width="20%">Arquivo:</th>
					<td>
						<t:inputFileUpload id="uFile" value="#{projetoBase.file}" storage="file" size="70"/>
					</td>
				</tr>
			
			     <tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton	action="#{projetoBase.anexarArquivo}" value="Anexar Arquivo" id="btAnexarArqui"/>
						</td>
					</tr>
				</tfoot>				
			</table>	
         </td>
       </tr>		
		
		
		<tr>
			<td colspan="2">
				<div class="infoAltRem">
					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar	    		
		    		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover	    		
				</div>
			</td>		
		</tr>
	
		<tr>
			<td colspan="2" class="subFormulario">	Lista de arquivos anexados com sucesso </td>
		</tr>
	
		<tr>
			<td colspan="2">
				<t:dataTable id="dtArquivos" value="#{projetoBase.obj.arquivos}" var="anexo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					<t:column>
						<f:facet name="header"><f:verbatim>Descrição do Arquivo</f:verbatim></f:facet>
						<h:outputText value="#{anexo.descricao}" />
					</t:column>
	
					<t:column width="5%">
						<h:commandLink action="#{projetoBase.viewArquivo}" title="Visualizar"	id="verArquivo" target="blank">
		                      <f:param name="idArquivo" value="#{anexo.idArquivo}" />
                              <h:graphicImage url="/img/view.gif" />
                        </h:commandLink>																		
						<h:commandLink action="#{projetoBase.removeArquivo}" title="Remover"
						   onclick="return confirm('Deseja Remover este Arquivo do Projeto?')" id="remArquivo">												      
                              <f:param name="idArquivo" value="#{anexo.idArquivo}" />
                              <f:param name="idArquivoProjeto" value="#{anexo.id}" />
                              <h:graphicImage url="/img/delete.gif" />
                        </h:commandLink>
					</t:column>					
				</t:dataTable>				
			</td>
		</tr>
		
		<c:if test="${empty projetoBase.obj.arquivos}">
              <tr><td colspan="2" align="center"><font color="red">Não há arquivos cadastrados</font> </td></tr>
        </c:if>
	
		<tfoot>
		<tr> <td colspan=2>
			<h:commandButton value="<< Voltar" action="#{projetoBase.passoAnterior}" id="btPassoAnteriorArquivos" />
			<h:commandButton value="Cancelar" action="#{projetoBase.cancelar}" onclick="#{confirm}" id="btCancelar"/>
			<h:commandButton value="Gravar e Avançar >>" action="#{projetoBase.submeterArquivos}" id="btSubmeterArquivos"/>
		</td> </tr>
		</tfoot>
	</table>
</h:form>
<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>