<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Anexar fotos ao projeto</h2>

	<div class="descricaoOperacao">
		<font color="red" size="2">Atenção:</font>
			Utilize este espaço para enviar foto ou qualquer outra imagem que julgar importante para publicação e/ou execução
			da Proposta de projeto.
	</div>							
	<br/>


<h:form id="form" enctype="multipart/form-data">
	<table class=formulario width="90%">
	
		<caption class="listagem">Informe os dados do arquivo de foto </caption>
	
		<h:outputText value="#{fotoProjeto.create}" />
		<h:inputHidden value="#{fotoProjeto.confirmButton}"/>
		<h:inputHidden value="#{fotoProjeto.projeto.id}" id="idProjeto"/>
	
	
		<tr>
			<th>Ano:</th>
			<td>
				<b><h:outputText id="ano" value="#{fotoProjeto.projeto.ano}"/></b>
			</td>
		</tr>
	
		<tr>
			<th width="15%">Título:</th>
			<td>
				<b><h:outputText id="titulo" value="#{fotoProjeto.projeto.titulo}"/></b>
			</td>
		</tr>
	
		<tr>
			<th  class="required">Arquivo de Foto:</th>
			<td>
				<t:inputFileUpload id="uFile" value="#{fotoProjeto.foto}" storage="file" size="70"/>
			</td>
		</tr>
	
		<tr>
			<th  class="required"> Descrição:</th>
			<td>
				<h:inputTextarea  id="descricao" value="#{fotoProjeto.descricaoFoto}"  style="width: 75%" rows="2"/>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton action="#{fotoProjeto.anexarFoto}" value="Anexar Foto" id="btAnexarFoto"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	<br/>
	<br/>

	<table class="listagem">
		<caption class="listagem">Lista de Fotos </caption>
	
		<tr>
			<td colspan="2">
				<input type="hidden" value="0" id="idFotoOriginal" name="idFotoOriginal"/>
				<input type="hidden" value="0" id="idFotoMini" name="idFotoMini"/>
				<input type="hidden" value="0" id="idFotoProjeto" name="idFotoProjeto"/>
	
				<t:dataTable id="dtFotos" value="#{fotoProjeto.projeto.fotos}" var="foto" align="center" 
					width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
	
					<t:column>
						<f:facet name="header"><f:verbatim>Foto</f:verbatim></f:facet>
						<h:outputLink value="/sigaa/verFoto?idFoto=#{foto.idFotoOriginal}&key=#{sf:generateArquivoKey(foto.idFotoOriginal)}" id="link_verfoto_original_" title="Click para ampliar">
							<h:graphicImage url="/verFoto?idFoto=#{foto.idFotoMini}&key=#{sf:generateArquivoKey(foto.idFotoMini)}" width="70" height="70"/>
						</h:outputLink>
					</t:column>
	
					<t:column>
						<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
						<h:outputText value="#{foto.descricao}" />
					</t:column>
	
					<t:column width="5%">
						<h:commandButton image="/img/delete.gif" action="#{fotoProjeto.removeFoto}"
							title="Remover Foto"  alt="Remover Foto"   onclick="$(idFotoMini).value=#{foto.idFotoMini};$(idFotoOriginal).value=#{foto.idFotoOriginal};$(idFotoProjeto).value=#{foto.id};return confirm('Deseja Remover esta Foto da Ação de Extensão?')" id="removerFoto_" />
					</t:column>
	
				</t:dataTable>
			</td>
		</tr>
	</table>
	
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>