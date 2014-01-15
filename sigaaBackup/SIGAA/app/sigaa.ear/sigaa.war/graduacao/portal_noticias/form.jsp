<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.portal.jsf.NoticiaPortalMBean"%>
<f:view>
<h2> <ufrn:subSistema /> > Cadastro de Notícia no Portal</h2>
	<h:outputText value="#{ noticiaPortalDiscente.create }"/>
	<h:form id="formNoticia" enctype="multipart/form-data">
	<h:inputHidden value="#{noticiaPortalDiscente.confirmButton}" />
	<h:inputHidden value="#{noticiaPortalDiscente.obj.id}" />
	<div id="ajuda" class="descricaoOperacao">
		<p>
			<strong>Bem-vindo ao Cadastro de Notícia</strong>
		</p>
		<br/>
		<p>A noticia irá ser exibida no portal do discente somente para os alunos do  
			<c:if test="${not empty noticiaPortalDiscente.curso.descricao}">curso de ${noticiaPortalDiscente.curso.descricao}.</c:if>
			<c:if test="${empty noticiaPortalDiscente.programa.nome}">programa ${noticiaPortalDiscente.programa.nome}.	</c:if>
		</p>			
		<p>
			Somente as <%= NoticiaPortalMBean.QTD_NOTICIAS_CURSO_PROGRAMA %> últimas notícias cadastradas com status <i>publicada</i> serão listadas.
		</p>
	</div>
	
	<table class="formulario" style="width: 100%;">
		<caption>Cadastro de Notícias para o Portal</caption>
			<tr>
				<td> </td>
				<td align="right" class="required">Título da Notícia:&nbsp;&nbsp;</td>
				<td><h:inputText value="#{noticiaPortalDiscente.obj.titulo}" readonly="#{noticiaPortalDiscente.readOnly}" style="width:95%;"/></td>
			</tr>
			<tr>
				<td> </td>
				<td align="right" valign="top" class="required">Corpo da Notícia:&nbsp;&nbsp;</td>
				<td><h:inputTextarea value="#{noticiaPortalDiscente.obj.descricao}" readonly="#{noticiaPortalDiscente.readOnly}" style="width: 95%" rows="10"/></td>
			</tr>
			<c:if test="${noticiaPortalDiscente.obj.idCurso != null}">
				<tr>
					<td></td>
					<td align="right"> Notificar:</td>
					<td style="text-align:left;padding-right:15px;"><h:selectBooleanCheckbox id="idNotificar" value="#{noticiaPortalDiscente.notificar}"/>
					<small> (Notificar os discentes por e-mail) </small>
					</td>
				</tr>
			</c:if>
			<tr>
				<td></td>
				<td align="right">Maior Destaque na Exibição:</td>
				<td style="text-align:left;padding-right:15px;" colspan="3">
					<a4j:region>
						<h:selectBooleanCheckbox value="#{noticiaPortalDiscente.obj.destaque}"
							 readonly="#{noticiaPortalDiscente.readOnly}" id="checkDestaque" onclick="submit()" immediate="true"/>
						</a4j:region>
			</tr>
			<tr>
				<td></td>
				<td align="right" class="required">
					Publicar até:&nbsp;&nbsp;
				</td>
				<td>
					<t:inputCalendar id="Publicar" title="Publicar até" 
						value="#{noticiaPortalDiscente.obj.expirarEm}"
						renderAsPopup="true" renderPopupButtonAsImage="true"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						popupDateFormat="dd/MM/yyyy" size="10" maxlength="10" />
				</td>
			</tr>
			<tr>
				<td colspan="3" class="subFormulario"> Anexar Arquivo </td>
			</tr>
			<tr>
				<td colspan="3">
					<p style="padding: 5px; margin: 0 120px; text-align: center; font-style: italic;">
					Opcionalmente você pode anexar um arquivo que ficará disponível para download
					enquanto a notícia estiver publicada.
					</p>
				</td>
			</tr>
			<tr>	
				<td> </td>
				<td align="right" nowrap="nowrap"> Descrição do Arquivo: </td>
				<td> <h:inputText value="#{noticiaPortalDiscente.obj.nomeArquivo}" readonly="#{noticiaPortalDiscente.readOnly}" style="width:95%;" maxlength="100"/> </td>
			</tr>
			<tr>
				<td> </td>
				<td align="right"> Caminho do arquivo: </td>
				<td> <t:inputFileUpload id="uFile" value="#{noticiaPortalDiscente.arquivo}" storage="file" size="70"/> </td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="#{noticiaPortalDiscente.confirmButton}" action="#{noticiaPortalDiscente.persistir}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{noticiaPortalDiscente.cancelar}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
	</table>
	<br/>
	</h:form>
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br>
		<br>
		</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>