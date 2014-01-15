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
				Nesta tela devem ser informadas as fotos do projeto. 
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
				Neste espaço você pode ou não enviar uma foto ou qualquer outra imagem que julgar importante para aprovação e/ou execução
				da Ação Integrada que está sendo cadastrada.<br /> 
				Os campos são obrigatórios caso queira anexar uma foto.		
			<br/>
			<br/>
			</td>
		</tr>
	</table>
</div>

<h:form id="frmFotos" enctype="multipart/form-data">
<table class=formulario width="100%">

	<caption>Informe os dados do arquivo de foto </caption>

	<h:inputHidden value="#{projetoBase.confirmButton}"/>
	<h:inputHidden value="#{projetoBase.obj.id}"/>

    <tr>    
      <td>
        <table width="100%">
			<tr>
				<th width="30%">Ano - Título:</th>
				<td>
					<b><h:outputText id="titulo" value="#{projetoBase.obj.anoTitulo}"/></b>
				</td>
			</tr>
		
			<tr>
				<th  class="required"> Descrição:</th>
				<td>
					<h:inputText  id="descricao" value="#{projetoBase.descricaoFoto}" size="83" maxlength="90"/>
				</td>
			</tr>
		
			<tr>
				<th  class="required">Arquivo de Foto:</th>
				<td>
					<t:inputFileUpload id="uFile" value="#{projetoBase.foto}" storage="file" size="60"/>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2" align="center">
					   <h:commandButton	action="#{projetoBase.anexarFoto}" value="Anexar Foto" id="btAnexarFoto" />
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
		<td colspan="2" class="subFormulario">	Lista de fotos do projeto </td>
	</tr>

	<tr>
		<td colspan="2">
			<%--As três entradas abaixo não interferem na inserção. São usadas na remoção.--%>
			<input type="hidden" value="0" id="idFotoOriginal" name="idFotoOriginal"/>
			<input type="hidden" value="0" id="idFotoMini" name="idFotoMini"/>
			<input type="hidden" value="0" id="idFotoProjeto" name="idFotoProjeto"/>

			<t:dataTable id="dtFotos" value="#{projetoBase.obj.fotos}" var="foto" align="center" 
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


				<t:column width="5%">
						<h:outputLink value="/sigaa/verFoto?idFoto=#{foto.idFotoOriginal}&key=#{sf:generateArquivoKey(foto.idFotoOriginal)}" 
						  id="link_verfoto_original_" title="Visualizar" target="blank">
							<h:graphicImage url="/img/view.gif" />
						</h:outputLink>						
				        <h:commandButton image="/img/delete.gif" action="#{projetoBase.removeFoto}"
						  title="Remover"  alt="Remover"   onclick="$(idFotoMini).value=#{foto.idFotoMini};$(idFotoOriginal).value=#{foto.idFotoOriginal};$(idFotoProjeto).value=#{foto.id};return confirm('Deseja Remover esta Foto do Projeto?')" id="remFoto" />
				</t:column>
				
			</t:dataTable>
		</td>
	</tr>
	
	<c:if test="${empty projetoBase.obj.fotos}">
        <tr><td colspan="2" align="center"><font color="red">Não há fotos cadastradas</font> </td></tr>
    </c:if>
	

	<tfoot>
	<tr> <td colspan=2>
		<h:commandButton value="<< Voltar" action="#{projetoBase.passoAnterior}" id="btPassoAnteriorFotos"/>
		<h:commandButton value="Cancelar" action="#{projetoBase.cancelar}"   onclick="#{confirm}" id="btCancelar"/>
		<h:commandButton value="Gravar e Avançar >>" action="#{projetoBase.submeterFotos}" id="btSubmeterFotos"/>
	</td> </tr>
	</tfoot>

</h:form>
</table>
<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>