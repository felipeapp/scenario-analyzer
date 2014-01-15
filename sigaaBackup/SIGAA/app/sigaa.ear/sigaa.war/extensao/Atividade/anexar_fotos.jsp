<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>

<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Anexar Fotos</h2>



<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="50%">
			Nesta tela devem ser anexados as fotos de uma Ação.
			</td>
			<td>
				<%@include file="passos_atividade.jsp"%>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<p><strong>OBSERVAÇÃO:</strong> Os dados informados só são cadastrados na base de dados quando clica-se em "Avançar >>".</p> 
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
			Neste espaço você pode ou não enviar uma foto ou qualquer outra imagem que julgar importante para aprovação e/ou execução
			da Ação de Extensão que está sendo cadastrada.<br /> 
			Os campos são obrigatórios caso queira anexar uma foto.			
		<br/>
		<br/>
		</td>
	</tr>
</table>
</div>

<h:form id="formAnexosAtividade" enctype="multipart/form-data">
<table class=formulario width="100%">

	<caption class="listagem">Informe os dados do arquivo de foto </caption>

	<h:inputHidden value="#{atividadeExtensao.confirmButton}"/>
	<h:inputHidden value="#{atividadeExtensao.obj.id}" id="id"/>


	<tr>
		<th width="30%"><b>Ano - Título:</b></th>
		<td>
			<h:outputText value="#{atividadeExtensao.obj.anoTitulo}">
				<f:attribute name="lineWrap" value="100"/>
				<f:converter converterId="convertTexto"/>
			</h:outputText>
		</td>
	</tr>

	<tr>
		<th  class="required"> Descrição:</th>
		<td>
			<h:inputText  id="descricao" value="#{atividadeExtensao.descricaoFoto}" size="83" maxlength="90"/>
		</td>
	</tr>

	<tr>
		<th  class="required">Arquivo de Foto:</th>
		<td>
			<t:inputFileUpload id="uFile" value="#{atividadeExtensao.foto}" storage="file" size="60"/>
		</td>
	</tr>

	<tr style="background: #DEDFE3;">
		<td colspan="2" align="center">
			<h:commandButton action="#{atividadeExtensao.anexarFoto}" value="Anexar Foto" id="btAnexarFoto"/>
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			<br/>
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Foto	    		
	    		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Foto	    		
			</div>
		</td>		
	</tr>

	<tr>
		<td colspan="2" class="subFormulario">	Lista de fotos da ação de extensão </td>
	</tr>

	<tr>
		<td colspan="2">
			<%--As três entradas abaixo não interferem na inserção. São usadas na remoção.--%>
			<input type="hidden" value="0" id="idFotoOriginal" name="idFotoOriginal"/>
			<input type="hidden" value="0" id="idFotoMini" name="idFotoMini"/>
			<input type="hidden" value="0" id="idFotoProjeto" name="idFotoProjeto"/>

			<t:dataTable id="dataTableFotos" value="#{atividadeExtensao.fotosProjeto}" var="foto" align="center" 
				width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">

				<t:column>
					<f:facet name="header"><f:verbatim>Foto</f:verbatim></f:facet>
					<f:verbatim >
						<div class="foto">							
							<h:graphicImage url="/verFoto?idFoto=#{foto.idFotoMini}&key=#{sf:generateArquivoKey(foto.idFotoMini)}" width="70" height="70"/>
						</div>
					</f:verbatim>
				</t:column>

				<t:column>
					<f:facet name="header"><f:verbatim>Descrição da Foto</f:verbatim></f:facet>
					<h:outputText value="#{foto.descricao}" />
				</t:column>


				<t:column>
						<h:outputLink value="/sigaa/verFoto?idFoto=#{foto.idFotoOriginal}&key=#{sf:generateArquivoKey(foto.idFotoOriginal)}" id="link_verfoto_original_" title="Visualizar Foto">
							<h:graphicImage url="/img/view.gif" />  
						</h:outputLink>						
				</t:column>
				
				<t:column>
					<h:commandButton image="/img/delete.gif" action="#{atividadeExtensao.removeFoto}"
						title="Remover Foto"  alt="Remover Foto"   onclick="$(idFotoMini).value=#{foto.idFotoMini};$(idFotoOriginal).value=#{foto.idFotoOriginal};$(idFotoProjeto).value=#{foto.id};return confirm('Deseja Remover esta Foto da Ação de Extensão?')" id="remFoto" />
				</t:column>
				
			</t:dataTable>
		</td>
	</tr>

	<tfoot>
	<tr> <td colspan=2>
		<h:commandButton value="<< Voltar" action="#{atividadeExtensao.passoAnterior}" />
		<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}"   onclick="#{confirm}"/>
		<h:commandButton value="Avançar >> " action="#{atividadeExtensao.submeterFotos}" />
	</td> </tr>
	</tfoot>

</h:form>
</table>
<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>